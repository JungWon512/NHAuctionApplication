package com.nh.share.setting;

public class AuctionShareSetting {
	public static final char DELIMITER = '|'; // 소켓 통신 메시지 구분자
	public static final String DELIMITER_REGEX = "\\|"; // 소켓 통신 메시지를 split할 때 사용할 구분자의 정규표현식
	public static final int NETTY_MAX_FRAME_LENGTH = 1024; // 소켓 통신 메시지 최대 길이

	public static final int AUCTION_SERVER_DESTROY_TIMER = (5000);
}
