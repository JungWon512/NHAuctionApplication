package com.nh.share.api.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nh.share.api.model.AbsenteeAuctionBidInfoResult;

/**
 * 
 * 부재자 입찰 정보 조회 요청 응답 객체
 * 
 * @see {ActionRequestAbsenteeAuctionBidInfo}
 *
 */
public class ResponseAbsenteeAuctionBidInfo extends BaseResponse {
    private List<AbsenteeAuctionBidInfoResult> result; // 응답 결과

    public List<AbsenteeAuctionBidInfoResult> getResult() {
        return result;
    }

    public HashMap<String, ArrayList<AbsenteeAuctionBidInfoResult>> getAbsenteeMap(
            List<AbsenteeAuctionBidInfoResult> listData) {
        HashMap<String, ArrayList<AbsenteeAuctionBidInfoResult>> resultMap = new HashMap<String, ArrayList<AbsenteeAuctionBidInfoResult>>();

        if (listData != null) {
            if (listData.size() > 0) {
                for (int i = 0; i < listData.size(); i++) {
                    if (resultMap.containsKey(listData.get(i).getAuctionEntryNum())) {
                        ArrayList<AbsenteeAuctionBidInfoResult> tempAbsenteeList = new ArrayList<AbsenteeAuctionBidInfoResult>();
                        tempAbsenteeList = resultMap.get(listData.get(i).getAuctionEntryNum());
                        tempAbsenteeList.add(listData.get(i));

                        resultMap.put(listData.get(i).getAuctionEntryNum(), tempAbsenteeList);
                    } else {
                        ArrayList<AbsenteeAuctionBidInfoResult> tempAbsenteeList = new ArrayList<AbsenteeAuctionBidInfoResult>();
                        tempAbsenteeList.add(listData.get(i));

                        resultMap.put(listData.get(i).getAuctionEntryNum(), tempAbsenteeList);
                    }
                }
            }
        }

        return resultMap;
    }

    public void setResult(List<AbsenteeAuctionBidInfoResult> result) {
        this.result = result;
    }

}
