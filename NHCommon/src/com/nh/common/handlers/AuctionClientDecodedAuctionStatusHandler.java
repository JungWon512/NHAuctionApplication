package com.nh.common.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.common.models.AuctionStatus;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedAuctionStatusHandler extends SimpleChannelInboundHandler<AuctionStatus> {
    private static final Logger mLogger = LoggerFactory.getLogger(AuctionClientDecodedAuctionStatusHandler.class);

    private final NettyControllable mController;

    public AuctionClientDecodedAuctionStatusHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AuctionStatus auctionStatus) throws Exception {
        
        if (mController != null) {
            mController.onAuctionStatus(auctionStatus);
        }
    }
}
