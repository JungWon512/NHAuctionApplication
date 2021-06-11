package com.nh.share.api.model;

/**
 * 응찰 프로그램 버전 확인 요청 응답 내부 객체
 * 
 * @see {ResponseApplicationVersion}
 *
 */
public class ApplicationVersionResult {
    private String version; // 버전명

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
