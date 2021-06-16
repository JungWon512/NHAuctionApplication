package com.nh.auction.models;

import com.nh.auction.interfaces.FromAuctionController;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 경매 정지 처리 기능
 * - 경매 제어 프로그램의 경매 정지 기능 처리 수행
 * - 현재 경매 진행 중인 출품 정보에 대한 경매 진행을 종료 처리한다.
 *
 * 구분자 | 출품번호
 * CS | 1001
 */
public class StopAuction implements FromAuctionController {

	public static final char TYPE = 'S';

	private String entryNum; // 출품번호

	public StopAuction() {}
	
	public StopAuction(String entryNum) {
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
