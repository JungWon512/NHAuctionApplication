package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.controller.models.PassAuction;
import com.nh.share.controller.models.PauseAuction;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@Sharable
public final class AuctionServerDecodedPassAuctionHandler extends SimpleChannelInboundHandler<PauseAuction> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerDecodedPassAuctionHandler.class);

	private final AuctionServer mAuctionServer;
	private final Auctioneer mAuctionScheduler;

	private Map<String, ChannelGroup> mControllerChannelsMap = null;
	private Map<String, ChannelGroup> mBidderChannelsMap = null;
	private Map<String, ChannelGroup> mWatcherChannelsMap = null;
	private Map<String, ChannelGroup> mAuctionResultMonitorChannelsMap = null;
	private Map<String, ChannelGroup> mConnectionMonitorChannelsMap = null;
	private Map<Object, ConnectionInfo> mConnectionInfoMap;
	private Map<String, Object> mConnectionChannelInfoMap;

	public AuctionServerDecodedPassAuctionHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
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
	protected void channelRead0(ChannelHandlerContext ctx, PauseAuction pauseAuction) throws Exception {
		mLogger.info("경매 정지 취소 요청 : " + (pauseAuction.getEntryNum()));

		if (mControllerChannelsMap.get(pauseAuction.getAuctionHouseCode()).contains(ctx.channel()) == true) {
			mLogger.info("정상 채널에서 경매 정지에 대한 취소 요청을 하였습니다.");
			mAuctionServer.itemAdded(pauseAuction.getEncodedMessage());
		} else {
			mLogger.info("비정상 채널에서 경매 정지 취소를 요청하였으나, 요청이 거부되었습니다.");
		}
	}
}
