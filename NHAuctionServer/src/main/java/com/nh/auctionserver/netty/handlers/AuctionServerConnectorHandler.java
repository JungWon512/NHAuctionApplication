package com.nh.auctionserver.netty.handlers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.RequestLogout;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.RequestAuctionResult;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.StandConnectInfo;
import com.nh.share.server.models.StandEntryInfo;
import com.nh.share.utils.JwtCertTokenUtils;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
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
	private Map<Object, ConnectionInfo> mConnectionInfoMap;
	private Map<String, Object> mConnectionChannelInfoMap;
	private Map<String, ChannelGroup> mStandChannelsMap = null;

	public AuctionServerConnectorHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
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
	protected void channelRead0(ChannelHandlerContext ctx, ConnectionInfo connectionInfo) throws Exception {
		String userMemNum = null;

		if(GlobalDefineCode.FLAG_TEST_MODE) {
			userMemNum = connectionInfo.getUserMemNum();
		} else {
			userMemNum = JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken());
		}
		
		
		// 제어 프로그램 채널 일 경우 토큰과 무관하게 userMemNum 생성 처리 필요
		if(connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER)) {
			userMemNum = connectionInfo.getAuctionHouseCode() + "_" + connectionInfo.getUserMemNum();
		}
		
		// 접속 정보 확인
		// 채널 id가 동일할 경우 중복 접속으로 간주
		// 같은 접속채널로 동일 접속 정보 전달 시 중복 접속으로 간주
		// 같은 회원번호로 접속 시도 시 중복 접속으로 간주
		if (userMemNum != null) {
			if (!mConnectionInfoMap.containsKey(ctx.channel().id()) && !mConnectionInfoMap.containsValue(connectionInfo)
					&& !mConnectionChannelInfoMap.containsKey(userMemNum)) {
				if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
					if (mControllerChannelsMap != null
							&& mControllerChannelsMap.containsKey(connectionInfo.getAuctionHouseCode())
							&& mControllerChannelsMap.get(connectionInfo.getAuctionHouseCode()).size() > 0) {
						mControllerChannelsMap.get(connectionInfo.getAuctionHouseCode())
								.writeAndFlush(connectionInfo.getEncodedMessage() + "\r\n");

						// Connector에 채널 아이디 등록 처리
						if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {	
							mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);

							// Connector Channel Map 등록
							if (GlobalDefineCode.FLAG_TEST_MODE) {
								mConnectionChannelInfoMap.put(
										connectionInfo.getUserMemNum(), ctx);
							} else {
								mConnectionChannelInfoMap.put(
										JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()),
										ctx);
							}
						}
					} else {
						ctx.channel()
								.writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
										GlobalDefineCode.CONNECT_CONTROLLER_ERROR, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage()
										+ "\r\n");
						ctx.channel().close();
					}
				} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER)) {
					mLogger.info("Controller Channel Count : " + mControllerChannelsMap.size());

					if (mConnectionInfoMap.size() > 0) {
						if (mControllerChannelsMap.containsKey(connectionInfo.getAuctionHouseCode()) && mControllerChannelsMap.get(connectionInfo.getAuctionHouseCode()).size() > 0) {
							mLogger.info("이미 제어 프로그램이 실행 중인 상태로 추가 실행이 불가합니다.");
							ctx.channel()
									.writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
											GlobalDefineCode.CONNECT_DUPLICATE, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage()
											+ "\r\n");
							ctx.close();
							return;
						} else {
							// 접속 처리 결과 응답 처리
							ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
									GlobalDefineCode.CONNECT_SUCCESS, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage() + "\r\n");

							// 출하안내시스템 접속 상태 전송
							if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {

								if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()).getIsStandConnect()) {
									ctx.writeAndFlush(new StandConnectInfo(connectionInfo.getAuctionHouseCode(), GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage());
								} else {
									ctx.writeAndFlush(new StandConnectInfo(connectionInfo.getAuctionHouseCode(), GlobalDefineCode.CONNECT_FAIL).getEncodedMessage());
								}
							}
							
							// Controller 채널 아이디 등록 처리
							if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
								mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);

								// Connector Channel Map 등록
								mConnectionChannelInfoMap.put(
										JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()),
										ctx);
							}

							// 제어 프로그램 채널 등록 처리
							if (!mControllerChannelsMap.containsKey(connectionInfo.getAuctionHouseCode())) {
								mControllerChannelsMap.put(connectionInfo.getAuctionHouseCode(),
										new DefaultChannelGroup(new DefaultEventExecutor()));
							}

							if (!mControllerChannelsMap.get(connectionInfo.getAuctionHouseCode())
									.contains(ctx.channel())) {
								mControllerChannelsMap.get(connectionInfo.getAuctionHouseCode()).add(ctx.channel());
							}

							// 현재 출품 정보 전송
							if (mAuctionScheduler.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
									.equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
								ctx.writeAndFlush(new ResponseCode(connectionInfo.getAuctionHouseCode(),
										GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage()
										+ "\r\n");
							} else {
								if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
									if (mAuctionScheduler.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
											.equals(GlobalDefineCode.AUCTION_STATUS_PASS)
											|| mAuctionScheduler
													.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
													.equals(GlobalDefineCode.AUCTION_STATUS_COMPLETED)) {

										ctx.writeAndFlush(new CurrentEntryInfo(
												mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
														.getCurrentEntryInfo()).getEncodedMessage()
												+ "\r\n");

										// 정상 접속자 초기 경매 상태 정보 전달 처리
										ctx.channel()
												.writeAndFlush(mAuctionScheduler
														.getAuctionState(connectionInfo.getAuctionHouseCode())
														.getAuctionStatus().getEncodedMessage() + "\r\n");

										// 낙유찰 정보 수신이 필요한 경우 확인
										ctx.writeAndFlush(new RequestAuctionResult(connectionInfo.getAuctionHouseCode(),
												mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
														.getAuctionStatus().getEntryNum()).getEncodedMessage());
									} else {
										ctx.writeAndFlush(new CurrentEntryInfo(
												mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
														.getCurrentEntryInfo()).getEncodedMessage()
												+ "\r\n");

										// 정상 접속자 초기 경매 상태 정보 전달 처리
										ctx.channel()
												.writeAndFlush(mAuctionScheduler
														.getAuctionState(connectionInfo.getAuctionHouseCode())
														.getAuctionStatus().getEncodedMessage() + "\r\n");
									}
								}
							}
							
							// 접속자 정보 최초 전송
							for (Object mapKey : mConnectionInfoMap.keySet()) {
								if (mConnectionInfoMap.get(mapKey).getAuctionHouseCode().equals(connectionInfo.getAuctionHouseCode())
										&& mConnectionInfoMap.get(mapKey).getChannel()
												.equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
									BidderConnectInfo bidderConnectInfo = new BidderConnectInfo(mConnectionInfoMap.get(mapKey).getAuctionHouseCode(),
											mConnectionInfoMap.get(mapKey).getAuctionJoinNum(),
											mConnectionInfoMap.get(mapKey).getChannel(), mConnectionInfoMap.get(mapKey).getOS(),
											"N", "0");
									
									mLogger.info("진행프로그램 접속자 정보 전달 : " + bidderConnectInfo.getEncodedMessage());
									
									ctx.channel()
											.writeAndFlush(bidderConnectInfo.getEncodedMessage() + "\r\n");
								}
							}
						}
						
//						for (Object key : mConnectionInfoMap.keySet()) {
//							if (mConnectionInfoMap.get(key).getChannel()
//									.equals(GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER)) {
//								mLogger.info("이미 제어 프로그램이 실행 중인 상태로 추가 실행이 불가합니다.");
//								ctx.channel()
//										.writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
//												GlobalDefineCode.CONNECT_ETC_ERROR, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage()
//												+ "\r\n");
//								return;
//							} else {
//								// 접속 처리 결과 응답 처리
//								ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
//										GlobalDefineCode.CONNECT_SUCCESS, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage() + "\r\n");
//
//								// Controller 채널 아이디 등록 처리
//								if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
//									mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);
//
//									// Connector Channel Map 등록
//									mConnectionChannelInfoMap.put(
//											JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()),
//											ctx);
//								}
//
//								// 제어 프로그램 채널 등록 처리
//								if (!mControllerChannelsMap.containsKey(connectionInfo.getAuctionHouseCode())) {
//									mControllerChannelsMap.put(connectionInfo.getAuctionHouseCode(),
//											new DefaultChannelGroup(new DefaultEventExecutor()));
//								}
//
//								if (!mControllerChannelsMap.get(connectionInfo.getAuctionHouseCode())
//										.contains(ctx.channel())) {
//									mControllerChannelsMap.get(connectionInfo.getAuctionHouseCode()).add(ctx.channel());
//								}
//
//								// 현재 출품 정보 전송
//								if (mAuctionScheduler.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
//										.equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
//									ctx.writeAndFlush(new ResponseCode(connectionInfo.getAuctionHouseCode(),
//											GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage()
//											+ "\r\n");
//								} else {
//									if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
//										if (mAuctionScheduler.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
//												.equals(GlobalDefineCode.AUCTION_STATUS_PASS)
//												|| mAuctionScheduler
//														.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
//														.equals(GlobalDefineCode.AUCTION_STATUS_COMPLETED)) {
//
//											ctx.writeAndFlush(new CurrentEntryInfo(
//													mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
//															.getCurrentEntryInfo()).getEncodedMessage()
//													+ "\r\n");
//
//											// 정상 접속자 초기 경매 상태 정보 전달 처리
//											ctx.channel()
//													.writeAndFlush(mAuctionScheduler
//															.getAuctionState(connectionInfo.getAuctionHouseCode())
//															.getAuctionStatus().getEncodedMessage() + "\r\n");
//
//											// 낙유찰 정보 수신이 필요한 경우 확인
//											ctx.writeAndFlush(new RequestAuctionResult(connectionInfo.getAuctionHouseCode(),
//													mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
//															.getAuctionStatus().getEntryNum()).getEncodedMessage());
//										} else {
//											ctx.writeAndFlush(new CurrentEntryInfo(
//													mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
//															.getCurrentEntryInfo()).getEncodedMessage()
//													+ "\r\n");
//
//											// 정상 접속자 초기 경매 상태 정보 전달 처리
//											ctx.channel()
//													.writeAndFlush(mAuctionScheduler
//															.getAuctionState(connectionInfo.getAuctionHouseCode())
//															.getAuctionStatus().getEncodedMessage() + "\r\n");
//										}
//									}
//								}
//								
//								// 접속자 정보 최초 전송
//								for (Object mapKey : mConnectionInfoMap.keySet()) {
//									if (mConnectionInfoMap.get(mapKey).getAuctionHouseCode().equals(connectionInfo.getAuctionHouseCode())
//											&& mConnectionInfoMap.get(mapKey).getChannel()
//													.equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
//										ctx.channel()
//												.writeAndFlush(new BidderConnectInfo(mConnectionInfoMap.get(mapKey).getAuctionHouseCode(),
//														mConnectionInfoMap.get(mapKey).getAuctionJoinNum(),
//														mConnectionInfoMap.get(mapKey).getChannel(), mConnectionInfoMap.get(mapKey).getOS(),
//														"N", "0").getEncodedMessage() + "\r\n");
//									}
//								}
//							}
//						}
					} else {
						// 접속 처리 결과 응답 처리
						ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
								GlobalDefineCode.CONNECT_SUCCESS, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage() + "\r\n");

						// Controller 채널 아이디 등록 처리
						if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
							mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);

							// Connector Channel Map 등록
							mConnectionChannelInfoMap.put(JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()), ctx);
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
						if (mAuctionScheduler.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
								.equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
							ctx.writeAndFlush(new ResponseCode(connectionInfo.getAuctionHouseCode(),
									GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage() + "\r\n");
						} else {
							if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
								if (mAuctionScheduler.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
										.equals(GlobalDefineCode.AUCTION_STATUS_PASS)
										|| mAuctionScheduler.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
												.equals(GlobalDefineCode.AUCTION_STATUS_COMPLETED)) {
									
									ctx.writeAndFlush(new CurrentEntryInfo(mAuctionScheduler
											.getAuctionState(connectionInfo.getAuctionHouseCode()).getCurrentEntryInfo())
													.getEncodedMessage()
											+ "\r\n");
									
									// 정상 접속자 초기 경매 상태 정보 전달 처리
									ctx.channel().writeAndFlush(
											mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
													.getAuctionStatus().getEncodedMessage() + "\r\n");

									// 낙유찰 정보 수신이 필요한 경우 확인
									ctx.writeAndFlush(new RequestAuctionResult(connectionInfo.getAuctionHouseCode(),
											mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
													.getAuctionStatus().getEntryNum()).getEncodedMessage());
								} else {
									ctx.writeAndFlush(new CurrentEntryInfo(mAuctionScheduler
											.getAuctionState(connectionInfo.getAuctionHouseCode()).getCurrentEntryInfo())
													.getEncodedMessage()
											+ "\r\n");

									// 정상 접속자 초기 경매 상태 정보 전달 처리
									ctx.channel().writeAndFlush(
											mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
													.getAuctionStatus().getEncodedMessage() + "\r\n");
								}
							}
						}
						
						// 접속자 정보 최초 전송
						for (Object key : mConnectionInfoMap.keySet()) {
							if (mConnectionInfoMap.get(key).getAuctionHouseCode().equals(connectionInfo.getAuctionHouseCode())
									&& mConnectionInfoMap.get(key).getChannel()
											.equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
								ctx.channel()
										.writeAndFlush(new BidderConnectInfo(mConnectionInfoMap.get(key).getAuctionHouseCode(),
												mConnectionInfoMap.get(key).getAuctionJoinNum(),
												mConnectionInfoMap.get(key).getChannel(), mConnectionInfoMap.get(key).getOS(),
												"N", "0").getEncodedMessage() + "\r\n");
							}
						}
					}
				} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_WATCHER)) {
					// 접속 처리 결과 응답 처리
					ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
							GlobalDefineCode.CONNECT_SUCCESS, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage() + "\r\n");

					// Controller 채널 아이디 등록 처리
					if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
						mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);

						// Connector Channel Map 등록
						mConnectionChannelInfoMap
								.put(JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()), ctx);
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
					if (mAuctionScheduler.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
							.equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
						ctx.writeAndFlush(new ResponseCode(connectionInfo.getAuctionHouseCode(),
								GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage() + "\r\n");
					} else {
						if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
							ctx.writeAndFlush(new CurrentEntryInfo(mAuctionScheduler
									.getAuctionState(connectionInfo.getAuctionHouseCode()).getCurrentEntryInfo())
											.getEncodedMessage()
									+ "\r\n");

							// 정상 접속자 초기 경매 상태 정보 전달 처리
							ctx.channel()
									.writeAndFlush(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
											.getAuctionStatus().getEncodedMessage() + "\r\n");
							
							if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()).getRetryTargetInfo() != null) {
								ctx.channel().writeAndFlush(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()).getRetryTargetInfo().getEncodedMessage() + "\r\n");
							}
							
							if (!mAuctionScheduler.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
									.equals(GlobalDefineCode.AUCTION_STATUS_READY) && mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()).getAuctionBidStatus() != null) {
								ctx.channel().writeAndFlush(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()).getAuctionBidStatus().getEncodedMessage() + "\r\n");
							}
						}
					}
				} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_RESULT_MONITOR)) {
					// 접속 처리 결과 응답 처리
					ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
							GlobalDefineCode.CONNECT_SUCCESS, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage() + "\r\n");

					// Controller 채널 아이디 등록 처리
					if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
						mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);

						// Connector Channel Map 등록
						mConnectionChannelInfoMap
								.put(JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()), ctx);
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
							GlobalDefineCode.CONNECT_SUCCESS, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage() + "\r\n");

					// Controller 채널 아이디 등록 처리
					if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
						mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);

						// Connector Channel Map 등록
						mConnectionChannelInfoMap
								.put(JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()), ctx);
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
					for (Object key : mConnectionInfoMap.keySet()) {
						if (mConnectionInfoMap.get(key).getAuctionHouseCode().equals(connectionInfo.getAuctionHouseCode())
								&& mConnectionInfoMap.get(key).getChannel()
										.equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
							ctx.channel()
									.writeAndFlush(new BidderConnectInfo(mConnectionInfoMap.get(key).getAuctionHouseCode(),
											mConnectionInfoMap.get(key).getAuctionJoinNum(),
											mConnectionInfoMap.get(key).getChannel(), mConnectionInfoMap.get(key).getOS(),
											"N", "0").getEncodedMessage() + "\r\n");
						}
					}
				} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_STAND)) {
					
					if (mStandChannelsMap.containsKey(connectionInfo.getAuctionHouseCode()) && mStandChannelsMap.get(connectionInfo.getAuctionHouseCode()).contains(ctx.channel())) {
						mLogger.info("이미 출하 안내 시스템이 접속 중인 상태로 중복 접속이 불가합니다.");
						ctx.channel()
								.writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
										GlobalDefineCode.CONNECT_ETC_ERROR, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage()
										+ "\r\n");
						return;
					} else {
						// 접속 처리 결과 응답 처리
						ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
								GlobalDefineCode.CONNECT_SUCCESS, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage() + "\r\n");

						// Controller 채널 아이디 등록 처리
						if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
							mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);

							// Connector Channel Map 등록
							mConnectionChannelInfoMap
									.put(JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()), ctx);
						}

						// 경매 출하 안내 시스템 채널 등록 처리
						if (!mStandChannelsMap.containsKey(connectionInfo.getAuctionHouseCode())) {
							mStandChannelsMap.put(connectionInfo.getAuctionHouseCode(),
									new DefaultChannelGroup(new DefaultEventExecutor()));
						}

						if (!mStandChannelsMap.get(connectionInfo.getAuctionHouseCode()).contains(ctx.channel())) {
							mStandChannelsMap.get(connectionInfo.getAuctionHouseCode()).add(ctx.channel());
						}

						// 출하안내시스템 접속 상태 저장
						if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
							mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()).setIsStandConnect(true);

							// 출하안내시스템 접속 상태 정보 전송
							mAuctionServer
									.itemAdded(new StandConnectInfo(connectionInfo.getAuctionHouseCode(), GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage());
						}
						
						// 현재 출품 정보 전송
						if (mAuctionScheduler.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
								.equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
							if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
								// 정상 접속자 초기 경매 상태 정보 전달 처리
								ctx.channel()
										.writeAndFlush(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
												.getAuctionStatus().getEncodedMessage() + "\r\n");
							}
						} else {
							if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
								// 정상 접속자 초기 경매 상태 정보 전달 처리
								ctx.channel()
										.writeAndFlush(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode())
												.getAuctionStatus().getEncodedMessage() + "\r\n");
								
								List<EntryInfo> entryList = mAuctionScheduler.getAuctionEntryRepositoryMap(connectionInfo.getAuctionHouseCode()).getEntryList();
								
								if (entryList != null) {
									if (entryList.size() > 0) {
										int i = 0;
										for (EntryInfo entryInfo : entryList) {
											i++;
											StandEntryInfo standEntryInfo = new StandEntryInfo(entryInfo);
											
											ctx.channel().writeAndFlush(standEntryInfo.getEncodedMessage() + "\r\n");
											
											mLogger.info("StandEntryInfo[" + i + "] : " + standEntryInfo.getEncodedMessage());
										}
									}
								}
							}
						}
					}
				}
			} else {
				if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
					// 중복 접속 불가 처리
					ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
							GlobalDefineCode.CONNECT_DUPLICATE, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage() + "\r\n");
					ctx.channel().close();

					return;
				} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER)) {
					// 중복 접속 불가 처리
					ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
							GlobalDefineCode.CONNECT_DUPLICATE, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage() + "\r\n");
					ctx.channel().close();

					return;
				} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_WATCHER)) {
					// 접속 처리 결과 응답 처리
					ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
							GlobalDefineCode.CONNECT_SUCCESS, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage() + "\r\n");

					// Controller 채널 아이디 등록 처리
					if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
						mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);

						// Connector Channel Map 등록
						mConnectionChannelInfoMap
								.put(JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()), ctx);
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
						
						if (mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()).getRetryTargetInfo() != null) {
							ctx.channel().writeAndFlush(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()).getRetryTargetInfo().getEncodedMessage() + "\r\n");
						}
						
						if (!mAuctionScheduler.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
								.equals(GlobalDefineCode.AUCTION_STATUS_READY) && mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()).getAuctionBidStatus() != null) {
							ctx.channel().writeAndFlush(mAuctionScheduler.getAuctionState(connectionInfo.getAuctionHouseCode()).getAuctionBidStatus().getEncodedMessage() + "\r\n");
						}
					}
				} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_RESULT_MONITOR)) {
					// 접속 처리 결과 응답 처리
					ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
							GlobalDefineCode.CONNECT_SUCCESS, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage() + "\r\n");

					// Controller 채널 아이디 등록 처리
					if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
						mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);

						// Connector Channel Map 등록
						mConnectionChannelInfoMap
								.put(JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()), ctx);
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
							GlobalDefineCode.CONNECT_SUCCESS, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage() + "\r\n");

					// Controller 채널 아이디 등록 처리
					if (!mConnectionInfoMap.containsKey(ctx.channel().id())) {
						mConnectionInfoMap.put(ctx.channel().id(), connectionInfo);

						// Connector Channel Map 등록
						mConnectionChannelInfoMap
								.put(JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()), ctx);
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
					for (Object key : mConnectionInfoMap.keySet()) {
						if (mConnectionInfoMap.get(key).getAuctionHouseCode().equals(connectionInfo.getAuctionHouseCode())
								&& mConnectionInfoMap.get(key).getChannel()
										.equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
							ctx.channel()
									.writeAndFlush(new BidderConnectInfo(mConnectionInfoMap.get(key).getAuctionHouseCode(),
											mConnectionInfoMap.get(key).getAuctionJoinNum(),
											mConnectionInfoMap.get(key).getChannel(), mConnectionInfoMap.get(key).getOS(),
											"N", "0").getEncodedMessage() + "\r\n");
						}
					}
				} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_STAND)) {
					// 중복 접속 불가 처리
					ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
							GlobalDefineCode.CONNECT_DUPLICATE, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage() + "\r\n");
					ctx.channel().close();

					return;
				}
			}
		} else {
			// 사용자 정보가 유효하지 않아 접속 불가
			ctx.channel().writeAndFlush(new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
					GlobalDefineCode.CONNECT_ETC_ERROR, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage() + "\r\n");
			ctx.channel().close();
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
			if(mConnectionInfoMap.get(ctx.channel().id()).getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER)) {
				closeMember = mConnectionInfoMap.get(ctx.channel().id()).getAuctionHouseCode() + "_" + mConnectionInfoMap.get(ctx.channel().id()).getUserMemNum();
			} else {
				if (GlobalDefineCode.FLAG_TEST_MODE) {
					closeMember = mConnectionInfoMap.get(ctx.channel().id()).getUserMemNum();
				} else {
					closeMember = JwtCertTokenUtils.getInstance()
							.getUserMemNum(mConnectionInfoMap.get(ctx.channel().id()).getAuthToken());
				}
			}

			mAuctionServer
					.logoutMember(new RequestLogout(mConnectionInfoMap.get(ctx.channel().id()).getAuctionHouseCode(),
							closeMember, mConnectionInfoMap.get(ctx.channel().id()).getChannel(), mConnectionInfoMap.get(ctx.channel().id()).getOS()), false);

//			mConnectionInfoMap.remove(ctx.channel().id());
//			mConnectionChannelInfoMap.remove(closeMember);
//
//			if (!mConnectionInfoMap.containsKey(ctx.channel().id())
//					&& !mConnectionChannelInfoMap.containsKey(closeMember)) {
//				mLogger.info("정상적으로 " + closeMember + "회원 정보가 Close 처리되었습니다.");
//			}
//
//			mLogger.info("ConnectorInfoMap size : " + mConnectionInfoMap.size());

		}

//		ctx.channel().close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}
}
