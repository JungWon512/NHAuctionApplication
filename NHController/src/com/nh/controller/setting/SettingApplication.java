package com.nh.controller.setting;

import com.nh.controller.ControllerApplication;
import com.nh.controller.controller.AuctionController;
import com.nh.controller.controller.AuctionMessageController;
import com.nh.controller.controller.LoginController;
import com.nh.controller.interfaces.StringListener;
import com.nh.controller.model.SettingInfoData;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 경매 환경 설정
 * 
 * @author jhlee
 *
 */
public class SettingApplication {

	private static SettingApplication instance = null;
	
	private SettingInfoData mSettingInfoData = null;

	public static synchronized SettingApplication getInstance() {

		if (instance == null) {
			instance = new SettingApplication();
		}

		return instance;
	}

	/**
	 * 경매 환경 설정
	 * @return
	 */
	public synchronized SettingInfoData getInfo() {

		if (mSettingInfoData == null) {
			mSettingInfoData = new SettingInfoData();
		}
		
		return mSettingInfoData;
	}
	
	
}
