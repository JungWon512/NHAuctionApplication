package com.nh.share.server.models;

import com.nh.share.controller.models.RequestShowFailBidding;
import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 유찰 대상 목록 표시 요청(일괄경매)
 * 
 * 경매서버 -> 출하안내시스템
 * 
 * SZ | 조합구분코드 | 경매일자(YYYYmmdd) | 경매구분(송아지 : 1 / 비육우 : 2 / 번식우 : 3) | 경매등록일련번호
 *
 */
public class ShowFailBidding implements FromAuctionServer {
	public static final char TYPE = 'Z';
	private String mAuctionHouseCode; // 거점코드
	private String mAuctionDate; // 경매일자(YYYYmmdd)
	private String mAucObjDsc; // 경매구분코드(송아지/비육우/번식우)
	private String mRgSqNo; // 경매등록일련번호

	public ShowFailBidding(String auctionHouseCode, String auctionDate, String aucObjDsc, String rgSqNo) {
		mAuctionHouseCode = auctionHouseCode;
		mAuctionDate = auctionDate;
		mAucObjDsc = aucObjDsc;
		mRgSqNo = rgSqNo;
	}

	public ShowFailBidding(RequestShowFailBidding requestShowFailBidding) {
		mAuctionHouseCode = requestShowFailBidding.getAuctionHouseCode();
		mAuctionDate = requestShowFailBidding.getAuctionDate();
		mAucObjDsc = requestShowFailBidding.getAucObjDsc();
		mRgSqNo = requestShowFailBidding.getRgSqNo();
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

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mAuctionDate, AuctionShareSetting.DELIMITER, mAucObjDsc,
				AuctionShareSetting.DELIMITER, mRgSqNo);
	}

}