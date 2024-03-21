package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 설정 변경 처리
 * <p>
 * 제어프로그램 -> 경매서버
 * <p>
 * CE | 조합구분코드 | 경매번호노출여부 | 출하주노출여부 | 성별노출여부 | 중량노출여부 | 어미노출여부 | 계대노출여부 | 산차노출여부
 * | KPN노출여부 | 지여명노출여부 | 비고노출여부 | 최저가노출여부 | 친자노출여부 | 카운트다운초설정값(1 ~ 9) |
 * 경매유형코드(10:일괄 / 20:단일) | 경매상한가(송아지) | 경매상한가(비육우) | 경매상한가(번식우) | 비육우응찰단위(1 : 원 / 1000 : 천원 / 10000 : 만원)
 * | 경매상한가(염소) | 경매상한가(말)
 */
public class EditSetting implements FromAuctionController {
	public static final char TYPE = 'E';
	private String mAuctionHouseCode; // 거점코드
	private String mIsShowEntryNum; // 경매번호노출여부
	private String mIsShowExhUser; // 출하주노출여부
	private String mIsShowGender; // 성별노출여부
	private String mIsShowWeight; // 중량노출여부
	private String mIsShowMother; // 어미노출여부
	private String mIsShowPasg; // 계대노출여부
	private String mIsShowCaving; // 산차노출여부
	private String mIsShowKpn; // KPN노출여부
	private String mIsShowLocation; // 지역명노출여부
	private String mIsShowNote; // 비고노출여부
	private String mIsShowLowPrice; // 최저가노출여부
	private String mIsShowDna; // 친자노출여부
	private String mCountDown; // 카운트다운횟수(초)
	private String mAuctionType; // 경매유형코드(10:일괄/20:단일)
	private String mAuctionLimitPrice1; // 경매상한가(송아지 일괄경매사용)
	private String mAuctionLimitPrice2; // 경매상한가(비육우 일괄경매사용)
	private String mAuctionLimitPrice3; // 경매상한가(번식우 일괄경매사용)

	private String mCutAm; // 비육우 응찰 단위
	private String mAuctionLimitPrice5; // 경매상한가(염소 일괄경매사용)
	private String mAuctionLimitPrice6; // 경매상한가(말 일괄경매사용)
	
	public EditSetting(String[] messages) {
		this.mAuctionHouseCode = messages[1];
		this.mIsShowEntryNum = messages[2];
		this.mIsShowExhUser = messages[3];
		this.mIsShowGender = messages[4];
		this.mIsShowWeight = messages[5];
		this.mIsShowMother = messages[6];
		this.mIsShowPasg = messages[7];
		this.mIsShowCaving = messages[8];
		this.mIsShowKpn = messages[9];
		this.mIsShowLocation = messages[10];
		this.mIsShowNote = messages[11];
		this.mIsShowLowPrice = messages[12];
		this.mIsShowDna = messages[13];
		this.mCountDown = messages[14];
		this.mAuctionType = messages[15];
		this.mAuctionLimitPrice1 = messages[16];
		this.mAuctionLimitPrice2 = messages[17];
		this.mAuctionLimitPrice3 = messages[18];
		
		System.out.println("message size : " + messages.length);
		System.out.println("EditSetting message : " + messages);
		if (messages.length > 19 && messages[19] != null) {
				this.mCutAm = messages[19];
				if (messages.length > 20 && messages[20] != null) {
					this.mAuctionLimitPrice5 = messages[20];
					this.mAuctionLimitPrice6 = messages[21];
				}else {
					this.mAuctionLimitPrice5 = "";
					this.mAuctionLimitPrice6 = "";
				}
		} else {
			this.mCutAm = "";
			this.mAuctionLimitPrice5 = "";
			this.mAuctionLimitPrice6 = "";
		}
	}

	public EditSetting(String mAuctionHouseCode, String mIsShowEntryNum, String mIsShowExhUser, String mIsShowGender,
			String mIsShowWeight, String mIsShowMother, String mIsShowPasg, String mIsShowCaving, String mIsShowKpn,
			String mIsShowLocation, String mIsShowNote, String mIsShowLowPrice, String mIsShowDna, String mCountDown,
			String mAuctionType, String mAuctionLimitPrice1, String mAuctionLimitPrice2, String mAuctionLimitPrice3,String cutAm
			, String mAuctionLimitPrice5, String mAuctionLimitPrice6
		) {
		this.mAuctionHouseCode = mAuctionHouseCode;
		this.mIsShowEntryNum = mIsShowEntryNum;
		this.mIsShowExhUser = mIsShowExhUser;
		this.mIsShowGender = mIsShowGender;
		this.mIsShowWeight = mIsShowWeight;
		this.mIsShowMother = mIsShowMother;
		this.mIsShowPasg = mIsShowPasg;
		this.mIsShowCaving = mIsShowCaving;
		this.mIsShowKpn = mIsShowKpn;
		this.mIsShowLocation = mIsShowLocation;
		this.mIsShowNote = mIsShowNote;
		this.mIsShowLowPrice = mIsShowLowPrice;
		this.mIsShowDna = mIsShowDna;
		this.mCountDown = mCountDown;
		this.mAuctionType = mAuctionType;
		this.mAuctionLimitPrice1 = mAuctionLimitPrice1;
		this.mAuctionLimitPrice2 = mAuctionLimitPrice2;
		this.mAuctionLimitPrice3 = mAuctionLimitPrice3;
		this.mCutAm = cutAm;
		this.mAuctionLimitPrice5 = mAuctionLimitPrice5;
		this.mAuctionLimitPrice6 = mAuctionLimitPrice6;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getIsShowEntryNum() {
		return mIsShowEntryNum;
	}

	public void setIsShowEntryNum(String isShowEntryNum) {
		this.mIsShowEntryNum = isShowEntryNum;
	}

	public String getIsShowExhUser() {
		return mIsShowExhUser;
	}

	public void setIsShowExhUser(String isShowExhUser) {
		this.mIsShowExhUser = isShowExhUser;
	}

	public String getIsShowGender() {
		return mIsShowGender;
	}

	public void setIsShowGender(String isShowGender) {
		this.mIsShowGender = isShowGender;
	}

	public String getIsShowWeight() {
		return mIsShowWeight;
	}

	public void setIsShowWeight(String isShowWeight) {
		this.mIsShowWeight = isShowWeight;
	}

	public String getIsShowMother() {
		return mIsShowMother;
	}

	public void setIsShowMother(String isShowMother) {
		this.mIsShowMother = isShowMother;
	}

	public String getIsShowPasg() {
		return mIsShowPasg;
	}

	public void setIsShowPasg(String isShowPasg) {
		this.mIsShowPasg = isShowPasg;
	}

	public String getIsShowCaving() {
		return mIsShowCaving;
	}

	public void setIsShowCaving(String isShowCaving) {
		this.mIsShowCaving = isShowCaving;
	}

	public String getIsShowKpn() {
		return mIsShowKpn;
	}

	public void setIsShowKpn(String isShowKpn) {
		this.mIsShowKpn = isShowKpn;
	}

	public String getIsShowLocation() {
		return mIsShowLocation;
	}

	public void setIsShowLocation(String isShowLocation) {
		this.mIsShowLocation = isShowLocation;
	}

	public String getIsShowNote() {
		return mIsShowNote;
	}

	public void setIsShowNote(String isShowNote) {
		this.mIsShowNote = isShowNote;
	}

	public String getIsShowLowPrice() {
		return mIsShowLowPrice;
	}

	public void setIsShowLowPrice(String isShowLowPrice) {
		this.mIsShowLowPrice = isShowLowPrice;
	}

	public String getIsShowDna() {
		return mIsShowDna;
	}

	public void setIsShowDna(String isShowDna) {
		this.mIsShowDna = isShowDna;
	}

	public String getCountDown() {
		return mCountDown;
	}

	public void setCountDown(String countDown) {
		this.mCountDown = countDown;
	}

	public String getAuctionType() {
		return mAuctionType;
	}

	public void setAuctionType(String auctionType) {
		this.mAuctionType = auctionType;
	}

	public String getmAuctionLimitPrice1() {
		return mAuctionLimitPrice1;
	}

	public void setmAuctionLimitPrice1(String mAuctionLimitPrice1) {
		this.mAuctionLimitPrice1 = mAuctionLimitPrice1;
	}
	
	public String getmAuctionLimitPrice2() {
		return mAuctionLimitPrice2;
	}

	public void setmAuctionLimitPrice2(String mAuctionLimitPrice2) {
		this.mAuctionLimitPrice2 = mAuctionLimitPrice2;
	}
	
	public String getmAuctionLimitPrice3() {
		return mAuctionLimitPrice3;
	}

	public void setmAuctionLimitPrice3(String mAuctionLimitPrice3) {
		this.mAuctionLimitPrice3 = mAuctionLimitPrice3;
	}

	public String getmCutAm() {
		return mCutAm;
	}

	public void setmCutAm(String mCutAm) {
		this.mCutAm = mCutAm;
	}

	@Override
	public String getEncodedMessage() {
		String result = String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE,
				AuctionShareSetting.DELIMITER, mAuctionHouseCode, AuctionShareSetting.DELIMITER, mIsShowEntryNum,
				AuctionShareSetting.DELIMITER, mIsShowExhUser, AuctionShareSetting.DELIMITER, mIsShowGender,
				AuctionShareSetting.DELIMITER, mIsShowWeight, AuctionShareSetting.DELIMITER, mIsShowMother,
				AuctionShareSetting.DELIMITER, mIsShowPasg, AuctionShareSetting.DELIMITER, mIsShowCaving,
				AuctionShareSetting.DELIMITER, mIsShowKpn, AuctionShareSetting.DELIMITER, mIsShowLocation,
				AuctionShareSetting.DELIMITER, mIsShowNote, AuctionShareSetting.DELIMITER, mIsShowLowPrice,
				AuctionShareSetting.DELIMITER, mIsShowDna, AuctionShareSetting.DELIMITER, mCountDown,
				AuctionShareSetting.DELIMITER, mAuctionType, AuctionShareSetting.DELIMITER, mAuctionLimitPrice1,
				AuctionShareSetting.DELIMITER, mAuctionLimitPrice2, AuctionShareSetting.DELIMITER, mAuctionLimitPrice3,
				AuctionShareSetting.DELIMITER,mCutAm,
				AuctionShareSetting.DELIMITER, mAuctionLimitPrice5, AuctionShareSetting.DELIMITER, mAuctionLimitPrice6);
		return result;
	}

	public String getmAuctionLimitPrice5() {
		return mAuctionLimitPrice5;
	}

	public void setmAuctionLimitPrice5(String mAuctionLimitPrice5) {
		this.mAuctionLimitPrice5 = mAuctionLimitPrice5;
	}

	public String getmAuctionLimitPrice6() {
		return mAuctionLimitPrice6;
	}

	public void setmAuctionLimitPrice6(String mAuctionLimitPrice6) {
		this.mAuctionLimitPrice6 = mAuctionLimitPrice6;
	}
	
	
}
