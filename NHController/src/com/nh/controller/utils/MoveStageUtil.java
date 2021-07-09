package com.nh.controller.utils;

import java.net.URL;
import java.util.ResourceBundle;

import com.nh.controller.ControllerApplication;
import com.nh.controller.controller.AuctionController;
import com.nh.controller.controller.AuctionMessageController;
import com.nh.controller.controller.LoginController;
import com.nh.controller.interfaces.StringListener;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Stage 이동 처리 Class
 * 
 * @author jhlee
 *
 */
public class MoveStageUtil {

	private static MoveStageUtil instance = null;

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
	public synchronized void onConnectServer(Stage loginStage,String ip, int port , String id) {

		try {

			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("AuctionControllerView.fxml"), getResourceBundle());
			fxmlLoader.load();
			AuctionController controller = fxmlLoader.getController();
			controller.onConnectServer(loginStage, fxmlLoader,ip,port,id);

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
			setScreenMinSize(stage);
		} catch (Exception e) {
			e.printStackTrace();
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
	 * 리소스 파일
	 * 
	 * @return
	 */
	public ResourceBundle getResourceBundle() {
		return ResourceBundle.getBundle("com/nh/controller/utils/properties/UIResources");
	}

}