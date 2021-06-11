package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.AuctionGenerateInformationResult;

public class ResponseAuctionGenerateInformation extends BaseResponse {
    private List<AuctionGenerateInformationResult> result; // 응답 결과

    public List<AuctionGenerateInformationResult> getResult() {
        return result;
    }

    public void setResult(List<AuctionGenerateInformationResult> result) {
        this.result = result;
    }
}
