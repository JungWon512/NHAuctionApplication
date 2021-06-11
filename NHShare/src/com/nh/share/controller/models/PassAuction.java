package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 강제 유찰 처리 기능  
 * 
 * 제어프로그램 -> 서버
 * 
 * CP|출품번호
 *
 */
public class PassAuction implements FromAuctionController {
	public static final char TYPE = 'P';
	private String mEntryNum;	// 출품 번호 
	
	public PassAuction(String entryNum) {
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
