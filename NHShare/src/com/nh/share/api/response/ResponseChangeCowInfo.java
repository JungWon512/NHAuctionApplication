package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.models.CowInfoData;

/**
 * 출장우 데이터 응답 객체
 * 
 * @see {ActionRequestSelecCowInfo}
 *
 */
public class ResponseChangeCowInfo extends BaseResponse {

	private List<CowInfoData> entryList;

	public List<CowInfoData> getData() {
		return entryList;
	}

	public void setData(List<CowInfoData> data) {
		this.entryList = data;
	}
	
}
