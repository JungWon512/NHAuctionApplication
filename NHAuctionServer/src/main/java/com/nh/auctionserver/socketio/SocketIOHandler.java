package com.nh.auctionserver.socketio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionResult;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.AuctionType;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.CancelBidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.RefreshConnector;
import com.nh.share.common.models.RequestBiddingInfo;
import com.nh.share.common.models.RequestEntryInfo;
import com.nh.share.common.models.RequestLogout;
import com.nh.share.common.models.ResponseBiddingInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.common.models.RetryTargetInfo;
import com.nh.share.server.models.AuctionBidStatus;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.ShowEntryInfo;
import com.nh.share.server.models.ToastMessage;
import com.nh.share.setting.AuctionShareSetting;
import com.nh.share.utils.JwtCertTokenUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SocketIOHandler {

	@Value("${socketio.host}")
	private String host;

	@Value("${socketio.port}")
	private Integer port;

	@Value("${socketio.bossCount}")
	private int bossCount;

	@Value("${socketio.workCount}")
	private int workCount;

	@Value("${socketio.allowCustomRequests}")
	private boolean allowCustomRequests;

	@Value("${socketio.upgradeTimeout}")
	private int upgradeTimeout;

	@Value("${socketio.pingTimeout}")
	private int pingTimeout;

	@Value("${socketio.pingInterval}")
	private int pingInterval;

	@Value("${socketio.ssl.dev.cert-name}")
	private String mDevCertName;
	@Value("${socketio.ssl.dev.key-name}")
	private String mDevKeyName;
	@Value("${socketio.ssl.dev.jks-name}")
	private String mDevJksName;
	@Value("${socketio.ssl.dev.password}")
	private String mDevPassword;

	@Value("${socketio.ssl.prd.cert-name}")
	private String mPrdCertName;
	@Value("${socketio.ssl.prd.key-name}")
	private String mPrdKeyName;
	@Value("${socketio.ssl.prd.jks-name}")
	private String mPrdJksName;
	@Value("${socketio.ssl.prd.password}")
	private String mPrdPassword;

	private SocketIOServer mSocketIOServer;

	private AuctionServer mAuctionServer;
	private Auctioneer mAuctioneer;

	private Map<Object, ConnectionInfo> mConnectorInfoMap;
	private Map<String, Object> mConnectorChannelInfoMap;
	private Map<String, ChannelGroup> mControllerChannelsMap;

	private SslContext mSslContext;

	// 응찰 접속 그룹 Map
	private static Map<String, Map<UUID, SocketIOClient>> mBidderChannelClientMap = new ConcurrentHashMap<>();
	// 관전 접속 그룹 Map
	private static Map<String, Map<UUID, SocketIOClient>> mWatchChannelClientMap = new ConcurrentHashMap<>();
	// 접속자 모니터링 접속 그룹 Map
	private static Map<String, Map<UUID, SocketIOClient>> mConnectorChannelClientMap = new ConcurrentHashMap<>();
	// 경매 결과 모니터링 접속 그룹 Map
	private static Map<String, Map<UUID, SocketIOClient>> mAuctionResultChannelClientMap = new ConcurrentHashMap<>();

	@Bean
	public SslContext sslContext() {
		ClassPathResource certPath = null;
		ClassPathResource keyPath = null;

		if (GlobalDefineCode.FLAG_PRD) {
			certPath = new ClassPathResource(mPrdCertName);
			keyPath = new ClassPathResource(mPrdKeyName);
		} else {
			certPath = new ClassPathResource(mDevCertName);
			keyPath = new ClassPathResource(mDevKeyName);
		}

		mSslContext = null;
		try {
			// mSslContext = SslContextBuilder.forServer(certPath.getInputStream(),
			// keyPath.getInputStream()).protocols("TLSv1.3").build();

			mSslContext = SslContextBuilder.forServer(certPath.getInputStream(), keyPath.getInputStream()).build();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mSslContext;
	}

	@Bean
	public SocketIOServer socketIOServer() {
		try {
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SocketConfig socketConfig = new SocketConfig();
		socketConfig.setTcpNoDelay(true);
		socketConfig.setSoLinger(0);
		com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
		config.setSocketConfig(socketConfig);
		config.setHostname(host);
		config.setPort(port);
		config.setBossThreads(bossCount);
		config.setWorkerThreads(workCount);
		config.setAllowCustomRequests(allowCustomRequests);
		config.setUpgradeTimeout(upgradeTimeout);
		config.setPingTimeout(pingTimeout);
		config.setPingInterval(pingInterval);

		if (GlobalDefineCode.FLAG_SSL) {
			if (GlobalDefineCode.FLAG_PRD) {
				// 운영 서버 Keystore Pw
				config.setKeyStorePassword(mPrdPassword);
				ClassPathResource jksPath = new ClassPathResource(mPrdJksName);

				try {
					config.setKeyStore(jksPath.getInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// 개발 서버 Keystore Pw
				config.setKeyStorePassword(mDevPassword);
				ClassPathResource jksPath = new ClassPathResource(mDevJksName);

				try {
					config.setKeyStore(jksPath.getInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		mSocketIOServer = new SocketIOServer(config, this);

		mSocketIOServer.addNamespace(GlobalDefineCode.NAMESPACE_BIDDER);
		mSocketIOServer.addNamespace(GlobalDefineCode.NAMESPACE_WATCH);
		mSocketIOServer.addNamespace(GlobalDefineCode.NAMESPACE_CONNECTOR);
		mSocketIOServer.addNamespace(GlobalDefineCode.NAMESPACE_AUCTION_RESULT);

		mSocketIOServer.getNamespace(GlobalDefineCode.NAMESPACE_BIDDER).addConnectListener(mConnectListener);
		mSocketIOServer.getNamespace(GlobalDefineCode.NAMESPACE_BIDDER).addDisconnectListener(mDisconnectListener);
		mSocketIOServer.getNamespace(GlobalDefineCode.NAMESPACE_BIDDER).addEventListener("packetData", Object.class,
				mDataListener);

		mSocketIOServer.getNamespace(GlobalDefineCode.NAMESPACE_WATCH).addConnectListener(mConnectListener);
		mSocketIOServer.getNamespace(GlobalDefineCode.NAMESPACE_WATCH).addDisconnectListener(mDisconnectListener);
		mSocketIOServer.getNamespace(GlobalDefineCode.NAMESPACE_WATCH).addEventListener("packetData", Object.class,
				mDataListener);

		mSocketIOServer.getNamespace(GlobalDefineCode.NAMESPACE_AUCTION_RESULT).addConnectListener(mConnectListener);
		mSocketIOServer.getNamespace(GlobalDefineCode.NAMESPACE_AUCTION_RESULT)
				.addDisconnectListener(mDisconnectListener);
		mSocketIOServer.getNamespace(GlobalDefineCode.NAMESPACE_AUCTION_RESULT).addEventListener("packetData",
				Object.class, mDataListener);

		mSocketIOServer.getNamespace(GlobalDefineCode.NAMESPACE_CONNECTOR).addConnectListener(mConnectListener);
		mSocketIOServer.getNamespace(GlobalDefineCode.NAMESPACE_CONNECTOR).addDisconnectListener(mDisconnectListener);
		mSocketIOServer.getNamespace(GlobalDefineCode.NAMESPACE_CONNECTOR).addEventListener("packetData", Object.class,
				mDataListener);

		log.info("======= Auctoin Web Socket Server Informations[Start] =======");
		log.info("Web Socket Server Host : " + host);
		log.info("Web Socket Server Port : " + port);
		log.info("======= Auctoin Web Socket Server Informations[End] =======");

		return mSocketIOServer;
	}

	public void setAuctionServer(AuctionServer auctionServer) {
		this.mAuctionServer = auctionServer;
	}

	public void setAuctioneer(Auctioneer auctioneer) {
		this.mAuctioneer = auctioneer;
	}

	public Auctioneer getAuctioneer() {
		return this.mAuctioneer;
	}

	public void setNettyConnectionInfoMap(Map<Object, ConnectionInfo> connectorInfoMap) {
		this.mConnectorInfoMap = connectorInfoMap;
	}

	public void setNettyConnectionChannelInfoMap(Map<String, Object> connectionChannelInfoMap) {
		this.mConnectorChannelInfoMap = connectionChannelInfoMap;
	}

	public void setNettyControllerChannelGroupMap(Map<String, ChannelGroup> controllerChannelsMap) {
		this.mControllerChannelsMap = controllerChannelsMap;
	}

	/**
	 * Annotations for scanning netty-socketio, such as @OnConnect, @OnEvent
	 */
	@Bean
	public SpringAnnotationScanner springAnnotationScanner() {
		return new SpringAnnotationScanner(socketIOServer());
	}

	private void registerConnectChannelGroup(SocketIOClient client, ConnectionInfo connectionInfo) {
		if (connectionInfo != null) {
			if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {

				if (mBidderChannelClientMap.get(connectionInfo.getAuctionHouseCode()) != null) {
					if (mBidderChannelClientMap.get(connectionInfo.getAuctionHouseCode())
							.containsKey(client.getSessionId()) || mConnectorInfoMap.containsValue(connectionInfo)) {
						
						try {
							// 이미 접속한 사용자 있을 경우 먼저 로그인 된 사용자를 접속 해제 처리하고 후에 로그인 한 사용자를 등록 처리
							if (mConnectorChannelInfoMap.containsKey(JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()))) {
								Object clientObject = mConnectorChannelInfoMap.get(JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()));
								if (clientObject instanceof SocketIOClient) {
									SocketIOClient disconnectClient = (SocketIOClient) clientObject;
									
									if (mConnectorInfoMap.containsKey(disconnectClient.getSessionId())) {
										mConnectorInfoMap.remove(disconnectClient.getSessionId());
									}
									
									if (mBidderChannelClientMap.containsKey(connectionInfo.getAuctionHouseCode())) {
										if (mBidderChannelClientMap.get(connectionInfo.getAuctionHouseCode()).containsKey(disconnectClient.getSessionId())) {
											mBidderChannelClientMap.get(connectionInfo.getAuctionHouseCode()).remove(disconnectClient.getSessionId());
										}
									}
									
									mConnectorChannelInfoMap.remove(JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()));
									
									disconnectClient.disconnect();
									
									if (mControllerChannelsMap != null
											&& mControllerChannelsMap.containsKey(connectionInfo.getAuctionHouseCode())
											&& mControllerChannelsMap.get(connectionInfo.getAuctionHouseCode()).size() > 0) {
										mControllerChannelsMap.get(connectionInfo.getAuctionHouseCode())
												.writeAndFlush(connectionInfo.getEncodedMessage() + "\r\n");

										// Connector에 채널 아이디 등록 처리
										if (!mConnectorInfoMap.containsKey(client.getSessionId())
												&& !mConnectorInfoMap.containsValue(connectionInfo)) {
											mConnectorInfoMap.put(client.getSessionId(), connectionInfo);

											// Connector Channel Map 등록
											try {
												mConnectorChannelInfoMap.put(
														JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()),
														client);
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									} else if (clientObject instanceof ChannelHandlerContext) {
										client.sendEvent("ResponseConnectionInfo",
												new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
														GlobalDefineCode.CONNECT_CONTROLLER_ERROR, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage());
										client.disconnect();
									}
								} else {
									client.sendEvent("ResponseConnectionInfo",
											new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
													GlobalDefineCode.CONNECT_DUPLICATE, GlobalDefineCode.EMPTY_DATA,
													GlobalDefineCode.EMPTY_DATA).getEncodedMessage());

									client.disconnect();

									return;
								}
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
						
//						client.sendEvent("ResponseConnectionInfo",
//								new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
//										GlobalDefineCode.CONNECT_DUPLICATE, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage());
//
//						client.disconnect();
//
//						return;
					}
				}

				if (mControllerChannelsMap != null
						&& mControllerChannelsMap.containsKey(connectionInfo.getAuctionHouseCode())
						&& mControllerChannelsMap.get(connectionInfo.getAuctionHouseCode()).size() > 0) {
					mControllerChannelsMap.get(connectionInfo.getAuctionHouseCode())
							.writeAndFlush(connectionInfo.getEncodedMessage() + "\r\n");

					// Connector에 채널 아이디 등록 처리
					if (!mConnectorInfoMap.containsKey(client.getSessionId())
							&& !mConnectorInfoMap.containsValue(connectionInfo)) {
						mConnectorInfoMap.put(client.getSessionId(), connectionInfo);

						// Connector Channel Map 등록
						try {
							mConnectorChannelInfoMap.put(
									JwtCertTokenUtils.getInstance().getUserMemNum(connectionInfo.getAuthToken()),
									client);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					client.sendEvent("ResponseConnectionInfo",
							new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
									GlobalDefineCode.CONNECT_CONTROLLER_ERROR, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage());
					client.disconnect();
				}
			} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_WATCHER)) {
				if (mWatchChannelClientMap.containsKey(connectionInfo.getAuctionHouseCode())) {
					log.info("Web socket session ID : " + client.getSessionId());
					mWatchChannelClientMap.get(connectionInfo.getAuctionHouseCode()).put(client.getSessionId(), client);

					log.info("Request Connect client namespace : " + client.getNamespace().getName());
					log.info("Request Connect client room : "
							+ client.getHandshakeData().getSingleUrlParam("auctionHouseCode"));

					String auctionHouseCode = client.getHandshakeData().getSingleUrlParam("auctionHouseCode");

					if (auctionHouseCode != null && !auctionHouseCode.isEmpty()) {
						client.joinRoom(auctionHouseCode);

						log.info("Client Join Room Completed : " + auctionHouseCode + " / "
								+ mSocketIOServer.getRoomOperations(auctionHouseCode).getClients().size());

						client.sendEvent("ResponseConnectionInfo",
								new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
										GlobalDefineCode.CONNECT_SUCCESS, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage());

						// 현재 출품 정보 전송
						if (mAuctioneer.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
								.equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
							client.sendEvent("ResponseCode", new ResponseCode(connectionInfo.getAuctionHouseCode(),
									GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage());
						} else {
							if (mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
								client.sendEvent("CurrentEntryInfo",
										new CurrentEntryInfo(
												mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode())
														.getCurrentEntryInfo()).getEncodedMessage());

								// 정상 접속자 초기 경매 상태 정보 전달 처리
								client.sendEvent("AuctionStatus",
										mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode())
												.getAuctionStatus().getEncodedMessage());
								
								if (mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode()).getRetryTargetInfo() != null) {
									client.sendEvent("RetryTargetInfo",mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode()).getRetryTargetInfo().getEncodedMessage() + "\r\n");
								}
								
								if (!mAuctioneer.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
										.equals(GlobalDefineCode.AUCTION_STATUS_READY) && mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode()).getAuctionBidStatus() != null) {
									client.sendEvent("AuctionBidStatus", mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode()).getAuctionBidStatus().getEncodedMessage() + "\r\n");
								}
							}
						}
					} else {
						client.sendEvent("ResponseConnectionInfo", new ResponseConnectionInfo(auctionHouseCode,
								GlobalDefineCode.CONNECT_ETC_ERROR, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage());
						client.disconnect();
					}

					// 중복 접속 불가
					/*
					 * if (!mWatchChannelClientMap.get(connectionInfo.getAuctionHouseCode())
					 * .containsKey(client.getSessionId())) {
					 * mWatchChannelClientMap.get(connectionInfo.getAuctionHouseCode()).put(client.
					 * getSessionId(), client);
					 * 
					 * log.info("Request Connect client namespace : " +
					 * client.getNamespace().getName()); log.info("Request Connect client room : " +
					 * client.getHandshakeData().getSingleUrlParam("auctionHouseCode"));
					 * 
					 * String auctionHouseCode =
					 * client.getHandshakeData().getSingleUrlParam("auctionHouseCode");
					 * 
					 * if (auctionHouseCode != null && !auctionHouseCode.isEmpty()) {
					 * client.joinRoom(auctionHouseCode);
					 * 
					 * log.info("Client Join Room Completed : " + auctionHouseCode + " / " +
					 * mSocketIOServer.getRoomOperations(auctionHouseCode).getClients().size());
					 * 
					 * client.sendEvent("ResponseConnectionInfo", new
					 * ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
					 * GlobalDefineCode.CONNECT_SUCCESS, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage());
					 * 
					 * // 현재 출품 정보 전송 if
					 * (mAuctioneer.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
					 * .equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
					 * client.sendEvent("ResponseCode", new
					 * ResponseCode(connectionInfo.getAuctionHouseCode(),
					 * GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO) .getEncodedMessage());
					 * } else { if
					 * (mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
					 * client.sendEvent("CurrentEntryInfo", new CurrentEntryInfo(
					 * mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode())
					 * .getCurrentEntryInfo()).getEncodedMessage());
					 * 
					 * // 정상 접속자 초기 경매 상태 정보 전달 처리 client.sendEvent("AuctionStatus",
					 * mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode())
					 * .getAuctionStatus().getEncodedMessage()); } } } else {
					 * client.sendEvent("ResponseConnectionInfo", new
					 * ResponseConnectionInfo(auctionHouseCode, GlobalDefineCode.CONNECT_ETC_ERROR,
					 * GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage()); client.disconnect(); } } else {
					 * client.sendEvent("ResponseConnectionInfo", new
					 * ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
					 * GlobalDefineCode.CONNECT_DUPLICATE, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage());
					 * client.disconnect(); }
					 */
				} else {
					Map<UUID, SocketIOClient> clientMap = new ConcurrentHashMap<>();
					clientMap.put(client.getSessionId(), client);

					log.info("Web socket session ID : " + client.getSessionId());
					mWatchChannelClientMap.put(connectionInfo.getAuctionHouseCode(), clientMap);

					log.info("Request Connect client namespace : " + client.getNamespace().getName());
					log.info("Request Connect client room : "
							+ client.getHandshakeData().getSingleUrlParam("auctionHouseCode"));
					String auctionHouseCode = client.getHandshakeData().getSingleUrlParam("auctionHouseCode");

					if (auctionHouseCode != null && !auctionHouseCode.isEmpty()) {
						client.joinRoom(auctionHouseCode);

						log.info("Client Join Room Completed : " + auctionHouseCode + " / "
								+ mSocketIOServer.getRoomOperations(auctionHouseCode).getClients().size());

						client.sendEvent("ResponseConnectionInfo",
								new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
										GlobalDefineCode.CONNECT_SUCCESS, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage());

						// 현재 출품 정보 전송
						if (mAuctioneer.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
								.equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
							client.sendEvent("ResponseCode", new ResponseCode(connectionInfo.getAuctionHouseCode(),
									GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage());
						} else {
							if (mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
								client.sendEvent("CurrentEntryInfo",
										new CurrentEntryInfo(
												mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode())
														.getCurrentEntryInfo()).getEncodedMessage());

								// 정상 접속자 초기 경매 상태 정보 전달 처리
								client.sendEvent("AuctionStatus",
										mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode())
												.getAuctionStatus().getEncodedMessage());
							}
						}
					} else {
						client.sendEvent("ResponseConnectionInfo", new ResponseConnectionInfo(auctionHouseCode,
								GlobalDefineCode.CONNECT_ETC_ERROR, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage());
						client.disconnect();
					}
				}

				if (mWatchChannelClientMap.containsKey(connectionInfo.getAuctionHouseCode())) {
					log.info("mWatchChannelClientMap Current Size : "
							+ mWatchChannelClientMap.get(connectionInfo.getAuctionHouseCode()).size());
				}

			} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_RESULT_MONITOR)) {
				// 중복 접속 가능
				if (mAuctionResultChannelClientMap.containsKey(connectionInfo.getAuctionHouseCode())) {
					mAuctionResultChannelClientMap.get(connectionInfo.getAuctionHouseCode()).put(client.getSessionId(),
							client);
				} else {
					Map<UUID, SocketIOClient> clientMap = new ConcurrentHashMap<>();
					clientMap.put(client.getSessionId(), client);

					mAuctionResultChannelClientMap.put(connectionInfo.getAuctionHouseCode(), clientMap);
				}

				log.info("Request Connect client namespace : " + client.getNamespace().getName());
				log.info("Request Connect client room : "
						+ client.getHandshakeData().getSingleUrlParam("auctionHouseCode"));
				String auctionHouseCode = client.getHandshakeData().getSingleUrlParam("auctionHouseCode");

				if (auctionHouseCode != null && !auctionHouseCode.isEmpty()) {
					client.joinRoom(auctionHouseCode);

					log.info("Client Join Room Completed : " + auctionHouseCode + " / "
							+ mSocketIOServer.getRoomOperations(auctionHouseCode).getClients().size());

					client.sendEvent("ResponseConnectionInfo",
							new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
									GlobalDefineCode.CONNECT_SUCCESS, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage());

				} else {
					client.sendEvent("ResponseConnectionInfo",
							new ResponseConnectionInfo(auctionHouseCode, GlobalDefineCode.CONNECT_ETC_ERROR, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA)
									.getEncodedMessage());
					client.disconnect();
				}

				if (mAuctionResultChannelClientMap.containsKey(connectionInfo.getAuctionHouseCode())) {
					log.info("mAuctionResultChannelClientMap Current Size : "
							+ mAuctionResultChannelClientMap.get(connectionInfo.getAuctionHouseCode()).size());
				}

			} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_CONNECT_MONITOR)) {
				log.info("Web socket session ID : " + client.getSessionId());
				// 중복 접속 가능
				if (mConnectorChannelClientMap.containsKey(connectionInfo.getAuctionHouseCode())) {
					mConnectorChannelClientMap.get(connectionInfo.getAuctionHouseCode()).put(client.getSessionId(),
							client);
				} else {
					Map<UUID, SocketIOClient> clientMap = new ConcurrentHashMap<>();
					clientMap.put(client.getSessionId(), client);

					mConnectorChannelClientMap.put(connectionInfo.getAuctionHouseCode(), clientMap);
				}

				log.info("Request Connect client namespace : " + client.getNamespace().getName());
				log.info("Request Connect client room : "
						+ client.getHandshakeData().getSingleUrlParam("auctionHouseCode"));
				String auctionHouseCode = client.getHandshakeData().getSingleUrlParam("auctionHouseCode");

				if (auctionHouseCode != null && !auctionHouseCode.isEmpty()) {
					client.joinRoom(auctionHouseCode);

					log.info("Client Join Room Completed : " + auctionHouseCode + " / "
							+ mSocketIOServer.getRoomOperations(auctionHouseCode).getClients().size());

					client.sendEvent("ResponseConnectionInfo",
							new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
									GlobalDefineCode.CONNECT_SUCCESS, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage());

					// 현재 출품 정보 전송
					if (mAuctioneer.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
							.equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
						client.sendEvent("ResponseCode", new ResponseCode(connectionInfo.getAuctionHouseCode(),
								GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage());
					} else {
						if (mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
							// 정상 접속자 초기 경매 상태 정보 전달 처리
							client.sendEvent("AuctionStatus",
									mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode()).getAuctionStatus()
											.getEncodedMessage());
						}
					}

					// 접속자 정보 최초 전송
					for (Object key : mConnectorInfoMap.keySet()) {
						if (mConnectorInfoMap.get(key).getAuctionHouseCode()
								.equals(connectionInfo.getAuctionHouseCode())
								&& mConnectorInfoMap.get(key).getChannel()
										.equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
							try {
								client.sendEvent("BidderConnectInfo",
										new BidderConnectInfo(mConnectorInfoMap.get(key).getAuctionHouseCode(),
												mConnectorInfoMap.get(key).getAuctionJoinNum(),
												mConnectorInfoMap.get(key).getChannel(),
												mConnectorInfoMap.get(key).getOS(), "N", "0").getEncodedMessage()
												+ "\r\n");
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

				} else {
					client.sendEvent("ResponseConnectionInfo",
							new ResponseConnectionInfo(auctionHouseCode, GlobalDefineCode.CONNECT_ETC_ERROR, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA)
									.getEncodedMessage());
					client.disconnect();
				}

				if (mConnectorChannelClientMap.containsKey(connectionInfo.getAuctionHouseCode())) {
					log.info("mConnectorChannelClientMap Current Size : "
							+ mConnectorChannelClientMap.get(connectionInfo.getAuctionHouseCode()).size());
				}

			} else {
				client.sendEvent("ResponseConnectionInfo",
						new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
								GlobalDefineCode.CONNECT_ETC_ERROR, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage());
				client.disconnect();
			}
		} else {
			client.sendEvent("ResponseConnectionInfo", new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
					GlobalDefineCode.CONNECT_ETC_ERROR, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage());
			client.disconnect();
		}
	}

	public void unRegisterConnectChannelGroup(SocketIOClient client) {
		boolean isFindClient = false;
		String closeMember = null;

		log.info("DisconnectListener client : " + client.getSessionId());

		if (mConnectorInfoMap.containsKey(client.getSessionId())) {
			try {
				closeMember = JwtCertTokenUtils.getInstance()
						.getUserMemNum(mConnectorInfoMap.get(client.getSessionId()).getAuthToken());

				// 사용자 접속 해제 상테 전송
				if (mConnectorInfoMap.get(client.getSessionId()).getChannel()
						.equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
					mAuctionServer.itemAdded(
							new BidderConnectInfo(mConnectorInfoMap.get(client.getSessionId()).getAuctionHouseCode(),
									mConnectorInfoMap.get(client.getSessionId()).getAuctionJoinNum(),
									mConnectorInfoMap.get(client.getSessionId()).getChannel(),
									mConnectorInfoMap.get(client.getSessionId()).getOS(), "L", "0")
											.getEncodedMessage());
				}

				mConnectorInfoMap.remove(client.getSessionId());
				mConnectorChannelInfoMap.remove(closeMember);

				if (!mConnectorInfoMap.containsKey(client.getSessionId())
						&& !mConnectorChannelInfoMap.containsKey(closeMember)) {
					log.info("정상적으로 " + closeMember + "회원 정보가 Close 처리되었습니다.");
				}

				log.info("ConnectorInfoMap size : " + mConnectorInfoMap.size());

				client.disconnect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (String key : mConnectorChannelClientMap.keySet()) {
			if (mConnectorChannelClientMap.get(key).size() > 0) {
				for (UUID uuid : mConnectorChannelClientMap.get(key).keySet()) {
					if (uuid.equals(client.getSessionId())) {
						mConnectorChannelClientMap.get(key).remove(uuid);
						isFindClient = true;

						log.info("mConnectorChannelClientMap remove SessionID : " + uuid);
						log.info("mConnectorChannelClientMap Current Size : "
								+ mConnectorChannelClientMap.get(key).size());

						break;
					}
				}

				if (isFindClient) {
					break;
				}
			}
		}

		for (String key : mAuctionResultChannelClientMap.keySet()) {
			if (mAuctionResultChannelClientMap.get(key).size() > 0) {
				for (UUID uuid : mAuctionResultChannelClientMap.get(key).keySet()) {
					if (uuid.equals(client.getSessionId())) {
						mAuctionResultChannelClientMap.get(key).remove(uuid);
						isFindClient = true;

						log.info("mAuctionResultChannelClientMap remove SessionID : " + uuid);
						log.info("mAuctionResultChannelClientMap Current Size : "
								+ mAuctionResultChannelClientMap.get(key).size());

						break;
					}
				}

				if (isFindClient) {
					break;
				}
			}
		}

		for (String key : mWatchChannelClientMap.keySet()) {
			if (mWatchChannelClientMap.get(key).size() > 0) {
				for (UUID uuid : mWatchChannelClientMap.get(key).keySet()) {
					if (uuid.equals(client.getSessionId())) {
						mWatchChannelClientMap.get(key).remove(uuid);
						isFindClient = true;

						log.info("mWatchChannelClientMap remove SessionID : " + uuid);
						log.info("mWatchChannelClientMap Current Size : " + mWatchChannelClientMap.get(key).size());

						break;
					}
				}

				if (isFindClient) {
					break;
				}
			}
		}

		for (String key : mBidderChannelClientMap.keySet()) {
			if (mBidderChannelClientMap.get(key).size() > 0) {
				for (UUID uuid : mBidderChannelClientMap.get(key).keySet()) {
					if (uuid.equals(client.getSessionId())) {
						mBidderChannelClientMap.get(key).remove(uuid);
						isFindClient = true;

						log.info("mBidderChannelClientMap remove SessionID : " + uuid);
						log.info("mBidderChannelClientMap Current Size : " + mBidderChannelClientMap.get(key).size());

						break;
					}
				}

				if (isFindClient) {
					break;
				}
			}
		}
	}

	public void unRegisterConnectChannelGroup(UUID client) {
		boolean isFindClient = false;
		String closeMember = null;

		log.info("DisconnectListener client : " + client);

		if (mConnectorInfoMap.containsKey(client)) {
			try {
				closeMember = JwtCertTokenUtils.getInstance()
						.getUserMemNum(mConnectorInfoMap.get(client).getAuthToken());

				// 사용자 접속 해제 상테 전송
				if (mConnectorInfoMap.get(client).getChannel()
						.equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
					mAuctionServer.itemAdded(
							new BidderConnectInfo(mConnectorInfoMap.get(client).getAuctionHouseCode(),
									mConnectorInfoMap.get(client).getAuctionJoinNum(),
									mConnectorInfoMap.get(client).getChannel(),
									mConnectorInfoMap.get(client).getOS(), "L", "0")
											.getEncodedMessage());
				}

				mConnectorInfoMap.remove(client);
				mConnectorChannelInfoMap.remove(closeMember);

				if (!mConnectorInfoMap.containsKey(client)
						&& !mConnectorChannelInfoMap.containsKey(closeMember)) {
					log.info("정상적으로 " + closeMember + "회원 정보가 Close 처리되었습니다.");
				}

				log.info("ConnectorInfoMap size : " + mConnectorInfoMap.size());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (String key : mConnectorChannelClientMap.keySet()) {
			if (mConnectorChannelClientMap.get(key).size() > 0) {
				for (UUID uuid : mConnectorChannelClientMap.get(key).keySet()) {
					if (uuid.equals(client)) {
						mConnectorChannelClientMap.get(key).get(uuid).disconnect();
						mConnectorChannelClientMap.get(key).remove(uuid);
						isFindClient = true;

						log.info("mConnectorChannelClientMap remove SessionID : " + uuid);
						log.info("mConnectorChannelClientMap Current Size : "
								+ mConnectorChannelClientMap.get(key).size());

						break;
					}
				}

				if (isFindClient) {
					break;
				}
			}
		}

		for (String key : mAuctionResultChannelClientMap.keySet()) {
			if (mAuctionResultChannelClientMap.get(key).size() > 0) {
				for (UUID uuid : mAuctionResultChannelClientMap.get(key).keySet()) {
					if (uuid.equals(client)) {
						mAuctionResultChannelClientMap.get(key).get(uuid).disconnect();
						mAuctionResultChannelClientMap.get(key).remove(uuid);
						isFindClient = true;

						log.info("mAuctionResultChannelClientMap remove SessionID : " + uuid);
						log.info("mAuctionResultChannelClientMap Current Size : "
								+ mAuctionResultChannelClientMap.get(key).size());

						break;
					}
				}

				if (isFindClient) {
					break;
				}
			}
		}

		for (String key : mWatchChannelClientMap.keySet()) {
			if (mWatchChannelClientMap.get(key).size() > 0) {
				for (UUID uuid : mWatchChannelClientMap.get(key).keySet()) {
					if (uuid.equals(client)) {
						mWatchChannelClientMap.get(key).get(uuid).disconnect();
						mWatchChannelClientMap.get(key).remove(uuid);
						isFindClient = true;

						log.info("mWatchChannelClientMap remove SessionID : " + uuid);
						log.info("mWatchChannelClientMap Current Size : " + mWatchChannelClientMap.get(key).size());

						break;
					}
				}

				if (isFindClient) {
					break;
				}
			}
		}

		for (String key : mBidderChannelClientMap.keySet()) {
			if (mBidderChannelClientMap.get(key).size() > 0) {
				for (UUID uuid : mBidderChannelClientMap.get(key).keySet()) {
					if (uuid.equals(client)) {
						mBidderChannelClientMap.get(key).get(uuid).disconnect();
						mBidderChannelClientMap.get(key).remove(uuid);
						isFindClient = true;

						log.info("mBidderChannelClientMap remove SessionID : " + uuid);
						log.info("mBidderChannelClientMap Current Size : " + mBidderChannelClientMap.get(key).size());

						break;
					}
				}

				if (isFindClient) {
					break;
				}
			}
		}
	}
	
	public String replaceEventPrefix(String eventMessage) {
		String result = null;

		if (eventMessage != null && !eventMessage.isEmpty()) {
			result = eventMessage.replace("{", "");
			result = result.replace("}", "");
			result = result.replace("data=", "");
			result = result.trim();
		}

		return result;
	}

	public Object messageParse(String message) {
		String[] messages = replaceEventPrefix(message).split(AuctionShareSetting.DELIMITER_REGEX, -1);
		Object result = null;

		if (messages[0].charAt(0) == 'S') {
			switch (messages[0].charAt(1)) {
			case AuctionCountDown.TYPE: // 경매 시작 카운트 다운 정보 전송
				result = new AuctionCountDown(messages[1], messages[2], messages[3]);
				break;
			case ToastMessage.TYPE: // 메시지 전송 처리
				result = new ToastMessage(messages[1], messages[2]);
				break;
			case FavoriteEntryInfo.TYPE: // 관심출품 여부 정보
				result = new FavoriteEntryInfo(messages);
				break;
			case ResponseCode.TYPE: // 예외 상황 전송 처리
				result = new ResponseCode(messages[1], messages[2]);
				break;
			case BidderConnectInfo.TYPE: // 접속자 정보 전송
				result = new BidderConnectInfo(messages);
				break;
			case CurrentEntryInfo.TYPE: // 출품 정보 전송
				result = new CurrentEntryInfo(messages);
				break;
			case ShowEntryInfo.TYPE: // 출품 정보 노출 설정 요청
				result = new ShowEntryInfo(messages[1], messages[2], messages[3], messages[4], messages[5], messages[6],
						messages[7], messages[8], messages[9], messages[10], messages[11]);
				break;
			default:
				result = null;
				break;
			}
		} else if (messages[0].charAt(0) == 'A') {
			switch (messages[0].charAt(1)) {
			case AuctionStatus.TYPE: // 경매 상태 정보 전송
				result = new AuctionStatus(messages);
				break;
			case AuctionResult.TYPE: // 낙유찰 결과 전송
				result = new AuctionResult(messages);
				break;
			case ResponseBiddingInfo.TYPE:
				result = new ResponseBiddingInfo(messages[1], messages[2], messages[3], messages[4], messages[5]);
				break;
			case RetryTargetInfo.TYPE: // 재경매 대상 정보 전송
				result = new RetryTargetInfo(messages[1], messages[2], messages[3]);
				break;
			case AuctionBidStatus.TYPE: // 경매 응찰 종료 상태 전송
				result = new AuctionBidStatus(messages[1], messages[2], messages[3]);
				break;
			default:
				result = null;
				break;
			}
		}

		return result;
	}

	public void connectWebBidderClient(SocketIOClient client, ConnectionInfo connectionInfo,
			ResponseConnectionInfo responseConnectionInfo) {
		log.info("connectWebBidderClient ConnectionInfo : " + connectionInfo.getEncodedMessage());
		log.info("connectWebBidderClient ResponseConnectionInfo : " + responseConnectionInfo.getEncodedMessage());

		try {
			if (mBidderChannelClientMap.get(connectionInfo.getAuctionHouseCode()) != null) {
				if (!mBidderChannelClientMap.get(connectionInfo.getAuctionHouseCode())
						.containsKey(client.getSessionId())) {
					mBidderChannelClientMap.get(connectionInfo.getAuctionHouseCode()).put(client.getSessionId(),
							client);

					log.info("Request Connect client namespace : " + client.getNamespace().getName());
					log.info("Request Connect client room : "
							+ client.getHandshakeData().getSingleUrlParam("auctionHouseCode"));

					String auctionHouseCode = client.getHandshakeData().getSingleUrlParam("auctionHouseCode");

					if (auctionHouseCode != null && !auctionHouseCode.isEmpty()) {
						client.joinRoom(auctionHouseCode);

						log.info("Client Join Room Completed : " + auctionHouseCode + " / "
								+ mSocketIOServer.getRoomOperations(auctionHouseCode).getClients().size());

						// 경매 참가 번호 설정
						if (mConnectorInfoMap.containsKey(client.getSessionId())) {
							mConnectorInfoMap.get(client.getSessionId())
									.setAuctionJoinNum(responseConnectionInfo.getAuctionJoinNum());
						}

						client.sendEvent("ResponseConnectionInfo", responseConnectionInfo.getEncodedMessage());

						// 접속자 정보 전송
						sendPacketData(new BidderConnectInfo(
								mConnectorInfoMap.get(client.getSessionId()).getAuctionHouseCode(),
								mConnectorInfoMap.get(client.getSessionId()).getAuctionJoinNum(),
								mConnectorInfoMap.get(client.getSessionId()).getChannel(),
								mConnectorInfoMap.get(client.getSessionId()).getOS(), "N", "0").getEncodedMessage());

						// 현재 출품 정보 노출 설정 정보 전송
						if (mAuctioneer.getAuctionEditSetting(responseConnectionInfo.getAuctionHouseCode()) != null) {
							client.sendEvent("ShowEntryInfo",
									new ShowEntryInfo(mAuctioneer
											.getAuctionEditSetting(responseConnectionInfo.getAuctionHouseCode()))
													.getEncodedMessage());
						}

						// 현재 출품 정보 전송
						if (mAuctioneer.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
								.equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
							client.sendEvent("ResponseCode", new ResponseCode(connectionInfo.getAuctionHouseCode(),
									GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage());
						} else {
							if (mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
								client.sendEvent("CurrentEntryInfo",
										new CurrentEntryInfo(
												mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode())
														.getCurrentEntryInfo()).getEncodedMessage());

								// 정상 접속자 초기 경매 상태 정보 전달 처리
								client.sendEvent("AuctionStatus",
										mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode())
												.getAuctionStatus().getEncodedMessage());
								
								
								if (mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode()).getRetryTargetInfo() != null) {
									client.sendEvent("RetryTargetInfo", mAuctioneer.getAuctionState(responseConnectionInfo.getAuctionHouseCode()).getRetryTargetInfo().getEncodedMessage());
								}
								
								if (!mAuctioneer.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
										.equals(GlobalDefineCode.AUCTION_STATUS_READY) && mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode()).getAuctionBidStatus() != null) {
									client.sendEvent("AuctionBidStatus", mAuctioneer.getAuctionState(responseConnectionInfo.getAuctionHouseCode()).getAuctionBidStatus().getEncodedMessage());
								}
							}
						}
					} else {
						client.sendEvent("ResponseConnectionInfo", new ResponseConnectionInfo(auctionHouseCode,
								GlobalDefineCode.CONNECT_ETC_ERROR, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage());
						client.disconnect();
					}
				} /*
					 * else { client.sendEvent("ResponseConnectionInfo", new
					 * ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
					 * GlobalDefineCode.CONNECT_DUPLICATE, GlobalDefineCode.EMPTY_DATA,
					 * GlobalDefineCode.EMPTY_DATA).getEncodedMessage());
					 * 
					 * client.disconnect(); }
					 */
			} else {
				Map<UUID, SocketIOClient> clientMap = new ConcurrentHashMap<>();
				clientMap.put(client.getSessionId(), client);

				mBidderChannelClientMap.put(connectionInfo.getAuctionHouseCode(), clientMap);

				log.info("Request Connect client namespace : " + client.getNamespace().getName());
				log.info("Request Connect client room : "
						+ client.getHandshakeData().getSingleUrlParam("auctionHouseCode"));

				String auctionHouseCode = client.getHandshakeData().getSingleUrlParam("auctionHouseCode");

				if (auctionHouseCode != null && !auctionHouseCode.isEmpty()) {
					client.joinRoom(auctionHouseCode);

					log.info("Client Join Room Completed : " + auctionHouseCode + " / "
							+ mSocketIOServer.getRoomOperations(auctionHouseCode).getClients().size());

					// 경매 참가 번호 설정
					if (mConnectorInfoMap.containsKey(client.getSessionId())) {
						mConnectorInfoMap.get(client.getSessionId())
								.setAuctionJoinNum(responseConnectionInfo.getAuctionJoinNum());
					}

					client.sendEvent("ResponseConnectionInfo", responseConnectionInfo.getEncodedMessage());

					// 접속자 정보 전송
					sendPacketData(
							new BidderConnectInfo(mConnectorInfoMap.get(client.getSessionId()).getAuctionHouseCode(),
									mConnectorInfoMap.get(client.getSessionId()).getAuctionJoinNum(),
									mConnectorInfoMap.get(client.getSessionId()).getChannel(),
									mConnectorInfoMap.get(client.getSessionId()).getOS(), "N", "0")
											.getEncodedMessage());

					// 현재 출품 정보 노출 설정 정보 전송
					if (mAuctioneer.getAuctionEditSetting(responseConnectionInfo.getAuctionHouseCode()) != null) {
						client.sendEvent("ShowEntryInfo",
								new ShowEntryInfo(
										mAuctioneer.getAuctionEditSetting(responseConnectionInfo.getAuctionHouseCode()))
												.getEncodedMessage());
					}

					// 현재 출품 정보 전송
					if (mAuctioneer.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
							.equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
						client.sendEvent("ResponseCode", new ResponseCode(connectionInfo.getAuctionHouseCode(),
								GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage());
					} else {
						if (mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode()) != null) {
							client.sendEvent("CurrentEntryInfo",
									new CurrentEntryInfo(
											mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode())
													.getCurrentEntryInfo()).getEncodedMessage());

							// 정상 접속자 초기 경매 상태 정보 전달 처리
							client.sendEvent("AuctionStatus",
									mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode()).getAuctionStatus()
											.getEncodedMessage());
							
							if (mAuctioneer.getAuctionState(connectionInfo.getAuctionHouseCode()).getRetryTargetInfo() != null) {
								client.sendEvent("RetryTargetInfo", mAuctioneer.getAuctionState(responseConnectionInfo.getAuctionHouseCode()).getRetryTargetInfo().getEncodedMessage());
							}
						}
					}
				} else {
					client.sendEvent("ResponseConnectionInfo",
							new ResponseConnectionInfo(auctionHouseCode, GlobalDefineCode.CONNECT_ETC_ERROR, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA)
									.getEncodedMessage());
					client.disconnect();
				}
			}

			if (mBidderChannelClientMap.containsKey(connectionInfo.getAuctionHouseCode())) {
				log.info("mBidderChannelClientMap Current Size : "
						+ mBidderChannelClientMap.get(connectionInfo.getAuctionHouseCode()).size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendPacketData(String message) {
		log.info("sendPacketData message : " + message);

		Object parseObject = messageParse(message);

		if (parseObject instanceof AuctionType) {
			if (mBidderChannelClientMap.containsKey(((AuctionCountDown) parseObject).getAuctionHouseCode())) {
				if (mBidderChannelClientMap.get(((AuctionCountDown) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mBidderChannelClientMap.get(((AuctionCountDown) parseObject).getAuctionHouseCode())
							.keySet()) {
						mBidderChannelClientMap.get(((AuctionCountDown) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("AuctionCountDown", message);
					}
				}
			}
		} else if (parseObject instanceof AuctionCountDown) {
			if (mBidderChannelClientMap.containsKey(((AuctionCountDown) parseObject).getAuctionHouseCode())) {
				if (mBidderChannelClientMap.get(((AuctionCountDown) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mBidderChannelClientMap.get(((AuctionCountDown) parseObject).getAuctionHouseCode())
							.keySet()) {
						mBidderChannelClientMap.get(((AuctionCountDown) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("AuctionCountDown", message);
					}
				}
			}

			if (mWatchChannelClientMap.containsKey(((AuctionCountDown) parseObject).getAuctionHouseCode())) {
				if (mWatchChannelClientMap.get(((AuctionCountDown) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mWatchChannelClientMap.get(((AuctionCountDown) parseObject).getAuctionHouseCode())
							.keySet()) {
						mWatchChannelClientMap.get(((AuctionCountDown) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("AuctionCountDown", message);
					}
				}
			}
		} else if (parseObject instanceof ToastMessage) {
			if (mBidderChannelClientMap.containsKey(((ToastMessage) parseObject).getAuctionHouseCode())) {
				if (mBidderChannelClientMap.get(((ToastMessage) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mBidderChannelClientMap.get(((ToastMessage) parseObject).getAuctionHouseCode())
							.keySet()) {
						mBidderChannelClientMap.get(((ToastMessage) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("ToastMessage", message);
					}
				}
			}

			if (mWatchChannelClientMap.containsKey(((ToastMessage) parseObject).getAuctionHouseCode())) {
				if (mWatchChannelClientMap.get(((ToastMessage) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mWatchChannelClientMap.get(((ToastMessage) parseObject).getAuctionHouseCode())
							.keySet()) {
						mWatchChannelClientMap.get(((ToastMessage) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("ToastMessage", message);
					}
				}
			}
		} else if (parseObject instanceof FavoriteEntryInfo) {
			if (mBidderChannelClientMap.containsKey(((FavoriteEntryInfo) parseObject).getAuctionHouseCode())) {
				if (mBidderChannelClientMap.get(((FavoriteEntryInfo) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mBidderChannelClientMap
							.get(((FavoriteEntryInfo) parseObject).getAuctionHouseCode()).keySet()) {
						mBidderChannelClientMap.get(((FavoriteEntryInfo) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("FavoriteEntryInfo", message);
					}
				}
			}

			if (mWatchChannelClientMap.containsKey(((FavoriteEntryInfo) parseObject).getAuctionHouseCode())) {
				if (mWatchChannelClientMap.get(((FavoriteEntryInfo) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mWatchChannelClientMap.get(((FavoriteEntryInfo) parseObject).getAuctionHouseCode())
							.keySet()) {
						mWatchChannelClientMap.get(((FavoriteEntryInfo) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("FavoriteEntryInfo", message);
					}
				}
			}
		} else if (parseObject instanceof ResponseCode) {
			if (mBidderChannelClientMap.containsKey(((ResponseCode) parseObject).getAuctionHouseCode())) {
				if (mBidderChannelClientMap.get(((ResponseCode) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mBidderChannelClientMap.get(((ResponseCode) parseObject).getAuctionHouseCode())
							.keySet()) {
						mBidderChannelClientMap.get(((ResponseCode) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("ResponseCode", message);
					}
				}
			}

			if (mWatchChannelClientMap.containsKey(((ResponseCode) parseObject).getAuctionHouseCode())) {
				if (mWatchChannelClientMap.get(((ResponseCode) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mWatchChannelClientMap.get(((ResponseCode) parseObject).getAuctionHouseCode())
							.keySet()) {
						mWatchChannelClientMap.get(((ResponseCode) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("ResponseCode", message);
					}
				}
			}

			if (mAuctionResultChannelClientMap.containsKey(((ResponseCode) parseObject).getAuctionHouseCode())) {
				if (mAuctionResultChannelClientMap.get(((ResponseCode) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mAuctionResultChannelClientMap
							.get(((ResponseCode) parseObject).getAuctionHouseCode()).keySet()) {
						mAuctionResultChannelClientMap.get(((ResponseCode) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("ResponseCode", message);
					}
				}
			}

			if (mConnectorChannelClientMap.containsKey(((ResponseCode) parseObject).getAuctionHouseCode())) {
				if (mConnectorChannelClientMap.get(((ResponseCode) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mConnectorChannelClientMap.get(((ResponseCode) parseObject).getAuctionHouseCode())
							.keySet()) {
						mConnectorChannelClientMap.get(((ResponseCode) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("ResponseCode", message);
					}
				}
			}
		} else if (parseObject instanceof CurrentEntryInfo) {
			if (mBidderChannelClientMap.containsKey(((CurrentEntryInfo) parseObject).getAuctionHouseCode())) {
				if (mBidderChannelClientMap.get(((CurrentEntryInfo) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mBidderChannelClientMap.get(((CurrentEntryInfo) parseObject).getAuctionHouseCode())
							.keySet()) {
						mBidderChannelClientMap.get(((CurrentEntryInfo) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("CurrentEntryInfo", message);
					}
				}
			}

			if (mWatchChannelClientMap.containsKey(((CurrentEntryInfo) parseObject).getAuctionHouseCode())) {
				if (mWatchChannelClientMap.get(((CurrentEntryInfo) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mWatchChannelClientMap.get(((CurrentEntryInfo) parseObject).getAuctionHouseCode())
							.keySet()) {
						mWatchChannelClientMap.get(((CurrentEntryInfo) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("CurrentEntryInfo", message);
					}
				}
			}
		} else if (parseObject instanceof BidderConnectInfo) {
			if (mConnectorChannelClientMap.containsKey(((BidderConnectInfo) parseObject).getAuctionHouseCode())) {
				if (mConnectorChannelClientMap.get(((BidderConnectInfo) parseObject).getAuctionHouseCode())
						.size() > 0) {
					for (UUID uuid : mConnectorChannelClientMap
							.get(((BidderConnectInfo) parseObject).getAuctionHouseCode()).keySet()) {
						mConnectorChannelClientMap.get(((BidderConnectInfo) parseObject).getAuctionHouseCode())
								.get(uuid).sendEvent("BidderConnectInfo", message);
					}
				}
			}
			
			// Netty Broadcast
			if (mControllerChannelsMap != null) {
				if (mControllerChannelsMap.containsKey(((BidderConnectInfo) parseObject).getAuctionHouseCode())) {
					if (mControllerChannelsMap.get(((BidderConnectInfo) parseObject).getAuctionHouseCode()).size() > 0) {
						mControllerChannelsMap.get(((BidderConnectInfo) parseObject).getAuctionHouseCode()).writeAndFlush(message + "\r\n");
					}
				}
			}
		} else if (parseObject instanceof AuctionStatus) {
			if (mBidderChannelClientMap.containsKey(((AuctionStatus) parseObject).getAuctionHouseCode())) {
				if (mBidderChannelClientMap.get(((AuctionStatus) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mBidderChannelClientMap.get(((AuctionStatus) parseObject).getAuctionHouseCode())
							.keySet()) {
						mBidderChannelClientMap.get(((AuctionStatus) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("AuctionStatus", message);
					}
				}
			}

			if (mWatchChannelClientMap.containsKey(((AuctionStatus) parseObject).getAuctionHouseCode())) {
				if (mWatchChannelClientMap.get(((AuctionStatus) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mWatchChannelClientMap.get(((AuctionStatus) parseObject).getAuctionHouseCode())
							.keySet()) {
						mWatchChannelClientMap.get(((AuctionStatus) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("AuctionStatus", message);
					}
				}
			}

			if (mAuctionResultChannelClientMap.containsKey(((AuctionStatus) parseObject).getAuctionHouseCode())) {
				if (mAuctionResultChannelClientMap.get(((AuctionStatus) parseObject).getAuctionHouseCode())
						.size() > 0) {
					for (UUID uuid : mAuctionResultChannelClientMap
							.get(((AuctionStatus) parseObject).getAuctionHouseCode()).keySet()) {
						mAuctionResultChannelClientMap.get(((AuctionStatus) parseObject).getAuctionHouseCode())
								.get(uuid).sendEvent("AuctionStatus", message);
					}
				}
			}

			if (mConnectorChannelClientMap.containsKey(((AuctionStatus) parseObject).getAuctionHouseCode())) {
				if (mConnectorChannelClientMap.get(((AuctionStatus) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mConnectorChannelClientMap.get(((AuctionStatus) parseObject).getAuctionHouseCode())
							.keySet()) {
						mConnectorChannelClientMap.get(((AuctionStatus) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("AuctionStatus", message);
					}
				}
			}
		} else if (parseObject instanceof AuctionResult) {
			if (mBidderChannelClientMap.containsKey(((AuctionResult) parseObject).getAuctionHouseCode())) {
				if (mBidderChannelClientMap.get(((AuctionResult) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mBidderChannelClientMap.get(((AuctionResult) parseObject).getAuctionHouseCode())
							.keySet()) {
						mBidderChannelClientMap.get(((AuctionResult) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("AuctionResult", message);
					}
				}
			}

			if (mWatchChannelClientMap.containsKey(((AuctionResult) parseObject).getAuctionHouseCode())) {
				if (mWatchChannelClientMap.get(((AuctionResult) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mWatchChannelClientMap.get(((AuctionResult) parseObject).getAuctionHouseCode())
							.keySet()) {
						mWatchChannelClientMap.get(((AuctionResult) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("AuctionResult", message);
					}
				}
			}

			if (mAuctionResultChannelClientMap.containsKey(((AuctionResult) parseObject).getAuctionHouseCode())) {
				if (mAuctionResultChannelClientMap.get(((AuctionResult) parseObject).getAuctionHouseCode())
						.size() > 0) {
					for (UUID uuid : mAuctionResultChannelClientMap
							.get(((AuctionResult) parseObject).getAuctionHouseCode()).keySet()) {
						mAuctionResultChannelClientMap.get(((AuctionResult) parseObject).getAuctionHouseCode())
								.get(uuid).sendEvent("AuctionResult", message);
					}
				}
			}

			if (mConnectorChannelClientMap.containsKey(((AuctionResult) parseObject).getAuctionHouseCode())) {
				if (mConnectorChannelClientMap.get(((AuctionResult) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mConnectorChannelClientMap.get(((AuctionResult) parseObject).getAuctionHouseCode())
							.keySet()) {
						mConnectorChannelClientMap.get(((AuctionResult) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("AuctionResult", message);
					}
				}
			}
		} else if (parseObject instanceof CancelBidding) {
			if (mConnectorChannelClientMap.containsKey(((CancelBidding) parseObject).getAuctionHouseCode())) {
				if (mConnectorChannelClientMap.get(((CancelBidding) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mConnectorChannelClientMap.get(((CancelBidding) parseObject).getAuctionHouseCode())
							.keySet()) {
						mConnectorChannelClientMap.get(((CancelBidding) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("CancelBidding", message);
					}
				}
			}
		} else if (parseObject instanceof ShowEntryInfo) {
			if (mBidderChannelClientMap.containsKey(((ShowEntryInfo) parseObject).getAuctionHouseCode())) {
				if (mBidderChannelClientMap.get(((ShowEntryInfo) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mBidderChannelClientMap.get(((ShowEntryInfo) parseObject).getAuctionHouseCode())
							.keySet()) {
						mBidderChannelClientMap.get(((ShowEntryInfo) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("ShowEntryInfo", message);
					}
				}
			}
		} else if (parseObject instanceof ResponseBiddingInfo) {
			if (mBidderChannelClientMap.containsKey(((ResponseBiddingInfo) parseObject).getAuctionHouseCode())) {
				if (mBidderChannelClientMap.get(((ResponseBiddingInfo) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mBidderChannelClientMap
							.get(((ResponseBiddingInfo) parseObject).getAuctionHouseCode()).keySet()) {
						mBidderChannelClientMap.get(((ResponseBiddingInfo) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("ResponseBiddingInfo", message);
					}
				}
			}
		} else if (parseObject instanceof RetryTargetInfo) {
			if (mBidderChannelClientMap.containsKey(((RetryTargetInfo) parseObject).getAuctionHouseCode())) {
				if (mBidderChannelClientMap.get(((RetryTargetInfo) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mBidderChannelClientMap.get(((RetryTargetInfo) parseObject).getAuctionHouseCode())
							.keySet()) {
						mBidderChannelClientMap.get(((RetryTargetInfo) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("RetryTargetInfo", message);
					}
				}
			}

			if (mWatchChannelClientMap.containsKey(((RetryTargetInfo) parseObject).getAuctionHouseCode())) {
				if (mWatchChannelClientMap.get(((RetryTargetInfo) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mWatchChannelClientMap.get(((RetryTargetInfo) parseObject).getAuctionHouseCode())
							.keySet()) {
						mWatchChannelClientMap.get(((RetryTargetInfo) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("RetryTargetInfo", message);
					}
				}
			}

			if (mConnectorChannelClientMap.containsKey(((RetryTargetInfo) parseObject).getAuctionHouseCode())) {
				if (mConnectorChannelClientMap.get(((RetryTargetInfo) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mConnectorChannelClientMap
							.get(((RetryTargetInfo) parseObject).getAuctionHouseCode()).keySet()) {
						mConnectorChannelClientMap.get(((RetryTargetInfo) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("RetryTargetInfo", message);
					}
				}
			}
		} else if (parseObject instanceof AuctionBidStatus) {
			if (mBidderChannelClientMap.containsKey(((AuctionBidStatus) parseObject).getAuctionHouseCode())) {
				if (mBidderChannelClientMap.get(((AuctionBidStatus) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mBidderChannelClientMap.get(((AuctionBidStatus) parseObject).getAuctionHouseCode())
							.keySet()) {
						mBidderChannelClientMap.get(((AuctionBidStatus) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("AuctionBidStatus", message);
					}
				}
			}
			
			if (mWatchChannelClientMap.containsKey(((AuctionBidStatus) parseObject).getAuctionHouseCode())) {
				if (mWatchChannelClientMap.get(((AuctionBidStatus) parseObject).getAuctionHouseCode()).size() > 0) {
					for (UUID uuid : mWatchChannelClientMap.get(((AuctionBidStatus) parseObject).getAuctionHouseCode())
							.keySet()) {
						mWatchChannelClientMap.get(((AuctionBidStatus) parseObject).getAuctionHouseCode()).get(uuid)
								.sendEvent("AuctionBidStatus", message);
					}
				}
			}
		}
	}

	private ConnectListener mConnectListener = new ConnectListener() {

		@Override
		public void onConnect(SocketIOClient client) {

		}
	};

	private DisconnectListener mDisconnectListener = new DisconnectListener() {

		@Override
		public void onDisconnect(SocketIOClient client) {
			unRegisterConnectChannelGroup(client);
		}
	};

	private DataListener<Object> mDataListener = new DataListener<Object>() {

		@Override
		public void onData(SocketIOClient client, Object data, AckRequest ackSender) throws Exception {
			Object parseObject = null;
			String[] messages = replaceEventPrefix(data.toString()).split(AuctionShareSetting.DELIMITER_REGEX);

			switch (messages[0].charAt(1)) {
			case ConnectionInfo.TYPE:
				parseObject = new ConnectionInfo(messages[1], messages[2], messages[3], messages[4], messages[5]);
				break;
			case RefreshConnector.TYPE:
				parseObject = new RefreshConnector(messages[1]);
				break;
			case Bidding.TYPE:
				parseObject = new Bidding(messages[1], messages[2], messages[3], messages[4], messages[5], messages[6],
						messages[7], messages[8]);
				break;
			case CancelBidding.TYPE:
				parseObject = new CancelBidding(messages[1], messages[2], messages[3], messages[4], messages[5],
						messages[6]);
				break;
			case RequestEntryInfo.TYPE:
				parseObject = new RequestEntryInfo(messages[1], messages[2], messages[3]);
				break;
			case RequestBiddingInfo.TYPE:
				parseObject = new RequestBiddingInfo(messages[1], messages[2], messages[3], messages[4]);
				break;
			case RequestLogout.TYPE:
				parseObject = new RequestLogout(messages[1], messages[2], messages[3], messages[4]);
				break;
			default:
				parseObject = null;
				break;
			}

			if (parseObject instanceof ConnectionInfo) {
				registerConnectChannelGroup(client, ((ConnectionInfo) parseObject));
			} else if (parseObject instanceof RefreshConnector) {
				if (mAuctioneer != null) {
					mAuctioneer.getAuctionServer().itemAdded(((RefreshConnector) parseObject).getEncodedMessage());
				}
			} else if (parseObject instanceof Bidding) {
				if (mConnectorInfoMap.containsKey(client.getSessionId()) && mBidderChannelClientMap
						.get(((Bidding) parseObject).getAuctionHouseCode()).containsKey(client.getSessionId())) {
					if (mAuctioneer.getCurrentAuctionStatus(((Bidding) parseObject).getAuctionHouseCode())
							.equals(GlobalDefineCode.AUCTION_STATUS_START)
							|| mAuctioneer.getCurrentAuctionStatus(((Bidding) parseObject).getAuctionHouseCode())
									.equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {

						log.info("Message ADD : " + ((Bidding) parseObject).getEncodedMessage());

						if (((Bidding) parseObject).getPriceInt() >= Integer.valueOf(mAuctioneer
								.getAuctionState(((Bidding) parseObject).getAuctionHouseCode()).getStartPrice())) {

							client.sendEvent("ResponseCode",
									new ResponseCode(((Bidding) parseObject).getAuctionHouseCode(),
											GlobalDefineCode.RESPONSE_SUCCESS_BIDDING).getEncodedMessage());

							// 응찰 정보 수집
							mAuctionServer.itemAdded(((Bidding) parseObject).getEncodedMessage());

						} else {
							log.info("=============================================");
							log.info("잘못 된 가격 응찰 시도 : " + ((Bidding) parseObject).getEncodedMessage());
							log.info("=============================================");
							client.sendEvent("ResponseCode",
									new ResponseCode(((Bidding) parseObject).getAuctionHouseCode(),
											GlobalDefineCode.RESPONSE_REQUEST_BIDDING_LOW_PRICE).getEncodedMessage());
						}
					} else {
						client.sendEvent("ResponseCode", new ResponseCode(((Bidding) parseObject).getAuctionHouseCode(),
								GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage());
					}
				} else {
					log.info("=============================================");
					log.info("유효하지 않은 채널에서 응찰을 시도 : " + client.getSessionId());
					log.info(client.getSessionId() + "를 Close 처리하였습니다.");
					log.info("=============================================");
					client.disconnect();
				}
			} else if (parseObject instanceof CancelBidding) {
				if (mConnectorInfoMap.containsKey(client.getSessionId()) && mBidderChannelClientMap
						.get(((CancelBidding) parseObject).getAuctionHouseCode()).containsKey(client.getSessionId())) {
					if (mAuctioneer.getCurrentAuctionStatus(((CancelBidding) parseObject).getAuctionHouseCode())
							.equals(GlobalDefineCode.AUCTION_STATUS_START)
							|| mAuctioneer.getCurrentAuctionStatus(((CancelBidding) parseObject).getAuctionHouseCode())
									.equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {

						log.info("Message ADD : " + ((CancelBidding) parseObject).getEncodedMessage());

						client.sendEvent("ResponseCode",
								new ResponseCode(((CancelBidding) parseObject).getAuctionHouseCode(),
										GlobalDefineCode.RESPONSE_SUCCESS_CANCEL_BIDDING).getEncodedMessage());

						mAuctionServer.itemAdded(((CancelBidding) parseObject).getEncodedMessage());

					} else {
						client.sendEvent("ResponseCode",
								new ResponseCode(((CancelBidding) parseObject).getAuctionHouseCode(),
										GlobalDefineCode.RESPONSE_DENIED_CANCEL_BIDDING).getEncodedMessage());
					}
				} else {
					log.info("=============================================");
					log.info("유효하지 않은 채널로 응찰 취소 요청 : " + client.getSessionId());
					log.info(client.getSessionId() + "를 Close 처리하였습니다.");
					log.info("=============================================");
					client.disconnect();
				}
			} else if (parseObject instanceof RequestEntryInfo) {
				if (mConnectorInfoMap.containsKey(client.getSessionId())
						&& mBidderChannelClientMap.get(((RequestEntryInfo) parseObject).getAuctionHouseCode())
								.containsKey(client.getSessionId())) {
					// 요청된 출품 정보 확인 및 결과 전송 처리
					if (mAuctioneer.getCurrentAuctionStatus(((RequestEntryInfo) parseObject).getAuctionHouseCode())
							.equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
						client.sendEvent("ResponseCode",
								new ResponseCode(((RequestEntryInfo) parseObject).getAuctionHouseCode(),
										GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO).getEncodedMessage());
					} else {
						if (mAuctioneer.getEntryInfo(((RequestEntryInfo) parseObject).getAuctionHouseCode(),
								((RequestEntryInfo) parseObject).getEntryNum()) != null) {
							client.sendEvent("CurrentEntryInfo",
									new CurrentEntryInfo(mAuctioneer.getEntryInfo(
											((RequestEntryInfo) parseObject).getAuctionHouseCode(),
											((RequestEntryInfo) parseObject).getEntryNum())).getEncodedMessage());
						} else {
							client.sendEvent("ResponseCode",
									new ResponseCode(((RequestEntryInfo) parseObject).getAuctionHouseCode(),
											GlobalDefineCode.RESPONSE_REQUEST_NOT_RESULT).getEncodedMessage());
						}
					}
				} else {
					log.info("=============================================");
					log.info("유효하지 않은 채널에서 출품 정보 전송을 요청하였습니다. : " + client.getSessionId());
					log.info("=============================================");

					client.sendEvent("ResponseCode",
							new ResponseCode(((RequestEntryInfo) parseObject).getAuctionHouseCode(),
									GlobalDefineCode.RESPONSE_REQUEST_FAIL).getEncodedMessage());
				}
			} else if (parseObject instanceof RequestBiddingInfo) {
				if (mConnectorInfoMap.containsKey(client.getSessionId())
						&& mConnectorChannelInfoMap.containsKey(((RequestBiddingInfo) parseObject).getUserNo())
						&& mBidderChannelClientMap.get(((RequestBiddingInfo) parseObject).getAuctionHouseCode())
								.containsKey(client.getSessionId())) {
					// 응찰 정보 조회 요청
					if (mControllerChannelsMap != null
							&& mControllerChannelsMap
									.containsKey(((RequestBiddingInfo) parseObject).getAuctionHouseCode())
							&& mControllerChannelsMap.get(((RequestBiddingInfo) parseObject).getAuctionHouseCode())
									.size() > 0) {
						mControllerChannelsMap.get(((RequestBiddingInfo) parseObject).getAuctionHouseCode())
								.writeAndFlush(((RequestBiddingInfo) parseObject).getEncodedMessage() + "\r\n");
					} else {
						client.sendEvent("ResponseCode",
								new ResponseConnectionInfo(((RequestBiddingInfo) parseObject).getAuctionHouseCode(),
										GlobalDefineCode.CONNECT_CONTROLLER_ERROR, GlobalDefineCode.EMPTY_DATA, GlobalDefineCode.EMPTY_DATA).getEncodedMessage());
						client.disconnect();
					}
				} else {
					log.info("=============================================");
					log.info("유효하지 않은 채널에서 응찰 정보를 요청하였습니다. : " + client.getSessionId());
					log.info("=============================================");

					client.sendEvent("ResponseCode",
							new ResponseCode(((RequestBiddingInfo) parseObject).getAuctionHouseCode(),
									GlobalDefineCode.RESPONSE_REQUEST_FAIL).getEncodedMessage());
				}
			} else if (parseObject instanceof RequestLogout) {
				if (mConnectorChannelClientMap.get(((RequestLogout) parseObject).getAuctionHouseCode())
								.containsKey(client.getSessionId())) {
					
					mAuctionServer.logoutMember(((RequestLogout) parseObject), true);
				} else {
					log.info("=============================================");
					log.info("유효하지 않은 채널에서 강제 로그아웃을 요청하였습니다. : " + client.getSessionId());
					log.info("=============================================");

					client.sendEvent("ResponseCode",
							new ResponseCode(((RequestBiddingInfo) parseObject).getAuctionHouseCode(),
									GlobalDefineCode.RESPONSE_REQUEST_FAIL).getEncodedMessage());
				}
				
			}
		}
	};
}
