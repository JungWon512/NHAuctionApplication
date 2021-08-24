package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.controller.model.AdminData;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.ApiUtils;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.GlobalDefine.AUCTION_INFO;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.SharedPreference;
import com.nh.share.api.ActionResultListener;
import com.nh.share.api.request.body.RequestLoginBody;
import com.nh.share.api.response.ResponseAuctionLogin;
import com.nh.share.code.GlobalDefineCode;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * 로그인
 *
 * @author jhlee
 */
public class LoginController implements Initializable {

	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private Stage mStage = null;

	private ResourceBundle mResMsg = null;

	@FXML
	private TextField	mIdTextField, // 아이디 
						mPwTextField; // 비밀번호

	@FXML
	private Button mBtnLogin;

	@FXML // 경매 거점 타입
	private ToggleGroup auctionHouseTypeToggleGroup;
	
	@FXML	//경매 거점, 하동,화순,무진장
	private ToggleButton mAuctionHouseHwadongToggleButton,mAuctionHouseHwaSunToggleButton,mAuctionHouseJangsuToggleButton;

	 
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
		initKeyConfig();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// get ResMsg
		if (resources != null) {
			mResMsg = resources;
		}

		// 앱 첫 실행시 기본 값들 저장
		SettingApplication.getInstance().initDefaultConfigration(mResMsg);

		//경매 거점 타입
		initAuctionHouseToggleTypes();

		mBtnLogin.setOnMouseClicked(event -> onLogin());
		
		testValues();
	}
	
	/**
	 * 테스트값. 지울것.
	 */
	private void testValues() {
		mIdTextField.setText(AUCTION_INFO.AUCTION_MEMBER);
		mPwTextField.setText("1111");
	}

	/**
	 * 경매 거점 Toggle
	 */
	private void initAuctionHouseToggleTypes() {

		//listener
		auctionHouseTypeToggleGroup.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_AUCTION_HOUSE_CODE, newValue.getUserData().toString().trim()));
		
		String auctionHouseToggle = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_AUCTION_HOUSE_CODE, "");

		switch (auctionHouseToggle.toUpperCase()) {
			case GlobalDefineCode.AUCTION_HOUSE_HWADONG -> mAuctionHouseHwadongToggleButton.setSelected(true);
			case GlobalDefineCode.AUCTION_HOUSE_HWASUN -> mAuctionHouseHwaSunToggleButton.setSelected(true);
			case GlobalDefineCode.AUCTION_HOUSE_JANGSU -> mAuctionHouseJangsuToggleButton.setSelected(true);
			default -> mAuctionHouseHwadongToggleButton.setSelected(true);
		}
	}
	
	/**
	 * 로그인 요청
	 */
	public void onLogin() {

		if (!CommonUtils.getInstance().isValidString(mIdTextField.getText()) && !CommonUtils.getInstance().isValidString(mPwTextField.getText())) {
			CommonUtils.getInstance().showAlertPopupOneButton(mStage, mResMsg.getString("dialog.login.empty.user.info"), mResMsg.getString("popup.btn.close"));
			return;
		}

		RequestLoginBody requestLoginBody = new RequestLoginBody(mIdTextField.getText().toString().trim(), mPwTextField.getText().toString().trim());

		//거점코드
		String nabzplc = auctionHouseTypeToggleGroup.getSelectedToggle().getUserData().toString();

		CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.login.request"));
		//로그읜
		ApiUtils.getInstance().requestLogin(nabzplc, requestLoginBody, new ActionResultListener<ResponseAuctionLogin>() {

			@Override
			public void onResponseResult(ResponseAuctionLogin result) {
				
				Platform.runLater(() ->{
					
					CommonUtils.getInstance().dismissLoadingDialog(); //dismiss loading
					
					mLogger.debug("[로그인 요청 결과]=> " + result.getSuccess() + "" + result.getAccessToken() + " / " + result.toString());

					if(result.getSuccess()) {
						//정보저장
						GlobalDefine.ADMIN_INFO.adminData = new AdminData();
						GlobalDefine.ADMIN_INFO.adminData.setUserId(mIdTextField.getText().toString().trim());
						GlobalDefine.ADMIN_INFO.adminData.setNabzplc(nabzplc);
						MoveStageUtil.getInstance().moveChooseAuctionStage(mStage);
				
					}else {
						CommonUtils.getInstance().showAlertPopupOneButton(mStage, result.getMessage(), mResMsg.getString("popup.btn.close"));
					}
					
				});
			}
			
			@Override
			public void onResponseError(String message) {
				Platform.runLater(() ->{
					CommonUtils.getInstance().dismissLoadingDialog();//dismiss loading
					CommonUtils.getInstance().showAlertPopupOneButton(mStage, message, mResMsg.getString("popup.btn.close"));
				});
			}
		});
	}

	/**
	 * 키 설정
	 */
	private void initKeyConfig() {

		Platform.runLater(() -> {
			mStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

				public void handle(KeyEvent ke) {

					if (ke.getCode() == KeyCode.ENTER) {
						onLogin();
						ke.consume();
					}

				}
			});
		});
	}
}
