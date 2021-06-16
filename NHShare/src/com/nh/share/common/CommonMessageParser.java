package com.nh.share.common;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.common.models.AuctionReponseSession;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 공통 메시지 파서
 *
 * 공통으로 보내진 메시지를 각 클래스로 파싱함 분류 기준은 메시지 2번째(index 1) 글자임
 *
 */
public class CommonMessageParser {
	public static FromAuctionCommon parse(String message) {
		String[] messages = message.split(AuctionShareSetting.DELIMITER_REGEX);
		switch (messages[0].charAt(1)) {
		case AuctionStatus.TYPE: // 경매 상태 정보 전송
			return new AuctionStatus(messages);
		case Bidding.TYPE:
            return new Bidding(messages[1], messages[2], messages[3], messages[4]);
		case AuctionReponseSession.TYPE: // 접속 응답 처리
			return new AuctionReponseSession(messages[1], messages[2], messages[3]);
		case ConnectionInfo.TYPE: // 접속 정보 응답 처리
			return new ConnectionInfo(messages[1], messages[2], messages[3], messages[4]);
		case ResponseConnectionInfo.TYPE: // 접속 정보 응답 처리
			return new ResponseConnectionInfo(messages[1]);
		default:
			return null;
		}
	}
}