package com.nh.share.language;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class Language {

    private static Language mLanguage = new Language();
    private static final Preferences prefs = Preferences.userNodeForPackage(Language.class);
    
    private static final String BUNDLES_PATH = "com.glovis.share.language.bundles.Language";
    private static final String PREF_LANGUAGE = "LANGUAGE"; // 현재 적용중인 언어 (Preferences key)
    private static ResourceBundle bundle;

    /**
     * 
     * @MethodName changeLanguage
     * @Description 언어 변경 
     * 
     * @param language > 변경할 언어 (LanguageDefine.KOREAN or LanguageDefine.ENGLISH)
     */
    public static void changeLanguage(String language) {
        if (language != null && language.length() > 0) {
            if (language.equals(LanguageDefine.KOREAN)) {
                bundle = ResourceBundle.getBundle(BUNDLES_PATH, new Locale("kr", "KR"));
            }else {
                bundle = ResourceBundle.getBundle(BUNDLES_PATH, new Locale("en", "EN"));
            }
            prefs.put(PREF_LANGUAGE,language);
        }
    }
    
    /**
     * 
     * @MethodName getNowLanguage
     * @Description 현재 설정 되어있는 언어 
     * 
     * @return default : korean
     */
    public static String getNowLanguage() {
        return prefs.get(PREF_LANGUAGE,LanguageDefine.KOREAN);
    }
    
    /**
     * 
     * @MethodName getValue
     * @Description 전달받은 Key의 value값 return.
     * 
     * @param keyName > Language_en.properties or Language_kr.properties 안에 있는 key 
     * @return
     */
    public static String getValue(String keyName) {
        if (bundle == null) {
            if (getNowLanguage().equals(LanguageDefine.KOREAN)) {
                bundle = ResourceBundle.getBundle(BUNDLES_PATH, new Locale("kr", "KR"));
            }else {
                bundle = ResourceBundle.getBundle(BUNDLES_PATH, new Locale("en", "EN"));
            }
        }
        
        String tempValue = "";
        try {
            tempValue = new String(bundle.getString(keyName).getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return tempValue;
    }
    
    /**
     * 
     * @MethodName setLanguage
     * @Description 언어 설정 정보 저장. 
     * 
     * @param language
     */
    private void setLanguage(String language) {
        if (language != null && language.length() > 0) {
            prefs.put(PREF_LANGUAGE,language);
        }
    }
    
    
}
