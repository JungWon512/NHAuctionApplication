package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.NettyControllable;
import com.nh.common.interfaces.UdpBillBoardStatusListener;
import com.nh.common.interfaces.UdpPdpBoardStatusListener;
import com.nh.controller.controller.SettingController.AuctionToggle;
import com.nh.controller.model.SpBidderConnectInfo;
import com.nh.controller.model.SpBidding;
import com.nh.controller.model.SpEntryInfo;
import com.nh.controller.netty.AuctionDelegate;
import com.nh.controller.netty.BillboardDelegate;
import com.nh.controller.netty.PdpDelegate;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.ApiUtils;
import com.nh.controller.utils.AudioFilePlay;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.ListComparator;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.SharedPreference;
import com.nh.controller.utils.SoundUtil;
import com.nh.controller.utils.MoveStageUtil.EntryDialogType;
import com.nh.share.api.ActionResultListener;
import com.nh.share.api.request.body.RequestCowInfoBody;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
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
	
	@FXML // 하단 버튼
	private Button mBtnEsc, mBtnF1,mBtnF4, mBtnF5,mBtnStart,mBtnPause,mBtnFinish;

	
	private ObservableList<SpEntryInfo> mWaitEntryInfoDataList = FXCollections.observableArrayList(); // 대기중 출품
	private ObservableList<SpBidding> mBiddingUserInfoDataList = FXCollections.observableArrayList(); // 응찰 현황
	private ObservableList<SpBidderConnectInfo> mConnectionUserDataList = FXCollections.observableArrayList(); // 접속자 현황
	private Map<String, BidderConnectInfo> mConnectionUserMap = new HashMap<>(); // 접속 현황
	
	private Image mResDisplayOn = new Image("/com/nh/controller/resource/images/ic_con_on.png"); // 전광판 On 이미지 리소스
	private Image mResDisplayOff = new Image("/com/nh/controller/resource/images/ic_con_off.png"); // 전광판 Off 이미지 리소스
	private FadeTransition mAnimationFadeIn; // 토스트 애니메이션 START
	private FadeTransition mAnimationFadeOut; // 토스트 애니메이션 END
	private boolean isApplicationClosePopup = false; // 임의 종료시 server 연결 해제 팝업 노출 막는 플래그
	
	private String REFRESH_ENTRY_LIST_TYPE_NONE = "NONE"; // 출장우 정보 갱신 - 기본
	private String REFRESH_ENTRY_LIST_TYPE_SEND = "SEND"; // 출장우 정보 갱신 후 정보 보냄
	private String REFRESH_ENTRY_LIST_TYPE_START = "START"; // 출장우 정보 갱신 후 시작
	
	private EntryDialogType mCurPageType; // 전체or보류목록 타입
	private int mRecordCount = 0; // cow total data count

	/**
	 * setStage
	 * 
	 * @param stage
	 */
	public void setStage(Stage stage) {
		mStage = stage;

		Platform.runLater(() -> {
			// 경매 데이터 set
//			requestAuctionInfo();
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

	}

	/**
	 * 기본 뷰 설정
	 */
	private void initViewConfiguration() {

		initParsingSharedData();
		
		initTableConfiguration();
		
		mBtnEsc.setOnMouseClicked(event -> onCloseApplication());
		mBtnF1.setOnMouseClicked(event -> onSendEntryData());
		
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
	
	
	/**
	 * 경매 시작.
	 */
	public void onStartAuction() {
		
	}
	

	@Override
	public void onActiveChannel(Channel channel) {
		// 제어프로그램 접속
		mLogger.debug(mResMsg.getString("msg.auction.send.connection.info") + AuctionDelegate.getInstance().onSendConnectionInfo());
	}

	@Override
	public void onActiveChannel() {

	}

	@Override
	public void onAuctionStatus(AuctionStatus auctionStatus) {

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
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestAuctionResult(RequestAuctionResult requestAuctionResult) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionException(int port) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChannelInactive(int port) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exceptionCaught(int port) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckSession(ChannelHandlerContext ctx, AuctionCheckSession auctionCheckSession) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBidderConnectInfo(BidderConnectInfo bidderConnectInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStandConnectInfo(StandConnectInfo standConnectInfo) {
		// TODO Auto-generated method stub

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
	private void initConfiguration(Stage stage) {
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
	
}
