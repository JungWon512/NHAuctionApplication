package com.nh.share.api.request;

import java.util.HashMap;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.NetworkDefine;
import com.nh.share.api.request.body.RequestAuctionResultBody;
import com.nh.share.api.response.BaseResponse;
import com.nh.share.api.response.ResponseAuctionLogin;
import com.nh.share.utils.CommonUtils;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * 경매 결과 업데이트
 *
 */
public class ActionRequestAuctionResult extends Action {
	
	private RequestAuctionResultBody body = null;
	
	public ActionRequestAuctionResult(RequestAuctionResultBody body, String token ,ActionResultListener<BaseResponse> resultListener) {
		this.mAccessToken = token;
		this.body = body;
		this.mResultListenerBase = resultListener;
	}

	public interface RetrofitAPIService {

		@PUT(NetworkDefine.API_REQUEST_AUCTION_RESULT)
		Call<BaseResponse> requestAuctionResult(
				@Path("version") String apiVer,
				@Body HashMap<String, String> paramBody);
	}

	private final Callback<BaseResponse> mCallBack = new Callback<BaseResponse>() {
		@Override
		public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
			actionDone(resultType.ACTION_RESULT_RUNNEXT);
			Headers headers = response.headers();
			String type = headers.get(CONTENT_TYPE);
			BaseResponse body = response.body();

			switch (response.code()) {
			case 200:
				if (body.getSuccess()) {
					mResultListenerBase.onResponseResult(body);
				} else {
					if(CommonUtils.getInstance().isValidString(body.getMessage())) {
						mResultListenerBase.onResponseError(body.getMessage());
					}else {
						actionDone(resultType.ACTION_RESULT_ERROR_SKIP);
					}
					
				}
				break;
			default:
				actionDone(resultType.ACTION_RESULT_ERROR_NOT_RESPONSE);
				break;
			}
		}

		@Override
		public void onFailure(Call<BaseResponse> call, Throwable t) {
			if (t.toString().contains("Exception") || t.toString().contains("JsonSyntaxException") || t.toString().contains("MalformedJsonException") || t.toString().contains("NoRouteToHostException") || t.toString().contains("SocketTimeoutException")) {
				ActionRuler.getInstance().finish();
				actionDone(resultType.ACTION_RESULT_ERROR_NOT_RESPONSE);
			}
		}

	};

	@Override
	void actionDone(String message, resultType type, String errorCode) {
		String errStr = "";
		String errCodeDisp = "";

		if (CommonUtils.getInstance().isValidString(errorCode)) {
			errCodeDisp = "[" + errorCode + "]";
		} else {
			errorCode = type.toString();
		}

		if (CommonUtils.getInstance().isValidString(message)) {
			errStr = message;
		}

		switch (type) {
		case ACTION_RESULT_RUNNEXT:
			ActionRuler.getInstance().runNext();
			break;
		case ACTION_RESULT_ERROR_DISABLE_NETWORK:
			ActionRuler.getInstance().finish();
			mResultListenerBase.onResponseError(errorCode);
			break;
		case ACTION_RESULT_ERROR_NOT_RESPONSE:
			ActionRuler.getInstance().finish();
			mResultListenerBase.onResponseError(errorCode);
			break;
		case ACTION_RESULT_ERROR_RESPONSE:
			ActionRuler.getInstance().finish();
			mResultListenerBase.onResponseError(errorCode);
			break;
		case ACTION_RESULT_ERROR_INTRO:
			ActionRuler.getInstance().runNext();
			mResultListenerBase.onResponseError(errorCode);
			break;
		case ACTION_RESULT_ERROR_SKIP:
			ActionRuler.getInstance().runNext();
			mResultListenerBase.onResponseError(errorCode);
			break;
		}
	}

	@Override
	void actionDone(resultType type, String errorCode) {
		actionDone("", type, errorCode);
	}

	@Override
	void actionDone(resultType type) {
		actionDone("", type, "");
	}

	@Override
	public void run() {
		mRetrofit = new Retrofit.Builder().baseUrl(NetworkDefine.NH_AUCTION_API_HOST).addConverterFactory(GsonConverterFactory.create()).client(getDefaultHttpClient()).build();
		RetrofitAPIService mRetrofitAPIService = mRetrofit.create(RetrofitAPIService.class);
		mRetrofitAPIService.requestAuctionResult(NetworkDefine.API_VERSION,body).enqueue(mCallBack);
	}
}
