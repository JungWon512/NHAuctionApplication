package com.nh.common.handlers;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.UdpStatusListener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class UdpClientInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger mLogger = LoggerFactory.getLogger(UdpClientInboundHandler.class);
    private final UdpStatusListener mUdpStatusListener;

    public UdpClientInboundHandler() {
		this.mUdpStatusListener = null;
    }
    
    public UdpClientInboundHandler(UdpStatusListener listener) {
        this.mUdpStatusListener = listener;
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
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        if(mUdpStatusListener != null) {
        	mUdpStatusListener.exceptionCaught();
        }
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
