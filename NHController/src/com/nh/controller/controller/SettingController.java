package com.nh.controller.controller;

import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.SharedPreference;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    private CheckBox mEntryNumCheckBox, mKpnCheckBox, mExhibitorCheckBox, mRegionCheckBox, mGenderCheckBox, mNoteCheckBox, mWeightCheckBox, mLowPriceCheckBox,
            mMotherCheckBox, mPassageCheckBox, mMaTimeCheckBox, mDNACheckBox;

    private ArrayList<CheckBox> mobileCheckBoxSelectedList = null;
    private ArrayList<CheckBox> mobileDefaultCheckBoxList = null;

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
        byte[] byteArr = sharedPreference.getByteArr(SharedPreference.PREFERENCE_SETTING_MOBILE_CHECK_BOX, new byte[12]);
        mobileCheckBoxSelectedList = new ArrayList<>( // TODO: get from preferences
                Arrays.asList(mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mWeightCheckBox)
        );
        mobileDefaultCheckBoxList = new ArrayList<>(
                Arrays.asList(mEntryNumCheckBox, mExhibitorCheckBox, mGenderCheckBox, mWeightCheckBox, mMotherCheckBox, mPassageCheckBox,
                        mMaTimeCheckBox, mKpnCheckBox, mRegionCheckBox, mNoteCheckBox, mLowPriceCheckBox, mDNACheckBox)
        );
        mobileCheckBoxSelectedList.forEach(checkBox -> checkBox.setSelected(true));
        mobileDefaultCheckBoxList.forEach((checkBox -> checkBox.setOnAction(e -> {
                    if (mobileCheckBoxSelectedList.size() > 7) {
                        checkBox.setSelected(false);
                        // 팝업띄우기
                        CommonUtils.getInstance().showAlertPopupOneButton(
                                this.mStage, " 모바일 노출 설정은\n최대 8개까지만 가능합니다.", "확인"
                        );
                    } else {
                        handleCheckBox(checkBox);
                    }
                }
        )));
    }

    /// Mobile checkBox Setting
    // TODO: 다른 체크박스 적용
    private void handleCheckBox(CheckBox checkBox) {
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

    private void saveSettings() {
        byte[] sharedMobileArr = new byte[mobileCheckBoxSelectedList.size()];
        mobileCheckBoxSelectedList.sort((i1, i2) -> {
            if (Integer.parseInt((String) i1.getUserData()) < Integer.parseInt((String) i2.getUserData())) {
                return -1;
            } else if (Integer.parseInt((String) i1.getUserData()) > Integer.parseInt((String) i2.getUserData())) {
                return 1;
            }
            return 0;
        });
        mLogger.debug(mobileCheckBoxSelectedList.toString());

        for (int i = 0; i < mobileCheckBoxSelectedList.size(); i++) {
        // TODO: Preference에 저장
//             mobileCheckBoxSelectedList.get(i).getUserData();
        }
    }

}
