package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.models.FeeBaseData;

/**
 * 출장우 데이터 응답 객체
 * 
 * @see {ActionRequestSelectFee}
 *
 */
public class ResponseFee extends BaseResponse {

	private List<FeeBaseData> data;

	public List<FeeBaseData> getData() {
		return data;
	}

	public void setData(List<FeeBaseData> data) {
		this.data = data;
	}
	
}
