package com.nh.controller.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.nh.controller.interfaces.StringListener;
import com.nh.controller.model.AuctionRound;
import com.nh.controller.model.SpBidding;
import com.nh.controller.model.SpEntryInfo;
import com.nh.controller.netty.AuctionDelegate;
import com.nh.controller.service.AuctionRoundMapperService;
import com.nh.controller.service.EntryInfoMapperService;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.CurrentEntryInfo;

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
import javafx.util.Duration;

public class AuctionController extends BaseAuctionController implements Initializable {

	@FXML // root pane
	public BorderPane mRootPane;

	@FXML // 경매 날짜 라벨
	public Label mHeaderAucInfoLabel;

	@FXML // 완료,대기,응찰현황,접속현황 테이블
	private TableView<SpEntryInfo> mFinishedTableView, mConnectionUserTableView, mWaitTableView;

	@FXML // 완료,대기,응찰현황,접속현황 테이블
	private TableView<SpBidding> mBiddingInfoTableView;

	@FXML // 완료된 출품
	private TableColumn<SpEntryInfo, String> mFinishedEntryNumColumn, mFinishedExhibitorColumn, mFinishedGenderColumn, mFinishedMotherColumn, mFinishedMatimeColumn, mFinishedPasgQcnColumn, mFinishedWeightColumn, mFinishedLowPriceColumn, mFinishedSuccessPriceColumn, mFinishedSuccessfulBidderColumn,
			mFinishedResultColumn, mFinishedNoteColumn;

	@FXML // 대기중인 출품
	private TableColumn<SpEntryInfo, String> mWaitEntryNumColumn, mWaitExhibitorColumn, mWaitGenderColumn, mWaitMotherColumn, mWaitMatimeColumn, mWaitPasgQcnColumn, mWaitWeightColumn, mWaitLowPriceColumn, mWaitSuccessPriceColumn, mWaitSuccessfulBidderColumn, mWaitResultColumn, mWaitNoteColumn;

	@FXML // 현재 경매
	private Label mCurEntryNumLabel, mCurExhibitorLabel, mCurGenterLabel, mCurMotherLabel, mCurMatimeLabel, mCurPasgQcnLabel, mCurWeightLabel, mCurLowPriceLabel, mCurSuccessPriceLabel, mCurSuccessfulBidderLabel, mCurResultLabel, mCurNoteLabel;

	@FXML // 사용자 접속 현황
	private TableColumn<SpEntryInfo, String> mConnectionUserColumn_1, mConnectionUserColumn_2, mConnectionUserColumn_3, mConnectionUserColumn_4, mConnectionUserColumn_5;

	@FXML // 응찰자 정보
	private TableColumn<SpBidding, String> mBiddingPriceColumn, mBiddingUserColumn;

	@FXML // 하단 버튼
	private Button mBtnEsc, mBtnF1, mBtnF3, mBtnF4, mBtnF5, mBtnF6, mBtnF7, mBtnF8, mBtnEnter, mBtnUpPrice, mBtnDownPrice;

	@FXML // 경매 정보
	private Label mAuctionInfoDateLabel, mAuctionInfoRoundLabel, mAuctionInfoGubunLabel, mAuctionInfoNameLabel;

	@FXML // 경매 정보 - 상태
	private Label mAuctionStateReadyLabel, mAuctionStateProgressLabel, mAuctionStateSuccessLabel, mAuctionStateFailLabel, mAuctionStateLabel;

	@FXML // 남은 시간 Bar
	private Label cnt_5, cnt_4, cnt_3, cnt_2, cnt_1;

	@FXML // 메세지 보내기 버튼
	private ImageView mImgMessage;

	private List<Label> cntList = new ArrayList<Label>(); // 남은 시간 Bar list

	public final int REMAINING_TIME_COUNT = 5; // 카운트다운 기준 시간

	private int mRemainingTimeCount = REMAINING_TIME_COUNT; // 카운트다운

	private List<EntryInfo> entryInfoDataList; // 출품 정보 DB 데이터

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

		mBtnEsc.setOnMouseClicked(event -> onCloseApplication());
		mBtnF1.setOnMouseClicked(event -> onSendEntryData());
		mBtnF3.setOnMouseClicked(event -> onPending());
		mBtnF4.setOnMouseClicked(event -> openEntryListPopUp());
		mBtnF5.setOnMouseClicked(event -> openEntryPendingListPopUp());
		mBtnEnter.setOnMouseClicked(event -> onStartAndStopAuction());
		mBtnF7.setOnMouseClicked(event -> onPassAuction(event));
		mBtnF8.setOnMouseClicked(event -> openSettingDialog());

		mImgMessage.setOnMouseClicked(event -> openSendMessage(event));
		mWaitTableView.setOnMouseClicked(event -> onClickWaitTableView(event));

		mBtnUpPrice.setOnMouseClicked(event -> onUpPrice(event));
		mBtnDownPrice.setOnMouseClicked(event -> onDownPrice(event));

	}

	/**
	 * 테이블뷰 관련
	 */
	private void initTableConfiguration() {

		// 테이블 컬럼 - 완료
		mFinishedEntryNumColumn.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mFinishedExhibitorColumn.setCellValueFactory(cellData -> cellData.getValue().getExhibitor());
		mFinishedGenderColumn.setCellValueFactory(cellData -> cellData.getValue().getGender());
		mFinishedMotherColumn.setCellValueFactory(cellData -> cellData.getValue().getMotherObjNum());
		mFinishedMatimeColumn.setCellValueFactory(cellData -> cellData.getValue().getMatime());
		mFinishedPasgQcnColumn.setCellValueFactory(cellData -> cellData.getValue().getPasgQcn());
		mFinishedWeightColumn.setCellValueFactory(cellData -> cellData.getValue().getWeight());
		mFinishedLowPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getLowPrice());
		mFinishedSuccessPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getAuctionBidPrice());
		mFinishedSuccessfulBidderColumn.setCellValueFactory(cellData -> cellData.getValue().getAuctionSucBidder());
		mFinishedResultColumn.setCellValueFactory(cellData -> cellData.getValue().getBiddingResult());
		mFinishedNoteColumn.setCellValueFactory(cellData -> cellData.getValue().getNote());

		// 테이블 컬럼 - 대기
		mWaitEntryNumColumn.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mWaitExhibitorColumn.setCellValueFactory(cellData -> cellData.getValue().getExhibitor());
		mWaitGenderColumn.setCellValueFactory(cellData -> cellData.getValue().getGender());
		mWaitMotherColumn.setCellValueFactory(cellData -> cellData.getValue().getMotherObjNum());
		mWaitMatimeColumn.setCellValueFactory(cellData -> cellData.getValue().getMatime());
		mWaitPasgQcnColumn.setCellValueFactory(cellData -> cellData.getValue().getPasgQcn());
		mWaitWeightColumn.setCellValueFactory(cellData -> cellData.getValue().getWeight());
		mWaitLowPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getLowPrice());
		mWaitSuccessPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getAuctionBidPrice());
		mWaitSuccessfulBidderColumn.setCellValueFactory(cellData -> cellData.getValue().getAuctionSucBidder());
		mWaitResultColumn.setCellValueFactory(cellData -> cellData.getValue().getBiddingResult());
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
        mAuctionInfoRoundLabel.setText(String.valueOf(this.auctionRound.getQcn()));
        mAuctionInfoGubunLabel.setText("큰소경매");
        mAuctionInfoNameLabel.setText("89두");
        mHeaderAucInfoLabel.setText(auctionDate + "- " + this.auctionRound.getQcn() + "회차");
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

						selecIndextWaitTable(0);

						mWaitTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldItem, newItem) -> {

							if (newItem != null) {

								SpEntryInfo oldSpEntryInfo = oldItem;
								SpEntryInfo newSpEntryInfo = newItem;

								if (newSpEntryInfo.getEntryNum() == null) {
									if (oldSpEntryInfo.getEntryNum() != null) {
										mWaitTableView.getSelectionModel().clearSelection();
										mWaitTableView.getSelectionModel().select(oldSpEntryInfo);
									}
								}

							}

						});
//						mWaitTableView.getSelectionModel().selectedIndexProperty().addListener((observable, oldIndex, newIndex) -> { 선택 콜백 인덱스	 필요시 주석 해제
//						});
					}
				});
				pauseTransition.play();
			}
		}

	}

	/**
	 * 대기중인 출품 목록 갱신
	 */
	private void refreshWaitTableView() {
		mWaitTableView.refresh();
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
	}

	/**
	 * 출품 데이터 전송
	 *
	 * @param event
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

			Thread thread = new Thread("onSendEntryData") {
				@Override
				public void run() {

					for (SpEntryInfo entryInfo : mWaitEntryInfoDataList) {
						addLogItem(mResMsg.getString("msg.auction.send.entry.data") + AuctionDelegate.getInstance().onSendEntryData(entryInfo));
					}

					addLogItem(String.format(mResMsg.getString("msg.send.entry.data.result"), mWaitEntryInfoDataList.size()));

					mBtnF1.setDisable(true);

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

	/**
	 * 보류 처리
	 */
	public void onPending() {
		Platform.runLater(() -> {
			SpEntryInfo spEntryInfo = mWaitTableView.getSelectionModel().getSelectedItem();

			if (!spEntryInfo.getAuctionResult().equals(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING)) {
				// 보류처리
				spEntryInfo.setAuctionResult(new SimpleStringProperty(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING));

				setCurrentEntryInfo();
				mWaitTableView.refresh();
			}
		});
	}

	/**
	 * 전체 보기
	 */
	public void openEntryListPopUp() {

		ObservableList<SpEntryInfo> dataList = getWaitEntryInfoDataList();

		MoveStageUtil.getInstance().openEntryListPopUp(mStage, dataList);
	}

	/**
	 * 보류 목록 보기
	 */
	public void openEntryPendingListPopUp() {

		ObservableList<SpEntryInfo> dataList = getWaitEntryInfoPendingDataList();

		MoveStageUtil.getInstance().openEntryPendingListPopUp(mStage, dataList);
	}

	/**
	 * 환경 설정
	 */
	public void openSettingDialog() {

		MoveStageUtil.getInstance().openSettingDialog(mStage);
	}

	/**
	 * 프로그램 종료
	 */
	public void onCloseApplication() {
		addLogItem("종료");
		Platform.exit();
		System.exit(0);
	}
	
	public void onStartAndStopAuction() {
		
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


		String msgStart = String.format(mResMsg.getString("msg.auction.send.start"), mCurrentSpEntryInfo.getEntryNum().getValue());

		// 시작
		addLogItem(msgStart + AuctionDelegate.getInstance().onStartAuction(mCurrentSpEntryInfo.getEntryNum().getValue()));
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
			mBtnF7.setDisable(true);
			addLogItem(mResMsg.getString("msg.auction.send.complete") + AuctionDelegate.getInstance().onPauseAuction(mCurrentSpEntryInfo.getEntryNum().getValue()));
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
	 */
	public void onPassAuction() {

		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_START:
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
			mIsPass = true;
			mBtnF7.setDisable(true);
			mBtnEnter.setDisable(true);
			addLogItem(mResMsg.getString("msg.auction.send.pass") + AuctionDelegate.getInstance().onPassAuction(mCurrentSpEntryInfo.getEntryNum().getValue()));
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
	 * 대기중인 테이블 뷰 클릭
	 *
	 * @param event
	 */
	public void onClickWaitTableView(MouseEvent event) {
		setCurrentEntryInfo();
	}

	/**
	 * 경매 출품 데이터
	 */
	private void requestEntryData() {
		// [경매회차조회] TEST!! 일단 경매정보 하나만 가져옴!!
		AuctionRoundMapperService service = new AuctionRoundMapperService();
//        List<AuctionRound> list = service.getAllAuctionRoundData(CommonUtils.getInstance().getCurrentTime("yyyyMMdd")); // 실제사용
		List<AuctionRound> list = service.getAllAuctionRoundData("20210702"); // 테스트
		this.auctionRound = list.get(0); // 테스트
//        for (AuctionRound t : list) {
//            qcn = t.getQcn();
//        }

		// 경매 출품 데이터 가져오기
		EntryInfoMapperService entryService = new EntryInfoMapperService();
//        entryInfoDataList = entryService.getAllEntryData(CommonUtils.getInstance().getCurrentTime("yyyyMMdd"),
//                this.auctionRound.getNaBzplc(),
//                String.valueOf(this.auctionRound.getAucObjDsc())); // 실제사용
		entryInfoDataList = entryService.getAllEntryData("20210702", "8808990656656", "3"); // 테스트

		// DB EntryInfo -> Sp EntryInfo
		mWaitEntryInfoDataList.clear();
		mWaitEntryInfoDataList = getParsingEntryDataList(entryInfoDataList);

//		for (Entry<String, SpEntryInfo> spEntryInfo : mEntryRepositoryMap.entrySet()) {
//			System.out.println(spEntryInfo.getKey());
//		}

		initWaitEntryDataList(mWaitEntryInfoDataList);
	}

	/**
	 * 예정가 높이기
	 * 
	 * @param event
	 */
	public void onUpPrice(MouseEvent event) {
		System.out.println("예정가 높이기");
		int upPrice = SettingApplication.getInstance().getInfo().getCowUpperLimitPrice();
		setLowPrice(upPrice);
	}

	/**
	 * 예정가 낮추기
	 * 
	 * @param event
	 */
	public void onDownPrice(MouseEvent event) {
		System.out.println("예정가 낮추기");
		int lowPrice = SettingApplication.getInstance().getInfo().getCowLowerLimitPrice() * -1;
		setLowPrice(lowPrice);
	}

	/**
	 * 예정가 Set
	 * 
	 * @param price
	 */
	private void setLowPrice(int price) {
		Platform.runLater(() -> {
			SpEntryInfo spEntryInfo = mWaitTableView.getSelectionModel().getSelectedItem();
			spEntryInfo.getLowPrice().setValue(Integer.toString(mCurrentSpEntryInfo.getLowPriceInt() + price));
			setCurrentEntryInfo();
//			mCurrentSpEntryInfo.getLowPrice().setValue(Integer.toString(mCurrentSpEntryInfo.getLowPriceInt() + price));
		});
	}

	// 2000 : 인증 성공
	// 2001 : 인증 실패
	// 2002 : 중복 접속
	// 2003 : 기타 장애
	@Override
	public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo) {
		super.onResponseConnectionInfo(responseConnectionInfo);

		mLogger.debug(responseConnectionInfo.getEncodedMessage());

		Platform.runLater(() -> {

			CommonUtils.getInstance().dismissLoadingDialog();

			switch (responseConnectionInfo.getResult()) {
			case GlobalDefineCode.CONNECT_SUCCESS:
				addLogItem(mResMsg.getString("msg.connection.success") + responseConnectionInfo.getEncodedMessage());
				// Setting 정보 전송
				// TEST
//				EditSetting setting = new EditSetting(new String[]{this.auctionRound.getNaBzplc(), "Y", "Y", "Y", "Y", "N", "N", "Y", "Y", "N", "Y", "Y", "N", "N", "3"});
//				addLogItem(mResMsg.getString("msg.auction.send.setting.info") + AuctionDelegate.getInstance().onSendSettingInfo(setting));
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
//		setCurrentEntryInfo(currentEntryInfo);
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
				mBtnF7.setDisable(true);

				initBiddingInfoDataList();

                    mIsPass = false;
                    // 경매 상태
                    updateAuctionStateInfo(code, false, null);
                    break;
                case GlobalDefineCode.AUCTION_STATUS_START:
                    break;
                case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
                    // ENTER 경매시작 -> 경매완료 변경
                    mBtnEnter.setText(mResMsg.getString("str.btn.stop"));
                    CommonUtils.getInstance().addStyleClass(mBtnEnter, "btn-auction-stop");
                    mBtnEnter.setDisable(false);
                    mBtnF7.setDisable(false);
                    // 경매 상태
                    updateAuctionStateInfo(code, false, null);
                    break;
                case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
                    break;
                case GlobalDefineCode.AUCTION_STATUS_FINISH:

				if (mCurrentSpEntryInfo != null) {
					addFinishedTableViewItem(mCurrentSpEntryInfo);
				}

				mCurEntryNumLabel.setText("");
				mCurExhibitorLabel.setText("");
				mCurGenterLabel.setText("");
				mCurMotherLabel.setText("");
				mCurMatimeLabel.setText("");
				mCurPasgQcnLabel.setText("");
				mCurWeightLabel.setText("");
				mCurLowPriceLabel.setText("");
				mCurSuccessPriceLabel.setText("");
				mCurSuccessfulBidderLabel.setText("");
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
                    // REFACTOR: 경매완료 후, 경매시작 - server가 아닌 controller에서 진행하도록 변경 됨. (21.07.27)

				mAuctionStateReadyLabel.setDisable(true);
				mAuctionStateProgressLabel.setDisable(true);

				if (isSuccess) {
					mAuctionStateSuccessLabel.setDisable(false);
					mAuctionStateFailLabel.setDisable(true);
					mCurrentSpEntryInfo.setAuctionSucBidder(new SimpleStringProperty(bidder.getUserNo().getValue()));
					mCurrentSpEntryInfo.setAuctionBidPrice(new SimpleStringProperty(bidder.getPrice().getValue()));
					mCurrentSpEntryInfo.setAuctionResult(new SimpleStringProperty(GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS));
					mCurrentSpEntryInfo.setAuctionBidDateTime(new SimpleStringProperty(bidder.getBiddingTime().getValue()));
				} else {
					mAuctionStateSuccessLabel.setDisable(true);
					mAuctionStateFailLabel.setDisable(false);
					mCurrentSpEntryInfo.setAuctionResult(new SimpleStringProperty(GlobalDefineCode.AUCTION_RESULT_CODE_FAIL));
				}

                    addFinishedTableViewItem(mCurrentSpEntryInfo);
                    // 낙유찰 화면 딜레이 2초 후 경매 대기 전환
                    PauseTransition pauseTransition = new PauseTransition(Duration.millis(2000));
                    pauseTransition.setOnFinished(event -> {
                        selecIndextWaitTable(1);
                        // 경매 대기
                        setAuctionStatus(GlobalDefineCode.AUCTION_STATUS_READY);
                        setAuctionVariableState(GlobalDefineCode.AUCTION_STATUS_READY);
                    });
                    pauseTransition.play();
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
	private void setCurrentEntryInfo() {

		SpEntryInfo currentEntryInfo = mWaitTableView.getSelectionModel().getSelectedItem();

		if (currentEntryInfo == null || currentEntryInfo.getEntryNum() == null) {
			return;
		}

//		if (mCurrentSpEntryInfo != null && mCurrentSpEntryInfo.getEntryNum().getValue().equals(currentEntryInfo.getEntryNum().getValue())) {
//			return;
//		}

		Platform.runLater(() -> {

			mCurrentSpEntryInfo = currentEntryInfo;

			mCurEntryNumLabel.setText(mCurrentSpEntryInfo.getEntryNum().getValue());
			mCurExhibitorLabel.setText(mCurrentSpEntryInfo.getExhibitor().getValue());
			mCurGenterLabel.setText(mCurrentSpEntryInfo.getGender().getValue());
			mCurMotherLabel.setText(mCurrentSpEntryInfo.getMotherObjNum().getValue());
			mCurMatimeLabel.setText(mCurrentSpEntryInfo.getMatime().getValue());
			mCurPasgQcnLabel.setText(mCurrentSpEntryInfo.getPasgQcn().getValue());
			mCurWeightLabel.setText(mCurrentSpEntryInfo.getWeight().getValue());
			mCurLowPriceLabel.setText(String.format(mResMsg.getString("str.price"), Integer.parseInt(mCurrentSpEntryInfo.getLowPrice().getValue())));
			mCurSuccessPriceLabel.setText("");
			mCurSuccessfulBidderLabel.setText("");
			mCurResultLabel.setText(mCurrentSpEntryInfo.getBiddingResult().getValue());
			mCurNoteLabel.setText(mCurrentSpEntryInfo.getNote().getValue());

			System.out.println("현재 준비 소 : " + mCurrentSpEntryInfo.getEntryNum().getValue());
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

					// 종료
					if (ke.getCode() == KeyCode.ESCAPE) {
						onCloseApplication();
						ke.consume(); // 다음 노드로 이벤트를 전달하지 않는다.
					}

					// 출품정보 전송
					if (ke.getCode() == KeyCode.F1) {
						onSendEntryData();
						ke.consume();
					}

					// 보류
					if (ke.getCode() == KeyCode.F3) {
						onPending();
						ke.consume();
					}

					// 전체보기
					if (ke.getCode() == KeyCode.F4) {
						openEntryListPopUp();
						ke.consume();
					}
					// 보류보기
					if (ke.getCode() == KeyCode.F5) {
						openEntryPendingListPopUp();
						ke.consume();
					}

					// 강제유찰?
					if (ke.getCode() == KeyCode.F7) {
						onPassAuction();
						ke.consume();
					}

					// 환경설정
					if (ke.getCode() == KeyCode.F8) {
						// 환경설정
						openSettingDialog();
						ke.consume();
					}

					// 경매 시작
					if (ke.getCode() == KeyCode.ENTER) {
						System.out.println("[KeyCode.ENTER]=> " + mAuctionStatus.getState());

						if (mBtnEnter.isDisable()) {
							return;
						}

						onStartAndStopAuction();

						ke.consume();
					}

					// 대기중인 목록 위로 이동
					if (ke.getCode() == KeyCode.UP) {

						if (mWaitTableView.getSelectionModel().getSelectedIndex() > mRecordCount) {
							mWaitTableView.getSelectionModel().select(mRecordCount - 2);
							mWaitTableView.scrollTo(mRecordCount - 1);
							setCurrentEntryInfo();
						} else {
							selecIndextWaitTable(-1);
						}

						ke.consume();
					}
					// 대기중인 목록 아래로 이동
					if (ke.getCode() == KeyCode.DOWN) {
						selecIndextWaitTable(1);
						ke.consume();
					}
				}
			});
		});
	}

	/**
	 * 대기중인 소 row 선택 ( KEY UP/DOWN )
	 *
	 * @param index
	 */
	private void selecIndextWaitTable(int index) {

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

					int selectIndex = currentSelectedIndex + index;

					if (mRecordCount > selectIndex) {
						mWaitTableView.getSelectionModel().select(selectIndex);
						mWaitTableView.scrollTo(mWaitTableView.getSelectionModel().getSelectedIndex() + 1);
					}
				}

				setCurrentEntryInfo();
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
			if (spEntryInfo.getEntryNum() != null) {
				dataList.add(spEntryInfo);
			}
		}

		return dataList;
	}

	/**
	 * 전체 출품 정보 조회 EntryNum 없는 dummy row 제외
	 * 
	 * @return
	 */
	public ObservableList<SpEntryInfo> getWaitEntryInfoPendingDataList() {

		ObservableList<SpEntryInfo> dataList = FXCollections.observableArrayList();

		for (SpEntryInfo spEntryInfo : mWaitEntryInfoDataList) {

			if (spEntryInfo.getEntryNum() != null && spEntryInfo.getAuctionResult() != null) {

				if (spEntryInfo.getAuctionResult().getValue().equals(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING)) {
					dataList.add(spEntryInfo);
				}
			}
		}

		return dataList;
	}

}
