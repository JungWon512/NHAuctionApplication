package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.TransmissionAuctionResultResult;

/**
 * 
 * 경매 낙유찰 결과 전송 요청 응답 객체
 * 
 * @see {ActionRequestTransmissionAuctionResult}
 *
 */
public class ResponseTransmissionAuctionResult extends BaseResponse {
    private List<TransmissionAuctionResultResult> result; // 응답 결과

    public List<TransmissionAuctionResultResult> getResult() {
        return result;
    }

    public void setResult(List<TransmissionAuctionResultResult> result) {
        this.result = result;
    }

}
