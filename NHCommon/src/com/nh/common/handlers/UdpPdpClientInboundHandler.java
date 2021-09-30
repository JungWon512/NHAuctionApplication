package com.nh.common.handlers;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.UdpPdpBoardStatusListener;
import com.nh.common.interfaces.ExceptionListener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;


public class UdpPdpClientInboundHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static final Logger mLogger = LoggerFactory.getLogger(UdpPdpClientInboundHandler.class);
    private final UdpPdpBoardStatusListener mUdpStatusListener;
    private ExceptionListener mExceptionListener;

    public UdpPdpClientInboundHandler() {
		this.mUdpStatusListener = null;
    }
    
    public UdpPdpClientInboundHandler(UdpPdpBoardStatusListener listener,ExceptionListener exceptionListener) {
        this.mUdpStatusListener = listener;
        this.mExceptionListener = exceptionListener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        if(mUdpStatusListener != null) {
          	mUdpStatusListener.onActive();
          }else {
        	  System.out.println("[전광판 PDP listener] mUdpStatusListener is null");
          }
        mLogger.info("[UDP] " + ((InetSocketAddress) ctx.channel().remoteAddress()).getPort() + "번 포트 채널이 Active 되었습니다.");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	  if(mUdpStatusListener != null) {
          	mUdpStatusListener.onInActive();
          }
    	  if(mExceptionListener != null) {
          	mExceptionListener.exceptionCaught();
          }
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        mLogger.info("[UDP] " + ((InetSocketAddress) ctx.channel().remoteAddress()).getPort() + "번 포트 채널이 Inactive 되었습니다.");
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        mLogger.info("[UDP exceptionCaught] " + ((InetSocketAddress) ctx.channel().remoteAddress()).getPort() + "번 포트 채널 exceptionCaught");

        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        if(mUdpStatusListener != null) {
        	mUdpStatusListener.exceptionCaught();
        }
        
        if(mExceptionListener != null) {
        	mExceptionListener.exceptionCaught();
        }
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
