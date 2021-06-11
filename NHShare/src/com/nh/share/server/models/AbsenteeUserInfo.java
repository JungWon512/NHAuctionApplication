package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 부재자 입찰 참여 여부에 대한 정보 전송 처리
 * 
 * 서버 -> 응찰기
 * 
 * SU|출품번호|부재자 입찰 여부(Y/N)|부재자 입찰 가격
 *
 */
public class AbsenteeUserInfo implements FromAuctionServer {
	public static final char TYPE = 'U';
	private String mEntryNum; // 출품번호
	private String mFlagBidAbsentee; // 부재자 입찰 여부(Y/N)
	private String mAbsenteePrice; // 부재자 입찰 가격

	public AbsenteeUserInfo(String entryNum, String flagBidAbsentee, String absenteePrice) {
		mEntryNum = entryNum;
		mFlagBidAbsentee = flagBidAbsentee;
		mAbsenteePrice = absenteePrice;
	}

	public AbsenteeUserInfo(String[] messages) {
		mEntryNum = messages[1];
		mFlagBidAbsentee = messages[2];
		mAbsenteePrice = messages[3];
	}

	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	public String getFlagBidAbsentee() {
		return mFlagBidAbsentee;
	}

	public void setFlagBidAbsentee(String flagBidAbsentee) {
		this.mFlagBidAbsentee = flagBidAbsentee;
	}

	public String getAbsenteePrice() {
		return mAbsenteePrice;
	}

	public void setAbsenteePrice(String absenteePrice) {
		this.mAbsenteePrice = absenteePrice;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mEntryNum,
				AuctionShareSetting.DELIMITER, mFlagBidAbsentee, AuctionShareSetting.DELIMITER, mAbsenteePrice);
	}
}
