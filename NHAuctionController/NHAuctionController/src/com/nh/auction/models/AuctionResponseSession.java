package com.nh.auction.models;

import com.nh.auction.interfaces.FromCommon;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 경매 서버 접속 정보 전달 기능
 * - 경매 서버에서 접속 상태를 요청 받을 경우 현재 접속 유효 여부에 대한 정보를 응답처리한다.
 * - 경매 서버는 응답에 대한 처리는 수행하지 않는다.
 * 
 * 구분자 | 회원번호 | 접속요청채널(6001/6002/6003/6004) | 사용채널(ANDROID/IOS/WEB)
 * ex) AS | 4122 | 6001 | ANDROID
 */
public class AuctionResponseSession implements FromCommon {
	
	public static final char TYPE = 'S';
	
	private String userNo;		// 회원(사원)번호
	private String channel;		// 접속 요청 채널
	private String os;			// 사용 채널

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

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c"
				+ "%c%s"
				+ "%c%s",
				ORIGIN, TYPE, 
				NETTY_INFO.DELIMITER,getUserNo(),
				NETTY_INFO.DELIMITER,getChannel(),
				NETTY_INFO.DELIMITER,getOs()
				);
	}

}
