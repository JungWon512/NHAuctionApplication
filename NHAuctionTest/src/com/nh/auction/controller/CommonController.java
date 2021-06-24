package com.nh.auction.controller;

import java.lang.invoke.MethodHandles;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.common.interfaces.NettyClientShutDownListener;
import com.nh.common.interfaces.NettyControllable;
import com.nh.share.common.models.AuctionResult;
import com.nh.share.common.models.AuctionStatus;
import com.nh.share.common.models.Bidding;
import com.nh.share.common.models.CancelBidding;
import com.nh.share.common.models.ResponseConnectionInfo;
import com.nh.share.server.models.AuctionCheckSession;
import com.nh.share.server.models.AuctionCountDown;
import com.nh.share.server.models.CurrentEntryInfo;
import com.nh.share.server.models.ResponseCode;
import com.nh.share.server.models.FavoriteEntryInfo;
import com.nh.share.server.models.ToastMessage;
import com.nh.share.setting.AuctionShareSetting;
import com.nh.share.utils.CommonUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CommonController implements NettyControllable {

    private Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    protected ResourceBundle mCurrentResources;

    @FXML
    protected AnchorPane mRoot;

    protected Stage mStage;

    protected int connectionClientSize = 0; // 접속된 클라이언트 수

    private int selectedMainPorts = 0;

    public CommonController() {
        BaseAuction.getAuctionInstance().setViewListener(this);
    }

    protected void setResources(ResourceBundle reBundle) {
        mCurrentResources = reBundle;
    }

    /**
     * @MethodName auctionConnect
     * @Description 네티 접속
     *
     * @param node
     */
    protected boolean auctionConnect(Node node) {

        if (BaseAuction.getAuctionInstance().isNettyReady()) {

            selectedMainPorts = 0;

            for (int i = 0; node.getStyleClass().size() > i; i++) {
                if (node.getStyleClass().get(i).contains("port_")) {
                    String[] arr = node.getStyleClass().get(i).split("_");
                    selectedMainPorts = Integer.parseInt(arr[1]);
                    break;
                }
            }

            String host = AuctionShareSetting.CLIENT_HOST;

            if (selectedMainPorts <= 0) {
                mLogger.debug("포트 정보가 없습니다.");
                return false;
            }

            BaseAuction.getAuctionInstance().onAuctionConnect(host, selectedMainPorts);
            
        } else {
            dismissLoadingDialog();
            mLogger.debug("isNettyReade False");
        }

        return true;
    }

    protected void onAuctionAllClose() {

        connectionClientSize = BaseAuction.getAuctionInstance().clients.size();

        if (connectionClientSize > 0) {
            // 네티 정상 종료 콜백
            BaseAuction.getAuctionInstance().onAuctionAllClose(new NettyClientShutDownListener() {
                @Override
                public void onShutDown(int port) {

                    connectionClientSize--;

                    if (connectionClientSize == 0) {
                        mLogger.debug("[NETTY CLEAR VARIABLE]");
                        BaseAuction.getAuctionInstance().setClearVariable();
                    }
                }
            });
        }
    }

    /**
     * @MethodName onAuctionClose
     * @Description 모든 네티 종료
     *
     */
    protected void onAuctionAllCloseAndMain() {

        connectionClientSize = BaseAuction.getAuctionInstance().clients.size();

        if (connectionClientSize > 0) {
            // 네티 정상 종료 콜백
            BaseAuction.getAuctionInstance().onAuctionAllClose(new NettyClientShutDownListener() {
                @Override
                public void onShutDown(int port) {

                    connectionClientSize--;

                    if (connectionClientSize == 0) {
                        mLogger.debug("[NETTY CLEAR VARIABLE]");
                        BaseAuction.getAuctionInstance().setClearVariable();

                        Platform.runLater(() -> {
                            dismissLoadingDialog();
                        });
                    }
                }
            });
        } else {
            Platform.runLater(() -> {
                dismissLoadingDialog();
            });
        }
    }

    protected Optional<ButtonType> showAlertPopupTwoButton(String message) {
        return CommonUtils.getInstance().showAlertPopupTwoButton(mStage, message,
                mCurrentResources.getString("membership.fee.btn.ok"),
                mCurrentResources.getString("membership.fee.btn.cancel"));
    }

    protected Optional<ButtonType> showAlertPopupOneButton(String message) {
        return CommonUtils.getInstance().showAlertPopupOneButton(mStage, message,
                mCurrentResources.getString("membership.fee.btn.ok"));
    }

    /**
     * 메인 / 경매 화면 네티 공통 로직 처리
     */
    @Override
    public void onActiveChannel() {
    }

    @Override
    public void onActiveChannel(Channel channel) {
    }

    @Override
    public void onAuctionCountDown(AuctionCountDown auctionCountDown) {
    }

    @Override
    public void onAuctionStatus(AuctionStatus auctionStatus) {
    }

    @Override
    public void onCurrentEntryInfo(CurrentEntryInfo currentEntryInfo) {
    }

    @Override
    public void onToastMessage(ToastMessage toastMessage) {
    }

    @Override
    public void onResponseConnectionInfo(ResponseConnectionInfo responseConnectionInfo) {

    }

    @Override
    public void onFavoriteEntryInfo(FavoriteEntryInfo favoriteEntryInfo) {

    }

    @Override
	public void onBidding(Bidding bidding) {
		
	}

	@Override
    public void onResponseCode(ResponseCode exceptionCode) {
        dismissLoadingDialog();
    }

    @Override
    public void onConnectionException(int port) {
        dismissLoadingDialog();
    }

    @Override
	public void onCancelBidding(CancelBidding cancelBidding) {
		
	}

	@Override
	public void onAuctionResult(AuctionResult auctionResult) {
		
	}

	protected void dismissLoadingDialog() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                CommonUtils.getInstance().dismissLoadingDialog();
            }
        });
    }

    @Override
    public void onChannelInactive(int port) {
    }

    @Override
    public void exceptionCaught(int port) {
    }

    @Override
    public void onCheckSession(ChannelHandlerContext ctx, AuctionCheckSession auctionCheckSession) {
        // TODO Auto-generated method stub

    }

    /**
     * @MethodName entryDataStringNullCheck
     * @Description
     *
     * @param entryString
     * @param comma
     * @return
     */
    protected String entryDataStringNullCheck(String entryString, boolean comma) {
        if (entryString != null && entryString.length() > 0) {
            if (comma) {
                return NumberFormat.getInstance().format(Integer.parseInt(entryString));
            } else {
                return entryString;
            }
        } else {
            return "";
        }
    }

}
