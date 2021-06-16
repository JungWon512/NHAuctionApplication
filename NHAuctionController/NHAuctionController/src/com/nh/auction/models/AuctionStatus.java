package com.nh.auction.models;

import java.io.Serializable;

import com.nh.auction.interfaces.FromCommon;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 경매 상태 정보 전송 기능
 * - 경매 상태가 변경될 경우 해당 상태를 각 Client에 전송 처리한다.
 * - 상태 변경 시점 기준으로 다음 응찰 가격 정보 및 순위 정로를 전송 처리한다.
 * - 경매 진행 상태는 기본값 / 대기 / 시작 / 진행 / 경쟁 / 낙찰 / 유찰 / 종료로 구분 처리
 * 
 * 구분자 | 출품번호 | 현재가 | 현재응찰자수 | 경매상태(NONE / READY / START / PROGRESS / COMPETITIVE / SUCCESS / FAIL / STOP / COMPLETED / FINISH) | 1순위회원번호 | 2순위회원번호| 3순위회원번호 | 경매진행완료출품수 | 경매잔여출품수
 * ex) AS | 1 | 450 | 50 | 8005 | 341597 | 458563 | 124587 | 100 | 200
 *
 */
public class AuctionStatus implements FromCommon, Serializable {

	private static final long serialVersionUID = 1L;

	public static final char TYPE = 'S';

	private String entryNum;				// 출품번호
	private String currentPrice;			// 현재가
	private String currentBidderCount;		// 현재 응찰자 수
	private String state;					// 경매상태 (NONE / READY / START / PROGRESS / COMPETITIVE / SUCCESS / FAIL / STOP / COMPLETED / FINISH)
	private String rank1MemberNum;			// 1순위 회원번호
	private String rank2MemberNum;			// 2순위 회원번호
	private String rank3MemberNum;			// 3순위 회원번호
	private String finishEntryCount;		// 경매 진행 완료 출품수
	private String remainEntryCount;		// 경매 잔여 출품수

	
	public String getEntryNum() {
		return entryNum;
	}
	public void setEntryNum(String entryNum) {
		this.entryNum = entryNum;
	}
	public String getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(String currentPrice) {
		this.currentPrice = currentPrice;
	}
	public String getCurrentBidderCount() {
		return currentBidderCount;
	}
	public void setCurrentBidderCount(String currentBidderCount) {
		this.currentBidderCount = currentBidderCount;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getRank1MemberNum() {
		return rank1MemberNum;
	}
	public void setRank1MemberNum(String rank1MemberNum) {
		this.rank1MemberNum = rank1MemberNum;
	}
	public String getRank2MemberNum() {
		return rank2MemberNum;
	}
	public void setRank2MemberNum(String rank2MemberNum) {
		this.rank2MemberNum = rank2MemberNum;
	}
	public String getRank3MemberNum() {
		return rank3MemberNum;
	}
	public void setRank3MemberNum(String rank3MemberNum) {
		this.rank3MemberNum = rank3MemberNum;
	}
	public String getFinishEntryCount() {
		return finishEntryCount;
	}
	public void setFinishEntryCount(String finishEntryCount) {
		this.finishEntryCount = finishEntryCount;
	}
	public String getRemainEntryCount() {
		return remainEntryCount;
	}
	public void setRemainEntryCount(String remainEntryCount) {
		this.remainEntryCount = remainEntryCount;
	}

	@Override
	public String getEncodedMessage() {
		return String.format("%c%c"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s"
				+ "%c%s",
				ORIGIN, TYPE, 
				NETTY_INFO.DELIMITER,getEntryNum(),
				NETTY_INFO.DELIMITER,getCurrentPrice(),
				NETTY_INFO.DELIMITER,getCurrentBidderCount(),
				NETTY_INFO.DELIMITER,getState(),
				NETTY_INFO.DELIMITER,getRank1MemberNum(),
				NETTY_INFO.DELIMITER,getRank2MemberNum(),
				NETTY_INFO.DELIMITER,getRank3MemberNum(),
				NETTY_INFO.DELIMITER,getFinishEntryCount(),
				NETTY_INFO.DELIMITER,getRemainEntryCount()
				);
	}
}
