package com.nh.share.api.response;

import java.util.List;

import com.nh.share.api.models.QcnData;
import com.nh.share.api.models.StnData;

/**
 * 회차 정보 조회 응답 객체
 * 
 * @see {ActionRequestSelectQcn}
 *
 */
public class ResponseQcn extends BaseResponse {

	private QcnData data;
	
	private List<StnData> stnList; //일괄경매 정보
	
	public QcnData getData() {
		return data;
	}

	public void setData(QcnData data) {
		this.data = data;
	}

	public List<StnData> getStnList() {
		return stnList;
	}

	public void setStnList(List<StnData> stnList) {
		this.stnList = stnList;
	}
	
}
