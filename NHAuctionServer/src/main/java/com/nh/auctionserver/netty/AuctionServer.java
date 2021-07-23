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
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedAuctionResponseConnectionInfoHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedAuctionResponseSessionHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedAuctionResultHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedBiddingHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedCancelBiddingHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedEditSettingHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedEntryInfoHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedPassAuctionHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedReadyEntryInfoHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedRequestLogoutHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedStartAuctionHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedStopAuctionHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerDecodedToastMessageRequestHandler;
import com.nh.auctionserver.netty.handlers.AuctionServerInboundDecoder;
import com.nh.auctionserver.netty.handlers.AuctionUserDuplexHandler;
import com.nh.auctionserver.setting.AuctionServerSetting;
import com.nh.auctionserver.socketio.SocketIOHandler;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.CommonMessageParser;
import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.common.models.AuctionResult;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.CancelBidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.controller.ControllerMessageParser;
import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.controller.models.EditSetting;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.PassAuction;
import com.nh.share.controller.models.ReadyEntryInfo;
import com.nh.share.controller.models.RequestLogout;
import com.nh.share.controller.models.SendAuctionResult;
import com.nh.share.controller.models.StartAuction;
import com.nh.share.controller.models.StopAuction;
import com.nh.share.controller.models.ToastMessageRequest;
import com.nh.share.server.ServerMessageParser;
import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.RequestAuctionResult;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.ToastMessage;
import com.nh.share.setting.AuctionShareSetting;
import com.nh.share.utils.JwtCertTokenUtils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
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

public class AuctionServer {
	// new DefaultChannelGroup(new DefaultEventExecutor());
	private final Logger mLogger = LoggerFactory.getLogger(AuctionServer.class);

	private EventLoopGroup mBossGroup;
	private EventLoopGroup mWorkerGroup;
	private Map<String, ChannelGroup> mControllerChannelsMap = new HashMap<String, ChannelGroup>();
	private Map<String, ChannelGroup> mBidderChannelsMap = new HashMap<String, ChannelGroup>();
	private Map<String, ChannelGroup> mWatcherChannelsMap = new HashMap<String, ChannelGroup>();
	private Map<String, ChannelGroup> mAuctionResultMonitorChannelsMap = new HashMap<String, ChannelGroup>();
	private Map<String, ChannelGroup> mConnectionMonitorChannelsMap = new HashMap<String, ChannelGroup>();

	private static Auctioneer mAuctioneer;

	private Map<ChannelId, ConnectionInfo> mConnectorInfoMap = new HashMap<ChannelId, ConnectionInfo>();
	private Map<String, ChannelHandlerContext> mConnectorChannelInfoMap = new HashMap<String, ChannelHandlerContext>();

	private boolean mWaitServerShutDown = false;

	private Map<String, Map<Integer, Object>> mBiddingInfoMap = new HashMap<String, Map<Integer, Object>>();

	private SocketIOHandler mSocketIOHandler;

	private AuctionServer(Builder builder) {
		this(builder.port, builder.portCount);
	}

	private AuctionServer(int port, int portCount) {
		try {
			mLogger.info("======= Auctoin Server Informations[Start] =======");
			mLogger.info("Server Host : " + InetAddress.getLocalHost());
			mLogger.info("Server Port : " + port);
			mLogger.info("======= Auctoin Server Informations[End] =======");

			createAuctioneer(this);
			createNettyServer(port);
		} catch (Exception e) {
			e.printStackTrace();
			stopServer();
		}
	}

	public void setSocketIOHandler(SocketIOHandler socketIOHandler) {
		this.mSocketIOHandler = socketIOHandler;
		mSocketIOHandler.setAuctioneer(mAuctioneer);
		mSocketIOHandler.setNettyConnectionInfoMap(mConnectorInfoMap);

		mLogger.debug("Register SocketIOHandler Completed" + this.mSocketIOHandler);
	}

	private void createAuctioneer(AuctionServer auctionServer) {
		mAuctioneer = new Auctioneer(this);
	}

	public Auctioneer getAuctioneer() {
		return this.mAuctioneer;
	}

	/**
	 * Netty 서버를 설정하고 생성, 바인드한다.
	 * 
	 * @param port Netty 포트
	 * @throws Exception
	 */
	private void createNettyServer(int port) throws Exception {
		/*
		 * 사설 인증서 구현 클래스 - 사용시 주석 해제 
		 * SelfSignedCertificate ssc = new
		 * SelfSignedCertificate(); SslContext sslContext =
		 * SslContextBuilder.forServer(ssc.certificate(),ssc.privateKey()).build();
		 */
//		SelfSignedCertificate ssc = new SelfSignedCertificate();
//		SslContext sslContext = SslContextBuilder.forServer(ssc.certificate(),ssc.privateKey()).build();
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
//						pipeline.addLast(sslContext.newHandler(ch.alloc(), AuctionShareSetting.SERVER_HOST, AuctionShareSetting.SERVER_PORT));

						pipeline.addLast("idleStateHandler",
								new IdleStateHandler(AuctionServerSetting.AUCTION_SERVER_READ_CHECK_SESSION_TIME,
										AuctionServerSetting.AUCTION_SERVER_WRITE_CHECK_SESSION_TIME, 0));
						pipeline.addLast("AuctionUserDuplexHandler", new AuctionUserDuplexHandler(mConnectorInfoMap));
						pipeline.addLast(new DelimiterBasedFrameDecoder(AuctionShareSetting.NETTY_MAX_FRAME_LENGTH,
								Delimiters.lineDelimiter()));
						pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
						pipeline.addLast(new AuctionServerInboundDecoder());

						pipeline.addLast(new AuctionServerConnectorHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mConnectorChannelInfoMap, mControllerChannelsMap, mBidderChannelsMap,
								mWatcherChannelsMap, mAuctionResultMonitorChannelsMap, mConnectionMonitorChannelsMap));
						pipeline.addLast(new AuctionServerDecodedAuctionResponseConnectionInfoHandler(
								AuctionServer.this, mAuctioneer, mConnectorInfoMap, mConnectorChannelInfoMap,
								mControllerChannelsMap, mBidderChannelsMap, mWatcherChannelsMap,
								mAuctionResultMonitorChannelsMap, mConnectionMonitorChannelsMap));
						pipeline.addLast(new AuctionServerDecodedEntryInfoHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mConnectorChannelInfoMap, mControllerChannelsMap, mBidderChannelsMap,
								mWatcherChannelsMap, mAuctionResultMonitorChannelsMap, mConnectionMonitorChannelsMap));
						pipeline.addLast(new AuctionServerDecodedBiddingHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mConnectorChannelInfoMap, mControllerChannelsMap, mBidderChannelsMap,
								mWatcherChannelsMap, mAuctionResultMonitorChannelsMap, mConnectionMonitorChannelsMap));
						pipeline.addLast(new AuctionServerDecodedEditSettingHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mConnectorChannelInfoMap, mControllerChannelsMap, mBidderChannelsMap,
								mWatcherChannelsMap, mAuctionResultMonitorChannelsMap, mConnectionMonitorChannelsMap));
						pipeline.addLast(new AuctionServerDecodedPassAuctionHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mConnectorChannelInfoMap, mControllerChannelsMap, mBidderChannelsMap,
								mWatcherChannelsMap, mAuctionResultMonitorChannelsMap, mConnectionMonitorChannelsMap));
						pipeline.addLast(new AuctionServerDecodedStopAuctionHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mConnectorChannelInfoMap, mControllerChannelsMap, mBidderChannelsMap,
								mWatcherChannelsMap, mAuctionResultMonitorChannelsMap, mConnectionMonitorChannelsMap));
						pipeline.addLast(new AuctionServerDecodedReadyEntryInfoHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mConnectorChannelInfoMap, mControllerChannelsMap, mBidderChannelsMap,
								mWatcherChannelsMap, mAuctionResultMonitorChannelsMap, mConnectionMonitorChannelsMap));
						pipeline.addLast(new AuctionServerDecodedStartAuctionHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mConnectorChannelInfoMap, mControllerChannelsMap, mBidderChannelsMap,
								mWatcherChannelsMap, mAuctionResultMonitorChannelsMap, mConnectionMonitorChannelsMap));
						pipeline.addLast(new AuctionServerDecodedAuctionResultHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mConnectorChannelInfoMap, mControllerChannelsMap, mBidderChannelsMap,
								mWatcherChannelsMap, mAuctionResultMonitorChannelsMap, mConnectionMonitorChannelsMap));
						pipeline.addLast(new AuctionServerDecodedCancelBiddingHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mConnectorChannelInfoMap, mControllerChannelsMap, mBidderChannelsMap,
								mWatcherChannelsMap, mAuctionResultMonitorChannelsMap, mConnectionMonitorChannelsMap));
						pipeline.addLast(new AuctionServerDecodedToastMessageRequestHandler(AuctionServer.this,
								mAuctioneer, mConnectorInfoMap, mConnectorChannelInfoMap, mControllerChannelsMap,
								mBidderChannelsMap, mWatcherChannelsMap, mAuctionResultMonitorChannelsMap,
								mConnectionMonitorChannelsMap));
						pipeline.addLast(new AuctionServerDecodedAuctionResponseSessionHandler(AuctionServer.this,
								mAuctioneer, mConnectorInfoMap, mConnectorChannelInfoMap, mControllerChannelsMap,
								mBidderChannelsMap, mWatcherChannelsMap, mAuctionResultMonitorChannelsMap,
								mConnectionMonitorChannelsMap));
						pipeline.addLast(new AuctionServerDecodedRequestLogoutHandler(AuctionServer.this, mAuctioneer,
								mConnectorInfoMap, mConnectorChannelInfoMap, mControllerChannelsMap, mBidderChannelsMap,
								mWatcherChannelsMap, mAuctionResultMonitorChannelsMap, mConnectionMonitorChannelsMap));

						pipeline.addFirst(new StringEncoder(CharsetUtil.UTF_8));
					}
				})
				/* Nagle 알고리즘 비활성화 여부 설정 */
				.childOption(ChannelOption.TCP_NODELAY, true)
				/* 정해진 시간마다 keepalive packet 전송 */
				.childOption(ChannelOption.SO_KEEPALIVE, true);
		// .childOption(ChannelOption.SO_LINGER, 0);

		b.bind(port);
	}

	public void stopServer() {
		mLogger.debug("Auction server stop!!");
		mLogger.debug("Auction server disconnect all channel!!");
		if (mBidderChannelsMap.size() > 0) {
			for (String key : mBidderChannelsMap.keySet()) {
				mBidderChannelsMap.get(key).close();
				mWaitServerShutDown = true;
			}
		}

		if (mWatcherChannelsMap.size() > 0) {
			for (String key : mWatcherChannelsMap.keySet()) {
				mWatcherChannelsMap.get(key).close();
				mWaitServerShutDown = true;
			}
		}

		if (mControllerChannelsMap.size() > 0) {
			for (String key : mControllerChannelsMap.keySet()) {
				mControllerChannelsMap.get(key).close();
				mWaitServerShutDown = true;
			}
		}

		if (mAuctionResultMonitorChannelsMap.size() > 0) {
			for (String key : mAuctionResultMonitorChannelsMap.keySet()) {
				mAuctionResultMonitorChannelsMap.get(key).close();
				mWaitServerShutDown = true;
			}
		}

		if (mConnectionMonitorChannelsMap.size() > 0) {
			for (String key : mConnectionMonitorChannelsMap.keySet()) {
				mConnectionMonitorChannelsMap.get(key).close();
				mWaitServerShutDown = true;
			}
		}

		mBossGroup.shutdownGracefully();
		mWorkerGroup.shutdownGracefully();

		mLogger.info("************************************* Bye Auction World! **************************************");

		System.exit(0);
	}

//	public void entryAdded(Object event) {
//		if (event instanceof Bidding) {
//			mLogger.debug("EntryAdded : " + ((Bidding) event).getEncodedMessage());
//			if (mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_COMPLETED)
//					|| mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_SUCCESS)
//					|| mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_FAIL)
//					|| mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {
//				mLogger.debug("경매 출품 단위 완료/낙찰/유찰/종료 상태, skip setBidding");
//			} else {
//				mAuctioneer.setBidding((Bidding) event);
//			}
//		}
//
//		if (event instanceof ConnectionInfo) {
//			mLogger.debug("ConnectionInfo entryAdded : " + ((ConnectionInfo) event).getEncodedMessage());
//			mLogger.debug("ConnectorInfoMap size : " + mConnectorInfoMap.size());
//
//			if (((ConnectionInfo) event).getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
//				if (mConnectionMonitorChannelsMap != null && mConnectionMonitorChannelsMap.size() > 0) {
//					mConnectionMonitorChannelsMap
//							.writeAndFlush(new BidderConnectInfo(((ConnectionInfo) event).getUserNo(),
//									((ConnectionInfo) event).getChannel(), ((ConnectionInfo) event).getOS(), "N", "0")
//											.getEncodedMessage()
//									+ "\r\n");
//				}
//			}
//		}
//	}
//
//	public void entryUpdated(Object event) {
//		mLogger.debug("EntryUpdated : " + ((Bidding) event).getEncodedMessage());
//		if (event instanceof Bidding) {
//			if (mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_COMPLETED)
//					|| mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_SUCCESS)
//					|| mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_FAIL)
//					|| mAuctioneer.getCurrentAuctionStatus().equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {
//				mLogger.debug("경매 출품 단위 완료/낙찰/유찰/종료 상태, skip setBidding");
//			} else {
//				mAuctioneer.setBidding((Bidding) event);
//			}
//		}
//	}

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

			if (serverParsedMessage instanceof CurrentEntryInfo) {
				channelItemWriteAndFlush(((CurrentEntryInfo) serverParsedMessage));
			}

			if (serverParsedMessage instanceof ResponseCode) {
				channelItemWriteAndFlush(((ResponseCode) serverParsedMessage));
			}

			if (serverParsedMessage instanceof AuctionCheckSession) {
				channelItemWriteAndFlush(((AuctionCheckSession) serverParsedMessage));
			}

			if (serverParsedMessage instanceof BidderConnectInfo) {
				channelItemWriteAndFlush(((BidderConnectInfo) serverParsedMessage));
			}

			if (serverParsedMessage instanceof RequestAuctionResult) {
				channelItemWriteAndFlush(((RequestAuctionResult) serverParsedMessage));
			}
			break;

		case FromAuctionController.ORIGIN:
			FromAuctionController controllerParsedMessage = ControllerMessageParser.parse(event);

			if (controllerParsedMessage instanceof EditSetting) {
			}

			if (controllerParsedMessage instanceof ReadyEntryInfo) {
				mLogger.info("경매 준비 요청 거점코드 : " + ((ReadyEntryInfo) controllerParsedMessage).getAuctionHouseCode());
				mLogger.info("경매 준비 요청 출품 번호 : " + ((ReadyEntryInfo) controllerParsedMessage).getEntryNum());
				mAuctioneer.readyEntryInfo(((ReadyEntryInfo) controllerParsedMessage).getAuctionHouseCode(),
						((ReadyEntryInfo) controllerParsedMessage).getEntryNum());
			}

			if (controllerParsedMessage instanceof PassAuction) {
				mLogger.info("경매 유찰 요청 거점코드 : " + ((PassAuction) controllerParsedMessage).getAuctionHouseCode());
				mLogger.info("경매 유찰 요청 : " + ((PassAuction) controllerParsedMessage).getEntryNum());

				if (mAuctioneer.getCurrentAuctionStatus(((PassAuction) controllerParsedMessage).getAuctionHouseCode())
						.equals(GlobalDefineCode.AUCTION_STATUS_READY)
						|| mAuctioneer
								.getCurrentAuctionStatus(((PassAuction) controllerParsedMessage).getAuctionHouseCode())
								.equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
					mAuctioneer.passAuction(((PassAuction) controllerParsedMessage).getAuctionHouseCode());
				} else {
					mControllerChannelsMap.get(((PassAuction) controllerParsedMessage).getAuctionHouseCode())
							.writeAndFlush(
									new ResponseCode(((PassAuction) controllerParsedMessage).getAuctionHouseCode(),
											GlobalDefineCode.RESPONSE_REQUEST_FAIL).getEncodedMessage() + "\r\n");
				}
			}

			if (controllerParsedMessage instanceof StopAuction) {
				mLogger.info("경매 진행 정지 요청 거점코드 : " + ((StopAuction) controllerParsedMessage).getAuctionHouseCode());
				mLogger.info("경매 진행 정지 요청 : " + ((StopAuction) controllerParsedMessage).getEntryNum());

				if (mAuctioneer.getCurrentAuctionStatus(((StopAuction) controllerParsedMessage).getAuctionHouseCode())
						.equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
					mAuctioneer.stopAuction(((StopAuction) controllerParsedMessage).getAuctionHouseCode());
				} else {
					mControllerChannelsMap.get(((StopAuction) controllerParsedMessage).getAuctionHouseCode())
							.writeAndFlush(
									new ResponseCode(((StopAuction) controllerParsedMessage).getAuctionHouseCode(),
											GlobalDefineCode.RESPONSE_REQUEST_FAIL).getEncodedMessage() + "\r\n");
				}
			}

			if (controllerParsedMessage instanceof StartAuction) {
				mLogger.info("경매 진행 시작 요청 거점코드 : " + ((StartAuction) controllerParsedMessage).getAuctionHouseCode());
				mLogger.info("경매 진행 시작 요청 : " + ((StartAuction) controllerParsedMessage).getEntryNum());

				// 경매 준비 상태 설정
				mAuctioneer.readyEntryInfo(((StartAuction) controllerParsedMessage).getAuctionHouseCode(), ((StartAuction) controllerParsedMessage).getEntryNum());
				
				if (mAuctioneer.getCurrentAuctionStatus(((StartAuction) controllerParsedMessage).getAuctionHouseCode())
						.equals(GlobalDefineCode.AUCTION_STATUS_READY)) {
					mAuctioneer.startAuction(((StartAuction) controllerParsedMessage).getAuctionHouseCode());
				} else {
					mControllerChannelsMap.get(((StartAuction) controllerParsedMessage).getAuctionHouseCode())
							.writeAndFlush(
									new ResponseCode(((StartAuction) controllerParsedMessage).getAuctionHouseCode(),
											GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage()
											+ "\r\n");
				}
			}

			if (controllerParsedMessage instanceof ToastMessageRequest) {
				mAuctioneer.broadcastToastMessage((ToastMessageRequest) controllerParsedMessage);
			}

			if (controllerParsedMessage instanceof RequestLogout) {
				logoutMember((RequestLogout) controllerParsedMessage);
			}

			if (controllerParsedMessage instanceof EntryInfo) {
				mAuctioneer.addEntryInfo(((EntryInfo) controllerParsedMessage).getAuctionHouseCode(),
						(EntryInfo) controllerParsedMessage);
			}

			if (controllerParsedMessage instanceof SendAuctionResult) {
				// 결과 Broadcast
				channelItemWriteAndFlush(((SendAuctionResult) controllerParsedMessage).getConvertAuctionResult());
				
				// 다음 출품 건 준비
				//mAuctioneer.runNextEntryInterval(((SendAuctionResult) controllerParsedMessage).getAuctionHouseCode());
			}
			break;
		case FromAuctionCommon.ORIGIN:
			FromAuctionCommon commonParsedMessage = CommonMessageParser.parse(event);

			if (commonParsedMessage instanceof AuctionStatus) {
				mAuctioneer.setAuctionStatus(((AuctionStatus) commonParsedMessage).getAuctionHouseCode(),
						(AuctionStatus) commonParsedMessage);

				channelItemWriteAndFlush((AuctionStatus) commonParsedMessage);
			}

			if (commonParsedMessage instanceof Bidding) { // Bidding은 경매가 시작한 후에만 수신되도록 한다.
				if (mAuctioneer.getCurrentAuctionStatus(((Bidding) commonParsedMessage).getAuctionHouseCode())
						.equals(GlobalDefineCode.AUCTION_STATUS_START)
						|| mAuctioneer.getCurrentAuctionStatus(((Bidding) commonParsedMessage).getAuctionHouseCode())
								.equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {

					((Bidding) commonParsedMessage).setBiddingTime(
							LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

					if (!mBiddingInfoMap.get(((Bidding) commonParsedMessage).getAuctionHouseCode())
							.containsKey(((Bidding) commonParsedMessage).getPriceInt())) {
						mBiddingInfoMap.get(((Bidding) commonParsedMessage).getAuctionHouseCode())
								.put(((Bidding) commonParsedMessage).getPriceInt(), (Bidding) commonParsedMessage);
						// entryAdded((Bidding) commonParsedMessage);
						((Bidding) commonParsedMessage).setIsNewBid("Y");
					} else {
						// entryUpdated((Bidding) commonParsedMessage);
						((Bidding) commonParsedMessage).setIsNewBid("N");
					}

					channelItemWriteAndFlush(((Bidding) commonParsedMessage));

					mLogger.debug("Bidding Data : " + ((Bidding) commonParsedMessage).getEncodedMessage());

					channelItemWriteAndFlush(
							(new BidderConnectInfo(((Bidding) commonParsedMessage).getAuctionHouseCode(),
									((Bidding) commonParsedMessage).getUserNo(),
									GlobalDefineCode.CONNECT_CHANNEL_BIDDER,
									((Bidding) commonParsedMessage).getChannel(), "B",
									((Bidding) commonParsedMessage).getPrice())).getEncodedMessage());
				}
			}

			if (commonParsedMessage instanceof CancelBidding) { // Bidding은 경매가 시작한 후에만 수신되도록 한다.
				if (mAuctioneer.getCurrentAuctionStatus(((CancelBidding) commonParsedMessage).getAuctionHouseCode())
						.equals(GlobalDefineCode.AUCTION_STATUS_START)
						|| mAuctioneer
								.getCurrentAuctionStatus(((CancelBidding) commonParsedMessage).getAuctionHouseCode())
								.equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {

					((CancelBidding) commonParsedMessage).setCancelBiddingTime(
							LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

					channelItemWriteAndFlush(((CancelBidding) commonParsedMessage));

					channelItemWriteAndFlush(
							(new BidderConnectInfo(((CancelBidding) commonParsedMessage).getAuctionHouseCode(),
									((CancelBidding) commonParsedMessage).getUserNo(),
									GlobalDefineCode.CONNECT_CHANNEL_BIDDER,
									((CancelBidding) commonParsedMessage).getChannel(), "C", "0")).getEncodedMessage());
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

			switch (splitMessages[0].charAt(1)) {
			case AuctionCountDown.TYPE: // 경매 시작 카운트 다운 정보 전송
				// Web Socket Broadcast
				if (mSocketIOHandler != null) {
					mSocketIOHandler.sendPacketData(GlobalDefineCode.NAMESPACE_WATCH, message);
				}

				// Netty Broadcast
				if (mBidderChannelsMap != null) {
					for (String key : mBidderChannelsMap.keySet()) {
						if (mBidderChannelsMap.get(key).size() > 0) {
							mBidderChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mControllerChannelsMap != null) {
					for (String key : mControllerChannelsMap.keySet()) {
						if (mControllerChannelsMap.get(key).size() > 0) {
							mControllerChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mWatcherChannelsMap != null) {
					for (String key : mWatcherChannelsMap.keySet()) {
						if (mWatcherChannelsMap.get(key).size() > 0) {
							mWatcherChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}
				break;
			case ToastMessage.TYPE: // 메시지 전송 처리
				// Web Socket Broadcast
				if (mSocketIOHandler != null) {
					mSocketIOHandler.sendPacketData(GlobalDefineCode.NAMESPACE_WATCH, message);
				}

				// Netty Broadcast
				if (mBidderChannelsMap != null) {
					for (String key : mBidderChannelsMap.keySet()) {
						if (mBidderChannelsMap.get(key).size() > 0) {
							mBidderChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mWatcherChannelsMap != null) {
					for (String key : mWatcherChannelsMap.keySet()) {
						if (mWatcherChannelsMap.get(key).size() > 0) {
							mWatcherChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}
				break;
			case FavoriteEntryInfo.TYPE: // 관심출품 여부 정보
				// Web Socket Broadcast
				if (mSocketIOHandler != null) {
					mSocketIOHandler.sendPacketData(GlobalDefineCode.NAMESPACE_WATCH, message);
				}

				// Netty Broadcast
				if (mBidderChannelsMap != null) {
					for (String key : mBidderChannelsMap.keySet()) {
						if (mBidderChannelsMap.get(key).size() > 0) {
							mBidderChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mWatcherChannelsMap != null) {
					for (String key : mWatcherChannelsMap.keySet()) {
						if (mWatcherChannelsMap.get(key).size() > 0) {
							mWatcherChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}
				break;
			case ResponseCode.TYPE: // 예외 상황 전송 처리
				// Web Socket Broadcast
				if (mSocketIOHandler != null) {
					mSocketIOHandler.sendPacketData(GlobalDefineCode.NAMESPACE_WATCH, message);
					mSocketIOHandler.sendPacketData(GlobalDefineCode.NAMESPACE_AUCTION_RESULT, message);
					mSocketIOHandler.sendPacketData(GlobalDefineCode.NAMESPACE_CONNECTOR, message);
				}

				// Netty Broadcast
				if (mBidderChannelsMap != null) {
					for (String key : mBidderChannelsMap.keySet()) {
						if (mBidderChannelsMap.get(key).size() > 0) {
							mBidderChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mWatcherChannelsMap != null) {
					for (String key : mWatcherChannelsMap.keySet()) {
						if (mWatcherChannelsMap.get(key).size() > 0) {
							mWatcherChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mControllerChannelsMap != null) {
					for (String key : mControllerChannelsMap.keySet()) {
						if (mControllerChannelsMap.get(key).size() > 0) {
							mControllerChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mAuctionResultMonitorChannelsMap != null) {
					for (String key : mAuctionResultMonitorChannelsMap.keySet()) {
						if (mAuctionResultMonitorChannelsMap.get(key).size() > 0) {
							mAuctionResultMonitorChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mConnectionMonitorChannelsMap != null) {
					for (String key : mConnectionMonitorChannelsMap.keySet()) {
						if (mConnectionMonitorChannelsMap.get(key).size() > 0) {
							mConnectionMonitorChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}
				break;
			case CurrentEntryInfo.TYPE: // 현재 출품 정보 전송
				// Web Socket Broadcast
				if (mSocketIOHandler != null) {
					mSocketIOHandler.sendPacketData(GlobalDefineCode.NAMESPACE_WATCH, message);
				}

				// Netty Broadcast
				if (mBidderChannelsMap != null) {
					for (String key : mBidderChannelsMap.keySet()) {
						if (mBidderChannelsMap.get(key).size() > 0) {
							mBidderChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mWatcherChannelsMap != null) {
					for (String key : mWatcherChannelsMap.keySet()) {
						if (mWatcherChannelsMap.get(key).size() > 0) {
							mWatcherChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mControllerChannelsMap != null) {
					for (String key : mControllerChannelsMap.keySet()) {
						if (mControllerChannelsMap.get(key).size() > 0) {
							mControllerChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}
				break;
			case RequestAuctionResult.TYPE: // 낙유찰 정보 전송 요청
				if (mControllerChannelsMap != null) {
					for (String key : mControllerChannelsMap.keySet()) {
						if (mControllerChannelsMap.get(key).size() > 0) {
							mControllerChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}
				break;
			case AuctionCheckSession.TYPE: // 접속 유효 확인 처리
				if (mBidderChannelsMap != null) {
					for (String key : mBidderChannelsMap.keySet()) {
						if (mBidderChannelsMap.get(key).size() > 0) {
							mBidderChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mWatcherChannelsMap != null) {
					for (String key : mWatcherChannelsMap.keySet()) {
						if (mWatcherChannelsMap.get(key).size() > 0) {
							mWatcherChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mControllerChannelsMap != null) {
					for (String key : mControllerChannelsMap.keySet()) {
						if (mControllerChannelsMap.get(key).size() > 0) {
							mControllerChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mAuctionResultMonitorChannelsMap != null) {
					for (String key : mAuctionResultMonitorChannelsMap.keySet()) {
						if (mAuctionResultMonitorChannelsMap.get(key).size() > 0) {
							mAuctionResultMonitorChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mConnectionMonitorChannelsMap != null) {
					for (String key : mConnectionMonitorChannelsMap.keySet()) {
						if (mConnectionMonitorChannelsMap.get(key).size() > 0) {
							mConnectionMonitorChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}
				break;
			case BidderConnectInfo.TYPE: // 접속자 정보 전송
				// Web Socket Broadcast
				if (mSocketIOHandler != null) {
					mSocketIOHandler.sendPacketData(GlobalDefineCode.NAMESPACE_CONNECTOR, message);
				}

				// Netty Broadcast
				if (mConnectionMonitorChannelsMap != null) {
					for (String key : mConnectionMonitorChannelsMap.keySet()) {
						if (mConnectionMonitorChannelsMap.get(key).size() > 0) {
							mConnectionMonitorChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}
				break;
			}
			/*
			 * Thread thread = new Thread() {
			 * 
			 * @Override public void run() {
			 * 
			 * } }; thread.setDaemon(true); thread.start();
			 */
		} else if (event instanceof FromAuctionCommon) {
			String message = ((FromAuctionCommon) event).getEncodedMessage();
			String[] splitMessages = message.split(AuctionShareSetting.DELIMITER_REGEX);

			switch (splitMessages[0].charAt(1)) {
			case ResponseConnectionInfo.TYPE: // 접속 인승 결과 전송
				if (mBidderChannelsMap != null) {
					for (String key : mBidderChannelsMap.keySet()) {
						if (mBidderChannelsMap.get(key).size() > 0) {
							mBidderChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mConnectionMonitorChannelsMap != null) {
					for (String key : mConnectionMonitorChannelsMap.keySet()) {
						if (mConnectionMonitorChannelsMap.get(key).size() > 0) {
							mConnectionMonitorChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mWatcherChannelsMap != null) {
					for (String key : mWatcherChannelsMap.keySet()) {
						if (mWatcherChannelsMap.get(key).size() > 0) {
							mWatcherChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mAuctionResultMonitorChannelsMap != null) {
					for (String key : mAuctionResultMonitorChannelsMap.keySet()) {
						if (mAuctionResultMonitorChannelsMap.get(key).size() > 0) {
							mAuctionResultMonitorChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}
				break;
			case Bidding.TYPE: // 응찰 정보 전송
				// Web Socket Broadcast
				if (mSocketIOHandler != null) {
					mSocketIOHandler.sendPacketData(GlobalDefineCode.NAMESPACE_CONNECTOR, message);
				}

				// Netty Broadcast
				if (mControllerChannelsMap != null) {
					for (String key : mControllerChannelsMap.keySet()) {
						if (mControllerChannelsMap.get(key).size() > 0) {
							mControllerChannelsMap.get(key).writeAndFlush(message + "\r\n");
							mLogger.debug("Send Bidding Data : " + message);
						}
					}
				}

				if (mConnectionMonitorChannelsMap != null) {
					for (String key : mConnectionMonitorChannelsMap.keySet()) {
						if (mConnectionMonitorChannelsMap.get(key).size() > 0) {
							mConnectionMonitorChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}
				break;
			case AuctionStatus.TYPE: // 현재 경매 상태 전송
				// Web Socket Broadcast
				if (mSocketIOHandler != null) {
					mSocketIOHandler.sendPacketData(GlobalDefineCode.NAMESPACE_WATCH, message);
					mSocketIOHandler.sendPacketData(GlobalDefineCode.NAMESPACE_AUCTION_RESULT, message);
					mSocketIOHandler.sendPacketData(GlobalDefineCode.NAMESPACE_CONNECTOR, message);
				}

				// Netty Broadcast
				if (mControllerChannelsMap != null) {
					for (String key : mControllerChannelsMap.keySet()) {
						if (mControllerChannelsMap.get(key).size() > 0) {
							mControllerChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mBidderChannelsMap != null) {
					for (String key : mBidderChannelsMap.keySet()) {
						if (mBidderChannelsMap.get(key).size() > 0) {
							mBidderChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mConnectionMonitorChannelsMap != null) {
					for (String key : mConnectionMonitorChannelsMap.keySet()) {
						if (mConnectionMonitorChannelsMap.get(key).size() > 0) {
							mConnectionMonitorChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mWatcherChannelsMap != null) {
					for (String key : mWatcherChannelsMap.keySet()) {
						if (mWatcherChannelsMap.get(key).size() > 0) {
							mWatcherChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mAuctionResultMonitorChannelsMap != null) {
					for (String key : mAuctionResultMonitorChannelsMap.keySet()) {
						if (mAuctionResultMonitorChannelsMap.get(key).size() > 0) {
							mAuctionResultMonitorChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}
				break;

			case CancelBidding.TYPE: // 응찰 취소 정보 전송
				// Web Socket Broadcast
				if (mSocketIOHandler != null) {
					mSocketIOHandler.sendPacketData(GlobalDefineCode.NAMESPACE_CONNECTOR, message);
				}

				// Netty Broadcast
				if (mControllerChannelsMap != null) {
					for (String key : mControllerChannelsMap.keySet()) {
						if (mControllerChannelsMap.get(key).size() > 0) {
							mControllerChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mConnectionMonitorChannelsMap != null) {
					for (String key : mConnectionMonitorChannelsMap.keySet()) {
						if (mConnectionMonitorChannelsMap.get(key).size() > 0) {
							mConnectionMonitorChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}
				break;

			case AuctionResult.TYPE: // 경매 낙유찰 결과 전송
				// Web Socket Broadcast
				if (mSocketIOHandler != null) {
					mSocketIOHandler.sendPacketData(GlobalDefineCode.NAMESPACE_WATCH, message);
					mSocketIOHandler.sendPacketData(GlobalDefineCode.NAMESPACE_AUCTION_RESULT, message);
				}

				// Netty Broadcast
				if (mBidderChannelsMap != null) {
					for (String key : mBidderChannelsMap.keySet()) {
						if (mBidderChannelsMap.get(key).size() > 0) {
							mBidderChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mWatcherChannelsMap != null) {
					for (String key : mWatcherChannelsMap.keySet()) {
						if (mWatcherChannelsMap.get(key).size() > 0) {
							mWatcherChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}

				if (mAuctionResultMonitorChannelsMap != null) {
					for (String key : mAuctionResultMonitorChannelsMap.keySet()) {
						if (mAuctionResultMonitorChannelsMap.get(key).size() > 0) {
							mAuctionResultMonitorChannelsMap.get(key).writeAndFlush(message + "\r\n");
						}
					}
				}
				break;
			}
			/*
			 * Thread thread = new Thread() {
			 * 
			 * @Override public void run() {
			 * 
			 * } }; thread.setDaemon(true); thread.start();
			 */
		}
	}

	public static class Builder {
		private final int port;
		private int portCount = 1;

		public Builder(int port) {
			this.port = port;
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

	public void logoutMember(RequestLogout requestLogout) {
		ChannelId channelId = null;
		String closeMember = requestLogout.getUserNo();

		for (ChannelId key : mConnectorInfoMap.keySet()) {
			try {
				if (JwtCertTokenUtils.getInstance().getUserMemNum(mConnectorInfoMap.get(key).getAuthToken())
						.equals(closeMember)) {
					channelId = key;
					break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (mConnectorInfoMap.containsKey(channelId)) {
			// 사용자 접속 해제 상테 전송
			itemAdded(new BidderConnectInfo(mConnectorInfoMap.get(channelId).getAuctionHouseCode(), closeMember,
					mConnectorInfoMap.get(channelId).getChannel(), mConnectorInfoMap.get(channelId).getOS(), "L", "0")
							.getEncodedMessage());

			mConnectorInfoMap.remove(channelId);

			if (mConnectorChannelInfoMap.containsKey(closeMember)) {
				mConnectorChannelInfoMap.remove(closeMember);
			}

			if (requestLogout.getConnectChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
				if (mBidderChannelsMap.containsKey(requestLogout.getAuctionHouseCode())) {
					if (mBidderChannelsMap.get(requestLogout.getAuctionHouseCode()).find(channelId) != null) {
						mBidderChannelsMap.get(requestLogout.getAuctionHouseCode()).find(channelId).close();
					}
				}
			}

			if (requestLogout.getConnectChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER)) {
				if (mControllerChannelsMap.containsKey(requestLogout.getAuctionHouseCode())) {
					if (mControllerChannelsMap.get(requestLogout.getAuctionHouseCode()).find(channelId) != null) {
						mControllerChannelsMap.get(requestLogout.getAuctionHouseCode()).find(channelId).close();
					}
				}
			}

			if (requestLogout.getConnectChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_WATCHER)) {
				if (mWatcherChannelsMap.containsKey(requestLogout.getAuctionHouseCode())) {
					if (mWatcherChannelsMap.get(requestLogout.getAuctionHouseCode()).find(channelId) != null) {
						mWatcherChannelsMap.get(requestLogout.getAuctionHouseCode()).find(channelId).close();
					}
				}
			}

			if (requestLogout.getConnectChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_RESULT_MONITOR)) {
				if (mAuctionResultMonitorChannelsMap.containsKey(requestLogout.getAuctionHouseCode())) {
					if (mAuctionResultMonitorChannelsMap.get(requestLogout.getAuctionHouseCode())
							.find(channelId) != null) {
						mAuctionResultMonitorChannelsMap.get(requestLogout.getAuctionHouseCode()).find(channelId)
								.close();
					}
				}
			}

			if (requestLogout.getConnectChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_CONNECT_MONITOR)) {
				if (mConnectionMonitorChannelsMap.containsKey(requestLogout.getAuctionHouseCode())) {
					if (mConnectionMonitorChannelsMap.get(requestLogout.getAuctionHouseCode())
							.find(channelId) != null) {
						mConnectionMonitorChannelsMap.get(requestLogout.getAuctionHouseCode()).find(channelId).close();
					}
				}
			}

			if (!mConnectorInfoMap.containsKey(channelId)) {
				mLogger.info("정상적으로 " + closeMember + "회원 정보가 Close 처리되었습니다.");
			}

			mLogger.debug("ConnectorInfoMap size : " + mConnectorInfoMap.size());
		}
	}

	public void resetBiddingInfoMap(String auctionHouseCode) {
		if (mBiddingInfoMap.containsKey(auctionHouseCode) && mBiddingInfoMap.get(auctionHouseCode) != null) {
			mBiddingInfoMap.get(auctionHouseCode).clear();
		} else {
			mBiddingInfoMap.put(auctionHouseCode, new HashMap<Integer, Object>());
		}
	}
}
