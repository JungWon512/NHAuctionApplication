package com.nh.controller.dao;

import com.nh.controller.model.AuctionStnData;
import com.nh.controller.model.AuctionRound;
import org.apache.ibatis.session.SqlSession;

import java.util.Date;
import java.util.List;

/**
 * 경매 회차 정보 Dao
 *
 * @author dhKim
 */
public class AuctionRoundDao {

    public List<AuctionRound> selectAllAuctionRound(AuctionRound auctionRound, SqlSession session) {
        return session.selectList("selectAllAuctionRound", auctionRound);
    }
    
    public List<AuctionStnData> selectAuctionStnData(AuctionStnData auctionStnData, SqlSession session) {
        return session.selectList("selectAuctionStnData", auctionStnData);
    }
    
    public AuctionRound obtainAuctionRound(AuctionRound auctionRound, SqlSession session) {
        return session.selectOne("obtainAuctionRound", auctionRound);
    }
}
