package com.nh.share.common.models;

import java.io.Serializable;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 재경매 대상 정보 전송
 * <p>
 * 경매서버 / 제어프로그램 -> 공통
 * <p>
 * AN | 조합구분코드 | 대상자참여번호1,대상자참여번호2,대상자참여번호3....대상자참여번호n
 */
public class RetryTargetInfo implements FromAuctionCommon, Serializable {
	public static final char TYPE = 'N';
	private String mAuctionHouseCode; // 조합구분코드
	private String mEntryNum; // 출품번호
	private String mRetryTargetInfo; // 재경매대장자참여번호

	public RetryTargetInfo() {
	}

	public RetryTargetInfo(String auctionHouseCode, String entryNum, String retryTargetInfo) {
		mAuctionHouseCode = auctionHouseCode;
		mEntryNum = entryNum;
		mRetryTargetInfo = retryTargetInfo;
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

	public String getRetryTargetInfo() {
		return mRetryTargetInfo;
	}

	public void setRetryTargetInfo(String retryTargetInfo) {
		this.mRetryTargetInfo = retryTargetInfo;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mEntryNum, AuctionShareSetting.DELIMITER, mRetryTargetInfo);
	}
}
