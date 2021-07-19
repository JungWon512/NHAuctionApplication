package com.nh.controller.service;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nh.controller.dao.EntryInfoDao;
import com.nh.controller.database.DBSeesionFactory;
import com.nh.controller.mapper.EntryInfoMapper;
import com.nh.share.controller.models.EntryInfo;

public class EntryInfoMapperService implements EntryInfoMapper {
	private EntryInfoDao dao;

	public EntryInfoMapperService() {
		dao = new EntryInfoDao();
	}

	@Override
	public Map<String, EntryInfo> getAllEntryData() {
		SqlSession session = DBSeesionFactory.getSession();
		Map<String, EntryInfo> map = null;
		try {
			map =  dao.selectAllEntryInfo(session);
		} finally {
			session.close();
		}
		return map;
	}
}
