package com.nh.controller.controller;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import com.nh.controller.preferences.SharedPreference;
import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.model.AuctionGenerateInformationResult;
import com.nh.share.api.model.AuctionResultResult;
import com.nh.share.api.request.ActionRequestAuctionResult;
import com.nh.share.api.response.ResponseAuctionResult;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionReponseSession;
import com.nh.share.common.models.ConnectionInfo;
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
import com.sun.javafx.scene.control.skin.TableHeaderRow;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * 
 * @ClassName AuctionResultController.java
 * @Description 낙/유찰 모니터링 View Controller
 * @author ishift
 * @since
 */
public class AuctionResultController implements NettyControllable, Initializable {

	private final Logger mLogger = LogManager.getLogger(AuctionResultController.class);

	private ResourceBundle mCurrentResources;

	@FXML
	ToggleButton mToggleBtnStart; // 모니터링 시작/종료 토글 버튼

	@FXML
	Button mBtnAuctionType; // 경매 타입 (실시간/SPOT)

	@FXML
	Button mBtnAuctionLaneName; // 경매 레인명

	@FXML
	Label mLabelAuctionInfoDetail; // 경매 상세 정보

	@FXML
	Label mLabelSuccess; // 낙찰건

	@FXML
	Label mLabelFail; // 유찰건

	@FXML
	Label mLabelSuccessPercent; // 낙찰률

	@FXML
	Label mLabelTotalPrice; // 금액(합)

	@FXML
	TableView<AuctionResultResult> mTableViewResult; // 경매 결과 목록

	@FXML
	TableColumn<AuctionResultResult, String> mTableColumnEntryNum; // 경매 결과 > 출품번호 컬럼

	@FXML
	TableColumn<AuctionResultResult, String> mTableColumnCarNm; // 경매 결과 > 차량명 컬럼

	@FXML
	TableColumn<AuctionResultResult, String> mTableColumnHopePrice; // 경매 결과 > 희망가 컬럼

	@FXML
	TableColumn<AuctionResultResult, String> mTableColumnHightPrice; // 경매 결과 > 최고가 컬럼

	@FXML
	TableColumn<AuctionResultResult, String> mTableColumnSuccBidPrice; // 경매 결과 > 낙찰가 컬럼

	@FXML
	TableColumn<AuctionResultResult, String> mTableColumnSuccBidMember; // 경매 결과 > 낙찰자 컬럼

	@FXML
	TableColumn<AuctionResultResult, String> mTableColumnResult; // 경매 결과 > 결과 컬럼 (낙찰/유찰)

	private Stage mStage; // 내 stage

	private AuctionGenerateInformationResult mAuctionListViewCellItem; // 경매 목록에서 전달받은 cell Data
	public AuctionShareNettyClient mClient; // Netty 연결
	private ResponseEntryInfo carInfo; // 경매 출품 차량 정보

	private ObservableList<AuctionResultResult> mObservableListResultItems; // TableView 데이터
	private List<AuctionResultResult> mListAuctionResult = new ArrayList<AuctionResultResult>(); // 경매 결과 데이터
	private boolean isRequestApi = false; // API 최초 한번 호출

	private final String TYPE_NONE = "none"; // 일반
	private final String TYPE_PRICE = "price"; // 금액
	private final String TYPE_RESULT = "result"; // 낙/유찰 결과

	private ScheduledExecutorService mService = Executors.newScheduledThreadPool(1); // 현재 동작하는 작업 스레드 풀
	private ScheduledFuture<?> mConnectInfoJob; // 경매 접속 정보 전송 처리 Job

	private boolean isBtnClose = false;
	private boolean isAuctionFinished = false;

	private boolean isShowAlertPopup = false;
	private Dialog<ButtonType> mAlertDialog = new Dialog<>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		mCurrentResources = resources;
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
	public void setStage(Stage stage, AuctionGenerateInformationResult item) {
		mStage = stage;
		mStage.setTitle(mCurrentResources.getString("str.auction.result.monitoring"));
		mAuctionListViewCellItem = item;

		initView(); // 낙/유찰 모니터링 화면 셋팅
		initTableView();
	}

	/**
	 * 
	 * @MethodName initView
	 * @Description 낙/유찰 모니터링 프로그램 내 고정으로 사용하는 항목 설정.
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

		mLabelSuccess.setText(mCurrentResources.getString("str.count.default.unit"));
		mLabelFail.setText(mCurrentResources.getString("str.count.default.unit"));
		mLabelSuccessPercent.setText(mCurrentResources.getString("str.rate.default.unit"));
		mLabelTotalPrice.setText(mCurrentResources.getString("str.price.unit"));
	}

	/**
	 * 
	 * @MethodName initTableView
	 * @Description tableView 셋팅
	 *
	 */
	private void initTableView() {
		mTableColumnEntryNum.setCellValueFactory(cellData -> new SimpleStringProperty(
				replaceString(TYPE_NONE, cellData.getValue().getAuctionEntryNum()))); // 출품번호
		mTableColumnCarNm.setCellValueFactory(
				cellData -> new SimpleStringProperty(replaceString(TYPE_NONE, cellData.getValue().getCarName()))); // 차량명
		mTableColumnHopePrice.setCellValueFactory(
				cellData -> new SimpleStringProperty(replaceString(TYPE_PRICE, cellData.getValue().getHopePrice()))); // 희망가격
		mTableColumnHightPrice.setCellValueFactory(
				cellData -> new SimpleStringProperty(replaceString(TYPE_PRICE, cellData.getValue().getHightPrice()))); // 최고가
		mTableColumnSuccBidPrice.setCellValueFactory(cellData -> new SimpleStringProperty(
				replaceString(TYPE_PRICE, cellData.getValue().getSuccessBidPrice()))); // 낙찰가
		mTableColumnSuccBidMember.setCellValueFactory(cellData -> new SimpleStringProperty(
				replaceString(TYPE_NONE, cellData.getValue().getSuccessBidMemberNum()))); // 회원번호
		mTableColumnResult.setCellValueFactory(cellData -> new SimpleStringProperty(
				replaceString(TYPE_RESULT, cellData.getValue().getAuctionResultCode()))); // 낙/유찰 결과

		// table column 가로 길이 %로 설정
		mTableColumnEntryNum.prefWidthProperty().bind(mTableViewResult.widthProperty().multiply(0.1));
		mTableColumnCarNm.prefWidthProperty().bind(mTableViewResult.widthProperty().multiply(0.4));
		mTableColumnHopePrice.prefWidthProperty().bind(mTableViewResult.widthProperty().multiply(0.1));
		mTableColumnHightPrice.prefWidthProperty().bind(mTableViewResult.widthProperty().multiply(0.1));
		mTableColumnSuccBidPrice.prefWidthProperty().bind(mTableViewResult.widthProperty().multiply(0.1));
		mTableColumnSuccBidMember.prefWidthProperty().bind(mTableViewResult.widthProperty().multiply(0.1));
		mTableColumnResult.prefWidthProperty().bind(mTableViewResult.widthProperty().multiply(0.1));

		// TableView Cell 아이템 Array 초기화.
		mObservableListResultItems = FXCollections.observableArrayList();

		mTableViewResult.setMouseTransparent(false);
		mTableViewResult.setFocusTraversable(false);

		// TableView Row Header 마우스 선택 막기

		mTableViewResult.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) {
				final TableHeaderRow header = (TableHeaderRow) mTableViewResult.lookup("TableHeaderRow");
				header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
							Boolean newValue) {
						header.setReordering(false);
					}
				});
			}
		});

		// TableView Selected 막기
		mTableViewResult.getSelectionModel().selectedIndexProperty().addListener((observable, oldvalue, newValue) -> {

			Platform.runLater(() -> {
				mTableViewResult.getSelectionModel().clearSelection();
			});

		});

		mTableViewResult.setPlaceholder(new Label(mCurrentResources.getString("str.not.exist.result")));
	}

	/**
	 * 
	 * @MethodName replaceString
	 * @Description table column에 들어가는 String 데이터 치환
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	private String replaceString(String type, String value) {
		if (value != null && value.length() > 0) {
			if (type.equals(TYPE_PRICE)) {
				value = NumberFormat.getInstance().format(Integer.parseInt(value));
			} else if (type.equals(TYPE_RESULT)) {
				if (value.equals(GlobalDefineCode.REQUEST_PARAM_AUCTION_RESULT_SUCCESS)) {
					value = mCurrentResources.getString("str.status.success");
				} else if (value.equals(GlobalDefineCode.REQUEST_PARAM_AUCTION_RESULT_FAIL)) {
					value = mCurrentResources.getString("str.status.fail");
				}
			}
		} else {
			value = "-";
		}
		return value;
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
				// TODO Auto-generated method stub
				CommonUtils.getInstance().showLoadingDialog(mStage, mCurrentResources.getString("str.connect.server"));
			}
		});

		String port = mAuctionListViewCellItem.getAuctionLanePort();

		if (port != null && port.length() > 0) {
			int server_port = Integer.parseInt(port);
			try {
				mClient = new AuctionShareNettyClient.Builder(AuctionShareSetting.CLIENT_HOST, server_port)
						.setController(this).buildAndRun();
			} catch (Exception e) {
				mLogger.debug("createClient Fail");
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						CommonUtils.getInstance().dismissLoadingDialog();
						CommonUtils.getInstance().showAlertPopupOneButton(mStage,
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
					// TODO Auto-generated method stub
					CommonUtils.getInstance().dismissLoadingDialog();
					CommonUtils.getInstance().showAlertPopupOneButton(mStage,
							mCurrentResources.getString("str.not.exist.port.info"),
							mCurrentResources.getString("str.ok"));
				}
			});
		}
	}

	/**
	 * 
	 * @MethodName requestAuctionResultAPI
	 * @Description getAuctionResult.do api 호출, 최초 1번만 호출하며 현재 진행중인 출품번호 이전 결과 데이터를
	 *              가져온다.
	 *
	 */
	private void requestAuctionResultAPI(String entryNum) {

		// TableView Cell 아이템 Array 초기화.
		mObservableListResultItems = FXCollections.observableArrayList();
		isRequestApi = true;

		String auctionCode = mAuctionListViewCellItem.getAuctionCode();
		String auctionRound = mAuctionListViewCellItem.getAuctionRound();
		String auctionLaneCode = mAuctionListViewCellItem.getAuctionLaneCode();

		ActionRuler.getInstance().addAction(new ActionRequestAuctionResult(auctionCode, auctionRound, auctionLaneCode,
				entryNum, new ActionResultListener<ResponseAuctionResult>() {
					@Override
					public void onResponseResult(ResponseAuctionResult result) {
						if (result.getResult().size() > 0) {
							List<AuctionResultResult> listResult = result.getResult();
							// 낙/유찰 일시 내림차순으로 정렬
							Collections.sort(listResult, new Comparator<AuctionResultResult>() {

								@Override
								public int compare(AuctionResultResult o1, AuctionResultResult o2) {
									int result = 0;
									if (Long.valueOf(o1.getAuctionResultDateTime()) < Long
											.valueOf(o2.getAuctionResultDateTime())) {
										result = 1;
									} else if (Long.valueOf(o1.getAuctionResultDateTime()) > Long
											.valueOf(o2.getAuctionResultDateTime())) {
										result = -1;
									}
									return result;
								}

							});
							mListAuctionResult.clear();
							mListAuctionResult.addAll(0, listResult);
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									loadTableView();
									loadResultSummary();
								}
							});
						}
					}

					@Override
					public void onResponseError(String message) {
						mLogger.debug("RequestAuctionLogin error : " + message);
					}
				}));
		ActionRuler.getInstance().runNext();
	}

	/**
	 * 
	 * @MethodName loadTableView
	 * @Description tableView Load
	 *
	 */
	private void loadTableView() {
		if (mListAuctionResult != null && mListAuctionResult.size() > 0) {
			mObservableListResultItems.addAll(mListAuctionResult);
			mTableViewResult.setItems(mObservableListResultItems);
		}
	}

	/**
	 * 
	 * @MethodName loadResultSummary
	 * @Description 낙찰/유찰/낙찰률/금액(합) 표시
	 *
	 */
	private void loadResultSummary() {
		// 낙찰건
		long succBidCount = mListAuctionResult.stream()
				.filter(x -> x.getAuctionResultCode().equals(GlobalDefineCode.REQUEST_PARAM_AUCTION_RESULT_SUCCESS))
				.count();
		mLabelSuccess.setText(Long.toString(succBidCount) + mCurrentResources.getString("str.count.unit"));

		// 유찰건
		long failBidCount = mListAuctionResult.stream()
				.filter(x -> x.getAuctionResultCode().equals(GlobalDefineCode.REQUEST_PARAM_AUCTION_RESULT_FAIL))
				.count();
		mLabelFail.setText(Long.toString(failBidCount) + mCurrentResources.getString("str.count.unit"));

		// 낙찰률
		double succBidRate = (double) ((double) succBidCount / (double) mListAuctionResult.size()) * 100;
		mLabelSuccessPercent.setText(String.format("%.1f", succBidRate) + mCurrentResources.getString("str.rate.unit"));

		// 총 낙찰 금액 합계
		Integer succBidPrice = mListAuctionResult.stream()
				.filter(x -> x.getAuctionResultCode().equals(GlobalDefineCode.REQUEST_PARAM_AUCTION_RESULT_SUCCESS))
				.mapToInt(x -> Integer.parseInt(x.getSuccessBidPrice())).sum();
		mLabelTotalPrice.setText(
				NumberFormat.getInstance().format(succBidPrice) + mCurrentResources.getString("str.price.unit"));
	}

	/**
	 * 
	 * @MethodName setAuctionResult
	 * @Description 경매 서버에서 받아온 AuctionStatus 값을 AuctionResult Model에 저장.
	 * 
	 * @param auctionStatus
	 * @return
	 */
	private AuctionResultResult setAuctionResult(AuctionStatus auctionStatus) {
		String carName = "";
		String hopePrice = "";
		if (carInfo != null) {
			if (carInfo.getAuctionEntryNum() != null
					&& carInfo.getAuctionEntryNum().equals(auctionStatus.getEntryNum())) {
				if (carInfo.getCarName() != null && carInfo.getCarName().length() > 0) {
					carName = carInfo.getCarName();
				}
				if (carInfo.getAuctionHopePrice() != null && carInfo.getAuctionHopePrice().length() > 0) {
					hopePrice = carInfo.getAuctionHopePrice();
				}
			}
		}

		AuctionResultResult result = new AuctionResultResult();
		result.setAuctionEntryNum(auctionStatus.getEntryNum());
		result.setCarName(carName);
		result.setHopePrice(hopePrice);
		result.setHightPrice(auctionStatus.getCurrentPrice());

		if (auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_SUCCESS)) {
			if (auctionStatus.getRank1MemberNum() != null && !auctionStatus.getRank1MemberNum().equals("null")
					&& !auctionStatus.getRank1MemberNum().equals("SYSTEM")
					&& !auctionStatus.getRank1MemberNum().equals("NULL")) {
				result.setSuccessBidMemberNum(auctionStatus.getRank1MemberNum());
			}

			result.setSuccessBidPrice(auctionStatus.getCurrentPrice());
			result.setAuctionResultCode(GlobalDefineCode.REQUEST_PARAM_AUCTION_RESULT_SUCCESS);
		} else if (auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_FAIL)) {
			result.setAuctionResultCode(GlobalDefineCode.REQUEST_PARAM_AUCTION_RESULT_FAIL);
		}
		return result;
	}

	/**
	 * 
	 * @MethodName closeStage
	 * @Description 낙/유찰 모니터링 프로그램 [X] 버튼, 종료 버튼 누르는 경우.
	 *
	 */
	private void closeStage() {
		if (mClient != null) {
			mClient.stopClient();
		}
		mStage.close();
	}

	@FXML
	private void onActionToggleBtnStart() {
		if (isShowAlertPopup) {
			return;
		}
		changeToggleBtnStart(mToggleBtnStart.isSelected());
	}

	private void changeToggleBtnStart(boolean isSelected) {
		if (isSelected) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					createClient(); // netty client 설정
				}
			};
			thread.setDaemon(true);
			thread.start();

			mToggleBtnStart.setText(mCurrentResources.getString("str.monitoring.stop"));
		} else {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					CommonUtils.getInstance().showLoadingDialog(mStage,
							mCurrentResources.getString("str.disconnect.server"));
				}
			});
			isRequestApi = false;
			isBtnClose = true;
			mClient.stopClient(new NettyClientShutDownListener() {
				@Override
				public void onShutDown(int port) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							CommonUtils.getInstance().dismissLoadingDialog();
							mToggleBtnStart.setText(mCurrentResources.getString("str.monitoring.start"));
						}
					});
				}
			});
		}
	}

	@Override
	public void onActiveChannel(Channel channel) {
		mLogger.debug("onActiveChannel");
		// 경매 서버 종료 리스너 등록
//        ChannelFuture closeFuture = channel.closeFuture();
//        closeFuture.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) throws Exception {
//                // session cleanup logic
//                mLogger.info("경매 서버가 종료되었습니다.");
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        mToggleBtnStart.setSelected(false);
//                        changeToggleBtnStart(false);
//                    }
//                });
//            }
//        });

		onConnectInfo();
	}

	@Override
	public void onActiveChannel() {

	}

	@Override
	public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAuctionStatus(AuctionStatus auctionStatus) {
		mLogger.debug("onAuctionStatus : " + auctionStatus.getState() + " / " + auctionStatus.getEntryNum());
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_SUCCESS)
						|| auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_FAIL)) {
					AuctionResultResult result = setAuctionResult(auctionStatus);
					mListAuctionResult.add(0, result);
					mTableViewResult.getItems().add(0, result);
					loadResultSummary();
				} else if (auctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {
					isAuctionFinished = true;
					isShowAlertPopup = true;
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							mToggleBtnStart.setSelected(false);
							mToggleBtnStart.setText(mCurrentResources.getString("str.monitoring.start"));
//                            changeToggleBtnStart(false);
							mAlertDialog = CommonUtils.getInstance().setAlertPopupStyle(mStage,
									CommonUtils.getInstance().ALERTPOPUP_ONE_BUTTON,
									mCurrentResources.getString("str.auction.complete.stay"),
									mCurrentResources.getString("str.ok"), "");
							mAlertDialog.setOnHiding(event -> {
								isShowAlertPopup = false;
							});
							mAlertDialog.show();
						}
					});
				}
			}
		});

		if (Integer.valueOf(auctionStatus.getFinishEntryCount()) > 0 && !isRequestApi) {
			String entryNum = auctionStatus.getEntryNum();
			requestAuctionResultAPI(entryNum);
		} else {
			isRequestApi = true;
		}
	}

	@Override
	public void onCurrentSetting(CurrentSetting currentSetting) {

	}

	@Override
	public void onResponseCarInfo(ResponseEntryInfo responseCarInfo) {
		mLogger.debug("[onResponseCarInfo] : " + responseCarInfo.getAuctionEntryNum());
		carInfo = new ResponseEntryInfo();
		carInfo = responseCarInfo;
	}

	@Override
	public void onToastMessage(ToastMessage toastMessage) {

	}

	@Override
	public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo) {
		mLogger.debug("[onResponseConnectionInfo] result : " + responseConnectionInfo.getResult());
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
	}

	@Override
	public void onFavoriteCarInfo(FavoriteEntryInfo favoriteCarInfo) {

	}

	@Override
	public void onAbsenteeUserInfo(AbsenteeUserInfo absenteeUserInfo) {

	}

	@Override
	public void onExceptionCode(ExceptionCode exceptionCode) {
		mLogger.debug("onExceptionCode");
	}

	@Override
	public void onConnectionException(int port) {
		mLogger.debug("onConnectionException");
		if (isBtnClose || isAuctionFinished) {
			return;
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				CommonUtils.getInstance().dismissLoadingDialog();
				Optional<ButtonType> result = CommonUtils.getInstance().showAlertPopupOneButton(mStage,
						mCurrentResources.getString("str.auction.not.accessible"),
						mCurrentResources.getString("str.ok"));
				if (result.isPresent() && result.get().getText() == mCurrentResources.getString("str.ok")) {
					mToggleBtnStart.setSelected(false);
					changeToggleBtnStart(false);
				}
			}
		});
	}

	@Override
	public void onChannelInactive(int port) {
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
						mCurrentResources.getString("str.auction.close.connection.stay"),
						mCurrentResources.getString("str.ok"), "");
				mAlertDialog.setOnHiding(event -> {
					isShowAlertPopup = false;
					changeToggleBtnStart(false);
				});
				mAlertDialog.show();
			}
		});

	}

	@Override
	public void exceptionCaught(int port) {

	}

	@Override
	public void onCheckSession(ChannelHandlerContext ctx, AuctionCheckSession auctionCheckSession) {
		// TODO Auto-generated method stub
		ctx.channel()
				.writeAndFlush(new AuctionReponseSession(SharedPreference.getMemberNum(),
						GlobalDefineCode.CONNECT_CHANNEL_AUCTION_RESULT_MONITOR, GlobalDefineCode.USE_CHANNEL_PC)
								.getEncodedMessage()
						+ "\r\n");
	}

	@Override
	public void onConnectorInfo(BidderConnectInfo bidderConnectInfo) {
		// TODO Auto-generated method stub

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

	private void onConnectInfo() {
		String id = SharedPreference.getMemberNum();

		if (mClient != null && mClient.isActive()) {
			mClient.sendMessage(new ConnectionInfo(id, GlobalDefineCode.CONNECT_CHANNEL_AUCTION_RESULT_MONITOR,
					GlobalDefineCode.USE_CHANNEL_PC, "N"));
		} else {
			startConnectInfoTimer();
		}
	}
}
