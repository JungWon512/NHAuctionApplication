package com.nh.auctionserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 경매 설정을 저장하는 클래스 AuctionState는 현재 경매 진행상황을 실시간으로 변경시키고 경매 생성시 설정한 값들은 이 클래스를
 * 이용한다
 *
 */
public class AuctionConfig {
    private final Logger mLogger = LoggerFactory.getLogger(AuctionConfig.class);

    private final String AUCTION_BASE_PRICE = "baseStartPrice";
}
