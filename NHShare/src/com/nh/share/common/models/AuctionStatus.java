package com.nh.share.common.models;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 상태 정보 전송
 * 
 * 서버 -> 공통
 * 
 * SA|출품번호|시작가|현재응찰자수|경매상태(NONE/READY/START/PROGRESS/COMPETITIVE/SUCCESS/FAIL/STOP/COMPLETED/FINISH)|1순위회원번호|2순위회원번호|3순위회원번호|경매진행완료출품수|경매잔여출품수
 *
 */
public class AuctionStatus implements FromAuctionCommon {
	public static final char TYPE = 'S';
	private String mEntryNum; // 출품번호
	private String mStartPrice; // 시작가
	private String mCurrentBidderCount; // 현재 응찰자 수
	private String mState; // 경매상태
	private String mRank1MemberNum; // 1순위 회원번호
	private String mRank2MemberNum; // 2순위 회원번호
	private String mRank3MemberNum; // 3순위 회원번호
	private String mFinishEntryCount; // 경매 진행 완료 출품수
	private String mRemainEntryCount; // 경매 잔여 출품수

	// HazelcastSerializeException을 막기위한 생성자
	public AuctionStatus() {

	}

	public AuctionStatus(String entryNum, String startPrice, String currentBidderCount, String state, String rank1,
			String rank2, String rank3, String finishEntryCount, String remainEntryCount) {
		mEntryNum = entryNum;
		mStartPrice = startPrice;
		mCurrentBidderCount = currentBidderCount;
		mState = state;
		mRank1MemberNum = rank1;
		mRank2MemberNum = rank2;
		mRank3MemberNum = rank3;
		mFinishEntryCount = finishEntryCount;
		mRemainEntryCount = remainEntryCount;
	}

	public AuctionStatus(String[] messages) {
		mEntryNum = messages[1];
		mStartPrice = messages[2];
		mCurrentBidderCount = messages[3];
		mState = messages[4];
		mRank1MemberNum = messages[5];
		mRank2MemberNum = messages[6];
		mRank3MemberNum = messages[7];
		mFinishEntryCount = messages[8];
		mRemainEntryCount = messages[9];
	}

	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	public String getStartPrice() {
		return mStartPrice;
	}

	public void setStartPrice(String startPrice) {
		this.mStartPrice = startPrice;
	}

	public String getCurrentBidderCount() {
		return mCurrentBidderCount;
	}

	public void setCurrentBidderCount(String currentBidderCount) {
		this.mCurrentBidderCount = currentBidderCount;
	}

	public String getState() {
		return mState;
	}

	public void setState(String state) {
		this.mState = state;
	}

	public String getRank1MemberNum() {
		return mRank1MemberNum;
	}

	public void setRank1MemberNum(String rank1MemberNum) {
		this.mRank1MemberNum = rank1MemberNum;
	}

	public String getRank2MemberNum() {
		return mRank2MemberNum;
	}

	public void setRank2MemberNum(String rank2MemberNum) {
		this.mRank2MemberNum = rank2MemberNum;
	}

	public String getRank3MemberNum() {
		return mRank3MemberNum;
	}

	public void setRank3MemberNum(String rank3MemberNum) {
		this.mRank3MemberNum = rank3MemberNum;
	}

	public String getFinishEntryCount() {
		return mFinishEntryCount;
	}

	public void setFinishEntryCount(String finishEntryCount) {
		this.mFinishEntryCount = finishEntryCount;
	}

	public String getRemainEntryCount() {
		return mRemainEntryCount;
	}

	public void setRemainEntryCount(String remainEntryCount) {
		this.mRemainEntryCount = remainEntryCount;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER,
				mEntryNum, AuctionShareSetting.DELIMITER, mStartPrice, AuctionShareSetting.DELIMITER,
				mCurrentBidderCount, AuctionShareSetting.DELIMITER, mState, AuctionShareSetting.DELIMITER,
				mRank1MemberNum, AuctionShareSetting.DELIMITER, mRank2MemberNum, AuctionShareSetting.DELIMITER,
				mRank3MemberNum, AuctionShareSetting.DELIMITER, mFinishEntryCount, AuctionShareSetting.DELIMITER,
				mRemainEntryCount);
	}
}
