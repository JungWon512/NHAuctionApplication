package com.nh.common.interfaces;

/**
 * UDP 연결 끊어졌을경우 수행
 * @author jhlee
 *
 */
public interface UdpStatusListener {

    public void exceptionCaught(); //연결 끊어졌을경우

}
