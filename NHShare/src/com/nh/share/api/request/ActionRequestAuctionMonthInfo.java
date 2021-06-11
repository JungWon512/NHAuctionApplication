package com.nh.share.api.request;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.GlobalDefine;
import com.nh.share.api.StringUtil;
import com.nh.share.api.response.ResponseAuctionMonthInfo;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 경매 당월 누적 정보 조회 요청 API명 : getAuctionMonthInfo
 *
 */
public class ActionRequestAuctionMonthInfo extends Action {
    private String mBaseDate; // 기준 연월

    /**
     * @param baseDate       기준 연월
     * @param resultListener
     */
    public ActionRequestAuctionMonthInfo(String baseDate, ActionResultListener resultListener) {
        mBaseDate = baseDate;
        mResultListenerBase = resultListener;
    }

    public interface RetrofitAPIService {
        @GET(GlobalDefine.API_GET_AUCTION_MONTH_INFO)
        Call<ResponseAuctionMonthInfo> getAuctionMonthInfo(@Query("baseDate") String baseDate);
    }

    private final Callback<ResponseAuctionMonthInfo> mCallBack = new Callback<ResponseAuctionMonthInfo>() {
        @Override
        public void onResponse(Call<ResponseAuctionMonthInfo> call, Response<ResponseAuctionMonthInfo> response) {
            actionDone(resultType.ACTION_RESULT_RUNNEXT);
            Headers headers = response.headers();
            String type = headers.get(CONTENT_TYPE);
            ResponseAuctionMonthInfo body = response.body();

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
        public void onFailure(Call<ResponseAuctionMonthInfo> call, Throwable t) {
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
        mRetrofitAPIService.getAuctionMonthInfo(mBaseDate).enqueue(mCallBack);
    }
}
