package com.nh.share.api.response;

import com.nh.share.api.models.BzlocData;

/**
 * 경매 조합정보
 * 
 * @see {ActionRequestApplicationVersion}
 *
 */
public class ResponseBzlocInfo extends BaseResponse {

	private BzlocData info;

	public BzlocData getInfo() {
		return info;
	}

	public void setInfo(BzlocData info) {
		this.info = info;
	}
}
