package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine.AUCTION_INFO;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.SharedPreference;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * 로그인
 *
 * @author jhlee
 */
public class LoginController implements Initializable {

	private Stage mStage;
	private ResourceBundle mResMsg;
	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@FXML
	private TextField mIpTextField, // ip
			mPortTextField, // port
			mIdTextField; // id

	@FXML
	private Button mBtnConnection;

	/**
	 * setStage
	 *
	 * @param stage
	 */
	public void setStage(Stage stage) {
		mStage = stage;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// get ResMsg
		if (resources != null) {
			mResMsg = resources;
		}
		mBtnConnection.setOnMouseClicked(event -> onConnectServer(event));

		initSharedConfigration();
	}

	/**
	 * 구성 설정
	 */
	public void initConfiguration() {
		CommonUtils.getInstance().canMoveStage(mStage, null);
	}

	public void onConnectServer(MouseEvent event) {

		mIpTextField.setText(AUCTION_INFO.AUCTION_HOST);
		mPortTextField.setText(Integer.toString(AUCTION_INFO.AUCTION_PORT));
		mIdTextField.setText(AUCTION_INFO.AUCTION_MEMBER);

		if (!mIpTextField.getText().isEmpty() && !mPortTextField.getText().isEmpty() && !mIdTextField.getText().isEmpty()) {

			CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("msg.connection"));

			String ip = mIpTextField.getText().toString().trim();
			int port = Integer.parseInt(mPortTextField.getText().toString().trim());
			String id = mIdTextField.getText().toString().trim();

			MoveStageUtil.getInstance().onConnectServer(mStage, ip, port, id);

		} else {
			CommonUtils.getInstance().showAlertPopupOneButton(mStage, "접속 정보를 입력해주세요.", mResMsg.getString("popup.btn.close"));
		}

	}

	private void initSharedConfigration() {

		boolean isFirstApplication = SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_IS_FIRST_APPLICATIOIN, true);

		if (isFirstApplication) {
			mLogger.debug("설치 후 첫 실행");
			
			// [S] 메인 경매 정보 음성 노출 여부 기본 설정
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_NUMBER,true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_EXHIBITOR,true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_GENDER,true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MOTHER, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MATIME, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_PASGQCN, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_WEIGHT, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_LOWPRICE,true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_BRAND,true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_KPN, true);
			// [E] 메인 경매 정보 음성 노출 여부 기본 설정
			
			// [S] 경매 음성 메세지 기본 설정
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_INTRO, mResMsg.getString("default.msg.setting.sound.intro"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_BUYER, mResMsg.getString("default.msg.setting.sound.buyer"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_GUIDE, mResMsg.getString("default.msg.setting.sound.guide"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_PRACTICE, mResMsg.getString("default.msg.setting.sound.practice"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_GENDER, mResMsg.getString("default.msg.setting.sound.gender"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_USE, mResMsg.getString("default.msg.setting.sound.use"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_1, mResMsg.getString("default.msg.setting.sound.etc1"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_2, mResMsg.getString("default.msg.setting.sound.etc2"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_3, mResMsg.getString("default.msg.setting.sound.etc3"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_4, mResMsg.getString("default.msg.setting.sound.etc4"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_5, mResMsg.getString("default.msg.setting.sound.etc5"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_6, mResMsg.getString("default.msg.setting.sound.etc6"));
			// [E] 경매 음성 메세지 기본 설정
			
			// 첫실행 후 false
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_IS_FIRST_APPLICATIOIN, false);
		} else {
			mLogger.debug("설치 후 첫 실행 아님.");
		}
	}

}
