package com.nh.share.controller.models;

import java.util.Iterator;
import java.util.Map;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 설정 변경 처리
 * 
 * 제어프로그램 -> 서버
 * 
 * CE|[변경키|변경값]
 *
 */
public class EditSetting implements FromAuctionController {
    public static final char TYPE = 'E';
    private Map<String, String> mSettings;

    public EditSetting(Map<String, String> settings) {
        this.mSettings = settings;
    }

    public Map<String, String> getSettings() {
        return mSettings;
    }

    @Override
    public String getEncodedMessage() {
        StringBuilder builder = new StringBuilder();
        Iterator<String> keys = mSettings.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            builder.append(key);
            builder.append(AuctionShareSetting.DELIMITER);
            builder.append(mSettings.get(key));
            if (keys.hasNext()) {
                builder.append(AuctionShareSetting.DELIMITER);
            }
        }
        return String.format("%c%c%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, builder.toString());
    }
}
