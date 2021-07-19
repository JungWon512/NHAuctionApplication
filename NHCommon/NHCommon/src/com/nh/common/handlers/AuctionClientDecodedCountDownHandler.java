package com.nh.common.handlers;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.server.models.AuctionCountDown;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedCountDownHandler extends SimpleChannelInboundHandler<AuctionCountDown> {
    private final NettyControllable mController;

    public AuctionClientDecodedCountDownHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AuctionCountDown auctionCountDown) throws Exception {
        if (mController != null) {
            mController.onAuctionCountDown(auctionCountDown);
        }
    }
}
