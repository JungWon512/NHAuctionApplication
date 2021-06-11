package com.nh.auctionserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.setting.AuctionServerSetting;
import com.nh.share.api.model.AuctionSettingsInformationResult;
import com.nh.share.controller.models.EditSetting;

/**
 * 경매 설정을 저장하는 클래스 AuctionState는 현재 경매 진행상황을 실시간으로 변경시키고 경매 생성시 설정한 값들은 이 클래스를
 * 이용한다
 *
 */
public class AuctionConfig {
    private final Logger mLogger = LoggerFactory.getLogger(AuctionConfig.class);

    private final String AUCTION_TIME = "biddingTime";
    private final String AUCTION_DETERMINE_TIME = "biddingAdditionalTime";
    private final String AUCTION_NEXT_DELAY_TIME = "biddingIntervalTime";
    private final String AUCTION_AUTO_RISE_COUNT = "maxAutoUpCount";
    private final String AUCTION_BASE_PRICE = "baseStartPrice";
    private final String AUCTION_BELOW_RISING_PRICE = "belowRisePrice";
    private final String AUCTION_MORE_RISING_PRICE = "moreRisePrice";
    private final String AUCTION_MAX_RISING_PRICE = "maxRisePrice";

    private AuctionSettingsInformationResult mAuctionInfo;

    public void setAuctionSettingInformation(EditSetting editSetting) {
        if (editSetting.getSettings() != null && editSetting.getSettings().containsKey(AUCTION_TIME)) {
            AuctionServerSetting.AUCTION_TIME = Integer.valueOf(editSetting.getSettings().get(AUCTION_TIME));
            mLogger.info("경매 진행 시간이 " + AuctionServerSetting.AUCTION_TIME + "으로 변경 되었습니다.");
        }

        if (editSetting.getSettings() != null && editSetting.getSettings().containsKey(AUCTION_DETERMINE_TIME)) {
            AuctionServerSetting.AUCTION_DETERMINE_TIME = Integer
                    .valueOf(editSetting.getSettings().get(AUCTION_DETERMINE_TIME));
            mLogger.info("경매 낙/유찰 지연 시간이 " + AuctionServerSetting.AUCTION_DETERMINE_TIME + "으로 변경 되었습니다.");
        }

        if (editSetting.getSettings() != null && editSetting.getSettings().containsKey(AUCTION_NEXT_DELAY_TIME)) {
            AuctionServerSetting.AUCTION_NEXT_DELAY_TIME = Integer
                    .valueOf(editSetting.getSettings().get(AUCTION_NEXT_DELAY_TIME));
            mLogger.info("경매 다음 시작 간격 시간이 " + AuctionServerSetting.AUCTION_NEXT_DELAY_TIME + "으로 변경 되었습니다.");
        }

        if (editSetting.getSettings() != null && editSetting.getSettings().containsKey(AUCTION_AUTO_RISE_COUNT)) {
            AuctionServerSetting.AUCTION_AUTO_RISE_COUNT = Integer
                    .valueOf(editSetting.getSettings().get(AUCTION_AUTO_RISE_COUNT));
            mLogger.info("경매 자동 상승 횟수가 " + AuctionServerSetting.AUCTION_AUTO_RISE_COUNT + "으로 변경 되었습니다.");
        }

        if (editSetting.getSettings() != null && editSetting.getSettings().containsKey(AUCTION_BASE_PRICE)) {
            AuctionServerSetting.AUCTION_BASE_PRICE = Integer
                    .valueOf(editSetting.getSettings().get(AUCTION_BASE_PRICE));
            mLogger.info("경매 기준 금액이 " + AuctionServerSetting.AUCTION_BASE_PRICE + "으로 변경 되었습니다.");
        }

        if (editSetting.getSettings() != null && editSetting.getSettings().containsKey(AUCTION_BELOW_RISING_PRICE)) {
            AuctionServerSetting.AUCTION_BELOW_RISING_PRICE = Integer
                    .valueOf(editSetting.getSettings().get(AUCTION_BELOW_RISING_PRICE));
            mLogger.info("경매 기준가 이하 상승 금액이 " + AuctionServerSetting.AUCTION_BELOW_RISING_PRICE + "으로 변경 되었습니다.");
        }

        if (editSetting.getSettings() != null && editSetting.getSettings().containsKey(AUCTION_MORE_RISING_PRICE)) {
            AuctionServerSetting.AUCTION_MORE_RISING_PRICE = Integer
                    .valueOf(editSetting.getSettings().get(AUCTION_MORE_RISING_PRICE));
            mLogger.info("경매 기준가 이상 상승 금액이 " + AuctionServerSetting.AUCTION_MORE_RISING_PRICE + "으로 변경 되었습니다.");
        }

        if (editSetting.getSettings() != null && editSetting.getSettings().containsKey(AUCTION_MAX_RISING_PRICE)) {
            AuctionServerSetting.AUCTION_MAX_RISING_PRICE = Integer
                    .valueOf(editSetting.getSettings().get(AUCTION_MAX_RISING_PRICE));
            mLogger.info("경매가 1억 이상 상승 금액이 " + AuctionServerSetting.AUCTION_MAX_RISING_PRICE + "으로 변경 되었습니다.");
        }
    }

    public AuctionSettingsInformationResult getAuctionSettingsInformationResult() {
        return mAuctionInfo;
    }

    public void setAuctionSettingsInformationResult(AuctionSettingsInformationResult result) {
        mAuctionInfo = result;

        AuctionServerSetting.AUCTION_TIME = Integer.valueOf(mAuctionInfo.getAuctionEntryTime());
        AuctionServerSetting.AUCTION_DETERMINE_TIME = Integer.valueOf(mAuctionInfo.getSuccBidDelayTime());
        AuctionServerSetting.AUCTION_NEXT_DELAY_TIME = Integer.valueOf(mAuctionInfo.getAuctionNextEntryTime());
        AuctionServerSetting.AUCTION_AUTO_RISE_COUNT = Integer.valueOf(mAuctionInfo.getAuctionAutoRiseCount());
        AuctionServerSetting.AUCTION_BASE_PRICE = Integer.valueOf(mAuctionInfo.getAuctionBasePrice());
        AuctionServerSetting.AUCTION_BELOW_RISING_PRICE = Integer.valueOf(mAuctionInfo.getAuctionBelowRisingPrice());
        AuctionServerSetting.AUCTION_MORE_RISING_PRICE = Integer.valueOf(mAuctionInfo.getAuctionMoreRisingPrice());
        AuctionServerSetting.AUCTION_MAX_RISING_PRICE = Integer.valueOf(mAuctionInfo.getAuctionMaxRisingPrice());
        AuctionServerSetting.AUCTION_ENTRY_TOTAL_COUNT = Integer.valueOf(mAuctionInfo.getTotalEntryCount());

        mLogger.debug("================ResponseAuctionSettingsInformation[Start]================");
        mLogger.debug("AUCTION_TIME : " + AuctionServerSetting.AUCTION_TIME);
        mLogger.debug("AUCTION_DETERMINE_TIME : " + AuctionServerSetting.AUCTION_DETERMINE_TIME);
        mLogger.debug("AUCTION_NEXT_DELAY_TIME : " + AuctionServerSetting.AUCTION_NEXT_DELAY_TIME);
        mLogger.debug("AUCTION_AUTO_RISE_COUNT : " + AuctionServerSetting.AUCTION_AUTO_RISE_COUNT);
        mLogger.debug("AUCTION_BASE_PRICE: " + AuctionServerSetting.AUCTION_BASE_PRICE);
        mLogger.debug("AUCTION_BELOW_RISING_PRICE : " + AuctionServerSetting.AUCTION_BELOW_RISING_PRICE);
        mLogger.debug("AUCTION_MORE_RISING_PRICE : " + AuctionServerSetting.AUCTION_MORE_RISING_PRICE);
        mLogger.debug("AUCTION_MAX_RISING_PRICE : " + AuctionServerSetting.AUCTION_MAX_RISING_PRICE);
        mLogger.debug("AUCTION_ENTRY_TOTAL_COUNT : " + AuctionServerSetting.AUCTION_ENTRY_TOTAL_COUNT);
        mLogger.debug("================ResponseAuctionSettingsInformation[ End ]================");
    }
}
