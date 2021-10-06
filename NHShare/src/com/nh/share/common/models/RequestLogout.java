package com.nh.share.common.models;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 접속자 강제 로그아웃 요청
 * 
 * 접속자모니터링 -> 경매서버 접속채널 ( 6001 : 응찰 프로그램)
 * 
 * AL | 조합구분코드 | 경매참가번호 | 접속채널
 *
 */
public class RequestLogout implements FromAuctionCommon {
	public static final char TYPE = 'L';
	private String mAuctionHouseCode; // 거점코드
	private String mUserJoinNum; // 경매참가번호
	private String mConnectChannel; // 접속채널

	public RequestLogout(String auctionHouseCode, String userJoinNum, String connectChannel) {
		mAuctionHouseCode = auctionHouseCode;
		mUserJoinNum = userJoinNum;
		mConnectChannel = connectChannel;
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

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mUserJoinNum, AuctionShareSetting.DELIMITER, mConnectChannel);
	}
}
