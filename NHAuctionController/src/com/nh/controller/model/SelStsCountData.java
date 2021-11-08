package com.nh.controller.model;

/**
 * 경매 결과 카운트
 * 
 * @author dhKim
 *
 */
public class SelStsCountData implements Cloneable{

	private int selStsReady; // 경매 준비
	
	private int selStsAuction; // 경매
	
	private int selStsFinish; // 종료
	
	private int selStsPending; // 보류
	
	private int selStsProgress; // 경매 진행
	
	private int totalCount; //총 수
	
	
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getSelStsAuction() {
		return selStsAuction;
	}
	public void setSelStsAuction(int selStsAuction) {
		this.selStsAuction = selStsAuction;
	}
	public int getSelStsReady() {
		return selStsReady;
	}
	public void setSelStsReady(int selStsReady) {
		this.selStsReady = selStsReady;
	}
	public int getSelStsProgress() {
		return selStsProgress;
	}
	public void setSelStsProgress(int selStsProgress) {
		this.selStsProgress = selStsProgress;
	}
	public int getSelStsFinish() {
		return selStsFinish;
	}
	public void setSelStsFinish(int selStsFinish) {
		this.selStsFinish = selStsFinish;
	}
	public int getSelStsPending() {
		return selStsPending;
	}
	public void setSelStsPending(int selStsPending) {
		this.selStsPending = selStsPending;
	}
}


