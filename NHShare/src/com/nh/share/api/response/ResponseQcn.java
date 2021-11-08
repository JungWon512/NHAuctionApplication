package com.nh.share.api.response;

import com.nh.share.api.models.QcnData;

/**
 * 회차 정보 조회 응답 객체
 * 
 * @see {ActionRequestSelectQcn}
 *
 */
public class ResponseQcn extends BaseResponse {

	private QcnData data;
	
	public QcnData getData() {
		return data;
	}

	public void setData(QcnData data) {
		this.data = data;
	}
	
}
