package com.nh.controller.controller;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nh.common.AuctionShareNettyClient;
import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.NettyControllable;
import com.nh.controller.model.AuctionEntryInfo;
import com.nh.controller.preferences.SharedPreference;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.share.api.model.AuctionGenerateInformationResult;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionReponseSession;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.EntryInfo;
import com.nh.share.controller.models.AutoMode;
import com.nh.share.controller.models.EditSetting;
import com.nh.share.controller.models.ManualMode;
import com.nh.share.controller.models.PassAuction;
import com.nh.share.controller.models.PauseAuction;
import com.nh.share.controller.models.StartAuction;
import com.nh.share.controller.models.ToastMessageRequest;
import com.nh.share.server.models.AbsenteeUserInfo;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.AuctionStatus;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.CurrentSetting;
import com.nh.share.server.models.ExceptionCode;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.ResponseConnectionInfo;
import com.nh.share.server.models.ResponseEntryInfo;
import com.nh.share.server.models.ToastMessage;
import com.nh.share.setting.AuctionShareSetting;
import com.nh.share.utils.CommonUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * 
 * @ClassName AuctionControlViewController.java
 * @Description 경매 목록에서 선택한 경매를 제어할 수 있는 경매 제어 프로그램 화면
 * @anthor ishift
 * @since 2019.10.28
 */
public class AuctionControlController implements NettyControllable, Initializable {

	private final Logger mLogger = LogManager.getLogger(AuctionControlController.class);

	private ResourceBundle mCurrentResources;

	@FXML
	private Label mLabelCurrentTime; // 현재 시간

	@FXML
	private Button mBtnAuctionType, // 경매 타입 (실시간/SPOT)
			mBtnAuctionLaneName; // 경매 레인명

	@FXML
	private Label mLabelAuctionInfoDetail; // 경매 상세 정보 (ex > 수요 정기경매 - 888회차 / 경매시간 2019.10.10 PM 2:00)

	@FXML
	private Label mLabelAuctionStartAuto; // 경매 자동 시작 On/Off

	@FXML
	private HBox mHboxAuctionAutoStart; // 경매 자동 시작 On/Off HBox

	@FXML
	private Label mLabelEntryNum1, mLabelEntryNum2, mLabelEntryNum3, mLabelEntryNum4, mLabelEntryNum5; // 출품번호
	private List<Label> mArrayEntryNumLabel; // 출품번호 Label List

	@FXML
	private Label mLabelCarName1, mLabelCarName2, mLabelCarName3, mLabelCarName4, mLabelCarName5; // 차량명
	private List<Label> mArrayCarNameLabel; // 차량명 Label List

	@FXML
	private Label mLabelChangeContext1, mLabelChangeContext2, mLabelChangeContext3, mLabelChangeContext4,
			mLabelChangeContext5; // 변경사항
	private List<Label> mArrayChangeContextLabel; // 변경사항 Label List

	@FXML
	private Label mLabelExhibitorName1, mLabelExhibitorName2, mLabelExhibitorName3, mLabelExhibitorName4,
			mLabelExhibitorName5; // 출품자
	private List<Label> mArrayExhibitorNameLabel; // 출품자 Label List

	@FXML
	private Label mLabelCarYear1, mLabelCarYear2, mLabelCarYear3, mLabelCarYear4, mLabelCarYear5; // 연식
	private List<Label> mArrayCarYearLabel; // 연식 Label List

	@FXML
	private Label mLabelEvalPoint1, mLabelEvalPoint2, mLabelEvalPoint3, mLabelEvalPoint4, mLabelEvalPoint5; // 평가점
	private List<Label> mArrayEvalPointLabel; // 평가점 Label List

	@FXML
	private Label mLabelAbsenteePrice1, mLabelAbsenteePrice2, mLabelAbsenteePrice3, mLabelAbsenteePrice4,
			mLabelAbsenteePrice5; // 부재자
	private List<Label> mArrayAbsenteePriceLabel; // 부재자 Label List

	@FXML
	private Label mLabelStartPrice1, mLabelStartPrice2, mLabelStartPrice3, mLabelStartPrice4, mLabelStartPrice5; // 시작가
	private List<Label> mArrayStartPriceLabel; // 시작가 Label List

	@FXML
	private Label mLabelHopePrice1, mLabelHopePrice2, mLabelHopePrice3, mLabelHopePrice4, mLabelHopePrice5; // 희망가
	private List<Label> mArrayHopePriceLabel; // 희망가 Label List

	@FXML
	private Label mLabelNoEntryData; // 출품목록이 없는 경우 "출품목록이 없습니다."

	@FXML
	private GridPane mGridPaneCarImage; // 차량 사진 GridPane

	@FXML
	private ImageView mCarImageView;

	// 경매설정 Contents
	@FXML
	private Label mLabelBasePrice, // 기준금액
			mLabelRisingPrice, // 상승금액
			mLabelNextEntryTime, // 경매시간
			mLabelAutoRiseCount; // 자동상승

	@FXML
	private Label mLabelAuctionEntryNum, // 출품번호
			mLabelCurrentPrice, // 현재가격
			mLabelHopePrice; // 희망가격

	@FXML
	private Label mLabelRank1MemberNum, // 제 1순위 회원번호
			mLabelRank2MemberNum, // 제 2순위 회원번호
			mLabelRank3MemberNum; // 제 3순위 회원번호

	// 경매 진행 상태
	@FXML
	private ToggleButton mBtnAuctionStatusStart, // 시작
			mBtnAuctionStatusAutoRaise, // 자동상승
			mBtnAuctionStatusProgress, // 진행
			mBtnAuctionStatusRaceCondition, // 경합
			mBtnAuctionStatusSuccessBid, // 낙찰
			mBtnAuctionStatusFailBid; // 유찰

	@FXML
	private Label mLabelNumberBidders; // 응찰자 수

	@FXML
	private ToggleButton mBtnEntryTime1, mBtnEntryTime2, mBtnEntryTime3, mBtnEntryTime4, mBtnEntryTime5; // 낙/유찰 시간 Time
	private List<ToggleButton> mArrayEntryTimeBtn; // 낙/유찰 시간 ToggleButton List

	@FXML
	private Label mLabelSuccessBidMember; // 낙찰회원 번호

	// 총 출품차량 정보 및 완료/잔여 출품 차량 건 수
	@FXML
	private Label mLabelTotalEntryCount, // 총 출품 차량
			mLabelFinishEntryCount, // 완료
			mLabelLeftoverEntry; // 잔여

	// 경매 운영 버튼
	@FXML
	private VBox mBtnAuctionStartVBox; // 경매 시작 버튼
	@FXML
	private Label mAuctionStartButtonLabel; // 경매 시작 버튼 라벨
	@FXML
	private Label mAuctionStartDescLabel; // 경매 시작 버튼 상태 라벨
	@FXML
	private VBox mBtnAuctionAutoStartVBox; // 경매 자동 상승 버튼
	@FXML
	private Label mAuctionAutoStartButtonLabel; // 경매 자동 상승 버튼 라벨
	@FXML
	private Label mAuctionAutoStartDescLabel; // 경매 자동 상승 버튼 상태 라벨

	@FXML
	private Button mBtnAuctionForced; // 강제 유찰

	private AuctionGenerateInformationResult mAuctionListViewCellItem; // 경매 목록에서 전달받은 cell Data
	private CurrentSetting mCurrentSetting;
	private Stage mStage;
	private Stage mParentStage;
	private AuctionShareNettyClient mClient;

	private List<AuctionEntryInfo> mAuctionEntryInfoList; // 출품목록 List Data

	private boolean mBooleanAuctionStartOrStop; // 경매 시작 / 정지 (true = start / false = stop)
	private boolean mBooleanEntryNumAutoRise; // 출품번호 자동상승 Y/N (default : true)
	private String mStringAuctionStatus; // 현재 경매 진행 상태
	private String mStringAuctionCarImageEntryNum; // 현재 표시되어있는 이미지의 출품차량 번호
	// private static AuctionStatus mResponseAuctionStatus; // 현재 차량 정보

	private ScheduledExecutorService mService = Executors.newScheduledThreadPool(1); // 현재 동작하는 작업 스레드 풀
	private ScheduledFuture<?> mConnectInfoJob; // 경매 접속 정보 전송 처리 Job
	private ScheduledFuture<?> mRemainTimeJob; // 경매 진행 시간 처리 Job
	private ScheduledFuture<?> mResourceDataCheckJob; // 경매 리소스 데이터 확인 Job

	private final int BASE_DELAY_TIME = 50; // 타이머 동작 시작 딜레이 시간(ms)
	private final int REMAIN_CHECK_DELAY_TIME = 50; // 경매 남은 시간 확인 간격 시간(ms)
	private int mRemainTime; // 현재 남은 시간

	private boolean isFirstEntryCarImageLoad = false; // 최초 출품 차량 이미지 로딩 여부
	private boolean isFirstEntryCarImageLoadTimer = false; // 최초 출품차량 이미지 타이머 동작 여부

	private boolean isAuctionStartAuto = true; // 경매 자동 시작 Default true
	private boolean isBtnClose = false; // 경매 닫기 버튼 클릭 flag
	private boolean isAuctionFinished = false; // 경매 완료 버튼 클릭 flag

	private boolean isShowAlertPopup = false; // AlertPopup 로딩 여부 flag
	private Dialog<ButtonType> mAlertDialog = new Dialog<>();

	private Stage mStageSetting; // 환경설정 팝업 stage
	private Stage mStageMessage; // 메세지 팝업 stage

	private Timeline mClock = new Timeline();

	public AuctionControlController() {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		mCurrentResources = resources;
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(this));
	}

	/**
	 * 
	 * @MethodName setStage
	 * @Description stage 열기 전 데이터 셋팅
	 * 
	 * @param parentStage
	 * @param stage
	 * @param item
	 */
	public void setStage(Stage parentStage, Stage stage, AuctionGenerateInformationResult item) {
		mParentStage = parentStage;
		mStage = stage;
		mStage.setTitle(mCurrentResources.getString("str.auction.controller.name"));
		mAuctionListViewCellItem = item; // 경매 목록에서 받아온 경매 정보

		initListArray(); // 제어에서 사용하는 List Array 초기화.
		initView(); // 경매 제어 화면 설정

		Thread thread = new Thread() {
			@Override
			public void run() {
				createClient(); // netty client 설정
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * 
	 * @MethodName initListArray
	 * @Description 제어에서 사용하는 List Array 초기화.
	 *
	 */
	private void initListArray() {
		mAuctionEntryInfoList = new ArrayList<AuctionEntryInfo>(); // 출품목록 List 초기화.

		// 출품번호 Label Array
		mArrayEntryNumLabel = Arrays.asList(mLabelEntryNum1, mLabelEntryNum2, mLabelEntryNum3, mLabelEntryNum4,
				mLabelEntryNum5);

		// 차량명 Label Array
		mArrayCarNameLabel = Arrays.asList(mLabelCarName1, mLabelCarName2, mLabelCarName3, mLabelCarName4,
				mLabelCarName5);

		// 변경사항 Label Array
		mArrayChangeContextLabel = Arrays.asList(mLabelChangeContext1, mLabelChangeContext2, mLabelChangeContext3,
				mLabelChangeContext4, mLabelChangeContext5);

		// 출품자 Label Array
		mArrayExhibitorNameLabel = Arrays.asList(mLabelExhibitorName1, mLabelExhibitorName2, mLabelExhibitorName3,
				mLabelExhibitorName4, mLabelExhibitorName5);

		// 연식 Label Array
		mArrayCarYearLabel = Arrays.asList(mLabelCarYear1, mLabelCarYear2, mLabelCarYear3, mLabelCarYear4,
				mLabelCarYear5);

		// 평가점 Label Array
		mArrayEvalPointLabel = Arrays.asList(mLabelEvalPoint1, mLabelEvalPoint2, mLabelEvalPoint3, mLabelEvalPoint4,
				mLabelEvalPoint5);

		// 부재자 Label Array
		mArrayAbsenteePriceLabel = Arrays.asList(mLabelAbsenteePrice1, mLabelAbsenteePrice2, mLabelAbsenteePrice3,
				mLabelAbsenteePrice4, mLabelAbsenteePrice5);

		// 시작가 Label Array
		mArrayStartPriceLabel = Arrays.asList(mLabelStartPrice1, mLabelStartPrice2, mLabelStartPrice3,
				mLabelStartPrice4, mLabelStartPrice5);

		// 희망가 Label Array
		mArrayHopePriceLabel = Arrays.asList(mLabelHopePrice1, mLabelHopePrice2, mLabelHopePrice3, mLabelHopePrice4,
				mLabelHopePrice5);

		// 낙/유찰 시간 Button Array
		mArrayEntryTimeBtn = Arrays.asList(mBtnEntryTime1, mBtnEntryTime2, mBtnEntryTime3, mBtnEntryTime4,
				mBtnEntryTime5);

		// ToggleButton 모든 마우스 액션 막기
		mBtnAuctionStatusStart.addEventFilter(MouseEvent.ANY, event -> {
			event.consume();
		});
		mBtnAuctionStatusAutoRaise.addEventFilter(MouseEvent.ANY, event -> {
			event.consume();
		});
		mBtnAuctionStatusProgress.addEventFilter(MouseEvent.ANY, event -> {
			event.consume();
		});
		mBtnAuctionStatusRaceCondition.addEventFilter(MouseEvent.ANY, event -> {
			event.consume();
		});
		mBtnAuctionStatusSuccessBid.addEventFilter(MouseEvent.ANY, event -> {
			event.consume();
		});
		mBtnAuctionStatusFailBid.addEventFilter(MouseEvent.ANY, event -> {
			event.consume();
		});
		mBtnEntryTime1.addEventFilter(MouseEvent.ANY, event -> {
			event.consume();
		});
		mBtnEntryTime2.addEventFilter(MouseEvent.ANY, event -> {
			event.consume();
		});
		mBtnEntryTime3.addEventFilter(MouseEvent.ANY, event -> {
			event.consume();
		});
		mBtnEntryTime4.addEventFilter(MouseEvent.ANY, event -> {
			event.consume();
		});
		mBtnEntryTime5.addEventFilter(MouseEvent.ANY, event -> {
			event.consume();
		});

	}

	/**
	 * 
	 * @MethodName initView
	 * @Description 경매 제어 프로그램 내 고정으로 사용하는 항목 설정.
	 *
	 */
	private void initView() {

		// 윈도우 X 버튼 클릭했을 때 발생하는 WindowEvent
		mStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				isBtnClose = true;
				closeStage();
			}
		});

		// 시작시간
		String auctionTime = mAuctionListViewCellItem.getAuctionTime();

		// 현재시간
		mClock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
			mLabelCurrentTime.setText(LocalDateTime.now()
					.format(DateTimeFormatter.ofPattern(mCurrentResources.getString("str.format.current.time"))));

			// 경매 자동 시작
			if (isAuctionStartAuto && auctionTime != null) {
				SimpleDateFormat startFormat = new SimpleDateFormat("HHmm");
				String now = startFormat.format(new Date());
//                mLogger.debug("now > "+now+" / auctionTime > "+auctionTime);
				int nowTime = Integer.parseInt(now);
				int startTime = Integer.parseInt(auctionTime);
				if (nowTime < startTime) {
					mLogger.debug("경매 자동 시작 대기 (Status : ON)");
				} else if (nowTime == startTime) {
					mLogger.debug("경매 자동 시작 (Status : ON)");
					isAuctionStartAuto = false;
					settingAuctionAutoStart();
					changeAuctionStart(!mBooleanAuctionStartOrStop);
				} else {
					isAuctionStartAuto = false;
					settingAuctionAutoStart();
					mLogger.debug("경매 시작 시간 지남 (Status change : ON > OFF)");
				}
			}
		}), new KeyFrame(Duration.seconds(1)) // 1초마다 갱신
		);
		mClock.setCycleCount(Animation.INDEFINITE);
		mClock.play();

		String auctionCode = "";
		if (mAuctionListViewCellItem.getAuctionCode() != null
				&& mAuctionListViewCellItem.getAuctionCode().length() > 0) {
			auctionCode = mAuctionListViewCellItem.getAuctionCode();
			if (auctionCode.equals(GlobalDefineCode.AUCTION_TYPE_REALTIME)) {
				auctionCode = mCurrentResources.getString("str.auction.realtime");
			} else if (auctionCode.equals(GlobalDefineCode.AUCTION_TYPE_SPOT)) {
				auctionCode = mCurrentResources.getString("str.auction.spot");
			}
		}
		mBtnAuctionType.setText(auctionCode); // 경매 타입

		String laneName = "";
		if (mAuctionListViewCellItem.getAuctionLaneName() != null
				&& mAuctionListViewCellItem.getAuctionLaneName().length() > 0) {
			laneName = mAuctionListViewCellItem.getAuctionLaneName();
		}
		mBtnAuctionLaneName.setText(laneName); // 경매 레인명

		String infoDetail = "";
		String auctionName = "";
		if (mAuctionListViewCellItem.getAuctionName() != null
				&& mAuctionListViewCellItem.getAuctionName().length() > 0) {
			auctionName = mAuctionListViewCellItem.getAuctionName();
			infoDetail = auctionName;
		}

		String auctionRound = mAuctionListViewCellItem.getAuctionRound();
		if (auctionRound != null && auctionRound.length() > 0) {
			auctionRound = auctionRound + mCurrentResources.getString("str.auction.round");
			if (infoDetail.length() > 0) {
				infoDetail = infoDetail + " - ";
			}
			infoDetail = infoDetail + auctionRound;
		}

		String date = "";
		if (mAuctionListViewCellItem.getAuctionDate() != null
				&& mAuctionListViewCellItem.getAuctionDate().length() > 0) {
			date = mAuctionListViewCellItem.getAuctionDate();
		}

		String time = "";
		if (mAuctionListViewCellItem.getAuctionTime() != null
				&& mAuctionListViewCellItem.getAuctionTime().length() > 0) {
			time = mAuctionListViewCellItem.getAuctionTime();
		}

		String auctionDate = "";
		if (date.length() > 0 && time.length() > 0) {
			auctionDate = CommonUtils.getInstance().getAuctionTimeHoureInAMandPM(date, time);
			infoDetail = infoDetail + mCurrentResources.getString("str.auction.subtime") + auctionDate;
		}
		mLabelAuctionInfoDetail.setText(infoDetail); // 경매명, 회차, 경매시간

		settingAuctionAutoStart(); // 경매 자동 시작 On/Off Label 설정

		// 출품차량 목록
		mLabelNoEntryData.setText(mCurrentResources.getString("str.not.exist.entry"));
		mLabelNoEntryData.setVisible(false); // hidden

		// 낙찰회원번호
		mLabelSuccessBidMember.setText("");

		changeAuctionAutoStart(true); // 출품번호 자동상승 On(true)/Off(false) (Default >> On(true))
		changeAuctionStart(false); // 경매 중지 상태

		mStringAuctionStatus = GlobalDefineCode.AUCTION_STATUS_NONE; // default setting

		setDefaultCarImageView();

		// 경매 운영 버튼 관련 이벤트 정의
		mBtnAuctionStartVBox.setOnMouseClicked(e -> {
			onActionAuctionStart();
		});

		mBtnAuctionAutoStartVBox.setOnMouseClicked(e -> {
			onActionAuctionAutoStart();
		});

		if (!isShowAlertPopup) {
			mStage.getScene().setOnKeyPressed(event -> {
				// 경매 시작/정지 단축키 설정
				if (event.getCode() == KeyCode.ENTER) {
					onActionAuctionStart();
				}
				// 출품 번호 자동 상승 On/Off 단축키 설정
				if (event.getCode() == KeyCode.F2) {
					onActionAuctionAutoStart();
				}
				if (event.getCode() == KeyCode.F5) {
					mBtnAuctionForced.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
					mBtnAuctionForced.fire();
				}
			});
		}
	}

	/**
	 * 
	 * @MethodName settingAuctionAutoStart
	 * @Description 경매 자동 시작 On/Off Label 설정
	 *
	 */
	private void settingAuctionAutoStart() {
		String autoStartStatus = " ON";
		mHboxAuctionAutoStart.getStyleClass().clear();
		if (!isAuctionStartAuto) {
			autoStartStatus = " OFF";
			mHboxAuctionAutoStart.getStyleClass().add("control-info-auction-autostart-off");
		} else {
			mHboxAuctionAutoStart.getStyleClass().add("control-info-auction-autostart-on");
		}
		mLabelAuctionStartAuto.setText(mCurrentResources.getString("str.auction.autostart") + autoStartStatus);
	}

	/**
	 * 
	 * @MethodName closeStage
	 * @Description 제어 프로그램 [X] 버튼, 종료 버튼 누르는 경우.
	 *
	 */
	private void closeStage() {
		if (mClock != null) {
			mClock.stop();
		}
		if (mRemainTimeJob != null) {
			mRemainTimeJob.cancel(true);
		}
		if (mClient != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					CommonUtils.getInstance().showLoadingDialog(mStage,
							mCurrentResources.getString("str.disconnect.server"));
				}
			});

			mClient.stopClient(new NettyClientShutDownListener() {
				@Override
				public void onShutDown(int port) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							mStage.close();
							CommonUtils.getInstance().dismissLoadingDialog();
							mLogger.debug("closeStage");
						}
					});
					mClient = null;
				}
			});
		} else {
			mStage.close();
		}
	}

	/**
	 * 
	 * @MethodName createClient
	 * @Description Netty Client 생성
	 *
	 */
	public void createClient() {
		mLogger.debug("createClient");

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				CommonUtils.getInstance().showLoadingDialog(mParentStage,
						mCurrentResources.getString("str.connect.server"));
			}
		});

		String port = mAuctionListViewCellItem.getAuctionLanePort();

		if (port != null && port.length() > 0) {
			int server_port = Integer.parseInt(port);
			try {
				mLogger.debug("=====================Connect Information[Start]=========================");
				mLogger.debug("Connect Host : " + AuctionShareSetting.CLIENT_HOST);
				mLogger.debug("Connect Port : " + server_port);
				mLogger.debug("=====================Connect Information[End]=========================");
				mClient = new AuctionShareNettyClient.Builder(AuctionShareSetting.CLIENT_HOST, server_port)
						.setController(this).buildAndRun();
			} catch (Exception e) {
				mLogger.debug("createClient Fail");
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (mClock != null) {
							mClock.stop();
						}
						CommonUtils.getInstance().dismissLoadingDialog();
						CommonUtils.getInstance().showAlertPopupOneButton(mParentStage,
								mCurrentResources.getString("str.auction.not.accessible"),
								mCurrentResources.getString("str.ok"));
					}
				});

			}
		} else {
			// 포트 정보 없음.
			mLogger.debug("createClient Fail no server port : " + port);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (mClock != null) {
						mClock.stop();
					}
					CommonUtils.getInstance().dismissLoadingDialog();
					CommonUtils.getInstance().showAlertPopupOneButton(mParentStage,
							mCurrentResources.getString("str.not.exist.port.info"),
							mCurrentResources.getString("str.ok"));
				}
			});
		}
	}

	/**
	 * 
	 * @MethodName entryDataLoad
	 * @Description 출품 목록 데이터 표시
	 *
	 */
	private void entryDataLoad() {

		for (int i = 0; i < 5; i++) {

			Label entryNumLabel = mArrayEntryNumLabel.get(i); // 출품번호
			Label carNameLabel = mArrayCarNameLabel.get(i); // 차량명
			Label changeContextLabel = mArrayChangeContextLabel.get(i); // 변경사항
			Label exhibitorNameLabel = mArrayExhibitorNameLabel.get(i); // 출품자
			Label carYearLabel = mArrayCarYearLabel.get(i); // 연식
			Label evalPointLabel = mArrayEvalPointLabel.get(i); // 평가점
			Label auctionAbsenteePriceLabel = mArrayAbsenteePriceLabel.get(i); // 부재자
			Label auctionStartPriceLabel = mArrayStartPriceLabel.get(i); // 시작가
			Label auctionHopePriceLabel = mArrayHopePriceLabel.get(i); // 희망가

			Label[] entryLabelArray = { entryNumLabel, carNameLabel, changeContextLabel, exhibitorNameLabel,
					carYearLabel, evalPointLabel, auctionAbsenteePriceLabel, auctionStartPriceLabel,
					auctionHopePriceLabel };

			if (mAuctionEntryInfoList.size() == 0) { // 출품 목록이 없다.
				entryDataLabelSetVisible(true, entryLabelArray); // show
			} else {
				entryDataLabelSetVisible(false, entryLabelArray); // hidden
				if (i < mAuctionEntryInfoList.size()) {
					AuctionEntryInfo entryInfo = mAuctionEntryInfoList.get(i);

					String entryNum = entryDataStringNullCheck(entryInfo.getAuctionEntryNum(), false);
					entryNumLabel.setText(entryNum);

					String carName = entryDataStringNullCheck(entryInfo.getCarName(), false);
					carNameLabel.setText(carName);

					String contextLabel = entryDataStringNullCheck(entryInfo.getChangeContext(), false);
					changeContextLabel.setText(contextLabel);

					String exhibitorName = entryDataStringNullCheck(entryInfo.getExhibitorName(), false);
					exhibitorNameLabel.setText(exhibitorName);

					String carYear = entryDataStringNullCheck(entryInfo.getCarYear(), false);
					carYearLabel.setText(carYear);

					String evalPoint = entryDataStringNullCheck(entryInfo.getEvalPoint(), false);
					evalPointLabel.setText(evalPoint);

					String auctionAbsenteePrice = entryDataStringNullCheck(entryInfo.getAuctionAbsenteePrice(), true);
					auctionAbsenteePriceLabel.setText(auctionAbsenteePrice);

					String auctionStartPrice = entryDataStringNullCheck(entryInfo.getAuctionStartPrice(), true);
					auctionStartPriceLabel.setText(auctionStartPrice);

					String auctionHopePrice = entryDataStringNullCheck(entryInfo.getAuctionHopePrice(), true);
					auctionHopePriceLabel.setText(auctionHopePrice);
				} else {
					for (Label entryLabel : entryLabelArray) {
						entryLabel.setText("");
					}
				}
			}
		}
	}

	/**
	 * 
	 * @MethodName entryDataStringNullCheck
	 * @Description 출품정보 String Null 체크 및 가격 천단위 콤마.
	 * 
	 * @param entryString
	 * @param comma
	 * @return
	 */
	private String entryDataStringNullCheck(String entryString, boolean comma) {
		if (entryString != null && entryString.length() > 0 && !entryString.equals("null")) {
			if (comma) {
				return NumberFormat.getInstance().format(Integer.parseInt(entryString));
			} else {
				return entryString;
			}
		} else {
			return "-";
		}
	}

	/**
	 * 
	 * @MethodName entryDataLabelSetVisible
	 * @Description EntryData Labels Hidden Or Show
	 * 
	 * @param visible
	 * @param entryLabelArray
	 */
	private void entryDataLabelSetVisible(boolean visible, Label[] entryLabelArray) {
		mLabelNoEntryData.setVisible(visible);
		for (Label entryLabel : entryLabelArray) {
			entryLabel.setVisible(!visible);
		}
	}

	/**
	 * 
	 * @MethodName changeAuctionState
	 * @Description 경매 진행 상태 표시
	 * 
	 * @param status
	 */
	private void changeAuctionState(String status) {
		mLogger.debug("changeAuctionState : " + status);

		mBtnAuctionStatusStart.setSelected(false);
		mBtnAuctionStatusAutoRaise.setSelected(false);
		mBtnAuctionStatusProgress.setSelected(false);
		mBtnAuctionStatusRaceCondition.setSelected(false);
		mBtnAuctionStatusSuccessBid.setSelected(false);
		mBtnAuctionStatusFailBid.setSelected(false);

		if (status.equals(GlobalDefineCode.AUCTION_STATUS_START)) {
			mBtnAuctionStatusStart.setSelected(true);
		} else if (status.equals(GlobalDefineCode.AUCTION_STATUS_SLOWDOWN)) {
			mBtnAuctionStatusAutoRaise.setSelected(true);
		} else if (status.equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
			mBtnAuctionStatusProgress.setSelected(true);
		} else if (status.equals(GlobalDefineCode.AUCTION_STATUS_COMPETITIVE)) {
			mBtnAuctionStatusRaceCondition.setSelected(true);
		} else if (status.equals(GlobalDefineCode.AUCTION_STATUS_SUCCESS)) {
			mBtnAuctionStatusSuccessBid.setSelected(true);
		} else if (status.equals(GlobalDefineCode.AUCTION_STATUS_FAIL)) {
			mBtnAuctionStatusFailBid.setSelected(true);
		}

	}

	/**
	 * 
	 * @MethodName changeAuctionTime
	 * @Description 낙/유찰 시간 표시
	 * 
	 * @param time
	 */
	public void changeAuctionTime(String time) {
		int msTime = Integer.parseInt(time);

		mRemainTime = msTime;

		if (msTime > 4000) {
			setAuctionTimeBtnColor(5);
		} else if (msTime <= 4000 && msTime > 3000) {
			setAuctionTimeBtnColor(4);
		} else if (msTime <= 3000 && msTime > 2000) {
			setAuctionTimeBtnColor(3);
		} else if (msTime <= 2000 && msTime > 1000) {
			setAuctionTimeBtnColor(2);
		} else if (msTime <= 1000 && msTime > 0) {
			setAuctionTimeBtnColor(1);
		} else {
			setAuctionTimeBtnColor(0);
		}
	}

	/**
	 * 
	 * @MethodName setAuctionTimeBtnColor
	 * @Description 남은 시간만큼 색상 표시
	 * 
	 * @param count > 남은 시간
	 */
	private void setAuctionTimeBtnColor(int count) {
		for (int i = 0; i < mArrayEntryTimeBtn.size(); i++) {
			ToggleButton mBtnEntryTime = mArrayEntryTimeBtn.get(i);
			if (i < count) {
				mBtnEntryTime.setSelected(true);
			} else {
				mBtnEntryTime.setSelected(false);
			}
		}
	}

	/**
	 * 
	 * @MethodName startAuctionTimer
	 * @Description 낙/유찰 시간 Timer
	 *
	 */
	private void startAuctionTimer() {
		if (mRemainTimeJob != null) {
			mRemainTimeJob.cancel(true);
		}

		mLogger.debug("경매 진행 타이머가 동작을 시작합니다.");

		// 초기화
		changeAuctionTime(mCurrentSetting.getBiddingTime());

		mRemainTimeJob = mService.scheduleAtFixedRate(new AuctionTimerJob(), BASE_DELAY_TIME, REMAIN_CHECK_DELAY_TIME,
				TimeUnit.MILLISECONDS);
	}

	/**
	 * 
	 * @ClassName AuctionControlController.java
	 * @Description 낙/유찰 시간 Timer Job
	 * @anthor ishift
	 * @since
	 */
	private class AuctionTimerJob implements Runnable {

		@Override
		public void run() {
			mRemainTime = mRemainTime - REMAIN_CHECK_DELAY_TIME;
			if (mRemainTime < 0) {
				if (mRemainTimeJob != null) {
					mRemainTimeJob.cancel(true);
				}
			}
			changeAuctionTime(Integer.toString(mRemainTime));
		}

	}

	/**
	 * 
	 * @MethodName changeHopePrice
	 * @Description 현재 출품차량의 희망가격을 출품목록 데이터에서 찾아 설정함.
	 *
	 */
	private void changeHopePrice() {
		String hopePrice = "";
		if (mAuctionEntryInfoList.size() > 0) {
			String entryNum = mLabelAuctionEntryNum.getText();
			// 출품번호가 같은 차량정보의 희망가격을 찾는다.
			for (AuctionEntryInfo entryInfo : mAuctionEntryInfoList) {
				if (entryInfo.getAuctionEntryNum().equals(entryNum)) {
					hopePrice = entryInfo.getAuctionHopePrice();
					break;
				}
			}
		}

		mLabelHopePrice.setText(entryDataStringNullCheck(hopePrice, true));
	}

	/**
	 * 
	 * @MethodName setMemberNumReplace
	 * @Description 회원번호 표시할때 null, NULL, SYSTEM 문구 예외처리
	 * 
	 * @param memberNum
	 * @return memberNum or "" (공백)
	 */
	private String setMemberNumReplace(String memberNum) {
		String result = memberNum;
		if (memberNum.equals("null") || memberNum.equals("SYSTEM") || memberNum.equals("NULL")) {
			result = "";
		}
		return result;
	}

	/**
	 * 
	 * @MethodName changeAuctionAutoStart
	 * @Description 출품번호 자동상승 토글 버튼 액션
	 * 
	 * @param status
	 */
	private void changeAuctionAutoStart(boolean status) {
		changeAuctionAutoStartStatus(status);

		if (mAuctionEntryInfoList.size() > 0) {
			String entryNum = mAuctionEntryInfoList.get(0).getAuctionEntryNum();
			if (status) {
				mClient.sendMessage(new AutoMode(entryNum));
			} else {
				mClient.sendMessage(new ManualMode(entryNum));

			}
		}
	}

	/**
	 * 
	 * @MethodName changeAuctionAutoStartStatus
	 * @Description 출품번호 자동상승 토글 버튼 문구 설정
	 * 
	 * @param status
	 */
	private void changeAuctionAutoStartStatus(boolean status) {
		mBooleanEntryNumAutoRise = status;

		if (status) {
			mAuctionAutoStartButtonLabel.setText(mCurrentResources.getString("str.auto.mode"));
			mAuctionAutoStartDescLabel.setText(mCurrentResources.getString("str.auto.mode.status"));
		} else {
			mAuctionAutoStartButtonLabel.setText(mCurrentResources.getString("str.manual.mode"));
			mAuctionAutoStartDescLabel.setText(mCurrentResources.getString("str.manual.mode.status"));
		}
	}

	/**
	 * 
	 * @MethodName changeAuctionStart
	 * @Description 경매 시작/정지 토글 버튼 액션
	 * 
	 * @param status
	 */
	private void changeAuctionStart(boolean status) {
		changeAuctionStartStatus(status);
		if (mAuctionEntryInfoList.size() > 0) {
			String entryNum = mAuctionEntryInfoList.get(0).getAuctionEntryNum();
			String flagAutoMode = "Y";
			if (!mBooleanEntryNumAutoRise) {
				flagAutoMode = "N";
			}
			if (status) {
				mClient.sendMessage(new StartAuction(flagAutoMode, entryNum));
			} else {
				mClient.sendMessage(new PauseAuction(entryNum));
			}

		}
	}

	/**
	 * 
	 * @MethodName changeAuctionStartStatus
	 * @Description 경매 시작/정지 토글 버튼 문구 설정
	 * 
	 * @param status
	 */
	private void changeAuctionStartStatus(boolean status) {
		mBooleanAuctionStartOrStop = status;

		if (status) {
			// 경매 시작 상태
			mAuctionStartButtonLabel.setText(mCurrentResources.getString("str.auction.start"));
			mAuctionStartDescLabel.setText(mCurrentResources.getString("str.auction.start.status"));
		} else {
			// 경매 정지 상태
			mAuctionStartButtonLabel.setText(mCurrentResources.getString("str.auction.stop"));
			mAuctionStartDescLabel.setText(mCurrentResources.getString("str.auction.stop.status"));
		}

	}

	/**
	 * 
	 * @MethodName setDefaultCarImageView
	 * @Description Place Holder 이미지
	 *
	 */
	private void setDefaultCarImageView() {
		// 창 크기 변환에 따라 전개도 이미지 사이즈 조절, 부모 가로/세로 사이즈와 바인딩
		mCarImageView.fitWidthProperty().bind(mGridPaneCarImage.widthProperty());
		mCarImageView.fitHeightProperty().bind(mGridPaneCarImage.heightProperty());

		mCarImageView.setImage(new Image("/com/glovis/controller/resource/place_holder.png"));
	}

	/**
	 * 
	 * @MethodName changeAuctionSetting
	 * @Description 경매설정 정보 설정
	 *
	 */
	private void changeAuctionSetting() {
		if (mCurrentSetting != null) {
			// 기준금액
			if (mCurrentSetting.getBaseStartPrice() != null && mCurrentSetting.getBaseStartPrice().length() > 0) {
				mLabelBasePrice.setText(
						NumberFormat.getInstance().format(Integer.parseInt(mCurrentSetting.getBaseStartPrice()))
								+ mCurrentResources.getString("str.price.unit"));
			} else {
				mLabelBasePrice.setText("");
			}

			// 상승금액
			if (mCurrentSetting.getMoreRisePrice() != null && mCurrentSetting.getMoreRisePrice().length() > 0) {
				mLabelRisingPrice
						.setText(NumberFormat.getInstance().format(Integer.parseInt(mCurrentSetting.getMoreRisePrice()))
								+ mCurrentResources.getString("str.price.unit"));
			} else {
				mLabelRisingPrice.setText("");
			}

			// 경매시간
			if (mCurrentSetting.getBiddingTime() != null && mCurrentSetting.getBiddingTime().length() > 0) {
				int milliseconds = Integer.parseInt(mCurrentSetting.getBiddingTime());
				int seconds = (int) (milliseconds / 1000) % 60;
				mLabelNextEntryTime.setText(Integer.toString(seconds) + mCurrentResources.getString("str.second.unit"));
				changeAuctionTime(mCurrentSetting.getBiddingTime());
			} else {
				mLabelNextEntryTime.setText("");
			}

			// 자동상승
			if (mCurrentSetting.getMaxAutoUpCount() != null && mCurrentSetting.getMaxAutoUpCount().length() > 0) {
				mLabelAutoRiseCount
						.setText(mCurrentSetting.getMaxAutoUpCount() + mCurrentResources.getString("str.round.unit"));
			} else {
				mLabelAutoRiseCount.setText("");
			}

			// 경매 시작 상태
			if (mCurrentSetting.getFlagAuctionStart() != null && mCurrentSetting.getFlagAuctionStart().length() > 0) {
				if (mCurrentSetting.getFlagAuctionStart().equals("Y")) { // 시작상태
					mBooleanAuctionStartOrStop = true;
				} else {
					mBooleanAuctionStartOrStop = false;
				}
				changeAuctionStartStatus(mBooleanAuctionStartOrStop);
			}

			// 현재 경매 진행 모드
			if (mCurrentSetting.getFlagAuctionAutoMode() != null
					&& mCurrentSetting.getFlagAuctionAutoMode().length() > 0) {
				if (mCurrentSetting.getFlagAuctionAutoMode().equals("Y")) { // 자동진행
					mBooleanEntryNumAutoRise = true;
				} else {
					mBooleanEntryNumAutoRise = false;
				}
				changeAuctionAutoStartStatus(mBooleanEntryNumAutoRise);
			}
		}
	}

	/**
	 * 
	 * @MethodName sendEditSetting
	 * @Description 환경설정 데이터 경매 서버로 send
	 * 
	 * @param currentSetting
	 */
	private void sendEditSetting(CurrentSetting currentSetting) {
		Map<String, String> setting = new HashMap<String, String>();

		setting.put("baseStartPrice", currentSetting.getBaseStartPrice());
		setting.put("moreRisePrice", currentSetting.getMoreRisePrice());
		setting.put("belowRisePrice", currentSetting.getBelowRisePrice());
		setting.put("maxRisePrice", currentSetting.getMaxRisePrice());
		setting.put("biddingTime", currentSetting.getBiddingTime());
		setting.put("biddingAdditionalTime", currentSetting.getBiddingAdditionalTime());
		setting.put("biddingIntervalTime", currentSetting.getBiddingIntervalTime());
		setting.put("maxAutoUpCount", currentSetting.getMaxAutoUpCount());

		if (mBooleanAuctionStartOrStop) {
			// 경매 시작 상태
			setting.put("flagAuctionStart", "Y");
		} else {
			// 경매 정지 상태
			setting.put("flagAuctionStart", "N");
		}

		if (mBooleanEntryNumAutoRise) {
			// 자동 진행
			setting.put("flagAuctionAutoMode", "Y");
		} else {
			// 수동 진행
			setting.put("flagAuctionAutoMode", "N");
		}

		mClient.sendMessage(new EditSetting(setting));
	}

	/**
	 * 
	 * @MethodName sendToastMessage
	 * @Description iOS/AOS/PC 응찰 프로그램으로 메시지(Toast) 전송
	 * 
	 * @param message
	 */
	private void sendToastMessage(String message) {
		mLogger.info("Send Toast Message : " + message);
		mClient.sendMessage(new ToastMessageRequest(message));
	}

	@FXML
	private void onMouseClickedSetting() {
		// dialog 팝업이 열려있는 경우 버튼 동작 막음.
		if (isShowAlertPopup) {
			return;
		}

		// 환경설정 OPEN
		try {
			FXMLLoader fxmlLoader = MoveStageUtil.getInstance().loadSettingFXMLLoader();
			Parent rootLayout = (Parent) fxmlLoader.load();
			mStageSetting = new Stage();
			mStageSetting.initModality(Modality.APPLICATION_MODAL);
			mStageSetting.initStyle(StageStyle.UTILITY);
			mStageSetting.setScene(new Scene(rootLayout));

			MoveStageUtil.getInstance().setResizeScreen(mStageSetting, false);
			MoveStageUtil.getInstance().setScreenMinSize(mStageSetting);
			AuctionSettingController controller = fxmlLoader.getController();
			controller.setStage(mStageSetting, mCurrentSetting, isAuctionStartAuto);

			controller.mBtnSaveSetting.setOnAction(e -> {
				mCurrentSetting = controller.getData();
				isAuctionStartAuto = controller.getAutoStartFlag();
				mStageSetting.close();
				sendEditSetting(mCurrentSetting); // send auction setting info [auction controller -> auction server]
				changeAuctionSetting(); // refresh auction setting label
				settingAuctionAutoStart();
			});

			// 제어프로그램 중앙에 나오도록 설정
			double centerXPosition = mStage.getX() + mStage.getWidth() / 2d;
			double centerYPosition = mStage.getY() + mStage.getHeight() / 2d;
			mStageSetting.setOnShown(e -> {
				mStageSetting.setX(centerXPosition - mStageSetting.getWidth() / 2d);
				mStageSetting.setY(centerYPosition - mStageSetting.getHeight() / 2d);
			});

			mStageSetting.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void onMouseClickedMessage() {
		// dialog 팝업이 열려있는 경우 버튼 동작 막음.
		if (isShowAlertPopup) {
			return;
		}

		// 메시지 OPEN
		try {
			FXMLLoader fxmlLoader = MoveStageUtil.getInstance().loadMessageFXMLLoader();
			Parent rootLayout = (Parent) fxmlLoader.load();
			mStageMessage = new Stage();
			mStageMessage.initModality(Modality.APPLICATION_MODAL);
			mStageMessage.initStyle(StageStyle.UTILITY);
			mStageMessage.setScene(new Scene(rootLayout));
			MoveStageUtil.getInstance().setResizeScreen(mStageMessage, false);
			MoveStageUtil.getInstance().setScreenMinSize(mStageMessage);
			AuctionMessageController controller = fxmlLoader.getController();
			controller.setStage(mStageMessage);
			controller.mBtnSend.setOnAction(e -> {
				if (controller.sendMessageTextNullCheck()) {
					String sendMessage = controller.getSendMessage();
					sendToastMessage(sendMessage);
				} else {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							CommonUtils.getInstance().showAlertPopupOneButton(mStageMessage,
									mCurrentResources.getString("str.send.message"),
									mCurrentResources.getString("str.ok"));
						}
					});

				}
			});
			// 제어프로그램 중앙에 나오도록 설정
			double centerXPosition = mStage.getX() + mStage.getWidth() / 2d;
			double centerYPosition = mStage.getY() + mStage.getHeight() / 2d;
			mStageMessage.setOnShown(e -> {
				mStageMessage.setX(centerXPosition - mStageMessage.getWidth() / 2d);
				mStageMessage.setY(centerYPosition - mStageMessage.getHeight() / 2d);
			});
			mStageMessage.showAndWait();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void onMouseClickedShutdown() {
		mLogger.debug("onMouseClicked Shutdown");
		// dialog 팝업이 열려있는 경우 버튼 동작 막음.
		if (isShowAlertPopup) {
			return;
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Optional<ButtonType> result = CommonUtils.getInstance().showAlertPopupTwoButton(mStage,
						mCurrentResources.getString("str.finish.program"), mCurrentResources.getString("str.ok"),
						mCurrentResources.getString("str.cancel"));
				if (result.isPresent() && result.get().getText() == mCurrentResources.getString("str.ok")) {
					isBtnClose = true;
					closeStage();
				}
			}
		});
	}

	@FXML
	private void onActionAuctionStart() {
		mLogger.debug("경매 시작/정지");
		// dialog 팝업이 열려있는 경우 버튼 동작 막음.
		if (isShowAlertPopup) {
			return;
		}

		if (mBooleanAuctionStartOrStop) {
			Optional<ButtonType> result = CommonUtils.getInstance().showAlertPopupTwoButton(mStage,
					mCurrentResources.getString("str.stop.command"), mCurrentResources.getString("str.ok"),
					mCurrentResources.getString("str.cancel"));
			if (result.isPresent() && result.get().getText() == mCurrentResources.getString("str.ok")) {
				isAuctionStartAuto = false;
				changeAuctionStart(!mBooleanAuctionStartOrStop);
			}
		} else {
			Optional<ButtonType> result = CommonUtils.getInstance().showAlertPopupTwoButton(mStage,
					mCurrentResources.getString("str.start.command"), mCurrentResources.getString("str.ok"),
					mCurrentResources.getString("str.cancel"));
			if (result.isPresent() && result.get().getText() == mCurrentResources.getString("str.ok")) {
				isAuctionStartAuto = false;
				changeAuctionStart(!mBooleanAuctionStartOrStop);
			}
		}
	}

	@FXML
	private void onActionAuctionAutoStart() {
		mLogger.debug("출품번호 자동상승");
		// dialog 팝업이 열려있는 경우 버튼 동작 막음.
		if (isShowAlertPopup) {
			return;
		}

		if (mBooleanEntryNumAutoRise) {
			Optional<ButtonType> result = CommonUtils.getInstance().showAlertPopupTwoButton(mStage,
					mCurrentResources.getString("str.manualmode.command"), mCurrentResources.getString("str.ok"),
					mCurrentResources.getString("str.cancel"));
			if (result.isPresent() && result.get().getText() == mCurrentResources.getString("str.ok")) {
				changeAuctionAutoStart(!mBooleanEntryNumAutoRise);
			}
		} else {
			Optional<ButtonType> result = CommonUtils.getInstance().showAlertPopupTwoButton(mStage,
					mCurrentResources.getString("str.automode.command"), mCurrentResources.getString("str.ok"),
					mCurrentResources.getString("str.cancel"));
			if (result.isPresent() && result.get().getText() == mCurrentResources.getString("str.ok")) {
				changeAuctionAutoStart(!mBooleanEntryNumAutoRise);
			}
		}
	}

	@FXML
	private void onActionAuctionForced() {
		mLogger.debug("강제유찰");
		// dialog 팝업이 열려있는 경우 버튼 동작 막음.
		if (isShowAlertPopup) {
			return;
		}

		// 경매 시작, 자동상승, 진행, 경쟁 상태일때만 강제 유찰 기능 동작.
		if (mStringAuctionStatus.equals(GlobalDefineCode.AUCTION_STATUS_START)
				|| mStringAuctionStatus.equals(GlobalDefineCode.AUCTION_STATUS_SLOWDOWN)
				|| mStringAuctionStatus.equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)
				|| mStringAuctionStatus.equals(GlobalDefineCode.AUCTION_STATUS_COMPETITIVE)) {
			// String entryNum = mAuctionEntryInfoList.get(0).getAuctionEntryNum();
			String entryNum = mLabelAuctionEntryNum.getText();

			Optional<ButtonType> result = CommonUtils.getInstance().showAlertPopupTwoButton(mStage,
					mCurrentResources.getString("str.entry.pass.command1") + entryNum
							+ mCurrentResources.getString("str.entry.pass.command2"),
					mCurrentResources.getString("str.ok"), mCurrentResources.getString("str.cancel"));
			if (result.isPresent() && result.get().getText() == mCurrentResources.getString("str.ok")) {
				mClient.sendMessage(new PassAuction(entryNum));
			}
		} else {
			CommonUtils.getInstance().showAlertPopupOneButton(mStage,
					mCurrentResources.getString("str.entry.pass.validation"), mCurrentResources.getString("str.ok"));
		}
	}

	@Override
	public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAuctionStatus(AuctionStatus auctionStatus) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// mResponseAuctionStatus = auctionStatus;

				mStringAuctionStatus = auctionStatus.getState(); // 현재 경매 상태.

				// 출품차량 정보 표시
				mLabelAuctionEntryNum.setText(auctionStatus.getEntryNum()); // 출품번호

				mLabelCurrentPrice.setText(entryDataStringNullCheck(auctionStatus.getCurrentPrice(), true)); // 현재가격
				mLabelNumberBidders.setText(
						NumberFormat.getInstance().format(Integer.parseInt(auctionStatus.getCurrentBidderCount()))); // 응찰자

				changeAuctionState(auctionStatus.getState()); // 경매상태

				changeHopePrice(); // 희망가격

				if (auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_SUCCESS)
						|| auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_FAIL)) {
					// 낙/유찰 완료 시 0번째 출품목록 삭제 (완료건)
					if (mAuctionEntryInfoList.size() > 0) {

						String entryNo = auctionStatus.getEntryNum();
						String listEntryNo = mAuctionEntryInfoList.get(0).getAuctionEntryNum();
						if (entryNo.equals(listEntryNo)) {
							mAuctionEntryInfoList.remove(0);
						}

						if (mAuctionEntryInfoList.size() < 5) {
							if (Integer.parseInt(auctionStatus.getRemainEntryCount()) >= 5) {
								String seqNum = String.valueOf(Integer.valueOf(
										mAuctionEntryInfoList.get(mAuctionEntryInfoList.size() - 1).getAuctionSeqNum())
										+ 1);
								mClient.sendMessage(new EntryInfo(seqNum));
							}
						}
					}

					if (!mBooleanEntryNumAutoRise) {
						mBooleanAuctionStartOrStop = false;
						// 경매 정지 상태
						mAuctionStartButtonLabel.setText(mCurrentResources.getString("str.auction.stop"));
						mAuctionStartDescLabel.setText(mCurrentResources.getString("str.auction.stop.status"));
					}
				}

				// 경매목록 데이터 보정
				if (auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_READY)
						|| auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_START)) {
					if (mAuctionEntryInfoList.size() > 1) {
						String entryNo = auctionStatus.getEntryNum();
						String listEntryNo = mAuctionEntryInfoList.get(0).getAuctionEntryNum();
						if (!entryNo.equals(listEntryNo)) {
							String nextEntryNo = mAuctionEntryInfoList.get(1).getAuctionEntryNum();
							if (entryNo.equals(nextEntryNo)) {
								mAuctionEntryInfoList.remove(0);

								if (mAuctionEntryInfoList.size() < 5) {
									if (Integer.parseInt(auctionStatus.getRemainEntryCount()) >= 5) {
										String seqNum = String.valueOf(Integer.valueOf(mAuctionEntryInfoList
												.get(mAuctionEntryInfoList.size() - 1).getAuctionSeqNum()) + 1);
										mClient.sendMessage(new EntryInfo(seqNum));
									}
								}
							}
						}
					}
				}

				// 낙/유찰 시간 설정 및 타이머 셋팅
				int time = Integer.parseInt(auctionStatus.getTime());
				int bidTime = Integer.parseInt(mCurrentSetting.getBiddingTime());

				if (auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_START)) {
					startAuctionTimer();
				} else if (auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_SUCCESS)
						|| auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_FAIL)
						|| auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_STOP)
						|| auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_COMPLETED)
						|| auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {
					if (mRemainTimeJob != null) {
						mRemainTimeJob.cancel(true);
					}
					changeAuctionTime(auctionStatus.getTime());
				} else if (auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
					if (time <= bidTime && time > (bidTime - 1000)) {
						startAuctionTimer(); // 타이머 초기화 하고 다시 돌림
					} else {
						changeAuctionTime(auctionStatus.getTime()); // 낙/유찰 시간
					}
				} else {
					changeAuctionTime(auctionStatus.getTime()); // 낙/유찰 시간
				}

				// 낙찰 회원 번호 표시
				if (auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_SUCCESS)) {
					mLabelSuccessBidMember.setText(auctionStatus.getRank1MemberNum());
				} else if (auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_FAIL)) {
					mLabelSuccessBidMember.setText(mCurrentResources.getString("str.status.fail"));
				} else {
					mLabelSuccessBidMember.setText("");
				}

				mLabelRank1MemberNum.setText(setMemberNumReplace(auctionStatus.getRank1MemberNum())); // 1순위 회원번호
				mLabelRank2MemberNum.setText(setMemberNumReplace(auctionStatus.getRank2MemberNum())); // 2순위 회원번호
				mLabelRank3MemberNum.setText(setMemberNumReplace(auctionStatus.getRank3MemberNum())); // 3순위 회원번호

				int finishEntryCount = Integer.parseInt(auctionStatus.getFinishEntryCount());
				int remainEntryCount = Integer.parseInt(auctionStatus.getRemainEntryCount());
				int totalEntryCount = finishEntryCount + remainEntryCount;
				mLabelTotalEntryCount.setText(NumberFormat.getInstance().format(totalEntryCount)); // 총 출품 차량

				// 경매 완료 상태
				if (auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {
					mLabelFinishEntryCount.setText(NumberFormat.getInstance().format(totalEntryCount)); // 완료
					mLabelLeftoverEntry.setText("0"); // 잔여
					isAuctionFinished = true;
					isShowAlertPopup = true;
					mAlertDialog = CommonUtils.getInstance().setAlertPopupStyle(mStage,
							CommonUtils.getInstance().ALERTPOPUP_ONE_BUTTON,
							mCurrentResources.getString("str.auction.complete"), mCurrentResources.getString("str.ok"),
							"");
					mAlertDialog.setOnHiding(event -> {
						isShowAlertPopup = false;
						closeStage(); // 닫기
					});
					mAlertDialog.show();
				} else {
					mLabelFinishEntryCount.setText(
							NumberFormat.getInstance().format(Integer.parseInt(auctionStatus.getFinishEntryCount()))); // 완료
					mLabelLeftoverEntry.setText(
							NumberFormat.getInstance().format(Integer.parseInt(auctionStatus.getRemainEntryCount()))); // 잔여

					// 잔여 출품차량이 5개 미만인 경우 onResponseCarInfo에 있는 entryDataLoad() 로직을 타지 않지않음.
					// 마지막 5건의 출품차량 목록 갱신을 위해 여기에도 entryDataLoad() 로직을 넣어준다.
					if (Integer.parseInt(auctionStatus.getRemainEntryCount()) < 5) {
						entryDataLoad();
					}
				}
			}
		});
	}

	@Override
	public void onCurrentSetting(CurrentSetting currentSetting) {
		mCurrentSetting = currentSetting;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				changeAuctionSetting();
			}
		});
	}

	@Override
	public void onResponseCarInfo(ResponseEntryInfo responseCarInfo) {
		// TODO Auto-generated method stub
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				mAuctionEntryInfoList.add(new AuctionEntryInfo(responseCarInfo));

				if (mStage.isShowing()) {
					entryDataLoad();
				}

				if (!isFirstEntryCarImageLoad && !isFirstEntryCarImageLoadTimer) {
					mLogger.debug("isFirstEntryCarImageLoad!");
					isFirstEntryCarImageLoadTimer = true;
					startCheckResourceCheckTimer(responseCarInfo);
				}

				if (mAuctionEntryInfoList.size() < 5) {
					String seqNum = String.valueOf(Integer.valueOf(
							mAuctionEntryInfoList.get(mAuctionEntryInfoList.size() - 1).getAuctionSeqNum()) + 1);
					mClient.sendMessage(new EntryInfo(seqNum));
				}

			}
		});
	}

	@Override
	public void onToastMessage(ToastMessage toastMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectorInfo(BidderConnectInfo bidderConnectInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo) {
		mLogger.debug("onResponseConnectionInfo");
		if (mConnectInfoJob != null) {
			mConnectInfoJob.cancel(true);
		}

		/*
		 * Platform.runLater(new Runnable() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub
		 * CommonUtils.getInstance().dismissLoadingDialog(); } });
		 */
		mLogger.debug("[onResponseConnectionInfo] result : " + responseConnectionInfo.getResult());
		if (responseConnectionInfo.getResult().equals(GlobalDefineCode.CONNECT_SUCCESS)
				|| responseConnectionInfo.getResult().equals(GlobalDefineCode.CONNECT_DUPLICATE)) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					mLogger.debug("[제어프로그램 열기]");
					/*
					 * mStage.show(); MoveStageUtil.getInstance().setScreenMinSize(mStage);
					 * MoveStageUtil.getInstance().setParentPositionCenter(mParentStage, mStage);
					 */
				}
			});
		} else {
			mLogger.debug("[onResponseConnectionInfo] No Open : " + responseConnectionInfo.getResult());
			isAuctionFinished = true;
			mClient.stopClient(new NettyClientShutDownListener() {
				@Override
				public void onShutDown(int port) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							CommonUtils.getInstance().dismissLoadingDialog();
							if (responseConnectionInfo.getResult().equals(GlobalDefineCode.CONNECT_DUPLICATE_FAIL)) {
								CommonUtils.getInstance().showAlertPopupOneButton(mParentStage,
										mCurrentResources.getString("str.redundant.connection"),
										mCurrentResources.getString("str.ok"));
							} else {
								CommonUtils.getInstance().showAlertPopupOneButton(mParentStage,
										mCurrentResources.getString("str.auction.fail.connection"),
										mCurrentResources.getString("str.ok"));
							}
						}
					});
				}
			});

		}
	}

	@Override
	public void onFavoriteCarInfo(FavoriteEntryInfo favoriteCarInfo) {

	}

	@Override
	public void onAbsenteeUserInfo(AbsenteeUserInfo absenteeUserInfo) {

	}

	@Override
	public void onExceptionCode(ExceptionCode exceptionCode) {
		// TODO Auto-generated method stub
		mLogger.debug("onExceptionCode [" + exceptionCode.getErrorCode() + "]");

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// 요청결과 미존재
				if (exceptionCode.getErrorCode() != null && exceptionCode.getErrorCode()
						.equals(GlobalDefineCode.RESPONSE_REQUEST_NOT_RESULT_EXCEPTION)) {
					mLogger.debug("요청결과 미존재");
					/*
					 * int remainEntryCount =
					 * Integer.parseInt(mResponseAuctionStatus.getRemainEntryCount()); if
					 * (remainEntryCount > 5) { if (mAuctionEntryInfoList.size() < 5) { String
					 * entryNum = String.valueOf(Integer.valueOf(mResponseEntryNum) + 1);
					 * mResponseEntryNum = entryNum; mClient.sendMessage(new CarInfo(entryNum)); } }
					 * else { if (remainEntryCount > mAuctionEntryInfoList.size()) { String entryNum
					 * = String.valueOf(Integer.valueOf(mResponseEntryNum) + 1); mResponseEntryNum =
					 * entryNum; mClient.sendMessage(new CarInfo(entryNum)); } }
					 */
				}
				// mLogger.debug("entryNum [" + mResponseEntryNum + "]");
			}
		});
	}

	@Override
	public void onActiveChannel() {
		mLogger.debug("onActiveChannel");
		onConnectInfo();
	}

	@Override
	public void onActiveChannel(Channel channel) {
		mLogger.debug("onActiveChannel channel");
		onConnectInfo();
	}

	@Override
	public void onConnectionException(int port) {
		mLogger.debug("onConnectionException");
		if (isBtnClose || isAuctionFinished) {
			return;
		}

		mClient.stopClient(new NettyClientShutDownListener() {
			@Override
			public void onShutDown(int port) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						CommonUtils.getInstance().dismissLoadingDialog();
						CommonUtils.getInstance().showAlertPopupOneButton(mParentStage,
								mCurrentResources.getString("str.auction.not.accessible"),
								mCurrentResources.getString("str.ok"));
					}
				});
			}
		});

	}

	@Override
	public void onChannelInactive(int port) {
		// TODO Auto-generated method stub
		mLogger.debug("onChannelInactive");
		mLogger.debug("[접속 끊김 onChannelInactive]==> PORT_ " + port);

		if (isBtnClose || isAuctionFinished) {
			return;
		}

		// 환경설정 팝업창이 열려있는 경우 닫아준다.
		if (mStageSetting != null && mStageSetting.isShowing()) {
			Platform.runLater(() -> {
				mStageSetting.close();
			});
		}

		// 메세지 팝업창이 열려있는 경우 닫아준다.
		if (mStageMessage != null && mStageMessage.isShowing()) {
			Platform.runLater(() -> {
				mStageMessage.close();
			});
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				isShowAlertPopup = true;
				CommonUtils.getInstance().dismissLoadingDialog();
				mAlertDialog = CommonUtils.getInstance().setAlertPopupStyle(mStage,
						CommonUtils.getInstance().ALERTPOPUP_ONE_BUTTON,
						mCurrentResources.getString("str.auction.close.connection"),
						mCurrentResources.getString("str.ok"), "");
				mAlertDialog.setOnHiding(event -> {
					isShowAlertPopup = false;
					closeStage(); // 닫기
				});
				mAlertDialog.show();
			}
		});
	}

	@Override
	public void exceptionCaught(int port) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckSession(ChannelHandlerContext ctx, AuctionCheckSession auctionCheckSession) {
		// TODO Auto-generated method stub
		ctx.channel()
				.writeAndFlush(new AuctionReponseSession(SharedPreference.getMemberNum(),
						GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER, GlobalDefineCode.USE_CHANNEL_PC)
								.getEncodedMessage()
						+ "\r\n");
	}

	private void onConnectInfo() {
		String id = SharedPreference.getMemberNum();
		mLogger.debug("onConnectInfo id : " + id);

		if (mClient != null) {
			mLogger.debug("onConnectInfo mClients Send Message");
			mClient.sendMessage(new ConnectionInfo(id, GlobalDefineCode.CONNECT_CHANNEL_CONTROLLER,
					GlobalDefineCode.USE_CHANNEL_PC, "N"));
		} else {
			mLogger.debug("onConnectInfo startConnectTimer");
			startConnectInfoTimer();
		}
	}

	private void startConnectInfoTimer() {
		if (mConnectInfoJob != null) {
			mConnectInfoJob.cancel(true);
		}

		mConnectInfoJob = mService.scheduleAtFixedRate(new ConnectInfoTimerJob(), 50, 1000, TimeUnit.MILLISECONDS);
	}

	private class ConnectInfoTimerJob implements Runnable {
		@Override
		public void run() {
			onConnectInfo();
		}
	}

	private void startCheckResourceCheckTimer(ResponseEntryInfo responseCarInfo) {
		if (mResourceDataCheckJob != null) {
			mResourceDataCheckJob.cancel(true);
		}

		mResourceDataCheckJob = mService.scheduleAtFixedRate(new ResourceCheckTimerJob(responseCarInfo), 50, 1000,
				TimeUnit.MILLISECONDS);
	}

	private class ResourceCheckTimerJob implements Runnable {
		ResponseEntryInfo mResponseCarInfo;

		public ResourceCheckTimerJob(ResponseEntryInfo responseCarInfo) {
			mResponseCarInfo = responseCarInfo;
		}

		@Override
		public void run() {

			if (isFirstEntryCarImageLoad) {

				// setCarInfo(mResponseCarInfo);

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						CommonUtils.getInstance().dismissLoadingDialog();
						entryDataLoad();
						mStage.show();
						MoveStageUtil.getInstance().setScreenMinSize(mStage);
					}
				});

				// showStage();

				if (mResourceDataCheckJob != null) {
					mResourceDataCheckJob.cancel(true);
				}
			}
		}
	}

	/**
	 * @Description 서비스 종료 시 호출
	 */
	class ShutdownHookThread extends Thread {

		AuctionControlController parent;

		public ShutdownHookThread(AuctionControlController parent) {
			this.parent = parent;
		}

		public void run() {
			parent.quit();
		}
	}

	/**
	 * @Description 서비스 종료 시 호출, 네티 Close
	 */
	private void quit() {
		try {
			System.out.println("[ControllerApplication Close]");
			// 네티 종료
			if (mClient != null) {
				mClient.stopClient(null);
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
	}

}
