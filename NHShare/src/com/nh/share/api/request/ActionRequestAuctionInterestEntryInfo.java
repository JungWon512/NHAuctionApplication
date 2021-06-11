package com.nh.share.api.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.GlobalDefine;
import com.nh.share.api.StringUtil;
import com.nh.share.api.response.ResponseAuctionInterestEntryInfo;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 관심 차량 정보 조회 요청 API명 : getAuctionInterestEntryInfo 관심 차량 정보를 조회한다.
 *
 */
public class ActionRequestAuctionInterestEntryInfo extends Action {
    private final Logger mLogger = LoggerFactory.getLogger(ActionRequestAuctionInterestEntryInfo.class);

    private String mAuctionCode; // 경매 구분 코드 (실시간:20/SPOT:40)
    private String mAuctionRound; // 경매 회차
    private String mAuctionLaneCode; // 경매 레인 코드
    private String mAuctionEntryNumSeq; // 경매 출품 순번

    /**
     * @param auctionMemberNum 경매 회원 번호
     * @param resultListener
     */
    public ActionRequestAuctionInterestEntryInfo(String auctionCode, String auctionRound, String auctionLaneCode,
            String auctionEntryNumSeq, ActionResultListener resultListener) {
        mAuctionCode = auctionCode;
        mAuctionRound = auctionRound;
        mAuctionLaneCode = auctionLaneCode;
        mAuctionEntryNumSeq = auctionEntryNumSeq;
        mResultListenerBase = resultListener;
    }

    public interface RetrofitAPIService {
        @GET(GlobalDefine.API_GET_AUCTION_INTEREST_ENTRY_INFO)
        Call<ResponseAuctionInterestEntryInfo> getAuctionInterestEntryInfo(@Query("auctionCode") String auctionCode,
                @Query("auctionRound") String auctionRound, @Query("auctionLaneCode") String auctionLaneCode,
                @Query("auctionEntryNumSeq") String auctionEntryNumSeq);
    }

    private final Callback<ResponseAuctionInterestEntryInfo> mCallBack = new Callback<ResponseAuctionInterestEntryInfo>() {
        @Override
        public void onResponse(Call<ResponseAuctionInterestEntryInfo> call,
                Response<ResponseAuctionInterestEntryInfo> response) {
            actionDone(resultType.ACTION_RESULT_RUNNEXT);
            Headers headers = response.headers();
            String type = headers.get(CONTENT_TYPE);
            ResponseAuctionInterestEntryInfo body = response.body();

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
        public void onFailure(Call<ResponseAuctionInterestEntryInfo> call, Throwable t) {
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

        mLogger.debug("================RequestAuctionInterestEntryInfo[Start]================");
        mLogger.debug("Auction Code : " + mAuctionCode);
        mLogger.debug("Auction Round : " + mAuctionRound);
        mLogger.debug("Auction Lane Code : " + mAuctionLaneCode);
        mLogger.debug("Auction Entry Seq : " + mAuctionEntryNumSeq);
        mLogger.debug("================RequestAuctionInterestEntryInfo[ End ]================");

        RetrofitAPIService mRetrofitAPIService = mRetrofit.create(RetrofitAPIService.class);
        mRetrofitAPIService
                .getAuctionInterestEntryInfo(mAuctionCode, mAuctionRound, mAuctionLaneCode, mAuctionEntryNumSeq)
                .enqueue(mCallBack);
    }
}
