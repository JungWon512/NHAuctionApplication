package com.nh.share.common.models;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 차량 정보 요청
 * 
 * 제어프로그램, 응찰기 -> 서버
 * 
 * OC|출품순번
 *
 */
public class EntryInfo implements FromAuctionCommon {
    public static final char TYPE = 'C';
    private String mEntrySeqNum;

    public EntryInfo(String entrySeqNum) {
        mEntrySeqNum = entrySeqNum;
    }

    public String getEntrySeqNum() {
        return mEntrySeqNum;
    }

    public void setEntrySeqNum(String entrySeqNum) {
        this.mEntrySeqNum = entrySeqNum;
    }

    @Override
    public String getEncodedMessage() {
        return String.format("%c%c%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mEntrySeqNum);
    }
}
