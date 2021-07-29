package com.nh.share.common.models;

import java.io.Serializable;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 접속자 정보 전송
 * <p>
 * 공통 -> 경매서버
 * <p>
 * AI | 조합구분코드 | 거래인관리번호 | 인증토큰 | 접속요청채널(6001/6002/6003/6004/6005) |
 * 사용채널(ANDROID/IOS/WEB/MANAGE)
 */
public class ConnectionInfo implements FromAuctionCommon, Serializable {
	private static final long serialVersionUID = 4703227204913480236L;
	public static final char TYPE = 'I';
	private String mAuctionHouseCode; // 조합구분코드
	private String mUserMemNum; // 거래인관리번호
	private String mAuthToken; // 인증토큰
	private String mChannel; // 접속 요청 채널
	private String mOS; // 사용 채널
	private String mAuctionJoinNum; // 경매참가번호(패킷데이터에서 제외)

	public ConnectionInfo() {
	}

	public ConnectionInfo(String auctionHouseCode, String userMemNum, String authToken, String channel, String os) {
		mAuctionHouseCode = auctionHouseCode;
		mUserMemNum = userMemNum;
		mAuthToken = authToken;
		mChannel = channel;
		mOS = os;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getUserMemNum() {
		return mUserMemNum;
	}

	public void setUserMemNum(String userMemNum) {
		this.mUserMemNum = userMemNum;
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

	public String getAuctionJoinNum() {
		return this.mAuctionJoinNum;
	}

	public void setAuctionJoinNum(String auctionJoinNum) {
		mAuctionJoinNum = auctionJoinNum;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mUserMemNum, AuctionShareSetting.DELIMITER, mAuthToken,
				AuctionShareSetting.DELIMITER, mChannel, AuctionShareSetting.DELIMITER, mOS);
	}
}
