package com.nh.auctionserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.setting.AuctionServerSetting;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.RetryTargetInfo;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.server.models.AuctionBidStatus;

public class AuctionState {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionState.class);

	private boolean mSettingsInformationLoaded = false;
	private boolean mEntryInformationLoaded = false;
	private boolean mEntryFavoriteCarInfoLoaded = true;
	private boolean mCheckSettingsInformation = false;
	private boolean mCheckEntryInformation = false;
	private boolean mCheckEntryFavoriteCarInfo = false;

	// 경매 시작 카운트 다운 시간
	private int mAuctionCountDownTime = 0;

	private String mEntryNum = ""; // 현재 진행 중인 출품번호
	private String mAuctionQcn = ""; // 현재 진행 중인 경매회차
	private String mStartPrice = ""; // 현재 응찰 가격
	private String mCurrentBidderCount = ""; // 현재 응찰자 수
	private String mState = GlobalDefineCode.AUCTION_STATUS_NONE; // 경매상태
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
	private String mEntryPositionCode = ""; // 출품 정보 거점 코드
	private String mFinishEntryCount = ""; // 경매 진행 완료 출품수
	private String mRemainEntryCount = ""; // 경매 잔여 출품수
	
	private RetryTargetInfo mRetryTargetInfo = null; // 재경매상태정보

	// 경매 시작 카운트 다운 상태 (R : 준비 / C : 카운트다운)
	private String mAuctionCountDownStatus = GlobalDefineCode.AUCTION_COUNT_DOWN_READY;

	// 현재 출품 차량 정보
	private EntryInfo mCurrentEntryInfo;

	// 현재 응찰 종료 상태
	private AuctionBidStatus mAuctionBidStatus;
	// 거점코드
	private String mAuctionHouseCode;
	
	// 출하안내시스템접속상태
	private boolean mIsStandConnect = false;
	
	// 일괄경매 정지 상태
	private boolean mIsAuctionPause = false;

	public AuctionState(String auctionHouseCode) {
		mAuctionHouseCode = auctionHouseCode;
	}

	public AuctionStatus getAuctionStatus() {
		AuctionStatus auctionStatus = new AuctionStatus(mAuctionHouseCode, mEntryNum, mAuctionQcn, mStartPrice, mCurrentBidderCount,
				mState, mRank1MemberNum, mRank2MemberNum, mRank3MemberNum, mFinishEntryCount, mRemainEntryCount);

		return auctionStatus;
	}

	public void setAuctionStatus(AuctionStatus auctionStatus) {
		mAuctionHouseCode = auctionStatus.getAuctionHouseCode();
		mEntryNum = auctionStatus.getEntryNum();
		mAuctionQcn = auctionStatus.getAuctionQcn();
		mStartPrice = auctionStatus.getStartPrice();
		mCurrentBidderCount = auctionStatus.getCurrentBidderCount();
		mState = auctionStatus.getState();
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

	public String getAuctionQcn() {
		return mAuctionQcn;
	}

	public void setAuctionQcn(String auctionQcn) {
		this.mAuctionQcn = auctionQcn;
	}

	public String getStartPrice() {
		return mStartPrice;
	}

	public void setStartPrice(String startPrice) {
		this.mStartPrice = startPrice;

		mLogger.debug("시작 가격을 " + mStartPrice + "만원으로 설정");
		AuctionServerSetting.AUCTION_START_PRICE = Integer.valueOf(mStartPrice);
	}

	public String getCurrentBidderCount() {
		return mCurrentBidderCount;
	}

	public void setCurrentBidderCount(String currentBidderCount) {
		this.mCurrentBidderCount = currentBidderCount;
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

	public String getEntryPositionCode() {
		return mEntryPositionCode;
	}

	public void setEntryPositionCode(String entryPositionCode) {
		this.mEntryPositionCode = entryPositionCode;
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

	public void setCheckSettingsInformation() {
		mCheckSettingsInformation = true;
	}

	public void setCheckEntryInformation() {
		mCheckEntryInformation = true;
	}

	public void setCheckFavoriteCarInfo() {
		mCheckEntryFavoriteCarInfo = true;
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
		mLogger.debug("================CheckAuctionData[ End ]================");

		if (mCheckSettingsInformation && mCheckEntryInformation && mCheckEntryFavoriteCarInfo) {
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
		mLogger.debug("================AuctionDataLoadingCompleted[ End ]================");

		if (mSettingsInformationLoaded && mEntryInformationLoaded && mEntryFavoriteCarInfoLoaded) {
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
	public void onStart() {
		this.mState = GlobalDefineCode.AUCTION_STATUS_START;
	}

	/**
	 * 
	 * @MethodName onPass
	 * @Description 경매 출품 건 강제 유찰 상테
	 *
	 */
	public void onPass() {
		this.mState = GlobalDefineCode.AUCTION_STATUS_PASS;
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
	public void onAuctionCountDownReady(int countDown) {
		this.mAuctionCountDownTime = countDown;
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
	 * @MethodName getCurrentEntryInfo
	 * @Description 현재 출품 정보 반환 처리
	 *
	 * @return EntryInfo 현재 출품 정보
	 */
	public EntryInfo getCurrentEntryInfo() {
		return mCurrentEntryInfo;
	}

	/**
	 * 
	 * @MethodName getAuctionCountDownStatus
	 * @Description 현재 경매 카운트 다운 상태 반환 처리
	 *
	 * @return String 현재 경매 카운트 다운 상태
	 */
	public String getAuctionCountDownStatus() {
		return mAuctionCountDownStatus;
	}

	/**
	 * 
	 * @MethodName getAuctionCountDownTime
	 * @Description 현재 경매 카운트 다운 남은 시간 반환 처리
	 *
	 * @return int 현재 경매 카운트 다운 시간
	 */
	public int getAuctionCountDownTime() {
		int remainSecond = mAuctionCountDownTime;

		return remainSecond;
	}

	/**
	 * 
	 * @MethodName setAuctionCountDownTime
	 * @Description 경매 카운트 다운 남은 시간 반환 처리
	 *
	 * @return int 현재 경매 카운트 다운 시간
	 */
	public void setAuctionCountDownTime(int second) {
		mAuctionCountDownTime = second;
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
	 * @param entryInfo 출품 정보
	 */
	public void setCurrentEntryInfo(EntryInfo entryInfo) {
		this.mCurrentEntryInfo = entryInfo;

		mLogger.debug("setCurrentEntryInfo : " + mCurrentEntryInfo.getEncodedMessage());
		mLogger.debug("setIsRetryTargetAuctioin : " + false);
		
		// 재경매상태정보 초기화
		setRetryTargetInfo(null);
		
		this.mEntryNum = mCurrentEntryInfo.getEntryNum();
		this.mStartPrice = String.valueOf(mCurrentEntryInfo.getLowPrice());
		this.mFinishEntryCount = String.valueOf(AuctionServerSetting.AUCTION_ENTRY_FINISH_COUNT + 1);
		AuctionServerSetting.AUCTION_ENTRY_FINISH_COUNT = Integer.valueOf(mFinishEntryCount);
		this.mRemainEntryCount = String.valueOf(AuctionServerSetting.AUCTION_ENTRY_TOTAL_COUNT
				- Long.valueOf(AuctionServerSetting.AUCTION_ENTRY_FINISH_COUNT));
		AuctionServerSetting.AUCTION_ENTRY_REMAIN_COUNT = Integer.valueOf(mRemainEntryCount);
	}
	
	public RetryTargetInfo getRetryTargetInfo() {
		return mRetryTargetInfo;
	}

	public void setRetryTargetInfo(RetryTargetInfo retryTargetInfo) {
		mRetryTargetInfo = retryTargetInfo;
	}
	
	public AuctionBidStatus getAuctionBidStatus() {
		return mAuctionBidStatus;
	}
	
	public void setAuctionBidStatus(AuctionBidStatus auctionBidStatus) {
		mAuctionBidStatus = auctionBidStatus;
	}
	
	public boolean getIsStandConnect() {
		return mIsStandConnect;
	}
	
	public void setIsStandConnect(boolean isStandConnect) {
		mIsStandConnect = isStandConnect;
	}
	
	public boolean getIsAuctionPause() {
		return mIsAuctionPause;
	}
	
	public void setIsAuctionPause(boolean isPause) {
		mIsAuctionPause = isPause;
	}
}
