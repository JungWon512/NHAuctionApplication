package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.controller.interfaces.IntegerListener;
import com.nh.share.api.models.StnData;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * 경매 선택
 *
 * @author jhlee
 */
public class ChooseAuctionNumberRangeController implements Initializable {

	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private ResourceBundle mResMsg = null;
	
	@FXML
	private ListView<StnData> mRangeListView;
	
	private IntegerListener mListener;

	/**
	 * setData
	 *
	 * @param stage
	 */
	public void setData(List<StnData> stnList, IntegerListener listener) {
		mListener = listener;
		mRangeListView.getItems().addAll(stnList);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// get ResMsg
		if (resources != null) {
			mResMsg = resources;
		}
		
		mRangeListView.setOnMouseClicked(event -> selectRow());
		
		mRangeListView.setCellFactory(param -> new ListCell<StnData>() {
			@Override
		    protected void updateItem(StnData item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty || item == null) {
		            setText(null);
		        } else {
		        	setText(String.format(mResMsg.getString("str.auction.number.range"),item.getST_AUC_NO(),item.getED_AUC_NO()));
		        }
		    }
		});
	}
	
	
	/**
	 * 구간 정보 선택
	 * @param event
	 */
	public void selectRow() {
		
		if(mRangeListView != null && mRangeListView.getItems() != null && mRangeListView.getItems().size() > 0) {
			if(mRangeListView.getSelectionModel().getSelectedItem() != null) {
				int index = mRangeListView.getSelectionModel().getSelectedIndex();
				mListener.callBack(index);
			}
		}
	}
}
