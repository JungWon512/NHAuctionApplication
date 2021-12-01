package com.nh.share.api;

/**
 * API 정보 클래스.
 * @author jhlee
 *
 */
public class NetworkDefine {

	private static NetworkDefine globalDefine = null;

	public static synchronized NetworkDefine getInstance() {

		if (globalDefine == null) {
			globalDefine = new NetworkDefine();
		}

		return globalDefine;
	}
	
	public static String ADMIN_ACCESS_TOKEN = "";
	
//	public static final String NH_AUCTION_API_HOST = "https://난장.kr"; // 개발 API의 호스트
//	public static final String NH_AUCTION_API_HOST = "http://nhlva.nonghyup.com"; // 개발 API의 호스트
//	public static final String NH_AUCTION_API_HOST = "http://192.168.0.92:8080"; // 개발 API의 호스트
//	public static final String NH_AUCTION_API_HOST = "http://1.201.161.58:8080"; // 운영 API의 호스트
	public static final String NH_AUCTION_API_HOST = "https://www.가축시장.kr"; // 개발 API의 호스트

	public static final String API_VERSION =  "v2"; // API Version
	
	public static final String API_REQUEST_AUCTION_LOGIN =  "/api/{version}/auth/login"; // 경매 로그인
	
	public static final String API_REQUEST_AUCTION_RESULT =  "/api/{version}/auction/result"; // 경매 경매 결과 전송
	
	public static final String API_REQUEST_AUCTION_BID_NUM =  "/api/{version}/auction/select/bidnum"; // 사용자 정보 검색
	 
	public static final String API_REQUEST_AUCTION_QCN =  "/api/{version}/auction/select/qcn"; // 회차정보 검색
	
	public static final String API_REQUEST_AUCTION_COW_CNT =  "/api/{version}/auction/select/cowcnt"; // 출장우 데이터 카운트
	
	public static final String API_REQUEST_AUCTION_COW_INFO =  "/api/{version}/auction/select/cowinfo"; // 출장우 데이터 조회
	
	public static final String API_REQUEST_AUCTION_BID_LOG_CNT =  "/api/{version}/auction/select/bidlogcnt"; // 응찰 내역 카운트
	
	public static final String API_REQUEST_AUCTION_NEXT_BID =  "/api/{version}/auction/select/nextbid"; // 다음응찰번호조회
	
	public static final String API_REQUEST_AUCTION_FEE =  "/api/{version}/auction/select/fee"; // 수수료 기준 정보 조회
		
	public static final String API_REQUEST_AUCTION_MACOYN =  "/api/{version}/auction/select/macoYn"; // 조합원/비조합원 유무 조회
	
	public static final String API_REQUEST_AUCTION_DELETE_FEE =  "/api/{version}/auction/delete/fee"; // 수수료 내역 삭제
	
	public static final String API_REQUEST_AUCTION_INSERT_FEE =  "/api/{version}/auction/insert/fee"; // 수수료 내역 저장
	
	public static final String API_REQUEST_AUCTION_BID_LOG =  "/api/{version}/auction/insert/bidlog"; // 응찰 로그 저장
	
	public static final String API_REQUEST_AUCTION_UPDATE_BID_AMT =  "/api/{version}/auction/update/lowsbidamt"; // 최저가 변경
	
	public static final String API_REQUEST_AUCTION_UPDATE_COW_ST =  "/api/{version}/auction/update/cowst"; // 경매 상태 변경(보류)
	
	public static final String API_REQUEST_AUCTION_UPDATE_COW_RESULT =  "/api/{version}/auction/update/cowresult"; // 경매 결과 저장
	
	public static final String API_REQUEST_MULTIPLE_AUCTION_STATUS =  "/api/{version}/auction/status"; // 일괄경매 시작
	 
	public static final String API_REQUEST_AUCTION_BID_ENTRY =  "/api/{version}/auction/select/bidentry"; // 응찰 내역 조회
	
}
