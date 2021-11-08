package com.nh.share.api.request.body;

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
public class RequestCowInfoBody extends RequestBaseBody {
	
	/**
	 * 
	 * @param naBzplc   조합코드
	 * @param aucObjDsc 구분코드
	 * @param aucDt     경매일
	 * @param token     관리자 토큰
	 * @param selStsDsc 경매 상태
	 * @param stnYn     단일or일반 구분
	 */
	public RequestCowInfoBody(String naBzplc, String aucObjDsc, String aucDt, String selStsDsc, String stnYn) {
		super(naBzplc, aucObjDsc, aucDt);
		this.put("mAuctionResult", selStsDsc); // 경매대상구분코드(1 : 송아지 / 2 : 비육우 / 3 : 번식우)
		this.put("stnYn", stnYn); // 단일or일반 구분
	}

	/**
	 * 경매 상태 변경
	 */
	public RequestCowInfoBody(EntryInfo entryInfo) {
		super(entryInfo.getAuctionHouseCode(), entryInfo.getEntryType(), entryInfo.getAucDt());
		this.put("mAuctionHouseCode", entryInfo.getAuctionHouseCode());    			   //경제통합사업장코드
		this.put("mEntryType", entryInfo.getEntryType());          				//경매대상구분코드
		this.put("mAucDt", entryInfo.getAucDt());             	 				//경매일자
		this.put("mEntryNum", entryInfo.getEntryNum());     					//경매 순번
		this.put("mAuctionResult", entryInfo.getAuctionResult());     			//상태
		this.put("mLsCmeNo", entryInfo.getLsCmeNo());            				//수정자
	}
	
	/**
	 * 경매 결과 저장
	 * @param resultInfo
	 * @param token
	 */
	public RequestCowInfoBody(SendAuctionResult resultInfo) {
		super(resultInfo.getAuctionHouseCode(), resultInfo.getEntryType(), resultInfo.getAucDt());
		this.put("mAuctionHouseCode",resultInfo.getAuctionHouseCode());
		this.put("mEntryType",resultInfo.getEntryType());
		this.put("mAucDt",resultInfo.getAucDt());
		this.put("mOslpNo",resultInfo.getOslpNo());
		this.put("mLedSqno",resultInfo.getLedSqno());
		this.put("mSuccessBidPrice",resultInfo.getSuccessBidPrice());
		this.put("mSuccessBidUpr",resultInfo.getSuccessBidUpr());
		this.put("mSuccessAuctionJoinNum",resultInfo.getSuccessAuctionJoinNum());
		this.put("mSuccessBidder",resultInfo.getSuccessBidder());
		this.put("mResultCode",resultInfo.getResultCode());
		this.put("mLsCmeNo",resultInfo.getLsCmeNo());
	}
	
}
