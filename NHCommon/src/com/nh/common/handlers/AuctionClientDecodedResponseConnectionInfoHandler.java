package com.nh.common.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.common.models.ResponseConnectionInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedResponseConnectionInfoHandler extends SimpleChannelInboundHandler<ResponseConnectionInfo> {
    private static final Logger mLogger = LoggerFactory.getLogger(AuctionClientDecodedResponseConnectionInfoHandler.class);
    private final NettyControllable mController;

    public AuctionClientDecodedResponseConnectionInfoHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseConnectionInfo responseConnectionInfo) throws Exception {
        mLogger.info("AuctionClientDecodedResponseConnectionInfoHandler:channelRead0 : " + responseConnectionInfo.getResult());

        if (mController != null) {
            mController.onResponseConnectionInfo(responseConnectionInfo);
        }
    }
}
