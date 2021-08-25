package com.nh.controller.setting;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.SharedPreference;

/**
 * 경매 환경 설정
 * 
 * @author jhlee
 *
 */
public class SettingApplication {
	// 전광판 노출 자리수 설정 기본값
	public static final String DEFAULT_BORAD_ENTRYNUM = "3";
	public static final String DEFAULT_BORAD_EXHIBITOR = "6";
	public static final String DEFAULT_BORAD_GENDER = "2";
	public static final String DEFAULT_BORAD_WEIGHT = "3";
	public static final String DEFAULT_BORAD_MOTHER = "4";
	public static final String DEFAULT_BORAD_PASSAGE = "2";
	public static final String DEFAULT_BORAD_MATIME = "2";
	public static final String DEFAULT_BORAD_KPN = "4";
	public static final String DEFAULT_BORAD_REGION = "4";
	public static final String DEFAULT_BORAD_NOTE = "12";
	public static final String DEFAULT_BORAD_LOWPRICE = "3";
	public static final String DEFAULT_BORAD_SUCPRICE = "3";
	public static final String DEFAULT_BORAD_SUCBIDDER = "3";
	public static final String DEFAULT_BORAD_DNA = "1";
	
	// PDP 노출 자리수 설정 기본값
	public static final String DEFAULT_PDP_ENTRYNUM = "3";
	public static final String DEFAULT_PDP_EXHIBITOR = "6";
	public static final String DEFAULT_PDP_GENDER = "2";
	public static final String DEFAULT_PDP_WEIGHT = "3";
	public static final String DEFAULT_PDP_MOTHER = "4";
	public static final String DEFAULT_PDP_PASSAGE = "2";
	public static final String DEFAULT_PDP_MATIME = "2";
	public static final String DEFAULT_PDP_KPN = "4";
	public static final String DEFAULT_PDP_REGION = "8";
	public static final String DEFAULT_PDP_NOTE = "12";
	public static final String DEFAULT_PDP_LOWPRICE = "3";
	public static final String DEFAULT_PDP_SUCPRICE = "3";
	public static final String DEFAULT_PDP_SUCBIDDER = "6";
	public static final String DEFAULT_PDP_DNA = "1";
	
	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private static SettingApplication instance = null;

	private String upperLimitCalf = null; // 응찰 상한가 - 송아지
	private String upperLimitFatteningCattle = null; // 응찰 상한가 - 비육우
	private String upperLimitBreedingCattle = null; // 응찰 상한가 - 번식우
	private String lowerLimitCalf = null; // 하한가 낮추기- 송아지
	private String lowerLimitFatteningCattle = null; // 하한가 낮추기- 비육우
	private String lowerLimitBreedingCattle = null; // 하한가 낮추기- 번식우
	private String reAuctionCount = null; // 동가 재경매 횟수
	private String auctionCountdown = null; // 경매 종료 카운트 다운 초
	private String soundAuctionWaitTime = null; // 음성 경매 대기 시간
	
	private boolean useReAuction = false; // 동가 재경매 여부
	private boolean useOneAuction = false; // 연속경매 - 하나씩 진행 여부
	private boolean useSoundAuction = false; // 음성경매 - 음성경부 여부
	
	private int aucObjDsc = 0;				//경매 구분
	
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
	 * 앱 첫 실행 기본 저장값
	 */
	public void initDefaultConfigration(ResourceBundle resMsg) {
		
		boolean isFirstApplication = SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_IS_FIRST_APPLICATION, true);

		if (isFirstApplication) {
			mLogger.debug("설치 후 첫 실행");
			
			// [S] 메인 경매 정보 음성 노출 여부 기본 설정
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_NUMBER,true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_EXHIBITOR,true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_GENDER,true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MOTHER, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MATIME, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_PASGQCN, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_WEIGHT, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_LOWPRICE,true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_BRAND,true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_KPN, true);
			// [E] 메인 경매 정보 음성 노출 여부 기본 설정
			
			// [S] 경매 음성 메세지 기본 설정
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_INTRO, resMsg.getString("default.msg.setting.sound.intro"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_BUYER, resMsg.getString("default.msg.setting.sound.buyer"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_GUIDE, resMsg.getString("default.msg.setting.sound.guide"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_PRACTICE, resMsg.getString("default.msg.setting.sound.practice"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_GENDER, resMsg.getString("default.msg.setting.sound.gender"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_USE, resMsg.getString("default.msg.setting.sound.use"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_1, resMsg.getString("default.msg.setting.sound.etc1"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_2, resMsg.getString("default.msg.setting.sound.etc2"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_3, resMsg.getString("default.msg.setting.sound.etc3"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_4, resMsg.getString("default.msg.setting.sound.etc4"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_5, resMsg.getString("default.msg.setting.sound.etc5"));
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_6, resMsg.getString("default.msg.setting.sound.etc6"));
			// [E] 경매 음성 메세지 기본 설정
			
			//상한가
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_UPPER_CALF_TEXT, "10000");
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_UPPER_FATTENING_TEXT, "10000");
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_UPPER_BREEDING_TEXT, "10000");

			//가격 낮추기
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_LOWER_CALF_TEXT, "10000");
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_LOWER_FATTENING_TEXT, "10000");
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_LOWER_BREEDING_TEXT, "10000");
			
			// 첫실행 후 false
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_IS_FIRST_APPLICATION, false);
			
	        // 전광판 표출 설정
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_BOARD_ENTRYNUM, DEFAULT_BORAD_ENTRYNUM);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_BOARD_KPN, DEFAULT_BORAD_KPN);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_BOARD_EXHIBITOR, DEFAULT_BORAD_EXHIBITOR);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_BOARD_REGION, DEFAULT_BORAD_REGION);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_BOARD_GENDER, DEFAULT_BORAD_GENDER);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_BOARD_NOTE, DEFAULT_BORAD_NOTE);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_BOARD_WEIGHT, DEFAULT_BORAD_WEIGHT);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_BOARD_LOWPRICE, DEFAULT_BORAD_LOWPRICE);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_BOARD_MOTHER, DEFAULT_BORAD_MOTHER);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_BOARD_SUCPRICE, DEFAULT_BORAD_SUCPRICE);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_BOARD_PASSAGE, DEFAULT_BORAD_PASSAGE);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_BOARD_SUCBIDDER, DEFAULT_BORAD_SUCBIDDER);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_BOARD_MATIME, DEFAULT_BORAD_MATIME);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_BOARD_DNA, DEFAULT_BORAD_DNA);
	        
	        // PDP 표출 설정
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PDP_ENTRYNUM, DEFAULT_PDP_ENTRYNUM);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PDP_KPN, DEFAULT_PDP_KPN);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PDP_EXHIBITOR, DEFAULT_PDP_EXHIBITOR);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PDP_REGION, DEFAULT_PDP_REGION);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PDP_GENDER, DEFAULT_PDP_GENDER);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PDP_NOTE, DEFAULT_PDP_NOTE);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PDP_WEIGHT, DEFAULT_PDP_WEIGHT);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PDP_LOWPRICE, DEFAULT_PDP_LOWPRICE);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PDP_MOTHER, DEFAULT_PDP_MOTHER);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PDP_SUCPRICE, DEFAULT_PDP_SUCPRICE);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PDP_PASSAGE, DEFAULT_PDP_PASSAGE);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PDP_SUCBIDDER, DEFAULT_PDP_SUCBIDDER);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PDP_MATIME, DEFAULT_PDP_MATIME);
	        SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PDP_DNA, DEFAULT_PDP_DNA);
		} else {
			mLogger.debug("설치 후 첫 실행 아님.");
		}
		
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
		auctionCountdown = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_COUNTDOWN, "5");
		reAuctionCount = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_RE_AUCTION_COUNT, "1");
		useReAuction = SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_SETTING_RE_AUCTION_CHECK, false);
		useOneAuction = SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_SETTING_USE_ONE_AUCTION, false);
		useSoundAuction = SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_SETTING_USE_SOUND_AUCTION, false);
		soundAuctionWaitTime = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_AUCTION_WAIT_TIME, "3");
	}

	
	/**
	 * 출품 정보 음성 설정 저장된 값들
	 */
	public List<Boolean> getParsingMainSoundFlag() {

		// 사운드 텍스트 저장 리스트.
		List<Boolean> checkDataList = new ArrayList<Boolean>();

		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_NUMBER,true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_EXHIBITOR,true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_GENDER,true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MOTHER,true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MATIME,true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_PASGQCN,true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_WEIGHT,true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_LOWPRICE,true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_BRAND,true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_KPN,true));

		return checkDataList;
	}
	
	
	/**
	 * 내부 저장된 음성 메세지 가져옴.
	 */
	public List<String> getParsingSoundDataList() {

		// 사운드 텍스트 저장 리스트.
		List<String> soundDataList = new ArrayList<String>();

		soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_INTRO, ""));
		soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_BUYER, ""));
		soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_GUIDE, ""));
		soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_PRACTICE, ""));
		soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_GENDER, ""));
		soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_USE, ""));
		soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_1, ""));
		soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_2, ""));
		soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_3, ""));
		soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_4, ""));
		soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_5, ""));
		soundDataList.add(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_6, ""));
		
		return soundDataList;
	}

	
	/**getBaseUnit
	 * 경매 기본 상한가
	 * @return 1.송아지 , 2.비육우 ,3.번식우
	 */
	public int getCowUpperLimitPrice(int aucObjDsc) {
		
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
	public int getCowLowerLimitPrice(int aucObjDsc) {
		
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
	public int getAuctionCountdown() {
		return reInt(auctionCountdown);
	}
	public int getSoundAuctionWaitTime() {
		return reInt(soundAuctionWaitTime);
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

}
