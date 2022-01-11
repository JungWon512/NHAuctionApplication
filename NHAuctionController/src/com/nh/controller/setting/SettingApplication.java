package com.nh.controller.setting;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.controller.controller.SettingController.AuctionToggle;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.SharedPreference;
import com.nh.share.controller.models.EditSetting;

/**
 * 경매 환경 설정
 * 
 * @author jhlee
 *
 */
public class SettingApplication {

	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// 전광판 설정 IP, PORT 기본값
	public final String DEFAULT_SETTING_IP_BOARD_TEXT1 = "";
	public final String DEFAULT_SETTING_PORT_BOARD_TEXT1 = "";
	public final String DEFAULT_SETTING_IP_BOARD_TEXT2 = "";
	public final String DEFAULT_SETTING_PORT_BOARD_TEXT2 = "";
	public final String DEFAULT_SETTING_IP_BOARD_TEXT3 = "";
	public final String DEFAULT_SETTING_PORT_BOARD_TEXT3 = "";

	public final String DEFAULT_SETTING_FORMAT = "1";

	// PDP 셋톱박스 기본값
	public final String DEFAULT_SETTING_IP_PDP_TEXT1 = "";
	public final String DEFAULT_SETTING_PORT_PDP_TEXT1 = "";
	// PDP 셋톱박스3 기본값
	public final String DEFAULT_SETTING_IP_PDP_TEXT2 = "";
	public final String DEFAULT_SETTING_PORT_PDP_TEXT2 = "";

	// 응찰석 셋톱박스 기본값
	public final String DEFAULT_SETTING_IP_BIDDER_TEXT = "";
	public final String DEFAULT_SETTING_PORT_BIDDER_TEXT = "";

	// 전광판 노출 자리수 설정 기본값
	public final String DEFAULT_BORAD_ENTRYNUM = "3";
	public final String DEFAULT_BORAD_EXHIBITOR = "6";
	public final String DEFAULT_BORAD_GENDER = "2";
	public final String DEFAULT_BORAD_WEIGHT = "3";
	public final String DEFAULT_BORAD_MOTHER = "4";
	public final String DEFAULT_BORAD_PASSAGE = "2";
	public final String DEFAULT_BORAD_MATIME = "2";
	public final String DEFAULT_BORAD_KPN = "4";
	public final String DEFAULT_BORAD_REGION = "4";
	public final String DEFAULT_BORAD_NOTE = "12";
	public final String DEFAULT_BORAD_LOWPRICE = "3";
	public final String DEFAULT_BORAD_SUCPRICE = "3";
	public final String DEFAULT_BORAD_SUCBIDDER = "3";
	public final String DEFAULT_BORAD_DNA = "1";

	// PDP 노출 자리수 설정 기본값
	public final String DEFAULT_PDP_ENTRYNUM = "3";
	public final String DEFAULT_PDP_EXHIBITOR = "6";
	public final String DEFAULT_PDP_GENDER = "2";
	public final String DEFAULT_PDP_WEIGHT = "3";
	public final String DEFAULT_PDP_MOTHER = "4";
	public final String DEFAULT_PDP_PASSAGE = "2";
	public final String DEFAULT_PDP_MATIME = "2";
	public final String DEFAULT_PDP_KPN = "4";
	public final String DEFAULT_PDP_REGION = "8";
	public final String DEFAULT_PDP_NOTE = "12";
	public final String DEFAULT_PDP_LOWPRICE = "3";
	public final String DEFAULT_PDP_SUCPRICE = "3";
	public final String DEFAULT_PDP_SUCBIDDER = "6";
	public final String DEFAULT_PDP_DNA = "1";

	// 응찰 상한가 기본값
	public final String DEFAULT_SETTING_UPPER_CFB_MAX = "99999";
	public final String DEFAULT_SETTING_UPPER_CALF_TEXT = "100"; //송아지
	public final String DEFAULT_SETTING_UPPER_FATTENING_TEXT = "100"; //비육우
	public final String DEFAULT_SETTING_UPPER_BREEDING_TEXT = "100"; //번식우

	// 하한가 낮추기 기본값
	public final String DEFAULT_SETTING_LOWER_CALF_TEXT_MAX = "99999";
	public final String DEFAULT_SETTING_LOWER_CALF_TEXT = "100";
	public final String DEFAULT_SETTING_LOWER_FATTENING_TEXT = "100";
	public final String DEFAULT_SETTING_LOWER_BREEDING_TEXT = "100";

	// 경매종료 멘트 설정 - 멘트 사용 기본값
	public final boolean DEFAULT_SETTING_ANNOUNCEMENT = true;
	// 비고 사용 기본값
	public final boolean DEFAULT_SETTING_NOTE = false;
	// 카운트 기본값
	public final String DEFAULT_SETTING_COUNTDOWN = "3";
	public final String DEFAULT_SETTING_COUNTDOWN_MAX = "9";
	// 동가 재경매 횟수
	public final String DEFAULT_SETTING_RE_AUCTION_COUNT = "2";
	// 동가 재경매 기본값
	public final boolean DEFAULT_SETTING_RE_AUCTION_CHECK = true;
	// 하나씩 진행 기본값
	public final boolean DEFAULT_SETTING_USE_ONE_AUCTION = false;
	// 음성경부여부 (음성경매) 기본값
	public final boolean DEFAULT_SETTING_USE_SOUND_AUCTION = false;
	// 대기시간 기본값
	public final String DEFAULT_SETTING_SOUND_AUCTION_WAIT_TIME_MAX = "50";
	public final String DEFAULT_SETTING_SOUND_AUCTION_WAIT_TIME = "5";
	
	// 모바일 표출 기본값
	public final String DEFAULT_SETTING_MOBILE_ENTRYNUM= "Y";
	public final String DEFAULT_SETTING_MOBILE_EXHIBITOR= "Y";
	public final String DEFAULT_SETTING_MOBILE_GENDER= "Y";
	public final String DEFAULT_SETTING_MOBILE_WEIGHT= "N";
	public final String DEFAULT_SETTING_MOBILE_MOTHER= "Y";
	public final String DEFAULT_SETTING_MOBILE_MATIME= "Y";
	public final String DEFAULT_SETTING_MOBILE_KPN= "Y";
	public final String DEFAULT_SETTING_MOBILE_REGION= "Y";
	public final String DEFAULT_SETTING_MOBILE_NOTE= "Y";
	public final String DEFAULT_SETTING_MOBILE_LOWPRICE= "Y";
	public final String DEFAULT_SETTING_MOBILE_PASSAGE= "Y";
	public final String DEFAULT_SETTING_MOBILE_DNA="N";
	public final String DEFAULT_SETTING_SOUND_CONFIG="";

	//경매 타입
	public final String DEFAULT_SETTING_AUCTION_TOGGLE_TYPE =  AuctionToggle.SINGLE.toString();

	//계류대번호
	public final String DEFAULT_SETTING_STAND_POSITION ="150";
	
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
	private String standPosition = null; // 계류대 번호

	private boolean useReAuction = false; // 동가 재경매 여부
	private boolean useOneAuction = false; // 연속경매 - 하나씩 진행 여부
	private boolean useSoundAuction = false; // 음성경매 - 음성경부 여부
	private boolean isSingleAuction = true;	//경매 타입
	private boolean isNote = false; //비고
	
	private boolean isBoardUseNote1 = false; // 전광판 비고 흐름 야부
	private boolean isBoardUseNote2 = false; // 전광판2 비고 흐름 야부
	

	private int aucObjDsc = 0; // 경매 구분

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
	 * 
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

			// 전광판 설정 IP, PORT
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT1, DEFAULT_SETTING_IP_BOARD_TEXT1);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT1, DEFAULT_SETTING_PORT_BOARD_TEXT1);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_IP_BOARD_TEXT2, DEFAULT_SETTING_IP_BOARD_TEXT2);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PORT_BOARD_TEXT2, DEFAULT_SETTING_PORT_BOARD_TEXT2);
			
			// PDP 셋톱박스 기본값
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_IP_PDP_TEXT1,DEFAULT_SETTING_IP_PDP_TEXT1);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_PORT_PDP_TEXT1,DEFAULT_SETTING_PORT_PDP_TEXT1);
	
			// [S] 메인 경매 정보 음성 노출 여부 기본 설정
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_NUMBER, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_EXHIBITOR, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_GENDER, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MOTHER, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MATIME, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_PASGQCN, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_WEIGHT, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_LOWPRICE, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_BRAND, true);
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_KPN, true);
			// [E] 메인 경매 정보 음성 노출 여부 기본 설정

			// [S] 경매 음성 메세지 기본 설정
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_INTRO,"");
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_BUYER,"");
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_GUIDE,"");
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_PRACTICE,"");
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_GENDER,"");
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_USE,"");
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_1,"");
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_2,"");
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_3,"");
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_4,"");
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_5,"");
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_6,"");
			// [E] 경매 음성 메세지 기본 설정
			
			// 상한가
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_UPPER_CALF_TEXT, DEFAULT_SETTING_UPPER_CALF_TEXT);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_UPPER_FATTENING_TEXT, DEFAULT_SETTING_UPPER_FATTENING_TEXT);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_UPPER_BREEDING_TEXT, DEFAULT_SETTING_UPPER_BREEDING_TEXT);

			// 가격 낮추기
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_LOWER_CALF_TEXT, DEFAULT_SETTING_LOWER_CALF_TEXT);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_LOWER_FATTENING_TEXT, DEFAULT_SETTING_LOWER_FATTENING_TEXT);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_LOWER_BREEDING_TEXT, DEFAULT_SETTING_LOWER_BREEDING_TEXT);

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
			
			// 경매종료 멘트 설정 - 멘트 사용 기본값
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_SETTING_ANNOUNCEMENT, DEFAULT_SETTING_ANNOUNCEMENT);
			// 비고 사용 기본값
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_SETTING_NOTE, DEFAULT_SETTING_NOTE);
			
			// 카운트 기본값
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_COUNTDOWN, DEFAULT_SETTING_COUNTDOWN);
			// 동가 재경매 횟수
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_RE_AUCTION_COUNT, DEFAULT_SETTING_RE_AUCTION_COUNT);
			
			// 동가 재경매 기본값
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_SETTING_RE_AUCTION_CHECK, DEFAULT_SETTING_RE_AUCTION_CHECK);
			// 하나씩 진행 기본값
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_SETTING_USE_ONE_AUCTION, DEFAULT_SETTING_USE_ONE_AUCTION);
			// 음성경부여부 (음성경매) 기본값
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_SETTING_USE_SOUND_AUCTION, DEFAULT_SETTING_USE_SOUND_AUCTION);
			// 대기시간 기본값
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_AUCTION_WAIT_TIME,DEFAULT_SETTING_SOUND_AUCTION_WAIT_TIME);
			
			// 모바일 표출 기본값
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_MOBILE_ENTRYNUM, DEFAULT_SETTING_MOBILE_ENTRYNUM);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_MOBILE_EXHIBITOR, DEFAULT_SETTING_MOBILE_EXHIBITOR);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_MOBILE_GENDER, DEFAULT_SETTING_MOBILE_GENDER);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_MOBILE_WEIGHT, DEFAULT_SETTING_MOBILE_WEIGHT);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_MOBILE_MOTHER, DEFAULT_SETTING_MOBILE_MOTHER);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_MOBILE_MATIME, DEFAULT_SETTING_MOBILE_MATIME);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_MOBILE_KPN, DEFAULT_SETTING_MOBILE_KPN);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_MOBILE_REGION, DEFAULT_SETTING_MOBILE_REGION);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_MOBILE_NOTE, DEFAULT_SETTING_MOBILE_NOTE);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_MOBILE_LOWPRICE, DEFAULT_SETTING_MOBILE_LOWPRICE);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_MOBILE_PASSAGE, DEFAULT_SETTING_MOBILE_PASSAGE);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_MOBILE_DNA, DEFAULT_SETTING_MOBILE_DNA);

			// 경매 타입 (단일 or 일괄)
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_AUCTION_TOGGLE_TYPE, DEFAULT_SETTING_AUCTION_TOGGLE_TYPE);
			// 계류대 번호
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_STAND_POSITION, DEFAULT_SETTING_STAND_POSITION);
			// 첫실행 후 false
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_IS_FIRST_APPLICATION, false);
			//음성설정 
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_CONFIG, DEFAULT_SETTING_SOUND_CONFIG);
			//아이디
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_LOGIN_SAVE_ID, "");
			
		} else {
			mLogger.debug("설치 후 첫 실행 아님.");
		}

	}

	/**
	 * 저장된 설정 데이터 가져옴.
	 */
	public void initSharedData() {
		
		mLogger.debug("환경설정값들 init");

		upperLimitCalf = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_UPPER_CALF_TEXT, DEFAULT_SETTING_UPPER_CALF_TEXT);
		upperLimitFatteningCattle = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_UPPER_FATTENING_TEXT, DEFAULT_SETTING_UPPER_FATTENING_TEXT);
		upperLimitBreedingCattle = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_UPPER_BREEDING_TEXT, DEFAULT_SETTING_UPPER_BREEDING_TEXT);
		lowerLimitCalf = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_LOWER_CALF_TEXT, DEFAULT_SETTING_LOWER_CALF_TEXT);
		lowerLimitFatteningCattle = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_LOWER_FATTENING_TEXT, DEFAULT_SETTING_LOWER_FATTENING_TEXT);
		lowerLimitBreedingCattle = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_LOWER_BREEDING_TEXT, DEFAULT_SETTING_LOWER_BREEDING_TEXT);
		auctionCountdown = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_COUNTDOWN, DEFAULT_SETTING_COUNTDOWN);
		reAuctionCount = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_RE_AUCTION_COUNT, DEFAULT_SETTING_RE_AUCTION_COUNT);
		useReAuction = SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_SETTING_RE_AUCTION_CHECK, DEFAULT_SETTING_RE_AUCTION_CHECK);
		useOneAuction = SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_SETTING_USE_ONE_AUCTION, DEFAULT_SETTING_USE_ONE_AUCTION);
		useSoundAuction = SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_SETTING_USE_SOUND_AUCTION, DEFAULT_SETTING_USE_SOUND_AUCTION);
		soundAuctionWaitTime = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_AUCTION_WAIT_TIME, DEFAULT_SETTING_SOUND_AUCTION_WAIT_TIME);
		String aucType = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_AUCTION_TOGGLE_TYPE, DEFAULT_SETTING_AUCTION_TOGGLE_TYPE).toUpperCase();
		
		if(aucType.equals(AuctionToggle.SINGLE.toString())) {
			isSingleAuction =  true;
		}else {
			isSingleAuction =  false;
		}
		
		standPosition = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_STAND_POSITION, DEFAULT_SETTING_STAND_POSITION);
	
		isNote = SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_SETTING_NOTE, DEFAULT_SETTING_NOTE);
		
		//전광판 비고
		isBoardUseNote1 = SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_BOARD_USE_NOTE_1, false);
		isBoardUseNote2 = SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_BOARD_USE_NOTE_2, false);
	}

	/**
	 * 출품 정보 음성 설정 저장된 값들
	 */
	public List<Boolean> getParsingMainSoundFlag() {

		// 사운드 텍스트 저장 리스트.
		List<Boolean> checkDataList = new ArrayList<Boolean>();

		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_NUMBER, true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_EXHIBITOR, true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_GENDER, true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MOTHER, true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_MATIME, true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_PASGQCN, true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_WEIGHT, true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_LOWPRICE, true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_BRAND, true));
		checkDataList.add(SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_MAIN_SOUND_ENTRY_DNA, true));

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

	/**
	 * getBaseUnit 경매 기본 상한가
	 * 
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
	 * 
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
			default:
			lowerLimitPrice = 100;
			break;
		}
		return lowerLimitPrice;
	}
	
	/**
	 * DNA - 친자 
	 * 
	 * @return 1.일치 , 2.불일치 ,3.미확인
	 */
	public String getDnaYn(String dnaYn) {

		String resultStr = "";

		switch (dnaYn) {
		case GlobalDefine.AUCTION_INFO.AUCTION_DNA_1:
			resultStr = "일치";
			break;
		case GlobalDefine.AUCTION_INFO.AUCTION_DNA_2:
			resultStr = "불일치";
			break;
		case GlobalDefine.AUCTION_INFO.AUCTION_DNA_3:
			resultStr  = "미확인";
			break;
		}
		
		return resultStr;
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

	public String getStandPosition() {
		return standPosition;
	}

	public void setStandPosition(String standPosition) {
		this.standPosition = standPosition;
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

	public boolean isSingleAuction() {
		return isSingleAuction;
	}

	public void setSingleAuction(boolean isSingleAuction) {
		this.isSingleAuction = isSingleAuction;
	}

	private int reInt(String value) {
		return Integer.parseInt(value);
	}
	
	public String getSettingAuctionType() {
		
		String aucType = "";
		
		if(isSingleAuction) {
			aucType = GlobalDefine.AUCTION_INFO.AUCTION_TYPE_SINGLE;
		}else {
			aucType = GlobalDefine.AUCTION_INFO.AUCTION_TYPE_MULTI;
		}
		
		return aucType;
		
	}

	public boolean isNote() {
		return isNote;
	}
	
	/**
	 * 전광판1 비고 흐름 사용여부
	 * @return
	 */
	public boolean isBoardUseNote1() {
		return isBoardUseNote1;
	}
	
	/**
	 * 전광판2 비고 흐름 사용여부
	 * @return
	 */
	public boolean isBoardUseNote2() {
		return isBoardUseNote2;
	}

	/**
	 * API 경매 Param시 사용.
	 * @return
	 */
	public String getSettingAuctionTypeYn() {
		
		String yn = "";
		
		if(!isSingleAuction) {
			yn = "Y";
		}else {
			yn = "N";
		}

		return yn;
	}
	
	/**
	 * 환경설정 서버로 전송 값
	 * @return
	 */
	public EditSetting getSettingInfo() {
	
		
		EditSetting setting = new EditSetting(GlobalDefine.AUCTION_INFO.auctionRoundData.getNaBzplc(), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_MOBILE_ENTRYNUM, "Y"), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_MOBILE_EXHIBITOR, "Y"),
				SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_MOBILE_GENDER, "Y"), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_MOBILE_WEIGHT, "Y"), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_MOBILE_MOTHER, "Y"),
				SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_MOBILE_PASSAGE, "Y"), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_MOBILE_MATIME, "Y"), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_MOBILE_KPN, "N"),
				SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_MOBILE_REGION, "N"), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_MOBILE_NOTE, "N"), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_MOBILE_LOWPRICE, "Y"),
				SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_MOBILE_DNA, "N"), SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_COUNTDOWN, "5"), getSettingAuctionType(),
				SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_UPPER_CALF_TEXT, DEFAULT_SETTING_UPPER_CALF_TEXT),
				SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_UPPER_FATTENING_TEXT, DEFAULT_SETTING_UPPER_FATTENING_TEXT),
				SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_UPPER_BREEDING_TEXT, DEFAULT_SETTING_UPPER_BREEDING_TEXT)
				);
		
		return setting;
	}
	
	/**
	 * 원/만원단위 여부
	 * true 원단위 , false 만원단위
	 * @return
	 */
	public boolean isWon(String aucObjDsc) {
		
		boolean isWon = true;
		
		if(aucObjDsc.equals(Integer.toString(GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_1))) {
			
			if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice1() > 1) {
				isWon =  false;
			}
			
		}else if(aucObjDsc.equals(Integer.toString(GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_2))) {
			
			if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice2() > 1) {
				isWon =  false;
			}
			
		}else if(aucObjDsc.equals(Integer.toString(GlobalDefine.AUCTION_INFO.AUCTION_OBJ_DSC_3))) {
			
			if(GlobalDefine.AUCTION_INFO.auctionRoundData.getDivisionPrice3() > 1) {
				isWon =  false;
			}	
		}
		
		return isWon;
	}
	
	
	/**
	 * 음성 재생 속도
	 * @return
	 */
	public boolean isSoundRate() {
		boolean isSoundRate = SharedPreference.getInstance().getBoolean(SharedPreference.PREFERENCE_SETTING_SOUND_RATE, false);
		return isSoundRate;
	}
	
	
}
