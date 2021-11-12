package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 종료 처리
 * 
 * 제어프로그램 -> 경매서버
 * 
 * CH | 조합구분코드
 *
 */
public class FinishAuction implements FromAuctionController {
	public static final char TYPE = 'H';
	private String mAuctionHouseCode; // 거점코드

	public FinishAuction(String auctionHouseCode) {
		mAuctionHouseCode = auctionHouseCode;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode);
	}

}
