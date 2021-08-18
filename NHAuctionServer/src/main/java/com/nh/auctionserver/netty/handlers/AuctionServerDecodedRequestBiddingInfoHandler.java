package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.RequestBiddingInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.server.models.ResponseCode;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@Sharable
public final class AuctionServerDecodedRequestBiddingInfoHandler
		extends SimpleChannelInboundHandler<RequestBiddingInfo> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerDecodedRequestBiddingInfoHandler.class);

	private final AuctionServer mAuctionServer;
	private final Auctioneer mAuctionScheduler;

	private Map<String, ChannelGroup> mControllerChannelsMap = null;
	private Map<String, ChannelGroup> mBidderChannelsMap = null;
	private Map<String, ChannelGroup> mWatcherChannelsMap = null;
	private Map<String, ChannelGroup> mAuctionResultMonitorChannelsMap = null;
	private Map<String, ChannelGroup> mConnectionMonitorChannelsMap = null;
	private Map<Object, ConnectionInfo> mConnectionInfoMap;
	private Map<String, Object> mConnectionChannelInfoMap;

	public AuctionServerDecodedRequestBiddingInfoHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
			Map<Object, ConnectionInfo> connectionInfoMap, Map<String, Object> connectionChannelInfoMap,
			Map<String, ChannelGroup> controllerChannelsMap, Map<String, ChannelGroup> bidderChannelsMap,
			Map<String, ChannelGroup> watcherChannelsMap, Map<String, ChannelGroup> auctionResultMonitorChannelsMap,
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
	protected void channelRead0(ChannelHandlerContext ctx, RequestBiddingInfo requestBiddingInfo) throws Exception {
		if (mConnectionInfoMap.containsKey(ctx.channel().id())
				&& mConnectionChannelInfoMap.containsKey(requestBiddingInfo.getUserNo())
				&& mBidderChannelsMap.get(requestBiddingInfo.getAuctionHouseCode()).contains(ctx.channel())) {
			
			// 제어 프로그램 상태가 유효하지 않을 경우 예외 처리
			if (mControllerChannelsMap.get(requestBiddingInfo.getAuctionHouseCode()).size() <= 0) {
				ctx.channel().writeAndFlush(new ResponseConnectionInfo(requestBiddingInfo.getAuctionHouseCode(),
						GlobalDefineCode.CONNECT_CONTROLLER_ERROR, null, null).getEncodedMessage() + "\r\n");
				ctx.channel().close();

				return;
			}
			
			// 응찰 정보 조회 요청
			if (mControllerChannelsMap != null
					&& mControllerChannelsMap.containsKey(requestBiddingInfo.getAuctionHouseCode())
					&& mControllerChannelsMap.get(requestBiddingInfo.getAuctionHouseCode()).size() > 0) {
				mControllerChannelsMap.get(requestBiddingInfo.getAuctionHouseCode())
						.writeAndFlush(requestBiddingInfo.getEncodedMessage() + "\r\n");
			} else {
				ctx.channel().writeAndFlush(new ResponseConnectionInfo(requestBiddingInfo.getAuctionHouseCode(),
						GlobalDefineCode.CONNECT_CONTROLLER_ERROR, null, null).getEncodedMessage() + "\r\n");
				ctx.channel().close();
			}
		} else {
			mLogger.debug("=============================================");
			mLogger.debug("유효하지 않은 채널에서 응찰 정보를 요청하였습니다. : " + ctx.channel().id());
			mLogger.debug("=============================================");

			ctx.writeAndFlush(
					new ResponseCode(requestBiddingInfo.getAuctionHouseCode(), GlobalDefineCode.RESPONSE_REQUEST_FAIL)
							.getEncodedMessage() + "\r\n");
		}
	}
}
