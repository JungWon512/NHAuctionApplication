package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 메시지 전송 요청 처리   
 * 
 * 제어프로그램 -> 서버
 * 
 * CO|메시지내용 
 *
 */
public class ToastMessageRequest implements FromAuctionController {
	public static final char TYPE = 'O';
	private String mMessage;	// 메시지 내용 
	
	public ToastMessageRequest(String message) {
		this.mMessage = message;
	}
	
	public void setMessage(String message) {
		mMessage = message;
	}
	
	public String getMessage() {
		return mMessage;
	}
	
	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mMessage);
	}
}
