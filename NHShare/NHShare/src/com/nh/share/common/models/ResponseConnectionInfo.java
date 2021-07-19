package com.nh.share.common.models;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 접속 정보 응답 처리
 * 
 * 경매서버/제어프로그램 -> 공통
 * 
 * AR | 조합구분코드 | 접속결과
 *
 */
public class ResponseConnectionInfo implements FromAuctionCommon {
	public static final char TYPE = 'R';
	private String mAuctionHouseCode; // 거점코드
	private String mResult; // 결과코드

	public ResponseConnectionInfo(String auctionHouseCode, String result) {
		mAuctionHouseCode = auctionHouseCode;
		mResult = result;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getResult() {
		return mResult;
	}

	public void setResult(String result) {
		this.mResult = result;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mResult);
	}
}
