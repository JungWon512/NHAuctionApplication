package com.nh.share.api.response;

/**
 * 
 * 파일 다운로드 후 응답 객체 성공시 파일 위치를 전달한다.
 * 
 * @see {ActionFileDownload}
 *
 */
public class ResponseFileDownload {
    private String filePath; // 다운로드 로컬 파일 위치
    private boolean success; // 다운로드 성공 여부

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
