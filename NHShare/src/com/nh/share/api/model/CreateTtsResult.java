package com.nh.share.api.model;

/**
 * SMS 인증 번호 발송 요청 응답 내부 객체
 * 
 * @see {ResponseCreateTtsResult}
 *
 */
public class CreateTtsResult {
    private String failCnt; // 실패 건수
    private String code; // 요청 결과
    private String succCnt; // 성공 건수
    private String cnt; // 요청 건수

    public String getFailCnt() {
        return failCnt;
    }

    public void setFailCnt(String failCnt) {
        this.failCnt = failCnt;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSuccCnt() {
        return succCnt;
    }

    public void setSuccCnt(String succCnt) {
        this.succCnt = succCnt;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }
}
