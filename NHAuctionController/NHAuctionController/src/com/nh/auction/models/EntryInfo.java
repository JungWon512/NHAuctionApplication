package com.nh.auction.models;

import com.nh.auction.interfaces.FromAuctionController;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 경매 출품 정보
 * - 제어프로그램에서 현재 진행 될 출품 정보를 송신 처리
 * 제어프로그램 -> 서버
 * 
 * 구분자 | 출품번호 | 출품유형 | 개체번호 | 출하주 | 생년월일 | 성별 | 중량 | KPN | 산차 | 어미 | 특이사항 | 경매일시 | 출품상태 | 시작가
 * ex) CC | 1 | 송아지 | 158407566 | 홍길동 | 2020-11-10 | 수 | 450 | 1151 | 5 | 111006038 | 가슴백반 | 2021-06-14 08:00:00 | 정상 | 370
 */
public class EntryInfo implements FromAuctionController {

	public static final char TYPE = 'C';

	private String entryNum;		// 출품번호
	private String entryType;		// 출품유형 송아지 : T0001 / 큰소 : T0002 / 육우 : T0003
	private String indNum;			// 개체번호
	private String exhibitor;		// 출하주
	private String birthday;		// 생년월일
	private String gender;			// 성별 수컷 : G0001 / 암컷 : G0002
	private String weight;			// 중량
	private String kpn;				// KPN
	private String cavingNum;		// 산차
	private String mother;			// 어미
	private String note;			// 특이사항
	private String auctDateTime;	// 경매일시
	private String entryStatus;		// 출품상태 정상 : E0001 / 취소 : E0002
	private String startPrice;		// 시작가

	public String getEntryNum() {
		return entryNum;
	}

	public void setEntryNum(String entryNum) {
		this.entryNum = entryNum;
	}

	public String getEntryType() {
		return entryType;
	}

	public void setEntryType(String entryType) {
		this.entryType = entryType;
	}

	public String getIndNum() {
		return indNum;
	}

	public void setIndNum(String indNum) {
		this.indNum = indNum;
	}

	public String getExhibitor() {
		return exhibitor;
	}

	public void setExhibitor(String exhibitor) {
		this.exhibitor = exhibitor;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getKpn() {
		return kpn;
	}

	public void setKpn(String kpn) {
		this.kpn = kpn;
	}

	public String getCavingNum() {
		return cavingNum;
	}

	public void setCavingNum(String cavingNum) {
		this.cavingNum = cavingNum;
	}

	public String getMother() {
		return mother;
	}

	public void setMother(String mother) {
		this.mother = mother;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getAuctDateTime() {
		return auctDateTime;
	}

	public void setAuctDateTime(String auctDateTime) {
		this.auctDateTime = auctDateTime;
	}

	public String getEntryStatus() {
		return entryStatus;
	}

	public void setEntryStatus(String entryStatus) {
		this.entryStatus = entryStatus;
	}

	public String getStartPrice() {
		return startPrice;
	}

	public void setStartPrice(String startPrice) {
		this.startPrice = startPrice;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s",
				ORIGIN, TYPE, 
				NETTY_INFO.DELIMITER,getEntryNum(),
				NETTY_INFO.DELIMITER,getEntryType(),
				NETTY_INFO.DELIMITER,getIndNum(),
				NETTY_INFO.DELIMITER,getExhibitor(),
				NETTY_INFO.DELIMITER,getBirthday(),
				NETTY_INFO.DELIMITER,getGender(),
				NETTY_INFO.DELIMITER,getWeight(),
				NETTY_INFO.DELIMITER,getKpn(),
				NETTY_INFO.DELIMITER,getCavingNum(),
				NETTY_INFO.DELIMITER,getMother(),
				NETTY_INFO.DELIMITER,getNote(),
				NETTY_INFO.DELIMITER,getAuctDateTime(),
				NETTY_INFO.DELIMITER,getEntryStatus(),
				NETTY_INFO.DELIMITER,getStartPrice()
				);
	}
}
