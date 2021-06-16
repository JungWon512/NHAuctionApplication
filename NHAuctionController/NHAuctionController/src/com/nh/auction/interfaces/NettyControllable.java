package com.nh.auction.interfaces;

import com.nh.auction.models.AuctionCheckSession;
import com.nh.auction.models.AuctionCountDown;
import com.nh.auction.models.Bidding;
import com.nh.auction.models.ConnectionInfo;
import com.nh.auction.models.ExceptionCode;
import com.nh.auction.models.ToastMessage;

import io.netty.channel.Channel;

/**
 * Netty 클라이언트가 어플리케이션을 조정하기 위한 인터페이스
 * 
 * 
 * 사용 예)
 * 
 * class NextBidController implements NettyControllable {
 * 
 * @Override public void setNextCar(NextCar nextCar) {
 *           carLabel.setText(nextCar.getCarName()); } }
 * 
 *           NextBidController controller = new NextBidController();
 *           AuctionShareNettyClient.Builder builder = new
 *           AcutionShareNettyClient.Builder("123.456.789.000", 12345);
 *           builder.setController(controller); builder.buildAndRun();
 * 
 * 
 * @see {@link AuctionShareNettyClient.Builder#setController()}
 */
public interface NettyControllable {

	public void onActiveChannel(Channel channel);								// Channel Active 상태 반환

	public void onChannelInactive(int port);									// 서버와 연결 끊어졌을경우

	public void exceptionCaught(int port);										// 서버와 연결 끊어졌을경우

	public void onToastMessage(ToastMessage toastMessage);						// 메시지 전송 처리

	public void onExceptionCode(ExceptionCode exceptionCode);					// 예외 상황 전송 처리
	
	public void onConnectionInfo(ConnectionInfo connectionInfo);				// 접속자 정보 인증 처리 요청 처리

	public void onAuctionCheckSession(AuctionCheckSession auctionCheckSession);	// 경매 서버 접속 정보 유효 확인

	public void onAuctionCountDown(AuctionCountDown auctionCountDown);			// 경매 시작 카운트 다운

	public void onBidding(Bidding bidding);										// 경매 응찰 처리
}
