package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 설정 변경 처리
 * 
 * 제어프로그램 -> 경매서버
 * 
 * CE | 조합구분코드 | 경매번호노출여부 | 출하주노출여부 | 성별노출여부 | 중량노출여부 | 어미노출여부 | 계대노출여부 | 산차노출여부
 * | KPN노출여부 | 지여명노출여부 | 비고노출여부 | 최저가노출여부 | 친자노출여부 | 카운트다운초설정값(1 ~ 9)
 *
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

	public EditSetting(String[] messages) {
		this.mAuctionHouseCode = messages[1];
		this.mIsShowEntryNum = messages[2];
		this.mIsShowExhUser = messages[3];
		this.mIsShowGender = messages[4];
		this.mIsShowWeight = messages[5];
		this.mIsShowMother = messages[6];
		this.mIsShowPasg = messages[6];
		this.mIsShowCaving = messages[7];
		this.mIsShowKpn = messages[8];
		this.mIsShowLocation = messages[10];
		this.mIsShowNote = messages[11];
		this.mIsShowLowPrice = messages[12];
		this.mIsShowDna = messages[13];
		this.mCountDown = messages[14];
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

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE,
				AuctionShareSetting.DELIMITER, mAuctionHouseCode, AuctionShareSetting.DELIMITER, mIsShowEntryNum,
				AuctionShareSetting.DELIMITER, mIsShowEntryNum, AuctionShareSetting.DELIMITER, mIsShowExhUser,
				AuctionShareSetting.DELIMITER, mIsShowGender, AuctionShareSetting.DELIMITER, mIsShowWeight,
				AuctionShareSetting.DELIMITER, mIsShowMother, AuctionShareSetting.DELIMITER, mIsShowPasg,
				AuctionShareSetting.DELIMITER, mIsShowCaving, AuctionShareSetting.DELIMITER, mIsShowKpn,
				AuctionShareSetting.DELIMITER, mIsShowLocation, AuctionShareSetting.DELIMITER, mIsShowNote,
				AuctionShareSetting.DELIMITER, mIsShowLowPrice, AuctionShareSetting.DELIMITER, mIsShowDna,
				AuctionShareSetting.DELIMITER, mCountDown);
	}
}
