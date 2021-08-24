package com.nh.share.api;

public class NetworkDefine {

	private static NetworkDefine globalDefine = null;

	public static synchronized NetworkDefine getInstance() {

		if (globalDefine == null) {
			globalDefine = new NetworkDefine();
		}

		return globalDefine;
	}
	
	public static final String NH_AUCTION_API_HOST = "http://115.41.222.25:8080"; // 운영 API의 호스트

	public static final String API_VERSION =  "v1"; // API Version
	
	public static final String API_REQUEST_AUCTION_LOGIN =  "/api/{version}/auth/{naBzplc}/login"; // 경매 사용자 인증 처리

}
