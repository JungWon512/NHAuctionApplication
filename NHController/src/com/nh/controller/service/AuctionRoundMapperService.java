package com.nh.controller.service;

import com.nh.controller.dao.AuctionRoundDao;
import com.nh.controller.database.DBSessionFactory;
import com.nh.controller.mapper.AuctionRoundMapper;
import com.nh.controller.model.AuctionStnData;
import com.nh.controller.model.AuctionRound;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * 경매 회차 정보 MapperService
 *
 * @author dhKim
 */
public class AuctionRoundMapperService extends BaseMapperService<AuctionRoundDao> implements AuctionRoundMapper {

    private static AuctionRoundMapperService auctionRoundMapperService = null;

    public static AuctionRoundMapperService getInstance() {
        if (auctionRoundMapperService == null) {
            auctionRoundMapperService = new AuctionRoundMapperService();
        }
        return auctionRoundMapperService;
    }

    public AuctionRoundMapperService() {
        this.setDao(new AuctionRoundDao());
    }

    @Override
    public List<AuctionRound> getAllAuctionRoundData(AuctionRound auctionRound) {
        try (SqlSession session = DBSessionFactory.getSession()) {
            return getDao().selectAllAuctionRound(auctionRound, session);
        }
    }

	@Override
	public List<AuctionStnData> searchAuctionStnData(AuctionStnData auctionStnData) {
		 try (SqlSession session = DBSessionFactory.getSession()) {
	            return getDao().selectAuctionStnData(auctionStnData, session);
	        }
	}

	@Override
	public AuctionRound obtainAuctionRoundData(AuctionRound auctionRound) {
		try (SqlSession session = DBSessionFactory.getSession()) {
            return getDao().obtainAuctionRound(auctionRound, session);
        }
	}
}