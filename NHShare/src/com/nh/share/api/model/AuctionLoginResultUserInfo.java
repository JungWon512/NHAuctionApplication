package com.nh.share.api.model;

/**
 * 경매 사용자 인증 처리 응답 내부 객체
 * 
 * @see {AuctionLoginResult}
 *
 */
public class AuctionLoginResultUserInfo {
    private String memberNum; // 고객 회원 번호
    private String userName; // 회원명
    private String userControlFlag; // 회원 통제 여부
    private String profileImageUrl; // 회원 프로필 이미지
    private String yearFeeExpiYmd; // 연회비 만료일
    private String yearFeeNotiYn; // 연회비 노출 여부  N 정상
    private String dDay; // 만료 남은 일자
    private String virtualAcc; // 가상계좌
    private String virtualAccNm; // 가상계좌 예금주
    private String recentConnectDateTime; // 최근 접속 일시
    private String authCI; // 약관동의 및 본인인증 화면 노출 여부
    

    public String getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(String memberNum) {
        this.memberNum = memberNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserControlFlag() {
        return userControlFlag;
    }

    public void setUserControlFlag(String userControlFlag) {
        this.userControlFlag = userControlFlag;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getRecentConnectDateTime() {
        return recentConnectDateTime;
    }

    public void setRecentConnectDateTime(String recentConnectDateTime) {
        this.recentConnectDateTime = recentConnectDateTime;
    }

    public String getYearFeeExpiYmd() {
        return yearFeeExpiYmd;
    }

    public void setYearFeeExpiYmd(String yearFeeExpiYmd) {
        this.yearFeeExpiYmd = yearFeeExpiYmd;
    }

    public String getdDay() {
        return dDay;
    }

    public void setdDay(String dDay) {
        this.dDay = dDay;
    }

    public String getVirtualAcc() {
        return virtualAcc;
    }

    public void setVirtualAcc(String virtualAcc) {
        this.virtualAcc = virtualAcc;
    }

    public String getVirtualAccNm() {
        return virtualAccNm;
    }

    public void setVirtualAccNm(String virtualAccNm) {
        this.virtualAccNm = virtualAccNm;
    }

    public String getYearFeeNotiYn() {
        return yearFeeNotiYn;
    }

    public void setYearFeeNotiYn(String yearFeeNotiYn) {
        this.yearFeeNotiYn = yearFeeNotiYn;
    }

    public String getAuthCI() {
        return authCI;
    }

    public void setAuthCI(String authCI) {
        this.authCI = authCI;
    }
    
    
}
