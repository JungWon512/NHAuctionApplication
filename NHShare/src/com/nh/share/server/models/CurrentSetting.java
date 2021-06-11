package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 환경 설정 정보
 * 
 * 서버 -> 제어프로그램
 * 
 * SS|기준금액|기준금액 이상 상승가|기준금액 이하 상승가|기준금액 1억 이상
 * 상승가|경매진행시간|낙찰지연시간|다음시작간격|자동상승횟수|현재경매시작상태|현재진행모드
 *
 */
public class CurrentSetting implements FromAuctionServer {
	public static final char TYPE = 'S';
	private String mBaseStartPrice; // 기준금액
	private String mMoreRisePrice; // 기준금액 이상 상승가
	private String mBelowRisePrice; // 기준금액 이하 상승가
	private String mMaxRisePrice; // 기준금액 1억 이상 상승가
	private String mBiddingTime; // 경매 진행 시간
	private String mBiddingAdditionalTime; // 낙찰 지연 시간
	private String mBiddingIntervalTime; // 다음 시작 간격
	private String mMaxAutoUpCount; // 자동 상승 횟수
	private String mFlagAuctionStart; // 현재 경매 시작 상태(Y : 시작 / N : 정지)
	private String mFlagAuctionAutoMode; // 현재 경매 진행 모드(Y : 자동 진행 / N : 수동 진행)

	public CurrentSetting(String baseStartPrice, String moreRisePrice, String belowRisePrice, String maxRisePrice,
			String biddingTime, String biddingAdditionalTime, String biddingIntervalTime, String maxAutoUpCount,
			String flagAuctionStart, String flagAuctionAutoMode) {
		super();
		this.mBaseStartPrice = baseStartPrice;
		this.mMoreRisePrice = moreRisePrice;
		this.mBelowRisePrice = belowRisePrice;
		this.mMaxRisePrice = maxRisePrice;
		this.mBiddingTime = biddingTime;
		this.mBiddingAdditionalTime = biddingAdditionalTime;
		this.mBiddingIntervalTime = biddingIntervalTime;
		this.mMaxAutoUpCount = maxAutoUpCount;
		this.mFlagAuctionStart = flagAuctionStart;
		this.mFlagAuctionAutoMode = flagAuctionAutoMode;
	}

	public CurrentSetting(String[] messages) {
		mBaseStartPrice = messages[1];
		mMoreRisePrice = messages[2];
		mBelowRisePrice = messages[3];
		mMaxRisePrice = messages[4];
		mBiddingTime = messages[5];
		mBiddingAdditionalTime = messages[6];
		mBiddingIntervalTime = messages[7];
		mMaxAutoUpCount = messages[8];
		mFlagAuctionStart = messages[9];
		mFlagAuctionAutoMode = messages[10];
	}

	public String getBaseStartPrice() {
		return mBaseStartPrice;
	}

	public void setBaseStartPrice(String baseStartPrice) {
		this.mBaseStartPrice = baseStartPrice;
	}

	public String getMoreRisePrice() {
		return mMoreRisePrice;
	}

	public void setMoreRisePrice(String moreRisePrice) {
		this.mMoreRisePrice = moreRisePrice;
	}

	public String getBelowRisePrice() {
		return mBelowRisePrice;
	}

	public void setBelowRisePrice(String belowRisePrice) {
		this.mBelowRisePrice = belowRisePrice;
	}

	public String getMaxRisePrice() {
		return mMaxRisePrice;
	}

	public void setMaxRisePrice(String maxRisePrice) {
		this.mMaxRisePrice = maxRisePrice;
	}

	public String getBiddingTime() {
		return mBiddingTime;
	}

	public void setBiddingTime(String biddingTime) {
		this.mBiddingTime = biddingTime;
	}

	public String getBiddingAdditionalTime() {
		return mBiddingAdditionalTime;
	}

	public void setBiddingAdditionalTime(String biddingAdditionalTime) {
		this.mBiddingAdditionalTime = biddingAdditionalTime;
	}

	public String getBiddingIntervalTime() {
		return mBiddingIntervalTime;
	}

	public void setBiddingIntervalTime(String biddingIntervalTime) {
		this.mBiddingIntervalTime = biddingIntervalTime;
	}

	public String getMaxAutoUpCount() {
		return mMaxAutoUpCount;
	}

	public void setMaxAutoUpCount(String maxAutoUpCount) {
		this.mMaxAutoUpCount = maxAutoUpCount;
	}

	public String getFlagAuctionStart() {
		return this.mFlagAuctionStart;
	}

	public void setFlagAuctionStart(String flagAuctionStart) {
		this.mFlagAuctionStart = flagAuctionStart;
	}

	public String getFlagAuctionAutoMode() {
		return mFlagAuctionAutoMode;
	}

	public void setFlagAuctionAutoMode(String flagAuctionAutoMode) {
		this.mFlagAuctionAutoMode = flagAuctionAutoMode;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE,
				AuctionShareSetting.DELIMITER, mBaseStartPrice, AuctionShareSetting.DELIMITER, mMoreRisePrice,
				AuctionShareSetting.DELIMITER, mBelowRisePrice, AuctionShareSetting.DELIMITER, mMaxRisePrice,
				AuctionShareSetting.DELIMITER, mBiddingTime, AuctionShareSetting.DELIMITER, mBiddingAdditionalTime,
				AuctionShareSetting.DELIMITER, mBiddingIntervalTime, AuctionShareSetting.DELIMITER, mMaxAutoUpCount,
				AuctionShareSetting.DELIMITER, mFlagAuctionStart, AuctionShareSetting.DELIMITER, mFlagAuctionAutoMode);
	}
}
