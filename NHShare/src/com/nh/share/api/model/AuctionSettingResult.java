package com.nh.share.api.model;

/**
 * 응찰 프로그램 설정 정보 조회 요청 응답 내부 객체
 * 
 * @see {ResponseAuctionSetting}
 *
 */
public class AuctionSettingResult {
    private String flagAutoLogin; // 자동 로그인 여부
    private String modifyPersonInfoUrl; // 개인정보 변경 URL
    private String flagAuctionNoticePush; // 경매 시작 푸시 알림 여부
    private String auctionSoundOnOff; // 경매 사운드 ON/OFF
    private String auctionDisplaySetting; // 경매 화면 배치
    private String languageType; // 언어 종류
    private String screenBrightness; // 화면 밝기 조절
    private String sessionDuration; // 실시간 경매 유지 시간

    public String getFlagAutoLogin() {
        return flagAutoLogin;
    }

    public void setFlagAutoLogin(String flagAutoLogin) {
        this.flagAutoLogin = flagAutoLogin;
    }

    public String getModifyPersonInfoUrl() {
        return modifyPersonInfoUrl;
    }

    public void setModifyPersonInfoUrl(String modifyPersonInfoUrl) {
        this.modifyPersonInfoUrl = modifyPersonInfoUrl;
    }

    public String getFlagAuctionNoticePush() {
        return flagAuctionNoticePush;
    }

    public void setFlagAuctionNoticePush(String flagAuctionNoticePush) {
        this.flagAuctionNoticePush = flagAuctionNoticePush;
    }

    public String getAuctionSoundOnOff() {
        return auctionSoundOnOff;
    }

    public void setAuctionSoundOnOff(String auctionSoundOnOff) {
        this.auctionSoundOnOff = auctionSoundOnOff;
    }

    public String getAuctionDisplaySetting() {
        return auctionDisplaySetting;
    }

    public void setAuctionDisplaySetting(String auctionDisplaySetting) {
        this.auctionDisplaySetting = auctionDisplaySetting;
    }

    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }

    public String getScreenBrightness() {
        return screenBrightness;
    }

    public void setScreenBrightness(String screenBrightness) {
        this.screenBrightness = screenBrightness;
    }

    public String getSessionDuration() {
        return sessionDuration;
    }

    public void setSessionDuration(String sessionDuration) {
        this.sessionDuration = sessionDuration;
    }
}
