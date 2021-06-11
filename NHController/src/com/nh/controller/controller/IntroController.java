package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.MoveStageUtil;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class IntroController implements Initializable {

    private Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FXML
    private AnchorPane mRoot;
    @FXML
    private Label mLabelVersion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mLogger.debug("" + getClass().getName());

        mLabelVersion.setText(GlobalDefine.RELEASE_VERION);

        Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Stage introStage = (Stage) mRoot.getScene().getWindow();
                        MoveStageUtil.getInstance().moveLoginStage(introStage); // LoginView로 이동
                    }
                });

            }
        };

        timer.schedule(timerTask, 1000);
    }
}
