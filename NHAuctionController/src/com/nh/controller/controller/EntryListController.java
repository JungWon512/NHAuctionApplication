package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.nh.controller.interfaces.SelectEntryListener;
import com.nh.controller.model.AuctionRound;
import com.nh.controller.model.SpEntryInfo;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.ApiUtils;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.MoveStageUtil.EntryDialogType;
import com.nh.share.api.ActionResultListener;
import com.nh.share.api.models.CowInfoData;
import com.nh.share.api.request.body.RequestCowInfoBody;
import com.nh.share.api.request.body.RequestUpdateLowsBidAmtBody;
import com.nh.share.api.response.ResponseCowInfo;
import com.nh.share.api.response.ResponseNumber;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.controller.models.EntryInfo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
	
	private Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
		
		showLoadingDialog();
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
	
	public void showLoadingDialog() {
		Platform.runLater(() -> CommonUtils.getInstance().showLoadingDialog(MoveStageUtil.getInstance().getDialog(), mResMsg.getString("dialog.searching.entry.list")));
	}
	
	public void dismissLoadingDialog() {
		Platform.runLater(() -> CommonUtils.getInstance().dismissLoadingDialog());
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
			
			if(SettingApplication.getInstance().isSingleAuction()) {
				
				int cowLowerLimitPrice = SettingApplication.getInstance().getCowLowerLimitPrice(auctionRound.getAucObjDsc());
				mDownPriceTextField.setText(Integer.toString(cowLowerLimitPrice));
				mBtnDownPrice.setOnMouseClicked(event -> onDownPrice(event));
				
				mDownPriceLabel.setVisible(true);
				mDownPriceTextField.setVisible(true);
				mBtnDownPrice.setVisible(true);
			}else {
				
				mDownPriceLabel.setVisible(false);
				mDownPriceTextField.setVisible(false);
				mBtnDownPrice.setVisible(false);
			}
		
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
		mTitleLabel.setVisible(true);
		mBtnClose.setOnMouseClicked(event -> onClose());
		mBtnClose.setVisible(true);

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
		mLowPriceColumn.setCellValueFactory(cellData ->  cellData.getValue().getOriLowPrice());
		mSuccessPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getSraSbidUpPrice());
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

		String naBzplc = GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc();
		String aucObjDsc = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc());
		String aucDate = GlobalDefine.AUCTION_INFO.auctionRoundData.getAucDt();
		String selStsDsc = aucResultCode;
		String stnYn = SettingApplication.getInstance().getSettingAuctionTypeYn();
		
		// 출장우 데이터 조회
		RequestCowInfoBody cowInfoBody = new RequestCowInfoBody(naBzplc, aucObjDsc, aucDate, selStsDsc, stnYn);
		
		ApiUtils.getInstance().requestSelectCowInfo(cowInfoBody, new ActionResultListener<ResponseCowInfo>() {
			@Override
			public void onResponseResult(final ResponseCowInfo result) {
				
				if (result != null && result.getSuccess() && !CommonUtils.getInstance().isListEmpty(result.getData())) {

					mLogger.debug("[출장우 정보 조회 데이터 수] " + result.getData().size());

					List<EntryInfo> entryInfoDataList = new ArrayList<EntryInfo>();

					for (CowInfoData cowInfo : result.getData()) {
						entryInfoDataList.add(new EntryInfo(cowInfo));
					}
					
					if (!CommonUtils.getInstance().isListEmpty(entryInfoDataList)) {
						ObservableList<SpEntryInfo> resultDataList = entryInfoDataList.stream().map(item -> new SpEntryInfo(item)).collect(Collectors.toCollection(FXCollections::observableArrayList));
						mEntryDataList.clear();
						mEntryDataList.addAll(resultDataList);

						if (mEntryTableView.getItems().size() <= 0) {
							mEntryTableView.setItems(mEntryDataList);
						} else {
							mEntryTableView.refresh();
						}
					}
				}else {
					mLogger.debug("[조회된 출장우 정보 없음]");
				}
				dismissLoadingDialog();
			}

			@Override
			public void onResponseError(String message) {
				mLogger.debug("[onResponseError] 출장우 정보 " + message);
				dismissLoadingDialog();
			}
		});
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
				
				if(spEntryInfo.getLowPriceInt() <= 0) {
					continue;
				}

				int targetPrice = Integer.parseInt(spEntryInfo.getOriLowPrice().getValue());
				
				if (targetPrice <= cowLowerLimitPrice) {
					System.out.println("entryinfo : " + spEntryInfo.getEntryNum().getValue() + " / " + spEntryInfo.getLowPriceInt() + "/ " + cowLowerLimitPrice);
					continue;
				}

				String targetEntryNum = spEntryInfo.getEntryNum().getValue();
				String targetAuctionHouseCode = spEntryInfo.getAuctionHouseCode().getValue();
				String targetEntryType = spEntryInfo.getEntryType().getValue();
				String targetAucDt = spEntryInfo.getAucDt().getValue();
				String updatePrice = Integer.toString(targetPrice + lowPrice);
				String oslpNo = spEntryInfo.getOslpNo().getValue();
				String ledSqNo = spEntryInfo.getLedSqno().getValue();
				
				//한번더체크
				if(Integer.parseInt(updatePrice) <= 0) {
					continue;
				}

				int lowPriceCnt = Integer.parseInt(spEntryInfo.getLwprChgNt().getValue());

				lowPriceCnt += 1;

				EntryInfo entryInfo = new EntryInfo();
				entryInfo.setEntryNum(targetEntryNum);
				entryInfo.setAuctionHouseCode(targetAuctionHouseCode);
				entryInfo.setEntryType(targetEntryType);
				entryInfo.setAucDt(targetAucDt);
				entryInfo.setLowPrice(Integer.parseInt(updatePrice));
				entryInfo.setOslpNo(oslpNo);
				entryInfo.setLedSqno(ledSqNo);
				entryInfo.setLsCmeNo(GlobalDefine.ADMIN_INFO.adminData.getUserId());
				entryInfo.setLwprChgNt(lowPriceCnt);
				entryInfoDataList.add(entryInfo);
			}
		}

		if (!CommonUtils.getInstance().isListEmpty(mEntryDataList)) {
			
			Gson gson = new Gson();
			String jonDataList = gson.toJson(entryInfoDataList);
			
			RequestUpdateLowsBidAmtBody body = new RequestUpdateLowsBidAmtBody(jonDataList);
			
			ApiUtils.getInstance().requestUpdateLowsBidAmt(body, new ActionResultListener<ResponseNumber>() {

				@Override
				public void onResponseResult(ResponseNumber result) {

					if (result != null && result.getSuccess()) {

						mLogger.debug("[최저가 수정 Success]");
						
//						CommonUtils.getInstance().showAlertPopupOneButton(MoveStageUtil.getInstance().getDialog(), mResMsg.getString("dialog.change.low.price.complete"), mResMsg.getString("popup.btn.ok"));
						Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.change.low.price.complete"),mResMsg.getString("popup.btn.ok")));
						
						
						searchEntryDataList(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING);

					} else {
						mLogger.debug("[최저가 수정 Fail]");
						Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.change.low.price.fail"),mResMsg.getString("popup.btn.close")));
					}
				}

				@Override
				public void onResponseError(String message) {
					mLogger.debug("[최저가 수정 Fail]");
					Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.change.low.price.fail"),mResMsg.getString("popup.btn.close")));
				}
			});

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
	
	/**
	 * 원버튼 팝업
	 *
	 * @param message
	 * @return
	 */
	private Optional<ButtonType> showAlertPopupOneButton(String message,String strBtn) {
		return CommonUtils.getInstance().showAlertPopupOneButton(MoveStageUtil.getInstance().getDialog(), message, strBtn);
	}

}
