package com.nh.share.api.request.body;

/**
 * 일괄경매 시작=start,중지=pause,종료=finish Body
 * 
 * @author jhlee
 * @see {ActionRequestMultipleAuctionStatus}
 *
 */
public class RequestMultipleAuctionStatusBody extends RequestBaseBody {

	public RequestMultipleAuctionStatusBody(String auctionHouseCode, String entryType, String auctionDate, String status,String rgSqNo) {
		super(auctionHouseCode, entryType, auctionDate);
		this.put("naBzPlc", auctionHouseCode);		// 조합구분코드
		this.put("status", status);
		this.put("rgSqno", rgSqNo);
	}
}
