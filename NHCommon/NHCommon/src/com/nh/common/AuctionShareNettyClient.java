package com.nh.common;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.handlers.AuctionClientDecodedAuctionResultHandler;
import com.nh.common.handlers.AuctionClientDecodedAuctionStatusHandler;
import com.nh.common.handlers.AuctionClientDecodedBiddingHandler;
import com.nh.common.handlers.AuctionClientDecodedCancelBiddingHandler;
import com.nh.common.handlers.AuctionClientDecodedCheckSessionHandler;
import com.nh.common.handlers.AuctionClientDecodedCountDownHandler;
import com.nh.common.handlers.AuctionClientDecodedCurrentEntryInfoHandler;
import com.nh.common.handlers.AuctionClientDecodedFavoriteEntryInfoHandler;
import com.nh.common.handlers.AuctionClientDecodedRequestAuctionResultHandler;
import com.nh.common.handlers.AuctionClientDecodedResponseCodeHandler;
import com.nh.common.handlers.AuctionClientDecodedResponseConnectionInfoHandler;
import com.nh.common.handlers.AuctionClientDecodedToastMessageHandler;
import com.nh.common.handlers.AuctionClientInboundDecoder;
import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.NettyControllable;
import com.nh.share.interfaces.NettySendable;
import com.nh.share.setting.AuctionShareSetting;

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
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 
 * Netty 클라이언트
 * 
 * 사용 예) AuctionShareNettyClient.Builder builder = new
 * AuctionShareNettyClient.Builder("123.456.789.000", 12345);
 * builder.setController(controller); AuctionShareNettyClient client =
 * builder.buildAndRun();
 * 
 * controller는 통신으로 수신한 String을 처리할 객체, NettyControllable을 구현하여야 함
 * 
 * @see {@link NettyControllable}
 *
 */
public class AuctionShareNettyClient {
	private Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private int port; // 테스트를 위해 port 확인용 변수, 운영 환경에서는 이용하지 않는다.
	private EventLoopGroup group;
	private Channel channel;

	private AuctionShareNettyClient(Builder builder) {
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
							/*
							 * 사설 인증서 - 사용시 주석 해제 SslContext sslContext =
							 * SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.
							 * INSTANCE).build(); SSLEngine engine = sslContext.newEngine(ch.alloc(),
							 * AuctionShareSetting.Server.SSL_HOST, AuctionShareSetting.Server.SSL_PORT);
							 * engine.setEnabledProtocols(new String[] {"TLSv1.2"}); pipeline.addLast(new
							 * SslHandler(engine));
							 */
							pipeline.addLast(new DelimiterBasedFrameDecoder(AuctionShareSetting.NETTY_MAX_FRAME_LENGTH,
									Delimiters.lineDelimiter()));
							// pipeline.addLast(new WriteTimeoutHandler(15));
							// pipeline.addLast(new ReadTimeoutHandler(15));
							pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
							pipeline.addLast(new AuctionClientInboundDecoder(controller));
							pipeline.addLast(new AuctionClientDecodedCurrentEntryInfoHandler(controller)); // 현재 출품 정보
							pipeline.addLast(new AuctionClientDecodedBiddingHandler(controller)); // 응찰 정보
							pipeline.addLast(new AuctionClientDecodedCountDownHandler(controller)); // 경매 시작 카운트 다운 기능
							pipeline.addLast(new AuctionClientDecodedFavoriteEntryInfoHandler(controller)); // 관심출품 정보
							pipeline.addLast(new AuctionClientDecodedAuctionStatusHandler(controller)); // 경매 상태 정보 전송
							pipeline.addLast(new AuctionClientDecodedToastMessageHandler(controller)); // 메시지 전송 처리
							pipeline.addLast(new AuctionClientDecodedResponseConnectionInfoHandler(controller)); // 접속응답처리
							pipeline.addLast(new AuctionClientDecodedResponseCodeHandler(controller)); // 예외 상황 전송
							pipeline.addLast(new AuctionClientDecodedCheckSessionHandler(controller)); // 경매 서버 접속 유효 확인
							pipeline.addLast(new AuctionClientDecodedAuctionResultHandler(controller)); // 낙유찰 정보
							pipeline.addLast(new AuctionClientDecodedCancelBiddingHandler(controller)); // 응찰 취소 정보
							pipeline.addLast(new AuctionClientDecodedRequestAuctionResultHandler(controller)); // 낙유찰 정보 요청


							pipeline.addFirst(new StringEncoder(CharsetUtil.UTF_8));
						}
					});
			channel = b.connect(host, port).sync().channel();
		} catch (Exception e) {
			controller.onConnectionException(port);
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

	public void stopClient() {
		group.shutdownGracefully();
	}

	/**
	 * @MethodName stopClient
	 * @Description 종료 후 결과 콜백
	 *
	 * @param listener
	 */
	public void stopClient(NettyClientShutDownListener listener) {
		Future<?> future = group.shutdownGracefully();
		future.addListener(new GenericFutureListener() {
			@Override
			public void operationComplete(Future future) throws Exception {
				if (listener != null) {
					listener.onShutDown(getPort());
				}
			}
		});
	}

	public int getPort() { // 테스트를 위해 port 확인용 변수, 운영 환경에서는 이용하지 않는다.
		return port;
	}

	public boolean isActive() {
		if (channel != null) {
			return channel.isActive();
		}

		return false;
	}

	public Channel getChannel() {
		return channel;
	}
	
	/**
	 * 접속 상태 확인
	 * @return
	 */
	public boolean isEmptyChannel() {
		if (channel != null) {
			return false;
		} else {
			return true;
		}
	}

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

		public AuctionShareNettyClient buildAndRun() {
			return new AuctionShareNettyClient(this);
		}
	}
}
