package com.nh.share.server.models;

import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 접속자 정보 전송
 * 
 * 경매서버 -> 공통
 * 
 * SI | 경매거점코드 | 회원번호 | 접속요청채널(6001/6002/6003/6004) | 사용채널(ANDROID/IOS/WEB) |
 * 상태(N : 미응찰 / B : 응찰 / C : 응찰취소) | 응찰가격
 *
 */
public class BidderConnectInfo implements FromAuctionServer {
	public static final char TYPE = 'I';
	private String mAuctionHouseCode; // 거점코드
	private String mUserNo; // 회원번호
	private String mChannel; // 접속 요청 채널
	private String mOS; // 사용 채널
	private String mStatus; // 응찰 상태
	private String mBidPrice; // 응찰 상태

	public BidderConnectInfo(String auctionHouseCode, String userNo, String channel, String os, String status,
			String bidPrice) {
		mAuctionHouseCode = auctionHouseCode;
		mUserNo = userNo;
		mChannel = channel;
		mOS = os;
		mStatus = status;
		mBidPrice = bidPrice;
	}

	public BidderConnectInfo(String[] messages) {
		mAuctionHouseCode = messages[1];
		mUserNo = messages[2];
		mChannel = messages[3];
		mOS = messages[4];
		mStatus = messages[5];
		mBidPrice = messages[6];
	}

	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getUserNo() {
		return mUserNo;
	}

	public void setUserNo(String userNo) {
		this.mUserNo = userNo;
	}

	public String getChannel() {
		return mChannel;
	}

	public void setChannel(String channel) {
		this.mChannel = channel;
	}

	public String getOS() {
		return mOS;
	}

	public void setOS(String os) {
		this.mOS = os;
	}

	public String getStatus() {
		return mStatus;
	}

	public void setStatus(String status) {
		this.mStatus = status;
	}

	public String getBidPrice() {
		return mBidPrice;
	}

	public void setBidPrice(String bidPrice) {
		this.mBidPrice = bidPrice;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER,
				mAuctionHouseCode, AuctionShareSetting.DELIMITER, mUserNo, AuctionShareSetting.DELIMITER, mChannel,
				AuctionShareSetting.DELIMITER, mOS, AuctionShareSetting.DELIMITER, mStatus,
				AuctionShareSetting.DELIMITER, mBidPrice);
	}
}
