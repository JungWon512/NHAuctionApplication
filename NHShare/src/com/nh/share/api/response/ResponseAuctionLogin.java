package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.AuctionLoginResult;

/**
 * 경매 사용자 인증 처리 응답 객체
 * 
 * @see {ActionRequestAuctionLogin}
 *
 */
public class ResponseAuctionLogin extends BaseResponse {
    private List<AuctionLoginResult> result; // 응답 결과

    public List<AuctionLoginResult> getResult() {
        return result;
    }

    public void setResult(List<AuctionLoginResult> result) {
        this.result = result;
    }

}
