package com.nh.auction.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionReponseSession;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.ToastMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TestController extends CommonController implements Initializable {

	private Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final String CONNECT_IP = "192.168.0.23";
	private final int CONNECT_PORT = 4001;
	private final String DEFAULT_USER_MEM_NUM = "MEM000011005";

	private Channel mConnectChannel;

	private List<EntryInfo> mEntryRepository = new ArrayList<EntryInfo>();

	private CurrentEntryInfo mCurrentEntryInfo = null;

	private AuctionStatus mAuctionStatus;

	@FXML
	private TextField mIpTextField;
	@FXML
	private TextField mPortTextField;
	@FXML
	private TextArea mLogTextArea;
	@FXML
	private Button mConnectButton;
	@FXML
	private TextField mUserMemNumTextField;

	/**
	 * @MethodName setStage
	 * @Description 윈도우 닫기버튼 리스너
	 *
	 * @param stage_
	 */
	public void setStage(Stage stage_) {

		this.mStage = stage_;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		mLogger.debug("" + getClass().getName());

		mIpTextField.setText(CONNECT_IP);
		mPortTextField.setText(String.valueOf(CONNECT_PORT));
		mUserMemNumTextField.setText(DEFAULT_USER_MEM_NUM);
		putText("접속 정보를 확인하세요.");
	}

	@FXML
	private void onConnectServer(ActionEvent ev) {
		putText(mIpTextField.getText().trim() + " / " + mPortTextField.getText().trim() + "로 접속을 시도합니다.");
		BaseAuction.getAuctionInstance().onAuctionConnect(mIpTextField.getText().trim(),
				Integer.valueOf(mPortTextField.getText().trim()));
	}

	@FXML
	private void onDisConnectServer(ActionEvent ev) {
		putText(mIpTextField.getText().trim() + " / " + mPortTextField.getText().trim() + "에서 접속 해제를 시도합니다.");
		BaseAuction.getAuctionInstance().onAuctionClose(new NettyClientShutDownListener() {

			@Override
			public void onShutDown(int port) {
				putText(mIpTextField.getText().trim() + " / " + mPortTextField.getText().trim() + "에서 접속 해제되었습니다.");
			}
		});
	}

	@FXML
	private void onSendEntryData(ActionEvent ev) {
		int recordCount = 0;
		
		loadEntryData();
		
		if (mConnectChannel != null) {
			for(int i = 0; i < mEntryRepository.size(); i++) {
				putText(BaseAuction.getAuctionInstance().onSendEntryData(mConnectChannel, mEntryRepository.get(i)));
				recordCount++;
			}
			
			putText("출품 자료 총 " + recordCount + "건이 전송 되었습니다.");
		}
	}

	@FXML
	private void onBidding(ActionEvent ev) {
		if (mConnectChannel != null && mAuctionStatus != null) {
			putText(BaseAuction.getAuctionInstance().onBidding(mConnectChannel, GlobalDefineCode.USE_CHANNEL_ANDROID,
					mUserMemNumTextField.getText().trim(), mCurrentEntryInfo.getEntryNum(), "450"));
		}
	}

	@FXML
	private void onNextEntryReady(ActionEvent ev) {
		if(!mCurrentEntryInfo.getIsLastEntry().equals("Y")) {
			putText(BaseAuction.getAuctionInstance().onNextEntryReady(mConnectChannel, String.valueOf((Integer.valueOf(mCurrentEntryInfo.getEntryNum()) + 1))));
		} else {
			putText("더 이상 진행할 출품 정보가 없습니다.");
		}
	}
	
	@FXML
	private void onStartAuction(ActionEvent ev) {
		putText(BaseAuction.getAuctionInstance().onStartAuction(mConnectChannel, mCurrentEntryInfo.getEntryNum()));
	}

	@FXML
	private void onStopAuction(ActionEvent ev) {
		putText(BaseAuction.getAuctionInstance().onPauseAuction(mConnectChannel, mCurrentEntryInfo.getEntryNum()));
	}

	@FXML
	protected void onClearText(ActionEvent ev) {
		clearText();
	}

	private void putText(String text) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				String result = null;

				if (mLogTextArea.getText().length() > 0) {
					result = "\n" + text;
				} else {
					result = text;
				}

				mLogTextArea.appendText(result);
			}
		});
	}

	private void clearText() {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				mLogTextArea.setText("");
			}
		});
	}

	@Override
	protected void onAuctionAllClose() {
		super.onAuctionAllClose();
	}

	@Override
	protected void onAuctionAllCloseAndMain() {
		super.onAuctionAllCloseAndMain();
	}

	@Override
	public void onActiveChannel() {
		super.onActiveChannel();
	}

	@Override
	public void onActiveChannel(Channel channel) {
		super.onActiveChannel(channel);
		mConnectChannel = channel;

		putText("onActiveChannel : " + channel.toString());
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				if (channel != null) {
					InetSocketAddress localAddress = (InetSocketAddress) channel.remoteAddress();

					int activeChannelPort = localAddress.getPort();
					putText(BaseAuction.getAuctionInstance().onConnectionInfo(mUserMemNumTextField.getText().trim(),
							channel, activeChannelPort)); // 사용자 정보 전송
				}
			}
		});
	}

	@Override
	public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
		super.onAuctionCountDown(auctionCountDown);
		putText(auctionCountDown.getEncodedMessage());
	}

	@Override
	public void onAuctionStatus(AuctionStatus AuctionStatus) {
		super.onAuctionStatus(AuctionStatus);
		mAuctionStatus = AuctionStatus;
		putText(AuctionStatus.getEncodedMessage());
	}

	@Override
	public void onCurrentEntryInfo(CurrentEntryInfo currentEntryInfo) {
		super.onCurrentEntryInfo(currentEntryInfo);

		mCurrentEntryInfo = currentEntryInfo;

		putText(currentEntryInfo.getEncodedMessage());
	}

	@Override
	public void onBidding(Bidding bidding) {
		super.onBidding(bidding);
		putText(bidding.getEncodedMessage());
	}

	@Override
	public void onToastMessage(ToastMessage toastMessage) {
		super.onToastMessage(toastMessage);
		putText(toastMessage.getEncodedMessage());
	}

	@Override
	public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo) {
		super.onResponseConnectionInfo(responseConnectionInfo);
		putText(responseConnectionInfo.getEncodedMessage());
	}

	@Override
	public void onFavoriteEntryInfo(FavoriteEntryInfo favoriteEntryInfo) {
		super.onFavoriteEntryInfo(favoriteEntryInfo);
		putText(favoriteEntryInfo.getEncodedMessage());
	}

	@Override
	public void onResponseCode(ResponseCode responseCode) {
		super.onResponseCode(responseCode);
		putText(responseCode.getEncodedMessage());
	}

	@Override
	public void onConnectionException(int port) {
		super.onConnectionException(port);
		putText("onConnectionException : " + port);
	}

	@Override
	public void onChannelInactive(int port) {
		super.onChannelInactive(port);
		putText("onChannelInactive : " + port);
	}

	@Override
	public void onCheckSession(ChannelHandlerContext ctx, AuctionCheckSession AuctionCheckSession) {
		super.onCheckSession(ctx, AuctionCheckSession);
		putText(AuctionCheckSession.getEncodedMessage());
		ctx.channel()
				.writeAndFlush(new AuctionReponseSession(mUserMemNumTextField.getText().trim(),
						GlobalDefineCode.CONNECT_CHANNEL_BIDDER, GlobalDefineCode.USE_CHANNEL_ANDROID)
								.getEncodedMessage()
						+ "\r\n");
	}

	private void loadEntryData() {
		BufferedReader tmpBuffer = null;

		try {
			tmpBuffer = Files.newBufferedReader(Paths.get("D:\\Project\\농협중앙회\\경매시스템\\testData.txt"));
			// Charset.forName("UTF-8");
			String line = "";

			while ((line = tmpBuffer.readLine()) != null) {
				String array[] = line.split(",");

				mEntryRepository.add(new EntryInfo(array[1], array[2], array[3], array[4], array[5], array[6], array[7],
						array[8], array[9], array[10], array[11], array[12], array[13], array[14], array[15], "N"));
			}

			System.out.println("mEntryRepository Size : " + mEntryRepository.size());

			mEntryRepository.get(mEntryRepository.size() - 1).setIsLastEntry("Y");

			for (int i = 0; i < mEntryRepository.size(); i++) {
				System.out.println((i + 1) + "번 개체번호 : " + mEntryRepository.get(i).getIndNum() + " / 마지막자료 여부 : "
						+ mEntryRepository.get(i).getIsLastEntry());
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (tmpBuffer != null) {
					tmpBuffer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
