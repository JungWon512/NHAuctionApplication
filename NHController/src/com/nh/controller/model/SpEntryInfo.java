package com.nh.controller.model;

import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.server.interfaces.FromAuctionServer;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.setting.AuctionShareSetting;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 현재 출품 정보 전송
 * 
 * 경매서버 -> 공통
 * 
 * SC|출품번호|출품유형|개체번호|출하주|생년월일|성별|중량|KPN|산차|어미|특이사항|경매일시|출품상태|시작가
 *
 */
public class SpEntryInfo implements FromAuctionController {
	
	public static final char TYPE = 'I';
	
	private StringProperty auctionHouseCode; // 거점코드
	private StringProperty entryNum; // 출품 번호
	private StringProperty entryType; // 출품 유형(큰소/송아지/육우)
	private StringProperty indNum; // 개체번호
	private StringProperty exhibitor; // 출하주
	private StringProperty birthday; // 생년월일
	private StringProperty gender; // 성별
	private StringProperty weight; // 중량
	private StringProperty kpn; // KPN
	private StringProperty cavingNum; // 산차
	private StringProperty mother; // 어미 혈통
	private StringProperty note; // 특이사항
	private StringProperty auctDateTime; // 경매일시
	private StringProperty entryStatus; // 출품상태
	private StringProperty startPrice; // 시작가
	private StringProperty isLastEntry; // 마지막 출품 여부
	
	private StringProperty successfulBidder;	// 낙찰자
	private StringProperty BiddingResult; 		// 낙찰결과
	private BooleanProperty isPending; 			// 보류
	
	
	public SpEntryInfo() {}

	public SpEntryInfo(String auctionHouseCode, String entryNum, String entryType, String indNum, String exhibitor,
			String birthday, String gender, String weight, String kpn, String cavingNum, String mother, String note,
			String auctDateTime, String entryStatus, String startPrice, String isLastEntry) {
		this.auctionHouseCode = new SimpleStringProperty(auctionHouseCode);
		this.entryNum = new SimpleStringProperty(entryNum);
		this.entryType = new SimpleStringProperty(entryType);
		this.indNum = new SimpleStringProperty(indNum);
		this.exhibitor = new SimpleStringProperty(exhibitor);
		this.birthday = new SimpleStringProperty(birthday);
		this.gender = new SimpleStringProperty(gender);
		this.weight = new SimpleStringProperty(weight);
		this.kpn = new SimpleStringProperty(kpn);
		this.cavingNum = new SimpleStringProperty(cavingNum);
		this.mother = new SimpleStringProperty(mother);
		this.note = new SimpleStringProperty(note);
		this.auctDateTime = new SimpleStringProperty(auctDateTime);
		this.entryStatus = new SimpleStringProperty(entryStatus);
		this.startPrice = new SimpleStringProperty(startPrice);
		this.isLastEntry = new SimpleStringProperty(isLastEntry);
	}
	
	

	public StringProperty getAuctionHouseCode() {
		return auctionHouseCode;
	}

	public void setAuctionHouseCode(StringProperty auctionHouseCode) {
		this.auctionHouseCode = auctionHouseCode;
	}

	public StringProperty getEntryNum() {
		return entryNum;
	}

	public void setEntryNum(StringProperty entryNum) {
		this.entryNum = entryNum;
	}

	public StringProperty getEntryType() {
		return entryType;
	}

	public void setEntryType(StringProperty entryType) {
		this.entryType = entryType;
	}

	public StringProperty getIndNum() {
		return indNum;
	}

	public void setIndNum(StringProperty indNum) {
		this.indNum = indNum;
	}

	public StringProperty getExhibitor() {
		return exhibitor;
	}

	public void setExhibitor(StringProperty exhibitor) {
		this.exhibitor = exhibitor;
	}

	public StringProperty getBirthday() {
		return birthday;
	}

	public void setBirthday(StringProperty birthday) {
		this.birthday = birthday;
	}

	public StringProperty getGender() {
		return gender;
	}

	public void setGender(StringProperty gender) {
		this.gender = gender;
	}

	public StringProperty getWeight() {
		return weight;
	}

	public void setWeight(StringProperty weight) {
		this.weight = weight;
	}

	public StringProperty getKpn() {
		return kpn;
	}

	public void setKpn(StringProperty kpn) {
		this.kpn = kpn;
	}

	public StringProperty getCavingNum() {
		return cavingNum;
	}

	public void setCavingNum(StringProperty cavingNum) {
		this.cavingNum = cavingNum;
	}

	public StringProperty getMother() {
		return mother;
	}

	public void setMother(StringProperty mother) {
		this.mother = mother;
	}

	public StringProperty getNote() {
		return note;
	}

	public void setNote(StringProperty note) {
		this.note = note;
	}

	public StringProperty getAuctDateTime() {
		return auctDateTime;
	}

	public void setAuctDateTime(StringProperty auctDateTime) {
		this.auctDateTime = auctDateTime;
	}

	public StringProperty getEntryStatus() {
		return entryStatus;
	}

	public void setEntryStatus(StringProperty entryStatus) {
		this.entryStatus = entryStatus;
	}

	public StringProperty getStartPrice() {
		return startPrice;
	}

	public void setStartPrice(StringProperty startPrice) {
		this.startPrice = startPrice;
	}

	public StringProperty getIsLastEntry() {
		return isLastEntry;
	}

	public void setIsLastEntry(StringProperty isLastEntry) {
		this.isLastEntry = isLastEntry;
	}

	
	public StringProperty getSuccessfulBidder() {
		return successfulBidder;
	}

	public void setSuccessfulBidder(StringProperty successfulBidder) {
		this.successfulBidder = successfulBidder;
	}

	
	public StringProperty getBiddingResult() {
		return BiddingResult;
	}

	public void setBiddingResult(StringProperty biddingResult) {
		BiddingResult = biddingResult;
	}
	

	public BooleanProperty getIsPending() {
		return isPending;
	}

	public void setIsPending(BooleanProperty isPending) {
		this.isPending = isPending;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE,
				AuctionShareSetting.DELIMITER, getAuctionHouseCode().getValue(), AuctionShareSetting.DELIMITER, getEntryNum().getValue(),
				AuctionShareSetting.DELIMITER, getEntryType().getValue(), AuctionShareSetting.DELIMITER, getIndNum().getValue(),
				AuctionShareSetting.DELIMITER, getExhibitor().getValue(), AuctionShareSetting.DELIMITER, getBirthday().getValue(),
				AuctionShareSetting.DELIMITER, getGender().getValue(), AuctionShareSetting.DELIMITER, getWeight().getValue(),
				AuctionShareSetting.DELIMITER, getKpn().getValue(), AuctionShareSetting.DELIMITER, getCavingNum().getValue(),
				AuctionShareSetting.DELIMITER, getMother().getValue(), AuctionShareSetting.DELIMITER, getNote().getValue(),
				AuctionShareSetting.DELIMITER, getAuctDateTime().getValue(), AuctionShareSetting.DELIMITER, getEntryStatus().getValue(),
				AuctionShareSetting.DELIMITER, getStartPrice().getValue(), AuctionShareSetting.DELIMITER, getIsLastEntry().getValue());
	}

}
