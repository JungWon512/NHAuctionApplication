package com.nh.controller.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.nh.common.interfaces.NettyControllable;
import com.nh.common.interfaces.UdpBillBoardStatusListener;
import com.nh.common.interfaces.UdpPdpBoardStatusListener;
import com.nh.controller.model.AucEntrData;
import com.nh.controller.model.AucResultData;
import com.nh.controller.model.BillboardData;
import com.nh.controller.model.PdpData;
import com.nh.controller.model.SpBidderConnectInfo;
import com.nh.controller.model.SpBidding;
import com.nh.controller.model.SpEntryInfo;
import com.nh.controller.netty.AuctionDelegate;
import com.nh.controller.netty.BillboardDelegate1;
import com.nh.controller.netty.BillboardDelegate2;
import com.nh.controller.netty.PdpDelegate;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.ApiUtils;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.GlobalDefine.FILE_INFO;
import com.nh.controller.utils.SharedPreference;
import com.nh.share.api.ActionResultListener;
import com.nh.share.api.models.CowInfoData;
import com.nh.share.api.request.body.RequestBidEntryBody;
import com.nh.share.api.request.body.RequestBidLogBody;
import com.nh.share.api.request.body.RequestBidNumBody;
import com.nh.share.api.response.BaseResponse;
import com.nh.share.api.response.ResponseAuctionResult;
import com.nh.share.api.response.ResponseBidEntry;
import com.nh.share.api.response.ResponseJoinNumber;
import com.nh.share.api.response.ResponseNumber;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionResult;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.CancelBidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.SendAuctionResult;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.RequestAuctionResult;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.ToastMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public abstract class BaseAuctionController implements NettyControllable {

	protected Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	protected ObservableList<SpEntryInfo> mWaitEntryInfoDataList = FXCollections.observableArrayList(); // 대기중 출품
	protected ObservableList<SpEntryInfo> mFinishedEntryInfoDataList = FXCollections.observableArrayList(); // 끝난 출품
	protected ObservableList<SpBidding> mBiddingUserInfoDataList = FXCollections.observableArrayList(); // 응찰 현황
	protected ObservableList<SpBidderConnectInfo> mConnectionUserDataList = FXCollections.observableArrayList(); // 접속자 현황
	protected ObservableList<SpEntryInfo> mDummyRow = FXCollections.observableArrayList(); // dummy row
	protected int DUMMY_ROW_WAIT = 8;
	protected int DUMMY_ROW_FINISHED = 4;
	protected int mRecordCount = 0; // cow total data count

	protected ResourceBundle mResMsg = null; // 메세지 처리

	protected Stage mStage = null; // 현재 Stage

	protected SpEntryInfo mCurrentSpEntryInfo = null; // 현재 진행 출품

	protected SpBidding mRank_1_User = null; // 낙찰 될 1순위 회원

	protected AuctionStatus mAuctionStatus = null; // 경매 상태

	protected LinkedHashMap<String, SpBidding> mCurrentBidderMap = null; // 응찰자 정보 수집 Map

	protected List<SpBidding> mBeForeBidderDataList = null; // 이전 응찰한 응찰자 정보 수집 List

	protected List<SpBidding> mReAuctionBidderDataList = null; // 재경매 응찰자 목록

	protected int mReAuctionCount = -1; // 동일가 설정 횟수

	protected boolean mIsPass = false; // 강제 유찰

	protected boolean isAuctionComplete = false; // 낙찰자 예정 여부

	protected boolean isReAuction = false; // 재경매 여부
	
	protected boolean isReAuctionNewBidding = false; // 재경매 응찰 여부
	
	protected boolean isCountDownRunning = false; // 카운트다운 실행 여부
	protected boolean isCountDownBtnPressed = false; // 카운트다운 버튼 눌림 여부

	protected boolean isStartSoundPlaying = false;

	protected boolean isCancel = false; // 취소 여부
	protected boolean isPause = false; // 정지 여부

	protected boolean isStartedAuction = false; // 시작 여부

	protected boolean isApplicationClosePopup = false; // 임의 종료시 server 연결 해제 팝업 노출 막는 플래그

	private List<SpBidding> mRankBiddingDataList; // 랭킹 계산

	protected boolean isResultCompleteFlag = false; // 낙찰/유찰 결과 ~ 다음 경매 준비까지 버튼 눌림 방지 방어 플래그

	public ExecutorService mExeCalculationRankService = null;

	private Comparator<SpBidding> mBiddingCompare = null; // 응찰금액,응찰시간 정렬

	protected boolean isReAuctionAndOverPrice = false;
	
	protected boolean isOverPricePlaySound = false; // 높은가 사운드 플래그 (중복사운드 방지)
	
	protected boolean isPlayReAuctionSound = false; // 재경매자 사운드
	
	
	protected boolean isAutoPlay = false;

	public BaseAuctionController() {
		init();
	}

	/**
	 * 초기화 작업
	 */
	protected void init() {
		mAuctionStatus = new AuctionStatus();
		setAuctionStatus(GlobalDefineCode.AUCTION_STATUS_NONE);
		mBeForeBidderDataList = new ArrayList<SpBidding>();
		mCurrentBidderMap = new LinkedHashMap<String, SpBidding>();
		mReAuctionBidderDataList = new ArrayList<SpBidding>();
		AuctionDelegate.getInstance().setClearVariable();

		mBiddingCompare = Comparator.comparing(SpBidding::getPriceInt).reversed().thenComparing(SpBidding::getBiddingTimeValue);

		initExecutorService();
	}

	/**
	 * 응찰,접속자 ThreadPool 초기화
	 */
	protected void initExecutorService() {

		if (mExeCalculationRankService == null) {
			mExeCalculationRankService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			mLogger.debug("[#응찰 ThreadPool init]");
		}
	}

	/**
	 * 응찰 ThreadPool ShutDown
	 */
	protected void exeCalculationRankServiceShutDown() {
		if (mExeCalculationRankService != null && !mExeCalculationRankService.isShutdown()) {
			mExeCalculationRankService.shutdown();
			mLogger.debug("[#응찰 ThreadPool shutDown]");
		}
		mExeCalculationRankService = null;
	}

	/**
	 * 경매 상태
	 *
	 * @param statusCode
	 */
	protected void setAuctionStatus(String statusCode) {
		mAuctionStatus.setState(statusCode);
	}

	/**
	 * 소켓 서버 접속
	 */
	protected void createClient(String host, int port, String userMemNum, String watchMode) {
		AuctionDelegate.getInstance().createClients(host, port, userMemNum, watchMode, this);
	}

	/**
	 * 전광판, PDP 서버 접속
	 * 
	 * @param udpBillBoardStatusListener1
	 * @param udpPdpBoardStatusListener
	 */
	protected void createUdpClient(UdpBillBoardStatusListener udpBillBoardStatusListener1, UdpBillBoardStatusListener udpBillBoardStatusListener2, UdpPdpBoardStatusListener udpPdpBoardStatusListener) {

		try {
			// UDP 전광판1
			if (SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, "") != null && !SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, "").isEmpty()) {
				BillboardDelegate1.getInstance().createClients(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, ""), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT1, ""), udpBillBoardStatusListener1);

				if (BillboardDelegate1.getInstance().isActive()) {
					// 전광판 자릿수 셋팅
					addLogItem(mResMsg.getString("msg.billboard.send.init.info") + BillboardDelegate1.getInstance().initBillboard());
				}
				mLogger.debug("Billboard connection ip : " + SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, ""));
				mLogger.debug("Billboard connection port : " + SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, ""));
				mLogger.debug("Billboard connection status : " + BillboardDelegate1.getInstance().isActive());
			}
			
			// UDP 전광판2
			if (SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT2, "") != null && !SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT2, "").isEmpty()) {
				BillboardDelegate2.getInstance().createClients(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT2, ""), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT2, ""), udpBillBoardStatusListener2);

				if (BillboardDelegate2.getInstance().isActive()) {
					// 전광판 자릿수 셋팅
					addLogItem(mResMsg.getString("msg.billboard.send.init.info") + BillboardDelegate2.getInstance().initBillboard());
				}
				mLogger.debug("Billboard connection ip : " + SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT2, ""));
				mLogger.debug("Billboard connection port : " + SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT2, ""));
				mLogger.debug("Billboard connection status : " + BillboardDelegate2.getInstance().isActive());
			}
			
			// UDP PDP
			if (SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT1, "") != null && !SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT1, "").isEmpty()) {
				PdpDelegate.getInstance().createClients(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT1, ""), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT1, ""), udpPdpBoardStatusListener);

				if (PdpDelegate.getInstance().isActive()) {
					// PDP 자릿수 셋팅
					addLogItem(mResMsg.getString("msg.pdp.send.init.info") + PdpDelegate.getInstance().initPdp());
				}

				mLogger.debug("PDP connection ip : " + SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT1, ""));
				mLogger.debug("PDP connection port : " + SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT1, ""));
				mLogger.debug("PDP connection status : " + PdpDelegate.getInstance().isActive());
			}

		} catch (Exception e) {
			mLogger.debug("[UDP 전광판 Exception] : " + e);
		}
	}

	@Override
	public void onActiveChannel(Channel channel) {
		mLogger.debug("onActiveChannel");
		isApplicationClosePopup = true;
		// 제어프로그램 접속
		addLogItem(mResMsg.getString("msg.auction.send.connection.info") + AuctionDelegate.getInstance().onSendConnectionInfo());
	}

	@Override
	public void onActiveChannel() {
		mLogger.debug("onActiveChannel");
	}

	@Override
	public void onAuctionStatus(AuctionStatus auctionStatus) {

		addLogItem("[onAuctionStatus]=> " + auctionStatus.getEncodedMessage());

		// AUCTION_STATUS_NONE = "8001" // 출품 자료 이관 전 상태
		// AUCTION_STATUS_READY = "8002" // 경매 준비 상태
		// AUCTION_STATUS_START = "8003" // 경매 시작 상태
		// AUCTION_STATUS_PROGRESS = "8004" // 경매 진행 상태
		// AUCTION_STATUS_PASS = "8005" // 경매 출품 건 강제 유찰
		// AUCTION_STATUS_COMPLETED = "8006" // 경매 출품 건 완료 상태
		// AUCTION_STATUS_FINISH = "8007" // 경매 종료 상태

		try {

			mAuctionStatus = auctionStatus;

			switch (auctionStatus.getState()) {
			case GlobalDefineCode.AUCTION_STATUS_NONE:
				addLogItem(mResMsg.getString("msg.auction.status.none"));
				break;
			case GlobalDefineCode.AUCTION_STATUS_READY:
				addLogItem(String.format(mResMsg.getString("msg.auction.status.ready"), auctionStatus.getEntryNum()));
				break;
			case GlobalDefineCode.AUCTION_STATUS_START:

				if (!BillboardDelegate1.getInstance().isEmptyClient() && BillboardDelegate1.getInstance().isActive()) {

					// UDP 통신
					BillboardData billboardData1 = new BillboardData();
					billboardData1.setbEntryNum(String.valueOf(mCurrentSpEntryInfo.getEntryNum().getValue()));
					billboardData1.setbExhibitor(String.valueOf(mCurrentSpEntryInfo.getExhibitor().getValue()));
					billboardData1.setbWeight(String.valueOf(mCurrentSpEntryInfo.getWeight().getValue()));
					billboardData1.setbGender(String.valueOf(mCurrentSpEntryInfo.getGender().getValue()));
					billboardData1.setbMotherTypeCode(String.valueOf(mCurrentSpEntryInfo.getMotherTypeCode().getValue()));
					billboardData1.setbPasgQcn(String.valueOf(mCurrentSpEntryInfo.getPasgQcn().getValue()));
					billboardData1.setbMatime(String.valueOf(mCurrentSpEntryInfo.getMatime().getValue()));
					billboardData1.setbKpn(String.valueOf(mCurrentSpEntryInfo.getKpn().getValue()));
					billboardData1.setbRegion(String.valueOf(mCurrentSpEntryInfo.getRgnName().getValue()));
					billboardData1.setbNote(String.valueOf(mCurrentSpEntryInfo.getNote().getValue()));
					billboardData1.setbLowPrice(String.valueOf(mCurrentSpEntryInfo.getLowPrice().getValue()));
					billboardData1.setbDnaYn(String.valueOf(mCurrentSpEntryInfo.getDnaYn().getValue()));

					BillboardDelegate1.getInstance().sendBillboardData(billboardData1);

					// 전광판1 비고 흐름 사용 여부
					if (SettingApplication.getInstance().isBoardUseNote1()) {
						BillboardDelegate1.getInstance().clearBillboardNote();
						BillboardDelegate1.getInstance().sendBillboardNote(billboardData1.getbNote());
					}

					BillboardDelegate1.getInstance().startBillboard();
					addLogItem(mResMsg.getString("msg.billboard.send.current.entry.data") + billboardData1.getEncodedMessage());
				}
				
				if (!BillboardDelegate2.getInstance().isEmptyClient() && BillboardDelegate2.getInstance().isActive()) {

					// UDP 통신
					BillboardData billboardData2 = new BillboardData();
					billboardData2.setbEntryNum(String.valueOf(mCurrentSpEntryInfo.getEntryNum().getValue()));
					billboardData2.setbExhibitor(String.valueOf(mCurrentSpEntryInfo.getExhibitor().getValue()));
					billboardData2.setbWeight(String.valueOf(mCurrentSpEntryInfo.getWeight().getValue()));
					billboardData2.setbGender(String.valueOf(mCurrentSpEntryInfo.getGender().getValue()));
					billboardData2.setbMotherTypeCode(String.valueOf(mCurrentSpEntryInfo.getMotherTypeCode().getValue()));
					billboardData2.setbPasgQcn(String.valueOf(mCurrentSpEntryInfo.getPasgQcn().getValue()));
					billboardData2.setbMatime(String.valueOf(mCurrentSpEntryInfo.getMatime().getValue()));
					billboardData2.setbKpn(String.valueOf(mCurrentSpEntryInfo.getKpn().getValue()));
					billboardData2.setbRegion(String.valueOf(mCurrentSpEntryInfo.getRgnName().getValue()));
					billboardData2.setbNote(String.valueOf(mCurrentSpEntryInfo.getNote().getValue()));
					billboardData2.setbLowPrice(String.valueOf(mCurrentSpEntryInfo.getLowPrice().getValue()));
					billboardData2.setbDnaYn(String.valueOf(mCurrentSpEntryInfo.getDnaYn().getValue()));

					BillboardDelegate2.getInstance().sendBillboardData(billboardData2);
					
					// 전광판2 비고 흐름 사용 여부
					if (SettingApplication.getInstance().isBoardUseNote2()) {
						BillboardDelegate2.getInstance().clearBillboardNote();
						BillboardDelegate2.getInstance().sendBillboardNote(billboardData2.getbNote());
					}
					
					BillboardDelegate2.getInstance().startBillboard();
					addLogItem(mResMsg.getString("msg.billboard.send.current.entry.data") + billboardData2.getEncodedMessage());
				}

				if (!PdpDelegate.getInstance().isEmptyClient() && PdpDelegate.getInstance().isActive()) {

					PdpData pdpData = new PdpData();
					pdpData.setbEntryType(String.valueOf(mCurrentSpEntryInfo.getEntryType().getValue()));
					pdpData.setbEntryNum(String.valueOf(mCurrentSpEntryInfo.getEntryNum().getValue()));
					pdpData.setbExhibitor(String.valueOf(mCurrentSpEntryInfo.getExhibitor().getValue()));
					pdpData.setbWeight(String.valueOf(mCurrentSpEntryInfo.getWeight().getValue()));
					pdpData.setbGender(String.valueOf(mCurrentSpEntryInfo.getGender().getValue()));
					pdpData.setbMotherTypeCode(String.valueOf(mCurrentSpEntryInfo.getMotherTypeCode().getValue()));
					pdpData.setbPasgQcn(String.valueOf(mCurrentSpEntryInfo.getPasgQcn().getValue()));
					pdpData.setbMatime(String.valueOf(mCurrentSpEntryInfo.getMatime().getValue()));
					pdpData.setbKpn(String.valueOf(mCurrentSpEntryInfo.getKpn().getValue()));
					pdpData.setbRegion(String.valueOf(mCurrentSpEntryInfo.getRgnName().getValue()));
					pdpData.setbNote(String.valueOf(mCurrentSpEntryInfo.getNote().getValue()));
					pdpData.setbLowPrice(String.valueOf(mCurrentSpEntryInfo.getLowPrice().getValue()));
					pdpData.setbDnaYn(String.valueOf(mCurrentSpEntryInfo.getDnaYn().getValue()));

					PdpDelegate.getInstance().sendPdpData(pdpData);
					PdpDelegate.getInstance().startPdp();
					addLogItem(mResMsg.getString("msg.pdp.send.current.entry.data") + pdpData.getEncodedMessage());
				}

				addLogItem(String.format(mResMsg.getString("msg.auction.status.start"), auctionStatus.getEntryNum()));

				// 시작 로그 저장
				if (!GlobalDefineCode.FLAG_TEST_MODE_BIDDING_LOG) {
					AucEntrData aucEntrData = getCurrentBaseEntrData(true);
					aucEntrData.setRgSqno(GlobalDefine.AUCTION_INFO.LOG_AUCTION_START);
					aucEntrData.setRmkCntn(mResMsg.getString("str.auction.start"));
					requestInsertBiddingHistory(aucEntrData,GlobalDefine.AUCTION_INFO.BID_LOG_TYPE_START);
				}

				break;
			case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
				addLogItem(String.format(mResMsg.getString("msg.auction.status.progress"), auctionStatus.getEntryNum()));
				break;
			case GlobalDefineCode.AUCTION_STATUS_PASS:
			case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
				
				if(auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PASS)) {
					addLogItem(String.format(mResMsg.getString("msg.auction.status.pass"), auctionStatus.getEntryNum()));
					BillboardDelegate1.getInstance().completeBillboard();
					BillboardDelegate2.getInstance().completeBillboard();
					PdpDelegate.getInstance().completePdp();
				}else if(auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_COMPLETED)) {		
					addLogItem(String.format(mResMsg.getString("msg.auction.status.completed"), auctionStatus.getEntryNum()));
					BillboardDelegate1.getInstance().completeBillboard();
					BillboardDelegate2.getInstance().completeBillboard();
					PdpDelegate.getInstance().completePdp();
				}

				exeCalculationRankServiceShutDown();
			
				if (!GlobalDefineCode.FLAG_TEST_MODE_BIDDING_LOG) {
					AucEntrData aucEntrData = getCurrentBaseEntrData(true);
					aucEntrData.setRgSqno(GlobalDefine.AUCTION_INFO.LOG_AUCTION_FINISH);
					aucEntrData.setRmkCntn(mResMsg.getString("str.auction.finish"));
					requestInsertBiddingHistory(aucEntrData,GlobalDefine.AUCTION_INFO.BID_LOG_TYPE_FINISH);
				}

				break;
			case GlobalDefineCode.AUCTION_STATUS_FINISH:
				addLogItem(mResMsg.getString("msg.auction.status.finish"));
				BillboardDelegate1.getInstance().finishBillboard();
				BillboardDelegate2.getInstance().finishBillboard();
				PdpDelegate.getInstance().finishPdp();
				exeCalculationRankServiceShutDown();
				break;
			}

		} catch (Exception e) {
			mLogger.debug("[onAuctionStatus Send Udp Server Exception] " + e);
		}

	}

	@Override
	public void onCurrentEntryInfo(CurrentEntryInfo currentEntryInfo) {
		addLogItem(mResMsg.getString("msg.auction.get.current.entry.data") + currentEntryInfo.getEncodedMessage());
	}

	@Override
	public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
		mLogger.debug("onAuctionCountDown : " + auctionCountDown.getEncodedMessage());

		if (auctionCountDown.getStatus().equals(GlobalDefineCode.AUCTION_COUNT_DOWN)) {
			// 전광판 카운트다운 전송
			BillboardDelegate1.getInstance().onCountDown(auctionCountDown.getCountDownTime());
			BillboardDelegate2.getInstance().onCountDown(auctionCountDown.getCountDownTime());

			// PDP 카운트다운 전송
			PdpDelegate.getInstance().onCountDown(auctionCountDown.getCountDownTime());
			
//            String msg = String.format(mResMsg.getString("msg.auction.get.count.down"), mCurrentSpEntryInfo.getEntryNum().getValue(), auctionCountDown.getCountDownTime());
			addLogItem("onAuctionCountDown : " + auctionCountDown.getEncodedMessage());
		}
	}

	@Override
	public void onBidding(Bidding bidding) {
		// 응찰 쓰레드풀 실행
		initExecutorService();
		mExeCalculationRankService.submit(getBiddingRunnable(bidding));
	}

	/**
	 * 응찰 현황
	 * 
	 * @param Bidding
	 * @return
	 */
	private Runnable getBiddingRunnable(Bidding bidding) {

		return (() -> {

			try {
				// 시작.진행시 응찰 받음.
				if (!mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_START) && !mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
					addLogItem("경매 시작/진행중일때만 응찰아 가능합니다. " + mAuctionStatus.getState());
					return;
				}

				// 로그
				addLogItem(mResMsg.getString("msg.auction.get.bidding") + bidding.getEncodedMessage());

				if (isAuctionComplete) {
					addLogItem("이미 낙찰 확정. 응찰 안 받음");
					return;
				}
				// 응찰자 ,가격 체크.
				if (!CommonUtils.getInstance().isValidString(bidding.getUserNo()) || !CommonUtils.getInstance().isValidString(bidding.getAuctionJoinNum()) || !CommonUtils.getInstance().isValidString(bidding.getEntryNum()) || !CommonUtils.getInstance().isValidString(bidding.getPrice())) {
					addLogItem("출품번호/응찰자/가격 비정상입니다.");
					return;
				}

				// 현재 경매 진행중인 출품 번호가 아니면 응찰 안되게.
				if (!mCurrentSpEntryInfo.getEntryNum().getValue().equals(bidding.getEntryNum())) {
					addLogItem("현재 진행중인 출품 번호가 아닙니다.");
					return;
				}

				if (bidding.getPrice().length() > 5) {
					addLogItem("응찰가가 5자리 이상입니다. 응찰에 실패했습니다.");
					return;
				}

				// 재경매 상황시 목록에 있는 사람만 정보 받음.
				if (isReAuction) {

					for (SpBidding bidder : mReAuctionBidderDataList) {

						// 재경매 목록에 있는 응찰자만 받음.
						if (bidder.getAuctionJoinNum().getValue().equals(bidding.getAuctionJoinNum())) {
							// 최저가와 같거나 크고, 응찰된 금액이랑 같지 않을 경우 응찰 내역 저장
							if ((mCurrentBidderMap.get(bidding.getAuctionJoinNum()).getPriceInt() != bidding.getPriceInt()) && (mCurrentSpEntryInfo.getLowPriceInt() <= bidding.getPriceInt())) {

								setBidding(bidding,true);

								isReAuctionNewBidding = true;
								
								mLogger.debug("[onBidding] 재경매 응찰 저장 : " + bidding.getPriceInt());
							} else {
								addLogItem("재경매시 응찰 안 됨 " + bidding.getAuctionJoinNum());
							}

							break;
						}
					}

				} else {
					// 재경매 상황이 아니면 응찰 set
					setBidding(bidding,true);
				}

			} catch (Exception e) {
				mLogger.debug("[onBidding Exception] : " + e.toString());
				mCalculationRankCallBack.failed(e, null); // Error
			}
		});
	}

	@Override
	public void onToastMessage(ToastMessage toastMessage) {
		mLogger.debug("onToastMessage : " + toastMessage.getEncodedMessage());
		addLogItem(toastMessage.getEncodedMessage());
	}

	@Override
	public void onConnectionInfo(final ConnectionInfo connectionInfo) {
		mLogger.debug("onConnectionInfo : " + connectionInfo.getEncodedMessage());

		Thread thread = new Thread("onBidderConnectInfo") {
			@Override
			public void run() {

				String auctionHouseCode = GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc();
				String entryType = String.valueOf(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());
				String aucDt = GlobalDefine.AUCTION_INFO.auctionRoundData.getAucDt();
				String userMemNum = connectionInfo.getUserMemNum();

				// 테스트 접속
				if (GlobalDefineCode.FLAG_TEST_MODE) {
					// 성공or실패 서버 전송
					mLogger.debug(AuctionDelegate.getInstance().onSendConnectionInfo(new ResponseConnectionInfo(auctionHouseCode, GlobalDefineCode.CONNECT_SUCCESS, userMemNum, userMemNum)));
					return;
				}

				RequestBidNumBody bidNumBody = new RequestBidNumBody(auctionHouseCode, entryType, aucDt, userMemNum);

				ApiUtils.getInstance().requestSelectBidNum(bidNumBody, new ActionResultListener<ResponseJoinNumber>() {
					@Override
					public void onResponseResult(ResponseJoinNumber result) {

						if (result != null && result.getSuccess() && result.getData() != null && result.getData().getLVST_AUC_PTC_MN_NO() != null) {

							mLogger.debug("[사용자 정보 조회 성공]");

							String userJoinNum = result.getData().getLVST_AUC_PTC_MN_NO();

							mLogger.debug("onConnectionInfo - userNum :\t" + userJoinNum);

							String resultCode = "";

							if (userJoinNum == null || userJoinNum.isEmpty()) {
								// 실패
								resultCode = GlobalDefineCode.CONNECT_FAIL;
							} else {
								// 성공
								resultCode = GlobalDefineCode.CONNECT_SUCCESS;
							}

							// 성공or실패 서버 전송
							mLogger.debug(AuctionDelegate.getInstance().onSendConnectionInfo(new ResponseConnectionInfo(auctionHouseCode, resultCode, userMemNum, userJoinNum)));

						} else {
							mLogger.debug("[사용자 조회 데이터 없음.]" + AuctionDelegate.getInstance().onSendConnectionInfo(new ResponseConnectionInfo(auctionHouseCode, GlobalDefineCode.CONNECT_FAIL, userMemNum, "")));
						}
					}

					@Override
					public void onResponseError(String message) {
						mLogger.debug("[사용자 조회 Error]" + AuctionDelegate.getInstance().onSendConnectionInfo(new ResponseConnectionInfo(auctionHouseCode, GlobalDefineCode.CONNECT_FAIL, userMemNum, "")));
					}
				});
			}
		};

		thread.start();
	}

	@Override
	public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo) {
		mLogger.debug("onResponseConnectionInfo : " + responseConnectionInfo.getEncodedMessage());
	}

	@Override
	public void onResponseCode(ResponseCode responseCode) {
		mLogger.debug("onResponseCode : " + responseCode.getEncodedMessage());

		String msg = "";

		switch (responseCode.getResponseCode()) {
		case GlobalDefineCode.RESPONSE_REQUEST_NOT_RESULT:
			msg = mResMsg.getString("msg.auction.response.code.not.result");
			break;
		case GlobalDefineCode.RESPONSE_REQUEST_FAIL:
			msg = mResMsg.getString("msg.auction.response.code.fail");
			break;
		case GlobalDefineCode.RESPONSE_REQUEST_BIDDING_INVALID_PRICE:
			msg = mResMsg.getString("msg.auction.response.code.bidding.low.price");
			break;
		case GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO:
			msg = mResMsg.getString("msg.auction.response.code.not.transmission.entry.info");
			// 출품 이관 전 상태
			setAuctionStatus(GlobalDefineCode.AUCTION_STATUS_NONE);
			break;
		}

		addLogItem(msg + responseCode.getEncodedMessage());
	}

	@Override
	public void onCancelBidding(CancelBidding cancelBidding) {
		addLogItem(mResMsg.getString("msg.auction.get.bidding.cancel") + cancelBidding.getEncodedMessage());
		initExecutorService();
		// 응찰취소 쓰레드풀 실행
		mExeCalculationRankService.submit(getCancelBiddingRunnable(cancelBidding));
	}

	/**
	 * 응찰 취소
	 * 
	 * @param CancelBidding
	 * @return
	 */
	private Runnable getCancelBiddingRunnable(CancelBidding cancelBidding) {
		return (() -> {

			if (isReAuction) {
				return;
			}
			// 재경매 상황인경우 응찰 취소 불가
			if (SettingApplication.getInstance().isUseReAuction() && mReAuctionCount > 0 && !CommonUtils.getInstance().isListEmpty(mReAuctionBidderDataList)) {
				addLogItem("[재경매중 응찰 취소 불가]");
				return;
			}

			// 낙찰 확정시 취소 응찰 안 받음.
			if (isAuctionComplete) {
				addLogItem("[낙찰 확정 상태임. 응찰 취소 불가]");
				return;
			}

			// 현재 응찰맵에 있는 경우 이전 응찰내역 맵으로 이동 후 삭제처리
			if (mCurrentBidderMap.containsKey(cancelBidding.getAuctionJoinNum())) {

				SpBidding currentBidder = mCurrentBidderMap.get(cancelBidding.getAuctionJoinNum());
				currentBidder.setIsCancelBidding(new SimpleBooleanProperty(true));
				mBeForeBidderDataList.add(currentBidder);
				mCurrentBidderMap.remove(cancelBidding.getAuctionJoinNum());

				// 랭킹 재계산
				calculationRanking();

				Bidding bidding = new Bidding();
				bidding.setAuctionHouseCode(cancelBidding.getAuctionHouseCode());
				bidding.setEntryNum(cancelBidding.getEntryNum());
				bidding.setUserNo(cancelBidding.getUserNo());
				bidding.setAuctionJoinNum(cancelBidding.getAuctionJoinNum());
				bidding.setPrice("0");
				bidding.setPriceInt(0);
				bidding.setBiddingTime(cancelBidding.getCancelBiddingTime());

				if (!GlobalDefineCode.FLAG_TEST_MODE_BIDDING_LOG) {
					insertBiddingLog(bidding);
				}
			} else {
				addLogItem("[현재 응찰현황에 없음. 취소 불가.]");
			}

		});
	}

	@Override
	public void onRequestAuctionResult(RequestAuctionResult requestAuctionResult) {// 현재 사용 X.

	}

	@Override
	public void onCheckSession(ChannelHandlerContext ctx, AuctionCheckSession auctionCheckSession) {
		AuctionDelegate.getInstance().onSendCheckSession();
//		addLogItem(mResMsg.getString("msg.auction.send.session") + AuctionDelegate.getInstance().onSendCheckSession());
	}

	@Override
	public void onAuctionResult(AuctionResult auctionResult) {
	}

	@Override
	public void onConnectionException(int port) {
		Platform.runLater(() -> {
			addLogItem(mResMsg.getString("msg.connection.fail"));
			CommonUtils.getInstance().dismissLoadingDialog();
			showAlertPopupOneButton(mResMsg.getString("msg.connection.fail"));
		});
	}

	@Override
	public void onChannelInactive(int port) {
	}

	@Override
	public void exceptionCaught(int port) {
		mLogger.debug("exceptionCaught : " + port);
	}

	@Override
	public void onBidderConnectInfo(BidderConnectInfo bidderConnectInfo) {
		mLogger.debug("onBidderConnectInfo : " + bidderConnectInfo.getEncodedMessage());
	}

	/**
	 * 현재 가격 기준 모든 응찰 정보 수집 중복 응찰 정보는 수집 대상에서 제외 처리
	 *
	 * @param bidding
	 */
	public synchronized void setBidding(Bidding bidding,boolean isInsertLog) {

		SpBidding spBidding = new SpBidding(bidding);

		// 현재 응찰맵에 응찰내역이 있는경우. 이전 응찰 맵에 저장.
		if (mCurrentBidderMap.containsKey(spBidding.getAuctionJoinNum().getValue())) {

			SpBidding beforeBidder = mCurrentBidderMap.get(spBidding.getAuctionJoinNum().getValue());

			if (beforeBidder.getPriceInt() != spBidding.getPriceInt()) {
				// 이전 응찰 내역에 저장
				mBeForeBidderDataList.add(beforeBidder);
				// 현재 응찰에 저장
				mCurrentBidderMap.put(spBidding.getAuctionJoinNum().getValue(), spBidding);
			} else {
				addLogItem("==== 이전가 입력. ====");
			}
		} else {
			// 응찰 맵에 Set
			mCurrentBidderMap.put(spBidding.getAuctionJoinNum().getValue(), spBidding);
		}

		// 순위 실시간 계산
		calculationRanking();

		// 응찰 로그 저장
		if (!GlobalDefineCode.FLAG_TEST_MODE_BIDDING_LOG) {
			insertBiddingLog(bidding);
		}
	}

	/**
	 * 랭킹 계산
	 */
	protected void calculationRanking() {

		try {
			if (mCurrentBidderMap != null && mCurrentBidderMap.values().size() > 0) {
				// 순위 정렬
				List<SpBidding> list = new ArrayList<SpBidding>();

				list.addAll(mCurrentBidderMap.values());

				mRankBiddingDataList = getCurrentRankStream(list);

				updateBidderList(mRankBiddingDataList);

			} else {
				updateBidderList(null);
			}
		} catch (Exception e) {
			mCalculationRankCallBack.failed(e, null); // Error
		}
	}

	/**
	 * 순위 계산 -> ui 갱신 -> 콜백 -> 경매 정지 타이머 실행
	 */
	protected CompletionHandler<Boolean, Void> mCalculationRankCallBack = new CompletionHandler<Boolean, Void>() {
		@Override
		public void completed(Boolean result, Void attachment) {
			
			//음성경매만 
			if (!SettingApplication.getInstance().isUseSoundAuction() || isStartSoundPlaying) {
				mLogger.debug("[음성경매가 아니거나 시작 사운드 플래이중임");
				return;
			}
			
			
//			soundAuctionTimerTask();
			
			//재경매 상황 아님
			//경매 정지 주석 해제 2021-12-06
			if(!isReAuction) {
				
				mLogger.debug("[mCalculationRankCallBack 재경매 상황 아님]");
				
				soundAuctionTimerTask();
				
			}else {
				
				//재경매 상황
				mLogger.debug("[mCalculationRankCallBack 재경매 상황]");
				
				if(!isPlayReAuctionSound) {
					soundAuctionTimerTask();
				}
			}
			

			// 음성경매시 응찰 금액 들어오면 타이머 동작 변경.
//			if (SettingApplication.getInstance().isUseSoundAuction() && !isReAuction && !isStartSoundPlaying) {
//				addLogItem("[순위 산정 완료 정지 타이머 실행] isReAuction 1111 : " + isReAuction + " / isStartSoundPlaying : " + isStartSoundPlaying);
//				soundAuctionTimerTask();
//			} else {
//				if (isReAuction && SettingApplication.getInstance().isUseSoundAuction()) {
//					
//					addLogItem("[순위 산정 완료 정지 타이머 실행] isReAuction 2222 : " + isReAuction + " / isStartSoundPlaying : " + isStartSoundPlaying);
//					if (checkOverPrice(mBiddingUserInfoDataList.get(0))) {
//						stopSoundAuctionTimerTask();
//						soundAuctionTimerTask();
//					}else {
//						addLogItem("[순위 산정 완료 정지 타이머 실행] isReAuction 3333 : " + isReAuction + " / isStartSoundPlaying : " + isStartSoundPlaying);
//						stopSoundAuctionTimerTask();
//						checkBiddingUserPlaySound();
//					}
//				}
//			}
		}

		@Override
		public void failed(Throwable exc, Void attachment) {
			addLogItem("[순위 산정 실패 에러]" + exc.toString());
//			mExeCalculationRankService.shutdown();
		}
	};

	/**
	 * 1순위 응찰 금액 확인
	 * 
	 * @param spBidding
	 * @return
	 */
	protected boolean checkLowPrice(SpBidding spBidding) {

		boolean result = false;

		if (spBidding == null) {
			return result;
		}

		SpEntryInfo entryInfo = mCurrentSpEntryInfo;

		SpBidding bidder = spBidding;

		// 최저가
		int lowPrice = entryInfo.getLowPriceInt();
		// 응찰가
		int curPrice = bidder.getPriceInt();

		// 응찰 금액이 최저가와 같거나 크면 OK
		if (curPrice >= lowPrice) {
			result = true;
		}

		mLogger.debug("[응찰=최저가 비교] : " + result + " / 최저가 : " + lowPrice + " / 응찰가 : " + curPrice);

		return result;
	}

	/**
	 * 최저가 +상한가 , 응찰가 확인
	 * 
	 * @param spBidding
	 * @return
	 */
	protected boolean checkOverPrice(SpBidding spBidding) {

		boolean result = false;

		if (spBidding == null || spBidding.getPrice() == null || spBidding.getPriceInt() <= 0) {
			return result;
		}

		SpEntryInfo entryInfo = mCurrentSpEntryInfo;

		SpBidding bidder = spBidding;

		// 최저가
		int lowPrice = entryInfo.getLowPriceInt();

		// 상한가
		int upperLimitPrice = SettingApplication.getInstance().getCowUpperLimitPrice(Integer.parseInt(entryInfo.getEntryType().getValue()));

		if (upperLimitPrice <= 0) {
			upperLimitPrice = 0;
		}

		int targetPrice = lowPrice + upperLimitPrice;

		// 응찰가
		int curPrice = bidder.getPriceInt();

		if (curPrice <= targetPrice) {
			result = true;
			isOverPricePlaySound = false;
		}
		
		mLogger.debug("[최저가+상한가=응찰가 비교] : " + result + " / 최저가+상한가 : " + lowPrice + "(" + targetPrice + ")" + " / 응찰가 : " + curPrice + "(" + curPrice + ")" + " / 회원번호 : " + spBidding.getAuctionJoinNum());

		return result;
	}

	/**
	 * 낙유찰 결과 전송 + 결과 DB 저장
	 *
	 * @param isSuccess
	 * @param currentEntryInfo
	 * @param bidder
	 */

	protected void saveAuctionResult(boolean isSuccess, SpEntryInfo spEntryInfo, SpBidding bidder, String code) {


		SendAuctionResult auctionResult = new SendAuctionResult();

		auctionResult.setAuctionHouseCode(spEntryInfo.getAuctionHouseCode().getValue());
		auctionResult.setEntryNum(spEntryInfo.getEntryNum().getValue());
		auctionResult.setEntryType(spEntryInfo.getEntryType().getValue());
		auctionResult.setAucDt(spEntryInfo.getAucDt().getValue());
		auctionResult.setLsCmeNo(GlobalDefine.ADMIN_INFO.adminData.getUserId());
		auctionResult.setOslpNo(spEntryInfo.getOslpNo().getValue());
		auctionResult.setLedSqno(spEntryInfo.getLedSqno().getValue());

		switch (code) {
		case GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS:
		case GlobalDefineCode.AUCTION_RESULT_CODE_PENDING:
			// 낙찰 , 유찰인 경우 DB업데이트
			if (isSuccess) {
				
				addLogItem("[낙유찰 결과 낙찰 전송]");
				
				// 낙찰
				auctionResult.setResultCode(GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS);
				auctionResult.setSuccessBidder(bidder.getUserNo().getValue());
				auctionResult.setSuccessAuctionJoinNum(bidder.getAuctionJoinNum().getValue());
				auctionResult.setSuccessBidUpr(bidder.getPrice().getValue());

				int sraSbidAm = bidder.getPriceInt();
				
				auctionResult.setSuccessBidPrice(Integer.toString(sraSbidAm));

				bidder.setSraSbidAm(new SimpleStringProperty(Integer.toString(sraSbidAm)));

				try {

					if (!BillboardDelegate1.getInstance().isEmptyClient() && BillboardDelegate1.getInstance().isActive()) {
						// // 전광판 전송
						BillboardData billboardData1 = new BillboardData();
						billboardData1.setbEntryNum(String.valueOf(spEntryInfo.getEntryNum().getValue()));
						billboardData1.setbExhibitor(String.valueOf(spEntryInfo.getExhibitor().getValue()));
						billboardData1.setbWeight(String.valueOf(spEntryInfo.getWeight().getValue()));
						billboardData1.setbGender(String.valueOf(spEntryInfo.getGender().getValue()));
						billboardData1.setbMotherTypeCode(String.valueOf(spEntryInfo.getMotherTypeCode().getValue()));
						billboardData1.setbPasgQcn(String.valueOf(spEntryInfo.getPasgQcn().getValue()));
						billboardData1.setbMatime(String.valueOf(spEntryInfo.getMatime().getValue()));
						billboardData1.setbKpn(String.valueOf(spEntryInfo.getKpn().getValue()));
						billboardData1.setbRegion(String.valueOf(spEntryInfo.getRgnName().getValue()));
						billboardData1.setbNote(String.valueOf(spEntryInfo.getNote().getValue()));
						billboardData1.setbLowPrice(String.valueOf(spEntryInfo.getLowPrice().getValue()));
						billboardData1.setbAuctionBidPrice(String.valueOf(bidder.getPrice().getValue()));
						billboardData1.setbAuctionSucBidder(String.valueOf(bidder.getAuctionJoinNum().getValue()));
						billboardData1.setbDnaYn(String.valueOf(spEntryInfo.getDnaYn().getValue()));

						addLogItem(mResMsg.getString("log.billboard.auction.result.success") + billboardData1.getEncodedMessage());
						BillboardDelegate1.getInstance().sendBillboardData(billboardData1);
						
						// 전광판1 비고 흐름 사용 여부
						if (SettingApplication.getInstance().isBoardUseNote1()) {
							BillboardDelegate1.getInstance().clearBillboardNote();
							BillboardDelegate1.getInstance().sendBillboardNote(billboardData1.getbNote());
						}
					}
					
					if (!BillboardDelegate2.getInstance().isEmptyClient() && BillboardDelegate2.getInstance().isActive()) {
						// // 전광판 전송
						BillboardData billboardData2 = new BillboardData();
						billboardData2.setbEntryNum(String.valueOf(spEntryInfo.getEntryNum().getValue()));
						billboardData2.setbExhibitor(String.valueOf(spEntryInfo.getExhibitor().getValue()));
						billboardData2.setbWeight(String.valueOf(spEntryInfo.getWeight().getValue()));
						billboardData2.setbGender(String.valueOf(spEntryInfo.getGender().getValue()));
						billboardData2.setbMotherTypeCode(String.valueOf(spEntryInfo.getMotherTypeCode().getValue()));
						billboardData2.setbPasgQcn(String.valueOf(spEntryInfo.getPasgQcn().getValue()));
						billboardData2.setbMatime(String.valueOf(spEntryInfo.getMatime().getValue()));
						billboardData2.setbKpn(String.valueOf(spEntryInfo.getKpn().getValue()));
						billboardData2.setbRegion(String.valueOf(spEntryInfo.getRgnName().getValue()));
						billboardData2.setbNote(String.valueOf(spEntryInfo.getNote().getValue()));
						billboardData2.setbLowPrice(String.valueOf(spEntryInfo.getLowPrice().getValue()));
						billboardData2.setbAuctionBidPrice(String.valueOf(bidder.getPrice().getValue()));
						billboardData2.setbAuctionSucBidder(String.valueOf(bidder.getAuctionJoinNum().getValue()));
						billboardData2.setbDnaYn(String.valueOf(spEntryInfo.getDnaYn().getValue()));

						addLogItem(mResMsg.getString("log.billboard.auction.result.success") + billboardData2.getEncodedMessage());
						BillboardDelegate2.getInstance().sendBillboardData(billboardData2);
						
						// 전광판2 비고 흐름 사용 여부
						if (SettingApplication.getInstance().isBoardUseNote2()) {
							BillboardDelegate2.getInstance().clearBillboardNote();
							BillboardDelegate2.getInstance().sendBillboardNote(billboardData2.getbNote());
						}
					}

				} catch (Exception e) {
					mLogger.debug("BillboardDelegate error " + e);
				}

				try {
					if (!PdpDelegate.getInstance().isEmptyClient() && PdpDelegate.getInstance().isActive()) {
						// // PDP 전송
						PdpData pdpData = new PdpData();
						pdpData.setbEntryType(String.valueOf(spEntryInfo.getEntryType().getValue()));
						pdpData.setbEntryNum(String.valueOf(spEntryInfo.getEntryNum().getValue()));
						pdpData.setbExhibitor(String.valueOf(spEntryInfo.getExhibitor().getValue()));
						pdpData.setbWeight(String.valueOf(spEntryInfo.getWeight().getValue()));
						pdpData.setbGender(String.valueOf(spEntryInfo.getGender().getValue()));
						pdpData.setbMotherTypeCode(String.valueOf(spEntryInfo.getMotherTypeCode().getValue()));
						pdpData.setbPasgQcn(String.valueOf(spEntryInfo.getPasgQcn().getValue()));
						pdpData.setbMatime(String.valueOf(spEntryInfo.getMatime().getValue()));
						pdpData.setbKpn(String.valueOf(spEntryInfo.getKpn().getValue()));
						pdpData.setbRegion(String.valueOf(spEntryInfo.getRgnName().getValue()));
						pdpData.setbNote(String.valueOf(spEntryInfo.getNote().getValue()));
						pdpData.setbLowPrice(String.valueOf(spEntryInfo.getLowPrice().getValue()));
						pdpData.setbAuctionBidPrice(String.valueOf(bidder.getPrice().getValue()));
						pdpData.setbAuctionSucBidder(String.valueOf(bidder.getAuctionJoinNum().getValue()));
						pdpData.setbDnaYn(String.valueOf(spEntryInfo.getDnaYn().getValue()));

						addLogItem(mResMsg.getString("log.pdp.auction.result.success") + pdpData.getEncodedMessage());
						PdpDelegate.getInstance().sendPdpData(pdpData);
					}

				} catch (Exception e) {
					mLogger.debug("BillboardDelegate error " + e);
				}

			} else {
				
				addLogItem("[낙유찰 결과 유찰 전송]");
				
				// 유찰&보류
				auctionResult.setResultCode(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING);
				auctionResult.setSuccessBidder(null);
				auctionResult.setSuccessAuctionJoinNum(null);
				auctionResult.setSuccessBidPrice("0");
				auctionResult.setSuccessBidUpr("0");
			}

			// 최저가
			long resultLowPrice = 0;

			// 최저가는 원단위로
			if (CommonUtils.getInstance().isValidString(spEntryInfo.getLowPrice().getValue())) {
				resultLowPrice = Integer.parseInt(spEntryInfo.getLowPrice().getValue());
			}

			
			AucResultData aucResultData = new AucResultData();
			aucResultData.setNaBzPlc(auctionResult.getAuctionHouseCode());
			aucResultData.setAucObjDsc(auctionResult.getEntryType());
			aucResultData.setAucDt(auctionResult.getAucDt());
			aucResultData.setOslpNo(auctionResult.getOslpNo());
			aucResultData.setLedSqno(auctionResult.getLedSqno());
			aucResultData.setTrmnAmnno(auctionResult.getSuccessBidder());
			aucResultData.setLvstAucPtcMnNo(auctionResult.getSuccessAuctionJoinNum());
			aucResultData.setSraSbidAm(auctionResult.getSuccessBidPrice());
			aucResultData.setSraSbidUpr(auctionResult.getSuccessBidUpr());
			aucResultData.setSelStsDsc(auctionResult.getResultCode());
			aucResultData.setLowsSbidLmtAm(Long.toString(resultLowPrice));
			aucResultData.setLschgDtm("");
			aucResultData.setLsCmeno(auctionResult.getLsCmeNo());
			
			ArrayList<AucResultData> entryInfoList = new ArrayList<>();
			entryInfoList.add(aucResultData);
			
			Gson gson = new Gson();
			String jsonResult = gson.toJson(entryInfoList);

			mLogger.debug("[경매결과 param] : " + jsonResult.toString());
			
			// 경매 결과 API 전송
			ApiUtils.getInstance().requestAuctionResult(jsonResult, new ActionResultListener<ResponseAuctionResult>() {
				@Override
				public void onResponseResult(ResponseAuctionResult result) {
					
					if (result != null && result.getSuccess()) {
						
						mLogger.debug("[경매 결과 저장 성공] 결과코드 : " + auctionResult.getResultCode() + " /낙찰가 : " + auctionResult.getSuccessBidUpr() + " /낙찰자 : " + auctionResult.getSuccessAuctionJoinNum());

						
						//Fail list 있으면 저장 실패
						if(CommonUtils.getInstance().isListEmpty(result.getFailList())) {
							
							// 유찰 처리 시 DB 저장 후 낙찰자 및 참가번호를 빈값으로 변경(upadte query error 방지)
							if (auctionResult.getResultCode().equals(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING)) {
								auctionResult.setSuccessBidder("");
								auctionResult.setSuccessAuctionJoinNum("");
							}
							
							// 낙유찰 결과 전송
							addLogItem(mResMsg.getString("msg.auction.send.result") + AuctionDelegate.getInstance().onSendAuctionResult(auctionResult));
							
							// 낙유찰 결과 UI 업데이트
							updateAuctionStateInfo(isSuccess, bidder);
							
						}else {
							Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.auction.result.fail")));
							mLogger.debug("[경매 결과 업데이트 실패. 취소처리]");
							isResultCompleteFlag = false;
							onCancelOrClose();
						}
						
						
					} else {
						Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.auction.result.fail")));
						mLogger.debug("[경매 결과 업데이트 실패. 취소처리]");
						isResultCompleteFlag = false;
						onCancelOrClose();
					}
				
				}

				@Override
				public void onResponseError(String message) {
					Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.auction.result.fail")));
					mLogger.debug("[경매 결과 업데이트 실패. 취소처리]");
					isResultCompleteFlag = false;
					onCancelOrClose();
				}
			});
		

			break;
		case GlobalDefineCode.AUCTION_RESULT_CODE_CANCEL:
			
			addLogItem("[낙유찰 결과 취소]");
			
			auctionResult.setResultCode(GlobalDefineCode.AUCTION_RESULT_CODE_CANCEL);
			auctionResult.setSuccessBidder("");
			auctionResult.setSuccessAuctionJoinNum("");
			auctionResult.setSuccessBidPrice("0");
			auctionResult.setSuccessBidUpr("0");
			addLogItem(mResMsg.getString("msg.auction.send.result") + AuctionDelegate.getInstance().onSendAuctionResult(auctionResult));
			break;
		}
	}
	

	abstract void onCancelOrClose();
	
	abstract void onCancel();
	
	abstract void onClose();
	
	abstract void checkBiddingUserPlaySound();

	/**
	 * run Log
	 *
	 * @param dataList
	 * @param auctionStatus
	 * @param isSuccess
	 * @param userNo
	 */
	protected void runWriteLogFile(final AuctionStatus auctionStatus, final boolean isSuccess, final String userNo) {

		// Create LogFile
		Thread thread = new Thread("logFile") {
			@Override
			public void run() {
				writeLogFile(mRankBiddingDataList, auctionStatus, isSuccess, userNo);
			}
		};
		thread.start();

	}

	/**
	 * @MethodName getCurrentRankStream
	 * @Description 현재 응찰 가격 기준 우선순위 확인 처리
	 */
	public List<SpBidding> getCurrentRankStream(List<SpBidding> list) {

		List<SpBidding> spList = list.parallelStream().sorted(mBiddingCompare).collect(Collectors.toList());

		return spList;
	}

	/**
	 * @MethodName getCurrentRank
	 * @Description 현재 응찰 가격 기준 우선순위 확인 처리
	 */
	@SuppressWarnings("unlikely-arg-type")
	protected List<SpBidding> getCurrentRank(List<SpBidding> list) {

		mLogger.debug("응찰 가격 우선순위 확인");

		Collections.sort(list, new Comparator<SpBidding>() {

			public int compare(SpBidding o1, SpBidding o2) {
				int result = 0;

				if (Long.parseLong(o1.getPrice().getValue()) == Long.parseLong(o2.getPrice().getValue())) {
					if (Long.parseLong(o1.getBiddingTime().getValue()) == Long.parseLong(o2.getBiddingTime().getValue())) {
						result = 0;
					} else if (Long.parseLong(o1.getBiddingTime().getValue()) < Long.parseLong(o2.getBiddingTime().getValue())) {
						result = -1;
					} else if (Long.parseLong(o1.getBiddingTime().getValue()) > Long.parseLong(o2.getBiddingTime().getValue())) {
						result = 1;
					}
				} else if (Long.parseLong(o1.getPrice().getValue()) > Long.parseLong(o2.getPrice().getValue())) {
					result = -1;
				} else if (Long.parseLong(o1.getPrice().getValue()) < Long.parseLong(o2.getPrice().getValue())) {
					result = 1;
				}

				return result;
			}
		});

		return list;

	}

	/**
	 * @param biddingList
	 * @param auctionStatus
	 * @param isAuctionPass
	 * @Description 현재 응찰 가격 기준 우선순위 확인 처리
	 */
	protected synchronized void writeLogFile(List<SpBidding> biddingList, AuctionStatus auctionStatus, boolean isSuccess, String userNo) {

		String currentTime = CommonUtils.getInstance().getCurrentTime("yyyyMMdd");

		String LOG_DIRECTORY = FILE_INFO.AUCTION_LOG_FILE_PATH + currentTime + "_" + auctionStatus.getAuctionHouseCode().toUpperCase();

		String LOG_FILE_NAME = currentTime + "-" + auctionStatus.getEntryNum() + FILE_INFO.AUCTION_LOG_FILE_EXTENSION;

		File fileDirectory = new File(LOG_DIRECTORY);
		File file = new File(fileDirectory, LOG_FILE_NAME);
		FileWriter fileWriter = null;

		String EMPTY_SPACE = "    ";
		String MAIN_LINE = "\r\n====================================================================================================\r\n";
		String SUB_LINE = "\r\n---------------------------------------------------------------------------------------------------------------------------------------------------------\r\n";
		String ENTER_LINE = "\r\n";
		StringBuffer logContent = new StringBuffer();
		String logCurrentTime = CommonUtils.getInstance().getCurrentTime("yyyyMMdd HH:mm:ss:SSS");

		try {
			if (!fileDirectory.exists()) {
				fileDirectory.mkdirs();
			}
			if (file.exists()) {
				addLogItem(String.format(mResMsg.getString("msg.file.duplicated"), LOG_FILE_NAME));
			} else {
				if (file.createNewFile()) {
					addLogItem(String.format(mResMsg.getString("msg.file.create"), LOG_FILE_NAME));
				}
			}

			fileWriter = new FileWriter(file, true);

			String entryNum = auctionStatus.getEntryNum();

			String startPrice = String.format(mResMsg.getString("log.auction.result.start.price"), Integer.parseInt(auctionStatus.getStartPrice()));

			String auctionResult = null;

			if (isSuccess) {
				// 낙찰
				auctionResult = mResMsg.getString("log.auction.result.success");
			} else {
				// 유찰
				auctionResult = mResMsg.getString("log.auction.result.fail");
			}

			logContent.append(MAIN_LINE + ENTER_LINE);

			logContent.append(String.format(mResMsg.getString("log.auction.result.entry.info"), logCurrentTime, entryNum, startPrice, auctionResult, userNo));

			logContent.append(SUB_LINE);

			if (isSuccess && !CommonUtils.getInstance().isListEmpty(biddingList)) {

				for (int i = 0; i < biddingList.size(); i++) {
					logContent.append(ENTER_LINE);
					String user = biddingList.get(i).getAuctionJoinNum().getValue() + "(" + biddingList.get(i).getUserNo().getValue() + ")";
					String rankUser = String.format(mResMsg.getString("log.auction.result.rank"), Integer.toString((i + 1)), user);
					String price = String.format(mResMsg.getString("log.auction.result.price"), biddingList.get(i).getPriceInt()) + EMPTY_SPACE
							+ CommonUtils.getInstance().getCurrentTime_yyyyMMddHHmmssSSS(biddingList.get(i).getBiddingTime().getValue());
					logContent.append(rankUser + EMPTY_SPACE + price);

				}
				logContent.append(ENTER_LINE);
			} else {
				logContent.append(ENTER_LINE + mResMsg.getString("log.auction.result.fail") + ENTER_LINE);
			}

			if (!CommonUtils.getInstance().isListEmpty(mBeForeBidderDataList)) {

				logContent.append(SUB_LINE);

				// ㄱㄴㄷ sort
				Collections.sort(mBeForeBidderDataList);
				// 중복제거
				Set<SpBidding> linkedSet = new LinkedHashSet<>(mBeForeBidderDataList);
				List<SpBidding> distintListData = new ArrayList<SpBidding>(linkedSet);

				for (SpBidding disBidding : distintListData) {

					logContent.append(ENTER_LINE);

					String user = disBidding.getAuctionJoinNum().getValue() + "(" + disBidding.getUserNo().getValue() + ")";
					logContent.append(String.format(mResMsg.getString("log.auction.result.before.bidder"), user));
					logContent.append(ENTER_LINE);

					for (SpBidding beforeBidding : mBeForeBidderDataList) {

						if (disBidding.getUserNo().getValue().equals(beforeBidding.getUserNo().getValue())) {
							logContent.append(String.format(mResMsg.getString("log.auction.result.price"), beforeBidding.getPriceInt()));
							logContent.append(EMPTY_SPACE);
							logContent.append(EMPTY_SPACE);
							logContent.append(CommonUtils.getInstance().getCurrentTime_yyyyMMddHHmmssSSS(beforeBidding.getBiddingTime().getValue()));

							if (beforeBidding.getIsCancelBidding().getValue()) {
								logContent.append(EMPTY_SPACE);
								logContent.append(EMPTY_SPACE);
								logContent.append(mResMsg.getString("log.auction.result.cancel.bidding"));
							}

							logContent.append(ENTER_LINE);
						}
					}
				}
			}

			logContent.append(ENTER_LINE);
			addLogItem(logContent.toString());

			fileWriter.write(logContent.toString());
			fileWriter.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.close();
				if (mRankBiddingDataList != null && mRankBiddingDataList.size() > 0) {
					mRankBiddingDataList.clear();
					mRankBiddingDataList = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 경매정보 - 경매 상태 표시
	 *
	 * @param code      현재 경매 상태
	 * @param isSuccess true : 낙찰 , false : 유찰
	 */
	abstract void updateAuctionStateInfo(boolean isSuccess, SpBidding bidder);

	/**
	 * 응찰자 현황 표시
	 *
	 * @param spBiddingDataList 랭킹 산정된 응찰자 리스트
	 */
	abstract void updateBidderList(List<SpBidding> spBiddingDataList);

	/**
	 * 음성경매 타이머 동작
	 * 
	 * @param spBiddingDataList
	 */
	abstract void soundAuctionTimerTask();

	abstract void stopSoundAuctionTimerTask();

	/**
	 * ADD 로그
	 *
	 * @param str
	 */
	protected void addLogItem(String str) {
		if (!str.isEmpty()) {
			mLogger.debug("Log : " + str);
		}
	}

	/**
	 * 현재 출품 데이터 응찰 로그 관련 기본 객체
	 * 
	 * @return
	 */
	private AucEntrData getCurrentBaseEntrData(boolean isStartOrFinish) {

		AucEntrData aucEntrData = new AucEntrData();
		aucEntrData.setNaBzplc(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc());
		aucEntrData.setAucObjDsc(Integer.parseInt(mCurrentSpEntryInfo.getEntryType().getValue()));
		aucEntrData.setAucDt(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucDt());
		aucEntrData.setOslpNo(mCurrentSpEntryInfo.getOslpNo().getValue());
		aucEntrData.setAucPrgSq(mCurrentSpEntryInfo.getEntryNum().getValue());

		// 시작 , 종료 기본 값
		if (isStartOrFinish) {
			aucEntrData.setTrmnAmnno("0");
			aucEntrData.setLvstAucPtcMnNo("0");
			aucEntrData.setAtdrAm("0");
			aucEntrData.setAtdrDtm(CommonUtils.getInstance().getCurrentTimeSc());
		}

		return aucEntrData;
	}

	/**
	 * 응찰 로그 저장 API
	 * 
	 * @param aucEntrData
	 */
	private void requestInsertBiddingHistory(final AucEntrData aucEntrData ,final String type) {

		RequestBidLogBody insertBody = new RequestBidLogBody(aucEntrData.getNaBzplc(), Integer.toString(aucEntrData.getAucObjDsc()), aucEntrData.getAucDt(), aucEntrData.getOslpNo(), aucEntrData.getRgSqno(), aucEntrData.getTrmnAmnno(), aucEntrData.getLvstAucPtcMnNo(), aucEntrData.getAtdrAm(), aucEntrData.getRmkCntn(), aucEntrData.getAtdrDtm(), aucEntrData.getAucPrgSq());
		addLogItem("[응찰 로그 insertBody] : " +insertBody.toString());
		ApiUtils.getInstance().requestInsertBidLog(insertBody, new ActionResultListener<ResponseNumber>() {

			@Override
			public void onResponseResult(ResponseNumber result) {
				
				String logStr = "";
				
				if(type.equals(GlobalDefine.AUCTION_INFO.BID_LOG_TYPE_START)) {
					logStr = "시작";
				}else if(type.equals(GlobalDefine.AUCTION_INFO.BID_LOG_TYPE_ING)) {
					logStr = "응찰";
				}else if(type.equals(GlobalDefine.AUCTION_INFO.BID_LOG_TYPE_FINISH)) {
					logStr = "종료";
				}
				
				addLogItem("[" + logStr + "로그 onResponseResult] : " +result.getData() + " / " + result.getSuccess());
				
				if (result != null && result.getSuccess()) {
					addLogItem("[" + logStr + "로그 저장 완료] 출품 번호 : " + aucEntrData.getAucPrgSq() + " rgSqno : " + aucEntrData.getRgSqno() + " 응찰금액 : " + aucEntrData.getAtdrAm());
				} else {
					addLogItem("[" + logStr + "로그 저장 실패]");
				}
			}

			@Override
			public void onResponseError(String message) {
				
				String logStr = "";
				
				if(type.equals(GlobalDefine.AUCTION_INFO.BID_LOG_TYPE_START)) {
					logStr = "시작";
				}else if(type.equals(GlobalDefine.AUCTION_INFO.BID_LOG_TYPE_ING)) {
					logStr = "응찰";
				}else if(type.equals(GlobalDefine.AUCTION_INFO.BID_LOG_TYPE_FINISH)) {
					logStr = "종료";
				}
				
				addLogItem("[" + logStr + "로그 저장 실패]");
			}
		});
	}
	
	/**
	 * 경매 시작 => 응찰,응찰취소 로그 저장
	 */
	private void insertBiddingLog(final Bidding bidding) {

		Thread thread = new Thread("insertBiddingLog") {
			@Override
			public void run() {
				// 응찰 로그 저장
				AucEntrData aucEntrData = getCurrentBaseEntrData(false);
				aucEntrData.setTrmnAmnno(bidding.getUserNo());
				aucEntrData.setLvstAucPtcMnNo(bidding.getAuctionJoinNum());
				aucEntrData.setAtdrDtm(CommonUtils.getInstance().getCurrentTime_yyyyMMddHHmmssSSS(bidding.getBiddingTime()));
				aucEntrData.setAtdrAm(bidding.getPrice());
				aucEntrData.setRgSqno("");
				aucEntrData.setRmkCntn("");

				requestInsertBiddingHistory(aucEntrData,GlobalDefine.AUCTION_INFO.BID_LOG_TYPE_ING);
			}
		};
		thread.start();
	}

	

	/**
	 * 응찰 목록
	 */
	protected void requestSelectBidEntry() {

		final String naBzplc = GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc();
		final String aucObjDsc = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());
		final String aucDate =  GlobalDefine.AUCTION_INFO.auctionRoundData.getAucDt();
		RequestBidEntryBody body = new RequestBidEntryBody(naBzplc, aucObjDsc, aucDate, mCurrentSpEntryInfo.getOslpNo().getValue());

		ApiUtils.getInstance().requestSelectBidEntry(body, new ActionResultListener<ResponseBidEntry>() {

			@Override
			public void onResponseResult(ResponseBidEntry result) {
				
	
				if (result != null && result.getSuccess() && !CommonUtils.getInstance().isListEmpty(result.getData())) {

					mLogger.debug("[응찰목록조회 true ]  : " + result.getData().size());
					
					
					List<Bidding> resultDataList = result.getData().stream().map(item -> new Bidding(item)).collect(Collectors.toList());
					
					for(Bidding bidding : resultDataList) {
						setBidding(bidding , false);
					}					
				}else {
					mLogger.debug("[응찰목록조회 false ] : " + result.getMessage());
				}
				
				Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
			}

			@Override
			public void onResponseError(String message) {
				mLogger.debug("[onResponseError] 응찰목록조회 :  " + message);
				Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
			}
		});
	}
	
	
	/**
	 * 원버튼 팝업
	 *
	 * @param message
	 * @return
	 */
	protected Optional<ButtonType> showAlertPopupOneButton(String message) {
		return CommonUtils.getInstance().showAlertPopupOneButton(mStage, message, mResMsg.getString("popup.btn.close"));
	}
	
	

	/**
	 * EntryInfo -> SpEntryInfo
	 *
	 * @param dataList
	 * @return
	 */
	protected ObservableList<SpEntryInfo> getParsingCowEntryDataList(List<CowInfoData> dataList) {
		
		List<EntryInfo> entryInfoDataList = new ArrayList<EntryInfo>();

		for (int i = 0; i < dataList.size(); i++) {
			EntryInfo entryInfo = new EntryInfo(dataList.get(i));
			String flag = (i == dataList.size() - 1) ? "Y" : "N";
			entryInfo.setIsLastEntry(flag);
			entryInfo.setExpAuctionIntNum(Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getRgSqNo()));
			entryInfo.setAuctionTypeCode(Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc()));
			entryInfoDataList.add(entryInfo);
		}
		
		ObservableList<SpEntryInfo> resultDataList = entryInfoDataList.stream().map(item -> new SpEntryInfo(item)).collect(Collectors.toCollection(FXCollections::observableArrayList));

		return resultDataList;
	}
	
	/**
	 * EntryInfo -> SpEntryInfo
	 *
	 * @param dataList
	 * @return
	 */
	protected ObservableList<SpEntryInfo> getParsingEntryDataList(List<EntryInfo> dataList) {

		ObservableList<SpEntryInfo> resultDataList = dataList.stream().map(item -> new SpEntryInfo(item)).collect(Collectors.toCollection(FXCollections::observableArrayList));

		return resultDataList;
	}
	
	public void sendBillboardEntryData() {
		if (!BillboardDelegate1.getInstance().isEmptyClient() && BillboardDelegate1.getInstance().isActive()) {

			// UDP 통신
			BillboardData billboardData1 = new BillboardData();
			billboardData1.setbEntryNum(String.valueOf(mCurrentSpEntryInfo.getEntryNum().getValue()));
			billboardData1.setbExhibitor(String.valueOf(mCurrentSpEntryInfo.getExhibitor().getValue()));
			billboardData1.setbWeight(String.valueOf(mCurrentSpEntryInfo.getWeight().getValue()));
			billboardData1.setbGender(String.valueOf(mCurrentSpEntryInfo.getGender().getValue()));
			billboardData1.setbMotherTypeCode(String.valueOf(mCurrentSpEntryInfo.getMotherTypeCode().getValue()));
			billboardData1.setbPasgQcn(String.valueOf(mCurrentSpEntryInfo.getPasgQcn().getValue()));
			billboardData1.setbMatime(String.valueOf(mCurrentSpEntryInfo.getMatime().getValue()));
			billboardData1.setbKpn(String.valueOf(mCurrentSpEntryInfo.getKpn().getValue()));
			billboardData1.setbRegion(String.valueOf(mCurrentSpEntryInfo.getRgnName().getValue()));
			billboardData1.setbNote(String.valueOf(mCurrentSpEntryInfo.getNote().getValue()));
			billboardData1.setbLowPrice(String.valueOf(mCurrentSpEntryInfo.getLowPrice().getValue()));
			billboardData1.setbDnaYn(String.valueOf(mCurrentSpEntryInfo.getDnaYn().getValue()));

			BillboardDelegate1.getInstance().sendBillboardData(billboardData1);

			// 전광판1 비고 흐름 사용 여부
			if (SettingApplication.getInstance().isBoardUseNote1()) {
				BillboardDelegate1.getInstance().clearBillboardNote();
				BillboardDelegate1.getInstance().sendBillboardNote(billboardData1.getbNote());
			}

			addLogItem(mResMsg.getString("msg.billboard.send.current.entry.data") + billboardData1.getEncodedMessage());
		}
		
		if (!BillboardDelegate2.getInstance().isEmptyClient() && BillboardDelegate2.getInstance().isActive()) {

			// UDP 통신
			BillboardData billboardData2 = new BillboardData();
			billboardData2.setbEntryNum(String.valueOf(mCurrentSpEntryInfo.getEntryNum().getValue()));
			billboardData2.setbExhibitor(String.valueOf(mCurrentSpEntryInfo.getExhibitor().getValue()));
			billboardData2.setbWeight(String.valueOf(mCurrentSpEntryInfo.getWeight().getValue()));
			billboardData2.setbGender(String.valueOf(mCurrentSpEntryInfo.getGender().getValue()));
			billboardData2.setbMotherTypeCode(String.valueOf(mCurrentSpEntryInfo.getMotherTypeCode().getValue()));
			billboardData2.setbPasgQcn(String.valueOf(mCurrentSpEntryInfo.getPasgQcn().getValue()));
			billboardData2.setbMatime(String.valueOf(mCurrentSpEntryInfo.getMatime().getValue()));
			billboardData2.setbKpn(String.valueOf(mCurrentSpEntryInfo.getKpn().getValue()));
			billboardData2.setbRegion(String.valueOf(mCurrentSpEntryInfo.getRgnName().getValue()));
			billboardData2.setbNote(String.valueOf(mCurrentSpEntryInfo.getNote().getValue()));
			billboardData2.setbLowPrice(String.valueOf(mCurrentSpEntryInfo.getLowPrice().getValue()));
			billboardData2.setbDnaYn(String.valueOf(mCurrentSpEntryInfo.getDnaYn().getValue()));

			BillboardDelegate2.getInstance().sendBillboardData(billboardData2);
			
			// 전광판2 비고 흐름 사용 여부
			if (SettingApplication.getInstance().isBoardUseNote2()) {
				BillboardDelegate2.getInstance().clearBillboardNote();
				BillboardDelegate2.getInstance().sendBillboardNote(billboardData2.getbNote());
			}
			
			addLogItem(mResMsg.getString("msg.billboard.send.current.entry.data") + billboardData2.getEncodedMessage());
		}
//		BillboardDelegate1.getInstance().completeBillboard();
//		BillboardDelegate2.getInstance().completeBillboard();
//		PdpDelegate.getInstance().completePdp();
		if (!PdpDelegate.getInstance().isEmptyClient() && PdpDelegate.getInstance().isActive()) {

			PdpData pdpData = new PdpData();
			pdpData.setbEntryType(String.valueOf(mCurrentSpEntryInfo.getEntryType().getValue()));
			pdpData.setbEntryNum(String.valueOf(mCurrentSpEntryInfo.getEntryNum().getValue()));
			pdpData.setbExhibitor(String.valueOf(mCurrentSpEntryInfo.getExhibitor().getValue()));
			pdpData.setbWeight(String.valueOf(mCurrentSpEntryInfo.getWeight().getValue()));
			pdpData.setbGender(String.valueOf(mCurrentSpEntryInfo.getGender().getValue()));
			pdpData.setbMotherTypeCode(String.valueOf(mCurrentSpEntryInfo.getMotherTypeCode().getValue()));
			pdpData.setbPasgQcn(String.valueOf(mCurrentSpEntryInfo.getPasgQcn().getValue()));
			pdpData.setbMatime(String.valueOf(mCurrentSpEntryInfo.getMatime().getValue()));
			pdpData.setbKpn(String.valueOf(mCurrentSpEntryInfo.getKpn().getValue()));
			pdpData.setbRegion(String.valueOf(mCurrentSpEntryInfo.getRgnName().getValue()));
			pdpData.setbNote(String.valueOf(mCurrentSpEntryInfo.getNote().getValue()));
			pdpData.setbLowPrice(String.valueOf(mCurrentSpEntryInfo.getLowPrice().getValue()));
			pdpData.setbDnaYn(String.valueOf(mCurrentSpEntryInfo.getDnaYn().getValue()));

			PdpDelegate.getInstance().sendPdpData(pdpData);
			addLogItem(mResMsg.getString("msg.pdp.send.current.entry.data") + pdpData.getEncodedMessage());
		}
	}

}
