package com.nh.share.api.model;

/**
 * 응찰 프로그램 버전 확인 요청 응답 내부 객체
 * 
 * @see {ResponseAuctionImageNotice}
 *
 */
public class AuctionImageNoticeResult {
    
    private String noticeUrl;   //공지사항 이미지 랜딩 URL
    private String imageUrl;  //공지사항 이미지 URL
    
    public String getNoticeUrl() {
        return noticeUrl;
    }
    public void setNoticeUrl(String noticeUrl) {
        this.noticeUrl = noticeUrl;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
