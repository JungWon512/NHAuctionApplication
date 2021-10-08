package com.nh.share.common.models;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 접속자 강제 로그아웃 요청
 * 
 * 접속자모니터링 -> 경매서버 접속채널 
 * 
 * AL | 조합구분코드 | 경매참가번호 | 접속채널 | 접속유형(ANDROID/IOS/WEB)
 *
 */
public class RequestLogout implements FromAuctionCommon {
	public static final char TYPE = 'L';
	private String mAuctionHouseCode; // 거점코드
	private String mUserJoinNum; // 경매참가번호
	private String mConnectChannel; // 접속채널
	private String mConnectType; // 접속유형(ANDROID/IOS/WEB)

	public RequestLogout(String auctionHouseCode, String userJoinNum, String connectChannel, String connectType) {
		mAuctionHouseCode = auctionHouseCode;
		mUserJoinNum = userJoinNum;
		mConnectChannel = connectChannel;
		mConnectType = connectType;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getUserJoinNum() {
		return mUserJoinNum;
	}

	public void setUserJoinNum(String userNo) {
		this.mUserJoinNum = userNo;
	}

	public String getConnectChannel() {
		return mConnectChannel;
	}

	public void setConnectChannel(String connectChannel) {
		this.mConnectChannel = connectChannel;
	}

	public String getConnectType() {
		return mConnectType;
	}

	public void setConnectType(String connectType) {
		this.mConnectType = connectType;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mUserJoinNum, AuctionShareSetting.DELIMITER, mConnectChannel, AuctionShareSetting.DELIMITER, mConnectType);
	}
}
