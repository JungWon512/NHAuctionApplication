package com.nh.share.api.request;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.NetworkDefine;
import com.nh.share.api.response.BaseResponse;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class Action implements Runnable {

	private static final Logger mLogger = LoggerFactory.getLogger(Action.class);

	private int DEFAULT_TIMEOUT = 15; // API 통신 기본 Time out

	public final String CONTENT_TYPE = "Content-Type";

	public final String ACCEPT_CONTENT_TYPE = "application/json";

	// 공통 Request Header 정의[Start]
	private final String REQUEST_ACCEPT = "accept";
	private final String USER_AGENT = "user-agent";
	private static String USER_AGENT_OS = System.getProperty("os.name").toLowerCase();
	// 공통 Request Header 정의[End]

	protected ActionResultListener mResultListenerBase;

	protected Retrofit mRetrofit;

	Action() {

		if (mRetrofit == null) {
			mRetrofit = new Retrofit.Builder().baseUrl(NetworkDefine.NH_AUCTION_API_HOST).addConverterFactory(GsonConverterFactory.create()).client(getDefaultHttpClient()).build();
		}

	}

	public enum resultType {
		ACTION_RESULT_RUNNEXT, ACTION_RESULT_ERROR_DISABLE_NETWORK, ACTION_RESULT_ERROR_NOT_RESPONSE, ACTION_RESULT_ERROR_RESPONSE, ACTION_RESULT_ERROR_SKIP, ACTION_RESULT_ERROR_INTRO
	}

	abstract void actionDone(String message, resultType type, String errorCode);

	abstract void actionDone(resultType type, String errorCode);

	abstract void actionDone(resultType type);

	/**
	 * Application -> Okhttp 사이 동작 필요 헤더 정의
	 */
	private class ApplicationInterceptor implements Interceptor {
		@Override
		public Response intercept(Interceptor.Chain chain) throws IOException {

			Request original = chain.request();

			Request request = original.newBuilder().header(REQUEST_ACCEPT, ACCEPT_CONTENT_TYPE).header(USER_AGENT, USER_AGENT_OS).method(original.method(), original.body()).build();

			mLogger.debug("[Request Interceptor] url > " + request.url());
			mLogger.debug("[Request Interceptor] headers > " + request.headers());

			return chain.proceed(request);
		}
	}

	/**
	 * Response 상태에 따라 처리
	 */
	private class NetWorkInterceptor implements Interceptor {
		@Override
		public Response intercept(Interceptor.Chain chain) throws IOException {
			Request request = chain.request();
			Response response = chain.proceed(request);

			mLogger.debug("[Response CODE]" + response.code());

			return response;
		}
	}

	OkHttpClient getDefaultHttpClient() {

//		// 디버깅용
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		clientBuilder.retryOnConnectionFailure(true);
		clientBuilder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
		clientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
		clientBuilder.addInterceptor(new ApplicationInterceptor());
		clientBuilder.addNetworkInterceptor(new NetWorkInterceptor());

		return clientBuilder.build();
	}

	OkHttpClient getDownloadHttpClient() {

//		// 다운로드 디버깅용 
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		clientBuilder.retryOnConnectionFailure(true);
		clientBuilder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
		clientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
		clientBuilder.addNetworkInterceptor(new NetWorkInterceptor());

		return clientBuilder.build();
	}

	/**
	 * Response 데이터 유효성 확인 함수
	 *
	 * @param response 응답 데이터
	 * @return 응답 데이터 유효성 여부 반환(true : 정상, false : 비정상)
	 */
	boolean checkResponseBody(BaseResponse response) {
		if (response != null) {
			return false;
		} else {
			return true;
		}
	}

}
