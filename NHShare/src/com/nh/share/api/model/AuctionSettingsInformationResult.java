package com.nh.share.api.model;

/**
 * 
 * 경매 환경 설정 정보 조회 요청 응답 내부 객체
 * 
 * @see {ResponseAuctionSettingsInformation}
 *
 */
public class AuctionSettingsInformationResult {
    private String auctionCode; // 경매 구분 코드
    private String auctionRound; // 경매 회차
    private String auctionLaneCode; // 경매 레인 코드
    private String auctionBasePrice; // 기준 금액
    private String auctionBelowRisingPrice; // 기준 금액 이하 상승가
    private String auctionMoreRisingPrice; // 기준 금액 이상 상승가
    private String auctionMaxRisingPrice; // 기준 금액 1억 이상 상승가
    private String auctionEntryTime; // 경매 진행 시간(ms)
    private String succBidDelayTime; // 낙찰 지연 시간(ms)
    private String auctionNextEntryTime; // 다음 시작 간격(ms)
    private String auctionAutoRiseCount; // 자동 상승 횟수
    private String totalEntryCount; // 경매 출품 총수

    public AuctionSettingsInformationResult(String auctionCode, String auctionRound, String auctionLaneCode, String auctionBasePrice,
            String auctionBelowRisingPrice, String auctionMoreRisingPrice, String auctionMaxRisingPrice, String auctionEntryTime,
            String succBidDelayTime, String auctionNextEntryTime, String auctionAutoRiseCount, String totalEntryCount) {
        super();
        this.auctionCode = auctionCode;
        this.auctionRound = auctionRound;
        this.auctionLaneCode = auctionLaneCode;
        this.auctionBasePrice = auctionBasePrice;
        this.auctionBelowRisingPrice = auctionBelowRisingPrice;
        this.auctionMoreRisingPrice = auctionMoreRisingPrice;
        this.auctionMaxRisingPrice = auctionMaxRisingPrice;
        this.auctionEntryTime = auctionEntryTime;
        this.succBidDelayTime = succBidDelayTime;
        this.auctionNextEntryTime = auctionNextEntryTime;
        this.auctionAutoRiseCount = auctionAutoRiseCount;
        this.totalEntryCount = totalEntryCount;
    }

    public String getTotalEntryCount() {
        return totalEntryCount;
    }

    public void setTotalEntryCount(String totalEntryCount) {
        this.totalEntryCount = totalEntryCount;
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

    public String getAuctionLaneCode() {
        return auctionLaneCode;
    }

    public void setAuctionLaneCode(String auctionLaneCode) {
        this.auctionLaneCode = auctionLaneCode;
    }

    public String getAuctionBasePrice() {
        return auctionBasePrice;
    }

    public void setAuctionBasePrice(String auctionBasePrice) {
        this.auctionBasePrice = auctionBasePrice;
    }

    public String getAuctionBelowRisingPrice() {
        return auctionBelowRisingPrice;
    }

    public void setAuctionBelowRisingPrice(String auctionBelowRisingPrice) {
        this.auctionBelowRisingPrice = auctionBelowRisingPrice;
    }

    public String getAuctionMoreRisingPrice() {
        return auctionMoreRisingPrice;
    }

    public void setAuctionMoreRisingPrice(String auctionMoreRisingPrice) {
        this.auctionMoreRisingPrice = auctionMoreRisingPrice;
    }

    public String getAuctionMaxRisingPrice() {
        return auctionMaxRisingPrice;
    }

    public void setAuctionMaxRisingPrice(String auctionMaxRisingPrice) {
        this.auctionMaxRisingPrice = auctionMaxRisingPrice;
    }

    public String getAuctionEntryTime() {
        return auctionEntryTime;
    }

    public void setAuctionEntryTime(String auctionEntryTime) {
        this.auctionEntryTime = auctionEntryTime;
    }

    public String getSuccBidDelayTime() {
        return succBidDelayTime;
    }

    public void setSuccBidDelayTime(String succBidDelayTime) {
        this.succBidDelayTime = succBidDelayTime;
    }

    public String getAuctionNextEntryTime() {
        return auctionNextEntryTime;
    }

    public void setAuctionNextEntryTime(String auctionNextEntryTime) {
        this.auctionNextEntryTime = auctionNextEntryTime;
    }

    public String getAuctionAutoRiseCount() {
        return auctionAutoRiseCount;
    }

    public void setAuctionAutoRiseCount(String auctionAutoRiseCount) {
        this.auctionAutoRiseCount = auctionAutoRiseCount;
    }
}
