package com.nh.auctionserver.socketio;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.nh.auctionserver.core.Auctioneer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionResult;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.CancelBidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.ToastMessage;
import com.nh.share.setting.AuctionShareSetting;

import io.netty.channel.ChannelId;

@Configuration
public class SocketIOHandler {
	private final Logger mLogger = LoggerFactory.getLogger(SocketIOHandler.class);

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
	public SocketIOServer socketIOServer() {
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
					// 중복 접속 불가
					if (!mWatchChannelClientMap.get(connectionInfo.getAuctionHouseCode())
							.containsKey(client.getSessionId())) {
						mWatchChannelClientMap.get(connectionInfo.getAuctionHouseCode()).put(client.getSessionId(),
								client);

						mLogger.debug("Request Connect client namespace : " + client.getNamespace().getName());
						mLogger.debug("Request Connect client room : "
								+ client.getHandshakeData().getSingleUrlParam("auctionHouseCode"));

						String auctionHouseCode = client.getHandshakeData().getSingleUrlParam("auctionHouseCode");

						if (auctionHouseCode != null && !auctionHouseCode.isEmpty()) {
							client.joinRoom(auctionHouseCode);

							mLogger.debug("Client Join Room Completed : " + auctionHouseCode + " / "
									+ mSocketIOServer.getRoomOperations(auctionHouseCode).getClients().size());

							client.sendEvent("ResponseConnectionInfo",
									new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
											GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage());

							// 현재 출품 정보 전송
							if (mAuctioneer.getCurrentAuctionStatus(connectionInfo.getAuctionHouseCode())
									.equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
								client.sendEvent("ResponseCode",
										new ResponseCode(connectionInfo.getAuctionHouseCode(),
												GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO)
														.getEncodedMessage());
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
							client.sendEvent("ResponseConnectionInfo",
									new ResponseConnectionInfo(auctionHouseCode, GlobalDefineCode.CONNECT_ETC_ERROR)
											.getEncodedMessage());
							client.disconnect();
						}
					} else {
						client.sendEvent("ResponseConnectionInfo",
								new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
										GlobalDefineCode.CONNECT_DUPLICATE).getEncodedMessage());
						client.disconnect();
					}
				} else {
					Map<UUID, SocketIOClient> clientMap = new ConcurrentHashMap<>();
					clientMap.put(client.getSessionId(), client);

					mWatchChannelClientMap.put(connectionInfo.getAuctionHouseCode(), clientMap);

					mLogger.debug("Request Connect client namespace : " + client.getNamespace().getName());
					mLogger.debug("Request Connect client room : "
							+ client.getHandshakeData().getSingleUrlParam("auctionHouseCode"));
					String auctionHouseCode = client.getHandshakeData().getSingleUrlParam("auctionHouseCode");

					if (auctionHouseCode != null && !auctionHouseCode.isEmpty()) {
						client.joinRoom(auctionHouseCode);

						mLogger.debug("Client Join Room Completed : " + auctionHouseCode + " / "
								+ mSocketIOServer.getRoomOperations(auctionHouseCode).getClients().size());

						client.sendEvent("ResponseConnectionInfo",
								new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
										GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage());

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
						client.sendEvent("ResponseConnectionInfo",
								new ResponseConnectionInfo(auctionHouseCode, GlobalDefineCode.CONNECT_ETC_ERROR)
										.getEncodedMessage());
						client.disconnect();
					}
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

				mLogger.debug("Request Connect client namespace : " + client.getNamespace().getName());
				mLogger.debug("Request Connect client room : "
						+ client.getHandshakeData().getSingleUrlParam("auctionHouseCode"));
				String auctionHouseCode = client.getHandshakeData().getSingleUrlParam("auctionHouseCode");

				if (auctionHouseCode != null && !auctionHouseCode.isEmpty()) {
					client.joinRoom(auctionHouseCode);

					mLogger.debug("Client Join Room Completed : " + auctionHouseCode + " / "
							+ mSocketIOServer.getRoomOperations(auctionHouseCode).getClients().size());

					client.sendEvent("ResponseConnectionInfo",
							new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
									GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage());

				} else {
					client.sendEvent("ResponseConnectionInfo",
							new ResponseConnectionInfo(auctionHouseCode, GlobalDefineCode.CONNECT_ETC_ERROR)
									.getEncodedMessage());
					client.disconnect();
				}
			} else if (connectionInfo.getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_AUCTION_CONNECT_MONITOR)) {
				// 중복 접속 가능
				if (mConnectorChannelClientMap.containsKey(connectionInfo.getAuctionHouseCode())) {
					mConnectorChannelClientMap.get(connectionInfo.getAuctionHouseCode()).put(client.getSessionId(),
							client);
				} else {
					Map<UUID, SocketIOClient> clientMap = new ConcurrentHashMap<>();
					clientMap.put(client.getSessionId(), client);

					mConnectorChannelClientMap.put(connectionInfo.getAuctionHouseCode(), clientMap);
				}

				mLogger.debug("Request Connect client namespace : " + client.getNamespace().getName());
				mLogger.debug("Request Connect client room : "
						+ client.getHandshakeData().getSingleUrlParam("auctionHouseCode"));
				String auctionHouseCode = client.getHandshakeData().getSingleUrlParam("auctionHouseCode");

				if (auctionHouseCode != null && !auctionHouseCode.isEmpty()) {
					client.joinRoom(auctionHouseCode);

					mLogger.debug("Client Join Room Completed : " + auctionHouseCode + " / "
							+ mSocketIOServer.getRoomOperations(auctionHouseCode).getClients().size());

					client.sendEvent("ResponseConnectionInfo",
							new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
									GlobalDefineCode.CONNECT_SUCCESS).getEncodedMessage());

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
						if (mConnectorInfoMap.get(key).getChannel().equals(GlobalDefineCode.CONNECT_CHANNEL_BIDDER)) {
							client.sendEvent("BidderConnectInfo",
									new BidderConnectInfo(mConnectorInfoMap.get(key).getAuctionHouseCode(),
											mConnectorInfoMap.get(key).getUserNo(),
											mConnectorInfoMap.get(key).getChannel(), mConnectorInfoMap.get(key).getOS(),
											"N", "0").getEncodedMessage() + "\r\n");
						}
					}

				} else {
					client.sendEvent("ResponseConnectionInfo",
							new ResponseConnectionInfo(auctionHouseCode, GlobalDefineCode.CONNECT_ETC_ERROR)
									.getEncodedMessage());
					client.disconnect();
				}
			} else {
				client.sendEvent("ResponseConnectionInfo",
						new ResponseConnectionInfo(connectionInfo.getAuctionHouseCode(),
								GlobalDefineCode.CONNECT_ETC_ERROR).getEncodedMessage());
				client.disconnect();
			}
		}
	}

	private void unRegisterConnectChannelGroup(SocketIOClient client) {
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
		switch (messages[0].charAt(1)) {
		case AuctionCountDown.TYPE:
			return new AuctionCountDown(messages[1], messages[2], messages[3]);
		case ResponseCode.TYPE:
			return new ResponseCode(messages[1], messages[2]);
		case AuctionStatus.TYPE:
			return new AuctionStatus(messages);
		case Bidding.TYPE:
			return new Bidding(messages[1], messages[2], messages[3], messages[4], messages[5], messages[6],
					messages[7]);
		case BidderConnectInfo.TYPE:
			return new BidderConnectInfo(messages[1], messages[2], messages[3], messages[4], messages[5], messages[6]);
		case AuctionResult.TYPE:
			return new AuctionResult(messages);
		case CancelBidding.TYPE:
			return new CancelBidding(messages[1], messages[2], messages[3], messages[4], messages[5]);
		default:
			return null;
		}
	}

	public void sendPacketData(String targetNamespace, String message) {
		mLogger.debug("sendPacketData targetNamespace : " + targetNamespace);
		mLogger.debug("sendPacketData message : " + message);

		Object parseObject = messageParse(message);

		if (parseObject instanceof AuctionCountDown) {
			if (mSocketIOServer.getNamespace(targetNamespace).getBroadcastOperations().getClients().size() > 0) {
				mSocketIOServer.getNamespace(targetNamespace).getBroadcastOperations().sendEvent("AuctionCountDown",
						message);
			}
		} else if (parseObject instanceof ToastMessage) {
			if (mSocketIOServer.getNamespace(targetNamespace).getBroadcastOperations().getClients().size() > 0) {
				mSocketIOServer.getNamespace(targetNamespace).getBroadcastOperations().sendEvent("ToastMessage",
						message);
			}
		} else if (parseObject instanceof FavoriteEntryInfo) {
			if (mSocketIOServer.getNamespace(targetNamespace).getBroadcastOperations().getClients().size() > 0) {
				mSocketIOServer.getNamespace(targetNamespace).getBroadcastOperations().sendEvent("FavoriteEntryInfo",
						message);
			}
		} else if (parseObject instanceof ResponseCode) {
			if (mSocketIOServer.getNamespace(targetNamespace).getBroadcastOperations().getClients().size() > 0) {
				mSocketIOServer.getNamespace(targetNamespace).getBroadcastOperations().sendEvent("ResponseCode",
						message);
			}
		} else if (parseObject instanceof CurrentEntryInfo) {
			mSocketIOServer.getNamespace(targetNamespace).getBroadcastOperations().sendEvent("CurrentEntryInfo",
					message);
		} else if (parseObject instanceof BidderConnectInfo) {
			mSocketIOServer.getNamespace(targetNamespace).getBroadcastOperations().sendEvent("BidderConnectInfo",
					message);
		} else if (parseObject instanceof AuctionStatus) {
			mSocketIOServer.getNamespace(targetNamespace).getBroadcastOperations().sendEvent("AuctionStatus", message);
		} else if (parseObject instanceof AuctionResult) {
			mSocketIOServer.getNamespace(targetNamespace).getBroadcastOperations().sendEvent("AuctionResult", message);
		} else if (parseObject instanceof CancelBidding) {
			mSocketIOServer.getNamespace(targetNamespace).getBroadcastOperations().sendEvent("CancelBidding", message);
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
			mLogger.debug("DisconnectListener client : " + client.getSessionId());
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
			default:
				parseObject = null;
				break;
			}

			if (parseObject instanceof ConnectionInfo) {
				registerConnectChannelGroup(client, ((ConnectionInfo) parseObject));
			}
		}
	};
}
