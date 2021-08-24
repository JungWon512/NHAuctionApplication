package com.nh.controller.utils;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.request.ActionRequestAuctionLogin;
import com.nh.share.api.request.body.RequestLoginBody;

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
	public void requestLogin(String naBzplc,RequestLoginBody body, ActionResultListener listener_) {
		ActionRuler.getInstance().addAction(new ActionRequestAuctionLogin(naBzplc,body, listener_));
		ActionRuler.getInstance().runNext();
	}

}
