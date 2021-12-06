package com.nh.controller.model;

import java.io.Serializable;
import java.util.Objects;

import com.nh.share.api.models.BidEntryData;
import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.common.models.Bidding;
import com.nh.share.setting.AuctionShareSetting;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 경매 응찰 처리 기능
 * <p>
 * 응찰기 -> 경매서버 -> 제어
 * <p>
 * AB | 경매거점코드 | 접속채널(ANDROID/IOS/WEB) | 경매회원번호(거래인번호) | 경매참가번호 | 출품번호 |  응찰금액(만원) | 신규응찰여부(Y/N) | 응찰시간(yyyyMMddhhmmssSSS)
 */
public class SpBidding implements FromAuctionCommon, Serializable, Comparable<SpBidding> {

    private static final long serialVersionUID = 1L;

    public static final char TYPE = 'B';

    private StringProperty auctionHouseCode;
    private StringProperty channel;
    private StringProperty userNo; // 경매회원번호
    private StringProperty price;  //응찰가격 단가
    private StringProperty sraSbidAm; // 최종가격
    private StringProperty auctionJoinNum; // 경매참가번호
    private StringProperty entryNum; // 출품번호
    private StringProperty isNewBid;
    private StringProperty biddingTime;

    private BooleanProperty isCancelBidding = new SimpleBooleanProperty(false);

    public SpBidding() {
    }

    public SpBidding(Bidding bidding) {
        auctionHouseCode = new SimpleStringProperty(bidding.getAuctionHouseCode());
        channel = new SimpleStringProperty(bidding.getChannel());
        userNo = new SimpleStringProperty(bidding.getUserNo());
        auctionJoinNum = new SimpleStringProperty(bidding.getAuctionJoinNum());
        entryNum = new SimpleStringProperty(bidding.getEntryNum());
        isNewBid = new SimpleStringProperty(bidding.getIsNewBid());
        biddingTime = new SimpleStringProperty(bidding.getBiddingTime());
        price = new SimpleStringProperty(bidding.getPrice());
        sraSbidAm = new SimpleStringProperty("");
    }
    
    public SpBidding(BidEntryData data) {
    	  auctionHouseCode = new SimpleStringProperty(data.getNA_BZPLC());
          channel = new SimpleStringProperty("");
          userNo = new SimpleStringProperty(Integer.toString(data.getTRMN_AMNNO()));
          auctionJoinNum = new SimpleStringProperty(data.getLVST_AUC_PTC_MN_NO());
          entryNum = new SimpleStringProperty(Integer.toString(data.getBID_NUM()));
          isNewBid = new SimpleStringProperty("");
          biddingTime = new SimpleStringProperty(data.getATDR_DTM());
          price = new SimpleStringProperty(Integer.toString(data.getATDR_AM()));
          sraSbidAm = new SimpleStringProperty("");
    }

    public StringProperty getAuctionHouseCode() {
        return auctionHouseCode;
    }

    public void setAuctionHouseCode(StringProperty auctionHouseCode) {
        this.auctionHouseCode = auctionHouseCode;
    }

    public StringProperty getChannel() {
        return channel;
    }

    public void setChannel(StringProperty channel) {
        this.channel = channel;
    }

    public StringProperty getUserNo() {
        return userNo;
    }

    public void setUserNo(StringProperty userNo) {
        this.userNo = userNo;
    }

    public StringProperty getPrice() {
        return price;
    }

    public void setPrice(StringProperty price) {
        this.price = price;
    }

    public StringProperty getBiddingTime() {
        return biddingTime;
    }
    
    public long getBiddingTimeValue() {
        return Long.parseLong(biddingTime.getValue());
    }

    public void setBiddingTime(StringProperty biddingTime) {
        this.biddingTime = biddingTime;
    }

    public StringProperty getEntryNum() {
        return entryNum;
    }

    public void setEntryNum(StringProperty entryNum) {
        this.entryNum = entryNum;
    }

    public StringProperty getIsNewBid() {
        return isNewBid;
    }

    public void setIsNewBid(StringProperty isNewBid) {
        this.isNewBid = isNewBid;
    }

    public BooleanProperty getIsCancelBidding() {
        return isCancelBidding;
    }

    public StringProperty getAuctionJoinNum() {
        return auctionJoinNum;
    }

    public void setAuctionJoinNum(StringProperty auctionJoinNum) {
        this.auctionJoinNum = auctionJoinNum;
    }

    public void setIsCancelBidding(BooleanProperty isCancelBidding) {
        this.isCancelBidding = isCancelBidding;
    }
	public StringProperty getSraSbidAm() {
		return sraSbidAm;
	}

	public void setSraSbidAm(StringProperty sraSbidAm) {
		this.sraSbidAm = sraSbidAm;
	}

	public int getPriceInt() {

        if (price != null) {
            return Integer.parseInt(price.getValue());
        } else {
            return 0;
        }
    }
    
    public String getBiddingInfoForLog() {
        return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, getAuctionHouseCode().getValue(), AuctionShareSetting.DELIMITER,
                getChannel().getValue(), AuctionShareSetting.DELIMITER, getUserNo().getValue(), AuctionShareSetting.DELIMITER,
                getAuctionJoinNum().getValue(), AuctionShareSetting.DELIMITER, getEntryNum().getValue(), AuctionShareSetting.DELIMITER, getPrice().getValue(),
                AuctionShareSetting.DELIMITER, getIsNewBid().getValue(), AuctionShareSetting.DELIMITER, getBiddingTime().getValue());
    }

    @Override
    public String getEncodedMessage() {
        return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER, getAuctionHouseCode().getValue(),
                AuctionShareSetting.DELIMITER, getChannel().getValue(), AuctionShareSetting.DELIMITER, getUserNo().getValue(), AuctionShareSetting.DELIMITER,
                getAuctionJoinNum().getValue(), AuctionShareSetting.DELIMITER, getEntryNum().getValue(), AuctionShareSetting.DELIMITER, getPrice().getValue(),
                AuctionShareSetting.DELIMITER, getIsNewBid().getValue(), AuctionShareSetting.DELIMITER, getBiddingTime().getValue());
    }

    @Override
    public int compareTo(SpBidding bidding) {
        return this.getUserNo().getValue().compareTo(bidding.getUserNo().getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUserNo().getValue());
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof SpBidding)) {
            return false;
        }

        SpBidding p = (SpBidding) obj;

        if (p.getEntryNum() == null) {
            return false;
        }
        
        if (this.getUserNo() == null || p.getUserNo() == null) {
            return false;
        }
        

        return this.getUserNo().getValue().equals(p.getUserNo().getValue());
    }
}
