package com.nh.auctionserver.netty;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.handlers.AuctionServerConnectorHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedAuctionResponseSessionHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedBiddingHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedEditSettingHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedPassAuctionHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedRequestLogoutHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedStartAuctionHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedStopAuctionHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedToastMessageRequestHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerInboundDecoder;
import com.nh.auctionserver.netty.handlers.AuctionUserDuplexHandler;
import com.nh.auctionserver.setting.AuctionServerSetting;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.CommonMessageParser;
import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.CurrentEntryInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.controller.ControllerMessageParser;
import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.controller.models.EditSetting;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.PassAuction;
import com.nh.share.controller.models.RequestLogout;
import com.nh.share.controller.models.StartAuction;
import com.nh.share.controller.models.StopAuction;
import com.nh.share.controller.models.ToastMessageRequest;
import com.nh.share.server.ServerMessageParser;
import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.ExceptionCode;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.ToastMessage;
import com.nh.share.setting.AuctionShareSetting;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutor;

public class AuctionServer {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServer.class);

	private EventLoopGroup mBossGroup;
	private EventLoopGroup mWorkerGroup;
	private ChannelGroup mControllerChannels = new DefaultChannelGroup(new DefaultEventExecutor());
	private ChannelGroup mBidderChannels = new DefaultChannelGroup(new DefaultEventExecutor());
	private ChannelGroup mWatcherChannels = new DefaultChannelGroup(new DefaultEventExecutor());
	private ChannelGroup mAuctionResultMonitorChannels = new DefaultChannelGroup(new DefaultEventExecutor());
	private ChannelGroup mConnectionMonitorChannels = new DefaultChannelGroup(new DefaultEventExecutor());

	private Auctioneer mAuctioneer;

	private Map<ChannelId, ConnectionInfo> mConnectorInfoMap;

	private String mAuctionCode;
	private String mAuctionRound;
	private String mAuctionLaneCode;

	private boolean mWaitServerShutDown = false;

	private Map<Integer, Object> mBiddingInfoMap = new HashMap<Integer, Object>();

	private AuctionServer(Builder builder) {
		this(builder.port, builder.auctionCode, builder.auctionRound, builder.auctionLaneCode, builder.portCount);
	}

	private AuctionServer(int port, String auctionCode, String auctionRound, String auctionLaneCode, int portCount) {
		try {
			mLogger.info("======= Auctoin Server Informations[Start] =======");
			mLogger.info("Server Host : " + InetAddress.getLocalHost());
			mLogger.info("Server Port : " + port);
			mLogger.info("Auction Code : " + auctionCode);
			mLogger.info("Auction Round : " + auctionRound);
			mLogger.info("Auction Lane Code : " + auctionLaneCode);
			mLogger.info("======= Auctoin Server Informations[End] =======");

			mAuctionCode = auctionCode;
			mAuctionRound = auctionRound;
			mAuctionLaneCode = auctionLaneCode;

			createAuctioneer(this, auctionCode, auctionRound, auctionLaneCode);
			createNettyServer(port);
		} catch (Exception e) {
			e.printStackTrace();
			stopServer();
		}
	}

	private void createAuctioneer(AuctionServer auctionServer, String auctionCode, String auctionRound,
			String auctionLaneCode) {
		mAuctioneer = new Auctioneer(this);
	}

	/**
	 * Netty 서버를 설정하고 생성, 바인드한다.
	 * 
	 * @param port Netty 포트
	 * @throws Exception
	 */
	private void createNettyServer(int port) throws Exception {
		/*
		 * 사설 인증서 구현 클래스 - 사용시 주석 해제 SelfSignedCertificate ssc = new
		 * SelfSignedCertificate(); SslContext sslContext =
		 * SslContextBuilder.forServer(ssc.certificate(),ssc.privateKey()).build();
		 */

		/*
		 * bossGroup 클라이언트의 연결을 수락하는 부모 스레드 그룹 NioEventLoopGroup(인수) 스레드 그룹 내에서 생성할 최대
		 * 스레드 수 1이므로 단일 스레드
		 */
		mBossGroup = new NioEventLoopGroup(1);
		/*
		 * 연결된 클라이언트 소켓으로부터 데이터 입출력(I/O) 및 이벤트처리를 담당하는 자식 쓰레드 그룹 생성자에 인수가 없으므로 디폴트 값 설정.
		 * CPU 코어 수 * 2 쓰레드 생성.
		 */
		mWorkerGroup = new NioEventLoopGroup();
		/* 부트 스트랩 객체 생성, 서버를 구성할 수 있도록 해주는 Helper클래스 */
		ServerBootstrap b = new ServerBootstrap();
		/* 스레드 그룹 초기화, (부모,자식) */
		b.group(mBossGroup, mWorkerGroup)
				/* 채널초기화, 부모 쓰레드가 사용할 네트워크 입출력 모드 설정 */
				.channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100)
				/* 서버 소켓 채널에서 발생한 이벤트 로그 출력 */
				.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast(new LoggingHandler(LogLevel.INFO));
						// pipeline.addLast(sslContext.newHandler(ch.alloc(),
						// AuctionShareSetting.Server.SSL_HOST, AuctionShareSetting.Server.SSL_PORT));

						pipeline.addLast("idleStateHandler",
								new IdleStateHandler(AuctionServerSetting.AUCTION_SERVER_READ_CHECK_SESSION_TIME,
										AuctionServerSetting.AUCTION_SERVER_WRITE_CHECK_SESSION_TIME, 0));
						pipeline.addLast("AuctionUserDuplexHandler", new AuctionUserDuplexHandler(mConnectorInfoMap));
						pipeline.addLast(new DelimiterBasedFrameDecoder(AuctionShareSetting.NETTY_MAX_FRAME_LENGTH,
								Delimiters.lineDelimiter()));
						pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
						pipeline.addLast(new AuctionServerInboundDecoder());

						pipeline.addLast(new AuctionServerConnectorHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mControllerChannels, mBidderChannels, mWatcherChannels,
								mAuctionResultMonitorChannels, mConnectionMonitorChannels));
						pipeline.addLast(new AuctionServerDecodedBiddingHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mControllerChannels, mBidderChannels, mWatcherChannels,
								mAuctionResultMonitorChannels, mConnectionMonitorChannels));
						pipeline.addLast(new AuctionServerDecodedEditSettingHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mControllerChannels, mBidderChannels, mWatcherChannels,
								mAuctionResultMonitorChannels, mConnectionMonitorChannels));
						pipeline.addLast(new AuctionServerDecodedPassAuctionHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mControllerChannels, mBidderChannels, mWatcherChannels,
								mAuctionResultMonitorChannels, mConnectionMonitorChannels));
						pipeline.addLast(new AuctionServerDecodedStopAuctionHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mControllerChannels, mBidderChannels, mWatcherChannels,
								mAuctionResultMonitorChannels, mConnectionMonitorChannels));
						pipeline.addLast(new AuctionServerDecodedStartAuctionHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mControllerChannels, mBidderChannels, mWatcherChannels,
								mAuctionResultMonitorChannels, mConnectionMonitorChannels));
						pipeline.addLast(new AuctionServerDecodedToastMessageRequestHandler(AuctionServer.this,
								mAuctioneer, mConnectorInfoMap, mControllerChannels, mBidderChannels, mWatcherChannels,
								mAuctionResultMonitorChannels, mConnectionMonitorChannels));
						pipeline.addLast(new AuctionServerDecodedAuctionResponseSessionHandler(AuctionServer.this,
								mAuctioneer, mConnectorInfoMap, mControllerChannels, mBidderChannels, mWatcherChannels,
								mAuctionResultMonitorChannels, mConnectionMonitorChannels));
						pipeline.addLast(new AuctionServerDecodedRequestLogoutHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mControllerChannels, mBidderChannels, mWatcherChannels,
								mAuctionResultMonitorChannels, mConnectionMonitorChannels));

						pipeline.addFirst(new StringEncoder(CharsetUtil.UTF_8));
					}
				})
				/* Nagle 알고리즘 비활성화 여부 설정 */
				.childOption(ChannelOption.TCP_NODELAY, true)
				/* 정해진 시간마다 keepalive packet 전송 */
				.childOption(ChannelOption.SO_KEEPALIVE, true);

		b.bind(port);
	}

	public void stopServer() {
		mLogger.debug("Auction server stop!!");
		if (mAuctioneer.getAuctionState().getAuctionState() == null
				|| !mAuctioneer.getAuctionState().getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {
			mLogger.debug("Auction server disconnect all channel!!");
			if (mBidderChannels.size() > 0) {
				mBidderChannels.close();
				mWaitServerShutDown = true;
			}

			if (mWatcherChannels.size() > 0) {
				mWatcherChannels.close();
				mWaitServerShutDown = true;
			}

			if (mControllerChannels.size() > 0) {
				mControllerChannels.close();
				mWaitServerShutDown = true;
			}

			if (mAuctionResultMonitorChannels.size() > 0) {
				mAuctionResultMonitorChannels.close();
				mWaitServerShutDown = true;
			}

			if (mConnectionMonitorChannels.size() > 0) {
				mConnectionMonitorChannels.close();
				mWaitServerShutDown = true;
			}

			mBossGroup.shutdownGracefully();
			mWorkerGroup.shutdownGracefully();

			if (!mWaitServerShutDown) {
				mLogger.info(
						"************************************* Bye Auction World! **************************************");

				System.exit(0);
			}
		}
	}

	/**
	 * 
	 * @MethodName getControllerChannels
	 * @Description 현재 제어프로그램의 채널 그룹을 반환 처리
	 *
	 * @return ChannelGroup 제어프로그램 채널 그룹
	 */
	public ChannelGroup getControllerChannels() {
		return mControllerChannels;
	}

	public void entryAdded(Object event) {
		if (event instanceof Bidding) {
			mLogger.debug("EntryAdded : " + ((Bidding) event).getEncodedMessage());
			if (mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_COMPLETED)
					|| mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_SUCCESS)
					|| mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_FAIL)
					|| mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {
				mLogger.debug("경매 출품 단위 완료/낙찰/유찰/종료 상태, skip setBidding");
			} else {
				mAuctioneer.setBidding((Bidding) event);
			}
		}

		if (event instanceof ConnectionInfo) {
			mLogger.debug("ConnectionInfo entryAdded : " + ((ConnectionInfo) event).getEncodedMessage());
			mLogger.debug("ConnectorInfoMap size : " + mConnectorInfoMap.size());

			if (((ConnectionInfo) event).getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
				if (mConnectionMonitorChannels != null && mConnectionMonitorChannels.size() > 0) {
					mConnectionMonitorChannels.writeAndFlush(new BidderConnectInfo(((ConnectionInfo) event).getUserNo(),
							((ConnectionInfo) event).getChannel(), ((ConnectionInfo) event).getOS(), "N")
									.getEncodedMessage()
							+ "\r\n");
				}
			}
		}
	}

	public void entryUpdated(Object event) {
		mLogger.debug("EntryUpdated : " + ((Bidding) event).getEncodedMessage());
		if (event instanceof Bidding) {
			if (mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_COMPLETED)
					|| mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_SUCCESS)
					|| mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_FAIL)
					|| mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {
				mLogger.debug("경매 출품 단위 완료/낙찰/유찰/종료 상태, skip setBidding");
			} else {
				mAuctioneer.setBidding((Bidding) event);
			}
		}
	}

	public void itemAdded(String event) {
		// mLogger.debug("HazelcastBiddings itemAdded : " + event.getItem());
		switch (event.charAt(0)) {
		case FromAuctionServer.ORIGIN:
			FromAuctionServer serverParsedMessage = ServerMessageParser.parse(event);

			if (serverParsedMessage instanceof AuctionCountDown) {
				channelItemWriteAndFlush(((AuctionCountDown) serverParsedMessage));
			}

			if (serverParsedMessage instanceof ToastMessage) {
				channelItemWriteAndFlush(((ToastMessage) serverParsedMessage));
			}

			if (serverParsedMessage instanceof FavoriteEntryInfo) {
				channelItemWriteAndFlush(((FavoriteEntryInfo) serverParsedMessage));
			}

			if (serverParsedMessage instanceof ExceptionCode) {
				channelItemWriteAndFlush(((ExceptionCode) serverParsedMessage));
			}
			
			if (serverParsedMessage instanceof AuctionCheckSession) {
				channelItemWriteAndFlush(((AuctionCheckSession) serverParsedMessage));
			}
			
			if (serverParsedMessage instanceof BidderConnectInfo) {
				channelItemWriteAndFlush(((BidderConnectInfo) serverParsedMessage));
			}
			break;
			
		case FromAuctionController.ORIGIN:
			FromAuctionController controllerParsedMessage = ControllerMessageParser.parse(event);

			if (controllerParsedMessage instanceof EditSetting) {
			}

			if (controllerParsedMessage instanceof PassAuction) {
				mLogger.info("경매 유찰 : " + ((PassAuction) controllerParsedMessage).getEntryNum());
				mAuctioneer.passAuction();
			}

			if (controllerParsedMessage instanceof StopAuction) {
				mLogger.info("경매 진행 정지 : " + ((StopAuction) controllerParsedMessage).getEntryNum());
				mAuctioneer.stopAuction();
			}

			if (controllerParsedMessage instanceof StartAuction) {
				mLogger.info("경매 진행 시작 : " + ((StartAuction) controllerParsedMessage).getEntryNum());

				// 경매 최초 시작 여부 확인 후 카운트 다운 상황인지 일반 경매 시작 상황인지 확인
				if (mAuctioneer.getAuctionCountDownStatus().equals(GlobalDefineCode.AUCTION_COUNT_DOWN_COMPLETED)) {
					mAuctioneer.startAuction();
				} else {
					mAuctioneer.startAuctionCountDown();
				}				
			}

			if (controllerParsedMessage instanceof ToastMessageRequest) {
				mAuctioneer.broadcastToastMessage((ToastMessageRequest)controllerParsedMessage);
			}

			if (controllerParsedMessage instanceof RequestLogout) {
				
				
			}

			if (controllerParsedMessage instanceof EntryInfo) {
				
			}
			break;
		case FromAuctionCommon.ORIGIN:
			FromAuctionCommon commonParsedMessage = CommonMessageParser.parse(event);

			if (commonParsedMessage instanceof AuctionStatus) {
				mAuctioneer.setAuctionStatus((AuctionStatus) commonParsedMessage);

				channelItemWriteAndFlush((AuctionStatus) commonParsedMessage);
			}

			if (commonParsedMessage instanceof Bidding) { // Bidding은 경매가 시작한 후에만 수신되도록 한다.
				if (mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_START)
						|| mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)
						|| mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_COMPETITIVE)) {

					((Bidding) commonParsedMessage).setBiddingTime(
							LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

					if (!mBiddingInfoMap.containsKey(((Bidding) commonParsedMessage).getPriceInt())) {
						mBiddingInfoMap.put(((Bidding) commonParsedMessage).getPriceInt(),
								(Bidding) commonParsedMessage);
						entryAdded((Bidding) commonParsedMessage);
					} else {
						entryUpdated((Bidding) commonParsedMessage);
					}

					mConnectionMonitorChannels
							.writeAndFlush(new BidderConnectInfo(((Bidding) commonParsedMessage).getUserNo(),
									GlobalDefineCode.CONNECT_CHANNEL_BIDDER,
									((Bidding) commonParsedMessage).getChannel(), "B").getEncodedMessage() + "\r\n");
				}
			}
			break;
		default:
			break;
		}
	}

	public void channelItemWriteAndFlush(Object event) {
		if (event instanceof FromAuctionServer) {
			String message = ((FromAuctionServer) event).getEncodedMessage();
			String[] splitMessages = message.split(AuctionShareSetting.DELIMITER_REGEX);

			Thread thread = new Thread() {
				@Override
				public void run() {
					switch (splitMessages[0].charAt(1)) {
					case AuctionCountDown.TYPE: // 경매 시작 카운트 다운 정보 전송
						if (mBidderChannels != null && mBidderChannels.size() > 0) {
							mBidderChannels.writeAndFlush(message + "\r\n");
						}

						if (mWatcherChannels != null && mWatcherChannels.size() > 0) {
							mWatcherChannels.writeAndFlush(message + "\r\n");
						}
						break;
					case ToastMessage.TYPE: // 메시지 전송 처리
						if (mBidderChannels != null && mBidderChannels.size() > 0) {
							mBidderChannels.writeAndFlush(message + "\r\n");
						}
						break;
					case FavoriteEntryInfo.TYPE: // 관심출품 여부 정보
						if (mBidderChannels != null && mBidderChannels.size() > 0) {
							mBidderChannels.writeAndFlush(message + "\r\n");
						}

						if (mWatcherChannels != null && mWatcherChannels.size() > 0) {
							mWatcherChannels.writeAndFlush(message + "\r\n");
						}
						break;
					case ExceptionCode.TYPE: // 예외 상황 전송 처리
						if (mBidderChannels != null && mBidderChannels.size() > 0) {
							mBidderChannels.writeAndFlush(message + "\r\n");
						}

						if (mWatcherChannels != null && mWatcherChannels.size() > 0) {
							mWatcherChannels.writeAndFlush(message + "\r\n");
						}

						if (mControllerChannels != null && mControllerChannels.size() > 0) {
							mControllerChannels.writeAndFlush(message + "\r\n");
						}

						if (mAuctionResultMonitorChannels != null && mAuctionResultMonitorChannels.size() > 0) {
							mAuctionResultMonitorChannels.writeAndFlush(message + "\r\n");
						}

						if (mConnectionMonitorChannels != null && mConnectionMonitorChannels.size() > 0) {
							mConnectionMonitorChannels.writeAndFlush(message + "\r\n");
						}
						break;
					case AuctionCheckSession.TYPE: // 접속 유효 확인 처리
						if (mBidderChannels != null && mBidderChannels.size() > 0) {
							mBidderChannels.writeAndFlush(message + "\r\n");
						}

						if (mWatcherChannels != null && mWatcherChannels.size() > 0) {
							mWatcherChannels.writeAndFlush(message + "\r\n");
						}

						if (mControllerChannels != null && mControllerChannels.size() > 0) {
							mControllerChannels.writeAndFlush(message + "\r\n");
						}

						if (mAuctionResultMonitorChannels != null && mAuctionResultMonitorChannels.size() > 0) {
							mAuctionResultMonitorChannels.writeAndFlush(message + "\r\n");
						}

						if (mConnectionMonitorChannels != null && mConnectionMonitorChannels.size() > 0) {
							mConnectionMonitorChannels.writeAndFlush(message + "\r\n");
						}
						break;
					case BidderConnectInfo.TYPE: // 접속자 정보 전송
						break;
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
		} else if (event instanceof FromAuctionCommon) {
			String message = ((FromAuctionCommon) event).getEncodedMessage();
			String[] splitMessages = message.split(AuctionShareSetting.DELIMITER_REGEX);

			Thread thread = new Thread() {
				@Override
				public void run() {
					switch (splitMessages[0].charAt(1)) {
					case CurrentEntryInfo.TYPE: // 현재 출품 정보 전송
						if (mBidderChannels != null && mBidderChannels.size() > 0) {
							mBidderChannels.writeAndFlush(message + "\r\n");
						}
						break;
					case ResponseConnectionInfo.TYPE: // 접속 인승 결과 전송
						if (mBidderChannels != null && mBidderChannels.size() > 0) {
							mBidderChannels.writeAndFlush(message + "\r\n");
						}

						if (mConnectionMonitorChannels != null && mConnectionMonitorChannels.size() > 0) {
							mConnectionMonitorChannels.writeAndFlush(message + "\r\n");
						}

						if (mWatcherChannels != null && mWatcherChannels.size() > 0) {
							mWatcherChannels.writeAndFlush(message + "\r\n");
						}

						if (mAuctionResultMonitorChannels != null && mAuctionResultMonitorChannels.size() > 0) {
							mAuctionResultMonitorChannels.writeAndFlush(message + "\r\n");
						}
						break;
					case Bidding.TYPE: // 응찰 정보 전송
						if (mControllerChannels != null && mControllerChannels.size() > 0) {
							mControllerChannels.writeAndFlush(message + "\r\n");
						}
						break;
					case AuctionStatus.TYPE: // 현재 경매 상태 전송
						if (mBidderChannels != null && mBidderChannels.size() > 0) {
							mBidderChannels.writeAndFlush(message + "\r\n");
						}

						if (mControllerChannels != null && mControllerChannels.size() > 0) {
							mControllerChannels.writeAndFlush(message + "\r\n");
						}

						if (mConnectionMonitorChannels != null && mConnectionMonitorChannels.size() > 0) {
							mConnectionMonitorChannels.writeAndFlush(message + "\r\n");
						}

						if (mWatcherChannels != null && mWatcherChannels.size() > 0) {
							mWatcherChannels.writeAndFlush(message + "\r\n");
						}

						if (mAuctionResultMonitorChannels != null && mAuctionResultMonitorChannels.size() > 0) {
							mAuctionResultMonitorChannels.writeAndFlush(message + "\r\n");
						}
						break;
					}
				}
			};
			thread.setDaemon(true);
			thread.start();
		}
	}

	public static class Builder {
		private final int port;
		private String auctionCode; // 경매 구분 코드(실시간 / SPOT)
		private String auctionRound; // 경매 회차 정보
		private String auctionLaneCode; // 경매 레인 코드
		private int portCount = 1;

		public Builder(int port) {
			this.port = port;
		}

		public Builder setAuctionCode(String auctionCode) {
			this.auctionCode = auctionCode;
			return this;
		}

		public Builder setAuctionRound(String auctionRound) {
			this.auctionRound = auctionRound;
			return this;
		}

		public Builder setAuctionLaneCode(String auctionLaneCode) {
			this.auctionLaneCode = auctionLaneCode;
			return this;
		}

		public Builder setPortCount(int portCount) {
			if (portCount > 0) {
				this.portCount = portCount;
			}
			return this;
		}

		public AuctionServer buildAndRun() {
			return new AuctionServer(this);
		}
	}

	private void logoutMember(RequestLogout requestLogout) {
		ChannelId channelId = null;
		String closeMember = requestLogout.getUserNo();

		for (ChannelId key : mConnectorInfoMap.keySet()) {
			if (mConnectorInfoMap.get(key).getUserNo().equals(closeMember)) {
				channelId = key;
			}
		}

		if (mConnectorInfoMap.containsKey(channelId)) {
			mConnectorInfoMap.remove(channelId);

			if (!mConnectorInfoMap.containsKey(channelId)) {
				mLogger.info("정상적으로 " + closeMember + "회원 정보가 Close 처리되었습니다.");
			}

			mLogger.debug("ConnectorInfoMap size : " + mConnectorInfoMap.size());
		}

		if (mBidderChannels.find(channelId) != null) {
			mBidderChannels.find(channelId).close();
		}
	}

	public void resetBiddingInfoMap() {
		if (mBiddingInfoMap != null) {
			mBiddingInfoMap.clear();
		} else {
			mBiddingInfoMap = new HashMap<Integer, Object>();
		}
	}
}
