package com.nh.controller.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import com.nh.controller.interfaces.StringListener;
import com.nh.controller.model.AuctionRound;
import com.nh.controller.model.SpBidding;
import com.nh.controller.model.SpEntryInfo;
import com.nh.controller.netty.AuctionDelegate;
import com.nh.controller.service.AuctionRoundMapperService;
import com.nh.controller.service.EntryInfoMapperService;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.TestUtil;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.CurrentEntryInfo;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AuctionController extends BaseAuctionController implements Initializable {

	private ObservableList<SpEntryInfo> mFinishedEntryInfoDataList = FXCollections.observableArrayList(); // 끝난 출품
	private ObservableList<SpEntryInfo> mWaitEntryInfoDataList = FXCollections.observableArrayList(); // 대기중 출품
	private ObservableList<SpBidding> mBiddingUserInfoDataList = FXCollections.observableArrayList(); // 응찰 현황
	private ObservableList<SpEntryInfo> mConnectionUserDataList = FXCollections.observableArrayList(); // 접속자 현황

	@FXML // root pane
	public BorderPane mRootPane;

	@FXML // 경매 날짜 라벨
	public Label mHeaderAucInfoLabel;

	@FXML // 완료,대기,응찰현황,접속현황 테이블
	private TableView<SpEntryInfo> mFinishedTableView, mWaitTableView, mConnectionUserTableView;

	@FXML // 완료,대기,응찰현황,접속현황 테이블
	private TableView<SpBidding> mBiddingInfoTableView;

	@FXML // 완료된 출품
	private TableColumn<SpEntryInfo, String> mFinishedEntryNumColumn, mFinishedExhibitorColumn, mFinishedGenderColumn,
			mFinishedMotherColumn, mFinishedCavingNumColumn, mFinishedIndNumColumn, mFinishedWeightColumn,
			mFinishedLowPriceColumn, mFinishedStartPriceColumn, mFinishedSuccessColumn, mFinishedResultColumn,
			mFinishedNoteColumn;

	@FXML // 대기중인 출품
	private TableColumn<SpEntryInfo, String> mWaitEntryNumColumn, mWaitExhibitorColumn, mWaitGenderColumn,
			mWaitMotherColumn, mWaitCavingNumColumn, mWaitIndNumColumn, mWaitWeightColumn, mWaitLowPriceColumn,
			mWaitStartPriceColumn, mWaitSuccessColumn, mWaitResultColumn, mWaitNoteColumn;

	@FXML // 현재 경매
	private Label mCurEntryNumLabel, mCurExhibitorLabel, mCurGenterLabel, mCurMotherLabel, mCurCavingNumLabel,
			mCurIndNumLabel, mCurWeightLabel, mCurLowPriceLabel, mCurStartPriceLabel, mCurSuccessPriceLabel,
			mCurResultLabel, mCurNoteLabel;

	@FXML // 사용자 접속 현황
	private TableColumn<SpEntryInfo, String> mConnectionUserColumn_1, mConnectionUserColumn_2, mConnectionUserColumn_3,
			mConnectionUserColumn_4, mConnectionUserColumn_5;

	@FXML // 응찰자 정보
	private TableColumn<SpBidding, String> mBiddingPriceColumn, mBiddingUserColumn;

	@FXML // 하단 버튼
	private Button mBtnF1, mBtnEsc, mBtnF3, mBtnF4, mBtnF5, mBtnF8, mBtnEnter, mBtnSpace, mBtnConnection;

	@FXML // 경매 정보
	private Label mAuctionInfoDateLabel, mAuctionInfoRoundLabel, mAuctionInfoGubunLabel, mAuctionInfoNameLabel;

	@FXML // 경매 정보 - 상태
	private Label mAuctionStateReadyLabel, mAuctionStateProgressLabel, mAuctionStateSuccessLabel,
			mAuctionStateFailLabel, mAuctionStateLabel;

	@FXML // 남은 시간 Bar
	private Label cnt_5, cnt_4, cnt_3, cnt_2, cnt_1;

	@FXML // 메세지 보내기 버튼
	private ImageView mImgMessage;

	private List<Label> cntList = new ArrayList<Label>(); // 남은 시간 Bar list

	public final int REMAINING_TIME_COUNT = 5; // 카운트다운 기준 시간

	private int mRemainingTimeCount = REMAINING_TIME_COUNT; // 카운트다운

	private int qcn; // 회차정보 (test)

	private Map<String, EntryInfo> entryInfoTest; // 출품 정보

	/**
	 * setStage
	 * 
	 * @param stage
	 */
	public void setStage(Stage stage) {
		mStage = stage;
	}

	/**
	 * 구성 설정
	 */
	public void initConfiguration() {
		// 키 설정
		initKeyConfig();
		// 창 이동 설정
		CommonUtils.getInstance().canMoveStage(mStage, mRootPane);
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

		// Views
		initViewConfiguration();
		// MakeData
		requestEntryData();
		// 경매 정보
		setAuctionInfo();

//		initFinishedEntryDataList();
//		initWaitEntryDataList();
//		initBiddingInfoDataList();
//		initConnectionUserDataList();
	}

	/**
	 * 기본 뷰 설정
	 */
	private void initViewConfiguration() {

		initTableConfiguration();

		cntList.add(cnt_1);
		cntList.add(cnt_2);
		cntList.add(cnt_3);
		cntList.add(cnt_4);
		cntList.add(cnt_5);

		mBtnEnter.setOnMouseClicked(event -> onStartAuction(event));
		mBtnEsc.setOnMouseClicked(event -> onPassAuction(event));
		mBtnF1.setOnMouseClicked(event -> onSendEntryData(event));
		mImgMessage.setOnMouseClicked(event -> openSendMessage(event));

	}

	/**
	 * 테이블뷰 관련
	 */
	private void initTableConfiguration() {

		// 테이블 컬럼 - 완료
		mFinishedEntryNumColumn.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mFinishedExhibitorColumn.setCellValueFactory(cellData -> cellData.getValue().getExhibitor());
		mFinishedGenderColumn.setCellValueFactory(cellData -> cellData.getValue().getGender());
		mFinishedMotherColumn.setCellValueFactory(cellData -> cellData.getValue().getMother());
		mFinishedCavingNumColumn.setCellValueFactory(cellData -> cellData.getValue().getCavingNum());
		mFinishedIndNumColumn.setCellValueFactory(cellData -> cellData.getValue().getIndNum());
		mFinishedWeightColumn.setCellValueFactory(cellData -> cellData.getValue().getWeight());
		mFinishedLowPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getStartPrice());
		mFinishedStartPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getStartPrice());
		mFinishedSuccessColumn.setCellValueFactory(cellData -> cellData.getValue().getSuccessfulBidder());
		mFinishedResultColumn.setCellValueFactory(cellData -> cellData.getValue().getBiddingResult());
		mFinishedNoteColumn.setCellValueFactory(cellData -> cellData.getValue().getNote());

		// 테이블 컬럼 - 대기
		mWaitEntryNumColumn.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mWaitExhibitorColumn.setCellValueFactory(cellData -> cellData.getValue().getExhibitor());
		mWaitGenderColumn.setCellValueFactory(cellData -> cellData.getValue().getGender());
		mWaitMotherColumn.setCellValueFactory(cellData -> cellData.getValue().getMother());
		mWaitCavingNumColumn.setCellValueFactory(cellData -> cellData.getValue().getCavingNum());
		mWaitIndNumColumn.setCellValueFactory(cellData -> cellData.getValue().getIndNum());
		mWaitWeightColumn.setCellValueFactory(cellData -> cellData.getValue().getWeight());
		mWaitLowPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getStartPrice());
		mWaitStartPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getStartPrice());
		mWaitSuccessColumn.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mWaitResultColumn.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mWaitNoteColumn.setCellValueFactory(cellData -> cellData.getValue().getNote());

		// 테이블 컬럼 - 접속자
		mConnectionUserColumn_1.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mConnectionUserColumn_2.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mConnectionUserColumn_3.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mConnectionUserColumn_4.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mConnectionUserColumn_5.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());

		// 테이블 컬럼 - 응찰자
		mBiddingPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getPrice());
		mBiddingUserColumn.setCellValueFactory(cellData -> cellData.getValue().getUserNo());

		// holder default msg
		mFinishedTableView.setPlaceholder(new Label(mResMsg.getString("msg.entry.finish.default")));
		mWaitTableView.setPlaceholder(new Label(mResMsg.getString("msg.entry.wait.default")));
		mConnectionUserTableView.setPlaceholder(new Label(mResMsg.getString("msg.connected.user.default")));
		mBiddingInfoTableView.setPlaceholder(new Label(mResMsg.getString("msg.bidder.default")));

		initFinishedEntryDataList();
		initBiddingInfoDataList();
		initConnectionUserDataList();
	}

	/**
	 * 경매 정보
	 */
	private void setAuctionInfo() {

		String auctionDate = CommonUtils.getInstance().getCurrentTime("yyyy-MM-dd");

		mAuctionInfoDateLabel.setText(auctionDate);
		mAuctionInfoRoundLabel.setText("25");
		mAuctionInfoGubunLabel.setText("큰소경매");
		mAuctionInfoNameLabel.setText("89두");
		mHeaderAucInfoLabel.setText(auctionDate + "- " + String.valueOf(qcn) + "회차");
	}

	/**
	 * 경매 서버 접속
	 * 
	 * @param loginStage
	 * @param fxmlLoader
	 */
	public void onConnectServer(Stage loginStage, FXMLLoader fxmlLoader, String ip, int port, String id) {

		mStage = loginStage;
		mFxmlLoader = fxmlLoader;

		// connection server
		Thread thread = new Thread("server") {
			@Override
			public void run() {
				createClient(ip, port, id, "N");
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * 경매 대기 초기값
	 * 
	 * @param dataMap
	 */
	private void initWaitEntryDataList(LinkedHashMap<String, SpEntryInfo> dataMap) {

		if (dataMap.size() > 0) {

			mWaitEntryInfoDataList.clear();

			for (Entry<String, SpEntryInfo> spEntryInfo : dataMap.entrySet()) {
				mWaitEntryInfoDataList.add(spEntryInfo.getValue());
			}

			mWaitTableView.setItems(mWaitEntryInfoDataList);
		}
	}

	/**
	 * 접속자 현황 초기값.
	 */
	private void initConnectionUserDataList() {
		mConnectionUserTableView.setItems(mConnectionUserDataList);
	}

	/**
	 * 경매 완료 초기값
	 */
	private void initFinishedEntryDataList() {
		mFinishedEntryInfoDataList.add(new SpEntryInfo());
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
	}

	/**
	 * 출품 데이터 전송
	 * 
	 * @param event
	 */
	public void onSendEntryData(MouseEvent event) {
		onSendEntryData();
	}

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

			Thread thread = new Thread("onSendEntryData") {
				@Override
				public void run() {

//					for (Entry<String, SpEntryInfo> entryInfo : mEntryRepositoryMap.entrySet()) {
//						addLogItem(mResMsg.getString("msg.auction.send.entry.data")
//								+ AuctionDelegate.getInstance().onSendEntryData(entryInfo.getValue()));
//					}

					for (Entry<String, EntryInfo> entryInfo : entryInfoTest.entrySet()) {
						addLogItem(mResMsg.getString("msg.auction.send.entry.data")
								+ AuctionDelegate.getInstance().onSendEntryData(entryInfo.getValue()));
					}

					addLogItem(
							String.format(mResMsg.getString("msg.send.entry.data.result"), mEntryRepositoryMap.size()));

					mBtnF1.setDisable(true);

					initWaitEntryDataList(mEntryRepositoryMap);

					Platform.runLater(() -> {
						CommonUtils.getInstance().dismissLoadingDialog();
					});
				}
			};

			thread.setDaemon(true);
			thread.start();

		} else {
			addLogItem(mResMsg.getString("msg.need.connection"));
			CommonUtils.getInstance().dismissLoadingDialog();
		}
	}

	public void onStartAuction(MouseEvent event) {

		if (mBtnEnter.isDisable()) {
			return;
		}

		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_READY:
			onStartAuction();
			break;
		case GlobalDefineCode.AUCTION_STATUS_START:
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
			onStopAuction();
			break;
		}
	}

	/**
	 * 1.준비 2.시작
	 * 
	 * @param event
	 */
	public void onStartAuction() {

		if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
			addLogItem(mResMsg.getString("msg.auction.send.need.entry.data"));
			return;
		}

		if (!mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_READY)) {
			addLogItem(mResMsg.getString("msg.auction.not.ready"));
			return;
		}

		String msgReady = String.format(mResMsg.getString("msg.auction.send.ready"),
				mCurrentSpEntryInfo.getEntryNum().getValue());
		String msgStart = String.format(mResMsg.getString("msg.auction.send.start"),
				mCurrentSpEntryInfo.getEntryNum().getValue());

		// 준비
		// addLogItem(msgReady +
		// AuctionDelegate.getInstance().onNextEntryReady(mCurrentEntryInfo.getEntryNum()));
		// 시작
		addLogItem(
				msgStart + AuctionDelegate.getInstance().onStartAuction(mCurrentSpEntryInfo.getEntryNum().getValue()));
	}

	/**
	 * 종료
	 * 
	 * @param event
	 */
	public void onStopAuction() {

		if (mCurrentSpEntryInfo == null) {
			return;
		}

		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_START:
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
			mBtnEsc.setDisable(true);
			addLogItem(mResMsg.getString("msg.auction.send.complete")
					+ AuctionDelegate.getInstance().onPauseAuction(mCurrentSpEntryInfo.getEntryNum().getValue()));
			break;
		}

	}

	/**
	 * 강제유찰
	 * 
	 * @param event
	 */
	public void onPassAuction(MouseEvent event) {
		onPassAuction();
	}

	/**
	 * 강제유찰
	 * 
	 * @param event
	 */
	public void onPassAuction() {

		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_START:
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
			mIsPass = true;
			mBtnEsc.setDisable(true);
			mBtnEnter.setDisable(true);
			addLogItem(mResMsg.getString("msg.auction.send.pass")
					+ AuctionDelegate.getInstance().onPassAuction(mCurrentSpEntryInfo.getEntryNum().getValue()));
			break;
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

		MoveStageUtil.getInstance().loadMessageFXMLLoader(stage, new StringListener() {
			@Override
			public void callBack(String str) {
				AuctionDelegate.getInstance().onToastMessageRequest(str);
				addLogItem(String.format(mResMsg.getString("msg.auction.send.message"), str));
//				showToastMessage(String.format(mResMsg.getString("msg.auction.send.message"), str));
			}
		});
	}

	/**
	 * 경매 출품 데이터
	 */
	private void requestEntryData() {
		// TEST!! 일단 경매정보 하나만 가져옴!!
		AuctionRoundMapperService service = new AuctionRoundMapperService();
		List<AuctionRound> list = service.getAllAuctionRoundData();
		for (AuctionRound t : list) {
//			System.out.println(t);
			qcn = t.getQcn();
		}
		// TEST!! 경매 데이터 불러오기
		EntryInfoMapperService entryService = new EntryInfoMapperService();
		entryInfoTest = entryService.getAllEntryData();
		for (Entry<String, EntryInfo> info : entryInfoTest.entrySet()) {
			System.out.println(info.getValue().getEntryNum());
		}

		mEntryRepositoryMap.clear();
		mEntryRepositoryMap.putAll(TestUtil.getInstance().loadEntryDataMap());
		initWaitEntryDataList(mEntryRepositoryMap);
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

			switch (responseConnectionInfo.getResult()) {
			case GlobalDefineCode.CONNECT_SUCCESS:
				addLogItem(mResMsg.getString("msg.connection.success") + responseConnectionInfo.getEncodedMessage());
				MoveStageUtil.getInstance().moveAuctionStage(mStage, mFxmlLoader);
				break;
			case GlobalDefineCode.CONNECT_FAIL:
				addLogItem(mResMsg.getString("msg.connection.fail"));
				showAlertPopupOneButton(mResMsg.getString("msg.connection.fail"));
				AuctionDelegate.getInstance().onDisconnect(null);
				break;
			case GlobalDefineCode.CONNECT_DUPLICATE:
				addLogItem(mResMsg.getString("msg.connection.duplicate"));
				showAlertPopupOneButton(mResMsg.getString("msg.connection.duplicate"));
				AuctionDelegate.getInstance().onDisconnect(null);
				break;
			}

		});
	}

	@Override
	public void onCurrentEntryInfo(CurrentEntryInfo currentEntryInfo) {
		super.onCurrentEntryInfo(currentEntryInfo);
		setCurrentEntryInfo(currentEntryInfo);
	}

	@Override
	public void onAuctionStatus(AuctionStatus auctionStatus) {
		super.onAuctionStatus(auctionStatus);
		setAuctionVariableState(auctionStatus.getState());
	}

	@Override
	public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
		super.onAuctionCountDown(auctionCountDown);

		if (auctionCountDown.getStatus().equals(GlobalDefineCode.AUCTION_COUNT_DOWN)) {
			if (Integer.parseInt(auctionCountDown.getCountDownTime()) <= 4) {
				mRemainingTimeCount--;
				cntList.get(mRemainingTimeCount).setDisable(true);
			}
		}

		mBtnEnter.setDisable(true);
	}

	/**
	 * 경매 준비 뷰 초기화
	 */
	private void setAuctionVariableState(String code) {

		Platform.runLater(() -> {

			switch (code) {

			case GlobalDefineCode.AUCTION_STATUS_READY:

				// 초기화 작업
				for (int i = 0; cntList.size() > i; i++) {
					cntList.get(i).setDisable(false);
				}

				// 카운트 시간 초기화
				mRemainingTimeCount = REMAINING_TIME_COUNT;
				// 현재 응찰 내역 초기화
				mCurrentBidderMap.clear();
				// 이전 응찰 내역 초기화
				mBeForeBidderDataList.clear();
				// ENTER 경매완료 -> 경매시작 으로 변경
				mBtnEnter.setText(mResMsg.getString("str.btn.start"));
				CommonUtils.getInstance().removeStyleClass(mBtnEnter, "btn-auction-stop");

				// ready -> 출품 정보 보내기 버튼 비활성화
				mBtnF1.setDisable(true);
				mBtnEnter.setDisable(false);
				mBtnEsc.setDisable(true);

				initBiddingInfoDataList();

				mIsPass = false;
				// 경매 상태
				updateAuctionStateInfo(code, false, null);
				break;
			case GlobalDefineCode.AUCTION_STATUS_START:
			case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
				// ENTER 경매시작 -> 경매완료 변경
				mBtnEnter.setText(mResMsg.getString("str.btn.stop"));
				CommonUtils.getInstance().addStyleClass(mBtnEnter, "btn-auction-stop");
				mBtnEnter.setDisable(false);
				mBtnEsc.setDisable(false);
				// 경매 상태
				updateAuctionStateInfo(code, false, null);
				break;
			case GlobalDefineCode.AUCTION_STATUS_FINISH:

				if (mCurrentSpEntryInfo != null) {
					addFinishedTableViewItem(mCurrentSpEntryInfo);
				}

				mCurEntryNumLabel.setText("");
				mCurExhibitorLabel.setText("");
				mCurGenterLabel.setText("");
				mCurMotherLabel.setText("");
				mCurCavingNumLabel.setText("");
				mCurIndNumLabel.setText("");
				mCurWeightLabel.setText("");
				mCurLowPriceLabel.setText("");
				mCurStartPriceLabel.setText("");
				mCurSuccessPriceLabel.setText("");
				mCurResultLabel.setText("");
				mCurNoteLabel.setText("");

				initBiddingInfoDataList();

				showAlertPopupOneButton(mResMsg.getString("msg.auction.finish"));

				break;
			}

		});
	}

	/**
	 * 경매정보 - 경매 상태 표시
	 * 
	 * @param code      현재 경매 상태
	 * @param isSuccess true : 낙찰 , false : 유찰
	 */
	@Override
	protected void updateAuctionStateInfo(String code, boolean isSuccess, SpBidding bidder) {

		Platform.runLater(() -> {

			switch (code) {

			case GlobalDefineCode.AUCTION_STATUS_READY:
				mAuctionStateReadyLabel.setDisable(false);
				mAuctionStateProgressLabel.setDisable(true);
				mAuctionStateSuccessLabel.setDisable(true);
				mAuctionStateFailLabel.setDisable(true);
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.auction.ready"));
				break;
			case GlobalDefineCode.AUCTION_STATUS_START:
			case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
				mAuctionStateReadyLabel.setDisable(true);
				mAuctionStateProgressLabel.setDisable(false);
				mAuctionStateSuccessLabel.setDisable(true);
				mAuctionStateFailLabel.setDisable(true);
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.auction.progress"));
				break;
			case GlobalDefineCode.AUCTION_STATUS_COMPLETED:

				mAuctionStateReadyLabel.setDisable(true);
				mAuctionStateProgressLabel.setDisable(true);

				if (isSuccess) {
					mAuctionStateSuccessLabel.setDisable(false);
					mAuctionStateFailLabel.setDisable(true);
					mCurrentSpEntryInfo.setSuccessfulBidder(new SimpleStringProperty(bidder.getUserNo().getValue()));
					mCurrentSpEntryInfo
							.setBiddingResult(new SimpleStringProperty(mResMsg.getString("str.auction.state.success")));
				} else {
					mAuctionStateSuccessLabel.setDisable(true);
					mAuctionStateFailLabel.setDisable(false);
					mCurrentSpEntryInfo
							.setBiddingResult(new SimpleStringProperty(mResMsg.getString("str.auction.state.fail")));
				}

				break;
			case GlobalDefineCode.AUCTION_STATUS_FINISH:
				break;
			}

		});
	}

	@Override
	protected void updateBidderList(List<SpBidding> spBiddingDataList) {
		super.updateBidderList(spBiddingDataList);

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

	}

	/**
	 * 현재 진행할 데이터 Set
	 * 
	 * @param currentEntryInfo
	 */
	private void setCurrentEntryInfo(CurrentEntryInfo currentEntryInfo) {

		Platform.runLater(() -> {

			if (mCurrentSpEntryInfo != null) {
				addFinishedTableViewItem(mCurrentSpEntryInfo);
			}

			mCurrentSpEntryInfo = getWaitTableViewItem(currentEntryInfo.getEntryNum());

			removeWaitTableViewItem(mCurrentSpEntryInfo);

			mCurEntryNumLabel.setText(mCurrentSpEntryInfo.getEntryNum().getValue());
			mCurExhibitorLabel.setText(mCurrentSpEntryInfo.getExhibitor().getValue());
			mCurGenterLabel.setText(mCurrentSpEntryInfo.getGender().getValue());
			mCurMotherLabel.setText(mCurrentSpEntryInfo.getMother().getValue());
			mCurCavingNumLabel.setText(mCurrentSpEntryInfo.getCavingNum().getValue());
			mCurIndNumLabel.setText(mCurrentSpEntryInfo.getIndNum().getValue());
			mCurWeightLabel.setText(mCurrentSpEntryInfo.getWeight().getValue());
			mCurLowPriceLabel.setText("-");
			mCurStartPriceLabel.setText(String.format(mResMsg.getString("str.price"), Integer.parseInt(mCurrentSpEntryInfo.getStartPrice().getValue())));
			mCurSuccessPriceLabel.setText("");
			mCurResultLabel.setText("");
			mCurNoteLabel.setText(mCurrentSpEntryInfo.getNote().getValue());

		});
	}

	/**
	 * 대기중인 아이템 제거
	 * 
	 * @param entryNum
	 */
	private SpEntryInfo getWaitTableViewItem(String entryNum) {

		SpEntryInfo resultSpEntryInfo = null;

		for (SpEntryInfo spEntryInfo : mWaitTableView.getItems()) {
			if (spEntryInfo.getEntryNum().getValue().equals(entryNum)) {
				resultSpEntryInfo = spEntryInfo;
				break;
			}
		}
		return resultSpEntryInfo;

	}

	/**
	 * 경매 완료 row set
	 * 
	 * @param spEntryInfo
	 */
	private void addFinishedTableViewItem(SpEntryInfo spEntryInfo) {

		if (mFinishedTableView.getItems().size() > 0) {
			// 초기 빈 값 제거
			if (mFinishedTableView.getItems().get(0).getEntryNum() == null) {
				mFinishedTableView.getItems().remove(0);
			}
		}

		mFinishedEntryInfoDataList.add(spEntryInfo);
		mFinishedTableView.refresh();
		mFinishedTableView.scrollTo(mFinishedTableView.getItems().size() - 1);
	}

	/**
	 * 대기중인 아이템 제거
	 * 
	 * @param entryNum
	 */
	private void removeWaitTableViewItem(SpEntryInfo spEntryInfo) {
		Platform.runLater(() -> {
			mWaitTableView.getItems().remove(spEntryInfo);
		});
	}

	/**
	 * 대기중인 아이템 제거
	 * 
	 * @param entryNum
	 */
	private void removeWaitTableViewItem(String entryNum) {

		Platform.runLater(() -> {
			for (SpEntryInfo spEntryInfo : mWaitTableView.getItems()) {
				if (spEntryInfo.getEntryNum().getValue().equals(entryNum)) {
					mWaitTableView.getItems().remove(spEntryInfo);
					break;
				}
			}
		});
	}

	/**
	 * 키 설정
	 */
	private void initKeyConfig() {

		Platform.runLater(() -> {
			mStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

				public void handle(KeyEvent ke) {

					if (ke.getCode() == KeyCode.F1) {
						System.out.println("Key Pressed: " + ke.getCode());
						onSendEntryData();
						ke.consume(); // 다음 노드로 이벤트를 전달하지 않는다.
					}

					if (ke.getCode() == KeyCode.ESCAPE) {
						onPassAuction();
						System.out.println("Key Pressed: " + ke.getCode());
						ke.consume(); // 다음 노드로 이벤트를 전달하지 않는다.
					}

					if (ke.getCode() == KeyCode.SPACE) {
						System.out.println("눌림 KeyCode.SPACE");
						ke.consume(); // <-- stops passing the event to next node
					}
					if (ke.getCode() == KeyCode.ENTER) {
						System.out.println("눌림 KeyCode.Enter");

						if (mBtnEnter.isDisable()) {
							return;
						}

						switch (mAuctionStatus.getState()) {
						case GlobalDefineCode.AUCTION_STATUS_READY:
							onStartAuction();
							break;
						case GlobalDefineCode.AUCTION_STATUS_START:
						case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
							onStopAuction();
							break;
						}

						ke.consume(); // <-- stops passing the event to next node
					}
				}
			});
		});
	}

}
