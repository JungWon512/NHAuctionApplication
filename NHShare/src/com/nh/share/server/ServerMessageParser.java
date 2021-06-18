package com.nh.share.server;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.ToastMessage;
import com.nh.share.setting.AuctionShareSetting;

public class ServerMessageParser {
	public static FromAuctionServer parse(String message) {
		String[] messages = message.split(AuctionShareSetting.DELIMITER_REGEX);
		switch (messages[0].charAt(1)) {
		case AuctionCountDown.TYPE: // 경매 시작 카운트 다운 정보 전송
			return new AuctionCountDown(messages[1], messages[2], messages[3]);
		case ToastMessage.TYPE: // 메시지 전송 처리
			return new ToastMessage(messages[1], messages[2]);
		case FavoriteEntryInfo.TYPE: // 관심출품 여부 정보
			return new FavoriteEntryInfo(messages);
		case ResponseCode.TYPE: // 예외 상황 전송 처리
			return new ResponseCode(messages[1], messages[2]);
		case AuctionCheckSession.TYPE: // 접속 유효 확인 처리
			return new AuctionCheckSession();
		case BidderConnectInfo.TYPE: // 접속자 정보 전송
			return new BidderConnectInfo(messages);
		case CurrentEntryInfo.TYPE: // 접속 정보 전송
			return new CurrentEntryInfo(messages);
		default:
			return null;
		}
	}
}
