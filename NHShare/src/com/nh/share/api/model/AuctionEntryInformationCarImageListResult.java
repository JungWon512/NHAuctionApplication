package com.nh.share.api.model;

public class AuctionEntryInformationCarImageListResult {
    private String imageFileName; // 이미지 파일명

    public AuctionEntryInformationCarImageListResult setTestModeData(String fileName) {
        this.imageFileName = fileName;
        return this;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

}
