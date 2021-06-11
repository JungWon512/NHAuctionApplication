package com.nh.share.controller;

import java.util.HashMap;
import java.util.Map;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.controller.models.AutoMode;
import com.nh.share.controller.models.EditSetting;
import com.nh.share.controller.models.ManualMode;
import com.nh.share.controller.models.PassAuction;
import com.nh.share.controller.models.PauseAuction;
import com.nh.share.controller.models.RequestLogout;
import com.nh.share.controller.models.StartAuction;
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
        case AutoMode.TYPE: // 경매 자동 진행 모드
            return new AutoMode(messages[1]);
        case ManualMode.TYPE: // 경매 수동 진행 모드
            return new ManualMode(messages[1]);
        case EditSetting.TYPE: // 경매 설정 변경 처리
            Map<String, String> settings = new HashMap<>();
            for (int i = 1; i < messages.length; i = i + 2) {
                settings.put(messages[i], messages[i + 1]);
            }
            return new EditSetting(settings);
        case PassAuction.TYPE: // 강제 유찰 처리
            return new PassAuction(messages[1]);
        case PauseAuction.TYPE: // 경매 정치 기능
            return new PauseAuction(messages[1]);
        case StartAuction.TYPE: // 경매 시작 처리
            return new StartAuction(messages[1], messages[2]);
        case ToastMessageRequest.TYPE: // 메시지 전송 요청
            return new ToastMessageRequest(messages[1]);
        case RequestLogout.TYPE: // 로그아웃 처리 요청
            return new RequestLogout(messages[1]);
        default:
            throw null;
        }
    }
}
