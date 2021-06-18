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

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.DefaultEventExecutor;

@Sharable
public final class AuctionServerConnectorHandler extends SimpleChannelInboundHandler<ConnectionInfo> {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServerConnectorHandler.class);

	private final AuctionServer mAuctionServer;
	private final Auctioneer mAuctionScheduler;

	private Map<String, ChannelGroup> mControllerChannelsMap = null;
	private Map<String, ChannelGroup> mBidderChannelsMap = null;
	private Map<String, ChannelGroup> mWatcherChannelsMap = null;
	private Map<String, ChannelGroup> mAuctionResultMonitorChannelsMap = null;
	private Map<String, ChannelGroup> mConnectionMonitorChannelsMap = null;
	private Map<ChannelId, ConnectionInfo> mConnectionInfoMap;

	public AuctionServerConnectorHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
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
	protected void channelRead0(ChannelHandlerContext ctx, ConnectionInfo connectionInfo) throws Exception {
		mLogger.debug("!mConnectorInfoMap.containsKey(ctx.channel().id()) : "
				+ !mConnectionInfoMap.containsKey(ctx.channel().id()));
		mLogger.debug("!mConnectorInfoMap.containsValue((connectorInfo) : "
				+ !mConnectionInfoMap.containsValue(connectionInfo));

		// 접속 정보 확인
		// 채널 id가 동일할 경우 중복 접속으로 간주
		// 같은 접속채널로 동일 접속 정보 전달 시 중복 접속으로 간주
		// 같은 회원번호로 접속 시도 시 중복 접속으로 간주
		if (!mConnectionInfoMap.containsKey(ctx.channel().id()) && !mConnectionInfoMap.containsValue(connectionInfo)) {
			if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
				// 접속 처리 결과 응답 처리
				ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
						GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

				// Connector에 채널 아이디 등록 처리
				if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
					mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);
				}

				// 응찰 채널 등록 처리
				if (!mBidderChannelsMap.containsKey(connectionInfo.getAuctionHouseCode())) {
					mBidderChannelsMap.put(connectionInfo.getAuctionHouseCode(),
							new DefaultChannelGroup(new DefaultEventExecutor()));
				}

				if (!mBidderChannelsMap.get(connectionInfo.getAuctionHouseCode()).contains(ctx.channel())) {
					mBidderChannelsMap.get(connectionInfo.getAuctionHouseCode()).add(ctx.channel());
				}

				// 현재 출품 정보 전송
				if(mAuctionScheduler.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode()).equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
					ctx.writeAndFlush(new ResponseCode(connectionInfo.getAuctionHouseCode(), GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage() + "\r\n");
				} else {
					if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
						ctx.writeAndFlush(
								new CurrentEntryInfo(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
										.getCurrentEntryInfo()).getEncodedMessage() + "\r\n");

						// 정상 접속자 초기 경매 상태 정보 전달 처리
						ctx.channel().writeAndFlush(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
								.getAuctionStatus().getEncodedMessage() + "\r\n");
					}

//					// 관심 차량 정보 필요 대상 추출 및 관련 정보 전송 처리
//					if (mAuctionScheduler.containFavoriteCarInfoMap(mAuctionScheduler.getAuctionState().getEntryNum(),
//							connectorInfo.getUserNo())) {
//						ctx.writeAndFlush(new FavoriteEntryInfo(mAuctionScheduler.getAuctionState().getEntryNum(), "Y")
//								.getEncodedMessage() + "\r\n");
//					}
				}
			} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER)) {
				mLogger.info("Controller Channel Count : " + mControllerChannelsMap.size());

				if (mConnectionInfoMap.size() > 0) {
					for (ChannelId key : mConnectionInfoMap.keySet()) {
						if (mConnectionInfoMap.get(key).getChannel()
								.equals(GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER)) {
							mLogger.info("이미 제어 프로그램이 실행 중인 상태로 추가 실행이 불가합니다.");
							ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
									GlobalDefineCode.CONNECT_DUPLICATE_FAIL).getEncodedMessage() + "\r\n");
							return;
						} else {
							// 접속 처리 결과 응답 처리
							ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
									GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

							// Controller 채널 아이디 등록 처리
							if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
								mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);
							}

							// 제어 프로그램 채널 등록 처리
							if (!mControllerChannelsMap.containsKey(connectionInfo.getAuctionHouseCode())) {
								mControllerChannelsMap.put(connectionInfo.getAuctionHouseCode(),
										new DefaultChannelGroup(new DefaultEventExecutor()));
							}

							if (!mControllerChannelsMap.get(connectionInfo.getAuctionHouseCode()).contains(ctx.channel())) {
								mControllerChannelsMap.get(connectionInfo.getAuctionHouseCode()).add(ctx.channel());
							}
							
							// 현재 출품 정보 전송
							if(mAuctionScheduler.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode()).equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
								ctx.writeAndFlush(new ResponseCode(connectionInfo.getAuctionHouseCode(), GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage() + "\r\n");
							} else {
								if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
									ctx.writeAndFlush(
											new CurrentEntryInfo(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
													.getCurrentEntryInfo()).getEncodedMessage() + "\r\n");

									// 정상 접속자 초기 경매 상태 정보 전달 처리
									ctx.channel().writeAndFlush(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
											.getAuctionStatus().getEncodedMessage() + "\r\n");
								}
							}
						}
					}
				} else {
					// 접속 처리 결과 응답 처리
					ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
							GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

					// Controller 채널 아이디 등록 처리
					if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
						mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);
					}

					// 제어 프로그램 채널 등록 처리
					if (!mControllerChannelsMap.containsKey(connectionInfo.getAuctionHouseCode())) {
						mControllerChannelsMap.put(connectionInfo.getAuctionHouseCode(),
								new DefaultChannelGroup(new DefaultEventExecutor()));
					}

					if (!mControllerChannelsMap.get(connectionInfo.getAuctionHouseCode()).contains(ctx.channel())) {
						mControllerChannelsMap.get(connectionInfo.getAuctionHouseCode()).add(ctx.channel());
					}
					
					// 현재 출품 정보 전송
					if(mAuctionScheduler.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode()).equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
						ctx.writeAndFlush(new ResponseCode(connectionInfo.getAuctionHouseCode(), GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage() + "\r\n");
					} else {
						if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
							ctx.writeAndFlush(
									new CurrentEntryInfo(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
											.getCurrentEntryInfo()).getEncodedMessage() + "\r\n");

							// 정상 접속자 초기 경매 상태 정보 전달 처리
							ctx.channel().writeAndFlush(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
									.getAuctionStatus().getEncodedMessage() + "\r\n");
						}
					}
				}
			} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_WATCHER)) {
				// 접속 처리 결과 응답 처리
				ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
						GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

				// Controller 채널 아이디 등록 처리
				if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
					mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);
				}

				// 경매 관전 채널 등록 처리
				if (!mWatcherChannelsMap.containsKey(connectionInfo.getAuctionHouseCode())) {
					mWatcherChannelsMap.put(connectionInfo.getAuctionHouseCode(),
							new DefaultChannelGroup(new DefaultEventExecutor()));
				}

				if (!mWatcherChannelsMap.get(connectionInfo.getAuctionHouseCode()).contains(ctx.channel())) {
					mWatcherChannelsMap.get(connectionInfo.getAuctionHouseCode()).add(ctx.channel());
				}

				// 현재 출품 정보 전송
				if(mAuctionScheduler.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode()).equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
					ctx.writeAndFlush(new ResponseCode(connectionInfo.getAuctionHouseCode(), GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage() + "\r\n");
				} else {
					if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
						ctx.writeAndFlush(
								new CurrentEntryInfo(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
										.getCurrentEntryInfo()).getEncodedMessage() + "\r\n");

						// 정상 접속자 초기 경매 상태 정보 전달 처리
						ctx.channel().writeAndFlush(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
								.getAuctionStatus().getEncodedMessage() + "\r\n");
					}
				}
			} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_RESULT_MONITOR)) {
				// 접속 처리 결과 응답 처리
				ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
						GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

				// Controller 채널 아이디 등록 처리
				if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
					mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);
				}

				// 낙,유찰 모니터링 채널 등록 처리
				if (!mAuctionResultMonitorChannelsMap.containsKey(connectionInfo.getAuctionHouseCode())) {
					mAuctionResultMonitorChannelsMap.put(connectionInfo.getAuctionHouseCode(),
							new DefaultChannelGroup(new DefaultEventExecutor()));
				}

				if (!mAuctionResultMonitorChannelsMap.get(connectionInfo.getAuctionHouseCode())
						.contains(ctx.channel())) {
					mAuctionResultMonitorChannelsMap.get(connectionInfo.getAuctionHouseCode()).add(ctx.channel());
				}
			} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_CONNECT_MONITOR)) {
				// 접속 처리 결과 응답 처리
				ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
						GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

				// Controller 채널 아이디 등록 처리
				if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
					mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);
				}

				// 접속자 모니터링 채널 등록 처리
				if (!mConnectionMonitorChannelsMap.containsKey(connectionInfo.getAuctionHouseCode())) {
					mConnectionMonitorChannelsMap.put(connectionInfo.getAuctionHouseCode(),
							new DefaultChannelGroup(new DefaultEventExecutor()));
				}

				if (!mConnectionMonitorChannelsMap.get(connectionInfo.getAuctionHouseCode()).contains(ctx.channel())) {
					mConnectionMonitorChannelsMap.get(connectionInfo.getAuctionHouseCode()).add(ctx.channel());
				}

				// 접속자 정보 최초 전송
				for (ChannelId key : mConnectionInfoMap.keySet()) {
					if (mConnectionInfoMap.get(key).getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
						ctx.channel()
								.writeAndFlush(new BidderConnectInfo(mConnectionInfoMap.get(key).getAuctionHouseCode(),
										mConnectionInfoMap.get(key).getUserNo(),
										mConnectionInfoMap.get(key).getChannel(), mConnectionInfoMap.get(key).getOS(),
										"N", "0").getEncodedMessage() + "\r\n");
					}
				}
			}
		} else {
			if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
				// 중복 접속 불가 처리
				ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
						GlobalDefineCode.CONNECT_DUPLICATE_FAIL).getEncodedMessage() + "\r\n");
				ctx.channel().close();

				return;
			} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER)) {
				// 중복 접속 불가 처리
				ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
						GlobalDefineCode.CONNECT_DUPLICATE_FAIL).getEncodedMessage() + "\r\n");
				ctx.channel().close();

				return;
			} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_WATCHER)) {
				// 접속 처리 결과 응답 처리
				ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
						GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

				// Controller 채널 아이디 등록 처리
				if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
					mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);
				}

				// 경매 관전 채널 등록 처리
				if (!mWatcherChannelsMap.containsKey(connectionInfo.getAuctionHouseCode())) {
					mWatcherChannelsMap.put(connectionInfo.getAuctionHouseCode(),
							new DefaultChannelGroup(new DefaultEventExecutor()));
				}

				if (!mWatcherChannelsMap.get(connectionInfo.getAuctionHouseCode()).contains(ctx.channel())) {
					mWatcherChannelsMap.get(connectionInfo.getAuctionHouseCode()).add(ctx.channel());
				}

				if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
					ctx.writeAndFlush(
							new CurrentEntryInfo(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
									.getCurrentEntryInfo()).getEncodedMessage() + "\r\n");

					// 정상 접속자 초기 경매 상태 정보 전달 처리
					ctx.channel().writeAndFlush(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
							.getAuctionStatus().getEncodedMessage() + "\r\n");
				}
			} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_RESULT_MONITOR)) {
				// 접속 처리 결과 응답 처리
				ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
						GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

				// Controller 채널 아이디 등록 처리
				if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
					mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);
				}

				// 낙,유찰 모니터링 채널 등록 처리
				if (!mAuctionResultMonitorChannelsMap.containsKey(connectionInfo.getAuctionHouseCode())) {
					mAuctionResultMonitorChannelsMap.put(connectionInfo.getAuctionHouseCode(),
							new DefaultChannelGroup(new DefaultEventExecutor()));
				}

				if (!mAuctionResultMonitorChannelsMap.get(connectionInfo.getAuctionHouseCode())
						.contains(ctx.channel())) {
					mAuctionResultMonitorChannelsMap.get(connectionInfo.getAuctionHouseCode()).add(ctx.channel());
				}
			} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_CONNECT_MONITOR)) {
				// 접속 처리 결과 응답 처리
				ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
						GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage() + "\r\n");

				// Controller 채널 아이디 등록 처리
				if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
					mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);
				}

				// 접속자 모니터링 채널 등록 처리
				if (!mConnectionMonitorChannelsMap.containsKey(connectionInfo.getAuctionHouseCode())) {
					mConnectionMonitorChannelsMap.put(connectionInfo.getAuctionHouseCode(),
							new DefaultChannelGroup(new DefaultEventExecutor()));
				}

				if (!mConnectionMonitorChannelsMap.get(connectionInfo.getAuctionHouseCode()).contains(ctx.channel())) {
					mConnectionMonitorChannelsMap.get(connectionInfo.getAuctionHouseCode()).add(ctx.channel());
				}

				// 접속자 정보 최초 전송
				for (ChannelId key : mConnectionInfoMap.keySet()) {
					if (mConnectionInfoMap.get(key).getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
						ctx.channel()
								.writeAndFlush(new BidderConnectInfo(mConnectionInfoMap.get(key).getAuctionHouseCode(),
										mConnectionInfoMap.get(key).getUserNo(),
										mConnectionInfoMap.get(key).getChannel(), mConnectionInfoMap.get(key).getOS(),
										"N", "0").getEncodedMessage() + "\r\n");
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

		if (mConnectionInfoMap.containsKey(ctx.channel().id())) {
			closeMember = mConnectionInfoMap.get(ctx.channel().id()).getUserNo();
		}

		if (mConnectionInfoMap.containsKey(ctx.channel().id())) {
			mConnectionInfoMap.remove(ctx.channel().id());

			if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
				mLogger.info("정상적으로 " + closeMember + "회원 정보가 Close 처리되었습니다.");
			}

			mLogger.debug("ConnectorInfoMap size : " + mConnectionInfoMap.size());
		}

		ctx.channel().close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}
}
