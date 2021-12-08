package com.nh.controller.netty;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.BillboardShareNettyClient;
import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.UdpBillBoardStatusListener;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.SharedPreference;
import com.nh.share.interfaces.NettySendable;

public class BillboardDelegate2 {

    private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static BillboardDelegate2 instance = null;

    private BillboardShareNettyClient mClient; // 네티 접속 객체

    public static synchronized BillboardDelegate2 getInstance() {
        if (instance == null) {
            instance = new BillboardDelegate2();
        }
        return instance;
    }

    /**
     * @param host_
     * @param port_
     * @param controllable
     * @Description 전광판 서버 접속
     */
    public void createClients(String host_, String port_, UdpBillBoardStatusListener udpBillBoardStatusListener_) {
        this.mClient = new BillboardShareNettyClient.Builder(host_, port_,udpBillBoardStatusListener_).buildAndRun();
        clearBillboardNote();
    }

    /**
     * 객체를 송신할 때 사용한다.
     *
     * @param object 보낼 객체
     */
    public String sendMessage(NettySendable object) {
    	if (!isEmptyClient() && isActive()) {
            mClient.sendMessage(object.getEncodedMessage());
            return object.getEncodedMessage();
        }else {
        	return "";
        }
    }

    /**
     * String을 송신할 때 사용한다.
     *
     * @param object 보낼 객체
     */
    public String sendMessage(String object) {
        if (!isEmptyClient() &&  isActive()) {
        	mLogger.debug("BillboardData Send : " + object);
            mClient.sendMessage(object);
            return object;
        }else {
        	return "";
        }
       
    }

    /**
     * @Description 경매 시작
     */
    public void startBillboard() {
        mLogger.debug("startBillboard");
        sendMessage(String.format("%c%c%c", GlobalDefine.BILLBOARD.STX, GlobalDefine.BILLBOARD.START_CODE, GlobalDefine.BILLBOARD.ETX));
    }

    /**
     * @Description 경매 종료
     */
    public void finishBillboard() {
        mLogger.debug("finishBillboard");
        if (!isEmptyClient() &&  isActive()) {
        	 clearBillboard();
             sendMessage(String.format("%c%c%c", GlobalDefine.BILLBOARD.STX, GlobalDefine.BILLBOARD.FINISH_CODE, GlobalDefine.BILLBOARD.ETX));
        }
       
    }
   
    /**
     * @Description 경매 완료
     */
    public void completeBillboard() {
    if(!isEmptyClient() && isActive()) {
        mLogger.debug("completeBillboard");
        sendMessage(String.format("%c%c%c", GlobalDefine.BILLBOARD.STX, GlobalDefine.BILLBOARD.FINISH_CODE, GlobalDefine.BILLBOARD.ETX));
    	}
    }

    /**
     * @Description 경매 정보 전송
     */
    public void sendBillboardData(NettySendable sendable) {
    	if(!isEmptyClient() && isActive()) {
            //clearBillboard();
            sendMessage(sendable);
    	}
    }

    /**
     * @Description 전광판 시간 전송
     */
    public void sendBillboardTime() {
    	if(!isEmptyClient() && isActive()) {
    	mLogger.debug("sendBillboardTime");
        sendMessage(String.format("%c%c%c", GlobalDefine.BILLBOARD.STX, GlobalDefine.BILLBOARD.TIME_CODE, GlobalDefine.BILLBOARD.ETX));
    
    	}
    	}
    
    /**
     * @Description 전광판 카운트다운
     */
    public void onCountDown(String number) {
    	if(!isEmptyClient() && isActive()) {
        mLogger.debug("onCountDown " + number);
        String num = (number.equals("0")) ? " " : number;
        sendMessage(String.format("%c%c%s%c", GlobalDefine.BILLBOARD.STX, GlobalDefine.BILLBOARD.COUNTDOWN_CODE1, num, GlobalDefine.BILLBOARD.ETX));
        sendMessage(String.format("%c%c%s%c", GlobalDefine.BILLBOARD.STX, GlobalDefine.BILLBOARD.COUNTDOWN_CODE2, num, GlobalDefine.BILLBOARD.ETX));
    	}
    }

    /**
     * @Description 전광판 clear
     */
    public void clearBillboard() {
        int sum = Arrays.stream(getBillboardPref())
                .map(s -> Integer.parseInt(s))
                .reduce(0, (a, b) -> a + b);
        // STX + 1 + 전광판 전체 자리수(ex) 공백 67개)+ ETX
        sendMessage(String.format("%c%c%s%c", GlobalDefine.BILLBOARD.STX, GlobalDefine.BILLBOARD.DATA_CODE, " ".repeat(sum), GlobalDefine.BILLBOARD.ETX));
    }

    /**
     * @Description 전광판 초기화 작업: SharedPreference 값(자릿수) 전송
     */
    public String initBillboard() {
        finishBillboard();
        sendBillboardTime();
        String[] arr = getBillboardPref();
        StringBuilder sb = new StringBuilder();
        sb.append(GlobalDefine.BILLBOARD.STX);
        sb.append(GlobalDefine.BILLBOARD.INIT_CODE);
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i == arr.length - 1) {
                sb.append(GlobalDefine.BILLBOARD.ETX);
                break;
            }
            sb.append(GlobalDefine.BILLBOARD.DELIMITER);
        }
        return sendMessage(sb.toString());
    }

    /**
     * @Description 전광판 비고 clear
     */
    public void clearBillboardNote() {
        String blanks = " ".repeat(100); // 공백 100 자리
        sendMessage(GlobalDefine.BILLBOARD.STX + GlobalDefine.BILLBOARD.NOTE_CODE + blanks + GlobalDefine.BILLBOARD.ETX);
    }
    
    /**
     * @Description 전광판 비고 전송
     */
    public void sendBillboardNote(String noteText) {
        sendMessage(GlobalDefine.BILLBOARD.STX + GlobalDefine.BILLBOARD.NOTE_CODE + noteText + GlobalDefine.BILLBOARD.ETX);
    }

    /**
     * @Description 전광판 SharedPreference Setting Values
     */
    public String[] getBillboardPref() {
        SharedPreference sharedPreference = SharedPreference.getInstance();
        return new String[]{
                sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_ENTRYNUM, ""), // 경매번호
                sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_EXHIBITOR, ""), // 출하주명
                sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_WEIGHT, ""), // 중량
                sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_GENDER, ""), // 성별
                sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_MOTHER, ""), // 혈통

                sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_PASSAGE, ""), // 계대
                sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_MATIME, ""), // 산차
                sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_KPN, ""), // KPN

                sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_REGION, ""), // 지역
                sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_NOTE, ""), // 비고

                sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_LOWPRICE, ""), // 최저가
                sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_SUCPRICE, ""), // 낙찰가
                sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_SUCBIDDER, ""), // 낙찰자번호
                sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_BOARD_DNA, "") // 친자
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
