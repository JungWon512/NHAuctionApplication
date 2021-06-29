package com.nh.controller.utils;

/**
 * 공통 Define
 * 
 * @author jhlee
 *
 */
public class GlobalDefine {

	/**
	 * 어플리케이션 정보
	 */
	public static final class APPLICATION_INFO {

		public static final String RELEASE_VERION = "V1.0.7"; // application 버전

		public static final String RELEASE_DATE = "20201129"; // 업데이트 날짜

	}

	/**
	 * 네티 정보
	 */
	public static final class NETTY_INFO {

		public static final char DELIMITER = '|'; // 소켓 통신 메시지 구분자

		public static final String DELIMITER_REGEX = "\\|"; // 소켓 통신 메시지를 split할 때 사용할 구분자의 정규표현식

		public static final int NETTY_MAX_FRAME_LENGTH = 1024; // 네티 패킷 사이즈

	}

	/**
	 * 경매 관련 정보
	 */
	public static final class AUCTION_INFO {

//		public static final String AUCTION_HOST = "192.168.0.34"; // Server Host 내꺼
		public static final String AUCTION_HOST = "192.168.0.18"; // Server Host pc
//		public static final String AUCTION_HOST = "192.168.0.23"; // Server Host  팀장님

		public static final int AUCTION_PORT = 4001; // Server Port
		
		public static final String AUCTION_MEMBER = "C000011005"; // 테스트 회원 번호
		
		// 경매 진행 상태
		public static final String AUCTION_STATUS_NONE = "8000";
		public static final String AUCTION_STATUS_READY = "8001"; // 경매 준비 상태
		public static final String AUCTION_STATUS_START = "8002"; // 경매 시작 상태
		public static final String AUCTION_STATUS_SLOWDOWN = "8003"; // 경매 자동상승 상태
		public static final String AUCTION_STATUS_PROGRESS = "8004"; // 경매 진행 상태
		public static final String AUCTION_STATUS_COMPETITIVE = "8005"; // 경매 경쟁 상태
		public static final String AUCTION_STATUS_SUCCESS = "8006"; // 경매 낙찰 상태
		public static final String AUCTION_STATUS_FAIL = "8007"; // 경매 유찰 상태
		public static final String AUCTION_STATUS_STOP = "8008"; // 경매 정지 상태
		public static final String AUCTION_STATUS_COMPLETED = "8009"; // 경매 출품 건 완료 상태
		public static final String AUCTION_STATUS_FINISH = "8010"; // 경매 종료 상태
	}
	
	/**
	 * 파일 관련 정보
	 * @author jhlee
	 *
	 */
	public static final class FILE_INFO{
		public static final String AUCTION_LOG_FILE_PATH = "c:/NHAuction/LogFile/";
		public static final String AUCTION_LOG_FILE_EXTENSION = ".txt";
	}

}
