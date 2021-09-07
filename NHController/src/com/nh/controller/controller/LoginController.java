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
import com.nh.share.api.ActionResultListener;
import com.nh.share.api.request.body.RequestLoginBody;
import com.nh.share.api.response.ResponseAuctionLogin;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
	 * 로그인 요청
	 */
	public void onLogin() {

		if (!CommonUtils.getInstance().isValidString(mIdTextField.getText()) && !CommonUtils.getInstance().isValidString(mPwTextField.getText())) {
			CommonUtils.getInstance().showAlertPopupOneButton(mStage, mResMsg.getString("dialog.login.empty.user.info"), mResMsg.getString("popup.btn.close"));
			return;
		}

		final RequestLoginBody requestLoginBody = new RequestLoginBody(mIdTextField.getText().toString().trim(), mPwTextField.getText().toString().trim());

		CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.login.request"));
		//로그읜
		ApiUtils.getInstance().requestLogin(requestLoginBody, new ActionResultListener<ResponseAuctionLogin>() {

			@Override
			public void onResponseResult(ResponseAuctionLogin result) {
				
				Platform.runLater(() ->{
					
					CommonUtils.getInstance().dismissLoadingDialog(); //dismiss loading
					
					if(result.getSuccess()) {
						mLogger.debug("[로그인성공]=> " +result.getNaBzplc() + " / " + result.getAccessToken());
						//정보저장
						GlobalDefine.ADMIN_INFO.adminData = new AdminData();
						GlobalDefine.ADMIN_INFO.adminData.setUserId(mIdTextField.getText().toString().trim());
						GlobalDefine.ADMIN_INFO.adminData.setNabzplc(result.getNaBzplc());
						GlobalDefine.ADMIN_INFO.adminData.setAccessToken(result.getAccessToken());
						MoveStageUtil.getInstance().moveAuctionType(mStage);
				
					}else {
						
						if(CommonUtils.getInstance().isValidString(result.getMessage())) {
							CommonUtils.getInstance().showAlertPopupOneButton(mStage, result.getMessage(), mResMsg.getString("popup.btn.close"));
						}else {
							CommonUtils.getInstance().showAlertPopupOneButton(mStage, mResMsg.getString("msg.login.fail"), mResMsg.getString("popup.btn.close"));
						}
						
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
					
					if (ke.getCode() == KeyCode.ESCAPE) {
						Platform.exit();
						System.exit(0);
						ke.consume();
					}

				}
			});
		});
	}
}
