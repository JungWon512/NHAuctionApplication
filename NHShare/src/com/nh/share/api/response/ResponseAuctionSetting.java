package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.AuctionSettingResult;

/**
 * 응찰 프로그램 설정 정보 조회 요청 응답 객체
 * 
 * @see {ActionRequestAuctionSetting}
 *
 */
public class ResponseAuctionSetting extends BaseResponse {
    private List<AuctionSettingResult> result; // 응답 결과

    public List<AuctionSettingResult> getResult() {
        return result;
    }

    public void setResult(List<AuctionSettingResult> result) {
        this.result = result;
    }

}
