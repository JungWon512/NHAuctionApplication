package com.nh.auction;

import com.nh.auction.utils.MoveStageUtil;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main Class
 * 
 * @author jhlee
 *
 */
public class ControllerApplication extends Application {

	@Override
	public void start(Stage arg0) throws Exception {

		
		MoveStageUtil.getInstance().moveSampleStage(arg0);

	}

	public static void main(String[] args) {
		launch(args);
	}
}
