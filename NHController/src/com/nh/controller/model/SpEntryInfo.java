package com.nh.controller.model;

import java.io.Serializable;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 현재 출품 정보 전송
 * 
 * 경매서버 -> 공통
 * 
 * SC | 조합구분코드 | 출품번호 | 경매대상구분코드 | 축산개체관리번호 | 축산축종구분코드 | 농가식별번호 | 농장관리번호 | 농가명 |
 * 브랜드명 | 생년월일 | KPN번호 | 개체성별코드 | 어미소구분코드 | 어미소축산개체관리번호 | 산차 | 계대 | 계체식별번호 |
 * 축산개체종축등록번호 | 등록구분번호 | 신규여부 | 우출하중량 | 최초최저낙찰한도금액 | 최저낙찰한도금액 | 비고내용 | 마지막출품여부
 *
 */
public class SpEntryInfo implements FromAuctionController {

	public static final char TYPE = 'I';

	private StringProperty mAuctionHouseCode; // 조합구분코드
	private StringProperty mEntryNum; // 출품 번호
	private StringProperty mEntryType; // 경매대상구분코드(1 : 송아지 / 2 : 비육우 / 3 : 번식우)
	private StringProperty mIndNum; // 축산개체관리번호
	private StringProperty mIndMngCd; // 축산축종구분코드
	private StringProperty mFhsNum; // 농가식별번호
	private StringProperty mFarmMngNum; // 농장관리번호
	private StringProperty mExhibitor; // 농가명
	private StringProperty mBrandName; // 브랜드명
	private StringProperty mBirthday; // 생년월일
	private StringProperty mKpn; // KPN
	private StringProperty mGender; // 개체성별코드
	private StringProperty mMotherTypeCode; // 어미구분코드
	private StringProperty mMotherObjNum; // 어미축산개체관리번호
	private StringProperty mCavingNum; // 산차
	private StringProperty mPasgQcn; // 계대
	private StringProperty mObjIdNum; // 개체식별번호
	private StringProperty mObjRegNum; // 축산개체종축등록번호
	private StringProperty mObjRegTypeNum; // 등록구분번호
	private StringProperty mIsNew; // 신규여부
	private StringProperty mWeight; // 우출하중량
	private StringProperty mInitPrice; // 최초최저낙찰한도금액
	private StringProperty mLowPrice; // 최저낙찰한도금액
	private StringProperty mNote; // 비고내용
	private StringProperty mIsLastEntry; // 마지막 출품 여부

	private StringProperty successfulBidder; // 낙찰자
	private StringProperty BiddingResult; // 낙찰결과
	private BooleanProperty isPending; // 보류

	public SpEntryInfo() {
	}

	public SpEntryInfo(String auctionHouseCode, String entryNum, String entryType, String indNum, String indMngCd,
			String fhsNum, String farmMngNum, String exhibitor, String brandName, String birthday, String kpn,
			String gender, String motherTypeCode, String motherObjNum, String cavingNum, String pasgQcn,
			String objIdNum, String objRegNum, String objRegTypeNum, String isNew, String weight, String initPrice,
			String lowPrice, String note, String isLastEntry) {
		this.mAuctionHouseCode = new SimpleStringProperty(auctionHouseCode);
		this.mEntryNum = new SimpleStringProperty(entryNum);
		this.mEntryType = new SimpleStringProperty(entryType);
		this.mIndNum = new SimpleStringProperty(indNum);
		this.mIndMngCd = new SimpleStringProperty(indMngCd);
		this.mFhsNum = new SimpleStringProperty(fhsNum);
		this.mFarmMngNum = new SimpleStringProperty(farmMngNum);
		this.mExhibitor = new SimpleStringProperty(exhibitor);
		this.mBrandName = new SimpleStringProperty(brandName);
		this.mBirthday = new SimpleStringProperty(birthday);
		this.mKpn = new SimpleStringProperty(kpn);
		this.mGender = new SimpleStringProperty(gender);
		this.mMotherTypeCode = new SimpleStringProperty(motherTypeCode);
		this.mMotherObjNum = new SimpleStringProperty(motherObjNum);
		this.mCavingNum = new SimpleStringProperty(cavingNum);
		this.mPasgQcn = new SimpleStringProperty(pasgQcn);
		this.mObjIdNum = new SimpleStringProperty(objIdNum);
		this.mObjRegNum = new SimpleStringProperty(objRegNum);
		this.mObjRegTypeNum = new SimpleStringProperty(objRegTypeNum);
		this.mIsNew = new SimpleStringProperty(isNew);
		this.mWeight = new SimpleStringProperty(Integer.toString((int)Double.parseDouble(weight)));
		this.mInitPrice = new SimpleStringProperty(Integer.toString((int)Double.parseDouble(initPrice)));
		this.mLowPrice = new SimpleStringProperty(Integer.toString((int)Double.parseDouble(lowPrice)));
		this.mNote = new SimpleStringProperty(note);
		this.mIsLastEntry = new SimpleStringProperty(isLastEntry);
	}

	public StringProperty getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(StringProperty mAuctionHouseCode) {
		this.mAuctionHouseCode = mAuctionHouseCode;
	}

	public StringProperty getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(StringProperty mEntryNum) {
		this.mEntryNum = mEntryNum;
	}

	public StringProperty getEntryType() {
		return mEntryType;
	}

	public void setEntryType(StringProperty mEntryType) {
		this.mEntryType = mEntryType;
	}

	public StringProperty getIndNum() {
		return mIndNum;
	}

	public void setIndNum(StringProperty mIndNum) {
		this.mIndNum = mIndNum;
	}

	public StringProperty getIndMngCd() {
		return mIndMngCd;
	}

	public void setIndMngCd(StringProperty mIndMngCd) {
		this.mIndMngCd = mIndMngCd;
	}

	public StringProperty getFhsNum() {
		return mFhsNum;
	}

	public void setFhsNum(StringProperty mFhsNum) {
		this.mFhsNum = mFhsNum;
	}

	public StringProperty getFarmMngNum() {
		return mFarmMngNum;
	}

	public void setFarmMngNum(StringProperty mFarmMngNum) {
		this.mFarmMngNum = mFarmMngNum;
	}

	public StringProperty getExhibitor() {
		return mExhibitor;
	}

	public void setExhibitor(StringProperty mExhibitor) {
		this.mExhibitor = mExhibitor;
	}

	public StringProperty getBrandName() {
		return mBrandName;
	}

	public void setBrandName(StringProperty mBrandName) {
		this.mBrandName = mBrandName;
	}

	public StringProperty getBirthday() {
		return mBirthday;
	}

	public void setBirthday(StringProperty mBirthday) {
		this.mBirthday = mBirthday;
	}

	public StringProperty getKpn() {
		return mKpn;
	}

	public void setKpn(StringProperty mKpn) {
		this.mKpn = mKpn;
	}

	public StringProperty getGender() {
		return mGender;
	}

	public void setGender(StringProperty mGender) {
		this.mGender = mGender;
	}

	public StringProperty getMotherTypeCode() {
		return mMotherTypeCode;
	}

	public void setMotherTypeCode(StringProperty mMotherTypeCode) {
		this.mMotherTypeCode = mMotherTypeCode;
	}

	public StringProperty getMotherObjNum() {
		return mMotherObjNum;
	}

	public void setMotherObjNum(StringProperty mMotherObjNum) {
		this.mMotherObjNum = mMotherObjNum;
	}

	public StringProperty getCavingNum() {
		return mCavingNum;
	}

	public void setCavingNum(StringProperty mCavingNum) {
		this.mCavingNum = mCavingNum;
	}

	public StringProperty getPasgQcn() {
		return mPasgQcn;
	}

	public void setPasgQcn(StringProperty mPasgQcn) {
		this.mPasgQcn = mPasgQcn;
	}

	public StringProperty getObjIdNum() {
		return mObjIdNum;
	}

	public void setObjIdNum(StringProperty mObjIdNum) {
		this.mObjIdNum = mObjIdNum;
	}

	public StringProperty getObjRegNum() {
		return mObjRegNum;
	}

	public void setObjRegNum(StringProperty mObjRegNum) {
		this.mObjRegNum = mObjRegNum;
	}

	public StringProperty getObjRegTypeNum() {
		return mObjRegTypeNum;
	}

	public void setObjRegTypeNum(StringProperty mObjRegTypeNum) {
		this.mObjRegTypeNum = mObjRegTypeNum;
	}

	public StringProperty getIsNew() {
		return mIsNew;
	}

	public void setIsNew(StringProperty mIsNew) {
		this.mIsNew = mIsNew;
	}

	public StringProperty getWeight() {
		return mWeight;
	}

	public void setWeight(StringProperty mWeight) {
		this.mWeight = mWeight;
	}

	public StringProperty getInitPrice() {
		return mInitPrice;
	}

	public void setInitPrice(StringProperty mInitPrice) {
		this.mInitPrice = mInitPrice;
	}

	public StringProperty getLowPrice() {
		return mLowPrice;
	}

	public void setLowPrice(StringProperty mLowPrice) {
		this.mLowPrice = mLowPrice;
	}

	public StringProperty getNote() {
		return mNote;
	}

	public void setNote(StringProperty mNote) {
		this.mNote = mNote;
	}

	public StringProperty getIsLastEntry() {
		return mIsLastEntry;
	}

	public void setIsLastEntry(StringProperty mIsLastEntry) {
		this.mIsLastEntry = mIsLastEntry;
	}

	public StringProperty getSuccessfulBidder() {
		return successfulBidder;
	}

	public void setSuccessfulBidder(StringProperty successfulBidder) {
		this.successfulBidder = successfulBidder;
	}

	public StringProperty getBiddingResult() {
		return BiddingResult;
	}

	public void setBiddingResult(StringProperty biddingResult) {
		BiddingResult = biddingResult;
	}

	public BooleanProperty getIsPending() {
		return isPending;
	}

	public void setIsPending(BooleanProperty isPending) {
		this.isPending = isPending;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE,
				AuctionShareSetting.DELIMITER, mAuctionHouseCode.getValue(), AuctionShareSetting.DELIMITER, mEntryNum.getValue(),
				AuctionShareSetting.DELIMITER, mEntryType.getValue(), AuctionShareSetting.DELIMITER, mIndNum.getValue(),
				AuctionShareSetting.DELIMITER, mIndMngCd.getValue(), AuctionShareSetting.DELIMITER, mFhsNum.getValue(),
				AuctionShareSetting.DELIMITER, mFarmMngNum.getValue(), AuctionShareSetting.DELIMITER,
				mExhibitor.getValue(), AuctionShareSetting.DELIMITER, mBrandName.getValue(),
				AuctionShareSetting.DELIMITER, mBirthday.getValue(), AuctionShareSetting.DELIMITER, mKpn.getValue(),
				AuctionShareSetting.DELIMITER, mGender.getValue(), AuctionShareSetting.DELIMITER,
				mMotherTypeCode.getValue(), AuctionShareSetting.DELIMITER, mMotherObjNum.getValue(),
				AuctionShareSetting.DELIMITER, mCavingNum.getValue(), AuctionShareSetting.DELIMITER,
				mPasgQcn.getValue(), AuctionShareSetting.DELIMITER, mObjIdNum.getValue(), AuctionShareSetting.DELIMITER,
				mObjRegNum.getValue(), AuctionShareSetting.DELIMITER, mObjRegTypeNum.getValue(),
				AuctionShareSetting.DELIMITER, mIsNew.getValue(), AuctionShareSetting.DELIMITER, mWeight.getValue(),
				AuctionShareSetting.DELIMITER, mInitPrice.getValue(), AuctionShareSetting.DELIMITER,
				mLowPrice.getValue(), AuctionShareSetting.DELIMITER, mNote.getValue(), AuctionShareSetting.DELIMITER,
				mIsLastEntry.getValue());
	}

}
