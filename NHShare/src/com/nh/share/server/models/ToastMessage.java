package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 메시지 전송 처리
 * 
 * 경매서버 -> 공통
 * 
 * ST | 조합구분코드 | 메시지 내용
 *
 */
public class ToastMessage implements FromAuctionServer {
	public static final char TYPE = 'T';
	private String mAuctionHouseCode; // 거점코드
	private String mMessage; // 메시지 내용

	public ToastMessage() {

	}

	public ToastMessage(String auctionHouseCode, String message) {
		mAuctionHouseCode = auctionHouseCode;
		mMessage = message;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getMessage() {
		return mMessage;
	}

	public void setMessage(String message) {
		this.mMessage = message;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mMessage);
	}

}
