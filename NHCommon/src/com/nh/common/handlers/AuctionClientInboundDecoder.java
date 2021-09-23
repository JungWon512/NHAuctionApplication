package com.nh.common.handlers;

import java.net.InetSocketAddress;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyControllable;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.CommonMessageParser;
import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.server.ServerMessageParser;
import com.nh.share.server.interfaces.FromAuctionServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public final class AuctionClientInboundDecoder extends MessageToMessageDecoder<String> {
    private static final Logger mLogger = LoggerFactory.getLogger(AuctionClientInboundDecoder.class);
    private final NettyControllable mController;

    public AuctionClientInboundDecoder(NettyControllable controller) {
        mController = controller;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        //ssl 적용 안하면 여기서 channelActive 
        if (!GlobalDefineCode.USE_CLIENT_SSL_FLAG) {
        	 if (mController != null) {
                 mController.onActiveChannel(ctx.channel());
             }
        }

        mLogger.info(((InetSocketAddress)ctx.channel().remoteAddress()).getPort() + "번 포트 채널이 Active 되었습니다.");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, String message, List<Object> out) throws Exception {
        switch (message.charAt(0)) {
        case FromAuctionServer.ORIGIN:
            out.add(ServerMessageParser.parse(message));
            break;
        case FromAuctionCommon.ORIGIN:
            out.add(CommonMessageParser.parse(message));
            break;
        default:
            break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        mController.onChannelInactive(address.getPort());    //서버와 연결 끊어졌을경우
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        mController.exceptionCaught(address.getPort());
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }

}