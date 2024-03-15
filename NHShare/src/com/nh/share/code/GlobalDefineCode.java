package com.nh.share.code;

/**
 * 
 * @ClassName GlobalDefineCode.java
 * @Description 경매 시스템 공통 코드 정의 클래스
 * @author 박종식
 * @since 2021.06.15
 */
public class GlobalDefineCode {
	// SSL 적용 여부
	public static final boolean FLAG_SSL = true;
	// 개발,운영 여부
	public static final boolean FLAG_PRD = false;
	// 응찰자 테스트 모드
	public static final boolean FLAG_TEST_MODE = false;
	// 응찰 로그 테스트 모드
	public static final boolean FLAG_TEST_MODE_BIDDING_LOG = false;
	
	// 경매장 거점 코드
	public enum AUCTION_HOUSE {
		AUCTION_HOUSE_HADONG("8808990656656"),
		AUCTION_HOUSE_HWASUN("8808990661315"),
		AUCTION_HOUSE_JANGSU("8808990657202");
	    
	    private final String value;
	    
		AUCTION_HOUSE(String value){
	        this.value = value;
	    }
	    
	    public String getValue(){
	        return value;
	    }
	 
	}
	
	// 응찰 채널
	public static final String CONNECT_CHANNEL_BIDDER = "6001";
	// 제어 채널
	public static final String CONNECT_CHANNEL_CONTROLLER = "6002";
	// 관전 채널
	public static final String CONNECT_CHANNEL_WATCHER = "6003";
	// 경매 낙,유찰 결과 모니터링 채널
	public static final String CONNECT_CHANNEL_AUCTION_RESULT_MONITOR = "6004";
	// 경매 접속자 모니터링 채널
	public static final String CONNECT_CHANNEL_AUCTION_CONNECT_MONITOR = "6005";
	// 경매 출하 안내 시스템 채널
	public static final String CONNECT_CHANNEL_AUCTION_STAND = "6006";

	public static final String NAMESPACE_BIDDER = "/" + CONNECT_CHANNEL_BIDDER;
	public static final String NAMESPACE_WATCH = "/" + CONNECT_CHANNEL_WATCHER;
	public static final String NAMESPACE_AUCTION_RESULT = "/" + CONNECT_CHANNEL_AUCTION_RESULT_MONITOR;
	public static final String NAMESPACE_CONNECTOR = "/" + CONNECT_CHANNEL_AUCTION_CONNECT_MONITOR;
	
	// 안드로이드 채널
	public static final String USE_CHANNEL_ANDROID = "ANDROID";
	// iOS 채널
	public static final String USE_CHANNEL_IOS = "IOS";
	// WEB 채널
	public static final String USE_CHANNEL_WEB = "WEB";
	// 관리자 채널
	public static final String USE_CHANNEL_MANAGE = "MANAGE";

	// 경매 진행 상태
	public static final String AUCTION_STATUS_NONE = "8001"; // 출품 자료 이관 전 상태
	public static final String AUCTION_STATUS_READY = "8002"; // 경매 준비 상태
	public static final String AUCTION_STATUS_START = "8003"; // 경매 시작 상태
	public static final String AUCTION_STATUS_PROGRESS = "8004"; // 경매 진행 상태
	public static final String AUCTION_STATUS_PASS = "8005"; // 경매 출품 건 강제 유찰
	public static final String AUCTION_STATUS_COMPLETED = "8006"; // 경매 완료 상태
	public static final String AUCTION_STATUS_FINISH = "8007"; // 경매 종료 상태

	//일괄 경매 상태 코드
	public static final String STN_AUCTION_STATUS_READY = "11";		//대기
	public static final String STN_AUCTION_STATUS_PROGRESS = "21";	//경매시작
	public static final String STN_AUCTION_STATUS_FINISH = "22";	//종료
	public static final String STN_AUCTION_STATUS_PAUSE = "23";		//정지
	
	// 응찰 가능 여부 상태
	public static final String AUCTION_BID_STATUS_N = "N";		//기본값
	public static final String AUCTION_BID_STATUS_P = "P";		//응찰중
	public static final String AUCTION_BID_STATUS_F = "F";		//응찰완료

	// 경매 시작 카운트 다운 상태
	public static final String AUCTION_COUNT_DOWN_READY = "R"; // 경매 시작 카운트 다운 준비 상태
	public static final String AUCTION_COUNT_DOWN = "C"; // 경매 시작 카운트 다운 상태
	public static final String AUCTION_COUNT_DOWN_COMPLETED = "F"; // 경매 시작 카운트 다운 완료 상태

	// 접속 요청 결과
	public static final String CONNECT_SUCCESS = "2000"; // 서버 접속 성공
	public static final String CONNECT_FAIL = "2001"; // 서버 접속 실패
	public static final String CONNECT_DUPLICATE = "2002"; // 서버 중복 접속
	public static final String CONNECT_CONTROLLER_ERROR = "2003"; // 제어프로그램 준비 안된 상태
	public static final String CONNECT_ETC_ERROR = "2004"; // 기타 장애
	public static final String CONNECT_RUN_FAIL = "2005"; // 프로그램 실행 불가
	public static final String CONNECT_EXPIRE_WATCHER = "2006"; // 관전자 접속 만료

	//경매 결과 코드
	public static final String AUCTION_RESULT_CODE_READY = "11";		//대기
	public static final String AUCTION_RESULT_CODE_SUCCESS = "22";		//낙찰
	public static final String AUCTION_RESULT_CODE_PENDING = "23";		//보류
	public static final String AUCTION_RESULT_CODE_CANCEL = "24";		//취소
	
	// 개체 유형 코드
	public static final String AUCTION_OBJ_TYPE_1 = "1"; // 경매 구분 - 송아지
	public static final String AUCTION_OBJ_TYPE_2 = "2"; // 경매 구분 - 비육우
	public static final String AUCTION_OBJ_TYPE_3 = "3"; // 경매 구분 - 번육우
	public static final String AUCTION_OBJ_TYPE_0 = "0"; // 경매 구분 - 일괄
	public static final String AUCTION_OBJ_TYPE_5 = "5"; // 경매 구분 - 염소
	public static final String AUCTION_OBJ_TYPE_6 = "6"; // 경매 구분 - 말
	
	// 경매 유형 코드
	public static final String AUCTION_TYPE_SINGLE = "20";		// 단일
	public static final String AUCTION_TYPE_BUNDLE = "10";		// 일괄
	
	// 요청 결과 미존재
	public static final String RESPONSE_REQUEST_NOT_RESULT = "4001";
	// 요청 실패
	public static final String RESPONSE_REQUEST_FAIL = "4002";
	// 유효하지 않은 가격 응찰 시도
	public static final String RESPONSE_REQUEST_BIDDING_INVALID_PRICE = "4003";
	// 출품 이관 전 상태
	public static final String RESPONSE_NOT_TRANSMISSION_ENTRY_INFO = "4004";
	// 응찰 취소 불가
	public static final String RESPONSE_DENIED_CANCEL_BIDDING = "4005";
	// 정상 응찰 응답
	public static final String RESPONSE_SUCCESS_BIDDING = "4006";
	// 정상 응찰 취소 응답 
	public static final String RESPONSE_SUCCESS_CANCEL_BIDDING = "4007";

	// 비육우 응찰 단위
	public static final String BIDDING_CUT_UNIT_1 = "1";
	public static final String BIDDING_CUT_UNIT_1000 = "1000";
	public static final String BIDDING_CUT_UNIT_10000 = "10000";
	
	// 프로그램 사용자 로그인 요청 타입
	public static final String AUCTION_LOGIN_TYPE_MANAGER = "MANAGER"; // 관리자
	public static final String AUCTION_LOGIN_TYPE_AUCTIONMEMBER = "AUCTIONMEMBER"; // 경매회원
	public static final String AUCTION_LOGIN_AUTH_TYPE_OTP = "ETCMEMBER"; // 일반(관전)회원

	public static final String BILLBOARD_CHARSET = "EUC-KR"; // 전광판 character set
	
	public static final String EMPTY_DATA = "";
	

	/**
	 * Sentry Error Monitoring  Tool
	 *
	 */
	public static final class SENTRY_INFO {

		public static final String SENTRY_CLIENT_KEY = "https://14f5ec77e6074dff8e29b338f6f3fc2d@o1148992.ingest.sentry.io/6220683"; // Sentry Error Monitoring  Key
		
		public static final String SENTRY_PROJECT_NAME= "nhlyvly"; // Sentry Project Name
		
		public static final String SENTRY_CLIENT_ENVIRONMENTS_PRODUCTION = "production"; // 환경 - 운영
		
		public static final String SENTRY_CLIENT_ENVIRONMENTS_DEV = "dev"; // 환경 - 개발.
		
		public static final double SENTRY_RATE = 1.0; // default 1.0
	
	
		/**
		 * 환경 정보 
		 * @return  ex) production (운영) or dev (개발)
		 */
		public static String getSentryEnvironment() {
			
			String environment = "";
			
			if(GlobalDefineCode.FLAG_PRD) {
				environment = SENTRY_CLIENT_ENVIRONMENTS_PRODUCTION;
			}else {
				environment = SENTRY_CLIENT_ENVIRONMENTS_DEV;
			}
			
			return environment;
		}
		
		
		/**
		 * Sentry ProjectName
		 * @return nhlyvly
		 */
		public static String getSentryName() {
			return SENTRY_PROJECT_NAME; 
		}
	}
	
}
