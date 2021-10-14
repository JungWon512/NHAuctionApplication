package com.nh.controller.dao;

import com.nh.controller.model.UserInfo;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Map;

/**
 * 회원 인증 Dao
 *
 * @author dhKim
 */
public class ConnectionInfoDao {

    public String selectAdminInfo(Map<String, String> adminInfo, SqlSession session) {
        return session.selectOne("selectAdminInfo", adminInfo);
    }

    public String selectUserInfo(Map<String, String> connectionInfo, SqlSession session) {
        return session.selectOne("selectConnectionInfo", connectionInfo);
    }

    public String selectIdSequence(SqlSession session) {
        return session.selectOne("selectSequenceId");
    }

    public void insertUserInfo(List<UserInfo> user, SqlSession session) {
        session.insert("insertConnectionInfo", user);
    }
    
    public String selectMacoYn(Map<String, String> userInfo, SqlSession session) {
    	return session.selectOne ("selectMacoYn", userInfo);
    }

}
