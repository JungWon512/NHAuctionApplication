package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.UdpBillBoardStatusListener;
import com.nh.common.interfaces.UdpPdpBoardStatusListener;
import com.nh.controller.interfaces.SettingListener;
import com.nh.controller.netty.AuctionDelegate;
import com.nh.controller.netty.BillboardDelegate1;
import com.nh.controller.netty.BillboardDelegate2;
import com.nh.controller.netty.PdpDelegate;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.SharedPreference;
import com.nh.controller.utils.SoundUtil;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

/**
 * 제어 메인 F8 -> 환경설정
 *
 * @author jhlee
 */
public class SettingController implements Initializable {

	private Stage mStage;
	private ResourceBundle mResMsg;
	private final SharedPreference sharedPreference = SharedPreference.getInstance();
	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@FXML
	private BorderPane mRoot;
	// F5 저장
	@FXML
	private Button mBtnSave,mBtnInitServer;
	// 전광판 설정 IP, PORT
	@FXML
	private TextField mIpBoardTextField1, mPortBoardTextField1, mIpBoardTextField2, mPortBoardTextField2;
	
	@FXML
	private CheckBox mBoardUseNote1, mBoardUseNote2;
	
	// PDP, 응찰석, PDP3 셋톱박스 IP, PORT
	@FXML
	private TextField mIpPdpTextField1, mPortPdpTextField1;
	// 전광판 표출 설정
	@FXML
	private TextField mBoardEntryNumTextField, mBoardKpnTextField, mBoardExhibitorTextField, mBoardRegionTextField, mBoardGenderTextField, mBoardNoteTextField, mBoardWeightTextField, mBoardLowPriceTextField, mBoardMotherTextField, mBoardSucPriceTextField, mBoardPassageTextField,
			mBoardSucBidderTextField, mBoardMaTimeTextField, mBoardDNATextField;
	// PDP 표출 설정
	@FXML
	private TextField mPdpEntryNumTextField, mPdpKpnTextField, mPdpExhibitorTextField, mPdpRegionTextField, mPdpGenderTextField, mPdpNoteTextField, mPdpWeightTextField, mPdpLowPriceTextField, mPdpMotherTextField, mPdpSucPriceTextField, mPdpPassageTextField, mPdpSucBidderTextField,
			mPdpMaTimeTextField, mPdpDNATextField;
	// 상한가/하한가
	@FXML
	private TextField mUpperLimitCalfTextField, mUpperLimitFatteningCattleTextField, mUpperLimitBreedingCattleTextField, mLowerLimitCalfTextField, mLowerLimitFatteningCattleTextField, mLowerLimitBreedingCattleTextField;
	@FXML
	private TextField mUpperLimitGoatTextField,mUpperLimitHorseTextField,mLowerLimitGoatTextField,mLowerLimitHorseTextField;
	
	
	@FXML //상한가 단위
	private Label mUpCalfMoneyUnitLabel,mUpFCattleMoneyUnitLabel,mUpBCattleMoneyUnitLabel,mUpGoatMoneyUnitLabel,mUpHorseMoneyUnitLabel;
	
	@FXML //가격 낮추기 단위
	private Label mLowerCalfMoneyUnitLabel,mLowerFCattleMoneyUnitLabel,mLowerBCattleMoneyUnitLabel,mLowerGoatMoneyUnitLabel,mLowerHorseMoneyUnitLabel;
	
	// 경매종료멘트, 비고 창 설정
	@FXML
	private CheckBox mUseNoteCheckBox;
	// 카운트 (1-9초)
	@FXML
	private TextField mCountTextField;
	// 모바일노출설정 ( 최대 8개 )
	@FXML
	private CheckBox mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mWeightCheckBox, mMotherCheckBox, mPassageCheckBox, mMaTimeCheckBox, mKpnCheckBox, mRegionCheckBox, mNoteCheckBox, mLowPriceCheckBox, mDNACheckBox, mTtsTypeCheckBox;

	@FXML // 동가 재경매 횟수
	private TextField mReAuctionCountTextField;

	@FXML // 음성 경매 대기 시간
	private TextField mSoundAuctionWaitTime;

	@FXML // 동가 재경매,연속경매,음성경부여부,유찰우음성안내여부,유찰시 1회 자동시작
	private CheckBox mUseReAuction, mUseOneAuction, mUseSoundAuction, mUseSoundPendingAuction, mUsePendingAutoStart1;

	@FXML // 경매 타입
	private ToggleGroup auctionTypeToggleGroup;

	@FXML // 경매 타입 (단일 ,일괄)
	private ToggleButton mAuctionTypeSingleToggleButton, mAuctionTypeMultiToggleButton;
	
	@FXML // 하한가 정률 적용 여부 체크박스
	private CheckBox mLowPriceRateCheckBox;

	@FXML
	private TextArea mSoundValTextArea;
	
	/*
	@FXML
	private TextField mSoundRateTextField;
	*/
	@FXML
	private Slider mSoundSpeedSlider;	// by kih

	private final static String[] SHARED_MOBILE_ARRAY = new String[] { SharedPreference.PREFERENCE_SETTING_MOBILE_ENTRYNUM, SharedPreference.PREFERENCE_SETTING_MOBILE_EXHIBITOR, SharedPreference.PREFERENCE_SETTING_MOBILE_GENDER, SharedPreference.PREFERENCE_SETTING_MOBILE_WEIGHT,
			SharedPreference.PREFERENCE_SETTING_MOBILE_MOTHER, SharedPreference.PREFERENCE_SETTING_MOBILE_PASSAGE, SharedPreference.PREFERENCE_SETTING_MOBILE_MATIME, SharedPreference.PREFERENCE_SETTING_MOBILE_KPN, SharedPreference.PREFERENCE_SETTING_MOBILE_REGION,
			SharedPreference.PREFERENCE_SETTING_MOBILE_NOTE, SharedPreference.PREFERENCE_SETTING_MOBILE_LOWPRICE, SharedPreference.PREFERENCE_SETTING_MOBILE_DNA };

	private ArrayList<CheckBox> mobileCheckBoxSelectedList = null;
	private ArrayList<CheckBox> mobileCheckBoxList = null;
	private String auctionToggleType = "Single";
	private SettingListener mSettingListener = null;
	private UdpBillBoardStatusListener mUdpBillBoardStatusListener1 = null;
	private UdpBillBoardStatusListener mUdpBillBoardStatusListener2 = null;
	private UdpPdpBoardStatusListener mUdpPdpBoardStatusListener = null;
	private boolean isDisplayBordConnection = false; //경매일선택 -> 전광판 접속 안함.

	public enum MobileCheckBoxType {
		SETTING_MOBILE_ENTRYNUM("mEntryNumCheckBox"), SETTING_MOBILE_EXHIBITOR("mExhibitorCheckBox"), SETTING_MOBILE_GENDER("mGenderCheckBox"), SETTING_MOBILE_WEIGHT("mWeightCheckBox"), SETTING_MOBILE_MOTHER("mMotherCheckBox"), SETTING_MOBILE_PASSAGE("mPassageCheckBox"),
		SETTING_MOBILE_MATIME("mMaTimeCheckBox"), SETTING_MOBILE_KPN("mKpnCheckBox"), SETTING_MOBILE_REGION("mRegionCheckBox"), SETTING_MOBILE_NOTE("mNoteCheckBox"), SETTING_MOBILE_LOWPRICE("mLowPriceCheckBox"), SETTING_MOBILE_DNA("mDNACheckBox");

		public final String id;

		MobileCheckBoxType(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public static MobileCheckBoxType find(String s) {
			for (MobileCheckBoxType type : MobileCheckBoxType.values()) {
				if (type.getId().equals(s)) {
					return type;
				}
			}
			throw new RuntimeException();
		}
	}

	public enum AuctionToggle {
		SINGLE, MULTI
	}

	/**
	 * setStage
	 *
	 * @param stage
	 */
	public void setStage(Stage stage, boolean isDisplayBordConnection, SettingListener listener , UdpBillBoardStatusListener udpStatusListener1, UdpBillBoardStatusListener udpStatusListener2, UdpPdpBoardStatusListener udpPdpBoardStatusListener) {
		this.mStage = stage;
		this.mSettingListener = listener;
		this.mUdpBillBoardStatusListener1 = udpStatusListener1;
		this.mUdpBillBoardStatusListener2 = udpStatusListener2;
		this.mUdpPdpBoardStatusListener = udpPdpBoardStatusListener;
		this.isDisplayBordConnection = isDisplayBordConnection;
		
		if(!isDisplayBordConnection) {
			mBtnInitServer.setDisable(true);
		}else {
			mAuctionTypeSingleToggleButton.setDisable(true);
			mAuctionTypeMultiToggleButton.setDisable(true);
			mTtsTypeCheckBox.setDisable(true);
		}
		
		// by kih	
		Platform.runLater(() -> mBtnSave.requestFocus());
	}

	/**
	 * 구성 설정
	 */
	public void initConfiguration() {
		// 2023.03.13 설정 창 종료 후, 경매화면이 마우스 드래그 되어 주석ㅊ리 함.  by kih
		//CommonUtils.getInstance().canMoveStage(mStage, null);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// get ResMsg
		if (resources != null) {
			mResMsg = resources;
		}
		initUI();
	}

	private void initUI() {
		
		setNumberFmt();
		
		setMobileCheckBoxLists();
		getAnnounceNoteCheckboxPreference();
		getReAuctionCheckboxPreference();
		getUseOneAuctionCheckboxPreference();
		getSoundAuctionCheckboxPreference();
		getPendingCowSoundAuctionCheckboxPreference();	// 유찰우 안내멘트 사용 
		getPendingCowAutoStart1CheckboxPreference();	// 유찰시 1회 자동시작 by kih 2023.03.10
		getTtsTypeCheckboxPreference();	
		getBoardUseNoteCheckboxPreference();
		setToggleGroups();
		getCountTextField();
		getTextFields();
		initKeyConfig();
		addTextFieldListener();
		updateUseLowPriceRateCheckBox();
		setMoneyUnit();
		mBtnSave.setOnMouseClicked(event -> saveSettings());
		mBtnInitServer.setOnMouseClicked(event -> initServer());		
		mLowPriceRateCheckBox.setOnAction(event -> setMoneyUnit());
	}
	
	private void setNumberFmt() {
		

		UnaryOperator<Change> integerFilterType_1 = change -> {
		    String newText = change.getControlNewText();
		    if (newText.matches("-?([1-9][0-9]*)?")) { 
		        return change;
		    }
		    return null;
		};
		
		
		UnaryOperator<Change> integerFilterType_2 = change -> {
		    String newText = change.getControlNewText();
		    if (newText.matches("-?([0-9]*)?")) { 
		        return change;
		    }
		    return null;
		};
		
		mReAuctionCountTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilterType_1));
		mCountTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilterType_1));
		mSoundAuctionWaitTime.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilterType_1));
		mUpperLimitCalfTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilterType_1));
		mUpperLimitFatteningCattleTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilterType_1));
		mUpperLimitBreedingCattleTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilterType_1));
		
		mUpperLimitGoatTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilterType_1)); 
		mUpperLimitHorseTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilterType_1));
		
		mLowerLimitCalfTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilterType_1));
		mLowerLimitFatteningCattleTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilterType_1));
		mLowerLimitBreedingCattleTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilterType_1));

		mLowerLimitGoatTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilterType_1));
		mLowerLimitHorseTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilterType_1));
		//mSoundRateTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilterType_2));
		
	}

	private void initKeyConfig() {
		Platform.runLater(() -> mRoot.getScene().addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
			if (ke.getCode() == KeyCode.F5) {
				saveSettings();
			}
			if (ke.getCode() == KeyCode.ESCAPE) {
				mSettingListener.callBack(false);
			}
		}));
	}
	
	/**
	 * TextFields Preference에 저장
	 *
	 * @author jhlee
	 */
	private void setTextFields() {
		// 전광판 설정 IP, PORT
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, mIpBoardTextField1.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT1, mPortBoardTextField1.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT2, mIpBoardTextField2.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT2, mPortBoardTextField2.getText().trim());
		// PDP IP, PORT
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT1, mIpPdpTextField1.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT1, mPortPdpTextField1.getText().trim());
		// 전광판 표출 설정
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_ENTRYNUM, mBoardEntryNumTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_KPN, mBoardKpnTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_EXHIBITOR, mBoardExhibitorTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_REGION, mBoardRegionTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_GENDER, mBoardGenderTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_NOTE, mBoardNoteTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_WEIGHT, mBoardWeightTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_LOWPRICE, mBoardLowPriceTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_MOTHER, mBoardMotherTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_SUCPRICE, mBoardSucPriceTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_PASSAGE, mBoardPassageTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_SUCBIDDER, mBoardSucBidderTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_MATIME, mBoardMaTimeTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_DNA, mBoardDNATextField.getText().trim());
		// PDP 표출 설정
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_ENTRYNUM, mPdpEntryNumTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_KPN, mPdpKpnTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_EXHIBITOR, mPdpExhibitorTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_REGION, mPdpRegionTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_GENDER, mPdpGenderTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_NOTE, mPdpNoteTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_WEIGHT, mPdpWeightTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_LOWPRICE, mPdpLowPriceTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_MOTHER, mPdpMotherTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_SUCPRICE, mPdpSucPriceTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_PASSAGE, mPdpPassageTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_SUCBIDDER, mPdpSucBidderTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_MATIME, mPdpMaTimeTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_DNA, mPdpDNATextField.getText().trim());
	
		// 상한가,하한가
		if(CommonUtils.getInstance().isValidString(mUpperLimitCalfTextField.getText().trim())) {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_UPPER_CALF_TEXT, mUpperLimitCalfTextField.getText().trim());
		}else {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_UPPER_CALF_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_UPPER_CALF_TEXT);
		}
		
		if(CommonUtils.getInstance().isValidString(mUpperLimitFatteningCattleTextField.getText().trim())) {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_UPPER_FATTENING_TEXT, mUpperLimitFatteningCattleTextField.getText().trim());
		}else {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_UPPER_FATTENING_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_UPPER_FATTENING_TEXT);
		}
		
		if(CommonUtils.getInstance().isValidString(mUpperLimitBreedingCattleTextField.getText().trim())) {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_UPPER_BREEDING_TEXT, mUpperLimitBreedingCattleTextField.getText().trim());
		}else {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_UPPER_BREEDING_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_UPPER_BREEDING_TEXT);
		}
		
		if(CommonUtils.getInstance().isValidString(mUpperLimitGoatTextField.getText().trim())) {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_UPPER_GOAT_TEXT, mUpperLimitGoatTextField.getText().trim());
		}else {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_UPPER_GOAT_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_UPPER_GOAT_TEXT);
		}
		if(CommonUtils.getInstance().isValidString(mUpperLimitHorseTextField.getText().trim())) {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_UPPER_HORSE_TEXT, mUpperLimitHorseTextField.getText().trim());
		}else {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_UPPER_HORSE_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_UPPER_HORSE_TEXT);
		}
		
		if(CommonUtils.getInstance().isValidString(mLowerLimitCalfTextField.getText().trim())) {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_LOWER_CALF_TEXT, mLowerLimitCalfTextField.getText().trim());
		}else {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_LOWER_CALF_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_LOWER_CALF_TEXT);
		}
		
		if(CommonUtils.getInstance().isValidString(mLowerLimitFatteningCattleTextField.getText().trim())) {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_LOWER_FATTENING_TEXT, mLowerLimitFatteningCattleTextField.getText().trim());
		}else {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_LOWER_FATTENING_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_LOWER_FATTENING_TEXT);
		}
		
		if(CommonUtils.getInstance().isValidString(mLowerLimitBreedingCattleTextField.getText().trim())) {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_LOWER_BREEDING_TEXT, mLowerLimitBreedingCattleTextField.getText().trim());
		}else {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_LOWER_BREEDING_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_LOWER_BREEDING_TEXT);
		}
		if(CommonUtils.getInstance().isValidString(mLowerLimitGoatTextField.getText().trim())) {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_LOWER_GOAT_TEXT, mLowerLimitGoatTextField.getText().trim());
		}else {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_LOWER_GOAT_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_LOWER_GOAT_TEXT);
		}
		if(CommonUtils.getInstance().isValidString(mLowerLimitHorseTextField.getText().trim())) {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_LOWER_HORSE_TEXT, mLowerLimitHorseTextField.getText().trim());
		}else {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_LOWER_HORSE_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_LOWER_HORSE_TEXT);
		}
		
		// 동가재경매 횟수
		String reAuctionCount = mReAuctionCountTextField.getText().trim();

		if (reAuctionCount == null || reAuctionCount.equals("0") || reAuctionCount.isEmpty()) {
			reAuctionCount = SettingApplication.getInstance().DEFAULT_SETTING_RE_AUCTION_COUNT;
		}
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_RE_AUCTION_COUNT, reAuctionCount);

		// 대기시간
		String soundAuctionWaitTime = mSoundAuctionWaitTime.getText().trim();

		if (soundAuctionWaitTime == null || soundAuctionWaitTime.equals("0") || soundAuctionWaitTime.isEmpty()) {
			soundAuctionWaitTime = SettingApplication.getInstance().DEFAULT_SETTING_SOUND_AUCTION_WAIT_TIME;
		}
		// 음성경매 대기 시간
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_SOUND_AUCTION_WAIT_TIME, soundAuctionWaitTime);

		// TTS 인증 처리
		if (!SettingApplication.getInstance().isTtsType()) {
			SoundUtil.getInstance().initCertification(mSoundValTextArea.getText());
		}
	}

	/**
	 * TextFields Preference에서 가져오기
	 *
	 * @author jhlee
	 */
	private void getTextFields() {
		
		// 전광판 설정 IP, PORT
		mIpBoardTextField1.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, ""));
		mPortBoardTextField1.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT1, ""));
		mIpBoardTextField2.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT2, ""));
		mPortBoardTextField2.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT2, ""));
		// PDP, 응찰석, PDP3 셋톱박스 IP, PORT
		mIpPdpTextField1.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT1, ""));
		mPortPdpTextField1.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT1, ""));
		// 전광판 표출 설정
		mBoardEntryNumTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_ENTRYNUM, ""));
		mBoardKpnTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_KPN, ""));
		mBoardExhibitorTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_EXHIBITOR, ""));
		mBoardRegionTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_REGION, ""));
		mBoardGenderTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_GENDER, ""));
		mBoardNoteTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_NOTE, ""));
		mBoardWeightTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_WEIGHT, ""));
		mBoardLowPriceTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_LOWPRICE, ""));
		mBoardMotherTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_MOTHER, ""));
		mBoardSucPriceTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_SUCPRICE, ""));
		mBoardPassageTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_PASSAGE, ""));
		mBoardSucBidderTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_SUCBIDDER, ""));
		mBoardMaTimeTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_MATIME, ""));
		mBoardDNATextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_DNA, ""));
		// PDP 표출 설정
		mPdpEntryNumTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_ENTRYNUM, ""));
		mPdpKpnTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_KPN, ""));
		mPdpExhibitorTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_EXHIBITOR, ""));
		mPdpRegionTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_REGION, ""));
		mPdpGenderTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_GENDER, ""));
		mPdpNoteTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_NOTE, ""));
		mPdpWeightTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_WEIGHT, ""));
		mPdpLowPriceTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_LOWPRICE, ""));
		mPdpMotherTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_MOTHER, ""));
		mPdpSucPriceTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_SUCPRICE, ""));
		mPdpPassageTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_PASSAGE, ""));
		mPdpSucBidderTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_SUCBIDDER, ""));
		mPdpMaTimeTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_MATIME, ""));
		mPdpDNATextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_DNA, ""));
		// 상한가/하한가
		mUpperLimitCalfTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_UPPER_CALF_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_UPPER_CALF_TEXT));
		mUpperLimitFatteningCattleTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_UPPER_FATTENING_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_UPPER_FATTENING_TEXT));
		mUpperLimitBreedingCattleTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_UPPER_BREEDING_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_UPPER_BREEDING_TEXT));		
		mLowerLimitCalfTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_LOWER_CALF_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_LOWER_CALF_TEXT));
		mLowerLimitFatteningCattleTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_LOWER_FATTENING_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_LOWER_FATTENING_TEXT));
		mLowerLimitBreedingCattleTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_LOWER_BREEDING_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_LOWER_BREEDING_TEXT));

		mUpperLimitGoatTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_UPPER_GOAT_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_UPPER_GOAT_TEXT));
		mUpperLimitHorseTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_UPPER_HORSE_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_UPPER_HORSE_TEXT));
		mLowerLimitGoatTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_LOWER_GOAT_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_LOWER_GOAT_TEXT));
		mLowerLimitHorseTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_LOWER_HORSE_TEXT, SettingApplication.getInstance().DEFAULT_SETTING_LOWER_HORSE_TEXT));
		// 동가재경매 횟수
		mReAuctionCountTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_RE_AUCTION_COUNT, SettingApplication.getInstance().DEFAULT_SETTING_RE_AUCTION_COUNT));
		// 대기시간
		mSoundAuctionWaitTime.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_SOUND_AUCTION_WAIT_TIME, SettingApplication.getInstance().DEFAULT_SETTING_SOUND_AUCTION_WAIT_TIME));
		//음성 설정 파일
		mSoundValTextArea.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_SOUND_CONFIG, SettingApplication.getInstance().DEFAULT_SETTING_SOUND_CONFIG));
		//음성재생속도
		//mSoundRateTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_SOUND_RATE, SettingApplication.getInstance().DEFAULT_SETTING_SOUND_RATE));
		try {	// by kih
			String sndSpdVal = sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_SOUND_RATE, SettingApplication.getInstance().DEFAULT_SETTING_SOUND_RATE);
			Double d = Double.parseDouble(sndSpdVal);
			mSoundSpeedSlider.setValue(d);
		}
		catch(Exception e) {
			mSoundSpeedSlider.setValue(0);
		}
	}

	/**
	 * 음성재생속도, 비고 창 value setting
	 *
	 * @author jhlee
	 */
	private void setAnnounceNoteCheckboxPreference() {

		// 기존 저장값
		String savedSoundRate = sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_SOUND_RATE, SettingApplication.getInstance().DEFAULT_SETTING_SOUND_RATE);

		/*
		if(CommonUtils.getInstance().isValidString(mSoundRateTextField.getText())) {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_SOUND_RATE, mSoundRateTextField.getText().trim());
		}else {
			sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_SOUND_RATE, "0");
		}
		*/
		// by kih
		Double d = mSoundSpeedSlider.getValue();
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_SOUND_RATE, d.toString());

		//현재 설정값
		String currentSoundRate = sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_SOUND_RATE, SettingApplication.getInstance().DEFAULT_SETTING_SOUND_RATE);

		//기존과 현재 설정값이 다르면 음성 설정 문구 재반영
		if(!savedSoundRate.equals(currentSoundRate)) {
			mLogger.debug("[재생 속도 값이 변경됐습니다.음성 설정 속도를 반영합니다.]");
			if (!SettingApplication.getInstance().isTtsType()) {
				SoundUtil.getInstance().soundSettingSpeedChanged();
			}
		}

		//비고 사용 여부
		sharedPreference.setBoolean(SharedPreference.PREFERENCE_SETTING_NOTE, (mUseNoteCheckBox.isSelected()));

	}

	/**
	 * 음성재생속도, 비고 창 value 가져오기
	 *
	 * @author jhlee
	 */
	private void getAnnounceNoteCheckboxPreference() {
		boolean isNote = sharedPreference.getBoolean(SharedPreference.PREFERENCE_SETTING_NOTE, true);
		mUseNoteCheckBox.setSelected(isNote);
	}
	
	/**
	 * 전광판 비고 흐름 여부value 가져오기
	 *
	 * @author jhlee
	 */
	private void getBoardUseNoteCheckboxPreference() {
		
		boolean isUseNote_1 = sharedPreference.getBoolean(SharedPreference.PREFERENCE_BOARD_USE_NOTE_1, false);
		boolean isUseNote_2 = sharedPreference.getBoolean(SharedPreference.PREFERENCE_BOARD_USE_NOTE_2, false);
		
		mBoardUseNote1.setSelected(isUseNote_1);
		mBoardUseNote2.setSelected(isUseNote_2);
	}
	
	/**
	 * 전광판 비고 흐름 여부 value setting
	 *
	 * @author jhlee
	 */
	private void setBoardUseNoteCheckboxPreference() {
		sharedPreference.setBoolean(SharedPreference.PREFERENCE_BOARD_USE_NOTE_1, (mBoardUseNote1.isSelected()));
		sharedPreference.setBoolean(SharedPreference.PREFERENCE_BOARD_USE_NOTE_2, (mBoardUseNote2.isSelected()));
	}

	/**
	 * 동가 재경매 value 가져오기
	 *
	 * @author jhlee
	 */
	private void getReAuctionCheckboxPreference() {
		boolean isReAuction = sharedPreference.getBoolean(SharedPreference.PREFERENCE_SETTING_RE_AUCTION_CHECK, false);
		mUseReAuction.setSelected(isReAuction);
	}

	/**
	 * 동가 재경매 저장
	 *
	 * @author jhlee
	 */
	private void setReAuctionCheckboxPreference() {
		sharedPreference.setBoolean(SharedPreference.PREFERENCE_SETTING_RE_AUCTION_CHECK, (mUseReAuction.isSelected()));
	}

	/**
	 * 연속경매- 하나씩 진행 value 가져오기
	 *
	 * @author jhlee
	 */
	private void getUseOneAuctionCheckboxPreference() {
		boolean isOnePlayAuction = sharedPreference.getBoolean(SharedPreference.PREFERENCE_SETTING_USE_ONE_AUCTION, false);
		mUseOneAuction.setSelected(isOnePlayAuction);
	}

	/**
	 * 연속경매 하나씩 진행 저장
	 *
	 * @author jhlee
	 */
	private void setUseOneAuctionCheckboxPreference() {
		sharedPreference.setBoolean(SharedPreference.PREFERENCE_SETTING_USE_ONE_AUCTION, (mUseOneAuction.isSelected()));
	}

	/**
	 * 음성경부여부 value 가져오기
	 *
	 * @author jhlee
	 */
	private void getSoundAuctionCheckboxPreference() {
		boolean isSoundAuction = sharedPreference.getBoolean(SharedPreference.PREFERENCE_SETTING_USE_SOUND_AUCTION, false);
		mUseSoundAuction.setSelected(isSoundAuction);
	}

	/**
	 * 음성경부여부 저장
	 *
	 * @author jhlee
	 */
	private void setSoundAuctionCheckboxPreference() {
		sharedPreference.setBoolean(SharedPreference.PREFERENCE_SETTING_USE_SOUND_AUCTION, (mUseSoundAuction.isSelected()));
	}
	
	/**
	 * 유찰우 음성 안내 사용 여부 저장
	 */
	private void setPendingCowSoundAuctionCheckboxPreference() {
		sharedPreference.setBoolean(SharedPreference.PREFERENCE_SETTING_USE_PENDING_COW_SOUND_AUCTION, (mUseSoundPendingAuction.isSelected()));
	}
	
	/**
	 * 유찰우 음성 안내 사용 여부 value 가져오기
	 *
	 */
	private void getPendingCowSoundAuctionCheckboxPreference() {
		boolean isSoundAuction = sharedPreference.getBoolean(SharedPreference.PREFERENCE_SETTING_USE_PENDING_COW_SOUND_AUCTION, false);
		mUseSoundPendingAuction.setSelected(isSoundAuction);
	}
	
	/**
	 * 유찰시 1회 자동시작 여부 저장
	 */
	private void setPendingCowAutoStart1CheckboxPreference() {
		sharedPreference.setBoolean(SharedPreference.PREFERENCE_SETTING_USE_PENDING_AUTO_START1, (mUsePendingAutoStart1.isSelected()));
	}	
	
	/**
	 * 유찰시 1회 자동시작 여부 value 가져오기
	 *
	 */
	private void getPendingCowAutoStart1CheckboxPreference() {
		boolean isPendingCowAutoStart1 = sharedPreference.getBoolean(SharedPreference.PREFERENCE_SETTING_USE_PENDING_AUTO_START1, false);
		mUsePendingAutoStart1.setSelected(isPendingCowAutoStart1);
		
	}
	
	/**
	 * 내부TTS엔진 사용여부 value 가져오기
	 *
	 * @author jspark
	 */
	private void getTtsTypeCheckboxPreference() {
		boolean isTtsType = sharedPreference.getBoolean(SharedPreference.PREFERENCE_SETTING_USE_TTS_TYPE, false);
		mTtsTypeCheckBox.setSelected(isTtsType);
	}

	/**
	 * 내부TTS엔진 사용여부 저장
	 *
	 * @author jspark
	 */
	private void setTtsTypeCheckboxPreference() {
		sharedPreference.setBoolean(SharedPreference.PREFERENCE_SETTING_USE_TTS_TYPE, (mTtsTypeCheckBox.isSelected()));
	}
	
	/**
	 * 카운트 설정 Preference에 저장
	 *
	 * @author jhlee
	 */
	private void setCountTextField() {
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_COUNTDOWN, mCountTextField.getText().trim());
	}

	/**
	 * 카운트 설정 가져오기
	 *
	 * @author jhlee
	 */
	private void getCountTextField() {
		String second = sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_COUNTDOWN, "5");
		mCountTextField.setText(second);
	}

	/**
	 * toggle group setting
	 *
	 * @author jhlee
	 */
	private void setToggleGroups() {

		auctionTypeToggleGroup.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
			
			if(observableValue != null) {
				mLogger.debug("" + observableValue.getValue());
			}
			
			if(oldValue != null) {
				mLogger.debug("" + oldValue.toString());
			}
			
			if(newValue != null) {
				mLogger.debug("" + newValue.toString());
			}
				
			auctionToggleType = newValue.getUserData().toString().trim();
		});

		getToggleTypes();
	}

	/**
	 * toggle type Preference에 저장
	 *
	 * @author jhlee
	 */
	private void setToggleTypes() {
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_AUCTION_TOGGLE_TYPE, auctionToggleType);
	}

	/**
	 * toggle type Preference에서 가져오기
	 *
	 * @author jhlee
	 */
	private void getToggleTypes() {
		
		String auctionToggle = sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_AUCTION_TOGGLE_TYPE, "Single");

		mLogger.debug("auctionToggle : " + auctionToggle);
		switch (AuctionToggle.valueOf(auctionToggle.toUpperCase())) {
		case SINGLE -> mAuctionTypeSingleToggleButton.setSelected(true);
		case MULTI -> mAuctionTypeMultiToggleButton.setSelected(true);
		}

	}

	/**
	 * 모바일 노출설정 checkbox init
	 *
	 * @author jhlee
	 */
	private void setMobileCheckBoxLists() {
		mobileCheckBoxList = new ArrayList<>(Arrays.asList(mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mWeightCheckBox, mMotherCheckBox, mPassageCheckBox, mMaTimeCheckBox, mKpnCheckBox, mRegionCheckBox, mNoteCheckBox, mLowPriceCheckBox, mDNACheckBox));
		mobileCheckBoxList.forEach((checkBox -> checkBox.setOnAction(e -> {
			handleMobileCheckBox(checkBox);
			if (mobileCheckBoxSelectedList.size() > 10) {
				mobileCheckBoxSelectedList.remove(checkBox);
				checkBox.setSelected(false);
				showAlertMobileSettingLimit();
			}
		})));
		getAllMobileCheckboxPreference();
		mobileCheckBoxSelectedList.forEach(checkBox -> checkBox.setSelected(true));
	}

	/**
	 * 모바일 노출설정 checkBox Setting
	 *
	 * @author jhlee
	 */
	private void handleMobileCheckBox(CheckBox checkBox) {
		if (checkBox.isSelected()) {
			mobileCheckBoxSelectedList.add(checkBox);
			checkBox.setSelected(true);
		} else {
			mobileCheckBoxSelectedList.remove(checkBox);
			checkBox.setSelected(false);
		}
	}

	/**
	 * 하한가 낮추기 정률 사용 여부 설정 값 저장
	 * 
	 * @author PJS
	 */
	private void setUseLowPriceRateCheckBox() {
		sharedPreference.setBoolean(SharedPreference.PREFERENCE_SETTING_USE_LOW_PRICE_RATE, mLowPriceRateCheckBox.isSelected());
	}
	
	/**
	 * 하한가 낮추기 정률 사용 여부에 따른 CheckBox 상태 업데이트
	 * 
	 * @author PJS
	 */
	private void updateUseLowPriceRateCheckBox() {
		mLowPriceRateCheckBox.setSelected(sharedPreference.getBoolean(SharedPreference.PREFERENCE_SETTING_USE_LOW_PRICE_RATE, SettingApplication.getInstance().DEFAULT_SETTING_USE_LOW_PRICE_RATE));
	}
	
	/**
	 * 모바일 노출설정 Preference에 저장
	 *
	 * @param mobileCheckBoxSelectedList 선택된 모바일 노출설정
	 * @author jhlee
	 */
	private void setMobileCheckboxPreference(ArrayList<CheckBox> mobileCheckBoxSelectedList) {
		for (String key : SHARED_MOBILE_ARRAY) { // 모바일 노출설정 Preference 초기화
			sharedPreference.setString(key, "N");
		}

		for (CheckBox checkBox : mobileCheckBoxSelectedList) {
			switch (MobileCheckBoxType.find(checkBox.getId())) {
			case SETTING_MOBILE_ENTRYNUM -> sharedPreference.setString(SHARED_MOBILE_ARRAY[0], "Y");
			case SETTING_MOBILE_EXHIBITOR -> sharedPreference.setString(SHARED_MOBILE_ARRAY[1], "Y");
			case SETTING_MOBILE_GENDER -> sharedPreference.setString(SHARED_MOBILE_ARRAY[2], "Y");
			case SETTING_MOBILE_WEIGHT -> sharedPreference.setString(SHARED_MOBILE_ARRAY[3], "Y");
			case SETTING_MOBILE_MOTHER -> sharedPreference.setString(SHARED_MOBILE_ARRAY[4], "Y");
			case SETTING_MOBILE_PASSAGE -> sharedPreference.setString(SHARED_MOBILE_ARRAY[5], "Y");
			case SETTING_MOBILE_MATIME -> sharedPreference.setString(SHARED_MOBILE_ARRAY[6], "Y");
			case SETTING_MOBILE_KPN -> sharedPreference.setString(SHARED_MOBILE_ARRAY[7], "Y");
			case SETTING_MOBILE_REGION -> sharedPreference.setString(SHARED_MOBILE_ARRAY[8], "Y");
			case SETTING_MOBILE_NOTE -> sharedPreference.setString(SHARED_MOBILE_ARRAY[9], "Y");
			case SETTING_MOBILE_LOWPRICE -> sharedPreference.setString(SHARED_MOBILE_ARRAY[10], "Y");
			case SETTING_MOBILE_DNA -> sharedPreference.setString(SHARED_MOBILE_ARRAY[11], "Y");
			default -> mLogger.debug("setMobileCheckboxPreference default");
			}
		}
	}

	/**
	 * 모바일 노출설정 Preference에서 가져오기 + Default값 셋팅
	 *
	 * @author jhlee
	 */
	private void getAllMobileCheckboxPreference() {
		Map<String, String> tempMap = new HashMap<>();
		mobileCheckBoxSelectedList = new ArrayList<>();

		for (CheckBox checkBox : mobileCheckBoxList) {
			switch (MobileCheckBoxType.find(checkBox.getId())) {
			case SETTING_MOBILE_ENTRYNUM -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_ENTRYNUM.getId(), sharedPreference.getString(SHARED_MOBILE_ARRAY[0], "N"));
			case SETTING_MOBILE_EXHIBITOR -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_EXHIBITOR.getId(), sharedPreference.getString(SHARED_MOBILE_ARRAY[1], "N"));
			case SETTING_MOBILE_GENDER -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_GENDER.getId(), sharedPreference.getString(SHARED_MOBILE_ARRAY[2], "N"));
			case SETTING_MOBILE_WEIGHT -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_WEIGHT.getId(), sharedPreference.getString(SHARED_MOBILE_ARRAY[3], "N"));
			case SETTING_MOBILE_MOTHER -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_MOTHER.getId(), sharedPreference.getString(SHARED_MOBILE_ARRAY[4], "N"));
			case SETTING_MOBILE_PASSAGE -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_PASSAGE.getId(), sharedPreference.getString(SHARED_MOBILE_ARRAY[5], "N"));
			case SETTING_MOBILE_MATIME -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_MATIME.getId(), sharedPreference.getString(SHARED_MOBILE_ARRAY[6], "N"));
			case SETTING_MOBILE_KPN -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_KPN.getId(), sharedPreference.getString(SHARED_MOBILE_ARRAY[7], "N"));
			case SETTING_MOBILE_REGION -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_REGION.getId(), sharedPreference.getString(SHARED_MOBILE_ARRAY[8], "N"));
			case SETTING_MOBILE_NOTE -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_NOTE.getId(), sharedPreference.getString(SHARED_MOBILE_ARRAY[9], "N"));
			case SETTING_MOBILE_LOWPRICE -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_LOWPRICE.getId(), sharedPreference.getString(SHARED_MOBILE_ARRAY[10], "N"));
			case SETTING_MOBILE_DNA -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_DNA.getId(), sharedPreference.getString(SHARED_MOBILE_ARRAY[11], "N"));
			default -> mLogger.debug("getAllMobileCheckboxPreference default");
			}
		}

		tempMap.forEach((key, value) -> {
			if (value.equals("Y")) {
				mobileCheckBoxSelectedList.add((CheckBox) mRoot.lookup("#" + key));
			}
		});

		if (mobileCheckBoxSelectedList.isEmpty()) {
			// default 값 셋팅
			setMobileCheckBoxDefaultValue();
		}
	}

	/**
	 * 모바일 노출설정 Default값 셋팅 TODO: param 추가
	 *
	 * @author jhlee
	 */
	private void setMobileCheckBoxDefaultValue() {
		mobileCheckBoxSelectedList = new ArrayList<>(Arrays.asList(mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mWeightCheckBox));
	}

	/**
	 * 개체 경매 단위, 최저 가격 낮추기 단위
	 */
	private void setMoneyUnit() {
		
		if(GlobalDefine.AUCTION_INFO.auctionRoundData != null) {
			mUpperLimitCalfTextField.setDisable(false);
			mUpperLimitFatteningCattleTextField.setDisable(false);
			mUpperLimitBreedingCattleTextField.setDisable(false);
			mLowerLimitCalfTextField.setDisable(false);
			mLowerLimitFatteningCattleTextField.setDisable(false);
			mLowerLimitBreedingCattleTextField.setDisable(false);

			mUpperLimitGoatTextField.setDisable(false);
			mUpperLimitHorseTextField.setDisable(false);
			mLowerLimitGoatTextField.setDisable(false);
			mLowerLimitHorseTextField.setDisable(false);
			
			if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice1() == 10000) {
				mUpCalfMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				
				if (mLowPriceRateCheckBox.isSelected()) {
					mLowerCalfMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				} else {
					mLowerCalfMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				}
			}else if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice1() == 1000) {		// 2024.04.03 by kih 천단위 어나운싱 적용 
				mUpCalfMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.thousand.won"));
				
				if (mLowPriceRateCheckBox.isSelected()) {
					mLowerCalfMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				} else {
					mLowerCalfMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.thousand.won"));
				}
			}else {
				mUpCalfMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				
				if (mLowPriceRateCheckBox.isSelected()) {
					mLowerCalfMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				} else {
					mLowerCalfMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				}
			}
			
			if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice2() == 10000) {
				mUpFCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				
				if (mLowPriceRateCheckBox.isSelected()) {
					mLowerFCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				} else {
					mLowerFCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				}
			}else if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice2() == 1000) { 		// 2024.04.03 by kih 천단위 어나운싱 적용  
				mUpFCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.thousand.won"));
				
				if (mLowPriceRateCheckBox.isSelected()) {
					mLowerFCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				} else {
					mLowerFCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.thousand.won"));
				}
			}else {
				mUpFCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				
				if (mLowPriceRateCheckBox.isSelected()) {
					mLowerFCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				} else {
					mLowerFCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				}
				
			}
			
			if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice3() == 10000) {
				mUpBCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				
				if (mLowPriceRateCheckBox.isSelected()) {
					mLowerBCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				} else {
					mLowerBCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				}
			}else if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice3() == 1000) {		// 2024.04.03 by kih 천단위 어나운싱 적용 
				mUpBCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.thousand.won"));
				
				if (mLowPriceRateCheckBox.isSelected()) {
					mLowerBCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				} else {
					mLowerBCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.thousand.won"));
				}
			}else {
				mUpBCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				
				if (mLowPriceRateCheckBox.isSelected()) {
					mLowerBCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				} else {
					mLowerBCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				}
			}

			if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice5() == 1000) {
				mLowerGoatMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.thousand.won"));
				
				if (mLowPriceRateCheckBox.isSelected()) {
					mLowerGoatMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				} else {
					mLowerGoatMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.thousand.won"));
				}
			}else if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice5() == 1) {
				mLowerGoatMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				
				if (mLowPriceRateCheckBox.isSelected()) {
					mLowerGoatMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				} else {
					mLowerGoatMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				}
			}else {
				mLowerGoatMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				
				if (mLowPriceRateCheckBox.isSelected()) {
					mLowerGoatMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				} else {
					mLowerGoatMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				}
			}
			if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice6() == 1000) {
				mLowerHorseMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.thousand.won"));
				
				if (mLowPriceRateCheckBox.isSelected()) {
					mLowerHorseMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				} else {
					mLowerHorseMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.thousand.won"));
				}
			} else if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice6() == 1) {
				mLowerHorseMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				
				if (mLowPriceRateCheckBox.isSelected()) {
					mLowerHorseMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				} else {
					mLowerHorseMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				}
			}else {
				mLowerHorseMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				
				if (mLowPriceRateCheckBox.isSelected()) {
					mLowerHorseMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				} else {
					mLowerHorseMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				}
			}
			
		}else {
			
			//기본 단위 가격. 송아지,번식우(만원)/비육우(원)
			
			mUpperLimitCalfTextField.setDisable(true);
			mUpperLimitFatteningCattleTextField.setDisable(true);
			mUpperLimitBreedingCattleTextField.setDisable(true);
			mLowerLimitCalfTextField.setDisable(true);
			mLowerLimitFatteningCattleTextField.setDisable(true);
			mLowerLimitBreedingCattleTextField.setDisable(true);
			mLowerLimitGoatTextField.setDisable(true);
			mLowerLimitHorseTextField.setDisable(true);
			
			if (mLowPriceRateCheckBox.isSelected()) {
				mUpCalfMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				mLowerCalfMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				
				mUpFCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				mLowerFCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				
				mUpBCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				mLowerBCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));

				mUpGoatMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				mLowerGoatMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
				mUpHorseMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				mLowerHorseMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.rate"));
			} else {
				mUpCalfMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				mLowerCalfMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				
				mUpFCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				mLowerFCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				
				mUpBCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				mLowerBCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));

				mUpGoatMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				mLowerGoatMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				mUpHorseMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				mLowerHorseMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
			}
		}
		
	}
	
	/**
	 * valid listener
	 */
	private void addTextFieldListener() {
	
		//재경매 횟수
		mReAuctionCountTextField.textProperty().addListener(new ChangeListener<String>() {
			
			final String max = SettingApplication.getInstance().DEFAULT_SETTING_RE_AUCTION_COUNT_MAX;
			
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = "";
					
					if(newValue.length() == 2){
						tmpStr = newValue.substring(1);
					}else {
						tmpStr = newValue;
					}
		
					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else 	if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_RE_AUCTION_COUNT;
					}
						
					mReAuctionCountTextField.setText(tmpStr);		
				}
			}
		});
	
		//경매 종료 카운트
		mCountTextField.textProperty().addListener(new ChangeListener<String>() {
			
			final String max = SettingApplication.getInstance().DEFAULT_SETTING_COUNTDOWN_MAX;
			
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {

				if (CommonUtils.getInstance().isValidString(newValue)) {
				
					String tmpStr = "";
					
					if(newValue.length() == 2){
						tmpStr = newValue.substring(1);
					}else {
						tmpStr = newValue;
					}
		
					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else 	if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_COUNTDOWN;
					}
						
					mCountTextField.setText(tmpStr);		
				}

			}
		});

		//경매 종료 타이머 대기시간
		mSoundAuctionWaitTime.textProperty().addListener(new ChangeListener<String>() {
			
			final 	String max = SettingApplication.getInstance().DEFAULT_SETTING_SOUND_AUCTION_WAIT_TIME_MAX;
			
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;
					
					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_SOUND_AUCTION_WAIT_TIME;
					}
					mSoundAuctionWaitTime.setText(tmpStr);	
				}
			}
		});
		
		//송아지 응찰 상한가
		mUpperLimitCalfTextField.textProperty().addListener(new ChangeListener<String>() {
			
			final String max = SettingApplication.getInstance().DEFAULT_SETTING_UPPER_CFB_MAX;
			
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;
					
					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_UPPER_CALF_TEXT;
					}
					mUpperLimitCalfTextField.setText(tmpStr);	
				}
			}
		});
		//비육우 응찰 상한가
		mUpperLimitFatteningCattleTextField.textProperty().addListener(new ChangeListener<String>() {
			
			final String max = SettingApplication.getInstance().DEFAULT_SETTING_UPPER_CFB_MAX;
			
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;

					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_UPPER_FATTENING_TEXT;
					}
					mUpperLimitFatteningCattleTextField.setText(tmpStr);	
				}
			}
		});
		
		//번식우 응찰 상한가
		mUpperLimitBreedingCattleTextField.textProperty().addListener(new ChangeListener<String>() {
			
			final String max = SettingApplication.getInstance().DEFAULT_SETTING_UPPER_CFB_MAX;
			
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;
					
					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_UPPER_BREEDING_TEXT;
					}
					mUpperLimitBreedingCattleTextField.setText(tmpStr);	
				}
			}
		});
	
		//송아지 가격 낮춤가
		mLowerLimitCalfTextField.textProperty().addListener(new ChangeListener<String>() {
			
			final String max = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_CALF_TEXT_MAX;
			
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;
					
					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_CALF_TEXT;
					}
					mLowerLimitCalfTextField.setText(tmpStr);	
				}
			}
		});
		
		// 비육우 가격 낮춤가
		mLowerLimitFatteningCattleTextField.textProperty().addListener(new ChangeListener<String>() {
			
			final 	String max = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_CALF_TEXT_MAX;
			
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;

					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_FATTENING_TEXT;
					}
					mLowerLimitFatteningCattleTextField.setText(tmpStr);	
				}
			}
		});
		
		//번식우 가격 낮춤가
		mLowerLimitBreedingCattleTextField.textProperty().addListener(new ChangeListener<String>() {
			
			final String max = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_CALF_TEXT_MAX;
			
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;

					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_BREEDING_TEXT;
					}
					mLowerLimitBreedingCattleTextField.setText(tmpStr);	
				}
			}
		});

		
		//염소 가격 낮춤가
		mLowerLimitGoatTextField.textProperty().addListener(new ChangeListener<String>() {
			
			final String max = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_CALF_TEXT_MAX;
			
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;

					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_GOAT_TEXT;
					}
					mLowerLimitGoatTextField.setText(tmpStr);	
				}
			}
		});
		
		//말 가격 낮춤가
		mLowerLimitHorseTextField.textProperty().addListener(new ChangeListener<String>() {
			
			final String max = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_CALF_TEXT_MAX;
			
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;

					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_HORSE_TEXT;
					}
					mLowerLimitHorseTextField.setText(tmpStr);	
				}
			}
		});
		/*
		//음성 재생 속도
		mSoundRateTextField.textProperty().addListener(new ChangeListener<String>() {
			
			final String max = SettingApplication.getInstance().DEFAULT_SETTING_SOUND_RATE_MAX;
			
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {

				if (CommonUtils.getInstance().isValidString(newValue)) {

					String tmpStr = "";
					
					if(newValue.length() == 2){
						tmpStr = newValue.substring(1);
					}else {
						tmpStr = newValue;
					}
					
					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else 	if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_SOUND_RATE;
					}
						
					mSoundRateTextField.setText(tmpStr);		
				}
			}
		});
		*/
	}

	/**
	 * 설정 Preference에 저장
	 *
	 * @author jhlee
	 */
	private void saveSettings() {
		
		if (isValid()) {
			

			try {
		
			mLogger.debug("save Settings...");
			setTextFields();
			setAnnounceNoteCheckboxPreference();
			setReAuctionCheckboxPreference();
			setUseOneAuctionCheckboxPreference();
			setSoundAuctionCheckboxPreference();
			setPendingCowSoundAuctionCheckboxPreference();	// 유찰우 음성안내 사용 
			setPendingCowAutoStart1CheckboxPreference();	// 유찰시 1회 자동시작 by kih 2023.03.10
			setTtsTypeCheckboxPreference();
			setBoardUseNoteCheckboxPreference();
			setCountTextField();
			setMobileCheckboxPreference(mobileCheckBoxSelectedList);
			setToggleTypes();
			setUseLowPriceRateCheckBox();		
			
			mLogger.debug("auctionToggleType : " + auctionToggleType);
	
			
			// 서버에 edit setting 전송
			if(GlobalDefine.AUCTION_INFO.auctionRoundData != null && GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc() != null) {
				mLogger.debug(mResMsg.getString("msg.auction.send.setting.info") + AuctionDelegate.getInstance().onSendSettingInfo(SettingApplication.getInstance().getSettingInfo()));
			}
	
			
			if (this.isDisplayBordConnection) {

					// UDP 전광판1
					if (SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, "") != null && !SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, "").isEmpty()) {
	
						if (BillboardDelegate1.getInstance().isActive()) {
							BillboardDelegate1.getInstance().onDisconnect(new NettyClientShutDownListener() {
	
								@Override
								public void onShutDown(int port) {
									BillboardDelegate1.getInstance().createClients(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, ""), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT1, ""),mUdpBillBoardStatusListener1);
									
									// 전광판 셋팅
									BillboardDelegate1.getInstance().initBillboard();
								}
							});
						} else {
							BillboardDelegate1.getInstance().createClients(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, ""), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT1, ""),mUdpBillBoardStatusListener1);
							
							// 전광판 셋팅
							BillboardDelegate1.getInstance().initBillboard();
						}
					} else {
						BillboardDelegate1.getInstance().onDisconnect(new NettyClientShutDownListener() {
							
							@Override
							public void onShutDown(int port) {
								mLogger.debug("Billboard UDP Disconnect");
							}
						});
					}
	
					// UDP 전광판2
					if (SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT2, "") != null && !SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT2, "").isEmpty()) {
	
						if (BillboardDelegate2.getInstance().isActive()) {
							BillboardDelegate2.getInstance().onDisconnect(new NettyClientShutDownListener() {
	
								@Override
								public void onShutDown(int port) {
									BillboardDelegate2.getInstance().createClients(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT2, ""), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT2, ""),mUdpBillBoardStatusListener2);
									
									// 전광판 셋팅
									BillboardDelegate2.getInstance().initBillboard();
								}
							});
						} else {
							BillboardDelegate2.getInstance().createClients(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT2, ""), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT2, ""),mUdpBillBoardStatusListener2);
							
							// 전광판 셋팅
							BillboardDelegate2.getInstance().initBillboard();
						}
					} else {
						BillboardDelegate2.getInstance().onDisconnect(new NettyClientShutDownListener() {
							
							@Override
							public void onShutDown(int port) {
								mLogger.debug("Billboard UDP Disconnect");
							}
						});
					}
					
					// UDP PDP
					if (SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT1, "") != null && !SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT1, "").isEmpty()) {
	
						if (PdpDelegate.getInstance().isActive()) {
							PdpDelegate.getInstance().onDisconnect(new NettyClientShutDownListener() {
	
								@Override
								public void onShutDown(int port) {
									PdpDelegate.getInstance().createClients(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT1, ""), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT1, ""),mUdpPdpBoardStatusListener);
									
									// PDP 셋팅
									PdpDelegate.getInstance().initPdp();
								}
							});
						} else {
							PdpDelegate.getInstance().createClients(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT1, ""), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT1, ""),mUdpPdpBoardStatusListener);
							
							// PDP 셋팅
							PdpDelegate.getInstance().initPdp();
						}
					} else {
						PdpDelegate.getInstance().onDisconnect(new NettyClientShutDownListener() {
							
							@Override
							public void onShutDown(int port) {
								mLogger.debug("PDP UDP Disconnect");
							}
						});
					}
			}
			
			} catch (Exception e) {
				mLogger.debug("[saveSettings Exception] " + e);
			}finally {
				// 환경설정 저장 후 값들 재설정
				SettingApplication.getInstance().initSharedData();
				showAlertSuccess();
			}
				
		} else {
			mLogger.debug("validation failed!");
//            showAlertFailure();
		}
	}

	/**
	 * 서버 초기화
	 */
	private void initServer() {
		if(mSettingListener != null) {

			Optional<ButtonType> btnResult = CommonUtils.getInstance().showAlertPopupTwoButton(MoveStageUtil.getInstance().getDialog(), mResMsg.getString("str.init.server"), mResMsg.getString("popup.btn.ok"), mResMsg.getString("popup.btn.cancel"));

			if (btnResult.get().getButtonData() == ButtonData.LEFT) {
				mSettingListener.initServer();
			} 
		}
	}
	/**
	 * 최종 검사
	 *
	 * @author jhlee
	 */
	private boolean isValid() {
	
		// 카운트 설정 1 ~ 9 초
		if (!mCountTextField.getText().equals("") || !mCountTextField.getText().isEmpty()) {
			int second = Integer.parseInt(mCountTextField.getText());
			if (second < 1 || second > 9) {
				showAlertCountDown();
				return false;
			}
		} else {
			showAlertCountDown();
			return false;
		}
		
		// 대기시간 설정 1 ~ 50 초
		if (!mSoundAuctionWaitTime.getText().equals("") || !mSoundAuctionWaitTime.getText().isEmpty()) {
			int second = Integer.parseInt(mSoundAuctionWaitTime.getText());
			if (second < 1 || second > 50) {
				showAlert(mResMsg.getString("dialog.setting.aucwaittime.validation.fail"));
				return false;
			}
		} else {
			showAlert(mResMsg.getString("dialog.setting.aucwaittime.validation.fail"));
			return false;
		}
		
		// 음성경매 체크
		/* by kih - 2024.03.04 : 원본 주석처리 
		if(mUseSoundAuction.isSelected() && mTtsTypeCheckBox.isSelected())
		{
			// 로컬 TTS 설정시, Google private-key 확인 안 함.
			
		} else {
			if (mUseSoundAuction.isSelected() && !CommonUtils.getInstance().isValidString(mSoundValTextArea.getText())) {
				showAlert(mResMsg.getString("dialog.sound.empty.value"));
				return false;
			}else {
				
				if(CommonUtils.getInstance().isValidString(mSoundValTextArea.getText()) && !mSoundValTextArea.getText().contains("private_key")) {
					showAlert(mResMsg.getString("dialog.sound.no.value"));
					return false;
				}
			}
		}
		*/		
		if(mUseSoundAuction.isSelected()) {
			if(mTtsTypeCheckBox.isSelected()) {
				
				// 로컬 TTS 설정시, Google private-key 확인 안 함.
				
			} else {
				
				// check null,empty?		
				if(!CommonUtils.getInstance().isValidString(mSoundValTextArea.getText())
					|| !mSoundValTextArea.getText().contains("private_key")) { 				
					
					showAlert(mResMsg.getString("dialog.sound.no.value"));
					return false;									
				}
			}				
		}
		
		return true;
	}

	private void showAlertMobileSettingLimit() {
		CommonUtils.getInstance().showAlertPopupOneButton(MoveStageUtil.getInstance().getDialog(), mResMsg.getString("dialog.setting.mobile.validation.fail"), mResMsg.getString("popup.btn.close"));
	}

	private void showAlertDataFormat() {
		CommonUtils.getInstance().showAlertPopupOneButton(MoveStageUtil.getInstance().getDialog(), mResMsg.getString("dialog.setting.dataformat.validation.fail"), mResMsg.getString("popup.btn.close"));
	}

	private void showAlertCountDown() {
		CommonUtils.getInstance().showAlertPopupOneButton(MoveStageUtil.getInstance().getDialog(), mResMsg.getString("dialog.setting.countdown.validation.fail"), mResMsg.getString("popup.btn.close"));
	}
	
	private void showAlert(String msg) {
		CommonUtils.getInstance().showAlertPopupOneButton(MoveStageUtil.getInstance().getDialog(), msg, mResMsg.getString("popup.btn.close"));
	}
	

	private void showAlertSuccess() {

		Optional<ButtonType> btnResult = CommonUtils.getInstance().showAlertPopupOneButton(MoveStageUtil.getInstance().getDialog(), mResMsg.getString("dialog.setting.validation.success"), mResMsg.getString("popup.btn.close"));
		if (btnResult.get().getButtonData() == ButtonData.LEFT) {
			if (mSettingListener != null) {
				mSettingListener.callBack(true);
			}
		}

	}

	private void showAlertFailure() {
		CommonUtils.getInstance().showAlertPopupOneButton(MoveStageUtil.getInstance().getDialog(), mResMsg.getString("dialog.setting.validation.failure"), mResMsg.getString("popup.btn.close"));
	}
}
