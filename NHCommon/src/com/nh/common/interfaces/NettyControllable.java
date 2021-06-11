package com.nh.common.interfaces;

import com.nh.common.AuctionShareNettyClient;
import com.nh.share.server.models.AbsenteeUserInfo;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.AuctionStatus;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.server.models.CurrentSetting;
import com.nh.share.server.models.ExceptionCode;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.ResponseConnectionInfo;
import com.nh.share.server.models.ResponseEntryInfo;
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

	public void onAuctionCountDown(AuctionCountDown auctionCountDown); // 경매 시작 카운트 다운 정보

	public void onAuctionStatus(AuctionStatus auctionStatus); // 경매 상태 정보 전송

	public void onCurrentSetting(CurrentSetting currentSetting); // 경매 환경 설정 정보

	public void onResponseCarInfo(ResponseEntryInfo responseCarInfo); // 출품 정보

	public void onToastMessage(ToastMessage toastMessage); // 메시지 전송

	public void onConnectorInfo(BidderConnectInfo bidderConnectInfo); // 응찰자 접속 정보

	public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo); // 접속 정보 응답

	public void onFavoriteCarInfo(FavoriteEntryInfo favoriteCarInfo); // 관심 출품 정보

	public void onAbsenteeUserInfo(AbsenteeUserInfo absenteeUserInfo); // 부재자 입찰 참여 여부 정보

	public void onExceptionCode(ExceptionCode exceptionCode); // 예외 상황 전송

	public void onConnectionException(int port); // netty Connection Exception 시 전송

	public void onChannelInactive(int port); // 서버와 연결 끊어졌을경우

	public void exceptionCaught(int port); // 서버와 연결 끊어졌을경우

	public void onCheckSession(ChannelHandlerContext ctx, AuctionCheckSession auctionCheckSession); // 경매 서버 접속 유효 확인 요청
}
