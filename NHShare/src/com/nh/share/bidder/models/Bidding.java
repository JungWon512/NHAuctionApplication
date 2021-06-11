package com.nh.share.bidder.models;

import java.io.Serializable;

import com.nh.share.bidder.interfaces.FromAuctionBidder;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 응찰 처리 기능
 * 
 * 응찰기 -> 서버 -> 제어
 * 
 * BB|접속채널|경매회원번호|부재자여부(Y/N)|응찰금액(만원)|출품번호
 *
 */
public class Bidding implements FromAuctionBidder, Serializable {
	private static final long serialVersionUID = 1L;
	public static final char TYPE = 'B';
	private String mChannel;
	private String mUserNo;
	private String mAbsentee;
	private String mPrice;
	private String mBiddingTime;
	private String mEntryNum;

	private int mPriceInt;

	public Bidding(String channel, String userNo, String absentee, String price, String entryNum) {
		mChannel = channel;
		mUserNo = userNo;
		mAbsentee = absentee;
		mPrice = price;
		mEntryNum = entryNum;

		mPriceInt = Integer.parseInt(mPrice);
	}

	public String getChannel() {
		return mChannel;
	}

	public void setChannel(String channel) {
		this.mChannel = channel;
	}

	public String getUserNo() {
		return mUserNo;
	}

	public void setUserNo(String userNo) {
		this.mUserNo = userNo;
	}

	public String getAbsentee() {
		return mAbsentee;
	}

	public void setAbsentee(String absentee) {
		this.mAbsentee = absentee;
	}

	public String getPrice() {
		return mPrice;
	}

	public void setPrice(String price) {
		this.mPrice = price;
		mPriceInt = Integer.parseInt(mPrice);
	}

	public int getPriceInt() {
		return mPriceInt;
	}

	public void setPriceInt(int price) {
		this.mPriceInt = price;
	}

	public String getBiddingTime() {
		return mBiddingTime;
	}

	public void setBiddingTime(String biddingTime) {
		this.mBiddingTime = biddingTime;
	}

	public String getBiddingInfoForLog() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mChannel,
				AuctionShareSetting.DELIMITER, mUserNo, AuctionShareSetting.DELIMITER, mAbsentee,
				AuctionShareSetting.DELIMITER, mPrice, AuctionShareSetting.DELIMITER, mEntryNum,
				AuctionShareSetting.DELIMITER, mBiddingTime);
	}

	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mChannel,
				AuctionShareSetting.DELIMITER, mUserNo, AuctionShareSetting.DELIMITER, mAbsentee,
				AuctionShareSetting.DELIMITER, mPrice, AuctionShareSetting.DELIMITER, mEntryNum);
	}
}
