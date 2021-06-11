package com.nh.share.api.model;

import java.util.List;

/**
 * 
 * 경매 사용자 인증 처리 응답 내부 객체
 * 
 * @see {ResponseAuctionLogin}
 *
 */
public class AuctionLoginResult {
    private String authResult; // 인증 결과
    private String authToken; // 인증 토큰
    private String memberStatus; // 회원 정보
    private String refreshToken; // 인증 토큰
    private List<AuctionLoginResultUserInfo> userInfo; // 회원 정보

    public String getAuthResult() {
        return authResult;
    }

    public void setAuthResult(String authResult) {
        this.authResult = authResult;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
    }

    public List<AuctionLoginResultUserInfo> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(List<AuctionLoginResultUserInfo> userInfo) {
        this.userInfo = userInfo;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
