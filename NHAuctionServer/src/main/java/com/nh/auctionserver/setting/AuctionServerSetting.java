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
    public static int COUNT_DOWN_TIME = 10; // 경매 카운트 다운 시간(second)

    // 경매 유효 세션 확인 간격
    public static long CHECK_SESSION_TIME = 5000;

    // 경매설정 (변경 가능 설정들)
    public static long REMAIN_CHECK_DELAY_TIME = 10; // 경매 남은 시간 확인 간격 시간(ms)
    public static long DEFAULT_CHECK_DELAY_TIME = 700; // 기본 경매 데이터 수집 간격 시간(ms)
    public static long BASE_DELAY_TIME = 0; // 타이머 동작 시작 딜레이 시간(ms)
    public static long AUCTION_TIME = 3000; // 경매 진행 시간 (ms)
    public static long AUCTION_DETERMINE_TIME = 1000; // 경매 낙/유찰 지연 시간 (ms)
    public static long AUCTION_NEXT_DELAY_TIME = 1000; // 다음 출품 시작 간격 시간(ms)
    public static int AUCTION_AUTO_RISE_COUNT = 2; // 자동 상승 처리 횟수
    public static int AUCTION_CURRENT_RISING_PRICE = 0; // 현재 적용 중인 상승 가격(만원)
    public static int CHECK_AUCTION_TIME = 500; // 경매 관련 타이머 오류 검출 타이머 시작 딜레이 시간(ms)
    public static long CHECK_AUCTION_DELAY_TIME = 10; // 경매 관련 타이머 오류 검출 타이머 확인 간격 시간(ms)
    
    public static int AUCTION_BASE_PRICE = 500; // 경매 기준 금액(만원)
    public static int AUCTION_MAX_BASE_PRICE = 10000; // 경매 1억 기준 금액(만원)
    public static int AUCTION_BELOW_RISING_PRICE = 3; // 기준금액 이하 상승가(만원)
    public static int AUCTION_MORE_RISING_PRICE = 5; // 기준금액 이상 상승가(만원)
    public static int AUCTION_MAX_RISING_PRICE = 10; // 기준금액 1억이상 상승가(만원)

    public static int AUCTION_ENTRY_TOTAL_COUNT = 0; // 출품 차량 총 수
    public static int AUCTION_ENTRY_REMAIN_COUNT = 0; // 경매 남은 출품 차량 수
    public static int AUCTION_ENTRY_FINISH_COUNT = -1; // 경매 종료 출품 차량 수

    public static boolean AUCTION_START_STATUS = false; // 경매 시작/정지 상태
    public static boolean AUCTION_AUTO_MODE = true; // 경매 자동 진행 모드 설정
}