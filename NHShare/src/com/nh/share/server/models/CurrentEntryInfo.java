package com.nh.share.server.models;

import com.nh.share.controller.models.EntryInfo;
import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 현재 출품 정보 전송
 * 
 * 경매서버 -> 공통
 * 
 * SC | 조합구분코드 | 출품번호 | 경매회차 | 경매대상구분코드 | 축산개체관리번호 | 축산축종구분코드 | 농가식별번호 | 농장관리번호 | 농가명 |
 * 브랜드명 | 생년월일 | KPN번호 | 개체성별코드 | 어미소구분코드 | 어미소축산개체관리번호 | 산차 | 임신개월수 | 계대 |
 * 계체식별번호 | 축산개체종축등록번호 | 등록구분번호 | 출하생산지역 | 친자검사결과여부 | 신규여부 | 우출하중량 | 최초최저낙찰한도금액
 * | 최저낙찰한도금액 | 비고내용 | 낙유찰결과 | 낙찰자 | 낙찰금액 | 응찰일시 | 마지막출품여부 | 계류대번호 | 초과출장우여부 | 일괄경매구간번호
 *
 */
public class CurrentEntryInfo implements FromAuctionServer {
	public static final char TYPE = 'C';
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
	private String mMotherTypeCode; // 어미구분코드
	private String mMotherObjNum; // 어미축산개체관리번호
	private String mMaTime; // 산차
	private String mMaMonth; // 임신개월수
	private String mPasgQcn; // 계대
	private String mObjIdNum; // 개체식별번호
	private String mObjRegNum; // 축산개체종축등록번호
	private String mObjRegTypeNum; // 등록구분번호
	private String mRgnName; // 출하생산지역
	private String mDnaYn; // 친자검사결과여부
	private String mIsNew; // 신규여부
	private String mWeight; // 우출하중량
	private String mInitPrice; // 최초최저낙찰한도금액
	private String mLowPrice; // 최저낙찰한도금액
	private String mSraSbidUpPrice; // 축산낙찰단가
	private String mNote; // 비고내용
	private String mAuctionResult; // 낙유찰결과(22:낙찰/23:유찰)
	private String mAuctionSucBidder; // 낙찰자
	private String mAuctionBidPrice; // 응찰금액
	private String mAuctionBidDateTime; // 응찰일시
	private String mIsLastEntry; // 마지막 출품 여부
	private String mStandPosition; // 계류대 번호
	private String mIsExcessCow; // 초과출장우여부
	private String mExpAuctionIntNum; // 일괄 경매 구간 번호
	private String mAuctionTypeCode;	//경매 회차 유형 코드 (0:일괄,1:송아지,2:비육우.3:번식우)
	private String mPriceUnit;	
	private String mSraMwmnName;	
	private String mGapMonth;	
	private String mRgDscName;	
	
	
	
	public CurrentEntryInfo(String auctionHouseCode, String entryNum, String auctionQcn, String entryType, String indNum, String indMngCd,
			String fhsNum, String farmMngNum, String exhibitor, String brandName, String birthday, String kpn,
			String gender, String motherTypeCode, String motherObjNum, String maTime, String maMonth, String pasgQcn,
			String objIdNum, String objRegNum, String objRegTypeNum, String rgnName, String dnaYn, String isNew,
			String weight, String initPrice, String lowPrice, String note, String auctionResult,
			String auctionSucBidder, String auctionBidPrice, String auctionBidDateTime, String isLastEntry, String standPosition, String isExcessCow, String expAuctionIntNum) {
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
		mMaMonth = maMonth;
		mPasgQcn = pasgQcn;
		mObjIdNum = objIdNum;
		mObjRegNum = objRegNum;
		mObjRegTypeNum = objRegTypeNum;
		mRgnName = rgnName;
		mDnaYn = dnaYn;
		mIsNew = isNew;
		mWeight = weight;
		mInitPrice = initPrice;
		mLowPrice = lowPrice;
		mNote = note;
		mAuctionResult = auctionResult;
		mAuctionSucBidder = auctionSucBidder;
		mAuctionBidPrice = auctionBidPrice;
		mAuctionBidDateTime = auctionBidDateTime;
		mIsLastEntry = isLastEntry;
		mStandPosition = standPosition;
		mIsExcessCow = isExcessCow;
		mExpAuctionIntNum = expAuctionIntNum;
	}

	public CurrentEntryInfo(String[] messages) {
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
		mMaMonth = messages[17];
		mPasgQcn = messages[18];
		mObjIdNum = messages[19];
		mObjRegNum = messages[20];
		mObjRegTypeNum = messages[21];
		mRgnName = messages[22];
		mDnaYn = messages[23];
		mIsNew = messages[24];
		mWeight = messages[25];
		mInitPrice = messages[26];
		mLowPrice = messages[27];
		mNote = messages[28];
		mAuctionResult = messages[29];
		mAuctionSucBidder = messages[30];
		mAuctionBidPrice = messages[31];
		mAuctionBidDateTime = messages[32];
		mIsLastEntry = messages[33];
		mStandPosition = messages[34];
		mIsExcessCow = messages[35];
		mExpAuctionIntNum = messages[36];
		mAuctionTypeCode = messages[37];
		if(messages.length > 38) {
			mSraMwmnName = messages[38];
			mGapMonth = messages[39];
			mRgDscName = messages[40];
			mPriceUnit = messages[41];
		}
	}

	public CurrentEntryInfo(EntryInfo entryInfo) {
		mAuctionHouseCode = entryInfo.getAuctionHouseCode();
		mEntryNum = entryInfo.getEntryNum();
		mAuctionQcn = entryInfo.getAuctionQcn();
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
		mMaTime = entryInfo.getMaTime();
		mMaMonth = Integer.toString(entryInfo.getMaMonth());
		mPasgQcn = entryInfo.getPasgQcn();
		mObjIdNum = entryInfo.getObjIdNum();
		mObjRegNum = entryInfo.getObjRegNum();
		mObjRegTypeNum = entryInfo.getObjRegTypeNum();
		mRgnName = entryInfo.getRgnName();
		mDnaYn = entryInfo.getDnaYn();
		mIsNew = entryInfo.getIsNew();
		mWeight = entryInfo.getWeight();
		mInitPrice =  Integer.toString(entryInfo.getInitPrice());
		mLowPrice = Integer.toString(entryInfo.getLowPrice());
		mNote = entryInfo.getNote();
		mAuctionResult = entryInfo.getAuctionResult();
		mAuctionSucBidder = entryInfo.getAuctionSucBidder();
		mAuctionBidPrice = Integer.toString(entryInfo.getAuctionBidPrice());
		mAuctionBidDateTime = entryInfo.getAuctionBidDateTime();
		mIsLastEntry = entryInfo.getIsLastEntry();
		mStandPosition = entryInfo.getStandPosition();
		mIsExcessCow = entryInfo.getIsExcessCow();
		mSraSbidUpPrice = Integer.toString(entryInfo.getSraSbidUpPrice());
		mExpAuctionIntNum = entryInfo.getExpAuctionIntNum();
		mAuctionTypeCode = entryInfo.getAuctionTypeCode();
		mPriceUnit = entryInfo.getmPriceUnit();
		mSraMwmnName = entryInfo.getSraMwmnName();
		mGapMonth = entryInfo.getGapMonth();
		mRgDscName = entryInfo.getRgDscName();
		mPriceUnit = entryInfo.getmPriceUnit();
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
		return mMaTime;
	}

	public void setMatime(String mMatime) {
		this.mMaTime = mMatime;
	}
	
	public String getMaMonth() {
		return mMaMonth;
	}

	public void setMaMonth(String mMaMonth) {
		this.mMaMonth = mMaMonth;
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

	public String getAuctionBidPrice() {
		return mAuctionBidPrice;
	}

	public void setAuctionBidPrice(String auctionBidPrice) {
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

	public String getSraSbidUpPrice() {
		return mSraSbidUpPrice;
	}

	public void setSraSbidUpPrice(String mSraSbidUpPrice) {
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
	
	public String getExpAuctionIntNum() {
		return mExpAuctionIntNum;
	}

	public void setExpAuctionIntNum(String expAuctionIntNum) {
		this.mExpAuctionIntNum = expAuctionIntNum;
	}
	
	public String getmAuctionTypeCode() {
		return mAuctionTypeCode;
	}

	public void setmAuctionTypeCode(String mAuctionTypeCode) {
		this.mAuctionTypeCode = mAuctionTypeCode;
	}
	
	
	public String getmPriceUnit() {
		return mPriceUnit;
	}

	public void setmPriceUnit(String mPriceUnit) {
		this.mPriceUnit = mPriceUnit;
	}

	@Override
	public String getEncodedMessage() {
		return String.format(
				"%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s"
				+"%c%s%c%s%c%s%c%s",
				ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode, AuctionShareSetting.DELIMITER,
				mEntryNum, AuctionShareSetting.DELIMITER, mAuctionQcn, AuctionShareSetting.DELIMITER, mEntryType, AuctionShareSetting.DELIMITER, mIndNum,
				AuctionShareSetting.DELIMITER, mIndMngCd, AuctionShareSetting.DELIMITER, mFhsNum,
				AuctionShareSetting.DELIMITER, mFarmMngNum, AuctionShareSetting.DELIMITER, mExhibitor,
				AuctionShareSetting.DELIMITER, mBrandName, AuctionShareSetting.DELIMITER, mBirthday,
				AuctionShareSetting.DELIMITER, mKpn, AuctionShareSetting.DELIMITER, mGender,
				AuctionShareSetting.DELIMITER, mMotherTypeCode, AuctionShareSetting.DELIMITER, mMotherObjNum,
				AuctionShareSetting.DELIMITER, mMaTime, AuctionShareSetting.DELIMITER, mMaMonth, AuctionShareSetting.DELIMITER, mPasgQcn,
				AuctionShareSetting.DELIMITER, mObjIdNum, AuctionShareSetting.DELIMITER, mObjRegNum,
				AuctionShareSetting.DELIMITER, mObjRegTypeNum, AuctionShareSetting.DELIMITER, mRgnName,
				AuctionShareSetting.DELIMITER, mDnaYn, AuctionShareSetting.DELIMITER, mIsNew,
				AuctionShareSetting.DELIMITER, mWeight, AuctionShareSetting.DELIMITER, mInitPrice,
				AuctionShareSetting.DELIMITER, mLowPrice, AuctionShareSetting.DELIMITER, mNote,
				AuctionShareSetting.DELIMITER, mAuctionResult, AuctionShareSetting.DELIMITER, mAuctionSucBidder,
				AuctionShareSetting.DELIMITER, mAuctionBidPrice, AuctionShareSetting.DELIMITER, mAuctionBidDateTime,
				AuctionShareSetting.DELIMITER, mIsLastEntry, AuctionShareSetting.DELIMITER, mStandPosition,
				AuctionShareSetting.DELIMITER, mIsExcessCow, AuctionShareSetting.DELIMITER, mExpAuctionIntNum,
				AuctionShareSetting.DELIMITER, mAuctionTypeCode
				,AuctionShareSetting.DELIMITER, mSraMwmnName
				,AuctionShareSetting.DELIMITER, mGapMonth
				,AuctionShareSetting.DELIMITER, mRgDscName
				,AuctionShareSetting.DELIMITER, mPriceUnit);
	}

}
