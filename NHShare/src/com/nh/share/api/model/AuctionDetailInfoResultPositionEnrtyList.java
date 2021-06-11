package com.nh.share.api.model;

/**
 * 
 * 경매 상세 일정 정보 조회 요청 응답 내부 경매 거점별 출품 차량 수 객체
 * 
 * @see {AuctionDetailInfoResult}
 *
 */
public class AuctionDetailInfoResultPositionEnrtyList {
    private String auctionPositionEntryName; // 경매 거점명
    private String auctionPositionEntryCount; // 출품차량 수

    public String getAuctionPositionEntryName() {
        return auctionPositionEntryName;
    }

    public void setAuctionPositionEntryName(String auctionPositionEntryName) {
        this.auctionPositionEntryName = auctionPositionEntryName;
    }

    public String getAuctionPositionEntryCount() {
        return auctionPositionEntryCount;
    }

    public void setAuctionPositionEntryCount(String auctionPositionEntryCount) {
        this.auctionPositionEntryCount = auctionPositionEntryCount;
    }

}
