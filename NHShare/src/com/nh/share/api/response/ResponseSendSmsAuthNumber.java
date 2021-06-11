package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.SendSmsAuthNumberResult;

/**
 * SMS 인증 번호 발송 요청 응답 객체
 * 
 * @see {ActionRequestSendSmsAuthNumber}
 *
 */
public class ResponseSendSmsAuthNumber extends BaseResponse {
    private List<SendSmsAuthNumberResult> result; // 응답 결과

    public List<SendSmsAuthNumberResult> getResult() {
        return result;
    }

    public void setResult(List<SendSmsAuthNumberResult> result) {
        this.result = result;
    }
}
