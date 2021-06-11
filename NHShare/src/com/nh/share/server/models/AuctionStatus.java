package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * TTS 송출 기능
 * 
 * 서버 -> 공통
 * 
 * SA|출품번호|현재가|현재응찰자수|경매상태|다음응찰|남은시간|1순위회원번호|2순위회원번호|3순위회원번호|경매진행완료출품수|경매잔여출품수
 *
 */
public class AuctionStatus implements FromAuctionServer {
	public static final char TYPE = 'A';
	private String mEntryNum; // 출품번호
	private String mCurrentPrice; // 현재가
	private String mCurrentBidderCount; // 현재 응찰자 수
	private String mState; // 경매상태
	private String mNextPrice; // 다음응찰
	private String mTime; // 남은시간
	private String mRank1MemberNum; // 1순위 회원번호
	private String mRank2MemberNum; // 2순위 회원번호
	private String mRank3MemberNum; // 3순위 회원번호
	private String mFinishEntryCount; // 경매 진행 완료 출품수
	private String mRemainEntryCount; // 경매 잔여 출품수

	// HazelcastSerializeException을 막기위한 생성자
	public AuctionStatus() {

	}

	public AuctionStatus(String entryNum, String currentPrice, String currentBidderCount, String state,
			String nextPrice, String remainTime, String rank1, String rank2, String rank3, String finishEntryCount,
			String remainEntryCount) {
		mEntryNum = entryNum;
		mCurrentPrice = currentPrice;
		mCurrentBidderCount = currentBidderCount;
		mState = state;
		mNextPrice = nextPrice;
		mTime = remainTime;
		mRank1MemberNum = rank1;
		mRank2MemberNum = rank2;
		mRank3MemberNum = rank3;
		mFinishEntryCount = finishEntryCount;
		mRemainEntryCount = remainEntryCount;
	}

	public AuctionStatus(String[] messages) {
		mEntryNum = messages[1];
		mCurrentPrice = messages[2];
		mCurrentBidderCount = messages[3];
		mState = messages[4];
		mNextPrice = messages[5];
		mTime = messages[6];
		mRank1MemberNum = messages[7];
		mRank2MemberNum = messages[8];
		mRank3MemberNum = messages[9];
		mFinishEntryCount = messages[10];
		mRemainEntryCount = messages[11];
	}

	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	public String getCurrentPrice() {
		return mCurrentPrice;
	}

	public void setCurrentPrice(String currentPrice) {
		this.mCurrentPrice = currentPrice;
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

	public String getNextPrice() {
		return mNextPrice;
	}

	public void setNextPrice(String nextPrice) {
		this.mNextPrice = nextPrice;
	}

	public String getTime() {
		return mTime;
	}

	public void setTime(String time) {
		this.mTime = time;
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
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE,
				AuctionShareSetting.DELIMITER, mEntryNum, AuctionShareSetting.DELIMITER, mCurrentPrice,
				AuctionShareSetting.DELIMITER, mCurrentBidderCount, AuctionShareSetting.DELIMITER, mState,
				AuctionShareSetting.DELIMITER, mNextPrice, AuctionShareSetting.DELIMITER, mTime,
				AuctionShareSetting.DELIMITER, mRank1MemberNum, AuctionShareSetting.DELIMITER, mRank2MemberNum,
				AuctionShareSetting.DELIMITER, mRank3MemberNum, AuctionShareSetting.DELIMITER, mFinishEntryCount,
				AuctionShareSetting.DELIMITER, mRemainEntryCount);
	}
}
