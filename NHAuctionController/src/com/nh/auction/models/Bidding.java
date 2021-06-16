package com.nh.auction.models;

import java.io.Serializable;

import com.nh.auction.interfaces.FromCommon;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 경매 응찰 처리 기능
 * - 경매 참여 응찰자의 응찰 처리 기능 수행
 * - 응찰 정보에 대하여 경매 서버를 통해 제어프로그램으로 최종 수집되는 경매 처리 로직을 수행
 * 
 * 구분자 | 접속채널(ANDROID/IOS/WEB) | 경매회원번호 | 출품번호 | 응찰금액(만원)
 * ex) AB|PC|4122|1|2750
 *
 */
public class Bidding implements FromCommon, Serializable {

	private static final long serialVersionUID = 1L;

	public static final char TYPE = 'B';

	private String channel;		// 접속채널(ANDROID/IOS/WEB) 
	private String userNo;		// 경매회원번호
	private String entryNum;	// 출품번호
	private String price;		// 응찰금액(만원)

	public Bidding(String[] messages) {
		this.channel = messages[1];
		this.userNo = messages[2];
		this.entryNum = messages[3];
		this.price = messages[4];
	}
	
	public Bidding(String channel, String userNo, String price, String entryNum) {
		this.channel = channel;
		this.userNo = userNo;
		this.entryNum = entryNum;
		this.price = price;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getEntryNum() {
		return entryNum;
	}

	public void setEntryNum(String entryNum) {
		this.entryNum = entryNum;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s",
				ORIGIN, TYPE, 
				NETTY_INFO.DELIMITER,getChannel(),
				NETTY_INFO.DELIMITER,getUserNo(),
				NETTY_INFO.DELIMITER,getEntryNum(),
				NETTY_INFO.DELIMITER,getPrice()
				);
	}
}
