package com.nh.auctionserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.setting.AuctionServerSetting;
import com.nh.share.api.model.AuctionEntryInformationResult;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.server.models.AuctionStatus;

public class AuctionState {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionState.class);

	private Auctioneer mAuctioneer;

	private boolean mSettingsInformationLoaded = false;
	private boolean mEntryInformationLoaded = false;
	private boolean mEntryFavoriteCarInfoLoaded = true;
	private boolean mAbsenteeAuctionBidInfoLoaded = false;
	private boolean mCheckSettingsInformation = false;
	private boolean mCheckEntryInformation = false;
	private boolean mCheckEntryFavoriteCarInfo = false;
	private boolean mCheckAbsenteeAuctionBidInfo = false;

	private long mRemainMilliSeconds = AuctionServerSetting.AUCTION_TIME; // 경매 진행 남은 시간
	private long mBiddingRemainMilliSeconds = AuctionServerSetting.DEFAULT_CHECK_DELAY_TIME; // 경매 응찰 정보 수집 남은 시간

	// 경매 시작 카운트 다운 시간
	private int mAuctionCountDownTime = AuctionServerSetting.COUNT_DOWN_TIME;

	private String mEntryNum; // 현재 진행 중인 출품번호
	private String mEntrySeqNum; // 현재 진행 중인 출품순번
	private String mCurrentPrice; // 현재 응찰 가격
	private String mCurrentBidderCount; // 현재 응찰자 수
	private String mState = GlobalDefineCode.AUCTION_STATUS_NONE; // 경매상태
	private String mNextPrice; // 다음 응찰 가격
	private String mRank1MemberNum = ""; // 1순위 회원번호
	private String mRank2MemberNum = ""; // 2순위 회원번호
	private String mRank3MemberNum = ""; // 3순위 회원번호
	private String mRank4MemberNum = ""; // 4순위 회원번호
	private String mRank5MemberNum = ""; // 5순위 회원번호
	private String mRank1MemberChannel = ""; // 1순위 접속채널
	private String mRank2MemberChannel = ""; // 2순위 접속채널
	private String mRank3MemberChannel = ""; // 3순위 접속채널
	private String mRank4MemberChannel = ""; // 4순위 접속채널
	private String mRank5MemberChannel = ""; // 5순위 접속채널
	private String mRank1BidPrice = ""; // 1순위 응찰가
	private String mRank2BidPrice = ""; // 2순위 응찰가
	private String mRank3BidPrice = ""; // 3순위 응찰가
	private String mRank4BidPrice = ""; // 4순위 응찰가
	private String mRank5BidPrice = ""; // 5순위 응찰가
	private String mNextEntryNum; // 다음 출품 번호
	private String mNextEntrySeqNum; // 다음 출품 순번
	private String mNextEntryPositionCode; // 다음 출품 차량 거점 코드
	private String mNextEntryCarInfo; // 다음 출품 차량 정보
	private String mNextEntryTtsInfo; // 다음 출품 차량 TTS 정보
	private String mNextEntryImageInfo; // 다음 출품 차량 이미지 정보
	private String mNextEntryEvalImageInfo; // 다음 출품 차량 차량 전개도 이미지 정보
	private String mFinishEntryCount; // 경매 진행 완료 출품수
	private String mRemainEntryCount; // 경매 잔여 출품수

	// 경매 시작 카운트 다운 상태 (R : 준비 / C : 카운트다운)
	private String mAuctionCountDownStatus = GlobalDefineCode.AUCTION_COUNT_DOWN_READY;

	// 현재 출품 차량 정보
	private AuctionEntryInformationResult mCurrentEntryInfo;

	public AuctionState(Auctioneer auctioneer) {
		mAuctioneer = auctioneer;
	}

	public AuctionStatus getAuctionStatus() {
		AuctionStatus auctionStatus = new AuctionStatus(mEntryNum, mCurrentPrice, mCurrentBidderCount, mState,
				mNextPrice, String.valueOf(mRemainMilliSeconds), mRank1MemberNum, mRank2MemberNum, mRank3MemberNum,
				mFinishEntryCount, mRemainEntryCount);

		return auctionStatus;
	}

	public void setAuctionStatus(AuctionStatus auctionStatus) {
		mEntryNum = auctionStatus.getEntryNum();
		mCurrentPrice = auctionStatus.getCurrentPrice();
		mCurrentBidderCount = auctionStatus.getCurrentBidderCount();
		mState = auctionStatus.getState();
		mNextPrice = auctionStatus.getNextPrice();
		mRemainMilliSeconds = Long.valueOf(auctionStatus.getTime());
		mRank1MemberNum = auctionStatus.getRank1MemberNum();
		mRank2MemberNum = auctionStatus.getRank1MemberNum();
		mRank3MemberNum = auctionStatus.getRank1MemberNum();
		mFinishEntryCount = auctionStatus.getFinishEntryCount();
		mRemainEntryCount = auctionStatus.getRemainEntryCount();
	}

	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	public String getEntrySeqNum() {
		return mEntrySeqNum;
	}

	public void setEntrySeqNum(String entrySeqNum) {
		this.mEntrySeqNum = entrySeqNum;
	}

	public String getCurrentPrice() {
		return mCurrentPrice;
	}

	public void setCurrentPrice(String currentPrice) {
		this.mCurrentPrice = currentPrice;

		mLogger.debug("setCurrentPrice : " + mCurrentPrice);
		mLogger.debug("현재 가격을 " + mCurrentPrice + "만원으로 설정");

		// 현재가 기준 상승 가격 확인
		if (Integer.valueOf(mCurrentPrice) >= AuctionServerSetting.AUCTION_MAX_BASE_PRICE) {
			AuctionServerSetting.AUCTION_CURRENT_RISING_PRICE = AuctionServerSetting.AUCTION_MAX_RISING_PRICE;
		} else if (Integer.valueOf(mCurrentPrice) >= AuctionServerSetting.AUCTION_BASE_PRICE) {
			AuctionServerSetting.AUCTION_CURRENT_RISING_PRICE = AuctionServerSetting.AUCTION_MORE_RISING_PRICE;
		} else {
			AuctionServerSetting.AUCTION_CURRENT_RISING_PRICE = AuctionServerSetting.AUCTION_BELOW_RISING_PRICE;
		}

		this.mNextPrice = String
				.valueOf((Integer.valueOf(mCurrentPrice) + AuctionServerSetting.AUCTION_CURRENT_RISING_PRICE));
	}

	public String getCurrentBidderCount() {
		return mCurrentBidderCount;
	}

	public void setCurrentBidderCount(String currentBidderCount) {
		this.mCurrentBidderCount = currentBidderCount;
	}

	public String getNextPrice() {
		return mNextPrice;
	}

	public void setNextPrice(String nextPrice) {
		this.mNextPrice = nextPrice;
	}

	public String getRank1MemberNum() {
		return mRank1MemberNum;
	}

	public void setRank1MemberNum(String rank1MemberNum) {
		this.mRank1MemberNum = rank1MemberNum;
	}

	public String getRank2MemberNum() {
		return mRank2MemberNum;
	}

	public void setRank2MemberNum(String rank2MemberNum) {
		this.mRank2MemberNum = rank2MemberNum;
	}

	public String getRank3MemberNum() {
		return mRank3MemberNum;
	}

	public void setRank3MemberNum(String rank3MemberNum) {
		this.mRank3MemberNum = rank3MemberNum;
	}

	public String getRank4MemberNum() {
		return mRank4MemberNum;
	}

	public void setRank4MemberNum(String rank4MemberNum) {
		this.mRank4MemberNum = rank4MemberNum;
	}

	public String getRank5MemberNum() {
		return mRank5MemberNum;
	}

	public void setRank5MemberNum(String rank5MemberNum) {
		this.mRank5MemberNum = rank5MemberNum;
	}

	public String getRank1MemberChannel() {
		return mRank1MemberChannel;
	}

	public void setRank1MemberChannel(String rank1MemberChannel) {
		this.mRank1MemberChannel = rank1MemberChannel;
	}

	public String getRank2MemberChannel() {
		return mRank2MemberChannel;
	}

	public void setRank2MemberChannel(String rank2MemberChannel) {
		this.mRank2MemberChannel = rank2MemberChannel;
	}

	public String getRank3MemberChannel() {
		return mRank3MemberChannel;
	}

	public void setRank3MemberChannel(String rank3MemberChannel) {
		this.mRank3MemberChannel = rank3MemberChannel;
	}

	public String getRank4MemberChannel() {
		return mRank4MemberChannel;
	}

	public void setRank4MemberChannel(String rank4MemberChannel) {
		this.mRank4MemberChannel = rank4MemberChannel;
	}

	public String getRank5MemberChannel() {
		return mRank5MemberChannel;
	}

	public void setRank5MemberChannel(String rank5MemberChannel) {
		this.mRank5MemberChannel = rank5MemberChannel;
	}

	public String getRank1BidPrice() {
		return mRank1BidPrice;
	}

	public void setRank1BidPrice(String rank1BidPrice) {
		this.mRank1BidPrice = rank1BidPrice;
	}

	public String getRank2BidPrice() {
		return mRank2BidPrice;
	}

	public void setRank2BidPrice(String rank2BidPrice) {
		this.mRank2BidPrice = rank2BidPrice;
	}

	public String getRank3BidPrice() {
		return mRank3BidPrice;
	}

	public void setRank3BidPrice(String rank3BidPrice) {
		this.mRank3BidPrice = rank3BidPrice;
	}

	public String getRank4BidPrice() {
		return mRank4BidPrice;
	}

	public void setRank4BidPrice(String rank4BidPrice) {
		this.mRank4BidPrice = rank4BidPrice;
	}

	public String getRank5BidPrice() {
		return mRank5BidPrice;
	}

	public void setRank5BidPrice(String rank5BidPrice) {
		this.mRank5BidPrice = rank5BidPrice;
	}

	public String getNextEntryNum() {
		return mNextEntryNum;
	}

	public void setNextEntryNum(String nextEntryNum) {
		this.mNextEntryNum = nextEntryNum;
	}

	public String getNextEntrySeqNum() {
		return mNextEntrySeqNum;
	}

	public void setNextEntrySeqNum(String nextEntrySeqNum) {
		this.mNextEntrySeqNum = nextEntrySeqNum;
	}

	public String getNextEntryPositionCode() {
		return mNextEntryPositionCode;
	}

	public void setNextEntryPositionCode(String nextEntryPositionCode) {
		this.mNextEntryPositionCode = nextEntryPositionCode;
	}

	public String getNextEntryCarName() {
		return mNextEntryCarInfo;
	}

	public void setNextEntryCarName(String nextEntryCarName) {
		this.mNextEntryCarInfo = nextEntryCarName;
	}

	public String getNextEntryTtsInfo() {
		return mNextEntryTtsInfo;
	}

	public void setNextEntryTtsInfo(String nextEntryTtsInfo) {
		this.mNextEntryTtsInfo = nextEntryTtsInfo;
	}

	public String getNextEntryImageInfo() {
		return mNextEntryImageInfo;
	}

	public void setNextEntryImageInfo(String nextEntryImageInfo) {
		this.mNextEntryImageInfo = nextEntryImageInfo;
	}

	public String getNextEntryEvalImageInfo() {
		return mNextEntryEvalImageInfo;
	}

	public void setNextEntryEvalImageInfo(String nextEntryEvalImageInfo) {
		this.mNextEntryEvalImageInfo = nextEntryEvalImageInfo;
	}

	public String getFinishEntryCount() {
		return mFinishEntryCount;
	}

	public void setFinishEntryCount(String finishEntryCount) {
		this.mFinishEntryCount = finishEntryCount;
	}

	public String getRemainEntryCount() {
		return mRemainEntryCount;
	}

	public void setRemainEntryCount(String remainEntryCount) {
		this.mRemainEntryCount = remainEntryCount;
	}

	public void setAuctionState(String auctionState) {
		mState = auctionState;
	}

	public String getAuctionState() {
		return mState;
	}

	public void setSettingsInformationLoad() {
		mSettingsInformationLoaded = true;
	}

	public void setEntryInformationLoad() {
		mEntryInformationLoaded = true;
	}

	public boolean getEntryInformationLoad() {
		return mEntryInformationLoaded;
	}

	public void setFavoriteCarInfoLoad() {
		mEntryFavoriteCarInfoLoaded = true;
	}

	public void setAbsenteeAuctionBidInfoLoad() {
		mAbsenteeAuctionBidInfoLoaded = true;
	}

	public void setCheckSettingsInformation() {
		mCheckSettingsInformation = true;
	}

	public void setCheckEntryInformation() {
		mCheckEntryInformation = true;
	}

	public void setCheckFavoriteCarInfo() {
		mCheckEntryFavoriteCarInfo = true;
	}

	public void setCheckAbsenteeAuctionBidInfo() {
		mCheckAbsenteeAuctionBidInfo = true;
	}

	/**
	 * 
	 * @MethodName isCheckAuctionData
	 * @Description 경매 기초 데이터 확인 여부 반환 처리
	 *
	 * @return Boolean 확인 완료 여부
	 */
	public boolean isCheckAuctionData() {
		boolean result = false;

		mLogger.debug("================CheckAuctionData[Start]================");
		mLogger.debug("경매 설정 정보 확인 여부 : " + mCheckSettingsInformation);
		mLogger.debug("경매 출품 정보 확인 여부 : " + mCheckEntryInformation);
		mLogger.debug("경매 관심차량 정보 확인 여부 : " + mCheckEntryFavoriteCarInfo);
		mLogger.debug("경매 부재자 입찰 정보 확인 여부 : " + mCheckAbsenteeAuctionBidInfo);
		mLogger.debug("================CheckAuctionData[ End ]================");

		if (mCheckSettingsInformation && mCheckEntryInformation && mCheckEntryFavoriteCarInfo
				&& mCheckAbsenteeAuctionBidInfo) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	/**
	 * 
	 * @MethodName isAuctionDataLoadingCompleted
	 * @Description 경매 기초 데이터 로딩 완료 여부에 대한 반환 처리
	 *
	 * @return Boolean 로딩 완료 여부
	 */
	public boolean isAuctionDataLoadingCompleted() {
		boolean result = false;
		mLogger.debug("================AuctionDataLoadingCompleted[Start]================");
		mLogger.debug("경매 설정 정보 로딩 상태 : " + mSettingsInformationLoaded);
		mLogger.debug("경매 출품 정보 로딩 상태 : " + mEntryInformationLoaded);
		mLogger.debug("경매 관심차량 정보 로딩 상태 : " + mEntryFavoriteCarInfoLoaded);
		mLogger.debug("경매 부재자 입찰 정보 로딩 상태 : " + mAbsenteeAuctionBidInfoLoaded);
		mLogger.debug("================AuctionDataLoadingCompleted[ End ]================");

		if (mSettingsInformationLoaded && mEntryInformationLoaded && mEntryFavoriteCarInfoLoaded
				&& mAbsenteeAuctionBidInfoLoaded) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	/**
	 * 
	 * @MethodName onReady
	 * @Description 경매 준비 상태
	 *
	 */
	public void onReady() {
		this.mState = GlobalDefineCode.AUCTION_STATUS_READY;
	}

	/**
	 * 
	 * @MethodName onStart
	 * @Description 경매 시작 상태
	 * 
	 * @return boolean 자동 상승 처리 필요 여부
	 *
	 */
	public boolean onStart() {
		if (AuctionServerSetting.AUCTION_AUTO_RISE_COUNT > 0) {
			this.mState = GlobalDefineCode.AUCTION_STATUS_SLOWDOWN;

			return true;
		} else {
			this.mState = GlobalDefineCode.AUCTION_STATUS_START;

			return false;
		}

	}

	/**
	 * 
	 * @MethodName onStop
	 * @Description 경매 정지 상태
	 *
	 */
	public void onStop() {
		this.mState = GlobalDefineCode.AUCTION_STATUS_STOP;
	}

	/**
	 * 
	 * @MethodName onSlowDown
	 * @Description 경매 자동 상승 상태
	 *
	 */
	public void onSlowDown() {
		this.mState = GlobalDefineCode.AUCTION_STATUS_SLOWDOWN;
	}

	/**
	 * 
	 * @MethodName onProgress
	 * @Description 경매 진행 상태
	 *
	 */
	public void onProgress() {
		this.mState = GlobalDefineCode.AUCTION_STATUS_PROGRESS;
	}

	/**
	 * 
	 * @MethodName onCompetitive
	 * @Description 경매 경쟁 상태
	 *
	 */
	public void onCompetitive() {
		this.mState = GlobalDefineCode.AUCTION_STATUS_COMPETITIVE;
	}

	/**
	 * 
	 * @MethodName onSuccess
	 * @Description 경매 낙찰 상태
	 *
	 */
	public void onSuccess() {
		this.mState = GlobalDefineCode.AUCTION_STATUS_SUCCESS;
	}

	/**
	 * 
	 * @MethodName onFail
	 * @Description 경매 유찰 상태
	 *
	 */
	public void onFail() {
		this.mState = GlobalDefineCode.AUCTION_STATUS_FAIL;
	}

	/**
	 * 
	 * @MethodName onCompleted
	 * @Description 경매 출품 단위 완료 상태
	 *
	 */
	public void onCompleted() {
		this.mState = GlobalDefineCode.AUCTION_STATUS_COMPLETED;
	}

	/**
	 * 
	 * @MethodName onFinish
	 * @Description 경매 종료 상태
	 *
	 */
	public void onFinish() {
		this.mState = GlobalDefineCode.AUCTION_STATUS_FINISH;
	}

	/**
	 * 
	 * @MethodName onAuctionCountDownReady
	 * @Description 경매 시작 카운트 다운 준비 상태
	 *
	 */
	public void onAuctionCountDownReady() {
		this.mAuctionCountDownStatus = GlobalDefineCode.AUCTION_COUNT_DOWN_READY;
	}

	/**
	 * 
	 * @MethodName onAuctionCountDown
	 * @Description 경매 시작 카운트 다운 상태
	 *
	 */
	public void onAuctionCountDown() {
		this.mAuctionCountDownStatus = GlobalDefineCode.AUCTION_COUNT_DOWN;
	}

	/**
	 * 
	 * @MethodName onAuctionCountDownCompleted
	 * @Description 경매 시작 카운트 다운 완료 상태
	 *
	 */
	public void onAuctionCountDownCompleted() {
		this.mAuctionCountDownStatus = GlobalDefineCode.AUCTION_COUNT_DOWN_COMPLETED;
	}

	/**
	 * 
	 * @MethodName setRemainTime
	 * @Description 경매 진행 남은 시간 설정 처리
	 *
	 * @param seconds
	 */
	public void setRemainTime(long seconds) {
		this.mRemainMilliSeconds = seconds;
	}

	/**
	 * 
	 * @MethodName getRemainTime
	 * @Description 경매 진행 남은 시간 반환 처리
	 *
	 * @return Long 경매 남은 시간(ms)
	 */
	public long getRemainTime() {
		return mRemainMilliSeconds;
	}

	/**
	 * 
	 * @MethodName decreaseRemainTime
	 * @Description 경매 남은 시간 감소 처리
	 *
	 */
	public void decreaseRemainTime() {
		if (mRemainMilliSeconds > 0) {
			this.mRemainMilliSeconds = mRemainMilliSeconds - AuctionServerSetting.REMAIN_CHECK_DELAY_TIME;
		}

		// mLogger.debug("Auction resetRemainTime : " + this.mRemainMilliSeconds);
	}

	/**
	 * 
	 * @MethodName resetRemainTime
	 * @Description 경매 남은 시간 Reset 처리
	 *
	 */
	public void resetRemainTime() {
		// 경매 진행 혹은 경쟁 상태에서 현재가격이 희망가 이상일 경우 낙/유찰 지연 시간으로 경매 진행 시간 변경 처리
		if ((getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_PROGRESS)
				|| getAuctionState().equals(GlobalDefineCode.AUCTION_STATUS_COMPETITIVE))
				&& Integer.valueOf(getCurrentPrice()) > Integer.valueOf(getCurrentEntryInfo().getAuctionHopePrice())) {
			this.mRemainMilliSeconds = AuctionServerSetting.AUCTION_DETERMINE_TIME
					+ AuctionServerSetting.DEFAULT_CHECK_DELAY_TIME;
		} else {
			this.mRemainMilliSeconds = AuctionServerSetting.AUCTION_TIME
					+ AuctionServerSetting.DEFAULT_CHECK_DELAY_TIME;
		}

		mLogger.debug("Auction resetRemainTime : " + this.mRemainMilliSeconds);
	}

	/**
	 * 
	 * @MethodName setBiddingRemainTime
	 * @Description 경매 응찰 정보 수집 남은 시간 설정 처리
	 *
	 * @param seconds 남은 시간(ms)
	 */
	public void setBiddingRemainTime(long seconds) {
		this.mBiddingRemainMilliSeconds = seconds;
		mLogger.debug("Set BiddingRemainMilliSeconds : " + this.mBiddingRemainMilliSeconds);
	}

	/**
	 * 
	 * @MethodName getBiddingRemainTime
	 * @Description 경매 응찰 정보 수집 남은 시간 반환 처리
	 *
	 * @return Long 남은 시간(ms)
	 */
	public long getBiddingRemainTime() {
		return mBiddingRemainMilliSeconds;
	}

	/**
	 * 
	 * @MethodName decreaseBiddingRemainTime
	 * @Description 경매 응찰 정보 수집 남은 시간 감소 처리
	 *
	 */
	public void decreaseBiddingRemainTime() {
		if (mBiddingRemainMilliSeconds > 0) {
			this.mBiddingRemainMilliSeconds = mBiddingRemainMilliSeconds - AuctionServerSetting.REMAIN_CHECK_DELAY_TIME;
		}
		mLogger.debug("decreaseBiddingRemainTime : " + this.mBiddingRemainMilliSeconds);
	}

	/**
	 * 
	 * @MethodName resetBiddingRemainTime
	 * @Description 경매 응찰 정보 수집 시간 Reset 처리
	 *
	 */
	public void resetBiddingRemainTime() {
		this.mBiddingRemainMilliSeconds = AuctionServerSetting.DEFAULT_CHECK_DELAY_TIME;
		mLogger.debug("resetBiddingRemainTime : " + this.mBiddingRemainMilliSeconds);
	}

	/**
	 * 
	 * @MethodName getCurrentEntryInfo
	 * @Description 현재 출품 정보 반환 처리
	 *
	 * @return AuctionEntryInformationResult 현재 출품 정보
	 */
	public AuctionEntryInformationResult getCurrentEntryInfo() {
		return mCurrentEntryInfo;
	}

	/**
	 * 
	 * @MethodName getAuctionCountDownStatus
	 * @Description 현재 경매 시작 카운트 다운 상태 반환 처리
	 *
	 * @return String 현재 경매 시작 카운트 다운 상태
	 */
	public String getAuctionCountDownStatus() {
		return mAuctionCountDownStatus;
	}

	/**
	 * 
	 * @MethodName getAuctionCountDownTime
	 * @Description 현재 경매 시작 카운트 다운 남은 시간 반환 처리
	 *
	 * @return int 현재 경매 시작 카운트 다운 시간
	 */
	public int getAuctionCountDownTime() {
		int remainSecond = mAuctionCountDownTime;

		return remainSecond;
	}

	/**
	 * 
	 * @MethodName decreaseAuctionCountDownTime
	 * @Description 경매 시작 카운트 다운 남은 시간 감소 처리
	 *
	 */
	public void decreaseAuctionCountDownTime() {
		mAuctionCountDownTime--;
	}

	/**
	 * 
	 * @MethodName setCurrentEntryInfo
	 * @Description 현재 출품 정보 설정 처리
	 *
	 * @param entryInfo   출품 정보
	 * @param isLastEntry 마지막 출품 정보 여부
	 */
	public void setCurrentEntryInfo(AuctionEntryInformationResult entryInfo, boolean isLastEntry) {
		this.mCurrentEntryInfo = entryInfo;

		resetRemainTime();

		// 출품 차량의 시작가 기준으로 상승 가격 설정 처리
		if (Integer.valueOf(entryInfo.getAuctionStartPrice()) >= AuctionServerSetting.AUCTION_MAX_BASE_PRICE) {
			AuctionServerSetting.AUCTION_CURRENT_RISING_PRICE = AuctionServerSetting.AUCTION_MAX_RISING_PRICE;
		} else if (Integer.valueOf(entryInfo.getAuctionStartPrice()) >= AuctionServerSetting.AUCTION_BASE_PRICE) {
			AuctionServerSetting.AUCTION_CURRENT_RISING_PRICE = AuctionServerSetting.AUCTION_MORE_RISING_PRICE;
		} else {
			AuctionServerSetting.AUCTION_CURRENT_RISING_PRICE = AuctionServerSetting.AUCTION_BELOW_RISING_PRICE;
		}

		this.mEntryNum = mCurrentEntryInfo.getAuctionEntryNum();
		this.mEntrySeqNum = mCurrentEntryInfo.getAuctionEntrySeq();
		this.mCurrentPrice = mCurrentEntryInfo.getAuctionStartPrice();
		this.mNextPrice = String.valueOf((Integer.valueOf(mCurrentEntryInfo.getAuctionStartPrice())
				+ AuctionServerSetting.AUCTION_CURRENT_RISING_PRICE));

		this.mFinishEntryCount = String.valueOf(AuctionServerSetting.AUCTION_ENTRY_FINISH_COUNT + 1);
		AuctionServerSetting.AUCTION_ENTRY_FINISH_COUNT = Integer.valueOf(mFinishEntryCount);
		this.mRemainEntryCount = String.valueOf(AuctionServerSetting.AUCTION_ENTRY_TOTAL_COUNT
				- Long.valueOf(AuctionServerSetting.AUCTION_ENTRY_FINISH_COUNT));
		AuctionServerSetting.AUCTION_ENTRY_REMAIN_COUNT = Integer.valueOf(mRemainEntryCount);

		if (isLastEntry) {
			this.mNextEntryNum = null;
			this.mNextEntrySeqNum = null;
			this.mNextEntryPositionCode = null;
			this.mNextEntryCarInfo = null;
			this.mNextEntryTtsInfo = null;
			this.mNextEntryImageInfo = null;
			this.mNextEntryEvalImageInfo = null;

			this.mCurrentEntryInfo.setNextEntryNum(mNextEntryNum);
			this.mCurrentEntryInfo.setNextEntrySeqNum(mNextEntrySeqNum);
			this.mCurrentEntryInfo.setNextEntryPositionCode(mNextEntryPositionCode);
			this.mCurrentEntryInfo.setNextEntryCarName(mNextEntryCarInfo);
			this.mCurrentEntryInfo.setNextEntryTtsInfo(mNextEntryTtsInfo);
			this.mCurrentEntryInfo.setNextEntryImageInfo(mNextEntryImageInfo);
			this.mCurrentEntryInfo.setNextEntryEvalImageInfo(mNextEntryEvalImageInfo);
		} else {
			this.mNextEntrySeqNum = String.valueOf(Integer.valueOf(mEntrySeqNum) + 1);
			this.mNextEntrySeqNum = validEntrySeqNum(mNextEntrySeqNum);

			if (mNextEntrySeqNum != null) {
				this.mNextEntryPositionCode = mAuctioneer.getEntryCarInfo(mNextEntrySeqNum).getAuctionPositionCode();
				this.mNextEntryNum = mAuctioneer.getEntryCarInfo(mNextEntrySeqNum).getAuctionEntryNum();
				this.mNextEntryCarInfo = "[" + mAuctioneer.getEntryCarInfo(mNextEntrySeqNum).getVendorName() + "]"
						+ mAuctioneer.getEntryCarInfo(mNextEntrySeqNum).getCarName();

				this.mNextEntryTtsInfo = mAuctioneer.getEntryCarInfo(mNextEntrySeqNum).getTtsFilePath();

				if (mAuctioneer.getEntryCarInfo(mNextEntrySeqNum).getCarImageList().size() > 0) {
					this.mNextEntryImageInfo = mAuctioneer.getEntryCarInfo(mNextEntrySeqNum).getCarImageList().get(0)
							.getImageFileName();
				} else {
					this.mNextEntryImageInfo = null;
				}

				this.mNextEntryEvalImageInfo = mAuctioneer.getEntryCarInfo(mNextEntrySeqNum).getCarEvalLayoutImage();

				this.mCurrentEntryInfo.setNextEntryNum(mNextEntryNum);
				this.mCurrentEntryInfo.setNextEntrySeqNum(mNextEntrySeqNum);
				this.mCurrentEntryInfo.setNextEntryPositionCode(mNextEntryPositionCode);
				this.mCurrentEntryInfo.setNextEntryCarName(mNextEntryCarInfo);
				this.mCurrentEntryInfo.setNextEntryTtsInfo(mNextEntryTtsInfo);
				this.mCurrentEntryInfo.setNextEntryImageInfo(mNextEntryImageInfo);
				this.mCurrentEntryInfo.setNextEntryEvalImageInfo(mNextEntryEvalImageInfo);
			}
		}
	}

	public AuctionEntryInformationResult getResponseCarInfo(AuctionEntryInformationResult entryInfo) {
		String selectSeqNum;

		selectSeqNum = String.valueOf(Integer.valueOf(entryInfo.getAuctionEntrySeq()) + 1);
		selectSeqNum = validEntrySeqNum(selectSeqNum);

		if (selectSeqNum != null) {
			entryInfo.setNextEntryPositionCode(mAuctioneer.getEntryCarInfo(selectSeqNum).getAuctionPositionCode());
			entryInfo.setNextEntryNum(mAuctioneer.getEntryCarInfo(selectSeqNum).getAuctionEntryNum());
			entryInfo.setNextEntryCarName("[" + mAuctioneer.getEntryCarInfo(selectSeqNum).getVendorName() + "]"
					+ mAuctioneer.getEntryCarInfo(selectSeqNum).getCarName());
			entryInfo.setNextEntryTtsInfo(mAuctioneer.getEntryCarInfo(selectSeqNum).getTtsFilePath());

			if (mAuctioneer.getEntryCarInfo(selectSeqNum).getCarImageList().size() > 0) {
				entryInfo.setNextEntryImageInfo(
						mAuctioneer.getEntryCarInfo(selectSeqNum).getCarImageList().get(0).getImageFileName());
			} else {
				entryInfo.setNextEntryImageInfo(null);
			}

			entryInfo.setNextEntryEvalImageInfo(mAuctioneer.getEntryCarInfo(selectSeqNum).getCarEvalLayoutImage());
			entryInfo.setNextEntrySeqNum(selectSeqNum);
		}

		return entryInfo;
	}

	/**
	 * 
	 * @MethodName validEntryNum
	 * @Description 출품 번호가 유효한지 확인
	 *
	 * @param entryNum
	 * @return entryNum 유효한 출품 번호 반환
	 */
	private String validEntryNum(String entryNum) {
		int remainEntryCount = Integer.valueOf(mRemainEntryCount);

		for (int i = 0; i < remainEntryCount; i++) {
			if (mAuctioneer.getEntryCarInfo(entryNum) != null) {
				return entryNum;
			} else {
				entryNum = String.valueOf(Integer.valueOf(entryNum) + 1);
			}
		}

		return null;
	}

	/**
	 * 
	 * @MethodName validEntrySeqNum
	 * @Description 출품 순번이 유효한지 확인
	 *
	 * @param entrySeqNum
	 * @return entrySeqNum 유효한 출품 순번 반환
	 */
	private String validEntrySeqNum(String entrySeqNum) {
		int remainEntryCount = Integer.valueOf(mRemainEntryCount);

		for (int i = 0; i < remainEntryCount; i++) {
			if (mAuctioneer.getEntryCarInfo(entrySeqNum) != null) {
				return entrySeqNum;
			} else {
				entrySeqNum = String.valueOf(Integer.valueOf(entrySeqNum) + 1);
			}
		}

		return null;
	}
}
