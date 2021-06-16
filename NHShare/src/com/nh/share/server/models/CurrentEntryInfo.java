package com.nh.share.server.models;

import com.nh.share.controller.models.EntryInfo;
import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 현재 출품 정보 전송
 * 
 * 경매서버 -> 공통
 * 
 * SC|출품번호|출품유형|개체번호|출하주|생년월일|성별|중량|KPN|산차|어미|특이사항|경매일시|출품상태|시작가
 *
 */
public class CurrentEntryInfo implements FromAuctionServer {
	public static final char TYPE = 'C';
	private String mEntryNum; // 출품 번호
	private String mEntryType; // 출품 유형(큰소/송아지/육우)
	private String mIndNum; // 개체번호
	private String mExhibitor; // 출하주
	private String mBirthday; // 생년월일
	private String mGender; // 성별
	private String mWeight; // 중량
	private String mKpn; // KPN
	private String mCavingNum; // 산차
	private String mMother; // 어미 혈통
	private String mNote; // 특이사항
	private String mAuctDateTime; // 경매일시
	private String mEntryStatus; // 출품상태
	private String mStartPrice; // 시작가
	private String mIsLastEntry; // 마지막 출품 여부

	public CurrentEntryInfo(String entryNum, String entryType, String indNum, String exhibitor, String birthday, String gender,
			String weight, String kpn, String cavingNum, String mother, String note, String auctDateTime,
			String entryStatus, String startPrice, String isLastEntry) {
		mEntryNum = entryNum;
		mEntryType = entryType;
		mIndNum = indNum;
		mExhibitor = exhibitor;
		mBirthday = birthday;
		mGender = gender;
		mWeight = weight;
		mKpn = kpn;
		mCavingNum = cavingNum;
		mMother = mother;
		mNote = note;
		mAuctDateTime = auctDateTime;
		mEntryStatus = entryStatus;
		mStartPrice = startPrice;
		mIsLastEntry = isLastEntry;
	}

	public CurrentEntryInfo(String[] messages) {
		mEntryNum = messages[1];
		mEntryType = messages[2];
		mIndNum = messages[3];
		mExhibitor = messages[4];
		mBirthday = messages[5];
		mGender = messages[6];
		mWeight = messages[7];
		mKpn = messages[8];
		mCavingNum = messages[9];
		mMother = messages[10];
		mNote = messages[11];
		mAuctDateTime = messages[12];
		mEntryStatus = messages[13];
		mStartPrice = messages[14];
		mIsLastEntry = messages[15];
	}
	
	public CurrentEntryInfo(EntryInfo entryInfo) {
		mEntryNum = entryInfo.getEntryNum();
		mEntryType = entryInfo.getEntryType();
		mIndNum = entryInfo.getIndNum();
		mExhibitor = entryInfo.getExhibitor();
		mBirthday = entryInfo.getBirthday();
		mGender = entryInfo.getGender();
		mWeight = entryInfo.getWeight();
		mKpn = entryInfo.getKpn();
		mCavingNum = entryInfo.getCavingNum();
		mMother = entryInfo.getMother();
		mNote = entryInfo.getNote();
		mAuctDateTime = entryInfo.getAuctDateTime();
		mEntryStatus = entryInfo.getEntryStatus();
		mStartPrice = entryInfo.getStartPrice();
		mIsLastEntry = entryInfo.getIsLastEntry();
	}

	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	public String getEntryType() {
		return mEntryType;
	}

	public void setEntryType(String entryType) {
		this.mEntryType = entryType;
	}

	public String getIndNum() {
		return mIndNum;
	}

	public void setIndNum(String indNum) {
		this.mIndNum = indNum;
	}

	public String getExhibitor() {
		return mExhibitor;
	}

	public void setExhibitor(String exhibitor) {
		this.mExhibitor = exhibitor;
	}

	public String getBirthday() {
		return mBirthday;
	}

	public void setBirthday(String birthday) {
		this.mBirthday = birthday;
	}

	public String getGender() {
		return mGender;
	}

	public void setGender(String gender) {
		this.mGender = gender;
	}

	public String getWeight() {
		return mWeight;
	}

	public void setWeight(String weight) {
		this.mWeight = weight;
	}

	public String getKpn() {
		return mKpn;
	}

	public void setKpn(String kpn) {
		this.mKpn = kpn;
	}

	public String getCavingNum() {
		return mCavingNum;
	}

	public void setCavingNum(String cavingNum) {
		this.mCavingNum = cavingNum;
	}

	public String getMother() {
		return mMother;
	}

	public void setMother(String mother) {
		this.mMother = mother;
	}

	public String getNote() {
		return mNote;
	}

	public void setNote(String note) {
		this.mNote = note;
	}

	public String getAuctDateTime() {
		return mAuctDateTime;
	}

	public void setAuctDateTime(String auctDateTime) {
		this.mAuctDateTime = auctDateTime;
	}

	public String getEntryStatus() {
		return mEntryStatus;
	}

	public void setEntryStatus(String entryStatus) {
		this.mEntryStatus = entryStatus;
	}

	public String getStartPrice() {
		return mStartPrice;
	}

	public void setStartPrice(String startPrice) {
		this.mStartPrice = startPrice;
	}

	public String getIsLastEntry() {
		return mIsLastEntry;
	}

	public void setIsLastEntry(String isLastEntry) {
		this.mIsLastEntry = isLastEntry;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE,
				AuctionShareSetting.DELIMITER, mEntryNum, AuctionShareSetting.DELIMITER, mEntryType,
				AuctionShareSetting.DELIMITER, mIndNum, AuctionShareSetting.DELIMITER, mExhibitor,
				AuctionShareSetting.DELIMITER, mBirthday, AuctionShareSetting.DELIMITER, mGender,
				AuctionShareSetting.DELIMITER, mWeight, AuctionShareSetting.DELIMITER, mKpn,
				AuctionShareSetting.DELIMITER, mCavingNum, AuctionShareSetting.DELIMITER, mMother,
				AuctionShareSetting.DELIMITER, mNote, AuctionShareSetting.DELIMITER, mAuctDateTime,
				AuctionShareSetting.DELIMITER, mEntryStatus, AuctionShareSetting.DELIMITER, mStartPrice,
				AuctionShareSetting.DELIMITER, mIsLastEntry);
	}

}
