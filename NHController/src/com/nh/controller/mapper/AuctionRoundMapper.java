package com.nh.controller.mapper;

import com.nh.controller.model.AuctionRound;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 경매 회차 정보 Interface Mapper
 *
 * @author dhKim
 */
public interface AuctionRoundMapper {

    List<AuctionRound> getAllAuctionRoundData(@Param("date") String date);
    
    AuctionRound obtainAuctionRoundData(AuctionRound auctionRound);
}
