package com.nh.share.api.request.body;

import java.util.HashMap;

/**
 * 경매 결과 업데이트
 * 
 * @author jhlee
 * @see {ActionRequestAuctionResult}
 */
public class RequestAuctionResultBody extends HashMap<String, String> {

	public RequestAuctionResultBody(String naBzPlc, String aucObjDsc, String aucDt, String oslpNo, String ledSqno
			,String trmnAmnno, String lvstAucPtcMnNo, String sraSbidAm, String sraSbidUpr, String selStsDsc,
			String lschgDtm,String lsCmeno,String lowsSbidLmtAm) {

		this.put("naBzPlc", naBzPlc); // 조합코드
		this.put("aucObjDsc", aucObjDsc); // 경매대상 구분코드
		this.put("aucDt", aucDt); // 경매일자
		this.put("oslpNo", oslpNo); // 원표번호
		this.put("ledSqno", ledSqno); // 원장 일련번호
		this.put("trmnAmnno", trmnAmnno); // 중도매인 번호
		this.put("lvstAucPtcMnNo", lvstAucPtcMnNo); // 경매 참가자 번호
		this.put("sraSbidAm", sraSbidAm); // 낙찰 금액
		this.put("sraSbidUpr", sraSbidUpr); // 낙찰 단가
		this.put("selStsDsc", selStsDsc); // 판매상태 구분 코드
		this.put("lschgDtm", lschgDtm); // 최종 변경일시
		this.put("lsCmeno", lsCmeno); // 최종 변경자
		this.put("lowsSbidLmtAm", lowsSbidLmtAm); // 최저낙찰한도금액
	}
}
