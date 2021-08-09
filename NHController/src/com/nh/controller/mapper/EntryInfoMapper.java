package com.nh.controller.mapper;

import com.nh.controller.model.AuctionRound;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.SendAuctionResult;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 출품 데이터 Interface Mapper
 *
 * @author dhKim
 */
public interface EntryInfoMapper {

    List<EntryInfo> getAllEntryData(AuctionRound auctionRound);
    

    /**
     * 낙찰/보류 목록 조회
     * @param auctionRound
     * @return
     */
    List<EntryInfo> getFinishedEntryData(AuctionRound auctionRound);
/**
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
     * 경매 결과 저장
     * @param entryInfo
     * @return
     */
    int updateAuctionResult(SendAuctionResult auctionResult); 
    
}
