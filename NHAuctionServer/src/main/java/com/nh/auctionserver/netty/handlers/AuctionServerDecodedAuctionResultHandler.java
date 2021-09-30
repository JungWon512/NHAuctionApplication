package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.controller.models.SendAuctionResult;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@Sharable
public final class AuctionServerDecodedAuctionResultHandler extends SimpleChannelInboundHandler<SendAuctionResult> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerDecodedAuctionResultHandler.class);

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

	public AuctionServerDecodedAuctionResultHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
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

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, SendAuctionResult sendAuctionResult) throws Exception {
		mLogger.info(sendAuctionResult.getAuctionHouseCode() + " / " + sendAuctionResult.getEntryNum()
				+ "번 경매 낙/유찰 결과 수신 : " + sendAuctionResult.getEncodedMessage());

		// 경매 취소의 경우 해당 출품 건을 대기 상태로 초기화 처리
		if(sendAuctionResult.getResultCode().equals(GlobalDefineCode.AUCTION_RESULT_CODE_CANCEL)) {
			if (mControllerChannelsMap.get(sendAuctionResult.getAuctionHouseCode()).contains(ctx.channel()) == true) {
				mLogger.info("정상 채널에서 경매 결과를 수신 받았습니다.");
				mAuctionServer.itemAdded(sendAuctionResult.getEncodedMessage());
			} else {
				mLogger.info("비정상 채널에서 경매 결과를 수신받았으나, 해당 요청이 거부되었습니다.");
			}
			
			mAuctionScheduler.readyEntryInfo(sendAuctionResult.getAuctionHouseCode(), sendAuctionResult.getEntryNum());
		} else {
			mAuctionScheduler.setAuctionCompleted(sendAuctionResult.getAuctionHouseCode());
			
			if (mControllerChannelsMap.get(sendAuctionResult.getAuctionHouseCode()).contains(ctx.channel()) == true) {
				mLogger.info("정상 채널에서 경매 결과를 수신 받았습니다.");
				mAuctionServer.itemAdded(sendAuctionResult.getEncodedMessage());
			} else {
				mLogger.info("비정상 채널에서 경매 결과를 수신받았으나, 해당 요청이 거부되었습니다.");
			}
		}
	}
}
