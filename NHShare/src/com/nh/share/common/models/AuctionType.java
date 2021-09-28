package com.nh.share.common.models;

import java.io.Serializable;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 유형 정보 전송
 * <p>
 * 경매서버 / 제어프로그램 -> 공통
 * <p>
 * AT | 조합구분코드 | 경매유형코드
 */
public class AuctionType implements FromAuctionCommon, Serializable {
	public static final char TYPE = 'T';
	private String mAuctionHouseCode; // 조합구분코드
	private String mAuctionType; // 경매유형코드

	public AuctionType() {
	}

	public AuctionType(String auctionHouseCode, String auctionType) {
		mAuctionHouseCode = auctionHouseCode;
		mAuctionType = auctionType;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getAuctionType() {
		return mAuctionType;
	}

	public void setAuctionType(String auctionType) {
		this.mAuctionType = auctionType;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode,
				AuctionShareSetting.DELIMITER, mAuctionType);
	}
}
