package com.nh.scheduler.setting;

import com.nh.scheduler.utils.AuctionServerPortUtils;

/**
 * 
 * @ClassName AuctionSchedulerSetting.java
 * @Description 스케줄러 프로그램에서 사용하는 상수 Define
 * @anthor ishift
 * @since 2021.11.10
 */
public class AuctionSchedulerSetting {
    
    public static final String RELEASE_VERSION_NAME = "1.0.0";
    public static final String RELEASE_VERSION_DATE = "2021.11.10";

    public static final int SERVER_PORTNUM_START = 5001; // 경매서버 실행시 사용 PORT 대역
    public static final int SERVER_PORTNUM_END = 5999; // PORTNUM_START ~ PORTNUM_END 사이 포트 사용.
    
    // 기본 실행 주기
    public static final String DEFAULT_RUN_TIME = "10";

    // Scheduler Identity
    public static final String JOBDETAIL_IDENTITY = "CreateServer";
    public static final String JOBTRIGGER_IDENTITY = "ServerTrigger";
    public static final String MAP_IDENTITY = "SchedulerPort";
    
    // API
    public static final String API_SUCCESS = "success";

    public static AuctionServerPortUtils serverFunction = new AuctionServerPortUtils();
}
