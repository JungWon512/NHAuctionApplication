package com.nh.controller.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.nh.controller.model.SpEntryInfo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * 제어 메인 F4 -> 전체보기
 *
 * @author jhlee
 */
public class EntryPendingListController extends CommonController implements Initializable {

	private ResourceBundle mResMsg;

	@FXML
	private TableView<SpEntryInfo> mEntryTableView;

	@FXML // 대기중인 출품
	private TableColumn<SpEntryInfo, String> mEntryNumColumn, mExhibitorColumn, mGenderColumn, mMotherColumn, mMatimeColumn, mPasgQcnColumn, mWeightColumn, mLowPriceColumn, mSuccessPriceColumn, mSuccessfulBidderColumn, mResultColumn, mNoteColumn;

	
	/**
	 * 출품 목록 Set
	 * @param dataList
	 */
	public void setEntryDataList(ObservableList<SpEntryInfo> dataList) {
		mEntryDataList.clear();
		mEntryDataList.addAll(dataList);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// get ResMsg
		if (resources != null) {
			mResMsg = resources;
		}
		
		initTableConfiguration();
	}
	
	private void initTableConfiguration() {
		// 테이블 컬럼 - 대기
		mEntryNumColumn.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mExhibitorColumn.setCellValueFactory(cellData -> cellData.getValue().getExhibitor());
		mGenderColumn.setCellValueFactory(cellData -> cellData.getValue().getGender());
		mMotherColumn.setCellValueFactory(cellData -> cellData.getValue().getMotherObjNum());
		mMatimeColumn.setCellValueFactory(cellData -> cellData.getValue().getMatime());
		mPasgQcnColumn.setCellValueFactory(cellData -> cellData.getValue().getPasgQcn());
		mWeightColumn.setCellValueFactory(cellData -> cellData.getValue().getWeight());
		mLowPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getLowPrice());
		mSuccessPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getAuctionBidPrice());
		mSuccessfulBidderColumn.setCellValueFactory(cellData -> cellData.getValue().getAuctionSucBidder());
		mResultColumn.setCellValueFactory(cellData -> cellData.getValue().getBiddingResult());
		mNoteColumn.setCellValueFactory(cellData -> cellData.getValue().getNote());
		
		mEntryTableView.setPlaceholder(new Label(mResMsg.getString("msg.empty.list.default")));
		mEntryTableView.setItems(mEntryDataList);
		
		setNumberColumnFactory(mWeightColumn);
		setNumberColumnFactory(mLowPriceColumn);
		setNumberColumnFactory(mSuccessPriceColumn);
		
		mEntryTableView.setRowFactory(tv -> {
			
			TableRow<SpEntryInfo> row = new TableRow<>();

				row.setOnMouseClicked(event -> {
					if (event.getClickCount() == 2 && row.getItem() != null && row.getItem().getEntryNum() != null && !row.getItem().getEntryNum().getValue().isEmpty()) {
						int index = mEntryTableView.getSelectionModel().getSelectedIndex();
						if(index > -1) {
							mIntegerListener.callBack(mEntryTableView.getSelectionModel().getSelectedIndex());
						}
					}
				});
			
			return row;
		});
	}

}
