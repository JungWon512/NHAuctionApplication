package com.nh.common.handlers;

import com.nh.common.interfaces.NettyControllable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class BillboardClientInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger mLogger = LoggerFactory.getLogger(BillboardClientInboundHandler.class);
    private final NettyControllable mController;

    public BillboardClientInboundHandler(NettyControllable controller) {
        this.mController = controller;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        mLogger.info("[UDP] " + ((InetSocketAddress) ctx.channel().remoteAddress()).getPort() + "번 포트 채널이 Active 되었습니다.");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        mController.onChannelInactive(address.getPort());    // 서버와 연결 끊어졌을경우
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
