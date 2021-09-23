package com.nh.auctionserver.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseMap {
	final public static int REST_API_STATUS_SUCCESS = 1;
	final public static int REST_API_STATUS_FAIL = 0;
	final public static int REST_API_STATUS_ERROR = -1;
	final public static int REST_API_STATUS_EXPIRE = -2;

	final public String REST_API_STATUS_SUCCESS_MSG = "Success";
	final public String REST_API_STATUS_FAIL_MSG = "Fail";
	final public String REST_API_STATUS_ERROR_MSG = "Error";
	final public String REST_API_STATUS_EXPRIED_MSG = "Auth Token Expried";

	final public static String RESET_API_MESSAGE_SUCCESS = "요청이 정상적으로 처리되었습니다.";
	final public static String RESET_API_MESSAGE_FAIL = "요청 처리가 실패하였습니다.";
	final public static String RESET_API_MESSAGE_ERROR = "요청 처리 중 에러가 발생되었습니다.";
	final public static String RESET_API_MESSAGE_RQUIRE_FAIL = "필수 인자가 잘못 전달되었습니다.";
	final public static String RESET_API_MESSAGE_NO_DATA = "요청에 대한 응답 데이터가 존재하지 않습니다.";
	final public static String RESET_API_MESSAGE_AUTH_EXPIRE = "인증 토큰이 만료 혹은 위변조 되었습니다.";

	private int mStatus = REST_API_STATUS_ERROR;
	private String mMessage = null;
	private Map<String, Object> mResultMap = null;

	public ApiResponseMap(int status, String message, Map<String, Object> resultMap) {
		mStatus = status;

		if (message == null) {
			mMessage = "";
		} else {
			mMessage = message;
		}

		if (resultMap == null) {
			mResultMap = new HashMap<>();
		} else {
			mResultMap = resultMap;
		}
	}

	public ResponseEntity<Map<String, Object>> getResponseEntity() {
		if (mStatus == REST_API_STATUS_SUCCESS) {
			mResultMap.put("status", REST_API_STATUS_SUCCESS_MSG);

			if (mMessage.isEmpty() || mMessage == null) {
				mResultMap.put("message", RESET_API_MESSAGE_SUCCESS);
			} else {
				mResultMap.put("message", mMessage);
			}
		} else if (mStatus == REST_API_STATUS_FAIL) {
			mResultMap.put("status", REST_API_STATUS_FAIL_MSG);

			if (mMessage.isEmpty() || mMessage == null) {
				mResultMap.put("message", RESET_API_MESSAGE_FAIL);
			} else {
				mResultMap.put("message", mMessage);
			}
		} else if (mStatus == REST_API_STATUS_EXPIRE) {
			mResultMap.put("status", REST_API_STATUS_EXPRIED_MSG);

			if (mMessage.isEmpty() || mMessage == null) {
				mResultMap.put("message", RESET_API_MESSAGE_AUTH_EXPIRE);
			} else {
				mResultMap.put("message", mMessage);
			}
		} else {
			mResultMap.put("status", REST_API_STATUS_ERROR_MSG);

			if (mMessage.isEmpty() || mMessage == null) {
				mResultMap.put("message", RESET_API_MESSAGE_ERROR);
			} else {
				mResultMap.put("message", mMessage);
			}
		}

		return ResponseEntity.status(HttpStatus.OK).body(mResultMap);
	}
}
