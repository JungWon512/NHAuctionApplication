package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 시작 처리 기능
 * 
 * 제어프로그램 -> 서버
 * 
 * CS|출품번호
 *
 */
public class StartAuction implements FromAuctionController {
    public static final char TYPE = 'S';
    private String mFlagAutoMode; // 자동 진행 여부
    private String mEntryNum; // 출품번호

    public StartAuction(String flagAutoMode, String entryNum) {
        mFlagAutoMode = flagAutoMode;
        mEntryNum = entryNum;
    }

    public String getFlagAutoMode() {
        return mFlagAutoMode;
    }

    public void setFlagAutoMode(String flagAutoMode) {
        this.mFlagAutoMode = flagAutoMode;
    }

    public String getEntryNum() {
        return mEntryNum;
    }

    public void setEntryNum(String entryNum) {
        this.mEntryNum = entryNum;
    }

    @Override
    public String getEncodedMessage() {
        return String.format("%c%c%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mFlagAutoMode, AuctionShareSetting.DELIMITER, mEntryNum);
    }
}
