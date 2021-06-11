package com.nh.share.api.model;

/**
 * 경매 당월 누적 정보 조회 요청 응답 내부 객체
 * 
 * @see {ResponseAuctionMonthInfo}
 *
 */
public class AuctionMonthInfoResult {
    private String totalEntryCount; // 총 출품 차량수
    private String avgSuccBidRate; // 평균 낙찰율
    private String avgBidderCount; // 평균 참가 회원

    public String getTotalEntryCount() {
        return totalEntryCount;
    }

    public void setTotalEntryCount(String totalEntryCount) {
        this.totalEntryCount = totalEntryCount;
    }

    public String getAvgSuccBidRate() {
        return avgSuccBidRate;
    }

    public void setAvgSuccBidRate(String avgSuccBidRate) {
        this.avgSuccBidRate = avgSuccBidRate;
    }

    public String getAvgBidderCount() {
        return avgBidderCount;
    }

    public void setAvgBidderCount(String avgBidderCount) {
        this.avgBidderCount = avgBidderCount;
    }
}
