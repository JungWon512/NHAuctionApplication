package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 낙/유찰 결과 정보 전송 요청
 * 
 * 경매서버 -> 제어프로그램
 * 
 * SR | 경매거점코드 | 출품번호
 *
 */
public class RequestAuctionResult implements FromAuctionServer {
	public static final char TYPE = 'R';
	private String mAuctionHouseCode; // 거점코드
	private String mEntryNum; // 응답코드

	public RequestAuctionResult(String auctionHouseCode, String entryNum) {
		mAuctionHouseCode = auctionHouseCode;
		mEntryNum = entryNum;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mEntryNum);
	}
}
