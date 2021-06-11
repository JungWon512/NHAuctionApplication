package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.AuctionSettingsInformationResult;

/**
 * 경매 생성 정보 조회 요청 응답 객체
 * 
 * @see {ActionRequestAuctionGenerateInformation}
 *
 */
public class ResponseAuctionSettingsInformation extends BaseResponse {
    private List<AuctionSettingsInformationResult> result; // 응답 결과

    public List<AuctionSettingsInformationResult> getResult() {
        return result;
    }

    public void setResult(List<AuctionSettingsInformationResult> result) {
        this.result = result;
    }
}
