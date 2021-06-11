package com.nh.share.api.model;

/**
 * 
 * 부재자 입찰 정보 조회 요청 응답 내부 객체
 * 
 * @see {ResponseAbsenteeAuctionBidInfo}
 */
public class AbsenteeAuctionBidInfoResult {
    private String auctionChannel; // 경매 시도 채널
    private String auctionMemberNum; // 경매 회원 번호
    private String bidPrice; // 경매 입찰 금액
    private String bidDateTime; // 경매 입찰 일시
    private String productCode; // 상품 번호
    private String auctionCode; // 경매 구분 코드
    private String auctionRound; // 경매 회차
    private String auctionEntryNum; // 경매 출품 번호
    private String exhinoseq; // 출품 순번

    public String getAuctionChannel() {
        return auctionChannel;
    }

    public void setAuctionChannel(String auctionChannel) {
        this.auctionChannel = auctionChannel;
    }

    public String getAuctionMemberNum() {
        return auctionMemberNum;
    }

    public void setAuctionMemberNum(String auctionMemberNum) {
        this.auctionMemberNum = auctionMemberNum;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getBidDateTime() {
        return bidDateTime;
    }

    public void setBidDateTime(String bidDateTime) {
        this.bidDateTime = bidDateTime;
    }

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

    public String getExhinoseq() {
        return exhinoseq;
    }

    public void setExhinoseq(String exhinoseq) {
        this.exhinoseq = exhinoseq;
    }
}
