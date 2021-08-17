package com.nh.controller.setting;

import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.SharedPreference;

/**
 * 경매 환경 설정
 * 
 * @author jhlee
 *
 */
public class SettingApplication {

	private static SettingApplication instance = null;

	private String upperLimitCalf = null; // 응찰 상한가 - 송아지
	private String upperLimitFatteningCattle = null; // 응찰 상한가 - 비육우
	private String upperLimitBreedingCattle = null; // 응찰 상한가 - 번식우
	private String lowerLimitCalf = null; // 하한가 낮추기- 송아지
	private String lowerLimitFatteningCattle = null; // 하한가 낮추기- 비육우
	private String lowerLimitBreedingCattle = null; // 하한가 낮추기- 번식우

	private String reAuctionCount = null; // 동가 재경매 횟수
	private boolean useReAuction = false; // 동가 재경매 여부
	private boolean useOneAuction = false; // 연속경매 - 하나씩 진행 여부
	private boolean useSoundAuction = false; // 음성경매 - 음성경부 여부
	
	private int aucObjDsc = 0;		//경매 구분
	
	public static synchronized SettingApplication getInstance() {

		if (instance == null) {
			instance = new SettingApplication();
		}

		return instance;
	}

	public SettingApplication() {
		initSharedData();
	}
	
	/**
	 * 경매 구분
	 * @param aucObjDsc
	 */
	public void setAuctionObjDsc(int aucObjDsc) {
		this.aucObjDsc = aucObjDsc;
	}

	/**
	 * 저장된 설정 데이터 가져옴.
	 */
	public void initSharedData() {

		upperLimitCalf = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_UPPER_CALF_TEXT, "1");
		upperLimitFatteningCattle = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_UPPER_FATTENING_TEXT, "1");
		upperLimitBreedingCattle = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_UPPER_BREEDING_TEXT, "1");
		lowerLimitCalf = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_LOWER_CALF_TEXT, "1");
		lowerLimitFatteningCattle = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_LOWER_FATTENING_TEXT, "1");
		lowerLimitBreedingCattle = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_LOWER_BREEDING_TEXT, "1");
		reAuctionCount = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_RE_AUCTION_COUNT, "1");
		useReAuction = SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_SETTING_RE_AUCTION_CHECK, false);
		useOneAuction = SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_SETTING_USE_ONE_AUCTION, false);
		useSoundAuction = SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_SETTING_USE_SOUND_AUCTION, false);
	
	}

	public int getUpperLimitCalf() {
		return reInt(upperLimitCalf);
	}
	public int getUpperLimitFatteningCattle() {
		return reInt(upperLimitFatteningCattle);
	}
	public int getUpperLimitBreedingCattle() {
		return reInt(upperLimitBreedingCattle);
	}
	public int getLowerLimitCalf() {
		return reInt(lowerLimitCalf);
	}
	public int getLowerLimitFatteningCattle() {
		return reInt(lowerLimitFatteningCattle);
	}
	public int getLowerLimitBreedingCattle() {
		return reInt(lowerLimitBreedingCattle);
	}
	public int getReAuctionCount() {
		return reInt(reAuctionCount);
	}
	public boolean isUseReAuction() {
		return useReAuction;
	}
	public boolean isUseOneAuction() {
		return useOneAuction;
	}
	public boolean isUseSoundAuction() {
		return useSoundAuction;
	}
	private int reInt(String value) {
		return Integer.parseInt(value);
	}

	/**
	 * 경매 기본 상한가
	 * @return 1.송아지 , 2.비육우 ,3.번식우
	 */
	public int getBaseUnit() {
		
		int baseUnit = 1;
		
		switch (aucObjDsc) {
		case GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_1:
			baseUnit = getUpperLimitCalf();
			break;
		case GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_2:
			baseUnit = getUpperLimitFatteningCattle();
			break;
		case GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_3:
			baseUnit = getUpperLimitBreedingCattle();
			break;
		}
		
		return baseUnit;
	}
	
	/**
	 * 하한가 낮추기
	 * @return 1.송아지 , 2.비육우 ,3.번식우
	 */
	public int getCowLowerLimitPrice() {
		
	int lowerLimitPrice = 1;
		
		switch (aucObjDsc) {
		case GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_1:
			lowerLimitPrice = getLowerLimitCalf();
			break;
		case GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_2:
			lowerLimitPrice = getLowerLimitFatteningCattle();
			break;
		case GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_3:
			lowerLimitPrice = getLowerLimitBreedingCattle();
			break;
		}
		
		return lowerLimitPrice;
		
		
	}

}
