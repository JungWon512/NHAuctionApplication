package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.controller.model.AdminData;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.ApiUtils;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.SharedPreference;
import com.nh.share.api.ActionResultListener;
import com.nh.share.api.NetworkDefine;
import com.nh.share.api.request.body.RequestLoginBody;
import com.nh.share.api.response.ResponseAuctionLogin;
import com.nh.share.api.response.ResponseVersion;
import com.nh.share.utils.SentryUtil;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
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
	private TextField	mIdTextField; // 아이디
	
	@FXML
	private PasswordField	mPwTextField; // 비밀번호 

	@FXML	//로그인
	private Button mBtnLogin;
	
	@FXML	//아이디 저장
	private CheckBox mSaveIdCheckBox;

	 
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
		
		//버전 체크
		requestApplicationVersion();
		
		// 앱 첫 실행시 기본 값들 저장
		SettingApplication.getInstance().initDefaultConfigration(mResMsg);

		mBtnLogin.setOnMouseClicked(event -> onLogin());
		
		String savedID = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_LOGIN_SAVE_ID,"");
		
		if(CommonUtils.getInstance().isValidString(savedID)) {
			mIdTextField.setText(savedID.trim());
			Platform.runLater(()->mPwTextField.requestFocus());
			mSaveIdCheckBox.setSelected(true);
		}
	}

	
	/**
	 * 버전 체크
	 */
	private void requestApplicationVersion() {
		
		ApiUtils.getInstance().requestVersion(new ActionResultListener<ResponseVersion>() {
			@Override
			public void onResponseResult(ResponseVersion result) {

				Platform.runLater(()->{

				try {

					if(result !=null) {
	
						if(result.getSuccess()) {
	
							String currVersion = "";
							String minVersion = "";
							String maxVersion = "";
							
							//현재 버전 v 제거
							if(GlobalDefine.APPLICATION_INFO.RELEASE_VERION.toLowerCase().startsWith("v")) {
								currVersion = GlobalDefine.APPLICATION_INFO.RELEASE_VERION.substring(1).trim();
							}else {
								currVersion = GlobalDefine.APPLICATION_INFO.RELEASE_VERION.trim();
							}
							
							//현재 경매 진행 버전
							if(currVersion.contains(".")) {
								currVersion = currVersion.replace(".", "");
							}
							
							//서버 경매 진행 최소 버전
							if(result.getInfo().getMIN_VERSION().contains(".")) {
								minVersion = result.getInfo().getMIN_VERSION().replace(".","").trim();
							}else {
								minVersion = result.getInfo().getMIN_VERSION().trim();
							}
							
							//서버 경매 진행 최대 버전
							if(result.getInfo().getMAX_VERSION().contains(".")) {
								maxVersion = result.getInfo().getMAX_VERSION().replace(".","").trim();
							}else {
								maxVersion = result.getInfo().getMAX_VERSION().trim();
							}
							
							mLogger.debug("현재 프로그램 버전 : " + currVersion + " / 서버 프로그램 최소 버전 : " + minVersion + " / 서버 프로그램 최대 버전 : " + maxVersion);

							if(Integer.parseInt(currVersion) < Integer.parseInt(minVersion)) {
								//최소 버전보다 낮은경우 강제 업데이트
								mLogger.debug("프로그램 버전이 최소 버전보다 낮습니다.");
								Optional<ButtonType> btnResult = CommonUtils.getInstance().showAlertPopupTwoButton(mStage, mResMsg.getString("dialog.version.update"), mResMsg.getString("popup.btn.update"), mResMsg.getString("popup.btn.exit"));
	
								if (btnResult.get().getButtonData() == ButtonData.LEFT) {
									//경매 진행 프로그램 다운로드
									CommonUtils.getInstance().openBrowse(NetworkDefine.APPLICATION_DOWNLOAD_URL, true);
								}else {
									//종료
									CommonUtils.getInstance().applicationExit();
								}
								
							}else {
								
								if(Integer.parseInt(currVersion) < Integer.parseInt(maxVersion)) {

									mLogger.debug("업데이트 가능한 버전이 있습니다.");

									//선택 업데이트
									Optional<ButtonType> btnResult = CommonUtils.getInstance().showAlertPopupTwoButton(mStage, mResMsg.getString("dialog.version.update"), mResMsg.getString("popup.btn.update"), mResMsg.getString("popup.btn.close"));
									
									if (btnResult.get().getButtonData() == ButtonData.LEFT) {
										//경매 진행 프로그램 다운로드
										CommonUtils.getInstance().openBrowse(NetworkDefine.APPLICATION_DOWNLOAD_URL, true);
									}else {
										mLogger.debug("현재 버전을 유지합니다.");
									}
								}else {
									mLogger.debug("경매 진행 프로그램은 현재 최신 버전입니다.");
								}
							}
							
						}else {
							//api success false
							CommonUtils.getInstance().showAlertPopupOneButton(mStage,result.getMessage(), mResMsg.getString("popup.btn.close"));	
						}
						
					}else {
						//api object null 
						CommonUtils.getInstance().showAlertPopupOneButton(mStage, mResMsg.getString("str.api.response.fail"), mResMsg.getString("popup.btn.close"));	
					}

					}catch (Exception e) {
						e.printStackTrace();
						SentryUtil.getInstance().sendExceptionLog(e);
					}

				});
			}
			
			@Override
			public void onResponseError(String message) {
				mLogger.debug("버전체크 api error msg " + message);
				Platform.runLater(()-> CommonUtils.getInstance().showAlertPopupOneButton(mStage, mResMsg.getString("str.api.response.fail"), mResMsg.getString("popup.btn.close")));
			}
		});
		
	}
	
	/**
	 * 로그인 요청
	 */
	public void onLogin() {

		if (!CommonUtils.getInstance().isValidString(mIdTextField.getText()) || !CommonUtils.getInstance().isValidString(mPwTextField.getText())) {
			CommonUtils.getInstance().showAlertPopupOneButton(mStage, mResMsg.getString("dialog.login.empty.user.info"), mResMsg.getString("popup.btn.ok"));
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
						
						if(mSaveIdCheckBox.isSelected()) {
							SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_LOGIN_SAVE_ID,mIdTextField.getText().toString().trim());
						}
						
						mLogger.debug("[로그인성공]=> " +result.getNaBzplc() + " / " + result.getAccessToken());
						//정보저장
						GlobalDefine.ADMIN_INFO.adminData = new AdminData();
						GlobalDefine.ADMIN_INFO.adminData.setUserId(mIdTextField.getText().toString().trim());
						GlobalDefine.ADMIN_INFO.adminData.setNabzplc(result.getNaBzplc());
						GlobalDefine.ADMIN_INFO.adminData.setAccessToken(result.getAccessToken());
						NetworkDefine.ADMIN_ACCESS_TOKEN = result.getAccessToken();
						
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
					mLogger.debug("로그인 api error msg " + message);
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
						CommonUtils.getInstance().applicationExit();
						ke.consume();
					}
				}
			});
		});
	}
}
