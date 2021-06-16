package com.nh.auction.models;

import com.nh.auction.interfaces.FromAuctionController;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 강제 유찰 처리 기능
 * - 경매 제어 프로그램의 강제 유찰 기능 처리 수행
 * - 현재 출품 정보에 대하여 강제 유찰 처리 수행
 * 
 * 구분자 | 출품번호
 * ex) CP | 1
 */
public class PassAuction implements FromAuctionController {

	public static final char TYPE = 'P';

	private String entryNum; // 출품번호

	public PassAuction() {
	}

	public PassAuction(String entryNum) {
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
