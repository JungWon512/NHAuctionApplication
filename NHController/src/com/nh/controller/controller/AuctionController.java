package com.nh.controller.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.nh.controller.interfaces.IntegerListener;
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
import com.nh.share.controller.models.EditSetting;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.CurrentEntryInfo;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

//		initFinishedEntryDataList();
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

						if (mCurrentSpEntryInfo == null) {
							selectIndexWaitTable(0, false);
						}

						mWaitTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldItem, newItem) -> {

							if (newItem != null) {

								SpEntryInfo oldSpEntryInfo = oldItem;
								SpEntryInfo newSpEntryInfo = newItem;

								if (isEmptyProperty(newSpEntryInfo.getEntryNum())) {
									if (!isEmptyProperty(newSpEntryInfo.getEntryNum())) {
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
	private void refreshWaitEntryDataList() {

		List<EntryInfo> entryInfoDataList = EntryInfoMapperService.getInstance().getAllEntryData(this.auctionRound); // 테스트

		ObservableList<SpEntryInfo> newEntryDataList = getParsingEntryDataList(entryInfoDataList);

		// 조회 데이터 없으면 리턴
		if (CommonUtils.getInstance().isListEmpty(newEntryDataList)) {
			return;
		}

		// 현재 최종 수정시간 < 조회된 최종 수정시간 -> 데이터 갱신&서버 전달
		for (int i = 0; mRecordCount > i; i++) {

			String curEntryNum = mWaitEntryInfoDataList.get(i).getEntryNum().getValue();

			for (int j = 0; newEntryDataList.size() > j; j++) {

				String newEntryNum = newEntryDataList.get(j).getEntryNum().getValue();

				if (curEntryNum.equals(newEntryNum)) {

					if (newEntryDataList.get(j).getLsChgDtm() == null || newEntryDataList.get(j).getLsChgDtm().getValue().isEmpty() || mWaitEntryInfoDataList.get(i).getLsChgDtm() == null || mWaitEntryInfoDataList.get(i).getLsChgDtm().getValue().isEmpty()) {
						break;
					}

					long newDt = Long.parseLong(newEntryDataList.get(j).getLsChgDtm().getValue());
					long curDt = Long.parseLong(mWaitEntryInfoDataList.get(i).getLsChgDtm().getValue());

					if (newDt > curDt) {
						mWaitEntryInfoDataList.set(i, newEntryDataList.get(j));
						// 출품정보 전송 후 변경된 사항 전달.
						if (mBtnF1.isDisable()) {
							AuctionDelegate.getInstance().onSendEntryData(newEntryDataList.get(j));
							mLogger.debug("변경된 출품 정보 서버 전송 : " + newEntryDataList.get(j).getEntryNum().getValue());
						}
					}
					break;
				}
			}
		}

		// 추가된 데이터 있는지 확인
		ObservableList<SpEntryInfo> newDataList = newEntryDataList.stream().filter(e -> !mWaitEntryInfoDataList.contains(e)).collect(Collectors.toCollection(FXCollections::observableArrayList));

		// 추가된 데이터 항목이 있으면 add
		if (!CommonUtils.getInstance().isListEmpty(newDataList)) {
			mWaitEntryInfoDataList.addAll(mRecordCount, newDataList);
			mRecordCount += newDataList.size();

		}

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
	private void initFinishedEntryDataList(ObservableList<SpEntryInfo> dataList) {

		if (CommonUtils.getInstance().isListEmpty(dataList)) {
			// dummy row
			ObservableList<SpEntryInfo> dummyRow = FXCollections.observableArrayList(); // dummy row
			for (int i = 0; DUMMY_ROW_FINISHED > i; i++) {
				dummyRow.add(new SpEntryInfo());
			}
			mFinishedEntryInfoDataList.addAll(dummyRow);
		} else {

			int sumSize = dataList.size() - DUMMY_ROW_FINISHED;

			if (sumSize < 0) {

				ObservableList<SpEntryInfo> dummyRow = FXCollections.observableArrayList(); // dummy row

				int reSize = sumSize * -1;
				
				for (int i = 0; reSize > i; i++) {
					dummyRow.add(new SpEntryInfo());
				}
				
				System.out.println("sizee " + dummyRow.size() + " / " + reSize);

				mFinishedTableView.setItems(dummyRow);
				
				mFinishedTableView.getItems().addAll(dataList);
			}

			mFinishedTableView.scrollTo(mFinishedTableView.getItems().size() - 1);
		}
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
						if (entryInfo.getEntryNum() != null && !entryInfo.getEntryNum().getValue().isEmpty()) {
							addLogItem(mResMsg.getString("msg.auction.send.entry.data") + AuctionDelegate.getInstance().onSendEntryData(entryInfo));
						}
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

				String entryNum = spEntryInfo.getEntryNum().getValue();
				String auctionHouseCode = spEntryInfo.getAuctionHouseCode().getValue();
				String entryType = spEntryInfo.getEntryType().getValue();
				String aucDt = spEntryInfo.getAucDt().getValue();
				String state = GlobalDefineCode.AUCTION_RESULT_CODE_PENDING;

				EntryInfo entryInfo = new EntryInfo();
				entryInfo.setEntryNum(entryNum);
				entryInfo.setAuctionHouseCode(auctionHouseCode);
				entryInfo.setEntryType(entryType);
				entryInfo.setAucDt(aucDt);
				entryInfo.setAuctionResult(state);
				entryInfo.setLsCmeNo("admin");

				final int resultValue = EntryInfoMapperService.getInstance().updateEntryState(entryInfo);

				if (resultValue > 0) {
					// 보류처리
					spEntryInfo.setAuctionResult(new SimpleStringProperty(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING));
					// 서버에 전달
					AuctionDelegate.getInstance().onSendEntryData(spEntryInfo);

					setCurrentEntryInfo();

					mWaitTableView.refresh();

				} else {
					mLogger.debug("보류 처리 오류 " + entryNum);
				}

			}
		});
	}

	/**
	 * 전체 보기
	 */
	public void openEntryListPopUp() {

		ObservableList<SpEntryInfo> dataList = getWaitEntryInfoDataList();

		MoveStageUtil.getInstance().openEntryListPopUp(mStage, dataList, new IntegerListener() {

			@Override
			public void callBack(int value) {

				MoveStageUtil.getInstance().setBackStageDisableFalse(mStage);

				if (value > -1) {
					selectIndexWaitTable(value, true);
				}
			}
		});

	}

	/**
	 * 보류 목록 보기
	 */
	public void openEntryPendingListPopUp() {

		ObservableList<SpEntryInfo> dataList = getWaitEntryInfoPendingDataList();

		MoveStageUtil.getInstance().openEntryPendingListPopUp(mStage, dataList, new IntegerListener() {

			@Override
			public void callBack(int value) {

				MoveStageUtil.getInstance().setBackStageDisableFalse(mStage);

			}
		});
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
		case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
			setAuctionVariableState(mAuctionStatus.getState());
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

		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_READY:
		case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
			String msgStart = String.format(mResMsg.getString("msg.auction.send.start"), mCurrentSpEntryInfo.getEntryNum().getValue());
			// 시작
			addLogItem(msgStart + AuctionDelegate.getInstance().onStartAuction(mCurrentSpEntryInfo.getEntryNum().getValue()));
			break;
		}
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
			mBtnF6.setDisable(true);
			mBtnF7.setDisable(true);
			mBtnEnter.setDisable(true);
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

//			addFinishedTableViewItem(mCurrentSpEntryInfo); // 강제 유찰 완료상태에 넣어야되면 주석해제~

			// 낙유찰 화면 딜레이 2초 후 경매 대기 전환
			PauseTransition pauseTransition = new PauseTransition(Duration.millis(2000));
			pauseTransition.setOnFinished(event -> {
				selectIndexWaitTable(1, false);
				// 경매 대기
				setAuctionStatus(GlobalDefineCode.AUCTION_STATUS_READY);
				setAuctionVariableState(GlobalDefineCode.AUCTION_STATUS_READY);
			});
			pauseTransition.play();
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
		List<AuctionRound> list = service.getAllAuctionRoundData("20210806"); // 테스트
		this.auctionRound = list.get(0); // 테스트
//        for (AuctionRound t : list) {
//            qcn = t.getQcn();
//        }

		// 경매 출품 데이터 가져오기
		EntryInfoMapperService entryService = new EntryInfoMapperService();

		List<EntryInfo> finishedEntryInfoDataList = entryService.getFinishedEntryData(this.auctionRound);
		mFinishedEntryInfoDataList.clear();
		mFinishedEntryInfoDataList = getParsingEntryDataList(finishedEntryInfoDataList);

		List<EntryInfo> entryInfoDataList = entryService.getAllEntryData(this.auctionRound); // 테스트
		mWaitEntryInfoDataList.clear();
		mWaitEntryInfoDataList = getParsingEntryDataList(entryInfoDataList);

		initFinishedEntryDataList(mFinishedEntryInfoDataList);
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

		// 현재 선택된 row
		SpEntryInfo spEntryInfo = mWaitTableView.getSelectionModel().getSelectedItem();

		String targetEntryNum = spEntryInfo.getEntryNum().getValue();
		String targetAuctionHouseCode = spEntryInfo.getAuctionHouseCode().getValue();
		String targetEntryType = spEntryInfo.getEntryType().getValue();
		String targetAucDt = spEntryInfo.getAucDt().getValue();
		String updatePrice = Integer.toString(mCurrentSpEntryInfo.getLowPriceInt() + price);

		EntryInfo entryInfo = new EntryInfo();
		entryInfo.setEntryNum(targetEntryNum);
		entryInfo.setAuctionHouseCode(targetAuctionHouseCode);
		entryInfo.setEntryType(targetEntryType);
		entryInfo.setAucDt(targetAucDt);
		entryInfo.setLowPrice(updatePrice);
		entryInfo.setLsCmeNo("admin");

		if (updatePrice == null || updatePrice.isEmpty() || Integer.parseInt(updatePrice) < 0) {
			// 가격정보 null, 0보다 작으면 리턴
			return;
		}

		final int resultValue = EntryInfoMapperService.getInstance().updateEntryPrice(entryInfo);

		if (resultValue > 0) { // 업데이트 성공시 UI갱신, 서버로 바뀐 정보 보냄
			spEntryInfo.getLowPrice().setValue(updatePrice);
			setCurrentEntryInfo();
			AuctionDelegate.getInstance().onSendEntryData(spEntryInfo);
		} else {
			mLogger.debug("가격 업데이트 실패 : " + spEntryInfo.getEntryNum().getValue() + "=> " + updatePrice);
		}
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
				EditSetting setting = new EditSetting(new String[] { "", this.auctionRound.getNaBzplc(), "Y", "Y", "Y", "Y", "N", "Y", "Y", "N", "Y", "Y", "N", "N", "5" });
				addLogItem(mResMsg.getString("msg.auction.send.setting.info") + AuctionDelegate.getInstance().onSendSettingInfo(setting));
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

				System.out.println("[!! 재접속 스크롤 설정] : " + entryNum + " / index : " + i);
				
				mCurrentSpEntryInfo = mWaitTableView.getItems().get(i);

				selectIndexWaitTable(i, true);

				switch (mCurrentSpEntryInfo.getAuctionResult().getValue()) {
				case GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS:
					mAuctionStateReadyLabel.setDisable(true);
					mAuctionStateProgressLabel.setDisable(true);
					mAuctionStateSuccessLabel.setDisable(false);
					mAuctionStateFailLabel.setDisable(true);
					mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.success"));
					break;
				case GlobalDefineCode.AUCTION_RESULT_CODE_PENDING:
					mAuctionStateReadyLabel.setDisable(true);
					mAuctionStateProgressLabel.setDisable(true);
					mAuctionStateSuccessLabel.setDisable(true);
					mAuctionStateFailLabel.setDisable(false);
					mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.fail"));
					break;
				}
			}

		}

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

		// 카운트 다운 시 종료/강제낙찰/유찰(보류) 버튼 비활성화
		mBtnEnter.setDisable(true);
		mBtnF6.setDisable(true);
		mBtnF7.setDisable(true);
	}

	/**
	 * 경매 준비 뷰 초기화
	 */
	private void setAuctionVariableState(String code) {

		Platform.runLater(() -> {

			// 모든 상태 정보. 출품 정보 보내기 버튼 비활성화
			mBtnF1.setDisable(true);
			// 시작 or 종료 버튼 활성화
			mBtnEnter.setDisable(false);

			switch (code) {

			case GlobalDefineCode.AUCTION_STATUS_READY:

				// 남은 시간 Bar 초기화
				for (int i = 0; cntList.size() > i; i++) {
					cntList.get(i).setDisable(false);
				}
				// 카운트 시간 초기화
				mRemainingTimeCount = REMAINING_TIME_COUNT;
				// 현재 응찰 내역 초기화
				mCurrentBidderMap.clear();
				// 이전 응찰 내역 초기화
				mBeForeBidderDataList.clear();
				// 강제 낙찰 버튼 비활성화
				mBtnF6.setDisable(true);
				// 강제 유찰 버튼 비활성화
				mBtnF7.setDisable(true);
				// 응찰자 초기화
				initBiddingInfoDataList();
				// 유찰(보류) 여부 초기화
				mIsPass = false;

				// 진행 라벨 비활성화
				mAuctionStateProgressLabel.setDisable(true);
				// 완료 라벨 비활성화
				mAuctionStateSuccessLabel.setDisable(true);
				// 유찰(보류) 라벨 비활성화
				mAuctionStateFailLabel.setDisable(true);
				// 출품 대기 테이블 비활성화
				mWaitTableView.setDisable(false);
				break;
			case GlobalDefineCode.AUCTION_STATUS_START:
			case GlobalDefineCode.AUCTION_STATUS_PROGRESS:

				// ENTER 경매시작 -> 경매완료 변경
				mBtnEnter.setText(mResMsg.getString("str.btn.stop"));
				// ENTER 경매완료 css 적용
				CommonUtils.getInstance().addStyleClass(mBtnEnter, "btn-auction-stop");

				// 강제 낙찰 버튼 활성화
				mBtnF6.setDisable(false);
				// 강제 유찰 버튼 활성화
				mBtnF7.setDisable(false);
				// 대기 라벨 비활성화
				mAuctionStateReadyLabel.setDisable(true);
				// 진행 라벨 활성화
				mAuctionStateProgressLabel.setDisable(false);
				// 낙찰 라벨 비활성화
				mAuctionStateSuccessLabel.setDisable(true);
				// 유찰 라벨 비활성화
				mAuctionStateFailLabel.setDisable(true);
				// 경매 상태 문구 -> 경매진행
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.auction.progress"));
				// 출품 대기 테이블 비활성화
				mWaitTableView.setDisable(true);
				//가격 상승,다운 비활성화
				mBtnUpPrice.setDisable(true);
				mBtnDownPrice.setDisable(true);

				break;
			case GlobalDefineCode.AUCTION_STATUS_COMPLETED:

				// ENTER 경매완료 -> 경매시작 으로 변경
				mBtnEnter.setText(mResMsg.getString("str.btn.start"));
				CommonUtils.getInstance().removeStyleClass(mBtnEnter, "btn-auction-stop");
				mBtnEnter.setDisable(false);
				// 경매 종료시 /강제낙찰/유찰(보류) 버튼 비활성화
				mBtnF6.setDisable(true);
				mBtnF7.setDisable(true);
				// 출품 대기 테이블 활성화
				mWaitTableView.setDisable(false);
				//가격 상승,다운 활성화
				mBtnUpPrice.setDisable(false);
				mBtnDownPrice.setDisable(false);
				
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
	 * @param bidder    화원
	 */
	@Override
	protected void updateAuctionStateInfo(String code, boolean isSuccess, SpBidding bidder) {

		Platform.runLater(() -> {

			// REFACTOR: 경매완료 후, 경매시작 - server가 아닌 controller에서 진행하도록 변경 됨. (21.07.27)
			// 대기 라벨 비활성화
			mAuctionStateReadyLabel.setDisable(true);
			mAuctionStateProgressLabel.setDisable(true);

			if (isSuccess) {
				mAuctionStateSuccessLabel.setDisable(false);
				mAuctionStateFailLabel.setDisable(true);
				mCurrentSpEntryInfo.setAuctionSucBidder(new SimpleStringProperty(bidder.getUserNo().getValue()));
				mCurrentSpEntryInfo.setAuctionBidPrice(new SimpleStringProperty(bidder.getPrice().getValue()));
				mCurrentSpEntryInfo.setAuctionResult(new SimpleStringProperty(GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS));
				mCurrentSpEntryInfo.setAuctionBidDateTime(new SimpleStringProperty(bidder.getBiddingTime().getValue()));
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.success"));
			} else {
				mAuctionStateSuccessLabel.setDisable(true);
				mAuctionStateFailLabel.setDisable(false);
				mCurrentSpEntryInfo.setAuctionResult(new SimpleStringProperty(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING));
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.fail"));
			}

			addFinishedTableViewItem(mCurrentSpEntryInfo);

			// 낙유찰 화면 딜레이 2초 후 경매 대기 전환
			PauseTransition pauseTransition = new PauseTransition(Duration.millis(2000));
			pauseTransition.setOnFinished(event -> {
				// 다음 출품 번호 이동
				selectIndexWaitTable(1, false);
			});
			pauseTransition.play();

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

		if (isEmptyProperty(currentEntryInfo.getEntryNum())) {
			return;
		}

		Platform.runLater(() -> {

			mCurrentSpEntryInfo = currentEntryInfo;
			mCurEntryNumLabel.setText(mCurrentSpEntryInfo.getEntryNum().getValue());
			mCurExhibitorLabel.setText(mCurrentSpEntryInfo.getExhibitor().getValue());
			mCurGenterLabel.setText(mCurrentSpEntryInfo.getGender().getValue());
			mCurMotherLabel.setText(mCurrentSpEntryInfo.getMotherObjNum().getValue());
			mCurMatimeLabel.setText(mCurrentSpEntryInfo.getMatime().getValue());
			mCurPasgQcnLabel.setText(mCurrentSpEntryInfo.getPasgQcn().getValue());
			mCurWeightLabel.setText(mCurrentSpEntryInfo.getWeight().getValue());
			mCurLowPriceLabel.setText(String.format(mResMsg.getString("str.price"), mCurrentSpEntryInfo.getLowPriceInt()));
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
						refreshWaitEntryDataList();
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

						if (mWaitTableView.isDisable()) {
							return;
						}
						if (mWaitTableView.getSelectionModel().getSelectedIndex() > mRecordCount) {
							mWaitTableView.getSelectionModel().select(mRecordCount - 2);
							mWaitTableView.scrollTo(mRecordCount - 1);
							setCurrentEntryInfo();
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
				}
			});
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
			if (!isEmptyProperty(spEntryInfo.getEntryNum())) {
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

			if (!isEmptyProperty(spEntryInfo.getEntryNum()) && !isEmptyProperty(spEntryInfo.getAuctionResult())) {
				if (spEntryInfo.getAuctionResult().getValue().equals(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING)) {
					dataList.add(spEntryInfo);
				}
			}
		}

		return dataList;
	}
	

	/**
	 * null : true , not null : false;
	 * @param strProperty
	 * @return
	 */
    public boolean isEmptyProperty(StringProperty strProperty) {

    	if(strProperty != null && !strProperty.getValue().equals("")) {
    		return false;
    	}else {
    		return true;
    	}
    }
    

}
