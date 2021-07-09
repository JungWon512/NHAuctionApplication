package com.nh.controller;

import com.nh.controller.utils.MoveStageUtil;

import javafx.application.Application;
import javafx.stage.Stage;

public class ControllerApplication extends Application {
	
	@Override
	public void start(Stage arg0) throws Exception {
		MoveStageUtil.getInstance().moveSampleStage(arg0);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
