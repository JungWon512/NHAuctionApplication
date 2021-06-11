package com.nh.share.common.models;

import java.io.Serializable;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 서버 접속 정보 유효 확인 처리
 * 
 * 서버 -> 공통
 * 
 * MR|회원(사원)번호
 *
 */
public class AuctionReponseSession implements FromAuctionCommon, Serializable {
    private static final long serialVersionUID = 4703227204913480226L;
    public static final char TYPE = 'R';
    private String mUserNo; // 회원(사원)번호
    private String mChannel; // 접속 요청 채널
    private String mOS; // 사용 채널

    public AuctionReponseSession(String userNo, String channel, String os) {
        mUserNo = userNo;
        mChannel = channel;
        mOS = os;
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

    @Override
    public String getEncodedMessage() {
        return String.format("%c%c%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mUserNo,
                AuctionShareSetting.DELIMITER, mChannel, AuctionShareSetting.DELIMITER, mOS);
    }
}
