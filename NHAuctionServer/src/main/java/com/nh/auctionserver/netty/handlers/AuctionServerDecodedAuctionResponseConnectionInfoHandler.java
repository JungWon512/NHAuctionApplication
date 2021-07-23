package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.ResponseCode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.DefaultEventExecutor;

public class AuctionServerDecodedAuctionResponseConnectionInfoHandler
		extends SimpleChannelInboundHandler<ResponseConnectionInfo> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerDecodedAuctionResponseSessionHandler.class);

	private final AuctionServer mAuctionServer;
	private final Auctioneer mAuctionScheduler;

	private Map<String, ChannelGroup> mControllerChannelsMap = null;
	private Map<String, ChannelGroup> mBidderChannelsMap = null;
	private Map<String, ChannelGroup> mWatcherChannelsMap = null;
	private Map<String, ChannelGroup> mAuctionResultMonitorChannelsMap = null;
	private Map<String, ChannelGroup> mConnectionMonitorChannelsMap = null;
	private Map<ChannelId, ConnectionInfo> mConnectionInfoMap;
	private Map<String, ChannelHandlerContext> mConnectionChannelInfoMap;

	public AuctionServerDecodedAuctionResponseConnectionInfoHandler(AuctionServer auctionServer,
			Auctioneer auctionSchedule, Map<ChannelId, ConnectionInfo> connectionInfoMap,
			Map<String, ChannelHandlerContext> connectionChannelInfoMap,
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

	@SuppressWarnings("unlikely-arg-type")
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ResponseConnectionInfo responseConnectionInfo)
			throws Exception {
		if (mConnectionChannelInfoMap.containsKey(responseConnectionInfo.getUserMemNum())) {
			ChannelHandlerContext clientChannelContext = mConnectionChannelInfoMap
					.get(responseConnectionInfo.getUserMemNum());

			if (responseConnectionInfo.getResult().equals(GlobalDefineCode.CONNECT_SUCCESS)) {
				// 접속 처리 결과 응답 처리
				clientChannelContext.channel()
						.writeAndFlush(new ResponseConnectionInfo(responseConnectionInfo.getAuctionHouseCode(),
								GlobalDefineCode.CONNECT_SUCCESS, responseConnectionInfo.getUserMemNum(),
								responseConnectionInfo.getAuctionJoinNum()).getEncodedMessage() + "\r\n");

				// 접속자 정보 전송
				mAuctionServer
						.itemAdded(
								new BidderConnectInfo(
										mConnectionInfoMap.get(clientChannelContext.channel().id())
												.getAuctionHouseCode(),
										responseConnectionInfo.getUserMemNum(),
										mConnectionInfoMap.get(clientChannelContext.channel().id()).getChannel(),
										mConnectionInfoMap.get(clientChannelContext.channel().id()).getOS(), "N", "0")
												.getEncodedMessage());

				// 응찰 채널 등록 처리
				if (!mBidderChannelsMap.containsKey(responseConnectionInfo.getAuctionHouseCode())) {
					mBidderChannelsMap.put(responseConnectionInfo.getAuctionHouseCode(),
							new DefaultChannelGroup(new DefaultEventExecutor()));
				}

				if (!mBidderChannelsMap.get(responseConnectionInfo.getAuctionHouseCode())
						.contains(clientChannelContext.channel())) {
					mBidderChannelsMap.get(responseConnectionInfo.getAuctionHouseCode())
							.add(clientChannelContext.channel());
				}

				// 현재 출품 정보 전송
				if (mAuctionScheduler.getCurrentAuctionStatus(responseConnectionInfo.getAuctionHouseCode())
						.equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
					clientChannelContext.writeAndFlush(new ResponseCode(responseConnectionInfo.getAuctionHouseCode(),
							GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage() + "\r\n");
				} else {
					if (mAuctionScheduler.getAuctionState(responseConnectionInfo.getAuctionHouseCode()) != null) {
						clientChannelContext.writeAndFlush(new CurrentEntryInfo(mAuctionScheduler
								.getAuctionState(responseConnectionInfo.getAuctionHouseCode()).getCurrentEntryInfo())
										.getEncodedMessage()
								+ "\r\n");

						// 정상 접속자 초기 경매 상태 정보 전달 처리
						clientChannelContext.channel().writeAndFlush(
								mAuctionScheduler.getAuctionState(responseConnectionInfo.getAuctionHouseCode())
										.getAuctionStatus().getEncodedMessage() + "\r\n");
					}

//    				// 관심 차량 정보 필요 대상 추출 및 관련 정보 전송 처리
//    				if (mAuctionScheduler.containFavoriteCarInfoMap(mAuctionScheduler.getAuctionState().getEntryNum(),
//    						connectorInfo.getUserNo())) {
//    					clientChannelContext.writeAndFlush(new FavoriteEntryInfo(mAuctionScheduler.getAuctionState().getEntryNum(), "Y")
//    							.getEncodedMessage() + "\r\n");
//    				}
				}
			}
		}
	}
}