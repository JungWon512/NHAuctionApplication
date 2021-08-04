package com.nh.controller.model;

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
 * SC | 조합구분코드 | 출품번호 | 경매대상구분코드 | 축산개체관리번호 | 축산축종구분코드 | 농가식별번호 | 농장관리번호 | 농가명 |
 * 브랜드명 | 생년월일 | KPN번호 | 개체성별코드 | 어미소구분코드 | 어미소축산개체관리번호 | 산차 | 임신개월수 |  계대 | 계체식별번호 |
 * 축산개체종축등록번호 | 등록구분번호 | 신규여부 | 우출하중량 | 최초최저낙찰한도금액 | 최저낙찰한도금액 | 비고내용 | 마지막출품여부
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
    private StringProperty mMatime; // 산차
    private StringProperty mMaMonth; // 임신개월수
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
    private StringProperty mAucDt;				 // 경매일자
    
	private StringProperty mLsChgDtm; // 최종변경일시
	private StringProperty mLsCmeNo; // 최종변경자개인번호


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
        this.mMaMonth = new SimpleStringProperty(entryInfo.getMaMonth());
        this.mPasgQcn = new SimpleStringProperty(entryInfo.getPasgQcn());
        this.mObjIdNum = new SimpleStringProperty(entryInfo.getObjIdNum());
        this.mObjRegNum = new SimpleStringProperty(entryInfo.getObjRegNum());
        this.mObjRegTypeNum = new SimpleStringProperty(entryInfo.getObjRegTypeNum());
        this.mIsNew = new SimpleStringProperty(entryInfo.getIsNew());
        this.mWeight = new SimpleStringProperty(Integer.toString((int) Double.parseDouble(entryInfo.getWeight())));
        this.mInitPrice = new SimpleStringProperty(Integer.toString((int) Double.parseDouble(entryInfo.getInitPrice())));
        this.mLowPrice = new SimpleStringProperty(Integer.toString((int) Double.parseDouble(entryInfo.getLowPrice())));
        this.mNote = new SimpleStringProperty(entryInfo.getNote());
        this.mRgnName = new SimpleStringProperty(entryInfo.getRgnName());
        this.mDnaYn = new SimpleStringProperty(entryInfo.getDnaYn());
        this.mAuctionSucBidder = new SimpleStringProperty(entryInfo.getAuctionSucBidder());
        this.mAuctionBidPrice = new SimpleStringProperty(entryInfo.getAuctionBidPrice());
        this.mAuctionBidDateTime = new SimpleStringProperty(entryInfo.getAuctionBidDateTime());
        this.mAuctionResult = new SimpleStringProperty(entryInfo.getAuctionResult());
        this.mIsLastEntry = new SimpleStringProperty(entryInfo.getIsLastEntry());
        this.mAucDt = new SimpleStringProperty(entryInfo.getAucDt());
        this.mLsChgDtm = new SimpleStringProperty(entryInfo.getLsChgDtm());
        this.mLsCmeNo = new SimpleStringProperty(entryInfo.getLsCmeNo()); 
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

	public StringProperty getBiddingResult() {

        SimpleStringProperty resultStr = new SimpleStringProperty();

        if (mAuctionResult != null) {

            String code = mAuctionResult.getValue();

            if (code.equals(GlobalDefineCode.AUCTION_RESULT_CODE_READY)) {
                resultStr.setValue("대기");
            } else if (code.equals(GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS)) {
                resultStr.setValue("낙찰");
            } else if (code.equals(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING)) {
                resultStr.setValue("보류");
            }
        }

        return resultStr;
    }
	
	private StringProperty returnValue(StringProperty value) {

		if(value !=  null) {
			return value;
		}else {
			return new SimpleStringProperty("");
		}

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
                        + "%s%c"
                        + "%s",
                ORIGIN, TYPE, AuctionShareSetting.DELIMITER,
                getAuctionHouseCode().getValue(), AuctionShareSetting.DELIMITER,
                getEntryNum().getValue(), AuctionShareSetting.DELIMITER,
                getEntryType().getValue(), AuctionShareSetting.DELIMITER,
                getIndNum().getValue(), AuctionShareSetting.DELIMITER,
                getIndMngCd().getValue(), AuctionShareSetting.DELIMITER,
                getFhsNum().getValue(), AuctionShareSetting.DELIMITER,
                getFarmMngNum().getValue(), AuctionShareSetting.DELIMITER,
                getExhibitor().getValue(), AuctionShareSetting.DELIMITER,
                getBrandName().getValue(), AuctionShareSetting.DELIMITER,
                getBirthday().getValue(), AuctionShareSetting.DELIMITER,
                getKpn().getValue(), AuctionShareSetting.DELIMITER,
                getGender().getValue(), AuctionShareSetting.DELIMITER,
                getMotherTypeCode().getValue(), AuctionShareSetting.DELIMITER,
                getMotherObjNum().getValue(), AuctionShareSetting.DELIMITER,
                getMatime().getValue(), AuctionShareSetting.DELIMITER,
                getMaMonth().getValue(), AuctionShareSetting.DELIMITER,
                getPasgQcn().getValue(), AuctionShareSetting.DELIMITER,
                getObjIdNum().getValue(), AuctionShareSetting.DELIMITER,
                getObjRegNum().getValue(), AuctionShareSetting.DELIMITER,
                getObjRegTypeNum().getValue(), AuctionShareSetting.DELIMITER,
                getRgnName().getValue(), AuctionShareSetting.DELIMITER,
                getDnaYn().getValue(), AuctionShareSetting.DELIMITER,
                getIsNew().getValue(), AuctionShareSetting.DELIMITER,
                getWeight().getValue(), AuctionShareSetting.DELIMITER,
                getInitPrice().getValue(), AuctionShareSetting.DELIMITER,
                getLowPrice().getValue(), AuctionShareSetting.DELIMITER,
                getNote().getValue(), AuctionShareSetting.DELIMITER,
                getAuctionResult().getValue(), AuctionShareSetting.DELIMITER,
                getAuctionSucBidder().getValue(), AuctionShareSetting.DELIMITER,
                getAuctionBidDateTime().getValue(), AuctionShareSetting.DELIMITER,
                getNote().getValue(), AuctionShareSetting.DELIMITER,
                getIsLastEntry().getValue());
    }

    @Override
    public boolean equals(Object o) {
    	
        if (!(o instanceof SpEntryInfo)) {
            return false;
        }
     
        SpEntryInfo spEntryInfo = (SpEntryInfo) o;
        
        if(getEntryNum() == null || getEntryNum().getValue().isEmpty()) {
        	return false;
        }
        
        if (spEntryInfo.getEntryNum() == null || spEntryInfo.getEntryNum().getValue().isEmpty()) {
            return false;
        }

        return spEntryInfo.getEntryNum().getValue().equals(getEntryNum().getValue());
    }
}
