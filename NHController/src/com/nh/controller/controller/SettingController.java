package com.nh.controller.controller;

import com.nh.controller.netty.AuctionDelegate;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.SharedPreference;
import com.nh.share.controller.models.EditSetting;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.*;

/**
 * 제어 메인 F8 -> 환경설정
 *
 * @author jhlee
 */
public class SettingController implements Initializable {

    private Stage mStage;
    private ResourceBundle mResMsg;
    private final SharedPreference sharedPreference = new SharedPreference();
    private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FXML
    private BorderPane mRoot;
    // F5 저장
    @FXML
    private Button mBtnSave;
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
    private TextField mBoardEntryNumTextField, mBoardKpnTextField, mBoardExhibitorTextField, mBoardRegionTextField, mBoardGenderTextField,
            mBoardNoteTextField, mBoardWeightTextField, mBoardLowPriceTextField, mBoardMotherTextField, mBoardSucPriceTextField, mBoardPassageTextField,
            mBoardSucBidderTextField, mBoardMaTimeTextField, mBoardDNATextField;
    // PDP 표출 설정
    @FXML
    private TextField mPdpEntryNumTextField, mPdpKpnTextField, mPdpExhibitorTextField, mPdpRegionTextField, mPdpGenderTextField,
            mPdpNoteTextField, mPdpWeightTextField, mPdpLowPriceTextField, mPdpMotherTextField, mPdpSucPriceTextField, mPdpPassageTextField,
            mPdpSucBidderTextField, mPdpMaTimeTextField, mPdpDNATextField;
    // 상한가/하한가
    @FXML
    private TextField mUpperLimitCalfTextField, mUpperLimitFatteningCattleTextField, mUpperLimitBreedingCattleTextField,
            mLowerLimitCalfTextField, mLowerLimitFatteningCattleTextField, mLowerLimitBreedingCattleTextField;
    // 경매종료멘트, 비고 창 설정
    @FXML
    private CheckBox mUseAnnouncementCheckBox, mUseNoteCheckBox;
    // 카운트 (1-9초)
    @FXML
    private TextField mCountTextField;
    // 모바일노출설정 ( 최대 8개 )
    @FXML
    private CheckBox mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mWeightCheckBox, mMotherCheckBox, mPassageCheckBox, mMaTimeCheckBox, mKpnCheckBox,
            mRegionCheckBox, mNoteCheckBox, mLowPriceCheckBox, mDNACheckBox;

    private ArrayList<CheckBox> mobileCheckBoxSelectedList = null;
    private ArrayList<CheckBox> mobileCheckBoxList = null;
    private String boardToggleType = "None";
    private String pdpToggleType = "BoardType";

    String[] sharedMobileArray = new String[]{
            SharedPreference.PREFERENCE_SETTING_MOBILE_ENTRYNUM, SharedPreference.PREFERENCE_SETTING_MOBILE_EXHIBITOR, SharedPreference.PREFERENCE_SETTING_MOBILE_GENDER,
            SharedPreference.PREFERENCE_SETTING_MOBILE_WEIGHT, SharedPreference.PREFERENCE_SETTING_MOBILE_MOTHER, SharedPreference.PREFERENCE_SETTING_MOBILE_PASSAGE,
            SharedPreference.PREFERENCE_SETTING_MOBILE_MATIME, SharedPreference.PREFERENCE_SETTING_MOBILE_KPN, SharedPreference.PREFERENCE_SETTING_MOBILE_REGION,
            SharedPreference.PREFERENCE_SETTING_MOBILE_NOTE, SharedPreference.PREFERENCE_SETTING_MOBILE_LOWPRICE, SharedPreference.PREFERENCE_SETTING_MOBILE_DNA
    };

    public enum MobileCheckBoxType {
        SETTING_MOBILE_ENTRYNUM("mEntryNumCheckBox"),
        SETTING_MOBILE_EXHIBITOR("mExhibitorCheckBox"),
        SETTING_MOBILE_GENDER("mGenderCheckBox"),
        SETTING_MOBILE_WEIGHT("mWeightCheckBox"),
        SETTING_MOBILE_MOTHER("mMotherCheckBox"),
        SETTING_MOBILE_PASSAGE("mPassageCheckBox"),
        SETTING_MOBILE_MATIME("mMaTimeCheckBox"),
        SETTING_MOBILE_KPN("mKpnCheckBox"),
        SETTING_MOBILE_REGION("mRegionCheckBox"),
        SETTING_MOBILE_NOTE("mNoteCheckBox"),
        SETTING_MOBILE_LOWPRICE("mLowPriceCheckBox"),
        SETTING_MOBILE_DNA("mDNACheckBox");

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
        setMobileCheckBoxLists();
        getAnnounceNoteCheckboxPreference();
        setToggleGroups();
        getCountTextField();
        getTextFields();
        initKeyConfig();
        mBtnSave.setOnMouseClicked(event -> saveSettings());
    }

    private void initKeyConfig() {
        Platform.runLater(() -> mStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode() == KeyCode.F5) {
                saveSettings();
            }
        }));
    }

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
        // 상한가/하한가
        sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_UPPER_CALF_TEXT, mUpperLimitCalfTextField.getText().trim());
        sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_UPPER_FATTENING_TEXT, mUpperLimitFatteningCattleTextField.getText().trim());
        sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_UPPER_BREEDING_TEXT, mUpperLimitBreedingCattleTextField.getText().trim());
        sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_LOWER_CALF_TEXT, mLowerLimitCalfTextField.getText().trim());
        sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_LOWER_FATTENING_TEXT, mLowerLimitFatteningCattleTextField.getText().trim());
        sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_LOWER_BREEDING_TEXT, mLowerLimitBreedingCattleTextField.getText().trim());
    }

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
        mUpperLimitCalfTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_UPPER_CALF_TEXT, ""));
        mUpperLimitFatteningCattleTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_UPPER_FATTENING_TEXT, ""));
        mUpperLimitBreedingCattleTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_UPPER_BREEDING_TEXT, ""));
        mLowerLimitCalfTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_LOWER_CALF_TEXT, ""));
        mLowerLimitFatteningCattleTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_LOWER_FATTENING_TEXT, ""));
        mLowerLimitBreedingCattleTextField.setText(sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_LOWER_BREEDING_TEXT, ""));
    }

    ///  경매종료멘트, 비고 창 value setting
    private void setAnnounceNoteCheckboxPreference() {
        sharedPreference.setBoolean(SharedPreference.PREFERENCE_SETTING_ANNOUNCEMENT, (mUseAnnouncementCheckBox.isSelected()));
        sharedPreference.setBoolean(SharedPreference.PREFERENCE_SETTING_NOTE, (mUseNoteCheckBox.isSelected()));
    }

    ///  경매종료멘트, 비고 창 value 가져오기
    private void getAnnounceNoteCheckboxPreference() {
        boolean isAnnouncement = sharedPreference.getBoolean(SharedPreference.PREFERENCE_SETTING_ANNOUNCEMENT, true);
        boolean isNote = sharedPreference.getBoolean(SharedPreference.PREFERENCE_SETTING_NOTE, true);
        mUseAnnouncementCheckBox.setSelected(isAnnouncement);
        mUseNoteCheckBox.setSelected(isNote);
    }

    // 카운트 설정
    private void setCountTextField() {
        sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_COUNTDOWN, mCountTextField.getText().trim());
    }

    // 카운트 설정 가져오기
    private void getCountTextField() {
        String second = sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_COUNTDOWN, "5");
        mCountTextField.setText(second);
    }

    /// toggle group setting
    private void setToggleGroups() {
        getToggleTypes();
        boardToggleGroup.selectedToggleProperty().
                addListener((observableValue, oldValue, newValue) -> {
                    boardToggleType = newValue.getUserData().toString().trim();
                });

        pdpToggleGroup.selectedToggleProperty().
                addListener((observableValue, oldValue, newValue) -> {
                    pdpToggleType = newValue.getUserData().toString().trim();
                });
    }

    private void setToggleTypes() {
        sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_BOARD_TOGGLE_TYPE, boardToggleType);
        sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_PDP_TOGGLE_TYPE, pdpToggleType);
    }

    private void getToggleTypes() {
        String boardToggle = sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_TOGGLE_TYPE, "None");
        String pdpToggle = sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_TOGGLE_TYPE, "BoardType");

        switch (BoardToggle.valueOf(boardToggle.toUpperCase())) {
            case NONE -> mBoardNoneToggleButton.setSelected(true);
            case FIXED -> mBoardFixedToggleButton.setSelected(true);
            case MARQUEE -> mBoardMarqueeToggleButton.setSelected(true);
        }

        switch (PdpToggle.valueOf(pdpToggle.toUpperCase())) {
            case BOARDTYPE -> mPdpViewBoardTypeToggleButton.setSelected(true);
            case AUCTIONTYPE -> mPdpViewAuctionTypeToggleButton.setSelected(true);
        }
    }

    /// 모바일 노출설정 checkbox init
    private void setMobileCheckBoxLists() {
        mobileCheckBoxList = new ArrayList<>(
                Arrays.asList(mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mWeightCheckBox, mMotherCheckBox, mPassageCheckBox,
                        mMaTimeCheckBox, mKpnCheckBox, mRegionCheckBox, mNoteCheckBox, mLowPriceCheckBox, mDNACheckBox)
        );
        mobileCheckBoxList.forEach((checkBox -> checkBox.setOnAction(e -> {
                    handleMobileCheckBox(checkBox);
                    if (mobileCheckBoxSelectedList.size() > 8) {
                        mobileCheckBoxSelectedList.remove(checkBox);
                        checkBox.setSelected(false);
                        CommonUtils.getInstance().showAlertPopupOneButton(
                                this.mStage, "모바일 노출 설정은\n최대 8개까지만 가능합니다.", "확인"
                        );
                    }
                }
        )));
        getAllMobileCheckboxPreference();
        mobileCheckBoxSelectedList.forEach(checkBox -> checkBox.setSelected(true));
    }

    /// 모바일 노출설정 checkBox Setting
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
        for (String key : sharedMobileArray) {  // 모바일 노출설정 Preference 초기화
            sharedPreference.setString(key, "N");
        }

        for (CheckBox checkBox : mobileCheckBoxSelectedList) {
            switch (MobileCheckBoxType.find(checkBox.getId())) {
                case SETTING_MOBILE_ENTRYNUM -> sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_MOBILE_ENTRYNUM, "Y");
                case SETTING_MOBILE_EXHIBITOR -> sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_MOBILE_EXHIBITOR, "Y");
                case SETTING_MOBILE_GENDER -> sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_MOBILE_GENDER, "Y");
                case SETTING_MOBILE_WEIGHT -> sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_MOBILE_WEIGHT, "Y");
                case SETTING_MOBILE_MOTHER -> sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_MOBILE_MOTHER, "Y");
                case SETTING_MOBILE_PASSAGE -> sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_MOBILE_PASSAGE, "Y");
                case SETTING_MOBILE_MATIME -> sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_MOBILE_MATIME, "Y");
                case SETTING_MOBILE_KPN -> sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_MOBILE_KPN, "Y");
                case SETTING_MOBILE_REGION -> sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_MOBILE_REGION, "Y");
                case SETTING_MOBILE_NOTE -> sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_MOBILE_NOTE, "Y");
                case SETTING_MOBILE_LOWPRICE -> sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_MOBILE_LOWPRICE, "Y");
                case SETTING_MOBILE_DNA -> sharedPreference.setString(SharedPreference.PREFERENCE_SETTING_MOBILE_DNA, "Y");
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
                case SETTING_MOBILE_ENTRYNUM -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_ENTRYNUM.getId(), sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_ENTRYNUM, "N"));
                case SETTING_MOBILE_EXHIBITOR -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_EXHIBITOR.getId(), sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_EXHIBITOR, "N"));
                case SETTING_MOBILE_GENDER -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_GENDER.getId(), sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_GENDER, "N"));
                case SETTING_MOBILE_WEIGHT -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_WEIGHT.getId(), sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_WEIGHT, "N"));
                case SETTING_MOBILE_MOTHER -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_MOTHER.getId(), sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_MOTHER, "N"));
                case SETTING_MOBILE_PASSAGE -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_PASSAGE.getId(), sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_PASSAGE, "N"));
                case SETTING_MOBILE_MATIME -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_MATIME.getId(), sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_MATIME, "N"));
                case SETTING_MOBILE_KPN -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_KPN.getId(), sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_KPN, "N"));
                case SETTING_MOBILE_REGION -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_REGION.getId(), sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_REGION, "N"));
                case SETTING_MOBILE_NOTE -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_NOTE.getId(), sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_NOTE, "N"));
                case SETTING_MOBILE_LOWPRICE -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_LOWPRICE.getId(), sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_LOWPRICE, "N"));
                case SETTING_MOBILE_DNA -> tempMap.put(MobileCheckBoxType.SETTING_MOBILE_DNA.getId(), sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_DNA, "N"));
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
     * 모바일 노출설정 Default값 셋팅
     * TODO: param 추가
     *
     * @author dhKim
     */
    private void setMobileCheckBoxDefaultValue() {
        mobileCheckBoxSelectedList = new ArrayList<>(
                Arrays.asList(mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mWeightCheckBox)
        );
    }

    /**
     * 모바일 노출설정 Preference에 저장
     *
     * @author dhKim
     */
    private void saveSettings() {
        if (isValid()) {
            mLogger.debug("save Settings...");
            setTextFields();
            setAnnounceNoteCheckboxPreference();
            setCountTextField();
            setMobileCheckboxPreference(mobileCheckBoxSelectedList);
            setToggleTypes();
            // 서버에 edit setting 전송
            EditSetting setting = new EditSetting(sharedPreference.getString(SharedPreference.PREFERENCE_AUCTION_HOUSE_CODE, ""),
                    sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_ENTRYNUM, "Y"),
                    sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_EXHIBITOR, "Y"),
                    sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_GENDER, "Y"),
                    sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_WEIGHT, "Y"),
                    sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_MOTHER, "Y"),
                    sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_PASSAGE, "Y"),
                    sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_MATIME, "Y"),
                    sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_KPN, "N"),
                    sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_REGION, "N"),
                    sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_NOTE, "N"),
                    sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_LOWPRICE, "Y"),
                    sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_MOBILE_DNA, "N"),
                    sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_COUNTDOWN, "5"));
            mLogger.debug(mResMsg.getString("msg.auction.send.setting.info") + AuctionDelegate.getInstance().onSendSettingInfo(setting));
            showAlertSuccess();
        } else {
            mLogger.debug("validation failed!");
            showAlertFailure();
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
        return true;
    }

    private void showAlertDataFormat() {
        CommonUtils.getInstance().showAlertPopupOneButton(
                this.mStage, mResMsg.getString("dialog.setting.dataformat.validation.fail"), mResMsg.getString("popup.btn.close")
        );
    }

    private void showAlertCountDown() {
        CommonUtils.getInstance().showAlertPopupOneButton(
                this.mStage, mResMsg.getString("dialog.setting.countdown.validation.fail"), mResMsg.getString("popup.btn.close")
        );
    }

    private void showAlertSuccess() {
        CommonUtils.getInstance().showAlertPopupOneButton(
                this.mStage, mResMsg.getString("dialog.setting.validation.success"), mResMsg.getString("popup.btn.close")
        );
    }

    private void showAlertFailure() {
        CommonUtils.getInstance().showAlertPopupOneButton(
                this.mStage, mResMsg.getString("dialog.setting.validation.failure"), mResMsg.getString("popup.btn.close")
        );
    }
}
