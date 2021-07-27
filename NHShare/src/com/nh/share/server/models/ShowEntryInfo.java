package com.nh.share.server.models;

import com.nh.share.controller.models.EditSetting;
import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 출품 정보 노출 설정 전송
 * 
 * 경매서버 -> 응찰단말
 * 
 * SH | 조합구분코드 | 노출항목1 | 노출항목2 | 노출항목3 | 노출항목4 | 노출항목5 | 노출항목6 | 노출항목7 | 노출항목8
 * 
 * 1.출품번호 / 2.출하주 / 3.성별 / 4.중량 / 5.어미 / 6.계대 / 7.산차 / 8.KPN / 9.지역명 / 10.비고 / 11.최처가 / 12.친자여부
 *
 */
public class ShowEntryInfo implements FromAuctionServer {
	public static final char TYPE = 'H';

	final private String ITEM_ENTRY_NUM = "1";
	final private String ITEM_EXH_USER = "2";
	final private String ITEM_GENDER = "3";
	final private String ITEM_WEIGHT = "4";
	final private String ITEM_MOTHER = "5";
	final private String ITEM_PASG = "6";
	final private String ITEM_CAVING = "7";
	final private String ITEM_KPN = "8";
	final private String ITEM_LOCATION = "9";
	final private String ITEM_NOTE = "10";
	final private String ITEM_LOW_PRICE = "11";
	final private String ITEM_DNA = "12";
	
	private String mAuctionHouseCode; // 거점코드
	private String mItem1; // 1번째 항목
	private String mItem2; // 2번째 항목
	private String mItem3; // 3번째 항목
	private String mItem4; // 4번째 항목
	private String mItem5; // 5번째 항목
	private String mItem6; // 6번째 항목
	private String mItem7; // 7번째 항목
	private String mItem8; // 8번째 항목
	private int currentItem = 0;

	public ShowEntryInfo(EditSetting editSetting) {
		currentItem = 0;
		
		if(editSetting.getIsShowEntryNum().equals("Y")) {
			currentItem++;
			setData(ITEM_ENTRY_NUM);
		}
		
		if(editSetting.getIsShowExhUser().equals("Y")) {
			currentItem++;
			setData(ITEM_EXH_USER);
		}
		
		if(editSetting.getIsShowGender().equals("Y")) {
			currentItem++;
			setData(ITEM_GENDER);
		}
		
		if(editSetting.getIsShowWeight().equals("Y")) {
			currentItem++;
			setData(ITEM_WEIGHT);
		}
		
		if(editSetting.getIsShowMother().equals("Y")) {
			currentItem++;
			setData(ITEM_MOTHER);
		
		}
		
		if(editSetting.getIsShowPasg().equals("Y")) {
			currentItem++;
			setData(ITEM_PASG);
		}
		
		if(editSetting.getIsShowCaving().equals("Y")) {
			currentItem++;
			setData(ITEM_CAVING);
		}
		
		if(editSetting.getIsShowKpn().equals("Y")) {
			currentItem++;
			setData(ITEM_KPN);
		}
		
		if(editSetting.getIsShowLocation().equals("Y")) {
			currentItem++;
			setData(ITEM_LOCATION);
		}
		
		if(editSetting.getIsShowNote().equals("Y")) {
			currentItem++;
			setData(ITEM_NOTE);
		}
		
		if(editSetting.getIsShowLowPrice().equals("Y")) {
			currentItem++;
			setData(ITEM_LOW_PRICE);
		}
		
		if(editSetting.getIsShowDna().equals("Y")) {
			currentItem++;
			setData(ITEM_DNA);
		}
	}

	public ShowEntryInfo(String auctionHouseCode, String item1, String item2, String item3, String item4, String item5,
			String item6, String item7, String item8) {
		mAuctionHouseCode = auctionHouseCode;
		mItem1 = item1;
		mItem2 = item2;
		mItem3 = item3;
		mItem4 = item4;
		mItem5 = item5;
		mItem6 = item6;
		mItem7 = item7;
		mItem8 = item8;
	}

	private void setData(String item) {
		switch(currentItem) {
		case 1:
			mItem1 = item;
			break;
		case 2:
			mItem2 = item;
			break;
		case 3:
			mItem3 = item;
			break;
		case 4:
			mItem4 = item;
			break;
		case 5:
			mItem5 = item;
			break;
		case 6:
			mItem6 = item;
			break;
		case 7:
			mItem7 = item;
			break;
		case 8:
			mItem8 = item;
			break;
		}
	}
	
	public String getAuctionHouseCode() {
		return mAuctionHouseCode;
	}

	public void setAuctionHouseCode(String auctionHouseCode) {
		this.mAuctionHouseCode = auctionHouseCode;
	}

	public String getItem1() {
		return mItem1;
	}

	public void setItem1(String item1) {
		this.mItem1 = item1;
	}

	public String getItem2() {
		return mItem2;
	}

	public void setItem2(String item2) {
		this.mItem2 = item2;
	}

	public String getItem3() {
		return mItem3;
	}

	public void setItem3(String item3) {
		this.mItem3 = item3;
	}

	public String getItem4() {
		return mItem4;
	}

	public void setItem4(String item4) {
		this.mItem4 = item4;
	}

	public String getItem5() {
		return mItem5;
	}

	public void setItem5(String item5) {
		this.mItem5 = item5;
	}

	public String getItem6() {
		return mItem6;
	}

	public void setItem6(String item6) {
		this.mItem6 = item6;
	}

	public String getItem7() {
		return mItem7;
	}

	public void setItem7(String item7) {
		this.mItem7 = item7;
	}

	public String getItem8() {
		return mItem8;
	}

	public void setItem8(String item8) {
		this.mItem8 = item8;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER,
				mAuctionHouseCode, AuctionShareSetting.DELIMITER, mItem1, AuctionShareSetting.DELIMITER, mItem2,
				AuctionShareSetting.DELIMITER, mItem3, AuctionShareSetting.DELIMITER, mItem4,
				AuctionShareSetting.DELIMITER, mItem5, AuctionShareSetting.DELIMITER, mItem6,
				AuctionShareSetting.DELIMITER, mItem7, AuctionShareSetting.DELIMITER, mItem8);
	}

}