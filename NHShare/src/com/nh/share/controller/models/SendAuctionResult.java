package com.nh.share.controller.models;

import com.nh.share.common.models.AuctionResult;
import com.nh.share.controller.interfaces.FromAuctionController;
import com.nh.share.setting.AuctionShareSetting;

/**
 * 경매 설정 변경 처리
 * <p>
 * 제어프로그램 -> 경매서버
 * <p>
 * CF | 조합구분코드 | 출품번호 | 낙/유찰결과코드(11/22/23/24) | 낙찰자회원번호(거래인번호) | 낙찰자경매참가번호 | 낙찰금액
 */
public class SendAuctionResult implements FromAuctionController {
    public static final char TYPE = 'F';
    private String mAuctionHouseCode; // 거점코드
    private String mEntryNum; // 출품 번호
    private String mResultCode; // 낙,유찰 결과 코드
    private String mSuccessBidder; // 낙찰자 회원번호(거래인번호)
    private String mSuccessAuctionJoinNum; // 낙찰자 경매참가번호
    private String mSuccessBidPrice;	//금액 원단위
    private String mSuccessBidUpr;		//금액 만단위
    
    
    private String mEntryType;	// 경매대상구분코드(1 : 송아지 / 2 : 비육우 / 3 : 번식우)
    private String mAucDt;		// 경매일
    private String mLsCmeNo;
	private String mOslpNo; // 원표 번호
	private String mLedSqno; // 원장 일련번호
    

    public SendAuctionResult() {
    }

    public SendAuctionResult(String auctionHouseCode, String entryNum, String resultCode, String successBidder,
                             String successAuctionJoinNum, String successBidPrice) {
        mAuctionHouseCode = auctionHouseCode;
        mEntryNum = entryNum;
        mResultCode = resultCode;
        mSuccessBidder = successBidder;
        mSuccessAuctionJoinNum = successAuctionJoinNum;
        mSuccessBidPrice = successBidPrice;
    }

	public SendAuctionResult(String[] messages) {
		mAuctionHouseCode = messages[1];
		mEntryNum = messages[2];
		mResultCode = messages[3];
		mSuccessBidder = messages[4];
		mSuccessAuctionJoinNum = messages[5];
		mSuccessBidUpr = messages[6];
	}

    public String getAuctionHouseCode() {
        return mAuctionHouseCode;
    }

    public void setAuctionHouseCode(String auctionHouseCode) {
        this.mAuctionHouseCode = auctionHouseCode;
    }

    public String getEntryNum() {
        return mEntryNum;
    }

    public void setEntryNum(String entryNum) {
        this.mEntryNum = entryNum;
    }

    public String getResultCode() {
        return mResultCode;
    }

    public void setResultCode(String resultCode) {
        this.mResultCode = resultCode;
    }

    public String getSuccessBidder() {
        return mSuccessBidder;
    }

    public void setSuccessBidder(String successBidder) {
        this.mSuccessBidder = successBidder;
    }

    public String getSuccessAuctionJoinNum() {
        return mSuccessAuctionJoinNum;
    }

    public void setSuccessAuctionJoinNum(String successAuctionJoinNum) {
        this.mSuccessAuctionJoinNum = successAuctionJoinNum;
    }

    public String getSuccessBidPrice() {
        return mSuccessBidPrice;
    }

    public void setSuccessBidPrice(String successBidPrice) {
        this.mSuccessBidPrice = successBidPrice;
    }

    public AuctionResult getConvertAuctionResult() {
        return new AuctionResult(mAuctionHouseCode, mEntryNum, mResultCode, mSuccessBidder, mSuccessAuctionJoinNum,
        		mSuccessBidUpr);
    }
    
    public String getEntryType() {
		return mEntryType;
	}

	public void setEntryType(String mEntryType) {
		this.mEntryType = mEntryType;
	}
	
	public String getAucDt() {
		return mAucDt;
	}

	public void setAucDt(String mAucDt) {
		this.mAucDt = mAucDt;
	}
	
	public String getLsCmeNo() {
		return mLsCmeNo;
	}

	public void setLsCmeNo(String mLsCmeNo) {
		this.mLsCmeNo = mLsCmeNo;
	}

	public String getSuccessBidUpr() {
		return mSuccessBidUpr;
	}

	public void setSuccessBidUpr(String mSuccessBidUpr) {
		this.mSuccessBidUpr = mSuccessBidUpr;
	}

	public String getOslpNo() {
		return mOslpNo;
	}

	public void setOslpNo(String mOslpNo) {
		this.mOslpNo = mOslpNo;
	}

	public String getLedSqno() {
		return mLedSqno;
	}

	public void setLedSqno(String mLedSqno) {
		this.mLedSqno = mLedSqno;
	}

	@Override
    public String getEncodedMessage() {
        return String.format("%c%c%c%s%c%s%c%s%c%s%c%s%c%s", ORIGIN, TYPE, AuctionShareSetting.DELIMITER,
                mAuctionHouseCode, AuctionShareSetting.DELIMITER, mEntryNum, AuctionShareSetting.DELIMITER, mResultCode,
                AuctionShareSetting.DELIMITER, mSuccessBidder, AuctionShareSetting.DELIMITER, mSuccessAuctionJoinNum,
                AuctionShareSetting.DELIMITER, mSuccessBidUpr);
    }
}
