package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.AuctionScheduleInfoResult;

/**
 * 
 * 경매 일정 정보 조회 요청 응답 객체
 * 
 * @see {ActionRequestAuctionScheduleInfo}
 *
 */
public class ResponseAuctionScheduleInfo extends BaseResponse {
    private List<AuctionScheduleInfoResult> result; // 응답 결과

    public List<AuctionScheduleInfoResult> getResult() {
        return result;
    }

    public void setResult(List<AuctionScheduleInfoResult> result) {
        this.result = result;
    }

}