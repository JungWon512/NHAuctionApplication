package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 관심차량 여부에 대한 정보 전송 처리
 * 
 * 서버 -> 응찰기
 * 
 * SF|출품번호|관심차량 여부(Y/N)
 *
 */
public class FavoriteEntryInfo implements FromAuctionServer {
    public static final char TYPE = 'F';
    private String mEntryNum; // 출품번호
    private String mFlagFavorite; // 관심차량 여부(Y/N)

    public FavoriteEntryInfo(String entryNum, String flagFavorite) {
        mEntryNum = entryNum;
        mFlagFavorite = flagFavorite;
    }

    public FavoriteEntryInfo(String[] messages) {
        mEntryNum = messages[1];
        mFlagFavorite = messages[2];
    }

    public String getEntryNum() {
        return mEntryNum;
    }

    public void setEntryNum(String entryNum) {
        this.mEntryNum = entryNum;
    }

    public String getFlagFavorite() {
        return mFlagFavorite;
    }

    public void setFlagFavorite(String flagFavorite) {
        this.mFlagFavorite = flagFavorite;
    }

    @Override
    public String getEncodedMessage() {
        return String.format("%c%c%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mEntryNum, AuctionShareSetting.DELIMITER, mFlagFavorite);
    }
}
