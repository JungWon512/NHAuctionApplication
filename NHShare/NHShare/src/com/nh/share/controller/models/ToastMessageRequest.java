package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 메시지 전송 요청 처리
 * 
 * 제어프로그램 -> 경매서버
 * 
 * CT | 조합구분코드 | 메시지 내용
 *
 */
public class ToastMessageRequest implements FromAuctionController {
	public static final char TYPE = 'T';
	private String mAuctionHouseCode; // 거점코드
	private String mMessage; // 메시지 내용

	public ToastMessageRequest(String auctionHouseCode, String message) {
		this.mAuctionHouseCode = auctionHouseCode;
		this.mMessage = message;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public void setMessage(String message) {
		mMessage = message;
	}

	public String getMessage() {
		return mMessage;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mMessage);
	}
}
