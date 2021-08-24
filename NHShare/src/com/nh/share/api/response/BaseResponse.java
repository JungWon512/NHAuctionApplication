package com.nh.share.api.response;

import java.io.Serializable;

public class BaseResponse implements Serializable {
	
	protected boolean success; // 요청 응답 결과
	
	protected String message; // 요청 응답 메시지
	
	protected String recordCount; // 응답 데이터 수


	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(String recordCount) {
		this.recordCount = recordCount;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
