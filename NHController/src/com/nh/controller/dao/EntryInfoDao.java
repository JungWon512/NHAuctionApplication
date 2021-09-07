package com.nh.controller.dao;

import com.nh.controller.model.AucEntrData;
import com.nh.controller.model.AuctionRound;
import com.nh.controller.model.AuctionStnData;
import com.nh.controller.model.SelStsCountData;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.SendAuctionResult;

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
	
	public int selectAllEntryInfoCount(AuctionRound auctionRound, SqlSession session) {
		return session.selectOne("selectAllEntryCount", auctionRound);
	}

    public List<EntryInfo> selectAllEntryInfo(AuctionRound auctionRound, SqlSession session) {
        return session.selectList("selectAllEntry", auctionRound);
    }
    
    public List<EntryInfo> selectStnEntryInfo(AuctionStnData auctionStnData, SqlSession session) {
        return session.selectList("selectStnEntry", auctionStnData);
    }
    
    public SelStsCountData selectSelStsCount(AuctionStnData auctionStnData, SqlSession session) {
        return session.selectOne("selectSelStsCount", auctionStnData);
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
     * 가격 정보 다중 업데이트
     * @param entryInfo
     * @param session
     * @return
     */
    public int updateEntryPriceList(List<EntryInfo> entryInfoList,SqlSession session) {
        return session.update("updateEntryPriceList", entryInfoList);
    }
    
    /**
     * 경매 상태 업데이트
     * @param entryInfo
     * @param session
     * @return
     */
    public int updateEntryState(EntryInfo entryInfo,SqlSession session) {
        return session.update("updateEntryState", entryInfo);
    }
    
    /**
     * 경매 결과 저장
     * @param auctionResult
     * @param session
     * @return
     */
    public int updateAuctionResult(SendAuctionResult auctionResult,SqlSession session) {
        return session.update("updateAuctionResult", auctionResult);
    }
    
    /**
     * 경매 결과 저장
     * @param auctionResult
     * @param session
     * @return
     */
    public int insertBiddingHistory(AucEntrData aucEntrData,SqlSession session) {
        return session.update("insertBiddingHistory", aucEntrData);
    }
}
