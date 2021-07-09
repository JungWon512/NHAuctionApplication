package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.ResponseCode;

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

	private Map<String, ChannelGroup> mControllerChannelsMap = null;
	private Map<String, ChannelGroup> mBidderChannelsMap = null;
	private Map<String, ChannelGroup> mWatcherChannelsMap = null;
	private Map<String, ChannelGroup> mAuctionResultMonitorChannelsMap = null;
	private Map<String, ChannelGroup> mConnectionMonitorChannelsMap = null;
	private Map<ChannelId, ConnectionInfo> mConnectionInfoMap;

	public AuctionServerDecodedBiddingHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
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
	protected void channelRead0(ChannelHandlerContext ctx, Bidding bidding) throws Exception {
		if (mConnectionInfoMap.containsKey(ctx.channel().id())
				&& mBidderChannelsMap.get(bidding.getAuctionHouseCode()).contains(ctx.channel())) {
			if (mAuctionScheduler.getCurrentAuctionStatus(bidding.getAuctionHouseCode())
					.equals(GlobalDefineCode.AUCTION_STATUS_START)
					|| mAuctionScheduler.getCurrentAuctionStatus(bidding.getAuctionHouseCode())
							.equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {

				mLogger.debug("Message ADD : " + bidding.getEncodedMessage());

				if (bidding.getPriceInt() >= Integer
						.valueOf(mAuctionScheduler.getAuctionState(bidding.getAuctionHouseCode()).getStartPrice())) {
					
					ctx.writeAndFlush(new ResponseCode(bidding.getAuctionHouseCode(),
							GlobalDefineCode.RESPONSE_SUCCESS_BIDDING).getEncodedMessage() + "\r\n");
					
					// 응찰 정보 수집
					mAuctionServer.itemAdded(bidding.getEncodedMessage());
					
					// 응찰 정보 모니터 채널 전송
					mAuctionServer.itemAdded(new BidderConnectInfo(bidding.getAuctionHouseCode(),
							bidding.getUserNo(), GlobalDefineCode.CONNECT_CHANNEL_BIDDER,
							bidding.getChannel(), "B", bidding.getPrice()).getEncodedMessage());
				} else {
					mLogger.debug("=============================================");
					mLogger.debug("잘못 된 가격 응찰 시도 : " + bidding.getEncodedMessage());
					mLogger.debug("=============================================");
					ctx.writeAndFlush(new ResponseCode(bidding.getAuctionHouseCode(), GlobalDefineCode.RESPONSE_REQUEST_BIDDING_LOW_PRICE).getEncodedMessage() + "\r\n");
				}
			} else {
				ctx.writeAndFlush(new ResponseCode(bidding.getAuctionHouseCode(), GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage() + "\r\n");
			}
		} else {
			mLogger.debug("=============================================");
			mLogger.debug("유효하지 않은 채널에서 응찰을 시도 : " + ctx.channel().id());
			mLogger.debug(ctx.channel().id() + "를 Close 처리하였습니다.");
			mLogger.debug("=============================================");
			ctx.close();
		}
	}
}
