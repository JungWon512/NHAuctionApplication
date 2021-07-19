package com.nh.share.common.models;

import java.io.Serializable;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 접속자 정보 전송
 * 
 * 공통 -> 경매서버
 * 
 * AI | 조합구분코드 | 회원번호 | 인증토큰 | 접속요청채널(6001/6002/6003/6004) | 사용채널(ANDROID/IOS/WEB) |
 * 관전자여부(Y/N)
 *
 */
public class ConnectionInfo implements FromAuctionCommon, Serializable {
	private static final long serialVersionUID = 4703227204913480236L;
	public static final char TYPE = 'I';
	private String mAuctionHouseCode; // 조합구분코드
	private String mUserNo; // 회원번호
	private String mAuthToken; // 인증토큰
	private String mChannel; // 접속 요청 채널
	private String mOS; // 사용 채널
	private String mWatcher; // 관전자여부

	public ConnectionInfo(String auctionHouseCode, String userNo, String authToken, String channel, String os, String watcher) {
		mAuctionHouseCode = auctionHouseCode;
		mUserNo = userNo;
		mAuthToken = authToken;
		mChannel = channel;
		mOS = os;
		mWatcher = watcher;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getUserNo() {
		return mUserNo;
	}

	public void setUserNo(String userNo) {
		this.mUserNo = userNo;
	}

	public String getAuthToken() {
		return mAuthToken;
	}

	public void setAuthToken(String authToken) {
		this.mAuthToken = authToken;
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
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mUserNo, AuctionShareSetting.DELIMITER, mAuthToken, AuctionShareSetting.DELIMITER, mChannel,
				AuctionShareSetting.DELIMITER, mOS, AuctionShareSetting.DELIMITER, mWatcher);
	}
}
