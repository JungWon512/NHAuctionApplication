package com.nh.controller.dao;

import com.nh.controller.model.User;
import org.apache.ibatis.session.SqlSession;

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
