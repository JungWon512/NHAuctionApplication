package com.nh.share.api.model;

/**
 * 
 * 경매 일정 정보 조회 요청 응답 내부 객체
 * 
 * @see {ResponseAuctionScheduleInfo}
 *
 */
public class AuctionScheduleInfoResult {
    private String displayDate; // 경매 예정 일자

    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }
}
