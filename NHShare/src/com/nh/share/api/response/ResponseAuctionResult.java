package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.AuctionResultResult;

/**
 * 경매 공지 사항 목록 조회 요청 응답 객체
 * 
 * @see {ActionRequestAuctionResult}
 *
 */
public class ResponseAuctionResult extends BaseResponse {
    private List<AuctionResultResult> result; // 응답 결과

    public List<AuctionResultResult> getResult() {
        return result;
    }

    public void setResult(List<AuctionResultResult> result) {
        this.result = result;
    }
}
