package com.nh.auction.models;

import java.util.Iterator;
import java.util.Map;

import com.nh.auction.interfaces.FromAuctionController;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 경매 설정 변경 처리
 * - 경매 제어 프로그램에서 변경 처리한 경매 설정 정보를 경매 서버에 전송 처리 기능 수행
 * - 전송된 경매 설정 정보는 경매 서버 동작 로직에 반영 처리
 * 
 * 구분자 | 기준금액 | 100 | 기준금액 이상 상승가 | 5 | 기준금액 이하 상승가 | 3 | 기준금액 1억 이상 상승가 | 10 | 경매 진행 시간 | 3000 | 낙찰 지연시간 | 1000 | 다음 시작 간격 | 2000 | 자동 상승 횟수 | 2
 * CE|baseStartPrice|200|biddingTime|5000
 *
 */
public class EditSetting implements FromAuctionController {

	public static final char TYPE = 'E';

	private String baseStartPrice;				// 기준금액 단위: 만원
	private String moreRisePrice;				// 기준금액 이상 상승가 단위: 만원
	private String belowRisePrice;				// 기준금액 이하 상승가 단위: 만원
	private String maxRisePrice;				// 기준금액 1억 이상 상승가 단위: 만원
	private String biddingTime;					// 경매 진행 시간 단위: ms
	private String biddingAdditionalTime;		// 낙찰 지연 시간 단위: ms
	private String biddingIntervalTime;			// 다음 시작 간격 단위: ms
	private String maxAutoUpCount;				// 자동 상승 횟수 단위: ms

	private Map<String, String> settings;

	public String getBaseStartPrice() {
		return baseStartPrice;
	}

	public void setBaseStartPrice(String baseStartPrice) {
		this.baseStartPrice = baseStartPrice;
	}

	public String getMoreRisePrice() {
		return moreRisePrice;
	}

	public void setMoreRisePrice(String moreRisePrice) {
		this.moreRisePrice = moreRisePrice;
	}

	public String getBelowRisePrice() {
		return belowRisePrice;
	}

	public void setBelowRisePrice(String belowRisePrice) {
		this.belowRisePrice = belowRisePrice;
	}

	public String getMaxRisePrice() {
		return maxRisePrice;
	}

	public void setMaxRisePrice(String maxRisePrice) {
		this.maxRisePrice = maxRisePrice;
	}

	public String getBiddingTime() {
		return biddingTime;
	}

	public void setBiddingTime(String biddingTime) {
		this.biddingTime = biddingTime;
	}

	public String getBiddingAdditionalTime() {
		return biddingAdditionalTime;
	}

	public void setBiddingAdditionalTime(String biddingAdditionalTime) {
		this.biddingAdditionalTime = biddingAdditionalTime;
	}

	public String getBiddingIntervalTime() {
		return biddingIntervalTime;
	}

	public void setBiddingIntervalTime(String biddingIntervalTime) {
		this.biddingIntervalTime = biddingIntervalTime;
	}

	public String getMaxAutoUpCount() {
		return maxAutoUpCount;
	}

	public void setMaxAutoUpCount(String maxAutoUpCount) {
		this.maxAutoUpCount = maxAutoUpCount;
	}

	public Map<String, String> getSettings() {
		return settings;
	}

	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}

	//TODO getEncodedMessage 확인 , 수정 필요
	@Override
	public String getEncodedMessage() {

		StringBuilder builder = new StringBuilder();

		Iterator<String> keys = getSettings().keySet().iterator();

		while (keys.hasNext()) {
			String key = keys.next();
			builder.append(key);
			builder.append(NETTY_INFO.DELIMITER);
			builder.append(getSettings().get(key));
			if (keys.hasNext()) {
				builder.append(NETTY_INFO.DELIMITER);
			}
		}

		return String.format("%c%c%c%s", ORIGIN, TYPE, NETTY_INFO.DELIMITER, builder.toString());
	}
}
