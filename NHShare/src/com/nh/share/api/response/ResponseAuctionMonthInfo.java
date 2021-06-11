package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.AuctionMonthInfoResult;

/**
 * 경매 당월 누적 정보 조회 요청
 * 
 * @see {ActionRequestAuctionMonthInfo}
 *
 */
public class ResponseAuctionMonthInfo extends BaseResponse {
    private List<AuctionMonthInfoResult> result; // 응답 결과

    public List<AuctionMonthInfoResult> getResult() {
        return result;
    }

    public void setResult(List<AuctionMonthInfoResult> result) {
        this.result = result;
    }
}
