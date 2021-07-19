package com.nh.common.handlers;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.server.models.AuctionCheckSession;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedCheckSessionHandler extends SimpleChannelInboundHandler<AuctionCheckSession> {
    private final NettyControllable mController;

    public AuctionClientDecodedCheckSessionHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AuctionCheckSession auctionCheckSession) throws Exception {
        if (mController != null) {
            mController.onCheckSession(ctx, auctionCheckSession);
        }
    }
}
