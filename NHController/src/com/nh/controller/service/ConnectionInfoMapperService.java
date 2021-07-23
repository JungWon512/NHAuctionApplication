package com.nh.controller.service;

import com.nh.controller.dao.ConnectionInfoDao;
import com.nh.controller.database.DBSessionFactory;
import com.nh.controller.mapper.ConnectionInfoMapper;
import com.nh.controller.model.UserInfo;
import com.nh.controller.utils.CommonUtils;
import com.nh.share.common.models.ConnectionInfo;
import org.apache.ibatis.session.SqlSession;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 가축경매 참여자 번호 MapperService
 *
 * @author dhKim
 */
public class ConnectionInfoMapperService extends BaseMapperService<ConnectionInfoDao> implements ConnectionInfoMapper {

    private String userNum = "-1";

    public ConnectionInfoMapperService() {
        this.setDao(new ConnectionInfoDao());
    }

    public String getUserNum() {
        return userNum;
    }

    @Override
    public String selectConnectionInfo(String auctionHouseCode, String auctionDate, String entryType, String userMemNum) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("auctionHouseCode", auctionHouseCode);
        map.put("auctionDate", auctionDate);
        map.put("entryType", entryType);
        map.put("userMemNum", userMemNum);

        try (SqlSession session = DBSessionFactory.getSession()) {
            userNum = dao.selectUserInfo(map, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userNum;
    }

    @Override
    public int selectSequenceId() {
        try (SqlSession session = DBSessionFactory.getSession()) {
            return dao.selectIdSequence(session);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void insertConnectionInfo(List<UserInfo> infoList) {
        SqlSession session = DBSessionFactory.getSession();
        try {
            dao.insertUserInfo(infoList, session);
            session.commit();
        } catch (Exception e) {
            session.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * insert 하기 전에 ConnectionInfo -> UserInfoList 로 변환하는 메서드
     * 경매대상구분코드(1,2,3) 모두 insert
     *
     * @param info ConnectionInfo
     * @return ArrayList
     * @author dhKim
     */
    public ArrayList<UserInfo> convertConnectionInfo(ConnectionInfo info) {
        ArrayList<UserInfo> userList = new ArrayList<>();
        userNum = String.valueOf(selectSequenceId() + 1);
        Timestamp timeStamp = Timestamp.valueOf(LocalDateTime.now());

        for (int i = 1; i < 4; i++) {
            userList.add(new UserInfo(info.getAuctionHouseCode(),
                    CommonUtils.getInstance().getCurrentTime("yyyyMMdd"),
                    String.valueOf(i),
                    userNum,
                    Integer.parseInt(info.getUserMemNum()),
                    timeStamp,
                    "141507078"
            ));
        }
        return userList;
    }
}
