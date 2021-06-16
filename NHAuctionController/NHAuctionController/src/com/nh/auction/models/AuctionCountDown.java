package com.nh.auction.models;

import com.nh.auction.interfaces.FromAuctionServer;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 경매 시작 카운트 다운 정보 전송
 * - 경매 시작/종료 카운트 다운 정보 전송 처리
 * - 경매 제어에서 시작/종료 명령 후 카운트 다운 처리를 진행한다.
 * 
 * 구분자 | 유형(S : 시작 / F : 종료) | 상태구분(R : 준비 / C : 카운트다운 / F : 카운트다운 완료) | 카운트다운 시간(second)
 * ex) SD | S | R | 10
 */
public class AuctionCountDown implements FromAuctionServer {
	
	public static final char TYPE = 'D';
	
	private String status;			// 상태구분 (R : 준비 / C : 카운트다운)
	private String countDownTime;	// 카운트다운시간

	public AuctionCountDown() {}

	public AuctionCountDown(String[] messages) {
		this.status = messages[1];
		this.countDownTime = messages[2];
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCountDownTime() {
		return countDownTime;
	}

	public void setCountDownTime(String countDownTime) {
		this.countDownTime = countDownTime;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c"
				+ "%c%s"
				+ "%c%s",
				ORIGIN, TYPE, 
				NETTY_INFO.DELIMITER,getStatus(),
				NETTY_INFO.DELIMITER,getCountDownTime()
				);
	}
	
}
