package com.nh.controller.model;

/**
 * 관리자 회원 정보
 *
 * @author jhlee
 */
public class AdminData {
	
	private String userId;		//아이디
	
	private String nabzplc;		//속한 거점
	
	private String authToken;	//로그인 토큰

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getNabzplc() {
		return nabzplc;
	}

	public void setNabzplc(String nabzplc) {
		this.nabzplc = nabzplc;
	}
}