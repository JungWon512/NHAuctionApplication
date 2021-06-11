package com.nh.share.api.model;

/**
 * 경매 진행 상태 업데이트 요청 응답 내부 객체
 * 
 * @see {ResponseUpdateAuctionStatus}
 *
 */
public class UpdateAuctionStatusResult {
    private String updateResult; // 경매 상태 반영 결과
    private String auctionCode; // 경매 상태 반영 결매 구분 코드
    private String auctionRound; // 경매 회차
    private String auctionLaneCode; // 경매 레인 코드

    public String getUpdateResult() {
        return updateResult;
    }

    public void setUpdateResult(String updateResult) {
        this.updateResult = updateResult;
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

}
