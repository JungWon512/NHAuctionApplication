package com.nh.share.api.request;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.GlobalDefine;
import com.nh.share.api.StringUtil;
import com.nh.share.api.response.ResponseAuctionEntryInformation;
import com.nh.share.api.response.ResponseAuctionJoinStatus;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 응찰 프로그램 메인 화면에서 참여버튼 클릭 시 참여 가능 여부를 조회한다.(경매 상태가 대기 또는 진행이며, 포트 정보 있을경우 참여 가능
 * 경매시작 10분전이 아니면 모두 참여불가상태
 */
public class ActionRequestAuctionJoinStatus extends Action {
    private String mAuctionCode; // 경매 구분 코드
    private String mAuctionRound; // 경매 회차 정보
    private String mAuctionLaneCode; // 경매 레인 코드

    /**
     * @param auctionCode        경매 구분 코드
     * @param auctionRound       경매 회차 정보
     * @param auctionLaneCode    경매 레인 코드
     * @param resultListener
     */
    public ActionRequestAuctionJoinStatus(String auctionCode, String auctionRound, String auctionLaneCode, ActionResultListener resultListener) {
        mAuctionCode = auctionCode;
        mAuctionRound = auctionRound;
        mAuctionLaneCode = auctionLaneCode;
        mResultListenerBase = resultListener;
    }

    public interface RetrofitAPIService {
        @GET(GlobalDefine.API_AUCTION_CONNECTION_STATUS)
        Call<ResponseAuctionJoinStatus> getAuctionConnectionStatus(@Query("auctionCode") String auctionCode,
                @Query("auctionRound") String auctionRound, @Query("auctionLaneCode") String auctionLaneCode);
    }

    private final Callback<ResponseAuctionJoinStatus> mCallBack = new Callback<ResponseAuctionJoinStatus>() {
        @Override
        public void onResponse(Call<ResponseAuctionJoinStatus> call,
                Response<ResponseAuctionJoinStatus> response) {
            actionDone(resultType.ACTION_RESULT_RUNNEXT);
            Headers headers = response.headers();
            String type = headers.get(CONTENT_TYPE);
            ResponseAuctionJoinStatus body = response.body();

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
        public void onFailure(Call<ResponseAuctionJoinStatus> call, Throwable t) {
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
        mRetrofitAPIService
                .getAuctionConnectionStatus(mAuctionCode, mAuctionRound, mAuctionLaneCode)
                .enqueue(mCallBack);
    }
}
