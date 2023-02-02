package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.controller.interfaces.BooleanListener;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.SharedPreference;
import com.nh.controller.utils.SoundUtil;
import com.nh.controller.utils.SoundUtils;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * 제어 메인 -> 음성설정
 *
 * @author jhlee
 */
public class SettingSoundController implements Initializable {

    private Stage mStage;
    private ResourceBundle mResMsg;
    private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FXML
    private BorderPane mRoot;

    @FXML // 음성 시작
    private Button mBtnPlaySound_1, mBtnPlaySound_2, mBtnPlaySound_3, mBtnPlaySound_4, mBtnPlaySound_5, mBtnPlaySound_6, mBtnPlaySound_7, mBtnPlaySound_8, mBtnPlaySound_9, mBtnPlaySound_10, mBtnPlaySound_11, mBtnPlaySound_12;

    @FXML // 음성 정지
    private Button mBtnStopSound_1, mBtnStopSound_2, mBtnStopSound_3, mBtnStopSound_4, mBtnStopSound_5, mBtnStopSound_6, mBtnStopSound_7, mBtnStopSound_8, mBtnStopSound_9, mBtnStopSound_10, mBtnStopSound_11, mBtnStopSound_12;

    @FXML // 상단
    private Button mBtnRePlaySound, mBtnStopSound, mBtnSave, mBtnClose;

    @FXML // 메세지
    private TextArea mMsgTextArea_1, mMsgTextArea_2, mMsgTextArea_3, mMsgTextArea_4, mMsgTextArea_5, mMsgTextArea_6, mMsgTextArea_7, mMsgTextArea_8, mMsgTextArea_9, mMsgTextArea_10, mMsgTextArea_11, mMsgTextArea_12;

    private BooleanListener mBooleanListener = null;

    /**
     * setStage
     *
     * @param stage
     */
    public void setStage(Stage stage) {
        mStage = stage;
    }

    /**
     * 콜백 리스너
     *
     * @param listener
     */
    public void setListener(BooleanListener listener) {
        mBooleanListener = listener;
    }

    /**
     * 구성 설정
     */
    public void initConfiguration() {
//		CommonUtils.getInstance().canMoveStage(mStage, null);
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
        initKeyConfig();

        if (SettingApplication.getInstance().isTtsType()) {
        	mBtnPlaySound_1.setOnMouseClicked(event -> SoundUtils.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_INTRO));
            mBtnPlaySound_2.setOnMouseClicked(event -> SoundUtils.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_BUYER));
            mBtnPlaySound_3.setOnMouseClicked(event -> SoundUtils.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_GUIDE));
            mBtnPlaySound_4.setOnMouseClicked(event -> SoundUtils.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_PRACTICE));
            mBtnPlaySound_5.setOnMouseClicked(event -> SoundUtils.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_GENDER));
            mBtnPlaySound_6.setOnMouseClicked(event -> SoundUtils.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_USE));
            mBtnPlaySound_7.setOnMouseClicked(event -> SoundUtils.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_1));
            mBtnPlaySound_8.setOnMouseClicked(event -> SoundUtils.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_2));
            mBtnPlaySound_9.setOnMouseClicked(event -> SoundUtils.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_3));
            mBtnPlaySound_10.setOnMouseClicked(event -> SoundUtils.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_4));
            mBtnPlaySound_11.setOnMouseClicked(event -> SoundUtils.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_5));
            mBtnPlaySound_12.setOnMouseClicked(event -> SoundUtils.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_6));

            mBtnStopSound_1.setOnMouseClicked(event -> SoundUtils.getInstance().stopSound());
            mBtnStopSound_2.setOnMouseClicked(event -> SoundUtils.getInstance().stopSound());
            mBtnStopSound_3.setOnMouseClicked(event -> SoundUtils.getInstance().stopSound());
            mBtnStopSound_4.setOnMouseClicked(event -> SoundUtils.getInstance().stopSound());
            mBtnStopSound_5.setOnMouseClicked(event -> SoundUtils.getInstance().stopSound());
            mBtnStopSound_6.setOnMouseClicked(event -> SoundUtils.getInstance().stopSound());
            mBtnStopSound_7.setOnMouseClicked(event -> SoundUtils.getInstance().stopSound());
            mBtnStopSound_8.setOnMouseClicked(event -> SoundUtils.getInstance().stopSound());
            mBtnStopSound_9.setOnMouseClicked(event -> SoundUtils.getInstance().stopSound());
            mBtnStopSound_10.setOnMouseClicked(event -> SoundUtils.getInstance().stopSound());
            mBtnStopSound_11.setOnMouseClicked(event -> SoundUtils.getInstance().stopSound());
            mBtnStopSound_12.setOnMouseClicked(event -> SoundUtils.getInstance().stopSound());

            mBtnRePlaySound.setOnMouseClicked(event -> SoundUtils.getInstance().playDefineSound(SoundUtils.getInstance().getDefinePrevKey()));
            mBtnStopSound.setOnMouseClicked(event -> SoundUtils.getInstance().stopSound());
        } else {
        	mBtnPlaySound_1.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_INTRO));
            mBtnPlaySound_2.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_BUYER));
            mBtnPlaySound_3.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_GUIDE));
            mBtnPlaySound_4.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_PRACTICE));
            mBtnPlaySound_5.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_GENDER));
            mBtnPlaySound_6.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_USE));
            mBtnPlaySound_7.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_1));
            mBtnPlaySound_8.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_2));
            mBtnPlaySound_9.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_3));
            mBtnPlaySound_10.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_4));
            mBtnPlaySound_11.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_5));
            mBtnPlaySound_12.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_6));

            mBtnStopSound_1.setOnMouseClicked(event -> SoundUtil.getInstance().stopSound());
            mBtnStopSound_2.setOnMouseClicked(event -> SoundUtil.getInstance().stopSound());
            mBtnStopSound_3.setOnMouseClicked(event -> SoundUtil.getInstance().stopSound());
            mBtnStopSound_4.setOnMouseClicked(event -> SoundUtil.getInstance().stopSound());
            mBtnStopSound_5.setOnMouseClicked(event -> SoundUtil.getInstance().stopSound());
            mBtnStopSound_6.setOnMouseClicked(event -> SoundUtil.getInstance().stopSound());
            mBtnStopSound_7.setOnMouseClicked(event -> SoundUtil.getInstance().stopSound());
            mBtnStopSound_8.setOnMouseClicked(event -> SoundUtil.getInstance().stopSound());
            mBtnStopSound_9.setOnMouseClicked(event -> SoundUtil.getInstance().stopSound());
            mBtnStopSound_10.setOnMouseClicked(event -> SoundUtil.getInstance().stopSound());
            mBtnStopSound_11.setOnMouseClicked(event -> SoundUtil.getInstance().stopSound());
            mBtnStopSound_12.setOnMouseClicked(event -> SoundUtil.getInstance().stopSound());

            mBtnRePlaySound.setOnMouseClicked(event -> SoundUtil.getInstance().playDefineSound(SoundUtil.getInstance().getDefinePrevKey()));
            mBtnStopSound.setOnMouseClicked(event -> SoundUtil.getInstance().stopSound());
        }
        
        mBtnSave.setOnMouseClicked(event -> saveMsg());
        mBtnClose.setOnMouseClicked(event -> close());

        if (SettingApplication.getInstance().isTtsType()) {
        	SoundUtils.getInstance().initSoundSetting();        	
        } else {
        	SoundUtil.getInstance().initSoundSetting();
        }
        
        List<String> soundDataList = initParsingSoundDataList();

        mMsgTextArea_1.setText(soundDataList.get(0));
        mMsgTextArea_2.setText(soundDataList.get(1));
        mMsgTextArea_3.setText(soundDataList.get(2));
        mMsgTextArea_4.setText(soundDataList.get(3));
        mMsgTextArea_5.setText(soundDataList.get(4));
        mMsgTextArea_6.setText(soundDataList.get(5));
        mMsgTextArea_7.setText(soundDataList.get(6));
        mMsgTextArea_8.setText(soundDataList.get(7));
        mMsgTextArea_9.setText(soundDataList.get(8));
        mMsgTextArea_10.setText(soundDataList.get(9));
        mMsgTextArea_11.setText(soundDataList.get(10));
        mMsgTextArea_12.setText(soundDataList.get(11));
    }

    private void initKeyConfig() {

        mRoot.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F5) {
                System.out.println("F5");
                saveMsg();
                event.consume();
            }

            if (event.getCode() == KeyCode.ESCAPE) {
                System.out.println("ESCAPE");
                close();
                event.consume();
            }

        });

    }

    /**
     * 내부 저장된 메세지 가져옴.
     */
    private List<String> initParsingSoundDataList() {

        // 사운드 텍스트 저장 리스트.
        List<String> soundDataList = new ArrayList<String>();

        soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_INTRO, ""));
        soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_BUYER, ""));
        soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_GUIDE, ""));
        soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_PRACTICE, ""));
        soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_GENDER, ""));
        soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_USE, ""));
        soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_1, ""));
        soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_2, ""));
        soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_3, ""));
        soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_4, ""));
        soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_5, ""));
        soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_6, ""));

        return soundDataList;
    }

    /**
     * 메세지 저장
     */
    private void setSaveSharedMessage() {

        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_INTRO, mMsgTextArea_1.getText());
        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_BUYER, mMsgTextArea_2.getText());
        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_GUIDE, mMsgTextArea_3.getText());
        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_PRACTICE, mMsgTextArea_4.getText());
        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_GENDER, mMsgTextArea_5.getText());
        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_USE, mMsgTextArea_6.getText());
        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_1, mMsgTextArea_7.getText());
        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_2, mMsgTextArea_8.getText());
        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_3, mMsgTextArea_9.getText());
        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_4, mMsgTextArea_10.getText());
        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_5, mMsgTextArea_11.getText());
        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_6, mMsgTextArea_12.getText());

        showAlertPopup(mResMsg.getString("str.setting.sound.save"), mResMsg.getString("popup.btn.ok"));

        if (SettingApplication.getInstance().isTtsType()) {
        	SoundUtils.getInstance().soundSettingDataChanged();
        } else {
        	SoundUtil.getInstance().soundSettingDataChanged();
        }
    }

    /**
     * 메세지 내부 저장
     */
    private void saveMsg() {
        setSaveSharedMessage();
    }

    /**
     * 창 닫기
     */
    private void close() {
        MoveStageUtil.getInstance().dismissDialog();
        mBooleanListener.callBack(true);
        MoveStageUtil.getInstance().setBackStageDisableFalse(mStage);
    }

    private void showAlertPopup(String msg, String btnMsg) {
        CommonUtils.getInstance().showAlertPopupOneButton(MoveStageUtil.getInstance().getDialog(), msg, btnMsg);
    }
}
