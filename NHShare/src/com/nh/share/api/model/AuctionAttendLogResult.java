package com.nh.share.api.model;

/**
 * 
 * 경매 참여 이력 저장
 * 
 * @see {ResponseSaveAuctionAttendLog}
 *
 */
public class AuctionAttendLogResult {

    private String savingStatus;

    public String getSavingStatus() {
        return savingStatus;
    }
    public void setSavingStatus(String savingStatus) {
        this.savingStatus = savingStatus;
    }
}
