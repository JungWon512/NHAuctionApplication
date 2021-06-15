package com.nh.common.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.common.models.Bidding;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedBiddingHandler extends SimpleChannelInboundHandler<Bidding> {
    private static final Logger mLogger = LoggerFactory.getLogger(AuctionClientDecodedBiddingHandler.class);
    private final NettyControllable mController;

    public AuctionClientDecodedBiddingHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Bidding bidding) throws Exception {
        mLogger.info("AuctionClientDecodedBiddingHandler:channelRead0 : " + bidding.getEncodedMessage());

        if (mController != null) {
            mController.onBidding(bidding);
        }
    }
}
