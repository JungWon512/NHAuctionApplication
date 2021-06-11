package com.nh.auctionserver.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.model.AbsenteeAuctionBidInfoResult;
import com.nh.share.api.model.AuctionEntryInformationResult;
import com.nh.share.api.model.AuctionInterestEntryFavoriteUserInfo;
import com.nh.share.api.model.AuctionResult;
import com.nh.share.api.model.AuctionSettingsInformationResult;
import com.nh.share.api.request.ActionRequestAbsenteeAuctionBidInfo;
import com.nh.share.api.request.ActionRequestAuctionEntryInformation;
import com.nh.share.api.request.ActionRequestAuctionInterestEntryInfo;
import com.nh.share.api.request.ActionRequestAuctionSettingsInformation;
import com.nh.share.api.request.ActionRequestSendSmsAuctionResult;
import com.nh.share.api.request.ActionRequestTransmissionAuctionResult;
import com.nh.share.api.request.ActionRequestUpdateAuctionStatus;
import com.nh.share.api.response.ResponseAbsenteeAuctionBidInfo;
import com.nh.share.api.response.ResponseAuctionEntryInformation;
import com.nh.share.api.response.ResponseAuctionInterestEntryInfo;
import com.nh.share.api.response.ResponseAuctionSettingsInformation;
import com.nh.share.api.response.ResponseSendSmsAuctionServerResult;
import com.nh.share.api.response.ResponseTransmissionAuctionResult;
import com.nh.share.api.response.ResponseUpdateAuctionStatus;
import com.nh.share.bidder.models.Bidding;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.controller.models.EditSetting;
import com.nh.share.controller.models.StartAuction;
import com.nh.share.controller.models.ToastMessageRequest;
import com.nh.share.server.models.AbsenteeUserInfo;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.AuctionStatus;
import com.nh.share.server.models.CurrentSetting;
import com.nh.share.server.models.ResponseEntryInfo;
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
	// 부재자 입찰 정보
	private static HashMap<String, ArrayList<AbsenteeAuctionBidInfoResult>> mAbsenteeBiddingMap = new HashMap<>();
	// 관심 차량
	private static HashMap<String, List<AuctionInterestEntryFavoriteUserInfo>> mFavoriteCarInfoMap = new HashMap<String, List<AuctionInterestEntryFavoriteUserInfo>>();

	private LinkedHashMap<String, String> mCurrentEntryAbsenteeBiddingMap = new LinkedHashMap<String, String>();

	private ScheduledExecutorService mStartCountDownService = Executors.newScheduledThreadPool(5);
	private ScheduledExecutorService mRemainTimeService = Executors.newScheduledThreadPool(5);
	private ScheduledExecutorService mBiddingTimeService = Executors.newScheduledThreadPool(20);
	private ScheduledExecutorService mNextEntryIntervalTimeService = Executors.newScheduledThreadPool(5);
	private ScheduledExecutorService mReadyAuctionTimeService = Executors.newScheduledThreadPool(5);
	private ScheduledExecutorService mDelayTimerService = Executors.newScheduledThreadPool(5);
	private ScheduledExecutorService mCheckTimerService = Executors.newScheduledThreadPool(5);

	private ScheduledFuture<?> mStartCountDownJob; // 경매 시작 카운트 다운 시간 처리 Job
	private ScheduledFuture<?> mRemainTimeJob; // 경매 진행 시간 처리 Job
	private ScheduledFuture<?> mBiddingTimeJob; // 경매 응찰 정보 수집 시간 처리 Job
	private ScheduledFuture<?> mNextEntryIntervalTimeJob; // 다음 출품 진행 대기 시간 처리 Job
	private ScheduledFuture<?> mReadyAuctionTimeJob; // 경매 초기화 진행 타이머 Job
	private ScheduledFuture<?> mDelayTimerJob; // 경매 종료 처리 Job

	private ScheduledFuture<?> mCheckTimerJob; // 경매 타이머 및 가격 수집 타이머 정상 동작 확인 Job

	private ScheduledExecutorService mCheckSessionService = Executors.newScheduledThreadPool(5);
	private ScheduledFuture<?> mCheckSessionTimerJob; // 유효 세션 확인 Job

	private ExecutorService mLogWriterService = Executors.newCachedThreadPool();

	// 동시에 들어온 입찰 중 최초 입찰 정보만 기록되는 맵, 각 가격당 한명의 입찰자만 기록되어야 하므로 Map을 사용함.
	private Map<Integer, String> mTopBiddingMap = new HashMap<>();

	// Bidding Time Job 시작 로그 Flag
	private boolean mFlagStartBiddingTimeJob = false;

	/// Bidding Timer 오류 검출 Flag
	private boolean mFlagFail = false;

	/*
	 * 최초 입찰을 제외한 나머지 입찰이 기록되는 리스트 순서대로 담기만 할 것이므로 List를 사용함. 통신 속도에 따라 가격 순서가 뒤바뀔 수
	 * 있으니 경매가 끝난 후 우선순위 계산을 다시 해야한다.
	 */

	// private List<Bidding> mCurrentEntryBiddingList = new ArrayList<Bidding>();
	private LinkedHashMap<String, Bidding> mCurrentEntryBiddingMap = new LinkedHashMap<String, Bidding>();

	private ArrayList<Bidding> mCurrentEntryBiddingList = new ArrayList<Bidding>();

	// 동일가 응찰자 정보 수집 Map
	private Map<String, Bidding> mCurrentBidderMap = new HashMap<>();

	// 자동 상승 응찰을 시도한 횟수
	private int mAutoBiddingCount = 0;

	// 낙,유찰 판단 기록 시간 저장 변수
	private String mResultRecordTime = null;

	// 경매 서버 상태 저장 변수
	private String mAuctionServerStatus = null;

	// 낙,유찰 결과 전송 처리 실패 저장 리스트
	private HashMap<String, AuctionResult> mAuctionResultTransmissionFailMap = new HashMap<String, AuctionResult>();

	private ArrayList<String> mLogStringList = new ArrayList<String>();

	// 부재자 추가 응찰 필요 여부
	private boolean mIsNeedAbsenteeBidding = false;

	// 마지막 출품번호 여부
	private boolean mIsLastEntry = false;

	// 부재자 우선 입찰 상태 여부
	private boolean mIsPriorityAbsenteeBidding = false;

	// 직전 Auction Time 저장 변수(Auction Time 유효성 검사 시 필요)
	private long mPrevAuctionTime = 0;
	private int mCheckPrevAuctionTimeCount = 0;

	// 직전 Bidding Time 저장 변수(Bidding Time 유효성 검사 시 필요)
	private long mPrevLocalTime = 0;
	private long mPrevBiddingTime = 0;
	private int mCheckPrevBiddingTimeCount = 0;

	private long mCheckAuctionTimerCount = 0;

	private boolean mIsActiveBiddingTime = false;

	public Auctioneer(AuctionServer auctionServer, String auctionCode, String auctionRound, String auctionLaneCode) {
		this.mAuctionServer = auctionServer;
		this.mAuctionCode = auctionCode;
		this.mAuctionRound = auctionRound;
		this.mAuctionLaneCode = auctionLaneCode;
		initAuction();
	}

	private void initAuction() {
		// 경매 출품 정보 조회
		ActionRuler.getInstance().addAction(new ActionRequestAuctionEntryInformation(mAuctionCode, mAuctionRound,
				mAuctionLaneCode, null, mActionResultAuctionEntryInformation));
		ActionRuler.getInstance().runNext();

		// 경매 설정 정보 조회
		ActionRuler.getInstance().addAction(new ActionRequestAuctionSettingsInformation(mAuctionCode, mAuctionRound,
				mAuctionLaneCode, mActionResultAuctionSettingsInformation));
		ActionRuler.getInstance().runNext();

		// 관심 차량 정보 조회
		ActionRuler.getInstance().addAction(new ActionRequestAuctionInterestEntryInfo(mAuctionCode, mAuctionRound,
				mAuctionLaneCode, null, mActionResultInterestEntryInfo));
		ActionRuler.getInstance().runNext();

		// 부재자 입찰 정보 조회
		ActionRuler.getInstance().addAction(new ActionRequestAbsenteeAuctionBidInfo(mAuctionCode, mAuctionRound,
				mAuctionLaneCode, null, mActionResultAbsenteeAuctionBidInfo));
		ActionRuler.getInstance().runNext();

		if (mAuctionState == null) {
			mAuctionState = new AuctionState(this);
		}

		// 오류 검출 타이머 동작
		startCheckAuctionTimer();

		// 경매 유효 접속 세션 확인 처리 시작
		startCheckSessionTimer();

		// 경매 초기화 상태 확인 시작 처리
		startReadyAuctionTimer();
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

			setAuctionStartStatus(true);

			startAuctionTimer();
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
		setAuctionStartStatus(false);
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

	/**
	 * 
	 * @MethodName setAuctionStartStatus
	 * @Description 경매 시작 / 정지 상태 변경 처리
	 *
	 * @param flagStart
	 */
	public synchronized void setAuctionStartStatus(boolean flagStart) {
		String auctionStartStatus;
		String auctionAutoMode;

		if (flagStart) {
			AuctionServerSetting.AUCTION_START_STATUS = true;
		} else {
			AuctionServerSetting.AUCTION_START_STATUS = false;
		}

		if (AuctionServerSetting.AUCTION_START_STATUS) {
			auctionStartStatus = "Y";
		} else {
			auctionStartStatus = "N";
		}

		if (AuctionServerSetting.AUCTION_AUTO_MODE) {
			auctionAutoMode = "Y";
		} else {
			auctionAutoMode = "N";
		}

		if (mAuctionServer != null) {
			mAuctionServer.itemAdded(new CurrentSetting(String.valueOf(AuctionServerSetting.AUCTION_BASE_PRICE),
					String.valueOf(AuctionServerSetting.AUCTION_MORE_RISING_PRICE),
					String.valueOf(AuctionServerSetting.AUCTION_BELOW_RISING_PRICE),
					String.valueOf(AuctionServerSetting.AUCTION_MAX_RISING_PRICE),
					String.valueOf(AuctionServerSetting.AUCTION_TIME),
					String.valueOf(AuctionServerSetting.AUCTION_DETERMINE_TIME),
					String.valueOf(AuctionServerSetting.AUCTION_NEXT_DELAY_TIME),
					String.valueOf(AuctionServerSetting.AUCTION_AUTO_RISE_COUNT), auctionStartStatus, auctionAutoMode)
							.getEncodedMessage());
		}
	}

	/**
	 * 
	 * @MethodName setAutoMode
	 * @Description 경매 진행 모드 변경 처리
	 *
	 * @param enable
	 */
	public synchronized void setAutoMode(boolean enable) {
		if (enable) {
			AuctionServerSetting.AUCTION_AUTO_MODE = true;
		} else {
			AuctionServerSetting.AUCTION_AUTO_MODE = false;
		}
	}

	public synchronized void broadcastToastMessage(ToastMessageRequest requestToastMessage) {
		ToastMessage toastMessage = new ToastMessage(requestToastMessage.getMessage());

		if (mAuctionServer != null) {
			mAuctionServer.itemAdded(toastMessage.getEncodedMessage());
		}
	}

	/**
	 * 
	 * @MethodName startAuctionTimer
	 * @Description 경매 진행 타이머 시작 처리
	 *
	 */
	public synchronized void startAuctionTimer() {
		if (mRemainTimeJob != null) {
			mRemainTimeJob.cancel(true);
		}

		mLogger.debug("경매 진행 타이머가 동작을 시작합니다.");

		// 경매 서버 상태를 진행 상태로 업데이트 요청
		if (mAuctionServerStatus.equals(GlobalDefineCode.AUCTION_API_STATUS_STANDBY)) {
			requestUpdateAuctionStatus(GlobalDefineCode.AUCTION_API_STATUS_START);
		}

		// 경매 진행 타이머 초기화
		mAuctionState.resetRemainTime();

		// 자동 상승 설정 상태일 경우 해당 횟수만큼 상승 처리(true : 자동 상승 처리(일반인 응찰불가) / false : 응찰 가능)
		if (mAuctionState.onStart()) {
			if (!mAuctionState.getCurrentEntryInfo().getFlagCancelEntry().equals("Y")) {
				runAutoBidding();
			}
		}

		// 경매 출품 취소 처리 확인(출품 취소 혹은 시작가가 희망가 보다 낮거나 같을 경우 유찰 처리)
		if (mAuctionState.getCurrentEntryInfo().getFlagCancelEntry().equals("Y")
				|| Integer.valueOf(mAuctionState.getCurrentEntryInfo().getAuctionStartPrice()) >= Integer
						.valueOf(mAuctionState.getCurrentEntryInfo().getAuctionHopePrice())) {
			if (mIsRequestAuctionPass != true) {
				mIsRequestAuctionPass = true;
			}
		}

		try {
			mRemainTimeJob = mRemainTimeService.scheduleAtFixedRate(new AuctionTimerJob(),
					AuctionServerSetting.BASE_DELAY_TIME, AuctionServerSetting.REMAIN_CHECK_DELAY_TIME,
					TimeUnit.MILLISECONDS);
		} catch (Throwable t) {
			t.printStackTrace();
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
	 * @MethodName startCheckAuctionTimer
	 * @Description 경매 타이머 및 응찰 수집 타이머 정상 동작 확인 처리
	 *
	 */
	public synchronized void startCheckAuctionTimer() {
		try {
			mCheckTimerJob = mCheckTimerService.scheduleAtFixedRate(new CheckAuctionTimerJob(),
					AuctionServerSetting.CHECK_AUCTION_TIME, AuctionServerSetting.CHECK_AUCTION_DELAY_TIME,
					TimeUnit.MILLISECONDS);
		} catch (Throwable t) {
			t.printStackTrace();

			mLogger.debug("현재 경매 관련 타이머 동작 시 예외 상황이 발생하였습니다.");
			mLogger.debug("예외 상황 조치로 startCheckAuctionTimer을 재요청 합니다.");
			if (mCheckTimerJob != null) {
				mCheckTimerJob.cancel(true);
			}

			mCheckTimerJob = mCheckTimerService.scheduleAtFixedRate(new CheckAuctionTimerJob(),
					AuctionServerSetting.CHECK_AUCTION_TIME, AuctionServerSetting.CHECK_AUCTION_DELAY_TIME,
					TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * 
	 * @MethodName startBiddingTimer
	 * @Description 경매 응찰 정보 수집 타이머 시작 처리
	 *
	 */
	public synchronized void startBiddingTimer() {
		if (mBiddingTimeJob != null) {
			mBiddingTimeJob.cancel(true);
		}

		// 경매 진행 타이머 초기화
		mAuctionState.resetBiddingRemainTime();

		if (!mFlagStartBiddingTimeJob) {
			mFlagStartBiddingTimeJob = true;
		}

		// Bidding Timer 오류 검출 Flag
		if (!mFlagFail) {
			mFlagFail = true;
		}

		try {
			mIsActiveBiddingTime = true;

			mBiddingTimeJob = mBiddingTimeService.scheduleAtFixedRate(new BiddingTimerJob(),
					AuctionServerSetting.BASE_DELAY_TIME, AuctionServerSetting.REMAIN_CHECK_DELAY_TIME,
					TimeUnit.MILLISECONDS);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * 
	 * @MethodName startNextIntervalTimer
	 * @Description 다음 출품 경매 대기 시간 처리
	 *
	 */
	public synchronized void startNextIntervalTimer() {
		mNextEntryIntervalTimeJob = mNextEntryIntervalTimeService.schedule(new NextEntryIntervalTimerJob(),
				AuctionServerSetting.AUCTION_NEXT_DELAY_TIME, TimeUnit.MILLISECONDS);
	}

	/**
	 * 
	 * @MethodName startReadyAuctionTimer
	 * @Description 경매 초기화 타이머 처리
	 *
	 */
	private void startReadyAuctionTimer() {
		mReadyAuctionTimeJob = mReadyAuctionTimeService.scheduleAtFixedRate(new ReadyAuctionTimerJob(),
				CHECK_READY_AUCTION_TIME, CHECK_READY_AUCTION_TIME, TimeUnit.MILLISECONDS);
	}

	/**
	 * 
	 * @MethodName startDelayTimer
	 * @Description 경매 종료 상태 전송 딜레이 처리
	 *
	 */
	private void startDelayTimer() {
		mDelayTimerJob = mDelayTimerService.schedule(new DelayTimerJob(), AUCTION_FINISH_DELAY_TIME,
				TimeUnit.MILLISECONDS);
	}

	/**
	 * 
	 * @ClassName Auctioneer.java
	 * @Description 경매 초기화 상태 확인 처리
	 * @author 박종식
	 * @since 2019.12.05
	 */
	private class ReadyAuctionTimerJob implements Runnable {
		@Override
		public void run() {
			if (mAuctionState.isCheckAuctionData()) {
				// 경매 초기 설정 완료 상태 확인
				if (mAuctionState.isAuctionDataLoadingCompleted()) {
					if (mAuctionEntryRepository.getTotalCount() > 0) {
						mAuctionState.onAuctionCountDownReady();

						requestUpdateAuctionStatus(GlobalDefineCode.AUCTION_API_STATUS_STANDBY);
						readyCarInfo();

						// 경매 상태가 정상인 경우는 관리자에게 메시지 전송 불필요
						// requestCreateAuctionServerResult(true);
					} else {
						mLogger.debug("Auction Entry Data is 0 size!");
						mLogger.debug("Auction Server Destroy!");
						destroyAuctionServer();
					}
				} else {
					mLogger.debug("Fail Initialize for Auction Data");
					mLogger.debug("Auction Server Destroy!");
					destroyAuctionServer();
				}

				if (mReadyAuctionTimeJob != null) {
					mReadyAuctionTimeJob.cancel(true);
				}
			}
		}
	}

	/**
	 * 
	 * @MethodName readyCarInfo
	 * @Description 경매 출품 정보 설정 처리
	 *
	 */
	public synchronized void readyCarInfo() {
		// 경매 응찰 정보 Reset
		resetAuctionData();

		AuctionEntryInformationResult entryInfo = mAuctionEntryRepository.popEntry();

		// 부재자 입찰 정보 설정
		if (mAbsenteeBiddingMap.size() > 0) {
			if (mAbsenteeBiddingMap.containsKey(entryInfo.getAuctionEntryNum())) {
				if (mAbsenteeBiddingMap.get(entryInfo.getAuctionEntryNum()) != null) {
					if (mAbsenteeBiddingMap.get(entryInfo.getAuctionEntryNum()).size() > 0) {

						mCurrentEntryAbsenteeBiddingMap.clear();

						for (int i = 0; i < mAbsenteeBiddingMap.get(entryInfo.getAuctionEntryNum()).size(); i++) {
							mCurrentEntryAbsenteeBiddingMap.put(
									mAbsenteeBiddingMap.get(entryInfo.getAuctionEntryNum()).get(i)
											.getAuctionMemberNum(),
									mAbsenteeBiddingMap.get(entryInfo.getAuctionEntryNum()).get(i).getBidPrice());
						}

						entryInfo.setAbsenteePrice(
								mAbsenteeBiddingMap.get(entryInfo.getAuctionEntryNum()).get(0).getBidPrice());
					}
				}
			}
		}

		if (mAuctionEntryRepository.getTotalCount() >= 1) {
			mAuctionState.setCurrentEntryInfo(entryInfo, false);
		} else {
			mAuctionState.setCurrentEntryInfo(entryInfo, true);
		}

		mAuctionState.setCurrentBidderCount(String.valueOf(mCurrentBidderMap.size()));

		mLogger.debug(entryInfo.getAuctionEntryNum() + "번 출품 차량의 경매 준비가 되었습니다.");
		mAuctionState.onReady();
	}

	/**
	 * 
	 * @MethodName getEntryCarInfo
	 * @Description 시퀀스번호를 통해 요청된 출품 정보 반환 처리
	 *
	 * @param entrySeqNum
	 * @return
	 */
	public AuctionEntryInformationResult getEntryCarInfo(String entrySeqNum) {
		AuctionEntryInformationResult entryCarInfo;

		entryCarInfo = mAuctionEntryRepository.getEntryInfo(entrySeqNum);

		if (entryCarInfo != null) {
			// 부재자 입찰 정보 설정
			if (mAbsenteeBiddingMap.containsKey(entryCarInfo.getAuctionEntryNum())) {
				if (mAbsenteeBiddingMap.get(entryCarInfo.getAuctionEntryNum()) != null) {
					if (mAbsenteeBiddingMap.get(entryCarInfo.getAuctionEntryNum()).size() > 0) {
						entryCarInfo.setAbsenteePrice(
								mAbsenteeBiddingMap.get(entryCarInfo.getAuctionEntryNum()).get(0).getBidPrice());
					}
				}
			}
		}

		return entryCarInfo;
	}

	/**
	 * 
	 * @MethodName getEntryCarInfo
	 * @Description carInfo를 통해 요청된 출품 순번 정보 반환 처리
	 *
	 * @param entrySeqNum
	 * @return
	 */
	public AuctionEntryInformationResult getCarInfo(String entrySeqNum) {
		AuctionEntryInformationResult entryCarInfo;

		entryCarInfo = getEntryCarInfo(entrySeqNum);

		if (entryCarInfo != null) {
			// 부재자 입찰 정보 설정
			if (mAbsenteeBiddingMap.containsKey(entryCarInfo.getAuctionEntryNum())) {
				if (mAbsenteeBiddingMap.get(entryCarInfo.getAuctionEntryNum()) != null) {
					if (mAbsenteeBiddingMap.get(entryCarInfo.getAuctionEntryNum()).size() > 0) {
						entryCarInfo.setAbsenteePrice(
								mAbsenteeBiddingMap.get(entryCarInfo.getAuctionEntryNum()).get(0).getBidPrice());
					}
				}
			}
		}

		return mAuctionState.getResponseCarInfo(entryCarInfo);
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
		// 응찰 가격 점검
		// - 응찰 된 가격이 현재가격 보다 낮을 경우 인정되지 않음(비정상)
		// - 응찰 된 가격이 다음 응찰가격 보다 큰 경우 인정되지 않음(비정상)
		if (bidding != null) {
			// 현재 응찰중인 출품번호인지 확인.
			if (bidding.getEntryNum().equals(mAuctionState.getEntryNum())) {
				// 부재자 응찰 필요 여부 확인(타인 경쟁 상황)
				if (bidding.getAbsentee().equals("N") && !bidding.getUserNo().equals(SYSTEM_USER_NAME)
						&& bidding.getPriceInt() == Integer.valueOf(mAuctionState.getNextPrice())) {
					if (isRunAbseteeBidding()) {
						absenteeBidding(bidding);
					}
				}

				mLogger.debug("부재자 응찰 처리 완료 여부 : " + mIsPriorityAbsenteeBidding);

				if (bidding.getPriceInt() == Integer.valueOf(mAuctionState.getNextPrice())) {
					if ((mTopBiddingMap.containsKey(bidding.getPriceInt()) == false
							&& bidding.getAbsentee().equals("Y"))
							|| (mTopBiddingMap.containsKey(bidding.getPriceInt()) == false
									&& bidding.getAbsentee().equals("N") && !mIsPriorityAbsenteeBidding)) {
						if (mTopBiddingMap.isEmpty()) {
							mTopBiddingMap.put(bidding.getPriceInt(), bidding.getUserNo());

							mAuctionState.resetRemainTime();

							startBiddingTimer();
						} else {
							// 자동 상승 구간 시스템 응찰 가격 반영
							if ((mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_SLOWDOWN)
									&& bidding.getUserNo().equals(SYSTEM_USER_NAME))
									|| (!mAuctionState.getAuctionState()
											.equals(GlobalDefineCode.AUCTION_STATUS_SLOWDOWN)
											&& Integer.valueOf(mAuctionState.getCurrentPrice()) < Integer.valueOf(
													mAuctionState.getCurrentEntryInfo().getAuctionHopePrice()))) {
								mTopBiddingMap.put(bidding.getPriceInt(), bidding.getUserNo());

								mAuctionState.resetRemainTime();

								startBiddingTimer();
							} else {
								// 부재자 응찰의 경우는 희망가 이상이더라도 무조건 1순위로 응찰 처리 필요
								// 부재자 응찰 외 일반 응찰은 희망가 이상일 경우 동일인 연속 응찰 할 수 없음
								if (bidding.getAbsentee() == null) {
									mLogger.debug("bidding.getAbsentee() null!");
								}

								if (bidding.getUserNo() == null) {
									mLogger.debug("bidding.getUserNo() null!");
								}

								if (mAuctionState.getCurrentPrice() == null) {
									mLogger.debug("mAuctionState.getCurrentPrice()");
								}

								if (mTopBiddingMap.get(Integer.valueOf(mAuctionState.getCurrentPrice())) == null) {
									mLogger.debug(
											"mTopBiddingMap.get(Integer.valueOf(mAuctionState.getCurrentPrice()))");
								}

								mLogger.debug("TopBiddingMap ContainsKey : "
										+ mTopBiddingMap.containsKey(bidding.getPriceInt()));
								mLogger.debug(
										"직전 권리 여부 : " + bidding.getUserNo().equals(mAuctionState.getRank1MemberNum()));

								if ((!mTopBiddingMap.containsKey(bidding.getPriceInt())
										&& (bidding.getAbsentee().equals("Y") || (bidding.getAbsentee().equals("N")
												&& !bidding.getUserNo().equals(mAuctionState.getRank1MemberNum()))))) {

									mTopBiddingMap.put(bidding.getPriceInt(), bidding.getUserNo());
									mLogger.debug("mTopBiddingMap put : " + bidding.getPriceInt() + " / "
											+ bidding.getUserNo());

									mAuctionState.resetRemainTime();

									startBiddingTimer();
								}

								if (bidding.getAbsentee().equals("N")
										&& bidding.getUserNo().equals(mAuctionState.getRank1MemberNum())) {
									// 동일가 응찰자 정보 수집
									if (!mCurrentBidderMap.containsKey(bidding.getUserNo())) {
										mCurrentBidderMap.put(bidding.getUserNo(), bidding);
										mAuctionState.setCurrentBidderCount(String.valueOf(mCurrentBidderMap.size()));
									}

									bidding = null;
								}
							}
						}

						if (mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)
								&& Integer.valueOf(mAuctionState.getCurrentPrice()) >= Integer
										.valueOf(mAuctionState.getCurrentEntryInfo().getAuctionHopePrice())) {
							mAuctionState.onCompetitive();
						}
					}

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
					mLogger.debug("현재 가격 : " + mAuctionState.getCurrentPrice());
					mLogger.debug("다음 가격 : " + mAuctionState.getNextPrice());
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

		String LOG_DIRECTORY = AuctionServerSetting.AUCTION_LOG_FILE_PATH + currentTime + "_"
				+ mAuctionState.getCurrentEntryInfo().getAuctionCode().toUpperCase() + "_"
				+ mAuctionState.getCurrentEntryInfo().getAuctionRound() + "_"
				+ mAuctionState.getCurrentEntryInfo().getAuctionLaneName();
		String LOG_FILE_NAME = getCurrentTime("yyyyMMdd") + "-" + mAuctionState.getCurrentEntryInfo().getAuctionRound()
				+ "-" + mAuctionState.getEntryNum() + AuctionServerSetting.AUCTION_LOG_FILE_EXTENSION;

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

						if (Integer.valueOf(auctionState.getCurrentPrice()) >= Integer
								.valueOf(auctionState.getCurrentEntryInfo().getAuctionHopePrice()) && !isAuctionPass) {
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
								+ EMPTY_SPACE + "시작가 : " + auctionState.getCurrentEntryInfo().getAuctionStartPrice()
								+ EMPTY_SPACE + "희망가 : " + auctionState.getCurrentEntryInfo().getAuctionHopePrice()
								+ EMPTY_SPACE + "상승가 : " + AuctionServerSetting.AUCTION_CURRENT_RISING_PRICE
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
								+ "출품번호 : " + auctionState.getEntryNum() + EMPTY_SPACE + "현재가 : "
								+ String.valueOf(biddingList.get(i).getPriceInt()
										- AuctionServerSetting.AUCTION_CURRENT_RISING_PRICE)
								+ EMPTY_SPACE + "회원번호 : " + biddingList.get(i).getUserNo() + EMPTY_SPACE + "접속채널 : "
								+ biddingList.get(i).getChannel() + EMPTY_SPACE + "부재자 : "
								+ biddingList.get(i).getAbsentee() + EMPTY_SPACE + "응찰가 : "
								+ biddingList.get(i).getPrice();

						fileWriter.write(logContent);
						fileWriter.flush();
					} else {
						logContent = "\r\n" + getLogTimeFormat(biddingList.get(i).getBiddingTime()) + EMPTY_SPACE
								+ "출품번호 : " + auctionState.getEntryNum() + EMPTY_SPACE + "현재가 : "
								+ String.valueOf(biddingList.get(i).getPriceInt()
										- AuctionServerSetting.AUCTION_CURRENT_RISING_PRICE)
								+ EMPTY_SPACE + "회원번호 : " + biddingList.get(i).getUserNo() + EMPTY_SPACE + "접속채널 : "
								+ biddingList.get(i).getChannel() + EMPTY_SPACE + "부재자 : "
								+ biddingList.get(i).getAbsentee() + EMPTY_SPACE + "응찰가 : "
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
	 * @MethodName isNeddRunAutoBidding
	 * @Description 자동 상승 처리 필요 여부 반환
	 *
	 * @return boolean 자동 상승 처리 필요 여부(true : 처리필요 / false : 처리불필요)
	 */
	private boolean isNeddRunAutoBidding() {
		if (mAutoBiddingCount < AuctionServerSetting.AUCTION_AUTO_RISE_COUNT) {
			return true;
		} else {
			if (mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_SLOWDOWN)) {
				mAuctionState.onProgress();

				// 경매 상태 변경 Broadcast
				if (mAuctionServer != null) {
					mAuctionServer.itemAdded(mAuctionState.getAuctionStatus().getEncodedMessage());
				}
			}
		}

		return false;
	}

	/**
	 * 
	 * @MethodName runAutoBidding
	 * @Description 자동 상승 처리
	 *
	 */
	private void runAutoBidding() {
		if (mAutoBiddingCount < AuctionServerSetting.AUCTION_AUTO_RISE_COUNT) {
			Bidding autoBidding = new Bidding(SYSTEM_USER_NAME, SYSTEM_USER_NAME, "N", mAuctionState.getNextPrice(),
					mAuctionState.getEntryNum());

			mAutoBiddingCount++;

			autoBidding.setBiddingTime(getCurrentTime("yyyyMMddHHmmssSSS"));

			mLogger.debug(mAutoBiddingCount + "회 자동 상승 가격 : " + autoBidding.getPrice());
			mAuctionServer.itemAdded(autoBidding.getEncodedMessage());
		} else {
			mAuctionState.onProgress();

			// 경매 상태 변경 Broadcast
			if (mAuctionServer != null) {
				mAuctionServer.itemAdded(mAuctionState.getAuctionStatus().getEncodedMessage());
			}

			mAutoBiddingCount = 0;
		}
	}

	/**
	 * 
	 * @MethodName isRunAbseteeBidding
	 * @Description 부재자 응찰 시도 필요 여부 판단 처리
	 *
	 * @return boolean 부재자 응찰 시도 필요 여부(true : 부재자 응찰 시도 필요 / false : 부재자 응찰 시도 불필요)
	 */
	private boolean isRunAbseteeBidding() {
		boolean result = false;

		if (mAbsenteeBiddingMap != null) {
			if (mAbsenteeBiddingMap.containsKey(mAuctionState.getEntryNum())) {
				if (mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).size() > 0) {
					if (Integer.valueOf(mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).get(0)
							.getBidPrice()) > Integer.valueOf(mAuctionState.getCurrentPrice())) {
						mIsPriorityAbsenteeBidding = true;
						result = true;
					} else {
						mIsPriorityAbsenteeBidding = false;
						result = false;
					}
				} else {
					mIsPriorityAbsenteeBidding = false;
					result = false;
				}
			} else {
				mIsPriorityAbsenteeBidding = false;
				result = false;
			}
		}

		return result;
	}

	public int getAbsenteeBiddingCount() {
		int count = 0;

		if (mAbsenteeBiddingMap != null) {
			if (mAbsenteeBiddingMap.containsKey(mAuctionState.getEntryNum())) {
				if (mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).size() > 0) {
					for (int i = 0; i < mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).size(); i++) {
						if (Integer.valueOf(mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).get(i)
								.getBidPrice()) > Integer.valueOf(mAuctionState.getCurrentPrice())) {
							count++;
						}
					}
				}
			}
		}

		return count;
	}

	/**
	 * 
	 * @MethodName runAbsenteeBidding
	 * @Description 부재자 응찰 처리
	 *
	 */
	public void absenteeBidding(Bidding bidding) {
		if (mAbsenteeBiddingMap != null) {
			if (mAbsenteeBiddingMap.containsKey(mAuctionState.getEntryNum())) {
				if (mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).size() > 0) {
					for (int i = 0; i < mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).size(); i++) {
						if (Integer.valueOf(mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).get(i)
								.getBidPrice()) > Integer.valueOf(mAuctionState.getCurrentPrice())) {

							/*
							 * 21.03.31 부재자 응찰 처리 수정 기존 실시간 경매 상태의 가격과 출품번호로 부재자 응찰 정보를 생성했다면, setBidding을
							 * 통해 수신된 응찰 정보로 부재자 응찰 정보를 구성함. 0.001ms 차이로 수집 종료와 응찰 정보가 동시에 발생했을때 부재자 응찰 가격이
							 * 수집 종료 이후 다음 가격으로 설정되어 응찰되는 이슈 해결.
							 */
							Bidding absenteeBidding = new Bidding(
									mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).get(i).getAuctionChannel(),
									mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).get(i).getAuctionMemberNum(),
									"Y", bidding.getPrice(), bidding.getEntryNum());

							absenteeBidding.setBiddingTime(
									mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).get(i).getBidDateTime());

							if (mAuctionServer != null) {
								mLogger.debug("Absentee Bidding : " + absenteeBidding.getUserNo() + " / "
										+ absenteeBidding.getPrice());
								mAuctionServer.itemAdded(absenteeBidding.getEncodedMessage());
							}
						}
					}
				}
			}
		}

		mIsPriorityAbsenteeBidding = false;

		// 부재자 응찰 필요 여부 초기화
		if (mIsNeedAbsenteeBidding) {
			mIsNeedAbsenteeBidding = false;
		}
	}

	/**
	 * 
	 * @MethodName runAbsenteeBidding
	 * @Description 부재자 응찰 처리
	 *
	 */
	public void runAbsenteeBidding() {
		if (mAbsenteeBiddingMap != null) {
			if (mAbsenteeBiddingMap.containsKey(mAuctionState.getEntryNum())) {
				if (mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).size() > 0) {
					for (int i = 0; i < mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).size(); i++) {
						if (Integer.valueOf(mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).get(i)
								.getBidPrice()) > Integer.valueOf(mAuctionState.getCurrentPrice())) {
							Bidding absenteeBidding = new Bidding(
									mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).get(i).getAuctionChannel(),
									mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).get(i).getAuctionMemberNum(),
									"Y", mAuctionState.getNextPrice(), mAuctionState.getEntryNum());

							// 부재자 응찰은 희망가 이상이고, 경쟁 상황이 아닌 경우 동일 회원이 연속하여 부재자 응찰을 하지 못하도록 처리
							if (!mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_SLOWDOWN)
									&& Integer.valueOf(mAuctionState.getCurrentPrice()) < Integer
											.valueOf(mAuctionState.getCurrentEntryInfo().getAuctionHopePrice())) {
								absenteeBidding.setBiddingTime(
										mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).get(i).getBidDateTime());

								if (mAuctionServer != null) {
									mLogger.debug("Absentee Bidding : " + absenteeBidding.getUserNo() + " / "
											+ absenteeBidding.getPrice());
									mAuctionServer.itemAdded(absenteeBidding.getEncodedMessage());
								}
							} else {
								if (mIsNeedAbsenteeBidding) {
									absenteeBidding.setBiddingTime(mAbsenteeBiddingMap.get(mAuctionState.getEntryNum())
											.get(i).getBidDateTime());

									if (mAuctionServer != null) {
										mLogger.debug("Absentee Bidding : " + absenteeBidding.getUserNo() + " / "
												+ absenteeBidding.getPrice());
										mAuctionServer.itemAdded(absenteeBidding.getEncodedMessage());
									}
								}
							}

						}
					}
				}
			}
		}

		mIsPriorityAbsenteeBidding = false;

		// 부재자 응찰 필요 여부 초기화
		if (mIsNeedAbsenteeBidding) {
			mIsNeedAbsenteeBidding = false;
		}
	}

	/**
	 * 
	 * @MethodName nextCarInfoLoad
	 * @Description 다음 출품 정보 로딩 처리
	 *
	 */
	private void nextCarInfoLoad() {
		String requestEntrySeq = "";

		if (mAuctionEntryRepository != null) {
			if (mAuctionEntryRepository.getEntryList().size() > 0) {
				requestEntrySeq = String
						.valueOf(Integer
								.valueOf(mAuctionEntryRepository.getEntryList()
										.get(mAuctionEntryRepository.getEntryList().size() - 1).getAuctionEntrySeq())
								+ 1);

				// 다음 출품 차량 정보 요청
				ActionRuler.getInstance().addAction(new ActionRequestAuctionEntryInformation(mAuctionCode,
						mAuctionRound, mAuctionLaneCode, requestEntrySeq, mActionResultAuctionEntryInformation));
				ActionRuler.getInstance().runNext();

				// 관심 차량 정보 조회
				ActionRuler.getInstance().addAction(new ActionRequestAuctionInterestEntryInfo(mAuctionCode,
						mAuctionRound, mAuctionLaneCode, requestEntrySeq, mActionResultInterestEntryInfo));
				ActionRuler.getInstance().runNext();

				// 다음 출품 차량 부재자 정보 요청
				ActionRuler.getInstance().addAction(new ActionRequestAbsenteeAuctionBidInfo(mAuctionCode, mAuctionRound,
						mAuctionLaneCode, requestEntrySeq, mActionResultAbsenteeAuctionBidInfo));
				ActionRuler.getInstance().runNext();
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

					String flagAutoMode = "N";

					// 경매 자동 시작 여부 확인
					if (AuctionServerSetting.AUCTION_AUTO_MODE) {
						flagAutoMode = "Y";
					} else {
						flagAutoMode = "N";
					}

					mAuctionServer
							.itemAdded(new StartAuction(flagAutoMode, mAuctionState.getEntryNum()).getEncodedMessage());
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

	/**
	 * 
	 * @ClassName Auctioneer.java
	 * @Description 경매 진행 타이머 처리 클래스
	 * @author 박종식
	 * @since 2019.10.23
	 */
	private class AuctionTimerJob implements Runnable {
		@Override
		public void run() {
			try {
				ArrayList<Bidding> currentBiddingList = new ArrayList<Bidding>();
				AuctionState auctionState;
				boolean isAuctionPass;

				// 강제 유찰 처리 확인
				if (mIsRequestAuctionPass) {
					if (mBiddingTimeJob != null) {
						mBiddingTimeJob.cancel(true);
					}

					mLogger.debug(mAuctionState.getEntryNum() + "출품 차량 강제 유찰 요청");

					mAuctionState.setRemainTime(0);

					mAuctionState.onCompleted();

					mIsAuctionPass = true;
					mIsRequestAuctionPass = false;
				} else if (mAuctionState.getRemainTime() > 0 && (mAuctionState.getAuctionState()
						.equals(GlobalDefineCode.AUCTION_STATUS_START)
						|| mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_SLOWDOWN)
						|| mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)
						|| mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_COMPETITIVE))) {

					mAuctionState.decreaseRemainTime();

				} else {
					mLogger.debug(mAuctionState.getEntryNum() + "출품 차량 응찰 종료");
					mAuctionState.onCompleted();

					// Bidding Timer 오류 검출 Flag
					if (mFlagFail || mIsActiveBiddingTime) {
						mFlagFail = false;
						mLogger.debug("Fail Bidding Time Completed : " + mAuctionState.getEntryNum() + "출품 차량");
					}

					mIsActiveBiddingTime = false;

					// 현재 응찰 우선 순위 확인 처리
					setCurrentRank(true);

					currentBiddingList.clear();
					currentBiddingList.addAll(mCurrentEntryBiddingList);
					auctionState = mAuctionState;
					isAuctionPass = mIsAuctionPass;

					// 로그 Write 처리
					Runnable createLogContentRunnable = new Runnable() {

						@Override
						public void run() {
							writeLogFile(currentBiddingList, auctionState, isAuctionPass);
						}
					};

					mLogWriterService.execute(createLogContentRunnable);

					determineAuctionResult();

					// 낙유찰 결과 전송 처리 수행
					transmissionAuctionResult();

					if (mAuctionServer != null) {
						mAuctionServer.itemAdded(mAuctionState.getAuctionStatus().getEncodedMessage());
					}

					if (mAuctionEntryRepository != null && mAuctionEntryRepository.getTotalCount() > 0) {
						readyCarInfo();
					} else {
						mIsLastEntry = true;
					}

					startNextIntervalTimer();

					if (mRemainTimeJob != null) {
						mRemainTimeJob.cancel(true);
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @ClassName Auctioneer.java
	 * @Description 경매 관련 타이머 오류 확인 처리
	 * @author 박종식
	 * @since 2020.05.09
	 */
	private class CheckAuctionTimerJob implements Runnable {
		@Override
		public synchronized void run() {
			if (mCheckAuctionTimerCount == 60000) {
				mLogger.debug("경매 타이머 오류 검출 타이머 정상 수행");
				mCheckAuctionTimerCount = 0;
			} else {
				mCheckAuctionTimerCount = mCheckAuctionTimerCount + AuctionServerSetting.REMAIN_CHECK_DELAY_TIME;
			}

			// Bidding Time 오류 검출 로직
			if (mIsActiveBiddingTime && mPrevBiddingTime == mAuctionState.getBiddingRemainTime()) {
				mLogger.debug("mPrevBiddingTime / getBiddingRemainTime : " + mPrevBiddingTime + " / "
						+ mAuctionState.getBiddingRemainTime());

				if (Long.parseLong(
						LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))) > (mPrevLocalTime
								+ AuctionServerSetting.REMAIN_CHECK_DELAY_TIME)) {
					mPrevLocalTime = Long
							.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
					mCheckPrevBiddingTimeCount++;
				}
			} else {
				mPrevBiddingTime = mAuctionState.getBiddingRemainTime();
				mCheckPrevBiddingTimeCount = 0;
			}

			if (mIsActiveBiddingTime && mCheckPrevBiddingTimeCount >= CHECK_TIME_COUNT) {
				mLogger.debug("[CHECK BIDDING TIME] mCurrentBidderMap.size() : " + mCurrentBidderMap.size());
				mLogger.debug("[CHECK BIDDING TIME] mCheckPrevBiddingTimeCount : " + mCheckPrevBiddingTimeCount);
				mLogger.debug("[CHECK BIDDING TIME] mPrevBiddingTime : " + mPrevBiddingTime);

				mLogger.debug(
						"현재 Bidding Time 동작 시 예외 상황이 발생하였습니다.(잔여 시간 : " + mAuctionState.getBiddingRemainTime() + "ms)");
				mLogger.debug("예외 상황 조치로 Bidding Time을 재요청 합니다.");

				if (mBiddingTimeJob != null) {
					mBiddingTimeJob.cancel(true);
				}

				if (mAuctionServer != null) {
					mAuctionServer.itemAdded(mAuctionState.getAuctionStatus().getEncodedMessage());
				}

				mCheckPrevBiddingTimeCount = 0;

				mBiddingTimeJob = mBiddingTimeService.scheduleAtFixedRate(new BiddingTimerJob(),
						AuctionServerSetting.BASE_DELAY_TIME, AuctionServerSetting.REMAIN_CHECK_DELAY_TIME,
						TimeUnit.MILLISECONDS);
			}
		}
	}

	/**
	 * 
	 * @MethodName resetAuctionData
	 * @Description 경매 출품 단위 데이터를 초기화 처리
	 *
	 */
	public void resetAuctionData() {
		mAutoBiddingCount = 0;

		mIsActiveBiddingTime = false;

		mIsPriorityAbsenteeBidding = false;

		// Bidding Time 오류 검출 확인 변수 초기화
		mPrevBiddingTime = 0;
		mCheckPrevBiddingTimeCount = 0;

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
	 * @ClassName Auctioneer.java
	 * @Description 경매 응찰 정보 수집 타이머 처리 클래스
	 * @author 박종식
	 * @since 2019.10.23
	 */
	private class BiddingTimerJob implements Runnable {
		@Override
		public void run() {
			try {
				if (mAuctionState.getBiddingRemainTime() > 0 && (mAuctionState.getAuctionState()
						.equals(GlobalDefineCode.AUCTION_STATUS_START)
						|| mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_SLOWDOWN)
						|| mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)
						|| mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_COMPETITIVE))) {

					if (mFlagStartBiddingTimeJob) {
						mLogger.debug("경매 응찰 정보 수집 타이머가 동작을 시작합니다.");
						mFlagStartBiddingTimeJob = false;
					}

					mAuctionState.decreaseBiddingRemainTime();

				} else {

					mLogger.debug("현재가 : " + mAuctionState.getCurrentPrice() + "만원");
					mLogger.debug(mAuctionState.getNextPrice() + "만원 응찰가 수집 종료");

					mIsActiveBiddingTime = false;

					// Bidding Time 오류 검출 확인 변수 초기화
					mPrevBiddingTime = 0;
					mCheckPrevBiddingTimeCount = 0;

					if (mCurrentBidderMap != null) {
						if (getAbsenteeBiddingCount() >= 2) {
							mIsNeedAbsenteeBidding = true;
						}

						mCurrentBidderMap.clear();
					}

					mAuctionState.resetBiddingRemainTime();

					// 현재 응찰 우선 순위 확인 처리
					setCurrentRank(false);

					// 응찰 수집 가격 정보 전송
					if (mAuctionServer != null) {
						int currentPrice = Integer.valueOf(mAuctionState.getCurrentPrice())
								+ AuctionServerSetting.AUCTION_CURRENT_RISING_PRICE;
						mAuctionState.setCurrentPrice(String.valueOf(currentPrice));

						mAuctionServer.itemAdded(mAuctionState.getAuctionStatus().getEncodedMessage());
					}

					// Bidding Timer 오류 검출 Flag
					mFlagFail = false;

					if (mBiddingTimeJob != null) {
						mBiddingTimeJob.cancel(true);
					}

					// 자동 상승 처리 및 부재자 응찰 필요 확인
					if (isNeddRunAutoBidding()) {
						runAutoBidding();
					} else if (isRunAbseteeBidding()) {
						runAbsenteeBidding();
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @ClassName Auctioneer.java
	 * @Description 다음 출품 정보로 넘어가기전 대기 시간 처리 클래스
	 * @author 박종식
	 * @since 2019.10.24
	 */
	private class NextEntryIntervalTimerJob implements Runnable {

		@Override
		public void run() {
			// 다음 출품 데이터 확인 및 관련 처리 진행
			nextEntryAndFinishEntry();

			if (mNextEntryIntervalTimeJob != null) {
				mNextEntryIntervalTimeJob.cancel(true);
			}
		}
	}

	/**
	 * 
	 * @ClassName Auctioneer.java
	 * @Description 경매 종료 알림 딜레이 처리
	 * @author 박종식
	 * @since 2019.11.20
	 */
	private class DelayTimerJob implements Runnable {

		@Override
		public void run() {
			if (mAuctionServer != null) {
				mAuctionServer.itemAdded(mAuctionState.getAuctionStatus().getEncodedMessage());
			}

			if (mDelayTimerJob != null) {
				mDelayTimerJob.cancel(true);
			}
		}
	}

	private void determineAuctionResult() {
		mLogger.debug(mAuctionState.getEntryNum() + "번 출품 차량 낙/유찰 결과 확인");

		mLogger.debug("1순위 : " + mAuctionState.getRank1MemberNum());
		// 마지막 응찰 가격이 희망가 이상인지 확인
		// 예외 사항(유찰 처리 대상)
		// - 시작가가 희망가 이상일 경우 유찰(비정상 상황)
		// - 자동 상승 후 System이 낙찰자일 경우(비정상 상황)
		if ((Integer.valueOf(mAuctionState.getCurrentPrice()) >= Integer
				.valueOf(mAuctionState.getCurrentEntryInfo().getAuctionHopePrice()))
				&& !(Integer.valueOf(mAuctionState.getCurrentEntryInfo().getAuctionStartPrice()) >= Integer
						.valueOf(mAuctionState.getCurrentEntryInfo().getAuctionHopePrice()))
				&& mAuctionState.getRank1MemberNum() != null
				&& !mAuctionState.getRank1MemberNum().equals(SYSTEM_USER_NAME)
				&& !mAuctionState.getRank1MemberNum().equals("") && !mAuctionState.getRank1MemberNum().equals(null)
				&& !mIsAuctionPass) {
			mAuctionState.onSuccess();
		} else {
			mAuctionState.onFail();
		}

		if (mIsAuctionPass) {
			mIsAuctionPass = false;
		}
	}

	/**
	 * 
	 * @MethodName requestTransmissionAuctionResult
	 * @Description 출품 단위 낙/유찰 결과 정보 전송 처리
	 *
	 */
	private void requestTransmissionAuctionResult(AuctionEntryInformationResult auctionEntryInformationResult,
			String auctionResultCode, String auctionResultDateTime, String successBidMemberNum,
			String successBidChannel, String successBidPrice) {

		// 출품 단위 낙/유찰 결과 정보 전송 처리
		AuctionResult auctionResult = new AuctionResult(auctionEntryInformationResult.getAuctionCode(),
				auctionEntryInformationResult.getAuctionRound(), auctionEntryInformationResult.getAuctionLaneCode(),
				auctionEntryInformationResult.getAuctionEntryNum(), auctionEntryInformationResult.getProductCode(),
				auctionResultCode, auctionResultDateTime, successBidMemberNum, successBidPrice, successBidChannel,
				auctionEntryInformationResult.getCarName(), auctionEntryInformationResult.getAuctionHopePrice(),
				mAuctionState.getRank1BidPrice(), mAuctionState.getRank1MemberNum(), mAuctionState.getRank1BidPrice(),
				mAuctionState.getRank2MemberNum(), mAuctionState.getRank2BidPrice(), mAuctionState.getRank3MemberNum(),
				mAuctionState.getRank3BidPrice(), mAuctionState.getRank4MemberNum(), mAuctionState.getRank4BidPrice(),
				mAuctionState.getRank5MemberNum(), mAuctionState.getRank5BidPrice());

		// 낙,유찰 결과 전송 요청 이후 성공 시 mAuctionResultTransmissionFailMap에서 삭제 처리
		mAuctionResultTransmissionFailMap.put(auctionResult.getAuctionEntryNum(), auctionResult);

		ActionRuler.getInstance().addAction(
				new ActionRequestTransmissionAuctionResult(auctionResult, mActionResultTransmissionAuctionResult));
		ActionRuler.getInstance().runNext();
	}

	/**
	 * 
	 * @MethodName nextEntryAndFinishEntry
	 * @Description 다음 출품 정보 확인 및 경매 진행 종료 확인 처리
	 *
	 */
	private void nextEntryAndFinishEntry() {
		// 다음 출품 차량 존재 여부 확인 후 처리
		if (mAuctionEntryRepository != null && mAuctionEntryRepository.getTotalCount() >= 0 && !mIsLastEntry) {
			// 다음 출품 정보 Broadcast 처리
			if (mAuctionServer != null) {
				ResponseEntryInfo carInfo = mAuctionState.getCurrentEntryInfo().toResponseCarInfo();

				mAuctionServer.itemAdded(carInfo.getEncodedMessage());
			}

			nextCarInfoLoad();
		} else {
			mAuctionState.onFinish();

			// 경매 서버 상태를 진행 상태로 업데이트 요청
			if (mAuctionServerStatus.equals(GlobalDefineCode.AUCTION_API_STATUS_START)) {
				requestUpdateAuctionStatus(GlobalDefineCode.AUCTION_API_STATUS_COMPLETED);
			}
		}

		if (mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {
			ToastMessage toastMessage = new ToastMessage("경매가 마감되었습니다. 많은 성원에 감사합니다.");

			if (mAuctionServer != null) {
				mAuctionServer.itemAdded(toastMessage.getEncodedMessage());
			}

			startDelayTimer();
		} else {
			if (mAuctionServer != null) {
				mAuctionServer.itemAdded(mAuctionState.getAuctionStatus().getEncodedMessage());
			}
		}

		// 자동 진행 모드일 경우에만 자동 실행 처리
		if (AuctionServerSetting.AUCTION_AUTO_MODE && !mIsRequestAuctionStop) {
			if (mAuctionServer != null) {
				String flagAutoMode = "N";

				// 경매 자동 시작 여부 확인
				if (AuctionServerSetting.AUCTION_AUTO_MODE) {
					flagAutoMode = "Y";
				} else {
					flagAutoMode = "N";
				}

				mAuctionServer
						.itemAdded(new StartAuction(flagAutoMode, mAuctionState.getEntryNum()).getEncodedMessage());
			}

//            startAuction();
		} else {
			stopAuction();
		}

		if (mIsRequestAuctionStop) {
			mIsRequestAuctionStop = false;
		}

		// 경매 종료 시 경매 서버 소멸 처리
		if (mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_FINISH)) {
			destroyAuctionServer();
		}
	}

	/**
	 * 
	 * @MethodName setCurrentRank
	 * @Description 현재 응찰 가격 기준 우선순위 확인 처리
	 *
	 */
	@SuppressWarnings("unlikely-arg-type")
	private void setCurrentRank(boolean isEntryFinish) {
		Bidding rank1 = null;
		Bidding rank2 = null;
		Bidding rank3 = null;
		Bidding rank4 = null;
		Bidding rank5 = null;

		List<Bidding> list = new ArrayList<Bidding>();
		list.addAll(mCurrentEntryBiddingMap.values());

		mLogger.debug("응찰 가격 우선순위 확인");

		if (!isEntryFinish) {
			mCurrentEntryBiddingList.addAll(list);

			if (mCurrentEntryBiddingMap != null) {
				mCurrentEntryBiddingMap.clear();
			}
		}

		Collections.sort(mCurrentEntryBiddingList, new Comparator<Bidding>() {

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

					// api에서 받아온 부재자 순위대로 rank를 설정하기 위해 필요한 로직.
					if (mCurrentEntryAbsenteeBiddingMap.containsKey(o1.getUserNo())
							&& mCurrentEntryAbsenteeBiddingMap.containsKey(o2.getUserNo())) {
						if (Integer.parseInt(mCurrentEntryAbsenteeBiddingMap.get(o1.getUserNo())) < Integer
								.parseInt(mCurrentEntryAbsenteeBiddingMap.get(o2.getUserNo()))) {
							result = 0;
						}
					}
				} else if (Long.parseLong(o1.getPrice()) > Long.parseLong(o2.getPrice())) {
					result = -1;
				} else if (Long.parseLong(o1.getPrice()) < Long.parseLong(o2.getPrice())) {
					result = 1;
				}

				return result;
			}
		});

		for (int i = 0; i < mCurrentEntryBiddingList.size(); i++) {
			if (rank1 == null) {
				rank1 = mCurrentEntryBiddingList.get(i);
			} else if (rank1 != null && rank2 == null) {
				if (!mCurrentEntryBiddingList.get(i).getUserNo().equals(rank1.getUserNo())) {
					rank2 = mCurrentEntryBiddingList.get(i);
				}
			} else if (rank1 != null && rank2 != null && rank3 == null) {
				if (!mCurrentEntryBiddingList.get(i).getUserNo().equals(rank1.getUserNo())
						&& !mCurrentEntryBiddingList.get(i).getUserNo().equals(rank2.getUserNo())) {
					rank3 = mCurrentEntryBiddingList.get(i);
				}
			} else if (rank1 != null && rank2 != null && rank3 != null && rank4 == null) {
				if (!mCurrentEntryBiddingList.get(i).getUserNo().equals(rank1.getUserNo())
						&& !mCurrentEntryBiddingList.get(i).getUserNo().equals(rank2.getUserNo())
						&& !mCurrentEntryBiddingList.get(i).getUserNo().equals(rank3.getUserNo())) {
					rank4 = mCurrentEntryBiddingList.get(i);
				}
			} else if (rank1 != null && rank2 != null && rank3 != null && rank4 != null && rank5 == null) {
				if (!mCurrentEntryBiddingList.get(i).getUserNo().equals(rank1.getUserNo())
						&& !mCurrentEntryBiddingList.get(i).getUserNo().equals(rank2.getUserNo())
						&& !mCurrentEntryBiddingList.get(i).getUserNo().equals(rank3.getUserNo())
						&& !mCurrentEntryBiddingList.get(i).getUserNo().equals(rank4.getUserNo())) {
					rank5 = mCurrentEntryBiddingList.get(i);
				}
			}

			if (rank1 != null && rank2 != null && rank3 != null && rank4 != null && rank5 != null) {
				break;
			}
		}

		mLogger.debug("응찰 가격 우선순위 적용 시작");

		if (rank1 != null) {
			if (!rank1.getUserNo().equals(SYSTEM_USER_NAME)) {
				mAuctionState.setRank1MemberNum(rank1.getUserNo());
				mAuctionState.setRank1BidPrice(rank1.getPrice());
				mAuctionState.setRank1MemberChannel(rank1.getChannel());
			}
		}

		if (rank2 != null) {
			if (!rank2.getUserNo().equals(SYSTEM_USER_NAME)) {
				mAuctionState.setRank2MemberNum(rank2.getUserNo());
				mAuctionState.setRank2BidPrice(rank2.getPrice());
				mAuctionState.setRank2MemberChannel(rank2.getChannel());
			}
		}

		if (rank3 != null) {
			if (!rank3.getUserNo().equals(SYSTEM_USER_NAME)) {
				mAuctionState.setRank3MemberNum(rank3.getUserNo());
				mAuctionState.setRank3BidPrice(rank3.getPrice());
				mAuctionState.setRank3MemberChannel(rank3.getChannel());
			}
		}

		if (rank4 != null) {
			if (!rank4.getUserNo().equals(SYSTEM_USER_NAME)) {
				mAuctionState.setRank4MemberNum(rank4.getUserNo());
				mAuctionState.setRank4BidPrice(rank4.getPrice());
				mAuctionState.setRank4MemberChannel(rank4.getChannel());
			}
		}

		if (rank5 != null) {
			if (!rank5.getUserNo().equals(SYSTEM_USER_NAME)) {
				mAuctionState.setRank5MemberNum(rank5.getUserNo());
				mAuctionState.setRank5BidPrice(rank5.getPrice());
				mAuctionState.setRank5MemberChannel(rank5.getChannel());
			}
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
		} else if (auctionStatus.equals(GlobalDefineCode.AUCTION_STATUS_SLOWDOWN)) {
			state = "상승";
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

	public synchronized void editAuctionConfig(EditSetting editSetting) {
		if (mAuctionConfig != null) {
			mAuctionConfig.setAuctionSettingInformation(editSetting);
		}

		if (mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_NONE)
				|| mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_READY)) {
			mAuctionState.resetRemainTime();
		}
	}

	ActionResultListener<ResponseAuctionSettingsInformation> mActionResultAuctionSettingsInformation = new ActionResultListener<ResponseAuctionSettingsInformation>() {
		@Override
		public void onResponseResult(ResponseAuctionSettingsInformation response) {
			if (response != null && response.getResult() != null) {
				mLogger.debug("================ResponseAuctionSettingsInformation[Start]================");
				mLogger.debug("Auction Code : " + mAuctionCode);
				mLogger.debug("Auction Round : " + mAuctionRound);
				mLogger.debug("Auction Lane Code : " + mAuctionLaneCode);
				mLogger.debug("Auction Entry Count : " + response.getRecordCount());
				mLogger.debug("Server Response Message : " + response.getStatus());
				mLogger.debug("Server Response Message : " + response.getMessage());
				mLogger.debug("================ResponseAuctionSettingsInformation[ End ]================");

				if (response.getStatus().equals("success")) {
					if (response.getResult().size() > 0 && response.getResult().get(0) != null) {
						AuctionSettingsInformationResult result = response.getResult().get(0);
						mAuctionConfig.setAuctionSettingsInformationResult(result);
					}

					mAuctionState.setSettingsInformationLoad();
				}
			}

			mAuctionState.setCheckSettingsInformation();
		}

		@Override
		public void onResponseError(String message) {
			mLogger.debug("================ResponseAuctionSettingsInformation[Start]================");
			mLogger.debug("Auction Code : " + mAuctionCode);
			mLogger.debug("Auction Round : " + mAuctionRound);
			mLogger.debug("Auction Lane Code : " + mAuctionLaneCode);
			mLogger.debug("Server Response Message : " + message);
			mLogger.debug("================ResponseAuctionSettingsInformation[ End ]================");

			mAuctionState.setCheckSettingsInformation();
		}
	};

	ActionResultListener<ResponseAuctionEntryInformation> mActionResultAuctionEntryInformation = new ActionResultListener<ResponseAuctionEntryInformation>() {
		@Override
		public void onResponseResult(ResponseAuctionEntryInformation response) {
			if (response != null && response.getResult() != null) {
				mLogger.debug("================ResponseAuctionEntryInformation[Start]================");
				mLogger.debug("Auction Code : " + mAuctionCode);
				mLogger.debug("Auction Round : " + mAuctionRound);
				mLogger.debug("Auction Lane Code : " + mAuctionLaneCode);
				mLogger.debug("Auction Entry Count : " + response.getRecordCount());
				mLogger.debug("Server Response Message : " + response.getStatus());
				mLogger.debug("Server Response Message : " + response.getMessage());
				mLogger.debug("================ResponseAuctionEntryInformation[ End ]================");

				if (response.getStatus().equals("success")) {
					if (response.getResult().size() > 0) {
						mAuctionEntryRepository.setInitialEntryList(response.getResult());
					}

					mAuctionState.setEntryInformationLoad();
				}
			}

			mAuctionState.setCheckEntryInformation();
		}

		@Override
		public void onResponseError(String message) {
			mLogger.debug("================ResponseAuctionEntryInformation[Start]================");
			mLogger.debug("Auction Code : " + mAuctionCode);
			mLogger.debug("Auction Round : " + mAuctionRound);
			mLogger.debug("Auction Lane Code : " + mAuctionLaneCode);
			mLogger.debug("Server Response Message : " + message);
			mLogger.debug("================ResponseAuctionEntryInformation[ End ]================");

			mAuctionState.setCheckEntryInformation();
		}
	};

	ActionResultListener<ResponseAuctionInterestEntryInfo> mActionResultInterestEntryInfo = new ActionResultListener<ResponseAuctionInterestEntryInfo>() {
		@Override
		public void onResponseResult(ResponseAuctionInterestEntryInfo response) {
			if (response != null && response.getResult() != null) {
				mLogger.debug("================ResponseAuctionInterestEntryInfo[Start]================");
				mLogger.debug("Auction Code : " + mAuctionCode);
				mLogger.debug("Auction Round : " + mAuctionRound);
				mLogger.debug("Auction Lane Code : " + mAuctionLaneCode);
				mLogger.debug("Auction Entry Count : " + response.getRecordCount());
				mLogger.debug("Server Response Message : " + response.getStatus());
				mLogger.debug("Server Response Message : " + response.getMessage());
				mLogger.debug("================ResponseAuctionInterestEntryInfo[ End ]================");

				if (response.getStatus().equals("success")) {
					if (response.getFavoriteCarInfoMap(response.getResult()).size() > 0) {
						for (String key : response.getFavoriteCarInfoMap(response.getResult()).keySet()) {
							mFavoriteCarInfoMap.put(key, response.getFavoriteCarInfoMap(response.getResult()).get(key));
						}
					}

					mAuctionState.setFavoriteCarInfoLoad();
				}
			}

			mAuctionState.setCheckFavoriteCarInfo();
		}

		@Override
		public void onResponseError(String message) {
			mLogger.debug("================ResponseAuctionInterestEntryInfo[Start]================");
			mLogger.debug("Auction Code : " + mAuctionCode);
			mLogger.debug("Auction Round : " + mAuctionRound);
			mLogger.debug("Auction Lane Code : " + mAuctionLaneCode);
			mLogger.debug("Server Response Message : " + message);
			mLogger.debug("================ResponseAuctionInterestEntryInfo[ End ]================");

			mAuctionState.setCheckFavoriteCarInfo();
		}
	};

	ActionResultListener<ResponseAbsenteeAuctionBidInfo> mActionResultAbsenteeAuctionBidInfo = new ActionResultListener<ResponseAbsenteeAuctionBidInfo>() {
		@Override
		public void onResponseResult(ResponseAbsenteeAuctionBidInfo response) {
			if (response != null && response.getResult() != null) {
				mLogger.debug("================ResponseAbsenteeAuctionBidInfo[Start]================");
				mLogger.debug("Auction Code : " + mAuctionCode);
				mLogger.debug("Auction Round : " + mAuctionRound);
				mLogger.debug("Auction Lane Code : " + mAuctionLaneCode);
				mLogger.debug("Auction Entry Count : " + response.getRecordCount());
				mLogger.debug("Server Response Message : " + response.getStatus());
				mLogger.debug("Server Response Message : " + response.getMessage());
				mLogger.debug("================ResponseAbsenteeAuctionBidInfo[ End ]================");

				if (response.getStatus().equals("success")) {
					if (response.getAbsenteeMap(response.getResult()).size() > 0) {
						for (String key : response.getAbsenteeMap(response.getResult()).keySet()) {
							mAbsenteeBiddingMap.put(key, response.getAbsenteeMap(response.getResult()).get(key));
						}
					}

					mAuctionState.setAbsenteeAuctionBidInfoLoad();
				}
			}

			mAuctionState.setCheckAbsenteeAuctionBidInfo();
		}

		@Override
		public void onResponseError(String message) {
			mLogger.debug("================ResponseAbsenteeAuctionBidInfo[Start]================");
			mLogger.debug("Auction Code : " + mAuctionCode);
			mLogger.debug("Auction Round : " + mAuctionRound);
			mLogger.debug("Auction Lane Code : " + mAuctionLaneCode);
			mLogger.debug("Server Response Message : " + message);
			mLogger.debug("================ResponseAbsenteeAuctionBidInfo[ End ]================");

			mAuctionState.setCheckAbsenteeAuctionBidInfo();
		}
	};

//    ActionResultListener<ResponseSendSmsAuctionServerResult> mActionResultSendSmsAuctionServerStatus = new ActionResultListener<ResponseSendSmsAuctionServerResult>() {
//        @Override
//        public void onResponseResult(ResponseSendSmsAuctionServerResult response) {
//            if (response != null && response.getResult() != null) {
//                mLogger.debug("================ResponseSendSmsAuctionServerResult[Start]================");
//                mLogger.debug("Auction Code : " + mAuctionCode);
//                mLogger.debug("Auction Round : " + mAuctionRound);
//                mLogger.debug("Auction Lane Code : " + mAuctionLaneCode);
//                mLogger.debug("Auction Entry Count : " + response.getRecordCount());
//                mLogger.debug("Server Response Message : " + response.getStatus());
//                mLogger.debug("Server Response Message : " + response.getMessage());
//                mLogger.debug("================ResponseSendSmsAuctionServerResult[ End ]================");
//
//                if (response.getStatus().equals("success")) {
//
//                }
//            }
//        }
//
//        @Override
//        public void onResponseError(String message) {
//            mLogger.debug("================ResponseSendSmsAuctionServerResult[Start]================");
//            mLogger.debug("Auction Code : " + mAuctionCode);
//            mLogger.debug("Auction Round : " + mAuctionRound);
//            mLogger.debug("Auction Lane Code : " + mAuctionLaneCode);
//            mLogger.debug("Server Response Message : " + message);
//            mLogger.debug("================ResponseSendSmsAuctionServerResult[ End ]================");
//        }
//    };

	ActionResultListener<ResponseTransmissionAuctionResult> mActionResultTransmissionAuctionResult = new ActionResultListener<ResponseTransmissionAuctionResult>() {
		@Override
		public void onResponseResult(ResponseTransmissionAuctionResult response) {
			if (response != null && response.getResult() != null) {
				mLogger.debug("================ResponseTransmissionAuctionResult[Start]================");
				mLogger.debug("Auction Code : " + mAuctionCode);
				mLogger.debug("Auction Round : " + mAuctionRound);
				mLogger.debug("Auction Lane Code : " + mAuctionLaneCode);
				mLogger.debug("Server Response Message : " + response.getStatus());
				mLogger.debug("Server Response Message : " + response.getMessage());
				mLogger.debug("Transmission Result Code : " + response.getResult().get(0).getTransmissionResultCode());
				mLogger.debug("Transmission Entry Number : " + response.getResult().get(0).getTransmissionEntryNum());
				mLogger.debug("================ResponseTransmissionAuctionResult[ End ]================");

				// 낙,유찰 결과 전송 요청 이후 성공 시 mAuctionResultTransmissionFailMap에서 삭제 처리
				if (response.getStatus().equals("success")) {
					mAuctionResultTransmissionFailMap.remove(response.getResult().get(0).getTransmissionEntryNum());
				}
			}
		}

		@Override
		public void onResponseError(String entryNum) {
			mLogger.debug("================ResponseTransmissionAuctionResult[Start]================");
			mLogger.debug("Auction Code : " + mAuctionCode);
			mLogger.debug("Auction Round : " + mAuctionRound);
			mLogger.debug("Auction Lane Code : " + mAuctionLaneCode);
			mLogger.debug("Transmission Fail EntryNum : " + entryNum);
			mLogger.debug("================ResponseTransmissionAuctionResult[ End ]================");

			// 낙,유찰 결과 전송 실패 시 한번 더 결과 전송 api 호출
			if (mAuctionResultTransmissionFailMap.containsKey(entryNum)) {
				ActionRuler.getInstance().addAction(new ActionRequestTransmissionAuctionResult(
						mAuctionResultTransmissionFailMap.get(entryNum), mActionResultTransmissionAuctionResult));
				ActionRuler.getInstance().runNext();

				// 낙,유찰 결과 전송 요청 이후 결과 상관없이 mAuctionResultTransmissionFailMap에서 삭제 처리
				mAuctionResultTransmissionFailMap.remove(entryNum);
			}
		}
	};

	ActionResultListener<ResponseUpdateAuctionStatus> mActionUpdateAuctionStatusResult = new ActionResultListener<ResponseUpdateAuctionStatus>() {
		@Override
		public void onResponseResult(ResponseUpdateAuctionStatus response) {
			if (response != null && response.getResult() != null) {
				mLogger.debug("================ResponseUpdateAuctionStatus[Start]================");
				mLogger.debug("Auction Code : " + mAuctionCode);
				mLogger.debug("Auction Round : " + mAuctionRound);
				mLogger.debug("Auction Lane Code : " + mAuctionLaneCode);
				mLogger.debug("Server Response Message : " + response.getStatus());
				mLogger.debug("Server Response Message : " + response.getMessage());
				mLogger.debug("================ResponseUpdateAuctionStatus[ End ]================");

				if (response.getStatus().equals("success")) {

				}
			}
		}

		@Override
		public void onResponseError(String message) {
			mLogger.debug("================ResponseUpdateAuctionStatus[Start]================");
			mLogger.debug("Auction Code : " + mAuctionCode);
			mLogger.debug("Auction Round : " + mAuctionRound);
			mLogger.debug("Auction Lane Code : " + mAuctionLaneCode);
			mLogger.debug("Server Response Message : " + message);
			mLogger.debug("================ResponseUpdateAuctionStatus[ End ]================");
		}
	};

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
				mRemainTimeService.shutdownNow();
				mBiddingTimeService.shutdownNow();
				mNextEntryIntervalTimeService.shutdownNow();
				mReadyAuctionTimeService.shutdownNow();
				mDelayTimerService.shutdownNow();
				mCheckTimerService.shutdownNow();
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

	public boolean containFavoriteCarInfoMap(String entryNum, String userId) {
		boolean result = false;

		if (mFavoriteCarInfoMap.containsKey(entryNum)) {
			for (int i = 0; i < mFavoriteCarInfoMap.get(entryNum).size(); i++) {
				if (mFavoriteCarInfoMap.get(entryNum).get(i).getAuctionMemberId().equals(userId)) {
					result = true;
				}
			}
		}

		return result;
	}

	public boolean containBidAbsenteeUserInfoMap(String entryNum, String userId) {
		boolean result = false;

		if (mAbsenteeBiddingMap.containsKey(entryNum)) {
			for (int i = 0; i < mAbsenteeBiddingMap.get(entryNum).size(); i++) {
				if (mAbsenteeBiddingMap.get(entryNum).get(i).getAuctionMemberNum().equals(userId)) {
					result = true;
				}
			}
		}

		return result;
	}

	public AbsenteeUserInfo containBidAbsenteeUserInfo(String entryNum, String userId) {
		AbsenteeUserInfo resultInfo = null;

		if (mAbsenteeBiddingMap.containsKey(entryNum)) {
			for (int i = 0; i < mAbsenteeBiddingMap.get(entryNum).size(); i++) {
				if (mAbsenteeBiddingMap.get(entryNum).get(i).getAuctionMemberNum().equals(userId)) {
					resultInfo = new AbsenteeUserInfo(entryNum, "Y",
							mAbsenteeBiddingMap.get(entryNum).get(i).getBidPrice());
				}
			}
		}

		return resultInfo;
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
	 * @MethodName requestCreateAuctionServerResult
	 * @Description 경매 서버 생성 결과 정보 전송 처리
	 *
	 * @param isSuccess 경매 서버 생성 결과 (true : 성공, false : 실패)
	 */
	public void requestCreateAuctionServerResult(boolean isSuccess,
			ActionResultListener<ResponseSendSmsAuctionServerResult> actionResultSendSmsAuctionServerStatus) {
		try {
			String host = InetAddress.getLocalHost().toString();
			String result = "";
			String smsText = "";
			String successText = "경매가 정상적으로 생성 되었습니다.";
			String failText = "경매 생성에 실패하였습니다. 확인 부탁 드립니다.";
			String auctionType = "";
			String auctionRound = "";
			String auctionLaneName = "";

			if (mAuctionCode.equals(GlobalDefineCode.AUCTION_TYPE_REALTIME)) {
				auctionType = "실시간 경매 ";
			} else if (mAuctionCode.equals(GlobalDefineCode.AUCTION_TYPE_SPOT)) {
				auctionType = "SPOT 경매 ";
			}

			auctionRound = mAuctionRound + "회차 ";
			auctionLaneName = mAuctionLaneCode + "레인 ";

			failText = "[" + host + "] 서버에 생성 요청 된 " + auctionType + auctionRound + auctionLaneName
					+ "생성에 실패하였습니다. 조속히 확인 부탁 드립니다.";

			mLogger.debug("경매 생성 실패로 인하여 관리자에게 메시지를 전송 처리하였습니다.\n(" + failText + ")");

			if (isSuccess) {
				result = "Y";
				smsText = successText;
			} else {
				result = "N";
				smsText = failText;
			}

			// 경매 생성 결과 정보 전송
			ActionRuler.getInstance().addAction(new ActionRequestSendSmsAuctionResult(mAuctionCode, mAuctionRound,
					mAuctionLaneCode, result, smsText, actionResultSendSmsAuctionServerStatus));
			ActionRuler.getInstance().runNext();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @MethodName getAuctionStatus
	 * @Description 경매 서버 진행 상태 반환 처리
	 *
	 * @return status 경매 서버 상태 (10 : 대기 / 11 : 진행 / 20 : 완료)
	 */
	public String getAuctionStatus() {
		return mAuctionServerStatus;
	}

	/**
	 * 
	 * @MethodName requestUpdateAuctionStatus
	 * @Description 경매 서버 진행 상태 업데이트 요청 처리
	 *
	 * @param status 경매 서버 상태 (10 : 대기 / 11 : 진행 / 20 : 완료)
	 */
	public void requestUpdateAuctionStatus(String status) {
		try {
			// 경매 서버가 마스터 일 경우에만 수행 처리
			mAuctionServerStatus = status;

			ActionRuler.getInstance().addAction(new ActionRequestUpdateAuctionStatus(mAuctionCode, mAuctionRound,
					mAuctionLaneCode, status, mActionUpdateAuctionStatusResult));
			ActionRuler.getInstance().runNext();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 경매 남은 시간 초기화 처리
	 * 
	 * @param time 경매 남은 시간
	 * 
	 */
	public void setAuctionRemainTime(long time) {
		mAuctionState.setRemainTime(time);
	}

	/**
	 * 응찰 가격 수집 남은 시간 초기화 처리
	 * 
	 * @param time 응찰 가격 수집 남은 시간
	 * 
	 */
	public void setBiddingRemainTime(long time) {
		mAuctionState.setBiddingRemainTime(time);
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

	public String getAbsenteeBiddingTime(String userNum) {
		String result = null;

		if (mAbsenteeBiddingMap != null) {
			if (mAbsenteeBiddingMap.containsKey(mAuctionState.getEntryNum())) {
				if (mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).size() > 0) {
					for (int i = 0; i < mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).size(); i++) {
						if (mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).get(i).getAuctionMemberNum()
								.equals(userNum)) {
							result = mAbsenteeBiddingMap.get(mAuctionState.getEntryNum()).get(i).getBidDateTime();
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * 
	 */
	private void transmissionAuctionResult() {
		// 낙유찰 처리 시간은 DB 시간 기준으로 표시하기 때문에 관련 값 제거
		String currentTime = getCurrentTime("yyyyMMdd HH:mm:ss:SSS");
		mResultRecordTime = currentTime.replace(":", "").replace(" ", "");

		// 낙찰 유찰 결과 전송
		if (mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_SUCCESS)) {
			requestTransmissionAuctionResult(mAuctionState.getCurrentEntryInfo(),
					GlobalDefineCode.REQUEST_PARAM_AUCTION_RESULT_SUCCESS, mResultRecordTime,
					mAuctionState.getRank1MemberNum(), mAuctionState.getRank1MemberChannel(),
					mAuctionState.getRank1BidPrice());
		} else if (mAuctionState.getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_FAIL)) {
			requestTransmissionAuctionResult(mAuctionState.getCurrentEntryInfo(),
					GlobalDefineCode.REQUEST_PARAM_AUCTION_RESULT_FAIL, mResultRecordTime, "", "", "");
		}
	}
}
