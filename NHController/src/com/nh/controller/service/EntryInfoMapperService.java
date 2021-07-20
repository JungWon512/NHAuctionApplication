package com.nh.controller.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nh.controller.dao.EntryInfoDao;
import com.nh.controller.database.DBSeesionFactory;
import com.nh.controller.mapper.EntryInfoMapper;
import com.nh.share.controller.models.EntryInfo;

/**
 * 출품 데이터 MapperService
 * 
 * @author dhKim
 *
 */
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

			// 마지막 출품정보 표기 (Y/N)
			for (int i = 0; i < list.size(); i++) {
				String flag = (i == list.size() - 1) ? "Y" : "N";
				list.get(i).setIsLastEntry(flag);
			}
		} finally {
			session.close();
		}
		return list;
	}
}
