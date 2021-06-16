package com.nh.auction.netty;

import com.nh.auction.interfaces.NettyControllable;

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
	 * @Description 네티 접속 해제
	 */
	public void disconnect() {
		mClient.stopClient();
	}

}
