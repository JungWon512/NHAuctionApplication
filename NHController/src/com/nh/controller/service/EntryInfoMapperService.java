package com.nh.controller.service;

import com.nh.controller.dao.EntryInfoDao;
import com.nh.controller.database.DBSessionFactory;
import com.nh.controller.mapper.EntryInfoMapper;
import com.nh.share.controller.models.EntryInfo;
import org.apache.ibatis.session.SqlSession;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 출품 데이터 MapperService
 *
 * @author dhKim
 */
public class EntryInfoMapperService extends BaseMapperService<EntryInfoDao> implements EntryInfoMapper {

    public EntryInfoMapperService() {
        this.setDao(new EntryInfoDao());
    }

    @Override
    public List<EntryInfo> getAllEntryData(String date,
                                           String auctionHouseCode,
                                           String entryType) {

        Map<String, String> map = new LinkedHashMap<>();
        map.put("auctionDate", date);
        map.put("auctionHouseCode", auctionHouseCode);
        map.put("entryType", entryType);

        List<EntryInfo> list;
        try (SqlSession session = DBSessionFactory.getSession()) {
            list = dao.selectAllEntryInfo(map, session);
        }

        if (!list.isEmpty()) {
            // 마지막 출품정보 표기 (Y/N)
            for (int i = 0; i < list.size(); i++) {
                String flag = (i == list.size() - 1) ? "Y" : "N";
                list.get(i).setIsLastEntry(flag);
            }
        }
        return list;
    }
}
