package com.nh.common.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.server.models.RequestAuctionResult;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedRequestAuctionResultHandler extends SimpleChannelInboundHandler<RequestAuctionResult> {
	private static final Logger mLogger = LoggerFactory
			.getLogger(AuctionClientDecodedRequestAuctionResultHandler.class);
	private final NettyControllable mController;

	public AuctionClientDecodedRequestAuctionResultHandler(NettyControllable controller) {
		this.mController = controller;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RequestAuctionResult requestAuctionResult) throws Exception {
		mLogger.info("AuctionClientDecodedRequestAuctionResultHandler:channelRead0 : "
				+ requestAuctionResult.getEncodedMessage());

		if (mController != null) {
			mController.onRequestAuctionResult(requestAuctionResult);
		}
	}
}
