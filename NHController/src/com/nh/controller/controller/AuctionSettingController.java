package com.nh.controller.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nh.share.server.models.CurrentSetting;
import com.nh.share.utils.CommonUtils;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class AuctionSettingController implements Initializable {
    private final Logger mLogger = LogManager.getLogger(AuctionSettingController.class);

    @FXML
    private RadioButton mRadioStartOn; // 경매 자동 시작 ON
    
    @FXML
    private RadioButton mRadioStartOff; // 경매 자동 시작 OFF
    
    @FXML
    private TextField mTextFieldBaseStartPrice; // 기준금액

    @FXML
    private TextField mTextFieldMoreRisePrice; // 기준금액 이상 상승가

    @FXML
    private TextField mTextFieldBelowRisePrice; // 기준금액 미만 상승가

    @FXML
    private TextField mTextFieldBiddingTime; // 경매 진행 시간(ms)

    @FXML
    private TextField mTextFieldBiddingAdditionalTime; // 낙찰 지연 시간 (ms)

    @FXML
    private TextField mTextFieldBiddingIntervalTime; // 다음 시작 간격 (ms)

    @FXML
    private TextField mTextFieldMaxAutoUpCount; // 자동상승 횟수

    @FXML
    public Button mBtnSaveSetting; // 확인

    private Stage mStage;
    private CurrentSetting mCurrentSetting;

    private ResourceBundle mCurrentResources;
    
    private ToggleGroup mAutoStartGroup = new ToggleGroup();
    
    private boolean mAuctionStartAutoFlag; // 경매 자동 시작 

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        mCurrentResources = resources;
        CommonUtils.getInstance().setTextStyleOnly_Number(mTextFieldBaseStartPrice);
        CommonUtils.getInstance().setTextStyleOnly_Number(mTextFieldMoreRisePrice);
        CommonUtils.getInstance().setTextStyleOnly_Number(mTextFieldBelowRisePrice);
        CommonUtils.getInstance().setTextStyleOnly_Number(mTextFieldBiddingTime);
        CommonUtils.getInstance().setTextStyleOnly_Number(mTextFieldBiddingAdditionalTime);
        CommonUtils.getInstance().setTextStyleOnly_Number(mTextFieldBiddingIntervalTime);
        CommonUtils.getInstance().setTextStyleOnly_Number(mTextFieldMaxAutoUpCount);
        
        // 경매 자동 시작 On.Off
        mRadioStartOn.setUserData(true);
        mRadioStartOff.setUserData(false);
        mRadioStartOn.setToggleGroup(mAutoStartGroup);
        mRadioStartOff.setToggleGroup(mAutoStartGroup);
        mAutoStartGroup.selectedToggleProperty().addListener(mAutoStartGroupListener);
    }

    public void setStage(Stage stage, CurrentSetting currentSetting, boolean isAuctionStartAuto) {
        mStage = stage;
        mCurrentSetting = currentSetting;
        mStage.setTitle(mCurrentResources.getString("str.auction.controller.setting"));
        
        mAuctionStartAutoFlag = isAuctionStartAuto;
        
        // 경매 자동 시작 
        if (isAuctionStartAuto) {
            mAutoStartGroup.selectToggle(mRadioStartOn);
        }else {
            mAutoStartGroup.selectToggle(mRadioStartOff);
        }
        
        if (currentSetting != null) {
            // 기준금액
            String baseStartPrice = "";
            if (currentSetting.getBaseStartPrice() != null && currentSetting.getBaseStartPrice().length() > 0) {
                baseStartPrice = currentSetting.getBaseStartPrice();
            }
            mTextFieldBaseStartPrice.setText(baseStartPrice);

            // 기준금액 이상 상승가
            String moreRisePrice = "";
            if (currentSetting.getMoreRisePrice() != null && currentSetting.getMoreRisePrice().length() > 0) {
                moreRisePrice = currentSetting.getMoreRisePrice();
            }
            mTextFieldMoreRisePrice.setText(moreRisePrice);

            // 기준금액 미만 상승가
            String belowRisePrice = "";
            if (currentSetting.getBelowRisePrice() != null && currentSetting.getBelowRisePrice().length() > 0) {
                belowRisePrice = currentSetting.getBelowRisePrice();
            }
            mTextFieldBelowRisePrice.setText(belowRisePrice);

            // 경매 진행 시간
            String biddingTime = "";
            if (currentSetting.getBiddingTime() != null && currentSetting.getBiddingTime().length() > 0) {
                biddingTime = currentSetting.getBiddingTime();
            }
            mTextFieldBiddingTime.setText(biddingTime);

            // 낙찰 지연 시간
            String biddingAdditionalTime = "";
            if (currentSetting.getBiddingAdditionalTime() != null
                    && currentSetting.getBiddingAdditionalTime().length() > 0) {
                biddingAdditionalTime = currentSetting.getBiddingAdditionalTime();
            }
            mTextFieldBiddingAdditionalTime.setText(biddingAdditionalTime);

            // 다음 시작 간격
            String biddingIntervalTime = "";
            if (currentSetting.getBiddingIntervalTime() != null
                    && currentSetting.getBiddingIntervalTime().length() > 0) {
                biddingIntervalTime = currentSetting.getBiddingIntervalTime();
            }
            mTextFieldBiddingIntervalTime.setText(biddingIntervalTime);

            // 자동 상승 횟수
            String maxAutoUpCount = "";
            if (currentSetting.getMaxAutoUpCount() != null && currentSetting.getMaxAutoUpCount().length() > 0) {
                maxAutoUpCount = currentSetting.getMaxAutoUpCount();
            }
            mTextFieldMaxAutoUpCount.setText(maxAutoUpCount);
        }
    }
    
    /*
     * 경매 자동 시작 On/Off 라디오 버튼 리스너 
     */
    private ChangeListener<Toggle> mAutoStartGroupListener = (observable, oldValue, newValue) -> {
        boolean newVal = (boolean) newValue.getUserData();
        if (newVal) {
            mAuctionStartAutoFlag = true;
        }else {
            mAuctionStartAutoFlag = false;
        }
    };

    @FXML
    private void onActionCancel() {
        mStage.close();
    }

    CurrentSetting getData() {

        // 기준금액
        String baseStartPrice = mTextFieldBaseStartPrice.getText();
        if (baseStartPrice != null && baseStartPrice.length() > 0) {
            mCurrentSetting.setBaseStartPrice(baseStartPrice);
        }

        // 기준금액 이상 상승가
        String moreRisePrice = mTextFieldMoreRisePrice.getText();
        if (moreRisePrice != null && moreRisePrice.length() > 0) {
            mCurrentSetting.setMoreRisePrice(moreRisePrice);
        }

        // 기준금액 미만 상승가
        String belowRisePrice = mTextFieldBelowRisePrice.getText();
        if (belowRisePrice != null && belowRisePrice.length() > 0) {
            mCurrentSetting.setBelowRisePrice(belowRisePrice);
        }

        // 경매 진행 시간
        String biddingTime = mTextFieldBiddingTime.getText();
        if (biddingTime != null && biddingTime.length() > 0) {
            mCurrentSetting.setBiddingTime(biddingTime);
        }

        // 낙찰 지연 시간
        String biddingAdditionalTime = mTextFieldBiddingAdditionalTime.getText();
        if (biddingAdditionalTime != null && biddingAdditionalTime.length() > 0) {
            mCurrentSetting.setBiddingAdditionalTime(biddingAdditionalTime);
        }

        // 다음 시작 간격
        String biddingIntervalTime = mTextFieldBiddingIntervalTime.getText();
        if (biddingIntervalTime != null && biddingIntervalTime.length() > 0) {
            mCurrentSetting.setBiddingIntervalTime(biddingIntervalTime);
        }

        // 자동 상승 횟수
        String maxAutoUpCount = mTextFieldMaxAutoUpCount.getText();
        if (maxAutoUpCount != null && maxAutoUpCount.length() > 0) {
            mCurrentSetting.setMaxAutoUpCount(maxAutoUpCount);
        }

        return mCurrentSetting;
    }
    
    boolean getAutoStartFlag() {
        return mAuctionStartAutoFlag;
    }
}
