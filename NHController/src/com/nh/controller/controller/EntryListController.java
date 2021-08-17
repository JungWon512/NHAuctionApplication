package com.nh.controller.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.nh.controller.interfaces.IntegerListener;
import com.nh.controller.model.SpEntryInfo;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.MoveStageUtil.EntryDialogType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

/**
 * 제어 메인 F4 -> 전체보기
 *
 * @author jhlee
 */
public class EntryListController implements Initializable {

	private ResourceBundle mResMsg;
	
	@FXML
	private BorderPane mRoot;

	@FXML
	private TableView<SpEntryInfo> mEntryTableView;
	
	@FXML //페이지 타이틀 , 최저가 낮춤
	private Label mTitleLabel,mDownPriceLabel;

	@FXML // 대기중인 출품
	private TableColumn<SpEntryInfo, String> mEntryNumColumn, mExhibitorColumn, mGenderColumn, mMotherColumn, mMatimeColumn, mPasgQcnColumn, mWeightColumn, mLowPriceColumn, mSuccessPriceColumn, mSuccessfulBidderColumn, mResultColumn, mNoteColumn;

	@FXML //최저가 낮춤
	private TextField mDownPriceTextField;
	
	@FXML //최저가 낮춤,선택,종료
	private Button mBtnDownPrice,mBtnSelect,mBtnClose;
	
	private ObservableList<SpEntryInfo> mEntryDataList = FXCollections.observableArrayList(); // 출품 목록
	private IntegerListener mIntegerListener = null; // listener
	private EntryDialogType mCurPageType= null; // pageType
	

	/**
	 *
	 * @param dataList 출품 리스트 
	 * @param type 페이지 타입 (전체/보류/낙찰결과)
	 * @param listener (전체/보류 row 선택시 콜백)
	 */
	public void setConfig(ObservableList<SpEntryInfo> dataList,EntryDialogType type,IntegerListener listener) {
		
		this.mEntryDataList.clear();
		this.mEntryDataList.addAll(dataList);
		this.mIntegerListener = listener;
		
		setPageType(type);
	}
	
	/**
	 * 페이지 타입
	 * @param EntryDialogType
	 */
	public void setPageType(EntryDialogType type) {

		String pageTitle = "";

		switch (type) {
		case ENTRY_LIST: 
			
			initClickCallback();
			mBtnSelect.setVisible(true);
			mBtnSelect.setOnMouseClicked(event -> onCallBack());
			pageTitle = mResMsg.getString("str.page.title.auction_all_list");
			break;
		case ENTRY_PENDING_LIST :
			
			initClickCallback();
			mDownPriceLabel.setVisible(true);
			mDownPriceTextField.setVisible(true);
			mBtnDownPrice.setVisible(true);
			mBtnDownPrice.setOnMouseClicked(event -> onDownPrice(event));
			pageTitle = mResMsg.getString("str.page.title.auction_pending_list");

			mBtnSelect.setVisible(true);
			mBtnSelect.setOnMouseClicked(event -> onCallBack());
			break;
		case ENTRY_FINISH_LIST :
			pageTitle = mResMsg.getString("str.page.title.auction_finish_list");
			break;
		}

		mTitleLabel.setText(pageTitle);
		mBtnClose.setOnMouseClicked(event -> onClose());
		
		this.mCurPageType = type;
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// get ResMsg
		if (resources != null) {
			mResMsg = resources;
		}

		initKeyConfig();
		
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
		
	}

	
	/**
	 * 예정가 낮추기
	 *
	 * @param event
	 */
	public void onDownPrice(MouseEvent event) {
		System.out.println("예정가 낮추기");
		int lowPrice = SettingApplication.getInstance().getCowLowerLimitPrice() * -1;
	}
	
	/**
	 * 컬럼 데이터 콤마 표시
	 * 
	 * @param column
	 */
	public synchronized <T> void setNumberColumnFactory(TableColumn<T, String> column) {

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
	
	
	/**
	 * callback -> 제어 메인
	 */
	private void initClickCallback() {

		mEntryTableView.setRowFactory(tv -> {
			
			TableRow<SpEntryInfo> row = new TableRow<>();

				row.setOnMouseClicked(event -> {
					if (event.getClickCount() == 2 && row.getItem() != null && row.getItem().getEntryNum() != null && !row.getItem().getEntryNum().getValue().isEmpty()) {
						onCallBack();
					}
				});
			
			return row;
		});

	}
	
	private void initKeyConfig() {

		mRoot.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			
			if (event.getCode() == KeyCode.ENTER) {
				switch (mCurPageType) {
				case ENTRY_LIST: 
				case ENTRY_PENDING_LIST :
					System.out.println("F5");
					onCallBack();
					break;
				}
				event.consume();
			}

			if (event.getCode() == KeyCode.ESCAPE) {
				System.out.println("ESCAPE");
				onClose();
				event.consume();
			}

		});
	}
	
	private void onCallBack() {
		int index = mEntryTableView.getSelectionModel().getSelectedIndex();
		if(index > -1) {
			mIntegerListener.callBack(mEntryTableView.getSelectionModel().getSelectedIndex());
		}
	}
	
	/**
	 * 창 닫기
	 */
	private void onClose() {
		mIntegerListener.callBack(-1);
	}
}
