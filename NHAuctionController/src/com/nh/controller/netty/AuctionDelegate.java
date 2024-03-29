package com.nh.controller.netty;

import com.nh.common.AuctionShareNettyClient;
import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.NettyControllable;
import com.nh.controller.model.SpEntryInfo;
import com.nh.controller.utils.GlobalDefine;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionReponseSession;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.common.models.RetryTargetInfo;
import com.nh.share.controller.models.*;
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
     * @param host_
     * @param port_
     * @param controllable
     * @Description 서버 접속
     */
    public void createClients(String host_, int port_, String userNumber, String watchMode,
                              NettyControllable controllable) {
        this.mUserNumber = userNumber;
        this.mWatchMode = watchMode;
        this.mClient = new AuctionShareNettyClient.Builder(host_, port_).setController(controllable).buildAndRun();
    }

    /**
     * @param activeChannelPort
     * @Description 접속자 정보 전송: 제어프로그램 정보
     */
    public String onSendConnectionInfo() {
 
        NettySendable nettySendable = new ConnectionInfo(
        		GlobalDefine.ADMIN_INFO.adminData.getNabzplc(),
                GlobalDefine.ADMIN_INFO.adminData.getUserId(),
                GlobalDefine.ADMIN_INFO.adminData.getAccessToken(),
                GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER,
                GlobalDefineCode.USE_CHANNEL_MANAGE);
        return sendMessage(nettySendable);
    }

    /**
     * @param responseConnectionInfo 접속자 정보
     * @Description 접속자 정보 전송: DB 조회한 사용자 정보
     */
    public String onSendConnectionInfo(ResponseConnectionInfo responseConnectionInfo) {
        return sendMessage(responseConnectionInfo);
    }

    /**
     * @param editSetting 셋팅 정보
     * @Description 셋팅 정보 전송
     */
    public String onSendSettingInfo(EditSetting editSetting) {
        return sendMessage(editSetting);
    }

    /**
     * @return
     * @Description 세션 체크
     */
    public String onSendCheckSession() {

        NettySendable nettySendable = new AuctionReponseSession(mUserNumber,
                GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER, GlobalDefineCode.USE_CHANNEL_MANAGE);

        return sendMessage(nettySendable);

    }

    /**
     * @param entryData
     * @return
     * @Description 출품 데이터 전송
     */
    public String onSendEntryData(SpEntryInfo entryData) {
        return sendMessage(entryData);
    }

    /**
     * @param entryData
     * @return
     * @Description 출품 데이터 전송 TEST
     */
    public String onSendEntryData(EntryInfo entryData) {
        return sendMessage(entryData);
    }

    /**
     * 준비 전송
     *
     * @param entrySeq
     * @return
     */
    public String onNextEntryReady(String entrySeq) {
        return sendMessage(new ReadyEntryInfo(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc(), entrySeq));
    }

    /**
     * @param entrySeq
     * @return
     * @Description 시작 전송
     */
    public String onStartAuction(String entrySeq) {
        return sendMessage(new StartAuction(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc(), entrySeq));
    }

    /**
     * @param entrySeq
     * @return
     * @Description 정지 전송
     */
    public String onStopAuction(String entrySeq,int countDown) {
        return sendMessage(new StopAuction(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc(), entrySeq, Integer.toString(countDown)));
    }

    /**
     * 일시 정지
     * @param pauseAuction
     * @return
     */
    public String onPause(PauseAuction pauseAuction) {
    	 return sendMessage(pauseAuction);
    }

    /**
     * 재경매자 보냄.
     * @param retryTargetInfo
     * @return
     */
    public String onRetryTargetInfo(RetryTargetInfo retryTargetInfo) {
   	 return sendMessage(retryTargetInfo);
   }
    
    /**
     * @param entrySeq
     * @return
     * @Description 강제유찰 전송
     */
    public String onPassAuction(String entrySeq) {
        return sendMessage(new PassAuction(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc(), entrySeq));
    }

    /**
     * @param AuctionResult
     * @return
     * @Description 낙/유찰 결과 전송
     */
    public String onSendAuctionResult(SendAuctionResult auctionResult) {
        return sendMessage(auctionResult);
    }

    /**
     * @param msg
     * @return
     * @Description 메시지 전송
     */
    public String onToastMessageRequest(String msg) {
        return sendMessage(new ToastMessageRequest(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc(), msg));
    }

    /**
     * @param InitEntryInfo
     * @return
     * @Description 출품 데이터	 초기화
     */
    public String onInitEntryInfo(InitEntryInfo initEntryInfo) {
        return sendMessage(initEntryInfo);
    }
    

    /**
     * 객체를 송신할 때 사용한다.
     *
     * @param message 보낼 객체
     */
    public String sendMessage(NettySendable object) {

        if (!isEmptyClient() && isActive()) {
            mClient.sendMessage(object.getEncodedMessage());
            return object.getEncodedMessage();
        }

        return "[전송실패]" + object.getEncodedMessage();
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
    
    public String getHost() {
    	if(isActive()) {
    		return this.mClient.getHost();
    	}else {
    		return null;
    	}
    }
    
    public int getPort() {
    	if(isActive()) {
    		return this.mClient.getPort();
    	}else {
    		return -1;
    	}
    
    }

}
