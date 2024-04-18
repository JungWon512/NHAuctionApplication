package com.nh.share.api.response;

import com.nh.share.api.models.BzlocData;

/**
 * 조합정보
 * 
 * @see {ActionRequestNaBzloc}
 *
 */
public class ResponseBzlocInfo extends BaseResponse {

	private BzlocData data;

	public BzlocData getInfo() {
		return data;
	}

	public void setInfo(BzlocData info) {
		this.data = info;
	}
}
