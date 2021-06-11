package com.nh.share.api.model;

public class AuctionResult {
    private String mAuctionCode; // 경매 구분 코드
    private String mAuctionRound; // 경매 회차
    private String mAuctionLaneCode; // 경매 레인 코드
    private String mAuctionEntryNum; // 경매 출품 번호
    private String mProductCode; // 상품 번호
    private String mAuctionResultCode; // 경매 결과 코드
    private String mAuctionResultDateTime; // 낙/유찰 일시
    private String mSuccessBidMemberNum; // 낙찰회원번호
    private String mSuccessBidChannel; // 낙찰회원응찰채널
    private String mSuccessBidPrice; // 낙찰금액
    private String mCarName; // 차량명
    private String mHopePrice; // 희망가
    private String mHightPrice; // 최고가
    private String mRank1MemberNum; // 1순위 회원번호
    private String mRank1BidPrice; // 1순위 응찰금액
    private String mRank2MemberNum; // 2순위 회원번호
    private String mRank2BidPrice; // 2순위 응찰금액
    private String mRank3MemberNum; // 3순위 회원번호
    private String mRank3BidPrice; // 3순위 응찰금액
    private String mRank4MemberNum; // 4순위 회원번호
    private String mRank4BidPrice; // 4순위 응찰금액
    private String mRank5MemberNum; // 5순위 회원번호
    private String mRank5BidPrice; // 5순위 응찰금액]

    public AuctionResult(String auctionCode, String auctionRound, String auctionLaneCode, String auctionEntryNum,
            String productCode, String auctionResultCode, String auctionResultDateTime, String successBidMemberNum,
            String successBidPrice, String successBidChannel, String carName, String hopePrice, String hightPrice,
            String rank1MemberNum, String rank1BidPrice, String rank2MemberNum, String rank2BidPrice,
            String rank3MemberNum, String rank3BidPrice, String rank4MemberNum, String rank4BidPrice,
            String rank5MemberNum, String rank5BidPrice) {
        this.mAuctionCode = auctionCode;
        this.mAuctionRound = auctionRound;
        this.mAuctionLaneCode = auctionLaneCode;
        this.mAuctionEntryNum = auctionEntryNum;
        this.mProductCode = productCode;
        this.mAuctionResultCode = auctionResultCode;
        this.mAuctionResultDateTime = auctionResultDateTime;
        this.mSuccessBidMemberNum = successBidMemberNum;
        this.mSuccessBidChannel = successBidChannel;
        this.mSuccessBidPrice = successBidPrice;
        this.mCarName = carName;
        this.mHopePrice = hopePrice;
        this.mHightPrice = hightPrice;
        this.mRank1MemberNum = rank1MemberNum;
        this.mRank1BidPrice = rank1BidPrice;
        this.mRank2MemberNum = rank2MemberNum;
        this.mRank2BidPrice = rank2BidPrice;
        this.mRank3MemberNum = rank3MemberNum;
        this.mRank3BidPrice = rank3BidPrice;
        this.mRank4MemberNum = rank4MemberNum;
        this.mRank4BidPrice = rank4BidPrice;
        this.mRank5MemberNum = rank5MemberNum;
        this.mRank5BidPrice = rank5BidPrice;
    }

    public String getAuctionCode() {
        return mAuctionCode;
    }

    public void setAuctionCode(String auctionCode) {
        this.mAuctionCode = auctionCode;
    }

    public String getAuctionRound() {
        return mAuctionRound;
    }

    public void setAuctionRound(String auctionRound) {
        this.mAuctionRound = auctionRound;
    }

    public String getAuctionLaneCode() {
        return mAuctionLaneCode;
    }

    public void setAuctionLaneCode(String auctionLaneCode) {
        this.mAuctionLaneCode = auctionLaneCode;
    }

    public String getAuctionEntryNum() {
        return mAuctionEntryNum;
    }

    public void setAuctionEntryNum(String auctionEntryNum) {
        this.mAuctionEntryNum = auctionEntryNum;
    }

    public String getProductCode() {
        return mProductCode;
    }

    public void setProductCode(String productCode) {
        this.mProductCode = productCode;
    }

    public String getAuctionResultCode() {
        return mAuctionResultCode;
    }

    public void setAuctionResultCode(String auctionResultCode) {
        this.mAuctionResultCode = auctionResultCode;
    }

    public String getAuctionResultDateTime() {
        return mAuctionResultDateTime;
    }

    public void setAuctionResultDateTime(String auctionResultDateTime) {
        this.mAuctionResultDateTime = auctionResultDateTime;
    }

    public String getSuccessBidMemberNum() {
        return mSuccessBidMemberNum;
    }

    public void setSuccessBidMemberNum(String successBidMemberNum) {
        this.mSuccessBidMemberNum = successBidMemberNum;
    }

    public String getSuccessBidChannel() {
        return mSuccessBidChannel;
    }

    public void setSuccessBidChannel(String successBidChannel) {
        this.mSuccessBidChannel = successBidChannel;
    }

    public String getSuccessBidPrice() {
        return mSuccessBidPrice;
    }

    public void setSuccessBidPrice(String successBidPrice) {
        this.mSuccessBidPrice = successBidPrice;
    }

    public String getCarName() {
        return mCarName;
    }

    public void setCarName(String carName) {
        this.mCarName = carName;
    }

    public String getHopePrice() {
        return mHopePrice;
    }

    public void setHopePrice(String hopePrice) {
        this.mHopePrice = hopePrice;
    }

    public String getHightPrice() {
        return mHightPrice;
    }

    public void setHightPrice(String hightPrice) {
        this.mHightPrice = hightPrice;
    }

    public String getRank1MemberNum() {
        return mRank1MemberNum;
    }

    public void setRank1MemberNum(String rank1MemberNum) {
        this.mRank1MemberNum = rank1MemberNum;
    }

    public String getRank1BidPrice() {
        return mRank1BidPrice;
    }

    public void setRank1BidPrice(String rank1BidPrice) {
        this.mRank1BidPrice = rank1BidPrice;
    }

    public String getRank2MemberNum() {
        return mRank2MemberNum;
    }

    public void setRank2MemberNum(String rank2MemberNum) {
        this.mRank2MemberNum = rank2MemberNum;
    }

    public String getRank2BidPrice() {
        return mRank2BidPrice;
    }

    public void setRank2BidPrice(String rank2BidPrice) {
        this.mRank2BidPrice = rank2BidPrice;
    }

    public String getRank3MemberNum() {
        return mRank3MemberNum;
    }

    public void setRank3MemberNum(String rank3MemberNum) {
        this.mRank3MemberNum = rank3MemberNum;
    }

    public String getRank3BidPrice() {
        return mRank3BidPrice;
    }

    public void setRank3BidPrice(String rank3BidPrice) {
        this.mRank3BidPrice = rank3BidPrice;
    }

    public String getRank4MemberNum() {
        return mRank4MemberNum;
    }

    public void setRank4MemberNum(String rank4MemberNum) {
        this.mRank4MemberNum = rank4MemberNum;
    }

    public String getRank4BidPrice() {
        return mRank4BidPrice;
    }

    public void setRank4BidPrice(String rank4BidPrice) {
        this.mRank4BidPrice = rank4BidPrice;
    }

    public String getRank5MemberNum() {
        return mRank5MemberNum;
    }

    public void setRank5MemberNum(String rank5MemberNum) {
        this.mRank5MemberNum = rank5MemberNum;
    }

    public String getRank5BidPrice() {
        return mRank5BidPrice;
    }

    public void setRank5BidPrice(String rank5BidPrice) {
        this.mRank5BidPrice = rank5BidPrice;
    }

}
