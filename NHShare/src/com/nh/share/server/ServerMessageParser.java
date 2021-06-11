package com.nh.share.server;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.server.models.AbsenteeUserInfo;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.AuctionStatus;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.CurrentSetting;
import com.nh.share.server.models.ExceptionCode;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.ResponseConnectionInfo;
import com.nh.share.server.models.ResponseEntryInfo;
import com.nh.share.server.models.ToastMessage;
import com.nh.share.setting.AuctionShareSetting;

public class ServerMessageParser {
	public static FromAuctionServer parse(String message) {
		String[] messages = message.split(AuctionShareSetting.DELIMITER_REGEX);
		switch (messages[0].charAt(1)) {
		case AuctionCountDown.TYPE: // 경매 시작 카운트 다운 정보 전송
			return new AuctionCountDown(messages[1], messages[2]);
		case AuctionStatus.TYPE: // 경매 상태 정보 전송
			return new AuctionStatus(messages);
		case CurrentSetting.TYPE: // 경매 환경 설정 정보
			return new CurrentSetting(messages);
		case ResponseEntryInfo.TYPE: // 출품 정보 전송
			return new ResponseEntryInfo(messages);
		case ToastMessage.TYPE: // 메시지 전송 처리
			return new ToastMessage(messages[1]);
		case BidderConnectInfo.TYPE: // 접속 정보 응답 처리
			return new BidderConnectInfo(messages);
		case ResponseConnectionInfo.TYPE: // 접속 정보 응답 처리
			return new ResponseConnectionInfo(messages[1]);
		case FavoriteEntryInfo.TYPE: // 관심출품 여부 정보
			return new FavoriteEntryInfo(messages);
		case AbsenteeUserInfo.TYPE: // 부재자 입찰 참여 여부 정보
			return new AbsenteeUserInfo(messages);
		case ExceptionCode.TYPE: // 예외 상황 전송 처리
			return new ExceptionCode(messages[1]);
		case AuctionCheckSession.TYPE: // 접속 유효 확인 처리
			return new AuctionCheckSession();
		default:
			return null;
		}
	}
}
