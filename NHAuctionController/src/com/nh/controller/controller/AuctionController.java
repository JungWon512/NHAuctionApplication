package com.nh.controller.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.UdpBillBoardStatusListener;
import com.nh.common.interfaces.UdpPdpBoardStatusListener;
import com.nh.controller.controller.SettingController.AuctionToggle;
import com.nh.controller.interfaces.AudioPlayListener;
import com.nh.controller.interfaces.BooleanListener;
import com.nh.controller.interfaces.MessageStringListener;
import com.nh.controller.interfaces.SelectEntryListener;
import com.nh.controller.interfaces.SettingListener;
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
import com.nh.share.api.request.body.RequestCowInfoBody;
import com.nh.share.api.request.body.RequestUpdateLowsBidAmtBody;
import com.nh.share.api.response.ResponseCowInfo;
import com.nh.share.api.response.ResponseNumber;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.common.models.RetryTargetInfo;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.FinishAuction;
import com.nh.share.controller.models.InitEntryInfo;
import com.nh.share.controller.models.PauseAuction;
import com.nh.share.server.models.AuctionBidStatus;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.StandConnectInfo;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class AuctionController extends BaseAuctionController implements Initializable {

	@FXML // root pane
	public BorderPane mRootAuction;

	public FXMLLoader mFxmlLoader = null;// auction controller fxml

	@FXML // 완료,대기,응찰현황 테이블
	private TableView<SpEntryInfo> mFinishedTableView, mWaitTableView;

	@FXML // 접속현황 테이블
	private TableView<SpBidderConnectInfo> mConnectionUserTableView;

	@FXML // 완료,대기,응찰현황,접속현황 테이블
	private TableView<SpBidding> mBiddingInfoTableView;

	@FXML // 완료된 출품
	private TableColumn<SpEntryInfo, String> mFinishedEntryNumColumn, mFinishedExhibitorColumn, mFinishedGenderColumn, mFinishedMotherColumn, mFinishedMatimeColumn, mFinishedPasgQcnColumn, mFinishedWeightColumn, mFinishedLowPriceColumn, mFinishedSuccessPriceColumn, mFinishedSuccessfulBidderColumn, mFinishedResultColumn, mFinishedNoteColumn;

	@FXML // 대기중인 출품
	private TableColumn<SpEntryInfo, String> mWaitEntryNumColumn, mWaitExhibitorColumn, mWaitGenderColumn, mWaitMotherColumn, mWaitMatimeColumn, mWaitPasgQcnColumn, mWaitWeightColumn, mWaitLowPriceColumn, mWaitSuccessPriceColumn, mWaitSuccessfulBidderColumn, mWaitResultColumn, mWaitNoteColumn;

	@FXML // 현재 경매
	private Label mCurEntryNumLabel, mCurExhibitorLabel, mCurGenterLabel, mCurMotherLabel, mCurMatimeLabel, mCurPasgQcnLabel, mCurWeightLabel, mCurLowPriceLabel, mCurSuccessPriceLabel, mCurSuccessfulBidderLabel, mCurResultLabel, mCurNoteLabel;

	@FXML // 사용자 접속 현황
	private TableColumn<SpBidderConnectInfo, String> mConnectionUserColumn_1, mConnectionUserColumn_2, mConnectionUserColumn_3, mConnectionUserColumn_4, mConnectionUserColumn_5;

	@FXML // 응찰자 정보
	private TableColumn<SpBidding, String> mBiddingPriceColumn, mBiddingUserColumn;

	@FXML // 하단 버튼
	private Button mBtnEsc, mBtnF3, mBtnF1, mBtnF2, mBtnF6, mBtnF8, mBtnEnter, mBtnSpace, mBtnMessage, mBtnUpPrice, mBtnDownPrice,mBtnQcnFinish,mBtnF5;

	@FXML // 경매 정보
	private Label mAuctionInfoDateLabel, mAuctionInfoRoundLabel, mAuctionInfoGubunLabel, mAuctionInfoTotalCountLabel;

	@FXML // 경매 정보 - 상태
	private Label mAuctionStateReadyLabel, mAuctionStateProgressLabel, mAuctionStateSuccessLabel, mAuctionStateFailLabel, mAuctionStateLabel, mAuctionSecLabel;

	@FXML // 남은 시간 Bar
	private Label cnt_5, cnt_4, cnt_3, cnt_2, cnt_1;

	@FXML // 경매 상단 아이콘 메세지보내기,전광판 1 상태, 전광판 2 상태
	private ImageView mDisplay_1_ImageView, mDisplay_2_ImageView, mDisplay_3_ImageView;

	@FXML // 감가 기준 금액 / 횟수
	private Label mDeprePriceLabel, mLowPriceChgNtLabel;

	@FXML // 음성 선택 check-box
	private CheckBox mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mMotherObjNumCheckBox, mMaTimeCheckBox, mPasgQcnCheckBox, mWeightCheckBox, mLowPriceCheckBox, mBrandNameCheckBox ,mDnaCheckBox ;

	@FXML // 음성 멘트 버튼
	private Button mBtnIntroSound, mBtnBuyerSound, mBtnGuideSound, mBtnEtc_1_Sound, mBtnEtc_2_Sound, mBtnEtc_3_Sound, mBtnEtc_4_Sound, mBtnEtc_5_Sound, mBtnEtc_6_Sound;

	@FXML // 음성설정 ,저장 ,음성중지 ,낙찰결과
	private Button mBtnSettingSound, mBtnStopSound, mBtnEntrySuccessList;

	@FXML // 일시정지 재시작 ,일시정지
	private Button mBtnReStart, mBtnPause;

	@FXML // 재경매중 라벨,카운트다운
	private Label mReAuctionLabel, mReAuctionCountLabel, mCountDownLabel;

	@FXML // 접속자 정보 수
	private Label mConnectionUserCntLabel;

	@FXML // 비고 상위 뷰
	private VBox mNoteVbox;

	@FXML // 비고 뷰
	private TextArea mNoteTextArea;

	@FXML // 하단 메세지 전송 상위 뷰
	private StackPane mSTPMessage;

	@FXML // 하단 메세지 전송 텍스트
	private Label mMessageText;

	@FXML
	private GridPane mAuctionStateGridPane;

	private int mRemainingTimeCount = 5; // 카운트다운 남은 시간. 정지 상황에서 남은 시간을 저장하기 위함.

	private final SharedPreference preference = new SharedPreference();

	private Timer mAutoStopScheduler = null; // 음성 경매 정지 타이머
	private Timer mStartAuctionSecScheduler = null; // 경매 시작 후 초 증가 타이머
	private int mStartAuctionSec = 0; // 경매 시작 후 초 증가 타이머 증가값

	private EntryDialogType mCurPageType; // 전체or보류목록 타입

	private Map<String, BidderConnectInfo> mConnectionUserMap = new HashMap<>(); // 접속 현황

	private Image mResDisplayOn = new Image("/com/nh/controller/resource/images/ic_con_on.png"); // 전광판 On 이미지 리소스
	private Image mResDisplayOff = new Image("/com/nh/controller/resource/images/ic_con_off.png"); // 전광판 Off 이미지 리소스

	private boolean isShowToast = false; // 메세지 전송 상태 플래그

	private FadeTransition mAnimationFadeIn; // 토스트 애니메이션 START
	private FadeTransition mAnimationFadeOut; // 토스트 애니메이션 END

	private Queue<String> mMsgQueue = new PriorityQueue(); // 메세지 전송 queue

	private Stage mMessageStage = null;

	private boolean isPlusKeyStartAuction = false; // 플러스키 경매 진행 플래그

	private boolean isFirst = false;


	private boolean isRestart = false;

	private String REFRESH_ENTRY_LIST_TYPE_NONE = "NONE"; // 출장우 정보 갱신 - 기본
	private String REFRESH_ENTRY_LIST_TYPE_SEND = "SEND"; // 출장우 정보 갱신 후 정보 보냄
	private String REFRESH_ENTRY_LIST_TYPE_START = "START"; // 출장우 정보 갱신 후 시작

	private boolean isSendEntryData = false; // 출장우 데이터 전송 여부
	
	private boolean isQcnFinish = false;	//경매 회차 종료 버튼 눌렀을때 플래그
	
	private boolean isQcnChange = false; //경매 회차 변경
	
	// 우클릭 새로고침 적용
	private ContextMenu mRefreshContextMenu = null;
	
	private List<CowInfoData> mTmpCowDataList = null; //경매일 선택에서 조회된 출장우 데이터 set
	

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
				// 전광판
				createUdpClient(mUdpBillBoardStatusListener1, mUdpBillBoardStatusListener2, mUdpPdpBoardStatusListener);
			}
		};
		thread.setDaemon(true);
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
					} else {
						evt.consume();
						
						if (!SettingApplication.getInstance().isSingleAuction()) {
							// 환경설정 -> 일괄경매 변경 -> 팝업 -> 취소시 다시 단일로 설정
							SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_AUCTION_TOGGLE_TYPE, AuctionToggle.SINGLE.toString());
							SettingApplication.getInstance().initSharedData();
						}
					}
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

	@Override
	public void initialize(URL arg0, ResourceBundle resources) {

		// get ResMsg
		if (resources != null) {
			mResMsg = resources;
		}
		// 뷰 초기화
		initViewConfiguration();

		// 사운드 초기화
		SoundUtil.getInstance();

		// 비고 수정 막음.
		mNoteTextArea.setEditable(false);
	}

	/**
	 * 기본 뷰 설정
	 */
	private void initViewConfiguration() {

		initParsingSharedData();
		initTableConfiguration();

		setCountDownLabelState(SettingApplication.getInstance().getAuctionCountdown(), true);

		mBtnEsc.setOnMouseClicked(event -> onClose());
		mBtnF1.setOnMouseClicked(event -> openEntryListPopUp());
		mBtnF2.setOnMouseClicked(event -> openEntryPendingListPopUp());
		mBtnF3.setOnMouseClicked(event -> onPending());
		mBtnF5.setOnMouseClicked(event -> {
			Platform.runLater(() -> CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.searching.entry.list")));
			refreshWaitAllEntryDataList(mWaitTableView.getSelectionModel().getSelectedIndex());
		});

		mBtnF6.setOnMouseClicked(event -> onSuccessAuction());
		mBtnF8.setOnMouseClicked(event -> openSettingDialog());
		
		mBtnEnter.setOnMouseClicked(event -> normalEnterStartAuction());
		mBtnSpace.setOnMouseClicked(event -> normalSpaceStartAuction());

		mWaitTableView.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY){
                	onClickWaitTableView();
                }
            }
        });
		
//		mWaitTableView.setOnMouseClicked(event -> onClickWaitTableView());
		mBtnMessage.setOnMouseClicked(event -> openSendMessage(event));
		mBtnUpPrice.setOnMouseClicked(event -> onUpPrice(event));
		mBtnDownPrice.setOnMouseClicked(event -> onDownPrice(event));

		mBtnSettingSound.setOnMouseClicked(event -> openSettingSoundDialog(event));
		mBtnStopSound.setOnMouseClicked(event -> SoundUtil.getInstance().stopSound());
		mBtnEntrySuccessList.setOnMouseClicked(event -> openFinishedEntryListPopUp());
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

		mBtnReStart.setOnMouseClicked(event -> onReStart());
		mBtnPause.setOnMouseClicked(event -> checkAndOnPause());

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
		// 완료된 출품
		CommonUtils.getInstance().setAlignCenterCol(mFinishedEntryNumColumn);
		CommonUtils.getInstance().setAlignCenterCol(mFinishedExhibitorColumn);
		CommonUtils.getInstance().setAlignCenterCol(mFinishedGenderColumn);
		CommonUtils.getInstance().setAlignCenterCol(mFinishedMotherColumn);
		CommonUtils.getInstance().setAlignCenterCol(mFinishedMatimeColumn);
		CommonUtils.getInstance().setAlignCenterCol(mFinishedPasgQcnColumn);
		CommonUtils.getInstance().setAlignCenterCol(mFinishedWeightColumn);
		CommonUtils.getInstance().setAlignCenterCol(mFinishedLowPriceColumn);
		CommonUtils.getInstance().setAlignCenterCol(mFinishedSuccessPriceColumn);
		CommonUtils.getInstance().setAlignCenterCol(mFinishedSuccessfulBidderColumn);
		CommonUtils.getInstance().setAlignCenterCol(mFinishedResultColumn);
		CommonUtils.getInstance().setAlignLeftCol(mFinishedNoteColumn);
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
		CommonUtils.getInstance().setNumberColumnFactory(mFinishedWeightColumn, false);
		CommonUtils.getInstance().setNumberColumnFactory(mFinishedLowPriceColumn, true);
		CommonUtils.getInstance().setNumberColumnFactory(mFinishedSuccessPriceColumn, true);
		CommonUtils.getInstance().setNumberColumnFactory(mWaitWeightColumn, false);
		CommonUtils.getInstance().setNumberColumnFactory(mWaitLowPriceColumn, true);
		CommonUtils.getInstance().setNumberColumnFactory(mWaitSuccessPriceColumn, true);
		CommonUtils.getInstance().setNumberColumnFactory(mBiddingPriceColumn, true);
		// [e] fmt - number

		// [s] binding
		// 테이블 컬럼 - 완료
		mFinishedEntryNumColumn.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mFinishedExhibitorColumn.setCellValueFactory(cellData -> cellData.getValue().getExhibitor());
		mFinishedGenderColumn.setCellValueFactory(cellData -> cellData.getValue().getGenderName());
		mFinishedMotherColumn.setCellValueFactory(cellData -> cellData.getValue().getMotherCowName());
		mFinishedMatimeColumn.setCellValueFactory(cellData -> cellData.getValue().getMatime());
		mFinishedPasgQcnColumn.setCellValueFactory(cellData -> cellData.getValue().getPasgQcn());
		mFinishedWeightColumn.setCellValueFactory(cellData -> cellData.getValue().getWeight());
		mFinishedLowPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getLowPrice());
		mFinishedSuccessPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getSraSbidUpPrice());
		mFinishedSuccessfulBidderColumn.setCellValueFactory(cellData -> cellData.getValue().getAuctionSucBidder());
		mFinishedResultColumn.setCellValueFactory(cellData -> cellData.getValue().getBiddingResult());
		mFinishedNoteColumn.setCellValueFactory(cellData -> cellData.getValue().getNote());

		// 테이블 컬럼 - 대기
		mWaitEntryNumColumn.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mWaitExhibitorColumn.setCellValueFactory(cellData -> cellData.getValue().getExhibitor());
		mWaitGenderColumn.setCellValueFactory(cellData -> cellData.getValue().getGenderName());
		mWaitMotherColumn.setCellValueFactory(cellData -> cellData.getValue().getMotherCowName());
		mWaitMatimeColumn.setCellValueFactory(cellData -> cellData.getValue().getMatime());
		mWaitPasgQcnColumn.setCellValueFactory(cellData -> cellData.getValue().getPasgQcn());
		mWaitWeightColumn.setCellValueFactory(cellData -> cellData.getValue().getWeight());
		mWaitLowPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getLowPrice());
		mWaitSuccessPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getSraSbidUpPrice());
		mWaitSuccessfulBidderColumn.setCellValueFactory(cellData -> cellData.getValue().getAuctionSucBidder());
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
		mFinishedTableView.setPlaceholder(new Label(mResMsg.getString("msg.entry.finish.default")));
		mWaitTableView.setPlaceholder(new Label(mResMsg.getString("msg.entry.wait.default")));
		mConnectionUserTableView.setPlaceholder(new Label(mResMsg.getString("msg.connected.user.default")));
		mBiddingInfoTableView.setPlaceholder(new Label(mResMsg.getString("msg.bidder.default")));

//		initFinishedEntryDataList();
		// 응찰 현황
		initBiddingInfoDataList();
		// 접속 현황
		initConnectionUserDataList();

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
		mDnaCheckBox.setSelected(checkList.get(9));

		mEntryNumCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_NUMBER);
		mExhibitorCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_EXHIBITOR);
		mGenderCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_GENDER);
		mMotherObjNumCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MOTHER);
		mMaTimeCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MATIME);
		mPasgQcnCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_PASGQCN);
		mWeightCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_WEIGHT);
		mLowPriceCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_LOWPRICE);
		mBrandNameCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_BRAND);
		mDnaCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_DNA);

		mEntryNumCheckBox.setOnAction(mCheckBoxEventHandler);
		mExhibitorCheckBox.setOnAction(mCheckBoxEventHandler);
		mGenderCheckBox.setOnAction(mCheckBoxEventHandler);
		mMotherObjNumCheckBox.setOnAction(mCheckBoxEventHandler);
		mMaTimeCheckBox.setOnAction(mCheckBoxEventHandler);
		mPasgQcnCheckBox.setOnAction(mCheckBoxEventHandler);
		mWeightCheckBox.setOnAction(mCheckBoxEventHandler);
		mLowPriceCheckBox.setOnAction(mCheckBoxEventHandler);
		mBrandNameCheckBox.setOnAction(mCheckBoxEventHandler);
		mDnaCheckBox.setOnAction(mCheckBoxEventHandler);
		// 메인 상단 체크박스 [E]
	}

	/**
	 * 경매 데이터 가져옴.
	 */
	private void requestAuctionInfo() {
		// 출장우 정보
		requestEntryData();
		// 수수료 정보
//		reqeustFee();
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
	 * 예정가 낮추기 금액
	 *
	 * @param downPrice
	 */
	private void setBaseDownPrice(String downPrice) {
		Platform.runLater(() -> {
			
			String priceText = "";
			
			if(SettingApplication.getInstance().isWon(mCurrentSpEntryInfo.getEntryType().getValue())) {
				
				priceText = String.format(mResMsg.getString("fmt.money.unit.won"), Integer.parseInt(downPrice));
				
			}else {
				
				priceText = String.format(mResMsg.getString("fmt.money.unit.tenthousand.won"), Integer.parseInt(downPrice));
				
			}
			
			mDeprePriceLabel.setText(priceText);
			
		});
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
			
			// 우클릭 새로고침 적용
			if (mRefreshContextMenu == null) {

				MenuItem item1 = new MenuItem(mResMsg.getString("str.refresh"));

				item1.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						// 새로고침
						Platform.runLater(() -> CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.searching.entry.list")));
						refreshWaitAllEntryDataList(mWaitTableView.getSelectionModel().getSelectedIndex());
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
//						mWaitTableView.getSelectionModel().selectedIndexProperty().addListener((observable, oldIndex, newIndex) -> { 선택 콜백 인덱스	 필요시 주석 해제
//						});

//						PauseTransition pauseTransition = new PauseTransition(Duration.millis(9000));
//						pauseTransition.setOnFinished(new EventHandler<ActionEvent>() {
//							@Override
//							public void handle(ActionEvent event) {
//								mLogger.debug("[????????????????????????출장우 데이터 전송????????????????????????] " + isQcnChange);
//								
								if (!isSendEnterInfo() && !isQcnChange) {
									// 출장우 전송
									onSendEntryData();
								} else {
									mLogger.debug("[출장우 데이터 전송 X. 현재 경매 상태 ]=> " + mAuctionStatus.getState());
									Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
								}

//							}
//						});
//						pauseTransition.play();
						
						
					
					}
				});
				pauseTransition.play();
			} else {
				Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
			}
		}

	}

	
	/**
	 * 대기중인 출품 목록 전체 갱신.서버전달.
	 */
	private void refreshWaitAllEntryDataList(int index) {
	
		
		ApiUtils.getInstance().requestSelectCowInfo(getCowInfoParam(), new ActionResultListener<ResponseCowInfo>() {
			@Override
			public void onResponseResult(final ResponseCowInfo result) {

				if (result != null && result.getSuccess() && !CommonUtils.getInstance().isListEmpty(result.getData())) {
					
					mLogger.debug("[출장우 정보 조회 데이터 수] " + result.getData().size());

					ObservableList<SpEntryInfo> newEntryDataList = getParsingCowEntryDataList(result.getData());
					
					Platform.runLater(() -> {
						
						mRecordCount = result.getData().size();
						mWaitEntryInfoDataList.clear();
						mWaitEntryInfoDataList.addAll(newEntryDataList);

						
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
						}
						
					
						setCurrentEntryInfo(true);
						setCowTotalCount(result.getData().size());
						
						CommonUtils.getInstance().dismissLoadingDialog();
					});
				}
			}
			
			@Override
			public void onResponseError(String message) {
				mLogger.debug("[onResponseError] 출장우 정보 " + message);
				Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
			}	
		});
	}
	
	/**
	 * 출장우 데이터 파라미터값
	 * @return
	 */
	private RequestCowInfoBody getCowInfoParam() {
		
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
		return new RequestCowInfoBody(naBzplc, aucObjDsc, aucDate, selStsDsc, stnYn,"");
	}
	
	/**
	 * 대기중인 출품 목록 갱신 변경/추가된 데이터 서버 전달
	 */
	private void refreshWaitEntryDataList(boolean isRefresh,String type) {

		ApiUtils.getInstance().requestSelectCowInfo(getCowInfoParam(), new ActionResultListener<ResponseCowInfo>() {
			@Override
			public void onResponseResult(final ResponseCowInfo result) {

				if (result != null && result.getSuccess() && !CommonUtils.getInstance().isListEmpty(result.getData())) {
					
					mLogger.debug("[출장우 정보 조회 데이터 수] " + result.getData().size());

					ObservableList<SpEntryInfo> newEntryDataList = getParsingCowEntryDataList(result.getData());

					// 조회 데이터 없으면 리턴
					if (CommonUtils.getInstance().isListEmpty(newEntryDataList)) {
						addLogItem("조회 데이터 없음.");
						return;
					}

					// 현재 최종 수정시간 < 조회된 최종 수정시간 -> 데이터 갱신&서버 전달
					for (int i = 0; mRecordCount > i; i++) {

						String curEntryNum = mWaitEntryInfoDataList.get(i).getEntryNum().getValue();

						
						for (int j = 0; newEntryDataList.size() > j; j++) {

							String newEntryNum = newEntryDataList.get(j).getEntryNum().getValue();

							if (curEntryNum.equals(newEntryNum)) {

								if (CommonUtils.getInstance().isEmptyProperty(newEntryDataList.get(j).getLsChgDtm()) || CommonUtils.getInstance().isEmptyProperty(mWaitEntryInfoDataList.get(i).getLsChgDtm())) {
									continue;
								}

								long newDt = Long.parseLong(newEntryDataList.get(j).getLsChgDtm().getValue());
								long curDt = Long.parseLong(mWaitEntryInfoDataList.get(i).getLsChgDtm().getValue());

								
								if (newDt > curDt) {
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
					
						// 추가된 데이터 있는지 확인
						ObservableList<SpEntryInfo> newDataList = newEntryDataList.stream().filter(e -> !mWaitEntryInfoDataList.contains(e)).collect(Collectors.toCollection(FXCollections::observableArrayList));
	
						// 추가된 데이터 항목이 있으면 add
						if (!CommonUtils.getInstance().isListEmpty(newDataList)) {
	
							mLogger.debug("추가된 데이터 있음.");
	
							for (SpEntryInfo spEntryInfo : newDataList) {
								addLogItem("추가된 데이터 전송=> " + AuctionDelegate.getInstance().onSendEntryData(spEntryInfo));
							}
	
							mWaitEntryInfoDataList.addAll(mRecordCount, newDataList);
							mRecordCount += newDataList.size();
	
						} else {
							addLogItem("추기된 데이터 없음.");
						}
	
						mWaitTableView.setItems(mWaitEntryInfoDataList);
						mWaitTableView.refresh();
					
					}
					
					Platform.runLater(() -> {
						setCurrentEntryInfo(false);
						setCowTotalCount(result.getData().size());
					});
					
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
	 * 출장우 전송 & 경매 시작.
	 * 
	 * @param type
	 */
	private void onCowInfoSendOrStartAuction(String type) {

		if (type.equals(REFRESH_ENTRY_LIST_TYPE_SEND)) {

			Thread thread = new Thread("onSendEntryData") {
				@Override
				public void run() {

					addLogItem("start onSendEntryData thread");

					int count = 0;

					for (SpEntryInfo entryInfo : mWaitEntryInfoDataList) {
						if (!CommonUtils.getInstance().isEmptyProperty(entryInfo.getEntryNum())) {
							addLogItem(mResMsg.getString("msg.auction.send.entry.data") + AuctionDelegate.getInstance().onSendEntryData(entryInfo));
							count++;
						}
					}

					addLogItem(String.format(mResMsg.getString("msg.send.entry.data.result"), count));

					isSendEntryData = true;

//					Platform.runLater(() -> {
//						CommonUtils.getInstance().dismissLoadingDialog();
//					});

				}
			};

			thread.setDaemon(true);
			thread.start();

		} else if (type.equals(REFRESH_ENTRY_LIST_TYPE_START)) {
			// 경매시
			onStartAuction();
		} else {
			Platform.runLater(() -> {
				CommonUtils.getInstance().dismissLoadingDialog();
			});
		}

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
	 * DataList 5개씩 자르기
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Collection<List<BidderConnectInfo>> partDataList(List<BidderConnectInfo> list, int size) {

		Collection Rlist = Arrays.asList();

		Rlist = partition(list, size);

		return Rlist;
	}

	private static <T> Collection<List<T>> partition(List<T> list, int size) {
		final AtomicInteger counter = new AtomicInteger(0);
		return list.stream().collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size)).values();
	}

	/**
	 * 경매 완료 초기값
	 */
	private void initFinishedEntryDataList() {

		// dummy row
		mDummyRow.clear();
		for (int i = 0; DUMMY_ROW_FINISHED > i; i++) {
			mDummyRow.add(new SpEntryInfo());
		}

		mFinishedEntryInfoDataList.addAll(mDummyRow);
		mFinishedTableView.setItems(mFinishedEntryInfoDataList);
	}

	/**
	 * 응찰자 초기값
	 */
	private void initBiddingInfoDataList() {
		mBiddingUserInfoDataList.clear();
		mBiddingUserInfoDataList.add(new SpBidding());
		mBiddingInfoTableView.setItems(mBiddingUserInfoDataList);
		mBiddingInfoTableView.getSelectionModel().select(0);
		biddingInfoTableStyleToggle();
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
					refreshWaitEntryDataList(true,REFRESH_ENTRY_LIST_TYPE_SEND);
				}
			};

			refreshWaitThread.setDaemon(true);
			refreshWaitThread.start();

		} else {
			addLogItem(mResMsg.getString("msg.need.connection"));
			CommonUtils.getInstance().dismissLoadingDialog();
		}
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
	 * 보류 처리
	 */
	public void onPending() {
		
		
		Platform.runLater(() -> {
			
			String msg = String.format(mResMsg.getString("msg.auction.cow.pending"), mCurrentSpEntryInfo.getEntryNum().getValue());
			
				Optional<ButtonType> btnResult = CommonUtils.getInstance().showAlertPopupTwoButton(mStage, msg, mResMsg.getString("popup.btn.ok"), mResMsg.getString("popup.btn.cancel"));
	
				if (btnResult.get().getButtonData() == ButtonData.LEFT) {
				
					SpEntryInfo spEntryInfo = mWaitTableView.getSelectionModel().getSelectedItem();

					if (!spEntryInfo.getAuctionResult().getValue().equals(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING)) {

						String entryNum = spEntryInfo.getEntryNum().getValue();
						String auctionHouseCode = spEntryInfo.getAuctionHouseCode().getValue();
						String entryType = spEntryInfo.getEntryType().getValue();
						String aucDt = spEntryInfo.getAucDt().getValue();
						String state = GlobalDefineCode.AUCTION_RESULT_CODE_PENDING;
//						String oslpNo = spEntryInfo.getOslpNo().getValue();
//						String ledSqNo = spEntryInfo.getLedSqno().getValue();

						EntryInfo entryInfo = new EntryInfo();
						entryInfo.setEntryNum(entryNum);
						entryInfo.setAuctionHouseCode(auctionHouseCode);
						entryInfo.setEntryType(entryType);
						entryInfo.setAucDt(aucDt);
						entryInfo.setAuctionResult(state);
						entryInfo.setLsCmeNo(GlobalDefine.ADMIN_INFO.adminData.getUserId());

						ApiUtils.getInstance().requestUpdateCowSt(new RequestCowInfoBody(entryInfo), new ActionResultListener<ResponseNumber>() {
							@Override
							public void onResponseResult(ResponseNumber result) {

								if (result != null && result.getSuccess() && result.getData() > 0) {

									mLogger.debug("[보류처리 완료]");

									// 보류처리
									spEntryInfo.setAuctionResult(new SimpleStringProperty(state));
									// 서버에 전달
									AuctionDelegate.getInstance().onSendEntryData(spEntryInfo);

									setCurrentEntryInfo(true);

									mWaitTableView.refresh();

								} else {
									Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.auction.state.pending.fail")));
								}
							}

							@Override
							public void onResponseError(String message) {
								Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.auction.state.pending.fail")));
							}
						});
					}
					
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
				
				//경매 진행중인경우 갱신 안하도록 2021-12-14
				if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_START) || mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
					return;
				}
		
				if (index < 0 || CommonUtils.getInstance().isListEmpty(dataList) || type.equals(EntryDialogType.ENTRY_FINISH_LIST) ) {
					refreshWaitAllEntryDataList(mWaitTableView.getSelectionModel().getSelectedIndex());
					return;
				}
				
				mCurPageType = type;
				
				refreshWaitAllEntryDataList(index);

			}
		});
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

				if (isSaved) {

					if (!SettingApplication.getInstance().isSingleAuction()) {
						// 일괄경매로 변경된 경우 현재창 종료 후 일괄경매 이동
						onCloseApplication();
						return;
					}
					// 환경 설정 후 변경 값들 재설정
					setCountDownLabelState(SettingApplication.getInstance().getAuctionCountdown(), true);

					// 단일 경매, 음성 경매 버튼
					if (!SettingApplication.getInstance().isUseSoundAuction()) {
						mBtnEnter.setDisable(false);
						mBtnSpace.setDisable(true);
					} else {
						mBtnEnter.setDisable(true);
						mBtnSpace.setDisable(false);
					}

					// 최저가 낮추기 금액
					int BaselowPrice = SettingApplication.getInstance().getCowLowerLimitPrice(Integer.parseInt(mCurrentSpEntryInfo.getEntryType().getValue()));

					setBaseDownPrice(Integer.toString(BaselowPrice));

					// 비고란 show or hide
					if (SettingApplication.getInstance().isNote()) {
						mNoteVbox.setVisible(true);
						if (!CommonUtils.getInstance().isValidString(mCurNoteLabel.getText())) {
							mNoteTextArea.setText(mCurNoteLabel.getText().toString());
						}
					} else {
						mNoteVbox.setVisible(false);
					}
				}
			}

			@Override
			public void initServer() {
				dismissShowingDialog();
				mLogger.debug("[CLEAR INIT SERVER]" + AuctionDelegate.getInstance().onInitEntryInfo(new InitEntryInfo(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc(), Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getQcn()))));
				isApplicationClosePopup = true;
				onServerAndClose();
			}

		}, mUdpBillBoardStatusListener1, mUdpBillBoardStatusListener2, mUdpPdpBoardStatusListener);
	}

	/**
	 * 경매 진행중 => 취소 경매 진행중 아님 => 종료
	 */

	/**
	 * 프로그램 종료
	 */
	public void onCloseApplication() {

		Platform.runLater(() -> {

			Optional<ButtonType> btnResult = CommonUtils.getInstance().showAlertPopupTwoButton(mStage, mResMsg.getString("str.ask.application.close"), mResMsg.getString("popup.btn.ok"), mResMsg.getString("popup.btn.cancel"));

			if (btnResult.get().getButtonData() == ButtonData.LEFT) {

				isApplicationClosePopup = true;

				onServerAndClose();

			} else {

				if (!SettingApplication.getInstance().isSingleAuction()) {
					// 환경설정 -> 일괄경매 변경 -> 팝업 -> 취소시 다시 단일로 설정
					SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_AUCTION_TOGGLE_TYPE, AuctionToggle.SINGLE.toString());
					SettingApplication.getInstance().initSharedData();
				}
			}
		});
	}

	/**
	 * 경매 진행중 => 취소 경매 진행중 아님 => 종료
	 */

	/**
	 * 프로그램 종료
	 */
	public void onCloseApplicationPopup() {

		Platform.runLater(() -> {

			Optional<ButtonType> btnResult = CommonUtils.getInstance().showAlertPopupTwoButton(mStage, mResMsg.getString("str.ask.application.close"), mResMsg.getString("popup.btn.ok"), mResMsg.getString("popup.btn.cancel"));

			if (btnResult.get().getButtonData() == ButtonData.LEFT) {
				isApplicationClosePopup = true;
				onServerAndClose();
				Platform.exit();
				System.exit(0);
			} else {

				if (!SettingApplication.getInstance().isSingleAuction()) {
					// 환경설정 -> 일괄경매 변경 -> 팝업 -> 취소시 다시 단일로 설정
					SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_AUCTION_TOGGLE_TYPE, AuctionToggle.SINGLE.toString());
					SettingApplication.getInstance().initSharedData();
				}
			}
		});
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

	@Override
	void onCancelOrClose() {

		// 경매 진행중인 경우 취소처리
		if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_START) || mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {

//			Platform.runLater(()-> CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.app.cancel.ing")));

			if (isCancel) {
				return;
			}

			mLogger.debug("[출품 취소 처리 시작] " + isCancel);
			isStartSoundPlaying = false;
			isCancel = true;

			stopAllSound();
			stopStartAuctionSecScheduler();

			// 정지
			onPause();
			// 음성경매에서 + 눌러 단일경매로 시작한 경우
			if (!SettingApplication.getInstance().isUseSoundAuction() && isPlusKeyStartAuction) {
				toggleAuctionType();
				isPlusKeyStartAuction = false;
				// ENTER 경매 시작으로.
				Platform.runLater(() -> {
					mBtnEnter.setText(mResMsg.getString("str.btn.start"));
					CommonUtils.getInstance().removeStyleClass(mBtnEnter, "btn-auction-stop");
				});

			}

			// 서버로 취소결과 전달.
			saveAuctionResult(false, mCurrentSpEntryInfo, null, GlobalDefineCode.AUCTION_RESULT_CODE_CANCEL);
			mBiddingInfoTableView.setDisable(false);
			// 전광판 전달.
			BillboardDelegate1.getInstance().completeBillboard();
			BillboardDelegate2.getInstance().completeBillboard();
			PdpDelegate.getInstance().completePdp();

		} else {
			if (SettingApplication.getInstance().isUseSoundAuction()) {
				if (mBtnSpace.getUserData() != null && !mBtnSpace.getUserData().toString().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
					// 경매 진행중 아니면.. 프로그램 종료
					onCloseApplication();
				} else {
					onCloseApplication();
				}

			} else {
				// 경매 진행중 아니면.. 프로그램 종료
				onCloseApplication();
			}
		}
	}

	@Override
	void onCancel() {
		if (isCancel) {
			return;
		}

		mLogger.debug("[출품 취소 처리 시작] " + isCancel);
		isStartSoundPlaying = false;
		isCancel = true;

		stopAllSound();
		stopStartAuctionSecScheduler();

		// 정지
		onPause();
		// 음성경매에서 + 눌러 단일경매로 시작한 경우
		if (!SettingApplication.getInstance().isUseSoundAuction() && isPlusKeyStartAuction) {
			toggleAuctionType();
			isPlusKeyStartAuction = false;
			// ENTER 경매 시작으로.
			Platform.runLater(() -> {
				mBtnEnter.setText(mResMsg.getString("str.btn.start"));
				CommonUtils.getInstance().removeStyleClass(mBtnEnter, "btn-auction-stop");
			});

		}

		// 서버로 취소결과 전달.
		saveAuctionResult(false, mCurrentSpEntryInfo, null, GlobalDefineCode.AUCTION_RESULT_CODE_CANCEL);
		mBiddingInfoTableView.setDisable(false);
		// 전광판 전달.
		BillboardDelegate1.getInstance().completeBillboard();
		BillboardDelegate2.getInstance().completeBillboard();
		PdpDelegate.getInstance().completePdp();
	}

	@Override
	void onClose() {
		if (SettingApplication.getInstance().isUseSoundAuction()) {
			if (mBtnSpace.getUserData() != null && !mBtnSpace.getUserData().toString().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
				// 경매 진행중 아니면.. 프로그램 종료
				onCloseApplication();
			} else {
				onCloseApplication();
			}
		} else {
			// 경매 진행중 아니면.. 프로그램 종료
			onCloseApplication();
		}
	}

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
						exeCalculationRankServiceShutDown();
						MoveStageUtil.getInstance().moveAuctionType(mStage);
					});
				}
			});
		} else {
			Platform.runLater(() -> {
				CommonUtils.getInstance().dismissLoadingDialog();
				exeCalculationRankServiceShutDown();
				MoveStageUtil.getInstance().moveAuctionType(mStage);
			});
		}
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
	public void onStartAndStopAuction(int countDown) {

		// 출품 이관 체크
		if (!isSendEnterInfo()) {
			showAlertPopupOneButton(mResMsg.getString("msg.auction.send.need.entry.data"));
		}

		// 음성경매 상황에서 ENTER 누른 경우.
		if (SettingApplication.getInstance().isUseSoundAuction()) {
			if (!isStartedAuction) {
				showAlertPopupOneButton(mResMsg.getString("dialog.auction.sound"));
			}
			return;
		}

		// 정지상태인경우
		if (isPause) {
			return;
		}

		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_READY: // 준비,경매완료,유찰 상황에서 시작 가능.
		case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
		case GlobalDefineCode.AUCTION_STATUS_PASS:
			refreshAndStart();
			break;
		case GlobalDefineCode.AUCTION_STATUS_START: // 경매 진행 상황에서 낙찰 예정자가 있으면 경매 완료, 없으면 카운트다운 보냄.
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:

			if (isAuctionComplete) {
				// isAuctionComplete : true , 낙찰 예정자 있으면 경매 결과,DB 저장
				sendAuctionResultInfo();
			} else {
				// 종료 카운트다운 시작.
				onStopAuction(countDown);
			}
			break;
		}
	}

	/**
	 * 갱신 + 경매 시작
	 */
	private void refreshAndStart() {

		if (isStartedAuction) {
			return;
		}

		mAuctionStatus.setState(GlobalDefineCode.AUCTION_STATUS_READY);
		// 경매 뷰 초기화
		setAuctionVariableState(mAuctionStatus.getState());

		Thread thread = new Thread() {
			@Override
			public void run() {
				// 갱신 후 변경점 있으면 서버 전달.
				refreshWaitEntryDataList(true,REFRESH_ENTRY_LIST_TYPE_START);
			}
		};

		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * 사운드 경매 시작
	 */
	public void onStartSoundAuction() {

		// 출품 이관 체크
		if (!isSendEnterInfo()) {
			showAlertPopupOneButton(mResMsg.getString("msg.auction.send.need.entry.data"));
		}

		// 단일 경매 상황에서 ENTER 누른 경우.
		if (!SettingApplication.getInstance().isUseSoundAuction()) {
			if (!isStartedAuction) {
				showAlertPopupOneButton(mResMsg.getString("dialog.auction.no.sound"));
			}
			return;
		}

		if (mBtnSpace.isDisable()) {
			return;
		}

		if (isPause) {
			return;
		}

		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_READY:
		case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
		case GlobalDefineCode.AUCTION_STATUS_PASS:

			refreshAndStart();

			break;
		}
	}

	/**
	 * 경매 결과 전송, DB 저장
	 */
	public void sendAuctionResultInfo() {

		// 결과 전송~ 다음 경매 준비 까지 방어 플래그
		isResultCompleteFlag = true;
		mBtnF6.setDisable(true);

		// 사운드 경매 타이머 정지
		mLogger.debug("[sendAuctionResultInfo 경매 결과 전송, DB 저장 사운드 초기화]");

		stopAutoAuctionScheduler();

		if (mRank_1_User != null) {
			// 1순위자 체크
			if (!CommonUtils.getInstance().isEmptyProperty(mRank_1_User.getAuctionJoinNum())) {
				// 1순위 있으면 낙찰
				saveAuctionResult(true, mCurrentSpEntryInfo, mRank_1_User, GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS);
			} else {
				// 없는경우 유찰
				saveAuctionResult(false, mCurrentSpEntryInfo, mRank_1_User, GlobalDefineCode.AUCTION_RESULT_CODE_PENDING);
			}
		} else {
			// 없는경우 유찰
			saveAuctionResult(false, mCurrentSpEntryInfo, mRank_1_User, GlobalDefineCode.AUCTION_RESULT_CODE_PENDING);
		}
	}

	/**
	 * 1.준비 2.시작
	 */
	public void onStartAuction() {

		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_READY:
		case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
		case GlobalDefineCode.AUCTION_STATUS_PASS:

			// 취소 플래그
			isCancel = false;
			isStartSoundPlaying = true;
			// 경매 시작 플래그
			isStartedAuction = true;
			// 낙찰 후 경매 자동 시작 플래그
			isAutoPlay = false;

			setCurrentEntrySoundData();

			// 결장 응찰가 없음.
			if (CommonUtils.getInstance().isEmptyProperty(mCurrentSpEntryInfo.getLowPrice()) || mCurrentSpEntryInfo.getLowPriceInt() <= 0) {

				if (SettingApplication.getInstance().isUseSoundAuction()) {
					// 결장 사운드 시작
					SoundUtil.getInstance().playCurrentEntryMessage(new PlaybackListener() {
						@Override
						public void playbackFinished(PlaybackEvent evt) {

							isStartSoundPlaying = false;

							setAuctionVariableState(GlobalDefineCode.AUCTION_STATUS_READY);
							// 다음 번호 이동
							selectIndexWaitTable(1, false);

						}
					});

				} else {
					// 음성경매에서 + 눌러 단일경매로 시작한 경우
					if (isPlusKeyStartAuction) {
						toggleAuctionType();
						isPlusKeyStartAuction = false;
					}
					isStartSoundPlaying = false;
					setAuctionVariableState(GlobalDefineCode.AUCTION_STATUS_READY);
					// 다음 번호 이동
					selectIndexWaitTable(1, false);
				}

				return;
			}

			// 시작 로그 msg
			String msgStart = String.format(mResMsg.getString("msg.auction.send.start"), mCurrentSpEntryInfo.getEntryNum().getValue());

			// 시작 서버로 Start 보냄.
			addLogItem(msgStart + AuctionDelegate.getInstance().onStartAuction(mCurrentSpEntryInfo.getEntryNum().getValue()));

			System.out.println("[출품정보 음성 읽기 전 isCancel] " + isCancel);

			AudioFilePlay.getInstance().setTargetPlay(this.getClass().getResource(AudioPlayTypes.which(AudioPlayTypes.START)).toExternalForm(), new AudioPlayListener() {
				@Override
				public void onPlayReady(AudioFilePlay audioFilePlay, MediaPlayer mediaPlayer) {
					mLogger.info("START TTS 재생이 준비되었습니다.");
					mLogger.info("START TTS 재생 시간 : " + AudioFilePlay.getInstance().getPlayDuration());
					AudioFilePlay.getInstance().playSound();
				}

				@Override
				public void onPlayCompleted() {
					mLogger.info("START Auction TTS 재생이 완료되었습니다. 취소 여부 : " + isCancel);
					if (SettingApplication.getInstance().isUseSoundAuction()) {
						playStartCurrentEntrySound();
					} else {
						isStartSoundPlaying = false;
					}
				}
			});

			break;
		}
	}

	/**
	 * 출품정보 사운드
	 */
	private void playStartCurrentEntrySound() {

		if (isPause) {
			System.out.println("[일시 정지 상태. 출품 정보 읽지 않음.]");
			return;
		}

		// 출품 정보 읽음.
		SoundUtil.getInstance().playCurrentEntryMessage(new PlaybackListener() {
			@Override
			public void playbackFinished(PlaybackEvent evt) {

				System.out.println("[출품정보 음성 읽음.]");
				isStartSoundPlaying = false;

				if (isCancel) {
					return;
				}
				// 음성 경매시 종료 타이머 시작.
				System.out.println("[출품정보 음성 읽음. 정지 타이머 실행]");
				soundAuctionTimerTask();
			}
		});

	}

	/**
	 * 종료
	 *
	 * @param countDown
	 */
	public void onStopAuction(int countDown) {

		// 현재 경매 대상이 없으면 ..
		if (mCurrentSpEntryInfo == null) {
			return;
		}

		// 카운트다운 중에는 막음.
		// 카운트다운 안내 멘트 송출 중에 숫자키로 카운트다운 재실행 시 중복 실행 방지를 위해 제거
//		if (isCountDownRunning) {
//			return;
//		}

		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_START:
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
			// 카운트 시작 플래그 . onAuctionCountDown 에서 하는게 맞지만 0으로 보낼경우 임의 시작 처리 하기 위함. 방어코드
			isCountDownRunning = true;
			// 카운트 시간
			mRemainingTimeCount = countDown;
			// 남은 시간별 라벨 표시
			setCountDownLabelState(countDown, true);
			// 서버로 정지 전송. 카운트 시작.
			addLogItem(mResMsg.getString("msg.auction.send.complete") + AuctionDelegate.getInstance().onStopAuction(mCurrentSpEntryInfo.getEntryNum().getValue(), countDown));
			break;
		}

	}

	/**
	 * 강제 낙찰
	 */
	public void onSuccessAuction() {

		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_START:
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
			// 카운트 다운 중일 경우 실행 안 함
			// 정지상태인경우 실행 안함
			if (isCountDownRunning || mCountDownLabel.isVisible() || isPause) {
				return;
			}

			if (!isAuctionComplete) {
				if (!CommonUtils.getInstance().isListEmpty(mBiddingUserInfoDataList)) {
					// 1순위 회원
					SpBidding rank_1_user = mBiddingUserInfoDataList.get(0);
					setSuccessUser(rank_1_user);
				} else {
					setSuccessUser(null);
				}

			} else {
				switch (mAuctionStatus.getState()) {
				case GlobalDefineCode.AUCTION_STATUS_START: // 경매 진행 상황에서 낙찰 예정자가 있으면 경매 완료, 없으면 카운트다운 보냄.
				case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
					sendAuctionResultInfo();
					break;
				}
			}
			break;
		}
	}

	/**
	 * 강제유찰
	 *
	 */
//	public void onPassAuction() {
//
//		switch (mAuctionStatus.getState()) {
//		case GlobalDefineCode.AUCTION_STATUS_START:
//		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
//			
//			if(isCountDownRunning) {
//				return;
//			}
//			
//			if(!isAuctionComplete) {
//				auctionResult(true);
//			}else {
//				onStartAndStopAuction(0);
//			}
//			break;
//		}
//	}

	public void checkAndOnPause() {

		if (isAuctionComplete) {
			return;
		}

		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_START:
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:

			if (!isPause) {
				isPause = true;
				mBtnReStart.setDisable(false);
				mBtnPause.setDisable(true);

				// 자동경매 카운트다운중인경우 스케줄러 멈춤.
				if (SettingApplication.getInstance().isUseSoundAuction()) {
					stopAutoAuctionScheduler();
					mBtnSpace.setUserData("");
				}

				onPause();
			}
		}
	}

	/**
	 * 경매 진행 -> 카운트 다운 일시 정지.
	 */
	public void onPause() {
		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_START:
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:

			if(isStartSoundPlaying) {
				SoundUtil.getInstance().stopSound();
			}
		
//			isOverPricePlaySound = false;
//			isPlayReAuctionSound = false;

			// 응찰영역 카운트다운 라벨 숨김.
//			if (mCountDownLabel.isVisible()) {
//				mCountDownLabel.setVisible(false);
//			}

			addLogItem("카운트 다운 정지 : " + AuctionDelegate.getInstance().onPause(new PauseAuction(mCurrentSpEntryInfo.getAuctionHouseCode().getValue(), mCurrentSpEntryInfo.getEntryNum().getValue())));
		}
	}

	/**
	 * 경매 진행 -> 카운트 다운 일시 정지. 경매 진행 -> 카운트 다운 시작.
	 */
	public void onReStart() {
		
		if(!isPause) {
			return;
		}

		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_START:
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:

			mBtnReStart.setDisable(true);
			mBtnPause.setDisable(false);
			isPause = false;
			mLogger.debug("isStartSoundPlaying : " + isStartSoundPlaying);
			// 출품정보 읽는 도중 정지눌렀다가 다시 시작 하는경우. 다시 읽음
			if (isStartSoundPlaying) {
				playStartCurrentEntrySound();
			} else {
				isRestart = true;

				if (SettingApplication.getInstance().isUseSoundAuction()) {

					if (!CommonUtils.getInstance().isListEmpty(mBiddingUserInfoDataList)) {

						// 가격 체크
						if (!checkOverPrice(mBiddingUserInfoDataList.get(0))) {
							
							if (mBiddingUserInfoDataList.get(0).getAuctionJoinNum() != null && CommonUtils.getInstance().isValidString(mBiddingUserInfoDataList.get(0).getAuctionJoinNum().getValue())) {
								playOverPriceSound(mBiddingUserInfoDataList.get(0).getAuctionJoinNum().getValue());
							} else {
								
								stopAuctionFromReStart();
							}
							
						} else {
							
							
							if(isReAuction) {
								
								mLogger.debug("[재시작 재경매 상태] : " + isReAuction);
								
								if(!CommonUtils.getInstance().isListEmpty(mBiddingUserInfoDataList)){
									
									if(mBiddingUserInfoDataList.size() > 1) {
										
										SpBidding rank_1 = mBiddingUserInfoDataList.get(0);
										SpBidding rank_2 = mBiddingUserInfoDataList.get(1);
										
										mLogger.debug("[재시작 1,2순위 금액 확인] : 1순위자: " + rank_1.getPriceInt() + " / 2순위자: " + rank_2.getPriceInt());
										
										// 1순위와 같은 가격 목록
										if (rank_1.getPriceInt() != rank_2.getPriceInt()) {
											
											stopAuctionFromReStart();
											
										}
									}
								}
								
								
							}else {
								
								stopAuctionFromReStart();
						
							}
						
						}
					} else {
						
						stopAuctionFromReStart();
							
					}

				} else {

					if (mCountDownLabel.isVisible()) {
						onStopAuction(mRemainingTimeCount);
					}

					isRestart = false;
				}
			}
		}
	}
	
	
	private void stopAuctionFromReStart() {
		
		if (!mCountDownLabel.isVisible()) {
			if(!isPause) {
		    	startAutoAuctionScheduler(mRemainingTimeCount);
			}
		}else {
			SoundUtil.getInstance().playSound(String.format(mResMsg.getString("str.sound.auction.countdown"), mRemainingTimeCount), new PlaybackListener() {
				@Override
				public void playbackFinished(PlaybackEvent evt) {
					if(!isPause) {
						onStopAuction(mRemainingTimeCount);
					}
					isRestart = false;
				}
			});
		}
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
					addLogItem(String.format(mResMsg.getString("msg.auction.send.message"), str));
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
	 * 대기중인 테이블 뷰 클릭
	 *
	 * @param event
	 */
	public void onClickWaitTableView() {
		
		// 우클릭 새로고침 창 숨김.
		if (mRefreshContextMenu != null && mRefreshContextMenu.isShowing()) {
			mRefreshContextMenu.hide();
		}
		
		setCurrentEntryInfo(true);
	}

	/**
	 * 경매 출품 데이터
	 * 2022.01.13 경매일선택에서 조회된 출장우 데이터로 수정
	 */
	private void requestEntryData() {

		mCurPageType = EntryDialogType.ENTRY_LIST;
		
		if(!CommonUtils.getInstance().isListEmpty(mTmpCowDataList)) {
			Platform.runLater(() -> {
					mLogger.debug("[출장우 정보 조회 데이터 수] " + mTmpCowDataList.size());
					mWaitEntryInfoDataList.clear();
					mWaitEntryInfoDataList = getParsingCowEntryDataList(mTmpCowDataList);
					initFinishedEntryDataList();
					initWaitEntryDataList(mWaitEntryInfoDataList);
					setCowTotalCount(mTmpCowDataList.size());
					mTmpCowDataList = null;
			});
		}else {
			Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
		}
	}
	
	/**
	 * 출장우 개체 총 수
	 * @param totalCount
	 */
	private void setCowTotalCount(int totalCount) {
		mAuctionInfoTotalCountLabel.setText(String.format(mResMsg.getString("str.total.cow.count"), totalCount));
	}

	/**
	 * 예정가 높이기
	 *
	 * @param event
	 */
	public void onUpPrice(MouseEvent event) {
	
		long upPrice = SettingApplication.getInstance().getCowLowerLimitPrice(Integer.parseInt(mCurrentSpEntryInfo.getEntryType().getValue()));
		
		System.out.println("예정가 높이기 : "  + upPrice + " / 구분코드 :  " + mCurrentSpEntryInfo.getEntryType().getValue());
		
		setLowPrice(upPrice, true);
	}

	/**
	 * 예정가 낮추기
	 *
	 * @param event
	 */
	public void onDownPrice(MouseEvent event) {

		long lowPrice = SettingApplication.getInstance().getCowLowerLimitPrice(Integer.parseInt(mCurrentSpEntryInfo.getEntryType().getValue())) * -1;
		
		System.out.println("예정가 낮추기 : " + lowPrice + " / 구분코드 : " + mCurrentSpEntryInfo.getEntryType().getValue());
		
		setLowPrice(lowPrice, false);
	}

	/**
	 * 예정가 Set
	 *
	 * @param price
	 */
	private void setLowPrice(long price, boolean isUp) {

		Platform.runLater(() -> {
			// 현재 선택된 row
			SpEntryInfo spEntryInfo = mWaitTableView.getSelectionModel().getSelectedItem();

			String targetEntryNum = spEntryInfo.getEntryNum().getValue();
			String targetAuctionHouseCode = spEntryInfo.getAuctionHouseCode().getValue();
			String targetEntryType = spEntryInfo.getEntryType().getValue();
			String targetAucDt = spEntryInfo.getAucDt().getValue();
			long targetPrice = spEntryInfo.getLowPriceInt();
			String oslpNo = spEntryInfo.getOslpNo().getValue();
			String ledSqNo = spEntryInfo.getLedSqno().getValue();
			int lowPriceCnt = Integer.parseInt(spEntryInfo.getLwprChgNt().getValue());

			if (isUp) {
				lowPriceCnt -= 1;
			} else {
				lowPriceCnt += 1;
			}

			String updatePrice = Long.toString(targetPrice + price);

			EntryInfo entryInfo = new EntryInfo();
			entryInfo.setEntryNum(targetEntryNum);
			entryInfo.setAuctionHouseCode(targetAuctionHouseCode);
			entryInfo.setEntryType(targetEntryType);
			entryInfo.setAucDt(targetAucDt);
			entryInfo.setLowPrice(Integer.parseInt(updatePrice));
			entryInfo.setOslpNo(oslpNo);
			entryInfo.setLedSqno(ledSqNo);
			entryInfo.setLsCmeNo(GlobalDefine.ADMIN_INFO.adminData.getUserId());
			entryInfo.setLwprChgNt(lowPriceCnt);

			if (updatePrice == null || updatePrice.isEmpty() || Integer.parseInt(updatePrice) < 0) {
				// 가격정보 null, 0보다 작으면 리턴
				mLogger.debug("하한가 낮추기 => 0원 미만은 낮출 수 없습니다.");
				return;
			}

			if (updatePrice == null || updatePrice.isEmpty() || Integer.parseInt(updatePrice) > Integer.parseInt(SettingApplication.getInstance().DEFAULT_SETTING_UPPER_CFB_MAX)) {
				// 가격정보 null, 0보다 작으면 리턴
				mLogger.debug("하한가 높이기 => 99999원 이상 높일 수 없습니다.");
				return;
			}

			ArrayList<EntryInfo> entryInfoList = new ArrayList<>();
			entryInfoList.add(entryInfo);

			Gson gson = new Gson();
			String jonData = gson.toJson(entryInfoList);
			
			mLogger.debug("[가격변경]=> " + jonData );

			RequestUpdateLowsBidAmtBody body = new RequestUpdateLowsBidAmtBody(jonData);

			ApiUtils.getInstance().requestUpdateLowsBidAmt(body, new ActionResultListener<ResponseNumber>() {

				@Override
				public void onResponseResult(ResponseNumber result) {

					if (result != null && result.getSuccess()) {

						mLogger.debug("[최저가 수정 Success]");

						spEntryInfo.setLowPrice(new SimpleStringProperty(updatePrice));
						spEntryInfo.setLwprChgNt(new SimpleStringProperty(Integer.toString(entryInfo.getLwprChgNt())));
						setCurrentEntryInfo(true);

						String tmpIsLastEntry = spEntryInfo.getIsLastEntry().getValue();

						spEntryInfo.getIsLastEntry().setValue(GlobalDefine.ETC_INFO.AUCTION_DATA_MODIFY_M);

						addLogItem("[가격 변경 정보 보냄]=> " + AuctionDelegate.getInstance().onSendEntryData(spEntryInfo));

						spEntryInfo.getIsLastEntry().setValue(tmpIsLastEntry);

						if (!isUp) {
							long soundPrice = price * -1;
							
							String wonMsg = "";
							
							if(SettingApplication.getInstance().isWon(mCurrentSpEntryInfo.getEntryType().getValue())) {
								wonMsg = mResMsg.getString("str.sound.change.low.price");
							}else {
								wonMsg = mResMsg.getString("str.sound.change.low.price.10000");
							}
							
							SoundUtil.getInstance().playSound(String.format(wonMsg, soundPrice), null);
						}
						
						mWaitTableView.refresh();

						sendBillboardEntryData();
					} else {
						mLogger.debug("[최저가 수정 Fail]");
						Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.change.low.price.fail")));
					}
				}

				@Override
				public void onResponseError(String message) {
					mLogger.debug("[최저가 수정 Fail]");
					Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.change.low.price.fail")));
				}
			});
		});
	}

	// 2000 : 인증 성공
	// 2001 : 인증 실패
	// 2002 : 중복 접속
	// 2003 : 기타 장애
	@Override
	public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo) {
		super.onResponseConnectionInfo(responseConnectionInfo);
		Platform.runLater(() -> {

			CommonUtils.getInstance().dismissLoadingDialog();

			isApplicationClosePopup = false;

			switch (responseConnectionInfo.getResult()) {
			case GlobalDefineCode.CONNECT_SUCCESS:
				addLogItem(mResMsg.getString("msg.connection.success") + responseConnectionInfo.getEncodedMessage());

				// Setting 정보 전송
				onSendSettingInfo();
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
				addLogItem(mResMsg.getString("msg.connection.fail"));
				showAlertPopupOneButton(mResMsg.getString("msg.connection.fail"));
				AuctionDelegate.getInstance().onDisconnect(null);
				break;
			case GlobalDefineCode.CONNECT_DUPLICATE:

				CommonUtils.getInstance().dismissLoadingDialog();
				addLogItem(mResMsg.getString("msg.connection.duplicate"));
				showAlertPopupOneButton(mResMsg.getString("msg.connection.duplicate"));
				AuctionDelegate.getInstance().onDisconnect(null);
				break;
			}

		});
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

	private void onSendSettingInfo() {
		// Setting 정보 전송
		mLogger.debug(mResMsg.getString("msg.auction.send.setting.info") + AuctionDelegate.getInstance().onSendSettingInfo(SettingApplication.getInstance().getSettingInfo()));
	}

	@Override
	public void onCurrentEntryInfo(CurrentEntryInfo currentEntryInfo) {
		super.onCurrentEntryInfo(currentEntryInfo);

		// 회차 다를경우 실행. 하위 코드 실행 X
//		if (GlobalDefine.AUCTION_INFO.auctionRoundData.getQcn() != Integer.parseInt(currentEntryInfo.getAuctionQcn())) {
//			return;
//		}

		if (isFirst) {
			return;
		}

		Platform.runLater(() -> {

			// 21-08-05 경매 도중 프로그램 재 시작시 현재 경매 진행중인 아이템으로 이동.
			// 경매 종료 상태에 따라 라벨 표시
			if (mWaitTableView.getItems().size() <= 0) {
				return;
			}

			String currentEntryNum = currentEntryInfo.getEntryNum();

			if (mCurrentSpEntryInfo != null && mCurrentSpEntryInfo.getEntryNum().getValue().equals(currentEntryNum)) {
				return;
			}

			for (int i = 0; mWaitTableView.getItems().size() > i; i++) {

				String entryNum = mWaitTableView.getItems().get(i).getEntryNum().getValue();

				if (currentEntryNum.equals(entryNum)) {

					System.out.println("[!! 재접속 스크롤 설정] : " + entryNum + " / index : " + i + "/ " + mAuctionStatus.getState());

					mCurrentSpEntryInfo = mWaitTableView.getItems().get(i);

					selectIndexWaitTable(i, true);

				}
			}

			// 응찰내역
			requestSelectBidEntry();
		});

		isFirst = true;
	}

	@Override
	public void onAuctionStatus(AuctionStatus auctionStatus) {
		super.onAuctionStatus(auctionStatus);
		
		// 회차 종료 플래그.
		if(isQcnFinish) {
			return;
		}
	
		mLogger.debug("[회차정보 확인. 현재 qcn : " + GlobalDefine.AUCTION_INFO.auctionRoundData.getQcn()  + " / AS qcn :  " +  Integer.parseInt(auctionStatus.getAuctionQcn()));

		// 회차정보 다를경우 처리
		if ((GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc().equals(auctionStatus.getAuctionHouseCode()))
				&& (GlobalDefine.AUCTION_INFO.auctionRoundData.getQcn() != Integer.parseInt(auctionStatus.getAuctionQcn()))
			|| auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {

			isQcnChange = true;
			
			Platform.runLater(() -> {
		
				Optional<ButtonType> btnResult = CommonUtils.getInstance().showAlertPopupTwoButton(mStage, mResMsg.getString("dialog.change.qcn"), mResMsg.getString("popup.btn.ok"), mResMsg.getString("popup.btn.exit"));

				if (btnResult.get().getButtonData() == ButtonData.LEFT) {
					
					CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.msg.send.data"));
					
					mAuctionStatus.setState(GlobalDefineCode.AUCTION_STATUS_NONE);
					mLogger.debug("[CLEAR INIT SERVER] : " + AuctionDelegate.getInstance().onInitEntryInfo(new InitEntryInfo(auctionStatus.getAuctionHouseCode(), auctionStatus.getAuctionQcn())));
					isSendEntryData = false;
					onSendEntryData();
				} else {

					isApplicationClosePopup = true;
					onServerAndClose();
				}

			});

			return;
		}else {
			isQcnChange = false;
		}

		if (!mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
			// 출장우 정보 보냄 플래그
			isSendEntryData = true;
		}

		setAuctionVariableState(auctionStatus.getState());

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

	/**
	 * 사운드경매(자동경매) 일정 대기시간 후 경매 카운트
	 */
	public void startAutoAuctionScheduler(int countDown) {

		if (isPause || isCountDownRunning || isCountDownBtnPressed || isResultCompleteFlag || isCancel) {
			addLogItem("정지 타이머 실행 안함. isPause : " + isPause + " / isCountDownRunning : " + isCountDownRunning + " / isCountDownBtnPressed : " + isCountDownBtnPressed + " / isResultCompleteFlag : " + isResultCompleteFlag);
			return;
		}

		int waitingTime = SettingApplication.getInstance().getSoundAuctionWaitTime() * 1000;

		if (mAutoStopScheduler != null) {
			addLogItem("정지 타이머 NOT NULL");
			return;
		}

		// 정지 Task
		// waiting time 기다린 후 실행.
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {

				Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());

				addLogItem("Run Stop Scheduler. ");
				addLogItem("isCountDownBtnPressed : " + isCountDownBtnPressed + " isResultCompleteFlag : " + isResultCompleteFlag);

				if (isCountDownBtnPressed || isResultCompleteFlag) {
					stopAutoAuctionScheduler();
					return;
				}

				// 카운트다운 안내 멘트 송출 중에 숫자키로 카운트다운 재실행 시 중복 실행 방지 코드
				if (!isCountDownRunning) {
					isCountDownRunning = true;
				}

				// 카운트다운 사운드
				playCountDownSound(countDown);

			}
		};

		mAutoStopScheduler = new Timer();
		mAutoStopScheduler.schedule(timerTask, waitingTime);
		addLogItem("정지 타이머 시작 : " + waitingTime);
	}

	/**
	 * 카운트다운 사운드
	 * 
	 * @param countDown
	 */
	public void playCountDownSound(int countDown) {

//		if(isPlayCountDownSound) {
//			return;
//		}
//		
		SoundUtil.getInstance().playSound(String.format(mResMsg.getString("str.sound.auction.countdown"), countDown), new PlaybackListener() {
			@Override
			public void playbackFinished(PlaybackEvent evt) {
				super.playbackFinished(evt);
				
				
				if(isPause) {
					return;
				}
				
				if(mBiddingUserInfoDataList != null && mBiddingUserInfoDataList.size() > 0 && mBiddingUserInfoDataList.get(0).getAuctionJoinNum() != null 
						&& mBiddingUserInfoDataList.get(0).getAuctionJoinNum().getValue() != null ) {
					
					if (checkOverPrice(mBiddingUserInfoDataList.get(0))) {
						// 서버로 정지 전송
						onStopAuction(countDown);
					}
					
				}else {
					// 서버로 정지 전송
					onStopAuction(countDown);
				}
			
				isRestart = false;

//				checkBiddingUserPlaySound();

			}
		});

	}

	@Override
	void checkBiddingUserPlaySound() {
		if (mBiddingUserInfoDataList.size() > 0) {
			if (!checkOverPrice(mBiddingUserInfoDataList.get(0))) {
				if (mBiddingUserInfoDataList.get(0).getAuctionJoinNum() != null && CommonUtils.getInstance().isValidString(mBiddingUserInfoDataList.get(0).getAuctionJoinNum().getValue())) {
					stopAutoAuctionScheduler();
					playOverPriceSound(mBiddingUserInfoDataList.get(0).getAuctionJoinNum().getValue());
				}
			}else {
				startAutoAuctionScheduler(SettingApplication.getInstance().getAuctionCountdown());
			}
		}
	}

	/**
	 * 사운드경매(자동경매) 스케줄러 정지
	 */
	public void stopAutoAuctionScheduler() {
		if (mAutoStopScheduler != null) {
			addLogItem("사운드 경매 자동 종료");
			mAutoStopScheduler.cancel();
			mAutoStopScheduler = null;
		}
	}

	/**
	 * 사운드 경매시 응찰 금액에 따라 타이머 동작 제어
	 */
	@Override
	protected synchronized void soundAuctionTimerTask() {

		mLogger.debug("[soundAuctionTimerTask 응찰자 맵 카운트] " + isCancel + " / "  + isPause  );
		
		if (isCancel) {
			return;
		}

		// 응찰자가 있는경우
		if (mCurrentBidderMap.size() > 0) {

			mLogger.debug("[soundAuctionTimerTask 응찰자 맵 카운트] " + mCurrentBidderMap.size());
			// 현재 1순위
			SpBidding rank_1_user = mBiddingUserInfoDataList.get(0);
			// 응찰 가격 조건 체크
			// 최저가 + 상한가 상황이면 사운드 재생 , 경매 카운트 정지.
			if (!checkOverPrice(rank_1_user)) {

				if (isPause) {
					playOverPriceSound(rank_1_user.getAuctionJoinNum().getValue());
					return;
				}

				stopAutoAuctionScheduler();

				addLogItem("soundAuctionTimerTask ==== 사운드 경매 자동 종료");

				// 응찰영역 카운트다운 라벨 숨김.
				if (mCountDownLabel.isVisible()) {
					mCountDownLabel.setVisible(false);
				}

				// 자동경매 카운트다운중인경우 스케줄러 멈춤.
				if (SettingApplication.getInstance().isUseSoundAuction()) {
					addLogItem("soundAuctionTimerTask [타이머 초기화]");

					stopAutoAuctionScheduler();
					mBtnSpace.setUserData("");
				}
				// 타이머 멈춤.
				onPause();

				return;
			} else {
				mLogger.debug("[응찰자 있음. 응찰가격 정상 정지 타이머 실행]");
				// 응찰 가격이 정상인경우 설정 대기시간 기다린 후 경매 정지
				startAutoAuctionScheduler(SettingApplication.getInstance().getAuctionCountdown());
			}
		} else {
			mLogger.debug("[응찰자 없음. 정지 타이머 실행]");
			// 타이머 시작시
			startAutoAuctionScheduler(SettingApplication.getInstance().getAuctionCountdown());
		}
	}

	@Override
	void stopSoundAuctionTimerTask() {
		mLogger.debug("[stopSoundAuctionTimerTask]");
		stopAutoAuctionScheduler();
	}

	@Override
	public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
		super.onAuctionCountDown(auctionCountDown);

		if (auctionCountDown.getStatus().equals(GlobalDefineCode.AUCTION_COUNT_DOWN)) {

			// 카운트다운 시작 플래그
			isCountDownRunning = true;

			Platform.runLater(() -> {

				if (!mCountDownLabel.isVisible()) {
					mCountDownLabel.setVisible(true);
				}

				mCountDownLabel.setText(auctionCountDown.getCountDownTime());
			});

			// 현재 카운트다운 초 저장.
			mRemainingTimeCount = Integer.parseInt(auctionCountDown.getCountDownTime());

			// 카운트다운 라벨 상태
			setCountDownLabelState(mRemainingTimeCount, false);

//			// 전광판 카운트다운 전송
//			BillboardDelegate1.getInstance().onCountDown(auctionCountDown.getCountDownTime());
//			BillboardDelegate2.getInstance().onCountDown(auctionCountDown.getCountDownTime());
//
//			// PDP 카운트다운 전송
//			PdpDelegate.getInstance().onCountDown(auctionCountDown.getCountDownTime());

			// 카운트다운 효과음
			if (isStartSoundPlaying) {
				isStartSoundPlaying = false;
			}

			mLogger.debug("카운트다운 isStartSoundPlaying : " + isStartSoundPlaying);

			AudioFilePlay.getInstance().setTargetPlay(this.getClass().getResource(AudioPlayTypes.which(AudioPlayTypes.DING)).toExternalForm(), new AudioPlayListener() {

				@Override
				public void onPlayReady(AudioFilePlay audioFilePlay, MediaPlayer mediaPlayer) {
					AudioFilePlay.getInstance().playSound();
				}

				@Override
				public void onPlayCompleted() {
					// TODO Auto-generated method stub
				}
			});
		}

		if (auctionCountDown.getStatus().equals(GlobalDefineCode.AUCTION_COUNT_DOWN_COMPLETED)) {

			// 카운트다운 종료 플래그
			isCountDownRunning = false;
			isCountDownBtnPressed = false;

			addLogItem("==== 카운트 다운 완료 ====");

			// 취소 눌린경우 카운트다운 완료가 후에 들어오기 때문에.. 아래 코드 실행 안 함.

			addLogItem("==== 카운트 완료 취소/정지 상태 : " + isCancel + " / " + isPause);

			if (isCancel) {
				return;
			}

			// 정지인경우. 정지 플래그는 reStart 시에 바꿔줌.
			if (isPause) {
				return;
			}

			// 남은시간이 0이거나 현재 카운트다운
			if (mRemainingTimeCount <= 1 || Integer.parseInt(auctionCountDown.getCountDownTime()) <= 0) {
				if (mCountDownLabel.isVisible()) {
					mCountDownLabel.setVisible(false);
				}
			}

			// 음성경매가 아닌경우.
			if (!SettingApplication.getInstance().isUseSoundAuction()) {
				// 카운트다운 종료시 응찰자가 있는경우
				if (mCurrentBidderMap.size() > 0) {
					// 아직 낙찰 확정자가 없으면
					if (!isAuctionComplete) {
						// 낙찰자 저장
						addLogItem("[음성경매 아님 낙찰 확정자 없음]");
						calSuccessfulBidder(false);
					} else {
						// 낙찰자가 있으면
						onStartAndStopAuction(0);
					}
				} else {
					setCountDownLabelState(SettingApplication.getInstance().getAuctionCountdown(), false);
					// 카운트 다운 후 응찰 종료 상태
					sendAuctionBidStatus(false);
				}

			} else {
				// 음성경매인경우
				if (mRemainingTimeCount <= 1 || Integer.parseInt(auctionCountDown.getCountDownTime()) <= 0) {
					addLogItem("==== 음성경매 카운트 다운 완료 ==== : " + mRemainingTimeCount);
					calSuccessfulBidder(false);
				} else {
//                	if (mCountDownLabel.isVisible()) {
//                        mCountDownLabel.setVisible(false);
//                    }
				}
			}

			mRemainingTimeCount = SettingApplication.getInstance().getAuctionCountdown();
		}
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
			}).forEach(System.out::println);

			if (mConnectionUserMap.size() <= 0) {
				sortConnectionUserDataList();
			}

		}
		return true;
	}

	/**
	 * 카운트다운 라벨
	 *
	 * @param countDown
	 */
	private void setCountDownLabelState(int countDown, boolean isInit) {

		if (isInit) {

			switch (countDown) {
			case 5:
				cnt_5.setDisable(false);
				cnt_4.setDisable(false);
				cnt_3.setDisable(false);
				cnt_2.setDisable(false);
				cnt_1.setDisable(false);
				break;
			case 4:
				cnt_5.setDisable(true);
				cnt_4.setDisable(false);
				cnt_3.setDisable(false);
				cnt_2.setDisable(false);
				cnt_1.setDisable(false);
				break;
			case 3:
				cnt_5.setDisable(true);
				cnt_4.setDisable(true);
				cnt_3.setDisable(false);
				cnt_2.setDisable(false);
				cnt_1.setDisable(false);
				break;
			case 2:
				cnt_5.setDisable(true);
				cnt_4.setDisable(true);
				cnt_3.setDisable(true);
				cnt_2.setDisable(false);
				cnt_1.setDisable(false);
				break;
			case 1:
				cnt_5.setDisable(true);
				cnt_4.setDisable(true);
				cnt_3.setDisable(true);
				cnt_2.setDisable(true);
				cnt_1.setDisable(false);
				break;
			case 0:
				cnt_5.setDisable(true);
				cnt_4.setDisable(true);
				cnt_3.setDisable(true);
				cnt_2.setDisable(true);
				cnt_1.setDisable(true);
				break;
			default:
				cnt_5.setDisable(false);
				cnt_4.setDisable(false);
				cnt_3.setDisable(false);
				cnt_2.setDisable(false);
				cnt_1.setDisable(false);
				break;
			}

		} else {
			switch (countDown) {
			case 4:
				cnt_5.setDisable(true);
				cnt_4.setDisable(false);
				cnt_3.setDisable(false);
				cnt_2.setDisable(false);
				cnt_1.setDisable(false);
				break;
			case 3:
				cnt_5.setDisable(true);
				cnt_4.setDisable(true);
				cnt_3.setDisable(false);
				cnt_2.setDisable(false);
				cnt_1.setDisable(false);
				break;
			case 2:
				cnt_5.setDisable(true);
				cnt_4.setDisable(true);
				cnt_3.setDisable(true);
				cnt_2.setDisable(false);
				cnt_1.setDisable(false);
				break;
			case 1:
				cnt_5.setDisable(true);
				cnt_4.setDisable(true);
				cnt_3.setDisable(true);
				cnt_2.setDisable(true);
				cnt_1.setDisable(false);
				break;
			case 0:
				cnt_5.setDisable(true);
				cnt_4.setDisable(true);
				cnt_3.setDisable(true);
				cnt_2.setDisable(true);
				cnt_1.setDisable(true);
				break;
			default:
				cnt_5.setDisable(false);
				cnt_4.setDisable(false);
				cnt_3.setDisable(false);
				cnt_2.setDisable(false);
				cnt_1.setDisable(false);
				break;
			}
		}
	}

	/**
	 * 낙유찰 및 재경매 설정
	 *
	 * @param isPass : 강제 유찰 : true
	 */
	private void calSuccessfulBidder(boolean isPass) {

		addLogItem("[낙유찰 및 재경매 설정]");
		
		// 응찰자 여부
		if (mCurrentBidderMap.size() > 0) {
			// 응찰 가격 조건 체크
			if (!isBidderPriceValid()) {
				sendAuctionBidStatus(false);
				return;
			}
		} else {
			// 응찰자가 없는경우.
			setSuccessUser(null);
			return;
		}

		// 응찰자 리스트 체크
		if (CommonUtils.getInstance().isListEmpty(mBiddingUserInfoDataList)) {
			return;
		}

		// 응찰자 존재. 응찰가격 정상 이후 체크 ===============

		// 1순위 회원
		SpBidding rank_1_user = mBiddingUserInfoDataList.get(0);

		// 동일가 재경매 설정.
		// 설정 플래그 ,횟수 체크
		if (SettingApplication.getInstance().isUseReAuction() && SettingApplication.getInstance().getReAuctionCount() > 0) {

			if (mReAuctionCount < 0) {
				// 재경매 횟수 +
				mReAuctionCount = SettingApplication.getInstance().getReAuctionCount();
			} else {
				// 카운트다운마다 재경매 횟수 -
				mReAuctionCount--;
			}

			// 재경매 횟수가 없으면 1순위 응찰자 낙/유찰 처리
			if (mReAuctionCount <= 0) {
				if (!CommonUtils.getInstance().isListEmpty(mBiddingUserInfoDataList)) {
					// 1순위 낙/유찰 처리
					setSuccessUser(rank_1_user);
				} else {
					// 카운트 다운 후 응찰 종료 상태
					sendAuctionBidStatus(false);
					addLogItem("==== 응찰자.. 없으면 오류... 무조건..있음....있어야 됨 ");
				}

				return;
			}

			addLogItem("==== 재경매 횟수 ====: " + mReAuctionCount);

			// 재경매 횟수가 남아있고 응찰자가 한명 이상이면 비교 시작..
			if (mBiddingUserInfoDataList.size() > 1) {

				// 재경매자 목록 초기화
				mReAuctionBidderDataList.clear();

				for (SpBidding spBidding : mBiddingUserInfoDataList) {

					if (rank_1_user.getAuctionJoinNum().equals(spBidding.getAuctionJoinNum())) {
						continue;
					}

					// 1순위와 같은 가격 목록
					if (rank_1_user.getPriceInt() == spBidding.getPriceInt()) {
						mReAuctionBidderDataList.add(spBidding);
					}
				}

				// 동가 없으면 낙찰 처리
				if (mReAuctionBidderDataList.size() <= 0) {
					addLogItem("[동가 없음 낙찰 처리]");
					setSuccessUser(rank_1_user);
				} else {

					// 재경매 여부
					isReAuction = true;

					// 1순위 넣어줌
					mReAuctionBidderDataList.add(0, rank_1_user);

					// 재경매 대상자
					StringBuffer strReAuctionBidder = new StringBuffer();
					strReAuctionBidder.append(mReAuctionBidderDataList.stream().map(v -> v.getAuctionJoinNum().getValue()).collect(Collectors.joining(",")));
					// 재경매자 목록 보냄
					addLogItem("재경매 대상자 보냄 : " + AuctionDelegate.getInstance().onRetryTargetInfo(new RetryTargetInfo(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc(), mCurrentSpEntryInfo.getEntryNum().getValue(), strReAuctionBidder.toString())));

					//재경매자 음성 play
					playReAuctionSound();

					Platform.runLater(() -> {
						// 재경매중 라벨 보이게.
						if (!mReAuctionLabel.isVisible()) {
							mReAuctionLabel.setVisible(true);
						}
						// 재경매 카운트 라벨
						mReAuctionCountLabel.setText(Integer.toString(mReAuctionCount));
					});

					addLogItem("[재경매자 체크 끝]");

					// 카운트 다운 후 응찰 종료 상태
					sendAuctionBidStatus(false);

				}

			} else {
				setSuccessUser(rank_1_user);
			}

		} else {
			// 동일가 재경매 설정 X 낙찰예정자 처리
			setSuccessUser(rank_1_user);
		}
	}

	/**
	 * 재경매자 음성 play
	 */
	private void playReAuctionSound() {

		// 사운드 경매인경우 재경매자 음성 시작
		if (SettingApplication.getInstance().isUseSoundAuction() && !CommonUtils.getInstance().isListEmpty(mReAuctionBidderDataList)) {

			StringBuffer stringBuffer = new StringBuffer();

			for (SpBidding bidder : mReAuctionBidderDataList) {
				stringBuffer.append(String.format(mResMsg.getString("str.sound.user.number"), bidder.getAuctionJoinNum().getValue()));
			}

			stringBuffer.append(mResMsg.getString("str.sound.user.sam.price"));
			stringBuffer.append(stringBuffer.toString());
			stringBuffer.append(mResMsg.getString("str.sound.user.re.auction"));

			addLogItem("[재경매 중 타이머 초기화]");

			stopAutoAuctionScheduler();

			isPlayReAuctionSound = true;

			SoundUtil.getInstance().playSound(stringBuffer.toString(), new PlaybackListener() {
				@Override
				public void playbackFinished(PlaybackEvent evt) {
					
					isPlayReAuctionSound = false;
				
					// 1순위 회원
					SpBidding rank_1_user = mBiddingUserInfoDataList.get(0);

					// 최저가 + 상한가 / 응찰가 비교.
					// 이상인 경우 팝업.
					if (!checkOverPrice(rank_1_user)) {
						// 낙찰금액을 확인해주세요.
						playOverPriceSound(rank_1_user.getAuctionJoinNum().getValue());
						
					}else {
						if(isReAuctionNewBidding) {
							//경매 정지 주석 처리
							//경매 정지 주석 해제 2021-12-06
							startAutoAuctionScheduler(SettingApplication.getInstance().getAuctionCountdown());
							isReAuctionNewBidding = false;
						}
						
					}	
				}
			});
		}
	}

	/**
	 * 1순위 정보 저장
	 *
	 * @param rank_1_user
	 */
	private void setSuccessUser(SpBidding rank_1_user) {

		// 카운트 다운 후 응찰 종료 상태
		sendAuctionBidStatus(true);

		setCountDownLabelState(0, false);

		// 1순위자
		mRank_1_User = rank_1_user;
		// 1순위 낙찰 예정자 플래그
		isAuctionComplete = true;
		// 재경매 여부
		isReAuction = false;
		// 재경매 현재 횟수
		mReAuctionCount = -1;

		mBiddingInfoTableView.refresh();

		Platform.runLater(() -> {
			// 재경매 라벨, 카운트 숨김.
			if (mReAuctionLabel.isVisible()) {
				mReAuctionLabel.setVisible(false);
				mReAuctionCountLabel.setText("");
			}
		});

		// 낙찰 예정자 확인용 로그.
		if (rank_1_user != null) {
			if (rank_1_user.getAuctionJoinNum() != null) {
				addLogItem("[낙찰 예정자 번호] : " + rank_1_user.getAuctionJoinNum().getValue());
			} else {
				addLogItem("[낙찰 예정자 없음]");
			}
		} else {
			addLogItem("[낙찰 예정자 없음]");
		}

		// 단일경매일 경우 한번더 카운팅이나 enter시 경매 결과 전송 처리함.
		// 음성경매 중이면 경매 결과 전송 자동 실행
		if (SettingApplication.getInstance().isUseSoundAuction()) {
			// 경매 결과 전송
			sendAuctionResultInfo();
		}

	}

	/**
	 * 카운트 다운 후 응찰 종료 상태
	 * 
	 * @param isFinish
	 */
	private void sendAuctionBidStatus(boolean isFinish) {

		// 카운트 다운 후 응찰 종료 상태
		AuctionBidStatus auctionBidStatus = new AuctionBidStatus();
		auctionBidStatus.setAuctionHouseCode(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc());
		auctionBidStatus.setEntryNum(mCurrentSpEntryInfo.getEntryNum().getValue());

		if (!isFinish) {
			auctionBidStatus.setStatus(GlobalDefine.AUCTION_INFO.AUCTION_BID_STATUS_P);
		} else {
			auctionBidStatus.setStatus(GlobalDefine.AUCTION_INFO.AUCTION_BID_STATUS_F);
		}

		addLogItem("[카운트다운->응찰상태 전송] : " + AuctionDelegate.getInstance().sendMessage(auctionBidStatus));

	}

	/**
	 * 경매 진행 완료시
	 */
	private void biddingInfoTableStyleToggle() {

		mBiddingInfoTableView.setRowFactory(tv -> {

			TableRow<SpBidding> row = new TableRow<>();

			if (isAuctionComplete) {

				BooleanBinding critical = row.itemProperty().isEqualTo(mBiddingInfoTableView.getItems().get(0));

				row.styleProperty().bind(Bindings.when(critical).then("-fx-background-color: #ffe135 ;").otherwise("-fx-background-color: red ;"));
			}
			return row;
		});
	}

	/**
	 * 경매 준비 뷰 초기화
	 */
	private void setAuctionVariableState(String code) {

		if (isResultCompleteFlag) {
			return;
		}

//		Platform.runLater(() -> {
		// 버튼들
		btnToggle();

		switch (code) {

		case GlobalDefineCode.AUCTION_STATUS_READY:

			System.out.println("#### AUCTION_STATUS_READY ####");
			// 타이머 초기화
			stopAutoAuctionScheduler();
			// 응찰 쓰레드폴 init
			initExecutorService();
			// 카운트 시간 초기화
			mRemainingTimeCount = SettingApplication.getInstance().getAuctionCountdown();
			// 현재 응찰 내역 초기화
			mCurrentBidderMap.clear();
			// 이전 응찰 내역 초기화
			mBeForeBidderDataList.clear();
			// 대기 라벨 비활성화
			mAuctionStateReadyLabel.setDisable(false);
			// 진행 라벨 비활성화
			mAuctionStateProgressLabel.setDisable(true);
			// 완료 라벨 비활성화
			mAuctionStateSuccessLabel.setDisable(true);
			// 유찰(보류) 라벨 비활성화
			mAuctionStateFailLabel.setDisable(true);
			// 출품 대기 테이블 활성화
			mWaitTableView.setDisable(false);
			// 응찰 테이블 활성화
			mBiddingInfoTableView.setDisable(false);
			// 경매 재경매 횟수
			mReAuctionCount = 0;
			// 1순위 회원
			mRank_1_User = null;
			// 유찰(보류) 여부 초기화
			mIsPass = false;
			// 경매 1건 종료 여부
			isAuctionComplete = false;
			// 재경매 여부
			isReAuction = false;
			// 재경매중 라벨 숨김.
			mReAuctionLabel.setVisible(false);
			// 카운트 다운 라벨
			mCountDownLabel.setVisible(false);
			// 재경매 횟수 초기화
			mReAuctionCount = -1;
			// 재경매자 목록 초기화
			mReAuctionBidderDataList.clear();
			// 경매 시작 여부
			isStartedAuction = false;
			// 취소 여부
//				isCancel = false;
			// 정지 여부
			isPause = false;
			// 결과 전송~ 다음 경매 준비 까지 방어 플래그 초기화
			isResultCompleteFlag = false;
			// 카운트다운 키 눌림 여부
			isCountDownBtnPressed = false;
			// 정지,다시시작 버튼
			mBtnReStart.setDisable(true);
			mBtnPause.setDisable(false);
			// 최고가 이상 사운드 플래그
			isOverPricePlaySound = false;
			// 재경매 사운드 플래그
			isPlayReAuctionSound = false;
			// 경매 시작 ~ 경과시간 초
			mStartAuctionSec = 0;
			//재경매시 응찰 여부
			isReAuctionNewBidding = false;
			break;
		// case GlobalDefineCode.AUCTION_STATUS_START:
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:

			Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());

			// 경매 경과 시간 타이머 시작
			startAuctionSecScheduler();

			// 사운드 경매인 경우 타이머 시작.
			if (SettingApplication.getInstance().isUseSoundAuction() && !isStartSoundPlaying) {
				// 음성 경매시 종료 타이머 시작.
				System.out.println("[경매 상태에서. 정지 타이머 실행]");
				soundAuctionTimerTask();
			}

			// 대기 라벨 비활성화
			mAuctionStateReadyLabel.setDisable(true);
			// 진행 라벨 활성화
			mAuctionStateProgressLabel.setDisable(false);
			// 낙찰 라벨 비활성화
			mAuctionStateSuccessLabel.setDisable(true);
			// 유찰 라벨 비활성화
			mAuctionStateFailLabel.setDisable(true);
			break;
		case GlobalDefineCode.AUCTION_STATUS_PASS:
		case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
			isCancel = false;
			isStartSoundPlaying = false;

			// 경매 경과 시간 타이머 종료
			stopStartAuctionSecScheduler();
			break;
		case GlobalDefineCode.AUCTION_STATUS_FINISH:
			break;
		}

		Platform.runLater(() -> {
			// 경매 상태 - 경매 대기
			if (code.equals(GlobalDefineCode.AUCTION_STATUS_READY)) {
				// 카운트다운 라벨 초기화
				setCountDownLabelState(SettingApplication.getInstance().getAuctionCountdown(), true);
				// 응찰자 초기화
				initBiddingInfoDataList();
				// 응찰자 테이블
				mBiddingInfoTableView.refresh();
				// 재경매중 카운트 초기화.
				mReAuctionCountLabel.setText("");
				// 경매 상태 문구 -> 경매대기
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.auction.ready"));

				mAuctionStateGridPane.getStyleClass().clear();
				CommonUtils.getInstance().addStyleClass(mAuctionStateGridPane, "bg-color-008fff");

				// 경매 시작 후 경과 시간
				mAuctionSecLabel.setText(String.format(mResMsg.getString("str.start.auction.sec"), mStartAuctionSec));

			} else if (code.equals(GlobalDefineCode.AUCTION_STATUS_START) || code.equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
				// 경매 상태 문구 -> 경매진행
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.auction.progress"));
				mAuctionStateGridPane.getStyleClass().clear();
				CommonUtils.getInstance().addStyleClass(mAuctionStateGridPane, "bg-color-04cf5c");

			} else if (code.equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {

				Platform.runLater(() -> {

					isApplicationClosePopup = true;

					Optional<ButtonType> btnResult = showAlertPopupOneButton(mResMsg.getString("msg.auction.finish"));

					if (btnResult.get().getButtonData() == ButtonData.LEFT) {
						onServerAndClose();
					}

				});
			}
		});

		Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());

		System.out.println("[뷰 작업 완료]" + mAuctionStatus.getState());
//		});
	}

	/**
	 * 경매정보 - 경매 상태 표시
	 *
	 * 현재 경매 상태
	 * 
	 * @param isSuccess true : 낙찰 , false : 유찰
	 * @param bidder    화원
	 */
	@Override
	protected void updateAuctionStateInfo(boolean isSuccess, SpBidding bidder) {

		Platform.runLater(() -> {
			// REFACTOR: 경매완료 후, 경매시작 - server가 아닌 controller에서 진행하도록 변경 됨. (21.07.27)
			SpEntryInfo spEntryInfo = mWaitTableView.getSelectionModel().getSelectedItem();
			mLogger.debug("[updateAuctionStateInfo]");
			// 낙유찰 사운드 메세지
			StringBuffer resultStringBuffer = new StringBuffer();

			if (isSuccess) {
				spEntryInfo.setAuctionSucBidder(new SimpleStringProperty(bidder.getAuctionJoinNum().getValue()));
				spEntryInfo.setAuctionBidPrice(new SimpleStringProperty(bidder.getSraSbidAm().getValue()));
				spEntryInfo.setSraSbidUpPrice(new SimpleStringProperty(bidder.getPrice().getValue()));
				spEntryInfo.setAuctionResult(new SimpleStringProperty(GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS));
				spEntryInfo.setAuctionBidDateTime(new SimpleStringProperty(bidder.getBiddingTime().getValue()));
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.success"));
			
				String succMsg = "";
				
				if(SettingApplication.getInstance().isWon(mCurrentSpEntryInfo.getEntryType().getValue())) {
					succMsg = mResMsg.getString("str.sound.auction.result.success.won");
				}else {
					succMsg = mResMsg.getString("str.sound.auction.result.success");
				}
				
				
				resultStringBuffer.append(String.format(succMsg, bidder.getAuctionJoinNum().getValue(), bidder.getPriceInt()));
			} else {
				spEntryInfo.setAuctionSucBidder(new SimpleStringProperty(""));
				spEntryInfo.setAuctionBidPrice(new SimpleStringProperty("0"));
				spEntryInfo.setSraSbidUpPrice(new SimpleStringProperty("0"));
				spEntryInfo.setAuctionResult(new SimpleStringProperty(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING));
				spEntryInfo.setAuctionBidDateTime(new SimpleStringProperty(""));
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.fail"));
				resultStringBuffer.append(String.format(mResMsg.getString("str.sound.auction.result.fail"), spEntryInfo.getEntryNum().getValue()));
				mBtnSpace.setUserData("");
				CommonUtils.getInstance().removeStyleClass(mBtnSpace, "bg-color-04cf5c");
			}

			stopStartAuctionSecScheduler();
			mAuctionStateGridPane.getStyleClass().clear();
			CommonUtils.getInstance().addStyleClass(mAuctionStateGridPane, "bg-color-ea0030");

			// 상단 경매 상태 라벨 낙/유찰 에 따라 표시
			auctionStateLabelToggle(spEntryInfo.getAuctionResult().getValue());

			mWaitTableView.refresh();
			
			AudioFilePlay.getInstance().setTargetPlay(this.getClass().getResource(AudioPlayTypes.which(AudioPlayTypes.FINISH)).toExternalForm(), new AudioPlayListener() {

				@Override
				public void onPlayReady(AudioFilePlay audioFilePlay, MediaPlayer mediaPlayer) {
					mLogger.info("TTS 재생이 준비되었습니다.");
					mLogger.info("TTS 재생 시간 : " + AudioFilePlay.getInstance().getPlayDuration());
					AudioFilePlay.getInstance().playSound();
				}

				@Override
				public void onPlayCompleted() {

					if (SettingApplication.getInstance().isUseSoundAuction()) {

						// 낙유찰 사운드 메세지 사운드 시작
						SoundUtil.getInstance().playSound(resultStringBuffer.toString(), new PlaybackListener() {
							@Override
							public void playbackFinished(PlaybackEvent evt) {
								nextEntryInfo(spEntryInfo);
							}
						});

					} else {
						nextEntryInfo(spEntryInfo);
					}

				}
			});

//
//			SoundUtil.getInstance().playLocalSound(LocalSoundDefineRunnable.LocalSoundType.END, new LineListener() {
//
//				@Override
//				public void update(LineEvent event) {
//					if (event.getType() == LineEvent.Type.STOP || event.getType() == LineEvent.Type.CLOSE) {
//
//						if (SettingApplication.getInstance().isUseSoundAuction()) {
//
//							// 낙유찰 사운드 메세지 사운드 시작
//							SoundUtil.getInstance().playSound(resultStringBuffer.toString(), new PlaybackListener() {
//								@Override
//								public void playbackFinished(PlaybackEvent evt) {
//									nextEntryInfo(spEntryInfo);
//								}
//							});
//
//						} else {
//							nextEntryInfo(spEntryInfo);
//						}
//
//					}
//				}
//			});
		});
	}

	/**
	 * 다음 출품
	 * 
	 * @param spEntryInfo
	 */
	private void nextEntryInfo(SpEntryInfo spEntryInfo) {

		PauseTransition pauseTransition = new PauseTransition(Duration.millis(1000));
		pauseTransition.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				isResultCompleteFlag = false;

				// 경매 완료 테이블에 데이터 넣음
				addFinishedTableViewItem(spEntryInfo);

				boolean isPlusSucStop = false;

				// 음성경매중에 + 키로 단일로 진행한경우
				if (!SettingApplication.getInstance().isUseSoundAuction() && isPlusKeyStartAuction) {
					toggleAuctionType();
					isPlusKeyStartAuction = false;
					isPlusSucStop = true;
					// ENTER 경매 시작으로.
					mBtnEnter.setText(mResMsg.getString("str.btn.start"));
					CommonUtils.getInstance().removeStyleClass(mBtnEnter, "btn-auction-stop");
				}

				if (spEntryInfo.getAuctionResult().getValue().equals(GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS)) {

					if ((mWaitTableView.getSelectionModel().getSelectedIndex() + 1) == mRecordCount) {
						System.out.println("마지막. 뷰 초기화");
						// 경매 준비 상태로 뷰들 초기화
						setAuctionVariableState(GlobalDefineCode.AUCTION_STATUS_READY);
						setCurrentEntryInfo(true);
						return;
					}
					
					// 음성경매 && 하나씩 진행 아닌경우
					if (SettingApplication.getInstance().isUseSoundAuction() && !SettingApplication.getInstance().isUseOneAuction() && !isPlusKeyStartAuction && !isPlusSucStop) {
						// 자동 시작
						PauseTransition start = new PauseTransition(Duration.millis(1000));
						start.setOnFinished(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								isAutoPlay = true;
								// 경매 준비 상태로 뷰들 초기화
								setAuctionVariableState(GlobalDefineCode.AUCTION_STATUS_READY);
								// 다음 번호 이동
								selectIndexWaitTable(1, false);
								onStartSoundAuction();
								
							}
						});
						start.play();
					} else {
						// 다음 번호 이동
						selectIndexWaitTable(1, false);
						// 단일건 경매 준비 상태로 뷰들 초기화
						setAuctionVariableState(GlobalDefineCode.AUCTION_STATUS_READY);
					}

				} else {
					// 유찰건 경매 준비 상태로 뷰들 초기화
					setAuctionVariableState(GlobalDefineCode.AUCTION_STATUS_READY);
					addLogItem("경매 상태 유찰이거나 하나씩진행 체크 됨." + spEntryInfo.getAuctionResult().getValue() + " / " + SettingApplication.getInstance().isUseOneAuction());
				}
				// 현재 선택된 row 갱신
				setCurrentEntryInfo(true);

			}
		});
		pauseTransition.play();
	}

	/**
	 * 응찰자 응찰 테이블에 업데이트
	 */
	@Override
	protected synchronized void updateBidderList(List<SpBidding> spBiddingDataList) {

		Platform.runLater(() -> {

			List<SpBidding> bidderDataList = null;

			if (spBiddingDataList != null) {
				if (spBiddingDataList.size() > 11) {
					bidderDataList = new ArrayList<SpBidding>(spBiddingDataList.subList(0, 12));
				} else {
					bidderDataList = new ArrayList<SpBidding>(spBiddingDataList);
				}

				mBiddingUserInfoDataList.clear();
				mBiddingUserInfoDataList.addAll(bidderDataList);
				mBiddingInfoTableView.getSelectionModel().select(0);
				mBiddingInfoTableView.refresh();

			} else {
				initBiddingInfoDataList();
			}

			// 응찰자 갱신 후 완료 콜백
			mCalculationRankCallBack.completed(true, null);

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
			//예정가 낮추기 금액
			int BaselowPrice = SettingApplication.getInstance().getCowLowerLimitPrice(Integer.parseInt(currentEntryInfo.getEntryType().getValue()));
			setBaseDownPrice(Integer.toString(BaselowPrice));
			//비고 사용유무 show or hide
			if (SettingApplication.getInstance().isNote()) {
				mNoteVbox.setVisible(true);
				mNoteTextArea.setText(mCurrentSpEntryInfo.getNote().getValue());
			} else {
				mNoteVbox.setVisible(false);
			}

			if (SettingApplication.getInstance().isUseSoundAuction() && isSoundData) {
				setCurrentEntrySoundData();
			}

			CommonUtils.getInstance().dismissLoadingDialog();
		});
	}

	/**
	 * 출품정보 사운드 값
	 * 
	 * @param spEntryInfo
	 */
	@SuppressWarnings("unlikely-arg-type")
	private void setCurrentEntrySoundData() {
		
		if (!SettingApplication.getInstance().isUseSoundAuction()) {
			return;
		}

		boolean isSkipCowSound = false;

		if (mCurrentSpEntryInfo.getAuctionResult() != null && CommonUtils.getInstance().isValidString(mCurrentSpEntryInfo.getAuctionResult().getValue())) {
			if (mCurrentSpEntryInfo.getAuctionResult().getValue().equals(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING)) {
				isSkipCowSound = true;
				SoundUtil.getInstance().setCurrentEntryInfoMessage(null);
				System.out.println("[보류. 사운드 재생 안 함]");
			}
		}

		if (isSkipCowSound) {
			return;
		}

		StringBuffer entrySoundContent = new StringBuffer();

		String EMPTY_SPACE = " ";

		boolean isOnlyEntryNumber = false;

		if (!CommonUtils.getInstance().isEmptyProperty(mCurrentSpEntryInfo.getLowPrice()) && mCurrentSpEntryInfo.getLowPriceInt() > 0) {

			if (mEntryNumCheckBox.isSelected() && CommonUtils.getInstance().isValidString(mCurEntryNumLabel.getText())) {
				entrySoundContent.append(String.format(mResMsg.getString("str.sound.auction.info.entry.number"), mCurEntryNumLabel.getText()));
				isOnlyEntryNumber = true;
			}

			if (mExhibitorCheckBox.isSelected() && CommonUtils.getInstance().isValidString(mCurExhibitorLabel.getText())) {
				entrySoundContent.append(EMPTY_SPACE);
				entrySoundContent.append(String.format(mResMsg.getString("str.sound.auction.info.entry.exhibitor"), mCurExhibitorLabel.getText()));
				isOnlyEntryNumber = false;
			}

			if (mGenderCheckBox.isSelected() && CommonUtils.getInstance().isValidString(mCurGenterLabel.getText())) {
				entrySoundContent.append(EMPTY_SPACE);

				if (mCurrentSpEntryInfo.getGender().getValue().equals(GlobalDefine.AUCTION_INFO.AUCTION_INDV_SEX_C_1) || mCurrentSpEntryInfo.getGender().getValue().equals(GlobalDefine.AUCTION_INFO.AUCTION_INDV_SEX_C_2)) {

					if (mCurrentSpEntryInfo.getEntryType().getValue().equals(Integer.toString(GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_1))) {
						entrySoundContent.append(String.format(mResMsg.getString("str.sound.auction.info.entry.gender.normal"), mCurGenterLabel.getText()));
					} else {

						if (mCurrentSpEntryInfo.getGender().getValue().equals(GlobalDefine.AUCTION_INFO.AUCTION_INDV_SEX_C_1)) {
							entrySoundContent.append(mResMsg.getString("str.sound.auction.info.entry.gender.f"));
						} else {
							entrySoundContent.append(mResMsg.getString("str.sound.auction.info.entry.gender.m"));
						}
					}

				} else if (mCurrentSpEntryInfo.getGender().getValue().equals(GlobalDefine.AUCTION_INFO.AUCTION_INDV_SEX_C_4)) {
					entrySoundContent.append(String.format(mResMsg.getString("str.sound.auction.info.entry.nomat"), mCurGenterLabel.getText()));
				} else {
					entrySoundContent.append(String.format(mResMsg.getString("str.sound.auction.info.entry.gender"), mCurGenterLabel.getText()));
				}

				isOnlyEntryNumber = false;
			}

			if (mMotherObjNumCheckBox.isSelected() && CommonUtils.getInstance().isValidString(mCurMotherLabel.getText())) {
				entrySoundContent.append(EMPTY_SPACE);
				entrySoundContent.append(String.format(mResMsg.getString("str.sound.auction.info.entry.mother"), mCurMotherLabel.getText()));
				isOnlyEntryNumber = false;
			}
			if (mMaTimeCheckBox.isSelected() && CommonUtils.getInstance().isValidString(mCurMatimeLabel.getText())) {
				entrySoundContent.append(EMPTY_SPACE);
				entrySoundContent.append(String.format(mResMsg.getString("str.sound.auction.info.entry.matime"), mCurMatimeLabel.getText()));
				isOnlyEntryNumber = false;
			}
			if (mPasgQcnCheckBox.isSelected() && CommonUtils.getInstance().isValidString(mCurPasgQcnLabel.getText())) {
				entrySoundContent.append(EMPTY_SPACE);
				entrySoundContent.append(String.format(mResMsg.getString("str.sound.auction.info.entry.pasgqcn"), mCurPasgQcnLabel.getText()));
				isOnlyEntryNumber = false;
			}
			if (mWeightCheckBox.isSelected() && CommonUtils.getInstance().isValidString(mCurWeightLabel.getText())) {
				entrySoundContent.append(EMPTY_SPACE);
				entrySoundContent.append(String.format(mResMsg.getString("str.sound.auction.info.entry.weight"), mCurWeightLabel.getText()));
				isOnlyEntryNumber = false;
			}

			if (mLowPriceCheckBox.isSelected() && CommonUtils.getInstance().isValidString(mCurLowPriceLabel.getText())) {
				entrySoundContent.append(EMPTY_SPACE);
				
				String wonMsg = "";
				
				if(SettingApplication.getInstance().isWon(mCurrentSpEntryInfo.getEntryType().getValue())) {
					wonMsg = mResMsg.getString("str.sound.auction.info.entry.low.price.1000");
				}else {
					wonMsg = mResMsg.getString("str.sound.auction.info.entry.low.price.10000");
				}

				entrySoundContent.append(String.format(wonMsg, mCurLowPriceLabel.getText()));
				isOnlyEntryNumber = false;
			}

			if (mBrandNameCheckBox.isSelected() && !CommonUtils.getInstance().isEmptyProperty(mCurrentSpEntryInfo.getBrandName())) {
				entrySoundContent.append(EMPTY_SPACE);
				entrySoundContent.append(String.format(mResMsg.getString("str.sound.auction.info.entry.brand"), mCurrentSpEntryInfo.getBrandName().getValue()));
				isOnlyEntryNumber = false;
			}
			
			if (mDnaCheckBox.isSelected() && !CommonUtils.getInstance().isEmptyProperty(mCurrentSpEntryInfo.getDnaYn())) {
				if(mCurrentSpEntryInfo.getDnaYn().getValue().equals(GlobalDefine.AUCTION_INFO.AUCTION_DNA_1)) {			
					entrySoundContent.append(EMPTY_SPACE);
					entrySoundContent.append(mResMsg.getString("str.sound.auction.info.entry.dna"));
					isOnlyEntryNumber = false;
				}
			}
			
			if (!CommonUtils.getInstance().isEmptyProperty(mCurrentSpEntryInfo.getNote())) {
				entrySoundContent.append(EMPTY_SPACE);
				entrySoundContent.append(String.format(mResMsg.getString("str.sound.auction.info.entry.note"), mCurrentSpEntryInfo.getNote().getValue()));
				isOnlyEntryNumber = false;
			} else {
				if (CommonUtils.getInstance().isValidString(entrySoundContent.toString().trim()) && !isOnlyEntryNumber) {
					entrySoundContent.append(EMPTY_SPACE);
					entrySoundContent.append(mResMsg.getString("str.sound.auction.info.entry.it.is"));
				}
			}
		} else {
			entrySoundContent.append(String.format(mResMsg.getString("str.sound.auction.info.entry.low.price.empty"), mCurEntryNumLabel.getText()));
		}

		SoundUtil.getInstance().setCurrentEntryInfoMessage(entrySoundContent.toString());

		mLogger.debug("[출장우 정보 사운드 메세지] " + entrySoundContent.toString());
	}

	/**
	 * 경매 완료 row set
	 *
	 * @param spEntryInfo
	 */
	private void addFinishedTableViewItem(SpEntryInfo spEntryInfo) {

		SpEntryInfo finishSpEntryInfo = new SpEntryInfo();

		finishSpEntryInfo = spEntryInfo.clone();

		mFinishedEntryInfoDataList.add(finishSpEntryInfo);

		if (mFinishedEntryInfoDataList.size() > 4) {
			mFinishedEntryInfoDataList.remove(0);
		}

		mFinishedTableView.refresh();
		mFinishedTableView.scrollTo(mFinishedTableView.getItems().size() - 1);
	}

	/**
	 * 키 설정
	 */
	private void initKeyConfig() {

		Platform.runLater(() -> {

			mStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

				public void handle(KeyEvent ke) {

					if (isResultCompleteFlag) {
						return;
					}

					// 종료
					if (ke.getCode() == KeyCode.ESCAPE) {
						onCancel();
						ke.consume(); // 다음 노드로 이벤트를 전달하지 않는다.
					}

					switch (mAuctionStatus.getState()) {
					case GlobalDefineCode.AUCTION_STATUS_START:
					case GlobalDefineCode.AUCTION_STATUS_PROGRESS: // 경매 진행중에 눌림

						// 강제낙찰
						if (ke.getCode() == KeyCode.F6) {
							onSuccessAuction();
							ke.consume();
						}

						if (ke.getCode() == KeyCode.DIGIT1 || ke.getCode() == KeyCode.NUMPAD1) {
							sendCountDown(1);
							ke.consume();
						}

						if (ke.getCode() == KeyCode.DIGIT2 || ke.getCode() == KeyCode.NUMPAD2) {
							sendCountDown(2);
							ke.consume();
						}
						if (ke.getCode() == KeyCode.DIGIT3 || ke.getCode() == KeyCode.NUMPAD3) {
							sendCountDown(3);
							ke.consume();
						}

						if (ke.getCode() == KeyCode.DIGIT4 || ke.getCode() == KeyCode.NUMPAD4) {
							sendCountDown(4);
							ke.consume();
						}
						if (ke.getCode() == KeyCode.DIGIT5 || ke.getCode() == KeyCode.NUMPAD5) {
							sendCountDown(5);
							ke.consume();
						}
						if (ke.getCode() == KeyCode.DIGIT6 || ke.getCode() == KeyCode.NUMPAD6) {
							sendCountDown(6);
							ke.consume();
						}
						if (ke.getCode() == KeyCode.DIGIT7 || ke.getCode() == KeyCode.NUMPAD7) {
							sendCountDown(7);
							ke.consume();
						}
						if (ke.getCode() == KeyCode.DIGIT8 || ke.getCode() == KeyCode.NUMPAD8) {
							sendCountDown(8);
							ke.consume();
						}
						if (ke.getCode() == KeyCode.DIGIT9 || ke.getCode() == KeyCode.NUMPAD9) {
							sendCountDown(9);
							ke.consume();
						}

						break;
					default: // 경매 진행중에 눌리지 않음.
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
						
						// 보류
						if (ke.getCode() == KeyCode.F3) {
							onPending();
							ke.consume();
						}

						//새로고침
						if (ke.getCode() == KeyCode.F5) {
							Platform.runLater(() -> CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.searching.entry.list")));
							refreshWaitAllEntryDataList(mWaitTableView.getSelectionModel().getSelectedIndex());
							ke.consume();
						}

						// 환경설정
						if (ke.getCode() == KeyCode.F8) {
							// 환경설정
							openSettingDialog();
							ke.consume();
						}

						// 대기중인 목록 위로 이동
						if (ke.getCode() == KeyCode.UP) {

							if (mWaitTableView.isDisable() || isAutoPlay) {
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

							if (mWaitTableView.isDisable() || isAutoPlay) {
								return;
							}

							selectIndexWaitTable(1, false);
							ke.consume();
						}

						break;
					}

					// 경매 시작
					if (ke.getCode() == KeyCode.ADD) {

						System.out.println("[KeyCode.ADD]=> isStartedAuction : " + isStartedAuction);
						System.out.println("[KeyCode.ADD]=> isPlusKeyStartAuction : " + isPlusKeyStartAuction);
						System.out.println("[KeyCode.ADD]=> isStartSoundPlaying : " + isStartSoundPlaying);
						System.out.println("[KeyCode.ADD]=> mAuctionStatus.getState() : " + mAuctionStatus.getState());

						if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_READY) || mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_COMPLETED) || mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PASS)) {
							// 출장우 정보 TTS 송출 여부 초기화
							isStartSoundPlaying = false;
						}

						if (isStartedAuction || isPlusKeyStartAuction || isStartSoundPlaying) {
							return;
						}

						switch (mAuctionStatus.getState()) {
						case GlobalDefineCode.AUCTION_STATUS_READY: // 준비,경매완료,유찰 상황에서 시작 가능.
						case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
						case GlobalDefineCode.AUCTION_STATUS_PASS:

							if (SettingApplication.getInstance().isUseSoundAuction()) {
								toggleAuctionType();
								isPlusKeyStartAuction = true;
								onStartAndStopAuction(0);
							}
							break;
						}
						ke.consume();
					}

					// 경매 시작
					if (ke.getCode() == KeyCode.ENTER) {
						System.out.println("[KeyCode.ENTER]=> " + mAuctionStatus.getState());
						normalEnterStartAuction();
						ke.consume();
					}
					// 음성 경매 시작
					if (ke.getCode() == KeyCode.SPACE) {
						System.out.println("[KeyCode.SPACE]=> " + mAuctionStatus.getState());
						normalSpaceStartAuction();
						ke.consume();
					}
				}
			});
		});
	}

	private void normalEnterStartAuction() {
		onStartAndStopAuction(0);
	}

	private void normalSpaceStartAuction() {

		if (SettingApplication.getInstance().isUseSoundAuction()) {
			if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_START) || mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
				//
//				onCancelOrClose();
			} else {
				isPlusKeyStartAuction = false;
				onStartSoundAuction();
			}
		} else {

			if (!mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_START) && !mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
				if (!isStartedAuction) {
					showAlertPopupOneButton(mResMsg.getString("dialog.auction.no.sound"));
				}
			}
		}
	}

	private void toggleAuctionType() {

//		Platform.runLater(()->{
		if (SettingApplication.getInstance().isUseSoundAuction()) {
			mLogger.debug("토글 => 단일 경매 전환");
			mBtnEnter.setDisable(false);
			mBtnSpace.setDisable(true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_SETTING_USE_SOUND_AUCTION, false);
		} else {
			mLogger.debug("토글 => 음성경매 전환");
			mBtnEnter.setDisable(true);
			mBtnSpace.setDisable(false);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_SETTING_USE_SOUND_AUCTION, true);
		}

		SettingApplication.getInstance().initSharedData();
//		});
	}

	/**
	 * 키패드 카운트 다운
	 *
	 * @param countDown
	 */
	private void sendCountDown(int countDown) {

		if (isCountDownRunning || isPause || isCountDownBtnPressed || isRestart) {
			return;
		}

		if (SettingApplication.getInstance().isUseSoundAuction()) {

			if (isStartSoundPlaying || isOverPricePlaySound) {
				return;
			}

			isCountDownBtnPressed = true;

			// 카운트다운 안내 멘트 송출 중에 숫자키로 카운트다운 재실행 시 중복 실행 방지 코드
			if (!isCountDownRunning) {
				isCountDownRunning = true;
			}

			playCountDownSound(countDown);

		} else {

			isCountDownBtnPressed = true;

			onStopAuction(countDown);
		}

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
	 * 전체 출품 정보 조회 EntryNum 없는 dummy row 제외
	 *
	 * @return
	 */
	public ObservableList<SpEntryInfo> getWaitEntryInfoDataList() {

		ObservableList<SpEntryInfo> dataList = FXCollections.observableArrayList();

		for (SpEntryInfo spEntryInfo : mWaitEntryInfoDataList) {
			if (!CommonUtils.getInstance().isEmptyProperty(spEntryInfo.getEntryNum())) {
				dataList.add(spEntryInfo);
			}
		}

		return dataList;
	}

	/**
	 * 완료된 출품 정보 조회 EntryNum 없는 dummy row 제외
	 *
	 * @return
	 */
	public ObservableList<SpEntryInfo> getFinishedEntryInfoDataList() {

		ObservableList<SpEntryInfo> dataList = FXCollections.observableArrayList();

		for (SpEntryInfo spEntryInfo : mFinishedEntryInfoDataList) {
			if (!CommonUtils.getInstance().isEmptyProperty(spEntryInfo.getEntryNum())) {
				dataList.add(spEntryInfo);
			}
		}

		return dataList;
	}

	/**
	 * 보류 정보 조회 EntryNum 없는 dummy row 제외
	 *
	 * @return
	 */
	public ObservableList<SpEntryInfo> getWaitEntryInfoPendingDataList() {

		ObservableList<SpEntryInfo> dataList = FXCollections.observableArrayList();

		for (SpEntryInfo spEntryInfo : mWaitEntryInfoDataList) {

			if (!CommonUtils.getInstance().isEmptyProperty(spEntryInfo.getEntryNum()) && !CommonUtils.getInstance().isEmptyProperty(spEntryInfo.getAuctionResult())) {
				if (spEntryInfo.getAuctionResult().getValue().equals(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING)) {
					dataList.add(spEntryInfo);
				}
			}
		}

		return dataList;
	}

	/**
	 * 컬럼 데이터 콤마 표시
	 *
	 * @param column
	 */
	private synchronized <T> void setNumberColumnFactory(TableColumn<T, String> column, boolean isPrice) {

		column.setCellFactory(col -> new TableCell<T, String>() {
			@Override
			protected void updateItem(String value, boolean empty) {
				super.updateItem(value, empty);

				if (CommonUtils.getInstance().isValidString(value)) {
					setText(CommonUtils.getInstance().getNumberFormatComma(Integer.parseInt(value)));
				} else {
					setText("");
				}
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
	 * Showing dialog Close
	 */
	private void dismissShowingDialog() {
		MoveStageUtil.getInstance().dismissDialog();
		MoveStageUtil.getInstance().setBackStageDisableFalse(mStage);
	}

	/**
	 * 응찰 가격 체크
	 *
	 * @return
	 */
	private boolean isBidderPriceValid() {

		// 응찰 테이블 뷰
		if (CommonUtils.getInstance().isListEmpty(mBiddingUserInfoDataList)) {
			// 응찰 내역 없음.
			addLogItem("==== 응찰 내역 없음. ====");
			return false;
		}

		// 1순위 회원
		SpBidding rank_1_user = mBiddingUserInfoDataList.get(0);

		// 최저가 / 응찰가 비교.
		// 1순위가 최저가보다 미만 팝업.
		if (!checkLowPrice(rank_1_user)) {
			// 최저가격 이하는 낙찰이 불가능 합니다.
			if (!SettingApplication.getInstance().isUseSoundAuction()) {
				Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("str.auction.low.price.valid")));
			}
			return false;
		}

		// 최저가 + 상한가 / 응찰가 비교.
		// 이상인 경우 팝업.
		if (!checkOverPrice(rank_1_user)) {
			// 낙찰금액을 확인해주세요.
			if (!SettingApplication.getInstance().isUseSoundAuction()) {
				Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("str.auction.over.price.valid")));
			} else {
				// 응찰금액 확인 사운드
				playOverPriceSound(rank_1_user.getAuctionJoinNum().getValue());
				// 카운트 라벨 설정 시간 기준 초기화
				setCountDownLabelState(SettingApplication.getInstance().getAuctionCountdown(), false);
			}
			return false;
		}

		return true;
	}

	/**
	 * 응찰금액 확인 사운
	 * 
	 * @param joinNumber
	 */
	private void playOverPriceSound(String joinNumber) {
		
		System.out.println("[isStartSoundPlaying] : " + isStartSoundPlaying
				+" [isOverPricePlaySound] : " + isOverPricePlaySound
				+" [isPlayReAuctionSound] : " + isPlayReAuctionSound
				+" [isPause] : " + isPause);
		
		if (isStartSoundPlaying || isOverPricePlaySound || isPlayReAuctionSound || isPause) {
			return;
		}

		// 응찰금액 확인 사운드 메세지
		String overPriceSoundMessage = String.format(mResMsg.getString("str.sound.auction.over.price"), joinNumber);

		isOverPricePlaySound = true;

		// 사운드 시작
		SoundUtil.getInstance().playSound(overPriceSoundMessage, new PlaybackListener() {
			@Override
			public void playbackFinished(PlaybackEvent evt) {
				isOverPricePlaySound = false;
				checkBiddingUserPlaySound();
			}
		});
	}

	/**
	 * 경매 상태에 따라 버튼 동작
	 */
	private void btnToggle() {

		Platform.runLater(() -> {

			// 경매 시작 버튼 활성화
			if (!SettingApplication.getInstance().isUseSoundAuction()) {
				mBtnEnter.setDisable(false);
				mBtnSpace.setDisable(true);
			} else {
				mBtnEnter.setDisable(true);
				mBtnSpace.setDisable(false);
			}

			switch (mAuctionStatus.getState()) {
			case GlobalDefineCode.AUCTION_STATUS_START:
			case GlobalDefineCode.AUCTION_STATUS_PROGRESS:

				if (!SettingApplication.getInstance().isUseSoundAuction()) {
					// ENTER 경매시작 -> 경매완료 변경
					mBtnEnter.setText(mResMsg.getString("str.btn.stop"));
					// ENTER 경매완료 css 적용
					CommonUtils.getInstance().addStyleClass(mBtnEnter, "btn-auction-stop");
				} else {
					mBtnSpace.setText(mResMsg.getString("str.btn.sound.auction.progress"));
					CommonUtils.getInstance().addStyleClass(mBtnSpace, "bg-color-04cf5c");
					mBtnSpace.setUserData(GlobalDefineCode.AUCTION_STATUS_PROGRESS);
				}

				// 강제 유찰 버튼 비활성화
				mBtnF3.setDisable(true);
				// 전체보기 비활성화
				mBtnF1.setDisable(true);
				// 보류보기 비활성화
				mBtnF2.setDisable(true);
				// 강제 낙찰 버튼 활성화
				mBtnF6.setDisable(false);
				// 환경설정 버튼 비활성화
				mBtnF8.setDisable(true);
				// 가격 상승,다운 비활성화
				mBtnUpPrice.setDisable(true);
				mBtnDownPrice.setDisable(true);
				// 출품 대기 테이블 비활성화
				mWaitTableView.setDisable(true);
				//새로고침
				mBtnF5.setDisable(true);
				
				break;
			default:

				if (!SettingApplication.getInstance().isUseSoundAuction()) {
					// ENTER 경매 시작으로.
					mBtnEnter.setText(mResMsg.getString("str.btn.start"));
					CommonUtils.getInstance().removeStyleClass(mBtnEnter, "btn-auction-stop");
				} else {
					mBtnSpace.setText(mResMsg.getString("str.btn.sound.auction.ready"));
					CommonUtils.getInstance().removeStyleClass(mBtnSpace, "bg-color-04cf5c");
				}
				// 강제 유찰 버튼 활성화
				mBtnF3.setDisable(false);
				// 전체보기 활성화
				mBtnF1.setDisable(false);
				// 보류보기 활성화
				mBtnF2.setDisable(false);
				// 강제 낙찰 버튼 비활성화
				mBtnF6.setDisable(true);
				// 환경설정 버튼
				mBtnF8.setDisable(false);
				// 가격 상승,다운 활성화
				mBtnUpPrice.setDisable(false);
				mBtnDownPrice.setDisable(false);
				//새로고침
				mBtnF5.setDisable(false);
				break;
			}
		});
	}

	/**
	 * 상단 경매 진행 상태
	 *
	 * @param state
	 */
	private void auctionStateLabelToggle(String state) {

		switch (state) {
		case GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS:
			// 대기 라벨 비활성화
			mAuctionStateReadyLabel.setDisable(true);
			// 진행 라벨 비활성화
			mAuctionStateProgressLabel.setDisable(true);
			// 완료 라벨 비활성화
			mAuctionStateSuccessLabel.setDisable(false);
			// 유찰(보류) 라벨 비활성화
			mAuctionStateFailLabel.setDisable(true);
			// 경매 상태 라벨
			mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.success"));
			break;
		case GlobalDefineCode.AUCTION_RESULT_CODE_PENDING:
			// 대기 라벨 비활성화
			mAuctionStateReadyLabel.setDisable(true);
			// 진행 라벨 비활성화
			mAuctionStateProgressLabel.setDisable(true);
			// 완료 라벨 비활성화
			mAuctionStateSuccessLabel.setDisable(true);
			// 유찰(보류) 라벨 비활성화
			mAuctionStateFailLabel.setDisable(false);
			// 경매 상태 라벨
			mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.fail"));
			break;
		default:
			// 대기 라벨 비활성화
			mAuctionStateReadyLabel.setDisable(false);
			// 진행 라벨 비활성화
			mAuctionStateProgressLabel.setDisable(true);
			// 완료 라벨 비활성화
			mAuctionStateSuccessLabel.setDisable(true);
			// 유찰(보류) 라벨 비활성화
			mAuctionStateFailLabel.setDisable(true);
			// 경매 상태 라벨
			mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.auction.ready"));

			break;
		}
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
	 * 
	 * @MethodName showToastMessage
	 * @Description 하단 Toast 표시
	 *
	 * @param message
	 */
	private void showToastMessage(String message) {

		if (isShowToast) {
			return;
		}

		Platform.runLater(() -> {

			if (!mSTPMessage.isVisible()) {
				isShowToast = true;
				mSTPMessage.setVisible(true);
				mAnimationFadeIn.playFromStart();
			}

			mMessageText.setText(message);
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
	
	/**
	 * 원버튼 팝업
	 *
	 * @param message
	 * @return
	 */
	private Optional<ButtonType> showAlertPopupOneButton(String message,String btnStr) {
		return CommonUtils.getInstance().showAlertPopupOneButton(mStage, message, btnStr);
	}

}
