package com.nh.share.api.models;

/**
 * 경매 진행 프로그램 버전 정보
 */
public class VersionData {

	private String APP_VERSION_ID;
	
	private String MAX_VERSION;
	
	private String MIN_VERSION;

	public String getAPP_VERSION_ID() {
		return APP_VERSION_ID;
	}
	public void setAPP_VERSION_ID(String aPP_VERSION_ID) {
		APP_VERSION_ID = aPP_VERSION_ID;
	}
	public String getMAX_VERSION() {
		return MAX_VERSION;
	}
	public void setMAX_VERSION(String mAX_VERSION) {
		MAX_VERSION = mAX_VERSION;
	}
	public String getMIN_VERSION() {
		return MIN_VERSION;
	}
	public void setMIN_VERSION(String mIN_VERSION) {
		MIN_VERSION = mIN_VERSION;
	}
}