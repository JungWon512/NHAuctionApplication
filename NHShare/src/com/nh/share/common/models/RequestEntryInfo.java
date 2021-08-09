package com.nh.share.common.models;

import java.io.Serializable;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 출품 정보 전송 요청
 * 
 * 응찰기 -> 경매서버 -> 제어
 * 
 * AE | 조합구분코드 | 거래인관리번호 | 출품번호
 *
 */
public class RequestEntryInfo implements FromAuctionCommon, Serializable {
	private static final long serialVersionUID = 1L;
	public static final char TYPE = 'E';
	private String mAuctionHouseCode;
	private String mUserNo; // 거래인관리번호
	private String mEntryNum;

	public RequestEntryInfo(String auctionHouseCode, String userNo, String entryNum) {
		mAuctionHouseCode = auctionHouseCode;
		mUserNo = userNo;
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

	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mUserNo, AuctionShareSetting.DELIMITER, mEntryNum);
	}
}
