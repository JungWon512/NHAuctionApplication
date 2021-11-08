package com.nh.share.api.request.body;

/**
 * 응찰 내역,저장 Body
 * 
 * @author jhlee
 * @see {ActionRequestSelecBidLogCnt}
 * @see {ActionRequestSelecNextBid}
 * @see {ActionRequestInsertBidLog}
 *
 */
public class RequestBidLogBody extends RequestBaseBody {
	
	public RequestBidLogBody(String naBzplc, String aucObjDsc, String aucDt, String oslpNo) {
		super(naBzplc, aucObjDsc, aucDt);
		this.put("oslpNo", oslpNo);			// 원표번호
	}
	
	public RequestBidLogBody(String naBzplc, String aucObjDsc, String aucDt, String oslpNo ,String rgSqno) {
		super(naBzplc, aucObjDsc, aucDt);
		this.put("oslpNo", oslpNo);			// 원표번호
		this.put("rgSqno", rgSqno);			// 등록일련번호 ( 0 : 시작 , 99999999 : 종료 )
	}
	
	public RequestBidLogBody(String naBzplc, String aucObjDsc, String aucDt, String oslpNo,
			String rgSqno, String trmnAmnno, String lvstAucPtcMnNo, String atdrAm, String rmkCntn, String atdrDtm,
			String aucPrgSq) {
		
		super(naBzplc, aucObjDsc, aucDt);
		this.put("oslpNo", oslpNo);					// 원표번호
		this.put("rgSqno", rgSqno);					// 등록일련번호 ( 0 : 시작 , 99999999 : 종료 )
		this.put("trmnAmnno", trmnAmnno);			// 거래인관리번호
		this.put("lvstAucPtcMnNo", lvstAucPtcMnNo);	// 가축경매참여자번호
		this.put("atdrAm", atdrAm);					// 응찰금액
		this.put("rmkCntn", rmkCntn);				// 비고내용
		this.put("atdrDtm", atdrDtm);				// 응찰일시
		this.put("aucPrgSq", aucPrgSq);				// 경매진행순번
	}

}
