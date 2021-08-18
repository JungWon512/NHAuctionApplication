package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.common.models.AuctionReponseSession;
import com.nh.share.common.models.ConnectionInfo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@Sharable
public final class AuctionServerDecodedAuctionResponseSessionHandler
		extends SimpleChannelInboundHandler<AuctionReponseSession> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerDecodedAuctionResponseSessionHandler.class);

	private final AuctionServer mAuctionServer;
	private final Auctioneer mAuctionScheduler;

	private Map<String, ChannelGroup> mControllerChannelsMap = null;
	private Map<String, ChannelGroup> mBidderChannelsMap = null;
	private Map<String, ChannelGroup> mWatcherChannelsMap = null;
	private Map<String, ChannelGroup> mAuctionResultMonitorChannelsMap = null;
	private Map<String, ChannelGroup> mConnectionMonitorChannelsMap = null;
	private Map<Object, ConnectionInfo> mConnectionInfoMap;
	private Map<String, Object> mConnectionChannelInfoMap;

	public AuctionServerDecodedAuctionResponseSessionHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
			Map<Object, ConnectionInfo> connectionInfoMap, Map<String, Object> connectionChannelInfoMap, Map<String, ChannelGroup> controllerChannelsMap,
			Map<String, ChannelGroup> bidderChannelsMap, Map<String, ChannelGroup> watcherChannelsMap,
			Map<String, ChannelGroup> auctionResultMonitorChannelsMap,
			Map<String, ChannelGroup> connectionMonitorChannelsMap) {
		mAuctionServer = auctionServer;
		mConnectionInfoMap = connectionInfoMap;
		mConnectionChannelInfoMap = connectionChannelInfoMap;
		mAuctionScheduler = auctionSchedule;
		mControllerChannelsMap = controllerChannelsMap;
		mBidderChannelsMap = bidderChannelsMap;
		mWatcherChannelsMap = watcherChannelsMap;
		mAuctionResultMonitorChannelsMap = auctionResultMonitorChannelsMap;
		mConnectionMonitorChannelsMap = connectionMonitorChannelsMap;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AuctionReponseSession auctionReponseSession)
			throws Exception {

	}
}
