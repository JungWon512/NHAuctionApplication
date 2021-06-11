package com.nh.share.api.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.GlobalDefine;
import com.nh.share.api.StringUtil;
import com.nh.share.api.model.AuctionResult;
import com.nh.share.api.response.ResponseTransmissionAuctionResult;
import com.nh.share.code.GlobalDefineCode;

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
 * 경매 낙유찰 결과 전송 요청 API명 : requestTransmissionAuctionResult
 *
 */
public class ActionRequestTransmissionAuctionResult extends Action {
    private final Logger mLogger = LoggerFactory.getLogger(ActionRequestTransmissionAuctionResult.class);

    private AuctionResult mAuctionResult; // 경매 낙,유찰 결과 정보

    /**
     * @param AuctionResult  경매 결과 정보
     * @param resultListener
     */
    public ActionRequestTransmissionAuctionResult(AuctionResult auctionResult, ActionResultListener resultListener) {
        mAuctionResult = auctionResult;
        mResultListenerBase = resultListener;
    }

    public interface RetrofitAPIService {
        @FormUrlEncoded
        @POST(GlobalDefine.API_REQUEST_TRANSMISSION_AUCTION_RESULT)
        Call<ResponseTransmissionAuctionResult> requestTransmissionAuctionResult(@FieldMap RequestBody body);
    }

    private final Callback<ResponseTransmissionAuctionResult> mCallBack = new Callback<ResponseTransmissionAuctionResult>() {
        @Override
        public void onResponse(Call<ResponseTransmissionAuctionResult> call,
                Response<ResponseTransmissionAuctionResult> response) {
            actionDone(resultType.ACTION_RESULT_RUNNEXT);
            Headers headers = response.headers();
            String type = headers.get(CONTENT_TYPE);
            ResponseTransmissionAuctionResult body = response.body();

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
        public void onFailure(Call<ResponseTransmissionAuctionResult> call, Throwable t) {
            if (t.toString().contains("Exception") || t.toString().contains("JsonSyntaxException")
                    || t.toString().contains("MalformedJsonException")
                    || t.toString().contains("NoRouteToHostException")
                    || t.toString().contains("SocketTimeoutException")) {
                ActionRuler.getInstance().finish();
                actionDone(resultType.ACTION_RESULT_ERROR_NOT_RESPONSE);
            }
        }
    };

    @SuppressWarnings("unchecked")
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
            mResultListenerBase.onResponseError(mAuctionResult.getAuctionEntryNum());
            break;
        case ACTION_RESULT_ERROR_NOT_RESPONSE:
            ActionRuler.getInstance().finish();
            mResultListenerBase.onResponseError(mAuctionResult.getAuctionEntryNum());
            break;
        case ACTION_RESULT_ERROR_RESPONSE:
            ActionRuler.getInstance().finish();
            mResultListenerBase.onResponseError(mAuctionResult.getAuctionEntryNum());
            break;
        case ACTION_RESULT_ERROR_INTRO:
            ActionRuler.getInstance().runNext();
            mResultListenerBase.onResponseError(mAuctionResult.getAuctionEntryNum());
            break;
        case ACTION_RESULT_ERROR_SKIP:
            ActionRuler.getInstance().runNext();
            mResultListenerBase.onResponseError(mAuctionResult.getAuctionEntryNum());
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
        body.put("auctionCode", mAuctionResult.getAuctionCode());
        body.put("auctionRound", mAuctionResult.getAuctionRound());
        body.put("auctionLaneCode", mAuctionResult.getAuctionLaneCode());
        body.put("auctionEntryNum", mAuctionResult.getAuctionEntryNum());
        body.put("productCode", mAuctionResult.getProductCode());
        body.put("auctionResultCode", mAuctionResult.getAuctionResultCode());
        body.put("auctionResultDateTime", mAuctionResult.getAuctionResultDateTime());

        if (mAuctionResult.getSuccessBidMemberNum().equals("SYSTEM")) {
            body.put("successBidMemberNum", "0");
        } else {
            body.put("successBidMemberNum", mAuctionResult.getSuccessBidMemberNum());
        }

        body.put("successBidChannel", getAuctionBidChannelCode(mAuctionResult.getSuccessBidChannel()));

        body.put("successBidPrice", mAuctionResult.getSuccessBidPrice());

        body.put("carName", mAuctionResult.getCarName());
        body.put("hopePrice", mAuctionResult.getHopePrice());
        body.put("hightPrice", mAuctionResult.getHightPrice());

        if (mAuctionResult.getRank1MemberNum().equals("SYSTEM")) {
            body.put("rank1MemberNum", "0");
        } else {
            body.put("rank1MemberNum", mAuctionResult.getRank1MemberNum());
        }

        body.put("rank1BidPrice", mAuctionResult.getRank1BidPrice());

        if (mAuctionResult.getRank2MemberNum().equals("SYSTEM")) {
            body.put("rank2MemberNum", "0");
        } else {
            body.put("rank2MemberNum", mAuctionResult.getRank2MemberNum());
        }

        body.put("rank2BidPrice", mAuctionResult.getRank2BidPrice());

        if (mAuctionResult.getRank3MemberNum().equals("SYSTEM")) {
            body.put("rank3MemberNum", "0");
        } else {
            body.put("rank3MemberNum", mAuctionResult.getRank3MemberNum());
        }

        body.put("rank3BidPrice", mAuctionResult.getRank3BidPrice());

        if (mAuctionResult.getRank4MemberNum().equals("SYSTEM")) {
            body.put("rank4MemberNum", "0");
        } else {
            body.put("rank4MemberNum", mAuctionResult.getRank4MemberNum());
        }

        body.put("rank4BidPrice", mAuctionResult.getRank4BidPrice());

        if (mAuctionResult.getRank5MemberNum().equals("SYSTEM")) {
            body.put("rank5MemberNum", "0");
        } else {
            body.put("rank5MemberNum", mAuctionResult.getRank5MemberNum());
        }

        body.put("rank5BidPrice", mAuctionResult.getRank5BidPrice());

        mLogger.debug("================ActionRequestTransmissionAuction[Start]================");
        mLogger.debug("auctionCode : " + body.get("auctionCode"));
        mLogger.debug("auctionRound : " + body.get("auctionRound"));
        mLogger.debug("auctionLaneCode : " + body.get("auctionLaneCode"));
        mLogger.debug("auctionEntryNum : " + body.get("auctionEntryNum"));
        mLogger.debug("productCode : " + body.get("productCode"));
        mLogger.debug("auctionResultCode : " + body.get("auctionResultCode"));
        mLogger.debug("auctionResultDateTime : " + body.get("auctionResultDateTime"));
        mLogger.debug("successBidMemberNum : " + body.get("successBidMemberNum"));
        mLogger.debug("successBidChannel : " + body.get("successBidChannel"));
        mLogger.debug("successBidPrice : " + body.get("successBidPrice"));
        mLogger.debug("carName : " + body.get("carName"));
        mLogger.debug("hopePrice : " + body.get("hopePrice"));
        mLogger.debug("hightPrice : " + body.get("hightPrice"));
        mLogger.debug("rank1MemberNum : " + body.get("rank1MemberNum"));
        mLogger.debug("rank1BidPrice : " + body.get("rank1BidPrice"));
        mLogger.debug("rank2MemberNum : " + body.get("rank2MemberNum"));
        mLogger.debug("rank2BidPrice : " + body.get("rank2BidPrice"));
        mLogger.debug("rank3MemberNum : " + body.get("rank3MemberNum"));
        mLogger.debug("rank3BidPrice : " + body.get("rank3BidPrice"));
        mLogger.debug("rank4MemberNum : " + body.get("rank4MemberNum"));
        mLogger.debug("rank4BidPrice : " + body.get("rank4BidPrice"));
        mLogger.debug("rank5MemberNum : " + body.get("rank5MemberNum"));
        mLogger.debug("rank5BidPrice : " + body.get("rank5BidPrice"));
        mLogger.debug("================ActionRequestTransmissionAuction[End]================");

        mRetrofitAPIService.requestTransmissionAuctionResult(body).enqueue(mCallBack);
    }

    private String getAuctionBidChannelCode(String channel) {
        String result = "";

        if (channel.equals(GlobalDefineCode.USE_CHANNEL_PC)) {
            result = GlobalDefineCode.USE_CHANNEL_PC_CODE;
        } else if (channel.equals(GlobalDefineCode.USE_CHANNEL_AUCTION_HOUSE)) {
            result = GlobalDefineCode.USE_CHANNEL_AUCTION_HOUSE_CODE;
        } else if (channel.equals(GlobalDefineCode.USE_CHANNEL_ANDROID)) {
            result = GlobalDefineCode.USE_CHANNEL_ANDROID_CODE;
        } else if (channel.equals(GlobalDefineCode.USE_CHANNEL_IOS)) {
            result = GlobalDefineCode.USE_CHANNEL_IOS_CODE;
        } else if (channel.equals(GlobalDefineCode.USE_CHANNEL_ABSENTEE)) {
            result = GlobalDefineCode.USE_CHANNEL_ABSENTEE_CODE;
        } else {
            result = "";
        }

        return result;
    }
}
