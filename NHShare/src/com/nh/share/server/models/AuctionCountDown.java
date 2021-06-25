package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 시작 카운트 다운 정보 전송
 * 
 * 경매서버 -> 공통
 * 
 * SD | 경매거점코드 | 상태구분(R : 준비 / C : 카운트다운 / F : 카운트다운 완료) |
 * 카운트다운 시간(second)
 *
 */
public class AuctionCountDown implements FromAuctionServer {
	public static final char TYPE = 'D';
	private String mAuctionHouseCode;
	private String mStatus; // 상태구분 (R : 준비 / C : 카운트다운)
	private String mCountDownTime; // 카운트다운시간

	public AuctionCountDown() {

	}

	public AuctionCountDown(String auctionHouseCode, String status, String countDownTime) {
		mAuctionHouseCode = auctionHouseCode;
		mStatus = status;
		mCountDownTime = countDownTime;
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

	public String getCountDownTime() {
		return mCountDownTime;
	}

	public void setCountDownTime(String countDownTime) {
		this.mCountDownTime = countDownTime;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mStatus, AuctionShareSetting.DELIMITER, mCountDownTime);
	}
}
