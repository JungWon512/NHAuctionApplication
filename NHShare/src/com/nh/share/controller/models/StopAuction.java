package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 정지 기능 처리
 * 
 * 제어프로그램 -> 경매서버
 * 
 * CS | 조합구분코드 | 출품번호 | 카운트다운적용시간(second)
 *
 */
public class StopAuction implements FromAuctionController {
	public static final char TYPE = 'S';
	private String mAuctionHouseCode; // 거점코드
	private String mEntryNum; // 출품번호
	private String mCountDown; // 카운트다운적용시간(초)

	public StopAuction(String auctionHouseCode, String entryNum, String countDown) {
		mAuctionHouseCode = auctionHouseCode;
		mEntryNum = entryNum;
		mCountDown = countDown;
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

	public String getCountDown() {
		return mCountDown;
	}

	public void setCountDown(String countDown) {
		this.mCountDown = countDown;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mEntryNum, AuctionShareSetting.DELIMITER, mCountDown);
	}

}
