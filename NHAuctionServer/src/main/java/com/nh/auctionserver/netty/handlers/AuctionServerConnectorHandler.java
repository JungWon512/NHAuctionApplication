package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.auctionserver.setting.AuctionServerSetting;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.FavoriteEntryInfo;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

@Sharable
public final class AuctionServerConnectorHandler extends SimpleChannelInboundHandler<ConnectionInfo> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerConnectorHandler.class);

	private final AuctionServer mAuctionServer;
	private final Auctioneer mAuctionScheduler;

	private ChannelGroup mControllerChannels = null;
	private ChannelGroup mBidderChannels = null;
	private ChannelGroup mWatcherChannels = null;
	private ChannelGroup mAuctionResultMonitorChannels = null;
	private ChannelGroup mConnectionMonitorChannels = null;
	private Map<ChannelId, ConnectionInfo> mConnectorInfoMap;

	public AuctionServerConnectorHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
			Map<ChannelId, ConnectionInfo> connectorInfoMap, ChannelGroup controllerChannels,
			ChannelGroup bidderChannels, ChannelGroup watcherChannels, ChannelGroup auctionResultMonitorChannels,
			ChannelGroup connectionMonitorChannels) {
		mAuctionServer = auctionServer;
		mConnectorInfoMap = connectorInfoMap;
		mAuctionScheduler = auctionSchedule;
		mControllerChannels = controllerChannels;
		mBidderChannels = bidderChannels;
		mWatcherChannels = watcherChannels;
		mAuctionResultMonitorChannels = auctionResultMonitorChannels;
		mConnectionMonitorChannels = connectionMonitorChannels;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ConnectionInfo connectorInfo) throws Exception {
		mLogger.debug("!mConnectorInfoMap.containsKey(ctx.channel().id()) : "
				+ !mConnectorInfoMap.containsKey(ctx.channel().id()));
		mLogger.debug("!mConnectorInfoMap.containsValue((connectorInfo) : "
				+ !mConnectorInfoMap.containsValue(connectorInfo));

		// 접속 정보 확인
		// 채널 id가 동일할 경우 중복 접속으로 간주
		// 같은 접속채널로 동일 접속 정보 전달 시 중복 접속으로 간주
		// 같은 회원번호로 접속 시도 시 중복 접속으로 간주
		if (!mConnectorInfoMap.containsKey(ctx.channel().id()) && !mConnectorInfoMap.containsValue(connectorInfo)) {
			if (connectorInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
				// 접속 처리 결과 응답 처리
				ctx.channel().writeAndFlush(
						new ResponseConnectionInfo(GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

				// Connector에 채널 아이디 등록 처리
				if (!mConnectorInfoMap.containsKey(ctx.channel().id())) {
					mConnectorInfoMap.put(ctx.channel().id(), connectorInfo);

					// 응찰 채널 등록 처리
					if (!mBidderChannels.contains(ctx.channel())) {
						mBidderChannels.add(ctx.channel());
					}
				}

				// 경매 시작 전인 경우 관련 카운트 다운 관련 상태 정보 전송 처리
				if (!mAuctionScheduler.getAuctionCountDownStatus()
						.equals(GlobalDefineCode.AUCTION_COUNT_DOWN_COMPLETED)) {
					ctx.writeAndFlush(new AuctionCountDown(mAuctionScheduler.getAuctionCountDownStatus(),
							mAuctionScheduler.getAuctionCountDownTime()).getEncodedMessage() + "\r\n");
				}

//				// 관심 차량 정보 필요 대상 추출 및 관련 정보 전송 처리
//				if (mAuctionScheduler.containFavoriteCarInfoMap(mAuctionScheduler.getAuctionState().getEntryNum(),
//						connectorInfo.getUserNo())) {
//					ctx.writeAndFlush(new FavoriteEntryInfo(mAuctionScheduler.getAuctionState().getEntryNum(), "Y")
//							.getEncodedMessage() + "\r\n");
//				}

			} else if (connectorInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER)) {
				mLogger.info("Controller Channel Count : " + mControllerChannels.size());

				if (mConnectorInfoMap.size() > 0) {
					for (ChannelId key : mConnectorInfoMap.keySet()) {
						if (mConnectorInfoMap.get(key).getChannel()
								.equals(GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER)) {
							mLogger.info("이미 제어 프로그램이 실행 중인 상태로 추가 실행이 불가합니다.");
							ctx.channel()
									.writeAndFlush(new ResponseConnectionInfo(GlobalDefineCode.CONNECT_DUPLICATE_FAIL)
											.getEncodedMessage() + "\r\n");
							return;
						}
					}
				}

				if (mControllerChannels.size() > 0) {
					mLogger.info("이미 제어 프로그램이 실행 중인 상태로 추가 실행이 불가합니다.");
					ctx.channel().writeAndFlush(
							new ResponseConnectionInfo(GlobalDefineCode.CONNECT_DUPLICATE_FAIL).getEncodedMessage()
									+ "\r\n");
					return;
				} else {
					// 접속 처리 결과 응답 처리
					ctx.channel().writeAndFlush(
							new ResponseConnectionInfo(GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

					// Controller 채널 아이디 등록 처리
					if (!mConnectorInfoMap.containsKey(ctx.channel().id())) {
						mConnectorInfoMap.put(ctx.channel().id(), connectorInfo);
					}

					// 제어 프로그램 채널 등록 처리
					if (!mControllerChannels.contains(ctx.channel())) {
						mControllerChannels.add(ctx.channel());
					}
				}
			} else if (connectorInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_WATCHER)) {
				// 접속 처리 결과 응답 처리
				ctx.channel().writeAndFlush(
						new ResponseConnectionInfo(GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

				// Controller 채널 아이디 등록 처리
				if (!mConnectorInfoMap.containsKey(ctx.channel().id())) {
					mConnectorInfoMap.put(ctx.channel().id(), connectorInfo);
				}

				// 경매 관전 채널 등록 처리
				if (!mWatcherChannels.contains(ctx.channel())) {
					mWatcherChannels.add(ctx.channel());
				}

				// 경매 시작 전인 경우 관련 카운트 다운 관련 상태 정보 전송 처리
				if (!mAuctionScheduler.getAuctionCountDownStatus()
						.equals(GlobalDefineCode.AUCTION_COUNT_DOWN_COMPLETED)) {
					ctx.writeAndFlush(new AuctionCountDown(mAuctionScheduler.getAuctionCountDownStatus(),
							mAuctionScheduler.getAuctionCountDownTime()).getEncodedMessage() + "\r\n");
				}

			} else if (connectorInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_RESULT_MONITOR)) {
				// 접속 처리 결과 응답 처리
				ctx.channel().writeAndFlush(
						new ResponseConnectionInfo(GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

				// Controller 채널 아이디 등록 처리
				if (!mConnectorInfoMap.containsKey(ctx.channel().id())) {
					mConnectorInfoMap.put(ctx.channel().id(), connectorInfo);
				}

				// 낙,유찰 모니터링 채널 등록 처리
				if (!mAuctionResultMonitorChannels.contains(ctx.channel())) {
					mAuctionResultMonitorChannels.add(ctx.channel());
				}

			} else if (connectorInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_CONNECT_MONITOR)) {
				// 접속 처리 결과 응답 처리
				ctx.channel().writeAndFlush(
						new ResponseConnectionInfo(GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

				// Controller 채널 아이디 등록 처리
				if (!mConnectorInfoMap.containsKey(ctx.channel().id())) {
					mConnectorInfoMap.put(ctx.channel().id(), connectorInfo);
				}

				// 접속자 모니터링 채널 등록 처리
				if (!mConnectionMonitorChannels.contains(ctx.channel())) {
					mConnectionMonitorChannels.add(ctx.channel());
				}

				// 접속자 정보 최초 전송
				for (ChannelId key : mConnectorInfoMap.keySet()) {
					if (mConnectorInfoMap.get(key).getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
						ctx.channel()
								.writeAndFlush(new BidderConnectInfo(mConnectorInfoMap.get(key).getUserNo(),
										mConnectorInfoMap.get(key).getChannel(), mConnectorInfoMap.get(key).getOS(),
										"N").getEncodedMessage() + "\r\n");
					}
				}
			}
		} else {
			if (connectorInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
				// 중복 접속 불가 처리
				ctx.channel().writeAndFlush(
						new ResponseConnectionInfo(GlobalDefineCode.CONNECT_DUPLICATE_FAIL).getEncodedMessage()
								+ "\r\n");
				ctx.channel().close();
			} else if (connectorInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER)) {
				// 중복 접속 불가 처리
				ctx.channel().writeAndFlush(
						new ResponseConnectionInfo(GlobalDefineCode.CONNECT_DUPLICATE_FAIL).getEncodedMessage()
								+ "\r\n");
				ctx.channel().close();
			} else if (connectorInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_WATCHER)) {
				// 접속 처리 결과 응답 처리
				ctx.channel().writeAndFlush(
						new ResponseConnectionInfo(GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

				// Controller 채널 아이디 등록 처리
				if (!mConnectorInfoMap.containsKey(ctx.channel().id())) {
					mConnectorInfoMap.put(ctx.channel().id(), connectorInfo);
				}

				// 경매 관전 채널 등록 처리
				if (!mWatcherChannels.contains(ctx.channel())) {
					mWatcherChannels.add(ctx.channel());
				}

				// 경매 시작 전인 경우 관련 카운트 다운 관련 상태 정보 전송 처리
				if (!mAuctionScheduler.getAuctionCountDownStatus()
						.equals(GlobalDefineCode.AUCTION_COUNT_DOWN_COMPLETED)) {
					ctx.writeAndFlush(new AuctionCountDown(mAuctionScheduler.getAuctionCountDownStatus(),
							mAuctionScheduler.getAuctionCountDownTime()).getEncodedMessage() + "\r\n");
				}

				// 현재 출품 정보 전송
				ctx.channel().writeAndFlush(mAuctionScheduler.getAuctionState().getCurrentEntryInfo().getEncodedMessage() + "\r\n");

				// 정상 접속자 초기 경매 상태 정보 전달 처리
				ctx.channel().writeAndFlush(
						mAuctionScheduler.getAuctionState().getAuctionStatus().getEncodedMessage() + "\r\n");
			} else if (connectorInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_RESULT_MONITOR)) {
				// 접속 처리 결과 응답 처리
				ctx.channel().writeAndFlush(
						new ResponseConnectionInfo(GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

				// Controller 채널 아이디 등록 처리
				if (!mConnectorInfoMap.containsKey(ctx.channel().id())) {
					mConnectorInfoMap.put(ctx.channel().id(), connectorInfo);
				}

				// 낙,유찰 모니터링 채널 등록 처리
				if (mAuctionResultMonitorChannels.contains(ctx.channel())) {
					mAuctionResultMonitorChannels.add(ctx.channel());
				}

				// 현재 출품 정보 전송
				ctx.channel().writeAndFlush(mAuctionScheduler.getAuctionState().getCurrentEntryInfo().getEncodedMessage() + "\r\n");

				// 정상 접속자 초기 경매 상태 정보 전달 처리
				ctx.channel().writeAndFlush(
						mAuctionScheduler.getAuctionState().getAuctionStatus().getEncodedMessage() + "\r\n");
			} else if (connectorInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_CONNECT_MONITOR)) {
				// 접속 처리 결과 응답 처리
				ctx.channel().writeAndFlush(
						new ResponseConnectionInfo(GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

				// Controller 채널 아이디 등록 처리
				if (!mConnectorInfoMap.containsKey(ctx.channel().id())) {
					mConnectorInfoMap.put(ctx.channel().id(), connectorInfo);
				}

				// 접속자 모니터링 채널 등록 처리
				if (!mConnectionMonitorChannels.contains(ctx.channel())) {
					mConnectionMonitorChannels.add(ctx.channel());
				}

				// 접속자 정보 최초 전송
				for (ChannelId key : mConnectorInfoMap.keySet()) {
					if (mConnectorInfoMap.get(key).getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
						ctx.channel()
								.writeAndFlush(new BidderConnectInfo(mConnectorInfoMap.get(key).getUserNo(),
										mConnectorInfoMap.get(key).getChannel(), mConnectorInfoMap.get(key).getOS(),
										"N").getEncodedMessage() + "\r\n");
					}
				}
			}
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		String closeMember = null;

		super.channelInactive(ctx);

		if (mConnectorInfoMap.containsKey(ctx.channel().id())) {
			closeMember = mConnectorInfoMap.get(ctx.channel().id()).getUserNo();
		}

		if (mConnectorInfoMap.containsKey(ctx.channel().id())) {
			mConnectorInfoMap.remove(ctx.channel().id());

			if (!mConnectorInfoMap.containsKey(ctx.channel().id())) {
				mLogger.info("정상적으로 " + closeMember + "회원 정보가 Close 처리되었습니다.");
			}

			mLogger.debug("ConnectorInfoMap size : " + mConnectorInfoMap.size());
		}

		ctx.channel().close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}
}
