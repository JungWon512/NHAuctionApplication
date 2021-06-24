package com.nh.common.interfaces;

import com.nh.common.AuctionShareNettyClient;
import com.nh.share.common.models.AuctionResult;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.CancelBidding;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.ToastMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

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

	public void onActiveChannel(Channel channel); // Channel Active 상태 반환

	public void onActiveChannel();

	public void onAuctionStatus(AuctionStatus auctionStatus); // 현재 경매 상태

	public void onCurrentEntryInfo(CurrentEntryInfo currentEntryInfo); // 현재 출품 정보

	public void onAuctionCountDown(AuctionCountDown auctionCountDown); // 경매 시작 카운트 다운 정보

	public void onBidding(Bidding bidding); // 응찰 정보

	public void onCancelBidding(CancelBidding cancelBidding); // 응찰 취소

	public void onToastMessage(ToastMessage toastMessage); // 메시지 전송

	public void onFavoriteEntryInfo(FavoriteEntryInfo favoriteEntryInfo); // 관심 출품 정보

	public void onAuctionResult(AuctionResult auctionResult); // 경매 낙유찰 결과 전송

	public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo); // 접속 정보 인증 응답

	public void onResponseCode(ResponseCode responseCode); // 응답 코드 전송

	public void onConnectionException(int port); // netty Connection Exception 시 전송

	public void onChannelInactive(int port); // 서버와 연결 끊어졌을경우

	public void exceptionCaught(int port); // 서버와 연결 끊어졌을경우

	public void onCheckSession(ChannelHandlerContext ctx, AuctionCheckSession auctionCheckSession); // 경매 서버 접속 유효 확인 요청
}
