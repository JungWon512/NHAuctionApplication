package com.nh.controller.controller;

import com.nh.controller.utils.CommonUtils;

import javafx.stage.Stage;

public class CommonController {

	protected Stage mStage = null; // 현재 Stage

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
	}

}
