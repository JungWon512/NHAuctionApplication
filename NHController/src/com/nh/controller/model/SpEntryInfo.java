package com.nh.controller.model;

import com.nh.share.code.GlobalDefineCode;
import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.setting.AuctionShareSetting;

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
	private StringProperty mMatime ; // 산차
	private StringProperty mPasgQcn; // 계대
	private StringProperty mObjIdNum; // 개체식별번호
	private StringProperty mObjRegNum; // 축산개체종축등록번호
	private StringProperty mObjRegTypeNum; // 등록구분번호
	private StringProperty mRgnName; // 출하생산지역
	private StringProperty mDnaYn; // 친자검사결과여부
	private StringProperty mIsNew; // 신규여부
	private StringProperty mWeight; // 우출하중량
	private StringProperty mInitPrice; // 최초최저낙찰한도금액
	private StringProperty mLowPrice; // 최저낙찰한도금액
	private StringProperty mNote; // 비고내용
	private StringProperty mIsLastEntry; // 마지막 출품 여부
	private StringProperty mAuctionResult; // 낙유찰결과(01:낙찰/02:유찰)
	private StringProperty mAuctionSucBidder; // 낙찰자
	private StringProperty mAuctionBidPrice; // 응찰금액
	private StringProperty mAuctionBidDateTime; // 응찰일시
	

	public SpEntryInfo() {
	}

	public SpEntryInfo(EntryInfo entryInfo) {

		this.mAuctionHouseCode = new SimpleStringProperty(entryInfo.getAuctionHouseCode());
		this.mEntryNum = new SimpleStringProperty(entryInfo.getEntryNum());
		this.mEntryType = new SimpleStringProperty(entryInfo.getEntryType());
		this.mIndNum = new SimpleStringProperty(entryInfo.getIndNum());
		this.mIndMngCd = new SimpleStringProperty(entryInfo.getIndMngCd());
		this.mFhsNum = new SimpleStringProperty(entryInfo.getFhsNum());
		this.mFarmMngNum = new SimpleStringProperty(entryInfo.getFarmMngNum());
		this.mExhibitor = new SimpleStringProperty(entryInfo.getExhibitor());
		this.mBrandName = new SimpleStringProperty(entryInfo.getBrandName());
		this.mBirthday = new SimpleStringProperty(entryInfo.getBirthday());
		this.mKpn = new SimpleStringProperty(entryInfo.getKpn());
		this.mGender = new SimpleStringProperty(entryInfo.getGender());
		this.mMotherTypeCode = new SimpleStringProperty(entryInfo.getMotherTypeCode());
		this.mMotherObjNum = new SimpleStringProperty(entryInfo.getMotherObjNum());
		this.mMatime = new SimpleStringProperty(entryInfo.getMaTime());
		this.mPasgQcn = new SimpleStringProperty(entryInfo.getPasgQcn());
		this.mObjIdNum = new SimpleStringProperty(entryInfo.getObjIdNum());
		this.mObjRegNum = new SimpleStringProperty(entryInfo.getObjRegNum());
		this.mObjRegTypeNum = new SimpleStringProperty(entryInfo.getObjRegTypeNum());
		this.mIsNew = new SimpleStringProperty(entryInfo.getIsNew());
		this.mWeight = new SimpleStringProperty(Integer.toString((int)Double.parseDouble(entryInfo.getWeight())));
		this.mInitPrice = new SimpleStringProperty(Integer.toString((int)Double.parseDouble(entryInfo.getInitPrice())));
		this.mLowPrice = new SimpleStringProperty(Integer.toString((int)Double.parseDouble(entryInfo.getLowPrice())));
		this.mNote = new SimpleStringProperty(entryInfo.getNote());
		this.mRgnName = new SimpleStringProperty(entryInfo.getRgnName());
		this.mDnaYn = new SimpleStringProperty(entryInfo.getDnaYn());
		this.mAuctionSucBidder = new SimpleStringProperty(entryInfo.getAuctionSucBidder());
		this.mAuctionBidPrice = new SimpleStringProperty(entryInfo.getAuctionBidPrice());
		this.mAuctionBidDateTime = new SimpleStringProperty(entryInfo.getAuctionBidDateTime());
		this.mAuctionResult = new SimpleStringProperty(GlobalDefineCode.AUCTION_RESULT_CODE_READY);
		this.mIsLastEntry = new SimpleStringProperty(entryInfo.getIsLastEntry());
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

	public StringProperty getMatime() {
		return mMatime;
	}

	public void setMatime(StringProperty mMatime) {
		this.mMatime = mMatime;
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
	
	public int getLowPriceInt() {
		
		int lowPrice = 0;
		
		if(getLowPrice() != null) {
			lowPrice =  Integer.parseInt(getLowPrice().getValue());
		}
		
		return lowPrice;
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

	public StringProperty getRgnName() {
		return mRgnName;
	}

	public void setRgnName(StringProperty mRgnName) {
		this.mRgnName = mRgnName;
	}

	public StringProperty getDnaYn() {
		return mDnaYn;
	}

	public void setDnaYn(StringProperty mDnaYn) {
		this.mDnaYn = mDnaYn;
	}

	public StringProperty getAuctionResult() {
		return mAuctionResult;
	}

	public void setAuctionResult(StringProperty mAuctionResult) {
		this.mAuctionResult = mAuctionResult;
	}

	public StringProperty getAuctionSucBidder() {
		return mAuctionSucBidder;
	}

	public void setAuctionSucBidder(StringProperty mAuctionSucBidder) {
		this.mAuctionSucBidder = mAuctionSucBidder;
	}

	public StringProperty getAuctionBidPrice() {
		return mAuctionBidPrice;
	}

	public void setAuctionBidPrice(StringProperty mAuctionBidPrice) {
		this.mAuctionBidPrice = mAuctionBidPrice;
	}

	public StringProperty getAuctionBidDateTime() {
		return mAuctionBidDateTime;
	}

	public void setAuctionBidDateTime(StringProperty mAuctionBidDateTime) {
		this.mAuctionBidDateTime = mAuctionBidDateTime;
	}

	public StringProperty getBiddingResult() {
	
		SimpleStringProperty resultStr = new SimpleStringProperty();

		if(mAuctionResult != null) {

			String code = mAuctionResult.getValue();
			
			if(code.equals(GlobalDefineCode.AUCTION_RESULT_CODE_READY)) {
				resultStr.setValue("대기");
			}else 	if(code.equals(GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS)) {
				resultStr.setValue("낙찰");
			}else 	if(code.equals(GlobalDefineCode.AUCTION_RESULT_CODE_FAIL)) {
				resultStr.setValue("유찰");
			}else 	if(code.equals(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING)) {
				resultStr.setValue("보류");
			}
		}

		return resultStr;
	}
	
	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s%c"
				+ "%s", 
				ORIGIN, TYPE,AuctionShareSetting.DELIMITER,
				getAuctionHouseCode().getValue(), AuctionShareSetting.DELIMITER,
				getEntryNum().getValue(),AuctionShareSetting.DELIMITER, 
				getEntryType().getValue(), AuctionShareSetting.DELIMITER,
				getIndNum().getValue(),AuctionShareSetting.DELIMITER,
				getIndMngCd().getValue(), AuctionShareSetting.DELIMITER, 
				getFhsNum().getValue(),AuctionShareSetting.DELIMITER,
				getFarmMngNum().getValue(), AuctionShareSetting.DELIMITER,
				getExhibitor().getValue(), AuctionShareSetting.DELIMITER,
				getBrandName().getValue(),AuctionShareSetting.DELIMITER,
				getBirthday().getValue(), AuctionShareSetting.DELIMITER, 
				getKpn().getValue(),AuctionShareSetting.DELIMITER, 
				getGender().getValue(), AuctionShareSetting.DELIMITER,
				getMotherTypeCode().getValue(), AuctionShareSetting.DELIMITER,
				getMotherObjNum().getValue(),AuctionShareSetting.DELIMITER,
				getMatime().getValue(), AuctionShareSetting.DELIMITER,
				getPasgQcn().getValue(), AuctionShareSetting.DELIMITER,
				getObjIdNum().getValue(), AuctionShareSetting.DELIMITER,
				getObjRegNum().getValue(), AuctionShareSetting.DELIMITER, 
				getObjRegTypeNum().getValue(),AuctionShareSetting.DELIMITER,
				getRgnName().getValue(),AuctionShareSetting.DELIMITER,
				getDnaYn().getValue(),AuctionShareSetting.DELIMITER,
				getIsNew().getValue(), AuctionShareSetting.DELIMITER,
				getWeight().getValue(),AuctionShareSetting.DELIMITER, 
				getInitPrice().getValue(), AuctionShareSetting.DELIMITER,
				getLowPrice().getValue(), AuctionShareSetting.DELIMITER, 
				getNote().getValue(), AuctionShareSetting.DELIMITER,
				getAuctionResult().getValue(), AuctionShareSetting.DELIMITER,
				getAuctionSucBidder().getValue(), AuctionShareSetting.DELIMITER,
				getAuctionBidDateTime().getValue(), AuctionShareSetting.DELIMITER,
				getNote().getValue(), AuctionShareSetting.DELIMITER,
				getIsLastEntry().getValue());
	}

}
