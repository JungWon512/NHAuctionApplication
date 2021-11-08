package com.nh.share.api.request.body;

/**
 * 수수료 기준 정보 조회 ,수수료 저장 Body
 * 
 * @author jhlee
 * @see {ActionRequestSelectFee}
 * @see {ActionRequestInsertFee}
 * @see {ActionRequestDeleteFee}
 *
 */
public class RequestFeeBody extends RequestBaseBody {
	
	
	public RequestFeeBody(String naBzplc, String aucObjDsc, String aucDt) {
		super(naBzplc, aucObjDsc, aucDt);
		this.put("aplDt", aucDt); 		// 수수료적용일자
	}
			

	public RequestFeeBody(String naBzplc, String aucObjDsc, String aucDt, String oslpNo, String ledSqNo,
			String feeRgSqno, String aplDt, String naFee_c, String feeAplObj_c, String ansDsc, String sBidYn,
			String sraTrFee, String tmsYn) {

		super(naBzplc, aucObjDsc, aucDt);

		this.put("oslpNo", oslpNo); 		// 원표번호
		this.put("ledSqNo", ledSqNo); 	// 원장일련번호
		this.put("feeRgSqno", feeRgSqno); 	// 경제통합수수료코드
		this.put("aplDt", aplDt); 		// 수수료적용일자
		this.put("naFee_c", naFee_c); 	//
		this.put("feeAplObj_c", feeAplObj_c); // 수수료적용대상코드
		this.put("ansDsc", ansDsc); 		// 가감구분코드
		this.put("sBidYn", sBidYn); 		// 축산수수료유형코드
		this.put("sraTrFee", sraTrFee); 	// 축산거래수수료 (기본값 : 0)
		this.put("tmsYn", tmsYn); 		// 전송여부 (기본값 : 0)
	}

}
