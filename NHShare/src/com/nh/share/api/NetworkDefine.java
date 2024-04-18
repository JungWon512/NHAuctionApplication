package com.nh.share.api;

import com.nh.share.code.GlobalDefineCode;

/**
 * API 정보 클래스.
 * 
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
//	115.41.222.25
//	1.201.161.58
	public static final String LOCAL_NH_AUCTION_API_HOST = "http://192.168.1.53:8080";
	public static final String DEV_NH_AUCTION_API_HOST = "https://xn--e20bw05b.kr";
	public static final String PRD_NH_AUCTION_API_HOST = "https://www.xn--o39an74b9ldx9g.kr";

	//경매 진행 프로그램 다운로드 경로
	public static final String APPLICATION_DOWNLOAD_URL = DEV_NH_AUCTION_API_HOST + "/static/file/NHController.exe";
	
	public static final String API_VERSION = "v2"; // API Version

	public static final String API_REQUEST_APPLICATION_VERSION= "/api/appversion"; // 경매 진행 프로그램 버전 체크
	
	public static final String PARAM_OS_TYPE = "EXE"; // 경매 진행 프로그램 버전 체크 파라미터값
	
	public static final String API_REQUEST_AUCTION_LOGIN = "/api/{version}/auth/login"; // 경매 로그인

	public static final String API_REQUEST_AUCTION_RESULT = "/api/{version}/auction/result"; // 경매 경매 결과 전송

	public static final String API_REQUEST_AUCTION_BID_NUM = "/api/{version}/auction/select/bidnum"; // 사용자 정보 검색

	public static final String API_REQUEST_AUCTION_QCN = "/api/{version}/auction/select/qcn"; // 회차정보 검색

	public static final String API_REQUEST_AUCTION_COW_CNT = "/api/{version}/auction/select/cowcnt"; // 출장우 데이터 카운트

	public static final String API_REQUEST_AUCTION_COW_INFO = "/api/{version}/auction/select/cowinfo"; // 출장우 데이터 조회

	public static final String API_REQUEST_AUCTION_BID_LOG = "/api/{version}/auction/insert/bidlog"; // 응찰 로그 저장

	public static final String API_REQUEST_AUCTION_UPDATE_BID_AMT = "/api/{version}/auction/update/lowsbidamt"; // 최저가 변경

	public static final String API_REQUEST_AUCTION_UPDATE_COW_ST = "/api/{version}/auction/update/cowst"; // 경매 상태  변경(보류)

	public static final String API_REQUEST_MULTIPLE_AUCTION_STATUS = "/api/{version}/auction/status"; // 일괄경매 시작

	public static final String API_REQUEST_AUCTION_BID_ENTRY = "/api/{version}/auction/select/bidentry"; // 응찰 내역 조회
	public static final String API_REQUEST_GET_BZLOC = "/api/{version}/auction/select/bzloc"; // 응찰 내역 조회;

	/**
	 * 운영 or 개발 도메인
	 * 
	 * @return
	 */
	public String getBaseDomain() {

		if (GlobalDefineCode.FLAG_PRD) {
			return PRD_NH_AUCTION_API_HOST;
		} else {
			//return DEV_NH_AUCTION_API_HOST;
			return LOCAL_NH_AUCTION_API_HOST;
		}
	}
}
