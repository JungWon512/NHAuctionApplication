package com.nh.controller.service;

import com.nh.controller.mapper.ConnectionMapper;
import com.nh.controller.model.User;

public class ConnectionMapperService extends BaseMapperService<User> implements ConnectionMapper {

	public ConnectionMapperService() {
		this.setDao(new User());
	}

	@Override
	public User selectUserInfo() {
		return null;
	}

	@Override
	public void insertUserInfo() {
	}

}
