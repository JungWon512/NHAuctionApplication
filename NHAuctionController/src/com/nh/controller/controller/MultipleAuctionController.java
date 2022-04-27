package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.NettyControllable;
import com.nh.common.interfaces.UdpBillBoardStatusListener;
import com.nh.common.interfaces.UdpPdpBoardStatusListener;
import com.nh.controller.interfaces.AudioPlayListener;
import com.nh.controller.interfaces.BooleanListener;
import com.nh.controller.interfaces.MessageStringListener;
import com.nh.controller.interfaces.SelectEntryListener;
import com.nh.controller.interfaces.SettingListener;
import com.nh.controller.model.AucEntrData;
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
import com.nh.controller.utils.AuctionUtil;
import com.nh.controller.utils.AudioFilePlay;
import com.nh.controller.utils.AudioFilePlay.AudioPlayTypes;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.ListComparator;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.MoveStageUtil.EntryDialogType;
import com.nh.controller.utils.SharedPreference;
import com.nh.controller.utils.SoundUtil;
import com.nh.share.api.ActionResultListener;
import com.nh.share.api.models.CowInfoData;
import com.nh.share.api.request.body.RequestBidEntryBody;
import com.nh.share.api.request.body.RequestBidLogBody;
import com.nh.share.api.request.body.RequestBidNumBody;
import com.nh.share.api.request.body.RequestCowInfoBody;
import com.nh.share.api.request.body.RequestMultipleAuctionStatusBody;
import com.nh.share.api.response.ResponseBidEntry;
import com.nh.share.api.response.ResponseChangeCowInfo;
import com.nh.share.api.response.ResponseCowInfo;
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
import com.nh.share.controller.models.FinishAuction;
import com.nh.share.controller.models.InitEntryInfo;
import com.nh.share.controller.models.PauseAuction;
import com.nh.share.controller.models.RequestShowFailBidding;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.RequestAuctionResult;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.StandConnectInfo;
import com.nh.share.server.models.ToastMessage;
import com.nh.share.utils.SentryUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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

	@FXML // 대기중인 출품
	private TableColumn<SpEntryInfo, String> mWaitEntryNumColumn, mWaitExhibitorColumn, mWaitGenderColumn, mWaitMotherColumn, mWaitMatimeColumn, mWaitPasgQcnColumn, mWaitWeightColumn, mWaitLowPriceColumn, mWaitExSuccessPriceColumn, mWaitExSuccessfulBidderColumn, mWaitSuccessPriceColumn, mWaitSuccessfulBidderColumn, mWaitResultColumn, mWaitNoteColumn;

	@FXML // 현재 경매
	private Label mCurEntryNumLabel, mCurExhibitorLabel, mCurGenterLabel, mCurMotherLabel, mCurMatimeLabel, mCurPasgQcnLabel, mCurWeightLabel, mCurLowPriceLabel, mCurSuccessPriceLabel, mCurSuccessfulBidderLabel, mCurResultLabel, mCurNoteLabel;

	@FXML // 응찰자 정보
	private TableColumn<SpBidding, String> mBiddingPriceColumn, mBiddingUserColumn;

	@FXML // 사용자 접속 현황
	private TableColumn<SpBidderConnectInfo, String> mConnectionUserColumn_1, mConnectionUserColumn_2, mConnectionUserColumn_3, mConnectionUserColumn_4, mConnectionUserColumn_5;

	@FXML // 음성 멘트 버튼
	private Button mBtnIntroSound, mBtnBuyerSound, mBtnGuideSound, mBtnEtc_1_Sound, mBtnEtc_2_Sound, mBtnEtc_3_Sound, mBtnEtc_4_Sound, mBtnEtc_5_Sound, mBtnEtc_6_Sound;

	@FXML // 접속자 정보 수
	private Label mConnectionUserCntLabel;

	@FXML // 경매 정보
	private Label mAuctionInfoDateLabel, mAuctionInfoRoundLabel, mAuctionInfoGubunLabel, mAuctionInfoTotalCountLabel;

	@FXML // 경매 정보 - 상태
	private Label mAuctionStateReadyLabel, mAuctionStateProgressLabel, mAuctionStatePauseLabel, mAuctionStateFinishLabel, mAuctionStateLabel, mAuctionSecLabel;

	@FXML
	private GridPane mAuctionStateGridPane;

	@FXML // 상단 버튼
	private Button mHeaderBtnRefresh, mHeaderBtnStart, mHeaderBtnPause, mHeaderBtnFinish;

	@FXML // 하단 버튼
	private Button mBtnEsc, mBtnF1, mBtnF2,mBtnF5, mBtnF8, mBtnStart, mBtnPause, mBtnFinish, mBtnMessage, mBtnSendPending,mBtnSendPendingHide, mBtnQcnFinish;

	@FXML // 하단 메세지 전송 상위 뷰
	private StackPane mSTPMessage;

	@FXML // 하단 메세지 전송 텍스트
	private Label mMessageText;

	@FXML // 음성설정 ,저장 ,음성중지 ,낙찰결과
	private Button mBtnSettingSound, mBtnSave, mBtnStopSound, mBtnEntrySuccessList;

	@FXML
	private Label mColSuccessBidderLabel, mColSuccessPriceLabel;

	private ObservableList<SpEntryInfo> mWaitEntryInfoDataList = FXCollections.observableArrayList(); // 대기중 출품
	private ObservableList<SpBidding> mBiddingUserInfoDataList = FXCollections.observableArrayList(); // 응찰 현황
	private ObservableList<SpBidderConnectInfo> mConnectionUserDataList = FXCollections.observableArrayList(); // 접속자 현황
	private Map<String, BidderConnectInfo> mConnectionUserMap = new HashMap<>(); // 접속 현황

	private LinkedHashMap<String, SpBidding> mCurrentBidderMap = new LinkedHashMap<String, SpBidding>();; // 응찰자 정보 수집 Map

	private Image mResDisplayOn = new Image("/com/nh/controller/resource/images/ic_con_on.png"); // 전광판 On 이미지 리소스
	private Image mResDisplayOff = new Image("/com/nh/controller/resource/images/ic_con_off.png"); // 전광판 Off 이미지 리소스
	private FadeTransition mAnimationFadeIn; // 토스트 애니메이션 START
	private FadeTransition mAnimationFadeOut; // 토스트 애니메이션 END

	private String REFRESH_ENTRY_LIST_TYPE_NONE = "NONE"; // 출장우 정보 갱신 - 기본
	private String REFRESH_ENTRY_LIST_TYPE_SEND = "SEND"; // 출장우 정보 갱신 후 정보 보냄
	private String REFRESH_ENTRY_LIST_TYPE_START = "START"; // 출장우 정보 갱신 후 시작
	private String REFRESH_ENTRY_LIST_TYPE_REFRESH = "REFRESH"; // 출장우 정보 새로고침
	private String REFRESH_ENTRY_LIST_TYPE_SEND_START = "REFRESH_START"; // 출장우 정보 전송 후 시작

	private EntryDialogType mCurPageType; // 전체or보류목록 타입
	private int mRecordCount = 0; // cow total data count

	private boolean isShowToast = false; // 메세지 전송 상태 플래그
	private boolean isApplicationClosePopup = false; // 임의 종료시 server 연결 해제 팝업 노출 막는 플래그
	private Queue<String> mMsgQueue = new PriorityQueue(); // 메세지 전송 queue
	private ObservableList<SpEntryInfo> mDummyRow = FXCollections.observableArrayList(); // dummy row
	private int DUMMY_ROW_WAIT = 8;
	private SpEntryInfo mCurrentSpEntryInfo = null; // 현재 진행 출품
	private AuctionStatus mAuctionStatus = new AuctionStatus(); // 경매 상태

	private ExecutorService mExeOnBiddingService = null; // 응찰 서비스
	private Timer mStartAuctionSecScheduler = null; // 경매 시작 후 초 증가 타이머
	private int mStartAuctionSec = 0; // 경매 시작 후 초 증가 타이머 증가값

	private boolean isSendEntryData = false; // 출장우 데이터 전송 여부

	// 우클릭 새로고침 적용
	private ContextMenu mRefreshContextMenu = null;
	
	private boolean isQcnFinish = false;	//경매 회차 종료 버튼 눌렀을때 플래그
	
	private boolean isQcnChange = false; //경매 회차 변경
	
	private boolean isRgsqNoChange = false; //경매 구간 정보 변경
	
	private List<CowInfoData> mTmpCowDataList = null; //경매일 선택에서 조회된 출장우 데이터

	

	/**
	 * setStage
	 * 
	 * @param stage
	 */
	public void setStage(Stage stage) {
		mStage = stage;

		// 경매 데이터
		Thread thread = new Thread("cowInfo") {
			@Override
			public void run() {
				// 경매 데이터 set
				requestAuctionInfo();

				createUdpClient(mUdpBillBoardStatusListener1, mUdpBillBoardStatusListener2, mUdpPdpBoardStatusListener);

			}
		};

		thread.start();

		// 윈도우 x버튼 클릭시 팝업 노출되도록 변경 적용 2021.12.23 jspark
		if (mStage != null) {
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent evt) {
					Optional<ButtonType> btnResult = CommonUtils.getInstance().showAlertPopupTwoButton(mStage, mResMsg.getString("str.ask.application.close"), mResMsg.getString("popup.btn.ok"), mResMsg.getString("popup.btn.cancel"));

					if (btnResult.get().getButtonData() == ButtonData.LEFT) {
						isApplicationClosePopup = true;
						onServerAndClose();
						Platform.exit();
						System.exit(0);
					}
					
					evt.consume();
				}
			});
		}
	}
	
	/**
	 * 경매일 선택에서 조회된 출장우 데이터 set
	 * @param dataList
	 */
	public void setCowData(List<CowInfoData> dataList) {
		mTmpCowDataList = new ArrayList<CowInfoData>();
		mTmpCowDataList.addAll(dataList);
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
					mLogger.debug(mResMsg.getString("msg.billboard.send.init.info") + BillboardDelegate1.getInstance().initBillboard());
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
					mLogger.debug(mResMsg.getString("msg.billboard.send.init.info") + BillboardDelegate2.getInstance().initBillboard());
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
					mLogger.debug(mResMsg.getString("msg.pdp.send.init.info") + PdpDelegate.getInstance().initPdp());
				}

				mLogger.debug("PDP connection ip : " + SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT1, ""));
				mLogger.debug("PDP connection port : " + SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT1, ""));
				mLogger.debug("PDP connection status : " + PdpDelegate.getInstance().isActive());
			}

		} catch (Exception e) {
			mLogger.debug("[UDP 전광판 Exception] : " + e);
			SentryUtil.getInstance().sendExceptionLog(e);
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

		// 경매 출품 이관 전 상태로
		setAuctionStatus(GlobalDefineCode.AUCTION_STATUS_NONE);

		// 뷰 초기화
		initViewConfiguration();
		// 사운드 초기화
		SoundUtil.getInstance();
	}

	/**
	 * 기본 뷰 설정
	 */
	private void initViewConfiguration() {

		initTableConfiguration();

		mBtnEsc.setOnMouseClicked(event -> onCloseApplication());
		mBtnF1.setOnMouseClicked(event -> openEntryListPopUp());
		mBtnF2.setOnMouseClicked(event -> openEntryPendingListPopUp());
		mBtnF8.setOnMouseClicked(event -> openSettingDialog());
		mBtnStart.setOnMouseClicked(event -> onRefreshStartAuction());
		mBtnPause.setOnMouseClicked(event -> onPause());
		mBtnFinish.setOnMouseClicked(event -> onFinish());
		mHeaderBtnStart.setOnMouseClicked(event -> onRefreshStartAuction());
		mHeaderBtnPause.setOnMouseClicked(event -> onPause());
		mHeaderBtnFinish.setOnMouseClicked(event -> onFinish());
		mBtnMessage.setOnMouseClicked(event -> openSendMessage(event));
		
		mWaitTableView.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY){
                	onClickWaitTableView();
                }
            }
        });
		
		
		mBtnSettingSound.setOnMouseClicked(event -> openSettingSoundDialog(event));
		mBtnStopSound.setOnMouseClicked(event -> SoundUtil.getInstance().stopSound());
		mBtnEntrySuccessList.setOnMouseClicked(event -> openFinishedEntryListPopUp());

		mBtnF5.setOnMouseClicked(event -> {
			Platform.runLater(() -> CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.searching.entry.list")));
			onRefresh(REFRESH_ENTRY_LIST_TYPE_REFRESH);
		});

		mHeaderBtnRefresh.setOnMouseClicked(event -> {
			Platform.runLater(() -> CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.searching.entry.list")));
			onRefresh(REFRESH_ENTRY_LIST_TYPE_REFRESH);
		});

		mBtnSendPending.setOnMouseClicked(event -> onSendPendingList(true));
		mBtnSendPendingHide.setOnMouseClicked(event -> onSendPendingList(false));
		mBtnQcnFinish.setOnMouseClicked(event -> onSendQcnFinish());

		// 표시 숨김.
		mBtnIntroSound.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_INTRO));
		mBtnBuyerSound.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_BUYER));
		mBtnGuideSound.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_GUIDE));
		mBtnEtc_1_Sound.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_1));
		mBtnEtc_2_Sound.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_2));
		mBtnEtc_3_Sound.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_3));
		mBtnEtc_4_Sound.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_4));
		mBtnEtc_5_Sound.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_5));
		mBtnEtc_6_Sound.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_6));

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
		CommonUtils.getInstance().setAlignCenterCol(mWaitExSuccessPriceColumn);
		CommonUtils.getInstance().setAlignCenterCol(mWaitExSuccessfulBidderColumn);
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
		CommonUtils.getInstance().setNumberColumnFactory(mWaitExSuccessPriceColumn, true);
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

		mWaitExSuccessPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getExpAuctionBidPrice()); // 낙찰 예정가
		mWaitExSuccessfulBidderColumn.setCellValueFactory(cellData -> cellData.getValue().getExpAuctionSucBidder()); // 낙찰 예정자
		mWaitSuccessPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getSraSbidUpPrice()); // 낙찰가
		mWaitSuccessfulBidderColumn.setCellValueFactory(cellData -> cellData.getValue().getAuctionSucBidder()); // 낙찰자

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

		updateWaitColumnAuctionState(false);

		// 응찰 현황
		initBiddingInfoDataList();
		// 접속 현황
		initConnectionUserDataList();

		
		//미 응찰건 색상 적용
		mWaitTableView.setRowFactory(tbl -> new TableRow<SpEntryInfo>() {
			 @Override
		        protected void updateItem(SpEntryInfo item, boolean empty) {
		            super.updateItem(item, empty);
		            if(!empty) {
		            	
		            	if(GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PROGRESS)
		    					|| GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PAUSE)) { //경매 상태는 준비, 회차는 진행중
		            		
		            		if(CommonUtils.getInstance().isValidString(item.getExpAuctionBidPrice().getValue())) {
		            			if(Integer.parseInt(item.getExpAuctionBidPrice().getValue()) <= 0) {
		            				styleProperty().set("-fx-background-color: #eeeeee");
		            			}else {
		            				styleProperty().set("-fx-background-color: #ffffff");
		            			}
		            		}else {
	            				styleProperty().set("-fx-background-color: #eeeeee");
	            			} 
		            	}else {
            				styleProperty().set("-fx-background-color: #ffffff");
            			}
		            }
		        }

		});
	}
	
	/**
	 * 출장우 정보 param
	 * @return
	 */
	private RequestCowInfoBody getCowInfoParamBody() {
		final String naBzplc = GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc();
		final String aucObjDsc = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());
		final String aucDate = GlobalDefine.AUCTION_INFO.auctionRoundData.getAucDt();
		final String stnYn = SettingApplication.getInstance().getSettingAuctionTypeYn();
		final String rgSqno = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getRgSqNo());
		final String selStsDsc = "";
		
		// 출장우 데이터 조회
		return new RequestCowInfoBody(naBzplc, aucObjDsc, aucDate, selStsDsc, stnYn,rgSqno);
	}

	/**
	 * 경매 대기~진행 낙찰 예정가,예정자 표시 경매 완료 낙찰가,낙찰자 표시
	 * 
	 * @param isAucFinish
	 */
	private void updateWaitColumnAuctionState(boolean isAucFinish) {

		Platform.runLater(() -> {

			if (!isAucFinish) {
				mColSuccessPriceLabel.setText(mResMsg.getString("str.table.col.ing.price"));
				mColSuccessBidderLabel.setText(mResMsg.getString("str.table.col.ing.bidder"));
				mWaitExSuccessPriceColumn.setVisible(true); // 낙찰 예정가
				mWaitExSuccessfulBidderColumn.setVisible(true); // 낙찰 예정자
				mWaitSuccessPriceColumn.setVisible(false); // 경락가
				mWaitSuccessfulBidderColumn.setVisible(false); // 낙찰자
			} else {
				mColSuccessPriceLabel.setText(mResMsg.getString("str.table.col.finish.price"));
				mColSuccessBidderLabel.setText(mResMsg.getString("str.table.col.finish.bidder"));
				mWaitExSuccessPriceColumn.setVisible(false); // 낙찰 예정가
				mWaitExSuccessfulBidderColumn.setVisible(false); // 낙찰 예정자
				mWaitSuccessPriceColumn.setVisible(true); // 경락가
				mWaitSuccessfulBidderColumn.setVisible(true); // 낙찰자
			}
		});
	}

	/**
	 * 응찰자 초기값
	 */
	private void initBiddingInfoDataList() {
		Platform.runLater(() -> {
			mBiddingUserInfoDataList.clear();
			mBiddingUserInfoDataList.add(new SpBidding());
			mBiddingInfoTableView.setItems(mBiddingUserInfoDataList);
			mBiddingInfoTableView.getSelectionModel().select(0);
			// biddingInfoTableStyleToggle();
		});
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
	
		showLogAuctionInfo();
		
		
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

						if (!isSendEnterInfo() && !isQcnChange && !isRgsqNoChange) {
							// 출장우 전송
							onSendEntryData();							
						}else {
							syncAuctionInfo();							
						}

						
//						if (!isSendEnterInfo() && !isQcnChange && !isRgsqNoChange) {
//							mLogger.debug("[출장우 정보 전송]");							
//							// 출장우 전송
//							onSendEntryData();
//						} else {
//							mLogger.debug("[출장우 정보 전송된 상태]");		
//							syncAuctionInfo();
//						}
					}
				});
				pauseTransition.play();
			}

			// 우클릭 새로고침 적용
			if (mRefreshContextMenu == null) {

				MenuItem item1 = new MenuItem(mResMsg.getString("str.refresh"));

				item1.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						// 새로고침
						Platform.runLater(() -> CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.searching.entry.list")));
						onRefresh(REFRESH_ENTRY_LIST_TYPE_REFRESH);
					}
				});

				mRefreshContextMenu = new ContextMenu();
				mRefreshContextMenu.getItems().addAll(item1);

				mWaitTableView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
					@Override
					public void handle(ContextMenuEvent event) {
						// 우클릭 새로고침 show
						mRefreshContextMenu.show(mWaitTableView, event.getScreenX(), event.getSceneY());
					}
				});
			}
		}
	}

	
	private void syncAuctionInfo() {
		
		
		mWaitTableView.getSelectionModel().select(0);
	
		if(mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_READY)) {//경매 상태 준비
			
			if(GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PROGRESS)) { //경매 상태는 준비, 회차는 진행중

				mLogger.debug("[경매 상태는 준비, 회차는 진행중]");
				
				onAuctionStatusStart(false);

			}else if(GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PAUSE)) {//경매 상태는 준비, 회차는 정지중
				
				mLogger.debug("[경매 상태는 준비, 회차는 정지중]");
				
				onAuctionStatusStart(false);
				
				onAuctionStatusPause();
				// 경매 상태 버튼 제어
				auctionStateButtonToggle(mAuctionStatus.getState());
				// 상단 경매 진행 상태
				auctionStateLabelToggle(GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc());
			}else if(GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_FINISH)) { //경매 상태는 준비, 회차는 종료
				mLogger.debug("[경매 상태는 준비, 회차는 종료중]");
				onAuctionStatusStart(false);
				onAuctionStatusFinish(false);
			}
			
		}else if(mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) { //경매 상태 진행
			
			if(GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PAUSE)) { //경매 상태는 진행, 회차는 정지중
				onAuctionStatusPause();
				// 경매 상태 버튼 제어
				auctionStateButtonToggle(mAuctionStatus.getState());
				// 상단 경매 진행 상태
				auctionStateLabelToggle(GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc());
			}
			
		}
		
		Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
	}
	
	/**
	 * 응찰,접속자 ThreadPool 초기화
	 */
	private void initExecutorService() {
		if (mExeOnBiddingService == null) {
			mExeOnBiddingService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			mLogger.debug("[#응찰 ThreadPool init]");
		}
	}

	/**
	 * 응찰,접속자 ThreadPool 정지
	 */
	private void shutDownExecutorService() {
		if (mExeOnBiddingService != null && !mExeOnBiddingService.isShutdown()) {
			mExeOnBiddingService.shutdown();
			mLogger.debug("[#응찰 ThreadPool shutDown]");
		}
		mExeOnBiddingService = null;
	}

	/**
	 * 대기중인 테이블 뷰 클릭
	 *
	 * @param event
	 */
	public void onClickWaitTableView() {

		// 마우스 왼쪽 클릭시 출장우 정보 선택, 우클릭 등 그외 버튼 막음.
		// 우클릭 새로고침 창 숨김.
		if (mRefreshContextMenu != null && mRefreshContextMenu.isShowing()) {
			mRefreshContextMenu.hide();
		}

		setCurrentEntryInfo();

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
		});
	}
	
	/**
	 * 출장우 개체 총 수
	 * @param totalCount
	 */
	private void setCowTotalCount(int totalCount) {
		
		// 시작.진행시 응찰 받음.
//		if (!mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_START) && !mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
			mLogger.debug("[현재 출장우 총 수]=> " + totalCount);
			mAuctionInfoTotalCountLabel.setText(String.format(mResMsg.getString("str.total.cow.count"), totalCount));
//		}
	}
	
	/**
	 * 경매 출품 데이터
	 * 2022.01.13 경매일선택에서 조회된 출장우 데이터로 수정
	 */
	private void requestEntryData() {

		mCurPageType = EntryDialogType.ENTRY_LIST;
		
		if(!CommonUtils.getInstance().isListEmpty(mTmpCowDataList)) {
			Platform.runLater(() -> {
					mWaitEntryInfoDataList.clear();
					mWaitEntryInfoDataList = getParsingCowEntryDataList(mTmpCowDataList);
					initWaitEntryDataList(mWaitEntryInfoDataList);
					setCowTotalCount(mTmpCowDataList.size());
					mTmpCowDataList = null;
			});
		}else {
			Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
		}
	}

	/**
	 * 새로고침 버튼
	 */
	private void onRefresh(String type) {
		refreshWaitEntryDataList(true, type);
	}


	/**
	 * 일괄경매 시작 -> response 출장우 데이터로 갱신 
	 */
	private void onResponseCowInfoRefresh(List<CowInfoData> resDataList,String type) {
		clearRefreshWaitEntryDataList(type,resDataList);
	}

	/**
	 * 전체 보기
	 */
	public void openEntryListPopUp() {

		if (MoveStageUtil.getInstance().getDialog() != null && MoveStageUtil.getInstance().getDialog().isShowing()) {
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

		if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_START) || mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
			return;
		}

		openEntryDialog(EntryDialogType.ENTRY_PENDING_LIST);
	}

	/**
	 * 환경 설정
	 */
	public void openSettingDialog() {

		if (MoveStageUtil.getInstance().getDialog() != null && MoveStageUtil.getInstance().getDialog().isShowing()) {
			return;
		}

		MoveStageUtil.getInstance().openSettingDialog(mStage, true, new SettingListener() {

			@Override
			public void callBack(Boolean isSaved) {
				dismissShowingDialog();
			}

			@Override
			public void initServer() {
				dismissShowingDialog();
				mLogger.debug("[CLEAR INIT SERVER] " + AuctionDelegate.getInstance().onInitEntryInfo(new InitEntryInfo(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc(), Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getQcn()))));
				isApplicationClosePopup = true;
				onServerAndClose();
			}

		}, mUdpBillBoardStatusListener1, mUdpBillBoardStatusListener2, mUdpPdpBoardStatusListener);
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
				
				// 낙찰 결과보기는 이동,갱신 안 함
				if (type.equals(EntryDialogType.ENTRY_FINISH_LIST)) {
					return;
				}
				
				ApiUtils.getInstance().requestSelectCowInfo(getCowInfoParamBody(), new ActionResultListener<ResponseCowInfo>() {
					@Override
					public void onResponseResult(final ResponseCowInfo result) {
						
						try {
							
							
							if (result != null && result.getSuccess() && !CommonUtils.getInstance().isListEmpty(result.getData())) {
								
								mLogger.debug("[출장우 정보 조회 데이터 수] " + result.getData().size());
	
								List<CowInfoData> dataList = new ArrayList<CowInfoData>();
										
								dataList = result.getData();
								
								// 보류목록일경우
								if (type.equals(EntryDialogType.ENTRY_LIST)) {
									
									dataList = result.getData();
								
								}else {
									
									dataList = result.getData().stream()
								            .filter(c -> c.getSEL_STS_DSC().equals(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING) || c.getSEL_STS_DSC().equals(GlobalDefineCode.AUCTION_RESULT_CODE_READY))
								            .collect(Collectors.toList());
								}
								
								ObservableList<SpEntryInfo> newEntryDataList = getParsingCowEntryDataList(dataList);
								
								mLogger.debug("[현재 타입 mCurPageType] => " + mCurPageType);
						
	
								// 조회 데이터 없으면 리턴
								if (CommonUtils.getInstance().isListEmpty(newEntryDataList)) {
									mLogger.debug("조회 데이터 없음.");
									return;
								}
								
								Platform.runLater(() -> {
									
									mRecordCount = newEntryDataList.size();
									mWaitEntryInfoDataList.clear();
									mWaitEntryInfoDataList.addAll(newEntryDataList);
									setCowTotalCount(result.getData().size());
	
									for (int i = 0; DUMMY_ROW_WAIT > i; i++) {
										mWaitEntryInfoDataList.add(new SpEntryInfo());
									}
									
									mWaitTableView.refresh();
									//초기화 전송
									mLogger.debug("[CLEAR INIT SERVER] : " + AuctionDelegate.getInstance().onInitEntryInfo(new InitEntryInfo(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc(), Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getQcn()))));
								
									//출장우 정보 전송
									onCowInfoSendOrStartAuction(REFRESH_ENTRY_LIST_TYPE_SEND);
									
									if (index > -1) {
										selectIndexWaitTable(index, true);
									}else {
										selectIndexWaitTable(0, true);
									}
									
									
									CommonUtils.getInstance().dismissLoadingDialog();
								});
								
								mCurPageType = type;
							}
						
						}catch (Exception e) {
							e.printStackTrace();
							SentryUtil.getInstance().sendExceptionLog(e);
						}

					}
					
					@Override
					public void onResponseError(String message) {
						mLogger.debug("[onResponseError] 출장우 정보 " + message);
						Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
					}	
				});
				
			}
		});
	}

	
	/**
	 * 메세지 전송
	 *
	 * @param event
	 */
	public void openSendMessage(MouseEvent event) {

		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		mBtnMessage.setDisable(true);
		mMessageStage = MoveStageUtil.getInstance().loadMessageFXMLLoader(stage, new MessageStringListener() {
			@Override
			public void callBack(String str) {

				if (CommonUtils.getInstance().isValidString(str)) {
					AuctionDelegate.getInstance().onToastMessageRequest(str);
					mLogger.debug(String.format(mResMsg.getString("msg.auction.send.message"), str));
					mMsgQueue.offer(str);
					showToastMessage();
				}
			}

			@Override
			public void onClose() {
				mBtnMessage.setDisable(false);
				mMessageStage = null;
			}
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

			Thread refreshWaitThread = new Thread("refreshWaitEntryDataList") {
				@Override
				public void run() {
					// 보내기 전 한번 더 갱신
					refreshWaitEntryDataList(true, REFRESH_ENTRY_LIST_TYPE_SEND);
				}
			};

			refreshWaitThread.start();

			Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());

		} else {
			mLogger.debug(mResMsg.getString("msg.need.connection"));
			Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
		}
	}

	/**
	 * 전광판으로 유찰 대상 목록 표시 or 해제 전송
	 */
	public void onSendPendingList(boolean isShow) {
		
		if (MoveStageUtil.getInstance().getDialog() != null && MoveStageUtil.getInstance().getDialog().isShowing()) {
			return;
		}

		String naBzplc = GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc();
		String aucObjDsc = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());
		String aucDate = GlobalDefine.AUCTION_INFO.auctionRoundData.getAucDt();
		String rgSqNo = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getRgSqNo());
		
		String stAucNo = "";
		String edAucNo =  "";
		
		if(GlobalDefine.AUCTION_INFO.auctionRoundData.getStAucNo() > -1) {
			stAucNo = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getStAucNo());
		}
		
		if(GlobalDefine.AUCTION_INFO.auctionRoundData.getEdAucNo() > -1) {
			edAucNo = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getEdAucNo());
		}
		
		String showYn = "";
		
		if(isShow) {
			showYn = "Y"; //표시
		}else {
			showYn = "N"; //해제
		}

		RequestShowFailBidding requestShowFailBidding = new RequestShowFailBidding(naBzplc, aucDate, aucObjDsc, rgSqNo,showYn,stAucNo,edAucNo);
		AuctionDelegate.getInstance().sendMessage(requestShowFailBidding);
		
		mLogger.debug("[유찰대상목록 표시/해제]=> " + requestShowFailBidding.getEncodedMessage());
	}

	/**
	 * 경매 회차 종료 처리
	 */
	public void onSendQcnFinish() {
			
		Platform.runLater(() -> {
			
			Optional<ButtonType> btnResult = CommonUtils.getInstance().showAlertPopupTwoButton(mStage, mResMsg.getString("msg.auction.finish.qcn"), mResMsg.getString("popup.btn.ok"), mResMsg.getString("popup.btn.cancel"));

			if (btnResult.get().getButtonData() == ButtonData.LEFT) {
				isQcnFinish = true;
				isApplicationClosePopup = true;
				FinishAuction finishAuction = new FinishAuction(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc());
				mLogger.debug("[겸매회차 종료]=>  " + AuctionDelegate.getInstance().sendMessage(finishAuction));
				onServerAndClose();
			}
		});
	}

	/**
	 * 대기중인 출품 목록 갱신 변경/추가된 데이터 서버 전달
	 */
	private void clearRefreshWaitEntryDataList(String type , List<CowInfoData> resDataList) {

		if(!CommonUtils.getInstance().isListEmpty(resDataList)) {

			mLogger.debug("[출장우 정보 조회 데이터 수] " + resDataList.size());

			List<CowInfoData> dataList = new ArrayList<CowInfoData>();
			
			dataList.addAll(resDataList);

			ObservableList<SpEntryInfo> newEntryDataList = getParsingCowEntryDataList(dataList);

			mLogger.debug("[현재 타입 mCurPageType] => " + mCurPageType);
		
			// 조회 데이터 없으면 리턴
			if (CommonUtils.getInstance().isListEmpty(newEntryDataList)) {
				mLogger.debug("조회 데이터 없음.");
				return;
			}

			mLogger.debug("[CLEAR INIT SERVER] : " + AuctionDelegate.getInstance().onInitEntryInfo(new InitEntryInfo(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc(), Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getQcn()))));
			
			mRecordCount = newEntryDataList.size();
			mWaitEntryInfoDataList.clear();
			mWaitEntryInfoDataList.addAll(newEntryDataList);

			for (int i = 0; DUMMY_ROW_WAIT > i; i++) {
				mWaitEntryInfoDataList.add(new SpEntryInfo());
			}

			Platform.runLater(() -> {
		
				mWaitTableView.refresh();
				selectIndexWaitTable(0,false);
				setCowTotalCount(resDataList.size());
				
			});
			

			for (SpEntryInfo spEntryInfo : newEntryDataList) {
				mLogger.debug("추가된 데이터 전송=> " + AuctionDelegate.getInstance().onSendEntryData(spEntryInfo));
			}
		}
	}

	
	/**
	 * 대기중인 출품 목록 갱신 변경/추가된 데이터 서버 전달
	 */
	private void refreshWaitEntryDataList(boolean isRefresh, String type) {

		ApiUtils.getInstance().requestSelectCowInfo(getCowInfoParamBody(), new ActionResultListener<ResponseCowInfo>() {
			@Override
			public void onResponseResult(final ResponseCowInfo result) {

				try {
					
					
					if (result != null && result.getSuccess() && !CommonUtils.getInstance().isListEmpty(result.getData())) {
						
						mLogger.debug("[출장우 정보 조회 데이터 수] " + result.getData().size());
	
						List<CowInfoData> dataList = new ArrayList<CowInfoData>();
						
						dataList.addAll(result.getData());
	
						ObservableList<SpEntryInfo> newEntryDataList = getParsingCowEntryDataList(dataList);
	
						mLogger.debug("[현재 타입 mCurPageType] => " + mCurPageType);
					
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
	
									if ((newDt > curDt) || type.equals(REFRESH_ENTRY_LIST_TYPE_REFRESH)) {
	
										mWaitEntryInfoDataList.set(i, newEntryDataList.get(j));
	
										// 출품정보 전송 후 변경된 사항 전달.
										if (isSendEnterInfo()) {
	
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
							
							ObservableList<SpEntryInfo> newDataList = null;
							
							// 보류목록일경우
							if (mCurPageType.equals(EntryDialogType.ENTRY_LIST)) {
							
								newDataList = newEntryDataList.stream()
										.filter(e -> !mWaitEntryInfoDataList.contains(e))
										.collect(Collectors.toCollection(FXCollections::observableArrayList));
								
							}else {
								
								newDataList = newEntryDataList.stream()
										.filter(e -> !mWaitEntryInfoDataList.contains(e))
										.filter(c -> c.getAuctionResult().getValue().equals(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING) || c.getAuctionResult().getValue().equals(GlobalDefineCode.AUCTION_RESULT_CODE_READY))
										.collect(Collectors.toCollection(FXCollections::observableArrayList));
							}
							
							// 추가된 데이터 항목이 있으면 add
							if (!CommonUtils.getInstance().isListEmpty(newDataList)) {
	
								mLogger.debug("추가된 데이터 있음.");
	
								for (SpEntryInfo spEntryInfo : newDataList) {
									mLogger.debug("추가된 데이터 전송=> " + AuctionDelegate.getInstance().onSendEntryData(spEntryInfo));
								}
								
								ObservableList<SpEntryInfo> tmpDataList  = mWaitEntryInfoDataList.stream()
										.filter(c -> c.getEntryNumberValue() > -1)
										.collect(Collectors.toCollection(FXCollections::observableArrayList));
								
								tmpDataList.addAll(newDataList);

								Comparator<SpEntryInfo> comparator = Comparator.comparing(SpEntryInfo::getEntryNumberValue);
								
								tmpDataList = tmpDataList.parallelStream().sorted(comparator).collect(Collectors.toCollection(FXCollections::observableArrayList));

								mRecordCount = tmpDataList.size();
								mWaitEntryInfoDataList.clear();
								mWaitEntryInfoDataList.addAll(tmpDataList);
								
								for (int i = 0; DUMMY_ROW_WAIT > i; i++) {
									mWaitEntryInfoDataList.add(new SpEntryInfo());
								}

							} else {
								mLogger.debug("추기된 데이터 없음.");
							}
	//						mWaitTableView.refresh();
	
						}
	
						Platform.runLater(() -> {
							mWaitTableView.refresh();
							setCowTotalCount(result.getData().size());
							//2022-04-26 새로고침 후 0번째 선택 기본 적용
							selectIndexWaitTable(0, false);
						});
	
						PauseTransition pauseTransition = new PauseTransition(Duration.millis(200));
						pauseTransition.setOnFinished(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								onCowInfoSendOrStartAuction(type);
							}
						});
						pauseTransition.play();
					}else {
						Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
					}
				
				}catch (Exception e) {
					e.printStackTrace();
					SentryUtil.getInstance().sendExceptionLog(e);
				}
			}

			@Override
			public void onResponseError(String message) {
				mLogger.debug("[onResponseError] 출장우 정보 " + message);
				onCowInfoSendOrStartAuction(type);
				Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
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
		if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_NONE) || !isSendEntryData) {
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
			Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("msg.auction.send.need.entry.data")));
			return;
		}

		Platform.runLater(() -> CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.multi.auction.start")));
		
		// 경매시
		onStartAuction(GlobalDefine.AUCTION_INFO.MULTIPLE_AUCTION_STATUS_START);

	}

	/**
	 * 일괄 경매 시작
	 */
	private void onStartAuction(String type) {

		// 출품 이관 체크
		final String naBzplc = GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc();
		final String aucObjDsc = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());
		final String aucDate = GlobalDefine.AUCTION_INFO.auctionRoundData.getAucDt();
		final String rgSqNo = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getRgSqNo());

		// 시작 서버로 Start 보냄.
		mLogger.debug("경매 Status 전송 : " + type);

		RequestMultipleAuctionStatusBody body = new RequestMultipleAuctionStatusBody(naBzplc, aucObjDsc, aucDate, type, rgSqNo);
		ApiUtils.getInstance().requestMultipleAuctionStatus(body, new ActionResultListener<ResponseChangeCowInfo>() {

			@Override
			public void onResponseResult(ResponseChangeCowInfo result) {

				if (result != null && result.getSuccess()) {

					mLogger.debug("[일괄경매 Status Success] : " + type);

					if (type.equals(GlobalDefine.AUCTION_INFO.MULTIPLE_AUCTION_STATUS_START)) {

						if(!CommonUtils.getInstance().isListEmpty(result.getData())) {
							GlobalDefine.AUCTION_INFO.auctionRoundData.setSelStsDsc(GlobalDefineCode.STN_AUCTION_STATUS_PROGRESS);
							onResponseCowInfoRefresh(result.getData(),REFRESH_ENTRY_LIST_TYPE_REFRESH);
							onAuctionStatusStart(Integer.toString(result.getData().get(0).getAUC_PRG_SQ()));
						}

					} else if (type.equals(GlobalDefine.AUCTION_INFO.MULTIPLE_AUCTION_STATUS_PAUSE)) {

						GlobalDefine.AUCTION_INFO.auctionRoundData.setSelStsDsc(GlobalDefineCode.STN_AUCTION_STATUS_PAUSE);

//						if(!CommonUtils.getInstance().isListEmpty(result.getData())) {
//							onResponseCowInfoRefresh(result.getData(),REFRESH_ENTRY_LIST_TYPE_REFRESH);
//						}
//						
						onRefresh(REFRESH_ENTRY_LIST_TYPE_REFRESH);

						onAuctionStatusPause();

						// 경매 상태 버튼 제어
						auctionStateButtonToggle(mAuctionStatus.getState());
						// 상단 경매 진행 상태
						auctionStateLabelToggle(GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc());

					} else if (type.equals(GlobalDefine.AUCTION_INFO.MULTIPLE_AUCTION_STATUS_FINISH)) {

						GlobalDefine.AUCTION_INFO.auctionRoundData.setSelStsDsc(GlobalDefineCode.STN_AUCTION_STATUS_FINISH);

//						if(!CommonUtils.getInstance().isListEmpty(result.getData())) {
//							onResponseCowInfoRefresh(result.getData(),REFRESH_ENTRY_LIST_TYPE_REFRESH);
//						}
						
						onRefresh(REFRESH_ENTRY_LIST_TYPE_REFRESH);

						onAuctionStatusFinish(true);
					}

					// 경매 상태 버튼 제어
//					auctionStateButtonToggle(mAuctionStatus.getState());
					// 상단 경매 진행 상태
//					auctionStateLabelToggle(GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc());

				} else {

					if (type.equals(GlobalDefine.AUCTION_INFO.MULTIPLE_AUCTION_STATUS_START) && mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_READY)) {
						mLogger.debug("[DB START+경매서버가 대기(8002)인 상황에서 경매시작 누른경우. 일괄경매 시작!! ]");
						GlobalDefine.AUCTION_INFO.auctionRoundData.setSelStsDsc(GlobalDefineCode.STN_AUCTION_STATUS_PROGRESS);
						onAuctionStatusStart(true);
					} else {
						mLogger.debug("[일괄경매 시작 False]");

						Platform.runLater(() -> {
							CommonUtils.getInstance().dismissLoadingDialog();
							showAlertPopupOneButton(result.getMessage());
						});
					}

				}
			}

			@Override
			public void onResponseError(String message) {
				Platform.runLater(() -> {
					CommonUtils.getInstance().dismissLoadingDialog();
					mLogger.debug("[일괄경매 시작 onResponseError ]");
					showAlertPopupOneButton(message);
				});
			}
		});
	}

	private void onPause() {
		Platform.runLater(() -> CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.multi.auction.pause")));
		onStartAuction(GlobalDefine.AUCTION_INFO.MULTIPLE_AUCTION_STATUS_PAUSE);
	}

	private void onFinish() {
		Platform.runLater(() -> CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.multi.auction.finish")));
		onStartAuction(GlobalDefine.AUCTION_INFO.MULTIPLE_AUCTION_STATUS_FINISH);
	}
	
	private String getCurrentEntryNumber() {
		
		String entryNum = "0";
		
		if(mWaitTableView.getItems().size() > 0) {
			
			
			if(mWaitTableView.getSelectionModel().getSelectedItem() != null) {
				entryNum = mWaitTableView.getSelectionModel().getSelectedItem().getEntryNum().getValue();
			}else {
				entryNum = mWaitTableView.getItems().get(0).getEntryNum().getValue();
			}

		}
		
		return entryNum;
	}

	/**
	 * 경매 시작 시작버튼 -> request api result -> 서버 전송
	 */
	private void onAuctionStatusStart(String entryNumber) {

		// 시작 로그 msg
		String msgStart = String.format(mResMsg.getString("msg.auction.send.start"), entryNumber);

		//시작 전 보냄
		onSendPendingList(false);
		
		// 시작 서버로 Start 보냄.
		mLogger.debug(msgStart + AuctionDelegate.getInstance().onStartAuction(entryNumber));
			// 시작음
			playLocalSound(AudioPlayTypes.START);
		
	}
	
	/**
	 * 경매 시작 시작버튼 -> request api result -> 서버 전송
	 */
	private void onAuctionStatusStart(boolean isPlaySound) {

		// 시작 로그 msg
		String msgStart = String.format(mResMsg.getString("msg.auction.send.start"), getCurrentEntryNumber());

		// 시작 서버로 Start 보냄.
		mLogger.debug(msgStart + AuctionDelegate.getInstance().onStartAuction(getCurrentEntryNumber()));

		if(isPlaySound) {
			// 시작음
			playLocalSound(AudioPlayTypes.START);
		}
		
	}
	

	/**
	 * 경매 정지 정지 버튼 -> request api result -> 서버 전송
	 */
	private void onAuctionStatusPause() {
		
		mLogger.debug("카운트 다운 정지 : " + AuctionDelegate.getInstance().onPause(new PauseAuction(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc(), getCurrentEntryNumber())));
	}

	/**
	 * 경매 종료 정지 버튼 -> request api result -> 서버 전송
	 */
	private void onAuctionStatusFinish(boolean isPlaySound) {
		stopAllSound();
		stopStartAuctionSecScheduler();
		mLogger.debug(mResMsg.getString("msg.auction.send.complete") + AuctionDelegate.getInstance().onStopAuction(getCurrentEntryNumber(), 0));
		
		if(isPlaySound) {
			// 종료음
			playLocalSound(AudioPlayTypes.FINISH);
		}

	}

	private void playLocalSound(AudioPlayTypes finish) {

		AudioFilePlay.getInstance().setTargetPlay(this.getClass().getResource(AudioPlayTypes.which(finish)).toExternalForm(), new AudioPlayListener() {
			@Override
			public void onPlayReady(AudioFilePlay audioFilePlay, MediaPlayer mediaPlayer) {
				mLogger.info("TTS 재생이 준비되었습니다.");
				mLogger.info("TTS 재생 시간 : " + AudioFilePlay.getInstance().getPlayDuration());
				AudioFilePlay.getInstance().playSound();
			}

			@Override
			public void onPlayCompleted() {
			}
		});
	}

	/**
	 * 출장우 전송 & 경매 시작.
	 * 
	 * @param type
	 */
	private void onCowInfoSendOrStartAuction(String type) {

		if (type.equals(REFRESH_ENTRY_LIST_TYPE_SEND)) {

			Thread thread = new Thread("onSendEntryData") {
				@Override
				public void run() {
					
					try {

					mLogger.debug("start onSendEntryData thread");

					int count = 0;

					for (SpEntryInfo entryInfo : mWaitEntryInfoDataList) {
						if (!CommonUtils.getInstance().isEmptyProperty(entryInfo.getEntryNum())) {
							mLogger.debug(mResMsg.getString("msg.auction.send.entry.data") + AuctionDelegate.getInstance().onSendEntryData(entryInfo));
							count++;
						}
					}

					mLogger.debug(String.format(mResMsg.getString("msg.send.entry.data.result"), count));

					isSendEntryData = true;

					Platform.runLater(() -> {
						CommonUtils.getInstance().dismissLoadingDialog();
					});

					}catch (Exception e) {
						e.printStackTrace();
						SentryUtil.getInstance().sendExceptionLog(e);
					}
				}
			};

			thread.start();

		} else if (type.equals(REFRESH_ENTRY_LIST_TYPE_START)) {
			// 경매시
			onStartAuction(GlobalDefine.AUCTION_INFO.MULTIPLE_AUCTION_STATUS_START);
		} else {
			Platform.runLater(() -> {
				CommonUtils.getInstance().dismissLoadingDialog();
			});
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

		mLogger.debug("[경매 상태 정보] " + auctionStatus.getEncodedMessage());
		
		// 회차 종료 플래그.
		if(isQcnFinish) {
			return;
		}

		mLogger.debug("[회차정보 확인. 현재 거점 : " + GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc()  + " /  :  " +  auctionStatus.getAuctionHouseCode());
		mLogger.debug("[회차정보 확인. 현재 qcn : " + GlobalDefine.AUCTION_INFO.auctionRoundData.getQcn()  + " /  :  " +  Integer.parseInt(auctionStatus.getAuctionQcn()));
		mLogger.debug("[회차정보 확인. 현재 rgSqno : " + GlobalDefine.AUCTION_INFO.auctionRoundData.getRgSqNo()  + " /  :  " +  Integer.parseInt(auctionStatus.getExpAuctionIntNum()));
		
		// 회차 정보 변경. 서버 데이터 초기화 ,출장우 정보 다시 보냄
		if ((GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc().equals(auctionStatus.getAuctionHouseCode()))
				&& (GlobalDefine.AUCTION_INFO.auctionRoundData.getQcn() != Integer.parseInt(auctionStatus.getAuctionQcn()))
			|| auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {
			
			isQcnChange = true;
			
			showChangeQcnPopup(mResMsg.getString("dialog.change.qcn"),auctionStatus);
			
			return;
		}
		
		// 구간 정보 변경. 서버 데이터 초기화 ,출장우 정보 다시 보냄
		if(GlobalDefine.AUCTION_INFO.auctionRoundData.getRgSqNo() != Integer.parseInt(auctionStatus.getExpAuctionIntNum()) ) {
		
			isRgsqNoChange = true;
			
			showChangeQcnPopup(mResMsg.getString("dialog.change.rgsqno"),auctionStatus);
			
			return;
		}

		mAuctionStatus = auctionStatus;

		if (!mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
			// 출장우 정보 보냄 플래그
			isSendEntryData = true;
		}

		onSendUpdServerState();

		// 출장우 컬럼 표시
		if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PASS) || mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_COMPLETED) || mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {
			updateWaitColumnAuctionState(true);
		} else {
			updateWaitColumnAuctionState(false);
		}

		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_READY:
			
			
			mLogger.debug("[회차 변경값 확인 isQcnChange : " + isQcnChange + " / isRgsqNoChange :  " +  isRgsqNoChange);
			
			if(isQcnChange || isRgsqNoChange) {
				
				isQcnChange = false;
				isRgsqNoChange = false;
				

				//1.경매 서버는 진행 상태.
				if (GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PROGRESS)) { // 회차가 진행상태일때
					//서버로 시작 보냄
					onAuctionStatusStart(false);
					
				}else if (GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PAUSE)) { //회차가 정지상태일때
					//서버로 시작 후 정지 보냄
					onAuctionStatusStart(false);
					onAuctionStatusPause();

				} else if (GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_FINISH)) {//회차가 종료상태일때
					//서버로 시작 후 종료 보냄
					onAuctionStatusStart(false);
					onAuctionStatusFinish(false);
				}
			}
			
			// 경매 시작 ~ 경과시간 초
			mStartAuctionSec = 0;
			break;
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:

			// 경매 경과 시간 타이머 시작
			startAuctionSecScheduler();

			break;
		case GlobalDefineCode.AUCTION_STATUS_PASS:
		case GlobalDefineCode.AUCTION_STATUS_COMPLETED:

			// 경매 경과 시간 타이머 종료
			stopStartAuctionSecScheduler();

			break;
		}

		// 경매 상태 버튼 제어
		auctionStateButtonToggle(mAuctionStatus.getState());
		// 상단 경매 진행 상태
		auctionStateLabelToggle(mAuctionStatus.getState());

		Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
	}

	
	/**
	 * 회차, 구간 정보 다를경우 팝업 노출
	 * left : 출장우 정보 다시 전송 , right : 종료 후 경매일 선택 이동.
	 * @param auctionStatus
	 */
	private void showChangeQcnPopup(String msg, AuctionStatus auctionStatus) {
		
		Platform.runLater(() -> {

			Optional<ButtonType> btnResult = CommonUtils.getInstance().showAlertPopupTwoButton(mStage, msg, mResMsg.getString("popup.btn.ok"), mResMsg.getString("popup.btn.exit"));

			if (btnResult.get().getButtonData() == ButtonData.LEFT) {
				mLogger.debug("[회차 or 구간정보 변경. 초기화 후 출장우 재전송]");
				isSendEntryData = false;
//				isQcnChange = false;
//				isRgsqNoChange = false;
				mAuctionStatus.setState(GlobalDefineCode.AUCTION_STATUS_NONE);
				mLogger.debug("[CLEAR INIT SERVER] " + AuctionDelegate.getInstance().onInitEntryInfo(new InitEntryInfo(auctionStatus.getAuctionHouseCode(), auctionStatus.getAuctionQcn())));
				//출장우 정보 전송
				onCowInfoSendOrStartAuction(REFRESH_ENTRY_LIST_TYPE_SEND);
				
			} else {
				isApplicationClosePopup = true;
				onServerAndClose();
			}

		});
	}
	
	/**
	 * 전광판으로 경매 상태 전송
	 */
	private void onSendUpdServerState() {

		try {

			switch (mAuctionStatus.getState()) {
			case GlobalDefineCode.AUCTION_STATUS_NONE:
				mLogger.debug(mResMsg.getString("msg.auction.status.none"));
				break;
			case GlobalDefineCode.AUCTION_STATUS_READY:
				mLogger.debug(String.format(mResMsg.getString("msg.auction.status.ready"), mAuctionStatus.getEntryNum()));
				break;
			case GlobalDefineCode.AUCTION_STATUS_START:

				if (!BillboardDelegate1.getInstance().isEmptyClient() && BillboardDelegate1.getInstance().isActive()) {

					// UDP 통신
					BillboardData billboardData = new BillboardData();
					billboardData.setbEntryNum(String.valueOf(mCurrentSpEntryInfo.getEntryNum().getValue()));
					billboardData.setbExhibitor(String.valueOf(mCurrentSpEntryInfo.getExhibitor().getValue()));
					billboardData.setbWeight(String.valueOf(mCurrentSpEntryInfo.getWeight().getValue()));
					billboardData.setbGender(String.valueOf(mCurrentSpEntryInfo.getGender().getValue()));
					billboardData.setbMotherTypeCode(String.valueOf(mCurrentSpEntryInfo.getMotherTypeCode().getValue()));
					billboardData.setbPasgQcn(String.valueOf(mCurrentSpEntryInfo.getPasgQcn().getValue()));
					billboardData.setbMatime(String.valueOf(mCurrentSpEntryInfo.getMatime().getValue()));
					billboardData.setbKpn(String.valueOf(mCurrentSpEntryInfo.getKpn().getValue()));
					billboardData.setbRegion(String.valueOf(mCurrentSpEntryInfo.getRgnName().getValue()));
					billboardData.setbNote(String.valueOf(mCurrentSpEntryInfo.getNote().getValue()));
					billboardData.setbLowPrice(String.valueOf(mCurrentSpEntryInfo.getLowPrice().getValue()));
					billboardData.setbDnaYn(String.valueOf(mCurrentSpEntryInfo.getDnaYn().getValue()));

					BillboardDelegate1.getInstance().sendBillboardData(billboardData);
					BillboardDelegate1.getInstance().sendBillboardNote(billboardData.getbNote());
					BillboardDelegate1.getInstance().startBillboard();
					mLogger.debug(mResMsg.getString("msg.billboard.send.current.entry.data") + billboardData.getEncodedMessage());
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
					mLogger.debug(mResMsg.getString("msg.pdp.send.current.entry.data") + pdpData.getEncodedMessage());
				}

				mLogger.debug(String.format(mResMsg.getString("msg.auction.status.start"), mAuctionStatus.getEntryNum()));

				// 시작 로그 저장
//				if (!GlobalDefineCode.FLAG_TEST_MODE_BIDDING_LOG) {
//					AucEntrData aucEntrData = getCurrentBaseEntrData(true);
//					aucEntrData.setRgSqno(GlobalDefine.AUCTION_INFO.LOG_AUCTION_START);
//					aucEntrData.setRmkCntn(mResMsg.getString("str.auction.start"));
//					requestInsertBiddingHistory(aucEntrData, GlobalDefine.AUCTION_INFO.BID_LOG_TYPE_START);
//				}

				break;
			case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
				mLogger.debug(String.format(mResMsg.getString("msg.auction.status.progress"), mAuctionStatus.getEntryNum()));
				break;
			case GlobalDefineCode.AUCTION_STATUS_PASS:
			case GlobalDefineCode.AUCTION_STATUS_COMPLETED:

				if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PASS)) {
					mLogger.debug(String.format(mResMsg.getString("msg.auction.status.pass"), mAuctionStatus.getEntryNum()));
					BillboardDelegate1.getInstance().completeBillboard();
					PdpDelegate.getInstance().completePdp();
				} else if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_COMPLETED)) {
					mLogger.debug(String.format(mResMsg.getString("msg.auction.status.completed"), mAuctionStatus.getEntryNum()));
					BillboardDelegate1.getInstance().completeBillboard();
					PdpDelegate.getInstance().completePdp();
				}

				shutDownExecutorService();

//				if (!GlobalDefineCode.FLAG_TEST_MODE_BIDDING_LOG) {
//					AucEntrData aucEntrData = getCurrentBaseEntrData(true);
//					aucEntrData.setRgSqno(GlobalDefine.AUCTION_INFO.LOG_AUCTION_FINISH);
//					aucEntrData.setRmkCntn(mResMsg.getString("str.auction.finish"));
//					requestInsertBiddingHistory(aucEntrData, GlobalDefine.AUCTION_INFO.BID_LOG_TYPE_FINISH);
//				}

				break;
			case GlobalDefineCode.AUCTION_STATUS_FINISH:
				mLogger.debug(mResMsg.getString("msg.auction.status.finish"));
				BillboardDelegate1.getInstance().finishBillboard();
				PdpDelegate.getInstance().finishPdp();
				shutDownExecutorService();
				break;
			}

		} catch (Exception e) {
			mLogger.debug("[onAuctionStatus Send Udp Server Exception] " + e);
			SentryUtil.getInstance().sendExceptionLog(e);
		}

	}

	/**
	 * 경매 상태 버튼 제어
	 * 
	 * @param state
	 */
	private void auctionStateButtonToggle(String state) {

		switch (state) {
		case GlobalDefineCode.AUCTION_STATUS_READY:

	
			mBtnF1.setDisable(false);
			mBtnF2.setDisable(false);
			mBtnF8.setDisable(false);

			// 경매시작
			mBtnStart.setDisable(false);
			mHeaderBtnStart.setDisable(false);
			// 경매정지
			mBtnPause.setDisable(true);
			mHeaderBtnPause.setDisable(true);
			// 경매종료
			mBtnFinish.setDisable(true);
			mHeaderBtnFinish.setDisable(true);
				
			
			break;
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
			
			mBtnF1.setDisable(true);
			mBtnF2.setDisable(true);
			mBtnF8.setDisable(true);
			
			//1.경매 서버는 진행 상태.
			if (GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PROGRESS)) { // 회차가 진행상태일때
				
				//시작버튼 
				mBtnStart.setDisable(true);
				mHeaderBtnStart.setDisable(true);
				//정지버튼
				mBtnPause.setDisable(false);
				mHeaderBtnPause.setDisable(false);
				//종료 버튼
				mBtnFinish.setDisable(false);
				mHeaderBtnFinish.setDisable(false);
				
				
			}else if (GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PAUSE)) { //회차가 정지상태일때
				
				//시작버튼 
				mBtnStart.setDisable(false);
				mHeaderBtnStart.setDisable(false);
				//정지버튼
				mBtnPause.setDisable(true);
				mHeaderBtnPause.setDisable(true);
				//종료 버튼
				mBtnFinish.setDisable(false);
				mHeaderBtnFinish.setDisable(false);

			} else if (GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_FINISH)) {//회차가 종료상태일때
			
				//시작버튼 
				mBtnStart.setDisable(false);
				mHeaderBtnStart.setDisable(false);
				//정지버튼
				mBtnPause.setDisable(true);
				mHeaderBtnPause.setDisable(true);
				//종료 버튼
				mBtnFinish.setDisable(true);
				mHeaderBtnFinish.setDisable(true);
				
			}
			

			break;
		case GlobalDefineCode.AUCTION_STATUS_PASS:
		case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
			
			mBtnF1.setDisable(false);
			mBtnF2.setDisable(false);
			mBtnF8.setDisable(false);
			
			//1.경매 서버는 종료상태.
			if (GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PROGRESS)) { // 회차가 진행상태일때
				
				//시작버튼 
				mBtnStart.setDisable(true);
				mHeaderBtnStart.setDisable(true);
				//정지버튼
				mBtnPause.setDisable(false);
				mHeaderBtnPause.setDisable(false);
				//종료 버튼
				mBtnFinish.setDisable(false);
				mHeaderBtnFinish.setDisable(false);
				
			}else if (GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PAUSE)) { //회차가 정지상태일때
				
				//시작버튼 
				mBtnStart.setDisable(false);
				mHeaderBtnStart.setDisable(false);
				//정지버튼
				mBtnPause.setDisable(true);
				mHeaderBtnPause.setDisable(true);
				//종료 버튼
				mBtnFinish.setDisable(false);
				mHeaderBtnFinish.setDisable(false);
				
			} else if (GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_FINISH)) {//회차가 종료상태일때
		
				//시작버튼 
				mBtnStart.setDisable(false);
				mHeaderBtnStart.setDisable(false);
				//정지버튼
				mBtnPause.setDisable(true);
				mHeaderBtnPause.setDisable(true);
				//종료 버튼
				mBtnFinish.setDisable(true);
				mHeaderBtnFinish.setDisable(true);
				
			}

			break;
		}

	}

	/**
	 * 상단 경매 진행 상태
	 *
	 * @param state
	 */
	private void auctionStateLabelToggle(String state) {

		Platform.runLater(() -> {
			switch (state) {

			case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
			case GlobalDefineCode.STN_AUCTION_STATUS_PROGRESS:
				// 대기 라벨 비활성화
				mAuctionStateReadyLabel.setDisable(true);
				// 시작 라벨 비활성화
				mAuctionStateProgressLabel.setDisable(false);
				// 정지 라벨 비활성화
				mAuctionStatePauseLabel.setDisable(true);
				// 종료 라벨 비활성화
				mAuctionStateFinishLabel.setDisable(true);
				// 경매 상태 라벨
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.start"));
				// 경매 상태 배경 색상
				mAuctionStateGridPane.getStyleClass().clear();
				CommonUtils.getInstance().addStyleClass(mAuctionStateGridPane, "bg-color-04cf5c");
				break;
			case GlobalDefineCode.STN_AUCTION_STATUS_PAUSE:
				// 대기 라벨 비활성화
				mAuctionStateReadyLabel.setDisable(true);
				// 시작 라벨 비활성화
				mAuctionStateProgressLabel.setDisable(true);
				// 정지 라벨 비활성화
				mAuctionStatePauseLabel.setDisable(false);
				// 종료 라벨 비활성화
				mAuctionStateFinishLabel.setDisable(true);
				// 경매 상태 라벨
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.pause"));
				mAuctionStateGridPane.getStyleClass().clear();
				CommonUtils.getInstance().addStyleClass(mAuctionStateGridPane, "bg-color-008fff");
				break;
			case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
			case GlobalDefineCode.STN_AUCTION_STATUS_FINISH:
				// 대기 라벨 비활성화
				mAuctionStateReadyLabel.setDisable(true);
				// 시작 라벨 비활성화
				mAuctionStateProgressLabel.setDisable(true);
				// 정지 라벨 비활성화
				mAuctionStatePauseLabel.setDisable(true);
				// 종료 라벨 비활성화
				mAuctionStateFinishLabel.setDisable(false);
				// 경매 상태 라벨
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.finish"));
				// 경매 상태 배경 색상
				mAuctionStateGridPane.getStyleClass().clear();
				CommonUtils.getInstance().addStyleClass(mAuctionStateGridPane, "bg-color-ea0030");
				break;
			default:
				// 대기 라벨 비활성화
				mAuctionStateReadyLabel.setDisable(false);
				// 시작 라벨 비활성화
				mAuctionStateProgressLabel.setDisable(true);
				// 정지 라벨 비활성화
				mAuctionStatePauseLabel.setDisable(true);
				// 종료 라벨 비활성화
				mAuctionStateFinishLabel.setDisable(true);
				// 경매 상태 라벨
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.auction.ready"));

				mAuctionStateGridPane.getStyleClass().clear();
				CommonUtils.getInstance().addStyleClass(mAuctionStateGridPane, "bg-color-008fff");

				break;
			}
		});
	}

	@Override
	public void onCurrentEntryInfo(CurrentEntryInfo currentEntryInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
//		if (auctionCountDown.getStatus().equals(GlobalDefineCode.AUCTION_COUNT_DOWN_COMPLETED)) {
//		}
	}

	@Override
	public void onBidding(Bidding bidding) {

		// 로그
		mLogger.debug(mResMsg.getString("msg.auction.get.bidding") + bidding.getEncodedMessage());

		if (GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PAUSE) || GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_FINISH)) {
			mLogger.debug("응찰 막음. 정지나 종료 상태 " + GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc());
			return;
		}

		// 시작.진행시 응찰 받음.
		if (!mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_START) && !mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
			mLogger.debug("경매 시작/진행중일때만 응찰 가능합니다. " + mAuctionStatus.getState());
			return;
		}

		// 응찰자 ,가격 체크.
		if (!CommonUtils.getInstance().isValidString(bidding.getUserNo()) || !CommonUtils.getInstance().isValidString(bidding.getAuctionJoinNum()) || !CommonUtils.getInstance().isValidString(bidding.getEntryNum()) || !CommonUtils.getInstance().isValidString(bidding.getPrice())) {
			mLogger.debug("출품번호/응찰자/가격 비정상입니다.");
			return;
		}

		if (bidding.getPrice().length() > 5) {
			mLogger.debug("응찰가가 5자리 이상입니다. 응찰에 실패했습니다.");
			return;
		}

		// 응찰 쓰레드풀 init
		initExecutorService();
		// 응찰 쓰레드풀 실행
		mExeOnBiddingService.submit(getBiddingRunnable(bidding));
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

				setBidding(bidding);

			} catch (Exception e) {
				mLogger.debug("[onBidding Exception] : " + e.toString());
				SentryUtil.getInstance().sendExceptionLog(e);
			}
		});
	}

	/**
	 * 현재 가격 기준 모든 응찰 정보 수집 중복 응찰 정보는 수집 대상에서 제외 처리
	 *
	 * @param bidding
	 */
	public synchronized void setBidding(Bidding bidding) {

		SpBidding spBidding = new SpBidding(bidding);

		// 현재 응찰맵에 응찰내역이 있는경우. 이전 응찰 맵에 저장.
		if (mCurrentBidderMap.containsKey(spBidding.getAuctionJoinNum().getValue())) {

			SpBidding beforeBidder = mCurrentBidderMap.get(spBidding.getAuctionJoinNum().getValue());

			if (beforeBidder.getPriceInt() != spBidding.getPriceInt()) {
				// 현재 응찰에 저장
				mCurrentBidderMap.put(spBidding.getAuctionJoinNum().getValue(), spBidding);
			} else {
				mLogger.debug("==== 이전가 입력 응찰 불가. ====");
			}

		} else {
			// 응찰 맵에 Set
			mCurrentBidderMap.put(spBidding.getAuctionJoinNum().getValue(), spBidding);
		}
		// 응찰 로그 저장
		if (!GlobalDefineCode.FLAG_TEST_MODE_BIDDING_LOG) {
			insertBiddingLog(bidding);
		}
	}

	/**
	 * 경매 시작 => 응찰,응찰취소 로그 저장
	 */
	private void insertBiddingLog(final Bidding bidding) {

		Thread thread = new Thread("insertBiddingLog") {
			@Override
			public void run() {
				// 응찰 로그 저장
				AucEntrData aucEntrData = new AucEntrData();

				aucEntrData.setNaBzplc(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc());
				aucEntrData.setAucDt(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucDt());
		
				for (SpEntryInfo spEntryInfo : mWaitEntryInfoDataList) {
					if (spEntryInfo.getEntryNum().getValue().equals(bidding.getEntryNum())) {
						aucEntrData.setAucObjDsc(Integer.parseInt(spEntryInfo.getEntryType().getValue()));
						aucEntrData.setOslpNo(spEntryInfo.getOslpNo().getValue());
						break;
					}
				}
		
				aucEntrData.setAucPrgSq(bidding.getEntryNum());
				aucEntrData.setTrmnAmnno(bidding.getUserNo());
				aucEntrData.setLvstAucPtcMnNo(bidding.getAuctionJoinNum());
				aucEntrData.setAtdrDtm(CommonUtils.getInstance().getCurrentTime_yyyyMMddHHmmssSSS(bidding.getBiddingTime()));
				aucEntrData.setAtdrAm(bidding.getPrice());
				aucEntrData.setRgSqno("");
				aucEntrData.setRmkCntn("");

				requestInsertBiddingHistory(aucEntrData, GlobalDefine.AUCTION_INFO.BID_LOG_TYPE_ING);
			}
		};
		thread.start();
	}

	/**
	 * 현재 출품 데이터 응찰 로그 관련 기본 객체
	 * 
	 * @return
	 */
	private AucEntrData getCurrentBaseEntrData(boolean isStartOrFinish) {

		AucEntrData aucEntrData = new AucEntrData();
		aucEntrData.setNaBzplc(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc());
		aucEntrData.setAucObjDsc(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());
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

	@Override
	public void onCancelBidding(CancelBidding cancelBidding) {

		mLogger.debug(mResMsg.getString("msg.auction.get.bidding.cancel") + cancelBidding.getEncodedMessage());

		if (GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PAUSE) || GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_FINISH)) {
			mLogger.debug("응찰 취소 막음. 정지나 종료 상태 " + GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc());
			return;
		}

		initExecutorService();
		// 응찰취소 쓰레드풀 실행
		mExeOnBiddingService.submit(getCancelBiddingRunnable(cancelBidding));
	}

	/**
	 * 응찰 취소
	 * 
	 * @param CancelBidding
	 * @return
	 */
	private Runnable getCancelBiddingRunnable(CancelBidding cancelBidding) {
		return (() -> {

			// 현재 응찰맵에 있는 경우 이전 응찰내역 맵으로 이동 후 삭제처리
			if (mCurrentBidderMap.containsKey(cancelBidding.getAuctionJoinNum())) {
				SpBidding currentBidder = mCurrentBidderMap.get(cancelBidding.getAuctionJoinNum());
				currentBidder.setIsCancelBidding(new SimpleBooleanProperty(true));
				mCurrentBidderMap.remove(cancelBidding.getAuctionJoinNum());
			}
			
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

		});
	}

	@Override
	public void onToastMessage(ToastMessage toastMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAuctionResult(AuctionResult auctionResult) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionInfo(ConnectionInfo connectionInfo) {
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
				SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SERVER_IP, AuctionDelegate.getInstance().getHost());

				if (AuctionDelegate.getInstance().getPort() > 0) {
					SharedPreference.getInstance().setInt(SharedPreference.PREFERENCE_SERVER_PORT, AuctionDelegate.getInstance().getPort());
				} else {
					SharedPreference.getInstance().setInt(SharedPreference.PREFERENCE_SERVER_PORT, GlobalDefine.AUCTION_INFO.AUCTION_PORT);
				}

				SharedPreference.getInstance().setInt(SharedPreference.PREFERENCE_SELECTED_OBJ, GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());

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
		case GlobalDefineCode.RESPONSE_REQUEST_BIDDING_INVALID_PRICE:
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
					SentryUtil.getInstance().sendExceptionLog(e);
				}
			}
		};
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
			SentryUtil.getInstance().sendExceptionLog(e);
		} catch (ExecutionException e) { // handle e
			mLogger.debug("[onBidderConnectInfo] " + e);
			SentryUtil.getInstance().sendExceptionLog(e);
		} catch (TimeoutException e) { // handle e }
			mLogger.debug("[onBidderConnectInfo] " + e);
			SentryUtil.getInstance().sendExceptionLog(e);
		}
	}

	/**
	 * 접속자 정보 Set
	 * 
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
			}).collect(Collectors.toCollection(FXCollections::observableArrayList));

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
	 * 원버튼 팝업
	 *
	 * @param message
	 * @return
	 */
	private Optional<ButtonType> showAlertPopupOneButton(String message,String btnStr) {
		return CommonUtils.getInstance().showAlertPopupOneButton(mStage, message, btnStr);
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

					// 종료
					if (ke.getCode() == KeyCode.ESCAPE) {
						onCloseApplication();
						ke.consume();
					}

					// 키패드 up down 막음.
					if (ke.getCode() == KeyCode.UP || ke.getCode() == KeyCode.DOWN) {
						ke.consume();
					}
					
					/* 2022-04-14 일괄경매 단축키 제거
						// 경매 시작
						if (ke.getCode() == KeyCode.ENTER) {
							onRefreshStartAuction();
							ke.consume();
						}
						// 경매정지
						if (ke.getCode() == KeyCode.F9) {
							onPause();
							ke.consume();
						}
						// 경매종료
						if (ke.getCode() == KeyCode.F10) {
							onFinish();
							ke.consume();
						}
					 */
					// 유찰대상목록표시
					if (ke.getCode() == KeyCode.F11) {
						onSendPendingList(true);
						ke.consume();
					}
					
					// 유찰대상목록해제
					if (ke.getCode() == KeyCode.F12) {
						onSendPendingList(false);
						ke.consume();
					}
					
					// 새로고침
					if (ke.getCode() == KeyCode.F5) {
						
						if (MoveStageUtil.getInstance().getDialog() != null && MoveStageUtil.getInstance().getDialog().isShowing()) {
							return;
						}
						
						Platform.runLater(() -> CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.searching.entry.list")));
						onRefresh(REFRESH_ENTRY_LIST_TYPE_REFRESH);
						ke.consume();
					}
					
					switch (mAuctionStatus.getState()) {
					case GlobalDefineCode.AUCTION_STATUS_START:
					case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
						// 경매 진행중에 눌림

						break;
					default:

						// 전체보기
						if (ke.getCode() == KeyCode.F1) {
							openEntryListPopUp();
							ke.consume();
						}
						// 보류보기
						if (ke.getCode() == KeyCode.F2) {
							openEntryPendingListPopUp();
							ke.consume();
						}

						// 환경설정
						if (ke.getCode() == KeyCode.F8) {
							// 환경설정
							openSettingDialog();
							ke.consume();
						}

						break;
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
	 * 사운드 메세지 설정
	 *
	 * @param event
	 */
	public void openSettingSoundDialog(MouseEvent event) {

		if (MoveStageUtil.getInstance().getDialog() != null && MoveStageUtil.getInstance().getDialog().isShowing()) {
			return;
		}

		MoveStageUtil.getInstance().openSettingSoundDialog(mStage, new BooleanListener() {

			@Override
			public void callBack(Boolean isRefresh) {
				mLogger.debug("openSettingSoundDialog ");
			}
		});
	}

	/**
	 * 전광판1 종합안내 상태 리스너
	 */
	private UdpBillBoardStatusListener mUdpBillBoardStatusListener1 = new UdpBillBoardStatusListener() {

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
	 * 전광판2 종합안내 상태 리스너
	 */
	private UdpBillBoardStatusListener mUdpBillBoardStatusListener2 = new UdpBillBoardStatusListener() {

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
					mWaitTableView.scrollTo(mWaitTableView.getSelectionModel().getSelectedIndex()+ 1);
				} else {

					int currentSelectedIndex = mWaitTableView.getSelectionModel().getSelectedIndex();

					int selectIndex = 0;

					if (!isPopupClicked) {
						selectIndex = currentSelectedIndex + index;
					} else {
						selectIndex = index;
					}

					mLogger.debug("mRecordCount" + mRecordCount + " / "  + selectIndex);
					
					if (mRecordCount > selectIndex) {
						mWaitTableView.getSelectionModel().select(selectIndex);
						
						if(mRecordCount > 13) {
							mWaitTableView.scrollTo(mWaitTableView.getSelectionModel().getSelectedIndex()+ 1);		
						}

					}
				}

				setCurrentEntryInfo();
			}

		});
	}

	/**
	 * 현재 진행할 데이터 Set
	 *
	 */
	private void setCurrentEntryInfo() {

		Platform.runLater(() -> {

			SpEntryInfo currentEntryInfo = mWaitTableView.getSelectionModel().getSelectedItem();

			if (currentEntryInfo == null || CommonUtils.getInstance().isEmptyProperty(currentEntryInfo.getEntryNum())) {
				return;
			}

			mCurrentSpEntryInfo = currentEntryInfo;
			mCurEntryNumLabel.setText(mCurrentSpEntryInfo.getEntryNum().getValue());
			mCurExhibitorLabel.setText(mCurrentSpEntryInfo.getExhibitor().getValue());
			mCurGenterLabel.setText(mCurrentSpEntryInfo.getGenderName().getValue());
			mCurMotherLabel.setText(mCurrentSpEntryInfo.getMotherCowName().getValue());
			mCurMatimeLabel.setText(mCurrentSpEntryInfo.getMatime().getValue());
			mCurPasgQcnLabel.setText(mCurrentSpEntryInfo.getPasgQcn().getValue());

			if (GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_FINISH)) {

				if (!CommonUtils.getInstance().isEmptyProperty(mCurrentSpEntryInfo.getAuctionSucBidder())) {
					mCurSuccessfulBidderLabel.setText(mCurrentSpEntryInfo.getAuctionSucBidder().getValue());
				} else {
					mCurSuccessfulBidderLabel.setText("");
				}

				if (!CommonUtils.getInstance().isEmptyProperty(mCurrentSpEntryInfo.getSraSbidUpPrice())) {
					mCurSuccessPriceLabel.setText(String.format(mResMsg.getString("str.price"), Integer.parseInt(mCurrentSpEntryInfo.getSraSbidUpPrice().getValue())));
				} else {
					mCurSuccessPriceLabel.setText("0");
				}

			} else {
				if (!CommonUtils.getInstance().isEmptyProperty(mCurrentSpEntryInfo.getExpAuctionSucBidder())) {
					mCurSuccessfulBidderLabel.setText(mCurrentSpEntryInfo.getExpAuctionSucBidder().getValue());
				} else {
					mCurSuccessfulBidderLabel.setText("0");
				}
				if (!CommonUtils.getInstance().isEmptyProperty(mCurrentSpEntryInfo.getExpAuctionBidPrice())) {
					mCurSuccessPriceLabel.setText(String.format(mResMsg.getString("str.price"), Integer.parseInt(mCurrentSpEntryInfo.getExpAuctionBidPrice().getValue())));
				} else {
					mCurSuccessPriceLabel.setText("0");
				}

			}

			mCurResultLabel.setText(mCurrentSpEntryInfo.getBiddingResult().getValue());
			mCurNoteLabel.setText(mCurrentSpEntryInfo.getNote().getValue());
			mCurWeightLabel.setText(String.format(mResMsg.getString("str.price"), Integer.parseInt(mCurrentSpEntryInfo.getWeight().getValue())));
			mCurLowPriceLabel.setText(String.format(mResMsg.getString("str.price"), Integer.parseInt(mCurrentSpEntryInfo.getLowPrice().getValue())));

			requestSelectBidEntry();

			if (GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PAUSE)) {
				Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
			}

		});
	}

	/**
	 * 응찰 목록
	 */
	private void requestSelectBidEntry() {
		
		if(mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_NONE) && mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_READY)){
			return;
		}

		final String naBzplc = GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc();
		final String aucObjDsc = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());
		final String aucDate = GlobalDefine.AUCTION_INFO.auctionRoundData.getAucDt();
		RequestBidEntryBody body = new RequestBidEntryBody(naBzplc, aucObjDsc, aucDate, mCurrentSpEntryInfo.getOslpNo().getValue());

		ApiUtils.getInstance().requestSelectBidEntry(body, new ActionResultListener<ResponseBidEntry>() {

			@Override
			public void onResponseResult(ResponseBidEntry result) {

				if (result != null && result.getSuccess() && !CommonUtils.getInstance().isListEmpty(result.getData())) {

					List<SpBidding> resultDataList = result.getData().stream().map(item -> new SpBidding(item)).collect(Collectors.toList());

					for (SpBidding spBidding : resultDataList) {
						// 현재 응찰맵에 응찰내역이 있는경우. 이전 응찰 맵에 저장.
						if (mCurrentBidderMap.containsKey(spBidding.getAuctionJoinNum().getValue())) {

							SpBidding beforeBidder = mCurrentBidderMap.get(spBidding.getAuctionJoinNum().getValue());

							if (beforeBidder.getPriceInt() != spBidding.getPriceInt()) {
								// 현재 응찰에 저장
								mCurrentBidderMap.put(spBidding.getAuctionJoinNum().getValue(), spBidding);
							} else {
								mLogger.debug("==== 이전가 입력 응찰 불가. ====");
							}

						} else {
							// 응찰 맵에 Set
							mCurrentBidderMap.put(spBidding.getAuctionJoinNum().getValue(), spBidding);
						}
					}

					updateBidderList(resultDataList);
				} else {
					initBiddingInfoDataList();
					mLogger.debug("[fail] 응찰목록조회 :  " + result.getMessage());
				}

			}

			@Override
			public void onResponseError(String message) {
				mLogger.debug("[onResponseError] 응찰목록조회 :  " + message);
				Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
				initBiddingInfoDataList();
			}
		});
	}

	/**
	 * 응찰 로그 저장 API
	 * 
	 * @param aucEntrData
	 */
	private void requestInsertBiddingHistory(final AucEntrData aucEntrData, final String type) {

		RequestBidLogBody insertBody = new RequestBidLogBody(aucEntrData.getNaBzplc(), Integer.toString(aucEntrData.getAucObjDsc()), aucEntrData.getAucDt(), aucEntrData.getOslpNo(), aucEntrData.getRgSqno(), aucEntrData.getTrmnAmnno(), aucEntrData.getLvstAucPtcMnNo(), aucEntrData.getAtdrAm(), aucEntrData.getRmkCntn(), aucEntrData.getAtdrDtm(), aucEntrData.getAucPrgSq());
		mLogger.debug("[응찰 로그 insertBody] : " + insertBody.toString());
		ApiUtils.getInstance().requestInsertBidLog(insertBody, new ActionResultListener<ResponseNumber>() {

			@Override
			public void onResponseResult(ResponseNumber result) {

				mLogger.debug("[응찰 로그 onResponseResult] : " + result.getData() + " / " + result.getSuccess());

				if (result != null && result.getSuccess()) {
					mLogger.debug("[응찰 로그 저장 완료] 출품 번호 : " + aucEntrData.getAucPrgSq() + " 응찰자 : " + aucEntrData.getLvstAucPtcMnNo() + " 응찰금액 : " + aucEntrData.getAtdrAm());
				} else {
					mLogger.debug("[응찰 로그 저장 실패]");
				}
			}

			@Override
			public void onResponseError(String message) {
				mLogger.debug("[응찰 로그 저장 실패]");
			}
		});
	}

	/**
	 * 응찰자 현황
	 * @param spBiddingDataList
	 */
	private synchronized void updateBidderList(List<SpBidding> spBiddingDataList) {

		Platform.runLater(() -> {

			if (spBiddingDataList != null) {

				List<SpBidding> bidderDataList = null;

				if (spBiddingDataList.size() > 11) {
					bidderDataList = new ArrayList<SpBidding>(spBiddingDataList.subList(0, 12));
				} else {
					bidderDataList = new ArrayList<SpBidding>(spBiddingDataList);
				}

				mBiddingUserInfoDataList.clear();
				mBiddingUserInfoDataList.addAll(bidderDataList);
				mBiddingInfoTableView.getSelectionModel().select(0);
				mBiddingInfoTableView.refresh();

				mLogger.debug("[응찰자 목록 갱신] :  " + mBiddingUserInfoDataList.size());

			} else {
				mLogger.debug("[응찰자 목록 초기화] :  " + mBiddingUserInfoDataList.size());
				initBiddingInfoDataList();
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
	 * 경과시간 타이머 시작
	 */
	private void startAuctionSecScheduler() {

		// 경매 경과 시간 타이머 종료
		stopStartAuctionSecScheduler();

		// 경매 시작 후 경과 시간 타이머. 1초마다 반복 수행
		mStartAuctionSecScheduler = new Timer();

		/**
		 * 경매 시작 후 경과 시간 1초마다 수행
		 */
		TimerTask mStartAuctionSecTask = new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					if (mStartAuctionSec >= 60) {
						mAuctionSecLabel.setText(String.format(mResMsg.getString("str.start.auction.min"), mStartAuctionSec / 60, mStartAuctionSec % 60));
					} else {
						mAuctionSecLabel.setText(String.format(mResMsg.getString("str.start.auction.sec"), mStartAuctionSec));
					}

					mStartAuctionSec++;
				});
			}
		};

		mStartAuctionSecScheduler.schedule(mStartAuctionSecTask, 0, 1000);
	}

	/**
	 * 경과시간 타이머 종료
	 */
	private void stopStartAuctionSecScheduler() {
		// 경매 시작 ~ 종료 경과 시간 타이머
		if (mStartAuctionSecScheduler != null) {
			mStartAuctionSecScheduler.cancel();
			mStartAuctionSecScheduler = null;
		}
	}

	
//	 STN_AUCTION_STATUS_READY = "11";		//대기
//	 STN_AUCTION_STATUS_PROGRESS = "21";	//경매시작
//	 STN_AUCTION_STATUS_FINISH = "22";	//종료
//	 STN_AUCTION_STATUS_PAUSE = "23";		//정지
	
//	 AUCTION_STATUS_NONE = "8001"; // 출품 자료 이관 전 상태
//	 AUCTION_STATUS_READY = "8002"; // 경매 준비 상태
//	 AUCTION_STATUS_START = "8003"; // 경매 시작 상태
//	 AUCTION_STATUS_PROGRESS = "8004"; // 경매 진행 상태
//	 AUCTION_STATUS_PASS = "8005"; // 경매 출품 건 강제 유찰
//	 AUCTION_STATUS_COMPLETED = "8006"; // 경매 완료 상태
//	 AUCTION_STATUS_FINISH = "8007"; // 경매 종료 상태
//	
	private void showLogAuctionInfo() {


		String qcnInfo = null;
		String aucInfo = null;
		
		if(GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_READY)) {
			qcnInfo = "대기";
		}else if(GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PROGRESS)) {
			qcnInfo = "진행";
		}else if(GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_FINISH)) {
			qcnInfo = "종료";
		}else if(GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_PAUSE)) {
			qcnInfo = "정지";
		}
		
		if(mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
			aucInfo = "출품 자료 이관 전";
		}else if(mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_READY)) {
			aucInfo = "대기";
		}else if(mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_START)) {
			aucInfo = "시작";
		}else if(mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
			aucInfo = "진행";
		}else if(mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PASS)) {
			aucInfo = "유찰";
		}else if(mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_COMPLETED)) {
			aucInfo = "완료";
		}else if(mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {
			aucInfo = "회차종료";
		}
		
		mLogger.debug("[현재 회차 경매 상태]=> " + qcnInfo +  "(" + GlobalDefine.AUCTION_INFO.auctionRoundData.getSelStsDsc() + ")");
		mLogger.debug("[현재 경매 서버 상태]=> " + aucInfo  +  "(" + mAuctionStatus.getState() + ")");
	}
}
