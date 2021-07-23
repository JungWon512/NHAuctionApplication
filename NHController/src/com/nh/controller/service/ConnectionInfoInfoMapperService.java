package com.nh.controller.service;

import com.nh.controller.dao.ConnectionInfoDao;
import com.nh.controller.database.DBSessionFactory;
import com.nh.controller.mapper.ConnectionInfoMapper;
import org.apache.ibatis.session.SqlSession;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 가축경매 참여자 번호 MapperService
 *
 * @author dhKim
 */
public class ConnectionInfoInfoMapperService extends BaseMapperService<ConnectionInfoDao> implements ConnectionInfoMapper {

    public ConnectionInfoInfoMapperService() {
        this.setDao(new ConnectionInfoDao());
    }

    @Override
    public String selectUserInfo(String auctionHouseCode, String auctionDate, String entryType, String userMemNum) {
        SqlSession session = DBSessionFactory.getSession();
        String userNum;

        Map<String, String> map = new LinkedHashMap<>();
        map.put("auctionHouseCode", auctionHouseCode);
        map.put("auctionDate", auctionDate);
        map.put("entryType", entryType);
        map.put("userMemNum", userMemNum);

        try {
            userNum = dao.selectUserInfo(map, session);
        } finally {
            session.close();
        }
        return userNum;
    }

    @Override
    public void insertUserInfo() {


    }

}
