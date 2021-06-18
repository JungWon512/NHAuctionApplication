package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 서버 응답 공통 코드
 * 
 * 경매서버 -> 공통
 * 
 * SE | 경매거점코드 | 응답 코드
 *
 */
public class ResponseCode implements FromAuctionServer {
	public static final char TYPE = 'E';
	private String mAuctionHouseCode; // 거점코드
	private String mResponseCode; // 응답코드

	public ResponseCode(String auctionHouseCode, String responseCode) {
		mAuctionHouseCode = auctionHouseCode;
		mResponseCode = responseCode;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getResponseCode() {
		return mResponseCode;
	}

	public void setResponseCode(String responseCode) {
		this.mResponseCode = responseCode;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mResponseCode);
	}
}
