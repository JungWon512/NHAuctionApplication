package com.nh.controller.utils;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.request.ActionRequestAuctionLogin;
import com.nh.share.api.request.ActionRequestAuctionResult;
import com.nh.share.api.request.ActionRequestInsertBidLog;
import com.nh.share.api.request.ActionRequestMultipleAuctionStatus;
import com.nh.share.api.request.ActionRequestSelectBidEntry;
import com.nh.share.api.request.ActionRequestSelectBidNum;
import com.nh.share.api.request.ActionRequestSelectCowCnt;
import com.nh.share.api.request.ActionRequestSelectCowInfo;
import com.nh.share.api.request.ActionRequestSelectQcn;
import com.nh.share.api.request.ActionRequestUpdateCowSt;
import com.nh.share.api.request.ActionRequestUpdateLowsBidAmt;
import com.nh.share.api.request.body.RequestBidEntryBody;
import com.nh.share.api.request.body.RequestBidLogBody;
import com.nh.share.api.request.body.RequestBidNumBody;
import com.nh.share.api.request.body.RequestCowInfoBody;
import com.nh.share.api.request.body.RequestLoginBody;
import com.nh.share.api.request.body.RequestMultipleAuctionStatusBody;
import com.nh.share.api.request.body.RequestQcnBody;
import com.nh.share.api.request.body.RequestUpdateLowsBidAmtBody;
import com.nh.share.api.response.ResponseAuctionLogin;
import com.nh.share.api.response.ResponseAuctionResult;
import com.nh.share.api.response.ResponseBidEntry;
import com.nh.share.api.response.ResponseChangeCowInfo;
import com.nh.share.api.response.ResponseCowInfo;
import com.nh.share.api.response.ResponseJoinNumber;
import com.nh.share.api.response.ResponseNumber;
import com.nh.share.api.response.ResponseQcn;

/**
 * API Class
 * 
 * @author jhlee
 *
 */
public class ApiUtils {

	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static ApiUtils instance = null;

	public ApiUtils() {
	}

	public static ApiUtils getInstance() {
		if (instance == null) {
			instance = new ApiUtils();
		}
		return instance;
	}

	/**
	 * 로그인
	 * @param body
	 * @param listener_
	 */
	public void requestLogin(RequestLoginBody body, ActionResultListener<ResponseAuctionLogin> listener_) {
		ActionRuler.getInstance().addAction(new ActionRequestAuctionLogin(body, listener_));
		ActionRuler.getInstance().runNext();
	}
	
	/**
	 * 경매 결과 전송
	 * @param body
	 * @param listener_
	 */
	public void requestAuctionResult(String params, ActionResultListener<ResponseAuctionResult> listener_) {
		ActionRuler.getInstance().addAction(new ActionRequestAuctionResult(params,listener_));
		ActionRuler.getInstance().runNext();
	}
	
	/**
	 * 사용자 정보 검색
	 * @param body
	 * @param listener_
	 */
	public void requestSelectBidNum(RequestBidNumBody body, ActionResultListener<ResponseJoinNumber> listener_) {
		ActionRuler.getInstance().addAction(new ActionRequestSelectBidNum(body, listener_));
		ActionRuler.getInstance().runNext();
	}
	
	/**
	 * 회차 정보 검색
	 * @param body
	 * @param listener_
	 */
	public void requestSelectQcn(RequestQcnBody body, ActionResultListener<ResponseQcn> listener_) {
		ActionRuler.getInstance().addAction(new ActionRequestSelectQcn(body, listener_));
		ActionRuler.getInstance().runNext();
	}
	
	/**
	 * 출장우 데이터 카운트
	 * @param body
	 * @param listener_
	 */
	public void requestSelectCowCnt(RequestCowInfoBody body, ActionResultListener<ResponseNumber> listener_) {
		ActionRuler.getInstance().addAction(new ActionRequestSelectCowCnt(body, listener_));
		ActionRuler.getInstance().runNext();
	}
	
	/**
	 * 출장우 데이터 조회
	 * @param body
	 * @param listener_
	 */
	public void requestSelectCowInfo(RequestCowInfoBody body, ActionResultListener<ResponseCowInfo> listener_) {
		ActionRuler.getInstance().addAction(new ActionRequestSelectCowInfo(body, listener_));
		ActionRuler.getInstance().runNext();
	}
	

	/**
	 * 응찰 로그 저장
	 * @param body
	 * @param listener_
	 */
	public void requestInsertBidLog(RequestBidLogBody body, ActionResultListener<ResponseNumber> listener_) {
		ActionRuler.getInstance().addAction(new ActionRequestInsertBidLog(body, listener_));
		ActionRuler.getInstance().runNext();
	}
	
	
	/**
	 * 최저가 변경
	 * @param body
	 * @param listener_
	 */
	public void requestUpdateLowsBidAmt(RequestUpdateLowsBidAmtBody body, ActionResultListener<ResponseNumber> listener_) {
		ActionRuler.getInstance().addAction(new ActionRequestUpdateLowsBidAmt(body, listener_));
		ActionRuler.getInstance().runNext();
	}
	
	/**
	 * 경매 상태 변경
	 * @param body
	 * @param listener_
	 */
	public void requestUpdateCowSt(RequestCowInfoBody body, ActionResultListener<ResponseNumber> listener_) {
		ActionRuler.getInstance().addAction(new ActionRequestUpdateCowSt(body, listener_));
		ActionRuler.getInstance().runNext();
	}
	
	
	/**
	 * 일괄경매 시작/정지/종료
	 * @param body
	 * @param listener_
	 */
	public void requestMultipleAuctionStatus(RequestMultipleAuctionStatusBody body, ActionResultListener<ResponseChangeCowInfo> listener_) {
		ActionRuler.getInstance().addAction(new ActionRequestMultipleAuctionStatus(body, listener_));
		ActionRuler.getInstance().runNext();
	}
	
	
	/**
	 * 일괄경매 응찰목록 조회
	 * @param body
	 * @param listener_
	 */
	public void requestSelectBidEntry(RequestBidEntryBody body, ActionResultListener<ResponseBidEntry> listener_) {
		ActionRuler.getInstance().addAction(new ActionRequestSelectBidEntry(body, listener_));
		ActionRuler.getInstance().runNext();
	}
	
	
}
