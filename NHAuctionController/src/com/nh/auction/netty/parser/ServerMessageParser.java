package com.nh.auction.netty.parser;

import com.nh.auction.interfaces.FromAuctionServer;
import com.nh.auction.interfaces.FromCommon;
import com.nh.auction.models.AuctionCheckSession;
import com.nh.auction.models.AuctionCountDown;
import com.nh.auction.models.Bidding;
import com.nh.auction.models.ConnectionInfo;
import com.nh.auction.models.ExceptionCode;
import com.nh.auction.models.ToastMessage;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

public class ServerMessageParser {

	public static FromAuctionServer parse_s(String message) {

		String[] messages = message.split(NETTY_INFO.DELIMITER_REGEX);

		switch (messages[0].charAt(1)) {
			case ConnectionInfo.TYPE:					// 접속자 정보 인증 처리 요청 처리
				return new ConnectionInfo(messages);
			case ToastMessage.TYPE:						// 메시지 전송 요청 처리
				return new ToastMessage(messages);
			case ExceptionCode.TYPE:					// 예외 상황 전송 처리 기능
				return new ExceptionCode(messages);
			case AuctionCheckSession.TYPE:				// 경매 서버 접속 정보 유효 확인
				return new AuctionCheckSession();
			case AuctionCountDown.TYPE:					// 경매 시작 카운트 다운 정보 전송
				return new AuctionCountDown(messages);
			default:
				return null;
		}
	}

	public static FromCommon parse_a(String message) {

		String[] messages = message.split(NETTY_INFO.DELIMITER_REGEX);

		switch (messages[0].charAt(1)) {
			case Bidding.TYPE:						// 경매 응찰 처리 기능
				return new Bidding(messages);
		
			default:
				return null;
		}

	}

}
