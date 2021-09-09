package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.NettyControllable;
import com.nh.controller.controller.SettingController.AuctionToggle;
import com.nh.controller.interfaces.BooleanListener;
import com.nh.controller.model.AuctionRound;
import com.nh.controller.model.AuctionStnData;
import com.nh.controller.model.SelStsCountData;
import com.nh.controller.model.SpAuctionStnData;
import com.nh.controller.netty.AuctionDelegate;
import com.nh.controller.netty.BillboardDelegate;
import com.nh.controller.netty.PdpDelegate;
import com.nh.controller.service.AuctionRoundMapperService;
import com.nh.controller.service.EntryInfoMapperService;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.AuctionUtil;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.SharedPreference;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionResult;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.CancelBidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.InitEntryInfo;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.RequestAuctionResult;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.ToastMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * 일괄 경매
 *
 * @author jhlee
 */
public class MultipleAuctionController implements Initializable, NettyControllable {

	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private Stage mStage = null;

	private ResourceBundle mResMsg = null;

	@FXML
	private TableView<SpAuctionStnData> mAuctionTableView;

	@FXML // 경매 대상 컬럼 - No
	private TableColumn<SpAuctionStnData, Void> mNoColumn;

	@FXML // 경매 대상 컬럼 - No,일련번호,경매시작번호,경매종료번호,차수 경매대상,진행상태,조회,시작,정지,종료
	private TableColumn<SpAuctionStnData, String> mSelStsDscColumn, mTotalCountColumn, mSendColumn, mProgressCountColumn, mObjDscColumn, mRgSqNoColumn, mStAucNoColumn, mEdAucNoColumn, mDdlQcnColumn;

	@FXML
	private Button mBtnEsc,mBtnF1,mBtnF8;

	@FXML
	private Label mCntCalfLabel, mCntFatteningCattleLabel, mCntBreedingCattleLabel;

	private ObservableList<SpAuctionStnData> mAuctionInfoDataList = FXCollections.observableArrayList(); // 경매 대상 목록
	
	private List<AuctionRound> mAucRoundDataList;
	
	private boolean isApplicationClosePopup = false;

	/**
	 * setStage
	 *
	 * @param stage
	 */
	public void setStage(Stage stage) {
		mStage = stage;

		// connection server
		Thread thread = new Thread("server") {
			@Override
			public void run() {

				Platform.runLater(() -> { CommonUtils.getInstance().showLoadingDialog(stage, mResMsg.getString("msg.connection"));});

				createClient(GlobalDefine.AUCTION_INFO.AUCTION_HOST, GlobalDefine.AUCTION_INFO.AUCTION_PORT);
			}
		};
		thread.setDaemon(true);
		thread.start();

	}

	/**
	 * 구성 설정
	 */
	public void initConfiguration() {
		CommonUtils.getInstance().canMoveStage(mStage, null);
		initKeyConfig();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// get ResMsg
		if (resources != null) {
			mResMsg = resources;
		}
		// 테이블 뷰 설정
		initTableConfiguration();
		
		
		mBtnF1.setOnMouseClicked(event -> onSendEntryInfo());
		mBtnF8.setOnMouseClicked(event -> openSettingDialog());
	}

	/**
	 * 소켓 서버 접속
	 */
	protected void createClient(String host, int port) {
		AuctionDelegate.getInstance().createClients(GlobalDefine.AUCTION_INFO.AUCTION_HOST, GlobalDefine.AUCTION_INFO.AUCTION_PORT, GlobalDefine.ADMIN_INFO.adminData.getUserId(), "N", (NettyControllable) this);
	}

	// 테이블 뷰 설정
	private void initTableConfiguration() {

		// Row Select Disable
		mAuctionTableView.setSelectionModel(null);
		// 셀높이,헤더높이,마진
		tableHeightHelper(mAuctionTableView, 80, 80, 33);

		// 경매대상
		mObjDscColumn.setCellValueFactory(cellData -> cellData.getValue().getAucObjDsc());
		// 일련번호
		mRgSqNoColumn.setCellValueFactory(cellData -> cellData.getValue().getRgSqno());
		// 경매시작번호
		mStAucNoColumn.setCellValueFactory(cellData -> cellData.getValue().getStAucNo());
		// 경매종료번호
		mEdAucNoColumn.setCellValueFactory(cellData -> cellData.getValue().getEdAucNo());
		// 진행상태
		mSelStsDscColumn.setCellValueFactory(cellData -> cellData.getValue().getSelStsDsc());
		// 차수
		mDdlQcnColumn.setCellValueFactory(cellData -> cellData.getValue().getDdlQcn());

		mSendColumn.setCellValueFactory(cellData -> cellData.getValue().getAucObjDsc());

		mTotalCountColumn.setCellValueFactory(cellData -> cellData.getValue().getTotalCount());

		mProgressCountColumn.setCellValueFactory(cellData -> cellData.getValue().getProgressCount());

		// 순번
		mNoColumn.setCellFactory(cellData -> {
			TableCell<SpAuctionStnData, Void> cell = new TableCell<>();
			cell.textProperty().bind(Bindings.createStringBinding(() -> {
				if (cell.isEmpty()) {
					return null;
				} else {
					return Integer.toString(cell.getIndex() + 1);
				}
			}, cell.emptyProperty(), cell.indexProperty()));
			return cell;
		});

		// 경매대상(송아지,비육우,번식우)
		mObjDscColumn.setCellFactory(col -> new TableCell<SpAuctionStnData, String>() {
			@Override
			protected void updateItem(String value, boolean empty) {
				super.updateItem(value, empty);
				if (!empty) {
					setText(AuctionUtil.AucObjDsc.which(value));
				} else {
					setText("");
				}
			}
		});

		// 경매상태 (대기,진행,종료)
		mSelStsDscColumn.setCellFactory(col -> new TableCell<SpAuctionStnData, String>() {
			@Override
			protected void updateItem(String value, boolean empty) {
				super.updateItem(value, empty);
				if (!empty) {

					String strSelSts = "";

					if (value.equals(GlobalDefineCode.STN_AUCTION_STATUS_NONE)) {
						strSelSts = "대기";
					} else if (value.equals(GlobalDefineCode.STN_AUCTION_STATUS_PROGRESS)) {
						strSelSts = "진행";
					} else if (value.equals(GlobalDefineCode.STN_AUCTION_STATUS_FINISH)) {
						strSelSts = "종료";
					}

					setText(strSelSts);

				} else {
					setText("");
				}
			}
		});

//		mSendColumn.setCellFactory(col -> new TableCell<SpAuctionStnData, String>() {
//
//			Button startButton = new Button("전송");
//
//			@Override
//			protected void updateItem(String value, boolean empty) {
//				super.updateItem(value, empty);
//				setText(null);
//				startButton.setOnAction(btnSearchClickListener);
//				startButton.setUserData(value);
//				setGraphic(startButton);
//			}
//		});
//
//		mStartColumn.setCellFactory(col -> new TableCell<SpAuctionStnData, String>() {
//
//			Button startButton = new Button("응찰시작");
//
//			@Override
//			protected void updateItem(String value, boolean empty) {
//				super.updateItem(value, empty);
//				setText(null);
//				startButton.setOnAction(btnStartClickListener);
//				startButton.setUserData("1");
//				setGraphic(startButton);
//			}
//		});
//
//		mFinishColumn.setCellFactory(col -> new TableCell<SpAuctionStnData, String>() {
//
//			Button startButton = new Button("응찰종료");
//
//			@Override
//			protected void updateItem(String value, boolean empty) {
//				super.updateItem(value, empty);
//				setText(null);
//				startButton.setOnAction(btnFinishClickListener);
//				startButton.setUserData("1");
//				setGraphic(startButton);
//			}
//		});

	}

	/**
	 * 경매 데이터 DB 조회
	 */
	private void initAuctionInfo() {

		Platform.runLater(() -> {

			AuctionRound auctionRoundParam = new AuctionRound();
			auctionRoundParam.setNaBzplc(GlobalDefine.ADMIN_INFO.adminData.getNabzplc());
			auctionRoundParam.setAucDt("20210825");

			// 경매 회차정보 조회
			List<AuctionRound> aucRoundDataList = requestAuctionRound(auctionRoundParam);

			if (!CommonUtils.getInstance().isListEmpty(aucRoundDataList)) {

				AuctionStnData auctionStnDataParam = new AuctionStnData();
				auctionStnDataParam.setNaBzplc(auctionRoundParam.getNaBzplc());
				auctionStnDataParam.setAucDt(auctionRoundParam.getAucDt());
				auctionStnDataParam.setAucObjDsc("0");

				// 경매 대상 목록 조회
				List<AuctionStnData> aucStnDataList = requestAuctionStn(auctionStnDataParam);

				if (!CommonUtils.getInstance().isListEmpty(aucStnDataList)) {

					aucStnDataList.forEach(item -> {

						// 경매 대상별 카운팅
						SelStsCountData selStsCountData = requestSelStsCount(item);
						// 경매 대상별 카운팅
						setAuctionObjDscSelStsCount(item.getAucObjDsc(), selStsCountData);

						if (!item.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_FINISH)) {

							AuctionStnData entryInfoParam = item.clone();
							// 경매 종료가 아닌 데이터 가져오기 위해 Finish로 설정.
							entryInfoParam.setSelStsDsc(GlobalDefineCode.STN_AUCTION_STATUS_FINISH);
							// 경매 데이터 조회
							List<EntryInfo> resultDataList = requestEntryInfo(entryInfoParam);
							
							mLogger.debug("[################### ] aucObj : " + entryInfoParam.getAucObjDsc()  + " / "+ resultDataList.size());

							item.setEntryInfoDataList(resultDataList);

							item.setTotalCount(Integer.toString(selStsCountData.getTotalCount()));

							item.setProgressCount(Integer.toString(selStsCountData.getSelStsProgress()));
						}

					});

					// Convert List
					ObservableList<SpAuctionStnData> spAucStnDataList = aucStnDataList.stream().map(item -> new SpAuctionStnData(item)).collect(Collectors.toCollection(FXCollections::observableArrayList));

					mAuctionTableView.setItems(spAucStnDataList);
				}

				mAucRoundDataList = aucRoundDataList;
			} else {
				// 경매 데이터 존재하지 않음.
				CommonUtils.getInstance().showAlertPopupOneButton(mStage, mResMsg.getString("dialog.auction.no.data"), mResMsg.getString("popup.btn.ok"));
			}
		});

		CommonUtils.getInstance().dismissLoadingDialog();
	}

	/**
	 * 경매 회차 정보 조회
	 */
	private List<AuctionRound> requestAuctionRound(AuctionRound auctionRound) {

		List<AuctionRound> auctionRoundDataList = null;

		try {
			auctionRoundDataList = AuctionRoundMapperService.getInstance().getAllAuctionRoundData(auctionRound);
		} catch (Exception e) {
			mLogger.debug("[Exception]" + e.toString());
			return new ArrayList<AuctionRound>();
		}

		return auctionRoundDataList;
	}

	/**
	 * 경매 정보 조회
	 */
	private List<AuctionStnData> requestAuctionStn(AuctionStnData auctionStnData) {

		List<AuctionStnData> auctionStnDataList = null;

		try {
			auctionStnDataList = AuctionRoundMapperService.getInstance().searchAuctionStnData(auctionStnData);
		} catch (Exception e) {
			mLogger.debug("[Exception]" + e.toString());
			return new ArrayList<AuctionStnData>();
		}

		return auctionStnDataList;
	}

	/**
	 * 경매 소 조회
	 */
	private List<EntryInfo> requestEntryInfo(AuctionStnData auctionStnData) {

		List<EntryInfo> auctionStnDataList = EntryInfoMapperService.getInstance().getStnEntryData(auctionStnData);

		return auctionStnDataList;
	}

	/**
	 * 구분별 카운트
	 * 
	 * @param auctionStnData
	 * @return
	 */
	private SelStsCountData requestSelStsCount(AuctionStnData auctionStnData) {

		SelStsCountData selStsCountData = EntryInfoMapperService.getInstance().getSelStsCount(auctionStnData);

		return selStsCountData;
	}

	private final EventHandler<ActionEvent> btnSearchClickListener = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent arg0) {
			mLogger.debug("[btnSearchClickListener] " + arg0.getSource());

			if (arg0.getSource() instanceof Button) {
				Button button = (Button) arg0.getSource();
				mLogger.debug("[btnSearchClickListener] " + button.getUserData().toString());

				int index = Integer.parseInt(button.getUserData().toString()) - 1;

				if (index <= mAuctionTableView.getItems().size()) {
					if (!CommonUtils.getInstance().isListEmpty(mAuctionTableView.getItems().get(index).getEntryInfoDataList())) {
						System.out.println(" siaeeee : " + mAuctionTableView.getItems().get(index).getEntryInfoDataList().size());
					}

				}
			}
		}
	};

	private final EventHandler<ActionEvent> btnStartClickListener = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent arg0) {
			mLogger.debug("[btnStartClickListener]");
		}
	};

	private final EventHandler<ActionEvent> btnFinishClickListener = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent arg0) {
			mLogger.debug("[btnFinishClickListener]");
		}
	};

	/**
	 * 구분별 카운트
	 * 
	 * @param aucObjDsc
	 * @param selStsCountData
	 */
	private void setAuctionObjDscSelStsCount(String aucObjDsc, SelStsCountData selStsCountData) {

		if (selStsCountData == null) {
			return;
		}

		Platform.runLater(() -> {

			final String strCounts = String.format(mResMsg.getString("str.sel.sts.cnt"), selStsCountData.getTotalCount(), selStsCountData.getSelStsProgress(), selStsCountData.getSelStsReady(), selStsCountData.getSelStsAuction(), selStsCountData.getSelStsFinish(), selStsCountData.getSelStsPending());

			switch (Integer.parseInt(aucObjDsc)) {
			case GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_1:
				mCntCalfLabel.setText(strCounts);
				break;
			case GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_2:
				mCntFatteningCattleLabel.setText(strCounts);
				break;
			case GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_3:
				mCntBreedingCattleLabel.setText(strCounts);
				break;
			}
		});
	}

	/**
	 * 출품 일괄 전송
	 */
	public void onSendEntryInfo() {

		List<EntryInfo> resultDataList = new ArrayList<EntryInfo>();

		// 경매 진행 여부
		boolean isAuctionProgress = false;

		for (SpAuctionStnData spAuctionStnData : mAuctionTableView.getItems()) {

			if (!spAuctionStnData.getSelStsDsc().equals(GlobalDefineCode.STN_AUCTION_STATUS_FINISH)) {
				
				if (!CommonUtils.getInstance().isListEmpty(spAuctionStnData.getEntryInfoDataList())) {
				
					resultDataList.addAll(spAuctionStnData.getEntryInfoDataList());
					
					isAuctionProgress = true;

					if(!CommonUtils.getInstance().isListEmpty(spAuctionStnData.getEntryInfoDataList())) {
						for(AuctionRound auctionRound : mAucRoundDataList) {
							if(spAuctionStnData.getAucObjDsc().getValue().equals(Integer.toString(auctionRound.getAucObjDsc()))){
								AuctionDelegate.getInstance().onInitEntryInfo(new InitEntryInfo(auctionRound.getNaBzplc(), Integer.toString(auctionRound.getQcn())));
								System.out.println("[QCN 초기화 요청]=> " + auctionRound.getNaBzplc() + " / " + Integer.toString(auctionRound.getQcn()));
								break;
							}
						}
						
					}
					
				}
			}
		}

		if (isAuctionProgress && !CommonUtils.getInstance().isListEmpty(resultDataList)) {

			// 마지막 출품정보 표기 (Y/N) & 데이터 전송
			for (int i = 0; i < resultDataList.size(); i++) {
				String flag = (i == resultDataList.size() - 1) ? "Y" : "N";
				resultDataList.get(i).setIsLastEntry(flag);
				AuctionDelegate.getInstance().onSendEntryData(resultDataList.get(i));
				System.out.println("[출품 정보 전송]=> " + resultDataList.get(i).getEntryNum() + " / " + resultDataList.get(i).getIsLastEntry());
			}
		}
	}

	@Override
	public void onActiveChannel(Channel channel) {
		// 제어프로그램 접속 정보 전송
		mLogger.debug(mResMsg.getString("msg.auction.send.connection.info") + AuctionDelegate.getInstance().onSendConnectionInfo());
	}

	@Override
	public void onActiveChannel() {
	}

	@Override
	public void onAuctionStatus(AuctionStatus auctionStatus) {
		mLogger.debug("onAuctionStatus : " + auctionStatus.getEncodedMessage());
	}

	@Override
	public void onCurrentEntryInfo(CurrentEntryInfo currentEntryInfo) {
		mLogger.debug("onCurrentEntryInfo : " + currentEntryInfo.getEncodedMessage());
	}

	@Override
	public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
		mLogger.debug("onAuctionCountDown : " + auctionCountDown.getEncodedMessage());
	}

	@Override
	public void onBidding(Bidding bidding) {
		mLogger.debug("bidding : " + bidding.getEncodedMessage());
	}

	@Override
	public void onCancelBidding(CancelBidding cancelBidding) {
		mLogger.debug("onCancelBidding : " + cancelBidding.getEncodedMessage());
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
		mLogger.debug("onAuctionResult : " + auctionResult.getEncodedMessage());
	}

	@Override
	public void onConnectionInfo(ConnectionInfo connectionInfo) {
		mLogger.debug("onConnectionInfo : " + connectionInfo.getEncodedMessage());
	}

	@Override
	public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo) {
		mLogger.debug(responseConnectionInfo.getEncodedMessage());

		Platform.runLater(() -> {

			switch (responseConnectionInfo.getResult()) {
			case GlobalDefineCode.CONNECT_SUCCESS:
				mLogger.debug(mResMsg.getString("msg.connection.success") + responseConnectionInfo.getEncodedMessage());
				initAuctionInfo();
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
	}

	@Override
	public void onRequestAuctionResult(RequestAuctionResult requestAuctionResult) {
		mLogger.debug("onResponseCode : " + requestAuctionResult.getEncodedMessage());
	}

	@Override
	public void onConnectionException(int port) {
		Platform.runLater(()->{
			CommonUtils.getInstance().dismissLoadingDialog();
		});
	
	}

	@Override
	public void onChannelInactive(int port) {
		mLogger.debug("onChannelInactive : " + port);

	}

	@Override
	public void exceptionCaught(int port) {
		mLogger.debug("exceptionCaught : " + port);
	}

	@Override
	public void onCheckSession(ChannelHandlerContext ctx, AuctionCheckSession auctionCheckSession) {
		AuctionDelegate.getInstance().onSendCheckSession();
	}

	/**
	 * 키 설정
	 */
	private void initKeyConfig() {

		Platform.runLater(() -> {
			mStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

				public void handle(KeyEvent ke) {

					if (ke.getCode() == KeyCode.ENTER) {
						ke.consume();
					}

					if (ke.getCode() == KeyCode.ESCAPE) {
						Platform.exit();
						System.exit(0);
						ke.consume();
					}

					if (ke.getCode() == KeyCode.F1) {
						onSendEntryInfo();
						ke.consume();
					}

				}
			});
		});
	}

	/**
	 * table view 높이설정
	 * 
	 * @param table        대상 테이블
	 * @param rowHeight    셀 높이
	 * @param headerHeight 헤더 높이
	 * @param margin       마진
	 */
	public void tableHeightHelper(TableView<?> table, int rowHeight, int headerHeight, int margin) {
		table.prefHeightProperty().bind(Bindings.max(1, Bindings.size(table.getItems())).multiply(rowHeight).add(headerHeight).add(margin));
		table.minHeightProperty().bind(table.prefHeightProperty());
		table.maxHeightProperty().bind(table.prefHeightProperty());
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

				if (isClose) {
				
					if(SettingApplication.getInstance().isSingleAuction()){
						//단일경매로 변경된 경우 현재창 종료 후 일괄경매 이동
						onCloseApplication();
						return;
					}
				}
			}
		});
	}
	
	/**
	 * 프로그램 종료
	 */
	public void onCloseApplication() {

		Platform.runLater(() -> {

			Optional<ButtonType> btnResult = CommonUtils.getInstance().showAlertPopupTwoButton(mStage, mResMsg.getString("str.ask.application.close"), mResMsg.getString("popup.btn.ok"), mResMsg.getString("popup.btn.cancel"));

			if (btnResult.get().getButtonData() == ButtonData.LEFT) {

				CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.app.closeing"));

				isApplicationClosePopup = true;

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
			}else {
				
				if(SettingApplication.getInstance().isSingleAuction()){
					//환경설정 -> 일괄경매 변경 -> 팝업 -> 취소시 다시 단일로 설정
					SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_AUCTION_TOGGLE_TYPE, AuctionToggle.MULTI.toString());
					SettingApplication.getInstance().initSharedData();
				}
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
