package com.nh.common;

import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.handlers.BillboardClientInboundHandler;
import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.NettyControllable;
import com.nh.share.code.GlobalDefineCode;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketEncoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class PdpShareNettyClient {

    private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private int port;
    private EventLoopGroup group;
    private Channel channel;

    private PdpShareNettyClient(Builder builder) {
        this.port = builder.port;
        createNettyClient(builder.host, builder.port, builder.controller);
    }

    private void createNettyClient(String host, int port, NettyControllable controller) {
        group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        public void initChannel(NioDatagramChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new BillboardClientInboundHandler(controller));
                            pipeline.addLast(new DatagramPacketEncoder<>(new StringEncoder(CharsetUtil.UTF_8)));
                        }
                    });
            channel = b.connect(host, port).sync().channel();
        } catch (Exception e) {
//            controller.onConnectionException(port);
            e.printStackTrace();
            stopClient();
        }
    }

    /**
     * 문자열을 송신할 때 사용한다.
     *
     * @param message 보낼 문자열
     */
    public void sendMessage(String message) {
        channel.writeAndFlush(datagramPacket(message, (InetSocketAddress) getChannel().remoteAddress()));
    }

    /**
     * String -> DatagramPacket 변환
     *
     * @param message
     * @param inetSocketAddress
     * @return DatagramPacket
     */
    public DatagramPacket datagramPacket(String message, InetSocketAddress inetSocketAddress) {
        ByteBuf dataBuf = Unpooled.copiedBuffer(
                message,
                Charset.forName(GlobalDefineCode.BILLBOARD_CHARSET));
        return new DatagramPacket(dataBuf, inetSocketAddress);
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
        future.addListener((GenericFutureListener) future1 -> {
            if (listener != null) {
                listener.onShutDown(getPort());
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

        public Builder(String host, String port) {
            this.host = host;
            this.port = Integer.parseInt(port);
        }

        public Builder setController(NettyControllable controller) {
            this.controller = controller;
            return this;
        }

        public PdpShareNettyClient buildAndRun() {
            return new PdpShareNettyClient(this);
        }
    }
}
