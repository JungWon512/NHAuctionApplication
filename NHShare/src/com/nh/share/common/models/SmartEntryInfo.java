package com.nh.share.common.models;

import java.io.Serializable;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 스마트계류 정보 전송 요청
 * 
 * 모바일업무 -> 경매서버 -> 스마트계류시스템
 *
 * AV | 조합구분코드 | 출품번호(경매번호) | 경매회차 |  경매대상구분코드 | 농가명 | 축산개체관리번호(이표) | 개체성별코드
 *
 */
public class SmartEntryInfo implements FromAuctionCommon, Serializable {
	private static final long serialVersionUID = 1L;
	public static final char TYPE = 'V';
	private String mAuctionHouseCode; // 조합구분코드
	private String mEntryNum; // 출품 번호
	private String mAuctionQcn; // 경매회차
	private String mEntryType; // 경매대상구분코드(1 : 송아지 / 2 : 비육우 / 3 : 번식우)
	private String mExhibitor; // 농가명
	private String mIndNum; // 축산개체관리번호
	private String mGender; // 개체성별코드
	
	public SmartEntryInfo(String auctionHouseCode, String entryNum, String auctionQcn, String entryType, String indNum, String exhibitor, String gender) {
		mAuctionHouseCode = auctionHouseCode;
		mEntryNum = entryNum;
		mAuctionQcn = auctionQcn;
		mEntryType = entryType;
		mExhibitor = exhibitor;
		mIndNum = indNum;
		mGender = gender;
	}

	public SmartEntryInfo(String[] messages) {
		mAuctionHouseCode = messages[1];
		mEntryNum = messages[2];
		mAuctionQcn = messages[3];
		mEntryType = messages[4];
		mExhibitor = messages[5];
		mIndNum = messages[6];
		mGender = messages[7];
	}

	public SmartEntryInfo(EntryInfo entryInfo) {
		mAuctionHouseCode = entryInfo.getAuctionHouseCode();
		mEntryNum = entryInfo.getEntryNum();
		mAuctionQcn = entryInfo.getAuctionQcn();
		mEntryType = entryInfo.getEntryType();
		mExhibitor = entryInfo.getExhibitor();
		mIndNum = entryInfo.getIndNum();
		mGender = entryInfo.getGender();
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

	public void setEntryNum(String mEntryNum) {
		this.mEntryNum = mEntryNum;
	}

	public String getAuctionQcn() {
		return mAuctionQcn;
	}

	public void setAuctionQcn(String auctionQcn) {
		this.mAuctionQcn = auctionQcn;
	}
	
	public String getEntryType() {
		return mEntryType;
	}

	public void setEntryType(String mEntryType) {
		this.mEntryType = mEntryType;
	}

	public String getExhibitor() {
		return mExhibitor;
	}

	public void setExhibitor(String mExhibitor) {
		this.mExhibitor = mExhibitor;
	}

	public String getIndNum() {
		return mIndNum;
	}

	public void setIndNum(String mIndNum) {
		this.mIndNum = mIndNum;
	}

	public String getGender() {
		return mGender;
	}

	public void setGender(String mGender) {
		this.mGender = mGender;
	}

	@Override
	public String getEncodedMessage() {
		return String.format(
				"%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s",
				ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode, AuctionShareSetting.DELIMITER,
				mEntryNum, AuctionShareSetting.DELIMITER, mAuctionQcn, AuctionShareSetting.DELIMITER, mEntryType, AuctionShareSetting.DELIMITER, mExhibitor, AuctionShareSetting.DELIMITER, mIndNum,
				AuctionShareSetting.DELIMITER, mGender);
	}
}
