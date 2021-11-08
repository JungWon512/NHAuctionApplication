package com.nh.share.api.response;

import com.nh.share.api.models.UserJoinNumberData;

/**
 * 경매 사용자 인증 처리 응답 객체
 * 
 * @see {ActionRequestSelectBidNum}
 *
 */
public class ResponseJoinNumber extends BaseResponse {

	private UserJoinNumberData data;

	public UserJoinNumberData getData() {
		return data;
	}

	public void setData(UserJoinNumberData data) {
		this.data = data;
	}
}
