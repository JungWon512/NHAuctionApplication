package com.nh.share.api.response;

/**
 * 경매 사용자 인증 처리 응답 객체
 * 
 * @see {ActionRequestAuctionLogin}
 *
 */
public class ResponseAuctionLogin extends BaseResponse {

	private String accessToken;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	

}
