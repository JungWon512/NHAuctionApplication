package com.nh.controller.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nh.common.AuctionShareNettyClient;
import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.NettyControllable;
import com.nh.controller.preferences.SharedPreference;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.share.api.model.AuctionGenerateInformationResult;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionReponseSession;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.controller.models.RequestLogout;
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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * 
 * @ClassName AuctionConnectController.java
 * @Description 경매 접속자 모티너링 컨트롤러 클래스
 * @since 2019.11.25
 */
public class AuctionConnectController implements NettyControllable, Initializable {
	private final Logger mLogger = LogManager.getLogger(AuctionConnectController.class);

	private final int GRID_MAX_COLUM = 20;

	private Stage mStage;

	@FXML
	private AnchorPane mRootAnchorPane;
	@FXML
	private GridPane mRootGridPane;
	@FXML
	private GridPane mConnectorGridPane;
	@FXML
	private AnchorPane mGrdViewCellAnchorPane;
	@FXML
	private ScrollPane mScrollPane;
	@FXML
	private Label mAuctionTypeLabel;
	@FXML
	private Label mAuctionLaneNameLabel;
	@FXML
	private Label mAuctionNameLabel;
	@FXML
	private Label mConnectBidderCountLabel;
	@FXML
	private Label mBiddingCountLabel;
	@FXML
	private Label mConnectAhCountLabel;
	@FXML
	private Label mConnectPcCountLabel;
	@FXML
	private Label mConnectANDCountLabel;
	@FXML
	private Label mConnectiOSCountLabel;
	@FXML
	private Button mStartMonitorButton;

	private AuctionShareNettyClient mClients;

	private int mColumIndex = 0;
	private int mRowIndex = 0;

	private GridPane mGridPane;

	private Map<String, BidderConnectInfo> mBidderConnectInfoMap = new LinkedHashMap<String, BidderConnectInfo>();
	private HashMap<String, AuctionConnectGridCellController> mGridCellMap = new HashMap<String, AuctionConnectGridCellController>();

	private HashMap<String, Boolean> mBiddingBidderMap = new HashMap<String, Boolean>();

	private ScheduledExecutorService mService = Executors.newScheduledThreadPool(1); // 현재 동작하는 작업 스레드 풀
	private ScheduledFuture<?> mResetTimeJob;
	private ScheduledFuture<?> mConnectInfoJob; // 경매 접속 정보 전송 처리 Job
	private AuctionGenerateInformationResult mItem;

	private int mBidderCount = 0;

	private boolean mIsStartMonitor = false;
	private boolean isBtnClose = false;
	private boolean isAuctionFinished = false;

	private boolean isShowAlertPopup = false;
	private Dialog<ButtonType> mAlertDialog = new Dialog<>();

	private ResourceBundle mCurrentResources;

	public AuctionConnectController() {

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		mCurrentResources = resources;
	}

	public void setStage(Stage stage, AuctionGenerateInformationResult item) {
		mStage = stage;
		mItem = item;
		mStage.setTitle(mCurrentResources.getString("str.auction.connector.monitoring"));

		mStage.setOnCloseRequest(event -> {
			isBtnClose = true;
			closeStage();
		});

		initDefaultInfo();
		initGridView();
	}

	private void initDefaultInfo() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (mItem.getAuctionCode().equals(GlobalDefineCode.AUCTION_TYPE_REALTIME)) {
					mAuctionTypeLabel.setText(mCurrentResources.getString("str.auction.realtime"));
				} else if (mItem.getAuctionCode().equals(GlobalDefineCode.AUCTION_TYPE_SPOT)) {
					mAuctionTypeLabel.setText(mCurrentResources.getString("str.auction.spot"));
				}

				mAuctionLaneNameLabel.setText(mItem.getAuctionLaneName());
				mAuctionNameLabel.setText(mItem.getAuctionName() + " - " + mItem.getAuctionRound()
						+ mCurrentResources.getString("str.auction.round"));
			}
		});
	}

	private void initGridView() {
		mGridPane = new GridPane();
		mGridPane.setPadding(new Insets(10));
		mGridPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		mGridPane.setHgap(10);
		mGridPane.setVgap(10);

		mGridPane.prefWidthProperty().bind(mScrollPane.widthProperty());

		mColumIndex = 0;
		mRowIndex = 0;

		mGridPane.setAlignment(Pos.BASELINE_LEFT);
		mScrollPane.setContent(mGridPane);
	}

	private void addGridView(BidderConnectInfo bidderConnectInfo) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				FXMLLoader fxmlLoader;
				AnchorPane cellLayout;

				try {
					fxmlLoader = MoveStageUtil.getInstance().getGridCellFXMLLoader();
					cellLayout = (AnchorPane) fxmlLoader.load();
					AuctionConnectGridCellController auctionConnectGridCellController = fxmlLoader.getController();

					if (!mGridCellMap.containsKey(bidderConnectInfo.getUserNo())) {
						mGridCellMap.put(bidderConnectInfo.getUserNo(), auctionConnectGridCellController);

						if (mColumIndex < GRID_MAX_COLUM) {
							mGridPane.add(cellLayout, mColumIndex, mRowIndex);
							mColumIndex++;
						} else {
							mColumIndex = 0;
							mRowIndex++;
							mGridPane.add(cellLayout, mColumIndex, mRowIndex);
							mColumIndex++;
						}

						mGridCellMap.get(bidderConnectInfo.getUserNo()).setConnectData(bidderConnectInfo.getUserNo(),
								bidderConnectInfo.getOS());
						mGridCellMap.get(bidderConnectInfo.getUserNo()).setBidding(false);
					}

					refreshConnectCount();
				} catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
	}

	private void removeGridView(BidderConnectInfo bidderConnectInfo) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (mGridCellMap.containsKey(bidderConnectInfo.getUserNo())) {
					mGridCellMap.get(bidderConnectInfo.getUserNo()).setDisconnect(bidderConnectInfo.getUserNo(),
							bidderConnectInfo.getOS());
					mGridCellMap.remove(bidderConnectInfo.getUserNo());
				}

				refreshConnectCount();
			}
		});
	}

	private void onRefreshView() {
		if (isShowAlertPopup) {
			return;
		}

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				FXMLLoader fxmlLoader;
				AnchorPane cellLayout;

				initGridView();

				mGridCellMap.clear();

				try {

					for (String key : mBidderConnectInfoMap.keySet()) {

						if (!mGridCellMap.containsKey(mBidderConnectInfoMap.get(key).getUserNo())) {

							fxmlLoader = MoveStageUtil.getInstance().getGridCellFXMLLoader();
							cellLayout = (AnchorPane) fxmlLoader.load();
							AuctionConnectGridCellController auctionConnectGridCellController = fxmlLoader
									.getController();

							mGridCellMap.put(mBidderConnectInfoMap.get(key).getUserNo(),
									auctionConnectGridCellController);

							cellLayout.getStyleClass().add("userNo_" + mBidderConnectInfoMap.get(key).getUserNo());
							cellLayout.setOnMouseClicked(event -> {
								if (event.getButton() != MouseButton.SECONDARY && event.getClickCount() == 1) {
									onLogout(event);
								}
							});

							if (mColumIndex < GRID_MAX_COLUM) {
								mGridPane.add(cellLayout, mColumIndex, mRowIndex);
								mColumIndex++;
							} else {
								mColumIndex = 0;
								mRowIndex++;
								mGridPane.add(cellLayout, mColumIndex, mRowIndex);
								mColumIndex++;
							}

							mGridCellMap.get(mBidderConnectInfoMap.get(key).getUserNo()).setConnectData(
									mBidderConnectInfoMap.get(key).getUserNo(), mBidderConnectInfoMap.get(key).getOS());
							mGridCellMap.get(mBidderConnectInfoMap.get(key).getUserNo()).setBidding(false);
						}

					}
					refreshConnectCount();
				} catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
	}

	@FXML
	private void onRefreshGridView() {

		TreeMap<String, BidderConnectInfo> treeMap = new TreeMap<String, BidderConnectInfo>(mBidderConnectInfoMap);

		if (treeMap != null && !treeMap.isEmpty()) {
			mBidderConnectInfoMap.clear();
			mBidderConnectInfoMap.putAll(treeMap);
			onRefreshView();
		}
	}

	private void onLogout(MouseEvent event) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Node node = (Node) event.getSource();
				String userNo = "";

				for (int i = 0; node.getStyleClass().size() > i; i++) {
					if (node.getStyleClass().get(i).contains("userNo_")) {
						String[] arr = node.getStyleClass().get(i).split("_");
						userNo = arr[1];
						break;
					}
				}

				String message = mCurrentResources.getString("str.logout1") + userNo
						+ mCurrentResources.getString("str.logout2");
				Optional<ButtonType> btnResult = CommonUtils.getInstance().showAlertPopupTwoButton(mStage, message,
						mCurrentResources.getString("str.ok"), mCurrentResources.getString("str.cancel"));
				if (btnResult.get().getButtonData() == ButtonData.LEFT) {
					mLogger.debug("onLogout : " + userNo);
					mClients.sendMessage(new RequestLogout(userNo));
				}
				if (btnResult.get().getButtonData() == ButtonData.RIGHT) {
				}
			}
		});
	}

	private void setBidding(BidderConnectInfo bidderConnectInfo) {
		if (mGridCellMap.containsKey(bidderConnectInfo.getUserNo())) {
			if (mBiddingBidderMap.containsKey(bidderConnectInfo.getUserNo())) {
				if (mBiddingBidderMap.get(bidderConnectInfo.getUserNo()) == true) {
					mBiddingBidderMap.put(bidderConnectInfo.getUserNo(), false);
//                    refreshBiddingCount(true);
				}
			}

			mGridCellMap.get(bidderConnectInfo.getUserNo()).setBidding(true);
			startResetTimer(bidderConnectInfo);
		}
	}

	private void refreshBiddingCount(boolean isPlus) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (isPlus) {
					mBidderCount++;
					mBiddingCountLabel
							.setText(mCurrentResources.getString("str.auction.bid") + String.valueOf(mBidderCount)
									+ mCurrentResources.getString("str.auction.person.count.unit"));
				} else {
					if (mBidderCount > 0) {
						mBidderCount--;
					} else {
						mBidderCount = 0;
					}

					mBiddingCountLabel
							.setText(mCurrentResources.getString("str.auction.bid") + String.valueOf(mBidderCount)
									+ mCurrentResources.getString("str.auction.person.count.unit"));
				}
			}
		});
	}

	private void refreshConnectCount() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				mConnectBidderCountLabel.setText(
						mCurrentResources.getString("str.auction.connector") + String.valueOf(mGridCellMap.size())
								+ mCurrentResources.getString("str.auction.person.count.unit"));

				int totalAhCount = 0;
				int totalPcCount = 0;
				int totalAndCount = 0;
				int totalIosCount = 0;

				for (String key : mBidderConnectInfoMap.keySet()) {

					if (mBidderConnectInfoMap.get(key).getOS().toUpperCase()
							.equals(GlobalDefineCode.USE_CHANNEL_AUCTION_HOUSE)) {
						totalAhCount++;
					}

					if (mBidderConnectInfoMap.get(key).getOS().toUpperCase().equals(GlobalDefineCode.USE_CHANNEL_PC)) {
						totalPcCount++;
					}

					if (mBidderConnectInfoMap.get(key).getOS().toUpperCase()
							.equals(GlobalDefineCode.USE_CHANNEL_ANDROID)) {
						totalAndCount++;
					}

					if (mBidderConnectInfoMap.get(key).getOS().toUpperCase().equals(GlobalDefineCode.USE_CHANNEL_IOS)) {
						totalIosCount++;
					}
				}

				mConnectAhCountLabel.setText(
						mCurrentResources.getString("str.auction.auctionhouse") + Integer.toString(totalAhCount)
								+ mCurrentResources.getString("str.auction.person.count.unit"));
				mConnectPcCountLabel
						.setText(mCurrentResources.getString("str.auction.pc") + Integer.toString(totalPcCount)
								+ mCurrentResources.getString("str.auction.person.count.unit"));
				mConnectANDCountLabel
						.setText(mCurrentResources.getString("str.auction.android") + Integer.toString(totalAndCount)
								+ mCurrentResources.getString("str.auction.person.count.unit"));
				mConnectiOSCountLabel
						.setText(mCurrentResources.getString("str.auction.ios") + Integer.toString(totalIosCount)
								+ mCurrentResources.getString("str.auction.person.count.unit"));
			}
		});
	}

	/**
	 * 
	 * @MethodName createClients
	 * @Description 접속자 모니터링 프로그램 접속 클라이언트 생성 처리
	 *
	 * @param port 접속포트번호
	 */
	private void createClients(int port) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				CommonUtils.getInstance().showLoadingDialog(mStage, mCurrentResources.getString("str.connect.server"));
			}
		});

		mClients = new AuctionShareNettyClient.Builder(AuctionShareSetting.CLIENT_HOST, port).setController(this)
				.buildAndRun();
	}

	private void onConnectInfo() {
		String memberId = SharedPreference.getMemberNum();

		if (mClients != null && mClients.isActive()) {
			if (mConnectInfoJob != null) {
				mConnectInfoJob.cancel(true);
			}

			mClients.sendMessage(new ConnectionInfo(memberId, GlobalDefineCode.CONNECT_CHANNEL_AUCTION_CONNECT_MONITOR,
					GlobalDefineCode.USE_CHANNEL_PC, "N"));
		} else {
			startConnectInfoTimer();
		}
	}

	@Override
	public void onActiveChannel(Channel channel) {
		// TODO Auto-generated method stub
		onConnectInfo();
	}

	@Override
	public void onActiveChannel() {
		// TODO Auto-generated method stub
		onConnectInfo();
	}

	@Override
	public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAuctionStatus(AuctionStatus auctionStatus) {
		// TODO Auto-generated method stub
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				// 응찰자 수
				mBiddingCountLabel
						.setText(mCurrentResources.getString("str.auction.bid") + auctionStatus.getCurrentBidderCount()
								+ mCurrentResources.getString("str.auction.person.count.unit"));

				if (auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {
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
				}
			}
		});
	}

	@Override
	public void onCurrentSetting(CurrentSetting currentSetting) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponseCarInfo(ResponseEntryInfo responseCarInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onToastMessage(ToastMessage toastMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectorInfo(BidderConnectInfo bidderConnectInfo) {
		// TODO Auto-generated method stub
		mLogger.debug("onConnectorInfo : " + bidderConnectInfo.getEncodedMessage());

		if (bidderConnectInfo.getStatus().equals("N")) {
			if (!mBidderConnectInfoMap.containsKey(bidderConnectInfo.getUserNo())) {
				mBidderConnectInfoMap.put(bidderConnectInfo.getUserNo(), bidderConnectInfo);
			}

			addGridView(bidderConnectInfo);

//            onRefreshView();
		} else if (bidderConnectInfo.getStatus().equals("B")) {
			if (!mBidderConnectInfoMap.containsKey(bidderConnectInfo.getUserNo())) {
				mBidderConnectInfoMap.put(bidderConnectInfo.getUserNo(), bidderConnectInfo);
			}

			if (!mBiddingBidderMap.containsKey(bidderConnectInfo.getUserNo())) {
				mBiddingBidderMap.put(bidderConnectInfo.getUserNo(), true);
			}

			setBidding(bidderConnectInfo);
		} else if (bidderConnectInfo.getStatus().equals("O")) {
			if (mBidderConnectInfoMap.containsKey(bidderConnectInfo.getUserNo())) {
				mBidderConnectInfoMap.remove(bidderConnectInfo.getUserNo());
			}

			removeGridView(bidderConnectInfo);

//            onRefreshView();
		}
	}

	@Override
	public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo) {
		if (mConnectInfoJob != null) {
			mConnectInfoJob.cancel(true);
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				CommonUtils.getInstance().dismissLoadingDialog();
			}
		});

		switch (responseConnectionInfo.getResult()) {
		case GlobalDefineCode.CONNECT_SUCCESS:
			mLogger.debug("[CONNECT_SUCCESS] 서버 접속 성공");
			break;
		case GlobalDefineCode.CONNECT_FAIL:
			mLogger.debug("[CONNECT_FAIL] 서버 접속 실패");
			CommonUtils.getInstance().showAlertPopupOneButton((Stage) mRootAnchorPane.getScene().getWindow(),
					mCurrentResources.getString("str.fail.connect.server"), mCurrentResources.getString("str.ok"));

			mIsStartMonitor = false;
			toggleStartButton();
			break;
		case GlobalDefineCode.CONNECT_DUPLICATE:
			mLogger.debug("[CONNECT_DUPLICATE] 서버 중복 접속");
			break;
		case GlobalDefineCode.CONNECT_DUPLICATE_FAIL:
			mLogger.debug("[CONNECT_DUPLICATE_FAIL] 서버 중복 접속 불가");
			CommonUtils.getInstance().showAlertPopupOneButton((Stage) mRootAnchorPane.getScene().getWindow(),
					mCurrentResources.getString("str.duplicated.connect"), mCurrentResources.getString("str.ok"));

			mIsStartMonitor = false;
			toggleStartButton();
		default:
			// do something ...
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

	}

	@Override
	public void onConnectionException(int port) {
		// TODO Auto-generated method stub
		if (isBtnClose || isAuctionFinished) {
			return;
		}

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				CommonUtils.getInstance().dismissLoadingDialog();
				CommonUtils.getInstance().showAlertPopupOneButton(mStage,
						mCurrentResources.getString("str.auction.fail.connection"),
						mCurrentResources.getString("str.ok"));

				mIsStartMonitor = false;
				toggleStartButton();
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
						GlobalDefineCode.CONNECT_CHANNEL_AUCTION_CONNECT_MONITOR, GlobalDefineCode.USE_CHANNEL_PC)
								.getEncodedMessage()
						+ "\r\n");
	}

	public void closeStage() {
		mLogger.debug("Stage Close");
		if (mClients != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					CommonUtils.getInstance().showLoadingDialog(mStage,
							mCurrentResources.getString("str.disconnect.server"));
				}
			});

			mClients.stopClient(new NettyClientShutDownListener() {
				@Override
				public void onShutDown(int port) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							CommonUtils.getInstance().dismissLoadingDialog();

							mStage.close();
						}
					});
				}
			});
		}
	}

	private void startResetTimer(BidderConnectInfo bidderConnectInfo) {
		mLogger.debug("startResetTimer : " + bidderConnectInfo.getUserNo());

		Thread thread = new Thread() {
			@Override
			public void run() {
				mResetTimeJob = mService.schedule(new ResetTimerJob(bidderConnectInfo), 300, TimeUnit.MILLISECONDS);
			}
		};
		thread.setDaemon(true);
		thread.start();

	}

	private class ResetTimerJob implements Runnable {
		BidderConnectInfo bidderConnectInfo;

		public ResetTimerJob(BidderConnectInfo bidderConnectInfo) {
			// TODO Auto-generated constructor stub
			this.bidderConnectInfo = bidderConnectInfo;
		}

		@Override
		public void run() {
			if (mGridCellMap.containsKey(bidderConnectInfo.getUserNo())) {
				mGridCellMap.get(bidderConnectInfo.getUserNo()).setBidding(false);

				if (mBiddingBidderMap.containsKey(bidderConnectInfo.getUserNo())) {
					mBiddingBidderMap.put(bidderConnectInfo.getUserNo(), true);
				}

				mLogger.debug("ResetTimerJob : " + bidderConnectInfo.getUserNo());

//                refreshBiddingCount(false);
			}
		}
	}

	@FXML
	private void onStartMonitor() {
		if (isShowAlertPopup) {
			return;
		}

		Thread thread = new Thread() {
			@Override
			public void run() {
				if (!mIsStartMonitor) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							CommonUtils.getInstance().showLoadingDialog(mStage,
									mCurrentResources.getString("str.connect.server"));
						}
					});

					createClients(Integer.valueOf(mItem.getAuctionLanePort()));
					mIsStartMonitor = true;

					toggleStartButton();
				} else {

					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							CommonUtils.getInstance().showLoadingDialog(mStage,
									mCurrentResources.getString("str.disconnect.server"));
						}
					});

					isBtnClose = true;
					mClients.stopClient(new NettyClientShutDownListener() {
						@Override
						public void onShutDown(int port) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									CommonUtils.getInstance().dismissLoadingDialog();

									mBidderConnectInfoMap.clear();
									onRefreshView();

									mIsStartMonitor = false;

									toggleStartButton();
								}
							});
						}
					});
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	private void toggleStartButton() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (mIsStartMonitor) {
					mStartMonitorButton.setText(mCurrentResources.getString("str.monitoring.stop"));
				} else {
					mStartMonitorButton.setText(mCurrentResources.getString("str.monitoring.start"));
				}
			}
		});
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

	public List<String> sortByBidderConnectInfo(HashMap<String, BidderConnectInfo> map) {
		List<String> list = new ArrayList<String>();
		list.addAll(map.keySet());

		Collections.sort(list, new Comparator<String>() {
			public int compare(String o1, String o2) {
				String v1 = map.get(o1).getUserNo();
				String v2 = map.get(o2).getUserNo();
				return v1.compareTo(v2);
			}
		});

		return list;
	}
}
