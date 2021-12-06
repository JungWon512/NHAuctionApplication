package com.nh.controller.netty;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.PdpShareNettyClient;
import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.NettyControllable;
import com.nh.common.interfaces.UdpPdpBoardStatusListener;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.SharedPreference;
import com.nh.share.interfaces.NettySendable;

public class PdpDelegate {

	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static PdpDelegate instance = null;

	private PdpShareNettyClient mClient; // 네티 접속 객체

	public static synchronized PdpDelegate getInstance() {
		if (instance == null) {
			instance = new PdpDelegate();
		}
		return instance;
		
	}

	/**
	 * @param host_
	 * @param port_
	 * @param controllable
	 * @Description PDP 서버 접속
	 */
	public void createClients(String host_, String port_, UdpPdpBoardStatusListener udpPdpBoardStatusListener_) {
		this.mClient = new PdpShareNettyClient.Builder(host_, port_,udpPdpBoardStatusListener_).buildAndRun();
	}

	/**
	 * 객체를 송신할 때 사용한다.
	 *
	 * @param object 보낼 객체
	 */
	public String sendMessage(NettySendable object) {
		if (!isEmptyClient() && isActive()) {
			mClient.sendMessage(object.getEncodedMessage());
			mLogger.debug("PDP SEND MESSAGE : " + object.getEncodedMessage());
		}
		return object.getEncodedMessage();
	}

	/**
	 * String을 송신할 때 사용한다.
	 *
	 * @param object 보낼 객체
	 */
	public String sendMessage(String object) {
		if (!isEmptyClient() && isActive()) {
			mLogger.debug("PdpData Send : " + object);
			mClient.sendMessage(object);
		}
		return object;
	}

	/**
	 * @Description 경매 시작
	 */
	public void startPdp() {
		mLogger.debug("startPdp");
		sendMessage(String.format("%c%c%c", GlobalDefine.PDP.STX, GlobalDefine.PDP.START_CODE, GlobalDefine.PDP.ETX));
	}

	/**
	 * @Description 경매 종료
	 */
	public void finishPdp() {
		if(!isEmptyClient() && isActive()) {
			mLogger.debug("finishPdp");
			clearPdp();
			sendMessage(String.format("%c%c%c", GlobalDefine.PDP.STX, GlobalDefine.PDP.FINISH_CODE, GlobalDefine.PDP.ETX));
		}
	}

	/**
	 * @Description 경매 완료
	 */
	public void completePdp() {
		mLogger.debug("completeBillboard");
		 if(!isEmptyClient() && isActive()) {
			 sendMessage(String.format("%c%c%c", GlobalDefine.PDP.STX, GlobalDefine.PDP.FINISH_CODE, GlobalDefine.PDP.ETX));
		 }
	}

	/**
	 * @Description 경매 정보 전송
	 */
	public void sendPdpData(NettySendable sendable) {
		
		if(!isEmptyClient() && isActive()) {
			clearPdp();
			sendMessage(sendable);
		}
	}

	/**
	 * @Description PDP 카운트다운
	 */
	public void onCountDown(String number) {
		mLogger.debug("onCountDown " + number);
		String num = (number.equals("0")) ? " " : number;
		sendMessage(String.format("%c%c%s%c", GlobalDefine.PDP.STX, GlobalDefine.PDP.COUNTDOWN_CODE, num,
				GlobalDefine.PDP.ETX));
	}

	/**
	 * @Description PDP clear
	 */
	public void clearPdp() {
		sendMessage(String.format("%c%c%c", GlobalDefine.PDP.STX, GlobalDefine.PDP.CLEAR_CODE, GlobalDefine.PDP.ETX));
	}

	/**
	 * @Description PDP 초기화 작업: SharedPreference 값(자릿수) 전송
	 */
	public String initPdp() {
		finishPdp();
		String[] arr = getPdpPref();
		StringBuilder sb = new StringBuilder();
		sb.append(GlobalDefine.PDP.STX);
		sb.append(GlobalDefine.PDP.INIT_CODE);
		for (int i = 0; i < arr.length; i++) {
			sb.append(arr[i]);
			if (i == arr.length - 1) {
				sb.append(GlobalDefine.PDP.ETX);
				break;
			}
			sb.append(GlobalDefine.PDP.DELIMITER);
		}
		return sendMessage(sb.toString());
	}

	/**
	 * @Description PDP 비고 clear
	 */
	public void clearPdpNote() {
		String blanks = " ".repeat(100); // 공백 100 자리
		sendMessage(
				GlobalDefine.BILLBOARD.STX + GlobalDefine.BILLBOARD.NOTE_CODE + blanks + GlobalDefine.BILLBOARD.ETX);
	}

	/**
	 * @Description PDP SharedPreference Setting Values
	 */
	public String[] getPdpPref() {
		SharedPreference sharedPreference = SharedPreference.getInstance();
		return new String[] { sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_ENTRYNUM, ""), // 경매번호
				sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_EXHIBITOR, ""), // 출하주명
				sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_WEIGHT, ""), // 중량
				sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_GENDER, ""), // 성별
				sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_MOTHER, ""), // 혈통

				sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_PASSAGE, ""), // 계대
				sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_MATIME, ""), // 산차
				sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_KPN, ""), // KPN

				sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_REGION, ""), // 지역
				sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_NOTE, ""), // 비고
				sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_LOWPRICE, ""), // 최저가
				sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_SUCPRICE, ""), // 낙찰가
				sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_SUCBIDDER, ""), // 낙찰자번호
				sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_PDP_DNA, "") // 친자
		};
	}

	/**
	 * 네티 체크
	 *
	 * @return
	 */
	public boolean isEmptyClient() {
		if (mClient != null) {
			return false;
		} else {
			return true;
		}
	}

    /**
     * 연결 상태 확인
     *
     * @return
     */
    public boolean isActive() {
 
    	if(isEmptyClient()) {
    		return false;
    	}
    	 return mClient.isActive();
    }
    
	/**
	 * @Description 네티 접속 해제
	 */
	public void onDisconnect(NettyClientShutDownListener listener) {
		if(mClient != null) {
			mClient.stopClient(listener);
		}
	}
}
