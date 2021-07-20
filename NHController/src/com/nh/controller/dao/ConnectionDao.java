package com.nh.controller.dao;

import org.apache.ibatis.session.SqlSession;

import com.nh.controller.model.User;

/**
 * 회원 인증 Dao
 * 
 * @author dhKim
 *
 */
public class ConnectionDao {

	public User selectUserInfo(SqlSession session) {
		User u = session.selectOne("getUserInfo", "param");
		return u;
	}

	public void insertUserInfo(User user, SqlSession session) {
		session.insert("insertUserInfo", user);
	}
	
}
