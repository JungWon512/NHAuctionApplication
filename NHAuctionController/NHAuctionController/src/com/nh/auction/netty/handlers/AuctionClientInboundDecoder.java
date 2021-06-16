package com.nh.auction.netty.handlers;

import java.net.InetSocketAddress;
import java.util.List;

import com.nh.auction.interfaces.FromAuctionServer;
import com.nh.auction.interfaces.FromCommon;
import com.nh.auction.interfaces.NettyControllable;
import com.nh.auction.netty.parser.ServerMessageParser;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public final class AuctionClientInboundDecoder extends MessageToMessageDecoder<String> {

	private final NettyControllable mController;

	public AuctionClientInboundDecoder(NettyControllable controller) {
		mController = controller;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		super.channelActive(ctx);

		if (mController != null) {
			mController.onActiveChannel(ctx.channel());
		}
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, String message, List<Object> out) throws Exception {
		switch (message.charAt(0)) {
		case FromAuctionServer.ORIGIN:
			out.add(ServerMessageParser.parse_s(message));
			break;
		case FromCommon.ORIGIN:
			out.add(ServerMessageParser.parse_a(message));
			break;
		default:
			break;
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
		mController.onChannelInactive(address.getPort()); // 서버와 연결 끊어졌을경우
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