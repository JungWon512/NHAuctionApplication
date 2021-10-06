package com.nh.common.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.server.models.StandConnectInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedStandConnectInfoHandler extends SimpleChannelInboundHandler<StandConnectInfo> {
    private static final Logger mLogger = LoggerFactory.getLogger(AuctionClientDecodedStandConnectInfoHandler.class);
    private final NettyControllable mController;

    public AuctionClientDecodedStandConnectInfoHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StandConnectInfo standConnectInfo) throws Exception {
//        mLogger.info("AuctionClientDecodedBiddingHandler:channelRead0 : " + bidding.getEncodedMessage());

        if (mController != null) {
            mController.onStandConnectInfo(standConnectInfo);
        }
    }
}
