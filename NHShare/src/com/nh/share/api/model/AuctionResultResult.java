package com.nh.share.api.model;

/**
 * 
 * 경매 공지 사항 목록 조회 요청 응답 내부 객체
 * 
 * @see {ResponseAuctionResult}
 *
 */
public class AuctionResultResult {
    private String auctionCode; // 경매 구분 코드
    private String auctionRound; // 경매 회차
    private String auctionEntryNum; // 경매 출품 번호
    private String productCode; // 상품 번호
    private String auctionResultCode; // 경매 결과 코드
    private String auctionResultDateTime; // 낙유찰 일시
    private String successBidMemberNum; // 낙찰 회원 번호
    private String successBidPrice; // 낙찰 금액
    private String carName; // 차량명
    private String hopePrice; // 희망가
    private String hightPrice; // 최고가

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

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getAuctionResultCode() {
        return auctionResultCode;
    }

    public void setAuctionResultCode(String auctionResultCode) {
        this.auctionResultCode = auctionResultCode;
    }

    public String getAuctionResultDateTime() {
        return auctionResultDateTime;
    }

    public void setAuctionResultDateTime(String auctionResultDateTime) {
        this.auctionResultDateTime = auctionResultDateTime;
    }

    public String getSuccessBidMemberNum() {
        return successBidMemberNum;
    }

    public void setSuccessBidMemberNum(String successBidMemberNum) {
        this.successBidMemberNum = successBidMemberNum;
    }

    public String getSuccessBidPrice() {
        return successBidPrice;
    }

    public void setSuccessBidPrice(String successBidPrice) {
        this.successBidPrice = successBidPrice;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getHopePrice() {
        return hopePrice;
    }

    public void setHopePrice(String hopePrice) {
        this.hopePrice = hopePrice;
    }

    public String getHightPrice() {
        return hightPrice;
    }

    public void setHightPrice(String hightPrice) {
        this.hightPrice = hightPrice;
    }
}
