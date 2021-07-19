package com.nh.controller.netty;

import com.nh.common.AuctionShareNettyClient;
import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.NettyControllable;
import com.nh.controller.model.SpEntryInfo;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionReponseSession;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.controller.models.PassAuction;
import com.nh.share.controller.models.ReadyEntryInfo;
import com.nh.share.controller.models.SendAuctionResult;
import com.nh.share.controller.models.StartAuction;
import com.nh.share.controller.models.StopAuction;
import com.nh.share.controller.models.ToastMessageRequest;
import com.nh.share.interfaces.NettySendable;

public class AuctionDelegate {

	private static AuctionDelegate instance = null;

	private AuctionShareNettyClient mClient; // 네티 접속 객체

	private String mUserNumber = null;
	private String mWatchMode = "N";

	public static synchronized AuctionDelegate getInstance() {

		if (instance == null) {
			instance = new AuctionDelegate();
		}

		return instance;
	}

	/**
	 * @Description 서버 접속
	 * @param host_
	 * @param port_
	 * @param controllable
	 */
	public void createClients(String host_, int port_, String userNumber, String watchMode,
			NettyControllable controllable) {
		this.mUserNumber = userNumber;
		this.mWatchMode = watchMode;
		this.mClient = new AuctionShareNettyClient.Builder(host_, port_).setController(controllable).buildAndRun();
	}

	/**
	 * @Description 접속자 정보 전송
	 *
	 * @param activeChannelPort
	 */
	public String onSendConnectionInfo() {

		NettySendable nettySendable = new ConnectionInfo(GlobalDefineCode.AUCTION_HOUSE_HWADONG, mUserNumber,
				"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWN0aW9uSG91c2VDb2RlIjoiMTEwMCIsImV4cCI6MTYyODQzNDgwMCwiZGV2aWNlVVVJRCI6IjAwMDAwMDAwLTU0YjMtZTdjNy0wMDAwLTAwMDA0NmJmZmQ5NyIsInVzZXJNZW1OdW0iOiJNRU0yMzQ1NjcifQ.LR5wDEURBxdOMdbcaha-_HNFFbPGbt4z5pIXFxpQ43dxeE4H-243hULSHs2tSIhyh4BvZzc8z_KlNmo5YEFZUA",
				GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER, GlobalDefineCode.USE_CHANNEL_MANAGE, mWatchMode);

		return sendMessage(nettySendable);

	}

	/**
	 * @Description 세션 체크
	 * @return
	 */
	public String onSendCheckSession() {

		NettySendable nettySendable = new AuctionReponseSession(mUserNumber,
				GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER, GlobalDefineCode.USE_CHANNEL_MANAGE);

		return sendMessage(nettySendable);

	}

	/**
	 * @Description 출품 데이터 전송
	 * @param entryData
	 * @return
	 */
	public String onSendEntryData(SpEntryInfo entryData) {
		return sendMessage(entryData);
	}

	/**
	 * 준비 전송
	 * 
	 * @param entrySeq
	 * @return
	 */
	public String onNextEntryReady(String entrySeq) {
		return sendMessage(new ReadyEntryInfo(GlobalDefineCode.AUCTION_HOUSE_HWADONG, entrySeq));
	}

	/**
	 * @Description 시작 전송
	 * @param entrySeq
	 * @return
	 */
	public String onStartAuction(String entrySeq) {
		return sendMessage(new StartAuction(GlobalDefineCode.AUCTION_HOUSE_HWADONG, entrySeq));
	}

	/**
	 * @Description 정지 전송
	 * @param entrySeq
	 * @return
	 */
	public String onPauseAuction(String entrySeq) {
		return sendMessage(new StopAuction(GlobalDefineCode.AUCTION_HOUSE_HWADONG, entrySeq));
	}

	/**
	 * @Description 강제유찰 전송
	 * @param entrySeq
	 * @return
	 */
	public String onPassAuction(String entrySeq) {
		return sendMessage(new PassAuction(GlobalDefineCode.AUCTION_HOUSE_HWADONG, entrySeq));
	}

	/**
	 * @Description 낙/유찰 결과 전송
	 * @param AuctionResult
	 * @return
	 */
	public String onSendAuctionResult(SendAuctionResult auctionResult) {
		return sendMessage(auctionResult);
	}

	/**
	 * @Description 메시지 전송
	 * @param msg
	 * @return
	 */
	public String onToastMessageRequest(String msg) {
		return sendMessage(new ToastMessageRequest(GlobalDefineCode.AUCTION_HOUSE_HWADONG, msg));
	}

	/**
	 * 객체를 송신할 때 사용한다.
	 * 
	 * @param message 보낼 객체
	 */
	public String sendMessage(NettySendable object) {

		if (!isEmptyClient()) {
			mClient.sendMessage(object.getEncodedMessage());
		}

		return object.getEncodedMessage();
	}

	// =======================================================================================================

	/**
	 * 접속 상태 확인
	 * 
	 * @return
	 */
	public boolean isActive() {

		if (!isEmptyClient() && !mClient.isEmptyChannel()) {
			return mClient.getChannel().isActive();
		}

		return false;
	}

	/**
	 * 네티 체크
	 * 
	 * @return
	 */
	private boolean isEmptyClient() {
		if (mClient != null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @Description 네티 접속 해제
	 */
	public void onDisconnect(NettyClientShutDownListener listener) {
		mClient.stopClient(listener);
	}

	/**
	 * 변수 초기화
	 */
	public void setClearVariable() {
		this.mUserNumber = null;
		this.mWatchMode = null;
		this.mClient = null;
	}

}
