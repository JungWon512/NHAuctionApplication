package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.controller.models.InitEntryInfo;
import com.nh.share.controller.models.PauseAuction;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@Sharable
public final class AuctionServerDecodedInitEntryInfoAuctionHandler extends SimpleChannelInboundHandler<InitEntryInfo> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerDecodedInitEntryInfoAuctionHandler.class);

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

	public AuctionServerDecodedInitEntryInfoAuctionHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
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
	protected void channelRead0(ChannelHandlerContext ctx, InitEntryInfo initEntryInfo) throws Exception {
		mLogger.info("경매 출품 데이터 초기화 요청 : " + (initEntryInfo.getAuctionQcn()));

		if (mControllerChannelsMap.get(initEntryInfo.getAuctionHouseCode()).contains(ctx.channel()) == true) {
			mLogger.info("정상 채널에서 경매 출품 데이터 초기화 요청을 하였습니다.");
			
			if (mAuctionScheduler.getAuctionEditSetting(initEntryInfo.getAuctionHouseCode()).getAuctionType().equals(GlobalDefineCode.AUCTION_TYPE_SINGLE)) {
				mAuctionServer.itemAdded(initEntryInfo.getEncodedMessage());
			} else {
				if (!mAuctionScheduler.getCurrentAuctionStatus(initEntryInfo.getAuctionHouseCode())
						.equals(GlobalDefineCode.AUCTION_STATUS_START) && !mAuctionScheduler.getCurrentAuctionStatus(initEntryInfo.getAuctionHouseCode())
						.equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
					mAuctionServer.itemAdded(initEntryInfo.getEncodedMessage());
				}
			}
		} else {
			mLogger.info("비정상 채널에서 경매 출품 데이터 초기화를 요청하였으나, 요청이 거부되었습니다.");
		}
	}
}
