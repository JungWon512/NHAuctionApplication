package com.nh.share.api.request.body;

/**
 * 회차 정보 검색 Body
 * @author jhlee
 * @see {ActionRequestSelectQcn}
 *
 */
public class RequestQcnBody extends RequestBaseBody {

	public RequestQcnBody(String naBzplc, String aucObjDsc, String aucDt,String token) {
		super(naBzplc, aucObjDsc, aucDt);
	}
}
