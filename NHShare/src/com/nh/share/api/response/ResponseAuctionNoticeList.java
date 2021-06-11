package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.AuctionNoticeListResult;

/**
 * 경매 공지 사항 목록 조회 요청 응답 객체
 * 
 * @see {ActionRequestAuctionNoticeList}
 *
 */
public class ResponseAuctionNoticeList extends BaseResponse {
    private List<AuctionNoticeListResult> result; // 응답 결과
    private String noticeMoreUrl; // 공지사항 더보기 URL
    private String mobileNoticeMoreUrl; // 모바일 공지사항 더보기 URL

    public List<AuctionNoticeListResult> getResult() {
        return result;
    }

    public void setResult(List<AuctionNoticeListResult> result) {
        this.result = result;
    }

    public String getNoticeMoreUrl() {
        return noticeMoreUrl;
    }

    public void setNoticeMoreUrl(String noticeMoreUrl) {
        this.noticeMoreUrl = noticeMoreUrl;
    }

    public String getMobileNoticeMoreUrl() {
        return mobileNoticeMoreUrl;
    }

    public void setMobileNoticeMoreUrl(String mobileNoticeMoreUrl) {
        this.mobileNoticeMoreUrl = mobileNoticeMoreUrl;
    }
}
