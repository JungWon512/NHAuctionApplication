package com.nh.controller.preferences;

import java.util.prefs.Preferences;

import com.nh.share.api.model.AuctionLoginResult;
import com.nh.share.api.model.AuctionLoginResultUserInfo;

public class SharedPreference {
    
    public static final Preferences prefs = Preferences.userNodeForPackage(SharedPreference.class);
    
    private static final String USERINFO_LOGIN_ID = "CONTROL_LOGIN_ID"; // 로그인 ID (로그인 화면에서 입력한 ID) 
    private static final String USERINFO_MEMBER_NUM = "CONTROL_MEMBER_NUM"; // 고객 회원 번호 
    
    public static void setUserLoginID(String id) {
        if (id != null || id.length() > 0) {
            prefs.put(USERINFO_LOGIN_ID,id);
        }
    }
    
    public static String getUserLoginId() {
        return prefs.get(USERINFO_LOGIN_ID,"");
    }
    
    /**
     * 
     * @MethodName setUserInfo
     * @Description 로그인 성공 후 User 정보 저장 
     * 
     * @param userResult
     */
    public static void setUserInfo(AuctionLoginResult userResult) {
        String authResult = userResult.getAuthResult();
        if (authResult != null && authResult.equals("success")) {
            if (userResult.getUserInfo() != null) {
                AuctionLoginResultUserInfo userInfo = userResult.getUserInfo().get(0);
                setMemberNum(userInfo.getMemberNum()); // 고객 회원 번호 저장 
            } 
        }
    }
    
    public static void setMemberNum(String memberNum) {
        if (memberNum != null && memberNum.length() > 0) {
            prefs.put(USERINFO_MEMBER_NUM,memberNum);
        }
    }
    
    public static String getMemberNum() {
        return prefs.get(USERINFO_MEMBER_NUM,"");
    }
}
