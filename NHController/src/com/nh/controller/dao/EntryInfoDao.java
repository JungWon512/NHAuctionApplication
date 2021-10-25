package com.nh.controller.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nh.controller.model.AucEntrData;
import com.nh.controller.model.AuctionRound;
import com.nh.controller.model.AuctionStnData;
import com.nh.controller.model.FeeData;
import com.nh.controller.model.FeeImpsData;
import com.nh.controller.model.SelStsCountData;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.SendAuctionResult;

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
    
    public EntryInfo obtainEntryInfo(EntryInfo entryInfo, SqlSession session) {
        return session.selectOne("obtainEntryInfo", entryInfo);
    }
    
    public List<FeeData> selectFee(FeeData fee, SqlSession session) {
        return session.selectList("selectFee", fee);
    }

    public SelStsCountData selectSelStsCount(AuctionStnData auctionStnData, SqlSession session) {
        return session.selectOne("selectSelStsCount", auctionStnData);
    }
    
    /**
     * 가격 정보 업데이트
     * @param EntryInfo
     * @param session
     * @return
     */
    public int updateEntryPrice(EntryInfo entryInfo,SqlSession session) {
        return session.update("updateEntryPrice", entryInfo);
    }
    
    /**
     * 가격 정보 다중 업데이트
     * @param List<EntryInfo>
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
     * @param SendAuctionResult
     * @param session
     * @return
     */
    public int updateAuctionResult(EntryInfo entryInfo,SqlSession session) {
        return session.update("updateAuctionResult", entryInfo);
    }
    
    /**
     * 경매 결과 저장
     * @param AucEntrData
     * @param session
     * @return
     */
    public int insertBiddingHistory(AucEntrData aucEntrData,SqlSession session) {
        return session.update("insertBiddingHistory", aucEntrData);
    }
    
    /**
     * 응찰 내역 카운트
     * @param AucEntrData
     * @param session
     * @return
     */
    public int selectBiddingHistoryCount(AucEntrData aucEntrData,SqlSession session) {
        return session.selectOne("selectBiddingHistoryCount", aucEntrData);
    }
    
    /**
     * 다음 저장될 응찰 내역 RG_SQNO 조회
     * @param AucEntrData
     * @param session
     * @return
     */
    public int selectNextBiddingHistoryCount(AucEntrData aucEntrData,SqlSession session) {
        return session.selectOne("selectNextBiddingHistoryCount", aucEntrData);
    }
    

    /**
     * 수수료 데이터 제거
     * @param feeData
     * @param session
     * @return
     */
    public int deleteFeeImps(FeeImpsData feeData,SqlSession session) {
        return session.delete("deleteFeeImps", feeData);
    }
    
    
    /**
     * 수수료 데이터 저장
     * @param feeData
     * @param session
     * @return
     */
    public int insertFeeImpsList(List<FeeImpsData> feeDataList,SqlSession session) {
        return session.insert("insertFeeImpsList", feeDataList);
    }
    
    
}
