package com.nh.controller.model;

import com.nh.share.server.models.ResponseEntryInfo;

/**
 * 
 * @ClassName AuctionEntryInfo.java
 * @Description 경매 제어 화면에 출품목록 Data Model
 * @anthor ishift
 * @since
 */
public class AuctionEntryInfo {

    private String mAuctionSeqNum; // 경매 출품 순번
    private String mAuctionEntryNum; // 출품번호
    private String mCarName; // 차량명
    private String mChangeContext; // 변경사항
    private String mExhibitorName; // 출품자
    private String mCarYear; // 연식
    private String mEvalPoint; // 평가점
    private String mAuctionAbsenteePrice; // 부재자
    private String mAuctionStartPrice; // 시작가
    private String mAuctionHopePrice; // 희망가
    private String mCarImage; // 차량 이미지

    public AuctionEntryInfo(ResponseEntryInfo carInfo) {
        this.setAuctionSeqNum(carInfo.getAuctionSeqNum());
        this.setAuctionEntryNum(carInfo.getAuctionEntryNum());
        this.setCarName(carInfo.getCarName());
        this.setChangeContext(carInfo.getChangeContext());
        this.setExhibitorName(carInfo.getExhibitorName());
        this.setCarYear(carInfo.getCarYear());
        this.setEvalPoint(carInfo.getEvalPoint1(), carInfo.getEvalPoint2());
        this.setAuctionAbsenteePrice(carInfo.getAbsenteePrice());
        this.setAuctionStartPrice(carInfo.getAuctionStartPrice());
        this.setAuctionHopePrice(carInfo.getAuctionHopePrice());

        String stringCarImageList = carInfo.getCarImageList();
        if (stringCarImageList.length() > 0 && stringCarImageList.contains(",")) {
            String[] carImageList = stringCarImageList.split(",");
            if (carImageList.length > 0) {
                // stringCarImageList =
                // CommonUtils.getInstance().getImageFilePath(carInfo.getAuctionPositionCode(),
                // carImageList[0]);
                stringCarImageList = carImageList[0];
            }
        }
        this.setCarImage(stringCarImageList);
    }

    public String getAuctionEntryNum() {
        return mAuctionEntryNum;
    }

    public void setAuctionEntryNum(String mAuctionEntryNum) {
        this.mAuctionEntryNum = mAuctionEntryNum;
    }

    public String getCarName() {
        return mCarName;
    }

    public void setCarName(String mCarName) {
        this.mCarName = mCarName;
    }

    public String getChangeContext() {
        return mChangeContext;
    }

    public void setChangeContext(String mChangeContext) {
        this.mChangeContext = mChangeContext;
    }

    public String getExhibitorName() {
        return mExhibitorName;
    }

    public void setExhibitorName(String mExhibitorName) {
        this.mExhibitorName = mExhibitorName;
    }

    public String getCarYear() {
        return mCarYear;
    }

    public void setCarYear(String mCarYear) {
        this.mCarYear = mCarYear;
    }

    public String getEvalPoint() {
        return mEvalPoint;
    }

    public void setEvalPoint(String mEvalPoint1, String mEvalPoint2) {
        this.mEvalPoint = mEvalPoint2 + mEvalPoint1;
    }

    public String getAuctionAbsenteePrice() {
        return mAuctionAbsenteePrice;
    }

    public void setAuctionAbsenteePrice(String mAuctionAbsenteePrice) {
        this.mAuctionAbsenteePrice = mAuctionAbsenteePrice;
    }

    public String getAuctionStartPrice() {
        return mAuctionStartPrice;
    }

    public void setAuctionStartPrice(String mAuctionStartPrice) {
        this.mAuctionStartPrice = mAuctionStartPrice;
    }

    public String getAuctionHopePrice() {
        return mAuctionHopePrice;
    }

    public void setAuctionHopePrice(String mAuctionHopePrice) {
        this.mAuctionHopePrice = mAuctionHopePrice;
    }

    public String getCarImage() {
        return mCarImage;
    }

    public void setCarImage(String mCarImage) {
        this.mCarImage = mCarImage;
    }

    public String getAuctionSeqNum() {
        return mAuctionSeqNum;
    }

    public void setAuctionSeqNum(String mAuctionSeqNum) {
        this.mAuctionSeqNum = mAuctionSeqNum;
    }

}
