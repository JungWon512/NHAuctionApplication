package com.nh.share.api.request;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.GlobalDefine;
import com.nh.share.api.StringUtil;
import com.nh.share.api.response.ResponseSaveAuctionAttendLog;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 경매 참여 이력 저장
 */
public class ActionRequestSaveAuctionAttendLog extends Action {

    private String mAuctionCode; // 경매 구분 코드
    private String mAuctionRound; // 경매 회차 정보
    private String mMemberNum; // 회원번호
    private String mMemberIp; // 회원 IP
    private String mAttendClassCode; // 접속채널
    private String mAuctionPositionCode; // 경매 거점 코드
    private String mAuctionLaneCode; // 레인코드 /* 200715 추가 */
    private String mAuctShortcut; // 단축키 설정(PC응찰 한정) /*201117 추가*/ 

    /**
     * @param auctionCode         경매 구분 코드
     * @param auctionRound        경매 회차 정보
     * @param memberNum           회원번호
     * @param memberIp            회원 IP
     * @param attendClassCode     접속채널
     * @param auctionPositionCode 경매 거점 코드
     * @param auctShortcut     단축키 설정(Y/N)
     * @param resultListener
     */
    public ActionRequestSaveAuctionAttendLog(String auctionCode, String auctionRound, String memberNum, String memberIp,
            String attendClassCode, String auctionPositionCode, String auctionLaneCode, String auctShortcut, ActionResultListener resultListener) {
        mAuctionCode = auctionCode;
        mAuctionRound = auctionRound;
        mMemberNum = memberNum;
        mMemberIp = memberIp;
        mAttendClassCode = attendClassCode;
        mAuctionPositionCode = auctionPositionCode;
        mAuctionLaneCode = auctionLaneCode;
        mAuctShortcut = auctShortcut;
        mResultListenerBase = resultListener;
    }

    public interface RetrofitAPIService {
        @GET(GlobalDefine.API_AUCTION_SAVE_AUCTION_ATTEND_LOG)
        Call<ResponseSaveAuctionAttendLog> getSaveAuctionAttendLog(@Query("auctionCode") String auctionCode,
                @Query("auctionRound") String auctionRound, @Query("memberNum") String memberNum,
                @Query("memberIp") String memberIp, @Query("attendClassCode") String attendClassCode,
                @Query("auctionPositionCode") String auctionPositionCode, @Query("auctionLaneCode") String auctionLaneCode, 
                @Query("auctShortcut") String auctShortcut);
    }

    private final Callback<ResponseSaveAuctionAttendLog> mCallBack = new Callback<ResponseSaveAuctionAttendLog>() {
        @Override
        public void onResponse(Call<ResponseSaveAuctionAttendLog> call,
                Response<ResponseSaveAuctionAttendLog> response) {
            actionDone(resultType.ACTION_RESULT_RUNNEXT);
            Headers headers = response.headers();
            String type = headers.get(CONTENT_TYPE);
            ResponseSaveAuctionAttendLog body = response.body();

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
        public void onFailure(Call<ResponseSaveAuctionAttendLog> call, Throwable t) {
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
        mRetrofitAPIService.getSaveAuctionAttendLog(mAuctionCode, mAuctionRound, mMemberNum, mMemberIp,
                mAttendClassCode, mAuctionPositionCode, mAuctionLaneCode, mAuctShortcut).enqueue(mCallBack);

    }
}
