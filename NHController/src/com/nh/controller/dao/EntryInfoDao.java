package com.nh.controller.dao;

import com.nh.share.controller.models.EntryInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Map;

/**
 * 출품 데이터 Dao
 *
 * @author dhKim
 */
public class EntryInfoDao {

    public List<EntryInfo> selectAllEntryInfo(Map<String, String> entryInfoMap, SqlSession session) {
        return session.selectList("selectAllEntry", entryInfoMap);
    }
    
    /**
     * 가격 정보 업데이트
     * @param entryInfo
     * @param session
     * @return
     */
    public int updateEntryPrice(EntryInfo entryInfo,SqlSession session) {
        return session.update("updateEntryPrice", entryInfo);
    }
    
    /**
     * 가격 정보 업데이트
     * @param entryInfo
     * @param session
     * @return
     */
    public int updateEntryState(EntryInfo entryInfo,SqlSession session) {
        return session.update("updateEntryState", entryInfo);
    }
}
