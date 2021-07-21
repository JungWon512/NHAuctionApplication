package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.utils.JwtCertTokenUtils;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@Sharable
public final class AuctionUserDuplexHandler extends ChannelDuplexHandler {
	private final Logger mLogger = LoggerFactory.getLogger(AuctionUserDuplexHandler.class);
	private Map<ChannelId, ConnectionInfo> mConnectorInfoMap;

	public AuctionUserDuplexHandler(Map<ChannelId, ConnectionInfo> connectorInfoMap) {
		// TODO Auto-generated constructor stub
		mConnectorInfoMap = connectorInfoMap;
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;

			// 600초(10분)(관전자에 한해서 Read Time 적용)
//			if (mConnectorInfoMap.containsKey(ctx.channel().id())) {
//				if (mConnectorInfoMap.get(ctx.channel().id()).getWatcher().equals("Y")) {
//					if (e.state() == IdleState.READER_IDLE) {
//						mLogger.debug("Expire watch time." + "Exprie User Number : "
//								+ mConnectorInfoMap.get(ctx.channel().id()).getUserNo());
//						ctx.channel().writeAndFlush(
//								new ResponseConnectionInfo(GlobalDefineCode.CONNECT_EXPIRE_WATCHER).getEncodedMessage()
//										+ "\r\n");
//					}
//				}
//			}

			// 10초(모든 접속자에 한해서 Read Time 적용)
			if (e.state() == IdleState.READER_IDLE) {
				if (mConnectorInfoMap.containsKey(ctx.channel().id())) {
					mLogger.debug("Expire watch time." + "Exprie User Number : " + JwtCertTokenUtils.getInstance()
							.getUserMemNum(mConnectorInfoMap.get(ctx.channel().id()).getAuthToken()));
				}
				ctx.channel().close();
			}

			if (e.state() == IdleState.WRITER_IDLE) {
				ctx.channel().writeAndFlush(new AuctionCheckSession().getEncodedMessage() + "\r\n");
			}
		}
	}
}
