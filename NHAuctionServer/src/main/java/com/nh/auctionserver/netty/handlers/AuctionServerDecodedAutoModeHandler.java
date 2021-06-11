package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.controller.models.AutoMode;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@Sharable
public final class AuctionServerDecodedAutoModeHandler extends SimpleChannelInboundHandler<AutoMode> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerDecodedAutoModeHandler.class);

	private final AuctionServer mAuctionServer;
	private final Auctioneer mAuctionScheduler;

	private ChannelGroup mControllerChannels = null;
	private ChannelGroup mBidderChannels = null;
	private ChannelGroup mWatcherChannels = null;
	private ChannelGroup mAuctionResultMonitorChannels = null;
	private ChannelGroup mConnectionMonitorChannels = null;
	private Map<ChannelId, ConnectionInfo> mConnectorInfoMap;

	public AuctionServerDecodedAutoModeHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
			Map<ChannelId, ConnectionInfo> connectorInfoMap, ChannelGroup controllerChannels,
			ChannelGroup bidderChannels, ChannelGroup watcherChannels, ChannelGroup auctionResultMonitorChannels,
			ChannelGroup connectionMonitorChannels) {
		mAuctionServer = auctionServer;
		mConnectorInfoMap = connectorInfoMap;
		mAuctionScheduler = auctionSchedule;
		mControllerChannels = controllerChannels;
		mBidderChannels = bidderChannels;
		mWatcherChannels = watcherChannels;
		mAuctionResultMonitorChannels = auctionResultMonitorChannels;
		mConnectionMonitorChannels = connectionMonitorChannels;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AutoMode autoMode) throws Exception {
		if (mControllerChannels.contains(ctx.channel()) == true) {
			mLogger.info("정상 채널에서 경매 진행 모드를 자동으로 요청하였습니다.");
			mAuctionServer.itemAdded(autoMode.getEncodedMessage());
		} else {
			mLogger.info("비정상 채널에서 경매 진행 모드를 자동으로 요청하였으나, 요청이 거부되었습니다.");
		}
	}
}
