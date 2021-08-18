package com.nh.share.common.models;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 상태 정보 전송
 * 
 * 경매서버/제어프로그램 -> 공통
 * 
 * AS | 조합구분코드 | 출품번호 | 경매회차 | 시작가 | 현재응찰자수 | 경매상태(NONE / READY / START /
 * PROGRESS / COMPETITIVE / SUCCESS / FAIL / STOP / COMPLETED / FINISH) |
 * 1순위회원번호 | 2순위회원번호 | 3순위회원번호 | 경매진행완료출품수 | 경매잔여출품수
 *
 */
public class AuctionStatus implements FromAuctionCommon {
	public static final char TYPE = 'S';
	private String mAuctionHouseCode; // 거점코드
	private String mEntryNum; // 출품번호
	private String mAuctionQcn; // 경매회차
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

	public AuctionStatus(String auctionHouseCode, String entryNum, String auctionQcn, String startPrice,
			String currentBidderCount, String state, String rank1, String rank2, String rank3, String finishEntryCount,
			String remainEntryCount) {
		mAuctionHouseCode = auctionHouseCode;
		mEntryNum = entryNum;
		mAuctionQcn = auctionQcn;
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
		mAuctionHouseCode = messages[1];
		mEntryNum = messages[2];
		mAuctionQcn = messages[3];
		mStartPrice = messages[4];
		mCurrentBidderCount = messages[5];
		mState = messages[6];
		mRank1MemberNum = messages[7];
		mRank2MemberNum = messages[8];
		mRank3MemberNum = messages[9];
		mFinishEntryCount = messages[10];
		mRemainEntryCount = messages[11];
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

	public String getAuctionQcn() {
		return mAuctionQcn;
	}

	public void setAuctionQcn(String auctionQcn) {
		this.mAuctionQcn = auctionQcn;
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
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE,
				AuctionShareSetting.DELIMITER, mAuctionHouseCode, AuctionShareSetting.DELIMITER, mEntryNum,
				AuctionShareSetting.DELIMITER, mAuctionQcn, AuctionShareSetting.DELIMITER, mStartPrice,
				AuctionShareSetting.DELIMITER, mCurrentBidderCount, AuctionShareSetting.DELIMITER, mState,
				AuctionShareSetting.DELIMITER, mRank1MemberNum, AuctionShareSetting.DELIMITER, mRank2MemberNum,
				AuctionShareSetting.DELIMITER, mRank3MemberNum, AuctionShareSetting.DELIMITER, mFinishEntryCount,
				AuctionShareSetting.DELIMITER, mRemainEntryCount);
	}
}
