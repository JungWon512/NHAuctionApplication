package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 차량 정보 전송
 * 
 * 서버 -> 공통
 * 
 * SR|상품번호|경매구분코드|경매회차|경매출품번호|경매레인명|경매레인코드|경매레인포트|경매거점명|경매거점코드|출품자번호|출품자명|출품종류코드|출품종류명|제조사명|차량명|차량번호|사용용도구분코드|사용용도구분명|
 * 소유구분코드|소유구분명|부재자가|시작가|희망가|경매출품순번|평가점수1|평가점수2|차량연식|주행거리|미션명|옵션명|연료명|배기량|보관품목록|기관상태|조향상태|변속상태|동력전달상태|제동상태|전기상태|공조상태|실내상태|
 * 점검의견|변경사항|특이사항|차량전개도이미지URL|차량이미지URL|TTS파일경로|TTS재생시간|출품정보변경여부|출품취소여부|출품자회원번호|다음출품번호|다음출품순번|다음출품차량거점코드|다음출품차량명|다음출품차량TTS|
 * 다음출품차량대표이미지정보|다음출품차량전개도이미지정보|
 *
 */
public class ResponseEntryInfo implements FromAuctionServer {
    public static final char TYPE = 'C';
    private String mProductCode; // 상품 번호
    private String mAuctionCode; // 경매 구분 코드
    private String mAuctionRound; // 경매 회차
    private String mAuctionEntryNum; // 경매 출품 번호
    private String mAuctionLaneName; // 경매 레인명
    private String mAuctionLaneCode; // 경매 레인코드
    private String mAuctionLanePort; // 경매 레인 포트
    private String mAuctionPositionName; // 경매 거점명
    private String mAuctionPositionCode; // 경매 거점 코드
    private String mExhibitorNum; // 출품자 번호
    private String mExhibitorName; // 출품자명
    private String mExhibitorTypeCode; // 출품 종류 코드
    private String mExhibitorTypeName; // 출품 종류명
    private String mVendorName; // 제조사명
    private String mCarName; // 차량명
    private String mCarNumber; // 차량 번호
    private String mUseTypeCode; // 사용 용도 구분 코드
    private String mUseTypeName; // 사용 용도 구분명
    private String mOwnerTypeCode; // 소유 구분 코드
    private String mOwnerTypeName; // 소유 구분명
    private String mAbsenteePrice; // 부재자 응찰가
    private String mAuctionStartPrice; // 시작가
    private String mAuctionHopePrice; // 희망가
    private String mAuctionSeqNum; // 경매 출품 순번
    private String mEvalPoint1; // 평가점수1
    private String mEvalPoint2; // 평가점수2
    private String mCarYear; // 차량 연식
    private String mMileage; // 주행거리
    private String mMissionName; // 미션명
    private String mOptionName; // 옵션명
    private String mFuelName; // 연료명
    private String mDisplacement; // 배기량
    private String mArchiveItem; // 보관품 목록
    private String mEngineState; // 기관상태
    private String mSteeringState; // 조향상태
    private String mShiftGearsState; // 변속상태
    private String mPowerState; // 동력전달상태
    private String mBrakeState; // 제동상태
    private String mElectricState; // 전기상태
    private String mAirConditioningState; // 공조상태
    private String mIndoorState; // 실내상태
    private String mInspectComment; // 점검의견
    private String mChangeContext; // 변경사항
    private String mSpecialNote; // 특이사항
    private String mCarEvalLayoutImage; // 차량 전개도 이미지 URL
    private String mCarImageList; // 차량 이미지 URL List
    private String mTtsFilePath; // TTS 파일 경로
    private String mTtsDuration; // TTS 재생 시간
    private String mFlagChangeInfo; // 출품 정보 변경 여부
    private String mFlagCancelEntry; // 출품 취소 여부
    private String mExhiCustno; // 출품자 회원번호
    private String mNextEntryNum; // 다음 출품 번호
    private String mNextEntrySeqNum; // 다움 출품 순번
    private String mNextEntryPositionCode; // 다음 출품 차량 거점 코드
    private String mNextEntryCarInfo; // 다음 출품 차량명
    private String mNextEntryTtsInfo; // 다음 출품 차량 TTS
    private String mNextEntryImageInfo; // 다음 출품 차량 이미지 정보
    private String mNextEntryEvalImageInfo; // 다음 출품 차량 전개도 이미지 정보

    public ResponseEntryInfo() {
    }

    public ResponseEntryInfo(String[] messages) {
        mProductCode = messages[1];
        mAuctionCode = messages[2];
        mAuctionRound = messages[3];
        mAuctionEntryNum = messages[4];
        mAuctionLaneName = messages[5];
        mAuctionLaneCode = messages[6];
        mAuctionLanePort = messages[7];
        mAuctionPositionName = messages[8];
        mAuctionPositionCode = messages[9];
        mExhibitorNum = messages[10];
        mExhibitorName = messages[11];
        mExhibitorTypeCode = messages[12];
        mExhibitorTypeName = messages[13];
        mVendorName = messages[14];
        mCarName = messages[15];
        mCarNumber = messages[16];
        mUseTypeCode = messages[17];
        mUseTypeName = messages[18];
        mOwnerTypeCode = messages[19];
        mOwnerTypeName = messages[20];
        mAbsenteePrice = messages[21];
        mAuctionStartPrice = messages[22];
        mAuctionHopePrice = messages[23];
        mAuctionSeqNum = messages[24];
        mEvalPoint1 = messages[25];
        mEvalPoint2 = messages[26];
        mCarYear = messages[27];
        mMileage = messages[28];
        mMissionName = messages[29];
        mOptionName = messages[30];
        mFuelName = messages[31];
        mDisplacement = messages[32];
        mArchiveItem = messages[33];
        mEngineState = messages[34];
        mSteeringState = messages[35];
        mShiftGearsState = messages[36];
        mPowerState = messages[37];
        mBrakeState = messages[38];
        mElectricState = messages[39];
        mAirConditioningState = messages[40];
        mIndoorState = messages[41];
        mInspectComment = messages[42];
        mChangeContext = messages[43];
        mSpecialNote = messages[44];
        mCarEvalLayoutImage = messages[45];
        mCarImageList = messages[46];
        mTtsFilePath = messages[47];
        mTtsDuration = messages[48];
        mFlagChangeInfo = messages[49];
        mFlagCancelEntry = messages[50];
        mExhiCustno = messages[51];
        mNextEntryNum = messages[52];
        mNextEntrySeqNum = messages[53];
        mNextEntryPositionCode = messages[54];
        mNextEntryCarInfo = messages[55];
        mNextEntryTtsInfo = messages[56];
        mNextEntryImageInfo = messages[57];
        mNextEntryEvalImageInfo = messages[58];
    }

    public String getProductCode() {
        return mProductCode;
    }

    public void setProductCode(String productCode) {
        this.mProductCode = productCode;
    }

    public String getAuctionCode() {
        return mAuctionCode;
    }

    public void setAuctionCode(String auctionCode) {
        this.mAuctionCode = auctionCode;
    }

    public String getAuctionRound() {
        return mAuctionRound;
    }

    public void setAuctionRound(String auctionRound) {
        this.mAuctionRound = auctionRound;
    }

    public String getAuctionEntryNum() {
        return mAuctionEntryNum;
    }

    public void setAuctionEntryNum(String auctionEntryNum) {
        this.mAuctionEntryNum = auctionEntryNum;
    }

    public String getAuctionLaneName() {
        return mAuctionLaneName;
    }

    public void setAuctionLaneName(String auctionLaneName) {
        this.mAuctionLaneName = auctionLaneName;
    }

    public String getAuctionLaneCode() {
        return mAuctionLaneCode;
    }

    public void setAuctionLaneCode(String auctionLaneCode) {
        this.mAuctionLaneCode = auctionLaneCode;
    }

    public String getAuctionLanePort() {
        return mAuctionLanePort;
    }

    public void setAuctionLanePort(String auctionLanePort) {
        this.mAuctionLanePort = auctionLanePort;
    }

    public String getAuctionPositionName() {
        return mAuctionPositionName;
    }

    public void setAuctionPositionName(String auctionPositionName) {
        this.mAuctionPositionName = auctionPositionName;
    }

    public String getAuctionPositionCode() {
        return mAuctionPositionCode;
    }

    public void setAuctionPositionCode(String auctionPositionCode) {
        this.mAuctionPositionCode = auctionPositionCode;
    }

    public String getExhibitorNum() {
        return mExhibitorNum;
    }

    public void setExhibitorNum(String exhibitorNum) {
        this.mExhibitorNum = exhibitorNum;
    }

    public String getExhibitorName() {
        return mExhibitorName;
    }

    public void setExhibitorName(String exhibitorName) {
        this.mExhibitorName = exhibitorName;
    }

    public String getExhibitorTypeCode() {
        return mExhibitorTypeCode;
    }

    public void setExhibitorTypeCode(String exhibitorTypeCode) {
        this.mExhibitorTypeCode = exhibitorTypeCode;
    }

    public String getExhibitorTypeName() {
        return mExhibitorTypeName;
    }

    public void setExhibitorTypeName(String exhibitorTypeName) {
        this.mExhibitorTypeName = exhibitorTypeName;
    }

    public String getVendorName() {
        return mVendorName;
    }

    public void setVendorName(String vendorName) {
        this.mVendorName = vendorName;
    }

    public String getCarName() {
        return mCarName;
    }

    public void setCarName(String carName) {
        this.mCarName = carName;
    }

    public String getCarNumber() {
        return mCarNumber;
    }

    public void setCarNumber(String carNumber) {
        this.mCarNumber = carNumber;
    }

    public String getUseTypeCode() {
        return mUseTypeCode;
    }

    public void setUseTypeCode(String useTypeCode) {
        this.mUseTypeCode = useTypeCode;
    }

    public String getUseTypeName() {
        return mUseTypeName;
    }

    public void setUseTypeName(String useTypeName) {
        this.mUseTypeName = useTypeName;
    }

    public String getOwnerTypeCode() {
        return mOwnerTypeCode;
    }

    public void setOwnerTypeCode(String ownerTypeCode) {
        this.mOwnerTypeCode = ownerTypeCode;
    }

    public String getOwnerTypeName() {
        return mOwnerTypeName;
    }

    public void setOwnerTypeName(String ownerTypeName) {
        this.mOwnerTypeName = ownerTypeName;
    }

    public String getAbsenteePrice() {
        return mAbsenteePrice;
    }

    public void setAbsenteePrice(String absenteePrice) {
        this.mAbsenteePrice = absenteePrice;
    }

    public String getAuctionStartPrice() {
        return mAuctionStartPrice;
    }

    public void setAuctionStartPrice(String auctionStartPrice) {
        this.mAuctionStartPrice = auctionStartPrice;
    }

    public String getAuctionHopePrice() {
        return mAuctionHopePrice;
    }

    public void setAuctionHopePrice(String auctionHopePrice) {
        this.mAuctionHopePrice = auctionHopePrice;
    }

    public String getAuctionSeqNum() {
        return mAuctionSeqNum;
    }

    public void setAuctionSeqNum(String auctionSeqNum) {
        this.mAuctionSeqNum = auctionSeqNum;
    }

    public String getEvalPoint1() {
        return mEvalPoint1;
    }

    public void setEvalPoint1(String evalPoint1) {
        this.mEvalPoint1 = evalPoint1;
    }

    public String getEvalPoint2() {
        return mEvalPoint2;
    }

    public void setEvalPoint2(String evalPoint2) {
        this.mEvalPoint2 = evalPoint2;
    }

    public String getCarYear() {
        return mCarYear;
    }

    public void setCarYear(String carYear) {
        this.mCarYear = carYear;
    }

    public String getMileage() {
        return mMileage;
    }

    public void setMileage(String mileage) {
        this.mMileage = mileage;
    }

    public String getMissionName() {
        return mMissionName;
    }

    public void setMissionName(String missionName) {
        this.mMissionName = missionName;
    }

    public String getOptionName() {
        return mOptionName;
    }

    public void setOptionName(String optionName) {
        this.mOptionName = optionName;
    }

    public String getFuelName() {
        return mFuelName;
    }

    public void setFuelName(String fuelName) {
        this.mFuelName = fuelName;
    }

    public String getDisplacement() {
        return mDisplacement;
    }

    public void setDisplacement(String displacement) {
        this.mDisplacement = displacement;
    }

    public String getArchiveItem() {
        return mArchiveItem;
    }

    public void setArchiveItem(String archiveItem) {
        this.mArchiveItem = archiveItem;
    }

    public String getEngineState() {
        return mEngineState;
    }

    public void setEngineState(String engineState) {
        this.mEngineState = engineState;
    }

    public String getSteeringState() {
        return mSteeringState;
    }

    public void setSteeringState(String steeringState) {
        this.mSteeringState = steeringState;
    }

    public String getShiftGearsState() {
        return mShiftGearsState;
    }

    public void setShiftGearsState(String shiftGearsState) {
        this.mShiftGearsState = shiftGearsState;
    }

    public String getPowerState() {
        return mPowerState;
    }

    public void setPowerState(String powerState) {
        this.mPowerState = powerState;
    }

    public String getBrakeState() {
        return mBrakeState;
    }

    public void setBrakeState(String brakeState) {
        this.mBrakeState = brakeState;
    }

    public String getElectricState() {
        return mElectricState;
    }

    public void setElectricState(String electricState) {
        this.mElectricState = electricState;
    }

    public String getAirConditioningState() {
        return mAirConditioningState;
    }

    public void setAirConditioningState(String airConditioningState) {
        this.mAirConditioningState = airConditioningState;
    }

    public String getIndoorState() {
        return mIndoorState;
    }

    public void setIndoorState(String indoorState) {
        this.mIndoorState = indoorState;
    }

    public String getInspectComment() {
        return mInspectComment;
    }

    public void setInspectComment(String inspectComment) {
        this.mInspectComment = inspectComment;
    }

    public String getChangeContext() {
        return mChangeContext;
    }

    public void setChangeContext(String changeContext) {
        this.mChangeContext = changeContext;
    }

    public String getSpecialNote() {
        return mSpecialNote;
    }

    public void setSpecialNote(String specialNote) {
        this.mSpecialNote = specialNote;
    }

    public String getCarEvalLayoutImage() {
        return mCarEvalLayoutImage;
    }

    public void setCarEvalLayoutImage(String carEvalLayoutImage) {
        this.mCarEvalLayoutImage = carEvalLayoutImage;
    }

    public String getCarImageList() {
        return mCarImageList;
    }

    public void setCarImageList(String carImageList) {
        this.mCarImageList = carImageList;
    }

    public String getTtsFilePath() {
        return mTtsFilePath;
    }

    public void setTtsFilePath(String ttsFilePath) {
        this.mTtsFilePath = ttsFilePath;
    }

    public String getTtsDuration() {
        return mTtsDuration;
    }

    public void setTtsDuration(String duration) {
        this.mTtsDuration = duration;
    }

    public String getFlagChangeInfo() {
        return mFlagChangeInfo;
    }

    public void setFlagChangeInfo(String flagChangeInfo) {
        this.mFlagChangeInfo = flagChangeInfo;
    }

    public String getFlagCancelEntry() {
        return mFlagCancelEntry;
    }

    public void setFlagCancelEntry(String flagCancelEntry) {
        this.mFlagCancelEntry = flagCancelEntry;
    }

    public String getExhiCustno() {
        return mExhiCustno;
    }

    public void setExhiCustno(String exhiCustno) {
        this.mExhiCustno = exhiCustno;
    }

    public String getNextEntryNum() {
        return mNextEntryNum;
    }

    public void setNextEntryNum(String nextEntryNum) {
        this.mNextEntryNum = nextEntryNum;
    }

    public String getNextEntrySeqNum() {
        return mNextEntrySeqNum;
    }

    public void setNextEntrySeqNum(String nextEntrySeqNum) {
        this.mNextEntrySeqNum = nextEntrySeqNum;
    }

    public String getNextEntryPositionCode() {
        return mNextEntryPositionCode;
    }

    public void setNextEntryPositionCode(String nextEntryPositionCode) {
        this.mNextEntryPositionCode = nextEntryPositionCode;
    }

    public String getNextEntryCarName() {
        return mNextEntryCarInfo;
    }

    public void setNextEntryCarName(String nextEntryCarName) {
        this.mNextEntryCarInfo = nextEntryCarName;
    }

    public String getNextEntryTtsInfo() {
        return mNextEntryTtsInfo;
    }

    public void setNextEntryTtsInfo(String nextEntryTtsInfo) {
        this.mNextEntryTtsInfo = nextEntryTtsInfo;
    }

    public String getNextEntryImageInfo() {
        return mNextEntryImageInfo;
    }

    public void setNextEntryImageInfo(String nextEntryImageInfo) {
        this.mNextEntryImageInfo = nextEntryImageInfo;
    }

    public String getNextEntryEvalImageInfo() {
        return mNextEntryEvalImageInfo;
    }

    public void setNextEntryEvalImageInfo(String nextEntryEvalImageInfo) {
        this.mNextEntryEvalImageInfo = nextEntryEvalImageInfo;
    }

    @Override
    public String getEncodedMessage() {
        return String.format(
                "%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s",
                ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mProductCode, AuctionShareSetting.DELIMITER, mAuctionCode,
                AuctionShareSetting.DELIMITER, mAuctionRound, AuctionShareSetting.DELIMITER, mAuctionEntryNum,
                AuctionShareSetting.DELIMITER, mAuctionLaneName, AuctionShareSetting.DELIMITER, mAuctionLaneCode,
                AuctionShareSetting.DELIMITER, mAuctionLanePort, AuctionShareSetting.DELIMITER, mAuctionPositionName,
                AuctionShareSetting.DELIMITER, mAuctionPositionCode, AuctionShareSetting.DELIMITER, mExhibitorNum,
                AuctionShareSetting.DELIMITER, mExhibitorName, AuctionShareSetting.DELIMITER, mExhibitorTypeCode,
                AuctionShareSetting.DELIMITER, mExhibitorTypeName, AuctionShareSetting.DELIMITER, mVendorName,
                AuctionShareSetting.DELIMITER, mCarName, AuctionShareSetting.DELIMITER, mCarNumber,
                AuctionShareSetting.DELIMITER, mUseTypeCode, AuctionShareSetting.DELIMITER, mUseTypeName,
                AuctionShareSetting.DELIMITER, mOwnerTypeCode, AuctionShareSetting.DELIMITER, mOwnerTypeName,
                AuctionShareSetting.DELIMITER, mAbsenteePrice, AuctionShareSetting.DELIMITER, mAuctionStartPrice,
                AuctionShareSetting.DELIMITER, mAuctionHopePrice, AuctionShareSetting.DELIMITER, mAuctionSeqNum,
                AuctionShareSetting.DELIMITER, mEvalPoint1, AuctionShareSetting.DELIMITER, mEvalPoint2,
                AuctionShareSetting.DELIMITER, mCarYear, AuctionShareSetting.DELIMITER, mMileage,
                AuctionShareSetting.DELIMITER, mMissionName, AuctionShareSetting.DELIMITER, mOptionName,
                AuctionShareSetting.DELIMITER, mFuelName, AuctionShareSetting.DELIMITER, mDisplacement,
                AuctionShareSetting.DELIMITER, mArchiveItem, AuctionShareSetting.DELIMITER, mEngineState,
                AuctionShareSetting.DELIMITER, mSteeringState, AuctionShareSetting.DELIMITER, mShiftGearsState,
                AuctionShareSetting.DELIMITER, mPowerState, AuctionShareSetting.DELIMITER, mBrakeState,
                AuctionShareSetting.DELIMITER, mElectricState, AuctionShareSetting.DELIMITER, mAirConditioningState,
                AuctionShareSetting.DELIMITER, mIndoorState, AuctionShareSetting.DELIMITER, mInspectComment,
                AuctionShareSetting.DELIMITER, mChangeContext, AuctionShareSetting.DELIMITER, mSpecialNote,
                AuctionShareSetting.DELIMITER, mCarEvalLayoutImage, AuctionShareSetting.DELIMITER, mCarImageList,
                AuctionShareSetting.DELIMITER, mTtsFilePath, AuctionShareSetting.DELIMITER, mTtsDuration,
                AuctionShareSetting.DELIMITER, mFlagChangeInfo, AuctionShareSetting.DELIMITER, mFlagCancelEntry,
                AuctionShareSetting.DELIMITER, mExhiCustno, AuctionShareSetting.DELIMITER, mNextEntryNum,
                AuctionShareSetting.DELIMITER, mNextEntrySeqNum, AuctionShareSetting.DELIMITER, mNextEntryPositionCode,
                AuctionShareSetting.DELIMITER, mNextEntryCarInfo, AuctionShareSetting.DELIMITER, mNextEntryTtsInfo,
                AuctionShareSetting.DELIMITER, mNextEntryImageInfo, AuctionShareSetting.DELIMITER,
                mNextEntryEvalImageInfo);
    }
}
