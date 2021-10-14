package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.server.models.AuctionBidStatus;
import com.nh.share.server.models.ResponseCode;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@Sharable
public final class AuctionServerDecodedAuctionBidStatusHandler extends SimpleChannelInboundHandler<AuctionBidStatus> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerDecodedAuctionBidStatusHandler.class);

	private final AuctionServer mAuctionServer;
	private final Auctioneer mAuctionScheduler;

	private Map<String, ChannelGroup> mControllerChannelsMap = null;
	private Map<String, ChannelGroup> mBidderChannelsMap = null;
	private Map<String, ChannelGroup> mWatcherChannelsMap = null;
	private Map<String, ChannelGroup> mAuctionResultMonitorChannelsMap = null;
	private Map<String, ChannelGroup> mConnectionMonitorChannelsMap = null;
	private Map<Object, ConnectionInfo> mConnectionInfoMap;
	private Map<String, Object> mConnectionChannelInfoMap;
	private Map<String, ChannelGroup> mStandChannelsMap = null;

	public AuctionServerDecodedAuctionBidStatusHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
			Map<Object, ConnectionInfo> connectionInfoMap, Map<String, Object> connectionChannelInfoMap,
			Map<String, ChannelGroup> controllerChannelsMap, Map<String, ChannelGroup> bidderChannelsMap,
			Map<String, ChannelGroup> watcherChannelsMap, Map<String, ChannelGroup> auctionResultMonitorChannelsMap,
			Map<String, ChannelGroup> connectionMonitorChannelsMap,
			Map<String, ChannelGroup> connectionStandChannelsMap) {
		mAuctionServer = auctionServer;
		mConnectionInfoMap = connectionInfoMap;
		mConnectionChannelInfoMap = connectionChannelInfoMap;
		mAuctionScheduler = auctionSchedule;
		mControllerChannelsMap = controllerChannelsMap;
		mBidderChannelsMap = bidderChannelsMap;
		mWatcherChannelsMap = watcherChannelsMap;
		mAuctionResultMonitorChannelsMap = auctionResultMonitorChannelsMap;
		mConnectionMonitorChannelsMap = connectionMonitorChannelsMap;
		mStandChannelsMap = connectionStandChannelsMap;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AuctionBidStatus auctionBidStatus) throws Exception {
		if (mControllerChannelsMap.containsKey(auctionBidStatus.getAuctionHouseCode())) {
			if (mControllerChannelsMap.get(auctionBidStatus.getAuctionHouseCode()).contains(ctx.channel())) {
				mLogger.info(auctionBidStatus.getEntryNum() + "번 출품 응찰 종료 상태 정보 수신");
				
//				if (!mAuctionScheduler.getCurrentAuctionStatus(auctionBidStatus.getAuctionHouseCode())
//						.equals(GlobalDefineCode.AUCTION_STATUS_READY) && mAuctionScheduler.getAuctionState(auctionBidStatus.getAuctionHouseCode()).getRetryTargetInfo() == null) {
//					mAuctionServer.itemAdded(auctionBidStatus.getEncodedMessage());
//				}
				mAuctionServer.itemAdded(auctionBidStatus.getEncodedMessage());
			} else {
				mLogger.info("비정상 채널에서 경매 응찰 종료 상태를 전송을 하였으나, 해당 요청이 거부되었습니다.");
				ctx.channel().writeAndFlush(
						new ResponseCode(auctionBidStatus.getAuctionHouseCode(), GlobalDefineCode.RESPONSE_REQUEST_FAIL)
								.getEncodedMessage() + "\r\n");
			}
		} else {
			mLogger.info("비정상 채널에서 경매 응찰 종료 상태를 전송을 하였으나, 해당 요청이 거부되었습니다.");
			ctx.channel().writeAndFlush(
					new ResponseCode(auctionBidStatus.getAuctionHouseCode(), GlobalDefineCode.RESPONSE_REQUEST_FAIL)
							.getEncodedMessage() + "\r\n");
		}
	}
}
