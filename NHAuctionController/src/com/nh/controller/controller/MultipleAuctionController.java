package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.NettyControllable;
import com.nh.common.interfaces.UdpBillBoardStatusListener;
import com.nh.common.interfaces.UdpPdpBoardStatusListener;
import com.nh.controller.interfaces.SelectEntryListener;
import com.nh.controller.model.SpBidderConnectInfo;
import com.nh.controller.model.SpBidding;
import com.nh.controller.model.SpEntryInfo;
import com.nh.controller.netty.AuctionDelegate;
import com.nh.controller.netty.BillboardDelegate;
import com.nh.controller.netty.PdpDelegate;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.ApiUtils;
import com.nh.controller.utils.AuctionUtil;
import com.nh.controller.utils.AudioFilePlay;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.ListComparator;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.MoveStageUtil.EntryDialogType;
import com.nh.controller.utils.SharedPreference;
import com.nh.controller.utils.SoundUtil;
import com.nh.share.api.ActionResultListener;
import com.nh.share.api.request.body.RequestBidEntryBody;
import com.nh.share.api.request.body.RequestCowInfoBody;
import com.nh.share.api.request.body.RequestMultipleAuctionStatusBody;
import com.nh.share.api.response.BaseResponse;
import com.nh.share.api.response.ResponseBidEntry;
import com.nh.share.api.response.ResponseCowInfo;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionResult;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.CancelBidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.RequestAuctionResult;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.StandConnectInfo;
import com.nh.share.server.models.ToastMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * 일괄 경매
 *
 * @author jhlee
 */
public class MultipleAuctionController implements Initializable, NettyControllable {

	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public FXMLLoader mFxmlLoader = null;// auction controller fxml

	private Stage mStage = null;

	private Stage mMessageStage = null;

	private ResourceBundle mResMsg = null;

	@FXML // 완료,대기,응찰현황 테이블
	private TableView<SpEntryInfo> mWaitTableView;
	
	@FXML // 접속현황 테이블
	private TableView<SpBidderConnectInfo> mConnectionUserTableView;
	
	@FXML // 완료,대기,응찰현황,접속현황 테이블
	private TableView<SpBidding> mBiddingInfoTableView;

	@FXML // root pane
	public BorderPane mRootAuction;

	@FXML // 경매 상단 아이콘 메세지보내기,전광판 1 상태, 전광판 2 상태
	private ImageView mDisplay_1_ImageView, mDisplay_2_ImageView, mDisplay_3_ImageView;

	@FXML // 음성 선택 check-box
	private CheckBox mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mMotherObjNumCheckBox, mMaTimeCheckBox, mPasgQcnCheckBox, mWeightCheckBox, mLowPriceCheckBox, mBrandNameCheckBox;

	@FXML // 대기중인 출품
	private TableColumn<SpEntryInfo, String> mWaitEntryNumColumn, mWaitExhibitorColumn, mWaitGenderColumn, mWaitMotherColumn, mWaitMatimeColumn, mWaitPasgQcnColumn, mWaitWeightColumn, mWaitLowPriceColumn, mWaitSuccessPriceColumn, mWaitSuccessfulBidderColumn, mWaitResultColumn, mWaitNoteColumn;

	@FXML // 현재 경매
	private Label mCurEntryNumLabel, mCurExhibitorLabel, mCurGenterLabel, mCurMotherLabel, mCurMatimeLabel, mCurPasgQcnLabel, mCurWeightLabel, mCurLowPriceLabel, mCurSuccessPriceLabel, mCurSuccessfulBidderLabel, mCurResultLabel, mCurNoteLabel;

	@FXML // 응찰자 정보
	private TableColumn<SpBidding, String> mBiddingPriceColumn, mBiddingUserColumn;
	
	@FXML // 사용자 접속 현황
	private TableColumn<SpBidderConnectInfo, String> mConnectionUserColumn_1, mConnectionUserColumn_2, mConnectionUserColumn_3, mConnectionUserColumn_4, mConnectionUserColumn_5;
	
	@FXML // 접속자 정보 수
	private Label mConnectionUserCntLabel;
	
	@FXML // 경매 정보
	private Label mAuctionInfoDateLabel, mAuctionInfoRoundLabel, mAuctionInfoGubunLabel, mAuctionInfoTotalCountLabel;
	
	
	@FXML // 하단 버튼
	private Button mBtnEsc, mBtnF1,mBtnF4, mBtnF5,mBtnStart,mBtnPause,mBtnFinish;
	
	@FXML // 하단 메세지 전송 상위 뷰
	private StackPane mSTPMessage;
	
	@FXML // 하단 메세지 전송 텍스트
	private Label mMessageText;
	
	@FXML // 감가 기준 금액 / 횟수
	private Label mDeprePriceLabel, mLowPriceChgNtLabel;
	
	@FXML // 음성설정 ,저장 ,음성중지 ,낙찰결과
	private Button mBtnSettingSound, mBtnSave, mBtnStopSound, mBtnEntrySuccessList;

	
	private ObservableList<SpEntryInfo> mWaitEntryInfoDataList = FXCollections.observableArrayList(); // 대기중 출품
	private ObservableList<SpBidding> mBiddingUserInfoDataList = FXCollections.observableArrayList(); // 응찰 현황
	private ObservableList<SpBidderConnectInfo> mConnectionUserDataList = FXCollections.observableArrayList(); // 접속자 현황
	private Map<String, BidderConnectInfo> mConnectionUserMap = new HashMap<>(); // 접속 현황
	
	private Image mResDisplayOn = new Image("/com/nh/controller/resource/images/ic_con_on.png"); // 전광판 On 이미지 리소스
	private Image mResDisplayOff = new Image("/com/nh/controller/resource/images/ic_con_off.png"); // 전광판 Off 이미지 리소스
	private FadeTransition mAnimationFadeIn; // 토스트 애니메이션 START
	private FadeTransition mAnimationFadeOut; // 토스트 애니메이션 END

	
	private String REFRESH_ENTRY_LIST_TYPE_NONE = "NONE"; // 출장우 정보 갱신 - 기본
	private String REFRESH_ENTRY_LIST_TYPE_SEND = "SEND"; // 출장우 정보 갱신 후 정보 보냄
	private String REFRESH_ENTRY_LIST_TYPE_START = "START"; // 출장우 정보 갱신 후 시작
	
	private EntryDialogType mCurPageType; // 전체or보류목록 타입
	private int mRecordCount = 0; // cow total data count
	
	private boolean isShowToast = false; // 메세지 전송 상태 플래그
	private boolean isApplicationClosePopup = false; // 임의 종료시 server 연결 해제 팝업 노출 막는 플래그
	private Queue<String> mMsgQueue = new PriorityQueue(); // 메세지 전송 queue
	private ObservableList<SpEntryInfo> mDummyRow = FXCollections.observableArrayList(); // dummy row
	private int DUMMY_ROW_WAIT = 8;
	private SpEntryInfo mCurrentSpEntryInfo = null; // 현재 진행 출품
	private AuctionStatus mAuctionStatus = null; // 경매 상태

	/**
	 * setStage
	 * 
	 * @param stage
	 */
	public void setStage(Stage stage) {
		mStage = stage;

		Platform.runLater(() -> {
			
			// 전광판 접속
			Thread udpServer = new Thread("server") {
				@Override
				public void run() {
					createUdpClient(mUdpBillBoardStatusListener, mUdpPdpBoardStatusListener);
				}
			};
			udpServer.setDaemon(true);
			udpServer.start();
		});

		if (mStage != null) {
			// 타이틀바 X 버튼 프로그램 완전 종료
			stage.setOnCloseRequest(e -> {
				onServerAndClose();
				Platform.exit();
				System.exit(0);
			});
		}
	}

	/**
	 * 전광판, PDP 서버 접속
	 * 
	 * @param udpBillBoardStatusListener
	 * @param udpPdpBoardStatusListener
	 */
	protected void createUdpClient(UdpBillBoardStatusListener udpBillBoardStatusListener, UdpPdpBoardStatusListener udpPdpBoardStatusListener) {

		try {
			// UDP 전광판
			if (SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, "") != null && !SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, "").isEmpty()) {
				BillboardDelegate.getInstance().createClients(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, ""), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT1, ""), udpBillBoardStatusListener);

				if (BillboardDelegate.getInstance().isActive()) {
					// 전광판 자릿수 셋팅
					mLogger.debug(mResMsg.getString("msg.billboard.send.init.info") + BillboardDelegate.getInstance().initBillboard());
				}
				mLogger.debug("Billboard connection ip : " + SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, ""));
				mLogger.debug("Billboard connection port : " + SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, ""));
				mLogger.debug("Billboard connection status : " + BillboardDelegate.getInstance().isActive());
			}

			// UDP PDP
			if (SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT1, "") != null && !SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT1, "").isEmpty()) {
				PdpDelegate.getInstance().createClients(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT1, ""), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT1, ""), udpPdpBoardStatusListener);

				if (PdpDelegate.getInstance().isActive()) {
					// PDP 자릿수 셋팅
					mLogger.debug(mResMsg.getString("msg.pdp.send.init.info") + PdpDelegate.getInstance().initPdp());
				}

				mLogger.debug("PDP connection ip : " + SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT1, ""));
				mLogger.debug("PDP connection port : " + SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT1, ""));
				mLogger.debug("PDP connection status : " + PdpDelegate.getInstance().isActive());
			}

		} catch (Exception e) {
			mLogger.debug("[UDP 전광판 Exception] : " + e);
		}
	}

	/**
	 * 경매 서버 접속
	 *
	 * @param chooseAuctionStage
	 * @param fxmlLoader
	 */
	public void onConnectServer(Stage chooseAuctionStage, FXMLLoader fxmlLoader, String ip, int port, String id) {

		mStage = chooseAuctionStage;
		mFxmlLoader = fxmlLoader;
		// 경매 구분
		SettingApplication.getInstance().setAuctionObjDsc(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());

		// connection server
		Thread aucServer = new Thread("server") {
			@Override
			public void run() {
				createClient(ip, port, id, "N");
			}
		};
		aucServer.setDaemon(true);
		aucServer.start();
	}

	/**
	 * 소켓 서버 접속
	 */
	public void createClient(String host, int port, String userMemNum, String watchMode) {
		AuctionDelegate.getInstance().createClients(host, port, userMemNum, watchMode, this);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// get ResMsg
		if (resources != null) {
			mResMsg = resources;
		}

		// 뷰 초기화
		initViewConfiguration();
		// 사운드 초기화
		SoundUtil.getInstance();
		
		// 경매 데이터
		Thread thread = new Thread("cowInfo") {
			@Override
			public void run() {
				// 경매 데이터 set
				requestAuctionInfo();
			}
		};
		thread.setDaemon(true);
		thread.start();
		
	}

	/**
	 * 기본 뷰 설정
	 */
	private void initViewConfiguration() {

		initParsingSharedData();
		
		initTableConfiguration();
		
		mBtnEsc.setOnMouseClicked(event -> onCloseApplication());
		mBtnF1.setOnMouseClicked(event -> onSendEntryData());
		mBtnF4.setOnMouseClicked(event -> openEntryListPopUp());
		mBtnF5.setOnMouseClicked(event -> openEntryPendingListPopUp());
		mBtnStart.setOnMouseClicked(event -> onRefreshStartAuction());
		mBtnPause.setOnMouseClicked(event -> onCloseApplication());
		mBtnFinish.setOnMouseClicked(event -> onCloseApplication());
		
		mBtnEntrySuccessList.setOnMouseClicked(event -> openFinishedEntryListPopUp());
		
		// 메세지 전송 애니메이션 초기화
		initMsgToast();
	}
	
	private void initMsgToast() {

		mAnimationFadeIn = new FadeTransition(Duration.millis(250));
		mAnimationFadeIn.setNode(mSTPMessage);
		mAnimationFadeIn.setFromValue(0.0);
		mAnimationFadeIn.setToValue(1.0);
		mAnimationFadeIn.setCycleCount(1);
		mAnimationFadeIn.setAutoReverse(false);

		mAnimationFadeOut = new FadeTransition(Duration.millis(500));
		mAnimationFadeOut.setNode(mSTPMessage);
		mAnimationFadeOut.setFromValue(1.0);
		mAnimationFadeOut.setToValue(0.0);
		mAnimationFadeOut.setCycleCount(1);
		mAnimationFadeOut.setAutoReverse(false);

		mAnimationFadeIn.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				Timer timer = new Timer(true);
				TimerTask timerTask = new TimerTask() {
					@Override
					public void run() {
						mAnimationFadeOut.playFromStart();
					}
				};
				timer.schedule(timerTask, 1000);
			}
		});

		mAnimationFadeOut.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				if (mSTPMessage.isVisible()) {
					mSTPMessage.setVisible(false);
					isShowToast = false;
					showToastMessage();
				}
			}
		});
	}
	
	
	/**
	 * 출품 정보 음성 설정 저장된 값들 셋팅
	 */
	@SuppressWarnings("unchecked")
	private void initParsingSharedData() {

		List<Boolean> checkList = SettingApplication.getInstance().getParsingMainSoundFlag();

		// 메인 상단 체크박스 [S]
		mEntryNumCheckBox.setSelected(checkList.get(0));
		mExhibitorCheckBox.setSelected(checkList.get(1));
		mGenderCheckBox.setSelected(checkList.get(2));
		mMotherObjNumCheckBox.setSelected(checkList.get(3));
		mMaTimeCheckBox.setSelected(checkList.get(4));
		mPasgQcnCheckBox.setSelected(checkList.get(5));
		mWeightCheckBox.setSelected(checkList.get(6));
		mLowPriceCheckBox.setSelected(checkList.get(7));
		mBrandNameCheckBox.setSelected(checkList.get(8));

		mEntryNumCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_NUMBER);
		mExhibitorCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_EXHIBITOR);
		mGenderCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_GENDER);
		mMotherObjNumCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MOTHER);
		mMaTimeCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MATIME);
		mPasgQcnCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_PASGQCN);
		mWeightCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_WEIGHT);
		mLowPriceCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_LOWPRICE);
		mBrandNameCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_BRAND);

		mEntryNumCheckBox.setOnAction(mCheckBoxEventHandler);
		mExhibitorCheckBox.setOnAction(mCheckBoxEventHandler);
		mGenderCheckBox.setOnAction(mCheckBoxEventHandler);
		mMotherObjNumCheckBox.setOnAction(mCheckBoxEventHandler);
		mMaTimeCheckBox.setOnAction(mCheckBoxEventHandler);
		mPasgQcnCheckBox.setOnAction(mCheckBoxEventHandler);
		mWeightCheckBox.setOnAction(mCheckBoxEventHandler);
		mLowPriceCheckBox.setOnAction(mCheckBoxEventHandler);
		mBrandNameCheckBox.setOnAction(mCheckBoxEventHandler);
		// 메인 상단 체크박스 [E]
	}
	
	
	/**
	 * 테이블뷰 관련
	 */
	private void initTableConfiguration() {

		// [s] 정렬 css

		// 대기중 출품
		CommonUtils.getInstance().setAlignCenterCol(mWaitEntryNumColumn);
		CommonUtils.getInstance().setAlignCenterCol(mWaitExhibitorColumn);
		CommonUtils.getInstance().setAlignCenterCol(mWaitGenderColumn);
		CommonUtils.getInstance().setAlignCenterCol(mWaitMotherColumn);
		CommonUtils.getInstance().setAlignCenterCol(mWaitMatimeColumn);
		CommonUtils.getInstance().setAlignCenterCol(mWaitPasgQcnColumn);
		CommonUtils.getInstance().setAlignCenterCol(mWaitWeightColumn);
		CommonUtils.getInstance().setAlignCenterCol(mWaitLowPriceColumn);
		CommonUtils.getInstance().setAlignCenterCol(mWaitSuccessPriceColumn);
		CommonUtils.getInstance().setAlignCenterCol(mWaitSuccessfulBidderColumn);
		CommonUtils.getInstance().setAlignCenterCol(mWaitResultColumn);
		CommonUtils.getInstance().setAlignLeftCol(mWaitNoteColumn);
		
		// 응찰 현황
		CommonUtils.getInstance().setAlignRightCol(mBiddingPriceColumn);
		CommonUtils.getInstance().setAlignRightCol(mBiddingUserColumn);
		
		// 접속 현황
		CommonUtils.getInstance().setAlignCenterCol(mConnectionUserColumn_1);
		CommonUtils.getInstance().setAlignCenterCol(mConnectionUserColumn_2);
		CommonUtils.getInstance().setAlignCenterCol(mConnectionUserColumn_3);
		CommonUtils.getInstance().setAlignCenterCol(mConnectionUserColumn_4);
		CommonUtils.getInstance().setAlignCenterCol(mConnectionUserColumn_5);
		// [e] 정렬 css

		// [s] fmt - number
		CommonUtils.getInstance().setNumberColumnFactory(mWaitWeightColumn, false);
		CommonUtils.getInstance().setNumberColumnFactory(mWaitLowPriceColumn, true);
		CommonUtils.getInstance().setNumberColumnFactory(mWaitSuccessPriceColumn, true);
		CommonUtils.getInstance().setNumberColumnFactory(mBiddingPriceColumn, true);
		// [e] fmt - number

		// [s] binding
		// 테이블 컬럼 - 대기
		mWaitEntryNumColumn.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mWaitExhibitorColumn.setCellValueFactory(cellData -> cellData.getValue().getExhibitor());
		mWaitGenderColumn.setCellValueFactory(cellData -> cellData.getValue().getGenderName());
		mWaitMotherColumn.setCellValueFactory(cellData -> cellData.getValue().getMotherCowName());
		mWaitMatimeColumn.setCellValueFactory(cellData -> cellData.getValue().getMatime());
		mWaitPasgQcnColumn.setCellValueFactory(cellData -> cellData.getValue().getPasgQcn());
		mWaitWeightColumn.setCellValueFactory(cellData -> cellData.getValue().getWeight());
		mWaitLowPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getLowPrice());
//		mWaitSuccessPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getSraSbidUpPrice()); //낙찰가
//		mWaitSuccessfulBidderColumn.setCellValueFactory(cellData -> cellData.getValue().getAuctionSucBidder()); //낙찰자
		mWaitSuccessPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getExpAuctionBidPrice()); //낙찰 예정가
		mWaitSuccessfulBidderColumn.setCellValueFactory(cellData -> cellData.getValue().getExpAuctionSucBidder()); //낙찰 예정자
		mWaitResultColumn.setCellValueFactory(cellData -> cellData.getValue().getBiddingResult());
		mWaitNoteColumn.setCellValueFactory(cellData -> cellData.getValue().getNote());

		// 테이블 컬럼 - 응찰자
		mBiddingPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getPrice());
		mBiddingUserColumn.setCellValueFactory(cellData -> cellData.getValue().getAuctionJoinNum());

		// 테이블 컬럼 - 접속자
		mConnectionUserColumn_1.setCellValueFactory(cellData -> cellData.getValue().getUserNo()[0]);
		mConnectionUserColumn_2.setCellValueFactory(cellData -> cellData.getValue().getUserNo()[1]);
		mConnectionUserColumn_3.setCellValueFactory(cellData -> cellData.getValue().getUserNo()[2]);
		mConnectionUserColumn_4.setCellValueFactory(cellData -> cellData.getValue().getUserNo()[3]);
		mConnectionUserColumn_5.setCellValueFactory(cellData -> cellData.getValue().getUserNo()[4]);
		// [e] binding

		// holder default msg
		mWaitTableView.setPlaceholder(new Label(mResMsg.getString("msg.entry.wait.default")));
		mConnectionUserTableView.setPlaceholder(new Label(mResMsg.getString("msg.connected.user.default")));
		mBiddingInfoTableView.setPlaceholder(new Label(mResMsg.getString("msg.bidder.default")));

		// 응찰 현황
		initBiddingInfoDataList();
		// 접속 현황
		initConnectionUserDataList();

	}
	
	
	/**
	 * 응찰자 초기값
	 */
	private void initBiddingInfoDataList() {
		mBiddingUserInfoDataList.clear();
		mBiddingUserInfoDataList.add(new SpBidding());
		mBiddingInfoTableView.setItems(mBiddingUserInfoDataList);
		mBiddingInfoTableView.getSelectionModel().select(0);
//		biddingInfoTableStyleToggle();
	}
	
	/**
	 * 접속자 현황 set
	 */
	private synchronized void initConnectionUserDataList() {

		ContextMenu contextMenu = new ContextMenu();

		MenuItem item1 = new MenuItem(mResMsg.getString("str.connect.user.sort"));
		item1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// 접속자 재정렬
				if (mConnectionUserMap.size() > 0) {
					sortConnectionUserDataList();
				}
			}
		});
		contextMenu.getItems().addAll(item1);

		mConnectionUserTableView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
			@Override
			public void handle(ContextMenuEvent event) {
				// 우클릭 새로고침 show
				contextMenu.show(mConnectionUserTableView, event.getScreenX(), event.getSceneY());
			}
		});

		mConnectionUserDataList.clear();

		if (mConnectionUserDataList.size() <= 0) {
			ObservableList<SpBidderConnectInfo> observDataList = FXCollections.observableArrayList();
			observDataList.add(new SpBidderConnectInfo());
			mConnectionUserDataList.addAll(observDataList);
		}

		mConnectionUserTableView.setItems(mConnectionUserDataList);

		// 접속자수
		Platform.runLater(() -> mConnectionUserCntLabel.setText(String.format(mResMsg.getString("str.connection.user.count"), mConnectionUserMap.size())));
	}
	
	
	/**
	 * 경매 대기 초기값
	 *
	 * @param dataList
	 */
	private void initWaitEntryDataList(ObservableList<SpEntryInfo> dataList) {

		if (dataList.size() > 0) {

			// 총 데이터 수 저장
			mRecordCount = dataList.size();

			// dummy row
			mDummyRow.clear();
			for (int i = 0; DUMMY_ROW_WAIT > i; i++) {
				mDummyRow.add(new SpEntryInfo());
			}

			mWaitTableView.setItems(dataList);
			mWaitTableView.getItems().addAll(mDummyRow);

			if (mWaitTableView.getItems().size() > 0) {

				// 방어 코드... 초기 랜더링 버그..딜레이 줌...
				PauseTransition pauseTransition = new PauseTransition(Duration.millis(1000));
				pauseTransition.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {

						if (mCurrentSpEntryInfo == null) {
							selectIndexWaitTable(0, false);
						}

						mWaitTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldItem, newItem) -> {

							if (newItem != null) {

								SpEntryInfo oldSpEntryInfo = oldItem;
								SpEntryInfo newSpEntryInfo = newItem;

								if (CommonUtils.getInstance().isEmptyProperty(newSpEntryInfo.getEntryNum())) {
									if (!CommonUtils.getInstance().isEmptyProperty(newSpEntryInfo.getEntryNum())) {
										mWaitTableView.getSelectionModel().clearSelection();
										mWaitTableView.getSelectionModel().select(oldSpEntryInfo);
									}
								}
							}
						});
					}
				});
				pauseTransition.play();
			}
		}
	}
	
	
	/**
	 * 경매 데이터 가져옴.
	 */
	private void requestAuctionInfo() {
		// 출장우 정보
		requestEntryData();
		// 경매 정보
		setAuctionInfo();
	}

	/**
	 * 경매 정보
	 */
	private void setAuctionInfo() {
		Platform.runLater(() -> {
			mAuctionInfoDateLabel.setText(CommonUtils.getInstance().getCurrentTime_yyyyMMdd(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucDt()));
			mAuctionInfoRoundLabel.setText(String.valueOf(GlobalDefine.AUCTION_INFO.auctionRoundData.getQcn()));
			mAuctionInfoGubunLabel.setText(AuctionUtil.AucObjDsc.which(Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc())));
			int BaselowPrice = SettingApplication.getInstance().getCowLowerLimitPrice(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());
			setBaseDownPrice(Integer.toString(BaselowPrice));
		});
	}
	
	/**
	 * 예정가 낮추기 금액
	 *
	 * @param downPrice
	 */
	private void setBaseDownPrice(String downPrice) {
		Platform.runLater(() -> mDeprePriceLabel.setText(String.format(mResMsg.getString("str.price"), Integer.parseInt(downPrice))));
	}
	
	
	/**
	 * 경매 출품 데이터
	 */
	private void requestEntryData() {

		mCurPageType = EntryDialogType.ENTRY_LIST;
		
		final String naBzplc = GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc();
		final String aucObjDsc = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());
		final String aucDate = GlobalDefine.AUCTION_INFO.auctionRoundData.getAucDt();
		final String stnYn = SettingApplication.getInstance().getSettingAuctionTypeYn();

		// 출장우 데이터 조회
		RequestCowInfoBody cowInfoBody = new RequestCowInfoBody(naBzplc, aucObjDsc, aucDate, "", stnYn);
		
		ApiUtils.getInstance().requestSelectCowInfo(cowInfoBody, new ActionResultListener<ResponseCowInfo>() {

			@Override
			public void onResponseResult(final ResponseCowInfo result) {

				Platform.runLater(() -> {

					if (result != null && result.getSuccess() && !CommonUtils.getInstance().isListEmpty(result.getData())) {

						mLogger.debug("[출장우 정보 조회 데이터 수] " + result.getData().size());

						List<EntryInfo> entryInfoDataList = new ArrayList<EntryInfo>();

						for (int i = 0; i < result.getData().size(); i++) {
							EntryInfo entryInfo = new EntryInfo(result.getData().get(i));
							String flag = (i == result.getData().size() - 1) ? "Y" : "N";
							entryInfo.setIsLastEntry(flag);
							entryInfoDataList.add(entryInfo);
						}

						mWaitEntryInfoDataList.clear();
						mWaitEntryInfoDataList = getParsingEntryDataList(entryInfoDataList);

						if (!CommonUtils.getInstance().isListEmpty(entryInfoDataList)) {
							mAuctionInfoTotalCountLabel.setText(String.format(mResMsg.getString("str.total.cow.count"), entryInfoDataList.size()));
						}

						initWaitEntryDataList(mWaitEntryInfoDataList);
					}

				});
			}

			@Override
			public void onResponseError(String message) {
				mLogger.debug("[onResponseError] 출장우 정보 " + message);
				// ChooseAuctionController 에서 처리
			}
		});
	}
	
	
	/**
	 * 전체 보기
	 */
	public void openEntryListPopUp() {

		if (MoveStageUtil.getInstance().getDialog() != null && MoveStageUtil.getInstance().getDialog().isShowing()) {
			return;
		}

		if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_START) || mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
			return;
		}

		openEntryDialog(EntryDialogType.ENTRY_LIST);

	}

	/**
	 * 낙찰 결과 보기
	 */
	public void openFinishedEntryListPopUp() {
		openEntryDialog(EntryDialogType.ENTRY_FINISH_LIST);
	}
	
	/**
	 * 보류 목록 보기
	 */
	public void openEntryPendingListPopUp() {

		if (MoveStageUtil.getInstance().getDialog() != null && MoveStageUtil.getInstance().getDialog().isShowing()) {
			return;
		}

		openEntryDialog(EntryDialogType.ENTRY_PENDING_LIST);
	}
	
	/**
	 * 전체보기,보류보기,낙찰결과보기 Dialog
	 *
	 * @param type
	 */
	private void openEntryDialog(EntryDialogType type) {

		MoveStageUtil.getInstance().openEntryListDialog(type, mStage, GlobalDefine.AUCTION_INFO.auctionRoundData, new SelectEntryListener() {
			@Override
			public void callBack(EntryDialogType type, int index, ObservableList<SpEntryInfo> dataList) {

				dismissShowingDialog();

				mLogger.debug("Dialog callBack Value : " + type);

				if (type.equals(EntryDialogType.ENTRY_LIST) || type.equals(EntryDialogType.ENTRY_PENDING_LIST)) {
					refreshWaitEntryDataList(false, REFRESH_ENTRY_LIST_TYPE_NONE);
				}

				if (index < 0) {
					return;
				}

				if (CommonUtils.getInstance().isListEmpty(dataList)) {
					return;
				}

				mCurPageType = type;

				// 낙찰 결과보기는 이동,갱신 안 함
				if (type.equals(EntryDialogType.ENTRY_FINISH_LIST)) {
					return;
				}

				setWaitEntryDataList(dataList);

				if (index > -1) {
					selectIndexWaitTable(index, true);
				}
			}
		});
	}
	
	/**
	 * 출장우 정보 갱신
	 * @param dataList
	 */
	private void setWaitEntryDataList(ObservableList<SpEntryInfo> dataList) {

		Platform.runLater(() -> {

			mRecordCount = dataList.size();
			mWaitEntryInfoDataList.clear();
			mWaitEntryInfoDataList.addAll(dataList);

			for (int i = 0; DUMMY_ROW_WAIT > i; i++) {
				mWaitEntryInfoDataList.add(new SpEntryInfo());
			}
			mWaitTableView.refresh();
		});

	}
	
	
	
	/**
	 * 출품 데이터 전송
	 *
	 */
	public void onSendEntryData() {

		/**
		 * 네티 접속 상태 출품 데이터 전송 전 상태
		 */
		if (AuctionDelegate.getInstance().isActive()) {

			// 버튼 상태
			if (mBtnF1.isDisable()) {
				return;
			}

			CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.msg.send.data"));

			Thread refreshWaitThread = new Thread("refreshWaitEntryDataList") {
				@Override
				public void run() {
					// 보내기 전 한번 더 갱신
					refreshWaitEntryDataList(true, REFRESH_ENTRY_LIST_TYPE_SEND);
				}
			};

			refreshWaitThread.setDaemon(true);
			refreshWaitThread.start();

		} else {
			mLogger.debug(mResMsg.getString("msg.need.connection"));
			CommonUtils.getInstance().dismissLoadingDialog();
		}
	}
	
	
	/**
	 * 대기중인 출품 목록 갱신 변경/추가된 데이터 서버 전달
	 */
	private void refreshWaitEntryDataList(boolean isRefresh, String type) {

		String naBzplc = GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc();
		String aucObjDsc = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());
		String aucDate = GlobalDefine.AUCTION_INFO.auctionRoundData.getAucDt();
		String stnYn = SettingApplication.getInstance().getSettingAuctionTypeYn();
		String selStsDsc = "";
		
		// 보류목록일경우
		if (mCurPageType.equals(EntryDialogType.ENTRY_PENDING_LIST)) {
			selStsDsc = GlobalDefineCode.AUCTION_RESULT_CODE_PENDING;
		}

		// 출장우 데이터 조회
		RequestCowInfoBody cowInfoBody = new RequestCowInfoBody(naBzplc, aucObjDsc, aucDate, selStsDsc, stnYn);

		ApiUtils.getInstance().requestSelectCowInfo(cowInfoBody, new ActionResultListener<ResponseCowInfo>() {
			@Override
			public void onResponseResult(final ResponseCowInfo result) {

				if (result != null && result.getSuccess() && !CommonUtils.getInstance().isListEmpty(result.getData())) {
					mLogger.debug("[출장우 정보 조회 데이터 수] " + result.getData().size());
					
					
					List<EntryInfo> entryInfoDataList = new ArrayList<EntryInfo>();

					for (int i = 0; i < result.getData().size(); i++) {

						EntryInfo entryInfo = new EntryInfo(result.getData().get(i));
						String flag = (i == result.getData().size() - 1) ? "Y" : "N";
						entryInfo.setIsLastEntry(flag);
						entryInfoDataList.add(entryInfo);
					}

					ObservableList<SpEntryInfo> newEntryDataList = getParsingEntryDataList(entryInfoDataList);

					// 조회 데이터 없으면 리턴
					if (CommonUtils.getInstance().isListEmpty(newEntryDataList)) {
						mLogger.debug("조회 데이터 없음.");
						return;
					}
					
					// 현재 최종 수정시간 < 조회된 최종 수정시간 -> 데이터 갱신&서버 전달
					for (int i = 0; mRecordCount > i; i++) {

						String curEntryNum = mWaitEntryInfoDataList.get(i).getEntryNum().getValue();

						for (int j = 0; newEntryDataList.size() > j; j++) {

							String newEntryNum = newEntryDataList.get(j).getEntryNum().getValue();

							if (curEntryNum.equals(newEntryNum)) {

								if (CommonUtils.getInstance().isEmptyProperty(newEntryDataList.get(j).getLsChgDtm()) || CommonUtils.getInstance().isEmptyProperty(mWaitEntryInfoDataList.get(i).getLsChgDtm())) {
									break;
								}

								long newDt = Long.parseLong(newEntryDataList.get(j).getLsChgDtm().getValue());
								long curDt = Long.parseLong(mWaitEntryInfoDataList.get(i).getLsChgDtm().getValue());

								if (newDt > curDt) {
									mWaitEntryInfoDataList.set(i, newEntryDataList.get(j));
									// 출품정보 전송 후 변경된 사항 전달.
									if (mBtnF1.isDisable()) {

										String tmpIsLastEntry = newEntryDataList.get(j).getIsLastEntry().getValue();

										newEntryDataList.get(j).getIsLastEntry().setValue(GlobalDefine.ETC_INFO.AUCTION_DATA_MODIFY_M);

										AuctionDelegate.getInstance().onSendEntryData(newEntryDataList.get(j));

										newEntryDataList.get(j).getIsLastEntry().setValue(tmpIsLastEntry);

										mLogger.debug("변경된 출품 정보 서버 전송 : " + newEntryDataList.get(j).getEntryNum().getValue());
									}
								}
								break;
							}
						}
					}
					

					if (isRefresh) {
						// 추가된 데이터 있는지 확인
						ObservableList<SpEntryInfo> newDataList = newEntryDataList.stream().filter(e -> !mWaitEntryInfoDataList.contains(e)).collect(Collectors.toCollection(FXCollections::observableArrayList));

						// 추가된 데이터 항목이 있으면 add
						if (!CommonUtils.getInstance().isListEmpty(newDataList)) {

							mLogger.debug("추가된 데이터 있음.");

							for (SpEntryInfo spEntryInfo : newDataList) {
								mLogger.debug("추가된 데이터 전송=> " + AuctionDelegate.getInstance().onSendEntryData(spEntryInfo));
							}

							mWaitEntryInfoDataList.addAll(mRecordCount, newDataList);
							mRecordCount += newDataList.size();

						} else {
							mLogger.debug("추기된 데이터 없음.");
						}

						mWaitTableView.setItems(mWaitEntryInfoDataList);
						mWaitTableView.refresh();
					}

					PauseTransition pauseTransition = new PauseTransition(Duration.millis(200));
					pauseTransition.setOnFinished(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							onCowInfoSendOrStartAuction(type);
						}
					});
					pauseTransition.play();
				}
			}

			@Override
			public void onResponseError(String message) {
				mLogger.debug("[onResponseError] 출장우 정보 " + message);
				onCowInfoSendOrStartAuction(type);
				// ChooseAuctionController 에서 처리
			}
		});
	}
	
	
	/**
	 * ( 출품 이관 전 체크
	 * 
	 * @return true : 이관 완료 , false : 이관 필요
	 */
	private boolean isSendEnterInfo() {
		if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
			return false;
		} else {
			return true;
		}
	}
	
	
	/**
	 * 단일 경매 시작. 종료
	 *
	 * @param countDown
	 */
	public void onRefreshStartAuction() {

		// 출품 이관 체크
		if (!isSendEnterInfo()) {
			showAlertPopupOneButton(mResMsg.getString("msg.auction.send.need.entry.data"));
		}

		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_READY: // 준비,경매완료,유찰 상황에서 시작 가능.
		case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
		case GlobalDefineCode.AUCTION_STATUS_PASS:
		
			Thread thread = new Thread() {
				@Override
				public void run() {
					// 갱신 후 변경점 있으면 서버 전달.
					refreshWaitEntryDataList(true, REFRESH_ENTRY_LIST_TYPE_START);
					Platform.runLater(() -> setCurrentEntryInfo(false));
				}
			};

			thread.setDaemon(true);
			thread.start();
			break;
		}
	}
	
	/**
	 * 일괄 경매 시작
	 */
	private void onStartAuction() {

		final String naBzplc = GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc();
		final String aucObjDsc = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());
		final String aucDate = GlobalDefine.AUCTION_INFO.auctionRoundData.getAucDt();
		
		
		RequestMultipleAuctionStatusBody body = new RequestMultipleAuctionStatusBody(naBzplc,aucObjDsc,aucDate,GlobalDefine.AUCTION_INFO.MULTIPLE_AUCTION_STATUS_START);
		ApiUtils.getInstance().requestMultipleAuctionStatus(body ,new ActionResultListener<BaseResponse>() {
			
			@Override
			public void onResponseResult(BaseResponse result) {
				if (result != null && result.getSuccess()) {
					mLogger.debug("[일괄경매 시작 Success]");
					
				}else {
					mLogger.debug("[일괄경매 시작 False]");
					
					Platform.runLater(() ->showAlertPopupOneButton(result.getMessage()));
				}
			}
			
			@Override
			public void onResponseError(String message) {
				mLogger.debug("[일괄경매 시작 onResponseError ]");
				showAlertPopupOneButton(message);
			}
		});
	}
	
	/**
	 * 출장우 전송 & 경매 시작.
	 * @param type
	 */
	private void onCowInfoSendOrStartAuction(String type) {

		if (type.equals(REFRESH_ENTRY_LIST_TYPE_SEND)) {

			Thread thread = new Thread("onSendEntryData") {
				@Override
				public void run() {

					mLogger.debug("start onSendEntryData thread");

					int count = 0;

					for (SpEntryInfo entryInfo : mWaitEntryInfoDataList) {
						if (!CommonUtils.getInstance().isEmptyProperty(entryInfo.getEntryNum())) {
							mLogger.debug(mResMsg.getString("msg.auction.send.entry.data") + AuctionDelegate.getInstance().onSendEntryData(entryInfo));
							count++;
						}
					}

					mLogger.debug(String.format(mResMsg.getString("msg.send.entry.data.result"), count));

					mBtnF1.setDisable(true);

					Platform.runLater(() -> {
						CommonUtils.getInstance().dismissLoadingDialog();
					});

				}
			};

			thread.setDaemon(true);
			thread.start();

		} else if (type.equals(REFRESH_ENTRY_LIST_TYPE_START)) {
			// 경매시
			onStartAuction();
		}
	}
	


	@Override
	public void onActiveChannel(Channel channel) {
		isApplicationClosePopup = true;
		// 제어프로그램 접속
		mLogger.debug(mResMsg.getString("msg.auction.send.connection.info") + AuctionDelegate.getInstance().onSendConnectionInfo());
	}

	@Override
	public void onActiveChannel() {

	}

	@Override
	public void onAuctionStatus(AuctionStatus auctionStatus) {

		mAuctionStatus = auctionStatus;
		
		if(!mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
			//출품 정보 보내기 버튼 비활성화
			mBtnF1.setDisable(true);
		}
		
		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_READY:
			
			break;
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
			
			break;
		case GlobalDefineCode.AUCTION_STATUS_PASS:
		case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
				
			break;
		}
		
	}

	@Override
	public void onCurrentEntryInfo(CurrentEntryInfo currentEntryInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBidding(Bidding bidding) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCancelBidding(CancelBidding cancelBidding) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onToastMessage(ToastMessage toastMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFavoriteEntryInfo(FavoriteEntryInfo favoriteEntryInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAuctionResult(AuctionResult auctionResult) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionInfo(ConnectionInfo connectionInfo) {
		// TODO Auto-generated method stub

	}

	// 2000 : 인증 성공
	// 2001 : 인증 실패
	// 2002 : 중복 접속
	// 2003 : 기타 장애
	@Override
	public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo) {

		Platform.runLater(() -> {

			CommonUtils.getInstance().dismissLoadingDialog();
			
			isApplicationClosePopup = false;

			switch (responseConnectionInfo.getResult()) {
			case GlobalDefineCode.CONNECT_SUCCESS:
				mLogger.debug(mResMsg.getString("msg.connection.success") + responseConnectionInfo.getEncodedMessage());

				// Setting 정보 전송
				mLogger.debug(mResMsg.getString("msg.auction.send.setting.info") + AuctionDelegate.getInstance().onSendSettingInfo(SettingApplication.getInstance().getSettingInfo()));

				MoveStageUtil.getInstance().moveAuctionStage(mStage, mFxmlLoader);

				// 내부 저장 port ip obj
				SharedPreference.getInstance().setString(SharedPreference.getInstance().PREFERENCE_SERVER_IP, AuctionDelegate.getInstance().getHost());

				if (AuctionDelegate.getInstance().getPort() > 0) {
					SharedPreference.getInstance().setInt(SharedPreference.getInstance().PREFERENCE_SERVER_PORT, AuctionDelegate.getInstance().getPort());
				} else {
					SharedPreference.getInstance().setInt(SharedPreference.getInstance().PREFERENCE_SERVER_PORT, GlobalDefine.AUCTION_INFO.AUCTION_PORT);
				}

				SharedPreference.getInstance().setInt(SharedPreference.getInstance().PREFERENCE_SELECTED_OBJ, GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());

				break;
			case GlobalDefineCode.CONNECT_FAIL:

				CommonUtils.getInstance().dismissLoadingDialog();
				mLogger.debug(mResMsg.getString("msg.connection.fail"));
				showAlertPopupOneButton(mResMsg.getString("msg.connection.fail"));
				AuctionDelegate.getInstance().onDisconnect(null);
				break;
			case GlobalDefineCode.CONNECT_DUPLICATE:

				CommonUtils.getInstance().dismissLoadingDialog();
				mLogger.debug(mResMsg.getString("msg.connection.duplicate"));
				showAlertPopupOneButton(mResMsg.getString("msg.connection.duplicate"));
				AuctionDelegate.getInstance().onDisconnect(null);
				break;
			}
		});
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
		case GlobalDefineCode.RESPONSE_REQUEST_BIDDING_LOW_PRICE:
			msg = mResMsg.getString("msg.auction.response.code.bidding.low.price");
			break;
		case GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO:
			msg = mResMsg.getString("msg.auction.response.code.not.transmission.entry.info");
			// 출품 이관 전 상태
			setAuctionStatus(GlobalDefineCode.AUCTION_STATUS_NONE);
			break;
		}

		mLogger.debug(msg + responseCode.getEncodedMessage());
	}
	
	/**
	 * 경매 상태
	 *
	 * @param statusCode
	 */
	protected void setAuctionStatus(String statusCode) {
		mAuctionStatus.setState(statusCode);
	}
	

	@Override
	public void onRequestAuctionResult(RequestAuctionResult requestAuctionResult) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionException(int port) {
		Platform.runLater(() -> {
			mLogger.debug(mResMsg.getString("msg.connection.fail"));
			CommonUtils.getInstance().dismissLoadingDialog();
			showAlertPopupOneButton(mResMsg.getString("msg.connection.fail"));
		});
	}

	@Override
	public void onChannelInactive(int port) {
		mLogger.debug("onChannelInactive : " + port);
		// ESC 눌러서 임의로 접속 종료시 접속 해제 팝업 노출 X
		if (isApplicationClosePopup) {
			return;
		}
		Platform.runLater(() -> {

			Optional<ButtonType> btnResult = showAlertPopupOneButton(mResMsg.getString("msg.disconnection"));

			if (btnResult.get().getButtonData() == ButtonData.LEFT) {
				onServerAndClose();
			}

		});
	}

	@Override
	public void exceptionCaught(int port) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckSession(ChannelHandlerContext ctx, AuctionCheckSession auctionCheckSession) {
		AuctionDelegate.getInstance().onSendCheckSession();
	}

	@Override
	public void onBidderConnectInfo(BidderConnectInfo bidderConnectInfo) {
//		super.onBidderConnectInfo(bidderConnectInfo);

		if (bidderConnectInfo == null || !CommonUtils.getInstance().isValidString(bidderConnectInfo.getUserJoinNum())) {
			mLogger.debug("[SI 응찰자 정보 없음.]");
			return;
		}
		// 미응찰,접속해제 상태만 받음.
		if (!bidderConnectInfo.getStatus().equals(GlobalDefine.AUCTION_INFO.BIDDER_STATUS_N) && !bidderConnectInfo.getStatus().equals(GlobalDefine.AUCTION_INFO.BIDDER_STATUS_L)) {
			return;
		}
		mLogger.debug("[SI 응찰자 접속] " + bidderConnectInfo.getEncodedMessage());

		CompletableFuture<Boolean> futurePrice = new CompletableFuture<>();

		Thread thread = new Thread("onBidderConnectInfo") {
			@Override
			public void run() {
				try {
					boolean isComplete = getConnectionBidderInfo(bidderConnectInfo);
					futurePrice.complete(isComplete);
				} catch (Exception e) {
					futurePrice.completeExceptionally(e);
				}
			}
		};
		thread.setDaemon(true);
		thread.start();

		try {

			Boolean result = futurePrice.get(3000, TimeUnit.SECONDS);

			mLogger.debug("[onBidderConnectInfo result] " + result);

			if (result) {

				Platform.runLater(() -> {
					mConnectionUserCntLabel.setText(String.format(mResMsg.getString("str.connection.user.count"), mConnectionUserMap.size()));
					// 갱신
					mConnectionUserTableView.refresh();
				});
			}

		} catch (InterruptedException e) { // handle e
			mLogger.debug("[onBidderConnectInfo] " + e);
		} catch (ExecutionException e) { // handle e
			mLogger.debug("[onBidderConnectInfo] " + e);
		} catch (TimeoutException e) { // handle e }
			mLogger.debug("[onBidderConnectInfo] " + e);
		}
	}
	
	/**
	 * 접속자 정보 Set
	 * @param bidderConnectInfo
	 * @return
	 */
	private boolean getConnectionBidderInfo(BidderConnectInfo bidderConnectInfo) {

		if (!bidderConnectInfo.getStatus().equals(GlobalDefine.AUCTION_INFO.BIDDER_STATUS_L)) {

			// 상태가 접속해제(L)이 아니고 이미 맵에 있는경우 하위 실행 X
			if (!mConnectionUserMap.containsKey(bidderConnectInfo.getUserJoinNum())) {
				// 맵에 담음.
				mConnectionUserMap.put(bidderConnectInfo.getUserJoinNum(), bidderConnectInfo);

				if (mConnectionUserDataList.size() <= 0) {
					ObservableList<SpBidderConnectInfo> observDataList = FXCollections.observableArrayList();
					observDataList.add(new SpBidderConnectInfo());
					mConnectionUserDataList.addAll(observDataList);
				}

				Loop: for (SpBidderConnectInfo spBidderConnectInfo : mConnectionUserDataList) {

					int len = spBidderConnectInfo.getUserNo().length;

					for (int i = 0; len > i; i++) {
						if (spBidderConnectInfo.getUserNo()[i] == null || !CommonUtils.getInstance().isValidString(spBidderConnectInfo.getUserNo()[i].getValue())) {

							spBidderConnectInfo.getUserNo()[i] = new SimpleStringProperty(bidderConnectInfo.getUserJoinNum());
							// 부가 정보들 필요시 주석 해제
							spBidderConnectInfo.getStatus()[i] = new SimpleStringProperty(bidderConnectInfo.getStatus());

							if (i == 4) {
								ObservableList<SpBidderConnectInfo> observDataList = FXCollections.observableArrayList();
								observDataList.add(new SpBidderConnectInfo());
								mConnectionUserDataList.addAll(observDataList);
							}

							break Loop;
						}
					}
				}
			} else {
				return false;
			}

		} else {
			// 접속 해제인경우.
			// 지움.
			mConnectionUserMap.remove(bidderConnectInfo.getUserJoinNum());
			// 찾아서 지움.
			mConnectionUserDataList.stream().flatMap(a -> Arrays.stream(a.getUserNo())).filter(b -> b != null && b.getValue().equals(bidderConnectInfo.getUserJoinNum())).map(item -> {
				item.setValue("");
				return item;
			}).forEach(System.out::println);

			if (mConnectionUserMap.size() <= 0) {
				sortConnectionUserDataList();
			}

		}
		return true;
	}

	@Override
	public void onStandConnectInfo(StandConnectInfo standConnectInfo) {
		mLogger.debug("[전광판 계류대] 접속여부 : " + standConnectInfo.getEncodedMessage());
		// 계류대 모니터링 On/Off
		if (standConnectInfo.getStatus().equals(GlobalDefine.AUCTION_INFO.AUCTION_STAND_CONNECTION_ON)) {
			setDisplayStand(true);
		} else {
			setDisplayStand(false);
		}
	}

	/**
	 * 원버튼 팝업
	 *
	 * @param message
	 * @return
	 */
	private Optional<ButtonType> showAlertPopupOneButton(String message) {
		return CommonUtils.getInstance().showAlertPopupOneButton(mStage, message, mResMsg.getString("popup.btn.close"));
	}

	/**
	 * 구성 설정
	 */
	public void initConfiguration(Stage stage) {
		// 키 설정
		initKeyConfig();
		// 창 이동 설정
		Platform.runLater(() -> CommonUtils.getInstance().canMoveStage(stage, mRootAuction));
//		CommonUtils.getInstance().canMoveStage(mStage, mWaitTableView);  //대기중인 테이블뷰 드래그 이동. 일단 막아둠
		// 더블클릭 풀스크린
//		CommonUtils.getInstance().onDoubleClickfullScreenMode(mStage, null , null);
	}

	/**
	 * 키 설정
	 */
	private void initKeyConfig() {

		Platform.runLater(() -> {

			mStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

				public void handle(KeyEvent ke) {
					
					
					// 대기중인 목록 위로 이동
					if (ke.getCode() == KeyCode.UP) {

						if (mWaitTableView.isDisable()) {
							return;
						}

						if (mWaitTableView.getSelectionModel().getSelectedIndex() > mRecordCount) {
							mWaitTableView.getSelectionModel().select(mRecordCount - 2);
							mWaitTableView.scrollTo(mRecordCount - 1);
							setCurrentEntryInfo(true);
						} else {
							selectIndexWaitTable(-1, false);
						}

						ke.consume();
					}
					
					// 대기중인 목록 아래로 이동
					if (ke.getCode() == KeyCode.DOWN) {

						if (mWaitTableView.isDisable()) {
							return;
						}

						selectIndexWaitTable(1, false);
						ke.consume();
					}
					
					
					// 경매 시작
					if (ke.getCode() == KeyCode.ENTER) {
						System.out.println("[KeyCode.ENTER]=> " + mAuctionStatus.getState());
						onRefreshStartAuction();
						ke.consume();
					}
				}
			});

		});
	}

	/**
	 * 서버 종료 & 날짜선택 이동
	 */
	private void onServerAndClose() {

		stopAllSound();

		CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.app.closeing"));

		if (mMessageStage != null) {
			if (mMessageStage.isShowing()) {
				mMessageStage.close();
			}
		}

		if (AuctionDelegate.getInstance().isActive()) {
			AuctionDelegate.getInstance().onDisconnect(new NettyClientShutDownListener() {
				@Override
				public void onShutDown(int port) {
					Platform.runLater(() -> {
						CommonUtils.getInstance().dismissLoadingDialog();
						MoveStageUtil.getInstance().moveAuctionType(mStage);
					});
				}
			});
		} else {
			Platform.runLater(() -> {
				CommonUtils.getInstance().dismissLoadingDialog();
				MoveStageUtil.getInstance().moveAuctionType(mStage);
			});
		}
	}

	/**
	 * 경매 취소 모든 사운드 종료
	 */
	public void stopAllSound() {
		// 경매 취소시 사운드 종료
		AudioFilePlay.getInstance().stopSound();
		if (SettingApplication.getInstance().isUseSoundAuction()) {
			// 사운드 중지
			SoundUtil.getInstance().setCurrentEntryInfoMessage(null);
			SoundUtil.getInstance().stopSound();
		}
	}
	
	/**
	 * 프로그램 종료
	 */
	public void onCloseApplication() {

		Platform.runLater(() -> {

			Optional<ButtonType> btnResult = CommonUtils.getInstance().showAlertPopupTwoButton(mStage, mResMsg.getString("str.ask.application.close"), mResMsg.getString("popup.btn.ok"), mResMsg.getString("popup.btn.cancel"));

			if (btnResult.get().getButtonData() == ButtonData.LEFT) {

				isApplicationClosePopup = true;
				
				onServerAndClose();

			}
		});
	}

	/**
	 * 전광판 종합안내 상태 리스너
	 */
	private UdpBillBoardStatusListener mUdpBillBoardStatusListener = new UdpBillBoardStatusListener() {

		@Override
		public void onActive() {
			setDisplayBilboard(true);
		}

		@Override
		public void onInActive() {
			setDisplayBilboard(false);
		}

		@Override
		public void exceptionCaught() {
			setDisplayBilboard(false);
		}
	};

	/**
	 * 전광판 TV(PDP) 상태 리스너
	 */
	public UdpPdpBoardStatusListener mUdpPdpBoardStatusListener = new UdpPdpBoardStatusListener() {

		@Override
		public void onActive() {
			setDisplayPdp(true);
		}

		@Override
		public void onInActive() {
			setDisplayPdp(false);
		}

		@Override
		public void exceptionCaught() {
			setDisplayPdp(false);
		}
	};

	/**
	 * 전광판-종합안내 On/Off
	 * 
	 * @param isOn
	 */
	private void setDisplayBilboard(boolean isOn) {

		Platform.runLater(() -> {
			if (isOn) {
				mLogger.debug("[전광판 Bilboard] onActive");
				mDisplay_1_ImageView.setImage(mResDisplayOn);
			} else {
				mLogger.debug("[전광판 Bilboard] inActive");
				mDisplay_1_ImageView.setImage(mResDisplayOff);
			}
		});
	}

	/**
	 * 전광판-TV(PDP) On/Off
	 * 
	 * @param isOn
	 */
	private void setDisplayPdp(boolean isOn) {
		Platform.runLater(() -> {
			if (isOn) {
				mLogger.debug("[전광판 PDP-TV] onActive");
				mDisplay_2_ImageView.setImage(mResDisplayOn);
			} else {
				mLogger.debug("[전광판 PDP-TV] inActive");
				mDisplay_2_ImageView.setImage(mResDisplayOff);
			}
		});
	}

	/**
	 * 전광판-계류대 On/Off
	 * 
	 * @param isOn
	 */
	private void setDisplayStand(boolean isOn) {
		Platform.runLater(() -> {
			if (isOn) {
				mLogger.debug("[전광판 계류대] onActive");
				mDisplay_3_ImageView.setImage(mResDisplayOn);
			} else {
				mLogger.debug("[전광판 계류대] inActive");
				mDisplay_3_ImageView.setImage(mResDisplayOff);
			}
		});
	}
	
	@SuppressWarnings("rawtypes")
	private EventHandler mCheckBoxEventHandler = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent event) {
			// 체크박스 내부 저장
			if (event.getSource() instanceof CheckBox) {
				CheckBox checkBox = (CheckBox) event.getSource();
				SharedPreference.getInstance().setBoolean(checkBox.getUserData().toString(), checkBox.isSelected());
			}
		}
	};
	
	/**
	 * 접속자 현황 순번으로 재정렬.
	 */
	@SuppressWarnings("unchecked")
	private synchronized void sortConnectionUserDataList() {

		Platform.runLater(() -> {

			mConnectionUserDataList.clear();

			if (mConnectionUserMap.size() > 0) {

				List<BidderConnectInfo> connectionUserList = new ArrayList<BidderConnectInfo>();

				connectionUserList.addAll(mConnectionUserMap.values());

				Collections.sort(connectionUserList, new ListComparator());

				ObservableList<SpBidderConnectInfo> observDataList = CommonUtils.getInstance().partDataList(connectionUserList, 5).stream().map(item -> new SpBidderConnectInfo(item)).collect(Collectors.toCollection(FXCollections::observableArrayList));

				mConnectionUserDataList.addAll(observDataList);
			} else {

				ObservableList<SpBidderConnectInfo> observDataList = FXCollections.observableArrayList();
				observDataList.add(new SpBidderConnectInfo());
				mConnectionUserDataList.addAll(observDataList);

			}

			mConnectionUserTableView.setItems(mConnectionUserDataList);
			mConnectionUserTableView.refresh();
			mConnectionUserTableView.scrollTo(0);

		});
	}
	
	
	/**
	 * EntryInfo -> SpEntryInfo
	 *
	 * @param dataList
	 * @return
	 */
	private ObservableList<SpEntryInfo> getParsingEntryDataList(List<EntryInfo> dataList) {

		ObservableList<SpEntryInfo> resultDataList = dataList.stream().map(item -> new SpEntryInfo(item)).collect(Collectors.toCollection(FXCollections::observableArrayList));

		return resultDataList;
	}
	
	/**
	 * 
	 * @MethodName showToastMessage
	 * @Description 하단 Toast 표시
	 *
	 * @param message
	 */
	private void showToastMessage() {

		if (isShowToast) {
			return;
		}

		Platform.runLater(() -> {

			Object object = null;

			if ((object = mMsgQueue.poll()) != null) {
				if (!mSTPMessage.isVisible()) {
					isShowToast = true;
					mSTPMessage.setVisible(true);
					mAnimationFadeIn.playFromStart();
				}

				mMessageText.setText(String.format(mResMsg.getString("msg.auction.send.message"), object.toString()));
			}

		});
	}
	

	/**
	 * 대기중인 소 row 선택 ( KEY UP/DOWN )
	 *
	 * @param index
	 */
	private void selectIndexWaitTable(int index, boolean isPopupClicked) {

		if (mWaitTableView.getItems().size() == 0) {
			return;
		}

		Platform.runLater(() -> {

			if (mWaitTableView.getItems().size() > 0) {

				if (index == 0) {
					mWaitTableView.getSelectionModel().select(0);
					mWaitTableView.scrollTo(mWaitTableView.getSelectionModel().getSelectedIndex() + 1);
				} else {

					int currentSelectedIndex = mWaitTableView.getSelectionModel().getSelectedIndex();

					int selectIndex = 0;

					if (!isPopupClicked) {
						selectIndex = currentSelectedIndex + index;
					} else {
						selectIndex = index;
					}

					if (mRecordCount > selectIndex) {
						mWaitTableView.getSelectionModel().select(selectIndex);
						mWaitTableView.scrollTo(mWaitTableView.getSelectionModel().getSelectedIndex() + 1);
					}
				}

				setCurrentEntryInfo(true);
			}

		});
	}
	

	/**
	 * 현재 진행할 데이터 Set
	 *
	 */
	private void setCurrentEntryInfo(boolean isSoundData) {

		Platform.runLater(() -> {

			SpEntryInfo currentEntryInfo = mWaitTableView.getSelectionModel().getSelectedItem();

			if (CommonUtils.getInstance().isEmptyProperty(currentEntryInfo.getEntryNum())) {
				return;
			}

			mCurrentSpEntryInfo = currentEntryInfo;
			mCurEntryNumLabel.setText(mCurrentSpEntryInfo.getEntryNum().getValue());
			mCurExhibitorLabel.setText(mCurrentSpEntryInfo.getExhibitor().getValue());
			mCurGenterLabel.setText(mCurrentSpEntryInfo.getGenderName().getValue());
			mCurMotherLabel.setText(mCurrentSpEntryInfo.getMotherCowName().getValue());
			mCurMatimeLabel.setText(mCurrentSpEntryInfo.getMatime().getValue());
			mCurPasgQcnLabel.setText(mCurrentSpEntryInfo.getPasgQcn().getValue());
			mCurSuccessfulBidderLabel.setText(mCurrentSpEntryInfo.getAuctionSucBidder().getValue());
			mCurResultLabel.setText(mCurrentSpEntryInfo.getBiddingResult().getValue());
			mCurNoteLabel.setText(mCurrentSpEntryInfo.getNote().getValue());
			mLowPriceChgNtLabel.setText(String.format(mResMsg.getString("str.price"), Integer.parseInt(mCurrentSpEntryInfo.getLwprChgNt().getValue())));
			mCurWeightLabel.setText(String.format(mResMsg.getString("str.price"), Integer.parseInt(mCurrentSpEntryInfo.getWeight().getValue())));
			mCurLowPriceLabel.setText(String.format(mResMsg.getString("str.price"), Integer.parseInt(mCurrentSpEntryInfo.getLowPrice().getValue())));
			mCurSuccessPriceLabel.setText(String.format(mResMsg.getString("str.price"), Integer.parseInt(mCurrentSpEntryInfo.getSraSbidUpPrice().getValue())));

			requestSelectBidEntry();	
			
			CommonUtils.getInstance().dismissLoadingDialog();
		});
	}
	
	/**
	 * 응찰 목록
	 */
	private void requestSelectBidEntry() {
		
		final String naBzplc = GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc();
		final String aucObjDsc = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());
		final String aucDate = GlobalDefine.AUCTION_INFO.auctionRoundData.getAucDt();
		RequestBidEntryBody body = new RequestBidEntryBody(naBzplc, aucObjDsc, aucDate, mCurrentSpEntryInfo.getOslpNo().getValue());
		
		ApiUtils.getInstance().requestSelectBidEntry(body, new ActionResultListener<ResponseBidEntry>() {
			
			@Override
			public void onResponseResult(ResponseBidEntry result) {
				
				if (result != null && result.getSuccess() && !CommonUtils.getInstance().isListEmpty(result.getData())) {
					
				}else {
					mLogger.debug("[fail] 응찰목록조회 :  " + result.getMessage());
				}
			}
			
			@Override
			public void onResponseError(String message) {
				mLogger.debug("[onResponseError] 응찰목록조회 :  " + message);
			}
		});
		
	}
	
	/**
	 * Showing dialog Close
	 */
	private void dismissShowingDialog() {
		MoveStageUtil.getInstance().dismissDialog();
		MoveStageUtil.getInstance().setBackStageDisableFalse(mStage);
	}
	
}
