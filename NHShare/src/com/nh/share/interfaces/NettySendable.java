package com.nh.share.interfaces;


/**
 * Netty로 통신할 때 Encoding, Decoding 하는 모델은 이 인터페이스의 구현체여야 한다.
 *
 */
public interface NettySendable {
	
	
	/**
	 * @return Netty를 통해 통신하기 위한 String.
	 */
	public abstract String getEncodedMessage();
}
