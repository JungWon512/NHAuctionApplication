package com.nh.controller.mapper;

import com.nh.controller.model.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 가축경매 참여자 로그인 정보 Interface Mapper
 *
 * @author dhKim
 */
public interface ConnectionInfoMapper {

    /**
     * 관리자 로그인
     *
     * @param userId  아이디
     * @param userPwd 비밀번호
     * @return 사용자 이름
     */
    String selectAdminInfo(@Param("userId") String userId,
                           @Param("userPwd") String userPwd);

    /**
     * 가축경매 참여자 번호 조회
     *
     * @param auctionHouseCode 조합구분코드
     * @param auctionDate      경매일자
     * @param entryType        경매대상구분코드(1 : 송아지 / 2 : 비육우 / 3 : 번식우)
     * @param userMemNum       참여자 번호
     * @return 가축경매 참여자 번호
     */
    String selectConnectionInfo(@Param("auctionHouseCode") String auctionHouseCode,
                                @Param("auctionDate") String auctionDate,
                                @Param("entryType") String entryType,
                                @Param("userMemNum") String userMemNum);

    /**
     * 가축경매 참여자 ID 시퀀스 조회
     */
    int selectSequenceId();

    /**
     * 가축경매 참여자 정보 추가
     *
     * @param info 참여자 정보
     */
    void insertConnectionInfo(List<UserInfo> info);
}
