package com.nh.controller.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.nh.controller.utils.CommonUtils;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * 환경설정
 *
 * @author jhlee
 */
public class SettingController implements Initializable {

	private Stage mStage;
	private ResourceBundle mResMsg;

	@FXML
	private TextField mIpTextField, // ip
			mPortTextField, // port
			mIdTextField; // id

	@FXML
	private Button mBtnConnection;

	/**
	 * setStage
	 *
	 * @param stage
	 */
	public void setStage(Stage stage) {
		mStage = stage;
	}

	 /**
     * 구성 설정
     */
    public void initConfiguration() {
        CommonUtils.getInstance().canMoveStage(mStage, null);
    }

    
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// get ResMsg
		if (resources != null) {
			mResMsg = resources;
		}

	}

}
