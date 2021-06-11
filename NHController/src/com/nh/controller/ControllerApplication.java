package com.nh.controller;

import com.nh.controller.utils.MoveStageUtil;

import javafx.application.Application;
import javafx.stage.Stage;

public class ControllerApplication extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			startMain(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startMain(Stage primaryStage) throws Exception {
		MoveStageUtil.getInstance().moveIntroStage(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
