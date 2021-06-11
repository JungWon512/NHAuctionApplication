package com.nh.share.api.model;

import java.util.List;

/**
 * 
 * 관심 차량 정보 조회 요청 내부 객체
 * 
 * @see {ResponseAuctionInterestEntryInfo}
 *
 */
public class AuctionInterestEntryInfoResult {
    private String productCode; // 상품 번호
    private String auctionCode; // 경매 구분 코드
    private String auctionRound; // 경매 회차
    private String auctionEntryNum; // 경매 출품 번호
    private List<AuctionInterestEntryFavoriteUserInfo> favoriteUserInfo; // 관심차량 회원 리스트

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

    public List<AuctionInterestEntryFavoriteUserInfo> getFavoriteUserInfo() {
        return favoriteUserInfo;
    }

    public void setFavoriteUserInfo(List<AuctionInterestEntryFavoriteUserInfo> favoriteUserInfo) {
        this.favoriteUserInfo = favoriteUserInfo;
    }
}
