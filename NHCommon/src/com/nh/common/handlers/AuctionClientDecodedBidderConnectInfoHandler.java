package com.nh.common.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.server.models.BidderConnectInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedBidderConnectInfoHandler extends SimpleChannelInboundHandler<BidderConnectInfo> {
    private static final Logger mLogger = LoggerFactory.getLogger(AuctionClientDecodedBidderConnectInfoHandler.class);
    private final NettyControllable mController;

    public AuctionClientDecodedBidderConnectInfoHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BidderConnectInfo bidderConnectInfo) throws Exception {
        mLogger.info("AuctionClientDecodedConnectionInfoHandler:channelRead0 : " + bidderConnectInfo.getEncodedMessage());

        if (mController != null) {
            mController.onBidderConnectInfo(bidderConnectInfo);
        }
    }
}
