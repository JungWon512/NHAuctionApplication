package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 예외 상황 코드
 * 
 * 서버 -> 공통
 * 
 * SE|예외상황코드
 *
 */
public class ExceptionCode implements FromAuctionServer {
    public static final char TYPE = 'E';
    private String mErrorCode; // 예외상황코드

    public ExceptionCode(String errorCode) {
        mErrorCode = errorCode;
    }

    public String getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(String errorCode) {
        this.mErrorCode = errorCode;
    }

    @Override
    public String getEncodedMessage() {
        return String.format("%c%c%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mErrorCode);
    }
}
