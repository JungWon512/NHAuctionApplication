package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.SendSmsAuctionServerResult;

/**
 * 경매 서버 생성 결과 반영 관련 응답
 * 
 * @see {ActionRequestSendSmsAuctionResult}
 *
 */
public class ResponseSendSmsAuctionServerResult extends BaseResponse {
    private List<SendSmsAuctionServerResult> result; // 응답 결과

    public List<SendSmsAuctionServerResult> getResult() {
        return result;
    }

    public void setResult(List<SendSmsAuctionServerResult> result) {
        this.result = result;
    }
}
