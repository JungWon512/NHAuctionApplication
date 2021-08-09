package com.nh.share.common.models;

import java.io.Serializable;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 응찰 정보 조회 요청 기능
 * 
 * 응찰기 -> 경매서버 -> 제어
 * 
 * AD | 조합구분코드 | 거래인관리번호 | 경매참여번호 | 출품번호
 *
 */
public class RequestBiddingInfo implements FromAuctionCommon, Serializable {
	private static final long serialVersionUID = 1L;
	public static final char TYPE = 'D';
	private String mAuctionHouseCode;
	private String mUserNo;
	private String mAuctionJoinNum;
	private String mEntryNum;

	public RequestBiddingInfo(String auctionHouseCode, String userNo, String auctionJoinNum, String entryNum) {
		mAuctionHouseCode = auctionHouseCode;
		mUserNo = userNo;
		mAuctionJoinNum = auctionJoinNum;
		mEntryNum = entryNum;
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

	public String getAuctionJoinNum() {
		return mAuctionJoinNum;
	}

	public void setAuctionJoinNum(String auctionJoinNum) {
		this.mAuctionJoinNum = auctionJoinNum;
	}

	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mUserNo, AuctionShareSetting.DELIMITER, mAuctionJoinNum,
				AuctionShareSetting.DELIMITER, mEntryNum);
	}
}
