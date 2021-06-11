package com.nh.share.code;

/**
 * 
 * @ClassName GlobalDefineCode.java
 * @Description 경매 시스템 예외 상황 공통 코드 정의 클래스
 * @author 박종식
 * @since 2019.10.31
 */
public class GlobalDefineCode {
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

    // PC 채널
    public static final String USE_CHANNEL_PC = "PC";
    // 경매장 단말 채널
    public static final String USE_CHANNEL_AUCTION_HOUSE = "AH";
    // 안드로이드 채널
    public static final String USE_CHANNEL_ANDROID = "ANDROID";
    // iOS 채널
    public static final String USE_CHANNEL_IOS = "IOS";
    // 부재자 채널
    public static final String USE_CHANNEL_ABSENTEE = "AB";
    // 이미지 공지사항 파라미터 코드
    public static final String USE_CHANNEL_AUCTION_PC_HOUSE_CODE = "01";
    // 경매장 단말 채널 코드
    public static final String USE_CHANNEL_AUCTION_HOUSE_CODE = "01";
    // PC 채널 코드
    public static final String USE_CHANNEL_PC_CODE = "02";
    // 안드로이드 채널 코드
    public static final String USE_CHANNEL_ANDROID_CODE = "03";
    // iOS 채널 코드
    public static final String USE_CHANNEL_IOS_CODE = "04";
    // 부재자 채널 코드
    public static final String USE_CHANNEL_ABSENTEE_CODE = "05";

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

    // 경매 시작 카운트 다운 상태
    public static final String AUCTION_COUNT_DOWN_READY = "R"; // 경매 시작 카운트 다운 준비 상태
    public static final String AUCTION_COUNT_DOWN = "C"; // 경매 시작 카운트 다운 상태
    public static final String AUCTION_COUNT_DOWN_COMPLETED = "F"; // 경매 시작 카운트 다운 완료 상태

    // 접속 요청 결과
    public static final String CONNECT_SUCCESS = "2000"; // 서버 접속 성공
    public static final String CONNECT_FAIL = "2001"; // 서버 접속 실패
    public static final String CONNECT_DUPLICATE = "2002"; // 서버 중복 접속
    public static final String CONNECT_DUPLICATE_FAIL = "2003"; // 서버 중복 접속 불가
    public static final String CONNECT_RUN_FAIL = "2004"; // 프로그램 실행 불가
    public static final String CONNECT_EXPIRE_WATCHER = "2005"; // 관전자 접속 만료

    // 요청 결과 미존재
    public static final String RESPONSE_REQUEST_NOT_RESULT_EXCEPTION = "4001";
    // 요청 실패
    public static final String RESPONSE_REQUEST_FAIL_EXCEPTION = "4002";
    // 중복 로그인
    public static final String RESPONSE_DUPLECATE_ACCOUNT_EXCEPTION = "5001";

    // 프로그램 사용자 로그인 요청 타입
    public static final String AUCTION_LOGIN_TYPE_MANAGER = "MANAGER"; // 관리자 (업무 시스템)
    public static final String AUCTION_LOGIN_TYPE_AUCTIONMEMBER = "AUCTIONMEMBER"; // 경매 회원
    public static final String AUCTION_LOGIN_AUTH_TYPE_OTP = "OTP"; // 로그인 인증 OTP
    public static final String AUCTION_LOGIN_AUTH_TYPE_SMS = "SMS"; // 로그인 인증 SMS

    // 프로그램 사용자 로그인 상태 경매회원 로그인 시
    public static final String AUCTION_LOGIN_STATUS_L003_1 = "L003-1"; // 주민등록번호 변경 요청
    public static final String AUCTION_LOGIN_STATUS_L003_2 = "L003-2"; // 암호가 초기화 상태이므로 변경 요청
    public static final String AUCTION_LOGIN_STATUS_L003_3 = "L003-3"; // 비밀번호와 아이디가 동일한 상태 변경 요청
    public static final String AUCTION_LOGIN_STATUS_L003_4 = "L003-4"; // 휴먼계정
    public static final String AUCTION_LOGIN_STATUS_L003_5 = "L003-5"; // 비밀번호 6개월 지난 상태이므로 변경 요청
    public static final String AUCTION_LOGIN_STATUS_L003_6 = "L003-6"; // 로그인 미 성공시
    public static final String AUCTION_LOGIN_STATUS_L003_7 = "L003-7"; // 비밀번호 찾기를 통해 비밀번호 변경된 경우 새로 변경 요청
    public static final String AUCTION_LOGIN_STATUS_L003_8 = "L003-8"; // 로그인은 되었으나 회원정보를 폿가져오는 경우 메세지 처리(회원사 회원등록이
                                                                       // 잘못된// 경우 발생)<권한이 정상적으로 등록되지 않았습니다.관리자에게
                                                                       // 문의하세요.>
    public static final String AUCTION_LOGIN_STATUS_L003_9 = "L003-9"; // 오토옥션 회원가입 서류심사중 (중고차개선 프로젝트)
    public static final String AUCTION_LOGIN_STATUS_L003_10 = "L003-10"; // 오토옥션 회원가입 가입승인중 (중고차개선 프로젝트)
    public static final String AUCTION_LOGIN_STATUS_L003_11 = "L003-11"; // 오토옥션 회원가입 가입완료(경매교육참여) (중고차개선 프로젝트)
    public static final String AUCTION_LOGIN_STATUS_L003_12 = "L003-12"; // 오토옥션 회원탈퇴 요청 진행결과(심사중) (중고차개선 프로젝트)
    public static final String AUCTION_LOGIN_STATUS_L003_13 = "L003-13"; // 오토옥션 회원탈퇴 진행결과 (최종승인중) (중고차개선 프로젝트)
    public static final String AUCTION_LOGIN_STATUS_L003_14 = "L003-14"; // 오토옥션 회원탈퇴 진행결과(보증금 반환처리중) (중고차개선 프로젝트)
    public static final String AUCTION_LOGIN_STATUS_L003_15 = "L003-15"; // 오토옥션 회원탈퇴 완료 1주일 안내 (중고차개선 프로젝트)
    public static final String AUCTION_LOGIN_STATUS_L003_16 = "L003-16"; // 오토옥션 탁송기사 로그인 (중고차개선 프로젝트)
    public static final String AUCTION_LOGIN_STATUS_L003_17 = "L003-17"; // 오토옥션 회원가입 가입승인중 반려 (중고차개선 프로젝트)
    public static final String AUCTION_LOGIN_STATUS_L003_18 = "L003-18"; // 존재하지 않는 ID 입니다.
    public static final String AUCTION_LOGIN_STATUS_L003_19 = "L003-19"; // 연회비 조회 시 오류 발생
    public static final String AUCTION_LOGIN_STATUS_L003_20 = "L003-20"; // 비밀번호 5회이상 오류

    // 프로그램 사용자 로그인 상태 : 일반회원 로그인 시
    public static final String SELLING_PLATFORM_LOGIN_STATUS_MBR_1001 = "MBR1001"; // 아이디를 입력해 주세요.
    public static final String SELLING_PLATFORM_LOGIN_STATUS_MBR_2001 = "MBR2001"; // 비밀번호를 입력해 주세요.
    public static final String SELLING_PLATFORM_LOGIN_STATUS_MBR_3001 = "MBR3001"; // 로그인에 실패하였습니다.
    public static final String SELLING_PLATFORM_LOGIN_STATUS_MBR_4001 = "MBR4001"; // 회원 아이디 또는 비밀번호가 일치하지 않습니다.
    public static final String SELLING_PLATFORM_LOGIN_STATUS_MBR_4002 = "MBR4002"; // 딜러회원 가입을 위한 심사가 진행중입니다. 빠른처리를 원하시면
                                                                                   // 고객센터로 문의해 주세요. 고객센터:02-1234-1234
    public static final String SELLING_PLATFORM_LOGIN_STATUS_MBR_4003 = "MBR4003"; // 딜러(또는 단체) 회원 가입심사가 반려되었습니다.[반려사유:
                                                                                   // 매매종사원증 유효기간 기재 오류] 반려사유를 확인하신 후
                                                                                   // 재미사를원하시면 고객센터로 문의해 주세요. 고객센터 :
                                                                                   // 02-1234-1234
    public static final String SELLING_PLATFORM_LOGIN_STATUS_MBR_5000 = "MBR5000"; // 없음. (로그인 처리 후 메인으로 이동)
    public static final String SELLING_PLATFORM_LOGIN_STATUS_MBR_5001 = "MBR5001"; // 소중한 개인정보를 안전하게 관리하고 개인정보 도용으로 인한
                                                                                   // 피해방지를 위해 6개월마다 비밀번호 변경을 권장하고 있습니다.
                                                                                   // 불편하시더라도 비밀번호 변경절차를 거치신 후 서비스를 이용해
                                                                                   // 주세요.
    public static final String SELLING_PLATFORM_LOGIN_STATUS_MBR_5002 = "MBR5002"; // 없음. (로그인 처리 후 메인으로 이동)
    public static final String SELLING_PLATFORM_LOGIN_STATUS_MBR_5003 = "MBR5003"; // 없음. (로그인 처리 후 메인으로 이동)
    // 로그인 토큰 에러
    public static final String LOGIN_RESULT_ERROR_CODE_001 = "err001"; // 발급받은 토큰이 존재하지 않음
    public static final String LOGIN_RESULT_ERROR_CODE_002 = "err002"; // 토큰이 일치하지 않습니다.
    public static final String LOGIN_RESULT_ERROR_CODE_003 = "err003"; // 토큰이 만료 되었습니다.
    // 인증 토큰 코드
    public static final String LOGIN_TOKEN_REFRESH = "R"; // 리프레시 토큰
    public static final String LOGIN_TOKEN_AUTH = "A"; // 인증 토큰
    // 로그인 시 플래그값
    public static final String AUCTION_CONTROL_FLAG_01 = "01"; // 경매 회원 - 정상
    public static final String AUCTION_CONTROL_FLAG_03 = "03"; // 경매 회원 통제 - 관전

    // 경매 종류
    public static final String AUCTION_TYPE_REALTIME = "20"; // 실시간 경매
    public static final String AUCTION_TYPE_SPOT = "40"; // SPOT 경매
    public static final String AUCTION_TYPE_FIXTIME = "30"; // 지정시간 경매

    // 경매 상태 (API)
    public static final String AUCTION_API_STATUS_STANDBY = "10"; // 대기
    public static final String AUCTION_API_STATUS_START = "11"; // 진행
    public static final String AUCTION_API_STATUS_COMPLETED = "20"; // 완료

    // 낙/유찰 전송 결과 코드
    public static final String REQUEST_AUCTION_RESULT_SUCCESS = "30001"; // 전송 성공
    public static final String REQUEST_AUCTION_RESULT_FAIL = "30002"; // 전송 실패

    // 낙/유찰 전송 Param 경매 결과 코드
    public static final String REQUEST_PARAM_AUCTION_RESULT_SUCCESS = "02"; // 낙찰
    public static final String REQUEST_PARAM_AUCTION_RESULT_FAIL = "03"; // 유찰

    // 경매장 거점 코드
    public static final String AUCTION_HOUSE_HWADONG = "1100";
    public static final String AUCTION_HOUSE_HWASUN = "2100";
    public static final String AUCTION_HOUSE_JANGSU = "3100";
}
