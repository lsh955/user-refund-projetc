package com.example.project.app.account.service;

import com.example.project.app.account.domain.JoinAvailableRepository;
import com.example.project.app.account.domain.User;
import com.example.project.app.account.domain.UserRepository;
import com.example.project.app.account.dto.UserDto;
import com.example.project.app.common.dto.JwtTokenDto;
import com.example.project.app.common.enums.AccountStatus;
import com.example.project.app.common.util.AESCryptoUtil;
import com.example.project.app.common.util.JwtTokenUtil;
import com.example.project.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;

import static com.example.project.app.common.enums.ErrorCode.*;

/**
 * @author 이승환
 * @since 2022-02-18
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImp implements AccountService {

    private final UserRepository userRepository;
    private final JoinAvailableRepository joinAvailableRepository;

    private final JwtTokenUtil jwtTokenUtil;
    private final AESCryptoUtil aesCryptoUtil;

    /**
     * 회원가입
     *
     * @param userId    아이디
     * @param password  패스워드
     * @param name      이름
     * @param regNo     주민등록번호
     * @return          성공여부
     */
    @Transactional
    @Override
    public AccountStatus addSignup(String userId, String password, String name, String regNo) throws Exception {
        // 패스워드 암호화
        String encryptedRegNo = this.aesCryptoUtil.encrypt(regNo);
        String encryptedPassword = this.aesCryptoUtil.encrypt(password);

        // 가입가능한 주민번호 체크(이름은 동명이인 있을 수 있으니 가입가능한 이름은 검사하지 않음)
        if (!this.joinAvailableRepository.existsByRegNo(encryptedRegNo))
            throw new CustomException(UNABLE_TO_REG_NO);

        // 주민번호 중복체크
        if (this.userRepository.existsByRegNo(encryptedRegNo))
            throw new CustomException(REG_NO_OVERLAP);

        // 사용자 등록
        this.userRepository.save(User.builder()
                .userId(userId)
                .password(encryptedPassword)
                .name(name)
                .regNo(encryptedRegNo)
                .build()
        );

        return AccountStatus.SIGNUP_SUCCESS; // 성공
    }

    /**
     * 로그인
     *
     * @param userId    아이디
     * @param password  패스워드
     * @return          성공여부
     */
    @Transactional
    @Override
    public JwtTokenDto login(String userId, String password) throws Exception {
        // 사용자 아이디 기준으로 데이터 불러오기
        User user = userRepository.findByUserId(userId);

        if(user == null)
            throw new CustomException(MEMBER_NOT_FOUND);

        // 패스워드 복호화
        String decryptedDbPassword = this.aesCryptoUtil.decrypt(user.getPassword());

        // 패스와드가 맞는지 검증
        if (!password.equals(decryptedDbPassword))
            throw new CustomException(UNAUTHORIZED_PASSWORD);

        // 가입이 되었다면 토큰생성.
        HashMap<String, String> token = this.jwtTokenUtil.createToken(
                user.getName(),
                user.getRegNo()
        );

        return JwtTokenDto.builder()
                .token(token.get("token"))
                .build();
    }

    /**
     * 내 정보 보기
     *
     * @param token User Token
     * @return      성공여부
     */
    @Transactional
    @Override
    public UserDto readMember(String token) throws Exception {
        // Token 검증
        HashMap<String, String> strToken = this.jwtTokenUtil.decoderToken(token);

        // 주민등록번호 암호화
        String encryptRegNo = this.aesCryptoUtil.encrypt(strToken.get("regNo"));

        // 사용자정보 불러오기
        Optional<User> user = this.userRepository.findByNameAndRegNo(
                strToken.get("name"),
                encryptRegNo
        );

        User result = user.orElseThrow(() ->
                // 정보가 없다면 가입되지 않는회원으로 간주
                new CustomException(MEMBER_NOT_FOUND)
        );

        return UserDto.builder()
                .userId(result.getUserId())
                .name(result.getName())
                .password(result.getPassword())
                .regNo(result.getRegNo())
                .build();
    }
}
