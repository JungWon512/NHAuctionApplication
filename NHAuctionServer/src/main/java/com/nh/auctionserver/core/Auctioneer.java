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
import com.nh.share.controller.models.StartAuction;
import com.nh.share.controller.models.ToastMessageRequest;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.ToastMessage;
import com.nh.share.setting.AuctionShareSetting;

import io.netty.channel.ChannelId;

public class Auctioneer {
	private final Logger mLogger = LoggerFactory.getLogger(Auctioneer.class);

	private final long CHECK_READY_AUCTION_TIME = 1000;
	public final String SYSTEM_USER_NAME = "SYSTEM";
	private final long AUCTION_FINISH_DELAY_TIME = 3000;

	private final int CHECK_TIME_COUNT = 3;

	private AuctionServer mAuctionServer;

	private String mAuctionCode;
	private String mAuctionRound;
	private String mAuctionLaneCode;
	private Map<ChannelId, ConnectionInfo> mConnectorInfoMap;

	private boolean mIsRequestAuctionStop = false; // 경매 자동 시작 정지 요청 Flag
	private boolean mIsRequestAuctionPass = false; // 경매 유찰 처리 요청 Flag
	private boolean mIsAuctionPass = false; // 경매 강제 유찰 처리 Flag

	private static AuctionConfig mAuctionConfig = new AuctionConfig(); // 경매 생성시 설정 정보
	private static AuctionState mAuctionState; // 경매 진행 상태, 현재 경매 상황 등 계속 변화하는 정보
	// 출품 목록
	private static AuctionEntryRepository mAuctionEntryRepository = new AuctionEntryRepository();

	private ScheduledExecutorService mStartCountDownService = Executors.newScheduledThreadPool(5);

	private ScheduledFuture<?> mStartCountDownJob; // 경매 시작 카운트 다운 시간 처리 Job
	private ScheduledFuture<?> mDelayTimerJob; // 경매 종료 처리 Job

	private ScheduledExecutorService mCheckSessionService = Executors.newScheduledThreadPool(5);
	private ScheduledFuture<?> mCheckSessionTimerJob; // 유효 세션 확인 Job

	private ExecutorService mLogWriterService = Executors.newCachedThreadPool();

	// 동시에 들어온 입찰 중 최초 입찰 정보만 기록되는 맵, 각 가격당 한명의 입찰자만 기록되어야 하므로 Map을 사용함.
	private Map<Integer, String> mTopBiddingMap = new HashMap<>();

	/*
	 * 최초 입찰을 제외한 나머지 입찰이 기록되는 리스트 순서대로 담기만 할 것이므로 List를 사용함. 통신 속도에 따라 가격 순서가 뒤바뀔 수
	 * 있으니 경매가 끝난 후 우선순위 계산을 다시 해야한다.
	 */

	private LinkedHashMap<String, Bidding> mCurrentEntryBiddingMap = new LinkedHashMap<String, Bidding>();

	private ArrayList<Bidding> mCurrentEntryBiddingList = new ArrayList<Bidding>();

	// 동일가 응찰자 정보 수집 Map
	private Map<String, Bidding> mCurrentBidderMap = new HashMap<>();

	private ArrayList<String> mLogStringList = new ArrayList<String>();

	// 마지막 출품번호 여부
	private boolean mIsLastEntry = false;

	public Auctioneer(AuctionServer auctionServer) {
		this.mAuctionServer = auctionServer;
		initAuction();
	}

	private void initAuction() {
		if (mAuctionState == null) {
			mAuctionState = new AuctionState(this);
		}

		// 경매 유효 접속 세션 확인 처리 시작
		startCheckSessionTimer();
	}

	/**
	 * 
	 * @MethodName getCurrentAuctionStatus
	 * @Description 현재 경매 상태 반환 처리
	 *
	 * @return AuctionState 현재 경매 상태
	 */
	public String getCurrentAuctionStatus() {
		return mAuctionState.getAuctionState();
	}

	/**
	 * 
	 * @MethodName startAuction
	 * @Description 경매 진행 시작 처리
	 *
	 */
	public synchronized void startAuction() {
		if (mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_READY)) {
			mLogger.debug("경매가 시작되었습니다.");

			mAuctionState.setAuctionState(GlobalDefineCode.AUCTION_STATUS_START);

			if (mAuctionServer != null) {
				mAuctionServer.itemAdded(mAuctionState.getAuctionStatus().getEncodedMessage());
			}

//			mAuctionEntryRepository.setInitialEntryList(response.getResult());
//			mAuctionState.setEntryInformationLoad();
//			mAuctionState.setCheckEntryInformation();

		}
	}

	/**
	 * 
	 * @MethodName stopAuction
	 * @Description 경매 진행 정지 처리
	 *
	 */
	public synchronized void stopAuction() {
		mIsRequestAuctionStop = true;
	}

	/**
	 * 
	 * @MethodName passAuction
	 * @Description 경매 유찰 처리
	 *
	 */
	public synchronized void passAuction() {
		mIsRequestAuctionPass = true;
	}

	public synchronized void broadcastToastMessage(ToastMessageRequest requestToastMessage) {
		ToastMessage toastMessage = new ToastMessage(requestToastMessage.getMessage());

		if (mAuctionServer != null) {
			mAuctionServer.itemAdded(toastMessage.getEncodedMessage());
		}
	}

	public void startAuctionCountDown() {
		if (mStartCountDownJob != null) {
			mStartCountDownJob.cancel(true);
		}

		mAuctionState.onAuctionCountDown();

		mStartCountDownJob = mStartCountDownService.scheduleAtFixedRate(new AuctionCountDownTimerJob(),
				AuctionServerSetting.COUNT_DOWN_DELAY_TIME, AuctionServerSetting.COUNT_DOWN_DELAY_TIME,
				TimeUnit.MILLISECONDS);
	}

	/**
	 * 
	 * @MethodName readyEntryInfo
	 * @Description 경매 출품 정보 설정 처리
	 *
	 */
	public synchronized void readyEntryInfo() {
		// 경매 응찰 정보 Reset
		resetAuctionData();

		EntryInfo entryInfo = mAuctionEntryRepository.popEntry();

		if (mAuctionEntryRepository.getTotalCount() >= 1) {
			mAuctionState.setCurrentEntryInfo(entryInfo);
		} else {
			// 마지막 차량 일 경우 수행 처리 로직 개발 필요
		}

		mAuctionState.setCurrentBidderCount(String.valueOf(mCurrentBidderMap.size()));

		mLogger.debug(entryInfo.getEntryNum() + "번 출품 상품이 경매 준비되었습니다.");
		mAuctionState.onReady();
	}

	/**
	 * 
	 * @MethodName getEntryInfo
	 * @Description 출품번호를 통해 요청된 출품 정보 반환 처리
	 *
	 * @param entryNum
	 * @return
	 */
	public EntryInfo getEntryInfo(String entryNum) {
		EntryInfo entryInfo;

		entryInfo = mAuctionEntryRepository.getEntryInfo(entryNum);

		return entryInfo;
	}

	/**
	 * 
	 * @MethodName getAuctionState
	 * @Description 현재 경매 상태 정보 반환 처리
	 *
	 * @return AuctionState 경매 상태 정보
	 */
	public AuctionState getAuctionState() {
		return mAuctionState;
	}

	/**
	 * 
	 * @MethodName setAuctionStatus
	 * @Description 현재 경매 상태 정보 적용 처리
	 *
	 * @return setAuctionStatus 경매 상태 정보
	 */
	public void setAuctionStatus(AuctionStatus auctionStatus) {
		mAuctionState.setAuctionStatus(auctionStatus);
	}

	/**
	 * 유효 응찰 정보 수집 처리
	 * 
	 * @param bidding 유효한 응찰 정보
	 */
	public synchronized void setBidding(Bidding bidding) {
		if (bidding != null) {
			// 현재 응찰중인 출품번호인지 확인.
			if (bidding.getEntryNum().equals(mAuctionState.getEntryNum())) {
				if (bidding.getPriceInt() == Integer.valueOf(mAuctionState.getStartPrice())) {

					// 현재 가격 기준 모든 응찰 정보 수집
					if (bidding != null) {
						setAllBidding(bidding);
					}
				} else {
					mLogger.debug("=========================================================");
					mLogger.debug("응찰 가능 가격이 아닙니다. 본 응찰 정보는 인정되지 않았습니다.");
					mLogger.debug("회원번호(채널) : " + bidding.getUserNo() + "(" + bidding.getChannel() + ")");
					mLogger.debug("응찰 출품번호 : " + mAuctionState.getEntryNum());
					mLogger.debug("응찰 가격 : " + bidding.getPrice());
					mLogger.debug("현재 가격 : " + mAuctionState.getStartPrice());
					mLogger.debug("=========================================================");
				}

			} else {
				mLogger.debug("=========================================================");
				mLogger.debug("응찰 가능 출품번호가 아닙니다. 본 응찰 정보는 인정되지 않았습니다.");
				mLogger.debug("회원번호(채널) : " + bidding.getUserNo() + "(" + bidding.getChannel() + ")");
				mLogger.debug("응찰 출품번호 : " + bidding.getEntryNum());
				mLogger.debug("현재 출품번호 : " + mAuctionState.getEntryNum());
				mLogger.debug("=========================================================");
			}
		}
	}

	/**
	 * 현재 가격 기준 모든 응찰 정보 수집 중복 응찰 정보는 수집 대상에서 제외 처리
	 * 
	 * @param bidding
	 */
	public synchronized void setAllBidding(Bidding bidding) {
		// 동일가 응찰자 정보 수집
		if (!mCurrentBidderMap.containsKey(bidding.getUserNo())
				|| (mCurrentBidderMap.containsKey(bidding.getUserNo()) && bidding.getAbsentee().equals("Y"))) {
			mCurrentBidderMap.put(bidding.getUserNo(), bidding);
			mAuctionState.setCurrentBidderCount(String.valueOf(mCurrentBidderMap.size()));

			if (mCurrentEntryBiddingMap != null) {
				if (!mCurrentEntryBiddingMap.containsKey(bidding.getUserNo())) {
					mCurrentEntryBiddingMap.put(bidding.getUserNo(), bidding);
				}
			}
		}
	}

	private synchronized void writeLogFile(List<Bidding> biddingList, AuctionState auctionState,
			boolean isAuctionPass) {
		String currentTime = getCurrentTime("yyyyMMdd");

		String LOG_DIRECTORY = AuctionServerSetting.AUCTION_LOG_FILE_PATH + currentTime;
		String LOG_FILE_NAME = getCurrentTime("yyyyMMdd") + "-" + mAuctionState.getEntryNum()
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
		@Override
		public void run() {
			mLogger.debug("경매 카운트 다운 시간 : " + mAuctionState.getAuctionCountDownTime());

			if (mAuctionState.getAuctionCountDownTime() < 0) {
				if (mAuctionServer != null) {
					mAuctionState.onAuctionCountDownCompleted();

					mAuctionServer.itemAdded(new AuctionCountDown(mAuctionState.getAuctionCountDownStatus(),
							String.valueOf(mAuctionState.getAuctionCountDownTime())).getEncodedMessage());

					mAuctionServer.itemAdded(new StartAuction(mAuctionState.getEntryNum()).getEncodedMessage());
				}

				mStartCountDownJob.cancel(true);
			} else {
				if (mAuctionServer != null) {
					mAuctionServer.itemAdded(new AuctionCountDown(mAuctionState.getAuctionCountDownStatus(),
							String.valueOf(mAuctionState.getAuctionCountDownTime())).getEncodedMessage());

					mAuctionState.decreaseAuctionCountDownTime();
				}
			}
		}
	}

	private void writeLog() {
		// 로그 Write 처리
		Runnable createLogContentRunnable = new Runnable() {

			@Override
			public void run() {
				writeLogFile(mCurrentEntryBiddingList, mAuctionState, mIsAuctionPass);
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
	public void resetAuctionData() {
		if (mTopBiddingMap != null) {
			mTopBiddingMap.clear();
		}

		if (mAuctionServer != null) {
			mAuctionServer.resetBiddingInfoMap();
		}

		if (mCurrentEntryBiddingMap != null) {
			mCurrentEntryBiddingMap.clear();
		}

		if (mCurrentEntryBiddingList != null) {
			mCurrentEntryBiddingList.clear();
		}

		if (mCurrentBidderMap != null) {
			mCurrentBidderMap.clear();
		}

		// mAuctionState 초기화 처리
		if (mAuctionState != null) {
			mAuctionState.setRank1MemberNum("");
			mAuctionState.setRank1BidPrice("");
			mAuctionState.setRank1MemberChannel("");
			mAuctionState.setRank2MemberNum("");
			mAuctionState.setRank2BidPrice("");
			mAuctionState.setRank2MemberChannel("");
			mAuctionState.setRank3MemberNum("");
			mAuctionState.setRank3BidPrice("");
			mAuctionState.setRank3MemberChannel("");
			mAuctionState.setRank4MemberNum("");
			mAuctionState.setRank4BidPrice("");
			mAuctionState.setRank4MemberChannel("");
			mAuctionState.setRank5MemberNum("");
			mAuctionState.setRank5BidPrice("");
			mAuctionState.setRank5MemberChannel("");
		}
	}

	/**
	 * 
	 * @MethodName getKoreanAuctionStatus
	 * @Description 경매 상태 코드에 대한 한글 상태명 반환 처리
	 *
	 * @param auctionStatus
	 * @return String 경매 한글 상태명
	 */
	private String getKoreanAuctionStatus(String auctionStatus) {
		String state = null;

		if (auctionStatus.equals(GlobalDefineCode.AUCTION_STATUS_NONE)) {
			state = "NONE";
		} else if (auctionStatus.equals(GlobalDefineCode.AUCTION_STATUS_READY)) {
			state = "준비";
		} else if (auctionStatus.equals(GlobalDefineCode.AUCTION_STATUS_START)) {
			state = "시작";
		} else if (auctionStatus.equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)) {
			state = "진행";
		} else if (auctionStatus.equals(GlobalDefineCode.AUCTION_STATUS_COMPETITIVE)) {
			state = "경쟁";
		} else if (auctionStatus.equals(GlobalDefineCode.AUCTION_STATUS_SUCCESS)) {
			state = "낙찰";
		} else if (auctionStatus.equals(GlobalDefineCode.AUCTION_STATUS_FAIL)) {
			state = "유찰";
		} else if (auctionStatus.equals(GlobalDefineCode.AUCTION_STATUS_STOP)) {
			state = "정지";
		} else if (auctionStatus.equals(GlobalDefineCode.AUCTION_STATUS_COMPLETED)) {
			state = "완료";
		} else if (auctionStatus.equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {
			state = "종료";
		}

		return state;
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

				mStartCountDownService.shutdownNow();
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
	public String getAuctionCountDownStatus() {
		return mAuctionState.getAuctionCountDownStatus();
	}

	/**
	 * 
	 * @MethodName getAuctionCountDownTime
	 * @Description 현재 경매 카운트 다운 남은 시간 반환 처리
	 *
	 * @return String 현재 경매 카운트 다운 남은 시간
	 */
	public String getAuctionCountDownTime() {
		return String.valueOf(mAuctionState.getAuctionCountDownTime());
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
