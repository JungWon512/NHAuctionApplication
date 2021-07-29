package com.nh.share.common.models;

import java.io.Serializable;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 현재 접속자 정보 전송
 * <p>
 * 접속자모니터링 -> 경매서버
 * <p>
 * AK | 조합구분코드
 */
public class RefreshConnector implements FromAuctionCommon, Serializable {
	public static final char TYPE = 'K';
	private String mAuctionHouseCode; // 조합구분코드

	public RefreshConnector() {
	}

	public RefreshConnector(String auctionHouseCode) {
		mAuctionHouseCode = auctionHouseCode;
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, mAuctionHouseCode);
	}
}
