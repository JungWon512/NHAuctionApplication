package com.nh.share.code;

/**
 * 
 * @ClassName GlobalDefineCode.java
 * @Description 경매 시스템 공통 코드 정의 클래스
 * @author 박종식
 * @since 2021.06.15
 */
public class GlobalDefineCode {
	// 경매장 거점 코드
	public enum AUCTION_HOUSE {
		AUCTION_HOUSE_HWADONG("8808990656656"),
		AUCTION_HOUSE_HWASUN("2100"),
		AUCTION_HOUSE_JANGSU("3100");
	    
	    private final String value;
	    
		AUCTION_HOUSE(String value){
	        this.value = value;
	            
	    }
	    
	    public String getValue(){
	        return value;
	    }
	 
	}
	
	public static final String AUCTION_HOUSE_HWADONG = "8808990656656";
	public static final String AUCTION_HOUSE_HWASUN = "2100";
	public static final String AUCTION_HOUSE_JANGSU = "3100";

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
	public static final String AUCTION_STATUS_COMPLETED = "8006"; // 경매 출품 건 완료 상태
	public static final String AUCTION_STATUS_FINISH = "8007"; // 경매 종료 상태

	// 경매 시작 카운트 다운 상태
	public static final String AUCTION_COUNT_DOWN_READY = "R"; // 경매 시작 카운트 다운 준비 상태
	public static final String AUCTION_COUNT_DOWN = "C"; // 경매 시작 카운트 다운 상태
	public static final String AUCTION_COUNT_DOWN_COMPLETED = "F"; // 경매 시작 카운트 다운 완료 상태

	// 접속 요청 결과
	public static final String CONNECT_SUCCESS = "2000"; // 서버 접속 성공
	public static final String CONNECT_FAIL = "2001"; // 서버 접속 실패
	public static final String CONNECT_DUPLICATE = "2002"; // 서버 중복 접속
	public static final String CONNECT_ETC_ERROR = "2003"; // 기타 장애
	public static final String CONNECT_RUN_FAIL = "2004"; // 프로그램 실행 불가
	public static final String CONNECT_EXPIRE_WATCHER = "2005"; // 관전자 접속 만료

	//경매 결과 코드
	public static final String AUCTION_RESULT_CODE_SUCCESS = "01";		//낙찰
	public static final String AUCTION_RESULT_CODE_FAIL = "02";			//유찰
	
	// 요청 결과 미존재
	public static final String RESPONSE_REQUEST_NOT_RESULT = "4001";
	// 요청 실패
	public static final String RESPONSE_REQUEST_FAIL = "4002";
	// 시작가 이하 응찰 시도
	public static final String RESPONSE_REQUEST_BIDDING_LOW_PRICE = "4003";
	// 출품 이관 전 상태
	public static final String RESPONSE_NOT_TRANSMISSION_ENTRY_INFO = "4004";
	// 응찰 취소 불가
	public static final String RESPONSE_DENIED_CANCEL_BIDDING = "4005";
	// 정상 응찰 응답
	public static final String RESPONSE_SUCCESS_BIDDING = "4006";
	// 정상 응찰 취소 응답 
	public static final String RESPONSE_SUCCESS_CANCEL_BIDDING = "4007";

	// 프로그램 사용자 로그인 요청 타입
	public static final String AUCTION_LOGIN_TYPE_MANAGER = "MANAGER"; // 관리자
	public static final String AUCTION_LOGIN_TYPE_AUCTIONMEMBER = "AUCTIONMEMBER"; // 경매회원
	public static final String AUCTION_LOGIN_AUTH_TYPE_OTP = "ETCMEMBER"; // 일반(관전)회원
}