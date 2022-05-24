package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 유찰 대상 목록 표시 요청(일괄경매)
 * 
 * 제어프로그램 -> 경매서버
 * 
 * CZ | 조합구분코드 | 경매일자(YYYYmmdd) | 경매구분(송아지 : 1 / 비육우 : 2 / 번식우 : 3) | 경매등록일련번호 | 표시여부(Y:유찰대상표시 / N:경매진행화면표시 / P:경매진행대상표시 / A:기본영상표시 / H:일괄경매진행구간표시) | 경매대상시작번호 | 경매대상끝번호
 *
 */
public class RequestShowFailBidding implements FromAuctionController {
	public static final char TYPE = 'Z';
	private String mAuctionHouseCode; // 거점코드
	private String mAuctionDate; // 경매일자(YYYYmmdd)
	private String mAucObjDsc; // 경매구분코드(송아지/비육우/번식우)
	private String mRgSqNo; // 경매등록일련번호
	private String mIsShow; // 전광판표시여부
	private String mStartIndex; // 경매대상시작번호
	private String mEndIndex; // 경매대상끝번호

	public RequestShowFailBidding(String auctionHouseCode, String auctionDate, String aucObjDsc, String rgSqNo, String isShow, String startIndex, String endIndex) {
		mAuctionHouseCode = auctionHouseCode;
		mAuctionDate = auctionDate;
		mAucObjDsc = aucObjDsc;
		mRgSqNo = rgSqNo;
		mIsShow = isShow;
		mStartIndex = startIndex;
		mEndIndex = endIndex;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getAuctionDate() {
		return mAuctionDate;
	}

	public void setAuctionDate(String auctionDate) {
		this.mAuctionDate = auctionDate;
	}

	public String getAucObjDsc() {
		return mAucObjDsc;
	}

	public void setAucObjDsc(String aucObjDsc) {
		this.mAucObjDsc = aucObjDsc;
	}

	public String getRgSqNo() {
		return mRgSqNo;
	}

	public void setRgSqNo(String rgSqNo) {
		this.mRgSqNo = rgSqNo;
	}

	public String getIsShow() {
		return mIsShow;
	}

	public void setIsShow(String isShow) {
		this.mIsShow = isShow;
	}

	public String getStartIndex() {
		return mStartIndex;
	}

	public void setStartIndex(String startIndex) {
		this.mStartIndex = startIndex;
	}

	public String getEndIndex() {
		return mEndIndex;
	}

	public void setEndIndex(String endIndex) {
		this.mEndIndex = endIndex;
	}
	
	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mAuctionDate, AuctionShareSetting.DELIMITER, mAucObjDsc,
				AuctionShareSetting.DELIMITER, mRgSqNo, AuctionShareSetting.DELIMITER, mIsShow, AuctionShareSetting.DELIMITER, mStartIndex, AuctionShareSetting.DELIMITER, mEndIndex);
	}

}
