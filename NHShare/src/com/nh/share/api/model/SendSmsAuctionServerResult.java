package com.nh.share.api.model;

/**
 * SMS 인증 번호 발송 요청 응답 내부 객체
 * 
 * @see {ResponseApplicationVersion}
 *
 */
public class SendSmsAuctionServerResult {
    private String code; // 요청 결과
    private String cnt; // 발송 건수

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }
}
