package com.nh.share.api.request;

import java.util.HashMap;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.NetworkDefine;
import com.nh.share.api.request.body.RequestCowInfoBody;
import com.nh.share.api.response.ResponseNumber;
import com.nh.share.utils.CommonUtils;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * 출장우 데이터 수
 * @author jhlee
 */
public class ActionRequestSelectCowCnt extends Action {
	
	private RequestCowInfoBody mBody = null;
	
	public ActionRequestSelectCowCnt(RequestCowInfoBody body, ActionResultListener<ResponseNumber> resultListener) {
		this.mBody = body;
		this.mResultListenerBase = resultListener;
	}

	public interface RetrofitAPIService {

		@FormUrlEncoded
		@POST(NetworkDefine.API_REQUEST_AUCTION_COW_CNT)
		Call<ResponseNumber> requestSelecCowCnt(
				@Path("version") String apiVer,
				@FieldMap HashMap<String, Object> paramBody);
	}

	private final Callback<ResponseNumber> mCallBack = new Callback<ResponseNumber>() {
		@Override
		public void onResponse(Call<ResponseNumber> call, Response<ResponseNumber> response) {
			actionDone(resultType.ACTION_RESULT_RUNNEXT);
			Headers headers = response.headers();
			String type = headers.get(CONTENT_TYPE);
			ResponseNumber body = response.body();

			switch (response.code()) {
			case 200:
				mResultListenerBase.onResponseResult(body);
				break;
			default:
				actionDone(resultType.ACTION_RESULT_ERROR_NOT_RESPONSE);
				break;
			}
		}

		@Override
		public void onFailure(Call<ResponseNumber> call, Throwable t) {
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
			mResultListenerBase.onResponseError(errStr);
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
		String errMsg = "네트워크 상태가 원활하지 않습니다.\n잠시 후 다시 시도해주세요.";
		actionDone(errMsg, type, "");
	}

	@Override
	public void run() {
		mRetrofit = new Retrofit.Builder().baseUrl(NetworkDefine.NH_AUCTION_API_HOST).addConverterFactory(GsonConverterFactory.create()).client(getDefaultHttpClient()).build();
		RetrofitAPIService mRetrofitAPIService = mRetrofit.create(RetrofitAPIService.class);
		mRetrofitAPIService.requestSelecCowCnt(NetworkDefine.API_VERSION,mBody).enqueue(mCallBack);
	}
}
