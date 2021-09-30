package com.nh.common.interfaces;

/**
 * UDP 연결 끊어졌을경우 수행
 * 
 * @author jhlee
 *
 */
public interface UdpBillBoardStatusListener {

	public void onActive(); // 연결 성공

	public void onInActive(); // 연결 끊어졌을경우

	public void exceptionCaught(); // 연결 끊어졌을경우

}
