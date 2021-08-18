package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 출품 데이터 초기화 요청
 * 
 * 제어프로그램 -> 경매서버
 * 
 * CD | 조합구분코드 | 경매차수
 *
 */
public class InitEntryInfo implements FromAuctionController {
	public static final char TYPE = 'D';
	private String mAuctionHouseCode; // 거점코드
	private String mAuctionQcn; // 경매회차

	public InitEntryInfo(String auctionHouseCode, String auctionQcn) {
		mAuctionHouseCode = auctionHouseCode;
		mAuctionQcn = auctionQcn;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getAuctionQcn() {
		return mAuctionQcn;
	}

	public void setAuctionQcn(String auctionQcn) {
		this.mAuctionQcn = auctionQcn;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mAuctionQcn);
	}

}
