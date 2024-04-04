package com.nh.controller.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.api.client.util.StringUtils;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.setting.AuctionShareSetting;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 현재 출품 정보 전송
 * <p>
 * 경매서버 -> 공통
 * <p>
 * SC | 조합구분코드 | 출품번호 | 경매회차| 경매대상구분코드 | 축산개체관리번호 | 축산축종구분코드 | 농가식별번호 | 농장관리번호 |
 * 농가명 | 브랜드명 | 생년월일 | KPN번호 | 개체성별코드 | 어미소구분코드 | 어미소축산개체관리번호 | 산차 | 임신개월수 | 계대
 * | 계체식별번호 | 축산개체종축등록번호 | 등록구분번호 | 신규여부 | 우출하중량 | 최초최저낙찰한도금액 | 최저낙찰한도금액 | 비고내용
 * | 마지막출품여부 | 
 */
public class SpEntryInfo implements FromAuctionController, Cloneable {

	public static final char TYPE = 'I';

	private StringProperty mAuctionHouseCode; // 조합구분코드
	private StringProperty mEntryNum; // 출품 번호 (원표번호)
	private StringProperty mEntryType; // 경매대상구분코드(1 : 송아지 / 2 : 비육우 / 3 : 번식우)
	private StringProperty mAuctionQcn; // 경매회차
	private StringProperty mIndNum; // 축산개체관리번호
	private StringProperty mIndMngCd; // 축산축종구분코드
	private StringProperty mFhsNum; // 농가식별번호
	private StringProperty mFarmMngNum; // 농장관리번호
	private StringProperty mExhibitor; // 농가명
	private StringProperty mBrandName; // 브랜드명
	private StringProperty mBirthday; // 생년월일
	private StringProperty mKpn; // KPN
	private StringProperty mGender; // 개체성별코드
	private StringProperty mGenderName; // 개체성별코드 명
	private StringProperty mMotherTypeCode; // 어미구분코드
	private StringProperty mMotherObjNum; // 어미축산개체관리번호
	private StringProperty mMotherCowName; // 혈통 명
	private StringProperty mMatime; // 산차
	private StringProperty mMaMonth; // 임신개월수
	private StringProperty mPasgQcn; // 계대
	private StringProperty mObjIdNum; // 개체식별번호
	private StringProperty mObjRegNum; // 축산개체종축등록번호
	private StringProperty mObjRegTypeNum; // 등록구분번호
	private StringProperty mRgnName; // 출하생산지역
	private StringProperty mReRgnName; // 출하생산지역 split => ex) 경남 하동군 금남면 계천리 => 금남, 경상남도 하동군 악양면 상중대2길 22-3 => 악양
	private StringProperty mDnaYn; // 친자검사결과여부
	private StringProperty mIsNew; // 신규여부
	private StringProperty mWeight; // 우출하중량
	private StringProperty mInitPrice; // 최초최저낙찰한도금액
	private StringProperty mLowPrice; // 최저낙찰한도금액
	private StringProperty mSraSbidUpPrice; // 축산낙찰단가
	private StringProperty mNote; // 비고내용
	private StringProperty mAucDt; // 경매일자
	private StringProperty mOslpNo; // 원표 번호
	private StringProperty mTrmnAmnNo; // 거래인 관리 번호
	private StringProperty mLedSqno; // 원장 일련번호

	private StringProperty mAuctionResult; // 낙유찰결과(01:낙찰/02:유찰)
	private StringProperty mAuctionSucBidder; // 낙찰자
	private StringProperty mAuctionBidPrice; // 응찰금액/낙찰금액
	private StringProperty mAuctionBidDateTime; // 응찰일시

	private StringProperty mLsChgDtm; // 최종변경일시
	private StringProperty mLsCmeNo; // 최종변경자개인번호
	private StringProperty mLwprChgNt; // 최저가 변경 횟수

	private StringProperty mIsLastEntry; // 마지막 출품 여부

	private StringProperty mStandPosition; // 계류대 번호
	private StringProperty mIsExcessCow; // 초과출장우여부
	private StringProperty mMacoYn; // 조합원/비조합원 여부
	private StringProperty mTrpcsPyYn; // 자가운송여부
	private StringProperty mPpgcowFeeDsc; // 번식우 - 임신,비임신 구분 코드

	private StringProperty mExpAuctionBidPrice; // -일괄 낙찰 예정 금액
	private StringProperty mExpAuctionSucBidder; // -일괄 낙찰 예정자
	private StringProperty aucYn; // 출장우 경매 여부
	private StringProperty rgSqno; //일괄 - 경매 구간 정보
	private StringProperty auctionTypeCode;	//경매 회차 유형 코드 (0:일괄,1:송아지,2:비육우.3:번식우)
	
	private StringProperty mGapMonth; // 월령(개월수) (2022.09.07)
	private StringProperty mRgDscName; // 송아지혈통명 (2023.03.15) 
	private StringProperty mSraMwmName; // 낙찰자명 (2023.03.15)
	private StringProperty mPriceUnit; //경매단가
	
	private EntryInfo TEST;

	public SpEntryInfo() {
	}	

	public SpEntryInfo(EntryInfo entryInfo) {
		TEST = entryInfo;
		this.mAuctionHouseCode = new SimpleStringProperty(entryInfo.getAuctionHouseCode());
		this.mEntryNum = new SimpleStringProperty(entryInfo.getEntryNum());
		this.mAuctionQcn = new SimpleStringProperty(entryInfo.getAuctionQcn());
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
		this.mGenderName = new SimpleStringProperty(entryInfo.getGenderName());
		this.mMotherTypeCode = new SimpleStringProperty(entryInfo.getMotherTypeCode());
		this.mMotherObjNum = new SimpleStringProperty(entryInfo.getMotherObjNum());
		this.mMotherCowName = new SimpleStringProperty(entryInfo.getMotherCowName());
		this.mMatime = new SimpleStringProperty(entryInfo.getMaTime());
		this.mMaMonth = new SimpleStringProperty(Integer.toString(entryInfo.getMaMonth()));
		this.mPasgQcn = new SimpleStringProperty(entryInfo.getPasgQcn());
		this.mObjIdNum = new SimpleStringProperty(entryInfo.getObjIdNum());
		this.mObjRegNum = new SimpleStringProperty(entryInfo.getObjRegNum());
		this.mObjRegTypeNum = new SimpleStringProperty(entryInfo.getObjRegTypeNum());
		this.mIsNew = new SimpleStringProperty(entryInfo.getIsNew());
		this.mWeight = new SimpleStringProperty(Integer.toString((int) Double.parseDouble(entryInfo.getWeight())));
		this.mInitPrice = new SimpleStringProperty(Integer.toString(entryInfo.getInitPrice()));
		this.mLowPrice = new SimpleStringProperty(Integer.toString(entryInfo.getLowPrice()));
		this.mNote = new SimpleStringProperty(entryInfo.getNote());
		this.mRgnName = new SimpleStringProperty(entryInfo.getRgnName());
		this.mDnaYn = new SimpleStringProperty(entryInfo.getDnaYn());
		this.mAuctionSucBidder = new SimpleStringProperty(entryInfo.getAuctionSucBidder());
		this.mAuctionBidPrice = new SimpleStringProperty(Integer.toString(entryInfo.getAuctionBidPrice()));
		this.mAuctionBidDateTime = new SimpleStringProperty(entryInfo.getAuctionBidDateTime());
		this.mAuctionResult = new SimpleStringProperty(entryInfo.getAuctionResult());
		this.mIsLastEntry = new SimpleStringProperty(entryInfo.getIsLastEntry());
		this.mAucDt = new SimpleStringProperty(entryInfo.getAucDt());
		this.mLsChgDtm = new SimpleStringProperty(entryInfo.getLsChgDtm());
		this.mLsCmeNo = new SimpleStringProperty(entryInfo.getLsCmeNo());
		this.mLwprChgNt = new SimpleStringProperty(Integer.toString(entryInfo.getLwprChgNt()));
		this.mOslpNo = new SimpleStringProperty(entryInfo.getOslpNo());
		this.mTrmnAmnNo = new SimpleStringProperty(entryInfo.getTrmnAmnNo());
		this.mLedSqno = new SimpleStringProperty(entryInfo.getLedSqno());
		this.mSraSbidUpPrice = new SimpleStringProperty(Integer.toString(entryInfo.getSraSbidUpPrice()));
		this.mStandPosition = new SimpleStringProperty(entryInfo.getStandPosition());
		this.mIsExcessCow = new SimpleStringProperty(entryInfo.getIsExcessCow());
		this.mReRgnName = new SimpleStringProperty(entryInfo.getRgnName());
		this.mMacoYn = new SimpleStringProperty(entryInfo.getMacoYn());
		this.mTrpcsPyYn = new SimpleStringProperty(entryInfo.getTrpcsPyYn());
		this.mPpgcowFeeDsc = new SimpleStringProperty(entryInfo.getPpgcowFeeDsc());
		this.mExpAuctionBidPrice = new SimpleStringProperty(entryInfo.getExpAuctionBidPrice());
		this.mExpAuctionSucBidder = new SimpleStringProperty(entryInfo.getExpAuctionSucBidder());
		this.aucYn = new SimpleStringProperty(entryInfo.getAucYn());
		this.rgSqno = new SimpleStringProperty(entryInfo.getExpAuctionIntNum());
		this.auctionTypeCode = new SimpleStringProperty(entryInfo.getAuctionTypeCode());
		this.mGapMonth = new SimpleStringProperty(entryInfo.getGapMonth());
		this.mRgDscName = new SimpleStringProperty(entryInfo.getRgDscName());
		this.mSraMwmName = new SimpleStringProperty(entryInfo.getSraMwmnName());
		this.mPriceUnit = new SimpleStringProperty(entryInfo.getmPriceUnit());
	}
	
	public EntryInfo getEntryInfo() {
		return TEST;
	}

	public StringProperty getAuctionHouseCode() {
		return returnValue(mAuctionHouseCode);
	}

	public void setAuctionHouseCode(StringProperty mAuctionHouseCode) {
		this.mAuctionHouseCode = mAuctionHouseCode;
	}

	public StringProperty getEntryNum() {
		return returnValue(mEntryNum);
	}

	public void setEntryNum(StringProperty mEntryNum) {
		this.mEntryNum = mEntryNum;
	}

	public int getEntryNumberValue() {
		
		if(!CommonUtils.getInstance().isEmptyProperty(mEntryNum)) {
			return Integer.parseInt(this.mEntryNum.getValue()) ;
		}else {
			return -1;
		}
	}


	public StringProperty getEntryType() {
		return returnValue(mEntryType);
	}

	public void setEntryType(StringProperty mEntryType) {
		this.mEntryType = mEntryType;
	}

	public StringProperty getIndNum() {
		return returnValue(mIndNum);
	}

	public void setIndNum(StringProperty mIndNum) {
		this.mIndNum = mIndNum;
	}

	public StringProperty getIndMngCd() {
		return returnValue(mIndMngCd);
	}

	public void setIndMngCd(StringProperty mIndMngCd) {
		this.mIndMngCd = mIndMngCd;
	}

	public StringProperty getFhsNum() {
		return returnValue(mFhsNum);
	}

	public void setFhsNum(StringProperty mFhsNum) {
		this.mFhsNum = mFhsNum;
	}

	public StringProperty getFarmMngNum() {
		return returnValue(mFarmMngNum);
	}

	public void setFarmMngNum(StringProperty mFarmMngNum) {
		this.mFarmMngNum = mFarmMngNum;
	}

	public StringProperty getExhibitor() {
		return returnValue(mExhibitor);
	}

	public void setExhibitor(StringProperty mExhibitor) {
		this.mExhibitor = mExhibitor;
	}

	public StringProperty getBrandName() {
		return returnValue(mBrandName);
	}

	public void setBrandName(StringProperty mBrandName) {
		this.mBrandName = mBrandName;
	}

	public StringProperty getBirthday() {
		return returnValue(mBirthday);
	}

	public void setBirthday(StringProperty mBirthday) {
		this.mBirthday = mBirthday;
	}

	public StringProperty getKpn() {
		return returnValue(mKpn);
	}

	public void setKpn(StringProperty mKpn) {
		this.mKpn = mKpn;
	}

	public StringProperty getGender() {
		return returnValue(mGender);
	}

	public void setGender(StringProperty mGender) {
		this.mGender = mGender;
	}

	public StringProperty getMotherTypeCode() {
		return returnValue(mMotherTypeCode);
	}

	public void setMotherTypeCode(StringProperty mMotherTypeCode) {
		this.mMotherTypeCode = mMotherTypeCode;
	}

	public StringProperty getMotherObjNum() {
		return returnValue(mMotherObjNum);
	}

	public void setMotherObjNum(StringProperty mMotherObjNum) {
		this.mMotherObjNum = mMotherObjNum;
	}

	public StringProperty getMatime() {
		return returnValue(mMatime);
	}

	public void setMatime(StringProperty mMatime) {
		this.mMatime = mMatime;
	}

	public StringProperty getPasgQcn() {
		return returnValue(mPasgQcn);
	}

	public void setPasgQcn(StringProperty mPasgQcn) {
		this.mPasgQcn = mPasgQcn;
	}

	public StringProperty getObjIdNum() {
		return returnValue(mObjIdNum);
	}

	public void setObjIdNum(StringProperty mObjIdNum) {
		this.mObjIdNum = mObjIdNum;
	}

	public StringProperty getObjRegNum() {
		return returnValue(mObjRegNum);
	}

	public void setObjRegNum(StringProperty mObjRegNum) {
		this.mObjRegNum = mObjRegNum;
	}

	public StringProperty getObjRegTypeNum() {
		return returnValue(mObjRegTypeNum);
	}

	public void setObjRegTypeNum(StringProperty mObjRegTypeNum) {
		this.mObjRegTypeNum = mObjRegTypeNum;
	}

	public StringProperty getIsNew() {
		return returnValue(mIsNew);
	}

	public void setIsNew(StringProperty mIsNew) {
		this.mIsNew = mIsNew;
	}

	public StringProperty getWeight() {
		return returnValue(mWeight);
	}

	public void setWeight(StringProperty mWeight) {
		this.mWeight = mWeight;
	}

	public StringProperty getInitPrice() {
		return returnValue(mInitPrice);
	}

	public void setInitPrice(StringProperty mInitPrice) {
		this.mInitPrice = mInitPrice;
	}

	public StringProperty getLowPrice() {
		return returnValue(mLowPrice);
	}

	public void setMaMonth(StringProperty maMonth) {
		this.mMaMonth = maMonth;
	}

	public StringProperty getMaMonth() {
		return returnValue(mMaMonth);
	}

	public int getLowPriceInt() {

		int lowPrice = 0;

		if (getLowPrice() != null || !getLowPrice().getValue().isEmpty()) {
			lowPrice = Integer.parseInt(getLowPrice().getValue());
		}

		return lowPrice;
	}

	public void setLowPrice(StringProperty mLowPrice) {
		this.mLowPrice = mLowPrice;
	}

	public StringProperty getNote() {
		return returnValue(mNote);
	}

	public void setNote(StringProperty mNote) {
		this.mNote = mNote;
	}

	public StringProperty getIsLastEntry() {
		return returnValue(mIsLastEntry);
	}

	public void setIsLastEntry(StringProperty mIsLastEntry) {
		this.mIsLastEntry = mIsLastEntry;
	}

	public StringProperty getRgnName() {
		return returnValue(mRgnName);
	}

	public void setRgnName(StringProperty mRgnName) {
		this.mRgnName = mRgnName;
	}

	public StringProperty getDnaYn() {
		return returnValue(mDnaYn);
	}

	public void setDnaYn(StringProperty mDnaYn) {
		this.mDnaYn = mDnaYn;
	}

	public StringProperty getAuctionResult() {
		return returnValue(mAuctionResult);
	}

	public void setAuctionResult(StringProperty mAuctionResult) {
		this.mAuctionResult = mAuctionResult;
	}

	public StringProperty getAuctionSucBidder() {
		return returnValue(mAuctionSucBidder);
	}

	public void setAuctionSucBidder(StringProperty mAuctionSucBidder) {
		this.mAuctionSucBidder = mAuctionSucBidder;
	}

	public StringProperty getAuctionBidPrice() {
		return returnValue(mAuctionBidPrice);
	}

	public void setAuctionBidPrice(StringProperty mAuctionBidPrice) {
		this.mAuctionBidPrice = mAuctionBidPrice;
	}

	public StringProperty getAuctionBidDateTime() {
		return returnValue(mAuctionBidDateTime);
	}

	public void setAuctionBidDateTime(StringProperty mAuctionBidDateTime) {
		this.mAuctionBidDateTime = mAuctionBidDateTime;
	}

	public StringProperty getAucDt() {
		return returnValue(mAucDt);
	}

	public void setAucDt(StringProperty mAucDt) {
		this.mAucDt = mAucDt;
	}

	public StringProperty getLsChgDtm() {
		return returnValue(mLsChgDtm);
	}

	public void setLsChgDtm(StringProperty mLsChgDtm) {
		this.mLsChgDtm = mLsChgDtm;
	}

	public StringProperty getLsCmeNo() {
		return returnValue(mLsCmeNo);
	}

	public void setLsCmeNo(StringProperty mLsCmeNo) {
		this.mLsCmeNo = mLsCmeNo;
	}

	public StringProperty getLwprChgNt() {
		return mLwprChgNt;
	}

	public void setLwprChgNt(StringProperty mLwprChgNt) {
		this.mLwprChgNt = mLwprChgNt;
	}

	public StringProperty getAuctionQcn() {
		return returnValue(mAuctionQcn);
	}

	public void setAuctionQcn(StringProperty mAuctionQcn) {
		this.mAuctionQcn = mAuctionQcn;
	}

	public StringProperty getOslpNo() {
		return returnValue(mOslpNo);
	}

	public void setOslpNo(StringProperty mOslpNo) {
		this.mOslpNo = mOslpNo;
	}

	public StringProperty getTrmnAmnNo() {
		return returnValue(mTrmnAmnNo);
	}

	public void setTrmnAmnNo(StringProperty mTrmnAmnNo) {
		this.mTrmnAmnNo = mTrmnAmnNo;
	}

	public StringProperty getLedSqno() {
		return returnValue(mLedSqno);
	}

	public void setLedSqno(StringProperty mLedSqno) {
		this.mLedSqno = mLedSqno;
	}

	public StringProperty getGenderName() {
		return returnValue(mGenderName);
	}

	public void setGenderName(StringProperty mGenderName) {
		this.mGenderName = mGenderName;
	}

	public StringProperty getMotherCowName() {
		return returnValue(mMotherCowName);
	}

	public void setMotherCowName(StringProperty mMotherCowName) {
		this.mMotherCowName = mMotherCowName;
	}

	public StringProperty getSraSbidUpPrice() {
		return mSraSbidUpPrice;
	}

	public void setSraSbidUpPrice(StringProperty mSraSbidUpPrice) {
		this.mSraSbidUpPrice = mSraSbidUpPrice;
	}

	public StringProperty getStandPosition() {
		return mStandPosition;
	}

	public void setStandPosition(StringProperty mStandPosition) {
		this.mStandPosition = mStandPosition;
	}

	public StringProperty getIsExcessCow() {
		return mIsExcessCow;
	}

	public void setIsExcessCow(StringProperty mIsExcessCow) {
		this.mIsExcessCow = mIsExcessCow;
	}

	public StringProperty getReRgnName() {
		return returnValue(mReRgnName);
	}

	public void setReRgnName(StringProperty mReRgnName) {
		this.mReRgnName = mReRgnName;
	}

	public StringProperty getMacoYn() {
		return mMacoYn;
	}

	public void setMacoYn(StringProperty mMacoYn) {
		this.mMacoYn = mMacoYn;
	}

	public StringProperty getTrpcsPyYn() {
		return mTrpcsPyYn;
	}

	public void setTrpcsPyYn(StringProperty mTrpcsPyYn) {
		this.mTrpcsPyYn = mTrpcsPyYn;
	}

	public StringProperty getPpgcowFeeDsc() {
		return mPpgcowFeeDsc;
	}

	public void setPpgcowFeeDsc(StringProperty mPpgcowFeeDsc) {
		this.mPpgcowFeeDsc = mPpgcowFeeDsc;
	}

	public StringProperty getExpAuctionBidPrice() {
		return returnValue(mExpAuctionBidPrice);
	}

	public void setExpAuctionBidPrice(StringProperty mExpAuctionBidPrice) {
		this.mExpAuctionBidPrice = mExpAuctionBidPrice;
	}

	public StringProperty getExpAuctionSucBidder() {
		return returnValue(mExpAuctionSucBidder);
	}

	public void setExpAuctionSucBidder(StringProperty mExpAuctionSucBidder) {
		this.mExpAuctionSucBidder = mExpAuctionSucBidder;
	}

	public StringProperty getAucYn() {
		return aucYn;
	}

	public void setAucYn(StringProperty aucYn) {
		this.aucYn = aucYn;
	}
	
	public StringProperty getRgSqno() {
		return returnValue(rgSqno);
	}

	public void setRgSqno(StringProperty rgSqno) {
		this.rgSqno = rgSqno;
	}

	public StringProperty getAuctionTypeCode() {
		return auctionTypeCode;
	}

	public void setAuctionTypeCode(StringProperty auctionTypeCode) {
		this.auctionTypeCode = auctionTypeCode;
	}
	
	public StringProperty getGapMonth() {
		return mGapMonth;
	}

	public void setGapMonth(StringProperty gapMonth) {
		this.mGapMonth = gapMonth;
	}
	
	public StringProperty getRgDscName() {
		return mRgDscName;
	}

	public void setRgDscName(StringProperty rgDscName) {
		this.mRgDscName = rgDscName;
	}
	
	public StringProperty getSraMwmnName() {
		return mSraMwmName;
	}

	public void setSraMwmnName(StringProperty sraMwmName) {
		this.mSraMwmName = sraMwmName;
	}

	public StringProperty getmPriceUnit() {
		return mPriceUnit;
	}

	public void setmPriceUnit(StringProperty mPriceUnit) {
		this.mPriceUnit = mPriceUnit;
	}

	public String getConvertBirthDay() {

		String convertBirthDay = "";

		String month = "";

		if (CommonUtils.getInstance().isValidString(getBirthday().getValue())) {

			boolean isCheck = CommonUtils.getInstance().isValidationDate(getBirthday().getValue());

			if (isCheck) {

				try {
					SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
					SimpleDateFormat newDtFormat = new SimpleDateFormat("yy.MM.dd");
					Date formatDate = dtFormat.parse(getBirthday().getValue());
					convertBirthDay = newDtFormat.format(formatDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				month = CommonUtils.getInstance().geDiffDateMonth(getBirthday().getValue(),
						CommonUtils.getInstance().getTodayYYYYMMDD());
			} else {
				convertBirthDay = getBirthday().getValue();
				month = "";
			}
		}

		if (CommonUtils.getInstance().isValidString(convertBirthDay)
				&& CommonUtils.getInstance().isValidString(convertBirthDay)) {
			return convertBirthDay + "(" + month + "개월)";
		} else {
			return "";
		}
	}

	public StringProperty getBiddingResult() {

		SimpleStringProperty resultStr = new SimpleStringProperty();

		if (mAuctionResult != null) {

			String code = mAuctionResult.getValue();

			if (code.equals(GlobalDefineCode.AUCTION_RESULT_CODE_READY)) {

				if (Integer.parseInt(mLowPrice.getValue()) > 0) {

					if (SettingApplication.getInstance().isSingleAuction()) {

						resultStr.setValue("대기");

					} else {
						
						if ((aucYn != null && "1".equals(aucYn.getValue())) || GlobalDefine.AUCTION_INFO.auctionRoundData != null
								&& GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PAUSE)) {
							resultStr.setValue("진행");
						} else {
							resultStr.setValue("대기");
						}

					}

				} else {
					resultStr.setValue("결장");
				}

			} else if (code.equals(GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS)) {
				resultStr.setValue("낙찰");
			} else if (code.equals(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING)) {
		
				if (SettingApplication.getInstance().isSingleAuction()) {
					
					resultStr.setValue("보류");
				
				} else {

					if ((aucYn != null && "1".equals(aucYn.getValue())) || GlobalDefine.AUCTION_INFO.auctionRoundData != null
							&& GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PAUSE)) {
						resultStr.setValue("진행");
					} else {
						resultStr.setValue("보류");
					}

				}
			}
		}

		return resultStr;
	}

	private StringProperty returnValue(StringProperty value) {
		if (value != null && value.getValue() != null && !value.getValue().isBlank()) {
			return value;
		} else {
			return new SimpleStringProperty("");
		}
	}
	
	

	@Override
	public String getEncodedMessage() {
		return String.format(
				"%c%c%c" + "%s%c" 
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
						+ "%s%c"
						+ "%s%c"
						+ "%s%c"
						+ "%s%c"
						+ "%s%c"
						+ "%s%c"
						+ "%s%c"
						+ "%s%c"
						+ "%s"
						+ "%c%s",
				ORIGIN, TYPE, AuctionShareSetting.DELIMITER,
				CommonUtils.getInstance().replaceDelimiter(getAuctionHouseCode().getValue()),AuctionShareSetting.DELIMITER, 
				CommonUtils.getInstance().replaceDelimiter(getEntryNum().getValue()),AuctionShareSetting.DELIMITER, 
				CommonUtils.getInstance().replaceDelimiter(getAuctionQcn().getValue()),AuctionShareSetting.DELIMITER, 
				CommonUtils.getInstance().replaceDelimiter(getEntryType().getValue()),AuctionShareSetting.DELIMITER,
				CommonUtils.getInstance().replaceDelimiter(getIndNum().getValue()),AuctionShareSetting.DELIMITER, 			// 축산개체관리번호 SRA_INDV_AMNNO
				CommonUtils.getInstance().replaceDelimiter(getIndMngCd().getValue()), AuctionShareSetting.DELIMITER,		// 축산축종구분코드		SRA_SRS_DSC
				CommonUtils.getInstance().replaceDelimiter(getFhsNum().getValue()), AuctionShareSetting.DELIMITER,			// 농가 식별번호			FHS_ID_NO
				CommonUtils.getInstance().replaceDelimiter(getFarmMngNum().getValue()), AuctionShareSetting.DELIMITER,		// 농장관리번호		FARM_AMNNO
				CommonUtils.getInstance().replaceDelimiter(getExhibitor().getValue()), AuctionShareSetting.DELIMITER,		// 농가명  			FTSNM
				CommonUtils.getInstance().replaceDelimiter(getBrandName().getValue()), AuctionShareSetting.DELIMITER,		// 브랜드명			BRANDNM
				CommonUtils.getInstance().replaceDelimiter(getBirthday().getValue()), AuctionShareSetting.DELIMITER,		// 생년월일 			BIRTH
				CommonUtils.getInstance().replaceDelimiter(getKpn().getValue()), AuctionShareSetting.DELIMITER,				// KPN					KPN_NO
				CommonUtils.getInstance().replaceDelimiter(getGenderName().getValue()), AuctionShareSetting.DELIMITER,		// 개체성별코드명     INDV_SEX_C_NAME
				CommonUtils.getInstance().replaceDelimiter(getMotherCowName().getValue()),AuctionShareSetting.DELIMITER, // 혈통명 SIMP_CNM AS MCOW_DSC
				CommonUtils.getInstance().replaceDelimiter(getMotherObjNum().getValue()), AuctionShareSetting.DELIMITER,	// 어미축산개체관리번호	 MCOW_SRA_INDV_AMNNO
				CommonUtils.getInstance().replaceDelimiter(getMatime().getValue()), AuctionShareSetting.DELIMITER,			// 산차 MATIME
				CommonUtils.getInstance().replaceDelimiter(getMaMonth().getValue()), AuctionShareSetting.DELIMITER,			// 임신개월수	PRNY_MTCN
				CommonUtils.getInstance().replaceDelimiter(getPasgQcn().getValue()), AuctionShareSetting.DELIMITER,			// 계대	SRA_INDV_PASG_QCN
				CommonUtils.getInstance().replaceDelimiter(getObjIdNum().getValue()), AuctionShareSetting.DELIMITER,		// 개체	 식별번호	INDV_ID_NO
				CommonUtils.getInstance().replaceDelimiter(getObjRegNum().getValue()), AuctionShareSetting.DELIMITER,		// 축산개체종축등록번호	SRA_INDV_BRDSRA_RG_NO
				CommonUtils.getInstance().replaceDelimiter(getObjRegTypeNum().getValue()),AuctionShareSetting.DELIMITER, 	// 등록구분코드 RG_DSC
				CommonUtils.getInstance().replaceDelimiter(getRgnName().getValue()), AuctionShareSetting.DELIMITER,	 	// 출하생산지역	 SRA_PD_RGNNM_FMT
				CommonUtils.getInstance().replaceDelimiter(getDnaYn().getValue()), AuctionShareSetting.DELIMITER,		 	// 친자검사결과여부	 DNA_YN
				CommonUtils.getInstance().replaceDelimiter(getIsNew().getValue()), AuctionShareSetting.DELIMITER,
				getWeight().getValue(), AuctionShareSetting.DELIMITER, 														// 중량, 보낼때만 KG 붙임.
				getInitPrice().getValue(), AuctionShareSetting.DELIMITER, 
				getLowPrice().getValue(),AuctionShareSetting.DELIMITER,
				CommonUtils.getInstance().replaceDelimiter(getNote().getValue()),AuctionShareSetting.DELIMITER,				// 비고 
				CommonUtils.getInstance().replaceDelimiter(getAuctionResult().getValue()),AuctionShareSetting.DELIMITER,
				CommonUtils.getInstance().replaceDelimiter(getAuctionSucBidder().getValue()),AuctionShareSetting.DELIMITER,
				getSraSbidUpPrice().getValue(), AuctionShareSetting.DELIMITER,
				CommonUtils.getInstance().replaceDelimiter(getAuctionBidDateTime().getValue()),AuctionShareSetting.DELIMITER, 
				CommonUtils.getInstance().replaceDelimiter(getIsLastEntry().getValue()),AuctionShareSetting.DELIMITER,				
				CommonUtils.getInstance().replaceDelimiter(getStandPosition().getValue()),AuctionShareSetting.DELIMITER,
				CommonUtils.getInstance().replaceDelimiter(getIsExcessCow().getValue()),AuctionShareSetting.DELIMITER,
				getRgSqno().getValue(), AuctionShareSetting.DELIMITER, 				
				getAuctionTypeCode().getValue(), AuctionShareSetting.DELIMITER, 
				CommonUtils.getInstance().replaceDelimiter(getSraMwmnName().getValue()),	// 낙찰자명 (2023.03.15) : 홍길동 
				AuctionShareSetting.DELIMITER, getGapMonth().getValue(), 					// 월령 (2023.03.15)
				AuctionShareSetting.DELIMITER, getRgDscName().getValue()					// 송아지혈통명 (2023.03.15) : 고등,혈통,미등록우
				, AuctionShareSetting.DELIMITER, getmPriceUnit().getValue()				
				
				);
	}

	@Override
	public boolean equals(Object o) {

		if (!(o instanceof SpEntryInfo)) {
			return false;
		}

		SpEntryInfo spEntryInfo = (SpEntryInfo) o;

		if (getEntryNum() == null || getEntryNum().getValue().isEmpty()) {
			return false;
		}

		if (spEntryInfo.getEntryNum() == null || spEntryInfo.getEntryNum().getValue().isEmpty()) {
			return false;
		}

		return spEntryInfo.getEntryNum().getValue().equals(getEntryNum().getValue());
	}

	public SpEntryInfo clone() {
		SpEntryInfo vo = null;
		try {
			vo = (SpEntryInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return vo;
	}

}
