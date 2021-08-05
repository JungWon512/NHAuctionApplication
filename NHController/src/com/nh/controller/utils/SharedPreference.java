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
    public static final String PREFERENCE_SETTING_AAA = "PREFERENCE_SETTING_AAA"; // TEST AAA
    public static final String PREFERENCE_SETTING_BBB = "PREFERENCE_SETTING_BBB"; // TEST BBB
    public static final String PREFERENCE_SETTING_MOBILE_CHECK_BOX = "PREFERENCE_SETTING_MOBILE_CHECK_BOX"; // 모바일 설정 Checkbox
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
    public byte[] getByteArr(String key, byte[] defaultValue) { return prefs.getByteArray(key,defaultValue); }

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
