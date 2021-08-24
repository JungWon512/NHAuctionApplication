package com.nh.share.api.model;

import com.nh.share.api.response.BaseResponse;

/**
 * 
 * 경매 사용자 인증 처리 응답 내부 객체
 * 
 * @see {ResponseAuctionLogin}
 *
 */
public class AuctionLoginResult extends BaseResponse {

	private String authToken; // 인증 토큰

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	
}
