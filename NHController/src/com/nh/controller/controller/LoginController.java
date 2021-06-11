package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.controller.preferences.SharedPreference;
import com.nh.controller.utils.GlobalDefine;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.model.AuctionLoginResult;
import com.nh.share.api.request.ActionRequestAuctionLogin;
import com.nh.share.api.response.ResponseAuctionLogin;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.utils.CommonUtils;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    private Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FXML
    private AnchorPane mRoot;
    @FXML
    private TextField mTextFieldId; // 아이디 입력 필드
    @FXML
    private PasswordField mTextFieldPwd; // 패스워드 입력 필드
    @FXML
    private Button mBtnLogin; // 로그인 버튼
    @FXML
    private Label mLabelValidation; // 유효성 체크 문구
    @FXML
    private Label mLabelVersion; // 버전명

    private ResourceBundle mCurrentResources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        mLogger.debug("" + getClass().getName());
        mCurrentResources = resources;

        mTextFieldId.textProperty().addListener(mTextFieldChangeListener);
        mTextFieldPwd.textProperty().addListener(mTextFieldChangeListener);

        CommonUtils.getInstance().setTextStyle_Eng_Number(mTextFieldId);
        CommonUtils.getInstance().setTextStyle_Eng_Number_SpecialChar(mTextFieldPwd);

        CommonUtils.getInstance().setHideShowBehaviour(mLabelValidation, false);

        mBtnLogin.setOnAction(event -> requestLogin(event));
        mRoot.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                mBtnLogin.pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
                mBtnLogin.fire();
            }
        });

        mLabelVersion.setText(GlobalDefine.RELEASE_VERION);
    }

    /**
     * @MethodName mTextFieldIdtChangeListener
     * @Description 아이디 / 비밀번호 입력 필드 텍스트 변경 리스너
     *
     */
    ChangeListener<String> mTextFieldChangeListener = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

            if (newValue.length() > 0) {
                // 값 입력 시
                // validation 숨김
                CommonUtils.getInstance().setHideShowBehaviour(mLabelValidation, false);

                if (!mTextFieldId.getText().isEmpty() && !mTextFieldPwd.getText().isEmpty()) {
                    CommonUtils.getInstance().setNodeDisable(mBtnLogin, false);
                }

            } else {
                // 값 없음
                // 로그인 버튼 막음
                CommonUtils.getInstance().setNodeDisable(mBtnLogin, true);
            }
        }
    };

    /**
     * @MethodName requestLogin
     * @Description 로그인 API 실행
     *
     * @param event
     */
    private void requestLogin(ActionEvent event) {

        boolean resultValidation = false;

        resultValidation = checkLoginValidation();

        if (!resultValidation) {
            return;
        }

        String loginType = GlobalDefineCode.AUCTION_LOGIN_TYPE_MANAGER; // 로그인 종류
        String id = mTextFieldId.getText();// 아이디
        String pwd = CommonUtils.getInstance().encodingToBase64(mTextFieldPwd.getText()); // 비밀번호

        // TEST Code
        // SharedPreference.setUserLoginID(id);
        // Stage loginStage = (Stage) mRoot.getScene().getWindow();
        // MoveStageUtil.getInstance().moveAuctionListStage(loginStage);

        // API
        if (id.equals("D9000") && pwd.equals("YWRtaW4=")) {
            SharedPreference.setUserLoginID(id);
            SharedPreference.setMemberNum("D9000");
            Stage loginStage = (Stage) mRoot.getScene().getWindow();
            MoveStageUtil.getInstance().moveAuctionListStage(loginStage);
        } else {
            requestLoginApi(loginType, id, pwd);
        }
    }

    private void requestLoginApi(String loginType, String id, String pwd) {

        CommonUtils.getInstance().showLoadingDialog((Stage) mRoot.getScene().getWindow(),
                mCurrentResources.getString("str.wait"));

        ActionRuler.getInstance().addAction(new ActionRequestAuctionLogin(loginType, id, pwd, "", "", "", "",
                new ActionResultListener<ResponseAuctionLogin>() {
                    @Override
                    public void onResponseResult(ResponseAuctionLogin result) {
                        // 메인 화면으로 이동
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (result.getResult().size() > 0) {
                                    AuctionLoginResult loginResult = result.getResult().get(0);
                                    if (loginResult.getAuthResult().equals("success")
                                            && loginResult.getUserInfo().size() > 0) {
                                        SharedPreference.setUserLoginID(id);
                                        SharedPreference.setUserInfo(loginResult);
                                        Stage loginStage = (Stage) mRoot.getScene().getWindow();
                                        MoveStageUtil.getInstance().moveAuctionListStage(loginStage);

                                        CommonUtils.getInstance().dismissLoadingDialog();
                                    } else {
                                        String status = loginResult.getMemberStatus();

                                        CommonUtils.getInstance().dismissLoadingDialog();

                                        String mValidationMessage = result.getMessage();
                                        if (status != null && status.length() > 0
                                                && status.equals(GlobalDefineCode.AUCTION_LOGIN_STATUS_L003_6)) {
                                            mValidationMessage = mCurrentResources.getString("str.login.err.l003_6");
                                        } else if (status != null && status.length() > 0
                                                && status.equals(GlobalDefineCode.AUCTION_LOGIN_STATUS_L003_20)) {
                                            mValidationMessage = mCurrentResources.getString("str.login.err.l003_20");
                                        }

                                        CommonUtils.getInstance().showAlertPopupOneButton(
                                                (Stage) mRoot.getScene().getWindow(), mValidationMessage,
                                                mCurrentResources.getString("str.ok"));
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onResponseError(String message) {
                        mLogger.debug("RequestAuctionLogin error : " + message);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.getInstance().dismissLoadingDialog();
                                CommonUtils.getInstance().showAlertPopupOneButton((Stage) mRoot.getScene().getWindow(),
                                        mCurrentResources.getString("str.login.fail"),
                                        mCurrentResources.getString("str.ok"));
                            }
                        });
                    }
                }));
        ActionRuler.getInstance().runNext();
    }

    /**
     * @MethodName checkLoginValidation
     * @Description 아이디 / 비밀번호 입력 체크
     *
     * @return
     */
    private boolean checkLoginValidation() {

        String id = mTextFieldId.getText();
        String pwd = mTextFieldPwd.getText();

        if (id.isEmpty()) {
            mLabelValidation.setText(mCurrentResources.getString("str.input.id"));
            CommonUtils.getInstance().setHideShowBehaviour(mLabelValidation, true);
            return false;
        }

        if (pwd.isEmpty()) {
            mLabelValidation.setText(mCurrentResources.getString("str.input.password"));
            CommonUtils.getInstance().setHideShowBehaviour(mLabelValidation, true);
            return false;
        }

        return true;
    }
}
