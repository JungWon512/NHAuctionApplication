package com.nh.common.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.server.models.ToastMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public final class AuctionClientDecodedToastMessageHandler extends SimpleChannelInboundHandler<ToastMessage> {
    private static final Logger mLogger = LoggerFactory.getLogger(AuctionClientDecodedToastMessageHandler.class);

    private final NettyControllable mController;

    public AuctionClientDecodedToastMessageHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ToastMessage toastMessage) throws Exception {
        if (mController != null) {
            mController.onToastMessage(toastMessage);
        }
    }
}
