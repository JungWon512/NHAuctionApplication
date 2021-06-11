package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.UpdateAuctionPortInformation;

/**
 * 경매 포트 정보 전송 요청
 * 
 * @see {ActionRequestUpdateAuctionPortInformation}
 *
 */
public class ResponseUpdateAuctionPortInformation extends BaseResponse {
    private List<UpdateAuctionPortInformation> result; // 응답 결과

    public List<UpdateAuctionPortInformation> getResult() {
        return result;
    }

    public void setResult(List<UpdateAuctionPortInformation> result) {
        this.result = result;
    }
}
