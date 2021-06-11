package com.nh.share.api.model;

/**
 * 
 * 경매 서버 참여 가능 여부 요청 응답 내부 객체
 * 
 * @see {ResponseAuctionConnectionStatus}
 *
 */
public class AuctionJoinStatusResult {

    private String auctionConnctionFlag;

    public String getAuctionConnctionFlag() {
        return auctionConnctionFlag;
    }

    public void setAuctionConnctionFlag(String auctionConnctionFlag) {
        this.auctionConnctionFlag = auctionConnctionFlag;
    }

    
}
