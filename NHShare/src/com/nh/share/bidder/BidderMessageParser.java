package com.nh.share.bidder;

import com.nh.share.bidder.interfaces.FromAuctionBidder;
import com.nh.share.bidder.models.Bidding;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 응찰기 메시지 파서
 *
 * 응찰기에서 보내진 메시지를 각 클래스로 파싱함 분류 기준은 메시지 2번째(index 1) 글자임
 *
 */
public class BidderMessageParser {
    public static FromAuctionBidder parse(String message) {
        String[] messages = message.split(AuctionShareSetting.DELIMITER_REGEX);
        switch (messages[0].charAt(1)) {
        case Bidding.TYPE:
            return new Bidding(messages[1], messages[2], messages[3], messages[4], messages[5]);
        default:
            throw null;
        }
    }
}
