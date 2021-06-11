package com.nh.common.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.server.models.AbsenteeUserInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedAbsenteeUserInfoHandler extends SimpleChannelInboundHandler<AbsenteeUserInfo> {
    private static final Logger mLogger = LoggerFactory.getLogger(AuctionClientDecodedAbsenteeUserInfoHandler.class);
    private final NettyControllable mController;

    public AuctionClientDecodedAbsenteeUserInfoHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbsenteeUserInfo absenteeUserInfo) throws Exception {
        mLogger.info(
                "AuctionClientDecodedAbsenteeUserInfoHandler:channelRead0 : " + absenteeUserInfo.getEncodedMessage());

        if (mController != null) {
            mController.onAbsenteeUserInfo(absenteeUserInfo);
        }
    }
}
