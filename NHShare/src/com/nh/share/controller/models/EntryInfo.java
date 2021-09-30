package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 출품 정보 전송 처리
 * 
 * 제어프로그램 -> 경매서버
 * 
 * CI | 조합구분코드 | 출품번호 | 경매회차 | 경매대상구분코드 | 축산개체관리번호 | 축산축종구분코드 | 농가식별번호 | 농장관리번호
 * | 농가명 | 브랜드명 | 생년월일 | KPN번호 | 개체성별코드 | 어미소구분코드 | 어미소축산개체관리번호 | 산차 | 임신개월수 |
 * 계대 | 계체식별번호 | 축산개체종축등록번호 | 등록구분번호 | 출하생산지역 | 친자검사결과여부 | 신규여부 | 우출하중량 |
 * 최초최저낙찰한도금액 | 최저낙찰한도금액 | 비고내용 | 낙유찰결과 | 낙찰자 | 낙찰금액 | 응찰일시 | 마지막출품여부 | 계류대번호 | 초과출장우여부
 *
 */
public class EntryInfo implements FromAuctionController {
	public static final char TYPE = 'I';

	private String mAuctionHouseCode; // 조합구분코드
	private String mEntryNum; // 출품 번호
	private String mAuctionQcn; // 경매회차
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
	private String mGenderName; // 개체성별코드 명
	private String mMotherTypeCode; // 어미구분코드
	private String mMotherObjNum; // 어미축산개체관리번호
	private String mMotherCowName; // 혈통명
	private String mMaTime; // 산차
	private int mMaMonth; // 임신개월수
	private String mPasgQcn; // 계대
	private String mObjIdNum; // 개체식별번호
	private String mObjRegNum; // 축산개체종축등록번호
	private String mObjRegTypeNum; // 등록구분번호
	private String mRgnName; // 출하생산지역
	private String mReRgnName; // 출하생산지역 split => ex) 경남 하동군 금남면  계천리 => 금남, 경상남도 하동군 악양면 상중대2길 22-3 => 악양
	private String mDnaYn; // 친자검사결과여부
	private String mIsNew; // 신규여부
	private String mWeight; // 우출하중량
	private int mInitPrice; // 최초최저낙찰한도금액
	private int mLowPrice; // 최저낙찰한도금액
	private int mSraSbidUpPrice; // 축산낙찰단가
	private String mNote; // 비고내용
	private String mAucDt; // 경매일
	private String mOslpNo; // 원표 번호
	private String mLedSqno; // 원장 일련번호
	private String mTrmnAmnNo; // 거래인 관리 번호

	private String mAuctionResult; // 낙유찰결과 (11 대기 ,22 낙찰 ,23 보류)
	private String mAuctionSucBidder; // 낙찰자
	private int mAuctionBidPrice; // 응찰금액/낙찰금액
	private String mAuctionBidDateTime; // 응찰일시

	private String mLsChgDtm; // 최종변경일시
	private String mLsCmeNo; // 최종변경자개인번호
	private int mLwprChgNt; // 최저가 변경 횟수

	private String mIsLastEntry; // 마지막 출품 여부

	private String mStandPosition; // 계류대 번호
	private String mIsExcessCow; // 초과출장우여부

	public EntryInfo() {
	}

	public EntryInfo(String auctionHouseCode, String entryNum, String auctionQcn, String entryType, String indNum,
			String indMngCd, String fhsNum, String farmMngNum, String exhibitor, String brandName, String birthday,
			String kpn, String gender, String motherTypeCode, String motherObjNum, String maTime, String maMonth,
			String pasgQcn, String objIdNum, String objRegNum, String objRegTypeNum, String rgnName, String dnaYn,
			String isNew, String weight, String initPrice, String lowPrice, String note, String auctionResult,
			String auctionSucBidder, String auctionBidPrice, String auctionBidDateTime, String isLastEntry,
			String standPosition, String isExcessCow) {
		mAuctionHouseCode = auctionHouseCode;
		mEntryNum = entryNum;
		mAuctionQcn = auctionQcn;
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
		mMaTime = maTime;
		mMaMonth = Integer.parseInt(maMonth);
		mPasgQcn = pasgQcn;
		mObjIdNum = objIdNum;
		mObjRegNum = objRegNum;
		mObjRegTypeNum = objRegTypeNum;
		mRgnName = rgnName;
		mDnaYn = dnaYn;
		mIsNew = isNew;
		mWeight = weight;
		mInitPrice = Integer.parseInt(initPrice);
		mLowPrice = Integer.parseInt(lowPrice);
		mNote = note;
		mAuctionResult = auctionResult;
		mAuctionSucBidder = auctionSucBidder;
		mAuctionBidPrice = Integer.parseInt(auctionBidPrice);
		mAuctionBidDateTime = auctionBidDateTime;
		mIsLastEntry = isLastEntry;
		mStandPosition = standPosition;
		mIsExcessCow = isExcessCow;
	}

	public EntryInfo(String[] messages) {
		mAuctionHouseCode = messages[1];
		mEntryNum = messages[2];
		mAuctionQcn = messages[3];
		mEntryType = messages[4];
		mIndNum = messages[5];
		mIndMngCd = messages[6];
		mFhsNum = messages[7];
		mFarmMngNum = messages[8];
		mExhibitor = messages[9];
		mBrandName = messages[10];
		mBirthday = messages[11];
		mKpn = messages[12];
		mGender = messages[13];
		mMotherTypeCode = messages[14];
		mMotherObjNum = messages[15];
		mMaTime = messages[16];
		mMaMonth = Integer.parseInt(messages[17]);
		mPasgQcn = messages[18];
		mObjIdNum = messages[19];
		mObjRegNum = messages[20];
		mObjRegTypeNum = messages[21];
		mRgnName = messages[22];
		mDnaYn = messages[23];
		mIsNew = messages[24];
		mWeight = messages[25];
		mInitPrice = Integer.parseInt(messages[26]);
		mLowPrice = Integer.parseInt(messages[27]);
		mNote = messages[28];
		mAuctionResult = messages[29];
		mAuctionSucBidder = messages[30];
		mAuctionBidPrice = Integer.parseInt(messages[31]);
		mAuctionBidDateTime = messages[32];
		mIsLastEntry = messages[33];
		mStandPosition = messages[34];
		mIsExcessCow = messages[35];
	}

	@Override
	public boolean equals(Object obj) {
		return ((EntryInfo) obj).mEntryNum.equals(mEntryNum);
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String mAuctionHouseCode) {
		this.mAuctionHouseCode = mAuctionHouseCode;
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

	public String getMaTime() {
		return mMaTime;
	}

	public void setMaTime(String maTime) {
		this.mMaTime = maTime;
	}

	public int getMaMonth() {
		return mMaMonth;
	}

	public void setMatime(int maMonth) {
		this.mMaMonth = maMonth;
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

	public String getRgnName() {
		return mRgnName;
	}

	public void setRgnName(String rgnName) {
		this.mRgnName = rgnName;
	}

	public String getDnaYn() {
		return mDnaYn;
	}

	public void setDnaYn(String dnaYn) {
		this.mDnaYn = dnaYn;
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

	public int getInitPrice() {
		return mInitPrice;
	}

	public void setInitPrice(int mInitPrice) {
		this.mInitPrice = mInitPrice;
	}

	public int getLowPrice() {
		return mLowPrice;
	}

	public void setLowPrice(int mLowPrice) {
		this.mLowPrice = mLowPrice;
	}

	public String getNote() {
		return mNote;
	}

	public void setNote(String mNote) {
		this.mNote = mNote;
	}

	public String getAuctionResult() {
		return mAuctionResult;
	}

	public void setAuctionResult(String auctionResult) {
		this.mAuctionResult = auctionResult;
	}

	public String getAuctionSucBidder() {
		return mAuctionSucBidder;
	}

	public void setAuctionSucBidder(String auctionSucBidder) {
		this.mAuctionSucBidder = auctionSucBidder;
	}

	public int getAuctionBidPrice() {
		return mAuctionBidPrice;
	}

	public void setAuctionBidPrice(int auctionBidPrice) {
		this.mAuctionBidPrice = auctionBidPrice;
	}

	public String getAuctionBidDateTime() {
		return mAuctionBidDateTime;
	}

	public void setAuctionBidDateTime(String auctionBidDateTime) {
		this.mAuctionBidDateTime = auctionBidDateTime;
	}

	public String getIsLastEntry() {
		return mIsLastEntry;
	}

	public void setIsLastEntry(String mIsLastEntry) {
		this.mIsLastEntry = mIsLastEntry;
	}

	public String getAucDt() {
		return mAucDt;
	}

	public void setAucDt(String mAucDt) {
		this.mAucDt = mAucDt;
	}

	public String getLsChgDtm() {
		return mLsChgDtm;
	}

	public void setLsChgDtm(String mLsChgDtm) {
		this.mLsChgDtm = mLsChgDtm;
	}

	public String getLsCmeNo() {
		return mLsCmeNo;
	}

	public void setLsCmeNo(String mLsCmeNo) {
		this.mLsCmeNo = mLsCmeNo;
	}

	public int getLwprChgNt() {
		return mLwprChgNt;
	}

	public void setLwprChgNt(int mLwprChgNt) {
		this.mLwprChgNt = mLwprChgNt;
	}

	public String getOslpNo() {
		return mOslpNo;
	}

	public void setOslpNo(String mOslpNo) {
		this.mOslpNo = mOslpNo;
	}

	public String getTrmnAmnNo() {
		return mTrmnAmnNo;
	}

	public void setTrmnAmnNo(String mTrmnAmnNo) {
		this.mTrmnAmnNo = mTrmnAmnNo;
	}

	public String getLedSqno() {
		return mLedSqno;
	}

	public void setLedSqno(String mLedSqno) {
		this.mLedSqno = mLedSqno;
	}

	public String getGenderName() {
		return mGenderName;
	}

	public void setGenderName(String mGenderName) {
		this.mGenderName = mGenderName;
	}

	public String getMotherCowName() {
		return mMotherCowName;
	}

	public void setMotherCowName(String mMotherCowName) {
		this.mMotherCowName = mMotherCowName;
	}
	public int getSraSbidUpPrice() {
		return mSraSbidUpPrice;
	}
	public void setSraSbidUpPrice(int mSraSbidUpPrice) {
		this.mSraSbidUpPrice = mSraSbidUpPrice;
	}

	public String getStandPosition() {
		return mStandPosition;
	}

	public void setStandPosition(String standPosition) {
		this.mStandPosition = standPosition;
	}

	public String getIsExcessCow() {
		return mIsExcessCow;
	}

	public void setIsExcessCow(String isExcessCow) {
		this.mIsExcessCow = isExcessCow;
	}
	
	public String getReRgnName() {
		return mReRgnName;
	}

	public void setReRgnName(String mReRgnName) {
		this.mReRgnName = mReRgnName;
	}

	@Override
	public String getEncodedMessage() {
		return String.format(
				"%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s",
				ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode, AuctionShareSetting.DELIMITER,
				mEntryNum, AuctionShareSetting.DELIMITER, mAuctionQcn, AuctionShareSetting.DELIMITER, mEntryType,
				AuctionShareSetting.DELIMITER, mIndNum, AuctionShareSetting.DELIMITER, mIndMngCd,
				AuctionShareSetting.DELIMITER, mFhsNum, AuctionShareSetting.DELIMITER, mFarmMngNum,
				AuctionShareSetting.DELIMITER, mExhibitor, AuctionShareSetting.DELIMITER, mBrandName,
				AuctionShareSetting.DELIMITER, mBirthday, AuctionShareSetting.DELIMITER, mKpn,
				AuctionShareSetting.DELIMITER, mGender, AuctionShareSetting.DELIMITER, mMotherTypeCode,
				AuctionShareSetting.DELIMITER, mMotherObjNum, AuctionShareSetting.DELIMITER, mMaTime,
				AuctionShareSetting.DELIMITER, mMaMonth, AuctionShareSetting.DELIMITER, mPasgQcn,
				AuctionShareSetting.DELIMITER, mObjIdNum, AuctionShareSetting.DELIMITER, mObjRegNum,
				AuctionShareSetting.DELIMITER, mObjRegTypeNum, AuctionShareSetting.DELIMITER, mRgnName,
				AuctionShareSetting.DELIMITER, mDnaYn, AuctionShareSetting.DELIMITER, mIsNew,
				AuctionShareSetting.DELIMITER, mWeight, AuctionShareSetting.DELIMITER, mInitPrice,
				AuctionShareSetting.DELIMITER, mLowPrice, AuctionShareSetting.DELIMITER, mNote,
				AuctionShareSetting.DELIMITER, mAuctionResult, AuctionShareSetting.DELIMITER, mAuctionSucBidder,
				AuctionShareSetting.DELIMITER, mAuctionBidPrice, AuctionShareSetting.DELIMITER, mAuctionBidDateTime,
				AuctionShareSetting.DELIMITER, mIsLastEntry, AuctionShareSetting.DELIMITER, mStandPosition,
				AuctionShareSetting.DELIMITER, mIsExcessCow);
	}

}
