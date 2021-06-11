package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.AuctionDetailInfoResult;

/**
 * 
 * 경매 상세 일정 정보 조회 요청 응답 객체
 * 
 * @see {ActionRequestAuctionDetailInfo}
 *
 */
public class ResponseAuctionDetailInfo extends BaseResponse {
    private List<AuctionDetailInfoResult> result; // 응답 결과

    public List<AuctionDetailInfoResult> getResult() {
        return result;
    }

    public void setResult(List<AuctionDetailInfoResult> result) {
        this.result = result;
    }
}
