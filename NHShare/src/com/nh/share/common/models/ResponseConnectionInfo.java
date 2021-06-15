package com.nh.share.common.models;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 접속 정보 응답 처리
 * 
 * 경매서버/제어프로그램 -> 공통
 * 
 * AR|결과코드
 *
 */
public class ResponseConnectionInfo implements FromAuctionCommon {
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
