package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.models.BidEntryData;

/**
 * 회차 정보 조회 응답 객체
 * 
 * @see {ActionRequestSelectBidEntey}
 *
 */
public class ResponseBidEntry extends BaseResponse {

	private List<BidEntryData> data;

	public List<BidEntryData> getData() {
		return data;
	}

	public void setData(List<BidEntryData> data) {
		this.data = data;
	}
	
}
