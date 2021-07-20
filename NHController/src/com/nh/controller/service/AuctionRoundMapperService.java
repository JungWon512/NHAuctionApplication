package com.nh.controller.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nh.controller.dao.AuctionRoundDao;
import com.nh.controller.database.DBSeesionFactory;
import com.nh.controller.mapper.AuctionRoundMapper;
import com.nh.controller.model.AuctionRound;

/**
 * 경매 회차 정보 MapperService
 * 
 * @author dhKim
 *
 */
public class AuctionRoundMapperService extends BaseMapperService<AuctionRoundDao> implements AuctionRoundMapper {

	public AuctionRoundMapperService() {
		this.setDao(new AuctionRoundDao());
	}

	@Override
	public List<AuctionRound> getAllAuctionRoundData() {
		SqlSession session = DBSeesionFactory.getSession();
		List<AuctionRound> list = new ArrayList<>();
		try {
			list = dao.selectAllAuctionRound(session);
		} finally {
			session.close();
		}
		return list;
	}
}