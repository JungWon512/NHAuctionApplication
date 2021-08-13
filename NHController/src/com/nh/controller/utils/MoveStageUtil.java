package com.nh.controller.utils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.nh.controller.ControllerApplication;
import com.nh.controller.controller.AuctionController;
import com.nh.controller.controller.AuctionMessageController;
import com.nh.controller.controller.CommonController;
import com.nh.controller.controller.EntryListController;
import com.nh.controller.controller.EntryPendingListController;
import com.nh.controller.controller.LoginController;
import com.nh.controller.controller.SettingController;
import com.nh.controller.controller.SettingSoundController;
import com.nh.controller.interfaces.BooleanListener;
import com.nh.controller.interfaces.IntegerListener;
import com.nh.controller.interfaces.StringListener;
import com.nh.controller.model.SpEntryInfo;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Stage 이동 처리 Class
 * 
 * @author jhlee
 *
 */
public class MoveStageUtil {

	private static MoveStageUtil instance = null;
	
	private Dialog<Void> mDialog = null;
	
	public enum EntryDialogType{
		ENTRY_LIST,
		ENTRY_PENDING_LIST
	}

	public static synchronized MoveStageUtil getInstance() {

		if (instance == null) {
			instance = new MoveStageUtil();
		}

		return instance;
	}

	/**
	 * 로그인
	 * 
	 * @param stage
	 */
	public synchronized void moveLoginStage(Stage stage) {

		try {

			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("LoginView.fxml"), getResourceBundle());
			Parent parent = fxmlLoader.load();
			LoginController controller = fxmlLoader.getController();
			controller.setStage(stage);
			setRemoveTitlebar(stage);
			Scene scene = new Scene(parent);
			stage.setScene(scene);
			stage.show();
			centerOnScreen(stage);
			// show 후에 ..
			controller.initConfiguration();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 로그인 -> 경매 서버 접속 -> 경매 화면 이동
	 * 
	 * @param stage
	 */
	public synchronized void onConnectServer(Stage loginStage, String ip, int port, String id) {

		try {

			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("AuctionControllerView.fxml"), getResourceBundle());
			fxmlLoader.load();
			AuctionController controller = fxmlLoader.getController();
			controller.onConnectServer(loginStage, fxmlLoader, ip, port, id);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 경매 서버 접속 성공 -> 로그인 화면 닫음 -> 경매 제어 메인 show
	 * 
	 * @param stage
	 */
	public synchronized void moveAuctionStage(Stage loginStage, FXMLLoader fxmlLoader) {

		try {

			loginStage.close();

			AuctionController controller = fxmlLoader.getController();
			Stage stage = new Stage();
			controller.setStage(stage);
//			setRemoveTitlebar(stage);
			Scene scene = new Scene(fxmlLoader.getRoot());
			stage.setScene(scene);
			stage.show();
			// show 후에 ..
			controller.initConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @Description 환경설정 Dialog
	 */
	public void openSettingDialog(Stage stage) {

		if(mDialog != null && mDialog.isShowing()) {
			return;
		}
		
		try {

			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("SettingView.fxml"), getResourceBundle());

			Parent parent = fxmlLoader.load();

			SettingController controller = fxmlLoader.getController();

			controller.setStage(stage);

			openDialog(stage, parent);
			setStageDisable(stage);
			controller.initConfiguration();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 출품 리스트
	 * 출품 보류 리스트
	 * 
	 * @param stage
	 */
	
	public synchronized void openEntryListDialog(EntryDialogType type,Stage stage, ObservableList<SpEntryInfo> dataList, IntegerListener listener) {
		
		if(mDialog != null && mDialog.isShowing()) {
			return;
		}
		
		try {

			FXMLLoader fxmlLoader = null;
			
			switch (type) {
			case ENTRY_LIST:
				fxmlLoader = new FXMLLoader(getFXMLResource("EntryListView.fxml"), getResourceBundle());
				break;
			case ENTRY_PENDING_LIST:
				fxmlLoader = new FXMLLoader(getFXMLResource("EntryPendingListView.fxml"), getResourceBundle());
				break;
			}

			if(fxmlLoader == null) {
				return;
			}
			
			Parent parent = fxmlLoader.load();

			CommonController controller = fxmlLoader.getController();
			controller.setEntryDataList(dataList);
			controller.setListener(listener);

			openDialog(stage, parent);
			setStageDisable(stage);
			controller.initConfiguration();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	

	/**
	 * Open Dialog 전체보기,보류보기,환경설정
	 * 
	 * @param stage
	 */
	public void openDialog(Stage stage, Parent parent) {

		mDialog = new Dialog<>();
		DialogPane dialogPane = new DialogPane();

		dialogPane.getStylesheets().add(getApplicationClass().getResource("css/dialog.css").toExternalForm());
//		dialog.initStyle(StageStyle.UNDECORATED);
		
		mDialog.setResizable(true);
		mDialog.initModality(Modality.NONE);
		mDialog.setDialogPane(dialogPane);
		
		mDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
		Node closeButton = mDialog.getDialogPane().lookupButton(ButtonType.CLOSE);
		closeButton.managedProperty().bind(closeButton.visibleProperty());
		closeButton.setVisible(false);

		dialogPane.setContent(parent);
		mDialog.initOwner(stage);

//		stage.getScene().getRoot().setEffect(CommonUtils.getInstance().getDialogBlurEffect()); // 뒷 배경 블러처리 Add
//		stage.getScene().getRoot().setDisable(true);
//		Window window = mDialog.getDialogPane().getScene().getWindow();
//		window.setOnCloseRequest(e -> {
//			mDialog.close();
//			stage.getScene().getRoot().setEffect(null); // 뒷 배경 블러처리 remove
//			stage.getScene().getRoot().setDisable(false);
//			System.out.println("setOnCloseRequest");
//		});

		mDialog.show();
	}
	
	/**
	 * @Description 경매 음성 설정 Dialog
	 */
	public void openSettingSoundDialog(Stage stage,BooleanListener listener) {

		if(mDialog != null && mDialog.isShowing()) {
			return;
		}
		
		try {

			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("SettingSoundView.fxml"), getResourceBundle());

			Parent parent = fxmlLoader.load();

			SettingSoundController controller = fxmlLoader.getController();
			controller.setStage(stage);
			controller.setListener(listener);
			openDialog(stage, parent);
			
			setStageDisable(stage,listener);

			controller.initConfiguration();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * dialog 종료
	 */
	public void dismissDialog() {
		if(mDialog != null) {
			if(mDialog.isShowing()) {
				mDialog.close();
				mDialog = null;
				System.out.println("dismissDialog()");
			}
		}
	}
	
	/**
	 * @return mDialog
	 */
	public Dialog<Void> getDialog(){
		if(mDialog != null) {
			return mDialog;
		}else {
			return null;
		}
	}

	/**
	 * 이전 Stage Disable, Blower 처리 신규 Stage 닫기 처리
	 * 
	 * @param backStage
	 * @param newStage
	 */
	public void setStageDisable(Stage backStage) {

		backStage.getScene().getRoot().setEffect(CommonUtils.getInstance().getDialogBlurEffect()); // 뒷 배경 블러처리 Add
		backStage.getScene().getRoot().setDisable(true);

		Window window = mDialog.getDialogPane().getScene().getWindow();
		window.setOnCloseRequest(e -> {
			System.out.println("setStageDisable");
			setBackStageDisableFalse(backStage);
		});

	}

	/**
	 * 이전 Stage Disable, Blower 처리 신규 Stage 닫기 처리
	 * 
	 * @param backStage
	 * @param newStage
	 */
	public void setStageDisable(Stage backStage, BooleanListener listener) {

		backStage.getScene().getRoot().setEffect(CommonUtils.getInstance().getDialogBlurEffect()); // 뒷 배경 블러처리 Add
		backStage.getScene().getRoot().setDisable(true);

		Window window = mDialog.getDialogPane().getScene().getWindow();
		window.setOnCloseRequest(e -> {
			System.out.println("setStageDisable");
			listener.callBack(true);
			setBackStageDisableFalse(backStage);
		});

	}
	
	/**
	 * 이전 Stage Disable
	 * 
	 * @param backStage
	 * @param newStage
	 */
	public void setBackStageDisableFalse(Stage backStage) {
		if(backStage != null) {
			backStage.getScene().getRoot().setEffect(null); // 뒷 배경 블러처리 remove
			backStage.getScene().getRoot().setDisable(false);
		}
	}

	/**
	 * 
	 * @MethodName loadMessageFXMLLoader
	 * @Description
	 * 
	 * @return
	 */
	public synchronized void loadMessageFXMLLoader(Stage parentStage, StringListener listener) {

		try {

			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("AuctionMessageView.fxml"), getResourceBundle());
			Parent parent = fxmlLoader.load();
			AuctionMessageController controller = fxmlLoader.getController();
			controller.setCallBackListener(listener);
			Scene scene = new Scene(parent);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.showAndWait();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * FXML init
	 * 
	 * @param fxmlName_
	 * @return
	 */
	public synchronized URL getFXMLResource(String fxmlName_) {
		return getApplicationClass().getResource("view/" + fxmlName_);
	}

	/**
	 * CSS init
	 * 
	 * @param scene
	 */
	private synchronized void setCss(Scene scene) {
		scene.getStylesheets().add(getApplicationClass().getResource("css/application.css").toExternalForm());
	}

	/**
	 * Get Main class
	 * 
	 * @return
	 */
	private Class<?> getApplicationClass() {
		return ControllerApplication.class;
	}

	/**
	 * @MethodName setMinScreenSize
	 * @Description 최소 사이즈 설정
	 *
	 * @param stage
	 */
	public synchronized void setScreenMinSize(Stage stage) {
		stage.setMinWidth(stage.getWidth());
		stage.setMinHeight(stage.getHeight());
	}

	/**
	 * @MethodName setMaxScreenSize
	 * @Description 최소 사이즈 설정
	 *
	 * @param stage
	 */
	public synchronized void setMaxScreenSize(Stage stage) {
		ObservableList<Screen> screens = Screen.getScreens();

		if (screens.size() > 1 && (stage.getX() >= Screen.getPrimary().getBounds().getWidth())) {
			Rectangle2D subBounds = screens.get(1).getVisualBounds();

			stage.setWidth(subBounds.getWidth());
			stage.setHeight(subBounds.getHeight());
		} else {
			Rectangle2D mainBounds = screens.get(0).getVisualBounds();

			stage.setWidth(Screen.getPrimary().getBounds().getWidth());
			stage.setHeight(Screen.getPrimary().getBounds().getHeight());
		}
	}

	/**
	 * @MethodName setRemoveTitlebar
	 * @Description 창 크기 조절 막음
	 *
	 * @param stage
	 */
	public synchronized void setResizeScreen(Stage stage, boolean flag) {
		stage.setResizable(flag);
	}

	/**
	 * @MethodName setRemoveTitlebar
	 * @Description 타이틀바 제거
	 *
	 * @param stage
	 */
	public synchronized void setRemoveTitlebar(Stage stage) {
		stage.initStyle(StageStyle.UNDECORATED);
	}

	/**
	 * 
	 * @MethodName setParentPositionCenter
	 * @Description 부모뷰 화면 가운데 View 띄움
	 * 
	 * @param parentStage
	 * @param stage
	 */
	public synchronized void setParentPositionCenter(Stage parentStage, Stage stage) {
		double centerXPosition = parentStage.getX() + parentStage.getWidth() / 2d;
		double centerYPosition = parentStage.getY() + parentStage.getHeight() / 2d;
		stage.setX(centerXPosition - stage.getWidth() / 2d);
		stage.setY(centerYPosition - stage.getHeight() / 2d);
	}
	
	/**
	 * Stage 중앙 배치
	 * @param primaryStage
	 */
	public synchronized void centerOnScreen(Stage primaryStage) {
		Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
		primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
		primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
	}

	/**
	 * 리소스 파일
	 * 
	 * @return
	 */
	public ResourceBundle getResourceBundle() {
		return ResourceBundle.getBundle("com/nh/controller/utils/properties/UIResources");
	}

}
