package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.UpdateAuctionStatusResult;

/**
 * 경매 진행 상태 업데이트 요청
 * 
 * @see {ActionRequestUpdateAuctionStatus}
 *
 */
public class ResponseUpdateAuctionStatus extends BaseResponse {
    private List<UpdateAuctionStatusResult> result; // 응답 결과

    public List<UpdateAuctionStatusResult> getResult() {
        return result;
    }

    public void setResult(List<UpdateAuctionStatusResult> result) {
        this.result = result;
    }

}
