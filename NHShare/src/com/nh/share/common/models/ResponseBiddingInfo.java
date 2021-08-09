package com.nh.share.common.models;

import java.io.Serializable;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 응찰 정보 응답 전송 기능
 * 
 * 제어 -> 경매서버 -> 응찰기
 * 
 * AP | 조합구분코드 | 거래인관리번호 | 출품번호 | 응찰금액(만원) | 응찰시간(yyyyMMddhhmmssSSS)
 *
 */
public class ResponseBiddingInfo implements FromAuctionCommon, Serializable {
	private static final long serialVersionUID = 1L;
	public static final char TYPE = 'P';
	private String mAuctionHouseCode;
	private String mUserNo; // 거래인관리번호
	private String mEntryNum;
	private String mPrice;
	private String mBiddingTime;

	public ResponseBiddingInfo(String auctionHouseCode, String userNo, String entryNum, String price,
			String biddingTime) {
		mAuctionHouseCode = auctionHouseCode;
		mUserNo = userNo;
		mEntryNum = entryNum;
		mPrice = price;
		mBiddingTime = biddingTime;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getUserNo() {
		return mUserNo;
	}

	public void setUserNo(String userNo) {
		this.mUserNo = userNo;
	}

	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	public String getPrice() {
		return mPrice;
	}

	public void setPrice(String price) {
		this.mPrice = price;
	}

	public String getBiddingTime() {
		return mBiddingTime;
	}

	public void setBiddingTime(String biddingTime) {
		this.mBiddingTime = biddingTime;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mUserNo, AuctionShareSetting.DELIMITER, mEntryNum,
				AuctionShareSetting.DELIMITER, mPrice, AuctionShareSetting.DELIMITER, mBiddingTime);
	}
}
