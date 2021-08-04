package com.nh.controller.service;

import com.nh.controller.dao.EntryInfoDao;
import com.nh.controller.database.DBSessionFactory;
import com.nh.controller.mapper.EntryInfoMapper;
import com.nh.controller.utils.CommonUtils;
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

	private static EntryInfoMapperService entryInfoMapperService = null;
	
	public static EntryInfoMapperService getInstance() {
		
		if (entryInfoMapperService == null) {
			entryInfoMapperService = new EntryInfoMapperService();
		}
		return entryInfoMapperService;
	}
	
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
            list = getDao().selectAllEntryInfo(map, session);
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

	@Override
	public int updateEntryPrice(EntryInfo entryInfo) {
		
		int resultValue = 0;
		
		try (SqlSession session = DBSessionFactory.getSession()) {

			resultValue = getDao().updateEntryPrice(entryInfo,session);
			
			if(resultValue > 0) {
				session.commit();
			}else {
				session.rollback();
			}
			
		}catch (Exception e) {
			//exception에 대한 처리 시 사용
			resultValue = -1;
		}

		return resultValue;
	}
    
	@Override
	public int updateEntryState(EntryInfo entryInfo) {
		
		int resultValue = 0;
		
		try (SqlSession session = DBSessionFactory.getSession()) {

			resultValue = getDao().updateEntryState(entryInfo,session);
			
			if(resultValue > 0) {
				session.commit();
			}else {
				session.rollback();
			}
			
		}catch (Exception e) {
			//exception에 대한 처리 시 사용
			resultValue = -1;
		}

		return resultValue;
	}
    
    
}
