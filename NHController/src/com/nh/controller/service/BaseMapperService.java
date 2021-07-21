package com.nh.controller.service;

/**
 * MapperService를 구현하려면 이 클래스를 상속 받아야한다.
 * 
 * @author dhKim
 *
 */
public abstract class BaseMapperService<T> {
	protected T dao;

	public void setDao(T obj) {
		this.dao = obj;
	}

	public T getDao() {
		return dao;
	}
}
