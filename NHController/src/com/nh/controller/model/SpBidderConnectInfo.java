package com.nh.controller.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.setting.AuctionShareSetting;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 접속자 현황
 */
public class SpBidderConnectInfo {

	private StringProperty[] auctionHouseCode = new SimpleStringProperty[5];
	private StringProperty[] channel = new SimpleStringProperty[5]; // 접속 요청 채널
	private StringProperty[] userNo = new SimpleStringProperty[5];// 경매회원번호
	private StringProperty[] os = new SimpleStringProperty[5]; // 사용 채널
	private StringProperty[] status = new SimpleStringProperty[5]; // 응찰 상태
	private StringProperty[] bidPrice = new SimpleStringProperty[5]; // 응찰 가격

	public SpBidderConnectInfo() {
	}
	
	public SpBidderConnectInfo(List<BidderConnectInfo> bidderList) {
		
		for(int i= 0; bidderList.size() > i ; i++) {
			auctionHouseCode[i] = new SimpleStringProperty(bidderList.get(i).getAuctionHouseCode());
			channel[i] = new SimpleStringProperty(bidderList.get(i).getChannel());
			userNo[i] = new SimpleStringProperty(bidderList.get(i).getUserNo());
			os[i] = new SimpleStringProperty(bidderList.get(i).getOS());
			status[i] = new SimpleStringProperty(bidderList.get(i).getStatus());
			bidPrice[i] = new SimpleStringProperty(bidderList.get(i).getBidPrice());		
		}
	}

	public StringProperty[] getAuctionHouseCode() {
		return auctionHouseCode;
	}

	public void setAuctionHouseCode(StringProperty[] auctionHouseCode) {
		this.auctionHouseCode = auctionHouseCode;
	}

	public StringProperty[] getChannel() {
		return channel;
	}

	public void setChannel(StringProperty[] channel) {
		this.channel = channel;
	}

	public StringProperty[] getUserNo() {
		return userNo;
	}

	public void setUserNo(StringProperty[] userNo) {
		this.userNo = userNo;
	}

	public StringProperty[] getOs() {
		return os;
	}

	public void setOs(StringProperty[] os) {
		this.os = os;
	}

	public StringProperty[] getStatus() {
		return status;
	}

	public void setStatus(StringProperty[] status) {
		this.status = status;
	}

	public StringProperty[] getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(StringProperty[] bidPrice) {
		this.bidPrice = bidPrice;
	}
	
	
//
//	public SpBidderConnectInfo(BidderConnectInfo bidding) {
//		auctionHouseCode = new SimpleStringProperty(bidding.getAuctionHouseCode());
//		channel = new SimpleStringProperty(bidding.getChannel());
//		userNo = new SimpleStringProperty(bidding.getUserNo());
//		os = new SimpleStringProperty(bidding.getOS());
//		status = new SimpleStringProperty(bidding.getStatus());
//		bidPrice = new SimpleStringProperty(bidding.getBidPrice());
//	}
}
