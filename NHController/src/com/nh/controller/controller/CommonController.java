package com.nh.controller.controller;

import com.nh.controller.interfaces.IntegerListener;
import com.nh.controller.interfaces.StringListener;
import com.nh.controller.model.SpEntryInfo;
import com.nh.controller.utils.CommonUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

public class CommonController {

	protected ObservableList<SpEntryInfo> mEntryDataList = FXCollections.observableArrayList(); // 출품 목록
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
	 * setListener
	 * @param listener
	 */
	public void setListener(IntegerListener listener) {
		this.mIntegerListener = listener;
	}
	

	/**
	 * 출품 목록 Set
	 * 
	 * @param dataList
	 */
	public void setEntryDataList(ObservableList<SpEntryInfo> dataList) {
		mEntryDataList.clear();
		mEntryDataList.addAll(dataList);
	}

	/**
	 * 구성 설정
	 */
	public void initConfiguration() {
//		CommonUtils.getInstance().canMoveStage(mStage, null);
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
