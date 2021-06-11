package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 접속자 정보 전송
 * 
 * 공통 -> 서버
 * 
 * OI|회원(사원)번호|접속요청채널(6001/6002/6003/6004/6005)|사용채널(AH/PC/ANDROID/IOS)|응찰상태(Y/N)
 *
 */
public class BidderConnectInfo implements FromAuctionServer {
	public static final char TYPE = 'I';
	private String mUserNo; // 회원(사원)번호
	private String mChannel; // 접속 요청 채널
	private String mOS; // 사용 채널
	private String mStatus; // 응찰 상태

	public BidderConnectInfo(String userNo, String channel, String os, String status) {
		mUserNo = userNo;
		mChannel = channel;
		mOS = os;
		mStatus = status;
	}

	public BidderConnectInfo(String[] messages) {
		mUserNo = messages[1];
		mChannel = messages[2];
		mOS = messages[3];
		mStatus = messages[4];
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

	public String getStatus() {
		return mStatus;
	}

	public void setStatus(String status) {
		this.mStatus = status;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mUserNo,
				AuctionShareSetting.DELIMITER, mChannel, AuctionShareSetting.DELIMITER, mOS,
				AuctionShareSetting.DELIMITER, mStatus);
	}
}
