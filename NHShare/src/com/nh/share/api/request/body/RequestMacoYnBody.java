package com.nh.share.api.request.body;

/**
 * 사용자 조합원/비조합원 여부 Body
 * 
 * @author jhlee
 * @see {ActionRequestSelectMacoYn}
 *
 */
public class RequestMacoYnBody extends RequestBaseBody {

	public RequestMacoYnBody(String auctionHouseCode, String entryType, String auctionDate, String trmnAmnno) {
		super(auctionHouseCode, entryType, auctionDate);
		this.put("trmnAmnno", trmnAmnno);
	}
}
