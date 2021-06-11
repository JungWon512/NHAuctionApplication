package com.nh.share.api.model;

/**
 * 경매회원 참가통제여부 조회 응답 내부 객체
 * 
 * @see {ResponseApplicationVersion}
 *
 */
public class MemberJoinFlagResult {
	
    private String memberJoinFlag; // 경매 참여 가능 여부 - "01" : 참여가능, "기타" : 참여불가능

    public String getMemberJoinFlag() {
        return memberJoinFlag;
    }

    public void setMemberJoinFlag(String memberJoinFlag) {
        this.memberJoinFlag = memberJoinFlag;
    }
}