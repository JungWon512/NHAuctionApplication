package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.ConnectionInfo;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@Sharable
public final class AuctionServerDecodedBiddingHandler extends SimpleChannelInboundHandler<Bidding> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerDecodedBiddingHandler.class);

	private final AuctionServer mAuctionServer;
	private final Auctioneer mAuctionScheduler;

	private ChannelGroup mControllerChannels = null;
	private ChannelGroup mBidderChannels = null;
	private ChannelGroup mWatcherChannels = null;
	private ChannelGroup mAuctionResultMonitorChannels = null;
	private ChannelGroup mConnectionMonitorChannels = null;
	private Map<ChannelId, ConnectionInfo> mConnectorInfoMap;

	public AuctionServerDecodedBiddingHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
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
	protected void channelRead0(ChannelHandlerContext ctx, Bidding bidding) throws Exception {
		if (mConnectorInfoMap.containsKey(ctx.channel().id()) && mBidderChannels.contains(ctx.channel())) {
			if (mAuctionScheduler.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_START)
					|| mAuctionScheduler.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)
					|| mAuctionScheduler.getCurrentAuctionStatus()
							.equals(GlobalDefineCode.AUCTION_STATUS_COMPETITIVE)) {

				mLogger.debug("HazelcastMessage ADD : " + bidding.getEncodedMessage());

				if (bidding.getPriceInt() >= Integer.valueOf(mAuctionScheduler.getAuctionState().getStartPrice())) {
					mAuctionServer.itemAdded(bidding.getEncodedMessage());
				}
			}
		} else {
			mLogger.debug("=============================================");
			mLogger.debug("유효하지 않은 채널 : " + ctx.channel().id());
			mLogger.debug(ctx.channel().id() + "를 Close 처리하였습니다.");
			mLogger.debug("=============================================");
			ctx.close();
		}
	}
}
