package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 출품 정보 경매 준비 요청  
 * 
 * 제어프로그램 -> 서버
 * 
 * CR|출품번호
 *
 */
public class ReadyEntryInfo implements FromAuctionController {
	public static final char TYPE = 'R';
	private String mEntryNum;	// 출품 번호 
	
	public ReadyEntryInfo(String entryNum) {
		mEntryNum = entryNum;
	}
		
	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mEntryNum);
	}

}
