package com.nh.controller.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import com.nh.controller.netty.BillboardDelegate;
import com.nh.controller.utils.GlobalDefine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.controller.model.AucEntrData;
import com.nh.controller.model.AuctionRound;
import com.nh.controller.model.SpBidding;
import com.nh.controller.model.SpEntryInfo;
import com.nh.controller.model.UserInfo;
import com.nh.controller.netty.AuctionDelegate;
import com.nh.controller.service.ConnectionInfoMapperService;
import com.nh.controller.service.EntryInfoMapperService;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine.FILE_INFO;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.AuctionResult;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.CancelBidding;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.controller.models.EntryInfo;
import com.nh.share.controller.models.SendAuctionResult;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.RequestAuctionResult;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.ToastMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class BaseAuctionController implements NettyControllable {

    protected Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    protected ObservableList<SpEntryInfo> mWaitEntryInfoDataList = FXCollections.observableArrayList(); // 대기중 출품
    protected ObservableList<SpEntryInfo> mFinishedEntryInfoDataList = FXCollections.observableArrayList(); // 끝난 출품
    protected ObservableList<SpBidding> mBiddingUserInfoDataList = FXCollections.observableArrayList(); // 응찰 현황
    protected ObservableList<SpEntryInfo> mConnectionUserDataList = FXCollections.observableArrayList(); // 접속자 현황
    protected ObservableList<SpEntryInfo> mDummyRow = FXCollections.observableArrayList(); // dummy row
    protected int DUMMY_ROW_WAIT = 8;
    protected int DUMMY_ROW_FINISHED = 4;
    protected int mRecordCount = 0; // cow total data count

    protected ResourceBundle mResMsg = null; // 메세지 처리

    protected FXMLLoader mFxmlLoader = null;

    protected Stage mStage = null; // 현재 Stage

    protected AuctionRound auctionRound = null; // 경매 회차 정보

    protected SpEntryInfo mCurrentSpEntryInfo = null; // 현재 진행 출품
    
    protected SpBidding mRank_1_User = null; // 낙찰 될 1순위 회원

    protected AuctionStatus mAuctionStatus = null; // 경매 상태

    protected LinkedHashMap<String, SpBidding> mCurrentBidderMap = null; // 응찰자 정보 수집 Map

    protected List<SpBidding> mBeForeBidderDataList = null; // 이전 응찰한 응찰자 정보 수집 List

    protected List<SpBidding> mReAuctionBidderDataList = null; // 재경매 응찰자 목록
	
    protected int mReAuctionCount = -1; //동일가 설정 횟수

    protected boolean mIsPass = false;
    
    protected boolean isAuctionComplete = false;
    
    protected boolean isCountDownRunning = false;	//카운트다운 실행 여부
    
    public BaseAuctionController() {
        init();
    }

    /**
     * 초기화 작업
     */
    protected void init() {
        mAuctionStatus = new AuctionStatus();
        mBeForeBidderDataList = new ArrayList<SpBidding>();
        mCurrentBidderMap = new LinkedHashMap<String, SpBidding>();
        mReAuctionBidderDataList = new ArrayList<SpBidding>();
        AuctionDelegate.getInstance().setClearVariable();
    }

    /**
     * 경매 상태
     *
     * @param statusCode
     */
    protected void setAuctionStatus(String statusCode) {
        mAuctionStatus.setState(statusCode);
    }

    /**
     * 소켓 서버 접속
     */
    protected void createClient(String host, int port, String userMemNum, String watchMode) {
        AuctionDelegate.getInstance().createClients(host, port, userMemNum, watchMode, this);
        BillboardDelegate.getInstance().createClients("192.168.0.18", 9881, this); // UDP TEST !!!
    }

    @Override
    public void onActiveChannel(Channel channel) {
        mLogger.debug("onActiveChannel");
        // 제어프로그램 접속
        addLogItem(mResMsg.getString("msg.auction.send.connection.info") + AuctionDelegate.getInstance().onSendConnectionInfo());
    }

    @Override
    public void onActiveChannel() {
        mLogger.debug("onActiveChannel");
    }

    @Override
    public void onAuctionStatus(AuctionStatus auctionStatus) {

        // AUCTION_STATUS_NONE = "8001" // 출품 자료 이관 전 상태
        // AUCTION_STATUS_READY = "8002" // 경매 준비 상태
        // AUCTION_STATUS_START = "8003" // 경매 시작 상태
        // AUCTION_STATUS_PROGRESS = "8004" // 경매 진행 상태
        // AUCTION_STATUS_PASS = "8005" // 경매 출품 건 강제 유찰
        // AUCTION_STATUS_COMPLETED = "8006" // 경매 출품 건 완료 상태
        // AUCTION_STATUS_FINISH = "8007" // 경매 종료 상태

        mAuctionStatus = auctionStatus;

        switch (auctionStatus.getState()) {
            case GlobalDefineCode.AUCTION_STATUS_NONE:
                addLogItem(mResMsg.getString("msg.auction.status.none"));
                break;
            case GlobalDefineCode.AUCTION_STATUS_READY:
                addLogItem(String.format(mResMsg.getString("msg.auction.status.ready"), auctionStatus.getEntryNum()));
                break;
            case GlobalDefineCode.AUCTION_STATUS_START:
                addLogItem(String.format(mResMsg.getString("msg.auction.status.start"), auctionStatus.getEntryNum()));
                break;
            case GlobalDefineCode.AUCTION_STATUS_PROGRESS:
                addLogItem(String.format(mResMsg.getString("msg.auction.status.progress"), auctionStatus.getEntryNum()));
                break;
            case GlobalDefineCode.AUCTION_STATUS_PASS:
                addLogItem(String.format(mResMsg.getString("msg.auction.status.pass"), auctionStatus.getEntryNum()));
                break;
            case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
                addLogItem(String.format(mResMsg.getString("msg.auction.status.completed"), auctionStatus.getEntryNum()));
                break;
            case GlobalDefineCode.AUCTION_STATUS_FINISH:
                addLogItem(mResMsg.getString("msg.auction.status.finish"));
                break;
        }

    }

    @Override
    public void onCurrentEntryInfo(CurrentEntryInfo currentEntryInfo) {
        addLogItem(mResMsg.getString("msg.auction.get.current.entry.data") + currentEntryInfo.getEncodedMessage());
    }

    @Override
    public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
        mLogger.debug("onAuctionCountDown : " + auctionCountDown.getEncodedMessage());

        if (auctionCountDown.getStatus().equals(GlobalDefineCode.AUCTION_COUNT_DOWN)) {
            String msg = String.format(mResMsg.getString("msg.auction.get.count.down"), mCurrentSpEntryInfo.getEntryNum().getValue(), auctionCountDown.getCountDownTime());
            addLogItem(msg + auctionCountDown.getEncodedMessage());
        }

    }

    @Override
    public void onBidding(Bidding bidding) {
        addLogItem(mResMsg.getString("msg.auction.get.bidding") + bidding.getEncodedMessage());

        if (bidding.getAuctionJoinNum() == null || bidding.getPrice() == null
                || bidding.getAuctionJoinNum().equals("") || bidding.getPrice().equals("")) {
            return;
        }
        
        //현재 경매 진행중인 출품 번호가 아니면 return..
        if(!mCurrentSpEntryInfo.getEntryNum().getValue().equals(bidding.getEntryNum())) {
        	return;
        }
        
        //재경매 상황시 목록에 있는 사람만 정보 받음.
        if(SettingApplication.getInstance().isUseReAuction() && mReAuctionCount > 0 && !CommonUtils.getInstance().isListEmpty(mReAuctionBidderDataList)) {
        	
        	boolean isbidding = false;
        	for(SpBidding bidder : mReAuctionBidderDataList) {
        		//재경매 목록에 있는 응찰자만 받음. 
        		if(bidder.getAuctionJoinNum().getValue().equals(bidding.getAuctionJoinNum())) {
        			//최저가와 같거나 크고, 응찰된 금액이랑 같지 않을 경우 응찰 내역 저장

        			if((mCurrentBidderMap.get(bidding.getAuctionJoinNum()).getPriceInt() != bidding.getPriceInt()) && (mCurrentSpEntryInfo.getLowPriceInt() <= bidding.getPriceInt())) {
        				mLogger.debug("[onBidding] 재경매 응찰 저장 : " + bidding.getPriceInt());
        				setBidding(bidding);
        				isbidding = true;
        			}
        			break;
        		}
        	}
        	
        	if(isbidding) {
        		addLogItem("재경매시 응찰 됨 " + bidding.getAuctionJoinNum());
        	}else {
        		addLogItem("재경매시 응찰 안 됨 " + bidding.getAuctionJoinNum());
        	}
        	
        	
        }else {
        	//재경매 상황이 아니면 응찰 set
        	setBidding(bidding);
        }

        calculationRanking();
    }

    @Override
    public void onToastMessage(ToastMessage toastMessage) {
        mLogger.debug("onToastMessage : " + toastMessage.getEncodedMessage());
        addLogItem(toastMessage.getEncodedMessage());
    }

    @Override
    public void onFavoriteEntryInfo(FavoriteEntryInfo favoriteEntryInfo) {
        mLogger.debug("onFavoriteEntryInfo : " + favoriteEntryInfo.getEncodedMessage());
        addLogItem(favoriteEntryInfo.getEncodedMessage());
    }

    @Override
    public void onConnectionInfo(ConnectionInfo connectionInfo) {
        mLogger.debug("onConnectionInfo : " + connectionInfo.getEncodedMessage());
        String auctionHouseCode = connectionInfo.getAuctionHouseCode();
        String userMemNum = connectionInfo.getUserMemNum();
        String userNum;
        // 접속자 정보
        ConnectionInfoMapperService service = new ConnectionInfoMapperService();
        userNum = service.selectConnectionInfo(
                auctionHouseCode,
                CommonUtils.getInstance().getCurrentTime("yyyyMMdd"),
                String.valueOf(auctionRound.getAucObjDsc()),
                userMemNum
        ); // 실세사용
//        userNum = service.selectConnectionInfo(auctionHouseCode, "20210702", "3", userMemNum); // 테스트
        mLogger.debug("onConnectionInfo - userNum :\t" + userNum);

        if (userNum == null || userNum.isEmpty()) {
//          no user selected => insert DB
            mLogger.debug("no user selected.");

            List<UserInfo> userInfo = service.convertConnectionInfo(connectionInfo);
            service.insertConnectionInfo(userInfo);
            userNum = service.getUserNum();
        }

        // 서버 전송
        mLogger.debug(AuctionDelegate.getInstance().onSendConnectionInfo(
                new ResponseConnectionInfo(auctionHouseCode, GlobalDefineCode.CONNECT_SUCCESS, userMemNum, userNum))
        );
    }

    @Override
    public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo) {
        mLogger.debug("onResponseConnectionInfo : " + responseConnectionInfo.getEncodedMessage());
    }

    @Override
    public void onResponseCode(ResponseCode responseCode) {
        mLogger.debug("onResponseCode : " + responseCode.getEncodedMessage());

        String msg = "";

        switch (responseCode.getResponseCode()) {
            case GlobalDefineCode.RESPONSE_REQUEST_NOT_RESULT:
                msg = mResMsg.getString("msg.auction.response.code.not.result");
                break;
            case GlobalDefineCode.RESPONSE_REQUEST_FAIL:
                msg = mResMsg.getString("msg.auction.response.code.fail");
                break;
            case GlobalDefineCode.RESPONSE_REQUEST_BIDDING_LOW_PRICE:
                msg = mResMsg.getString("msg.auction.response.code.bidding.low.price");
                break;
            case GlobalDefineCode.RESPONSE_NOT_TRANSMISSION_ENTRY_INFO:
                msg = mResMsg.getString("msg.auction.response.code.not.transmission.entry.info");
                // 출품 이관 전 상태
                setAuctionStatus(GlobalDefineCode.AUCTION_STATUS_NONE);
                break;
        }

        addLogItem(msg + responseCode.getEncodedMessage());
    }

    @Override
    public void onCancelBidding(CancelBidding cancelBidding) {

        addLogItem(mResMsg.getString("msg.auction.get.bidding.cancel") + cancelBidding.getEncodedMessage());

        if (mCurrentBidderMap.containsKey(cancelBidding.getAuctionJoinNum())) {
            SpBidding currentBidder = mCurrentBidderMap.get(cancelBidding.getAuctionJoinNum());
            currentBidder.setIsCancelBidding(new SimpleBooleanProperty(true));
            mBeForeBidderDataList.add(currentBidder);
            mCurrentBidderMap.remove(cancelBidding.getAuctionJoinNum());
        }

        calculationRanking();
    }

    @Override
    public void onRequestAuctionResult(RequestAuctionResult requestAuctionResult) {

//        if (mWaitEntryInfoDataList != null && mWaitEntryInfoDataList.size() > 0) {
//
//            if (mCurrentSpEntryInfo != null && mCurrentSpEntryInfo.getEntryNum().getValue().equals(requestAuctionResult.getEntryNum())) {
//
//                SpEntryInfo entryInfo = mCurrentSpEntryInfo;
//
//                if (entryInfo != null) {
//
//                    if (mIsPass) {
//                        mCurrentSpEntryInfo.getAuctionResult().setValue(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING);
//                        calculationRankingAndLog(entryInfo, true);
//                    } else {
//                        // 낙유찰 결과 전송
//                        switch (mAuctionStatus.getState()) {
//                            case GlobalDefineCode.AUCTION_STATUS_PASS:
//                                calculationRankingAndLog(entryInfo, true);
//                                break;
//                            case GlobalDefineCode.AUCTION_STATUS_COMPLETED:
//                                calculationRankingAndLog(entryInfo, false);
//                                break;
//                        }
//                    }
//
//                }
//            }
//        }
    }

    @Override
    public void onCheckSession(ChannelHandlerContext ctx, AuctionCheckSession auctionCheckSession) {
        AuctionDelegate.getInstance().onSendCheckSession();
//		addLogItem(mResMsg.getString("msg.auction.send.session") + AuctionDelegate.getInstance().onSendCheckSession());
    }

    @Override
    public void onAuctionResult(AuctionResult auctionResult) {
    }

    @Override
    public void onConnectionException(int port) {
        Platform.runLater(() -> {
            addLogItem(mResMsg.getString("msg.connection.fail"));
            CommonUtils.getInstance().dismissLoadingDialog();
            showAlertPopupOneButton(mResMsg.getString("msg.connection.fail"));
        });
    }

    @Override
    public void onChannelInactive(int port) {
        mLogger.debug("onChannelInactive : " + port);
        addLogItem(mResMsg.getString("msg.disconnection"));
        Platform.runLater(() -> showAlertPopupOneButton(mResMsg.getString("msg.disconnection")));
    }

    @Override
    public void exceptionCaught(int port) {
        mLogger.debug("exceptionCaught : " + port);
        addLogItem("exceptionCaught : " + port);
    }

    /**
     * 현재 가격 기준 모든 응찰 정보 수집 중복 응찰 정보는 수집 대상에서 제외 처리
     *
     * @param bidding
     */
    public synchronized void setBidding(Bidding bidding) {

        SpBidding spBidding = new SpBidding(bidding);

        if (mCurrentBidderMap.containsKey(spBidding.getAuctionJoinNum().getValue())) {

            SpBidding beforeBidder = mCurrentBidderMap.get(spBidding.getAuctionJoinNum().getValue());
            
            if(beforeBidder.getPriceInt() != spBidding.getPriceInt()) {
            	   // 이전 응찰 내역에 저장
                mBeForeBidderDataList.add(beforeBidder);
                // 현재 응찰에 저장
                mCurrentBidderMap.put(spBidding.getAuctionJoinNum().getValue(), spBidding);
            }else {
            	addLogItem("==== 이전가 입력. ====");
            }
        } else {
            mCurrentBidderMap.put(spBidding.getAuctionJoinNum().getValue(), spBidding);
        }

        //응찰 로그 저장
        // TODO: OSLP_NO, RG_SQNO 확인 및 수정
        AucEntrData aucEntrData = new AucEntrData();
        aucEntrData.setNaBzplc(this.auctionRound.getNaBzplc());
        aucEntrData.setAucObjDsc(this.auctionRound.getAucObjDsc());
        aucEntrData.setAucDt(this.auctionRound.getAucDt());
        aucEntrData.setOslpNo(bidding.getUserNo());
        aucEntrData.setRgSqno("121212");
        aucEntrData.setTrmnAmnno(bidding.getUserNo());
        aucEntrData.setLvstAucPtcMnNo(bidding.getAuctionJoinNum());
        aucEntrData.setAtdrAm(bidding.getPrice());
        aucEntrData.setAtdrDtm(LocalDateTime.parse(bidding.getBiddingTime(), DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        aucEntrData.setAucPrgSq(bidding.getEntryNum());
//<!--	OSLP_NO            decimal       not null comment '원표번호',-->
//<!--	RG_SQNO            decimal       not null comment '등록일련번호',-->

        	int resultValue = EntryInfoMapperService.getInstance().insertBiddingHistory(aucEntrData);

            if (resultValue > 0) {
                addLogItem("응찰 내역 저장 완료 출품 번호 : " + bidding.getEntryNum() + " 응찰금액 : " + bidding.getPrice());
            } else {
                addLogItem("응찰 내역 저장 실패 출품 번호 : " + bidding.getEntryNum() + " 응찰금액 : " + bidding.getPrice());
            }
        
    }

    /**
     * 랭킹 계산
     */
    protected void calculationRanking() {
    	
        if (mCurrentBidderMap != null && mCurrentBidderMap.values().size() > 0) {
            // 순위 정렬
            List<SpBidding> list = new ArrayList<SpBidding>();

            list.addAll(mCurrentBidderMap.values());

            List<SpBidding> rankBiddingDataList = getCurrentRank(list);

            updateBidderList(rankBiddingDataList);
        } else {
            updateBidderList(null);
        }

    }


    /**
     * 1순위 응찰 금액 확인
     * @param spBidding
     * @return
     */
    protected boolean checkLowPrice(SpBidding spBidding) {
    	
    	boolean result = false;
    	
    	if(spBidding == null) {
    		return result;
    	}

    	SpEntryInfo entryInfo = mCurrentSpEntryInfo;
    	
    	SpBidding bidder = spBidding;

    	// 최저가
        int lowPrice = entryInfo.getLowPriceInt();
        // 응찰가
        int curPrice = bidder.getPriceInt();
        
        //응찰 금액이 최저가와 같거나 크면 OK
        
        if(curPrice >= lowPrice) {
        	result = true; 
        }

        System.out.println("checkLowPrice : " + result + " / 최저가 : " + lowPrice  + " / 응찰가 : " + curPrice);
        
    	return result;
    }

    protected boolean checkOverPrice(SpBidding spBidding) {
    	
    	boolean result = false;
    	
    	if(spBidding == null) {
    		return result;
    	}

    	SpEntryInfo entryInfo = mCurrentSpEntryInfo;

    	SpBidding bidder = spBidding;

    	// 최저가
        int lowPrice = entryInfo.getLowPriceInt() + SettingApplication.getInstance().getBaseUnit();
        // 응찰가
        int curPrice = bidder.getPriceInt();

        if(curPrice <= lowPrice) {
        	result = true; 
        }
    	
        System.out.println("checkOverPrice : " + result + " / 최저가+상한가 : " + lowPrice  + " / 응찰가 : " + curPrice);
        
    	
    	return result;
    }
    
    /**
     * 낙유찰 결과 전송 + 결과 DB 저장
     *
     * @param isSuccess
     * @param currentEntryInfo
     * @param bidder
     */
    
    protected void sendAuctionResult(boolean isSuccess, SpEntryInfo spEntryInfo, SpBidding bidder,String code) {
		
		SendAuctionResult auctionResult = new SendAuctionResult();
		auctionResult.setAuctionHouseCode(spEntryInfo.getAuctionHouseCode().getValue());
		auctionResult.setEntryNum(spEntryInfo.getEntryNum().getValue());
		auctionResult.setEntryType(spEntryInfo.getEntryType().getValue());
		auctionResult.setAucDt(spEntryInfo.getAucDt().getValue());
		auctionResult.setLsCmeNo(GlobalDefineCode.AUCTION_LOGIN_TYPE_MANAGER);
		
    	switch (code) {
		case GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS: 
		case GlobalDefineCode.AUCTION_RESULT_CODE_PENDING: 
			//낙찰 , 유찰인 경우 DB업데이트
	        if (isSuccess) {
	            // 낙찰
	            auctionResult.setResultCode(GlobalDefineCode.AUCTION_RESULT_CODE_SUCCESS);
	            auctionResult.setSuccessBidder(bidder.getUserNo().getValue());
	            auctionResult.setSuccessAuctionJoinNum(bidder.getAuctionJoinNum().getValue());
	            auctionResult.setSuccessBidPrice(bidder.getMultiplPriceString());
	            int priceUpr = CommonUtils.getInstance().getBaseUnitDivision(bidder.getMultiplPriceString(), SettingApplication.getInstance().getBaseUnit());
	            auctionResult.setSuccessBidUpr(Integer.toString(priceUpr));
	        } else {
	            // 유찰&보류
	            auctionResult.setResultCode(GlobalDefineCode.AUCTION_RESULT_CODE_PENDING);
	            auctionResult.setSuccessBidder(null);
	            auctionResult.setSuccessAuctionJoinNum(null);
	            auctionResult.setSuccessBidPrice("0");
	            auctionResult.setSuccessBidUpr("0");
	        }

	        final int resultValue = EntryInfoMapperService.getInstance().updateAuctionResult(auctionResult);

	        if (resultValue > 0) {

		        // 순위 정렬
	            List<SpBidding> list = new ArrayList<SpBidding>();

	            list.addAll(mCurrentBidderMap.values());

	            List<SpBidding> rankBiddingDataList = getCurrentRank(list);

	            if(bidder != null && bidder.getAuctionJoinNum() != null) {
	            	runWriteLogFile(rankBiddingDataList, mAuctionStatus, isSuccess, bidder.getAuctionJoinNum().getValue());
	            }else {
	            	   runWriteLogFile(rankBiddingDataList, mAuctionStatus, isSuccess, "");
	            }

	            updateAuctionStateInfo(isSuccess, bidder); 
	         // 낙유찰 결과 전송
	            addLogItem(mResMsg.getString("msg.auction.send.result") + AuctionDelegate.getInstance().onSendAuctionResult(auctionResult));
	        }

			break;
		case GlobalDefineCode.AUCTION_RESULT_CODE_CANCEL: 
				auctionResult.setResultCode(GlobalDefineCode.AUCTION_RESULT_CODE_CANCEL);
	            auctionResult.setSuccessBidder(null);
	            auctionResult.setSuccessAuctionJoinNum(null);
	            auctionResult.setSuccessBidPrice("0");
	            auctionResult.setSuccessBidUpr("0");
	            addLogItem(mResMsg.getString("msg.auction.send.result") + AuctionDelegate.getInstance().onSendAuctionResult(auctionResult));
			break;
		}
    	
        
    }

    /**
     * run Log
     *
     * @param dataList
     * @param auctionStatus
     * @param isSuccess
     * @param userNo
     */
    protected void runWriteLogFile(final List<SpBidding> dataList, final AuctionStatus auctionStatus, final boolean isSuccess, final String userNo) {

        // Create LogFile
        Thread thread = new Thread("logFile") {
            @Override
            public void run() {
                writeLogFile(dataList, auctionStatus, isSuccess, userNo);
            }
        };
        thread.setDaemon(true);
        thread.start();

    }

    /**
     * @MethodName setCurrentRank
     * @Description 현재 응찰 가격 기준 우선순위 확인 처리
     */
    @SuppressWarnings("unlikely-arg-type")
    protected List<SpBidding> getCurrentRank(List<SpBidding> list) {

        mLogger.debug("응찰 가격 우선순위 확인");

        Collections.sort(list, new Comparator<SpBidding>() {

            public int compare(SpBidding o1, SpBidding o2) {
                int result = 0;

                if (Long.parseLong(o1.getPrice().getValue()) == Long.parseLong(o2.getPrice().getValue())) {
                    if (Long.parseLong(o1.getBiddingTime().getValue()) == Long.parseLong(o2.getBiddingTime().getValue())) {
                        result = 0;
                    } else if (Long.parseLong(o1.getBiddingTime().getValue()) < Long.parseLong(o2.getBiddingTime().getValue())) {
                        result = -1;
                    } else if (Long.parseLong(o1.getBiddingTime().getValue()) > Long.parseLong(o2.getBiddingTime().getValue())) {
                        result = 1;
                    }
                } else if (Long.parseLong(o1.getPrice().getValue()) > Long.parseLong(o2.getPrice().getValue())) {
                    result = -1;
                } else if (Long.parseLong(o1.getPrice().getValue()) < Long.parseLong(o2.getPrice().getValue())) {
                    result = 1;
                }

                return result;
            }
        });

        return list;

    }

    /**
     * @param biddingList
     * @param auctionStatus
     * @param isAuctionPass
     * @Description 현재 응찰 가격 기준 우선순위 확인 처리
     */
    protected synchronized void writeLogFile(List<SpBidding> biddingList, AuctionStatus auctionStatus, boolean isSuccess, String userNo) {

        String currentTime = CommonUtils.getInstance().getCurrentTime("yyyyMMdd");

        String LOG_DIRECTORY = FILE_INFO.AUCTION_LOG_FILE_PATH + currentTime + "_" + auctionStatus.getAuctionHouseCode().toUpperCase();

        String LOG_FILE_NAME = currentTime + "-" + auctionStatus.getEntryNum() + FILE_INFO.AUCTION_LOG_FILE_EXTENSION;

        File fileDirectory = new File(LOG_DIRECTORY);
        File file = new File(fileDirectory, LOG_FILE_NAME);
        FileWriter fileWriter = null;

        String EMPTY_SPACE = "    ";
        String MAIN_LINE = "\r\n====================================================================================================\r\n";
        String SUB_LINE = "\r\n---------------------------------------------------------------------------------------------------------------------------------------------------------\r\n";
        String ENTER_LINE = "\r\n";
        StringBuffer logContent = new StringBuffer();
        String logCurrentTime = CommonUtils.getInstance().getCurrentTime("yyyyMMdd HH:mm:ss:SSS");

        try {
            if (!fileDirectory.exists()) {
                fileDirectory.mkdirs();
            }
            if (file.exists()) {
                addLogItem(String.format(mResMsg.getString("msg.file.duplicated"), LOG_FILE_NAME));
            } else {
                if (file.createNewFile()) {
                    addLogItem(String.format(mResMsg.getString("msg.file.create"), LOG_FILE_NAME));
                }
            }

            fileWriter = new FileWriter(file, true);

            String entryNum = auctionStatus.getEntryNum();

            String startPrice = String.format(mResMsg.getString("log.auction.result.start.price"), Integer.parseInt(auctionStatus.getStartPrice()));

            String auctionResult = null;

            if (isSuccess) {
                // 낙찰
                auctionResult = mResMsg.getString("log.auction.result.success");
            } else {
                // 유찰
                auctionResult = mResMsg.getString("log.auction.result.fail");
            }

            logContent.append(MAIN_LINE + ENTER_LINE);

            logContent.append(String.format(mResMsg.getString("log.auction.result.entry.info"), logCurrentTime, entryNum, startPrice, auctionResult, userNo));

            logContent.append(SUB_LINE);

            if (isSuccess && !CommonUtils.getInstance().isListEmpty(biddingList)) {

                for (int i = 0; i < biddingList.size(); i++) {
                    logContent.append(ENTER_LINE);
                    String user = biddingList.get(i).getAuctionJoinNum().getValue() + "(" + biddingList.get(i).getUserNo().getValue() + ")";
                    String rankUser = String.format(mResMsg.getString("log.auction.result.rank"), Integer.toString((i + 1)), user);
                    String price = String.format(mResMsg.getString("log.auction.result.price"), biddingList.get(i).getPriceInt()) + EMPTY_SPACE + CommonUtils.getInstance().getCurrentTime_yyyyMMddHHmmssSSS(biddingList.get(i).getBiddingTime().getValue());
                    logContent.append(rankUser + EMPTY_SPACE + price);

                }
                logContent.append(ENTER_LINE);
            } else {
                logContent.append(ENTER_LINE + mResMsg.getString("log.auction.result.fail") + ENTER_LINE);
            }

            if (!CommonUtils.getInstance().isListEmpty(mBeForeBidderDataList)) {

                logContent.append(SUB_LINE);

                // ㄱㄴㄷ sort
                Collections.sort(mBeForeBidderDataList);
                // 중복제거
                Set<SpBidding> linkedSet = new LinkedHashSet<>(mBeForeBidderDataList);
                List<SpBidding> distintListData = new ArrayList<SpBidding>(linkedSet);

                for (SpBidding disBidding : distintListData) {

                    logContent.append(ENTER_LINE);

                    String user = disBidding.getAuctionJoinNum().getValue() + "(" + disBidding.getUserNo().getValue() + ")";
                    logContent.append(String.format(mResMsg.getString("log.auction.result.before.bidder"), user));
                    logContent.append(ENTER_LINE);

                    for (SpBidding beforeBidding : mBeForeBidderDataList) {

                        if (disBidding.getUserNo().getValue().equals(beforeBidding.getUserNo().getValue())) {
                            logContent.append(String.format(mResMsg.getString("log.auction.result.price"), beforeBidding.getPriceInt()));
                            logContent.append(EMPTY_SPACE);
                            logContent.append(EMPTY_SPACE);
                            logContent.append(CommonUtils.getInstance().getCurrentTime_yyyyMMddHHmmssSSS(beforeBidding.getBiddingTime().getValue()));

                            if (beforeBidding.getIsCancelBidding().getValue()) {
                                logContent.append(EMPTY_SPACE);
                                logContent.append(EMPTY_SPACE);
                                logContent.append(mResMsg.getString("log.auction.result.cancel.bidding"));
                            }

                            logContent.append(ENTER_LINE);
                        }
                    }
                }
            }

            logContent.append(ENTER_LINE);
            addLogItem(logContent.toString());

            fileWriter.write(logContent.toString());
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 경매정보 - 경매 상태 표시
     *
     * @param code      현재 경매 상태
     * @param isSuccess true : 낙찰 , false : 유찰
     */
    protected void updateAuctionStateInfo(boolean isSuccess, SpBidding bidder) {
    }

    /**
     * 응찰자 현황 표시
     *
     * @param spBiddingDataList 랭킹 산정된 응찰자 리스트
     */
    protected void updateBidderList(List<SpBidding> spBiddingDataList) {
    }

    /**
     * ADD 로그
     *
     * @param str
     */
    protected void addLogItem(String str) {
        if (!str.isEmpty()) {
            mLogger.debug("Log : " + str);
        }
    }

    /**
     * 원버튼 팝업
     *
     * @param message
     * @return
     */
    protected Optional<ButtonType> showAlertPopupOneButton(String message) {
        return CommonUtils.getInstance().showAlertPopupOneButton(mStage, message, mResMsg.getString("popup.btn.close"));
    }


    /**
     * EntryInfo -> SpEntryInfo
     *
     * @param dataList
     * @return
     */
    protected ObservableList<SpEntryInfo> getParsingEntryDataList(List<EntryInfo> dataList) {

        ObservableList<SpEntryInfo> resultDataList = dataList.stream()
                .map(item -> new SpEntryInfo(item))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        return resultDataList;
    }

}
