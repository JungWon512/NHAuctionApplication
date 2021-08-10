package com.nh.controller.model;

/**
 * 경매 환경 설정
 * @author jhlee
 */
public class SettingInfoData {

	private int cowUpperLimitPrice = 100000;		//상한가
	private int cowLowerLimitPrice = 100000;		//하한가
	private int baseUnit = 10000;					//기준 금액 만워단위 
	
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
	public int getBaseUnit() {
		return baseUnit;
	}
	public void setBaseUnit(int baseUnit) {
		this.baseUnit = baseUnit;
	}
}
