package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 출하 안내 시스템 접속 상태 전송
 * 
 * 경매서버 -> 제어프로그램
 * 
 * SI | 조합구분코드 | 접속상태(2000/2001)
 * 상태(2000 : 접속상태 / 2001 : 미접속상태)
 *
 */
public class StandConnectInfo implements FromAuctionServer {
	public static final char TYPE = 'M';
	private String mAuctionHouseCode; // 거점코드
	private String mStatus; // 접속 상태

	public StandConnectInfo() {}
	
	public StandConnectInfo(String auctionHouseCode, String status) {
		mAuctionHouseCode = auctionHouseCode;
		mStatus = status;
	}

	public StandConnectInfo(String[] messages) {
		mAuctionHouseCode = messages[1];
		mStatus = messages[2];
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getStatus() {
		return mStatus;
	}

	public void setStatus(String status) {
		this.mStatus = status;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER,
				mAuctionHouseCode, AuctionShareSetting.DELIMITER, mStatus);
	}
	
	
}
