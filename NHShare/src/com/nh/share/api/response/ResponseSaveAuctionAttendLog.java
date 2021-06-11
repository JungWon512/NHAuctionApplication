package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.AuctionAttendLogResult;

/**
 * 응찰 프로그램 버전 확인 요청 응답 객체
 *
 * @see {ActionRequestSaveAuctionAttendLog}
 */
public class ResponseSaveAuctionAttendLog extends BaseResponse {

    private List<AuctionAttendLogResult> result; // 응답 결과

    public List<AuctionAttendLogResult> getResult() {
        return result;
    }

    public void setResult(List<AuctionAttendLogResult> result) {
        this.result = result;
    }
}
