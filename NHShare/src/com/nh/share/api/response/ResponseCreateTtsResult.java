package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.CreateTtsResult;

/**
 * 경매 서버 생성 결과 반영 관련 응답
 * 
 * @see {ActionRequestCreateTts}
 *
 */
public class ResponseCreateTtsResult extends BaseResponse {
    private List<CreateTtsResult> result; // 응답 결과

    public List<CreateTtsResult> getResult() {
        return result;
    }

    public void setResult(List<CreateTtsResult> result) {
        this.result = result;
    }
}
