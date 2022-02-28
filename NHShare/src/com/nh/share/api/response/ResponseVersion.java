package com.nh.share.api.response;

import com.nh.share.api.models.VersionData;

/**
 * 경매 진행 프로그램 버전 체크
 * 
 * @see {ActionRequestApplicationVersion}
 *
 */
public class ResponseVersion extends BaseResponse {

	private VersionData info;

	public VersionData getInfo() {
		return info;
	}

	public void setInfo(VersionData info) {
		this.info = info;
	}
}
