package com.nh.controller.utils;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.request.ActionRequestAuctionLogin;
import com.nh.share.api.request.ActionRequestAuctionResult;
import com.nh.share.api.request.body.RequestAuctionResultBody;
import com.nh.share.api.request.body.RequestLoginBody;
import com.nh.share.api.response.BaseResponse;
import com.nh.share.api.response.ResponseAuctionLogin;

/**
 * API Class
 * 
 * @author jhlee
 *
 */
public class ApiUtils {

	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static ApiUtils instance = null;

	public ApiUtils() {
	}

	public static ApiUtils getInstance() {
		if (instance == null) {
			instance = new ApiUtils();
		}
		return instance;
	}

	/**
	 * 로그인
	 * @param body
	 * @param listener_
	 */
	public void requestLogin(RequestLoginBody body, ActionResultListener<ResponseAuctionLogin> listener_) {
		ActionRuler.getInstance().addAction(new ActionRequestAuctionLogin(body, listener_));
		ActionRuler.getInstance().runNext();
	}
	
	/**
	 * 경매 결과 업데이트
	 * @param body
	 * @param listener_
	 */
	public void requestAuctionResult(RequestAuctionResultBody body, ActionResultListener<BaseResponse> listener_) {

		if(body.size() > 0) {
			System.out.println("[API 낙유찰 결과 전송 : " + body.toString());
		}
	
		ActionRuler.getInstance().addAction(new ActionRequestAuctionResult(body, listener_));
		ActionRuler.getInstance().runNext();
	}

}
