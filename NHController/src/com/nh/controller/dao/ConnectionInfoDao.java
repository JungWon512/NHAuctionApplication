package com.nh.controller.dao;

import com.nh.share.common.models.ConnectionInfo;
import org.apache.ibatis.session.SqlSession;

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

    public void insertUserInfo(ConnectionInfo user, SqlSession session) {
        session.insert("insertConnectionInfo", user);
    }

}
