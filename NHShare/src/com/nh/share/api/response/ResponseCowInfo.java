package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.models.CowInfoData;

/**
 * 출장우 데이터 응답 객체
 * 
 * @see {ActionRequestSelecCowInfo}
 *
 */
public class ResponseCowInfo extends BaseResponse {

	private List<CowInfoData> data;

	public List<CowInfoData> getData() {
		return data;
	}

	public void setData(List<CowInfoData> data) {
		this.data = data;
	}
	
}
