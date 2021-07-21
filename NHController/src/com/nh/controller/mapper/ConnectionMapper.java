package com.nh.controller.mapper;

import com.nh.controller.model.User;

/**
 * 회원 로그인 정보 Interface Mapper
 * 
 * @author dhKim
 *
 */
public interface ConnectionMapper {
	
	// select 
	User selectUserInfo();
	
	// insert
	void insertUserInfo();

}
