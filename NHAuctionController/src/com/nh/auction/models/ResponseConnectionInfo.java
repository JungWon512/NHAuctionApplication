package com.nh.auction.models;

import com.nh.auction.interfaces.FromCommon;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 접속 정보 응답 처리
 * - 최종 제어프로그램에서 인증 정보 확인 결과에 대한 응답 처리 기능 수행
 * 
 * 구분자 | 접속결과
 * ex) AR | 2000
 */
public class ResponseConnectionInfo implements FromCommon {

	public static final char TYPE = 'R';

	private String result;		// 결과코드
								//2000 : 인증 성공
								//2001 : 인증 실패
								//2002 : 중복 접속
								//2003 : 기타 장애

	public ResponseConnectionInfo(String result) {
		this.result = result;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s", ORIGIN, TYPE, NETTY_INFO.DELIMITER, getResult());
	}

}
