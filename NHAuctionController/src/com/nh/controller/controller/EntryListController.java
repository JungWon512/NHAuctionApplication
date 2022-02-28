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
import com.nh.share.utils.SentryUtil;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	private Label mTitleLabel, mDownPriceLabel , mDownPriceLabel2,mDownPriceLabel3;
	
	@FXML //가격 낮추기 단위
	private Label mLowerCalfMoneyUnitLabel,mLowerFCattleMoneyUnitLabel,mLowerBCattleMoneyUnitLabel;

	@FXML // 대기중인 출품
	private TableColumn<SpEntryInfo, String> mEntryNumColumn, mExhibitorColumn, mGenderColumn, mMotherColumn, mMatimeColumn, mPasgQcnColumn, mWeightColumn, mLowPriceColumn, mSuccessPriceColumn, mSuccessfulBidderColumn, mResultColumn, mNoteColumn;

	@FXML // 최저가 낮춤
	private TextField mDownPriceTextField,mDownPriceTextField2,mDownPriceTextField3;

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
			
//			if(SettingApplication.getInstance().isSingleAuction()) {
				
				int cowLowerLimitPrice = SettingApplication.getInstance().getCowLowerLimitPrice(GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_1);
				int cowLowerLimitPrice2 = SettingApplication.getInstance().getCowLowerLimitPrice(GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_2);
				int cowLowerLimitPrice3 = SettingApplication.getInstance().getCowLowerLimitPrice(GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_3);
				
				mDownPriceTextField.setText(Integer.toString(cowLowerLimitPrice));
				mDownPriceTextField2.setText(Integer.toString(cowLowerLimitPrice2));
				mDownPriceTextField3.setText(Integer.toString(cowLowerLimitPrice3));
				
				mBtnDownPrice.setOnMouseClicked(event -> onDownPrice(event));
				
				mDownPriceLabel.setVisible(true);
				mDownPriceLabel2.setVisible(true);
				mDownPriceLabel3.setVisible(true);
				
				mDownPriceTextField.setVisible(true);
				mDownPriceTextField2.setVisible(true);
				mDownPriceTextField3.setVisible(true);
				mBtnDownPrice.setVisible(true);
				
				mLowerCalfMoneyUnitLabel.setVisible(true);
				mLowerFCattleMoneyUnitLabel.setVisible(true);
				mLowerBCattleMoneyUnitLabel.setVisible(true);
				
				CommonUtils.getInstance().setNumberTextField(mDownPriceTextField);
				CommonUtils.getInstance().setNumberTextField(mDownPriceTextField2);
				CommonUtils.getInstance().setNumberTextField(mDownPriceTextField3);
				
				
				if(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc() == GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_1) {
					
					mDownPriceTextField.setDisable(false);
					mDownPriceTextField2.setDisable(true);
					mDownPriceTextField3.setDisable(true);
					
					
				}else if(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc() == GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_2) {
					
					mDownPriceTextField.setDisable(true);
					mDownPriceTextField2.setDisable(false);
					mDownPriceTextField3.setDisable(true);
					
					
				}else if(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc() == GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_3) {
					
					mDownPriceTextField.setDisable(true);
					mDownPriceTextField2.setDisable(true);
					mDownPriceTextField3.setDisable(false);
					
				}else {

					mDownPriceTextField.setDisable(false);
					mDownPriceTextField2.setDisable(false);
					mDownPriceTextField3.setDisable(false);
				}
				
				//최적 가격 낮추기 단위
				if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice1() == 10000) {
					mLowerCalfMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				}else {
					mLowerCalfMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				}
				if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice2() == 10000) {
					mLowerFCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				}else {
					mLowerFCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				}
				
				if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice3() == 10000) {
					mLowerBCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.tenthousand.won"));
				}else {
					mLowerBCattleMoneyUnitLabel.setText(mResMsg.getString("str.money.unit.won"));
				}
				
				
//			} else {
//				
//				mDownPriceLabel.setVisible(false);
//				mDownPriceLabel2.setVisible(false);
//				mDownPriceLabel3.setVisible(false);
//				mDownPriceTextField.setVisible(false);
//				mDownPriceTextField2.setVisible(false);
//				mDownPriceTextField3.setVisible(false);
//				mBtnDownPrice.setVisible(false);
//				mLowerCalfMoneyUnitLabel.setVisible(false);
//				mLowerFCattleMoneyUnitLabel.setVisible(false);
//				mLowerBCattleMoneyUnitLabel.setVisible(false);
//			}
//		
			pageTitle = mResMsg.getString("str.page.title.auction_pending_list");
			mBtnSelect.setVisible(true);
			mBtnSelect.setOnMouseClicked(event -> onCallBack());
		
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

		initKeyConfiguration();

		initTableConfiguration();
		
		initTextfieldConfiguration();
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
		mLowPriceColumn.setCellValueFactory(cellData ->  cellData.getValue().getLowPrice());
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
		String rgSqno = "";
		
		if(!SettingApplication.getInstance().isSingleAuction()) {
			rgSqno = Integer.toString(GlobalDefine.AUCTION_INFO.auctionRoundData.getRgSqNo());
		}
		
		// 출장우 데이터 조회
		RequestCowInfoBody cowInfoBody = new RequestCowInfoBody(naBzplc, aucObjDsc, aucDate, selStsDsc, stnYn,rgSqno);
		
		ApiUtils.getInstance().requestSelectCowInfo(cowInfoBody, new ActionResultListener<ResponseCowInfo>() {
			@Override
			public void onResponseResult(final ResponseCowInfo result) {
				
				try {
					
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
					
				}catch (Exception e) {
					e.printStackTrace();
					SentryUtil.getInstance().sendExceptionLog(e);
				}
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
		
		try {
		
			if(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc() == GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_0) {
			
				if (!CommonUtils.getInstance().isValidString(mDownPriceTextField.getText())
						|| !CommonUtils.getInstance().isValidString(mDownPriceTextField2.getText())
						|| !CommonUtils.getInstance().isValidString(mDownPriceTextField3.getText())) {
					
					Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.need.low.price"),mResMsg.getString("popup.btn.ok")));
					
					return;
				}
	
				int cowLowerLimitPrice = Integer.parseInt(mDownPriceTextField.getText());
				int cowLowerLimitPrice2 = Integer.parseInt(mDownPriceTextField2.getText());
				int cowLowerLimitPrice3 = Integer.parseInt(mDownPriceTextField3.getText());
			
				if (cowLowerLimitPrice <= 0 || cowLowerLimitPrice2 <= 0 || cowLowerLimitPrice3 <= 0) {
					
					Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.need.low.price"),mResMsg.getString("popup.btn.ok")));
					
					return;
				}
				
				
			}else {
				
				if(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc() == GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_1) {
			
					if (!CommonUtils.getInstance().isValidString(mDownPriceTextField.getText())) {
						Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.need.low.price"),mResMsg.getString("popup.btn.ok")));
						return;
					}
	
					int cowLowerLimitPrice = Integer.parseInt(mDownPriceTextField.getText());
				
					if (cowLowerLimitPrice <= 0 ) {
						Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.need.low.price"),mResMsg.getString("popup.btn.ok")));
						return;
					}
					
					
				}else if(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc() == GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_2) {
					
					if (!CommonUtils.getInstance().isValidString(mDownPriceTextField2.getText())) {
						Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.need.low.price"),mResMsg.getString("popup.btn.ok")));
						return;
					}
	
					int cowLowerLimitPrice = Integer.parseInt(mDownPriceTextField2.getText());
				
					if (cowLowerLimitPrice <= 0 ) {
						Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.need.low.price"),mResMsg.getString("popup.btn.ok")));
						return;
					}
					
					
				}else if(GlobalDefine.AUCTION_INFO.auctionRoundData.getAucObjDsc() == GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_3) {
					
					
					if (!CommonUtils.getInstance().isValidString(mDownPriceTextField3.getText())) {
						Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.need.low.price"),mResMsg.getString("popup.btn.ok")));
						return;
					}
	
					int cowLowerLimitPrice = Integer.parseInt(mDownPriceTextField3.getText());
				
					if (cowLowerLimitPrice <= 0 ) {
						Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.need.low.price"),mResMsg.getString("popup.btn.ok")));
						return;
					}
					
				}
				
			}
			
			int cowLowerLimitPrice = Integer.parseInt(mDownPriceTextField.getText()); //송아지
			int cowLowerLimitPrice2 = Integer.parseInt(mDownPriceTextField2.getText()); //비육우
			int cowLowerLimitPrice3 = Integer.parseInt(mDownPriceTextField3.getText()); //번식우
			
	
			List<EntryInfo> entryInfoDataList = new ArrayList<EntryInfo>();
	
			if (!CommonUtils.getInstance().isListEmpty(mEntryDataList)) {
	
				for (SpEntryInfo spEntryInfo : mEntryDataList) {
					
					if(spEntryInfo.getLowPriceInt() <= 0) {
						continue;
					}
	
					int targetPrice = spEntryInfo.getLowPriceInt();
					
					
					if(spEntryInfo.getEntryType().getValue().equals(Integer.toString(GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_1))) {
					
						if (targetPrice < cowLowerLimitPrice) {
							mLogger.debug("경매번호 : " + spEntryInfo.getEntryNum().getValue() + " / 최저가 :  " + spEntryInfo.getLowPriceInt() + "/ 감가 : " + cowLowerLimitPrice);
							continue;
						}
						
						
					}else if(spEntryInfo.getEntryType().getValue().equals(Integer.toString(GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_2))) {
						if (targetPrice < cowLowerLimitPrice2) {
							mLogger.debug("경매번호 : " + spEntryInfo.getEntryNum().getValue() + " / 최저가 :  " + spEntryInfo.getLowPriceInt() + "/ 감가 : " + cowLowerLimitPrice);
							continue;
						}
						
					}else if(spEntryInfo.getEntryType().getValue().equals(Integer.toString(GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_3))) {
						if (targetPrice < cowLowerLimitPrice3) {
							mLogger.debug("경매번호 : " + spEntryInfo.getEntryNum().getValue() + " / 최저가 :  " + spEntryInfo.getLowPriceInt() + "/ 감가 : " + cowLowerLimitPrice);
							continue;
						}
					}
					
					
	
					String targetEntryNum = spEntryInfo.getEntryNum().getValue();
					String targetAuctionHouseCode = spEntryInfo.getAuctionHouseCode().getValue();
					String targetEntryType = spEntryInfo.getEntryType().getValue();
					String targetAucDt = spEntryInfo.getAucDt().getValue();
					String updatePrice = "";
					String oslpNo = spEntryInfo.getOslpNo().getValue();
					String ledSqNo = spEntryInfo.getLedSqno().getValue();
					
					
					if(spEntryInfo.getEntryType().getValue().equals(Integer.toString(GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_1))) {
	
						updatePrice = 	Integer.toString(targetPrice - cowLowerLimitPrice);
	
					}else if(spEntryInfo.getEntryType().getValue().equals(Integer.toString(GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_2))) {
				
						updatePrice = 	Integer.toString(targetPrice - cowLowerLimitPrice2);
						
					}else if(spEntryInfo.getEntryType().getValue().equals(Integer.toString(GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_3))) {
					
						updatePrice = 	Integer.toString(targetPrice - cowLowerLimitPrice3);
						
					}
					
					
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
	
			if (!CommonUtils.getInstance().isListEmpty(entryInfoDataList)) {
				
				Gson gson = new Gson();
				String jonDataList = gson.toJson(entryInfoDataList);
				
				mLogger.debug("[가격변경]=> " + jonDataList);
				
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
	
			}else {
				Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("dialog.change.low.price.fail.empty"),mResMsg.getString("popup.btn.close")));
			}
		
		}catch (Exception e) {
			e.printStackTrace();
			SentryUtil.getInstance().sendExceptionLog(e);
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

	/**
	 * 키 설정
	 */
	private void initKeyConfiguration() {

		mRoot.addEventHandler(KeyEvent.KEY_PRESSED, event -> {

			if (event.getCode() == KeyCode.ENTER) {
				switch (mCurPageType) {
				case ENTRY_LIST:
				case ENTRY_PENDING_LIST:
					mLogger.debug("F5");
					onCallBack();
					break;
				}
				event.consume();
			}

			if (event.getCode() == KeyCode.ESCAPE) {
				mLogger.debug("ESCAPE");
				onClose();
				event.consume();
			}

		});
	}
	
	/**
	 * 덱스트 필드 설정
	 */
	private void initTextfieldConfiguration() {
		
		//송아지
		mDownPriceTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {

				if (mDownPriceTextField.getText().length() > 5) {
					String str = mDownPriceTextField.getText().substring(0, 5);
					mDownPriceTextField.setText(str);
				}
			}
		});
		
		//비육우
		mDownPriceTextField2.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {

				if (mDownPriceTextField2.getText().length() > 5) {

					String str = mDownPriceTextField2.getText().substring(0, 5);
					mDownPriceTextField2.setText(str);
				}
			}
		});
		
		//번식우
		mDownPriceTextField3.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {

				if (mDownPriceTextField3.getText().length() > 5) {

					String str = mDownPriceTextField3.getText().substring(0, 5);
					mDownPriceTextField3.setText(str);
				}
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
	
//		if(mCurPageType.equals(EntryDialogType.ENTRY_PENDING_LIST)) {
//			onCallBack();
//		}else {
			mSelectEntryListener.callBack(mCurPageType, -1, null);
//		}
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
