package com.nh.share.controller.models;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 자동 진행 모드  
 * 
 * 제어프로그램 -> 서버
 * 
 * CM|출품번호
 *
 */
public class ManualMode implements FromAuctionController {
	public static final char TYPE = 'M';
	private String mEntryNum;	// 출품번호 
	
	public ManualMode(String entryNum) {
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
