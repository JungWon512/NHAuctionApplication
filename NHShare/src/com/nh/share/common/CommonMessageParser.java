package com.nh.share.common;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.common.models.AuctionReponseSession;
import com.nh.share.common.models.AuctionResult;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.AuctionType;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.CancelBidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.RefreshConnector;
import com.nh.share.common.models.RequestBiddingInfo;
import com.nh.share.common.models.RequestEntryInfo;
import com.nh.share.common.models.RequestLogout;
import com.nh.share.common.models.ResponseBiddingInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.common.models.RetryTargetInfo;
import com.nh.share.common.models.SmartEntryInfo;
import com.nh.share.server.models.AuctionBidStatus;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 공통 메시지 파서
 *
 * 공통으로 보내진 메시지를 각 클래스로 파싱함 분류 기준은 메시지 2번째(index 1) 글자임
 *
 */
public class CommonMessageParser {
	public static FromAuctionCommon parse(String message) {
		String[] messages = message.split(AuctionShareSetting.DELIMITER_REGEX, -1);
		switch (messages[0].charAt(1)) {
		case AuctionStatus.TYPE: // 경매 상태 정보 전송
			return new AuctionStatus(messages);
		case Bidding.TYPE:
			return new Bidding(messages[1], messages[2], messages[3], messages[4], messages[5], messages[6],
					messages[7], messages[8]);
		case AuctionReponseSession.TYPE: // 접속 응답 처리
			return new AuctionReponseSession(messages[1], messages[2], messages[3]);
		case ConnectionInfo.TYPE: // 접속 정보 응답 처리
			return new ConnectionInfo(messages[1], messages[2], messages[3], messages[4], messages[5]);
		case ResponseConnectionInfo.TYPE: // 접속 정보 응답 처리
			return new ResponseConnectionInfo(messages[1], messages[2], messages[3], messages[4]);
		case AuctionResult.TYPE: // 낙유찰 결과 전송
			return new AuctionResult(messages);
		case CancelBidding.TYPE: // 낙유찰 결과 전송
			return new CancelBidding(messages[1], messages[2], messages[3], messages[4], messages[5], messages[6]);
		case RefreshConnector.TYPE:
			return new RefreshConnector(messages[1]);
		case RequestEntryInfo.TYPE:
			return new RequestEntryInfo(messages[1], messages[2], messages[3]); // 출품 정보 전송 요청
		case RequestBiddingInfo.TYPE:
			return new RequestBiddingInfo(messages[1], messages[2], messages[3], messages[4]); // 응찰 정보 전송 요청
		case ResponseBiddingInfo.TYPE:
			return new ResponseBiddingInfo(messages[1], messages[2], messages[3], messages[4], messages[5]); // 응찰 정보 응답
		case RetryTargetInfo.TYPE:
			return new RetryTargetInfo(messages[1], messages[2], messages[3]); // 재경매 대상 참여자번호
		case AuctionType.TYPE:
			return new AuctionType(messages[1], messages[2]); // 경매 유형 코드 전송
		case RequestLogout.TYPE: // 로그아웃 처리 요청
			return new RequestLogout(messages[1], messages[2], messages[3], messages[4]);
		case AuctionBidStatus.TYPE: // 경매 응찰 상태 전송
			return new AuctionBidStatus(messages[1], messages[2], messages[3]);
		case SmartEntryInfo.TYPE: // 스마트계류 시스템 정보 전송
			return new SmartEntryInfo(messages);
		default:
			return null;
		}
	}
}