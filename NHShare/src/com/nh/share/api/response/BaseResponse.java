package com.nh.share.api.response;

import java.io.Serializable;

public class BaseResponse implements Serializable {
    protected String status; // 요청 응답 결과
    protected String message; // 요청 응답 메시지
    protected String recordCount; // 응답 데이터 수

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        return "{" + "\"status\" : \"" + status + "\"" + ", \"message\" : \"" + message + "\"" + ", \"recordCount\" : \"" + recordCount + "\"" + "}";
    }
}
