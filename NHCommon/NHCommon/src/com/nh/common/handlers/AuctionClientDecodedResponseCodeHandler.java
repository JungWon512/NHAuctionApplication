package com.nh.common.handlers;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.server.models.ResponseCode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedResponseCodeHandler extends SimpleChannelInboundHandler<ResponseCode> {
    private final NettyControllable mController;

    public AuctionClientDecodedResponseCodeHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseCode exceptionCode) throws Exception {
        if (mController != null) {
            mController.onResponseCode(exceptionCode);
        }
    }
}
