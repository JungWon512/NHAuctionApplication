package com.nh.common.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.common.models.ConnectionInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedConnectionInfoHandler extends SimpleChannelInboundHandler<ConnectionInfo> {
    private static final Logger mLogger = LoggerFactory.getLogger(AuctionClientDecodedConnectionInfoHandler.class);
    private final NettyControllable mController;

    public AuctionClientDecodedConnectionInfoHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConnectionInfo connectionInfo) throws Exception {
        mLogger.info("AuctionClientDecodedConnectionInfoHandler:channelRead0 : " + connectionInfo.getEncodedMessage());

        if (mController != null) {
            mController.onConnectionInfo(connectionInfo);
        }
    }
}
