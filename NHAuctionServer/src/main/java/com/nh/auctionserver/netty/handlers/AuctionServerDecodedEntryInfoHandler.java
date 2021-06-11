package com.nh.auctionserver.netty.handlers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auctionserver.core.Auctioneer;
import com.nh.auctionserver.netty.AuctionServer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.common.models.ConnectionInfo;
import com.nh.share.common.models.EntryInfo;
import com.nh.share.server.models.ExceptionCode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;

public class AuctionServerDecodedEntryInfoHandler extends SimpleChannelInboundHandler<EntryInfo> {
    private final Logger mLogger = LoggerFactory.getLogger(AuctionServerDecodedEntryInfoHandler.class);

	private final AuctionServer mAuctionServer;
	private final Auctioneer mAuctionScheduler;

	private ChannelGroup mControllerChannels = null;
	private ChannelGroup mBidderChannels = null;
	private ChannelGroup mWatcherChannels = null;
	private ChannelGroup mAuctionResultMonitorChannels = null;
	private ChannelGroup mConnectionMonitorChannels = null;
	private Map<ChannelId, ConnectionInfo> mConnectorInfoMap;

    public AuctionServerDecodedEntryInfoHandler(AuctionServer auctionServer, Auctioneer auctionSchedule,
			Map<ChannelId, ConnectionInfo> connectorInfoMap, ChannelGroup controllerChannels,
			ChannelGroup bidderChannels, ChannelGroup watcherChannels, ChannelGroup auctionResultMonitorChannels,
			ChannelGroup connectionMonitorChannels) {
		mAuctionServer = auctionServer;
		mConnectorInfoMap = connectorInfoMap;
		mAuctionScheduler = auctionSchedule;
		mControllerChannels = controllerChannels;
		mBidderChannels = bidderChannels;
		mWatcherChannels = watcherChannels;
		mAuctionResultMonitorChannels = auctionResultMonitorChannels;
		mConnectionMonitorChannels = connectionMonitorChannels;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EntryInfo carInfo) throws Exception {
        if (mAuctionScheduler.getEntryCarInfo((carInfo).getEntrySeqNum()) != null) {
            ctx.channel().writeAndFlush(mAuctionScheduler.getCarInfo((carInfo).getEntrySeqNum())
                    .toResponseCarInfo().getEncodedMessage() + "\r\n");
        } else {
            ctx.channel().writeAndFlush(
                    new ExceptionCode(GlobalDefineCode.RESPONSE_REQUEST_NOT_RESULT_EXCEPTION).getEncodedMessage()
                            + "\r\n");
        }
    }
}
