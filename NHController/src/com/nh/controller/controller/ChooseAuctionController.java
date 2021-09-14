package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.controller.model.AuctionRound;
import com.nh.controller.service.AuctionRoundMapperService;
import com.nh.controller.service.EntryInfoMapperService;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.SharedPreference;
import com.sun.jdi.IntegerType;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
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
	private ToggleButton mCalfToggleButton, mFatteningCattleToggleButton, mBreedingCattleToggleButton;

	@FXML // 접속 , 종료
	private Button mBtnConnect, mBtnClose;

	@FXML // 경매 날짜
	private DatePicker mAuctionDatePicker;
	
	@FXML
	private TextField mTestIp, mTestPort;

	private boolean isTest = true; //TEST 지울것
	
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

		mBtnConnect.setOnMouseClicked(event -> onConnection());
		mBtnClose.setOnMouseClicked(event -> onCloseApplication());
		
		test();
	}
	
	
	private void test() {
		mTestIp.setText(GlobalDefine.AUCTION_INFO.AUCTION_HOST);
		mTestPort.setText(Integer.toString(GlobalDefine.AUCTION_INFO.AUCTION_PORT));
		mCalfToggleButton.setSelected(true);
	}

	/**
	 * 소 타입 Toggle
	 */
	private void initCowToggleTypes() {
		// listener
		cowTypeToggleGroup.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> System.out.println("소 타입 => " + newValue.getUserData().toString().trim()));
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
		
//		mAuctionDatePicker.setValue(LocalDate.now());
		mAuctionDatePicker.setValue(LocalDate.of(2021, 7, 16));
	}
	
	/**
	 * 경매 접속
	 */
	public void onConnection() {
		
		Platform.runLater(() ->{
			
		//선택된 경매일
		if(mAuctionDatePicker.getValue() == null) {
			CommonUtils.getInstance().showAlertPopupOneButton(mStage, mResMsg.getString("dialog.auction.no.data"), mResMsg.getString("popup.btn.ok"));
			return;
		}
		
		boolean isAuctionData = requestAuctionRound();
		
		if(isAuctionData) {
			CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("msg.connection"));
			
			if(!isTest) {
				MoveStageUtil.getInstance().onConnectServer(mStage, GlobalDefine.AUCTION_INFO.AUCTION_HOST, GlobalDefine.AUCTION_INFO.AUCTION_PORT, GlobalDefine.ADMIN_INFO.adminData.getUserId());
			}else {
				if(CommonUtils.getInstance().isValidString(mTestIp.getText()) && CommonUtils.getInstance().isValidString(mTestPort.getText())) {
					MoveStageUtil.getInstance().onConnectServer(mStage,mTestIp.getText().toString(), Integer.parseInt(mTestPort.getText().toString()), GlobalDefine.ADMIN_INFO.adminData.getUserId());
				}else {
					CommonUtils.getInstance().showAlertPopupOneButton(mStage, "IP 또는 PORT 정보를 입력해주세요.", mResMsg.getString("popup.btn.ok"));
					CommonUtils.getInstance().dismissLoadingDialog();
				}
				
			}
		
		}else {
			//경매 데이터 없습니다. 팝업
			CommonUtils.getInstance().showAlertPopupOneButton(mStage, mResMsg.getString("dialog.auction.no.data"), mResMsg.getString("popup.btn.ok"));
		}
		});
	}

	/**
	 * 경매 정보 조회
	 */
	private Boolean requestAuctionRound() {

		String aucDate = mAuctionDatePicker.getValue().toString().replace("-", "");
		String aucObjDsc = cowTypeToggleGroup.getSelectedToggle().getUserData().toString();

		AuctionRound auctionRound = new AuctionRound();
		auctionRound.setAucDt(aucDate);
		auctionRound.setAucObjDsc(Integer.parseInt(aucObjDsc));
		auctionRound.setNaBzplc(GlobalDefine.ADMIN_INFO.adminData.getNabzplc());

		
		GlobalDefine.AUCTION_INFO.auctionRoundData = null;
		GlobalDefine.AUCTION_INFO.auctionRoundData = AuctionRoundMapperService.getInstance().obtainAuctionRoundData(auctionRound);

		if(GlobalDefine.AUCTION_INFO.auctionRoundData == null) {
			//경매 회차 존재 하지 않음.
//			CommonUtils.getInstance().showAlertPopupOneButton(mStage, mResMsg.getString("dialog.auction.no.data"), mResMsg.getString("popup.btn.ok"));
			return false;
		}
		
		mLogger.debug("[경매 정보 조회 결과]=> " + GlobalDefine.AUCTION_INFO.auctionRoundData.toString());
		SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_AUCTION_HOUSE_CODE, GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc());
		auctionRound.setNaBzplc(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc());

		int count = EntryInfoMapperService.getInstance().getAllEntryDataCount(auctionRound);
		
		if(count <= 0) {
			//경매 데이터 존재 하지 않음.
			//CommonUtils.getInstance().showAlertPopupOneButton(mStage, mResMsg.getString("dialog.auction.no.data"), mResMsg.getString("popup.btn.ok"));
			return false;
		}
	
		return true;
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
	 * 프로그램 종료
	 */
	public void onCloseApplication() {
		Platform.exit();
		System.exit(0);
	}
}
