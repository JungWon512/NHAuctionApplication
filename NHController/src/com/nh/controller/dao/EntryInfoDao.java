package com.nh.controller.dao;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nh.share.controller.models.EntryInfo;

public class EntryInfoDao {

	public Map<String, EntryInfo> selectAllEntryInfo(SqlSession session) {
		Map<String, EntryInfo> map = session.selectMap("selectAllEntry", "mEntryNum");
		return map;
	}
}
