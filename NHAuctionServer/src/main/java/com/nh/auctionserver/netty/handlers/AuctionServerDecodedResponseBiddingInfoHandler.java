package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.SocketIOClient;
import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.ResponseBiddingInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

public class AuctionServerDecodedResponseBiddingInfoHandler extends SimpleChannelInboundHandler<ResponseBiddingInfo> {
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
	private Map<String, ChannelGroup> mStandChannelsMap = null;

	public AuctionServerDecodedResponseBiddingInfoHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
			Map<Object, ConnectionInfo> connectionInfoMap,
			Map<String, Object> connectionChannelInfoMap,
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

	@SuppressWarnings("unlikely-arg-type")
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ResponseBiddingInfo responseBiddingInfo) throws Exception {
		if (mConnectionChannelInfoMap.containsKey(responseBiddingInfo.getUserNo())) {
			 if (mConnectionChannelInfoMap
						.get(responseBiddingInfo.getUserNo()) instanceof ChannelHandlerContext) {
				 ChannelHandlerContext clientChannelContext = (ChannelHandlerContext) mConnectionChannelInfoMap.get(responseBiddingInfo.getUserNo());

					// 조회 된 응찰 정보 전송 처리
					clientChannelContext.channel().writeAndFlush(responseBiddingInfo.getEncodedMessage() + "\r\n");
			 } else if (mConnectionChannelInfoMap
						.get(responseBiddingInfo.getUserNo()) instanceof SocketIOClient) {
				 SocketIOClient socketIOClient = (SocketIOClient) mConnectionChannelInfoMap
							.get(responseBiddingInfo.getUserNo());

				 socketIOClient.sendEvent("ResponseBiddingInfo", responseBiddingInfo.getEncodedMessage());
			 }
		}
	}
}
