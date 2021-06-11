package com.nh.share.api;

import com.nh.share.setting.AuctionShareSetting;

public class GlobalDefine {
    
    private static GlobalDefine globalDefine = new GlobalDefine();

    public static final String API_REQUEST_AUCTION_LOGIN = "/api/auction/requestAuctionLogin.do"; // 경매 사용자 인증 처리

    public static final String API_GET_AUCTION_GENERATE_INFORMATIONS = "/api/auction/getAuctionGenerateInformations.do"; // 경매 생성 정보 조회 요청

    public static final String API_GET_AUCTION_SETTINGS_INFORMATIONS = "/api/auction/getAuctionSettingInformations.do"; // 경매 환경 설정 정보 조회 요청

    public static final String API_GET_AUCTION_ENTRY_INFORMATIONS = "/api/auction/getAuctionEntryInformations.do"; // 출품 정보 조회 요청

    public static final String API_GET_AUCTION_INTEREST_ENTRY_INFO = "/api/auction/getAuctionInterestEntryInfo.do"; // 관심 차량 정보 조회 요청

    public static final String API_GET_ABSENTEE_AUCTION_BID_INFO = "/api/auction/getAbsenteeAuctionBidInfo.do"; // 부재자 입찰 정보 조회 요청

    public static final String API_REQUEST_TRANSMISSION_AUCTION_RESULT = "/api/auction/requestTransmissionAuctionResult.do"; // 경매 낙/유찰 결과 전송 요청

    public static final String API_GET_AUCTION_RESULT = "/api/auction/getAuctionResult.do"; // 경매 낙/유찰 결과 정보 조회 요청

    public static final String API_GET_AUCTION_SCHEDULE_INFO = "/api/auction/getAuctionScheduleInfo.do"; // 경매 일정 정보 조회 요청(캘린더)

    public static final String API_GET_AUCTION_DETAIL_INFO = "/api/auction/getAuctionDetailInfo.do"; // 경매 상세 일정 정보 조회 요청

    public static final String API_GET_AUCTION_MONTH_INFO = "/api/auction/getAuctionMonthInfo.do"; // 경매 당월 누적 정보 조회 요청

    public static final String API_GET_AUCTION_NOTICE_LIST = "/api/auction/getAuctionNoticeList.do"; // 경매 공지 사항 목록 조회 요청

    public static final String API_GET_AUCTION_SETTINGS = "/api/auction/getAuctionSettings.do"; // 응찰 프로그램 설정 정보 조회 요청

    public static final String API_REQUEST_SAVE_AUCTION_SETTINGS = "/api/auction/requestSaveAuctionSettings.do"; // 응찰 프로그램 설정 정보 저장 요청

    public static final String API_GET_APPLICATION_VERSION = "/api/auction/getApplicationVersion.do"; // 응찰 프로그램 버전 확인 요청

    public static final String API_UPDATE_AUCTION_STATUS = "/api/auction/updateAuctionStatus.do"; // 경매 진행 상태 업데이트 요청

    public static final String API_UPDATE_AUCTION_PORT_INFORMATION = "/api/auction/updateAuctionPortInformation.do"; // 경매 포트 정보 전송 요청

    public static final String API_REQUEST_SEND_SMS_AUTH_NUMBER = "/api/auction/requestSendSmsAuthNumber.do"; // SMS 인증 번호 발송 요청
    
    public static final String API_REQUEST_SEND_SMS_AUCTION_SERVER_RESULT = "/api/auction/requestSendSmsAuctionServerResult.do"; // 경매 서버 생성 결과 전송 및 SMS 발송 요청
    
    public static final String API_REQUEST_CREATE_TTS = "/api/auction/requestTtsFileCreate.do"; // 경매 TTS 생성 요청

    public static final String API_AUCTION_CONNECTION_STATUS = "/api/auction/requestAuctionJoinStatus.do"; // 경매 서버 참여 가능 여부 조회 요청

    public static final String API_GET_AUCTION_IMAGE_NOTICE = "/api/auction/getAuctionImageNotice.do"; // 이미지 공지 팝업 조회 요청
    
    public static final String API_AUCTION_SAVE_AUCTION_ATTEND_LOG = "/api/auction/requestSaveAuctionAttendLog.do"; // 경매 참여 이력 로그

    public static final String API_GET_AUCTION_MEMBER_JOIN_FLAG = "/api/auction/getAuctionMemberJoinFlag.do"; // 경매회원 참가통제여부 조회
    
    public static GlobalDefine getInstance() {
        return globalDefine;
    }
    
    public String getBaseDomain() {
        if (AuctionShareSetting.gIsClientApplication) {
            return AuctionShareSetting.AUCTION_CLIENT_API_HOST;
        } else {
            return AuctionShareSetting.AUCTION_SERVER_API_HOST;
        }
    }
}
