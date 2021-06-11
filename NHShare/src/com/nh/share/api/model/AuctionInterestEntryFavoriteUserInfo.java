package com.nh.share.api.model;

/**
 * 
 * 관심 차량 정보 조회 요청 관심차량 회원 리스트 내부 객체
 * 
 * @see {AuctionInterestEntryInfoResult}
 *
 */
public class AuctionInterestEntryFavoriteUserInfo {
    private String auctionMemberId;

    public AuctionInterestEntryFavoriteUserInfo(String memberId) {
        auctionMemberId = memberId;
    }

    public String getAuctionMemberId() {
        return auctionMemberId;
    }

    public void setAuctionMemberId(String auctionMemberId) {
        this.auctionMemberId = auctionMemberId;
    }

}
