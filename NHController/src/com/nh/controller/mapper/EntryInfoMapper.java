package com.nh.controller.mapper;

import java.util.List;

import com.nh.controller.model.AucEntrData;
import com.nh.controller.model.AuctionRound;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.SendAuctionResult;

/**
 * 출품 데이터 Interface Mapper
 *
 * @author dhKim
 */
public interface EntryInfoMapper {

	int getAllEntryDataCount(AuctionRound auctionRound);
	
    List<EntryInfo> getAllEntryData(AuctionRound auctionRound);
    

    /**
     * 낙찰/보류 목록 조회
     * @param auctionRound
     * @return
     */
    List<EntryInfo> getFinishedEntryData(AuctionRound auctionRound);

    /**
     * 가격 정보 업데이트
     * @param entryInfo
     * @return
     */
    int updateEntryPrice(EntryInfo entryInfo); 
    
    /**
     * 경매 상태 정보 업데이트
     * @param entryInfo
     * @return
     */
    int updateEntryState(EntryInfo entryInfo); 
    
    /**
     * 경매 결과 업데이트
     * @param entryInfo
     * @return
     */
    int updateAuctionResult(SendAuctionResult auctionResult); 
    
    /**
     * 응찰 내역 저장
     * @param aucEntrData
     * @return
     */
    int insertBiddingHistory(AucEntrData aucEntrData); 
    
}
