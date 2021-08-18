package com.nh.controller.netty;

import com.nh.common.BillboardShareNettyClient;
import com.nh.common.interfaces.NettyControllable;

public class BillboardDelegate {

    private static BillboardDelegate instance = null;

    private BillboardShareNettyClient mClient; // 네티 접속 객체

    public static synchronized BillboardDelegate getInstance() {
        if (instance == null) {
            instance = new BillboardDelegate();
        }
        return instance;
    }

    /**
     * @param host_
     * @param port_
     * @param controllable
     * @Description 전광판 서버 접속
     */
    public void createClients(String host_, int port_, NettyControllable controllable) {
        this.mClient = new BillboardShareNettyClient.Builder(host_, port_).setController(controllable).buildAndRun();
    }



}
