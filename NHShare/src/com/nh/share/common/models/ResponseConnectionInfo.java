package com.nh.share.common.models;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 접속 정보 응답 처리
 * 
 * 경매서버/제어프로그램 -> 공통
 * 
 * AR | 조합구분코드 | 접속결과코드 | 거래인관리번호 | 경매참가번호
 *
 */
public class ResponseConnectionInfo implements FromAuctionCommon {
	public static final char TYPE = 'R';
	private String mAuctionHouseCode; // 거점코드
	private String mUserMemNum; // 거래인관리번호
	private String mResult; // 결과코드
	private String mAuctionJoinNum; // 경매참가번호

	public ResponseConnectionInfo(String auctionHouseCode, String result, String userMemNum, String auctionJoinNum) {
		mAuctionHouseCode = auctionHouseCode;
		mUserMemNum = userMemNum;
		mResult = result;
		mAuctionJoinNum = auctionJoinNum;
	}
	
	public ResponseConnectionInfo(String auctionHouseCode, String result) {
		mAuctionHouseCode = auctionHouseCode;
		mResult = result;
		mUserMemNum = null;
		mAuctionJoinNum = null;
	}
	

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getUserMemNum() {
		return mUserMemNum;
	}

	public void setUserMemNum(String userMemNum) {
		this.mUserMemNum = userMemNum;
	}

	public String getResult() {
		return mResult;
	}

	public void setResult(String result) {
		this.mResult = result;
	}

	public String getAuctionJoinNum() {
		return mAuctionJoinNum;
	}

	public void setAuctionJoinNum(String auctionJoinNum) {
		this.mAuctionJoinNum = auctionJoinNum;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mResult, AuctionShareSetting.DELIMITER, mUserMemNum,
				AuctionShareSetting.DELIMITER, mAuctionJoinNum);
	}

	@Override
	public String toString() {
		return "ResponseConnectionInfo{" +
				"mAuctionHouseCode='" + mAuctionHouseCode + '\'' +
				", mUserMemNum='" + mUserMemNum + '\'' +
				", mResult='" + mResult + '\'' +
				", mAuctionJoinNum='" + mAuctionJoinNum + '\'' +
				'}';
	}
}
