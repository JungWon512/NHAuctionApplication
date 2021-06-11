package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.ApplicationVersionResult;

/**
 * 응찰 프로그램 버전 확인 요청 응답 객체
 * 
 * @see {ActionRequestApplicationVersion}
 *
 */
public class ResponseApplicationVersion extends BaseResponse {
    private List<ApplicationVersionResult> result; // 응답 결과

    public List<ApplicationVersionResult> getResult() {
        return result;
    }

    public void setResult(List<ApplicationVersionResult> result) {
        this.result = result;
    }
}
