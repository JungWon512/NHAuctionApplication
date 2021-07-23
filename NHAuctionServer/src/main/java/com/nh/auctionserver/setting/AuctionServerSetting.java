package com.nh.auctionserver.setting;

public class AuctionServerSetting {
    // 경매 서버 버전정보
    public static final String RELEASE_VERSION_NAME = "0.0.9";
    public static final String RELEASE_VERSION_DATE = "2021.06.11";

    // Log 파일 정보
    //public static final String AUCTION_LOG_FILE_PATH = "/auctserver/nh_auction/FileUpDown/LogFile/";
    public static final String AUCTION_LOG_FILE_PATH = "c:/NHAuction/LogFile/";
    public static final String AUCTION_LOG_FILE_EXTENSION = ".txt";

    // 경매 서버 접속 세션 확인 간
    // public static final int AUCTION_SERVER_READ_CHECK_SESSION_TIME = 600; //
    // 600초(10분)(관전자에 한해서 Read Time 적용)
    public static final int AUCTION_SERVER_READ_CHECK_SESSION_TIME = 60; // 10초(모든 접속자에 한해서 Read Time 적용)
    public static final int AUCTION_SERVER_WRITE_CHECK_SESSION_TIME = 30; // 30초

    public static long COUNT_DOWN_DELAY_TIME = 1000; // 경매 카운트 다운 간격 시간(ms)
    public static int COUNT_DOWN_TIME = 3; // 경매 카운트 다운 시간(second)
    
    public static long AUCTION_NEXT_ENTRY_DELAY_TIME = 3000; // 다음 출품 준비 딜레이 시간(ms)
    
    public static long BASE_DELAY_TIME = 0; // 타이머 동작 시작 딜레이 시간(ms)
    
    // 경매 유효 세션 확인 간격
    public static long CHECK_SESSION_TIME = 5000;

    public static int AUCTION_ENTRY_TOTAL_COUNT = 0; // 출품 차량 총 수
    public static int AUCTION_ENTRY_REMAIN_COUNT = 0; // 경매 남은 출품 차량 수
    public static int AUCTION_ENTRY_FINISH_COUNT = -1; // 경매 종료 출품 차량 수

    public static int AUCTION_START_PRICE = 0; // 경매 시작가
}