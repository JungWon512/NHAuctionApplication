package com.nh.auction.netty;

import com.nh.auction.interfaces.NettyControllable;
import com.nh.auction.interfaces.NettySendable;
import com.nh.auction.netty.handlers.AuctionClientDecodedHandler;
import com.nh.auction.netty.handlers.AuctionClientInboundDecoder;
import com.nh.auction.utils.GlobalDefine.NETTY_INFO;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class AuctionNettyClient {

	private int port;

	private EventLoopGroup group;

	private Channel channel;

	private AuctionNettyClient(Builder builder) {
		this.port = builder.port;
		createNettyClient(builder.host, builder.port, builder.controller);
	}

	private void createNettyClient(String host, int port, NettyControllable controller) {

		group = new NioEventLoopGroup();

		try {

			Bootstrap b = new Bootstrap();

			b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {

							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast(new AuctionClientInboundDecoder(controller));
							pipeline.addLast(new DelimiterBasedFrameDecoder(NETTY_INFO.NETTY_MAX_FRAME_LENGTH,
									Delimiters.lineDelimiter()));
							pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
							pipeline.addLast(new AuctionClientInboundDecoder(controller));
							pipeline.addLast(new AuctionClientDecodedHandler(controller));
							pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));

						}
					});

			channel = b.connect(host, port).sync().channel();

		} catch (Exception e) {
			e.printStackTrace();
			stopClient();
		}
	}

	/**
	 * 객체를 송신할 때 사용한다.
	 * 
	 * @param message 보낼 객체
	 */
	public void sendMessage(NettySendable object) {
		sendMessage(object.getEncodedMessage());
	}

	/**
	 * 문자열을 송신할 때 사용한다.
	 * 
	 * @param message 보낼 문자열
	 */
	public void sendMessage(String message) {
		channel.writeAndFlush(message + "\r\n");
	}

	/**
	 * 네티 종료
	 */
	public void stopClient() {
		group.shutdownGracefully();
	}

	/**
	 * 접속 요청 포트
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * 접속 정보 host(ip) / port
	 * 
	 * @author jhlee
	 *
	 */
	public static class Builder {

		private final String host;
		private final int port;
		private NettyControllable controller;

		public Builder(String host, int port) {
			this.host = host;
			this.port = port;
		}

		public Builder setController(NettyControllable controller) {
			this.controller = controller;
			return this;
		}

		public AuctionNettyClient buildAndRun() {
			return new AuctionNettyClient(this);
		}

	}
}
