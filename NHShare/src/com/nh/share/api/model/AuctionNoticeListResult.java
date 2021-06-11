package com.nh.share.api.model;

/**
 * 
 * 경매 공지 사항 목록 조회 요청 응답 내부 객체
 * 
 * @see {ResponseAuctionNoticeList}
 *
 */
public class AuctionNoticeListResult {
    private String noticeTitle; // 공지사항 제목
    private String noticeUrl; // 공지사항 상세 URL
    private String noticeRegDate; // 공지사항 등록 일자
    private String mobileNoticeUrl; // 모바일 공지사항 상세 URL

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeUrl() {
        return noticeUrl;
    }

    public void setNoticeUrl(String noticeUrl) {
        this.noticeUrl = noticeUrl;
    }

    public String getNoticeRegDate() {
        return noticeRegDate;
    }

    public void setNoticeRegDate(String noticeRegDate) {
        this.noticeRegDate = noticeRegDate;
    }

    public String getMobileNoticeUrl() {
        return mobileNoticeUrl;
    }

    public void setMobileNoticeUrl(String mobileNoticeUrl) {
        this.mobileNoticeUrl = mobileNoticeUrl;
    }
}
