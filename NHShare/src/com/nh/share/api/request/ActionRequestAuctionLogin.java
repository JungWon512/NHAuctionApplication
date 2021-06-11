package com.nh.share.api.request;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.GlobalDefine;
import com.nh.share.api.StringUtil;
import com.nh.share.api.response.ResponseAuctionLogin;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 경매 사용자 인증 처리 API명 : requestActionLogin 경매 회원에 대한 인증 처리를 진행한다
 *
 */
public class ActionRequestAuctionLogin extends Action {
    private String mLoginType; // 로그인 종류
    private String mUserId; // 아이디
    private String mUserPassword; // 비밀번호
    private String mFlagOtpAuth; // OTP 인증 성공 여부
    private String mSmsAuthNumber; // SMS 인증번호
    private String mAuthToken; // 사용자 인증 토큰
    private String mTokenType; // 인증토큰 타입

    /**
     * @param loginType      로그인 종류
     * @param userId         아이디
     * @param userPassword   비밀번호
     * @param flagOtpAuth    OTP 인증 성공 여부
     * @param smsAuthNumber  SMS 인증번호
     * @param authToken      사용자 인증 토큰
     * @param resultListener
     */
    public ActionRequestAuctionLogin(String loginType, String userId, String userPassword, String flagOtpAuth,
            String smsAuthNumber, String authToken, String tokenType, ActionResultListener resultListener) {
        mLoginType = loginType;
        mUserId = userId;
        mUserPassword = userPassword;
        mFlagOtpAuth = flagOtpAuth;
        mSmsAuthNumber = smsAuthNumber;
        mAuthToken = authToken;
        mTokenType = tokenType;
        mResultListenerBase = resultListener;
    }

    public interface RetrofitAPIService {
        @GET(GlobalDefine.API_REQUEST_AUCTION_LOGIN)
        Call<ResponseAuctionLogin> requestAuctionLogin(@Query("loginType") String loginType,
                @Query("userId") String userId, @Query("userPassword") String userPassword,
                @Query("flagOtpAuth") String flagOtpAuth, @Query("smsAuthNumber") String smsAuthNumber,
                @Query("authToken") String authToken, @Query("tokenType") String tokenType);
    }

    private final Callback<ResponseAuctionLogin> mCallBack = new Callback<ResponseAuctionLogin>() {
        @Override
        public void onResponse(Call<ResponseAuctionLogin> call, Response<ResponseAuctionLogin> response) {
            actionDone(resultType.ACTION_RESULT_RUNNEXT);
            Headers headers = response.headers();
            String type = headers.get(CONTENT_TYPE);
            ResponseAuctionLogin body = response.body();

            switch (response.code()) {
            case 200:
                if (body.getStatus().equals("success")) {
                    mResultListenerBase.onResponseResult(body);
                } else {
                    actionDone(resultType.ACTION_RESULT_ERROR_SKIP);
                }
                break;
            default:
                actionDone(resultType.ACTION_RESULT_ERROR_NOT_RESPONSE);
                break;
            }
        }

        @Override
        public void onFailure(Call<ResponseAuctionLogin> call, Throwable t) {
            if (t.toString().contains("Exception") || t.toString().contains("JsonSyntaxException")
                    || t.toString().contains("MalformedJsonException")
                    || t.toString().contains("NoRouteToHostException")
                    || t.toString().contains("SocketTimeoutException")) {
                ActionRuler.getInstance().finish();
                actionDone(resultType.ACTION_RESULT_ERROR_NOT_RESPONSE);
            }
        }

    };

    @Override
    void actionDone(String message, resultType type, String errorCode) {
        String errStr = "";
        String errCodeDisp = "";

        if (StringUtil.getInstance().isValidString(errorCode)) {
            errCodeDisp = "[" + errorCode + "]";
        } else {
            errorCode = type.toString();
        }

        if (StringUtil.getInstance().isValidString(message)) {
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

        mRetrofit = new Retrofit.Builder().baseUrl(GlobalDefine.getInstance().getBaseDomain())
                .addConverterFactory(GsonConverterFactory.create()).client(getDefaultHttpClient()).build();

        RetrofitAPIService mRetrofitAPIService = mRetrofit.create(RetrofitAPIService.class);
        mRetrofitAPIService.requestAuctionLogin(mLoginType, mUserId, mUserPassword, mFlagOtpAuth, mSmsAuthNumber,
                mAuthToken, mTokenType).enqueue(mCallBack);
    }
}
