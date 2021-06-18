package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.server.models.ResponseCode;

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

	private Map<String, ChannelGroup> mControllerChannelsMap = null;
	private Map<String, ChannelGroup> mBidderChannelsMap = null;
	private Map<String, ChannelGroup> mWatcherChannelsMap = null;
	private Map<String, ChannelGroup> mAuctionResultMonitorChannelsMap = null;
	private Map<String, ChannelGroup> mConnectionMonitorChannelsMap = null;
	private Map<ChannelId, ConnectionInfo> mConnectionInfoMap;

	public AuctionServerDecodedEntryInfoHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
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
	protected void channelRead0(ChannelHandlerContext ctx, EntryInfo entryInfo) throws Exception {
		if (mControllerChannelsMap.containsKey(entryInfo.getAuctionHouseCode())) {
			if (mControllerChannelsMap.get(entryInfo.getAuctionHouseCode()).contains(ctx.channel())) {
				mLogger.info(entryInfo.getEntryNum() + "번 출품 자료 수신");
				mAuctionServer.itemAdded(entryInfo.getEncodedMessage());
			} else {
				mLogger.info("111비정상 채널에서 출품 자료 전송을 하였으나, 해당 요청이 거부되었습니다.");
				ctx.channel().writeAndFlush(new ResponseCode(entryInfo.getAuctionHouseCode(),
						GlobalDefineCode.RESPONSE_REQUEST_FAIL).getEncodedMessage() + "\r\n");
			}
		} else {
			mLogger.info("222비정상 채널에서 출품 자료 전송을 하였으나, 해당 요청이 거부되었습니다.");
			ctx.channel().writeAndFlush(
					new ResponseCode(entryInfo.getAuctionHouseCode(), GlobalDefineCode.RESPONSE_REQUEST_FAIL)
							.getEncodedMessage() + "\r\n");
		}
	}
}
