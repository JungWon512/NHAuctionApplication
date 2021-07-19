package com.nh.share.server.interfaces;

import com.nh.share.interfaces.NettySendable;

/**
 * 서버에서 보내지는 모델은 이 인터페이스의 구현체여야 한다.
 *
 */
public interface FromAuctionServer extends NettySendable {

	/**
	 * 서버에서 보내지는 메세지의 확인 플래그. getEncodedString()의 반환 String 맨 앞에 위치시키도록 한다.
	 * 
	 * 사용 예) public String getEncodedMessage(String delimiter) { return
	 * String.format("%c%c%s%s%s%d", ORIGIN, TYPE, delimiter, bidder, delimiter,
	 * price); }
	 * 
	 * switch (message.charAt(0)) { case FromAuctionServer.ORIGIN: ... }
	 * 
	 */
	static final char ORIGIN = 'S';
}
