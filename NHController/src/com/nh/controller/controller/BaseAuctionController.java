package com.nh.controller.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.controller.netty.AuctionDelegate;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine.FILE_INFO;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionResult;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.CancelBidding;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.SendAuctionResult;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.RequestAuctionResult;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.ToastMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class BaseAuctionController implements NettyControllable {

	protected Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@FXML
	protected ListView<String> logListView; // 로그 ListView

	protected ResourceBundle mResMsg = null; // 메세지 처리

	protected Stage mStage = null; // 현재 Stage
	
	protected LinkedHashMap<String, EntryInfo> mEntryRepositoryMap = null; // 출품 리스트
	
	protected CurrentEntryInfo mCurrentEntryInfo = null; // 현재 진행 출품

	protected AuctionStatus mAuctionStatus = null; // 경매 상태

	protected LinkedHashMap<String, Bidding> mCurrentBidderMap = null; // 응찰자 정보 수집 Map

	protected List<Bidding> mBeForeBidderDataList = null; // 이전 응찰한 응찰자 정보 수집 List

	public BaseAuctionController() {
		init();
	}

	/**
	 * 초기화 작업
	 */
	protected void init() {
		mAuctionStatus = new AuctionStatus();
		mBeForeBidderDataList = new ArrayList<Bidding>();
		mCurrentBidderMap = new LinkedHashMap<String, Bidding>();
		mEntryRepositoryMap = new LinkedHashMap<String, EntryInfo>();
		AuctionDelegate.getInstance().setClearVariable();
	}

	/**
	 * 경매 상태
	 * 
	 * @param statusCode
	 */
	protected void setAuctionStatus(String statusCode) {
		mAuctionStatus.setState(statusCode);
	}

	/**
	 * 소켓 서버 접속
	 */
	protected void createClient(String host, int port, String userMemNum, String watchMode) {
		AuctionDelegate.getInstance().createClients(host, port, userMemNum, watchMode, this);
	}

	@Override
	public void onActiveChannel(Channel channel) {
		mLogger.debug("onActiveChannel");
		addLogItem(mResMsg.getString("msg.auction.send.connection.info")+ AuctionDelegate.getInstance().onSendConnectionInfo());
		
	      Platform.runLater(new Runnable() {
              @Override
              public void run() {

                  if (channel != null) {

                      InetSocketAddress remoteAddr = (InetSocketAddress) channel.remoteAddress();
                      InetSocketAddress loacalAddr = (InetSocketAddress) channel.localAddress();
                      addLogItem("remoteAddr  :  " + remoteAddr  + " / " + "loacalAddr  :  " + loacalAddr );
                  }
              }
          });
      
	}

	@Override
	public void onActiveChannel() {
		mLogger.debug("onActiveChannel");
	}

	@Override
	public void onAuctionStatus(AuctionStatus auctionStatus) {

		// AUCTION_STATUS_NONE = "8001" // 출품 자료 이관 전 상태
		// AUCTION_STATUS_READY = "8002" // 경매 준비 상태
		// AUCTION_STATUS_START = "8003" // 경매 시작 상태
		// AUCTION_STATUS_PROGRESS = "8004" // 경매 진행 상태
		// AUCTION_STATUS_PASS = "8005" // 경매 출품 건 강제 유찰
		// AUCTION_STATUS_COMPLETED = "8006" // 경매 출품 건 완료 상태
		// AUCTION_STATUS_FINISH = "8007" // 경매 종료 상태

		mAuctionStatus = auctionStatus;

		switch (auctionStatus.getState()) {
		case GlobalDefineCode.AUCTION_STATUS_NONE:
			addLogItem(mResMsg.getString("msg.auction.status.none"));
			break;
		case GlobalDefineCode.AUCTION_STATUS_READY:
			addLogItem(String.format(mResMsg.getString("msg.auction.status.ready"), auctionStatus.getEntryNum()));
			break;
		case GlobalDefineCode.AUCTION_STATUS_START:
			addLogItem(String.format(mResMsg.getString("msg.auction.status.start"), auctionStatus.getEntryNum()));
			break;
		case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
			addLogItem(String.format(mResMsg.getString("msg.auction.status.progress"), auctionStatus.getEntryNum()));
			break;
		case GlobalDefineCode.AUCTION_STATUS_PASS:
			addLogItem(String.format(mResMsg.getString("msg.auction.status.pass"), auctionStatus.getEntryNum()));
			break;
		case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
			addLogItem(String.format(mResMsg.getString("msg.auction.status.completed"), auctionStatus.getEntryNum()));
			break;
		case GlobalDefineCode.AUCTION_STATUS_FINISH:
			addLogItem(mResMsg.getString("msg.auction.status.finish"));
			break;
		}

	}

	@Override
	public void onCurrentEntryInfo(CurrentEntryInfo currentEntryInfo) {

		mCurrentEntryInfo = currentEntryInfo;
		addLogItem(mResMsg.getString("msg.auction.get.current.entry.data") + currentEntryInfo.getEncodedMessage());
	}

	@Override
	public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
		mLogger.debug("onAuctionCountDown : " + auctionCountDown.getEncodedMessage());

		if (auctionCountDown.getStatus().equals(GlobalDefineCode.AUCTION_COUNT_DOWN)) {
			String msg = String.format(mResMsg.getString("msg.auction.get.count.down"), mCurrentEntryInfo.getEntryNum(),
					auctionCountDown.getCountDownTime());
			addLogItem(msg + auctionCountDown.getEncodedMessage());
		}

	}

	@Override
	public void onBidding(Bidding bidding) {
		addLogItem(mResMsg.getString("msg.auction.get.bidding") + bidding.getEncodedMessage());
		setAllBidding(bidding);
	}

	@Override
	public void onToastMessage(ToastMessage toastMessage) {
		mLogger.debug("onToastMessage : " + toastMessage.getEncodedMessage());
		addLogItem(toastMessage.getEncodedMessage());
	}

	@Override
	public void onFavoriteEntryInfo(FavoriteEntryInfo favoriteEntryInfo) {
		mLogger.debug("onFavoriteEntryInfo : " + favoriteEntryInfo.getEncodedMessage());
		addLogItem(favoriteEntryInfo.getEncodedMessage());
	}

	@Override
	public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo) {
	}

	@Override
	public void onResponseCode(ResponseCode responseCode) {
		mLogger.debug("onResponseCode : " + responseCode.getEncodedMessage());

		String msg = "";

		switch (responseCode.getResponseCode()) {
		case GlobalDefineCode.RESPONSE_REQUEST_NOT_RESULT:
			msg = mResMsg.getString("msg.auction.response.code.not.result");
			break;
		case GlobalDefineCode.RESPONSE_REQUEST_FAIL:
			msg = mResMsg.getString("msg.auction.response.code.fail");
			break;
		case GlobalDefineCode.RESPONSE_REQUEST_BIDDING_LOW_PRICE:
			msg = mResMsg.getString("msg.auction.response.code.bidding.low.price");
			break;
		case GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO:
			msg = mResMsg.getString("msg.auction.response.code.not.transmission.entry.info");
			// 출품 이관 전 상태
			setAuctionStatus(GlobalDefineCode.AUCTION_STATUS_NONE);
			break;
		}

		addLogItem(msg + responseCode.getEncodedMessage());
	}

	@Override
	public void onCancelBidding(CancelBidding cancelBidding) {
		
		addLogItem(mResMsg.getString("msg.auction.get.bidding.cancel") + cancelBidding.getEncodedMessage());
		
		if (mCurrentBidderMap.containsKey(cancelBidding.getUserNo())) {
			Bidding currentBidder = mCurrentBidderMap.get(cancelBidding.getUserNo());
			currentBidder.setCancelBidding(true);
			mBeForeBidderDataList.add(currentBidder);
			mCurrentBidderMap.remove(cancelBidding.getUserNo());
		}
	}

	@Override
	public void onRequestAuctionResult(RequestAuctionResult requestAuctionResult) {

		if(mEntryRepositoryMap != null && mEntryRepositoryMap.size() > 0) {

			if(mEntryRepositoryMap.containsKey(requestAuctionResult.getEntryNum())) {
				
				EntryInfo entryInfo = mEntryRepositoryMap.get(requestAuctionResult.getEntryNum());
				
				if(entryInfo != null) {
					// 낙유찰 결과 전송
					switch (mAuctionStatus.getState()) {
					case GlobalDefineCode.AUCTION_STATUS_PASS:
						calculationRankingAndLog(entryInfo, true);
						break;
					case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
						calculationRankingAndLog(entryInfo, false);
						break;
					}
				}
			}
		}
	}

	@Override
	public void onCheckSession(ChannelHandlerContext ctx, AuctionCheckSession auctionCheckSession) {
		AuctionDelegate.getInstance().onSendCheckSession();
//		addLogItem(mResMsg.getString("msg.auction.send.session") + AuctionDelegate.getInstance().onSendCheckSession());
	}

	@Override
	public void onAuctionResult(AuctionResult auctionResult) {
	}

	@Override
	public void onConnectionException(int port) {
		Platform.runLater(() -> {
			addLogItem(mResMsg.getString("msg.connection.fail"));
			CommonUtils.getInstance().dismissLoadingDialog();
		});
	}

	@Override
	public void onChannelInactive(int port) {
		mLogger.debug("onChannelInactive : " + port);
		addLogItem(mResMsg.getString("msg.disconnection"));
	}

	@Override
	public void exceptionCaught(int port) {
		mLogger.debug("exceptionCaught : " + port);
		addLogItem("exceptionCaught : " + port);
	}

	/**
	 * 현재 가격 기준 모든 응찰 정보 수집 중복 응찰 정보는 수집 대상에서 제외 처리
	 * 
	 * @param bidding
	 */
	public synchronized void setAllBidding(Bidding bidding) {

		if (mCurrentBidderMap.containsKey(bidding.getUserNo())) {

			Bidding beforeBidder = mCurrentBidderMap.get(bidding.getUserNo());

			mBeForeBidderDataList.add(beforeBidder);

			mCurrentBidderMap.put(bidding.getUserNo(), bidding);

		} else {
			mCurrentBidderMap.put(bidding.getUserNo(), bidding);
		}

	}

	/**
	 * 랭킹 , 로그파일
	 */
	protected void calculationRankingAndLog(EntryInfo entryInfo, boolean isPass) {

		// 순위 정렬
		List<Bidding> list = new ArrayList<Bidding>();

		list.addAll(mCurrentBidderMap.values());

		List<Bidding> rankBiddingDataList = getCurrentRank(list);

		boolean isSuccess = false; // 낙찰 :true , 유찰 : false

		if (!isPass && !CommonUtils.getInstance().isListEmpty(rankBiddingDataList)) {

			// 1순위 데이터
			Bidding biddingUser = rankBiddingDataList.get(0);

			// 현재 차량 시작가
			int startPrice = Integer.parseInt(entryInfo.getStartPrice());

			// 경매 결과 Obj
			SendAuctionResult auctionResult = new SendAuctionResult();
			auctionResult.setAuctionHouseCode(entryInfo.getAuctionHouseCode());
			auctionResult.setEntryNum(entryInfo.getEntryNum());

			// 응찰 금액이 시작가와 같거나 큰 경우 낙찰
			if (biddingUser.getPriceInt() >= startPrice) {
				isSuccess = true;
				sendAuctionResult(true, entryInfo, biddingUser);
			} else {
				isSuccess = false;
				// 유찰
				sendAuctionResult(false, entryInfo, null);
			}

			// Create LogFile
			runWriteLogFile(rankBiddingDataList, mAuctionStatus, isSuccess, biddingUser.getUserNo());

		} else {
			// 유찰
			sendAuctionResult(isSuccess, entryInfo, null);
			// Create LogFile
			runWriteLogFile(rankBiddingDataList, mAuctionStatus, isSuccess, "");
		}
	}

	/**
	 * 낙유찰 결과 전송
	 * 
	 * @param isSuccess
	 * @param currentEntryInfo
	 * @param bidder
	 */
	private void sendAuctionResult(boolean isSuccess, EntryInfo entryInfo, Bidding bidder) {

		SendAuctionResult auctionResult = new SendAuctionResult();
		auctionResult.setAuctionHouseCode(entryInfo.getAuctionHouseCode());
		auctionResult.setEntryNum(entryInfo.getEntryNum());

		if (isSuccess) {
			// 낙찰
			auctionResult.setResultCode(GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS);
			auctionResult.setSuccessBidder(bidder.getUserNo());
			auctionResult.setSuccessBidPrice(bidder.getPrice());
		} else {
			// 유찰
			auctionResult.setResultCode(GlobalDefineCode.AUCTION_RESULT_CODE_FAIL);
			auctionResult.setSuccessBidder(null);
			auctionResult.setSuccessBidPrice(null);
		}

		// 낙유찰 결과 전송
		addLogItem(mResMsg.getString("msg.auction.send.result") + AuctionDelegate.getInstance().onSendAuctionResult(auctionResult));

	}

	/**
	 * run Log
	 * 
	 * @param dataList
	 * @param auctionStatus
	 * @param isSuccess
	 * @param userNo
	 */
	protected void runWriteLogFile(final List<Bidding> dataList, final AuctionStatus auctionStatus,
			final boolean isSuccess, final String userNo) {

		// Create LogFile
		Thread thread = new Thread("logFile") {
			@Override
			public void run() {
				writeLogFile(dataList, auctionStatus, isSuccess, userNo);
			}
		};
		thread.setDaemon(true);
		thread.start();

	}

	/**
	 * 
	 * @MethodName setCurrentRank
	 * @Description 현재 응찰 가격 기준 우선순위 확인 처리
	 *
	 */
	@SuppressWarnings("unlikely-arg-type")
	protected List<Bidding> getCurrentRank(List<Bidding> list) {

		mLogger.debug("응찰 가격 우선순위 확인");

		Collections.sort(list, new Comparator<Bidding>() {

			public int compare(Bidding o1, Bidding o2) {
				int result = 0;

				if (Long.parseLong(o1.getPrice()) == Long.parseLong(o2.getPrice())) {
					if (Long.parseLong(o1.getBiddingTime()) == Long.parseLong(o2.getBiddingTime())) {
						result = 0;
					} else if (Long.parseLong(o1.getBiddingTime()) < Long.parseLong(o2.getBiddingTime())) {
						result = -1;
					} else if (Long.parseLong(o1.getBiddingTime()) > Long.parseLong(o2.getBiddingTime())) {
						result = 1;
					}
				} else if (Long.parseLong(o1.getPrice()) > Long.parseLong(o2.getPrice())) {
					result = -1;
				} else if (Long.parseLong(o1.getPrice()) < Long.parseLong(o2.getPrice())) {
					result = 1;
				}

				return result;
			}
		});

		return list;

	}

	/**
	 * @Description 현재 응찰 가격 기준 우선순위 확인 처리
	 * @param biddingList
	 * @param auctionStatus
	 * @param isAuctionPass
	 */
	protected synchronized void writeLogFile(List<Bidding> biddingList, AuctionStatus auctionStatus, boolean isSuccess,
			String userNo) {

		String currentTime = CommonUtils.getInstance().getCurrentTime("yyyyMMdd");

		String LOG_DIRECTORY = FILE_INFO.AUCTION_LOG_FILE_PATH + currentTime + "_"
				+ auctionStatus.getAuctionHouseCode().toUpperCase();

		String LOG_FILE_NAME = currentTime + "-" + auctionStatus.getEntryNum() + FILE_INFO.AUCTION_LOG_FILE_EXTENSION;

		File fileDirectory = new File(LOG_DIRECTORY);
		File file = new File(fileDirectory, LOG_FILE_NAME);
		FileWriter fileWriter = null;

		String EMPTY_SPACE = "    ";
		String MAIN_LINE = "\r\n====================================================================================================\r\n";
		String SUB_LINE = "\r\n---------------------------------------------------------------------------------------------------------------------------------------------------------\r\n";
		String ENTER_LINE = "\r\n";
		StringBuffer logContent = new StringBuffer();
		String logCurrentTime = CommonUtils.getInstance().getCurrentTime("yyyyMMdd HH:mm:ss:SSS");

		try {
			if (!fileDirectory.exists()) {
				fileDirectory.mkdirs();
			}
			if (file.exists()) {
				addLogItem(String.format(mResMsg.getString("msg.file.duplicated"), LOG_FILE_NAME));
			} else {
				if (file.createNewFile()) {
					addLogItem(String.format(mResMsg.getString("msg.file.create"), LOG_FILE_NAME));
				}
			}

			fileWriter = new FileWriter(file, true);

			String entryNum = auctionStatus.getEntryNum();

			String startPrice = String.format(mResMsg.getString("log.auction.result.start.price"),
					Integer.parseInt(auctionStatus.getStartPrice()));

			String auctionResult = null;

			if (isSuccess) {
				// 낙찰
				auctionResult = mResMsg.getString("log.auction.result.success");
			} else {
				// 유찰
				auctionResult = mResMsg.getString("log.auction.result.fail");
			}

			logContent.append(MAIN_LINE + ENTER_LINE);

			logContent.append(String.format(mResMsg.getString("log.auction.result.entry.info"), logCurrentTime,
					entryNum, startPrice, auctionResult, userNo));

			logContent.append(SUB_LINE);

			if (isSuccess && !CommonUtils.getInstance().isListEmpty(biddingList)) {

				for (int i = 0; i < biddingList.size(); i++) {
					logContent.append(ENTER_LINE);
					String rankUser = String.format(mResMsg.getString("log.auction.result.rank"),
							Integer.toString((i + 1)), biddingList.get(i).getUserNo());
					String price = String.format(mResMsg.getString("log.auction.result.price"),
							Integer.parseInt(biddingList.get(i).getPrice())) + EMPTY_SPACE
							+ CommonUtils.getInstance()
									.getCurrentTime_yyyyMMddHHmmssSSS(biddingList.get(i).getBiddingTime());
					logContent.append(rankUser + EMPTY_SPACE + price);

				}
				logContent.append(ENTER_LINE);
			} else {
				logContent.append(ENTER_LINE + mResMsg.getString("log.auction.result.fail") + ENTER_LINE);
			}

			if (!CommonUtils.getInstance().isListEmpty(mBeForeBidderDataList)) {

				logContent.append(SUB_LINE);

				// ㄱㄴㄷ sort
				Collections.sort(mBeForeBidderDataList);

				// 중복제거
				Set<Bidding> linkedSet = new LinkedHashSet<>(mBeForeBidderDataList);
				List<Bidding> distintListData = new ArrayList<Bidding>(linkedSet);

				for (Bidding disBidding : distintListData) {

					logContent.append(ENTER_LINE);
					logContent.append(String.format(mResMsg.getString("log.auction.result.before.bidder"),disBidding.getUserNo()));
					logContent.append(ENTER_LINE);

					for (Bidding beforBidding : mBeForeBidderDataList) {

						if (disBidding.getUserNo().equals(beforBidding.getUserNo())) {
							logContent.append(String.format(mResMsg.getString("log.auction.result.price"),beforBidding.getPriceInt()));
							logContent.append(EMPTY_SPACE);
							logContent.append(EMPTY_SPACE);
							logContent.append(CommonUtils.getInstance().getCurrentTime_yyyyMMddHHmmssSSS(beforBidding.getBiddingTime()));

							if(beforBidding.isCancelBidding()) {
								logContent.append(EMPTY_SPACE);
								logContent.append(EMPTY_SPACE);
								logContent.append(mResMsg.getString("log.auction.result.cancel.bidding"));
							}

							logContent.append(ENTER_LINE);
						}
					}
				}
			}

			logContent.append(ENTER_LINE);
			addLogItem(logContent.toString());

			fileWriter.write(logContent.toString());
			fileWriter.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ADD 로그
	 * 
	 * @param str
	 */
	protected void addLogItem(String str) {
		Platform.runLater(() -> {
			if (!str.isEmpty()) {
				logListView.getItems().add(str);
				logListView.scrollTo(logListView.getItems().size() - 1);
			}
		});
	}

	/**
	 * 로그 제거
	 * 
	 * @param event
	 */
	protected void onClearLog(MouseEvent event) {
		if (logListView != null) {
			if (logListView.getItems().size() > 0) {
				logListView.getItems().clear();
				addLogItem(mResMsg.getString("log.auction"));
			}
		}
	}

}
