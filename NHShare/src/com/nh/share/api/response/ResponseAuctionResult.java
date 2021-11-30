package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.models.AucResultData;

/**
 * 경매 결과 데이터 응답 객체
 * 
 * @see {ActionRequestAuctionResult}
 *
 */
public class ResponseAuctionResult extends BaseResponse {

	private List<AucResultData> failList;

	public List<AucResultData> getFailList() {
		return failList;
	}

	public void setFailList(List<AucResultData> failList) {
		this.failList = failList;
	}

}
