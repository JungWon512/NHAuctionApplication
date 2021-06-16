package com.nh.auction.netty.handlers;

import com.nh.auction.interfaces.NettyControllable;
import com.nh.auction.models.AuctionCheckSession;
import com.nh.auction.models.AuctionCountDown;
import com.nh.auction.models.Bidding;
import com.nh.auction.models.ConnectionInfo;
import com.nh.auction.models.ExceptionCode;
import com.nh.auction.models.ToastMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * handler
 * 
 * @author jhlee
 */
public class AuctionClientDecodedHandler extends SimpleChannelInboundHandler<Object> {

	private final NettyControllable mController;

	public AuctionClientDecodedHandler(NettyControllable controller) {
		this.mController = controller;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object object) throws Exception {

		if (mController != null) {

			// 접속자 정보 인증 처리 요청 처리
			if (object instanceof ConnectionInfo) {
				mController.onConnectionInfo((ConnectionInfo) object);
			}

			// 경매 서버 접속 정보 유효 확인
			if (object instanceof AuctionCheckSession) {
				mController.onAuctionCheckSession((AuctionCheckSession) object);
			}

			// 경매 시작 카운트 다운
			if (object instanceof AuctionCountDown) {
				mController.onAuctionCountDown((AuctionCountDown) object);
			}

			// 경매 응찰 처리
			if (object instanceof Bidding) {
				mController.onBidding((Bidding) object);
			}

			// 메시지 전송 처리
			if (object instanceof ToastMessage) {
				mController.onToastMessage((ToastMessage) object);
			}

			// 예외 상황 전송 처리
			if (object instanceof ExceptionCode) {
				mController.onExceptionCode((ExceptionCode) object);
			}

		}
	}
}
