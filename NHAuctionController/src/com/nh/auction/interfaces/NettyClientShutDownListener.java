package com.nh.auction.interfaces;

public interface NettyClientShutDownListener {
    
	/**
	 * 접속 종료 포트 리턴
	 * @param port
	 */
    public void onShutDown(int port);

}
