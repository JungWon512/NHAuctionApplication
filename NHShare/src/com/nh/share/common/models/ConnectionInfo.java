package com.nh.share.common.models;

import java.io.Serializable;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 접속자 정보 전송
 * 
 * 공통 -> 서버
 * 
 * OI|회원(사원)번호|접속요청채널(6001/6002/6003/6004/6005)|사용채널(PC/ANDROID/IOS)|관전여부(Y/N)
 *
 */
public class ConnectionInfo implements FromAuctionCommon, Serializable {
    private static final long serialVersionUID = 4703227204913480236L;
    public static final char TYPE = 'I';
    private String mUserNo; // 회원(사원)번호
    private String mChannel; // 접속 요청 채널
    private String mOS; // 사용 채널
    private String mWatcher; // 관전자여부

    public ConnectionInfo(String userNo, String channel, String os, String watcher) {
        mUserNo = userNo;
        mChannel = channel;
        mOS = os;
        mWatcher = watcher;
    }

    public String getUserNo() {
        return mUserNo;
    }

    public void setUserNo(String userNo) {
        this.mUserNo = userNo;
    }

    public String getChannel() {
        return mChannel;
    }

    public void setChannel(String channel) {
        this.mChannel = channel;
    }

    public String getOS() {
        return mOS;
    }

    public void setOS(String os) {
        this.mOS = os;
    }

    public String getWatcher() {
        return mWatcher;
    }

    public void setWatcher(String watcher) {
        this.mWatcher = watcher;
    }

    @Override
    public String getEncodedMessage() {
        return String.format("%c%c%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mUserNo,
                AuctionShareSetting.DELIMITER, mChannel, AuctionShareSetting.DELIMITER, mOS,
                AuctionShareSetting.DELIMITER, mWatcher);
    }
}
