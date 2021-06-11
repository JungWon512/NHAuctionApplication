package com.nh.share.api.response;

import java.util.HashMap;
import java.util.List;

import com.nh.share.api.model.AuctionInterestEntryFavoriteUserInfo;
import com.nh.share.api.model.AuctionInterestEntryInfoResult;

/**
 * 
 * 관심 차량 정보 조회 요청 응답 객체
 * 
 * @see {ActionRequestAuctionInterestEntryInfo}
 *
 */
public class ResponseAuctionInterestEntryInfo extends BaseResponse {
    private List<AuctionInterestEntryInfoResult> result; // 응답 결과

    public List<AuctionInterestEntryInfoResult> getResult() {
        return result;
    }

    public HashMap<String, List<AuctionInterestEntryFavoriteUserInfo>> getFavoriteCarInfoMap(List<AuctionInterestEntryInfoResult> listData) {
        HashMap<String, List<AuctionInterestEntryFavoriteUserInfo>> resultMap = new HashMap<String, List<AuctionInterestEntryFavoriteUserInfo>>();

        if (listData != null) {
            if (listData.size() > 0) {
                for (int i = 0; i < listData.size(); i++) {
                    if (listData.get(i).getFavoriteUserInfo() != null) {
                        if (listData.get(i).getFavoriteUserInfo().size() > 0) {
                            resultMap.put(listData.get(i).getAuctionEntryNum(), listData.get(i).getFavoriteUserInfo());
                        }
                    }
                }
            }
        }

        return resultMap;
    }

    public void setResult(List<AuctionInterestEntryInfoResult> result) {
        this.result = result;
    }
}
