package com.nh.share.setting;

public class AuctionShareSetting {
	// true : 그외 클라이언트 프로그램 일 경우
	// false : 경매서버 혹은 경매스케쥴러 일 경우
	public static boolean gIsClientApplication = false;

	public static final char DELIMITER = '|'; // 소켓 통신 메시지 구분자
	public static final String DELIMITER_REGEX = "\\|"; // 소켓 통신 메시지를 split할 때 사용할 구분자의 정규표현식
	public static final int NETTY_MAX_FRAME_LENGTH = 1024; // 소켓 통신 메시지 최대 길이

	// Auction Server
	public static final String SERVER_HOST = "115.41.222.25"; // 서버 호스트
	public static final int SERVER_PORT = 5001; // 서버 포트 (가용포트 : 5001 ~ 5020)

	public static final int AUCTION_SERVER_DESTROY_TIMER = (5000);
}
