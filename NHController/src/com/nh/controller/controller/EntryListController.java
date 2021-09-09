package com.nh.controller.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.http.ConnectionClosedException;

import com.nh.controller.interfaces.SelectEntryListener;
import com.nh.controller.model.AuctionRound;
import com.nh.controller.model.SpEntryInfo;
import com.nh.controller.service.EntryInfoMapperService;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.MoveStageUtil.EntryDialogType;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.controller.models.EntryInfo;

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
import javafx.stage.Stage;
import javafx.stage.Window;

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

	@FXML // 페이지 타이틀 , 최저가 낮춤
	private Label mTitleLabel, mDownPriceLabel;

	@FXML // 대기중인 출품
	private TableColumn<SpEntryInfo, String> mEntryNumColumn, mExhibitorColumn, mGenderColumn, mMotherColumn, mMatimeColumn, mPasgQcnColumn, mWeightColumn, mLowPriceColumn, mSuccessPriceColumn, mSuccessfulBidderColumn, mResultColumn, mNoteColumn;

	@FXML // 최저가 낮춤
	private TextField mDownPriceTextField;

	@FXML // 최저가 낮춤,선택,종료
	private Button mBtnDownPrice, mBtnSelect, mBtnClose;

	private ObservableList<SpEntryInfo> mEntryDataList = FXCollections.observableArrayList(); // 출품 목록
	private SelectEntryListener mSelectEntryListener = null; // listener
	private EntryDialogType mCurPageType = null; // pageType
	private AuctionRound auctionRound = null;
	private Stage mStage;

	/**
	 *
	 * @param dataList 출품 리스트
	 * @param type     페이지 타입 (전체/보류/낙찰결과)
	 * @param listener (전체/보류 row 선택시 콜백)
	 */
	public void setConfig(Stage stage,EntryDialogType type, AuctionRound auctionRound, SelectEntryListener listener) {
		this.mStage = stage;
		this.mEntryDataList.clear();
//		this.mEntryDataList.addAll(dataList);
		this.auctionRound = auctionRound;
		this.mSelectEntryListener = listener;
		setPageType(type);
	
	}

	public void setOnCloseRequest() {

		if(mStage != null) {
			Window window = MoveStageUtil.getInstance().getDialog().getDialogPane().getScene().getWindow();
			window.setOnCloseRequest(e -> {
				onClose();
				MoveStageUtil.getInstance().setBackStageDisableFalse(mStage);
			});
		}
	}
	/**
	 * 페이지 타입
	 * 
	 * @param EntryDialogType
	 */
	public void setPageType(EntryDialogType type) {

		String pageTitle = "";

		String searchAuctionResult = "";

		switch (type) {
		case ENTRY_LIST:
			initClickCallback();
			mBtnSelect.setVisible(true);
			mBtnSelect.setOnMouseClicked(event -> onCallBack());
			pageTitle = mResMsg.getString("str.page.title.auction_all_list");
			break;
		case ENTRY_PENDING_LIST:

			initClickCallback();
			mDownPriceLabel.setVisible(true);
			mDownPriceTextField.setVisible(true);
			int cowLowerLimitPrice = SettingApplication.getInstance().getCowLowerLimitPrice(auctionRound.getAucObjDsc());
			mDownPriceTextField.setText(Integer.toString(cowLowerLimitPrice));
			mBtnDownPrice.setVisible(true);
			mBtnDownPrice.setOnMouseClicked(event -> onDownPrice(event));
			pageTitle = mResMsg.getString("str.page.title.auction_pending_list");
			mBtnSelect.setVisible(true);
			mBtnSelect.setOnMouseClicked(event -> onCallBack());
			CommonUtils.getInstance().setNumberTextField(mDownPriceTextField);
			searchAuctionResult = GlobalDefineCode.AUCTION_RESULT_CODE_PENDING;
			break;
		case ENTRY_FINISH_LIST:
			pageTitle = mResMsg.getString("str.page.title.auction_finish_list");
			searchAuctionResult = GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS;
			break;
		}

		mTitleLabel.setText(pageTitle);
		mBtnClose.setOnMouseClicked(event -> onClose());

		this.mCurPageType = type;

		// 경매 전체or보류or낙찰 데이터 조회
		searchEntryDataList(searchAuctionResult);
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

		setAlignCenterCol(mEntryNumColumn);
		setAlignCenterCol(mExhibitorColumn);
		setAlignCenterCol(mGenderColumn);
		setAlignCenterCol(mMotherColumn);
		setAlignCenterCol(mMatimeColumn);
		setAlignCenterCol(mPasgQcnColumn);
		setAlignCenterCol(mWeightColumn);
		setAlignCenterCol(mLowPriceColumn);
		setAlignCenterCol(mSuccessPriceColumn);
		setAlignCenterCol(mSuccessfulBidderColumn);
		setAlignCenterCol(mResultColumn);
		setAlignLeftCol(mNoteColumn);

		// 테이블 컬럼 - 대기
		mEntryNumColumn.setCellValueFactory(cellData -> cellData.getValue().getEntryNum());
		mExhibitorColumn.setCellValueFactory(cellData -> cellData.getValue().getExhibitor());
		mGenderColumn.setCellValueFactory(cellData -> cellData.getValue().getGenderName());
		mMotherColumn.setCellValueFactory(cellData -> cellData.getValue().getMotherCowName());
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
	 * 경매 데이터 조회
	 * 
	 * @param aucResultCode
	 */
	private void searchEntryDataList(String aucResultCode) {

		AuctionRound aucRoundParam = new AuctionRound();
		aucRoundParam = auctionRound.clone();
		aucRoundParam.setAuctionResult(aucResultCode);

		List<EntryInfo> selectPendingList = EntryInfoMapperService.getInstance().getAllEntryData(aucRoundParam);

		if (!CommonUtils.getInstance().isListEmpty(selectPendingList)) {
			ObservableList<SpEntryInfo> resultDataList = selectPendingList.stream().map(item -> new SpEntryInfo(item)).collect(Collectors.toCollection(FXCollections::observableArrayList));
			mEntryDataList.clear();
			mEntryDataList.addAll(resultDataList);

			if (mEntryTableView.getItems().size() <= 0) {
				mEntryTableView.setItems(mEntryDataList);
			} else {
				mEntryTableView.refresh();
			}
		}
	}

	/**
	 * 예정가 낮추기
	 *
	 * @param event
	 */
	public void onDownPrice(MouseEvent event) {

		if (!CommonUtils.getInstance().isValidString(mDownPriceTextField.getText())) {
			return;
		}

		int cowLowerLimitPrice = Integer.parseInt(mDownPriceTextField.getText());

		if (cowLowerLimitPrice <= 0) {
			return;
		}

		int lowPrice = cowLowerLimitPrice * -1;

		List<EntryInfo> entryInfoDataList = new ArrayList<EntryInfo>();

		if (!CommonUtils.getInstance().isListEmpty(mEntryDataList)) {

			for (SpEntryInfo spEntryInfo : mEntryDataList) {

				if (spEntryInfo.getLowPriceInt() <= cowLowerLimitPrice) {
					System.out.println("entryinfo : " + spEntryInfo.getEntryNum().getValue() + " / " + spEntryInfo.getLowPriceInt() + "/ " + cowLowerLimitPrice);
					continue;
				}

				String targetEntryNum = spEntryInfo.getEntryNum().getValue();
				String targetAuctionHouseCode = spEntryInfo.getAuctionHouseCode().getValue();
				String targetEntryType = spEntryInfo.getEntryType().getValue();
				String targetAucDt = spEntryInfo.getAucDt().getValue();
				String updatePrice = Integer.toString(spEntryInfo.getLowPriceInt() + lowPrice);
				String oslpNo = spEntryInfo.getOslpNo().getValue();
				String ledSqNo = spEntryInfo.getLedSqno().getValue();

				int lowPriceCnt = Integer.parseInt(spEntryInfo.getLwprChgNt().getValue());

				lowPriceCnt += 1;

				EntryInfo entryInfo = new EntryInfo();
				entryInfo.setEntryNum(targetEntryNum);
				entryInfo.setAuctionHouseCode(targetAuctionHouseCode);
				entryInfo.setEntryType(targetEntryType);
				entryInfo.setAucDt(targetAucDt);
				entryInfo.setLowPrice(updatePrice);
				entryInfo.setOslpNo(oslpNo);
				entryInfo.setLedSqno(ledSqNo);
				entryInfo.setLsCmeNo(GlobalDefine.ADMIN_INFO.adminData.getUserId());
				entryInfo.setLwprChgNt(Integer.toString(lowPriceCnt));
				entryInfoDataList.add(entryInfo);
			}
		}

		if (!CommonUtils.getInstance().isListEmpty(mEntryDataList)) {

			final int resultValue = EntryInfoMapperService.getInstance().updateEntryPriceList(entryInfoDataList);

			if (resultValue > 0) {
				CommonUtils.getInstance().showAlertPopupOneButton(MoveStageUtil.getInstance().getDialog(), mResMsg.getString("dialog.change.low.price.complete"), mResMsg.getString("popup.btn.ok"));
				searchEntryDataList(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING);
			}
		}
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
				case ENTRY_PENDING_LIST:
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

	/**
	 * 경매 메인 이동
	 */
	private void onCallBack() {
		int index = mEntryTableView.getSelectionModel().getSelectedIndex();
		
		if (index < 0) {
			index = 0;
		}
		
		mSelectEntryListener.callBack(mCurPageType, index, mEntryDataList);
	}

	/**
	 * ESC 닫기 -> 경매 메인 이동
	 */
	private void onClose() {
	
		if(mCurPageType.equals(EntryDialogType.ENTRY_PENDING_LIST)) {
			onCallBack();
		}else {
			mSelectEntryListener.callBack(mCurPageType, -1, null);
		}
	}

	private void setAlignCenterCol(TableColumn<SpEntryInfo, String> col) {
		col.getStyleClass().add("center-column");
	}

	private void setAlignLeftCol(TableColumn<SpEntryInfo, String> col) {
		col.getStyleClass().add("left-column");
	}

}
