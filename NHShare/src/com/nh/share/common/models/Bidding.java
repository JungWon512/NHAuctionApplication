package com.nh.share.common.models;

import java.io.Serializable;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 응찰 처리 기능
 * 
 * 응찰기 -> 경매서버 -> 제어
 * 
 * AB | 경매거점코드 | 접속채널(ANDROID/IOS/WEB) | 경매회원번호 | 출품번호 | 응찰금액(만원) | 신규응찰여부(Y/N)
 *
 */
public class Bidding implements FromAuctionCommon, Serializable {
	private static final long serialVersionUID = 1L;
	public static final char TYPE = 'B';
	private String mAuctionHouseCode;
	private String mChannel;
	private String mUserNo;
	private String mPrice;
	private String mBiddingTime;
	private String mEntryNum;
	private String mIsNewBid;

	private int mPriceInt;

	public Bidding(String auctionHouseCode, String channel, String userNo, String entryNum, String price,
			String isNewBid) {
		mAuctionHouseCode = auctionHouseCode;
		mChannel = channel;
		mUserNo = userNo;
		mPrice = price;
		mEntryNum = entryNum;
		mPriceInt = Integer.parseInt(mPrice);
		mIsNewBid = isNewBid;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
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

	public String getIsNewBid() {
		return mIsNewBid;
	}

	public void setIsNewBid(String isNewBid) {
		this.mIsNewBid = isNewBid;
	}

	public String getBiddingTime() {
		return mBiddingTime;
	}

	public void setBiddingTime(String biddingTime) {
		this.mBiddingTime = biddingTime;
	}

	public String getBiddingInfoForLog() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER,
				mAuctionHouseCode, AuctionShareSetting.DELIMITER, mChannel, AuctionShareSetting.DELIMITER, mUserNo,
				AuctionShareSetting.DELIMITER, mEntryNum, AuctionShareSetting.DELIMITER, mPrice,
				AuctionShareSetting.DELIMITER, mIsNewBid, AuctionShareSetting.DELIMITER, mBiddingTime);
	}

	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER,
				mAuctionHouseCode, AuctionShareSetting.DELIMITER, mChannel, AuctionShareSetting.DELIMITER, mUserNo,
				AuctionShareSetting.DELIMITER, mEntryNum, AuctionShareSetting.DELIMITER, mPrice,
				AuctionShareSetting.DELIMITER, mIsNewBid, AuctionShareSetting.DELIMITER, mBiddingTime);
	}
}
