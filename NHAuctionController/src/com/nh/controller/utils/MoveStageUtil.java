package com.nh.controller.utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.nh.common.interfaces.UdpBillBoardStatusListener;
import com.nh.common.interfaces.UdpPdpBoardStatusListener;
import com.nh.controller.ControllerApplication;
import com.nh.controller.controller.AuctionController;
import com.nh.controller.controller.AuctionMessageController;
import com.nh.controller.controller.ChooseAuctionController;
import com.nh.controller.controller.ChooseAuctionNumberRangeController;
import com.nh.controller.controller.EntryListController;
import com.nh.controller.controller.LoginController;
import com.nh.controller.controller.MultipleAuctionController;
import com.nh.controller.controller.SettingController;
import com.nh.controller.controller.SettingSoundController;
import com.nh.controller.interfaces.BooleanListener;
import com.nh.controller.interfaces.IntegerListener;
import com.nh.controller.interfaces.MessageStringListener;
import com.nh.controller.interfaces.SelectEntryListener;
import com.nh.controller.interfaces.SettingListener;
import com.nh.controller.model.AuctionRound;
import com.nh.controller.netty.AuctionDelegate;
import com.nh.controller.setting.SettingApplication;
import com.nh.share.api.models.CowInfoData;
import com.nh.share.api.models.StnData;
import com.nh.share.code.GlobalDefineCode;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Stage 이동 처리 Class
 * 
 * @author jhlee
 *
 */
public class MoveStageUtil {

	private static MoveStageUtil instance = null;
	
	private Dialog<Void> mDialog = null;
	
	private boolean isShowingAuctionStage = false;
	
	
	public enum EntryDialogType{
		ENTRY_LIST,
		ENTRY_PENDING_LIST,
		ENTRY_FINISH_LIST
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
			setWindowTitle(stage,fxmlLoader.getResources());
//			setRemoveTitlebar(stage);
			Scene scene = new Scene(parent);
			stage.setScene(scene);
//			stage.setResizable(false);
			stage.show();
			centerOnScreen(stage);
		
			// show 후에 ..
			controller.initConfiguration();
			
			setWindowClose(stage);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 단일경매 타입 => moveChooseAuctionStage 이동
	 * 일괄경매 타입 => moveMultipleAuctionStage 이동
	 * @param previousStage 이전 스테이지
	 */
	public synchronized void moveAuctionType(Stage previousStage) {
		moveChooseAuctionStage(previousStage);
	}

	/**
	 * 경매 구분 ,경매일 선택
	 * @param stage
	 */
	public synchronized void moveChooseAuctionStage(Stage previousStage) {
		
		try {
			previousStage.close();
			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("ChooseAuctionView.fxml"), getResourceBundle());
			Parent parent = fxmlLoader.load();
			Stage stage = new Stage();
			ChooseAuctionController controller = fxmlLoader.getController();
			controller.setStage(stage);
			setWindowTitle(stage,fxmlLoader.getResources());
			Scene scene = new Scene(parent);
			stage.setScene(scene);
			stage.show();
			
			centerOnScreen(stage);
			
			isShowingAuctionStage = false;
			
			setWindowClose(stage);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 윈도우 X 버튼 
	 * @param stage
	 */
	private void setWindowClose(Stage stage) {
		
		if (stage != null) {
			
			stage.setOnCloseRequest(e -> {

				if (AuctionDelegate.getInstance().isActive()) {
					AuctionDelegate.getInstance().onDisconnect(null);
				}
				
				Platform.exit();
				System.exit(0);
			});
		}
	}
	
	/**
	 * 로그인 -> 경매 서버 접속 -> 경매 화면 이동
	 * 
	 * @param stage
	 */
	public synchronized void onConnectServer(Stage chooseAuctionStage, String ip, int port, String id) throws Exception{
		
		isShowingAuctionStage = false;
		
		if(SettingApplication.getInstance().isSingleAuction()) {
			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("AuctionControllerView.fxml"), getResourceBundle());
			fxmlLoader.load();
			AuctionController controller = fxmlLoader.getController();
			controller.onConnectServer(chooseAuctionStage, fxmlLoader, ip, port, id);
		}else {
			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("MultipleAuctionControllerView.fxml"), getResourceBundle());
			fxmlLoader.load();
			MultipleAuctionController controller = fxmlLoader.getController();
			controller.onConnectServer(chooseAuctionStage, fxmlLoader, ip, port, id);
		}

	}
	
	
	/**
	 * 로그인 -> 경매 서버 접속 -> 경매 화면 이동
	 * 
	 * @param stage
	 */
	public synchronized void onConnectServer(Stage chooseAuctionStage, String ip, int port, String id,List<CowInfoData> cowDataList) throws Exception{
		
		isShowingAuctionStage = false;
		
		if(SettingApplication.getInstance().isSingleAuction()) {
			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("AuctionControllerView.fxml"), getResourceBundle());
			fxmlLoader.load();
			AuctionController controller = fxmlLoader.getController();
			controller.onConnectServer(chooseAuctionStage, fxmlLoader, ip, port, id);
			controller.setCowData(cowDataList);
		}else {
			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("MultipleAuctionControllerView.fxml"), getResourceBundle());
			fxmlLoader.load();
			MultipleAuctionController controller = fxmlLoader.getController();
			controller.onConnectServer(chooseAuctionStage, fxmlLoader, ip, port, id);
			controller.setCowData(cowDataList);
		}

	}

	/**
	 * 경매 서버 접속 성공 -> 로그인 화면 닫음 -> 경매 제어 메인 show
	 * 
	 * @param stage
	 */
	public synchronized void moveAuctionStage(Stage loginStage, FXMLLoader fxmlLoader) {

		try {

			if(isShowingAuctionStage) {
				return;
			}
			
			loginStage.close();
			
			isShowingAuctionStage = true;
			
			Stage stage = new Stage();
			setWindowTitle(stage,fxmlLoader.getResources());
			Scene scene = new Scene(fxmlLoader.getRoot());
			
			if(SettingApplication.getInstance().isSingleAuction()) {
				AuctionController controller = fxmlLoader.getController();
				controller.setStage(stage);
				stage.setScene(scene);
				stage.show();
				// show 후에 ..
				controller.initConfiguration(stage);
			}else {
				MultipleAuctionController controller = fxmlLoader.getController();
				controller.setStage(stage);
				setWindowTitle(stage,fxmlLoader.getResources());
				stage.setScene(scene);
				stage.show();
				// show 후에 ..
				controller.initConfiguration(stage);
			}
			
			Platform.runLater(()->CommonUtils.getInstance().showLoadingDialog(stage, fxmlLoader.getResources().getString("dialog.searching.entry.list")));
//			auctionStageShowLoadingDialog(stage,fxmlLoader.getResources());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void auctionStageShowLoadingDialog(Stage stage ,ResourceBundle resources ) {
		
		PauseTransition pauseTransition = new PauseTransition(Duration.millis(200));
		pauseTransition.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				CommonUtils.getInstance().showLoadingDialog(stage, resources.getString("dialog.searching.entry.list"));
			}
		});
		pauseTransition.play();
	}

	/**
	 * @Description 환경설정 Dialog
	 */
	public void openSettingDialog(Stage stage,boolean isDisplayBordConnection,SettingListener listener , UdpBillBoardStatusListener udpStatusListener1, UdpBillBoardStatusListener udpStatusListener2, UdpPdpBoardStatusListener udpPdpBoardStatusListener) {

		if(mDialog != null && mDialog.isShowing()) {
			return;
		}
		
		try {

			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("SettingView.fxml"), getResourceBundle());

			Parent parent = fxmlLoader.load();

			SettingController controller = fxmlLoader.getController();

			controller.setStage(stage, isDisplayBordConnection, listener, udpStatusListener1, udpStatusListener2,
					udpPdpBoardStatusListener);

			openDialog(stage, parent,getResourceBundle().getString("app.title.setting.config"));
			setStageDisable(stage);
			controller.initConfiguration();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	/**
	 * 출품 리스트 ENTRY_LIST
	 * 출품 보류 리스트 ENTRY_PENDING_LIST
	 * 낙찰 결과 리스트 ENTRY_FINISH_LIST
	 * @param stage
	 */
	
	public synchronized void openEntryListDialog(EntryDialogType type,Stage stage,AuctionRound auctionRound, SelectEntryListener listener) {
		
		if(mDialog != null && mDialog.isShowing()) {
			return;
		}
		
		try {
			
			String pageTitle = "";
			
			if(type.equals(EntryDialogType.ENTRY_LIST)) {
				pageTitle = getResourceBundle().getString("app.title.entry.list");
			}else if(type.equals(EntryDialogType.ENTRY_PENDING_LIST)) {
				pageTitle = getResourceBundle().getString("app.title.entry.list.pending");
			}else if(type.equals(EntryDialogType.ENTRY_FINISH_LIST)) {
				pageTitle = getResourceBundle().getString("app.title.entry.list.finish");
			}
			
			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("EntryListView.fxml"), getResourceBundle());
			Parent parent = fxmlLoader.load();
			EntryListController controller = fxmlLoader.getController();
			openDialog(stage, parent,pageTitle);
			
			PauseTransition pauseTransition = new PauseTransition(Duration.millis(200));
			pauseTransition.setOnFinished(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					stage.getScene().getRoot().setEffect(CommonUtils.getInstance().getDialogBlurEffect()); // 뒷 배경 블러처리 Add
					stage.getScene().getRoot().setDisable(true);
					controller.setConfig(stage,type,auctionRound,listener);
					controller.setOnCloseRequest();
				
				}
			});
			pauseTransition.play();
			
					

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	

	/**
	 * Open Dialog 전체보기,보류보기,환경설정
	 * 
	 * @param stage
	 */
	public void openDialog(Stage stage, Parent parent,String title) {

		mDialog = new Dialog<>();
		DialogPane dialogPane = new DialogPane();

		dialogPane.getStylesheets().add(getApplicationClass().getResource("css/dialog.css").toExternalForm());
//		dialog.initStyle(StageStyle.UNDECORATED);
		
		mDialog.setResizable(true);
		mDialog.initModality(Modality.NONE);
		mDialog.setDialogPane(dialogPane);
		
		if(CommonUtils.getInstance().isValidString(title)) {
			mDialog.setTitle(title);
		}
		
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
			openDialog(stage, parent,getResourceBundle().getString("app.title.setting.sound"));
			
			setStageDisable(stage,listener);

			controller.initConfiguration();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 일괄 경매 구간 선택
	 */
	public void showChooseAuctionNumberRange(Stage rootStage , List<StnData> stnList,IntegerListener listener) {

		try {
			

			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("ChooseAuctionNumberRangeView.fxml"), getResourceBundle());
			Parent parent = fxmlLoader.load();
			ChooseAuctionNumberRangeController controller = fxmlLoader.getController();
			controller.setData(stnList,listener);
			
			mDialog = new Dialog<>();
			DialogPane dialogPane = new DialogPane();

			dialogPane.getStylesheets().add(getApplicationClass().getResource("css/dialog.css").toExternalForm());

			mDialog.setResizable(true);
			mDialog.initModality(Modality.NONE);
			mDialog.setDialogPane(dialogPane);
			mDialog.setTitle(getResourceBundle().getString("app.title.auction.number.range"));


			mDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
			Node closeButton = mDialog.getDialogPane().lookupButton(ButtonType.CLOSE);
			closeButton.managedProperty().bind(closeButton.visibleProperty());
			closeButton.setVisible(false);

			dialogPane.setContent(parent);
			mDialog.initOwner(rootStage);
			mDialog.show();

			setStageDisable(rootStage);
			
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
			System.out.println("1 setStageDisable");
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
	public synchronized Stage loadMessageFXMLLoader(Stage parentStage, MessageStringListener listener) {

		Stage stage = null;
		try {

			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("AuctionMessageView.fxml"), getResourceBundle());
			Parent parent = fxmlLoader.load();
			AuctionMessageController controller = fxmlLoader.getController();
			controller.setCallBackListener(listener);
			Scene scene = new Scene(parent);
			stage = new Stage();
			setWindowTitle(stage, getResourceBundle().getString("app.title.message"));
			stage.setScene(scene);
			stage.show();
			
			if (stage != null) {
				stage.setOnCloseRequest(e -> {
					listener.onClose();
				});
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return stage;
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
	

	/**
	 * 타이틀바 텍스트
	 * @param stage
	 * @param resources
	 */
	private void setWindowTitle(Stage stage, ResourceBundle resources) {
		
		if(GlobalDefineCode.FLAG_PRD) {
			stage.setTitle(	String.format(resources.getString("app.title"), GlobalDefine.APPLICATION_INFO.RELEASE_VERION));	
		}else {
			stage.setTitle(	String.format(resources.getString("app.title.dev"), GlobalDefine.APPLICATION_INFO.RELEASE_VERION));
		}
		
		stage.getIcons().add(new Image(getApplicationClass().getResourceAsStream("resource/images/ic_logo.png")));
	}
	
	/**
	 * 타이틀바 텍스트
	 * @param stage
	 * @param resources
	 */
	private void setWindowTitle(Stage stage, String msg) {
		if(CommonUtils.getInstance().isValidString(msg)) {
			stage.setTitle(msg);
		}else {
			stage.setTitle("");
		}
	}


}
