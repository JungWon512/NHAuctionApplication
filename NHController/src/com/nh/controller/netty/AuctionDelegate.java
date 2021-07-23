package com.nh.controller.netty;

import com.nh.common.AuctionShareNettyClient;
import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.NettyControllable;
import com.nh.controller.model.SpEntryInfo;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionReponseSession;
import com.nh.share.common.models.ConnectionInfo;
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
                GlobalDefineCode.AUCTION_HOUSE_HWADONG,
                "admin",
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWN0aW9uSG91c2VDb2RlIjoiODgwODk5MDY1NjY1NiIsInVzZXJSb2xlIjoiQURNSU4iLCJ1c2VyTWVtTnVtIjoiYWRtaW4iLCJleHAiOjE2MjY5NjU5OTl9.r8nvpk98OfPiAKN4tFfmzYSouYGntwkSbkwJg8JT0qXRrP6mGi88cac3oAyvVK5nJVeVSFBgrXUx5PnIFygiwg",
                GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER,
                GlobalDefineCode.USE_CHANNEL_MANAGE);
        return sendMessage(nettySendable);
    }

    /**
     * @param nettySendable 접속자 정보
     * @Description 접속자 정보 전송: DB 조회한 사용자 정보
     */
    public String onSendConnectionInfo(NettySendable nettySendable) {
        return sendMessage(nettySendable);
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
        return sendMessage(new ReadyEntryInfo(GlobalDefineCode.AUCTION_HOUSE_HWADONG, entrySeq));
    }

    /**
     * @param entrySeq
     * @return
     * @Description 시작 전송
     */
    public String onStartAuction(String entrySeq) {
        return sendMessage(new StartAuction(GlobalDefineCode.AUCTION_HOUSE_HWADONG, entrySeq));
    }

    /**
     * @param entrySeq
     * @return
     * @Description 정지 전송
     */
    public String onPauseAuction(String entrySeq) {
        return sendMessage(new StopAuction(GlobalDefineCode.AUCTION_HOUSE_HWADONG, entrySeq));
    }

    /**
     * @param entrySeq
     * @return
     * @Description 강제유찰 전송
     */
    public String onPassAuction(String entrySeq) {
        return sendMessage(new PassAuction(GlobalDefineCode.AUCTION_HOUSE_HWADONG, entrySeq));
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
