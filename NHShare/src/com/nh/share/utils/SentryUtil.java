package com.nh.share.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.share.code.GlobalDefineCode;

import io.sentry.Sentry;

public class SentryUtil {
	 
    private static final Logger mLogger = LoggerFactory.getLogger(SentryUtil.class);

    private static SentryUtil instance = null;
    
    public static synchronized SentryUtil getInstance() {

        if (instance == null) {
            instance = new SentryUtil();
        }

        return instance;
    }

    /**
	 * Sentry Error Monitoring  Tool 설정
	 */
	public void initSentry(String appVersion) {
		
		if(!CommonUtils.getInstance().isValidString(appVersion)) {
			mLogger.debug("[Sentri 초기화 실패. Application 버전명이 없습니다.]");
			return;
		}

		Sentry.init(options -> {
		  options.setDsn(GlobalDefineCode.SENTRY_INFO.SENTRY_CLIENT_KEY);
		  options.setEnvironment(GlobalDefineCode.SENTRY_INFO.getSentryEnvironment());
		  options.setRelease(getSentryRelease(appVersion)); 
		  options.setTracesSampleRate(GlobalDefineCode.SENTRY_INFO.SENTRY_RATE);
		  options.setDebug(true);
		});
		
	}

	/**
	 * Release 버전명 반환 
	 * @return  ex) nhlyvly@1.0.8
	 */
	public static String getSentryRelease(String appVersion) {

		String applicationVersion = "0.0.1";
		
		if(appVersion.toLowerCase().startsWith("v")) {
			applicationVersion = appVersion.substring(1);
		}else {
			applicationVersion = appVersion;
		}

		return GlobalDefineCode.SENTRY_INFO.SENTRY_PROJECT_NAME + "@" + applicationVersion;

	}	

	   
    /**
     * Sentry 에러 로그 전송
     * @param e
     */
    public void sendExceptionLog(Exception e) {
    	if(Sentry.isEnabled()) {
    		Sentry.captureException(e);	
    	}
    } 
}
