package com.nh.auction.controller;

import java.net.InetSocketAddress;

import com.nh.auction.interfaces.NettyControllable;
import com.nh.auction.models.AuctionCheckSession;
import com.nh.auction.models.AuctionCountDown;
import com.nh.auction.models.Bidding;
import com.nh.auction.models.ConnectionInfo;
import com.nh.auction.models.ExceptionCode;
import com.nh.auction.models.ToastMessage;
import com.nh.auction.netty.AuctionNettyClient;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

import io.netty.channel.Channel;
import javafx.application.Platform;
import javafx.stage.Stage;

public class BaseAuctionController implements NettyControllable {

	protected Stage mStage; // 현재 Stage

	protected AuctionNettyClient mClient; // 네티 접속 객체

	/**
	 * 소켓 서버 접속
	 */
	protected void createClient() {
		mClient = new AuctionNettyClient.Builder(NETTY_INFO.AUCTION_HOST, NETTY_INFO.AUCTION_PORT).setController(this)
				.buildAndRun();
	}

	@Override
	public void onActiveChannel(Channel channel) {
		Platform.runLater(()->{
			if (channel != null) {

				InetSocketAddress localAddress = (InetSocketAddress) channel.remoteAddress();

				int activeChannelPort = localAddress.getPort();

				System.out.println("port : " + activeChannelPort);
			}
		});
	}

	@Override
	public void onChannelInactive(int port) {
	}

	@Override
	public void exceptionCaught(int port) {
	}

	@Override
	public void onToastMessage(ToastMessage toastMessage) {
	}

	@Override
	public void onExceptionCode(ExceptionCode exceptionCode) {
	}

	@Override
	public void onConnectionInfo(ConnectionInfo connectionInfo) {
	}

	@Override
	public void onAuctionCheckSession(AuctionCheckSession auctionCheckSession) {
	}

	@Override
	public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
	}

	@Override
	public void onBidding(Bidding bidding) {
	}

}
