package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 접속 정보 응답 처리
 * 
 * 서버 -> 공통
 * 
 * CC|결과코드
 *
 */
public class ResponseConnectionInfo implements FromAuctionServer {
    public static final char TYPE = 'R';
    private String mResult; // 결과코드

    public ResponseConnectionInfo(String result) {
        mResult = result;
    }

    public String getResult() {
        return mResult;
    }

    public void setResult(String result) {
        this.mResult = result;
    }

    @Override
    public String getEncodedMessage() {
        return String.format("%c%c%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mResult);
    }
}
