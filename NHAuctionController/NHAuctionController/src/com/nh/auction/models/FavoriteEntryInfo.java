package com.nh.auction.models;

import com.nh.auction.interfaces.FromCommon;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 관심 출품 상품 여부 전송
 * - 각 회원 정보 기반으로 경매 서버가 관심 출품 정보를 전송 처리한다.
 * - 경매 회원 정보 기반으로 조회된 관심 출품 정보를 전송
 * 
 * 구분자 | 출품번호 | 관심 출품 상품 여부(Y/N)
 * ex) AF | 1 | Y
 *
 */
public class FavoriteEntryInfo implements FromCommon {

	public static final char TYPE = 'F';

	private String entryNum;			// 출품번호
	private String flagFavorite;		// 관심 출품 상품 여부(Y/N)

	public FavoriteEntryInfo(String entryNum,String flagFavorite) {
		this.entryNum = entryNum;
		this.flagFavorite = flagFavorite;
	}

	public String getEntryNum() {
		return entryNum;
	}

	public void setEntryNum(String entryNum) {
		this.entryNum = entryNum;
	}

	public String getFlagFavorite() {
		return flagFavorite;
	}

	public void setFlagFavorite(String flagFavorite) {
		this.flagFavorite = flagFavorite;
	}


	@Override
	public String getEncodedMessage() {
		return String.format("%c%c"
				+ "%c%s"
				+ "%c%s",
				ORIGIN, TYPE, 
				NETTY_INFO.DELIMITER,getEntryNum(),
				NETTY_INFO.DELIMITER,getFlagFavorite()
				);
	}
}
