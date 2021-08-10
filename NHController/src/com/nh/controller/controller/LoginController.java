package com.nh.controller.controller;

import com.nh.controller.service.ConnectionInfoMapperService;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.GlobalDefine.AUCTION_INFO;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.controller.utils.SharedPreference;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 로그인
 *
 * @author jhlee
 */
public class LoginController implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // get ResMsg
        if (resources != null) {
            mResMsg = resources;
        }
        mBtnConnection.setOnMouseClicked(event -> onConnectServer(event));
    }
    
    /**
     * 구성 설정
     */
    public void initConfiguration() {
        CommonUtils.getInstance().canMoveStage(mStage, null);
    }

    public void onConnectServer(MouseEvent event) {

		mIpTextField.setText(AUCTION_INFO.AUCTION_HOST);
        mPortTextField.setText(Integer.toString(AUCTION_INFO.AUCTION_PORT));
        mIdTextField.setText(AUCTION_INFO.AUCTION_MEMBER);

        if (!mIpTextField.getText().isEmpty() && !mPortTextField.getText().isEmpty() && !mIdTextField.getText().isEmpty()) {

            CommonUtils.getInstance().showLoadingDialog(mStage, mResMsg.getString("msg.connection"));

            String ip = mIpTextField.getText().toString().trim();
            int port = Integer.parseInt(mPortTextField.getText().toString().trim());
            String id = mIdTextField.getText().toString().trim();

            MoveStageUtil.getInstance().onConnectServer(mStage, ip, port, id);

        } else {
            CommonUtils.getInstance().showAlertPopupOneButton(mStage, "접속 정보를 입력해주세요.", mResMsg.getString("popup.btn.close"));
        }

    }

}
