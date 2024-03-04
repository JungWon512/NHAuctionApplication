package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.controller.interfaces.IntegerListener;
import com.nh.controller.interfaces.SettingListener;
import com.nh.controller.model.AuctionRound;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.ApiUtils;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.SharedPreference;
import com.nh.share.api.ActionResultListener;
import com.nh.share.api.models.QcnData;
import com.nh.share.api.models.StnData;
import com.nh.share.api.request.body.RequestCowInfoBody;
import com.nh.share.api.request.body.RequestQcnBody;
import com.nh.share.api.response.ResponseCowInfo;
import com.nh.share.api.response.ResponseNumber;
import com.nh.share.api.response.ResponseQcn;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.utils.SentryUtil;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 * 경매 선택
 *
 * @author jhlee
 */
public class ChooseAuctionController implements Initializable {

	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private Stage mStage = null;

	private ResourceBundle mResMsg = null;

	@FXML // 경매 거점 타입
	private ToggleGroup cowTypeToggleGroup;

	@FXML // 소 타입 , 송아지,비육우,번식우
	private ToggleButton mCalfToggleButton, mFatteningCattleToggleButton, mBreedingCattleToggleButton, mAllCowToggleButton;

	@FXML // 접속 , 종료 , 환경설정
	private Button mBtnConnect, mBtnClose, mBtnSetting;

	@FXML // 경매 날짜
	private DatePicker mAuctionDatePicker;

	@FXML
	private Label mTitleLabel,mVersionLabel, mReleaseDateLabel;

	@FXML
	private TextField mIp, mPort;

	@FXML
	private Button mBtnConnectionInfoModify;

	/**
	 * setStage
	 *
	 * @param stage
	 */
	public void setStage(Stage stage) {
		mStage = stage;
		CommonUtils.getInstance().canMoveStage(mStage, null);
		initKeyConfig();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// get ResMsg
		if (resources != null) {
			mResMsg = resources;
		}

		// 소 타입
		initCowToggleTypes();
		// 날짜 피커
		initDatePickerConfig();

		setApplicationInfo();

		mBtnConnect.setOnMouseClicked(event -> onConnection());
		mBtnClose.setOnMouseClicked(event -> onCloseApplication());
		mBtnSetting.setOnMouseClicked(event -> openSettingDialog());
		mBtnConnectionInfoModify.setOnMouseClicked(event -> modifyConnectionInfo());

		setDefaultSetting();
		
//		toggleAllCowDisable();
	}

	private void setApplicationInfo() {
		
		if(!GlobalDefineCode.FLAG_PRD) {
			String devStr = "[난장]" + mTitleLabel.getText();
			mTitleLabel.setText(devStr);
		}
		
		mVersionLabel.setText("v" +GlobalDefine.APPLICATION_INFO.RELEASE_VERION);
		mReleaseDateLabel.setText(GlobalDefine.APPLICATION_INFO.RELEASE_DATE);
	}

	/**
	 * 내부 저장된 값들 셋팅
	 */
	private void setDefaultSetting() {

		String host = GlobalDefine.AUCTION_INFO.AUCTION_HOST;
		
		if(GlobalDefineCode.FLAG_PRD) {
			host = GlobalDefine.AUCTION_INFO.AUCTION_HOST;
		}else {
			host = GlobalDefine.AUCTION_INFO.DEV_AUCTION_HOST;
		}
		
		String ip = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SERVER_IP, host);
		
		// by kih 2024.03.04 :  ip -> domain 으로 변경하여 저장 함. 
		int n = (int) ip.chars().filter(i -> String.valueOf((char) i).equals(".")).count(); // 3
		if(n == 3) {		
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SERVER_IP, host);
			ip = host;		// ip -> domain 으로 변경
		}
		
		int port = SharedPreference.getInstance().getInt(SharedPreference.PREFERENCE_SERVER_PORT, GlobalDefine.AUCTION_INFO.AUCTION_PORT);
		int obj = SharedPreference.getInstance().getInt(SharedPreference.PREFERENCE_SELECTED_OBJ, GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_1);

		mIp.setText(ip);
		mPort.setText(Integer.toString(port));

		switch (obj) {
		case GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_1:
			mCalfToggleButton.setSelected(true);
			break;
		case GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_2:
			mFatteningCattleToggleButton.setSelected(true);
			break;
		case GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_3:
			mBreedingCattleToggleButton.setSelected(true);
			break;
		case GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_0:
			mAllCowToggleButton.setSelected(true);
			break;
		default:
			mCalfToggleButton.setSelected(true);
		}
		
		GlobalDefine.AUCTION_INFO.auctionRoundData = null;
	}

	/**
	 * 소 타입 Toggle
	 */
	private void initCowToggleTypes() {
		// listener
		cowTypeToggleGroup.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> mLogger.debug("소 타입 => " + newValue.getUserData().toString().trim()));
	}

	/**
	 * 날짜 피커
	 */
	private void initDatePickerConfig() {

		mAuctionDatePicker.setConverter(new StringConverter<LocalDate>() {

			String pattern = "yyyy-MM-dd";

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

			{
				mAuctionDatePicker.setPromptText("년-월-일");
			}

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					String dateString = date.toString();

					return dateString.toString();
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		});

		mAuctionDatePicker.setValue(LocalDate.now());
	}

	private void modifyConnectionInfo() {

		if (mIp.isDisabled() && mPort.isDisabled()) {
			mIp.setDisable(false);
			mPort.setDisable(false);
			mBtnConnectionInfoModify.setText(mResMsg.getString("str.modify.cancel"));
		} else {
			mIp.setDisable(true);
			mPort.setDisable(true);
			mBtnConnectionInfoModify.setText(mResMsg.getString("str.modify"));
		}
	}

	/**
	 * 경매 접속
	 */
	public void onConnection() {
		
		if (!CommonUtils.getInstance().isValidString(mIp.getText()) || !CommonUtils.getInstance().isValidString(mPort.getText())) {
			CommonUtils.getInstance().showAlertPopupOneButton(mStage, mResMsg.getString("str.check.ip.port"), mResMsg.getString("popup.btn.ok"));
			return;
		}

		if (!CommonUtils.getInstance().isValidIp(mIp.getText())) {
			CommonUtils.getInstance().showAlertPopupOneButton(mStage, mResMsg.getString("str.check.ip.port"), mResMsg.getString("popup.btn.ok"));
			return;
		}

		// 선택된 경매일
		if (mAuctionDatePicker.getValue() == null) {
			CommonUtils.getInstance().showAlertPopupOneButton(mStage, mResMsg.getString("dialog.auction.no.data"), mResMsg.getString("popup.btn.ok"));
			return;
		}

		CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("msg.connection"));

		PauseTransition pauseTransition = new PauseTransition(Duration.millis(200));
		pauseTransition.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				requestAuctionRound();
			}
		});
		pauseTransition.play();

	}

	/**
	 * 경매 정보 조회
	 */
	private void requestAuctionRound() {

		final String naBzplc = GlobalDefine.ADMIN_INFO.adminData.getNabzplc();
		final String aucObjDsc = cowTypeToggleGroup.getSelectedToggle().getUserData().toString();
		final String aucDate = mAuctionDatePicker.getValue().toString().replace("-", "");
		String aucDsc = ""; 	//단일 1 , 일괄 2
		
		if(SettingApplication.getInstance().isSingleAuction()) {
			aucDsc = GlobalDefine.AUCTION_INFO.QCN_TYPE_PARAM_1;
		}else {
			aucDsc = GlobalDefine.AUCTION_INFO.QCN_TYPE_PARAM_2;
		}
		
		// 회차 정보 초기화
		clearGlobalData();

		// 회차 조회
		RequestQcnBody qcnBody = new RequestQcnBody(naBzplc, aucObjDsc, aucDate,aucDsc, GlobalDefine.ADMIN_INFO.adminData.getAccessToken());

		ApiUtils.getInstance().requestSelectQcn(qcnBody, new ActionResultListener<ResponseQcn>() {

			@Override
			public void onResponseResult(ResponseQcn result) {
				

//				Platform.runLater(()->CommonUtils.getInstance().dismissLoadingDialog());

				try {

					if (result != null ) {
	
						if (result.getSuccess() && result.getData() != null) {
						
							QcnData qcnData = result.getData();
							
							if(SettingApplication.getInstance().isSingleAuction()) {
								//단일
								setQcnData(naBzplc,aucObjDsc,aucDate,qcnData);
							}else {
								
								//일괄
								if(!CommonUtils.getInstance().isListEmpty(result.getStnList())) {
									
									int size  = result.getStnList().size();
									
									if(size == 1) {
										
										//구간 선택 안함. 0번째 선택
										setQcnData(naBzplc,aucDate,qcnData,result.getStnList().get(0));
	
									}else {
										
										Platform.runLater(() -> {
										
											CommonUtils.getInstance().dismissLoadingDialog();
											
											//구간 선택 팝업
											MoveStageUtil.getInstance().showChooseAuctionNumberRange(mStage ,result.getStnList(), new IntegerListener() {
												@Override
												public void callBack(int index) {
										
													if(index > -1) {
														
														//뒷배경 활성화
														MoveStageUtil.getInstance().dismissDialog();
														MoveStageUtil.getInstance().setBackStageDisableFalse(mStage);
	
														mLogger.debug("구간 선택 index : " + index);
														
														CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("msg.connection"));
														
														setQcnData(naBzplc,aucDate,qcnData,result.getStnList().get(index));
														
													}
												}
											});	
										});
									}
									
								}else {
									
									Platform.runLater(()->{
										CommonUtils.getInstance().dismissLoadingDialog();
										showAlertPopupOneButton(mResMsg.getString("dialog.auction.no.data"));
									});
	
								}
							}
							
	
						} else {
							
							Platform.runLater(()->{
								CommonUtils.getInstance().dismissLoadingDialog();
								if(CommonUtils.getInstance().isValidString(result.getMessage())) {
									showAlertPopupOneButton(result.getMessage());	
								}else {
									showAlertPopupOneButton(mResMsg.getString("dialog.auction.no.data"));	
								}
							});
							
						}
	
					} else {
						Platform.runLater(()->{
							CommonUtils.getInstance().dismissLoadingDialog();
							showAlertPopupOneButton(mResMsg.getString("dialog.auction.no.data"));
						});
					}
					
				}catch (Exception e) {
					e.printStackTrace();
					SentryUtil.getInstance().sendExceptionLog(e);
				}
			}

			@Override
			public void onResponseError(String message) {
				mLogger.debug("[회차정보검색 onResponseError] : " + message);
				Platform.runLater(()->{
					CommonUtils.getInstance().dismissLoadingDialog();
					showAlertPopupOneButton(mResMsg.getString("str.api.response.fail"));
				});
			}
		});
	}
	
	
	/**
	 * 회차 정보 셋팅 - 단일 경매 
	 * @param naBzplc
	 * @param aucObjDsc
	 * @param aucDate
	 * @param qcnData
	 */
	private void setQcnData(final String naBzplc,final String aucObjDsc,final String aucDate,QcnData qcnData) {
		
		GlobalDefine.AUCTION_INFO.auctionRoundData = new AuctionRound(qcnData);
		
		if (GlobalDefine.AUCTION_INFO.auctionRoundData != null) {

			mLogger.debug("[경매 회차 정보 조회 결과]=> " + GlobalDefine.AUCTION_INFO.auctionRoundData.toString());
			// 출장우 카운트
			requestCowInfo(naBzplc
					, aucObjDsc
					, Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDscStn())	// STN : by kih
					, aucDate
					,"");		
		} else {
			// 경매 회차 존재 하지 않음.
			showAlertPopupOneButton(mResMsg.getString("dialog.auction.no.data"));
		}
		
	}
	
	
	/**
	 * 회차 정보 셋팅 - 일괄 경매 
	 * @param naBzplc
	 * @param aucObjDsc
	 * @param aucDate
	 * @param qcnData
	 */
	private void setQcnData(final String naBzplc,final String aucDate,QcnData qcnData,StnData stnData) {
		
		GlobalDefine.AUCTION_INFO.auctionRoundData = new AuctionRound(qcnData,stnData);
		
		if (GlobalDefine.AUCTION_INFO.auctionRoundData != null) {
			mLogger.debug("[경매 회차 정보 조회 결과]=> " + GlobalDefine.AUCTION_INFO.auctionRoundData.toString());
//			Platform.runLater(()->CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("msg.connection")));
			// 출장우 카운트
			requestCowInfo(naBzplc
					, Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc())		// QCN : by kih
					, Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDscStn())	// STN : by kih
					, aucDate
					,Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getRgSqNo()));
		} else {
			// 경매 회차 존재 하지 않음.
			showAlertPopupOneButton(mResMsg.getString("dialog.auction.no.data"));
		}
		
	}
	

	/**
	 * 출장우 데이터 조회
	 * @param naBzplc
	 * @param aucObjDsc
	 * @param aucDate
	 * @param rgSqno
	 * 
	 * 2023.03.08 by kih
	 * @aucObjDscStn 일괄구간 대상구분 추가 
	 */
	private void requestCowInfo(final String naBzplc, final String aucObjDsc, final String aucObjDscStn, final String aucDate,String rgSqno) {
		
		// 단일or일괄 플래그 기본 단일 N
		String stnYn = "N";

		if (!SettingApplication.getInstance().isSingleAuction()) {
			stnYn = "Y"; // 일괄이면 Y
		}
		// 출장우 수
		RequestCowInfoBody cowInfoBody = new RequestCowInfoBody(naBzplc, aucObjDsc, aucObjDscStn, aucDate, "", stnYn, rgSqno);

		ApiUtils.getInstance().requestSelectCowInfo(cowInfoBody, new ActionResultListener<ResponseCowInfo>() {

			@Override
			public void onResponseResult(final ResponseCowInfo result) {

				Platform.runLater(() -> {

					if (result != null && result.getSuccess() && !CommonUtils.getInstance().isListEmpty(result.getData())) {

						new Thread() {
							public void run() {
								try {

									MoveStageUtil.getInstance().onConnectServer(mStage, mIp.getText().toString(), Integer.parseInt(mPort.getText().toString()), GlobalDefine.ADMIN_INFO.adminData.getUserId(),result.getData());

								} catch (Exception e) {
									e.printStackTrace();
									Platform.runLater(() -> {
										showAlertPopupOneButton(mResMsg.getString("msg.connection.fail"));
									});
								}
							}
						}.start();
						
					} else {
						Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.auction.no.data")));
					}

				});
			}

			@Override
			public void onResponseError(String message) {
				mLogger.debug("[error 출장우 조회] : " + message);
				Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("str.api.response.fail")));
			}
		});
	}

	/**
	 * 전역 변수 초기화 경매 회차 정보
	 */
	private void clearGlobalData() {
		GlobalDefine.AUCTION_INFO.auctionRoundData = null;
	}

	/**
	 * 키 설정
	 */
	private void initKeyConfig() {

		Platform.runLater(() -> {
			mStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

				public void handle(KeyEvent ke) {

					if (ke.getCode() == KeyCode.ENTER) {
						onConnection();
						ke.consume();
					}

					if (ke.getCode() == KeyCode.ESCAPE) {
						onCloseApplication();
						ke.consume();
					}
				}
			});
		});
	}

	/**
	 * 환경 설정
	 */
	public void openSettingDialog() {

		if (MoveStageUtil.getInstance().getDialog() != null && MoveStageUtil.getInstance().getDialog().isShowing()) {
			return;
		}

		MoveStageUtil.getInstance().openSettingDialog(mStage, false, new SettingListener() {

			@Override
			public void callBack(Boolean isClose) {

				dismissShowingDialog();
//				toggleAllCowDisable();
			}

			@Override
			public void initServer() {
			}

		}, null, null, null);
	}

	
	private void toggleAllCowDisable() {
		
		if (SettingApplication.getInstance().isSingleAuction()) {
			if(!mAllCowToggleButton.isDisable()) {
				mAllCowToggleButton.setDisable(true);
				
				String aucObjDsc = cowTypeToggleGroup.getSelectedToggle().getUserData().toString();
				
				if(aucObjDsc.equals(Integer.toString(GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_0))) {
					mCalfToggleButton.setSelected(true);
				}
			}
		
		}else {
			if(mAllCowToggleButton.isDisable()) {
				mAllCowToggleButton.setDisable(false);
			}
		}
	}
	/**
	 * Showing dialog Close
	 */
	private void dismissShowingDialog() {
		MoveStageUtil.getInstance().dismissDialog();
		MoveStageUtil.getInstance().setBackStageDisableFalse(mStage);
	}

	/**
	 * 프로그램 종료
	 */
	public void onCloseApplication() {
		Platform.exit();
		System.exit(0);
	}

	/**
	 * 원버튼 팝업
	 *
	 * @param message
	 * @return
	 */
	public void showAlertPopupOneButton(String message) {
		Platform.runLater(() -> {
			CommonUtils.getInstance().dismissLoadingDialog();// dismiss loading
			CommonUtils.getInstance().showAlertPopupOneButton(mStage, message, mResMsg.getString("popup.btn.ok"));
		});
	}

}
