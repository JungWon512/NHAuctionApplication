package com.nh.share.api.request;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.GlobalDefine;
import com.nh.share.api.StringUtil;
import com.nh.share.api.response.ResponseUpdateAuctionStatus;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 경매 진행 상태 업데이트 요청 API명 : updateAuctionStatus 응찰 프로그램 최신 버전 정보를 조회한다.
 *
 */
public class ActionRequestUpdateAuctionStatus extends Action {
    private String mAuctionCode; // 경매 구분 코드
    private String mAuctionRound; // 경매 회차
    private String mAuctionLaneCode; // 경매 레인 코드
    private String mAuctionStatus; // 경매 상태

    /**
     * @param auctionCode    경매 구분 코드
     * @param auctionStatus  경매 상태
     * @param resultListener
     */
    public ActionRequestUpdateAuctionStatus(String auctionCode, String auctionRound, String auctionLaneCode,
            String auctionStatus, ActionResultListener resultListener) {
        mAuctionCode = auctionCode;
        mAuctionRound = auctionRound;
        mAuctionLaneCode = auctionLaneCode;
        mAuctionStatus = auctionStatus;
        mResultListenerBase = resultListener;
    }

    public interface RetrofitAPIService {
        @FormUrlEncoded
        @POST(GlobalDefine.API_UPDATE_AUCTION_STATUS)
        Call<ResponseUpdateAuctionStatus> updateAuctionStatus(@FieldMap RequestBody body);
    }

    private final Callback<ResponseUpdateAuctionStatus> mCallBack = new Callback<ResponseUpdateAuctionStatus>() {
        @Override
        public void onResponse(Call<ResponseUpdateAuctionStatus> call, Response<ResponseUpdateAuctionStatus> response) {
            actionDone(resultType.ACTION_RESULT_RUNNEXT);
            Headers headers = response.headers();
            String type = headers.get(CONTENT_TYPE);
            ResponseUpdateAuctionStatus body = response.body();

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
        public void onFailure(Call<ResponseUpdateAuctionStatus> call, Throwable t) {
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
        RequestBody body = new RequestBody();
        body.put("auctionCode", mAuctionCode);
        body.put("auctionRound", mAuctionRound);
        body.put("auctionLaneCode", mAuctionLaneCode);
        body.put("auctionStatus", mAuctionStatus);
        mRetrofitAPIService.updateAuctionStatus(body).enqueue(mCallBack);
    }
}
