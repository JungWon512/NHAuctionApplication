package com.nh.common.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.common.models.CancelBidding;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedCancelBiddingHandler extends SimpleChannelInboundHandler<CancelBidding> {
	private static final Logger mLogger = LoggerFactory.getLogger(AuctionClientDecodedCancelBiddingHandler.class);
	private final NettyControllable mController;

	public AuctionClientDecodedCancelBiddingHandler(NettyControllable controller) {
		this.mController = controller;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CancelBidding cancelBidding) throws Exception {
		mLogger.info("AuctionClientDecodedCancelBiddingHandler:channelRead0 : " + cancelBidding.getEncodedMessage());

		if (mController != null) {
			mController.onCancelBidding(cancelBidding);
		}
	}
}
