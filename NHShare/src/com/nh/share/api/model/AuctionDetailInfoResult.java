package com.nh.share.api.model;

import java.util.List;

/**
 * 
 * 경매 상세 일정 정보 조회 요청 응답 내부 객체
 * 
 * @see {ResponseAuctionDetailInfo}
 *
 */
public class AuctionDetailInfoResult {
    private String auctionType; // 경매 종류 (실시간,스팟,지정시간)
    private String auctionName; // 경매명
    private String auctionRound; // 경매 회차
    private String auctionDate; // 경매 날짜
    private String auctionTime; // 경매 시간
    private String auctionPositionName; // 경매 거점명
    private String auctionPositionCode; // 경매 거점 코드
    private String auctionFinishDate; // 경매 종료 날짜 (지정시간 경매의 경우 해당)
    private String auctionFinishTime; // 경매 종료 시간 (지정시간 경매의 경우 해당)
    private String auctionRemainTime; // 경매 종료 남은 시간 (지정시간 경매의 경우 해당)
    private String flagAbsenteeBid; // 부재자 입찰 가능 여부
    private String absenteeBidUrl; // 부재자 입찰 URL
    private String flagConsultBid; // 후상담 입찰 가능 여부
    private String consultBidUrl; // 후상담 입찰 URL
    private String flagFixedTimeBid; // 지정시간 입찰 가능 여부
    private String fixedTimeBidUrl; // 지정시간 입찰 URL
    private String auctionEntryListUrl; // 출품 목록 URL
    private String auctionJoinFlag; // 경매 참여 가능 여부
    private List<AuctionDetailInfoResultAuctionList> auctionList; // 경매 리스트
    private String hashTagText; // 해시 태그 문구
    private String totalEntryCount; // 경매 출품 총수
    private String auctionTodayFlag; // 오늘 경매 여부
    private List<AuctionDetailInfoResultPositionEnrtyList> auctionPositionEntry; // 경매 거점별 출품 차량 수 리스트

    public String getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(String auctionType) {
        this.auctionType = auctionType;
    }

    public List<AuctionDetailInfoResultAuctionList> getAuctionList() {
        return auctionList;
    }

    public void setAuctionList(List<AuctionDetailInfoResultAuctionList> auctionList) {
        this.auctionList = auctionList;
    }

    public String getHashTagText() {
        return hashTagText;
    }

    public void setHashTagText(String hashTagText) {
        this.hashTagText = hashTagText;
    }

    public String getTotalEntryCount() {
        return totalEntryCount;
    }

    public void setTotalEntryCount(String totalEntryCount) {
        this.totalEntryCount = totalEntryCount;
    }

    public String getAuctionName() {
        return auctionName;
    }

    public void setAuctionName(String auctionName) {
        this.auctionName = auctionName;
    }

    public String getAuctionDate() {
        return auctionDate;
    }

    public void setAuctionDate(String auctionDate) {
        this.auctionDate = auctionDate;
    }

    public String getAuctionRound() {
        return auctionRound;
    }

    public void setAuctionRound(String auctionRound) {
        this.auctionRound = auctionRound;
    }

    public String getAuctionTime() {
        return auctionTime;
    }

    public void setAuctionTime(String auctionTime) {
        this.auctionTime = auctionTime;
    }

    public String getAuctionPositionName() {
        return auctionPositionName;
    }

    public void setAuctionPositionName(String auctionPositionName) {
        this.auctionPositionName = auctionPositionName;
    }

    public String getAuctionPositionCode() {
        return auctionPositionCode;
    }

    public void setAuctionPositionCode(String auctionPositionCode) {
        this.auctionPositionCode = auctionPositionCode;
    }

    public String getAuctionFinishTime() {
        return auctionFinishTime;
    }

    public void setAuctionFinishTime(String auctionFinishTime) {
        this.auctionFinishTime = auctionFinishTime;
    }

    public String getFlagAbsenteeBid() {
        return flagAbsenteeBid;
    }

    public void setFlagAbsenteeBid(String flagAbsenteeBid) {
        this.flagAbsenteeBid = flagAbsenteeBid;
    }

    public String getFlagConsultBid() {
        return flagConsultBid;
    }

    public void setFlagConsultBid(String flagConsultBid) {
        this.flagConsultBid = flagConsultBid;
    }

    public String getAbsenteeBidUrl() {
        return absenteeBidUrl;
    }

    public void setAbsenteeBidUrl(String absenteeBidUrl) {
        this.absenteeBidUrl = absenteeBidUrl;
    }

    public String getConsultBidUrl() {
        return consultBidUrl;
    }

    public void setConsultBidUrl(String consultBidUrl) {
        this.consultBidUrl = consultBidUrl;
    }

    public String getFlagFixedTimeBid() {
        return flagFixedTimeBid;
    }

    public void setFlagFixedTimeBid(String flagFixedTimeBid) {
        this.flagFixedTimeBid = flagFixedTimeBid;
    }

    public String getFixedTimeBidUrl() {
        return fixedTimeBidUrl;
    }

    public void setFixedTimeBidUrl(String fixedTimeBidUrl) {
        this.fixedTimeBidUrl = fixedTimeBidUrl;
    }

    public String getAuctionEntryListUrl() {
        return auctionEntryListUrl;
    }

    public void setAuctionEntryListUrl(String auctionEntryListUrl) {
        this.auctionEntryListUrl = auctionEntryListUrl;
    }

    public String getAuctionJoinFlag() {
        return auctionJoinFlag;
    }

    public void setAuctionJoinFlag(String auctionJoinFlag) {
        this.auctionJoinFlag = auctionJoinFlag;
    }

    public List<AuctionDetailInfoResultPositionEnrtyList> getAuctionPositionEntry() {
        return auctionPositionEntry;
    }

    public void setAuctionPositionEntry(List<AuctionDetailInfoResultPositionEnrtyList> auctionPositionEntry) {
        this.auctionPositionEntry = auctionPositionEntry;
    }

    public String getAuctionFinishDate() {
        return auctionFinishDate;
    }

    public void setAuctionFinishDate(String auctionFinishDate) {
        this.auctionFinishDate = auctionFinishDate;
    }

    public String getAuctionRemainTime() {
        return auctionRemainTime;
    }

    public void setAuctionRemainTime(String auctionRemainTime) {
        this.auctionRemainTime = auctionRemainTime;
    }

    public String getAuctionTodayFlag() {
        return auctionTodayFlag;
    }

    public void setAuctionTodayFlag(String auctionTodayFlag) {
        this.auctionTodayFlag = auctionTodayFlag;
    }
    
}
