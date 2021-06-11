package com.nh.share.api.model;

/**
 * SMS 인증 번호 발송 요청 응답 내부 객체
 * 
 * @see {ResponseApplicationVersion}
 *
 */
public class SendSmsAuthNumberResult {
    private String authNumber; // SMS 인증 번호

    public String getAuthNumber() {
        return authNumber;
    }

    public void setAuthNumber(String authNumber) {
        this.authNumber = authNumber;
    }

}
