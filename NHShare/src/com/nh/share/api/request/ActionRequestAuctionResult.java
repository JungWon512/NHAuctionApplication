package com.nh.share.api.request;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.GlobalDefine;
import com.nh.share.api.StringUtil;
import com.nh.share.api.response.ResponseAuctionResult;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 경매 낙유찰 결과 정보 조회 요청 API명 : getAuctionResult 경매 낙유찰 정보를 조회 후 반환 처리한다. 전달된 출품 차량
 * 정보 기준으로 이전에 발생된 모든 낙유찰 정보를 반환 처리한다.
 *
 */
public class ActionRequestAuctionResult extends Action {
    private String mAuctionCode; // 경매 구분 코드
    private String mAuctionRound; // 경매 회차
    private String mAuctionLaneCode; // 경매 레인 코드
    private String mAuctionEntryNum; // 경매 출품 번호

    /**
     * @param auctionCode     경매 구분 코드
     * @param auctionRound    경매 회차
     * @param auctionEntryNum 경매 출품 번호
     * @param productCode     상품 번호
     * @param resultListener
     */
    public ActionRequestAuctionResult(String auctionCode, String auctionRound, String auctionLaneCode,
            String auctionEntryNum, ActionResultListener resultListener) {
        mAuctionCode = auctionCode;
        mAuctionRound = auctionRound;
        mAuctionLaneCode = auctionLaneCode;
        mAuctionEntryNum = auctionEntryNum;
        mResultListenerBase = resultListener;
    }

    public interface RetrofitAPIService {
        @GET(GlobalDefine.API_GET_AUCTION_RESULT)
        Call<ResponseAuctionResult> getAuctionResult(@Query("auctionCode") String auctionCode,
                @Query("auctionRound") String auctionRound, @Query("auctionLaneCode") String auctionLaneCode,
                @Query("auctionEntryNum") String auctionEntryNum);
    }

    private final Callback<ResponseAuctionResult> mCallBack = new Callback<ResponseAuctionResult>() {
        @Override
        public void onResponse(Call<ResponseAuctionResult> call, Response<ResponseAuctionResult> response) {
            actionDone(resultType.ACTION_RESULT_RUNNEXT);
            Headers headers = response.headers();
            String type = headers.get(CONTENT_TYPE);
            ResponseAuctionResult body = response.body();

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
        public void onFailure(Call<ResponseAuctionResult> call, Throwable t) {
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
        mRetrofitAPIService.getAuctionResult(mAuctionCode, mAuctionRound, mAuctionLaneCode, mAuctionEntryNum)
                .enqueue(mCallBack);
    }
}
