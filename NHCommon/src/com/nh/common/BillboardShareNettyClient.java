package com.nh.common;

import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.NettyControllable;
import com.nh.share.interfaces.NettySendable;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class BillboardShareNettyClient {

    private Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private int port;
    private EventLoopGroup group;
    private Channel channel;

    private BillboardShareNettyClient(Builder builder) {
        this.port = builder.port;
        createNettyClient(builder.host, builder.port, builder.controller);
    }

    private void createNettyClient(String host, int port, NettyControllable controller) {
        group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        public void initChannel(NioDatagramChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            //encoder
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
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
     * @param object 보낼 객체
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
     * @param listener
     * @MethodName stopClient
     * @Description 종료 후 결과 콜백
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
     *
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

        public BillboardShareNettyClient buildAndRun() {
            return new BillboardShareNettyClient(this);
        }
    }
}
