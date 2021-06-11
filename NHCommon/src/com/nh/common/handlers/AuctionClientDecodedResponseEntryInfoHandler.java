package com.nh.common.handlers;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.server.models.ResponseEntryInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedResponseEntryInfoHandler extends SimpleChannelInboundHandler<ResponseEntryInfo> {
    private final NettyControllable mController;

    public AuctionClientDecodedResponseEntryInfoHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseEntryInfo responseCarInfo) throws Exception {
        if (mController != null) {
            mController.onResponseCarInfo(responseCarInfo);
        }
    }
}
