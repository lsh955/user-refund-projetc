package com.example.project.controller;

import com.example.project.TestValues;
import com.example.project.enums.ScrapStatus;
import com.example.project.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author 이승환
 * @since 2022-02-24
 */
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RefundControllerTest extends TestValues {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    @DisplayName(value = "환급액 계산 조회실패(저장된 스크랩결과가 없을 시)")
    public void REFUND_FAILURE_TEST() throws Exception {
        String name = "홍길동";
        String regNo = "ldU2Z5ZlRuwPfYA1YfvOTw==";

        HashMap<String, String> token = this.jwtTokenUtil.createToken(name, regNo);

        this.mockMvc.perform(post(apIBaseUrl + "/refund")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(ScrapStatus.NO_SCRAP_DATA.getCode()))
                .andExpect(jsonPath("message").value(ScrapStatus.NO_SCRAP_DATA.getMessage()));
    }
}
