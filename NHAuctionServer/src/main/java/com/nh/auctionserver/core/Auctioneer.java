package com.nh.auctionserver.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.netty.AuctionServer;
import com.nh.auctionserver.setting.AuctionServerSetting;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.ToastMessageRequest;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.RequestAuctionResult;
import com.nh.share.server.models.ToastMessage;
import com.nh.share.setting.AuctionShareSetting;
import com.sun.swing.internal.plaf.synth.resources.synth;

import io.netty.channel.ChannelId;

public class Auctioneer {
	private final Logger mLogger = LoggerFactory.getLogger(Auctioneer.class);

	public final String SYSTEM_USER_NAME = "SYSTEM";

	private AuctionServer mAuctionServer;

	private Map<ChannelId, ConnectionInfo> mConnectorInfoMap;

	private Map<String, Boolean> mIsRequestAuctionStopMap = new HashMap<String, Boolean>(); // 경매 자동 시작 정지 요청 Flag Map
	private Map<String, Boolean> mIsRequestAuctionPassMap = new HashMap<String, Boolean>(); // // 경매 유찰 처리 요청 Flag Map
	private Map<String, Boolean> mIsAuctionPassMap = new HashMap<String, Boolean>(); // 경매 강제 유찰 처리 Flag Map

	private static AuctionConfig mAuctionConfig = new AuctionConfig(); // 경매 생성시 설정 정보
	private static Map<String, AuctionState> mAuctionStateMap = null; // 경매 진행 상태, 현재 경매 상황 등 계속 변화하는 정보
	// 출품 목록
	private static AuctionEntryRepository mAuctionEntryRepository = new AuctionEntryRepository();

	private Map<String, ScheduledExecutorService> mStartCountDownServiceMap = new HashMap<String, ScheduledExecutorService>();
	private Map<String, ScheduledExecutorService> mNextEntryIntervalServiceMap = new HashMap<String, ScheduledExecutorService>();
	private Map<String, ScheduledFuture<?>> mStartCountDownJobMap = new HashMap<String, ScheduledFuture<?>>();
	private Map<String, ScheduledFuture<?>> mNextEntryIntervalJobMap = new HashMap<String, ScheduledFuture<?>>();

	private ScheduledExecutorService mCheckSessionService = Executors.newScheduledThreadPool(5);
	private ScheduledFuture<?> mCheckSessionTimerJob; // 유효 세션 확인 Job

	private ExecutorService mLogWriterService = Executors.newCachedThreadPool();

	// 동시에 들어온 입찰 중 최초 입찰 정보만 기록되는 맵, 각 가격당 한명의 입찰자만 기록되어야 하므로 Map을 사용함.
	private Map<String, Map<Integer, String>> mTopBiddingMap = new HashMap<String, Map<Integer, String>>();

	/*
	 * 최초 입찰을 제외한 나머지 입찰이 기록되는 리스트 순서대로 담기만 할 것이므로 List를 사용함. 통신 속도에 따라 가격 순서가 뒤바뀔 수
	 * 있으니 경매가 끝난 후 우선순위 계산을 다시 해야한다.
	 */

	private Map<String, LinkedHashMap<String, Bidding>> mCurrentEntryBiddingMap = new HashMap<String, LinkedHashMap<String, Bidding>>();

	private Map<String, ArrayList<Bidding>> mCurrentEntryBiddingListMap = new HashMap<String, ArrayList<Bidding>>();

	// 동일가 응찰자 정보 수집 Map
	private Map<String, Map<String, Bidding>> mCurrentBidderMap = new HashMap<String, Map<String, Bidding>>();

	public Auctioneer(AuctionServer auctionServer) {
		this.mAuctionServer = auctionServer;

		// 경매 유효 접속 세션 확인 처리 시작
		startCheckSessionTimer();

		for (GlobalDefineCode.AUCTION_HOUSE auctionHouseCode : GlobalDefineCode.AUCTION_HOUSE.values()) {
			initAuction(auctionHouseCode.getValue());
		}

	}

	private void initAuction(String auctionHouseCode) {
		if (mAuctionStateMap == null) {
			mAuctionStateMap = new HashMap<String, AuctionState>();
		}

		if (!mAuctionStateMap.containsKey(auctionHouseCode)) {
			mAuctionStateMap.put(auctionHouseCode, new AuctionState(auctionHouseCode, this));
		}

		// 경매 응찰 정보 Reset
		resetAuctionData(auctionHouseCode);
	}

	/**
	 * 
	 * @MethodName getCurrentAuctionStatus
	 * @Description 현재 경매 상태 반환 처리
	 *
	 * @return AuctionState 현재 경매 상태
	 */
	public String getCurrentAuctionStatus(String auctionHouseCode) {
		return mAuctionStateMap.get(auctionHouseCode).getAuctionState();
	}

	/**
	 * 
	 * @MethodName startAuction
	 * @Description 경매 진행 시작 처리
	 *
	 */
	public synchronized void startAuction(String auctionHouseCode) {
		mLogger.debug("startAuction auctionHouseCode : " + auctionHouseCode);
		if (mAuctionStateMap.containsKey(auctionHouseCode)) {
			if (mAuctionStateMap.get(auctionHouseCode).getAuctionState()
					.equals(GlobalDefineCode.AUCTION_STATUS_READY)) {
				mLogger.debug("경매가 시작되었습니다.");

				mAuctionStateMap.get(auctionHouseCode).onStart();

				if (mAuctionServer != null) {
					mAuctionServer
							.itemAdded(mAuctionStateMap.get(auctionHouseCode).getAuctionStatus().getEncodedMessage());
				}

				mAuctionStateMap.get(auctionHouseCode).onProgress();

				if (mAuctionServer != null) {
					mAuctionServer
							.itemAdded(mAuctionStateMap.get(auctionHouseCode).getAuctionStatus().getEncodedMessage());
				}

//				mAuctionEntryRepository.setInitialEntryList(response.getResult());
//				mAuctionState.setEntryInformationLoad();
//				mAuctionState.setCheckEntryInformation();

			}
		}
	}

	/**
	 * 
	 * @MethodName stopAuction
	 * @Description 경매 진행 정지 처리
	 *
	 */
	public synchronized void stopAuction(String auctionHouseCode) {
		mIsRequestAuctionStopMap.put(auctionHouseCode, true);
		startAuctionFinishCountDown(auctionHouseCode);
	}

	/**
	 * 
	 * @MethodName passAuction
	 * @Description 경매 유찰 처리
	 *
	 */
	public synchronized void passAuction(String auctionHouseCode) {
		mIsRequestAuctionPassMap.put(auctionHouseCode, true);

		if (mAuctionServer != null) {
			// 경매 출품 건 유찰로 상태 변경
			mAuctionStateMap.get(auctionHouseCode).onPass();

			if (mAuctionServer != null) {
				mAuctionServer.itemAdded(mAuctionStateMap.get(auctionHouseCode).getAuctionStatus().getEncodedMessage());
				
				// 낙유찰 정보 전송 요청
				mAuctionServer.itemAdded(new RequestAuctionResult(auctionHouseCode,
						mAuctionStateMap.get(auctionHouseCode).getAuctionStatus().getEntryNum())
								.getEncodedMessage());
			}
		}
	}

	public synchronized void broadcastToastMessage(ToastMessageRequest requestToastMessage) {
		ToastMessage toastMessage = new ToastMessage(requestToastMessage.getAuctionHouseCode(),
				requestToastMessage.getMessage());

		if (mAuctionServer != null) {
			mAuctionServer.itemAdded(toastMessage.getEncodedMessage());
		}
	}

	public void startAuctionFinishCountDown(String auctionHouseCode) {
		if (mStartCountDownJobMap.containsKey(auctionHouseCode)) {
			mStartCountDownJobMap.get(auctionHouseCode).cancel(true);
		}

		mAuctionStateMap.get(auctionHouseCode).onAuctionCountDown();

		if (!mStartCountDownServiceMap.containsKey(auctionHouseCode)) {
			mStartCountDownServiceMap.put(auctionHouseCode, Executors.newScheduledThreadPool(5));
		}

		mStartCountDownJobMap.put(auctionHouseCode,
				mStartCountDownServiceMap.get(auctionHouseCode).scheduleAtFixedRate(
						new AuctionCountDownTimerJob(auctionHouseCode), AuctionServerSetting.COUNT_DOWN_DELAY_TIME,
						AuctionServerSetting.COUNT_DOWN_DELAY_TIME, TimeUnit.MILLISECONDS));
	}

	public synchronized void runNextEntryInterval(String auctionHouseCode) {
		if (mNextEntryIntervalJobMap.containsKey(auctionHouseCode)) {
			mNextEntryIntervalJobMap.get(auctionHouseCode).cancel(true);
		}

		if (!mNextEntryIntervalServiceMap.containsKey(auctionHouseCode)) {
			mNextEntryIntervalServiceMap.put(auctionHouseCode, Executors.newScheduledThreadPool(5));
		}

		mNextEntryIntervalJobMap.put(auctionHouseCode,
				mNextEntryIntervalServiceMap.get(auctionHouseCode).scheduleAtFixedRate(
						new AuctionNextEntryIntervalTimerJob(auctionHouseCode),
						AuctionServerSetting.AUCTION_NEXT_ENTRY_DELAY_TIME,
						AuctionServerSetting.AUCTION_NEXT_ENTRY_DELAY_TIME, TimeUnit.MILLISECONDS));
	}

	/**
	 * 
	 * @MethodName readyEntryInfo
	 * @Description 경매 출품번호에 해당하는 출품 정보 설정 처리
	 *
	 */
	public synchronized void readyEntryInfo(String auctionHouseCode, String entryNum) {
		// 경매 응찰 정보 Reset
		resetAuctionData(auctionHouseCode);

		EntryInfo entryInfo = mAuctionEntryRepository.popEntry(auctionHouseCode, entryNum);

		if (mAuctionEntryRepository.getTotalCount(auctionHouseCode) >= 1) {
			mAuctionStateMap.get(auctionHouseCode).setCurrentEntryInfo(entryInfo);
		} else {
			// 마지막 차량 일 경우 수행 처리 로직 개발 필요
		}

		mAuctionStateMap.get(auctionHouseCode)
				.setCurrentBidderCount(String.valueOf(mCurrentBidderMap.get(auctionHouseCode).size()));

		mLogger.debug(entryInfo.getEntryNum() + "번 출품 상품이 경매 준비되었습니다.");
		mAuctionStateMap.get(auctionHouseCode).onReady();

		// 출품 정보 및 경매 상태 전송
		if (mAuctionServer != null) {
			mAuctionServer.itemAdded(new CurrentEntryInfo(mAuctionStateMap.get(auctionHouseCode).getCurrentEntryInfo())
					.getEncodedMessage());

			mAuctionServer.itemAdded(mAuctionStateMap.get(auctionHouseCode).getAuctionStatus().getEncodedMessage());
		}
	}

	/**
	 * 
	 * @MethodName readyEntryInfo
	 * @Description 경매 출품 정보 설정 처리(순차적)
	 *
	 */
	public synchronized void readyEntryInfo(String auctionHouseCode) {
		// 경매 응찰 정보 Reset
		resetAuctionData(auctionHouseCode);

		EntryInfo entryInfo = mAuctionEntryRepository.popEntry(auctionHouseCode);

		if (mAuctionEntryRepository.getTotalCount(auctionHouseCode) >= 1) {
			mAuctionStateMap.get(auctionHouseCode).setCurrentEntryInfo(entryInfo);
		} else {
			// 마지막 차량 일 경우 수행 처리 로직 개발 필요
		}

		mAuctionStateMap.get(auctionHouseCode)
				.setCurrentBidderCount(String.valueOf(mCurrentBidderMap.get(auctionHouseCode).size()));

		mLogger.debug(entryInfo.getEntryNum() + "번 출품 상품이 경매 준비되었습니다.");
		mAuctionStateMap.get(auctionHouseCode).onReady();

		// 출품 정보 및 경매 상태 전송
		if (mAuctionServer != null) {
			mAuctionServer.itemAdded(new CurrentEntryInfo(mAuctionStateMap.get(auctionHouseCode).getCurrentEntryInfo())
					.getEncodedMessage());

			mAuctionServer.itemAdded(mAuctionStateMap.get(auctionHouseCode).getAuctionStatus().getEncodedMessage());
		}
	}

	/**
	 * 
	 * @MethodName addEntryInfo
	 * @Description 출품 자료 전송에 따른 출품 자료 추가 처리
	 *
	 * @param entryInfo
	 * @return
	 */
	public void addEntryInfo(String auctionHouseCode, EntryInfo entryInfo) {
		mAuctionEntryRepository.pushEntry(auctionHouseCode, entryInfo);

		if (entryInfo.getIsLastEntry().equals("Y")) {
			readyEntryInfo(auctionHouseCode);
		}
	}

	/**
	 * 
	 * @MethodName getEntryInfo
	 * @Description 출품번호를 통해 요청된 출품 정보 반환 처리
	 *
	 * @param entryNum
	 * @return
	 */
	public EntryInfo getEntryInfo(String auctionHouseCode, String entryNum) {
		EntryInfo entryInfo;

		entryInfo = mAuctionEntryRepository.getEntryInfo(auctionHouseCode, entryNum);

		return entryInfo;
	}

	/**
	 * 
	 * @MethodName getAuctionState
	 * @Description 현재 경매 상태 정보 반환 처리
	 *
	 * @return AuctionState 경매 상태 정보
	 */
	public AuctionState getAuctionState(String auctionHouseCode) {
		AuctionState auctionState = null;

		if (mAuctionStateMap != null) {
			if (mAuctionStateMap.containsKey(auctionHouseCode)) {
				auctionState = mAuctionStateMap.get(auctionHouseCode);
			}
		}

		return auctionState;
	}

	/**
	 * 
	 * @MethodName setAuctionStatus
	 * @Description 현재 경매 상태 정보 적용 처리
	 *
	 * @return setAuctionStatus 경매 상태 정보
	 */
	public void setAuctionStatus(String auctionHouseCode, AuctionStatus auctionStatus) {
		if (mAuctionStateMap.containsKey(auctionHouseCode)) {
			mLogger.debug("setAuctionStatus mAuctionStateMap : " + mAuctionStateMap);
			mLogger.debug("setAuctionStatus mAuctionStateMap.get(auctionHouseCode) : "
					+ mAuctionStateMap.get(auctionHouseCode));

			mAuctionStateMap.get(auctionHouseCode).setAuctionStatus(auctionStatus);
		}
	}

	/**
	 * 유효 응찰 정보 수집 처리
	 * 
	 * @param bidding 유효한 응찰 정보
	 */
//	public synchronized void setBidding(Bidding bidding) {
//		if (bidding != null) {
//			// 현재 응찰중인 출품번호인지 확인.
//			if (bidding.getEntryNum().equals(mAuctionStateMap.getEntryNum())) {
//				if (bidding.getPriceInt() == Integer.valueOf(mAuctionStateMap.getStartPrice())) {
//
//					// 현재 가격 기준 모든 응찰 정보 수집
//					if (bidding != null) {
//						setAllBidding(bidding);
//					}
//				} else {
//					mLogger.debug("=========================================================");
//					mLogger.debug("응찰 가능 가격이 아닙니다. 본 응찰 정보는 인정되지 않았습니다.");
//					mLogger.debug("회원번호(채널) : " + bidding.getUserNo() + "(" + bidding.getChannel() + ")");
//					mLogger.debug("응찰 출품번호 : " + mAuctionStateMap.getEntryNum());
//					mLogger.debug("응찰 가격 : " + bidding.getPrice());
//					mLogger.debug("현재 가격 : " + mAuctionStateMap.getStartPrice());
//					mLogger.debug("=========================================================");
//				}
//
//			} else {
//				mLogger.debug("=========================================================");
//				mLogger.debug("응찰 가능 출품번호가 아닙니다. 본 응찰 정보는 인정되지 않았습니다.");
//				mLogger.debug("회원번호(채널) : " + bidding.getUserNo() + "(" + bidding.getChannel() + ")");
//				mLogger.debug("응찰 출품번호 : " + bidding.getEntryNum());
//				mLogger.debug("현재 출품번호 : " + mAuctionStateMap.getEntryNum());
//				mLogger.debug("=========================================================");
//			}
//		}
//	}

	/**
	 * 현재 가격 기준 모든 응찰 정보 수집 중복 응찰 정보는 수집 대상에서 제외 처리
	 * 
	 * @param bidding
	 */
//	public synchronized void setAllBidding(Bidding bidding) {
//		// 동일가 응찰자 정보 수집
//		if (!mCurrentBidderMap.containsKey(bidding.getUserNo())
//				|| (mCurrentBidderMap.containsKey(bidding.getUserNo()))) {
//			mCurrentBidderMap.put(bidding.getUserNo(), bidding);
//			mAuctionStateMap.setCurrentBidderCount(String.valueOf(mCurrentBidderMap.size()));
//
//			if (mCurrentEntryBiddingMap != null) {
//				if (!mCurrentEntryBiddingMap.containsKey(bidding.getUserNo())) {
//					mCurrentEntryBiddingMap.put(bidding.getUserNo(), bidding);
//				}
//			}
//		}
//	}

	private synchronized void writeLogFile(String auctionHouseCode, List<Bidding> biddingList,
			AuctionState auctionState, boolean isAuctionPass) {
		String currentTime = getCurrentTime("yyyyMMdd");

		String LOG_DIRECTORY = AuctionServerSetting.AUCTION_LOG_FILE_PATH + auctionHouseCode + "/" + currentTime;
		String LOG_FILE_NAME = getCurrentTime("yyyyMMdd") + "-" + mAuctionStateMap.get(auctionHouseCode).getEntryNum()
				+ AuctionServerSetting.AUCTION_LOG_FILE_EXTENSION;

		File fileDirectory = new File(LOG_DIRECTORY);
		File file = new File(fileDirectory, LOG_FILE_NAME);
		FileWriter fileWriter = null;

		String EMPTY_SPACE = "    ";
		String MAIN_LINE = "\r\n====================================================================================================\r\n";
		String SUB_LINE = "\r\n-------------------------------------------------------------------------------------------------------------------------------------------------------------\r\n";
		String logContent = null;
		String logCurrentTime = getCurrentTime("yyyyMMdd HH:mm:ss:SSS");
		String prevBiddingPrice = "";

		try {
			if (!fileDirectory.exists()) {
				fileDirectory.mkdirs();
			}
			if (file.exists()) {
				mLogger.info(" 이미 생성 된 " + LOG_FILE_NAME + "파일이 존재합니다.");
			} else {
				if (file.createNewFile()) {
					mLogger.info(LOG_FILE_NAME + "파일이 정상적으로 생성되었습니다.");
				}
			}

			fileWriter = new FileWriter(file, true);

			if (biddingList != null && biddingList.size() > 0) {
				for (int i = 0; i < biddingList.size(); i++) {
					if (i == 0) {
						String auctionResult = null;
						String auctionRank1Bidder = "-";
						String auctionRank2Bidder = "-";
						String auctionRank3Bidder = "-";
						String auctionRank1BidderChannel = "-";
						String auctionRank2BidderChannel = "-";
						String auctionRank3BidderChannel = "-";
						String auctionRank1BidPrice = "-";
						String auctionRank2BidPrice = "-";
						String auctionRank3BidPrice = "-";

						if (Integer.valueOf(auctionState.getRank1BidPrice()) >= Integer
								.valueOf(auctionState.getCurrentEntryInfo().getStartPrice()) && !isAuctionPass) {
							auctionResult = "낙찰";

							if (auctionState.getRank1MemberNum() != null) {
								auctionRank1Bidder = auctionState.getRank1MemberNum();
							}

							if (auctionState.getRank2MemberNum() != null) {
								auctionRank2Bidder = auctionState.getRank2MemberNum();
							}

							if (auctionState.getRank3MemberNum() != null) {
								auctionRank3Bidder = auctionState.getRank3MemberNum();
							}

							if (auctionState.getRank1MemberChannel() != null) {
								auctionRank1BidderChannel = auctionState.getRank1MemberChannel();
							}

							if (auctionState.getRank2MemberChannel() != null) {
								auctionRank2BidderChannel = auctionState.getRank2MemberChannel();
							}

							if (auctionState.getRank3MemberChannel() != null) {
								auctionRank3BidderChannel = auctionState.getRank3MemberChannel();
							}

							if (auctionState.getRank1BidPrice() != null) {
								auctionRank1BidPrice = auctionState.getRank1BidPrice();
							}

							if (auctionState.getRank2BidPrice() != null) {
								auctionRank2BidPrice = auctionState.getRank2BidPrice();
							}

							if (auctionState.getRank3BidPrice() != null) {
								auctionRank3BidPrice = auctionState.getRank3BidPrice();
							}
						} else {
							auctionResult = "유찰";
							auctionRank1Bidder = "-";
						}

						logContent = MAIN_LINE + logCurrentTime + EMPTY_SPACE + "출품번호 : " + auctionState.getEntryNum()
								+ EMPTY_SPACE + "시작가 : " + auctionState.getCurrentEntryInfo().getStartPrice()
								+ EMPTY_SPACE + "낙/유찰 결과 : " + auctionResult + EMPTY_SPACE + "낙찰회원 : "
								+ auctionRank1Bidder + EMPTY_SPACE + "최종가 : " + auctionRank1BidPrice + MAIN_LINE
								+ "[순위 정보] \r\n" + "1순위 회원번호 : " + auctionRank1Bidder + EMPTY_SPACE + "접속채널 : "
								+ auctionRank1BidderChannel + EMPTY_SPACE + "응찰가 : " + auctionRank1BidPrice + "\r\n"
								+ "2순위 회원번호 : " + auctionRank2Bidder + EMPTY_SPACE + "접속채널 : "
								+ auctionRank2BidderChannel + EMPTY_SPACE + "응찰가 : " + auctionRank2BidPrice + "\r\n"
								+ "3순위 회원번호 : " + auctionRank3Bidder + EMPTY_SPACE + "접속채널 : "
								+ auctionRank3BidderChannel + EMPTY_SPACE + "응찰가 : " + auctionRank3BidPrice + SUB_LINE;

						fileWriter.write(logContent);
						fileWriter.flush();
					}

					if (!prevBiddingPrice.equals(biddingList.get(i).getPrice())) {
						prevBiddingPrice = biddingList.get(i).getPrice();

						logContent = SUB_LINE + getLogTimeFormat(biddingList.get(i).getBiddingTime()) + EMPTY_SPACE
								+ "회원번호 : " + biddingList.get(i).getUserNo() + EMPTY_SPACE + "접속채널 : "
								+ biddingList.get(i).getChannel() + EMPTY_SPACE + "응찰가 : "
								+ biddingList.get(i).getPrice();

						fileWriter.write(logContent);
						fileWriter.flush();
					} else {
						logContent = "\r\n" + getLogTimeFormat(biddingList.get(i).getBiddingTime()) + EMPTY_SPACE
								+ "회원번호 : " + biddingList.get(i).getUserNo() + EMPTY_SPACE + "접속채널 : "
								+ biddingList.get(i).getChannel() + EMPTY_SPACE + "응찰가 : "
								+ biddingList.get(i).getPrice();

						fileWriter.write(logContent);
						fileWriter.flush();
					}

					if (i == (biddingList.size() - 1)) {
						logContent = SUB_LINE;

						fileWriter.write(logContent);
						fileWriter.flush();
					}
				}
			}
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
	 * 
	 * @ClassName Auctioneer.java
	 * @Description 경매 시작 카운트 다운 처리 클래스
	 * @author 박종식
	 * @since 2019.12.03
	 */
	private class AuctionCountDownTimerJob implements Runnable {
		String auctionHouseCode;

		public AuctionCountDownTimerJob(String auctionHouseCode) {
			this.auctionHouseCode = auctionHouseCode;
		}

		@Override
		public void run() {
			mLogger.debug("경매 카운트 다운 시간 : " + mAuctionStateMap.get(auctionHouseCode).getAuctionCountDownTime());

			if (mAuctionStateMap.get(auctionHouseCode).getAuctionCountDownTime() < 0) {
				if (mAuctionServer != null) {
					mAuctionStateMap.get(auctionHouseCode).onAuctionCountDownCompleted();

					mAuctionServer.itemAdded(new AuctionCountDown(auctionHouseCode,
							mAuctionStateMap.get(auctionHouseCode).getAuctionCountDownStatus(),
							String.valueOf(mAuctionStateMap.get(auctionHouseCode).getAuctionCountDownTime()))
									.getEncodedMessage());

					// 경매 출품 건 완료 상태로 전환
					mAuctionStateMap.get(auctionHouseCode).onCompleted();

					if (mAuctionServer != null) {
						mAuctionServer.itemAdded(
								mAuctionStateMap.get(auctionHouseCode).getAuctionStatus().getEncodedMessage());

						// 낙유찰 정보 전송 요청
						mAuctionServer.itemAdded(new RequestAuctionResult(auctionHouseCode,
								mAuctionStateMap.get(auctionHouseCode).getAuctionStatus().getEntryNum())
										.getEncodedMessage());
					}
				}

				mStartCountDownJobMap.get(auctionHouseCode).cancel(true);
			} else {
				if (mAuctionServer != null) {
					mAuctionServer.itemAdded(new AuctionCountDown(auctionHouseCode,
							mAuctionStateMap.get(auctionHouseCode).getAuctionCountDownStatus(),
							String.valueOf(mAuctionStateMap.get(auctionHouseCode).getAuctionCountDownTime()))
									.getEncodedMessage());

					mAuctionStateMap.get(auctionHouseCode).decreaseAuctionCountDownTime();
				}
			}
		}
	}

	/**
	 * 
	 * @ClassName Auctioneer.java
	 * @Description 다음 출품 정보 준비 딜레이 처리 클래스
	 * @author 박종식
	 * @since 2021.06.24
	 */
	private class AuctionNextEntryIntervalTimerJob implements Runnable {
		String auctionHouseCode;

		public AuctionNextEntryIntervalTimerJob(String auctionHouseCode) {
			this.auctionHouseCode = auctionHouseCode;
		}

		@Override
		public void run() {
			mLogger.debug("다음 출품 준비");

			readyEntryInfo(auctionHouseCode);

			mNextEntryIntervalJobMap.get(auctionHouseCode).cancel(true);
		}
	}

	private void writeLog(String auctionHouseCode) {
		// 로그 Write 처리
		Runnable createLogContentRunnable = new Runnable() {

			@Override
			public void run() {
				writeLogFile(auctionHouseCode, mCurrentEntryBiddingListMap.get(auctionHouseCode),
						mAuctionStateMap.get(auctionHouseCode), mIsAuctionPassMap.get(auctionHouseCode));
			}
		};

		mLogWriterService.execute(createLogContentRunnable);
	}

	/**
	 * 
	 * @MethodName resetAuctionData
	 * @Description 경매 출품 단위 데이터를 초기화 처리
	 *
	 */
	public void resetAuctionData(String auctionHouseCode) {
		if (mTopBiddingMap.containsKey(auctionHouseCode) && mTopBiddingMap.get(auctionHouseCode) != null) {
			mTopBiddingMap.get(auctionHouseCode).clear();
		} else {
			mTopBiddingMap.put(auctionHouseCode, new LinkedHashMap<Integer, String>());
		}

		if (mAuctionServer != null) {
			mAuctionServer.resetBiddingInfoMap(auctionHouseCode);
		}

		if (mCurrentEntryBiddingMap.containsKey(auctionHouseCode)
				&& mCurrentEntryBiddingMap.get(auctionHouseCode) != null) {
			mCurrentEntryBiddingMap.get(auctionHouseCode).clear();
		} else {
			mCurrentEntryBiddingMap.put(auctionHouseCode, new LinkedHashMap<String, Bidding>());
		}

		if (mCurrentEntryBiddingListMap.containsKey(auctionHouseCode)
				&& mCurrentEntryBiddingListMap.get(auctionHouseCode) != null) {
			mCurrentEntryBiddingListMap.get(auctionHouseCode).clear();
		} else {
			mCurrentEntryBiddingListMap.put(auctionHouseCode, new ArrayList<Bidding>());
		}

		if (mCurrentBidderMap.containsKey(auctionHouseCode) && mCurrentBidderMap.get(auctionHouseCode) != null) {
			mCurrentBidderMap.get(auctionHouseCode).clear();
		} else {
			mCurrentBidderMap.put(auctionHouseCode, new LinkedHashMap<String, Bidding>());
		}

		// mAuctionState 초기화 처리
		if (mAuctionStateMap.containsKey(auctionHouseCode) && mAuctionStateMap.get(auctionHouseCode) != null) {
			mAuctionStateMap.get(auctionHouseCode).onAuctionCountDownReady();

			mAuctionStateMap.get(auctionHouseCode).setRank1MemberNum("");
			mAuctionStateMap.get(auctionHouseCode).setRank1BidPrice("");
			mAuctionStateMap.get(auctionHouseCode).setRank1MemberChannel("");
			mAuctionStateMap.get(auctionHouseCode).setRank2MemberNum("");
			mAuctionStateMap.get(auctionHouseCode).setRank2BidPrice("");
			mAuctionStateMap.get(auctionHouseCode).setRank2MemberChannel("");
			mAuctionStateMap.get(auctionHouseCode).setRank3MemberNum("");
			mAuctionStateMap.get(auctionHouseCode).setRank3BidPrice("");
			mAuctionStateMap.get(auctionHouseCode).setRank3MemberChannel("");
			mAuctionStateMap.get(auctionHouseCode).setRank4MemberNum("");
			mAuctionStateMap.get(auctionHouseCode).setRank4BidPrice("");
			mAuctionStateMap.get(auctionHouseCode).setRank4MemberChannel("");
			mAuctionStateMap.get(auctionHouseCode).setRank5MemberNum("");
			mAuctionStateMap.get(auctionHouseCode).setRank5BidPrice("");
			mAuctionStateMap.get(auctionHouseCode).setRank5MemberChannel("");
		} else {
			mAuctionStateMap.put(auctionHouseCode, new AuctionState(auctionHouseCode, this));
		}
	}

	public void destroyAuctionServer() {
		Timer destroyTimer = new Timer();

		destroyTimer.schedule(new java.util.TimerTask() {

			@Override
			public void run() {
				if (mCheckSessionTimerJob != null) {
					mCheckSessionTimerJob.cancel(true);
					mCheckSessionTimerJob = null;
				}

				for (String key : mStartCountDownServiceMap.keySet()) {
					mStartCountDownServiceMap.get(key).shutdown();
				}

				mCheckSessionService.shutdownNow();
				mAuctionServer.stopServer();
			}
		}, AuctionShareSetting.AUCTION_SERVER_DESTROY_TIMER);
	}

	/**
	 * 
	 * @MethodName getCurrentTime
	 * @Description 현재 시간을 반환 처리(yyyyMMddHHmmssSSS)
	 *
	 * @param format yyyyMMddHHmmssSSS / yyyyMMddHHmmss / yyyyMMdd
	 * @return String
	 */
	private String getCurrentTime(String format) {
		String result = LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));

		return result;
	}

	private String getLogTimeFormat(String timeString) {
		String result;

		result = timeString.substring(0, 8) + " " + timeString.substring(8, 10) + ":" + timeString.substring(10, 12)
				+ ":" + timeString.substring(12, 14) + ":" + timeString.substring(14, 17);

		return result;
	}

	/**
	 * 
	 * @MethodName getAuctionCountDownStatus
	 * @Description 현재 경매 카운트 다운 상태 반환 처리
	 *
	 * @return String 현재 경매 카운트 다운 상태
	 */
	public String getAuctionCountDownStatus(String auctionHouseCode) {
		return mAuctionStateMap.get(auctionHouseCode).getAuctionCountDownStatus();
	}

	/**
	 * 
	 * @MethodName getAuctionCountDownTime
	 * @Description 현재 경매 카운트 다운 남은 시간 반환 처리
	 *
	 * @return String 현재 경매 카운트 다운 남은 시간
	 */
	public String getAuctionCountDownTime(String auctionHouseCode) {
		return String.valueOf(mAuctionStateMap.get(auctionHouseCode).getAuctionCountDownTime());
	}

	/**
	 * 
	 * @MethodName startCheckSessionTimer
	 * @Description 경매 유효 세션 확인 타이머
	 *
	 */
	public synchronized void startCheckSessionTimer() {
		if (mCheckSessionTimerJob != null) {
			mCheckSessionTimerJob.cancel(true);
		}

		mLogger.debug("경매 유효 세션 확인 타이머가 동작합니다.");

		mCheckSessionTimerJob = mCheckSessionService.scheduleAtFixedRate(new CheckSessionTimerJob(),
				AuctionServerSetting.BASE_DELAY_TIME, AuctionServerSetting.CHECK_SESSION_TIME, TimeUnit.MILLISECONDS);
	}

	/**
	 * 
	 * @ClassName Auctioneer.java
	 * @Description 경매 유효 세션 확인 타이머 처리 클래스
	 * @author 박종식
	 * @since 2020.03.04
	 */
	private class CheckSessionTimerJob implements Runnable {
		@Override
		public void run() {
			if (mAuctionServer != null) {
				mAuctionServer.itemAdded(new AuctionCheckSession().getEncodedMessage());
			}
		}
	}
}
