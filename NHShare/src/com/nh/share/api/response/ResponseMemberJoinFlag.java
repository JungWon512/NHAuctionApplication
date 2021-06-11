package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.model.MemberJoinFlagResult;

/**
 * 경매회원 참가통제여부 조회 응답 객체
 * 
 * @see {ResponseMemberJoinFlagResult}
 *
 */
public class ResponseMemberJoinFlag extends BaseResponse {
    private List<MemberJoinFlagResult> result; // 응답 결과

    public List<MemberJoinFlagResult> getResult() {
        return result;
    }

    public void setResult(List<MemberJoinFlagResult> result) {
        this.result = result;
    }
}
