package com.nh.share.common.models;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 설정 변경 처리
 * 
 * 제어프로그램 -> 경매서버
 * 
 * AF | 조합구분코드 | 출품번호 | 낙/유찰결과코드(01/02) | 낙찰자회원번호(거래인번호) | 낙찰자경매참가번호 | 낙찰금액
 *
 */
public class AuctionResult implements FromAuctionCommon {
	public static final char TYPE = 'F';
	private String mAuctionHouseCode; // 거점코드
	private String mEntryNum; // 출품 번호
	private String mResultCode; // 낙,유찰 결과 코드
	private String mSuccessBidder; // 낙찰자 회원번호(거래인번호)
	private String mSuccessAuctionJoinNum; // 낙찰자 경매참가번호
	private String mSuccessBidPrice; // 낙찰금액

	public AuctionResult() {
	}

	public AuctionResult(String auctionHouseCode, String entryNum, String resultCode, String successBidder,
			String successAuctionJoinNum, String successBidPrice) {
		mAuctionHouseCode = auctionHouseCode;
		mEntryNum = entryNum;
		mResultCode = resultCode;
		mSuccessBidder = successBidder;
		mSuccessAuctionJoinNum = successAuctionJoinNum;
		mSuccessBidPrice = successBidPrice;
	}

	public AuctionResult(String[] messages) {
		mAuctionHouseCode = messages[1];
		mEntryNum = messages[2];
		mResultCode = messages[3];
		mSuccessBidder = messages[4];
		mSuccessAuctionJoinNum = messages[5];
		mSuccessBidPrice = messages[6];
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getEntryNum() {
		return mEntryNum;
	}

	public void setEntryNum(String entryNum) {
		this.mEntryNum = entryNum;
	}

	public String getResultCode() {
		return mResultCode;
	}

	public void setResultCode(String resultCode) {
		this.mResultCode = resultCode;
	}

	public String getSuccessBidder() {
		return mSuccessBidder;
	}

	public void setSuccessBidder(String successBidder) {
		this.mSuccessBidder = successBidder;
	}

	public String getSuccessAuctionJoinNum() {
		return mSuccessAuctionJoinNum;
	}

	public void setSuccessAuctionJoinNum(String successAuctionJoinNum) {
		this.mSuccessAuctionJoinNum = successAuctionJoinNum;
	}

	public String getSuccessBidPrice() {
		return mSuccessBidPrice;
	}

	public void setSuccessBidPrice(String successBidPrice) {
		this.mSuccessBidPrice = successBidPrice;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER,
				mAuctionHouseCode, AuctionShareSetting.DELIMITER, mEntryNum, AuctionShareSetting.DELIMITER, mResultCode,
				AuctionShareSetting.DELIMITER, mSuccessBidder, AuctionShareSetting.DELIMITER, mSuccessAuctionJoinNum,
				AuctionShareSetting.DELIMITER, mSuccessBidPrice);
	}
}
