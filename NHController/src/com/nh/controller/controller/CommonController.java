package com.nh.controller.controller;

import com.nh.controller.interfaces.IntegerListener;
import com.nh.controller.interfaces.StringListener;
import com.nh.controller.utils.CommonUtils;

import javafx.stage.Stage;

public class CommonController {

	protected Stage mStage = null; // 현재 Stage
	protected IntegerListener mIntegerListener = null; // listener
	protected StringListener mStringListener = null; // listener
	
	/**
	 * setStage
	 *
	 * @param stage
	 */
	public void setStage(Stage stage) {
		this.mStage = stage;
	}
	
	/**
	 * setStage
	 *
	 * @param stage
	 */
	public void setStage(Stage stage,IntegerListener listener) {
		this.mStage = stage;
		this.mIntegerListener = listener;
	}


	/**
	 * setStage
	 *
	 * @param stage
	 */
	public void setStage(Stage stage,StringListener listener) {
		this.mStage = stage;
		this.mStringListener = listener;
	}
	
	/**
	 * 구성 설정
	 */
	public void initConfiguration() {
		CommonUtils.getInstance().canMoveStage(mStage, null);
	}

}
