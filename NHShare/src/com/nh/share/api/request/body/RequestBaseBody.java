package com.nh.share.api.request.body;

import java.util.HashMap;

/**
 * Base Body
 * @author jhlee
 */
public class RequestBaseBody extends HashMap<String, Object> {
	
	public RequestBaseBody(String naBzplc , String aucObjDsc, String aucDt) {
		this.put("naBzplc", naBzplc);		// 조합구분코드
		this.put("aucObjDsc", aucObjDsc);	// 경매대상구분코드(1 : 송아지 / 2 : 비육우 / 3 : 번식우)
		this.put("aucDt", aucDt);			// 경매일
	}

}
