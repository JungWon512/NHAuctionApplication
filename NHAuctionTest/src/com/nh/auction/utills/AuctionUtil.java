package com.nh.auction.utills;

import java.lang.invoke.MethodHandles;
import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auction.preferences.SharedPreference;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AuctionUtil {

    private Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static AuctionUtil instance = new AuctionUtil();

    public PauseTransition mTransition; // 메인 세션 컨트롤

    public Stage mSessionStage;

    private boolean isTimeOut = false;

    public static synchronized AuctionUtil getInstance() {
        return instance;
    }

    /**
     * 사용자 데이터 삭제
     *
     * @param context
     */
    public void clearUserInfo() {

        SharedPreference sharedPreference = new SharedPreference();

        sharedPreference.setString(SharedPreference.USER_INFO_AUTH_TOKEN, "");// 토큰 정보 저장
        sharedPreference.setString(SharedPreference.USER_INFO_REFRESH_TOKEN, "");// refresh 토큰 정보 저장
        sharedPreference.setString(SharedPreference.USER_INFO_USER_STATUS, "");// 회원 상태 저장
        sharedPreference.setString(SharedPreference.USER_INFO_USER_NUM, "");// 고객 회원 번호 저장
        sharedPreference.setString(SharedPreference.USER_INFO_USER_NAME, "");// 회원명 저장
        sharedPreference.setString(SharedPreference.USER_INFO_USER_CONTROL_FLAG, ""); // 통제 회원 여부
        sharedPreference.setString(SharedPreference.USER_INFO_PROFILE_IMAGE_URL, ""); // 회원 프로필 이미지 URL 저장
        sharedPreference.setString(SharedPreference.USER_INFO_YEAR_FEE_EXPI_YMD, ""); // 연회비 만료일자
        sharedPreference.setString(SharedPreference.USER_INFO_DDAY, ""); // 연회비 만료일자
        sharedPreference.setString(SharedPreference.USER_INFO_VIRTUAL_ACC, ""); // 가상계좌
        sharedPreference.setString(SharedPreference.USER_INFO_VIRTUAL_ACC_NM, ""); // 가상계좌 예금주
        sharedPreference.setString(SharedPreference.USER_INFO_YEAR_FEE_NOTI_YN, ""); // 연회비 노출 여부
        sharedPreference.setString(SharedPreference.USER_INFO_RECENT_CONNECT_DATE_TIME, ""); // 최근 접속일시 저장
        sharedPreference.setBoolean(SharedPreference.USER_INFO_AUTO_LOGIN, false); // 자동로그인
        sharedPreference.setBoolean(SharedPreference.USER_INFO_IS_AUCTION_MAMBER, false); // 경매 or 관전회원
        
        mLogger.debug("[clearUserInfo]");
    }

    /**
     * 
     * @MethodName setApplicationSession
     * @Description 환경설정에서 설정된 세션타임아웃 적용
     *
     */
    public void setApplicationSession(Stage primaryStage) {
	
        SharedPreference sharedPreference = new SharedPreference();

        int savedSessionTime = sharedPreference.getInt(SharedPreference.PREFERENCE_BIDDING_APPLICATION_SETTING_SESSION_TIME, 0);

        if (savedSessionTime > 0) {

            if (mTransition != null) {
                mTransition.stop();
                mTransition = null;
                mLogger.debug("SESSION mTransition init");
            }

            if (mSessionStage != null) {
                mLogger.debug("SESSION mSessionStage init ");
                mSessionStage = null;
            }

            int sessionTime = (savedSessionTime * 3 * 2) * 100;
            Duration delay = Duration.seconds(sessionTime);

            mTransition = new PauseTransition(delay);
            mTransition.playFromStart();
            mLogger.debug("sessionTime : " + sessionTime);

            setSessionStageListener(primaryStage);

            mSessionStage = primaryStage;

            isTimeOut = false;

            mLogger.debug("#### SESSION START ####");
        }
    }

    public void setSessionStageListener(Stage primaryStage) {
        mLogger.debug("setSessionStageListener");
        primaryStage.getScene().addEventFilter(InputEvent.ANY, mSessionInputEventListener);
        primaryStage.getScene().addEventFilter(MouseEvent.ANY, mSessionMouseEventListener);
        primaryStage.xProperty().addListener(mSessionPropertyListener);
        primaryStage.yProperty().addListener(mSessionPropertyListener);
    }

    EventHandler<InputEvent> mSessionInputEventListener = (e -> {
        if (mTransition != null) {
            mTransition.playFromStart();
        }
    });

    EventHandler<MouseEvent> mSessionMouseEventListener = (e -> {
        if (mTransition != null) {
            mTransition.playFromStart();
        }
    });

    private ChangeListener<? super Number> mSessionPropertyListener = (observable, oldValue, newValue) -> {
        if (mTransition != null) {
            mTransition.playFromStart();
        }
    };

    public synchronized void sessionCheckClose(Stage stage) {
        Platform.runLater(() -> {

            if (mTransition != null) {
                stage.getScene().removeEventFilter(MouseEvent.ANY, mSessionInputEventListener);
                stage.getScene().removeEventFilter(MouseEvent.ANY, mSessionMouseEventListener);
                stage.xProperty().removeListener(mSessionPropertyListener);
                stage.yProperty().removeListener(mSessionPropertyListener);

                mTransition.stop();
                mTransition = null;

                mLogger.debug("#### SESSION CLOSE ####");
            }

        });
    }

    /**
     * 세자리수 콤마 찍기
     *
     * @param num
     * @param decimal
     *            소수점 표현 유무
     * @return
     */
    public synchronized String setConvertComma(int num, boolean decimal) {
        if (decimal) {
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            return decimalFormat.format(num);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            return decimalFormat.format(num);
        }
    }

    /**
     * 세자리수 콤마 찍기
     *
     * @param numStr
     * @param decimal
     *            소수점 표현 유무
     * @return
     */
    public synchronized String setConvertComma(String numStr, boolean decimal) {
        if (isIntegerFromStr(numStr)) {
            return setConvertComma(Integer.parseInt(numStr), decimal);
        }
        return numStr;
    }

    /**
     * integer 체크
     *
     * @param num
     * @return
     */
    public synchronized boolean isIntegerFromStr(String num) {
        try {
            Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

}
