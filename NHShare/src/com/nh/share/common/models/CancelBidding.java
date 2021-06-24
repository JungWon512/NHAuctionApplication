package com.nh.share.common.models;

import java.io.Serializable;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 응찰 처리 기능
 * 
 * 응찰기 -> 경매서버 -> 제어
 * 
 * AC | 경매거점코드 | 출품번호 | 낙찰자회원번호
 *
 */
public class CancelBidding implements FromAuctionCommon, Serializable {
	private static final long serialVersionUID = 1L;
	public static final char TYPE = 'C';
	private String mAuctionHouseCode;
	private String mEntryNum;
	private String mUserNo;
	private String mChannel;
	private String mCancelBiddingTime;

	public CancelBidding(String auctionHouseCode, String entryNum, String userNo, String channel,
			String cancelBiddingTime) {
		mAuctionHouseCode = auctionHouseCode;
		mUserNo = userNo;
		mChannel = channel;
		mEntryNum = entryNum;
		mCancelBiddingTime = cancelBiddingTime;
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

	public String getChannel() {
		return mChannel;
	}

	public void setChannel(String channel) {
		this.mChannel = channel;
	}

	public String getCancelBiddingTime() {
		return mCancelBiddingTime;
	}

	public void setCancelBiddingTime(String cancelBiddingTime) {
		this.mCancelBiddingTime = cancelBiddingTime;
	}

	public String getBiddingInfoForLog() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mEntryNum, AuctionShareSetting.DELIMITER, mUserNo,
				AuctionShareSetting.DELIMITER, mChannel, AuctionShareSetting.DELIMITER, mCancelBiddingTime);
	}

	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mEntryNum, AuctionShareSetting.DELIMITER, mUserNo,
				AuctionShareSetting.DELIMITER, mChannel, AuctionShareSetting.DELIMITER, mCancelBiddingTime);
	}
}
