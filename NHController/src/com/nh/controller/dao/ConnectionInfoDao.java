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

    public String selectUserInfo(Map<String, String> connectionInfo, SqlSession session) {
        return session.selectOne("selectConnectionInfo", connectionInfo);
    }

    public int selectIdSequence(SqlSession session) {
        return session.selectOne("selectSequenceId");
    }

    public void insertUserInfo(List<UserInfo> user, SqlSession session) {
        session.insert("insertConnectionInfo", user);
    }

}
