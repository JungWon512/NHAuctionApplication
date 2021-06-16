package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.controller.models.EntryInfo;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@Sharable
public final class AuctionServerDecodedEntryInfoHandler extends SimpleChannelInboundHandler<EntryInfo> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerDecodedEntryInfoHandler.class);

	private final AuctionServer mAuctionServer;
	private final Auctioneer mAuctionScheduler;

	private ChannelGroup mControllerChannels = null;
	private ChannelGroup mBidderChannels = null;
	private ChannelGroup mWatcherChannels = null;
	private ChannelGroup mAuctionResultMonitorChannels = null;
	private ChannelGroup mConnectionMonitorChannels = null;
	private Map<ChannelId, ConnectionInfo> mConnectorInfoMap;

	public AuctionServerDecodedEntryInfoHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
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
	protected void channelRead0(ChannelHandlerContext ctx, EntryInfo entryInfo) throws Exception {
		if (mControllerChannels.contains(ctx.channel()) == true) {
			mLogger.info(entryInfo.getEntryNum() + "번 출품 자료 수신");
			mAuctionServer.itemAdded(entryInfo.getEncodedMessage());
		} else {
			mLogger.info("비정상 채널에서 출품 자료 전송을 하였으나, 해당 요청이 거부되었습니다.");
		}
	}
}
