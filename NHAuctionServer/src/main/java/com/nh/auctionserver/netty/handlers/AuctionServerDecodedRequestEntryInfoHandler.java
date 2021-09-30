package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.RequestEntryInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.ResponseCode;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@Sharable
public final class AuctionServerDecodedRequestEntryInfoHandler extends SimpleChannelInboundHandler<RequestEntryInfo> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerDecodedRequestEntryInfoHandler.class);

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

	public AuctionServerDecodedRequestEntryInfoHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
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
	protected void channelRead0(ChannelHandlerContext ctx, RequestEntryInfo requestEntryInfo) throws Exception {
		if (mConnectionInfoMap.containsKey(ctx.channel().id())
				&& mBidderChannelsMap.get(requestEntryInfo.getAuctionHouseCode()).contains(ctx.channel())) {
			
			// 제어 프로그램 상태가 유효하지 않을 경우 예외 처리
			if (mControllerChannelsMap.get(requestEntryInfo.getAuctionHouseCode()).size() <= 0) {
				ctx.channel().writeAndFlush(new ResponseConnectionInfo(requestEntryInfo.getAuctionHouseCode(),
						GlobalDefineCode.CONNECT_CONTROLLER_ERROR, null, null).getEncodedMessage() + "\r\n");
				ctx.channel().close();

				return;
			}
			
			// 요청된 출품 정보 확인 및 결과 전송 처리
			if (mAuctionScheduler.getCurrentAuctionStatus(requestEntryInfo.getAuctionHouseCode())
					.equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
				ctx.writeAndFlush(new ResponseCode(requestEntryInfo.getAuctionHouseCode(),
						GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage() + "\r\n");
			} else {
				if (mAuctionScheduler.getEntryInfo(requestEntryInfo.getAuctionHouseCode(),
						requestEntryInfo.getEntryNum()) != null) {
					ctx.writeAndFlush(
							new CurrentEntryInfo(mAuctionScheduler.getEntryInfo(requestEntryInfo.getAuctionHouseCode(),
									requestEntryInfo.getEntryNum())).getEncodedMessage());
				} else {
					ctx.writeAndFlush(new ResponseCode(requestEntryInfo.getAuctionHouseCode(),
							GlobalDefineCode.RESPONSE_REQUEST_NOT_RESULT).getEncodedMessage() + "\r\n");
				}
			}
		} else {
			mLogger.debug("=============================================");
			mLogger.debug("유효하지 않은 채널에서 출품 정보 전송을 요청하였습니다. : " + ctx.channel().id());
			mLogger.debug("=============================================");

			ctx.writeAndFlush(
					new ResponseCode(requestEntryInfo.getAuctionHouseCode(), GlobalDefineCode.RESPONSE_REQUEST_FAIL)
							.getEncodedMessage() + "\r\n");
		}
	}
}
