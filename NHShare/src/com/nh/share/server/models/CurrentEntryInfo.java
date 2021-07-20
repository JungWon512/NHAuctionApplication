package com.nh.share.server.models;

import com.nh.share.controller.models.EntryInfo;
import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 현재 출품 정보 전송
 * 
 * 경매서버 -> 공통
 * 
 * SC| 조합구분코드 | 출품번호 | 경매대상구분코드 | 축산개체관리번호 | 축산축종구분코드 | 농가식별번호 | 농장관리번호 | 농가명 |
 * 브랜드명 | 생년월일 | KPN번호 | 개체성별코드 | 어미소구분코드 | 어미소축산개체관리번호 | 산차 | 계대 | 계체식별번호 |
 * 축산개체종축등록번호 | 등록구분번호 | 신규여부 | 우출하중량 | 최초최저낙찰한도금액 | 최저낙찰한도금액 | 비고내용 | 마지막출품여부
 *
 */
public class CurrentEntryInfo implements FromAuctionServer {
	public static final char TYPE = 'C';
	private String mAuctionHouseCode; // 조합구분코드
	private String mEntryNum; // 출품 번호
	private String mEntryType; // 경매대상구분코드(1 : 송아지 / 2 : 비육우 / 3 : 번식우)
	private String mIndNum; // 축산개체관리번호
	private String mIndMngCd; // 축산축종구분코드
	private String mFhsNum; // 농가식별번호
	private String mFarmMngNum; // 농장관리번호
	private String mExhibitor; // 농가명
	private String mBrandName; // 브랜드명
	private String mBirthday; // 생년월일
	private String mKpn; // KPN
	private String mGender; // 개체성별코드
	private String mMotherTypeCode; // 어미구분코드
	private String mMotherObjNum; // 어미축산개체관리번호
	private String mMatime; // 산차
	private String mPasgQcn; // 계대
	private String mObjIdNum; // 개체식별번호
	private String mObjRegNum; // 축산개체종축등록번호
	private String mObjRegTypeNum; // 등록구분번호
	private String mIsNew; // 신규여부
	private String mWeight; // 우출하중량
	private String mInitPrice; // 최초최저낙찰한도금액
	private String mLowPrice; // 최저낙찰한도금액
	private String mNote; // 비고내용
	private String mIsLastEntry; // 마지막 출품 여부

	public CurrentEntryInfo(String auctionHouseCode, String entryNum, String entryType, String indNum, String indMngCd,
			String fhsNum, String farmMngNum, String exhibitor, String brandName, String birthday, String kpn,
			String gender, String motherTypeCode, String motherObjNum, String matime, String pasgQcn,
			String objIdNum, String objRegNum, String objRegTypeNum, String isNew, String weight, String initPrice,
			String lowPrice, String note, String isLastEntry) {
		mAuctionHouseCode = auctionHouseCode;
		mEntryNum = entryNum;
		mEntryType = entryType;
		mIndNum = indNum;
		mIndMngCd = indMngCd;
		mFhsNum = fhsNum;
		mFarmMngNum = farmMngNum;
		mExhibitor = exhibitor;
		mBrandName = brandName;
		mBirthday = birthday;
		mKpn = kpn;
		mGender = gender;
		mMotherTypeCode = motherTypeCode;
		mMotherObjNum = motherObjNum;
		mMatime = matime;
		mPasgQcn = pasgQcn;
		mObjIdNum = objIdNum;
		mObjRegNum = objRegNum;
		mObjRegTypeNum = objRegTypeNum;
		mIsNew = isNew;
		mWeight = weight;
		mInitPrice = initPrice;
		mLowPrice = lowPrice;
		mNote = note;
		mIsLastEntry = isLastEntry;
	}

	public CurrentEntryInfo(String[] messages) {
		mAuctionHouseCode = messages[1];
		mEntryNum = messages[2];
		mEntryType = messages[3];
		mIndNum = messages[4];
		mIndMngCd = messages[5];
		mFhsNum = messages[6];
		mFarmMngNum = messages[7];
		mExhibitor = messages[8];
		mBrandName = messages[9];
		mBirthday = messages[10];
		mKpn = messages[11];
		mGender = messages[12];
		mMotherTypeCode = messages[13];
		mMotherObjNum = messages[14];
		mMatime = messages[15];
		mPasgQcn = messages[16];
		mObjIdNum = messages[17];
		mObjRegNum = messages[18];
		mObjRegTypeNum = messages[19];
		mIsNew = messages[20];
		mWeight = messages[21];
		mInitPrice = messages[22];
		mLowPrice = messages[23];
		mNote = messages[24];
		mIsLastEntry = messages[25];
	}

	public CurrentEntryInfo(EntryInfo entryInfo) {
		mAuctionHouseCode = entryInfo.getAuctionHouseCode();
		mEntryNum = entryInfo.getEntryNum();
		mEntryType = entryInfo.getEntryType();
		mIndNum = entryInfo.getIndNum();
		mIndMngCd = entryInfo.getIndMngCd();
		mFhsNum = entryInfo.getFhsNum();
		mFarmMngNum = entryInfo.getFarmMngNum();
		mExhibitor = entryInfo.getExhibitor();
		mBrandName = entryInfo.getBrandName();
		mBirthday = entryInfo.getBirthday();
		mKpn = entryInfo.getKpn();
		mGender = entryInfo.getGender();
		mMotherTypeCode = entryInfo.getMotherTypeCode();
		mMotherObjNum = entryInfo.getMotherObjNum();
		mMatime = entryInfo.getMatime();
		mPasgQcn = entryInfo.getPasgQcn();
		mObjIdNum = entryInfo.getObjIdNum();
		mObjRegNum = entryInfo.getObjRegNum();
		mObjRegTypeNum = entryInfo.getObjRegTypeNum();
		mIsNew = entryInfo.getIsNew();
		mWeight = entryInfo.getWeight();
		mInitPrice = entryInfo.getInitPrice();
		mLowPrice = entryInfo.getLowPrice();
		mNote = entryInfo.getNote();
		mIsLastEntry = entryInfo.getIsLastEntry();
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

	public String getEntryType() {
		return mEntryType;
	}

	public void setEntryType(String mEntryType) {
		this.mEntryType = mEntryType;
	}

	public String getIndNum() {
		return mIndNum;
	}

	public void setIndNum(String mIndNum) {
		this.mIndNum = mIndNum;
	}

	public String getIndMngCd() {
		return mIndMngCd;
	}

	public void setIndMngCd(String mIndMngCd) {
		this.mIndMngCd = mIndMngCd;
	}

	public String getFhsNum() {
		return mFhsNum;
	}

	public void setFhsNum(String mFhsNum) {
		this.mFhsNum = mFhsNum;
	}

	public String getFarmMngNum() {
		return mFarmMngNum;
	}

	public void setFarmMngNum(String mFarmMngNum) {
		this.mFarmMngNum = mFarmMngNum;
	}

	public String getExhibitor() {
		return mExhibitor;
	}

	public void setExhibitor(String mExhibitor) {
		this.mExhibitor = mExhibitor;
	}

	public String getBrandName() {
		return mBrandName;
	}

	public void setBrandName(String mBrandName) {
		this.mBrandName = mBrandName;
	}

	public String getBirthday() {
		return mBirthday;
	}

	public void setBirthday(String mBirthday) {
		this.mBirthday = mBirthday;
	}

	public String getKpn() {
		return mKpn;
	}

	public void setKpn(String mKpn) {
		this.mKpn = mKpn;
	}

	public String getGender() {
		return mGender;
	}

	public void setGender(String mGender) {
		this.mGender = mGender;
	}

	public String getMotherTypeCode() {
		return mMotherTypeCode;
	}

	public void setMotherTypeCode(String mMotherTypeCode) {
		this.mMotherTypeCode = mMotherTypeCode;
	}

	public String getMotherObjNum() {
		return mMotherObjNum;
	}

	public void setMotherObjNum(String mMotherObjNum) {
		this.mMotherObjNum = mMotherObjNum;
	}

	public String getMatime() {
		return mMatime;
	}

	public void setMatime(String mMatime) {
		this.mMatime = mMatime;
	}

	public String getPasgQcn() {
		return mPasgQcn;
	}

	public void setPasgQcn(String mPasgQcn) {
		this.mPasgQcn = mPasgQcn;
	}

	public String getObjIdNum() {
		return mObjIdNum;
	}

	public void setObjIdNum(String mObjIdNum) {
		this.mObjIdNum = mObjIdNum;
	}

	public String getObjRegNum() {
		return mObjRegNum;
	}

	public void setObjRegNum(String mObjRegNum) {
		this.mObjRegNum = mObjRegNum;
	}

	public String getObjRegTypeNum() {
		return mObjRegTypeNum;
	}

	public void setObjRegTypeNum(String mObjRegTypeNum) {
		this.mObjRegTypeNum = mObjRegTypeNum;
	}

	public String getIsNew() {
		return mIsNew;
	}

	public void setIsNew(String mIsNew) {
		this.mIsNew = mIsNew;
	}

	public String getWeight() {
		return mWeight;
	}

	public void setWeight(String mWeight) {
		this.mWeight = mWeight;
	}

	public String getInitPrice() {
		return mInitPrice;
	}

	public void setInitPrice(String mInitPrice) {
		this.mInitPrice = mInitPrice;
	}

	public String getLowPrice() {
		return mLowPrice;
	}

	public void setLowPrice(String mLowPrice) {
		this.mLowPrice = mLowPrice;
	}

	public String getNote() {
		return mNote;
	}

	public void setNote(String mNote) {
		this.mNote = mNote;
	}

	public String getIsLastEntry() {
		return mIsLastEntry;
	}

	public void setIsLastEntry(String mIsLastEntry) {
		this.mIsLastEntry = mIsLastEntry;
	}

	@Override
	public String getEncodedMessage() {
		return String.format(
				"%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s",
				ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode, AuctionShareSetting.DELIMITER,
				mEntryNum, AuctionShareSetting.DELIMITER, mEntryType, AuctionShareSetting.DELIMITER, mIndNum,
				AuctionShareSetting.DELIMITER, mIndMngCd, AuctionShareSetting.DELIMITER, mFhsNum,
				AuctionShareSetting.DELIMITER, mFarmMngNum, AuctionShareSetting.DELIMITER, mExhibitor,
				AuctionShareSetting.DELIMITER, mBrandName, AuctionShareSetting.DELIMITER, mBirthday,
				AuctionShareSetting.DELIMITER, mKpn, AuctionShareSetting.DELIMITER, mGender,
				AuctionShareSetting.DELIMITER, mMotherTypeCode, AuctionShareSetting.DELIMITER, mMotherObjNum,
				AuctionShareSetting.DELIMITER, mMatime, AuctionShareSetting.DELIMITER, mPasgQcn,
				AuctionShareSetting.DELIMITER, mObjIdNum, AuctionShareSetting.DELIMITER, mObjRegNum,
				AuctionShareSetting.DELIMITER, mObjRegTypeNum, AuctionShareSetting.DELIMITER, mIsNew,
				AuctionShareSetting.DELIMITER, mWeight, AuctionShareSetting.DELIMITER, mInitPrice,
				AuctionShareSetting.DELIMITER, mLowPrice, AuctionShareSetting.DELIMITER, mNote,
				AuctionShareSetting.DELIMITER, mIsLastEntry);
	}

}
