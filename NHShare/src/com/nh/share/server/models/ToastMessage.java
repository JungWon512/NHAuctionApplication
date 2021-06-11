package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 메시지 전송 처리
 * 
 * 서버 -> 응찰기
 * 
 * SO|메시지내용
 *
 */
public class ToastMessage implements FromAuctionServer {
	public static final char TYPE = 'O';
	private String mMessage; // 메시지 내용

	public ToastMessage() {

	}

	public ToastMessage(String message) {
		mMessage = message;
	}

	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String message) {
		this.mMessage = message;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mMessage);
	}

}
