package com.nh.share.api.request.body;

/**
 * 응찰 내역 조회 Body
 * 
 * @author jhlee
 * @see {ActionRequestSelectBidEntey}
 *
 */
public class RequestBidEntryBody extends RequestBaseBody {

	public RequestBidEntryBody(String auctionHouseCode, String entryType, String auctionDate, String oslpNo) {
		super(auctionHouseCode, entryType, auctionDate);
		this.put("oslpNo", oslpNo);
	}
}
