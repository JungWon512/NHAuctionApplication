package com.nh.controller.utils;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * 내부 저장 Class
 *
 * @author jhlee
 */
public class SharedPreference {

    // 경매 응찰 프로그램 환경설정 [START]
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
    public static final String PREFERENCE_SETTING_NOTE = "PREFERENCE_SETTING_NOTE"; // 비고창 설정


    // 경매 응찰 프로그램 환경설정 [END]

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
    public void setByteArr(String key, byte[] value) { prefs.putByteArray(key, value); }

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
    public byte[] getByteArr(String key, byte[] defaultValue) { return prefs.getByteArray(key, defaultValue); }

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
