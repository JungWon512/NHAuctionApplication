package com.nh.share.api.model;

/**
 * 응찰 프로그램 설정 정보 저장 요청 응답 내부 객체
 * 
 * @see {ResponseSaveAuctionSetting}
 *
 */
public class SaveAuctionSettingResult {
    private String savingStatus; // 저장 결과

    public String getSavingStatus() {
        return savingStatus;
    }

    public void setSavingStatus(String savingStatus) {
        this.savingStatus = savingStatus;
    }

}
