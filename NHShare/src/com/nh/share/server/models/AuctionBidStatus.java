package com.nh.share.server.models;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 응찰 종료 상태 전송
 * 
 * 경매서버 / 제어프로그램 -> 응찰채널
 * 
 * AY | 조합구분코드 | 경매번호 | 응찰상태코드(F/P/N)
 *
 */
public class AuctionBidStatus implements FromAuctionCommon {
	public static final char TYPE = 'Y';
	private String mAuctionHouseCode;
	private String mEntryNum;
	private String mStatus; // 상태구분 (F : 응찰종료 / P : 응찰진행 / N : 기본값)

	public AuctionBidStatus() {

	}

	public AuctionBidStatus(String auctionHouseCode, String entryNum, String status) {
		mAuctionHouseCode = auctionHouseCode;
		mEntryNum = entryNum;
		mStatus = status;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getStatus() {
		return mStatus;
	}

	public void setStatus(String status) {
		this.mStatus = status;
	}

	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mEntryNum, AuctionShareSetting.DELIMITER, mStatus);
	}
}
