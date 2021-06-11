package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.ApplicationVersionResult;
import com.nh.share.api.model.AuctionJoinStatusResult;

/**
 * 응찰 프로그램 버전 확인 요청 응답 객체
 * 
 * @see {ActionRequestAuctionConnectionStatus}
 *
 */
public class ResponseAuctionJoinStatus extends BaseResponse {
    
    private List<AuctionJoinStatusResult> result; // 응답 결과

    public List<AuctionJoinStatusResult> getResult() {
        return result;
    }

    public void setResult(List<AuctionJoinStatusResult> result) {
        this.result = result;
    }
}
