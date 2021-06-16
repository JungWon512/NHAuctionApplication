package com.nh.auction.netty;

import com.nh.auction.interfaces.NettyControllable;
import com.nh.auction.interfaces.NettySendable;

public class AuctionDelegate {

	private static AuctionDelegate instance = null;

	private AuctionNettyClient mClient; // 네티 접속 객체

	public static synchronized AuctionDelegate getInstance() {

		if (instance == null) {
			instance = new AuctionDelegate();
		}

		return instance;
	}

	/**
	 * @Description 서버 접속
	 * @param host_
	 * @param port_
	 * @param controllable
	 */
	public void createClients(String host_, int port_, NettyControllable controllable) {
		mClient = new AuctionNettyClient.Builder(host_, port_).setController(controllable).buildAndRun();
	}

	/**
	 * 객체를 송신할 때 사용한다.
	 * 
	 * @param message 보낼 객체
	 */
	public void sendMessage(NettySendable object) {
		if (!isEmptyClient()) {
			mClient.sendMessage(object.getEncodedMessage());
		}
	}

	/**
	 * 접속 상태 확인
	 * 
	 * @return
	 */
	public boolean isActive() {

		if (!isEmptyClient() && !mClient.isEmptyChannel()) {
			return mClient.getChannel().isActive();
		}
		
		return false;
	}

	private boolean isEmptyClient() {
		if (mClient != null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @Description 네티 접속 해제
	 */
	public void disconnect() {
		mClient.stopClient();
	}

}
