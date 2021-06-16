package com.nh.auction.models;

import com.nh.auction.interfaces.FromAuctionController;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 메시지 전송 요청 처리
 * - 경매 제어 프로그램에서 전송 요청된 메시지 정보를 수신하여 응찰 프로그램에 메시지 전송되도록 처리 수행
 * - 경매 제어에서 요청된 메시지 내용을 확인 후 "ToastMessage"를 사용하여 메시지 전송 처리 수행
 *
 * 구분자 | 메시지 내용
 * ex) CT | 잠시 후 경매를 시작합니다.
 */
public class ToastMessageRequest implements FromAuctionController {

	public static final char TYPE = 'T';

	private String message; // 메시지 내용

	public ToastMessageRequest() {}
	
	public ToastMessageRequest(String message) {
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
