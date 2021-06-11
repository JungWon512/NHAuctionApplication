package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nh.controller.utils.MoveStageUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * 
 * @ClassName AuctionListCellController.java
 * @Description 경매 목록 리스트 뷰 Cell 아이템. 제어프로그램, 접속자, 낙/유찰 모니터링 프로그램으로 이동 처리.
 * @anthor ishift
 * @since
 */
public class AuctionListCellController extends AnchorPane {

    private final Logger mLogger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    @FXML
    private AnchorPane mAnchorPaneRoot;

    @FXML
    private Button mBtnAuctionType; // 경매 타입 (실시간/SPOT)

    @FXML
    private Button mBtnAuctionLaneName; // 경매 레인 명

    @FXML
    private Label mLabelAuctionName; // 경매명

    @FXML
    private Label mLabelAuctionDate; // 경매 시작 일시

    @FXML
    public Button mBtnActionControlProgram; // 제어프로그램 버튼

    @FXML
    public Button mBtnActionConnectMonitor; // 접속자 모니터링 버튼

    @FXML
    public Button mBtnAuctionResultMonitoringBtn; // 낙/유찰 모니터링 버튼

    @FXML
    public VBox mTtsInfoVBox; // TTS 생성 실패 정보 영역

    @FXML
    public Label mTtsFailInfoLabel; // TTS 생성 실패 건수 표시 라벨

    @FXML
    public Button mBtnRetryCreateTts; // TTS 재생성 요청 버튼

    public AuctionListCellController() {
        MoveStageUtil.getInstance().loadAuctionListCellFXML(this);
    }

    public AnchorPane getAnchorPaneRoot() {
        return mAnchorPaneRoot;
    }

    public void setBtnAuctionTypeText(String text) {
        mBtnAuctionType.setText(text);
    }

    public void setBtnAuctionLaneNameText(String text) {
        mBtnAuctionLaneName.setText(text);
    }

    public void setLabelAuctionNameText(String text) {
        mLabelAuctionName.setText(text);
    }

    public void setLabelAuctionDateText(String text) {
        mLabelAuctionDate.setText(text);
    }

    public void setTtsInfoVBoxVisible(boolean visible) {
        if (visible) {
            mTtsInfoVBox.setVisible(true);
        } else {
            mTtsInfoVBox.setVisible(false);
        }
    }

    public void setTtsFailInfoLabel(String info) {
        mTtsFailInfoLabel.setText(info);
    }
}
