package com.nh.common.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.server.models.FavoriteEntryInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuctionClientDecodedFavoriteEntryInfoHandler extends SimpleChannelInboundHandler<FavoriteEntryInfo> {
    private static final Logger mLogger = LoggerFactory.getLogger(AuctionClientDecodedFavoriteEntryInfoHandler.class);
    private final NettyControllable mController;

    public AuctionClientDecodedFavoriteEntryInfoHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FavoriteEntryInfo favoriteCarInfo) throws Exception {
        mLogger.info("AuctionClientDecodedFavoriteCarInfoHandler:channelRead0 : " + favoriteCarInfo.getEncodedMessage());

        if (mController != null) {
            mController.onFavoriteCarInfo(favoriteCarInfo);
        }
    }
}
