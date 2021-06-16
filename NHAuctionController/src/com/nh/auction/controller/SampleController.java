package com.nh.auction.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.nh.auction.interfaces.BooleanListener;
import com.nh.auction.interfaces.StringListener;
import com.nh.auction.models.AuctionCountDown;
import com.nh.auction.models.EntryInfoToServer;
import com.nh.auction.models.TestDataInfo;
import com.nh.auction.utils.CommonUtils;
import com.nh.auction.utils.MoveStageUtil;
import com.nh.auction.utils.TestUtil;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Test Controller
 * 
 * @author jhlee
 *
 */
public class SampleController extends BaseAuctionController implements Initializable {

	@FXML
	private Button btnSendEntryInfo, // 전송
			btnStart, // 시작
			btnStop, // 종료
			btnPass; // 강제유찰
	
	@FXML
	private Label cnt_5, cnt_4, cnt_3, cnt_2, cnt_1; // 남은 시간 Bar
	
	@FXML
	private Label sendMsgComplete;

	@FXML
	private Button btnSendMessage;

	@FXML
	private ListView<String> logListView; // Log List

	private List<Label> cntList = new ArrayList<Label>(); // 남은 시간 Bar list

	private TestDataInfo mTestDataInfo = null; // 현재 경매 진행 상품

	private int mCurrentIndex = 0; // 현재 진행 번호

	private int repeatCnt = 0;

	private int sleepSec = 1;

	private int curCnt = 1;
	
	private boolean isShowToast = false;

	final ScheduledThreadPoolExecutor mCountDownScheduler = new ScheduledThreadPoolExecutor(1);
	final ScheduledThreadPoolExecutor mAuctionPlayScheduler = new ScheduledThreadPoolExecutor(1);


	/**
	 * setStage
	 * @param stage
	 */
	public void setStage(Stage stage) {
		mStage = stage;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		btnSendEntryInfo.setOnMouseClicked(event -> sendEntryInfo(event));
		btnStart.setOnMouseClicked(event -> runOneEntry(event));
		btnStop.setOnMouseClicked(event -> stopOneEntry(event));
		btnPass.setOnMouseClicked(event -> passOneEntry(event));
		btnSendMessage.setOnMouseClicked(event -> openSendMessage(event));
		
		cntList.add(cnt_5);
		cntList.add(cnt_4);
		cntList.add(cnt_3);
		cntList.add(cnt_2);
		cntList.add(cnt_1);

		addLogItem("[AUCTION LOG]");
	}

	/**
	 * 출품 데이터 전송 scheduleAtFixedRate
	 * 
	 * @param event
	 */
	public void sendEntryInfo(MouseEvent event) {

		addLogItem("출품 데이터 전송");

		CommonUtils.getInstance().showLoadingDialog(mStage, "전송중 입니다.\r\n 잠시만 기다려주세요.");
		
		TestUtil.getInstance().sendData(new StringListener() {
			@Override
			public void callBack(String str) {
				
				addLogItem(str);
				// testData
				readyProduct();
				//dismiss loading
				CommonUtils.getInstance().dismissLoadingDialog();
			}
		});

	}

	/**
	 * 시작
	 * 
	 * @param event
	 */
	public void runOneEntry(MouseEvent event) {
		onAuctionCountDown(TestUtil.getInstance().obtainCountDownData());
	}

	/**
	 * 종료
	 * 
	 * @param event
	 */
	public void stopOneEntry(MouseEvent event) {
		finishProduct();
	}

	/**
	 * 강제유찰
	 * 
	 * @param event
	 */
	public void passOneEntry(MouseEvent event) {
		if (mTestDataInfo != null) {
			String msg = String.format("강제 유찰 : %s", mTestDataInfo.getName());
			addLogItem(msg);
		}
	}

	/**
	 * 강제유찰
	 * 
	 * @param event
	 */
	public void openSendMessage(MouseEvent event) {
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		MoveStageUtil.getInstance().loadMessageFXMLLoader(stage,new StringListener() {
			
			@Override
			public void callBack(String str) {
				System.out.println("callBack " + str);
				showToastMessage(str);
			}
		});
	}

	/**
	 * ADD 로그
	 * 
	 * @param str
	 */
	private void addLogItem(String str) {
		if (!str.isEmpty()) {
			logListView.getItems().add(str);
			logListView.scrollTo(logListView.getItems().size() - 1);
		}
	}

	@Override
	public void onAuctionCountDown(final AuctionCountDown auctionCountDown) {

		repeatCnt = Integer.parseInt(auctionCountDown.getCountDownTime());

		mCountDownScheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				Platform.runLater(() -> {
					try {

						if (repeatCnt <= 0) {
							playAuction();
							mCountDownScheduler.shutdown();
						} else {
							String msg = String.format("경매 시작 : %d 초 전", repeatCnt);
							addLogItem(msg);
						}

						repeatCnt--;

					} catch (Exception e) {
						e.printStackTrace();
						mCountDownScheduler.shutdown();
					}
				});
			}
		}, 0, sleepSec, TimeUnit.SECONDS);

	}

	/**
	 * 경매 시작 - 추후수정
	 */
	private void playAuction() {

		mAuctionPlayScheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				Platform.runLater(() -> {

					cntList.get(curCnt - 1).setDisable(true);
					curCnt++;

					if (curCnt > 5) {
						finishProduct();
						mAuctionPlayScheduler.shutdown();

					}

				});
			}
		}, 0, sleepSec, TimeUnit.SECONDS);

	}

	/**
	 * 경매 준비 - 추후수정
	 */
	private void readyProduct() {

		mTestDataInfo = TestUtil.getInstance().obtainProduct(mCurrentIndex);

		if (mTestDataInfo != null) {
			String str = String.format("경매 준비 출품 번호 : %s 출품명 : %s", mTestDataInfo.getProductCode(),
					mTestDataInfo.getName());
			addLogItem(str);
		}

	}

	/**
	 * 경매 종료 - 추후수정
	 */
	private void finishProduct() {
		if (mTestDataInfo != null) {
			String msg = String.format("경매 종료 : %s", mTestDataInfo.getName());
			addLogItem(msg);
		}
	}

	
	/**
	 * 
	 * @MethodName showToastMessage
	 * @Description 하단 Toast 표시
	 *
	 * @param message
	 */
	private void showToastMessage(String message) {

		if (isShowToast) {
			return;
		}

		Platform.runLater(() -> {
			if (!sendMsgComplete.isVisible()) {
				isShowToast = true;
				sendMsgComplete.setText(message);
				sendMsgComplete.setVisible(true);
				CommonUtils.getInstance().showToastMessage(sendMsgComplete , new BooleanListener() {
					@Override
					public void callBack(Boolean tf) {
						isShowToast = false;
					}
				} );
			}

			sendMsgComplete.setText(message);
		});
	}
}
