package com.nh.auction.models;

import com.nh.auction.interfaces.FromAuctionServer;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

/**
 * 차량 정보 전송
 * 
 * 서버 -> 공통
 * 
 * SR|상품번호|상품명
 *
 */
public class TestDataInfo implements FromAuctionServer {

	public static final char TYPE = 'C';

	private String productCode;		// 상품 번호

	private String name; 			// 츌품명

	public TestDataInfo() {
	}

	
	public String getProductCode() {
		return productCode;
	}


	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public static char getType() {
		return TYPE;
	}

	@Override
	public String getEncodedMessage() {
		return String.format(
				"%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s",
				ORIGIN, TYPE, NETTY_INFO.DELIMITER, productCode, NETTY_INFO.DELIMITER, name);
	}

}
