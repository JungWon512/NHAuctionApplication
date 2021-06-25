package com.nh.controller.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.controller.interfaces.BooleanListener;
import com.nh.controller.interfaces.StringListener;
import com.nh.controller.netty.AuctionDelegate;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine.AUCTION_INFO;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.TestUtil;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.server.models.AuctionCountDown;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
	private TextField tfIp, // ip
			tfPort, // port
			tfId; // id

	@FXML
	private Button btnSendEntryInfo, // 전송
			btnStart, // 시작
			btnStop, // 종료
			btnPass, // 강제유찰
			btnConnection, // 접속
			btnDisConnect, // 접속 끊기
			btnClearLog,	//로그 삭제
			btnSendMessage; //메세지 전송

	@FXML
	private Label cnt_5, cnt_4, cnt_3, cnt_2, cnt_1; // 남은 시간 Bar

	@FXML
	private Label sendMsgComplete;


	private List<Label> cntList = new ArrayList<Label>(); // 남은 시간 Bar list

	private boolean isShowToast = false; // 현재 toast msg 노출 여부

	public final int REMAINING_TIME_COUNT = 5; // 카운트다운 기준 시간
	
	private int mRemainingTimeCount = REMAINING_TIME_COUNT; // 카운트다운

	/**
	 * setStage
	 * 
	 * @param stage
	 */
	public void setStage(Stage stage) {
		mStage = stage;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		System.out.println("SampleController initialize");

		// get ResMsg
		if (resources != null) {
			mResMsg = resources;
		}

		btnSendEntryInfo.setOnMouseClicked(event -> onSendEntryData(event)); // 출품 데이터 전송
		btnStart.setOnMouseClicked(event -> onStartAuction(event));
		btnStop.setOnMouseClicked(event -> onStopAuction(event));
		btnPass.setOnMouseClicked(event -> onPassAuction(event));
		btnSendMessage.setOnMouseClicked(event -> openSendMessage(event));
		btnConnection.setOnMouseClicked(event -> onConnectServer(event));
		btnDisConnect.setOnMouseClicked(event -> onDisconnectServer(event));
		btnClearLog.setOnMouseClicked(event -> onClearLog(event));

		cntList.add(cnt_1);
		cntList.add(cnt_2);
		cntList.add(cnt_3);
		cntList.add(cnt_4);
		cntList.add(cnt_5);

		addLogItem(mResMsg.getString("log.auction"));
		addLogItem(mResMsg.getString("msg.connection.check"));

	}

	/**
	 * 서버 접속
	 * 
	 * @param event
	 */
	public void onConnectServer(MouseEvent event) {

		// test set
		tfIp.setText(AUCTION_INFO.AUCTION_HOST);
		tfPort.setText(Integer.toString(AUCTION_INFO.AUCTION_PORT));
		tfId.setText(AUCTION_INFO.AUCTION_MEMBER);

		// log
		addLogItem(tfIp.getText().trim() + " / " + tfPort.getText().trim() + " " + mResMsg.getString("msg.connection"));

		// loading dialog
		CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("msg.connection"));

		// connection server
		Thread thread = new Thread("server") {
			@Override
			public void run() {

				createClient(tfIp.getText().trim(), Integer.parseInt(tfPort.getText().trim()), tfId.getText().trim(),
						"N");
			}
		};
		thread.setDaemon(true);
		thread.start();

	}

	/**
	 * 접속 해제
	 * 
	 * @param event
	 */
	public void onDisconnectServer(MouseEvent event) {

		CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.msg.try.disconnect"));

		AuctionDelegate.getInstance().onDisconnect(new NettyClientShutDownListener() {
			@Override
			public void onShutDown(int port) {
				Platform.runLater(() -> {
					// 네티 변수 초기화
					AuctionDelegate.getInstance().setClearVariable();
					// 버튼 상태
					setButtonState(AuctionDelegate.getInstance().isActive());
					// hide dialog
					CommonUtils.getInstance().dismissLoadingDialog();
					// log
					addLogItem(mResMsg.getString("msg.disconnection"));
				});
			}
		});

	}

	/**
	 * 출품 데이터 전송
	 * 
	 * @param event
	 */
	public void onSendEntryData(MouseEvent event) {

		/**
		 * 네티 접속 상태 출품 데이터 전송 전 상태
		 */
		if (AuctionDelegate.getInstance().isActive()
				&& mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {

			if (btnSendEntryInfo.isDisable()) {
				return;
			}

			CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("dialog.msg.send.data"));

			Thread thread = new Thread("ss") {
				@Override
				public void run() {

					int recordCount = 0;

					mEntryRepository.addAll(TestUtil.getInstance().loadEntryData());

					for (int i = 0; i < mEntryRepository.size(); i++) {
						addLogItem(mResMsg.getString("msg.auction.send.entry.data")
								+ AuctionDelegate.getInstance().onSendEntryData(mEntryRepository.get(i)));
						recordCount++;
					}

					addLogItem(String.format(mResMsg.getString("msg.send.entry.data.result"), recordCount));

					btnSendEntryInfo.setDisable(true);

					Platform.runLater(() -> {
						CommonUtils.getInstance().dismissLoadingDialog();
					});
				}
			};

			thread.setDaemon(true);
			thread.start();

		} else {
			addLogItem(mResMsg.getString("msg.need.connection"));
			CommonUtils.getInstance().dismissLoadingDialog();
		}

	}

	/**
	 * 1.준비 2.시작
	 * 
	 * @param event
	 */
	public void onStartAuction(MouseEvent event) {

		if (mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
			addLogItem(mResMsg.getString("msg.auction.send.need.entry.data"));
			return;
		}

		if (!mAuctionStatus.getState().equals(GlobalDefineCode.AUCTION_STATUS_READY)) {
			addLogItem(mResMsg.getString("msg.auction.not.ready"));
			return;
		}

		String msgReady = String.format(mResMsg.getString("msg.auction.send.ready"), mCurrentEntryInfo.getEntryNum());
		String msgStart = String.format(mResMsg.getString("msg.auction.send.start"), mCurrentEntryInfo.getEntryNum());

		// 준비
		addLogItem(msgReady + AuctionDelegate.getInstance().onNextEntryReady(mCurrentEntryInfo.getEntryNum()));
		// 시작
		addLogItem(msgStart + AuctionDelegate.getInstance().onStartAuction(mCurrentEntryInfo.getEntryNum()));
	}

	/**
	 * 종료
	 * 
	 * @param event
	 */
	public void onStopAuction(MouseEvent event) {
		addLogItem(mResMsg.getString("msg.auction.send.complete") + AuctionDelegate.getInstance().onPauseAuction(mCurrentEntryInfo.getEntryNum()));
	}

	/**
	 * 강제유찰
	 * 
	 * @param event
	 */
	public void onPassAuction(MouseEvent event) {
		addLogItem(mResMsg.getString("msg.auction.send.pass") + AuctionDelegate.getInstance().onPassAuction(mCurrentEntryInfo.getEntryNum()));
	}

	/**
	 * 메세지 전송
	 * 
	 * @param event
	 */
	public void openSendMessage(MouseEvent event) {
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();

		MoveStageUtil.getInstance().loadMessageFXMLLoader(stage, new StringListener() {

			@Override
			public void callBack(String str) {
				AuctionDelegate.getInstance().onToastMessageRequest(str);
				showToastMessage(String.format(mResMsg.getString("msg.auction.send.message"), str));
			}
		});
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
				CommonUtils.getInstance().showToastMessage(sendMsgComplete, new BooleanListener() {
					@Override
					public void callBack(Boolean tf) {
						isShowToast = false;
					}
				});
			}

			sendMsgComplete.setText(message);
		});
	}

	// 2000 : 인증 성공
	// 2001 : 인증 실패
	// 2002 : 중복 접속
	// 2003 : 기타 장애
	@Override
	public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo) {
		super.onResponseConnectionInfo(responseConnectionInfo);

		Platform.runLater(() -> {
			switch (responseConnectionInfo.getResult()) {
			case GlobalDefineCode.CONNECT_SUCCESS:
				addLogItem(mResMsg.getString("msg.connection.success") + responseConnectionInfo.getEncodedMessage());
				setButtonState(AuctionDelegate.getInstance().isActive());
				break;
			case GlobalDefineCode.CONNECT_FAIL:
				addLogItem(mResMsg.getString("msg.connection.fail"));
				break;
			case GlobalDefineCode.CONNECT_DUPLICATE:
			case GlobalDefineCode.CONNECT_DUPLICATE_FAIL:
				addLogItem(mResMsg.getString("msg.connection.duplicate"));
				break;
			}

			CommonUtils.getInstance().dismissLoadingDialog();
		});

	}

	@Override
	public void onAuctionStatus(AuctionStatus auctionStatus) {
		super.onAuctionStatus(auctionStatus);
		initAuctionVariable(auctionStatus.getState());
	}

	@Override
	public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
		super.onAuctionCountDown(auctionCountDown);
		if (auctionCountDown.getStatus().equals(GlobalDefineCode.AUCTION_COUNT_DOWN)) {
			if (Integer.parseInt(auctionCountDown.getCountDownTime()) <= 4) {
				mRemainingTimeCount--;
				cntList.get(mRemainingTimeCount).setDisable(true);
			}
		}
	}

	@Override
	public void onChannelInactive(int port) {
		super.onChannelInactive(port);
		setButtonState(AuctionDelegate.getInstance().isActive());
	}

	/**
	 * 서버 접속 상태에 따른 버튼 및 텍스트 필드 상태
	 * 
	 * @param isConnection
	 */
	private void setButtonState(boolean isConnection) {
		if (isConnection) {
			tfIp.setDisable(true);				// 아이피 필드
			tfPort.setDisable(true);			// 포트 필드
			tfId.setDisable(true);				// 아이디 필드
			btnSendMessage.setDisable(false);	// 메세지 전송
			btnSendEntryInfo.setDisable(false);	// 출품자료 전송
			btnConnection.setVisible(false);	// 접속 버튼
			btnDisConnect.setVisible(true);		// 접속 해제 버튼
		} else {
			tfIp.setDisable(false);
			tfPort.setDisable(false);
			tfId.setDisable(false);
			btnSendEntryInfo.setDisable(true);
			btnSendMessage.setDisable(true);
			btnStart.setDisable(true);
			btnStop.setDisable(true);
			btnPass.setDisable(true);

			btnConnection.setVisible(true);
			btnDisConnect.setVisible(false);
			
		}
	}

	/**
	 * 경매 준비 뷰 초기화
	 */
	private void initAuctionVariable(String code) {

		switch (code) {

		case GlobalDefineCode.AUCTION_STATUS_READY:

			for(int i = 0; cntList.size() > i ; i++) {
				cntList.get(i).setDisable(false);
			}
			mRemainingTimeCount = REMAINING_TIME_COUNT;

			mCurrentBidderMap.clear();
			mBeForeBidderDataList.clear();

			btnStart.setDisable(false);
			btnStop.setDisable(true);
			btnPass.setDisable(true);
			btnSendEntryInfo.setDisable(false);

			break;
		case GlobalDefineCode.AUCTION_STATUS_START:
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
			btnStart.setDisable(true);
			btnStop.setDisable(false);
			btnPass.setDisable(false);
			break;
		case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
			break;
		case GlobalDefineCode.AUCTION_STATUS_FINISH:
			break;
		}
	}

}
