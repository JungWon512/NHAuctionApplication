package com.nh.share.api.request.body;

/**
 * 사용자 정보 검색 Body
 * 
 * @author jhlee
 * @see {ActionRequestSelectBidNum}
 *
 */
public class RequestBidNumBody extends RequestBaseBody {

	public RequestBidNumBody(String auctionHouseCode, String entryType, String auctionDate, String userMemNum) {
		super(auctionHouseCode, entryType, auctionDate);
		this.put("auctionHouseCode", auctionHouseCode);
		this.put("entryType", entryType);
		this.put("auctionDate", auctionDate);
		this.put("userMemNum", userMemNum);
	}
}
