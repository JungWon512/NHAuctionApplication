package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.AuctionImageNoticeResult;

/**
 * 응찰 프로그램 버전 확인 요청 응답 객체
 * 
 * @see {ActionRequestAuctionImageNotice}
 *
 */
public class ResponseAuctionImageNotice extends BaseResponse {
    
    private List<AuctionImageNoticeResult> result; // 응답 결과

    public List<AuctionImageNoticeResult> getResult() {
        return result;
    }

    public void setResult(List<AuctionImageNoticeResult> result) {
        this.result = result;
    }
}
