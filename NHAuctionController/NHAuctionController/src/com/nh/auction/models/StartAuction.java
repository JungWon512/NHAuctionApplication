package com.nh.auction.models;

import com.nh.auction.interfaces.FromAuctionController;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 경매 시작 처리 기능
 * - 경매 제어 프로그램의 경매 시작 명령 처리 수행
 * - 현재 출품 정보 기준으로 경매 시작 처리 수행
 * 
 * 구분자 | 출품번호
 * ex) CA | 1
 */
public class StartAuction implements FromAuctionController {

	public static final char TYPE = 'A';

	private String entryNum; // 출품번호

	public StartAuction() {}
	
	public StartAuction(String entryNum) {
		this.entryNum = entryNum;
	}

	public String getEntryNum() {
		return entryNum;
	}

	public void setEntryNum(String entryNum) {
		this.entryNum = entryNum;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s", ORIGIN, TYPE, NETTY_INFO.DELIMITER, getEntryNum());
	}
}
