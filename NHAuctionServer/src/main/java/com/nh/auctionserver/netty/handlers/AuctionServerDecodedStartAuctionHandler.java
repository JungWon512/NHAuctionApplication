package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.auctionserver.setting.AuctionServerSetting;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.controller.models.StartAuction;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@Sharable
public final class AuctionServerDecodedStartAuctionHandler extends SimpleChannelInboundHandler<StartAuction> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerDecodedStartAuctionHandler.class);

	private final AuctionServer mAuctionServer;
	private final Auctioneer mAuctionScheduler;

	private ChannelGroup mControllerChannels = null;
	private ChannelGroup mBidderChannels = null;
	private ChannelGroup mWatcherChannels = null;
	private ChannelGroup mAuctionResultMonitorChannels = null;
	private ChannelGroup mConnectionMonitorChannels = null;
	private Map<ChannelId, ConnectionInfo> mConnectorInfoMap;

	public AuctionServerDecodedStartAuctionHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
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
	protected void channelRead0(ChannelHandlerContext ctx, StartAuction auctionStart) throws Exception {
		mLogger.info("경매 자동 진행 모드 : " + auctionStart.getFlagAutoMode());
		mLogger.info("경매 진행 시작 : " + auctionStart.getEntryNum());

		// 경매 자동 시작 여부 확인
		if (auctionStart.getFlagAutoMode().equals("Y")) {
			AuctionServerSetting.AUCTION_AUTO_MODE = true;
		} else {
			AuctionServerSetting.AUCTION_AUTO_MODE = false;
		}

		if (AuctionServerSetting.AUCTION_START_STATUS) {
			mLogger.info("경매가 이미 시작된 상태입니다.");
		} else if (mControllerChannels.contains(ctx.channel()) == true) {
			mLogger.info("정상 채널에서 경매 시작을 요청하였습니다.");
			mAuctionServer.itemAdded(auctionStart.getEncodedMessage());
		} else {
			mLogger.info("비정상 채널에서 경매 시작을 요청하였으나, 요청이 거부되었습니다.");
		}
	}
}
