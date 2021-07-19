package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 정지 기능 처리
 * 
 * 제어프로그램 -> 경매서버
 * 
 * CS | 조합구분코드 | 출품번호
 *
 */
public class StopAuction implements FromAuctionController {
	public static final char TYPE = 'S';
	private String mAuctionHouseCode; // 거점코드
	private String mEntryNum; // 출품번호

	public StopAuction(String auctionHouseCode, String entryNum) {
		mAuctionHouseCode = auctionHouseCode;
		mEntryNum = entryNum;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mEntryNum);
	}

}
