package com.nh.controller.utils;

import com.nh.controller.model.AdminData;
import com.nh.controller.model.AuctionRound;

/**
 * 공통 Define
 *
 * @author jhlee
 */
public class GlobalDefine {

	/**
	 * 어플리케이션 정보
	 */
	public static final class APPLICATION_INFO {

		public static final String RELEASE_VERION = "V1.0.7"; // application 버전

		public static final String RELEASE_DATE = "20201129"; // 업데이트 날짜
		
		public static boolean isTestMode = false;		//테스트 플래그
	}

	/**
	 * 네티 정보
	 */
	public static final class NETTY_INFO {

		public static final char DELIMITER = '|'; // 소켓 통신 메시지 구분자

		public static final String DELIMITER_REGEX = "\\|"; // 소켓 통신 메시지를 split할 때 사용할 구분자의 정규표현식

		public static final int NETTY_MAX_FRAME_LENGTH = 1024; // 네티 패킷 사이즈
	}

	public static final class BILLBOARD {

		public static final char STX = '\u0002'; // UDP 통신 TCC start
		public static final char ETX = '\u0003'; // UDP 통신 TCC en
		public static final String DELIMITER = "|"; // UDP 전광판

		public static final char START_CODE = 'S'; // UDP 전광판 시작
		public static final char INIT_CODE = 'L'; // UDP 전광판 초기화
		public static final char FINISH_CODE = 'E'; // UDP 전광판 종료
		public static final char TIME_CODE = 'T'; // UDP 전광판 시간 전송
		public static final char DATA_CODE = '1'; // UDP 전광판 데이터 전송
		public static final String NOTE_CODE = "B42"; // UDP 전광판 비고(흐름타입) 전송
		public static final char COUNTDOWN_CODE = 'N'; // UDP 통신 카운드다운
	}

	public static final class PDP {
		public static final char STX = '\u0002'; // UDP 통신 TCC start
		public static final char ETX = '\u0003'; // UDP 통신 TCC en
		public static final String DELIMITER = "|"; // UDP 전광판

		public static final char START_CODE = 'S'; // UDP 전광판 시작
		public static final char INIT_CODE = 'L'; // UDP 전광판 초기화
		public static final char FINISH_CODE = 'E'; // UDP 전광판 종료
		public static final char DATA_CODE = '1'; // UDP 전광판 데이터 전송
		public static final char CLEAR_CODE = 'C'; // UDP 통신 클리어
		public static final char COUNTDOWN_CODE = 'N'; // UDP 통신 카운드다운
	}

	/**
	 * 관리자 정보
	 */
	public static final class ADMIN_INFO {

		public static AdminData adminData = null;

	}

	/**
	 * 경매 관련 정보
	 */
	public static final class AUCTION_INFO {
		
		public static AuctionRound auctionRoundData = null;
		
//		 public static final String AUCTION_HOST = "192.168.0."; // Server Host default
//		public static final String AUCTION_HOST = "192.168.0.34"; // Server Host 내꺼
//		public static final String AUCTION_HOST = "192.168.0.13"; // Server Host 도히꺼
//		public static final String AUCTION_HOST = "192.168.0.18"; // Server Host pc
		public static final String AUCTION_HOST = "192.168.0.23"; // Server Host  팀장님
//		public static final String AUCTION_HOST = "192.168.0.25"; // Server Host  홍민
//		public static final String AUCTION_HOST = "192.168.0.23"; // dev local server
//        public static final String AUCTION_HOST = "115.41.222.25"; // dev remote server

		public static final int AUCTION_PORT = 5001; // Server Port

		public static final String AUCTION_MEMBER = "admin1"; // 테스트 회원 번호

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

		public static final int AUCTION_OBJ_DSC_1 = 1; // 경매 구분 - 송아지
		public static final int AUCTION_OBJ_DSC_2 = 2; // 경매 구분 - 비육우
		public static final int AUCTION_OBJ_DSC_3 = 3; // 경매 구분 - 번육우

		public static final int MULTIPLICATION_BIDDER_PRICE_1000 = 1; // 응찰 기본 단위 -비육우
		public static final int MULTIPLICATION_BIDDER_PRICE_10000 = 10000; // 응찰 기본 단위 -송아지,번식우

		public static final String BIDDER_STATUS_L = "L"; // 응찰자 접속 해제 여부
		
		public static final String LOG_AUCTION_START = "0"; 		// 출품 경매 시작 로그 기본값
		public static final String LOG_AUCTION_FINISH = "99999999"; // 출품 경매 종료 로그 기본값
		
	}

	/**
	 * 파일 관련 정보
	 *
	 * @author jhlee
	 */
	public static final class FILE_INFO {
		public static final String AUCTION_LOG_FILE_PATH = "c:/NHAuction/LogFile/";
		public static final String AUCTION_LOG_FILE_EXTENSION = ".txt";
		public static final String RESOURCES_SOUND_PATH = "/com/nh/controller/resource/sounds/"; // 사운드 경로

		public static final String LOCAL_SOUND_DING = RESOURCES_SOUND_PATH + "ding.wav"; // 경매 진행 카운트다운 사운드 경로
		public static final String LOCAL_SOUND_START = RESOURCES_SOUND_PATH + "edasstart.wav"; // 경매 시작 사운드 경로
		public static final String LOCAL_SOUND_END = RESOURCES_SOUND_PATH + "edasend.wav"; // 경매 종료 사운드 경로

	}

	public static final class ETC_INFO {
		public static final String AUCTION_SEARCH_PARAM_S = "S"; // 낙찰만
		public static final String AUCTION_SEARCH_PARAM_P = "P"; // 보류만
		public static final String AUCTION_SEARCH_PARAM_SP = "SP"; // 낙찰&보류
		public static final String AUCTION_DATA_MODIFY_M = "M"; // 데이터 수정 여부
	}

}
