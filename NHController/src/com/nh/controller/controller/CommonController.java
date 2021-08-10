package com.nh.controller.controller;

import com.nh.controller.interfaces.IntegerListener;
import com.nh.controller.interfaces.StringListener;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.CommonUtils;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

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
	

	/**
	 * 컬럼 데이터 콤마 표시
	 * 
	 * @param column
	 */
	protected synchronized <T> void setNumberColumnFactory(TableColumn<T, String> column) {

		column.setCellFactory(col -> new TableCell<T, String>() {
			@Override
			protected void updateItem(String value, boolean empty) {
				super.updateItem(value, empty);
				if (value != null && !value.isEmpty() && !value.isBlank()) {
					setText(CommonUtils.getInstance().getNumberFormatComma(Integer.parseInt(value)));
				} else {
					setText("");
				}
			}
		});
	}
}
