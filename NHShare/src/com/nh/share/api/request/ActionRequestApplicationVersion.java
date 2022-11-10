package com.nh.share.api.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.NetworkDefine;
import com.nh.share.api.response.ResponseVersion;
import com.nh.share.utils.CommonUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 경매 진행 프로그램 버전 체크
 */
public class ActionRequestApplicationVersion extends Action {
	private static final Logger mLogger = LoggerFactory.getLogger(ActionRequestApplicationVersion.class);
	
	/**
	 * @param osType
	 * @param resultListener
	 */
	public ActionRequestApplicationVersion(ActionResultListener<ResponseVersion> resultListener) {
		this.mResultListenerBase = resultListener;
	}

	public interface RetrofitAPIService {
		@GET(NetworkDefine.API_REQUEST_APPLICATION_VERSION)
		Call<ResponseVersion> requestApplicationVersion(@Query("osType") String osType);
	}

	private final Callback<ResponseVersion> mCallBack = new Callback<ResponseVersion>() {
		@Override
		public void onResponse(Call<ResponseVersion> call, Response<ResponseVersion> response) {

			ResponseVersion body = response.body();

			switch (response.code()) {
			case 200:
				if (body.getSuccess()) {
					mResultListenerBase.onResponseResult(body);
				} else {
					if(CommonUtils.getInstance().isValidString(body.getMessage())) {
						mResultListenerBase.onResponseError(body.getMessage());
					}else {
						actionDone(resultType.ACTION_RESULT_ERROR_NOT_RESPONSE);
					}
				}
				break;
			default:
				actionDone(resultType.ACTION_RESULT_ERROR_NOT_RESPONSE);
				break;
			}
		}

		@Override
		public void onFailure(Call<ResponseVersion> call, Throwable t) {
			if (t.toString().contains("Exception") || t.toString().contains("JsonSyntaxException") || t.toString().contains("MalformedJsonException") || t.toString().contains("NoRouteToHostException") || t.toString().contains("SocketTimeoutException")) {
				ActionRuler.getInstance().finish();
				mResultListenerBase.onResponseError(t.toString());
			}
		}
	};

	@Override
	void actionDone(String message, resultType type, String errorCode) {
		ActionRuler.getInstance().finish();
		mResultListenerBase.onResponseError(errorCode);
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
		if (mRetrofit == null) {
			mLogger.debug("Retrofit 신규 객체 생성");
			mRetrofit = new Retrofit.Builder().baseUrl(NetworkDefine.getInstance().getBaseDomain()).addConverterFactory(GsonConverterFactory.create()).client(getDefaultHttpClient()).build();
		} else {
			mLogger.debug("Retrofit 기존 객체 사용");
		}
		
		RetrofitAPIService mRetrofitAPIService = mRetrofit.create(RetrofitAPIService.class);
		mRetrofitAPIService.requestApplicationVersion(NetworkDefine.PARAM_OS_TYPE).enqueue(mCallBack);
	}
}
