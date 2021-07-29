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
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionReponseSession;
import com.nh.share.common.models.AuctionResult;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.CancelBidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.RefreshConnector;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.RequestAuctionResult;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.ShowEntryInfo;
import com.nh.share.server.models.ToastMessage;
import com.nh.share.setting.AuctionShareSetting;

import io.netty.channel.ChannelId;
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

	@Value("${ssl.cert-name}")
	private String mCertName;
	@Value("${ssl.key-name}")
	private String mKeyName;

	private SocketIOServer mSocketIOServer;

	private Auctioneer mAuctioneer;

	private Map<ChannelId, ConnectionInfo> mConnectorInfoMap;

	// 접속 그룹 Map
	private static Map<String, Map<UUID, SocketIOClient>> mWatchChannelClientMap = new ConcurrentHashMap<>();
	// 접속 그룹 Map
	private static Map<String, Map<UUID, SocketIOClient>> mConnectorChannelClientMap = new ConcurrentHashMap<>();
	// 접속 그룹 Map
	private static Map<String, Map<UUID, SocketIOClient>> mAuctionResultChannelClientMap = new ConcurrentHashMap<>();

	@Bean
	public SslContext sslContext() {
		ClassPathResource certPath = new ClassPathResource(mCertName);
		ClassPathResource keyPath = new ClassPathResource(mKeyName);

		SslContext sslContext = null;
		try {
			sslContext = SslContextBuilder.forServer(certPath.getInputStream(), keyPath.getInputStream()).build();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sslContext;
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

		mSocketIOServer = new SocketIOServer(config, this);

		mSocketIOServer.addNamespace(GlobalDefineCode.NAMESPACE_WATCH);
		mSocketIOServer.addNamespace(GlobalDefineCode.NAMESPACE_CONNECTOR);
		mSocketIOServer.addNamespace(GlobalDefineCode.NAMESPACE_AUCTION_RESULT);

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

	public void setAuctioneer(Auctioneer auctioneer) {
		this.mAuctioneer = auctioneer;
	}

	public void setNettyConnectionInfoMap(Map<ChannelId, ConnectionInfo> connectorInfoMap) {
		this.mConnectorInfoMap = connectorInfoMap;
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
			if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_WATCHER)) {
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
										GlobalDefineCode.CONNECT_SUCCESS, null, null).getEncodedMessage());

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
								GlobalDefineCode.CONNECT_ETC_ERROR, null, null).getEncodedMessage());
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
					 * GlobalDefineCode.CONNECT_SUCCESS, null, null).getEncodedMessage());
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
					 * null, null).getEncodedMessage()); client.disconnect(); } } else {
					 * client.sendEvent("ResponseConnectionInfo", new
					 * ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
					 * GlobalDefineCode.CONNECT_DUPLICATE, null, null).getEncodedMessage());
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
										GlobalDefineCode.CONNECT_SUCCESS, null, null).getEncodedMessage());

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
								GlobalDefineCode.CONNECT_ETC_ERROR, null, null).getEncodedMessage());
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
									GlobalDefineCode.CONNECT_SUCCESS, null, null).getEncodedMessage());

				} else {
					client.sendEvent("ResponseConnectionInfo",
							new ResponseConnectionInfo(auctionHouseCode, GlobalDefineCode.CONNECT_ETC_ERROR, null, null)
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
									GlobalDefineCode.CONNECT_SUCCESS, null, null).getEncodedMessage());

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
					for (ChannelId key : mConnectorInfoMap.keySet()) {
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
							new ResponseConnectionInfo(auctionHouseCode, GlobalDefineCode.CONNECT_ETC_ERROR, null, null)
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
								GlobalDefineCode.CONNECT_ETC_ERROR, null, null).getEncodedMessage());
				client.disconnect();
			}
		}
	}

	private void unRegisterConnectChannelGroup(SocketIOClient client) {
		boolean isFindClient = false;

		log.info("DisconnectListener client : " + client.getSessionId());

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
		String[] messages = replaceEventPrefix(message).split(AuctionShareSetting.DELIMITER_REGEX);
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
						messages[7], messages[8], messages[9]);
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
			default:
				result = null;
				break;
			}
		}

		return result;
	}

	public void sendPacketData(String message) {
		log.info("sendPacketData message : " + message);

		Object parseObject = messageParse(message);

		if (parseObject instanceof AuctionCountDown) {
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
		} else if (parseObject instanceof AuctionStatus) {
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
			}
		}
	};
}
