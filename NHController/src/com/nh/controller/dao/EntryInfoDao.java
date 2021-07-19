package com.nh.controller.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nh.share.controller.models.EntryInfo;

public class EntryInfoDao {

	public List<EntryInfo> selectAllEntryInfo(SqlSession session) {
		List<EntryInfo> list = session.selectList("selectAllEntry");
		return list;
	}
}
