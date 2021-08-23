package com.nh.controller.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import com.nh.controller.interfaces.BooleanListener;
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
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.MoveStageUtil.EntryDialogType;
import com.nh.controller.utils.SharedPreference;
import com.nh.controller.utils.SoundUtil;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.common.models.RetryTargetInfo;
import com.nh.share.controller.models.EditSetting;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.PauseAuction;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.CurrentEntryInfo;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

public class AuctionController extends BaseAuctionController implements Initializable {

	@FXML // root pane
	public BorderPane mRootPane;

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
	private Button mBtnEsc, mBtnF1, mBtnF3, mBtnF4, mBtnF5, mBtnF6, mBtnF7, mBtnF8, mBtnEnter,mBtnSpace, mBtnUpPrice, mBtnDownPrice;

	@FXML // 경매 정보
	private Label mAuctionInfoDateLabel, mAuctionInfoRoundLabel, mAuctionInfoGubunLabel, mAuctionInfoNameLabel;

	@FXML // 경매 정보 - 상태
	private Label mAuctionStateReadyLabel, mAuctionStateProgressLabel, mAuctionStateSuccessLabel, mAuctionStateFailLabel, mAuctionStateLabel;

	@FXML // 남은 시간 Bar
	private Label cnt_5, cnt_4, cnt_3, cnt_2, cnt_1;

	@FXML // 메세지 보내기 버튼
	private ImageView mImgMessage;

	@FXML // 감가 기준 금액 / 횟수
	private Label mDeprePriceLabel, mLowPriceChgNtLabel;

	@FXML // 음성 선택 check-box
	private CheckBox mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mMotherObjNumCheckBox, mMaTimeCheckBox, mPasgQcnCheckBox, mWeightCheckBox, mLowPriceCheckBox, mBrandNameCheckBox, mKpnCheckBox;

	@FXML // 음성 멘트 버튼
	private Button mBtnIntroSound, mBtnBuyerSound, mBtnGuideSound, mBtnEtc_1_Sound, mBtnEtc_2_Sound, mBtnEtc_3_Sound, mBtnEtc_4_Sound, mBtnEtc_5_Sound, mBtnEtc_6_Sound;

	@FXML // 음성설정 ,저장 ,음성중지 ,낙찰결과
	private Button mBtnSettingSound, mBtnSave, mBtnStopSound, mBtnEntrySuccessList;

	@FXML // 일시정지 재시작 ,일시정지
	private Button mBtnReStart, mBtnPause;
	
	@FXML // 재경매중 라벨,카운트다운
	private Label mReAuctionLabel,mReAuctionCountLabel,mCountDownLabel;

	private List<Label> cntList = new ArrayList<Label>(); // 남은 시간 Bar list

	private int mRemainingTimeCount = 5; // 카운트다운

	private final SharedPreference preference = new SharedPreference();

	// 사운드 클래스
	private SoundUtil mEntryInfoSound = new SoundUtil();
	// 사운드 클래스
	private SoundUtil mAuctionInfoMessageSound = new SoundUtil();
	
	private Timer mAutoStopScheduler = null;
	
	
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
	}

	/**
	 * 기본 뷰 설정
	 */
	private void initViewConfiguration() {

		initParsingSharedData();
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
		mBtnEnter.setOnMouseClicked(event -> onStartAndStopAuction(0));
		mBtnSpace.setOnMouseClicked(event -> onStartSoundAuction());
		mBtnF6.setOnMouseClicked(event -> onSuccessAuction());
//		mBtnF7.setOnMouseClicked(event -> onPassAuction());
		mBtnF8.setOnMouseClicked(event -> openSettingDialog());
		mImgMessage.setOnMouseClicked(event -> openSendMessage(event));
		mWaitTableView.setOnMouseClicked(event -> onClickWaitTableView(event));
		mBtnUpPrice.setOnMouseClicked(event -> onUpPrice(event));
		mBtnDownPrice.setOnMouseClicked(event -> onDownPrice(event));
		mBtnSettingSound.setOnMouseClicked(event -> openSettingSoundDialog(event));
		mBtnEntrySuccessList.setOnMouseClicked(event -> openFinishedEntryListPopUp());
		// mBtnSave.setOnMouseClicked(event -> saveMainSoundEntryInfo()); 메인 저장 버튼 일단 UI
		// 표시 숨김.

		mBtnStopSound.setOnMouseClicked(event -> mAuctionInfoMessageSound.stopSound());
		mBtnIntroSound.setOnMouseClicked(event -> mAuctionInfoMessageSound.playSound(0));
		mBtnBuyerSound.setOnMouseClicked(event -> mAuctionInfoMessageSound.playSound(1));
		mBtnGuideSound.setOnMouseClicked(event -> mAuctionInfoMessageSound.playSound(2));
		mBtnEtc_1_Sound.setOnMouseClicked(event -> mAuctionInfoMessageSound.playSound(6));
		mBtnEtc_2_Sound.setOnMouseClicked(event -> mAuctionInfoMessageSound.playSound(7));
		mBtnEtc_3_Sound.setOnMouseClicked(event -> mAuctionInfoMessageSound.playSound(8));
		mBtnEtc_4_Sound.setOnMouseClicked(event -> mAuctionInfoMessageSound.playSound(9));
		mBtnEtc_5_Sound.setOnMouseClicked(event -> mAuctionInfoMessageSound.playSound(10));
		mBtnEtc_6_Sound.setOnMouseClicked(event -> mAuctionInfoMessageSound.playSound(11));
	
		mBtnReStart.setOnMouseClicked(event -> onReStart());
		mBtnPause.setOnMouseClicked(event -> onPause());
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

		setNumberColumnFactory(mFinishedWeightColumn, false);
		setNumberColumnFactory(mFinishedLowPriceColumn, true);
		setNumberColumnFactory(mFinishedSuccessPriceColumn, true);

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

		setNumberColumnFactory(mWaitWeightColumn, false);
		setNumberColumnFactory(mWaitLowPriceColumn, true);
		setNumberColumnFactory(mWaitSuccessPriceColumn, true);

		// 테이블 컬럼 - 접속자
		mConnectionUserColumn_1.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mConnectionUserColumn_2.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mConnectionUserColumn_3.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mConnectionUserColumn_4.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mConnectionUserColumn_5.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());

		// 테이블 컬럼 - 응찰자
		mBiddingPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getPrice());
		mBiddingUserColumn.setCellValueFactory(cellData -> cellData.getValue().getAuctionJoinNum());
		setNumberColumnFactory(mBiddingPriceColumn, true);
		
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
	 * 출품 정보 음성 설정 저장된 값들 셋팅
	 */
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
		mKpnCheckBox.setSelected(checkList.get(9));

		mEntryNumCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_NUMBER);
		mExhibitorCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_EXHIBITOR);
		mGenderCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_GENDER);
		mMotherObjNumCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MOTHER);
		mMaTimeCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MATIME);
		mPasgQcnCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_PASGQCN);
		mWeightCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_WEIGHT);
		mLowPriceCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_LOWPRICE);
		mBrandNameCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_BRAND);
		mKpnCheckBox.setUserData(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_KPN);

		mEntryNumCheckBox.setOnAction(mCheckBoxEventHandler);
		mExhibitorCheckBox.setOnAction(mCheckBoxEventHandler);
		mGenderCheckBox.setOnAction(mCheckBoxEventHandler);
		mMotherObjNumCheckBox.setOnAction(mCheckBoxEventHandler);
		mMaTimeCheckBox.setOnAction(mCheckBoxEventHandler);
		mPasgQcnCheckBox.setOnAction(mCheckBoxEventHandler);
		mWeightCheckBox.setOnAction(mCheckBoxEventHandler);
		mLowPriceCheckBox.setOnAction(mCheckBoxEventHandler);
		mBrandNameCheckBox.setOnAction(mCheckBoxEventHandler);
		mKpnCheckBox.setOnAction(mCheckBoxEventHandler);
		// 메인 상단 체크박스 [E]

		// 메세지 버튼 값 가져옴.
		initParsingSoundDataList();
	}

	/**
	 * 내부 저장된 음성 메세지 가져옴.
	 */
	private List<String> initParsingSoundDataList() {

		// 사운드 텍스트 저장 리스트.
		List<String> soundDataList = SettingApplication.getInstance().getParsingSoundDataList();
		mAuctionInfoMessageSound.setSoundDataList(soundDataList);
		return soundDataList;
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

		SettingApplication.getInstance().setAuctionObjDsc(this.auctionRound.getAucObjDsc());
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
	 * 대기중인 출품 목록 갱신 변경/추가된 데이터 서버 전달
	 */
	private void refreshWaitEntryDataList() {

		List<EntryInfo> entryInfoDataList = EntryInfoMapperService.getInstance().getAllEntryData(this.auctionRound); // 테스트

		ObservableList<SpEntryInfo> newEntryDataList = getParsingEntryDataList(entryInfoDataList);

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

				
					if (isEmptyProperty(newEntryDataList.get(j).getLsChgDtm()) || isEmptyProperty(mWaitEntryInfoDataList.get(i).getLsChgDtm())) {
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

		// 추가된 데이터 있는지 확인
		ObservableList<SpEntryInfo> newDataList = newEntryDataList.stream().filter(e -> !mWaitEntryInfoDataList.contains(e)).collect(Collectors.toCollection(FXCollections::observableArrayList));

		// 추가된 데이터 항목이 있으면 add
		if (!CommonUtils.getInstance().isListEmpty(newDataList)) {
			mWaitEntryInfoDataList.addAll(mRecordCount, newDataList);
			mRecordCount += newDataList.size();
		} else {
			addLogItem("추기된 데이터 없음.");
		}

		mWaitTableView.setItems(mWaitEntryInfoDataList);
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

					int count = 0;
					for (SpEntryInfo entryInfo : mWaitEntryInfoDataList) {
						if (!isEmptyProperty(entryInfo.getEntryNum())) {
							if(entryInfo.getIsLastEntry().getValue().equals("Y")) {
								System.out.println("entryInfo.getIsLastEntry() : " + entryInfo.getEntryNum().getValue());
							}
							
							addLogItem(mResMsg.getString("msg.auction.send.entry.data") + AuctionDelegate.getInstance().onSendEntryData(entryInfo));
							count++;
						}
					}

					addLogItem(String.format(mResMsg.getString("msg.send.entry.data.result"), count));

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

			if (!spEntryInfo.getAuctionResult().getValue().equals(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING)) {

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
				entryInfo.setLsCmeNo(GlobalDefineCode.AUCTION_LOGIN_TYPE_MANAGER);

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

		if (MoveStageUtil.getInstance().getDialog() != null && MoveStageUtil.getInstance().getDialog().isShowing()) {
			return;
		}

		if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_START) || mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
			return;
		}

		refreshWaitEntryDataList();

		ObservableList<SpEntryInfo> dataList = getWaitEntryInfoDataList();

		openEntryDialog(EntryDialogType.ENTRY_LIST, dataList);

	}

	/**
	 * 낙찰 결과 보기
	 */
	public void openFinishedEntryListPopUp() {

		ObservableList<SpEntryInfo> dataList = getFinishedEntryInfoDataList();

		if (CommonUtils.getInstance().isListEmpty(dataList)) {
			addLogItem("경매 결과 없음.");
			return;
		}

		openEntryDialog(EntryDialogType.ENTRY_FINISH_LIST, dataList);

	}

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
	 * 보류 목록 보기
	 */
	public void openEntryPendingListPopUp() {

		if (MoveStageUtil.getInstance().getDialog() != null && MoveStageUtil.getInstance().getDialog().isShowing()) {
			return;
		}

		ObservableList<SpEntryInfo> dataList = getWaitEntryInfoPendingDataList();

		openEntryDialog(EntryDialogType.ENTRY_PENDING_LIST, dataList);
	}

	/**
	 * 전체보기,보류보기,낙찰결과보기 Dialog
	 * 
	 * @param type
	 * @param dataList
	 */
	private void openEntryDialog(EntryDialogType type, ObservableList<SpEntryInfo> dataList) {

		MoveStageUtil.getInstance().openEntryListDialog(type, mStage, dataList, new IntegerListener() {
			@Override
			public void callBack(int value) {

				dismissShowingDialog();

				addLogItem("Dialog callBack Value : " + value);
				if (value < 0) {
					return;
				}
				if (CommonUtils.getInstance().isListEmpty(dataList)) {
					return;
				}
				if (type.equals(EntryDialogType.ENTRY_FINISH_LIST)) {
					return;
				}

				setWaitEntryDataList(dataList);

				if (value > -1) {
					selectIndexWaitTable(value, true);
				}
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

				initParsingSoundDataList();
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

		MoveStageUtil.getInstance().openSettingDialog(mStage, new BooleanListener() {

			@Override
			public void callBack(Boolean isClose) {
				dismissShowingDialog();
				// 환경설정 저장 후 값들 재설정
				SettingApplication.getInstance().initSharedData();
				
				//단일 경매, 음성 경매 버튼 
				if(!SettingApplication.getInstance().isUseSoundAuction()) {
					mBtnEnter.setDisable(false);
					mBtnSpace.setDisable(true);
				}else {
					mBtnEnter.setDisable(true);
					mBtnSpace.setDisable(false);
				}
			}
		});
	}

	/**
	 * 프로그램 종료
	 */
	public void onCloseApplication() {
		addLogItem("종료");
		Platform.exit();
		System.exit(0);
	}

	/**
	 * 단일 경매 시작. 종료
	 * @param countDown
	 */
	public void onStartAndStopAuction(int countDown) {
		
		if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
			showAlertPopupOneButton(mResMsg.getString("msg.auction.send.need.entry.data"));
			return;
		}

		if(SettingApplication.getInstance().isUseSoundAuction()) {
			if(!isStartedAuction) {
				showAlertPopupOneButton(mResMsg.getString("dialog.auction.sound"));
			}
			return;
		}
		
//		if (mBtnEnter.isDisable()) {
//			return;
//		}
		
		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_READY:
		case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
		case GlobalDefineCode.AUCTION_STATUS_PASS:
			// 갱신 후 변경점 있으면 서버 전달.
			refreshWaitEntryDataList();
			// 경매 뷰 초기화
			setAuctionVariableState(mAuctionStatus.getState());
			// 시작
			onStartAuction();
			break;
		case GlobalDefineCode.AUCTION_STATUS_START:
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
	
			if(isAuctionComplete) {
				//isAuctionComplete : true , 낙찰 예정자 있으면 경매 결과,DB 저장
				sendAuctionResultInfo();
			}else {
				//종료 카운트다운 시작.
				onStopAuction(countDown);
			}

			break;
		}
	}
	
	/**
	 * 사운드 경매 시작
	 */
	public void onStartSoundAuction() {

		if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
			showAlertPopupOneButton(mResMsg.getString("msg.auction.send.need.entry.data"));
			return;
		}

		if(!SettingApplication.getInstance().isUseSoundAuction()) {
			if(!isStartedAuction) {
				showAlertPopupOneButton(mResMsg.getString("dialog.auction.no.sound"));
			}
			return;
		}


		if (mBtnSpace.isDisable()) {
			return;
		}
		
		
		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_READY:
		case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
		case GlobalDefineCode.AUCTION_STATUS_PASS:
			// 갱신 후 변경점 있으면 서버 전달.
			refreshWaitEntryDataList();
			// 경매 뷰 초기화
			setAuctionVariableState(mAuctionStatus.getState());
			// 시작
			onStartAuction();
			break;
		}
	}


	
	/**
	 * 경매 결과 전송, DB 저장
	 */
	public void sendAuctionResultInfo() {
		
		if(mRank_1_User != null) {
			if(!isEmptyProperty(mRank_1_User.getAuctionJoinNum())) {
				sendAuctionResult(true, mCurrentSpEntryInfo,mRank_1_User,GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS);
			}else {
				sendAuctionResult(false, mCurrentSpEntryInfo,mRank_1_User,GlobalDefineCode.AUCTION_RESULT_CODE_PENDING);
			}
		}else {
			sendAuctionResult(false, mCurrentSpEntryInfo,mRank_1_User,GlobalDefineCode.AUCTION_RESULT_CODE_PENDING);
		}
		
		//사운드 경매 타이머 제거
		stopAutoAuctionScheduler();
	}
	
	/**
	 * 1.준비 2.시작
	 *
	 * @param event
	 */
	public void onStartAuction() {

		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_READY:
		case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
		case GlobalDefineCode.AUCTION_STATUS_PASS:
			// 사운드 시작
			mEntryInfoSound.playSound(0);

			String msgStart = String.format(mResMsg.getString("msg.auction.send.start"), mCurrentSpEntryInfo.getEntryNum().getValue());
			// 시작
			addLogItem(msgStart + AuctionDelegate.getInstance().onStartAuction(mCurrentSpEntryInfo.getEntryNum().getValue()));
			
			isStartedAuction = true;
			break;
		}
	}

	/**
	 * 종료
	 *
	 * @param event
	 */
	public void onStopAuction(int countDown) {

		if (mCurrentSpEntryInfo == null) {
			return;
		}

		//카운트다운 중에는 막음.
		if(isCountDownRunning) {
			return;
		}


		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_START:
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
			//카운트 다운 완료시 강제낙찰/강제유찰/경매완료 버튼 활성/비활성화
//			btnStopAuctionToggle(true);
			isCountDownRunning = true;
			mRemainingTimeCount =  countDown;
			addLogItem(mResMsg.getString("msg.auction.send.complete") + AuctionDelegate.getInstance().onStopAuction(mCurrentSpEntryInfo.getEntryNum().getValue(),countDown));
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
			//카운트 다운 중일 경우 실행 안 함
			if(isCountDownRunning) {
				return;
			}
			
			if(!isAuctionComplete) {
				if(!CommonUtils.getInstance().isListEmpty(mBiddingUserInfoDataList)) {
					//1순위 회원
					SpBidding rank_1_user = mBiddingUserInfoDataList.get(0);
					setSuccessUser(rank_1_user);
				}else {
					setSuccessUser(null);
				}
				
			}else {
				onStartAndStopAuction(0);
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
	
	/**
	 * 경매 진행 -> 카운트 다운 일시 정지.
	 */
	public void onPause() {
		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_START:
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
			
//			if(isCountDownRunning) {
				
				//자동경매 카운트다운중인경우 스케줄러 멈춤.
				if(SettingApplication.getInstance().isUseSoundAuction()) {
					stopAutoAuctionScheduler();
				}
				
				addLogItem("카운트 다운 정지 : " + AuctionDelegate.getInstance().onPause(new PauseAuction(mCurrentSpEntryInfo.getAuctionHouseCode().getValue(), mCurrentSpEntryInfo.getEntryNum().getValue())));
//			}
		}
	}

	/**
	 * 경매 진행 -> 카운트 다운 일시 정지.
	 * 경매 진행 -> 카운트 다운 시작.
	 */
	public void onReStart() {
		switch (mAuctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_START:
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
				onStopAuction(mRemainingTimeCount);
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
		// TODO: 경매정보 리스트 뿌리기
		// List<AuctionRound> list =
		// AuctionRoundMapperService.getInstance().getAllAuctionRoundData(CommonUtils.getInstance().getCurrentTime("yyyyMMdd"));
		// // 실제사용
		List<AuctionRound> list = AuctionRoundMapperService.getInstance().getAllAuctionRoundData("20210813"); // 테스트
		this.auctionRound = list.get(0); // 테스트
		preference.setString(SharedPreference.PREFERENCE_AUCTION_HOUSE_CODE, this.auctionRound.getNaBzplc()); // Setting Controller 에서 필요..
//        for (AuctionRound t : list) {
//            qcn = t.getQcn();
//        }

		// 경매 출품 데이터 가져오기
		List<EntryInfo> entryInfoDataList = EntryInfoMapperService.getInstance().getAllEntryData(this.auctionRound); // 테스트
		mWaitEntryInfoDataList.clear();
		mWaitEntryInfoDataList = getParsingEntryDataList(entryInfoDataList);

		initFinishedEntryDataList();
		initWaitEntryDataList(mWaitEntryInfoDataList);

	}

	/**
	 * 예정가 높이기
	 *
	 * @param event
	 */
	public void onUpPrice(MouseEvent event) {
		System.out.println("예정가 높이기");
		int upPrice = SettingApplication.getInstance().getCowLowerLimitPrice();
		setLowPrice(upPrice, true);
	}

	/**
	 * 예정가 낮추기
	 *
	 * @param event
	 */
	public void onDownPrice(MouseEvent event) {
		System.out.println("예정가 낮추기");
		int lowPrice = SettingApplication.getInstance().getCowLowerLimitPrice() * -1;
		setLowPrice(lowPrice, false);
	}

	/**
	 * 예정가 Set
	 *
	 * @param price
	 */
	private void setLowPrice(int price, boolean isUp) {

		// 현재 선택된 row
		SpEntryInfo spEntryInfo = mWaitTableView.getSelectionModel().getSelectedItem();

		String targetEntryNum = spEntryInfo.getEntryNum().getValue();
		String targetAuctionHouseCode = spEntryInfo.getAuctionHouseCode().getValue();
		String targetEntryType = spEntryInfo.getEntryType().getValue();
		String targetAucDt = spEntryInfo.getAucDt().getValue();
		String updatePrice = Integer.toString(spEntryInfo.getLowPriceInt() + price);
		int lowPriceCnt = Integer.parseInt(spEntryInfo.getLwprChgNt().getValue());

		if (isUp) {
			lowPriceCnt -= 1;
		} else {
			lowPriceCnt += 1;
		}

		EntryInfo entryInfo = new EntryInfo();
		entryInfo.setEntryNum(targetEntryNum);
		entryInfo.setAuctionHouseCode(targetAuctionHouseCode);
		entryInfo.setEntryType(targetEntryType);
		entryInfo.setAucDt(targetAucDt);
		entryInfo.setLowPrice(updatePrice);
		entryInfo.setLsCmeNo(GlobalDefineCode.AUCTION_LOGIN_TYPE_MANAGER);
		entryInfo.setLwprChgNt(Integer.toString(lowPriceCnt));

		if (updatePrice == null || updatePrice.isEmpty() || Integer.parseInt(updatePrice) < 0) {
			// 가격정보 null, 0보다 작으면 리턴
			return;
		}

		final int resultValue = EntryInfoMapperService.getInstance().updateEntryPrice(entryInfo);

		if (resultValue > 0) { // 업데이트 성공시 UI갱신, 서버로 바뀐 정보 보냄

			spEntryInfo.getLowPrice().setValue(updatePrice);
			spEntryInfo.getLwprChgNt().setValue(Integer.toString(lowPriceCnt));
			setCurrentEntryInfo();

			String tmpIsLastEntry = spEntryInfo.getIsLastEntry().getValue();

			spEntryInfo.getIsLastEntry().setValue(GlobalDefine.ETC_INFO.AUCTION_DATA_MODIFY_M);

			AuctionDelegate.getInstance().onSendEntryData(spEntryInfo);

			spEntryInfo.getIsLastEntry().setValue(tmpIsLastEntry);

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
				onSendSettingInfo();
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
	
	private void onSendSettingInfo() {
		// TODO: default value setting이랑 맞추기
		EditSetting setting = new EditSetting(this.auctionRound.getNaBzplc(), preference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_ENTRYNUM, "Y"), preference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_EXHIBITOR, "Y"),
				preference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_GENDER, "Y"), preference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_WEIGHT, "Y"), preference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_MOTHER, "Y"),
				preference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_PASSAGE, "Y"), preference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_MATIME, "Y"), preference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_KPN, "N"),
				preference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_REGION, "N"), preference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_NOTE, "N"), preference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_LOWPRICE, "Y"),
				preference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_DNA, "N"), preference.getString(SharedPreference.PREFERENCE_SETTING_COUNTDOWN, "5"));
		addLogItem(mResMsg.getString("msg.auction.send.setting.info") + AuctionDelegate.getInstance().onSendSettingInfo(setting));
	}

	@Override
	public void onCurrentEntryInfo(CurrentEntryInfo currentEntryInfo) {
		super.onCurrentEntryInfo(currentEntryInfo);

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

					System.out.println("[!! 재접속 스크롤 설정] : " + entryNum + " / index : " + i +  "/ " + mAuctionStatus.getState());

					mCurrentSpEntryInfo = mWaitTableView.getItems().get(i);

					selectIndexWaitTable(i, true);
					
				}
			}

//			if( mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_START)
//					&& mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
//				System.out.println("[!! isReconnection] ");
//				isReconnection = true;
//				onStopAuction(0);
//				
//			}
			
		});

//		setCurrentEntryInfo(currentEntryInfo);
	}

	@Override
	public void onAuctionStatus(AuctionStatus auctionStatus) {
		super.onAuctionStatus(auctionStatus);
		setAuctionVariableState(auctionStatus.getState());

		
		switch (auctionStatus.getState()) {
			case GlobalDefineCode.AUCTION_STATUS_PROGRESS :
				
				if(SettingApplication.getInstance().isUseSoundAuction()) {
					//음성 경매시 종료 타이머 시작.
					soundAuctionTimerTask();
				}

			break;
		}
	}
	
	/**
	 * 사운드경매(자동경매) 일정 대기시간 후 경매 카운트
	 */
	public void startAutoAuctionScheduler(int countDown) {
		
		int stopTime = SettingApplication.getInstance().getSoundAuctionWaitTime() * 1000;

		stopAutoAuctionScheduler();

		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				addLogItem("Start startAutoAuctionScheduler");
				onStopAuction(countDown);
			}
		};

		mAutoStopScheduler = new Timer();
		mAutoStopScheduler.schedule(timerTask,stopTime );
		addLogItem("타이머 시작");
	}
	
	/**
	 * 사운드경매(자동경매) 스케줄러 정지
	 */
	public void stopAutoAuctionScheduler() {
		if(mAutoStopScheduler != null) {
			addLogItem("사운드 경매 자동 종료");
			mAutoStopScheduler.cancel();
			mAutoStopScheduler = null;
		}
	}
	
	/**
	 * 사운드 경매시 응찰 금액에 따라 타이머 동작 제어
	 */
	@Override
	protected void soundAuctionTimerTask() {
		
		if(mCurrentBidderMap.size() > 0) {
			//응찰 가격 조건 체크
			if(!isBidderPriceValid()) {
				addLogItem("soundAuctionTimerTask ==== 사운드 경매 자동 종료");
				//타이머 멈춤.
				onPause();
				return;
			}else {
				startAutoAuctionScheduler(SettingApplication.getInstance().getAuctionCountdown());
			}
		}else {
			//타이머 시작
			startAutoAuctionScheduler(SettingApplication.getInstance().getAuctionCountdown());
		}
	
	}

	@Override
	public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
		super.onAuctionCountDown(auctionCountDown);

		if (auctionCountDown.getStatus().equals(GlobalDefineCode.AUCTION_COUNT_DOWN)) {
			
			isCountDownRunning = true;
			if (Integer.parseInt(auctionCountDown.getCountDownTime()) <= 4) {
//				cntList.get(mRemainingTimeCount).setDisable(true);
			}

			Platform.runLater(()->{
				if(!mCountDownLabel.isVisible()) {
					mCountDownLabel.setVisible(true);
				}
				
				mCountDownLabel.setText(auctionCountDown.getCountDownTime());
			});

			mRemainingTimeCount = Integer.parseInt(auctionCountDown.getCountDownTime());
			
		}
		
		if (auctionCountDown.getStatus().equals(GlobalDefineCode.AUCTION_COUNT_DOWN_COMPLETED)) {

			if(mCountDownLabel.isVisible()) {
				mCountDownLabel.setVisible(false);
			}
		
			isCountDownRunning = false;

			addLogItem("==== 카운트 다운 완료 ====");
			
			if(!SettingApplication.getInstance().isUseSoundAuction()) {
				if(mCurrentBidderMap.size() > 0) {
					if(!isAuctionComplete) {
						auctionResult(false);
					}else {
						onStartAndStopAuction(0);
					}
				}
			}else {

				if(mRemainingTimeCount <= 0) {
					addLogItem("==== 음성경매 카운트 다운 완료 ==== : " + mRemainingTimeCount);
					auctionResult(false);
				}
			}
			
			// 카운트 다운 완료시 강제낙찰/강제유찰/경매완료 버튼 활성화
//			btnStopAuctionToggle(false);
		}
	}
	
	/**
	 * 낙유찰 및 재경매
	 * @param isPass : 강제 유찰 : true
	 */
	private void auctionResult(boolean isPass) {
	
		if(mCurrentBidderMap.size() > 0) {
			//응찰 가격 조건 체크
			if(!isBidderPriceValid()) {
				return;
			}
		}else {
			//동일가 재경매 설정 X
			setSuccessUser(null);
			return;
		}
			
		//1순위 회원
		SpBidding rank_1_user = mBiddingUserInfoDataList.get(0);

		//동일가 재경매 설정.
		if(SettingApplication.getInstance().isUseReAuction()) {

			if(mReAuctionCount < 0) {
				//재경매 횟수 +
				mReAuctionCount = SettingApplication.getInstance().getReAuctionCount();
				addLogItem("==== 재경매 횟수 ====: " + mReAuctionCount);
			}else {
				//카운트다운마다 재경매 횟수 -
				mReAuctionCount--;
			}

			//재경매 횟수가 0이면 1순위 응찰자 낙/유찰
			if(mReAuctionCount <= 0) {
				if(!CommonUtils.getInstance().isListEmpty(mBiddingUserInfoDataList)) {
					setSuccessUser(rank_1_user);
				}else {
					addLogItem("==== 응찰자.. 없으면 오류... 무조건..있음....있어야 됨 ");
				}
				return;
			}

			addLogItem("==== --- 재경매 횟수 ====: " + mReAuctionCount);

			//응찰자가 한명 이상이면 비교 시작..
			if(mBiddingUserInfoDataList.size() > 1) {
				
				mReAuctionBidderDataList.clear();

				for(SpBidding spBidding : mBiddingUserInfoDataList) {

					if(rank_1_user.getAuctionJoinNum().equals(spBidding.getAuctionJoinNum())) {
						continue;
					}

					//1순위와 같은 가격 목록 
					if(rank_1_user.getPriceInt() == spBidding.getPriceInt()) {
						mReAuctionBidderDataList.add(spBidding);
					}
				}
				
				//동가 없으면 낙찰
				if(mReAuctionBidderDataList.size() <= 0) {
					setSuccessUser(rank_1_user);
				}else {

					//재경매 여부
					isReAuction = true;

					//1순위 넣어줌
					mReAuctionBidderDataList.add(0,rank_1_user);
					
					Platform.runLater(()->{
						//재경매중 라벨 보임.
						if(!mReAuctionLabel.isVisible()) {

							StringBuffer stringBuffer = new StringBuffer();
							stringBuffer.append(rank_1_user.getAuctionJoinNum().getValue());
							stringBuffer.append(",");
							stringBuffer.append(mReAuctionBidderDataList.stream().map(v->v.getAuctionJoinNum().getValue()).collect(Collectors.joining(",")));

							//재경매자 목록 보냄
							addLogItem("재경매 대상자 보냄 : " + AuctionDelegate.getInstance().onRetryTargetInfo(new RetryTargetInfo(this.auctionRound.getNaBzplc(), mCurrentSpEntryInfo.getEntryNum().getValue(), stringBuffer.toString())));
							
							mReAuctionLabel.setVisible(true);
						}
						//재경매 카운트
						mReAuctionCountLabel.setText(Integer.toString(mReAuctionCount));
					});
				}
				
				//음성경매시 응찰 금액 들어오면 타이머 동작 변경.
				if(SettingApplication.getInstance().isUseSoundAuction()) {
					soundAuctionTimerTask();
				}
			}else {
				setSuccessUser(rank_1_user);
			}
			
		}else {
			//동일가 재경매 설정 X
			setSuccessUser(rank_1_user);
		}
	}

	/**
	 * 1순위 정보 저장
	 * @param rank_1_user
	 */
	private void setSuccessUser(SpBidding rank_1_user) {
		mRank_1_User = rank_1_user;
		isAuctionComplete = true;
		isReAuction = false;
		mReAuctionCount = -1;
		
		Platform.runLater(() -> {
			if(mReAuctionLabel.isVisible()) {
				mReAuctionLabel.setVisible(false);
				mReAuctionCountLabel.setText("");	
			}
		});

		if(rank_1_user != null) {
			addLogItem("=== 낙찰 예정자 : " + rank_1_user.getAuctionJoinNum());
		}else {
			addLogItem("=== 낙찰 예정자 없음");
		}
		
		mBiddingInfoTableView.setDisable(true);
		
		//음성경매 중이면 자동 실행
		if(SettingApplication.getInstance().isUseSoundAuction()) {
			sendAuctionResultInfo();
		}
	}

	/**
	 * 경매 준비 뷰 초기화
	 */
	private void setAuctionVariableState(String code) {

		Platform.runLater(() -> {
			
			//버튼들
			btnToggle();

			switch (code) {

			case GlobalDefineCode.AUCTION_STATUS_READY:

				// 남은 시간 Bar 초기화
				for (int i = 0; cntList.size() > i; i++) {
					cntList.get(i).setDisable(false);
				}
				// 카운트 시간 초기화
				mRemainingTimeCount = SettingApplication.getInstance().getAuctionCountdown();
				// 현재 응찰 내역 초기화
				mCurrentBidderMap.clear();
				// 이전 응찰 내역 초기화
				mBeForeBidderDataList.clear();
				// 응찰자 초기화
				initBiddingInfoDataList();
				// 유찰(보류) 여부 초기화
				mIsPass = false;
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
				//1순위 회원
				mRank_1_User = null;
				//경매 1건 종료 여부
				isAuctionComplete = false;			
				//재경매 여부	
				isReAuction = false;
				//재경매중 라벨 숨김.
				mReAuctionLabel.setVisible(false);
				//재경매중 카운트 초기화.
				mReAuctionCountLabel.setText("");
				//카운트 다운 라벨
				mCountDownLabel.setVisible(false);
				//재경매 횟수 초기화
				mReAuctionCount = -1;
				//재경매자 목록 초기화
				mReAuctionBidderDataList.clear();
				//경매 상태 - 경매 대기
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.auction.ready"));
				//경매 시작 여부
				isStartedAuction = false;
				break;
			case GlobalDefineCode.AUCTION_STATUS_START:
			case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
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
				
				break;
			case GlobalDefineCode.AUCTION_STATUS_PASS:
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
				mLowPriceChgNtLabel.setText("");

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
	protected void updateAuctionStateInfo(boolean isSuccess, SpBidding bidder) {

		Platform.runLater(() -> {
			// REFACTOR: 경매완료 후, 경매시작 - server가 아닌 controller에서 진행하도록 변경 됨. (21.07.27)
			SpEntryInfo spEntryInfo = mWaitTableView.getSelectionModel().getSelectedItem();

			if (isSuccess) {
				spEntryInfo.setAuctionSucBidder(new SimpleStringProperty(bidder.getAuctionJoinNum().getValue()));
				spEntryInfo.setAuctionBidPrice(new SimpleStringProperty(bidder.getPrice().getValue()));
				spEntryInfo.setAuctionResult(new SimpleStringProperty(GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS));
				spEntryInfo.setAuctionBidDateTime(new SimpleStringProperty(bidder.getBiddingTime().getValue()));
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.success"));
			} else {

				spEntryInfo.setAuctionSucBidder(new SimpleStringProperty(""));
				spEntryInfo.setAuctionBidPrice(new SimpleStringProperty("0"));
				spEntryInfo.setAuctionResult(new SimpleStringProperty(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING));
				spEntryInfo.setAuctionBidDateTime(new SimpleStringProperty(""));
				mAuctionStateLabel.setText(mResMsg.getString("str.auction.state.fail"));
			}
			
			
			//상단 경매 상태 라벨 낙/유찰 에 따라 표시
			auctionStateLabelToggle(spEntryInfo.getAuctionResult().getValue());
			//현재 선택된 row 갱신
			setCurrentEntryInfo();
				
		
			PauseTransition pauseTransition = new PauseTransition(Duration.millis(1000));
			pauseTransition.setOnFinished(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					
					//경매 완료 테이블에 데이터 넣음
					addFinishedTableViewItem(spEntryInfo);
					//경매 준비 상태로 뷰들 초기화
					setAuctionVariableState(GlobalDefineCode.AUCTION_STATUS_READY);

					if(spEntryInfo.getAuctionResult().getValue().equals(GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS)) {

						//다음 번호 이동
						selectIndexWaitTable(1, false);
						
						//음성경매 && 하나씩 진행 아닌경우 
						if(SettingApplication.getInstance().isUseSoundAuction() && !SettingApplication.getInstance().isUseOneAuction()) {
							
							// 자동 시작
							PauseTransition start = new PauseTransition(Duration.millis(3000));
							start.setOnFinished(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent event) {
									onStartSoundAuction();
								}
							});
							start.play();
							
						}
						
					}else {
						addLogItem("경매 상태 유찰이거나 하나씩진행 체크 됨." + spEntryInfo.getAuctionResult().getValue() + " / " + SettingApplication.getInstance().isUseOneAuction());
					}
					
				}
			});
			pauseTransition.play();
			
		});
	}

	/**
	 * 응찰자 응찰 테이블에 업데이트
	 */
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
			mCurSuccessfulBidderLabel.setText(mCurrentSpEntryInfo.getAuctionSucBidder().getValue());
			mCurResultLabel.setText(mCurrentSpEntryInfo.getBiddingResult().getValue());
			mCurNoteLabel.setText(mCurrentSpEntryInfo.getNote().getValue());
			mLowPriceChgNtLabel.setText(mCurrentSpEntryInfo.getLwprChgNt().getValue());
			mCurWeightLabel.setText(String.format(mResMsg.getString("str.price"), Integer.parseInt(mCurrentSpEntryInfo.getWeight().getValue())));

			int price = CommonUtils.getInstance().getBaseUnitDivision(mCurrentSpEntryInfo.getLowPrice().getValue(), GlobalDefine.AUCTION_INFO.MULTIPLICATION_BIDDER_PRICE);
			int bidPrice = CommonUtils.getInstance().getBaseUnitDivision(mCurrentSpEntryInfo.getAuctionBidPrice().getValue(), GlobalDefine.AUCTION_INFO.MULTIPLICATION_BIDDER_PRICE);

			mCurLowPriceLabel.setText(String.format(mResMsg.getString("str.price"), price));
			mCurSuccessPriceLabel.setText(String.format(mResMsg.getString("str.price"), bidPrice));

			setSoundData(currentEntryInfo);
		
		});
	}

	private void setSoundData(SpEntryInfo spEntryInfo) {

		// 사운드 텍스트 저장 리스트.
		List<String> soundDataList = new ArrayList<String>();

		StringBuffer entrySoundContent = new StringBuffer();
		String EMPTY_SPACE = " ";

		if (mEntryNumCheckBox.isSelected()) {
			entrySoundContent.append(spEntryInfo.getEntryNum().getValue());
		}
		if (mExhibitorCheckBox.isSelected()) {
			entrySoundContent.append(EMPTY_SPACE);
			entrySoundContent.append(spEntryInfo.getExhibitor().getValue());
		}
		if (mGenderCheckBox.isSelected()) {
			entrySoundContent.append(EMPTY_SPACE);
			entrySoundContent.append(spEntryInfo.getGender().getValue());
		}
		if (mMotherObjNumCheckBox.isSelected()) {
			entrySoundContent.append(EMPTY_SPACE);
			entrySoundContent.append(spEntryInfo.getMotherObjNum().getValue());
		}
		if (mMaTimeCheckBox.isSelected()) {
			entrySoundContent.append(EMPTY_SPACE);
			entrySoundContent.append(spEntryInfo.getMatime().getValue());
		}
		if (mPasgQcnCheckBox.isSelected()) {
			entrySoundContent.append(EMPTY_SPACE);
			entrySoundContent.append(spEntryInfo.getPasgQcn().getValue());
		}
		if (mWeightCheckBox.isSelected()) {
			entrySoundContent.append(EMPTY_SPACE);
			entrySoundContent.append(spEntryInfo.getWeight().getValue());
		}
		if (mLowPriceCheckBox.isSelected()) {
			entrySoundContent.append(EMPTY_SPACE);
			entrySoundContent.append(spEntryInfo.getLowPrice().getValue());
		}
		if (mBrandNameCheckBox.isSelected()) {
			entrySoundContent.append(EMPTY_SPACE);
			entrySoundContent.append(spEntryInfo.getBrandName().getValue());
		}
		if (mKpnCheckBox.isSelected()) {
			entrySoundContent.append(EMPTY_SPACE);
			entrySoundContent.append(spEntryInfo.getKpn().getValue());
		}

		soundDataList.add(entrySoundContent.toString());
		mEntryInfoSound.setSoundDataList(soundDataList);
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
//						CommonUtils.getInstance().showAlertPopupTwoButton(mStage, null, null, null);
						
						//경매 진행중인 경우 취소처리
						if(mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_START)
										||mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
							sendAuctionResult(false, mCurrentSpEntryInfo, null,GlobalDefineCode.AUCTION_RESULT_CODE_CANCEL);
							mBiddingInfoTableView.setDisable(false);
							
						}else {
							//경매 진행중 아니면.. 프로그램 종료
							onCloseApplication();
						}
						
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
					
					switch (mAuctionStatus.getState()) {	
					case GlobalDefineCode.AUCTION_STATUS_START:
					case GlobalDefineCode.AUCTION_STATUS_PROGRESS:	//경매 진행중에 눌림

						// 강제낙찰
						if (ke.getCode() == KeyCode.F6) {
							onSuccessAuction();
							ke.consume();
						}
						// 강제유찰?
//						if (ke.getCode() == KeyCode.F7) {
//							
//							onPassAuction();
//							ke.consume();
//						}

						if (ke.getCode() == KeyCode.DIGIT1) {
							sendCountDown(1);
							ke.consume();
						}

						if (ke.getCode() == KeyCode.DIGIT2) {
							sendCountDown(2);
							ke.consume();
						}
						if (ke.getCode() == KeyCode.DIGIT3) {
							sendCountDown(3);
							ke.consume();
						}

						if (ke.getCode() == KeyCode.DIGIT4) {
							sendCountDown(4);
							ke.consume();
						}
						if (ke.getCode() == KeyCode.DIGIT5) {
							sendCountDown(5);
							ke.consume();
						}
						if (ke.getCode() == KeyCode.DIGIT6) {
							sendCountDown(6);
							ke.consume();
						}
						if (ke.getCode() == KeyCode.DIGIT7) {
							sendCountDown(7);
							ke.consume();
						}
						if (ke.getCode() == KeyCode.DIGIT8) {
							sendCountDown(8);
							ke.consume();
						}
						if (ke.getCode() == KeyCode.DIGIT9) {
							sendCountDown(9);
							ke.consume();
						}
						
						
						break;
						default:	//경매 진행중에 눌리지 않음.
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

							// 환경설정
							if (ke.getCode() == KeyCode.F8) {
								// 환경설정
								openSettingDialog();
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
							
							break;
					
					}

					// 경매 시작
					if (ke.getCode() == KeyCode.ENTER) {

						System.out.println("[KeyCode.ENTER]=> " + mAuctionStatus.getState());

						onStartAndStopAuction(0);
						
						ke.consume();
					}
					// 음성 경매 시작
					if (ke.getCode() == KeyCode.SPACE) {
						
						System.out.println("[KeyCode.ENTER]=> " + mAuctionStatus.getState());

						onStartSoundAuction();

						ke.consume();
					}
				}
			});
		});
	}
	
	/**
	 * 키패드 카운트 다운
	 * @param countDown
	 */
	private void sendCountDown(int countDown) {
		if(isCountDownRunning) {
			return;
		}
		onStopAuction(countDown);
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
	 * 완료된 출품 정보 조회 EntryNum 없는 dummy row 제외
	 *
	 * @return
	 */
	public ObservableList<SpEntryInfo> getFinishedEntryInfoDataList() {

		ObservableList<SpEntryInfo> dataList = FXCollections.observableArrayList();

		for (SpEntryInfo spEntryInfo : mFinishedEntryInfoDataList) {
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
	 *
	 * @param strProperty
	 * @return
	 */
	public boolean isEmptyProperty(StringProperty strProperty) {

		if (strProperty != null && !strProperty.getValue().equals("")) {
			return false;
		} else {
			return true;
		}
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

				if (value != null && !value.isEmpty() && !value.isBlank()) {

					int reValue = 0;

					// 응찰금액 기준 만원 적용시 주석 해제
					if (isPrice) {
						reValue = CommonUtils.getInstance().getBaseUnitDivision(value, GlobalDefine.AUCTION_INFO.MULTIPLICATION_BIDDER_PRICE);
					} else {
					reValue = Integer.parseInt(value);
					}

					setText(CommonUtils.getInstance().getNumberFormatComma(reValue));
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
	 * @return
	 */
	private boolean isBidderPriceValid() {
		
		//응찰 테이블 뷰
		if(CommonUtils.getInstance().isListEmpty(mBiddingUserInfoDataList)) {
			//응찰 내역 없음.
			addLogItem("==== 응찰 내역 없음. ====");
			return false;
		}

		//1순위 회원
		SpBidding rank_1_user = mBiddingUserInfoDataList.get(0);

		//최저가 / 응찰가 비교.
		//1순위가 최저가보다 미만 팝업.
		if(!checkLowPrice(rank_1_user)) {
			//최저가격 이하는 낙찰이 불가능 합니다.
			if(!SettingApplication.getInstance().isUseSoundAuction()) {
				Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("str.auction.low.price.valid")));
			}
			return false;
		}
		
		//최저가 / 응찰가 비교.
		//1순위가 최저가보다 미만 팝업.
		if(!checkOverPrice(rank_1_user)) {
			//최저가격 이하는 낙찰이 불가능 합니다.
			if(!SettingApplication.getInstance().isUseSoundAuction()) {
				Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("str.auction.over.price.valid")));
			}
			return false;
		}
		
		return true;
	}
	
	/**
	 * 경매 상태에 따라 버튼 동작
	 */
	private void btnToggle() {
		
		Platform.runLater(() ->{
			
			// 모든 상태 정보. 출품 정보 보내기 버튼 비활성화
			mBtnF1.setDisable(true);
			// 경매 시작 버튼 활성화
			if(!SettingApplication.getInstance().isUseSoundAuction()) {
				mBtnEnter.setDisable(false);
				mBtnSpace.setDisable(true);
			}else {
				mBtnEnter.setDisable(true);
				mBtnSpace.setDisable(false);
			}
		
			switch (mAuctionStatus.getState()) {
			case GlobalDefineCode.AUCTION_STATUS_START:
			case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
				
				if(!SettingApplication.getInstance().isUseSoundAuction()) {
					// ENTER 경매시작 -> 경매완료 변경
					mBtnEnter.setText(mResMsg.getString("str.btn.stop"));
					// ENTER 경매완료 css 적용
					CommonUtils.getInstance().addStyleClass(mBtnEnter, "btn-auction-stop");
				}else {
					CommonUtils.getInstance().addStyleClass(mBtnSpace, "bg-color-04cf5c");
				}
				
				// 강제 유찰 버튼 비활성화
				mBtnF3.setDisable(true);
				// 전체보기 비활성화
				mBtnF4.setDisable(true);
				// 보류보기 비활성화
				mBtnF5.setDisable(true);
				// 강제 낙찰 버튼 활성화
				mBtnF6.setDisable(false);
				// 강제 유찰 버튼 활성화
//				mBtnF7.setDisable(false);
				// 강제 유찰 버튼 비활성화
				mBtnF8.setDisable(true);
				// 가격 상승,다운 비활성화
				mBtnUpPrice.setDisable(true);
				mBtnDownPrice.setDisable(true);
				// 출품 대기 테이블 비활성화
				mWaitTableView.setDisable(true);

				break;
				default:
					
					if(!SettingApplication.getInstance().isUseSoundAuction()) {
						// ENTER 경매 시작으로.
						mBtnEnter.setText(mResMsg.getString("str.btn.start"));
						CommonUtils.getInstance().removeStyleClass(mBtnEnter, "btn-auction-stop");
					}else {
						CommonUtils.getInstance().removeStyleClass(mBtnSpace, "bg-color-04cf5c");
					}
					// 강제 유찰 버튼 활성화
					mBtnF3.setDisable(false);
					// 전체보기 활성화
					mBtnF4.setDisable(false);
					// 보류보기 활성화
					mBtnF5.setDisable(false);
					// 강제 낙찰 버튼 비활성화
					mBtnF6.setDisable(true);
					// 강제 유찰 버튼 비활성화
//					mBtnF7.setDisable(true);
					// 강제 유찰 버튼 활성화
					mBtnF8.setDisable(false);
					// 가격 상승,다운 활성화
					mBtnUpPrice.setDisable(false);
					mBtnDownPrice.setDisable(false);
					
					
					break;
			}
		});
	}
	
	/**
	 * 카운트 다운 완료시 강제낙찰/강제유찰/경매완료 버튼 활성/비활성화
	 * @param tf
	 */
	private void btnStopAuctionToggle(boolean tf) {
		if(!SettingApplication.getInstance().isUseSoundAuction()) {
			mBtnEnter.setDisable(tf);
		}else {
			mBtnSpace.setDisable(tf);
		}
		mBtnF6.setDisable(tf);
//		mBtnF7.setDisable(tf);
	}
	
	/**
	 * 상단 경매 진행 상태
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
}
