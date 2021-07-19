package com.nh.auctionserver.netty.handlers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.share.common.CommonMessageParser;
import com.nh.share.common.interfaces.FromAuctionCommon;
import com.nh.share.controller.ControllerMessageParser;
import com.nh.share.controller.interfaces.FromAuctionController;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * 메시지 형태에 따라서 각 객체로 변환 후 Routing 해준다.
 *
 */
@Sharable
public final class AuctionServerInboundDecoder extends MessageToMessageDecoder<String> {
    private final Logger mLogger = LoggerFactory.getLogger(AuctionServerInboundDecoder.class);

    public AuctionServerInboundDecoder() {
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, String message, List<Object> out) throws Exception {
        switch (message.charAt(0)) {
        case FromAuctionCommon.ORIGIN:
            FromAuctionCommon commonParsedMessage = CommonMessageParser.parse(message);
            out.add(commonParsedMessage);
            break;
        case FromAuctionController.ORIGIN:
        	System.out.println("111111111111111111");
        	System.out.println("message : " + message);
            FromAuctionController controllerParsedMessage = ControllerMessageParser.parse(message);
            out.add(controllerParsedMessage);
            break;
        default:
            break;
        }
    }
}
