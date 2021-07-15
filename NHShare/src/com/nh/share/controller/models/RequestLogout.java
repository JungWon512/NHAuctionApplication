package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 접속자 강제 로그아웃 요청
 * 
 * 제어프로그램 -> 경매서버 접속채널 ( 6001 : 응찰 프로그램 6002 : 제어 프로그램 6003 : 관전 프로그램 6004 :
 * 낙/유찰 결과 모니터링 프로그램 6005 : 접속자 모니터링 프로그램)
 * 
 * CL | 조합구분코드 | 회원번호 | 접속채널
 *
 */
public class RequestLogout implements FromAuctionController {
	public static final char TYPE = 'L';
	private String mAuctionHouseCode; // 거점코드
	private String mUserNo; // 회원번호
	private String mConnectChannel; // 접속채널

	public RequestLogout(String auctionHouseCode, String userNo, String connectChannel) {
		mAuctionHouseCode = auctionHouseCode;
		mUserNo = userNo;
		mConnectChannel = connectChannel;
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

	public String getConnectChannel() {
		return mConnectChannel;
	}

	public void setConnectChannel(String connectChannel) {
		this.mConnectChannel = connectChannel;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mUserNo, AuctionShareSetting.DELIMITER, mConnectChannel);
	}
}
