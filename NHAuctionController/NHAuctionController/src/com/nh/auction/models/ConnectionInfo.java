package com.nh.auction.models;

import com.nh.auction.interfaces.FromAuctionServer;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 접속자 정보 인증 처리 요청 처리
 * - 경매 서버에 접속 요청한 접속자 기본정보 및 접속 요청 채널을 제어프로그램에게 인증 요청 처리 수행
 * - 전달 된 접속자 기본정보 및 접속 요청 채널을 경매 서버에서 중복 참여 제어에 반영 처리
 * 
 * 구분자 | 회원번호 | 접속요청채널(6001/6002/6003/6004) | 사용채널(ANDROID/IOS/WEB) | 관전자여부(Y/N)
 * ex) SC | 4122 | 6001 | ANDROID | N
 */
public class ConnectionInfo implements FromAuctionServer {
	
	public static final char TYPE = 'C';
	
	private String userNo;		// 회원(사원)번호
	private String channel;		// 접속 요청 채널
	private String os;			// 사용 채널
	private String status;		// 관전자 여부 (Y/N)

	public ConnectionInfo(String userNo, String channel, String os, String status) {
		this.userNo = userNo;
		this.channel = channel;
		this.os = os;
		this.status = status;
	}

	public ConnectionInfo(String[] messages) {
		this.userNo = messages[1];
		this.channel = messages[2];
		this.os = messages[3];
		this.status = messages[4];
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s",
				ORIGIN, TYPE, 
				NETTY_INFO.DELIMITER,getUserNo(),
				NETTY_INFO.DELIMITER,getChannel(),
				NETTY_INFO.DELIMITER,getOs(),
				NETTY_INFO.DELIMITER,getStatus()
				);
	}

}
