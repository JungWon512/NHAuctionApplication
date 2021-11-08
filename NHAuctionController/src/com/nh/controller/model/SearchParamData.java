package com.nh.controller.model;

import com.nh.share.controller.models.EntryInfo;
/**
 * 출품 검색 파라미터
 */
public class SearchParamData extends EntryInfo{

	private String auctionResultParam;

	public String getAuctionResultParam() {
		return auctionResultParam;
	}

	public void setAuctionResultParam(String auctionResultParam) {
		this.auctionResultParam = auctionResultParam;
	}

	
}
