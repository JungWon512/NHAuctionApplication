package com.nh.controller.model;

/**
 * 관리자 회원 정보
 *
 * @author jhlee
 */
public class AdminData {
	
	private String userId;		//아이디

	private String accessToken;	//토큰

	private String nabzplc;		//속한 거점

	private String etcAucObjDsc; //기타 가축여부
	
	public String getEtcAucObjDsc() {
		return etcAucObjDsc;
	}

	public void setEtcAucObjDsc(String etcAucObjDsc) {
		this.etcAucObjDsc = etcAucObjDsc;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public String getNabzplc() {
		return nabzplc;
	}

	public void setNabzplc(String nabzplc) {
		this.nabzplc = nabzplc;
	}
}