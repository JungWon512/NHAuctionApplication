package com.nh.controller.service;

import java.util.List;

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
	public List<EntryInfo> getAllEntryData() {
		SqlSession session = DBSeesionFactory.getSession();
		List<EntryInfo> list = null;
		try {
			list = dao.selectAllEntryInfo(session);

			for (int i = 0; i < list.size(); i++) {
				if (i == list.size() - 1) {
					list.get(i).setIsLastEntry("Y");
				} else {
					list.get(i).setIsLastEntry("N");
				}
			}
		} finally {
			session.close();
		}
		return list;
	}
}
