package com.nh.share.api.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.nh.share.server.models.ResponseEntryInfo;

/**
 * 
 * 출품 정보 조회 요청 응답 내부 객체
 * 
 * @see {ResponseAuctionEntryInformation}
 *
 */
public class AuctionEntryInformationResult {
    @Expose
    private String productCode; // 상품 번호
    @Expose
    private String auctionCode; // 경매 구분 코드
    @Expose
    private String auctionRound; // 경매 회차
    @Expose
    private String auctionEntryNum; // 경매 출품 번호
    @Expose
    private String auctionEntryNumSeq; // 경매 출품 순번
    @Expose
    private String auctionLaneName; // 경매 레인명
    @Expose
    private String auctionLaneCode; // 경매 레인코드
    @Expose
    private String auctionLanePort; // 경매 레인 포트
    @Expose
    private String auctionPositionName; // 경매 거점명
    @Expose
    private String auctionPositionCode; // 경매 거점 코드
    @Expose
    private String exhibitorNum; // 출품자 번호
    @Expose
    private String exhibitorName; // 출품자명
    @Expose
    private String exhibitorTypeCode; // 출품 종류 코드
    @Expose
    private String exhibitorTypeName; // 출품 종류명
    @Expose
    private String venderName; // 제조사명
    @Expose
    private String carName; // 차량명
    @Expose
    private String carNumber; // 차량 번호
    @Expose
    private String useTypeName; // 사용 용도 구분명
    @Expose
    private String ownerTypeName; // 소유 구분명
    @Expose
    private String absenteePrice; // 부재자 입찰가
    @Expose
    private String auctionStartPrice; // 시작가
    @Expose
    private String auctionHopePrice; // 희망가
    @Expose
    private String evalPoint1; // 평가점수1
    @Expose
    private String evalPoint2; // 평가점수2
    @Expose
    private String carYear; // 차량 연식
    @Expose
    private String mileage; // 주행거리
    @Expose
    private String missionName; // 미션명
    @Expose
    private String fuelName; // 연료명
    @Expose
    private String displacement; // 배기량
    @Expose
    private String archiveItem; // 보관품 목록
    @Expose
    private String engineState; // 기관상태
    @Expose
    private String steeringState; // 조향상태
    @Expose
    private String shiftGearsState; // 변속상태
    @Expose
    private String powerState; // 동력전달상태
    @Expose
    private String brakeState; // 제동상태
    @Expose
    private String electricState; // 전기상태
    @Expose
    private String airConditioningState; // 공조상태
    @Expose
    private String indoorState; // 실내상태
    @Expose
    private String inspectComment; // 점검의견
    @Expose
    private String changeContext; // 변경사항
    @Expose
    private String carEvalLayoutImage; // 차량 전개도 이미지 URL
    @Expose
    private List<AuctionEntryInformationCarImageListResult> carImageList; // 차량 이미지 URL List
    @Expose
    private String ttsFilePath; // TTS 파일 경로
    @Expose
    private String ttsDuration; // TTS 재생 시간(ms)
    @Expose
    private String flagChangeInfo; // 출품 정보 변경 여부 (Y/N)
    @Expose
    private String flagCancelEntry; // 출품 취소 여부 (Y/N)
    @Expose
    private String exhiCustno; // 출품자 회원번호
    @Expose
    private String mNextEntryNum; // 다음 출품 번호
    @Expose
    private String mNextEntrySeqNum; // 다움 출품 순번
    @Expose
    private String mNextEntryPositionCode; // 다음 출품 차량 거점 코드
    @Expose
    private String mNextEntryCarInfo; // 다음 출품 차량명
    @Expose
    private String mNextEntryTtsInfo; // 다음 출품 차량 TTS
    @Expose
    private String mNextEntryImageInfo; // 다음 출품 차량 이미지 정보
    @Expose
    private String mNextEntryEvalImageInfo; // 다음 출품 차량 전개도 이미지 정보

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getAuctionCode() {
        return auctionCode;
    }

    public void setAuctionCode(String auctionCode) {
        this.auctionCode = auctionCode;
    }

    public String getAuctionRound() {
        return auctionRound;
    }

    public void setAuctionRound(String auctionRound) {
        this.auctionRound = auctionRound;
    }

    public String getAuctionEntryNum() {
        return auctionEntryNum;
    }

    public void setAuctionEntryNum(String auctionEntryNum) {
        this.auctionEntryNum = auctionEntryNum;
    }

    public String getAuctionEntrySeq() {
        return auctionEntryNumSeq;
    }

    public void setAuctionEntrySeq(String auctionEntrySeq) {
        this.auctionEntryNumSeq = auctionEntrySeq;
    }

    public String getAuctionLaneName() {
        return auctionLaneName;
    }

    public void setAuctionLaneName(String auctionLaneName) {
        this.auctionLaneName = auctionLaneName;
    }

    public String getAuctionLaneCode() {
        return auctionLaneCode;
    }

    public void setAuctionLaneCode(String auctionLaneCode) {
        this.auctionLaneCode = auctionLaneCode;
    }

    public String getAuctionLanePort() {
        return auctionLanePort;
    }

    public void setAuctionLanePort(String auctionLanePort) {
        this.auctionLanePort = auctionLanePort;
    }

    public String getAuctionPositionName() {
        return auctionPositionName;
    }

    public void setAuctionPositionName(String auctionPositionName) {
        this.auctionPositionName = auctionPositionName;
    }

    public String getAuctionPositionCode() {
        return auctionPositionCode;
    }

    public void setAuctionPositionCode(String auctionPositionCode) {
        this.auctionPositionCode = auctionPositionCode;
    }

    public String getExhibitorNum() {
        return exhibitorNum;
    }

    public void setExhibitorNum(String exhibitorNum) {
        this.exhibitorNum = exhibitorNum;
    }

    public String getExhibitorName() {
        return exhibitorName;
    }

    public void setExhibitorName(String exhibitorName) {
        this.exhibitorName = exhibitorName;
    }

    public String getExhibitorTypeCode() {
        return exhibitorTypeCode;
    }

    public void setExhibitorTypeCode(String exhibitorTypeCode) {
        this.exhibitorTypeCode = exhibitorTypeCode;
    }

    public String getExhibitorTypeName() {
        return exhibitorTypeName;
    }

    public void setExhibitorTypeName(String exhibitorTypeName) {
        this.exhibitorTypeName = exhibitorTypeName;
    }

    public String getVendorName() {
        return venderName;
    }

    public void setVendorName(String venderName) {
        this.venderName = venderName;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getUseTypeName() {
        return useTypeName;
    }

    public void setUseTypeName(String useTypeName) {
        this.useTypeName = useTypeName;
    }

    public String getOwnerTypeName() {
        return ownerTypeName;
    }

    public void setOwnerTypeName(String ownerTypeName) {
        this.ownerTypeName = ownerTypeName;
    }

    public String getAbsenteePrice() {
        return absenteePrice;
    }

    public void setAbsenteePrice(String absenteePrice) {
        this.absenteePrice = absenteePrice;
    }

    public String getAuctionStartPrice() {
        return auctionStartPrice;
    }

    public void setAuctionStartPrice(String auctionStartPrice) {
        this.auctionStartPrice = auctionStartPrice;
    }

    public String getAuctionHopePrice() {
        return auctionHopePrice;
    }

    public void setAuctionHopePrice(String auctionHopePrice) {
        this.auctionHopePrice = auctionHopePrice;
    }

    public String getEvalPoint1() {
        return evalPoint1;
    }

    public void setEvalPoint1(String evalPoint1) {
        this.evalPoint1 = evalPoint1;
    }

    public String getEvalPoint2() {
        return evalPoint2;
    }

    public void setEvalPoint2(String evalPoint2) {
        this.evalPoint2 = evalPoint2;
    }

    public String getCarYear() {
        return carYear;
    }

    public void setCarYear(String carYear) {
        this.carYear = carYear;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public String getFuelName() {
        return fuelName;
    }

    public void setFuelName(String fuelName) {
        this.fuelName = fuelName;
    }

    public String getArchiveItem() {
        return archiveItem;
    }

    public void setArchiveItem(String archiveItem) {
        this.archiveItem = archiveItem;
    }

    public String getEngineState() {
        return engineState;
    }

    public void setEngineState(String engineState) {
        this.engineState = engineState;
    }

    public String getSteeringState() {
        return steeringState;
    }

    public void setSteeringState(String steeringState) {
        this.steeringState = steeringState;
    }

    public String getShiftGearsState() {
        return shiftGearsState;
    }

    public void setShiftGearsState(String shiftGearsState) {
        this.shiftGearsState = shiftGearsState;
    }

    public String getPowerState() {
        return powerState;
    }

    public void setPowerState(String powerState) {
        this.powerState = powerState;
    }

    public String getBrakeState() {
        return brakeState;
    }

    public void setBrakeState(String brakeState) {
        this.brakeState = brakeState;
    }

    public String getElectricState() {
        return electricState;
    }

    public void setElectricState(String electricState) {
        this.electricState = electricState;
    }

    public String getAirConditioningState() {
        return airConditioningState;
    }

    public void setAirConditioningState(String airConditioningState) {
        this.airConditioningState = airConditioningState;
    }

    public String getIndoorState() {
        return indoorState;
    }

    public void setIndoorState(String indoorState) {
        this.indoorState = indoorState;
    }

    public String getInspectComment() {
        return inspectComment;
    }

    public void setInspectComment(String inspectComment) {
        this.inspectComment = inspectComment;
    }

    public String getChangeContext() {
        return changeContext;
    }

    public void setChangeContext(String changeContext) {
        this.changeContext = changeContext;
    }

    public String getCarEvalLayoutImage() {
        return carEvalLayoutImage;
    }

    public void setCarEvalLayoutImage(String carEvalLayoutImage) {
        this.carEvalLayoutImage = carEvalLayoutImage;
    }

    public List<AuctionEntryInformationCarImageListResult> getCarImageList() {
        return carImageList;
    }

    public void setCarImageList(List<AuctionEntryInformationCarImageListResult> carImageList) {
        this.carImageList = carImageList;
    }

    public String getTtsFilePath() {
        return ttsFilePath;
    }

    public void setTtsFilePath(String ttsFilePath) {
        this.ttsFilePath = ttsFilePath;
    }

    public String getTtsDuration() {
        return ttsDuration;
    }

    public void setTtsDuration(String duration) {
        this.ttsDuration = duration;
    }

    public String getFlagChangeInfo() {
        return flagChangeInfo;
    }

    public void setFlagChangeInfo(String flagChangeInfo) {
        this.flagChangeInfo = flagChangeInfo;
    }

    public String getFlagCancelEntry() {
        return flagCancelEntry;
    }

    public void setFlagCancelEntry(String flagCancelEntry) {
        this.flagCancelEntry = flagCancelEntry;
    }

    public String getDisplacement() {
        return displacement;
    }

    public void setDisplacement(String mDisplacement) {
        this.displacement = mDisplacement;
    }

    public String getExhiCustno() {
        return exhiCustno;
    }

    public void setExhiCustno(String exhiCustno) {
        this.exhiCustno = exhiCustno;
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

    /**
     * 경매 출품 차량 정보 중 클라이언트로 전달해야할 데이터만 간추려 Netty로 보낼 수 있도록 ResponseCarInfo 객체로 변경한다.
     * 
     * @return 경매 출품 차량 정보 중 필요 정보만 추출된 ResponseCarInfo 객체
     */
    public ResponseEntryInfo toResponseCarInfo() {
        // TODO: 차량 엔트리 정보에서 자동차 타입으로 필요한 데이터만 세팅해주기
        ResponseEntryInfo carInfo = new ResponseEntryInfo();
        carInfo.setProductCode(productCode);
        carInfo.setAuctionCode(auctionCode);
        carInfo.setAuctionRound(auctionRound);
        carInfo.setAuctionEntryNum(auctionEntryNum);
        carInfo.setAuctionLaneName(auctionLaneName);
        carInfo.setAuctionLaneCode(auctionLaneCode);
        carInfo.setAuctionLanePort(auctionLanePort);
        carInfo.setAuctionPositionName(auctionPositionName);
        carInfo.setAuctionPositionCode(auctionPositionCode);
        carInfo.setExhibitorNum(exhibitorNum);
        carInfo.setExhibitorName(exhibitorName);
        carInfo.setExhibitorTypeCode(exhibitorTypeCode);
        carInfo.setExhibitorTypeName(exhibitorTypeName);
        carInfo.setVendorName(venderName);
        carInfo.setCarName(carName);
        carInfo.setCarNumber(carNumber);
        carInfo.setUseTypeName(useTypeName);
        carInfo.setOwnerTypeName(ownerTypeName);
        carInfo.setAbsenteePrice(absenteePrice);
        carInfo.setAuctionStartPrice(auctionStartPrice);
        carInfo.setAuctionHopePrice(auctionHopePrice);
        carInfo.setAuctionSeqNum(auctionEntryNumSeq);
        carInfo.setEvalPoint1(evalPoint1);
        carInfo.setEvalPoint2(evalPoint2);
        carInfo.setCarYear(carYear);
        carInfo.setMileage(mileage);
        carInfo.setMissionName(missionName);
        carInfo.setFuelName(fuelName);
        carInfo.setDisplacement(displacement);
        carInfo.setArchiveItem(archiveItem);
        
        carInfo.setEngineState(engineState);
        carInfo.setSteeringState(steeringState);
        carInfo.setShiftGearsState(shiftGearsState);
        carInfo.setPowerState(powerState);
        carInfo.setBrakeState(brakeState);
        carInfo.setElectricState(electricState);
        carInfo.setAirConditioningState(airConditioningState);
        carInfo.setIndoorState(indoorState);

        carInfo.setChangeContext(changeContext);
        carInfo.setInspectComment(inspectComment);
        carInfo.setCarEvalLayoutImage(carEvalLayoutImage);
        carInfo.setCarImageList(getCarImageUrl());
        carInfo.setTtsFilePath(ttsFilePath);
        carInfo.setTtsDuration(ttsDuration);
        carInfo.setFlagChangeInfo(flagChangeInfo);
        carInfo.setFlagCancelEntry(flagCancelEntry);
        carInfo.setExhiCustno(exhiCustno);

        carInfo.setNextEntryNum(mNextEntryNum);
        carInfo.setNextEntrySeqNum(mNextEntrySeqNum);
        carInfo.setNextEntryPositionCode(mNextEntryPositionCode);
        carInfo.setNextEntryCarName(mNextEntryCarInfo);
        carInfo.setNextEntryTtsInfo(mNextEntryTtsInfo);
        carInfo.setNextEntryImageInfo(mNextEntryImageInfo);
        carInfo.setNextEntryEvalImageInfo(mNextEntryEvalImageInfo);

        return carInfo;
    }

    /**
     * 
     * @MethodName getCarImageUrl
     * @Description 차량 이미지 FileName List > String
     * 
     * @return
     */
    public String getCarImageUrl() {
        String fileUrl = "";
        for (int i = 0; i < carImageList.size(); i++) {
            if (carImageList.get(i).getImageFileName() != null && carImageList.get(i).getImageFileName().length() > 0) {
                fileUrl = fileUrl + carImageList.get(i).getImageFileName();
                if (i < (carImageList.size() - 1)) {
                    fileUrl = fileUrl + ",";
                }
            }
        }
        return fileUrl;
    }
}
