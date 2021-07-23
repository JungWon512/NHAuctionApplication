package com.nh.controller.model;

/**
 * 경매 환경 설정
 * @author jhlee
 */
public class SettingInfoData {

	private int cowUpperLimitPrice = 100;		//상한가
	private int cowLowerLimitPrice = 100;		//하한가
	
	public int getCowUpperLimitPrice() {
		return cowUpperLimitPrice;
	}
	public void setCowUpperLimitPrice(int cowUpperLimitPrice) {
		this.cowUpperLimitPrice = cowUpperLimitPrice;
	}
	public int getCowLowerLimitPrice() {
		return cowLowerLimitPrice;
	}
	public void setCowLowerLimitPrice(int cowLowerLimitPrice) {
		this.cowLowerLimitPrice = cowLowerLimitPrice;
	}
	

	
	
	
}
