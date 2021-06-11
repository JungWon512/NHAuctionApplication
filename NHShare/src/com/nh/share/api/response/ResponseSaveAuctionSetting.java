package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.SaveAuctionSettingResult;

/**
 * 응찰 프로그램 설정 정보 저장 요청 응답 객체
 * 
 * @see {ActionRequestSaveAuctionSetting}
 *
 */
public class ResponseSaveAuctionSetting extends BaseResponse {
    private List<SaveAuctionSettingResult> result; // 응답 결과

    public List<SaveAuctionSettingResult> getResult() {
        return result;
    }

    public void setResult(List<SaveAuctionSettingResult> result) {
        this.result = result;
    }

}
