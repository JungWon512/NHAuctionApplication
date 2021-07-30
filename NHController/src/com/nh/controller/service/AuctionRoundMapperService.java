package com.nh.controller.service;

import com.nh.controller.dao.AuctionRoundDao;
import com.nh.controller.database.DBSessionFactory;
import com.nh.controller.mapper.AuctionRoundMapper;
import com.nh.controller.model.AuctionRound;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * 경매 회차 정보 MapperService
 *
 * @author dhKim
 */
public class AuctionRoundMapperService extends BaseMapperService<AuctionRoundDao> implements AuctionRoundMapper {

    public AuctionRoundMapperService() {
        this.setDao(new AuctionRoundDao());
    }

    @Override
    public List<AuctionRound> getAllAuctionRoundData(String date) {
        try (SqlSession session = DBSessionFactory.getSession()) {
            return getDao().selectAllAuctionRound(date, session);
        }
    }
}