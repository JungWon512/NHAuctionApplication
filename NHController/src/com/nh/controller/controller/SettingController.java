package com.nh.controller.controller;

import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.SharedPreference;
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

import java.awt.event.ActionEvent;
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
    // 표출 데이터 포맷
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
    private TextField mPdpEntryNumTextField, mPdpKpnTextField, mPdpExhibitorTextField, mPdpRegionTextField, mPdpGenderTextField, mPdpNoteTextField, mPdpWeightTextField,
            mPdpLowPriceTextField, mPdpMotherTextField, mPdpSucPriceTextField, mPdpPassageTextField, mPdpSucBidderTextField, mPdpMaTimeTextField, mPdpDNATextField;
    // 상한가/하한가
    @FXML
    private TextField mUpperLimitCalfTextField, mUpperLimitFatteningCattleTextField, mUpperLimitBreedingCattleTextField,
            mLowerLimitCalfTextField, mLowerLimitFatteningCattleTextField, mLowerLimitBreedingCattleTextField;
    // 경매종료멘트, 비고 창 설정
    @FXML
    private CheckBox mUseAnnouncementCheckBox, mUseNoteCheckBox;
    // 모바일노출설정 ( 최대 8개 )
    @FXML
    private CheckBox mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mWeightCheckBox, mMotherCheckBox, mPassageCheckBox, mMaTimeCheckBox, mKpnCheckBox,
            mRegionCheckBox, mNoteCheckBox, mLowPriceCheckBox, mDNACheckBox;

    private ArrayList<CheckBox> mobileCheckBoxSelectedList = null;
    private ArrayList<CheckBox> mobileDefaultCheckBoxList = null;

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
        setToggleGroups();
        initKeyConfig();
        mBtnSave.setOnMouseClicked(event -> saveSettings());
    }

    /// mobile checkbox init
    private void setMobileCheckBoxLists() {
        mobileDefaultCheckBoxList = new ArrayList<>(
                Arrays.asList(mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mWeightCheckBox, mMotherCheckBox, mPassageCheckBox,
                        mMaTimeCheckBox, mKpnCheckBox, mRegionCheckBox, mNoteCheckBox, mLowPriceCheckBox, mDNACheckBox)
        );
        mobileDefaultCheckBoxList.forEach((checkBox -> checkBox.setOnAction(e -> {
                    if (mobileCheckBoxSelectedList.size() > 7) {
                        checkBox.setSelected(false);
                        CommonUtils.getInstance().showAlertPopupOneButton(
                                this.mStage, " 모바일 노출 설정은\n최대 8개까지만 가능합니다.", "확인"
                        );
                    } else {
                        handleMobileCheckBox(checkBox);
                    }
                }
        )));
        getAllMobileCheckboxPreference();
        mobileCheckBoxSelectedList.forEach(checkBox -> checkBox.setSelected(true));
    }

    ///  경매종료멘트, 비고 창
    private void setAnnounceNoteCheckboxPreference() {
        sharedPreference.setBoolean(SharedPreference.PREFERENCE_SETTING_ANNOUNCEMENT, (mUseAnnouncementCheckBox.isSelected()));
        sharedPreference.setBoolean(SharedPreference.PREFERENCE_SETTING_NOTE, (mUseNoteCheckBox.isSelected()));
    }

    /// Mobile checkBox Setting
    private void handleMobileCheckBox(CheckBox checkBox) {
        if (checkBox.isSelected()) {
            mobileCheckBoxSelectedList.add(checkBox);
        } else {
            mobileCheckBoxSelectedList.remove(checkBox);
        }
    }

    /// toggle group setting
    private void setToggleGroups() {
        boardToggleGroup.selectedToggleProperty().
                addListener((observableValue, oldValue, newValue) -> {
                    mLogger.debug("Board newValue: " + newValue.getUserData());
                });

        pdpToggleGroup.selectedToggleProperty().
                addListener(((observableValue, oldValue, newValue) -> {
                    mLogger.debug("PDP newValue: " + newValue.getUserData());
                }));
    }

    private void initKeyConfig() {
        Platform.runLater(() -> mStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
            if (ke.getCode() == KeyCode.F5) {
                saveSettings();
            }
        }));
    }

    /**
     * 모바일 노출설정 Preference에 저장
     *
     * @author dhKim
     */
    private void saveSettings() {
        mLogger.debug("saveSettings...");
        setMobileCheckboxPreference(mobileCheckBoxSelectedList);
        setAnnounceNoteCheckboxPreference();
    }

    /**
     * 모바일 노출설정 Preference 초기화
     *
     * @author dhKim
     */
    private void initMobileCheckboxPreference() {
        for (String key : sharedMobileArray) {
            sharedPreference.setString(key, "N");
        }
    }

    /**
     * 모바일 노출설정 Preference에 저장
     *
     * @param mobileCheckBoxSelectedList 선택된 모바일 노출설정
     * @author dhKim
     */
    private void setMobileCheckboxPreference(ArrayList<CheckBox> mobileCheckBoxSelectedList) {
//        mobileCheckBoxSelectedList.sort((i1, i2) -> { // protocol 순서대로 맞춤
//            if (Integer.parseInt((String) i1.getUserData()) < Integer.parseInt((String) i2.getUserData())) {
//                return -1;
//            } else if (Integer.parseInt((String) i1.getUserData()) > Integer.parseInt((String) i2.getUserData())) {
//                return 1;
//            }
//            return 0;
//        });

        initMobileCheckboxPreference();

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

        for (CheckBox checkBox : mobileDefaultCheckBoxList) {
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
            mobileCheckBoxSelectedList = new ArrayList<>(
                    Arrays.asList(mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mWeightCheckBox)
            );
        }
    }
}
