package com.nh.controller.model;

/**
 * 
 * @ClassName AuctionListViewCellItem.java
 * @Description 경매 선택 목록 화면에 ListView Cell Data Model
 * @anthor ishift
 * @since
 */
public class AuctionListViewCellItem {
    private String mAuctionType; // 경매 타입 (실시간/SPOT)
    private String mAuctionLaneName; // 경매 레인 이름
    private String mAuctionName; // 경매명
    private String mAuctionRound; // 경매 회차 
    private String mAuctionDate; // 경매 날짜 
    private String mAuctionTime; // 경매 시간 
    
    public AuctionListViewCellItem (
            String mAuctionType,
            String mAuctionLaneName,
            String mAuctionName,
            String mAuctionRound,
            String mAuctionDate,
            String mAuctionTime) {
        super();
        this.mAuctionType = mAuctionType;
        this.mAuctionLaneName = mAuctionLaneName;
        this.mAuctionName = mAuctionName;
        this.mAuctionRound = mAuctionRound;
        this.mAuctionDate = mAuctionDate;
        this.mAuctionTime = mAuctionTime;
    }

    public String getAuctionType() {
        return mAuctionType;
    }

    public String getAuctionLaneName() {
        return mAuctionLaneName;
    }

    public String getAuctionName() {
        return mAuctionName+" - "+mAuctionRound+"회차";
    }

    public String getAuctionDate() {
        return mAuctionDate;
    }

    public String getAuctionRound() {
        return mAuctionRound;
    }

    public String getAuctionTime() {
        return mAuctionTime;
    }
}
