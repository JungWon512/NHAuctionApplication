package com.nh.auction.utills;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auction.AuctionApplication;
import com.nh.auction.controller.TestController;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class MoveStageUtil {

	private Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public Stage mMainStage = null;

	private static MoveStageUtil instance = new MoveStageUtil();

	private ScheduledExecutorService mService = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> mDelayJob;

	public static synchronized MoveStageUtil getInstance() {
		return instance;
	}

	private Class<?> mClass = AuctionApplication.class;

	public synchronized void moveTestPage(Stage stage) {

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("TestView.fxml"), null);
			Parent parent = fxmlLoader.load();

			Stage newStage = new Stage();

			TestController auctionController = fxmlLoader.getController();
			auctionController.setStage(newStage);

			Scene scene = new Scene(parent);
			// newStage.setFullScreen(true);

			//setRemoveTitlebar(newStage);
			newStage.setMaximized(false);
			newStage.setScene(scene);

			newStage.setOnShown(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					startDelayTimer(stage);
				}
			});

			newStage.show();

			// 경매 화면 세션 체크 안함
			AuctionUtil.getInstance().sessionCheckClose(newStage);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public URL getFXMLResource(String fxmlName_) {
		return mClass.getResource("view/" + fxmlName_);
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
	 * @MethodName setScreenPositionCenter
	 * @Description 윈도우 화면 가운데 View 띄움
	 *
	 * @param stage
	 */
	public synchronized void setScreenPositionCenter(Stage stage) {
		// 부모 창이 위치한 화면에서 전체 화면으로 띄우도록 예외 처리
		ObservableList<Screen> screens = Screen.getScreens();

		if (screens.size() > 1 && (stage.getX() >= Screen.getPrimary().getBounds().getWidth())) {
			Rectangle2D mainBounds = screens.get(0).getVisualBounds();
			Rectangle2D subBounds = screens.get(1).getVisualBounds();

			stage.setX((mainBounds.getWidth() + (subBounds.getWidth() - stage.getWidth()) / 2));
			stage.setY((subBounds.getHeight() - stage.getHeight()) / 2);
		} else {
			Rectangle2D mainBounds = screens.get(0).getVisualBounds();

			stage.setX((mainBounds.getWidth() - stage.getWidth()) / 2);
			stage.setY((mainBounds.getHeight() - stage.getHeight()) / 2);
		}
	}

	public synchronized void setIcon(Stage stage) {
		if (stage != null) {
			stage.getIcons().add(new Image(mClass.getResourceAsStream("resources/images/AuctionApplication.png")));
		}
	}

	private void startDelayTimer(Stage closeStage) {
		mDelayJob = mService.schedule(new DelayTimerJob(closeStage), 100, TimeUnit.MILLISECONDS);
	}

	private class DelayTimerJob implements Runnable {
		private Stage stage;

		public DelayTimerJob(Stage closeStage) {
			this.stage = closeStage;
		}

		@Override
		public void run() {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					stage.close();
				}
			});
		}
	}
}
