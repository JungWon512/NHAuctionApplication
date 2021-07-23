package com.nh.share.controller;

import java.util.HashMap;
import java.util.Map;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.controller.models.EditSetting;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.PassAuction;
import com.nh.share.controller.models.ReadyEntryInfo;
import com.nh.share.controller.models.RequestLogout;
import com.nh.share.controller.models.SendAuctionResult;
import com.nh.share.controller.models.StartAuction;
import com.nh.share.controller.models.StopAuction;
import com.nh.share.controller.models.ToastMessageRequest;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 제어프로그램 메시지 파서
 *
 * 제어프로그램에서 보내진 메시지를 각 클래스로 파싱함 분류 기준은 메시지 2번째(index 1) 글자임
 *
 */
public class ControllerMessageParser {
	public static FromAuctionController parse(String message) {
		String[] messages = message.split(AuctionShareSetting.DELIMITER_REGEX);
		switch (messages[0].charAt(1)) {
		case EditSetting.TYPE: // 경매 설정 변경 처리
			return new EditSetting(messages);
		case PassAuction.TYPE: // 강제 유찰 처리
			return new PassAuction(messages[1], messages[2]);
		case StopAuction.TYPE: // 경매 정치 기능
			return new StopAuction(messages[1], messages[2]);
		case StartAuction.TYPE: // 경매 시작 처리
			return new StartAuction(messages[1], messages[2]);
		case ToastMessageRequest.TYPE: // 메시지 전송 요청
			return new ToastMessageRequest(messages[1], messages[2]);
		case RequestLogout.TYPE: // 로그아웃 처리 요청
			return new RequestLogout(messages[1], messages[2], messages[3]);
		case EntryInfo.TYPE: // 출품 정보 전송
			return new EntryInfo(messages);
		case ReadyEntryInfo.TYPE: // 출품 정보 경매 준비 요청
			return new ReadyEntryInfo(messages[1], messages[2]);
		case SendAuctionResult.TYPE: // 낙유찰 결과 전송
			return new SendAuctionResult(messages);
		default:
			throw null;
		}
	}
}
