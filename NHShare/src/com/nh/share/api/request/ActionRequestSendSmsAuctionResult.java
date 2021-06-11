package com.nh.share.api.request;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.GlobalDefine;
import com.nh.share.api.StringUtil;
import com.nh.share.api.response.ResponseSendSmsAuctionServerResult;

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
 * 경매 서버 생성 결과를 전송하고 관련 관리자에게 SMS 발송 처리
 *
 */
public class ActionRequestSendSmsAuctionResult extends Action {
    private String mAuctionCode; // 경매 구분 코드
    private String mAuctionRound; // 경매 회차
    private String mAuctionLaneCode; // 경매 레인 코드
    private String mAuctionServerStatus; // 경매 서버 생성 결과
    private String mAuctionSendSmsResultText; // SMS 발송 문자 내용

    /**
     * @param auctionCode              경매 구분 코드
     * @param auctionRound             경매 회차
     * @param auctionLaneCode          경매 레인 코드
     * @param auctionServerStatus      경매 서버 생성 결과
     * @param auctionSendSmsResultText SMS 발송 문자 내용
     * @param resultListener
     */
    public ActionRequestSendSmsAuctionResult(String auctionCode, String auctionRound, String auctionLaneCode,
            String auctionServerStatus, String auctionSendSmsResultText, ActionResultListener resultListener) {
        mAuctionCode = auctionCode;
        mAuctionRound = auctionRound;
        mAuctionLaneCode = auctionLaneCode;
        mAuctionServerStatus = auctionServerStatus;
        mAuctionSendSmsResultText = auctionSendSmsResultText;
        mResultListenerBase = resultListener;
    }

    public interface RetrofitAPIService {
        @FormUrlEncoded
        @POST(GlobalDefine.API_REQUEST_SEND_SMS_AUCTION_SERVER_RESULT)
        Call<ResponseSendSmsAuctionServerResult> updateAuctionServerResult(@FieldMap RequestBody body);
    }

    private final Callback<ResponseSendSmsAuctionServerResult> mCallBack = new Callback<ResponseSendSmsAuctionServerResult>() {
        @Override
        public void onResponse(Call<ResponseSendSmsAuctionServerResult> call,
                Response<ResponseSendSmsAuctionServerResult> response) {
            actionDone(resultType.ACTION_RESULT_RUNNEXT);
            Headers headers = response.headers();
            String type = headers.get(CONTENT_TYPE);
            ResponseSendSmsAuctionServerResult body = response.body();

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
        public void onFailure(Call<ResponseSendSmsAuctionServerResult> call, Throwable t) {
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
        body.put("auctionServerStatus", mAuctionServerStatus);
        body.put("resultText", mAuctionSendSmsResultText);
        mRetrofitAPIService.updateAuctionServerResult(body).enqueue(mCallBack);
    }
}
