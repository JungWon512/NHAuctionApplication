package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.AuctionEntryInformationResult;

/**
 * 
 * 출품 정보 조회 요청 응답 객체
 * 
 * @see {ActionRequestAuctionEntryInformation}
 *
 */
public class ResponseAuctionEntryInformation extends BaseResponse {
    private List<AuctionEntryInformationResult> result; // 응답 결과

    public List<AuctionEntryInformationResult> getResult() {
        return result;
    }

    public void setResult(List<AuctionEntryInformationResult> result) {
        this.result = result;
    }
}
