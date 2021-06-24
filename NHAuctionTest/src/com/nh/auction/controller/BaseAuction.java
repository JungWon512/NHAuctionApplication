package com.nh.auction.controller;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auction.preferences.SharedPreference;
import com.nh.common.AuctionShareNettyClient;
import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.NettyControllable;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionResult;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.CancelBidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.ReadyEntryInfo;
import com.nh.share.controller.models.SendAuctionResult;
import com.nh.share.controller.models.StartAuction;
import com.nh.share.controller.models.StopAuction;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.ToastMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class BaseAuction {

	private static Auction mAuction = new Auction();

	public static Auction getAuctionInstance() {
		return mAuction;
	}

	public static class Auction implements NettyControllable {
		private boolean mIsBidder = true;

		private Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

		public List<AuctionShareNettyClient> clients = new LinkedList<>(); // 서버 접속된 클라이언트

		private AuctionShareNettyClient mChangeLaneClient; // 레인 변경 시 임시 클라이언트

		private String mAuctionName = ""; // 경매 명

		private String mAuctionCode = ""; // 경매 구분 코드

		private String mAuctionRound = ""; // 경매 회차

		private String mAuctionPositionCode = ""; // 거점 코드

		private String mAuctionTime = ""; // 경매 시작 시간

		private boolean isNettyReady = true; // 네티 접속 가능 여부

		private String mHost = ""; // 접속할 호스트

		private Map<String, Integer> mPorts = new LinkedHashMap<>(); // 모든 포트

		private int mSelectedPort = -1; // 선택된 포트

		private int mMainLanePort = -1; // 현재 접속한 메인 포트

		private int mChangeLaneTempMainLanePort = -1; // 레인변경 시 현재 메인 레인 포트 임시 저장

		private String mMainLaneCode = ""; // 현재 접속한 메인 레인 A or B or C or D

		private boolean isBidMode = true; // 응찰 or 관전

		private boolean isChangeLane = false;

		// [차량정보] ==============================================================
		private FavoriteEntryInfo mFavoriteEntryInfo;
		private CurrentEntryInfo mCurrentEntryInfo;
		private AuctionStatus mAuctionStatus;
		private AuctionCountDown mAuctionCountDown;
		private NettyControllable mViewListener; // Controller View로 보낼 Listener
		private SharedPreference mSharedPreference;

		public void setViewListener(NettyControllable listener) {
			this.mViewListener = listener;
		}

		// [포트 SET] ==============================================================
		/**
		 * @MethodName setAuctionPorts
		 * @Description 포트명,포트 set
		 *
		 * @param laneName_
		 * @param port_
		 */
		public void setAuctionPorts(Map<String, Integer> ports_) {

			if (mPorts != null && mPorts.size() > 0) {
				mPorts.clear();
			}

			mPorts.putAll(ports_);
		}

		public void setAuctionName(String name_) {
			mAuctionName = name_;
		}

		public String getAuctionName() {
			return mAuctionName;
		}

		public void setAuctionCode(String auctionCode_) {
			mAuctionCode = auctionCode_;
		}

		public String getAuctionCode() {
			return mAuctionCode;
		}

		public void setAuctionRound(String auctionRound_) {
			mAuctionRound = auctionRound_;
		}

		public String getAuctionRound() {
			return mAuctionRound;
		}

		public void setAuctionPositionCode(String auctionPositionCode_) {
			mAuctionPositionCode = auctionPositionCode_;
		}

		public String getAuctionPositionCode() {
			return mAuctionPositionCode;
		}

		public void setAuctionTime(String time_) {
			mAuctionTime = time_;
		}

		public String getAuctionTime() {
			return mAuctionTime;
		}

		// [포트 정보 조회] ==============================================================
		/**
		 * @MethodName getAuctionPortName
		 * @Description 선택된 포트 명
		 *
		 * @param port_
		 * @return
		 */
		public String getAuctionPortName(int port_) {

			String resultPortName = "";

			if (mPorts != null && mPorts.size() > 0) {

				for (String key : mPorts.keySet()) {
					if (port_ == mPorts.get(key)) {
						resultPortName = key;
						break;
					}
				}
			}

			return resultPortName;
		}

		/**
		 * 
		 * @MethodName getAuctionPortNameAll
		 * @Description 모든 레인 코드 반환
		 *
		 * @return
		 */
		public List<String> getAuctionPortNameAll() {

			List<String> result = new ArrayList<>();

			if (mPorts != null && mPorts.size() > 0) {

				for (String key : mPorts.keySet()) {
					result.add(key);
				}
			}

			Collections.sort(result);

			return result;
		}

		/**
		 * @MethodName getAuctionPort
		 * @Description 선택된 포트
		 *
		 * @param port_
		 * @return
		 */
		public int getAuctionPort(int port_) {

			int resultPort = -1;

			if (mPorts != null && mPorts.size() > 0) {

				for (String key : mPorts.keySet()) {
					if (port_ == mPorts.get(key)) {
						resultPort = mPorts.get(key);
						break;
					}
				}
			}

			return resultPort;
		}

		/**
		 * @MethodName getAuctionPorts
		 * @Description 모든 포트 반환
		 *
		 * @return
		 */
		public ArrayList<Integer> getAuctionPorts() {

			ArrayList<Integer> ports = new ArrayList<>();

			for (String key : mPorts.keySet()) {
				ports.add(mPorts.get(key));
				mLogger.debug("[현재 모든 포트]==> " + mPorts.get(key));
			}

			return ports;

		}

		/**
		 * @MethodName getAuctionPorts
		 * @Description 모든 포트 반환
		 *
		 * @return
		 */
		public int getAuctionPort(String code) {

			int port = 0;

			for (String key : mPorts.keySet()) {

				if (code.equals(key)) {
					port = mPorts.get(key);
					break;
				}
			}

			return port;
		}

		// [메인 포트
		// setter,getter]==============================================================

		// 네티 접속 전 선택된 포트
		public int getSelectedPort() {
			return this.mSelectedPort;
		}

		// 네티 접속 후 메인레인포트
		public void setMainLanePort(int port_) {
			this.mMainLanePort = port_;
		}

		// 네티 접속 후 메인레인포트
		public int getMainLanePort() {
			return this.mMainLanePort;
		}

		// 네티 접속 후 메인레인명
		public void setMainLaneCode(String code_) {
			this.mMainLaneCode = code_;
		}

		// 응찰 or 관전모드
		public boolean getIsBidMode() {
			return this.isBidMode;
		}

		public void setIsBidMode(boolean flag_) {
			this.isBidMode = flag_;
		}

		// 선택된 메인레인
		public String getMainLaneCode() {
			return this.mMainLaneCode;
		}

		// 현재 메인 레인인 client 반환
		public AuctionShareNettyClient getMainLane() {

			AuctionShareNettyClient result = null;

			for (AuctionShareNettyClient client : clients) {
				int port = client.getPort();
				if (port == mMainLanePort) {
					result = client;
				}
			}
			return result;
		}

		/**
		 * @MethodName getLane
		 * @Description 해당 포트 클라이언트 반환
		 *
		 * @param port_
		 * @return
		 */
		public AuctionShareNettyClient getLaneClient(int port_) {

			AuctionShareNettyClient result = null;

			for (AuctionShareNettyClient client : clients) {
				int port = client.getPort();
				if (port == port_) {
					result = client;
				}
			}
			return result;
		}

		/**
		 * @MethodName getChangeLaneClient
		 * @Description 레인 변경 시 클라이언트 반환
		 *
		 * @return
		 */
		public AuctionShareNettyClient getChangeLaneClient() {
			return mChangeLaneClient;
		}

		/**
		 * @MethodName getChangeLaneClient
		 * @Description 레인 변경 클라이언트 제거
		 *
		 * @return
		 */
		public void setRemoveChangeLaneClient(NettyClientShutDownListener listener_) {
			if (mChangeLaneClient != null) {
				mChangeLaneClient.stopClient(listener_);
				mChangeLaneClient = null;
			}
		}

		// [수신 받은 차량 관련 정보
		// getter]==============================================================
		public AuctionCountDown getCountDownInfo() {
			return this.mAuctionCountDown;
		}

		public AuctionStatus getAuctionStatus() {
			return this.mAuctionStatus;
		}

		public FavoriteEntryInfo getFavoriteEntryInfo() {
			return this.mFavoriteEntryInfo;
		}

		// 차초기화 - 차량 정보 변수 삭제
		public void setClearVariable() {

			mHost = "";
			mSelectedPort = -1;
			mMainLanePort = -1;
			mChangeLaneTempMainLanePort = -1;
			mMainLaneCode = "";
			mAuctionName = "";
			mAuctionTime = "";
			mAuctionCode = "";
			mAuctionRound = "";
			mAuctionPositionCode = "";
			mAuctionCountDown = null;
			mFavoriteEntryInfo = null;
			mAuctionStatus = null;

			if (clients != null && clients.size() > 0) {
				clients.clear();
			}

			isNettyReady = true; // true 사용 가능 시켜줌
		}

		// 클라이언트 추가
		public void setAddTargetClient(AuctionShareNettyClient client_) {
			if (client_ != null) {
				clients.add(client_);
			}
		}

		// 접속중인 특정 클라이언트 삭제
		public void setRemoveTargetClient(int port_) {

			for (int i = 0; clients.size() > i; i++) {
				if (clients.get(i).getPort() == port_) {
					mLogger.debug("[클라이언트 삭제]==> " + clients.get(i).getPort());
					clients.remove(i);
					break;
				}
			}
		}

		public void setRemoveAllClient() {
			if (clients != null && clients.size() > 0) {
				clients.clear();
			}
		}

		/**
		 * @MethodName onAuctionClose
		 * @Description 클라이언트 하나 제거
		 *
		 * @param nettyClientShutDownListener
		 */
		public void onAuctionClose(NettyClientShutDownListener listener_) {

			isNettyReady = false;

			for (int i = 0; clients.size() > i; i++) {
				clients.get(i).stopClient(listener_);
			}
		}

		/**
		 * @MethodName onAuctionAllClose
		 * @Description 서버 접속중인 모든 클라이언트 종료
		 *
		 * @param nettyClientShutDownListener
		 */
		public void onAuctionAllClose(NettyClientShutDownListener listener_) {

			isNettyReady = false;

			for (int i = 0; clients.size() > i; i++) {
				clients.get(i).stopClient(listener_);
			}
		}

		// 사용여부 set
		public void setisNettyReady(boolean isReady_) {
			this.isNettyReady = isReady_;
		}

		// 사용여부 Close시 tr
		public boolean isNettyReady() {
			return isNettyReady;
		}

		// [네티 Connection]
		// ==============================================================

		/**
		 * @MethodName onAuctionConnect
		 * @Description 메인레인 접속
		 *
		 * @param host_
		 * @param port_
		 * @param laneCode_
		 */
		public void onAuctionConnect(String host_, int port_) {
			this.mHost = host_;
			this.mSelectedPort = port_;
			int port = getAuctionPort(port_);
			String watchMode = "N"; // 관전 모드 여부

			if (isBidMode) {
				watchMode = "N";
			} else {
				watchMode = "Y";
			}

			mSharedPreference = new SharedPreference();

			mLogger.debug("[메인 레인 클라이언트 생성 전]==>" + port_);
			createClients(port_); // 메인 레인
		}

		/**
		 * @MethodName onAuctionSubLaneConnect
		 * @Description 서브레인 접속
		 *
		 */
		public void onAuctionSubLaneConnect() {

			for (int subPort : getAuctionPorts()) {
				if (mSelectedPort != subPort) {
					mLogger.debug("[서브 레인 클라이언트 생성 전]==>" + subPort);
					createClients(subPort); // 서브레인
				}
			}
		}

		/**
		 * @MethodName createClients
		 * @Description 네티 접속
		 *
		 * @param port_
		 * @param isMainLane_
		 */
		private void createClients(int port_) {
			AuctionShareNettyClient client = new AuctionShareNettyClient.Builder(mHost, port_).setController(this)
					.buildAndRun();

			if (client.isActive()) {
				clients.add(client);
			}

		}

		/**
		 * @MethodName createChangeLaneClients
		 * @Description 네티 레인변경 접속
		 *
		 * @param port_
		 */
		private void createChangeLaneClients(int port_) {
			mChangeLaneClient = new AuctionShareNettyClient.Builder(mHost, port_).setController(this).buildAndRun();
		}

		/**
		 * @MethodName onConnectionInfo
		 * @Description 접속자 정보 전송
		 *
		 * @param activeChannelPort
		 */
		public String onConnectionInfo(String userMemNum, Channel channel, int activeChannelPort) {
			String watchMode = "N"; // 관전 모드 여부
			String data = null;

			if (isBidMode) {
				watchMode = "N";
			} else {
				watchMode = "Y";
			}

			if (mIsBidder) {
				data = new ConnectionInfo(GlobalDefineCode.AUCTION_HOUSE_HWADONG, userMemNum,
						GlobalDefineCode.CONNECT_CHANNEL_BIDDER, GlobalDefineCode.USE_CHANNEL_ANDROID, watchMode)
								.getEncodedMessage()
						+ "\r\n";
			} else {
				data = new ConnectionInfo(GlobalDefineCode.AUCTION_HOUSE_HWADONG, userMemNum,
						GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER, GlobalDefineCode.USE_CHANNEL_MANAGE, watchMode)
								.getEncodedMessage()
						+ "\r\n";
			}

			channel.writeAndFlush(data);

			return data;
		}

		public String onBidding(Channel channel, String connectChannel, String userMemNum, String entryNum,
				String price) {
			String data = new Bidding(GlobalDefineCode.AUCTION_HOUSE_HWADONG, connectChannel, userMemNum, entryNum,
					price, "Y").getEncodedMessage() + "\r\n";
			channel.writeAndFlush(data);

			return data;
		}

		public String onCancelBidding(Channel channel, String userNo, String connectChannel, String entryNum,
				String cancelBiddingTime) {
			String data = new CancelBidding(GlobalDefineCode.AUCTION_HOUSE_HWADONG, entryNum, userNo, connectChannel, cancelBiddingTime)
					.getEncodedMessage() + "\r\n";
			channel.writeAndFlush(data);

			return data;
		}

//		public String onSendEntryData(Channel channel, List<EntryInfo> entryDataList) {
//			for(int i = 0; i < entryDataList.size(); i++) {
//				String data = new EntryInfo(entryDataList.get(i).getEntryNum(), entryDataList.get(i).getEntryType(), entryDataList.get(i).getIndNum(), entryDataList.get(i).getExhibitor(), entryDataList.get(i).getBirthday(), entryDataList.get(i).getGender(),
//						entryDataList.get(i).getWeight(), entryDataList.get(i).getKpn(), entryDataList.get(i).getCavingNum(), entryDataList.get(i).getMother(), entryDataList.get(i).getNote(), entryDataList.get(i).getAuctDateTime(),
//						entryDataList.get(i).getEntryStatus(), entryDataList.get(i).getStartPrice(), entryDataList.get(i).getIsLastEntry()).getEncodedMessage() + "\r\n";
//				
//				channel.writeAndFlush(data);
//			}
//			
//			return true;
//		}

		public String onSendEntryData(Channel channel, EntryInfo entryData) {
			String data = entryData.getEncodedMessage() + "\r\n";

			channel.writeAndFlush(data);

			return data;
		}

		public String onNextEntryReady(Channel channel, String entrySeq) {
			String data = new ReadyEntryInfo(GlobalDefineCode.AUCTION_HOUSE_HWADONG, entrySeq).getEncodedMessage()
					+ "\r\n";
			channel.writeAndFlush(data);

			return data;
		}

		public String onStartAuction(Channel channel, String entrySeq) {
			String data = new StartAuction(GlobalDefineCode.AUCTION_HOUSE_HWADONG, entrySeq).getEncodedMessage()
					+ "\r\n";
			channel.writeAndFlush(data);

			return data;
		}

		public String onPauseAuction(Channel channel, String entrySeq) {
			String data = new StopAuction(GlobalDefineCode.AUCTION_HOUSE_HWADONG, entrySeq).getEncodedMessage()
					+ "\r\n";
			channel.writeAndFlush(data);

			return data;
		}

		public String onSendAuctionResult(Channel channel, String entryNum, String resultCode, String successBidder,
				String successBidPrice) {
			String data = new SendAuctionResult(GlobalDefineCode.AUCTION_HOUSE_HWADONG, entryNum, resultCode,
					successBidder, successBidPrice).getEncodedMessage() + "\r\n";
			channel.writeAndFlush(data);

			return data;
		}

		// [Interface CallBack 데이터 처리는 ViewAuctionRealTimeController / AuctionController
		// / CommonController ( 공통 ) 에서 처리]
		// ==============================================================
		@Override
		public void onActiveChannel() {
		}

		@Override
		public void onActiveChannel(Channel channel) {
			mViewListener.onActiveChannel(channel);

		}

		@Override
		public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo) {
			mViewListener.onResponseConnectionInfo(responseConnectionInfo);
			mLogger.debug("[BaseAuction onResponseConnectionInfo]   " + responseConnectionInfo.getEncodedMessage());
		}

		@Override
		public void onCurrentEntryInfo(CurrentEntryInfo currentEntryInfo) {

			mLogger.debug("[BaseAuction currentEntryInfo]   " + currentEntryInfo.getEncodedMessage());

			mCurrentEntryInfo = currentEntryInfo;

			mViewListener.onCurrentEntryInfo(currentEntryInfo);
		}

		@Override
		public void onAuctionStatus(AuctionStatus auctionStatus) {
			mAuctionStatus = auctionStatus;
			mViewListener.onAuctionStatus(auctionStatus);
		}

		@Override
		public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
			mAuctionCountDown = auctionCountDown;
			mViewListener.onAuctionCountDown(auctionCountDown);
		}

		@Override
		public void onToastMessage(ToastMessage toastMessage) {
			mViewListener.onToastMessage(toastMessage);
		}

		@Override
		public void onFavoriteEntryInfo(FavoriteEntryInfo favoriteEntryInfo) {
			mFavoriteEntryInfo = favoriteEntryInfo;
			mViewListener.onFavoriteEntryInfo(favoriteEntryInfo);
		}

		@Override
		public void onResponseCode(ResponseCode exceptionCode) {
			mViewListener.onResponseCode(exceptionCode);
		}

		@Override
		public void onConnectionException(int port) {
			mViewListener.onConnectionException(port);
		}

		@Override
		public void onChannelInactive(int port) {
			mViewListener.onChannelInactive(port);
		}

		@Override
		public void exceptionCaught(int port) {
			mViewListener.exceptionCaught(port);
		}

		@Override
		public void onCheckSession(ChannelHandlerContext ctx, AuctionCheckSession auctionCheckSession) {
			mViewListener.onCheckSession(ctx, auctionCheckSession);
		}

		@Override
		public void onBidding(Bidding bidding) {
			mViewListener.onBidding(bidding);
		}

		@Override
		public void onCancelBidding(CancelBidding cancelBidding) {
			mViewListener.onCancelBidding(cancelBidding);
		}

		@Override
		public void onAuctionResult(AuctionResult auctionResult) {
			mViewListener.onAuctionResult(auctionResult);
		}

	}

}
