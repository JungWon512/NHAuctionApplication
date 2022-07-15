package com.nh.controller.utils;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * 내부 저장 Class
 *
 * @author jhlee
 */
public class SharedPreference {
	private static SharedPreference mSharedPreference = null;

	public static SharedPreference getInstance() {

		if (mSharedPreference == null) {
			mSharedPreference = new SharedPreference();
		}
		return mSharedPreference;
	}

	// 경매 관련 기본 정보
	public static final String PREFERENCE_AUCTION_HOUSE_CODE = "PREFERENCE_AUCTION_HOUSE_CODE"; // 경제통합사업장코드

	// 경매 응찰 프로그램 환경설정 [START]

	// 프로그램 설치 후 첫 실행 여부
	public static final String PREFERENCE_IS_FIRST_APPLICATION = "PREFERENCE_IS_FIRST_APPLICATION";
	
	// 프로그램 설치 후 첫 실행 여부
	public static final String PREFERENCE_APPLICATION_VERSION_INFO = "PREFERENCE_APPLICATION_VERSION_INFO";

	// --- 모바일 노출설정 ---
	public static final String PREFERENCE_SETTING_MOBILE_ENTRYNUM = "PREFERENCE_SETTING_MOBILE_ENTRYNUM"; // 경매번호노출여부
	public static final String PREFERENCE_SETTING_MOBILE_EXHIBITOR = "PREFERENCE_SETTING_MOBILE_EXHIBITOR"; // 출하주노출여부
	public static final String PREFERENCE_SETTING_MOBILE_GENDER = "PREFERENCE_SETTING_MOBILE_GENDER"; // 성별노출여부
	public static final String PREFERENCE_SETTING_MOBILE_WEIGHT = "PREFERENCE_SETTING_MOBILE_WEIGHT"; // 중량노출여부
	public static final String PREFERENCE_SETTING_MOBILE_MOTHER = "PREFERENCE_SETTING_MOBILE_MOTHER"; // 어미노출여부
	public static final String PREFERENCE_SETTING_MOBILE_PASSAGE = "PREFERENCE_SETTING_MOBILE_PASSAGE"; // 계대노출여부
	public static final String PREFERENCE_SETTING_MOBILE_MATIME = "PREFERENCE_SETTING_MOBILE_MATIME"; // 산차노출여부
	public static final String PREFERENCE_SETTING_MOBILE_KPN = "PREFERENCE_SETTING_MOBILE_KPN"; // kpn노출여부
	public static final String PREFERENCE_SETTING_MOBILE_REGION = "PREFERENCE_SETTING_MOBILE_REGION"; // 지역명노출여부
	public static final String PREFERENCE_SETTING_MOBILE_NOTE = "PREFERENCE_SETTING_MOBILE_NOTE"; // 비고노출여부
	public static final String PREFERENCE_SETTING_MOBILE_LOWPRICE = "PREFERENCE_SETTING_MOBILE_LOWPRICE"; // 최저가노출여부
	public static final String PREFERENCE_SETTING_MOBILE_DNA = "PREFERENCE_SETTING_MOBILE_DNA"; // 친자노출여부

	public static final String PREFERENCE_SETTING_ANNOUNCEMENT = "PREFERENCE_SETTING_ANNOUNCEMENT"; // 경매종료 멘트 설정
	public static final String PREFERENCE_SETTING_SOUND_RATE = "PREFERENCE_SETTING_SOUND_RATE"; // 음성 재생 속도
	public static final String PREFERENCE_SETTING_NOTE = "PREFERENCE_SETTING_NOTE"; // 비고창 설정
	public static final String PREFERENCE_SETTING_COUNTDOWN = "PREFERENCE_SETTING_COUNTDOWN"; // 카운트다운 설정
	public static final String PREFERENCE_SETTING_AUCTION_TOGGLE_TYPE = "PREFERENCE_SETTING_AUCTION_TOGGLE_TYPE"; // 경매타입 toggle button
	
	public static final String PREFERENCE_SETTING_USE_LOW_PRICE_RATE = "PREFERENCE_SETTING_USE_LOW_PRICE_RATE"; // 하한가 정률 적용 여부
	
	// --- 전광판 설정 IP, PORT ---
	public static final String PREFERENCE_SETTING_IP_BOARD_TEXT1 = "PREFERENCE_SETTING_IP_BOARD_TEXT1";
	public static final String PREFERENCE_SETTING_PORT_BOARD_TEXT1 = "PREFERENCE_SETTING_PORT_BOARD_TEXT1";
	public static final String PREFERENCE_SETTING_IP_BOARD_TEXT2 = "PREFERENCE_SETTING_IP_BOARD_TEXT2";
	public static final String PREFERENCE_SETTING_PORT_BOARD_TEXT2 = "PREFERENCE_SETTING_PORT_BOARD_TEXT2";


	// --- PDP, 응찰석, PDP3 셋톱박스 IP, PORT ---
	public static final String PREFERENCE_SETTING_IP_PDP_TEXT1 = "PREFERENCE_SETTING_IP_PDP_TEXT1";
	public static final String PREFERENCE_SETTING_PORT_PDP_TEXT1 = "PREFERENCE_SETTING_PORT_PDP_TEXT1";
	// --- 전광판 표출 설정 ---
	public static final String PREFERENCE_SETTING_BOARD_ENTRYNUM = "PREFERENCE_SETTING_BOARD_ENTRYNUM";
	public static final String PREFERENCE_SETTING_BOARD_EXHIBITOR = "PREFERENCE_SETTING_BOARD_EXHIBITOR";
	public static final String PREFERENCE_SETTING_BOARD_GENDER = "PREFERENCE_SETTING_BOARD_GENDER";
	public static final String PREFERENCE_SETTING_BOARD_WEIGHT = "PREFERENCE_SETTING_BOARD_WEIGHT";
	public static final String PREFERENCE_SETTING_BOARD_MOTHER = "PREFERENCE_SETTING_BOARD_MOTHER";
	public static final String PREFERENCE_SETTING_BOARD_PASSAGE = "PREFERENCE_SETTING_BOARD_PASSAGE";
	public static final String PREFERENCE_SETTING_BOARD_MATIME = "PREFERENCE_SETTING_BOARD_MATIME";
	public static final String PREFERENCE_SETTING_BOARD_KPN = "PREFERENCE_SETTING_BOARD_KPN";
	public static final String PREFERENCE_SETTING_BOARD_REGION = "PREFERENCE_SETTING_BOARD_REGION";
	public static final String PREFERENCE_SETTING_BOARD_NOTE = "PREFERENCE_SETTING_BOARD_NOTE";
	public static final String PREFERENCE_SETTING_BOARD_LOWPRICE = "PREFERENCE_SETTING_BOARD_LOWPRICE";
	public static final String PREFERENCE_SETTING_BOARD_DNA = "PREFERENCE_SETTING_BOARD_DNA";
	public static final String PREFERENCE_SETTING_BOARD_SUCPRICE = "PREFERENCE_SETTING_BOARD_SUCPRICE";
	public static final String PREFERENCE_SETTING_BOARD_SUCBIDDER = "PREFERENCE_SETTING_BOARD_SUCBIDDER";

	// --- PDP 표출 설정 ---
	public static final String PREFERENCE_SETTING_PDP_ENTRYNUM = "PREFERENCE_SETTING_PDP_ENTRYNUM";
	public static final String PREFERENCE_SETTING_PDP_EXHIBITOR = "PREFERENCE_SETTING_PDP_EXHIBITOR";
	public static final String PREFERENCE_SETTING_PDP_GENDER = "PREFERENCE_SETTING_PDP_GENDER";
	public static final String PREFERENCE_SETTING_PDP_WEIGHT = "PREFERENCE_SETTING_PDP_WEIGHT";
	public static final String PREFERENCE_SETTING_PDP_MOTHER = "PREFERENCE_SETTING_PDP_MOTHER";
	public static final String PREFERENCE_SETTING_PDP_PASSAGE = "PREFERENCE_SETTING_PDP_PASSAGE";
	public static final String PREFERENCE_SETTING_PDP_MATIME = "PREFERENCE_SETTING_PDP_MATIME";
	public static final String PREFERENCE_SETTING_PDP_KPN = "PREFERENCE_SETTING_PDP_KPN";
	public static final String PREFERENCE_SETTING_PDP_REGION = "PREFERENCE_SETTING_PDP_REGION";
	public static final String PREFERENCE_SETTING_PDP_NOTE = "PREFERENCE_SETTING_PDP_NOTE";
	public static final String PREFERENCE_SETTING_PDP_LOWPRICE = "PREFERENCE_SETTING_PDP_LOWPRICE";
	public static final String PREFERENCE_SETTING_PDP_DNA = "PREFERENCE_SETTING_PDP_DNA";
	public static final String PREFERENCE_SETTING_PDP_SUCPRICE = "PREFERENCE_SETTING_PDP_SUCPRICE";
	public static final String PREFERENCE_SETTING_PDP_SUCBIDDER = "PREFERENCE_SETTING_PDP_SUCBIDDER";

	// --- 상한가/하한가 ---
	public static final String PREFERENCE_SETTING_UPPER_CALF_TEXT = "PREFERENCE_SETTING_UPPER_CALF_TEXT";
	public static final String PREFERENCE_SETTING_UPPER_FATTENING_TEXT = "PREFERENCE_SETTING_UPPER_FATTENING_TEXT";
	public static final String PREFERENCE_SETTING_UPPER_BREEDING_TEXT = "PREFERENCE_SETTING_UPPER_BREEDING_TEXT";
	public static final String PREFERENCE_SETTING_LOWER_CALF_TEXT = "PREFERENCE_SETTING_LOWER_CALF_TEXT";
	public static final String PREFERENCE_SETTING_LOWER_FATTENING_TEXT = "PREFERENCE_SETTING_LOWER_FATTENING_TEXT";
	public static final String PREFERENCE_SETTING_LOWER_BREEDING_TEXT = "PREFERENCE_SETTING_LOWER_BREEDING_TEXT";
	// 경매 응찰 프로그램 환경설정 [END]
	
	// --- 동가 재경매 횟수 , 체크박스 ---
	public static final String PREFERENCE_SETTING_RE_AUCTION_COUNT = "PREFERENCE_SETTING_RE_AUCTION_COUNT";
	public static final String PREFERENCE_SETTING_RE_AUCTION_CHECK = "PREFERENCE_SETTING_RE_AUCTION_CHECK";
	// 동가 재경매 횟수  [END]
	
	// --- 연속 ,음성 경매,음성경매 대기 시간  ---
	public static final String PREFERENCE_SETTING_USE_ONE_AUCTION = "PREFERENCE_SETTING_USE_ONE_AUCTION";
	public static final String PREFERENCE_SETTING_USE_SOUND_AUCTION = "PREFERENCE_SETTING_SOUND_AUCTION";
	public static final String PREFERENCE_SETTING_SOUND_AUCTION_WAIT_TIME = "PREFERENCE_SETTING_SOUND_AUCTION_WAIT_TIME";
	// 연속 ,음성 경매  [END]

	// --- 메인 -> 음성설정 -> 텍스트 ---
	public static final String PREFERENCE_SETTING_SOUND_MSG_INTRO = "PREFERENCE_SETTING_SOUND_MSG_INTRO";
	public static final String PREFERENCE_SETTING_SOUND_MSG_BUYER = "PREFERENCE_SETTING_SOUND_MSG_BUYER";
	public static final String PREFERENCE_SETTING_SOUND_GUIDE = "PREFERENCE_SETTING_SOUND_GUIDE";
	public static final String PREFERENCE_SETTING_SOUND_PRACTICE = "PREFERENCE_SETTING_SOUND_PRACTICE";
	public static final String PREFERENCE_SETTING_SOUND_GENDER = "PREFERENCE_SETTING_SOUND_GENDER";
	public static final String PREFERENCE_SETTING_SOUND_USE = "PREFERENCE_SETTING_SOUND_USE";
	public static final String PREFERENCE_SETTING_SOUND_ETC_1 = "PREFERENCE_SETTING_SOUND_ETC_1";
	public static final String PREFERENCE_SETTING_SOUND_ETC_2 = "PREFERENCE_SETTING_SOUND_ETC_2";
	public static final String PREFERENCE_SETTING_SOUND_ETC_3 = "PREFERENCE_SETTING_SOUND_ETC_3";
	public static final String PREFERENCE_SETTING_SOUND_ETC_4 = "PREFERENCE_SETTING_SOUND_ETC_4";
	public static final String PREFERENCE_SETTING_SOUND_ETC_5 = "PREFERENCE_SETTING_SOUND_ETC_5";
	public static final String PREFERENCE_SETTING_SOUND_ETC_6 = "PREFERENCE_SETTING_SOUND_ETC_6";
	// 메인 -> 음성설정 -> 텍스트  [END]

	//계류대 번호
	public static final String PREFERENCE_SETTING_STAND_POSITION = "PREFERENCE_SETTING_MOORING_SQ";
	
	public static final String PREFERENCE_SETTING_SOUND_CONFIG = "PREFERENCE_SETTING_SOUND_CONFIG";
	
	// --- 메인 출품 정보 음성 설정 체크박스
	public static final String PREFERENCE_MAIN_SOUND_ENTRY_NUMBER = "PREFERENCE_MAIN_SOUND_ENTRY_NUMBER";
	public static final String PREFERENCE_MAIN_SOUND_ENTRY_EXHIBITOR = "PREFERENCE_MAIN_SOUND_ENTRY_EXHIBITOR";
	public static final String PREFERENCE_MAIN_SOUND_ENTRY_GENDER = "PREFERENCE_MAIN_SOUND_ENTRY_GENDER";
	public static final String PREFERENCE_MAIN_SOUND_ENTRY_MOTHER = "PREFERENCE_MAIN_SOUND_ENTRY_MOTHER";
	public static final String PREFERENCE_MAIN_SOUND_ENTRY_MATIME = "PREFERENCE_MAIN_SOUND_ENTRY_MATIME";
	public static final String PREFERENCE_MAIN_SOUND_ENTRY_PASGQCN = "PREFERENCE_MAIN_SOUND_ENTRY_PASGQCN";
	public static final String PREFERENCE_MAIN_SOUND_ENTRY_WEIGHT = "PREFERENCE_MAIN_SOUND_ENTRY_WEIGHT";
	public static final String PREFERENCE_MAIN_SOUND_ENTRY_LOWPRICE = "PREFERENCE_MAIN_SOUND_ENTRY_LOWPRICE";
	public static final String PREFERENCE_MAIN_SOUND_ENTRY_BRAND = "PREFERENCE_MAIN_SOUND_ENTRY_BRAND";
	public static final String PREFERENCE_MAIN_SOUND_ENTRY_KPN = "PREFERENCE_MAIN_SOUND_ENTRY_KPN";
	public static final String PREFERENCE_MAIN_SOUND_ENTRY_DNA = "PREFERENCE_MAIN_SOUND_ENTRY_DNA";
	// 메인 출품 정보 음성 설정 체크박스  [END]
	
	public static final String PREFERENCE_SEND_MESSAGE = "PREFERENCE_SEND_MESSAGE"; // 메세지 전송
	
	//로그인 아이디 저장
	public static final String PREFERENCE_LOGIN_SAVE_ID = "PREFERENCE_LOGIN_SAVE_ID";
	
	public static final String PREFERENCE_SERVER_IP = "PREFERENCE_SERVER_IP";
	public static final String PREFERENCE_SERVER_PORT = "PREFERENCE_SERVER_PORT";
	public static final String PREFERENCE_SELECTED_OBJ = "PREFERENCE_SELECTED_OBJ";
	
	//전광판 비고 흐름 여부
	public static final String PREFERENCE_BOARD_USE_NOTE_1 = "PREFERENCE_BOARD_USE_NOTE_1";
	public static final String PREFERENCE_BOARD_USE_NOTE_2 = "PREFERENCE_BOARD_USE_NOTE_2";
	

	public final Preferences prefs;

	public SharedPreference() {
		prefs = Preferences.userNodeForPackage(SharedPreference.class);
	}

	/**
	 * Preference에 String형태로 저장하는 함수
	 *
	 * @param key   저장 String Key
	 * @param value 저장 String Value
	 * @author 이종환
	 */
	public void setString(String key, String value) {
		prefs.put(key, value);
	}

	/**
	 * Preference에 int형태로 저장하는 함수
	 *
	 * @param key   저장 String Key
	 * @param value 저장 String Value
	 * @author 이종환
	 */
	public void setInt(String key, int value) {
		prefs.putInt(key, value);
	}

	/**
	 * Preference에 int형태로 저장하는 함수
	 *
	 * @param key   저장 String Key
	 * @param value 저장 String Value
	 * @author 이종환
	 */
	public void setBoolean(String key, boolean value) {
		prefs.putBoolean(key, value);
	}

	/**
	 * Preference에 byteArray형태로 저장하는 함수
	 *
	 * @param key   저장 String Key
	 * @param value 저장 byte[] Value
	 * @author 김도희
	 */
	public void setByteArr(String key, byte[] value) {
		prefs.putByteArray(key, value);
	}

	/**
	 * Preference에서 key에 맞는 value 반환 함수
	 *
	 * @param key          저장 String Key
	 * @param defaultValue 반환 기본값
	 * @return String 반환값
	 * @author 이종환
	 */
	public String getString(String key, String defaultValue) {
		return prefs.get(key, defaultValue);
	}

	/**
	 * Preference에서 key에 맞는 value 반환 함수
	 *
	 * @param key          저장 String Key
	 * @param defaultValue 반환 기본값
	 * @return Int 반환값
	 * @author 이종환
	 */
	public int getInt(String key, int defaultValue) {
		return prefs.getInt(key, defaultValue);
	}

	/**
	 * Preference에서 key에 맞는 value 반환 함수
	 *
	 * @param key          저장 String Key
	 * @param defaultValue 반환 기본값
	 * @return boolean 반환값
	 * @author 이종환
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		return prefs.getBoolean(key, defaultValue);
	}

	/**
	 * Preference에서 key에 맞는 value 반환 함수
	 *
	 * @param key          저장 String Key
	 * @param defaultValue 반환 기본값
	 * @return boolean 반환값
	 * @author 김도희
	 */
	public byte[] getByteArr(String key, byte[] defaultValue) {
		return prefs.getByteArray(key, defaultValue);
	}

	/**
	 * @MethodName clearAll
	 * @Description 모든 정보 지움
	 */
	public void clearAll() {
		try {
			prefs.clear();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
