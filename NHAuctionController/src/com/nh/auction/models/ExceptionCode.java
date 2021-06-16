package com.nh.auction.models;

import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 예외 상황 전송 처리 기능
 * - 경매 서버에서 예외 상황에 따른 코드 전달 처리 수행
 * - 경매 서버는 모든 Client에게 예외 상황을 판별할 수 있는 예외 상황 코드를 전송 처리
 * 
 * SE|예외상황코드
 * ex) SE | 4001
 */
public class ExceptionCode implements com.nh.auction.interfaces.FromAuctionServer {
	
	public static final char TYPE = 'E';
	
	private String errorCode;	// 예외상황코드
								// 요청 결과 미존재		4001	
								// 요청 처리 실패		4002	
								// 중복 인증 상태		5001	
								// 경매 종료 상태		6001	

	public ExceptionCode(String[] messages) {
		this.errorCode = messages[1];
	}
	
	public ExceptionCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s", ORIGIN, TYPE, NETTY_INFO.DELIMITER, getErrorCode());
	}

}
