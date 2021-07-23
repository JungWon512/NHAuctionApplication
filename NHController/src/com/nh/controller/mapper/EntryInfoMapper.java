package com.nh.controller.mapper;

import com.nh.share.controller.models.EntryInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 출품 데이터 Interface Mapper
 *
 * @author dhKim
 */
public interface EntryInfoMapper {

    List<EntryInfo> getAllEntryData(@Param("auctionDate") String date,
                                    @Param("auctionHouseCode") String auctionHouseCode,
                                    @Param("entryType") String entryType); // TODO: 쿼리 및 param 수정
}
