package com.nh.auction.models;

import com.nh.auction.interfaces.FromAuctionServer;

/**
 * 경매 서버 접속 정보 유효 확인
 * - 일정 시간 경매 서버에 접속된 클라이언트에게 현재 접속 유효 여부를 확인 요청한다.
 * - 경매 서버에서 설정된 시간 간격으로 응답이 없는 클라이언트에게만 접속 유효 확인 처리를 실행한다.
 * 구분자
 * ex) SS
 */
public class AuctionCheckSession implements FromAuctionServer {

	public static final char TYPE = 'S';

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c", ORIGIN, TYPE);
	}

}
