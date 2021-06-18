package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 접속자 강제 로그아웃 요청
 * 
 * 제어프로그램 -> 경매서버
 * 
 * CL | 경매거점코드 | 회원번호
 *
 */
public class RequestLogout implements FromAuctionController {
	public static final char TYPE = 'L';
	private String mAuctionHouseCode; // 거점코드
	private String mUserNo; // 회원번호

	public RequestLogout(String auctionHouseCode, String userNo) {
		mAuctionHouseCode = auctionHouseCode;
		mUserNo = userNo;
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

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mUserNo);
	}
}
