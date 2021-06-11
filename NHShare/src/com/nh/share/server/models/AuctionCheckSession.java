package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;

/**
 * 경매 서버 접속 정보 유효 확인 처리
 * 
 * 서버 -> 공통
 * 
 * SK
 *
 */
public class AuctionCheckSession implements FromAuctionServer {
	public static final char TYPE = 'K';

	public AuctionCheckSession() {
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c", ORIGIN, TYPE);
	}
}
