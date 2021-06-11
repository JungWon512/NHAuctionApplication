package com.nh.share.api.model;

/**
 * 
 * 경매 상세 일정 정보 조회 요청 응답 내부 경매 리스트 객체
 * 
 * @see {AuctionDetailInfoResult}
 *
 */
public class AuctionDetailInfoResultAuctionList {
    private String auctionCode; // 경매 구분 코드
    private String auctionLaneName; // 경매 레인명
    private String auctionLaneCode; // 경매 레인코드
    private String auctionLanePort; // 경매 레인 포트
    private String auctionStatus; // 경매 상태
    private String auctionLaneEntryCount; // 경매 레인 출품 수량

    public String getAuctionCode() {
        return auctionCode;
    }

    public void setAuctionCode(String auctionCode) {
        this.auctionCode = auctionCode;
    }

    public String getAuctionLaneName() {
        return auctionLaneName;
    }

    public void setAuctionLaneName(String auctionLaneName) {
        this.auctionLaneName = auctionLaneName;
    }

    public String getAuctionLanePort() {
        return auctionLanePort;
    }

    public void setAuctionLanePort(String auctionLanePort) {
        this.auctionLanePort = auctionLanePort;
    }

    public String getAuctionStatus() {
        return auctionStatus;
    }

    public void setAuctionStatus(String auctionStatus) {
        this.auctionStatus = auctionStatus;
    }

    public String getAuctionLaneEntryCount() {
        return auctionLaneEntryCount;
    }

    public void setAuctionLaneEntryCount(String auctionLaneEntryCount) {
        this.auctionLaneEntryCount = auctionLaneEntryCount;
    }

    public String getAuctionLaneCode() {
        return auctionLaneCode;
    }

    public void setAuctionLaneCode(String auctionLaneCode) {
        this.auctionLaneCode = auctionLaneCode;
    }

}
