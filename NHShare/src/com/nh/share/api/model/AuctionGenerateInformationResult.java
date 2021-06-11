package com.nh.share.api.model;

/**
 * 경매 생성 정보 조회 요청 응답 내부 객체
 * 
 * @see {ResponseSaveAuctionSetting}
 *
 */
public class AuctionGenerateInformationResult {
    private String auctionCode; // 경매 구분 코드 (실시간 경매 : 20 / 스팟경매 : 40)
    private String auctionLaneCode; // 경매 레인 코드
    private String auctionName; // 경매명
    private String auctionRound; // 경매 회차
    private String auctionLaneName; // 경매 레인명
    private String auctionLanePort; // 경매 레인 포트
    private String auctionStatus; // 경매 상태 (10:대기/11:진행/20:완료)
    private String auctionLaneEntryCount; // 경매 레인 출품 수량
    private String auctionDate; // 경매 날짜
    private String auctionTime; // 경매 시간
    private String ttsFailExhino; // TTS 생성 실패 출품번호
    private String ttsFailCnt; // TTS 생성 실패 건수

    public String getAuctionCode() {
        return auctionCode;
    }

    public void setAuctionCode(String auctionCode) {
        this.auctionCode = auctionCode;
    }

    public String getAuctionLaneCode() {
        return auctionLaneCode;
    }

    public void setAuctionLaneCode(String auctionLaneCode) {
        this.auctionLaneCode = auctionLaneCode;
    }

    public String getAuctionName() {
        return auctionName;
    }

    public void setAuctionName(String auctionName) {
        this.auctionName = auctionName;
    }

    public String getAuctionRound() {
        return auctionRound;
    }

    public void setAuctionRound(String auctionRound) {
        this.auctionRound = auctionRound;
    }

    public String getAuctionLaneName() {
        return auctionLaneName;
    }

    public void setAuctionLaneName(String auctionLaneName) {
        this.auctionLaneName = auctionLaneName;
    }

    public String getAuctionLanePort() {
        return auctionLanePort;
    }

    public void setAuctionLanePort(String auctionLanePort) {
        this.auctionLanePort = auctionLanePort;
    }

    public String getAuctionStatus() {
        return auctionStatus;
    }

    public void setAuctionStatus(String auctionStatus) {
        this.auctionStatus = auctionStatus;
    }

    public String getAuctionLaneEntryCount() {
        return auctionLaneEntryCount;
    }

    public void setAuctionLaneEntryCount(String auctionLaneEntryCount) {
        this.auctionLaneEntryCount = auctionLaneEntryCount;
    }

    public String getAuctionDate() {
        return auctionDate;
    }

    public void setAuctionDate(String auctionDate) {
        this.auctionDate = auctionDate;
    }

    public String getAuctionTime() {
        return auctionTime;
    }

    public void setAuctionTime(String auctionTime) {
        this.auctionTime = auctionTime;
    }

    public String getTtsFailExhino() {
        return ttsFailExhino;
    }

    public void setTtsFailExhino(String ttsFailExhino) {
        this.ttsFailExhino = ttsFailExhino;
    }

    public String getTtsFailCnt() {
        return ttsFailCnt;
    }

    public void setTtsFailCnt(String ttsFailCnt) {
        this.ttsFailCnt = ttsFailCnt;
    }
}
