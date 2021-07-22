package com.nh.controller.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 회원 로그인 정보 Interface Mapper
 *
 * @author dhKim
 */
public interface ConnectionInfoMapper {

    /**
     * 가축경매 참여자 번호 조회
     *
     * @param auctionHouseCode 조합구분코드
     * @param auctionDate      경매일자
     * @param entryType        경매대상구분코드(1 : 송아지 / 2 : 비육우 / 3 : 번식우)
     * @param userMemNum       참여자 번호
     * @return 가축경매 참여자 번호
     */
    String selectUserInfo(@Param("auctionHouseCode") String auctionHouseCode,
                          @Param("auctionDate") String auctionDate,
                          @Param("entryType") String entryType,
                          @Param("userMemNum") String userMemNum);


    /**
     * 가축경매 참여자 정보 추가
     */
    void insertUserInfo();

}
