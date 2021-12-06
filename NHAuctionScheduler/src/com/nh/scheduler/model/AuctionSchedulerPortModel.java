package com.nh.scheduler.model;

import java.io.Serializable;

/**
 * 
 * @ClassName AuctionSchedulerPortModel.java
 * @Description 경매 스케줄 Hazelcast Map에서 사용한다.
 * @anthor ishift
 * @since 2021.11.10
 */
public class AuctionSchedulerPortModel implements Serializable {

	private String mAuctionBizPlaceCode; // 조합코드
	private int mAuctionQcn; // 경매회차
	private String mAuctionDate; // 경매 일자
	private String mAuctionPort; // 경매 서버 포트
	private Boolean mServerPortAvailable; // 서버 포트 사용 가능 여부
	private String mAPIUpdateResult; // updateAuctionPortInformation api 결과

	public AuctionSchedulerPortModel(String auctionBizPlaceCode, int auctionQcn, String auctionDate,
			String auctionPort, Boolean serverPortAvailable, String mAPIUpdateResult) {
		this.setAuctionBizPlaceCode(auctionBizPlaceCode);
		this.setAuctionQcn(auctionQcn);
		this.setAuctionDate(auctionDate);
		this.setAuctionPort(auctionPort);
		this.setServerPortAvailable(serverPortAvailable);
		this.setAPIUpdateResult(mAPIUpdateResult);
	}

	public AuctionSchedulerPortModel() {

	}

	public String getAuctionBizPlaceCode() {
		return mAuctionBizPlaceCode;
	}

	public void setAuctionBizPlaceCode(String auctionBizPlaceCode) {
		this.mAuctionBizPlaceCode = auctionBizPlaceCode;
	}

	public int getAuctionQcn() {
		return mAuctionQcn;
	}

	public void setAuctionQcn(int auctionQcn) {
		this.mAuctionQcn = auctionQcn;
	}

	public String getAuctionDate() {
		return mAuctionDate;
	}

	public void setAuctionDate(String auctionDate) {
		this.mAuctionDate = auctionDate;
	}

	public Boolean getServerPortAvailable() {
		return mServerPortAvailable;
	}

	public void setServerPortAvailable(Boolean serverPortAvailable) {
		this.mServerPortAvailable = serverPortAvailable;
	}

	public String getAPIUpdateResult() {
		return mAPIUpdateResult;
	}

	public void setAPIUpdateResult(String mAPIUpdateResult) {
		this.mAPIUpdateResult = mAPIUpdateResult;
	}

	public String getAuctionPort() {
		return mAuctionPort;
	}

	public void setAuctionPort(String auctionPort) {
		this.mAuctionPort = auctionPort;
	}
}
