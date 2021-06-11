package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 시작 처리 기능
 * 
 * 제어프로그램 -> 서버
 * 
 * CL|회원번호
 *
 */
public class RequestLogout implements FromAuctionController {
    public static final char TYPE = 'L';
    private String mUserNo; // 회원번호

    public RequestLogout(String userNo) {
        mUserNo = userNo;
    }

    public String getUserNo() {
        return mUserNo;
    }

    public void setUserNo(String userNo) {
        this.mUserNo = userNo;
    }

    @Override
    public String getEncodedMessage() {
        return String.format("%c%c%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mUserNo);
    }
}
