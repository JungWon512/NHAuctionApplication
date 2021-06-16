package com.nh.auction.netty;

import com.nh.auction.interfaces.NettyControllable;

public class AuctionDelegate {

	private static AuctionDelegate instance = null;

	public static synchronized AuctionDelegate getInstance() {

		if (instance == null) {
			instance = new AuctionDelegate();
		}

		return instance;
	}

	/**
	 * 서버 접속
	 * @param host_
	 * @param port_
	 * @param controllable
	 */
	private void createClients(String host_, int port_, NettyControllable controllable) {

		AuctionNettyClient client = new AuctionNettyClient.Builder(host_, port_)
				.setController(controllable)
				.buildAndRun();

	}
}
