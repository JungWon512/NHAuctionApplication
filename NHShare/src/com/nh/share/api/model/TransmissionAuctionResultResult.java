package com.nh.share.api.model;

/**
 * 
 * 경매 낙유찰 결과 정보 요청 응답 내부 객체
 * 
 * @see {ResponseTransmissionAuctionResult}
 *
 */
public class TransmissionAuctionResultResult {
    private String transmissionEntryNum; // 전송 출품 번호 결과
    private String transmissionResultCode; // 전송 결과 코드

    public String getTransmissionEntryNum() {
        return transmissionEntryNum;
    }

    public void setTransmissionEntryNum(String transmissionEntryNum) {
        this.transmissionEntryNum = transmissionEntryNum;
    }

    public String getTransmissionResultCode() {
        return transmissionResultCode;
    }

    public void setTransmissionResultCode(String transmissionResultCode) {
        this.transmissionResultCode = transmissionResultCode;
    }
}
