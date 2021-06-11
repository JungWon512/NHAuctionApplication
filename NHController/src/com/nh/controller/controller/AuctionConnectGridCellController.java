package com.nh.controller.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * 
 * @ClassName AuctionConnectGridCellController.java
 * @Description 경매 접속자 모티너링 컨트롤러 클래스
 * @since 2019.11.25
 */
public class AuctionConnectGridCellController implements Initializable {

    private final Logger mLogger = LogManager.getLogger(AuctionConnectGridCellController.class);

    private ResourceBundle mCurrentResources;

    @FXML
    private Label mMemberIdLabel;
    @FXML
    private Label mConnectChannelLabel;
    @FXML
    private VBox mBackGroundVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        mCurrentResources = resources;
    }

    public void setBidding(boolean enable) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (enable) {
                    mBackGroundVBox.setBackground(
                            new Background(new BackgroundFill(Paint.valueOf("#15a9ee"), CornerRadii.EMPTY, Insets.EMPTY)));
                    mMemberIdLabel.setTextFill(Color.WHITE);
                    mConnectChannelLabel.setTextFill(Color.WHITE);
                } else {
                    mBackGroundVBox.setBackground(
                            new Background(new BackgroundFill(Paint.valueOf("#31bf45"), CornerRadii.EMPTY, Insets.EMPTY)));
                    mMemberIdLabel.setTextFill(Color.WHITE);
                    mConnectChannelLabel.setTextFill(Color.WHITE);
                }
            }
        });
    }

    public void setConnectData(String memberId, String connectChannel) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mMemberIdLabel.setText(memberId);
                mConnectChannelLabel.setText("(" + connectChannel +  ")");
            }
        });
    }

    public void setDisconnect(String memberId, String connectChannel) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mMemberIdLabel.setText(memberId);
                mConnectChannelLabel.setText("(" + connectChannel +  ")");
                mBackGroundVBox.setBackground(
                        new Background(new BackgroundFill(Paint.valueOf("#777777"), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });
    }
}
