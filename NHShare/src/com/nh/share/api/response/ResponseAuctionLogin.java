package com.nh.share.api.response;

/**
 * 경매 사용자 인증 처리 응답 객체
 * 
 * @see {ActionRequestAuctionLogin}
 *
 */
public class ResponseAuctionLogin extends BaseResponse {

	private String accessToken;
	private String naBzplc;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getNaBzplc() {
		return naBzplc;
	}

	public void setNaBzplc(String naBzplc) {
		this.naBzplc = naBzplc;
	}

}
