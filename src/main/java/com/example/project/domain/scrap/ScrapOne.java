package com.example.project.domain.scrap;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author 이승환
 * @since 2022-02-21
 */
@Entity
@Getter
@Table(name = "scrap_One")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScrapOne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scrapOneIdx;  // 식별값

    @Column(name = "user_idx")
    private Long userIdx;

    private String incomeDetails;
    private Long totalPay;
    private String startDate;
    private String scrapCompany;
    private String name;
    private String payDate;
    private String endDate;
    private String regNo;
    private String incomeCate;
    private String comNo;

    @Builder
    public ScrapOne(String incomeDetails,
                    Long totalPay,
                    String startDate,
                    String scrapCompany,
                    String name,
                    String payDate,
                    String endDate,
                    String regNo,
                    String incomeCate,
                    String comNo,
                    Long userIdx) {

        this.incomeDetails = incomeDetails;
        this.totalPay = totalPay;
        this.startDate = startDate;
        this.scrapCompany = scrapCompany;
        this.name = name;
        this.payDate = payDate;
        this.endDate = endDate;
        this.regNo = regNo;
        this.incomeCate = incomeCate;
        this.comNo = comNo;
        this.userIdx = userIdx;
    }
}
