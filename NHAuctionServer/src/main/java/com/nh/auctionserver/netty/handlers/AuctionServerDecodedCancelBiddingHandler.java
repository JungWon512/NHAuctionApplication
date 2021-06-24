package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.CancelBidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.server.models.ResponseCode;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@Sharable
public final class AuctionServerDecodedCancelBiddingHandler extends SimpleChannelInboundHandler<CancelBidding> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerDecodedCancelBiddingHandler.class);

	private final AuctionServer mAuctionServer;
	private final Auctioneer mAuctionScheduler;

	private Map<String, ChannelGroup> mControllerChannelsMap = null;
	private Map<String, ChannelGroup> mBidderChannelsMap = null;
	private Map<String, ChannelGroup> mWatcherChannelsMap = null;
	private Map<String, ChannelGroup> mAuctionResultMonitorChannelsMap = null;
	private Map<String, ChannelGroup> mConnectionMonitorChannelsMap = null;
	private Map<ChannelId, ConnectionInfo> mConnectionInfoMap;

	public AuctionServerDecodedCancelBiddingHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
			Map<ChannelId, ConnectionInfo> connectionInfoMap, Map<String, ChannelGroup> controllerChannelsMap,
			Map<String, ChannelGroup> bidderChannelsMap, Map<String, ChannelGroup> watcherChannelsMap,
			Map<String, ChannelGroup> auctionResultMonitorChannelsMap,
			Map<String, ChannelGroup> connectionMonitorChannelsMap) {
		mAuctionServer = auctionServer;
		mConnectionInfoMap = connectionInfoMap;
		mAuctionScheduler = auctionSchedule;
		mControllerChannelsMap = controllerChannelsMap;
		mBidderChannelsMap = bidderChannelsMap;
		mWatcherChannelsMap = watcherChannelsMap;
		mAuctionResultMonitorChannelsMap = auctionResultMonitorChannelsMap;
		mConnectionMonitorChannelsMap = connectionMonitorChannelsMap;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CancelBidding cancelBidding) throws Exception {
		if (mConnectionInfoMap.containsKey(ctx.channel().id())
				&& mBidderChannelsMap.get(cancelBidding.getAuctionHouseCode()).contains(ctx.channel())) {
			if (mAuctionScheduler.getCurrentAuctionStatus(cancelBidding.getAuctionHouseCode())
					.equals(GlobalDefineCode.AUCTION_STATUS_START)
					|| mAuctionScheduler.getCurrentAuctionStatus(cancelBidding.getAuctionHouseCode())
							.equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {

				mLogger.debug("Message ADD : " + cancelBidding.getEncodedMessage());

				mAuctionServer.itemAdded(cancelBidding.getEncodedMessage());

			} else {
				ctx.writeAndFlush(new ResponseCode(cancelBidding.getAuctionHouseCode(),
						GlobalDefineCode.RESPONSE_DENIED_CANCEL_BIDDING).getEncodedMessage() + "\r\n");
			}
		} else {
			mLogger.debug("=============================================");
			mLogger.debug("유효하지 않은 채널로 응찰 취소 요청 : " + ctx.channel().id());
			mLogger.debug(ctx.channel().id() + "를 Close 처리하였습니다.");
			mLogger.debug("=============================================");
			ctx.close();
		}
	}
}
