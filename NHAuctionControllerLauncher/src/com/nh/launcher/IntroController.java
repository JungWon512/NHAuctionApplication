package com.nh.launcher;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.controller.utils.ApiUtils;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.SharedPreference;
import com.nh.launcher.util.FileUtils;
import com.nh.share.api.ActionResultListener;
import com.nh.share.api.NetworkDefine;
import com.nh.share.api.response.ResponseFileDownload;
import com.nh.share.api.response.ResponseVersion;
import com.nh.share.utils.SentryUtil;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * 인트로 컨트롤ㄹ서
 *
 * @author jspark
 */
public class IntroController implements Initializable {
	private final String RUN_AUCTION_CONTROLLER_PATH = "C:\\NHAuction\\NHController\\NHController.exe";
	private final String DOWNLOAD_PATH = "C:\\NHAuction\\NHController\\";
	private final String DOWNLOAD_FILE_NAME = "NHController.exe";
	
	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private Stage mStage = null;

	private ResourceBundle mResMsg = null;
	
	@FXML
	private Label prgMsg;
	

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
		
		//버전 체크
		requestApplicationVersion();
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
							mLogger.debug("프리퍼런스 : " + SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_APPLICATION_VERSION_INFO, GlobalDefine.APPLICATION_INFO.RELEASE_VERION));
							
							if(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_APPLICATION_VERSION_INFO, GlobalDefine.APPLICATION_INFO.RELEASE_VERION).toLowerCase().startsWith("v")) {
								currVersion = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_APPLICATION_VERSION_INFO, GlobalDefine.APPLICATION_INFO.RELEASE_VERION).substring(1).trim();
							}else {
								currVersion = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_APPLICATION_VERSION_INFO, GlobalDefine.APPLICATION_INFO.RELEASE_VERION).trim();
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
									
									prgMsg.setText("신규 버전 프로그램을 다운로드 중입니다.");
									
									FileUtils.getInstance().exeFileDownload(NetworkDefine.APPLICATION_DOWNLOAD_URL, DOWNLOAD_PATH, DOWNLOAD_FILE_NAME, new ActionResultListener<ResponseFileDownload>() {
										
										@Override
										public void onResponseResult(ResponseFileDownload result) {
											// TODO Auto-generated method stub
											mLogger.debug("onResponseResult : " + result);
											runAuctionController();
										}
										
										@Override
										public void onResponseError(String message) {
											// TODO Auto-generated method stub
											mLogger.debug("onResponseError : " + message);
										}
									}, null);
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
										prgMsg.setText("신규 버전 프로그램을 다운로드 중입니다.");
										
										FileUtils.getInstance().exeFileDownload(NetworkDefine.APPLICATION_DOWNLOAD_URL, DOWNLOAD_PATH, DOWNLOAD_FILE_NAME, new ActionResultListener<ResponseFileDownload>() {
											
											@Override
											public void onResponseResult(ResponseFileDownload result) {
												// TODO Auto-generated method stub
												mLogger.debug("onResponseResult : " + result);
												runAuctionController();
											}
											
											@Override
											public void onResponseError(String message) {
												// TODO Auto-generated method stub
												mLogger.debug("onResponseError : " + message);
											}
										}, null);
									}else {
										mLogger.debug("현재 버전을 유지합니다.");
										runAuctionController();
									}
								}else {
									mLogger.debug("경매 진행 프로그램은 현재 최신 버전입니다.");
									runAuctionController();
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
	
	private void runAuctionController() {
		Runtime rt = Runtime.getRuntime();
		String exeFile = RUN_AUCTION_CONTROLLER_PATH;
		Process p;
		                     
		try {
		    p = rt.exec(exeFile);
		    p.waitFor();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		CommonUtils.getInstance().applicationExit();
	}
}
