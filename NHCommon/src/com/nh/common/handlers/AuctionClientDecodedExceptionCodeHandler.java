package com.nh.common.handlers;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.server.models.ExceptionCode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedExceptionCodeHandler extends SimpleChannelInboundHandler<ExceptionCode> {
    private final NettyControllable mController;

    public AuctionClientDecodedExceptionCodeHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ExceptionCode exceptionCode) throws Exception {
        if (mController != null) {
            mController.onExceptionCode(exceptionCode);
        }
    }
}
