package com.nh.controller;

import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.controller.netty.AuctionDelegate;
import com.nh.controller.utils.MoveStageUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;


public class ControllerApplication extends Application {

	private Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public void start(Stage arg0) throws Exception {
		Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(this));
//		MoveStageUtil.getInstance().moveSampleStage(arg0);
		MoveStageUtil.getInstance().moveLoginStage(arg0);
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * 종료시 실행
	 * @author jhlee
	 *
	 */
	private class ShutdownHookThread extends Thread {

		ControllerApplication parent;

		public ShutdownHookThread(ControllerApplication parent) {
			this.parent = parent;
		}

		public void run() {
			parent.quit();
		}
	}

	/**
	 * 서비스 종료
	 */
	private void quit() {
		try {
			// 네티 종료
			if (AuctionDelegate.getInstance().isActive()) {
				
				Platform.runLater(() -> {
					
					AuctionDelegate.getInstance().onDisconnect(new NettyClientShutDownListener() {
						@Override
						public void onShutDown(int port) {
							mLogger.debug("[ShutdownHookThread AuctionApplication]");
						}
					});
					
				});

			}
			
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
}
