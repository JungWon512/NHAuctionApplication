package com.nh.auction.models;

import com.nh.auction.interfaces.FromAuctionServer;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 메시지 전송 처리 기능
 * - 경매 제어 프로그램에서 요청한 메시지 전송 내용을 각 응찰 Client측에 전송 처리 수행
 * - 서버는 수신한 메시지를 응찰기에 즉시 송신 처리
 *
 * 구분자 | 메시지 내용
 * ex) ST | 잠시 후 경매를 시작합니다.
 */
public class ToastMessage implements FromAuctionServer {

	public static final char TYPE = 'T';

	private String message; // 메시지 내용

	public ToastMessage(String[] messages) {
		this.message = messages[1];
	}
	
	public ToastMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s", ORIGIN, TYPE, NETTY_INFO.DELIMITER, getMessage());
	}
}
