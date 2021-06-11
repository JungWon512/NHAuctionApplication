package com.nh.share.api.request;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.GlobalDefine;
import com.nh.share.api.StringUtil;
import com.nh.share.api.response.ResponseAuctionDetailInfo;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 경매 상세 일정 정보 조회 요청 API명 : getAuctionDetailInfo 경매 상세 일정 정보를 조회한다.
 *
 */
public class ActionRequestAuctionDetailInfo extends Action {
    private String mBaseDate; // 기준 연월
    private String mAuctionType; // 경매 종류
    private String mMemberNum; // 고객 회원 번호
    private String mLoginType; // 경매회원 AUCTIONMEMBER , 일반회원 NORMAL

    /**
     * 
     * @param baseDate          // 기준 연월
     * @param auctionType        // 경매 종류
     * @param memberNum      // 고객 회원 번호
     * @param loginType          //로그인타입
     * @param resultListener
     */
    public ActionRequestAuctionDetailInfo(String baseDate, String auctionType, String memberNum, String loginType, ActionResultListener resultListener) {
        mBaseDate = baseDate;
        mAuctionType = auctionType;
        mMemberNum = memberNum;
        mLoginType = loginType;
        mResultListenerBase = resultListener;
    }

    public interface RetrofitAPIService {
        @GET(GlobalDefine.API_GET_AUCTION_DETAIL_INFO)
        Call<ResponseAuctionDetailInfo> getAuctionDetailInfo(@Query("baseDate") String baseDate, @Query("auctionType") String auctionType, @Query("memberNum") String memberNum, @Query("loginType") String loginType);
    }

    private final Callback<ResponseAuctionDetailInfo> mCallBack = new Callback<ResponseAuctionDetailInfo>() {
        @Override
        public void onResponse(Call<ResponseAuctionDetailInfo> call, Response<ResponseAuctionDetailInfo> response) {
            actionDone(resultType.ACTION_RESULT_RUNNEXT);
            Headers headers = response.headers();
            String type = headers.get(CONTENT_TYPE);
            ResponseAuctionDetailInfo body = response.body();

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
        public void onFailure(Call<ResponseAuctionDetailInfo> call, Throwable t) {
            if (t.toString().contains("Exception") || t.toString().contains("JsonSyntaxException") || t.toString().contains("MalformedJsonException") || t.toString().contains("NoRouteToHostException")
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

        mRetrofit = new Retrofit.Builder().baseUrl(GlobalDefine.getInstance().getBaseDomain()).addConverterFactory(GsonConverterFactory.create()).client(getDefaultHttpClient()).build();

        RetrofitAPIService mRetrofitAPIService = mRetrofit.create(RetrofitAPIService.class);
        mRetrofitAPIService.getAuctionDetailInfo(mBaseDate, mAuctionType, mMemberNum, mLoginType).enqueue(mCallBack);
    }
}
