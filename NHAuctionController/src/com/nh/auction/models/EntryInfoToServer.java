package com.nh.auction.models;

import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 출품 자료 정보 전송, 최초 출품 정보를 서버에 전송 처리
 * - 제어프로그램에서 진행해야 할 출품 자료를 경매 서버에 전송 처리
 * - 제어프로그램으로부터 다건의 정보를 수신 받은 후 데이터를 경매서버에서 관리 처리한다.
 * 
 * 구분자 | 출품번호 | 출품유형 | 개체번호 | 출하주 | 생년월일 | 성별 | 중량 | KPN | 산차 | 어미 | 특이사항 | 경매일시 | 출품상태 | 시작가 | 마지막자료여부(Y/N)
 * ex) CC | 1 | 송아지 | 158407566 | 홍길동 | 2020-11-10 | 수 | 450 | 1151 | 5 | 111006038 | 가슴백반 | 2021-06-14 08:00:00 | 정상 | 370 | N
 */
public class EntryInfoToServer extends EntryInfo {

	public static final char TYPE = 'I';

	public String isLast = "N";	//마지막 자료 여부

	public String getIsLast() {
		return isLast;
	}

	public void setIsLast(String isLast) {
		this.isLast = isLast;
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
				+ "%c%s"
				+ "%c%c",
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
				NETTY_INFO.DELIMITER,getStartPrice(),
				NETTY_INFO.DELIMITER,getIsLast()
				);
	}
}
