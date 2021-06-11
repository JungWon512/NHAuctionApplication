package com.nh.common.handlers;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.server.models.CurrentSetting;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public final class AuctionClientDecodedCurrentSettingHandler extends SimpleChannelInboundHandler<CurrentSetting> {
    private final NettyControllable mController;

    public AuctionClientDecodedCurrentSettingHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CurrentSetting currentSetting) throws Exception {
        if (mController != null) {
            mController.onCurrentSetting(currentSetting);
        }
    }
}
