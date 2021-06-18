package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 관심 출품 여부에 대한 정보 전송 처리
 * 
 * 경매서버 -> 공통
 * 
 * SF | 경매거점코드 | 출품번호 | 관심 출품 상품 여부(Y/N)
 *
 */
public class FavoriteEntryInfo implements FromAuctionServer {
	public static final char TYPE = 'F';
	private String mAuctionHouseCode; // 거점코드
	private String mEntryNum; // 출품번호
	private String mFlagFavorite; // 관심차량 여부(Y/N)

	public FavoriteEntryInfo(String auctionHouseCode, String entryNum, String flagFavorite) {
		mAuctionHouseCode = auctionHouseCode;
		mEntryNum = entryNum;
		mFlagFavorite = flagFavorite;
	}

	public FavoriteEntryInfo(String[] messages) {
		mAuctionHouseCode = messages[1];
		mEntryNum = messages[2];
		mFlagFavorite = messages[3];
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

	public String getFlagFavorite() {
		return mFlagFavorite;
	}

	public void setFlagFavorite(String flagFavorite) {
		this.mFlagFavorite = flagFavorite;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mEntryNum, AuctionShareSetting.DELIMITER, mFlagFavorite);
	}
}
