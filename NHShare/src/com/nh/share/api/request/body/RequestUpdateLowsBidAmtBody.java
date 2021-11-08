package com.nh.share.api.request.body;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.SendAuctionResult;

/**
 * 출장우 데이터 Body
 * 
 * @author jhlee
 * @see {ActionRequestSelectCowCnt}
 * @see {ActionRequestSelectCowInfo}
 * @see {ActionRequestUpdateCowSt}
 * @see {ActionRequestUpdateLowsBidAmt}
 * @see {ActionRequestUpdateCowResult}
 *
 */
public class RequestUpdateLowsBidAmtBody extends HashMap<String, String> {
	
	/**
	 * 최저가 변경
	 * 
	 * @param naBzplc
	 * @param aucObjDsc
	 * @param aucDt
	 * @param oslpNo
	 * @param ledSqno
	 * @param lwprChgNt
	 * @param lowPrice
	 * @param lsCmeNo
	 * @param token
	 */
	public RequestUpdateLowsBidAmtBody(String entryInfoList) {
		this.put("list", entryInfoList);    			  
	}
	
	
	
}
