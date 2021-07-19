package com.nh.controller.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nh.controller.model.AuctionRound;

public class AuctionRoundDao {

	public List<AuctionRound> selectAllAuctionRound(SqlSession session) {
		List<AuctionRound> list = session.selectList("selectAllAuctionRound");
		return list;
	}
}
