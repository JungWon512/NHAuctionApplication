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
	private TextField mIpBoardTextField1, mPortBoardTextField1, mIpBoardTextField2, mPortBoardTextField2, mIpBoardTextField3, mPortBoardTextField3;
	// 전광판 설정 문구
	@FXML
	private ToggleGroup boardToggleGroup;
	@FXML
	private ToggleButton mBoardNoneToggleButton, mBoardFixedToggleButton, mBoardMarqueeToggleButton;
	// PDP 화면 설정
	@FXML
	private ToggleGroup pdpToggleGroup;
	@FXML
	private ToggleButton mPdpViewBoardTypeToggleButton, mPdpViewAuctionTypeToggleButton;
	// 표출 데이터 포맷 (1,2)
	@FXML
	private TextField mFormatTextField;
	// PDP, 응찰석, PDP3 셋톱박스 IP, PORT
	@FXML
	private TextField mIpPdpTextField1, mPortPdpTextField1, mIpBidderTextField, mPortBidderTextField, mIpPdpTextField2, mPortPdpTextField2;
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
	// 경매종료멘트, 비고 창 설정
	@FXML
	private CheckBox mUseAnnouncementCheckBox, mUseNoteCheckBox;
	// 카운트 (1-9초)
	@FXML
	private TextField mCountTextField;
	// 모바일노출설정 ( 최대 8개 )
	@FXML
	private CheckBox mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mWeightCheckBox, mMotherCheckBox, mPassageCheckBox, mMaTimeCheckBox, mKpnCheckBox, mRegionCheckBox, mNoteCheckBox, mLowPriceCheckBox, mDNACheckBox;

	@FXML // 동가 재경매 횟수
	private TextField mReAuctionCountTextField;

	@FXML // 음성 경매 대기 시간
	private TextField mSoundAuctionWaitTime;

	@FXML // 동가 재경매,연속경매,음성경부여부
	private CheckBox mUseReAuction, mUseOneAuction, mUseSoundAuction;

	@FXML // 경매 타입
	private ToggleGroup auctionTypeToggleGroup;

	@FXML // 경매 타입 (단일 ,일괄)
	private ToggleButton mAuctionTypeSingleToggleButton, mAuctionTypeMultiToggleButton;
	
	@FXML
	private TextArea mSoundValTextArea;

	private final static String[] SHARED_MOBILE_ARRAY = new String[] { SharedPreference.PREFERENCE_SETTING_MOBILE_ENTRYNUM, SharedPreference.PREFERENCE_SETTING_MOBILE_EXHIBITOR, SharedPreference.PREFERENCE_SETTING_MOBILE_GENDER, SharedPreference.PREFERENCE_SETTING_MOBILE_WEIGHT,
			SharedPreference.PREFERENCE_SETTING_MOBILE_MOTHER, SharedPreference.PREFERENCE_SETTING_MOBILE_PASSAGE, SharedPreference.PREFERENCE_SETTING_MOBILE_MATIME, SharedPreference.PREFERENCE_SETTING_MOBILE_KPN, SharedPreference.PREFERENCE_SETTING_MOBILE_REGION,
			SharedPreference.PREFERENCE_SETTING_MOBILE_NOTE, SharedPreference.PREFERENCE_SETTING_MOBILE_LOWPRICE, SharedPreference.PREFERENCE_SETTING_MOBILE_DNA };

	private ArrayList<CheckBox> mobileCheckBoxSelectedList = null;
	private ArrayList<CheckBox> mobileCheckBoxList = null;
	private String boardToggleType = "None";
	private String pdpToggleType = "BoardType";
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

	public enum BoardToggle {
		NONE, FIXED, MARQUEE
	}

	public enum PdpToggle {
		BOARDTYPE, AUCTIONTYPE
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
		}
	}

	/**
	 * 구성 설정
	 */
	public void initConfiguration() {
		CommonUtils.getInstance().canMoveStage(mStage, null);
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
		setToggleGroups();
		getCountTextField();
		getTextFields();
		initKeyConfig();
		addTextFieldListener();
		mBtnSave.setOnMouseClicked(event -> saveSettings());
		mBtnInitServer.setOnMouseClicked(event -> initServer());
	}
	
	private void setNumberFmt() {
		

		UnaryOperator<Change> integerFilter = change -> {
		    String newText = change.getControlNewText();
		    if (newText.matches("-?([1-9][0-9]*)?")) { 
		        return change;
		    }
		    return null;
		};
		
		
		mReAuctionCountTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
		mCountTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
		mSoundAuctionWaitTime.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
		mUpperLimitCalfTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
		mUpperLimitFatteningCattleTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
		mUpperLimitBreedingCattleTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
		mLowerLimitCalfTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
		mLowerLimitFatteningCattleTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
		mLowerLimitBreedingCattleTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
		mLowerLimitBreedingCattleTextField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
		
		
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
	 * @author dhKim
	 */
	private void setTextFields() {
		// 전광판 설정 IP, PORT
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, mIpBoardTextField1.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT1, mPortBoardTextField1.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT2, mIpBoardTextField2.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT2, mPortBoardTextField2.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT3, mIpBoardTextField3.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT3, mPortBoardTextField3.getText().trim());
		// 표출 데이터 포맷 (1,2)
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_FORMAT, mFormatTextField.getText().trim());
		// PDP, 응찰석, PDP3 셋톱박스 IP, PORT
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT1, mIpPdpTextField1.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT1, mPortPdpTextField1.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT2, mIpPdpTextField2.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT2, mPortPdpTextField2.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_IP_BIDDER_TEXT, mIpBidderTextField.getText().trim());
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PORT_BIDDER_TEXT, mPortBidderTextField.getText().trim());
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
		SoundUtil.getInstance().initCertification(mSoundValTextArea.getText());
	}

	/**
	 * TextFields Preference에서 가져오기
	 *
	 * @author dhKim
	 */
	private void getTextFields() {
		
		// 전광판 설정 IP, PORT
		mIpBoardTextField1.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, ""));
		mPortBoardTextField1.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT1, ""));
		mIpBoardTextField2.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT2, ""));
		mPortBoardTextField2.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT2, ""));
		mIpBoardTextField3.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT3, ""));
		mPortBoardTextField3.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT3, ""));
		// 표출 데이터 포맷 (1,2)
		mFormatTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_FORMAT, ""));
		// PDP, 응찰석, PDP3 셋톱박스 IP, PORT
		mIpPdpTextField1.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT1, ""));
		mPortPdpTextField1.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT1, ""));
		mIpPdpTextField2.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT2, ""));
		mPortPdpTextField2.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT2, ""));
		mIpBidderTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_IP_BIDDER_TEXT, ""));
		mPortBidderTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PORT_BIDDER_TEXT, ""));
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
		// 동가재경매 횟수
		mReAuctionCountTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_RE_AUCTION_COUNT, SettingApplication.getInstance().DEFAULT_SETTING_RE_AUCTION_COUNT));
		// 대기시간
		mSoundAuctionWaitTime.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_SOUND_AUCTION_WAIT_TIME, SettingApplication.getInstance().DEFAULT_SETTING_SOUND_AUCTION_WAIT_TIME));

		//음성 설정 파일
		mSoundValTextArea.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_SOUND_CONFIG, SettingApplication.getInstance().DEFAULT_SETTING_SOUND_CONFIG));
	
	}

	/**
	 * 경매종료멘트, 비고 창 value setting
	 *
	 * @author dhKim
	 */
	private void setAnnounceNoteCheckboxPreference() {
		sharedPreference.setBoolean(SharedPreference.PREFERENCE_SETTING_ANNOUNCEMENT, (mUseAnnouncementCheckBox.isSelected()));
		sharedPreference.setBoolean(SharedPreference.PREFERENCE_SETTING_NOTE, (mUseNoteCheckBox.isSelected()));
	}

	/**
	 * 경매종료멘트, 비고 창 value 가져오기
	 *
	 * @author dhKim
	 */
	private void getAnnounceNoteCheckboxPreference() {
		boolean isAnnouncement = sharedPreference.getBoolean(SharedPreference.PREFERENCE_SETTING_ANNOUNCEMENT, true);
		boolean isNote = sharedPreference.getBoolean(SharedPreference.PREFERENCE_SETTING_NOTE, true);
		mUseAnnouncementCheckBox.setSelected(isAnnouncement);
		mUseNoteCheckBox.setSelected(isNote);
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
	 * 카운트 설정 Preference에 저장
	 *
	 * @author dhKim
	 */
	private void setCountTextField() {
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_COUNTDOWN, mCountTextField.getText().trim());
	}

	/**
	 * 카운트 설정 가져오기
	 *
	 * @author dhKim
	 */
	private void getCountTextField() {
		String second = sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_COUNTDOWN, "5");
		mCountTextField.setText(second);
	}

	/**
	 * toggle group setting
	 *
	 * @author dhKim
	 */
	private void setToggleGroups() {
		
		boardToggleGroup.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> boardToggleType = newValue.getUserData().toString().trim());

		pdpToggleGroup.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> pdpToggleType = newValue.getUserData().toString().trim());

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
	 * @author dhKim
	 */
	private void setToggleTypes() {
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_TOGGLE_TYPE, boardToggleType);
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_TOGGLE_TYPE, pdpToggleType);
		sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_AUCTION_TOGGLE_TYPE, auctionToggleType);
	}

	/**
	 * toggle type Preference에서 가져오기
	 *
	 * @author dhKim
	 */
	private void getToggleTypes() {
		String boardToggle = sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_TOGGLE_TYPE, "None");
		String pdpToggle = sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_TOGGLE_TYPE, "BoardType");
		String auctionToggle = sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_AUCTION_TOGGLE_TYPE, "Single");

		switch (BoardToggle.valueOf(boardToggle.toUpperCase())) {
		case NONE -> mBoardNoneToggleButton.setSelected(true);
		case FIXED -> mBoardFixedToggleButton.setSelected(true);
		case MARQUEE -> mBoardMarqueeToggleButton.setSelected(true);
		}

		switch (PdpToggle.valueOf(pdpToggle.toUpperCase())) {
		case BOARDTYPE -> mPdpViewBoardTypeToggleButton.setSelected(true);
		case AUCTIONTYPE -> mPdpViewAuctionTypeToggleButton.setSelected(true);
		}

		mLogger.debug("auctionToggle : " + auctionToggle);
		switch (AuctionToggle.valueOf(auctionToggle.toUpperCase())) {
		case SINGLE -> mAuctionTypeSingleToggleButton.setSelected(true);
		case MULTI -> mAuctionTypeMultiToggleButton.setSelected(true);
		}

	}

	/**
	 * 모바일 노출설정 checkbox init
	 *
	 * @author dhKim
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
	 * @author dhKim
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
	 * 모바일 노출설정 Preference에 저장
	 *
	 * @param mobileCheckBoxSelectedList 선택된 모바일 노출설정
	 * @author dhKim
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
	 * @author dhKim
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
	 * @author dhKim
	 */
	private void setMobileCheckBoxDefaultValue() {
		mobileCheckBoxSelectedList = new ArrayList<>(Arrays.asList(mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mWeightCheckBox));
	}

	/**
	 * valid listener
	 */
	private void addTextFieldListener() {
	
		
		mReAuctionCountTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {

				if (mReAuctionCountTextField.getText().length() > 1) {

					String str = mReAuctionCountTextField.getText().substring(0, 1);

					if (Integer.parseInt(str) > 0) {
						mReAuctionCountTextField.setText(str);
					} else {
						// default Value
						mReAuctionCountTextField.setText("1");
					}

				}
			}
		});
		
		mCountTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {

				if (CommonUtils.getInstance().isValidString(newValue)) {
				
					String tmpStr = newValue;
					String max = SettingApplication.getInstance().DEFAULT_SETTING_COUNTDOWN_MAX;
		
					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else 	if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_COUNTDOWN;
					}
						
					mCountTextField.setText(tmpStr);		
				}

			}
		});

		mSoundAuctionWaitTime.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;
					String max = SettingApplication.getInstance().DEFAULT_SETTING_SOUND_AUCTION_WAIT_TIME_MAX;
					
					
					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_SOUND_AUCTION_WAIT_TIME;
					}
					mSoundAuctionWaitTime.setText(tmpStr);	
				}
			}
		});
		
		mUpperLimitCalfTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;
					String max = SettingApplication.getInstance().DEFAULT_SETTING_UPPER_CFB_MAX;

					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_UPPER_CALF_TEXT;
					}
					mUpperLimitCalfTextField.setText(tmpStr);	
				}
			}
		});
		
		mUpperLimitFatteningCattleTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;
					String max = SettingApplication.getInstance().DEFAULT_SETTING_UPPER_CFB_MAX;

					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_UPPER_FATTENING_TEXT;
					}
					mUpperLimitFatteningCattleTextField.setText(tmpStr);	
				}
			}
		});
		
		mUpperLimitBreedingCattleTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;
					String max = SettingApplication.getInstance().DEFAULT_SETTING_UPPER_CFB_MAX;

					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_UPPER_BREEDING_TEXT;
					}
					mUpperLimitBreedingCattleTextField.setText(tmpStr);	
				}
			}
		});
	
		
		mLowerLimitCalfTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;
					String max = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_CALF_TEXT_MAX;

					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_CALF_TEXT;
					}
					mLowerLimitCalfTextField.setText(tmpStr);	
				}
			}
		});
		
		mLowerLimitFatteningCattleTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;
					String max = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_CALF_TEXT_MAX;

					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_FATTENING_TEXT;
					}
					mLowerLimitFatteningCattleTextField.setText(tmpStr);	
				}
			}
		});
		
		mLowerLimitBreedingCattleTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				
				if (CommonUtils.getInstance().isValidString(newValue)) {
					
					String tmpStr = newValue;
					String max = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_CALF_TEXT_MAX;

					if(Integer.parseInt(tmpStr) > Integer.parseInt(max)) {
						tmpStr  = max;
					}else if(Integer.parseInt(tmpStr) <= 0) {
						tmpStr = SettingApplication.getInstance().DEFAULT_SETTING_LOWER_BREEDING_TEXT;
					}
					mLowerLimitBreedingCattleTextField.setText(tmpStr);	
				}
			}
		});
		
		
		
	}

	/**
	 * 설정 Preference에 저장
	 *
	 * @author dhKim
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
			setCountTextField();
			setMobileCheckboxPreference(mobileCheckBoxSelectedList);
			setToggleTypes();
		
			
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
	 * @author dhKim
	 */
	private boolean isValid() {
		// 표출 데이터 포맷 (1 또는 2)
		if (!mFormatTextField.getText().equals("") || !mFormatTextField.getText().isEmpty()) {
			int format = Integer.parseInt(mFormatTextField.getText());
			if (format < 1 || format > 2) {
				showAlertDataFormat();
				return false;
			}
		} else {
			showAlertDataFormat();
			return false;
		}

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
		if (mUseSoundAuction.isSelected() && !CommonUtils.getInstance().isValidString(mSoundValTextArea.getText())) {
			showAlert(mResMsg.getString("dialog.sound.empty.value"));
			return false;
		}else {
			
			if(CommonUtils.getInstance().isValidString(mSoundValTextArea.getText()) && !mSoundValTextArea.getText().contains("private_key")) {
				showAlert(mResMsg.getString("dialog.sound.no.value"));
				return false;
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
