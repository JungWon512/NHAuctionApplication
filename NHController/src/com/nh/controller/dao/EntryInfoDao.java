package com.nh.controller.dao;

import com.nh.share.controller.models.EntryInfo;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * 출품 데이터 Dao
 * 
 * @author dhKim
 *
 */
public class EntryInfoDao {

	public List<EntryInfo> selectAllEntryInfo(SqlSession session) {
		List<EntryInfo> list = session.selectList("selectAllEntry");
		return list;
	}
}
