package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.ResponseCode;

import io.netty.channel.Channel;
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
	private Map<Object, ConnectionInfo> mConnectionInfoMap;
	private Map<String, Object> mConnectionChannelInfoMap;
	private Map<String, ChannelGroup> mStandChannelsMap = null;

	public AuctionServerDecodedBiddingHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
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
	protected void channelRead0(ChannelHandlerContext ctx, Bidding bidding) throws Exception {
		if (mConnectionInfoMap.containsKey(ctx.channel().id())
				&& mBidderChannelsMap.get(bidding.getAuctionHouseCode()).contains(ctx.channel())) {

			// 제어 프로그램 상태가 유효하지 않을 경우 예외 처리
			if (mControllerChannelsMap.get(bidding.getAuctionHouseCode()).size() <= 0) {
				ctx.channel().writeAndFlush(new ResponseConnectionInfo(bidding.getAuctionHouseCode(),
						GlobalDefineCode.CONNECT_CONTROLLER_ERROR, null, null).getEncodedMessage() + "\r\n");
				ctx.channel().close();

				return;
			}

			if (mAuctionScheduler.getCurrentAuctionStatus(bidding.getAuctionHouseCode())
					.equals(GlobalDefineCode.AUCTION_STATUS_START)
					|| mAuctionScheduler.getCurrentAuctionStatus(bidding.getAuctionHouseCode())
							.equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {

				mLogger.debug("Message ADD : " + bidding.getEncodedMessage());

				if (mAuctionScheduler.getAuctionEditSetting(bidding.getAuctionHouseCode()).getAuctionType()
						.equals(GlobalDefineCode.AUCTION_TYPE_SINGLE)) {
					// 현재 진행 출품번호 및 최저가에 만족하는지 확인
					if (bidding.getEntryNum()
							.equals(mAuctionScheduler.getAuctionState(bidding.getAuctionHouseCode())
									.getCurrentEntryInfo().getEntryNum())
							&& bidding.getPriceInt() >= Integer.valueOf(
									mAuctionScheduler.getAuctionState(bidding.getAuctionHouseCode()).getStartPrice())) {

						ctx.writeAndFlush(new ResponseCode(bidding.getAuctionHouseCode(),
								GlobalDefineCode.RESPONSE_SUCCESS_BIDDING).getEncodedMessage() + "\r\n");

						// 응찰 정보 수집
						mAuctionServer.itemAdded(bidding.getEncodedMessage());

					} else {
						mLogger.debug("=============================================");
						mLogger.debug("잘못 된 가격 응찰 시도 : " + bidding.getEncodedMessage());
						mLogger.debug("=============================================");
						ctx.writeAndFlush(new ResponseCode(bidding.getAuctionHouseCode(),
								GlobalDefineCode.RESPONSE_REQUEST_BIDDING_INVALID_PRICE).getEncodedMessage() + "\r\n");
					}
				} else {
					// 현재 진행 출품번호 및 최저가에 만족하는지 확인
					if (mAuctionScheduler.getAuctionState(bidding.getAuctionHouseCode()).getIsAuctionPause()) {
						mLogger.debug("=============================================");
						mLogger.debug("경매 정지로 인한 응찰 실패 : " + bidding.getEncodedMessage());
						mLogger.debug("=============================================");
						ctx.writeAndFlush(new ResponseCode(bidding.getAuctionHouseCode(),
								GlobalDefineCode.RESPONSE_REQUEST_FAIL).getEncodedMessage() + "\r\n");
					} else if (bidding
							.getPriceInt() >= Integer.valueOf(mAuctionScheduler
									.getEntryInfo(bidding.getAuctionHouseCode(), bidding.getEntryNum()).getLowPrice())
							&& bidding.getPriceInt() <= Integer.valueOf(mAuctionScheduler.getAuctionEditSetting(bidding.getAuctionHouseCode()).getmAuctionLimitPrice())) {

						ctx.writeAndFlush(new ResponseCode(bidding.getAuctionHouseCode(),
								GlobalDefineCode.RESPONSE_SUCCESS_BIDDING).getEncodedMessage() + "\r\n");

						// 응찰 정보 수집
						mAuctionServer.itemAdded(bidding.getEncodedMessage());

					} else {
						mLogger.debug("=============================================");
						mLogger.debug("잘못 된 가격 응찰 시도 : " + bidding.getEncodedMessage());
						mLogger.debug("=============================================");
						ctx.writeAndFlush(new ResponseCode(bidding.getAuctionHouseCode(),
								GlobalDefineCode.RESPONSE_REQUEST_BIDDING_INVALID_PRICE).getEncodedMessage() + "\r\n");
					}
				}
			} else {
				ctx.writeAndFlush(new ResponseCode(bidding.getAuctionHouseCode(),
						GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage() + "\r\n");
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
