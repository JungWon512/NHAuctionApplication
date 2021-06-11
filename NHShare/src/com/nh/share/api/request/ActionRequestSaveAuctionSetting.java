package com.nh.share.api.request;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.GlobalDefine;
import com.nh.share.api.StringUtil;
import com.nh.share.api.response.ResponseSaveAuctionSetting;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 응찰 프로그램 설정 정보 저장 요청 API명 : requestSaveAuctionSettings 응찰 프로그램 설정 정보를 저장 요청한다.
 *
 */
public class ActionRequestSaveAuctionSetting extends Action {
    private String mMemberNum; // 고객 회원 번호
    private String mFlagAutoLogin; // 자동 로그인 여부
    private String mFlagAuctionNoticePush; // 경매 시작 푸쉬 알림 여부
    private String mAuctionSoundOnOff; // 경매 사운드 ON/OFF
    private String mAuctionDisplaySetting; // 경매 화면 배치
    private String mLanguageType; // 언어 종류
    private String mScreenBrightness; // 화면 밝기 조절
    private String mSessionDuration; // 실시간 경매 유지 시간

    /**
     * @param memberNum             고객 회원 번호
     * @param flagAutoLogin         자동 로그인 여부
     * @param flagAuctionNoticePush 경매 시작 푸쉬 알림 여부
     * @param auctionSoundOnOff     경매 사운드 On, Off
     * @param auctionDisplaySetting 경매 화면 배치
     * @param languageType          언어 종류
     * @param screenBrightness      화면 밝기 조절
     * @param sessionDuration       실시간 경매 유지 시간
     * @param resultListener
     */
    public ActionRequestSaveAuctionSetting(String memberNum, String flagAutoLogin, String flagAuctionNoticePush,
            String auctionSoundOnOff, String auctionDisplaySetting, String languageType, String screenBrightness,
            String sessionDuration, ActionResultListener resultListener) {
        mMemberNum = memberNum;
        mFlagAutoLogin = flagAutoLogin;
        mFlagAuctionNoticePush = flagAuctionNoticePush;
        mAuctionSoundOnOff = auctionSoundOnOff;
        mAuctionDisplaySetting = auctionDisplaySetting;
        mLanguageType = languageType;
        mScreenBrightness = screenBrightness;
        mSessionDuration = sessionDuration;
        mResultListenerBase = resultListener;
    }

    public interface RetrofitAPIService {
        @POST(GlobalDefine.API_REQUEST_SAVE_AUCTION_SETTINGS)
        Call<ResponseSaveAuctionSetting> requestSaveAuctionSetting(@Body RequestBody body);
    }

    private final Callback<ResponseSaveAuctionSetting> mCallBack = new Callback<ResponseSaveAuctionSetting>() {
        @Override
        public void onResponse(Call<ResponseSaveAuctionSetting> call, Response<ResponseSaveAuctionSetting> response) {
            actionDone(resultType.ACTION_RESULT_RUNNEXT);
            Headers headers = response.headers();
            String type = headers.get(CONTENT_TYPE);
            ResponseSaveAuctionSetting body = response.body();

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
        public void onFailure(Call<ResponseSaveAuctionSetting> call, Throwable t) {
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
        body.put("memberNum", mMemberNum);
        if (mFlagAutoLogin != null) {
            body.put("flagAutoLogin", mFlagAutoLogin);
        }
        if (mFlagAuctionNoticePush != null) {
            body.put("flagAuctionNoticePush", mFlagAuctionNoticePush);
        }
        if (mAuctionSoundOnOff != null) {
            body.put("auctionSoundOnOff", mAuctionSoundOnOff);
        }
        if (mAuctionDisplaySetting != null) {
            body.put("auctionDisplaySetting", mAuctionDisplaySetting);
        }
        if (mLanguageType != null) {
            body.put("languageType", mLanguageType);
        }
        if (mScreenBrightness != null) {
            body.put("screenBrightness", mScreenBrightness);
        }
        if (mSessionDuration != null) {
            body.put("sessionDuration", mSessionDuration);
        }

        mRetrofitAPIService.requestSaveAuctionSetting(body).enqueue(mCallBack);
    }
}
