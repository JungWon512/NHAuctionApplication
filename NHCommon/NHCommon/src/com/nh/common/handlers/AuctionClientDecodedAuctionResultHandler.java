package com.nh.common.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.common.models.AuctionResult;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedAuctionResultHandler extends SimpleChannelInboundHandler<AuctionResult> {
	private static final Logger mLogger = LoggerFactory.getLogger(AuctionClientDecodedAuctionResultHandler.class);
	private final NettyControllable mController;

	public AuctionClientDecodedAuctionResultHandler(NettyControllable controller) {
		this.mController = controller;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AuctionResult auctionResult) throws Exception {
		mLogger.info("AuctionClientDecodedAuctionResultHandler:channelRead0 : " + auctionResult.getEncodedMessage());

		if (mController != null) {
			mController.onAuctionResult(auctionResult);
		}
	}
}
