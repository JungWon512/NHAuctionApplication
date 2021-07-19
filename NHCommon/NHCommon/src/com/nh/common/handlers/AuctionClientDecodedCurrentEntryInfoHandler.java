package com.nh.common.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.server.models.CurrentEntryInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedCurrentEntryInfoHandler extends SimpleChannelInboundHandler<CurrentEntryInfo> {
	private static final Logger mLogger = LoggerFactory.getLogger(AuctionClientDecodedCurrentEntryInfoHandler.class);
	private final NettyControllable mController;

	public AuctionClientDecodedCurrentEntryInfoHandler(NettyControllable controller) {
		this.mController = controller;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CurrentEntryInfo currentEntryInfo) throws Exception {
		mLogger.info("AuctionClientDecodedCurrentEntryInfoHandler:channelRead0 : " + currentEntryInfo.getEncodedMessage());

		if (mController != null) {
			mController.onCurrentEntryInfo(currentEntryInfo);
		}
	}
}
