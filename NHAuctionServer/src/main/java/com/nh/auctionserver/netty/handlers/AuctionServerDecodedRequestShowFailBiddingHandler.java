package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.controller.models.RequestShowFailBidding;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@Sharable
public final class AuctionServerDecodedRequestShowFailBiddingHandler
		extends SimpleChannelInboundHandler<RequestShowFailBidding> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerDecodedRequestShowFailBiddingHandler.class);

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

	public AuctionServerDecodedRequestShowFailBiddingHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
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
	protected void channelRead0(ChannelHandlerContext ctx, RequestShowFailBidding requestShowFailBIdding)
			throws Exception {
		mLogger.info("유찰 예상 목록 표시 요청 : " + (requestShowFailBIdding.getEncodedMessage()));

		if (mControllerChannelsMap.get(requestShowFailBIdding.getAuctionHouseCode()).contains(ctx.channel()) == true) {
			mLogger.info("정상 채널에서 유찰 예상 목록 표시를 요청하였습니다.");
			
			// 미출장우 노출 여부에 따라 노출 요청 혹은 경매 상태 코드에 따른 전관판 노출 처리 요청
			if (requestShowFailBIdding.getIsShow().equals("Y")) {
				mAuctionServer.itemAdded(requestShowFailBIdding.getEncodedMessage());
			} else {
				// 출하안내 시스템에 현재 경매 상태 코드 전송
				if (mStandChannelsMap != null) {
					if (mStandChannelsMap.containsKey(requestShowFailBIdding.getAuctionHouseCode())) {
						if (mStandChannelsMap.get(requestShowFailBIdding.getAuctionHouseCode()).size() > 0) {
							mStandChannelsMap.get(requestShowFailBIdding.getAuctionHouseCode()).writeAndFlush(mAuctionScheduler.getAuctionState(requestShowFailBIdding.getAuctionHouseCode())
									.getAuctionStatus().getEncodedMessage() + "\r\n");
						}
					}
				}
			}
		} else {
			mLogger.info("비정상 채널에서 유찰 예상 목록 표시를 요청하였으나, 요청이 거부되었습니다.");
		}
	}
}
