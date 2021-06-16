package com.nh.auction.preferences;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class SharedPreference {
    
 // 로그인 사용자 정보
    public static final String USER_INFO_LOGIN_ID = "PC_BDDING_APPLICATION_LOGIN_ID"; // 로그인 ID (로그인 화면에서 입력한 ID)
    public static final String USER_INFO_LOGIN_CHECK_SAVE = "PC_BDDING_APPLICATION_LOGIN_CHECK_BOX"; // 아이디 저장 여부
    public static final String USER_INFO_AUTH_TOKEN = "PC_BDDING_APPLICATION_AUTH_TOKEN"; // 로그인 Token
    public static final String USER_INFO_REFRESH_TOKEN = "PC_BDDING_APPLICATION_REFRESH_TOKEN"; // refresh Token
    public static final String USER_INFO_USER_STATUS = "PC_BDDING_APPLICATION_MAMBER_STATUS"; // 회원 상태
    public static final String USER_INFO_IS_AUCTION_MAMBER= "PC_BDDING_APPLICATION_IS_AUCTION_MAMBER"; // true : 경매회원 , false :  관전
    public static final String USER_INFO_USER_NUM = "PC_BDDING_APPLICATION_MEMBER_NUM"; // 고객 회원 번호
    public static final String USER_INFO_USER_NAME = "PC_BDDING_APPLICATION_USER_NAME"; // 회원명
    public static final String USER_INFO_USER_CONTROL_FLAG = "PC_BDDING_APPLICATION_USER_CONTROL_FLAG"; // 통제 회원 여부
    public static final String USER_INFO_PROFILE_IMAGE_URL = "PC_BDDING_APPLICATION_PROFILE_IMAGE_URL"; // 회원 프로필 이미지
    public static final String USER_INFO_YEAR_FEE_EXPI_YMD = "PC_BDDING_APPLICATION_YEAR_FEE_EXPI_YMD"; // 연회비 만료일
    public static final String USER_INFO_DDAY = "PC_BDDING_APPLICATION_DDAY"; // 만료 남은 일자
    public static final String USER_INFO_VIRTUAL_ACC = "PC_BDDING_APPLICATION_VIRTUAL_ACC"; // 가상계좌
    public static final String USER_INFO_VIRTUAL_ACC_NM = "PC_BDDING_APPLICATION_VIRTUAL_ACC_NM"; // 가상계좌 예금주
    public static final String USER_INFO_RECENT_CONNECT_DATE_TIME = "PC_BDDING_APPLICATION_RECENT_CONNECT_DATE_TIME"; // 최근 접속일시
    public static final String USER_INFO_AUTO_LOGIN = "PC_BDDING_APPLICATION_AUTO_LOGIN"; // 자동 로그인
    public static final String USER_INFO_YEAR_FEE_WEEK_DAY_CHECK = "PC_BDDING_APPLICATION_YEAR_FEE_WEEK_DAY_CHECK";   // 연회비 일주일 체크
    public static final String USER_INFO_YEAR_FEE_NOTI_YN = "PC_BDDING_APPLICATION_YEAR_FEE_NOTI_YN";   // 연회비 노출여부
    
    //경매 응찰 프로그램 환경설정 [START]
    public static final String PREFERENCE_BIDDING_APPLICATION_SETTING_SCREEN_BRIGHT= "PC_BDDING_APPLICATION_SCREEN_BRIGHT"; // 화면 밝기 
    public static final String PREFERENCE_BIDDING_APPLICATION_SETTING_SHORTCUT_KEY="PC_BDDING_APPLICATION_SHORTCUT_KEY"; // 단축키 ON/OFF
    public static final String PREFERENCE_BIDDING_APPLICATION_SETTING_LANGUAGE= "PC_BDDING_APPLICATION_LANGUAGE"; // 언어변경
    public static final String PREFERENCE_BIDDING_APPLICATION_SETTING_SESSION_TIME= "PC_BDDING_APPLICATION_SESSION_TIME"; // 세션 유지시간
    public static final String PREFERENCE_BIDDING_APPLICATION_VERSION= "PC_BDDING_APPLICATION_VERSION"; // 응찰 프로그램 버전
    public static final String PREFERENCE_BIDDING_APPLICATION_MAIN_TUTORIAL_FIRST= "PC_BIDDING_APPLICATION_MAIN_TUTORIAL_FIRST"; // 메인 화면 튜토리얼
    public static final String PREFERENCE_BIDDING_APPLICATION_AUCTION_TUTORIAL_FIRST= "PC_APPLICATION_AUCTION_TUTORIAL_FIRST"; // 경매 화면 튜토리얼
    public static final String PREFERENCE_MAIN_NOTICE= "PC_BDDING_APPLICATION_MAIN_NOTICE"; // 메인 화면 공지사항
    
    public static final String PREFERENCE_ANNUAL_MEMBERSHIP_FEE_CHECK= "PC_BDDING_APPLICATION_ANNUAL_MEMBERSHIP_FEE_CHECK"; // 연회비 일주일간 보지 않기 체크
    public static final String PREFERENCE_APP_FIRST_START= "PC_BDDING_APPLICATION_PREFERENCE_APP_FIRST_START"; // 앱 첫 실행 유무
  //경매 응찰 프로그램 환경설정 [END]
    
    public  final Preferences prefs;
    
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
        prefs.put(key,value);
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
   * 
   * @MethodName clearAll
   * @Description 모든 정보 지움
   *
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
