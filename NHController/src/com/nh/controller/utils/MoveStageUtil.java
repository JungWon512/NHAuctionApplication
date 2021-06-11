package com.nh.controller.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nh.controller.ControllerApplication;
import com.nh.controller.controller.AuctionListCellController;
import com.nh.controller.controller.AuctionListController;
import com.nh.controller.support.UTF8Control;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class MoveStageUtil {
    private final Logger mLogger = LogManager.getLogger(MoveStageUtil.class);

    private static MoveStageUtil instance = new MoveStageUtil();

    private Class<?> mClass = ControllerApplication.class;

    public static synchronized MoveStageUtil getInstance() {
        return instance;
    }

    /**
     * @MethodName moveIntroStage
     * @Description 인트로 화면으로 이동
     *
     * @param stage
     * @throws Exception
     */
    public synchronized void moveIntroStage(Stage stage) {

        try {

            Stage newStage = stage;
            Parent root = (Parent) FXMLLoader.load(getFXMLResource("IntroView.fxml"),
                    getResourceBundle(new Locale("ko")));

            Scene scene = new Scene(root);
            setCss(scene);
            newStage.setScene(scene);
            setRemoveTitlebar(newStage);
            setIcon(newStage);
            newStage.show();
            setScreenPositionCenter(newStage);

            newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent event) {
                    // TODO Auto-generated method stub
                    applicationFinish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @MethodName moveLoginStage
     * @Description 로그인 화면으로 이동
     *
     * @param stag
     * @throws Exception
     */
    public synchronized void moveLoginStage(Stage stage) {

        try {

            Parent root = null;
            root = (Parent) FXMLLoader.load(getFXMLResource("LoginView.fxml"), getResourceBundle(new Locale("ko")));

            Scene scene = new Scene(root);
            setCss(scene);
            Stage newStage = new Stage();
            newStage.setScene(scene);
            setIcon(newStage);
            newStage.show();
            setScreenMinSize(newStage);
            setScreenPositionCenter(newStage);
            stage.close(); // 기존 페이지 삭제

            newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent event) {
                    // TODO Auto-generated method stub
                    applicationFinish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @MethodName moveAuctionListStage
     * @Description 메인 화면으로 이동
     *
     * @param stage
     * @throws Exception
     */
    public synchronized void moveAuctionListStage(Stage stage) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("AuctionListView.fxml"),
                    getResourceBundle(new Locale("ko")));
            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent);
            setCss(scene);
            Stage newStage = new Stage();
            newStage.setScene(scene);
            setResizeScreen(newStage, false); // 창 크기 조절 막기
            setIcon(newStage);
            newStage.show();
            setScreenMinSize(newStage);
            setParentPositionCenter(stage, newStage);
//            setScreenPositionCenter(newStage);
            stage.close();
            AuctionListController controller = fxmlLoader.getController();
            controller.setStage(newStage);

            newStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent event) {
                    // TODO Auto-generated method stub
                    applicationFinish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 
     * @MethodName loadAuctionListCellFXML
     * @Description 경매 목록 ListView Cell Item FXML 설정
     * 
     * @param controller
     */
    public synchronized void loadAuctionListCellFXML(AuctionListCellController controller) {
        FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("AuctionListViewCell.fxml"),
                getResourceBundle(new Locale("ko")));
        fxmlLoader.setRoot(controller);
        fxmlLoader.setController(controller);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * @MethodName loadSettingFXMLLoader
     * @Description 제어프로그램 > 환경설정 FXML 경로
     * 
     * @return
     */
    public synchronized FXMLLoader loadSettingFXMLLoader() {
        FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("AuctionSettingView.fxml"),
                getResourceBundle(new Locale("ko")));
        return fxmlLoader;
    }

    /**
     * 
     * @MethodName loadMessageFXMLLoader
     * @Description
     * 
     * @return
     */
    public synchronized FXMLLoader loadMessageFXMLLoader() {
        FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("AuctionMessageView.fxml"),
                getResourceBundle(new Locale("ko")));
        return fxmlLoader;
    }

    public synchronized FXMLLoader getGridCellFXMLLoader() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(mClass.getResource("view/AuctionConnectGridViewCell.fxml"));

        return fxmlLoader;
    }

    /**
     * @MethodName setCss
     * @Description css 파일 적용
     *
     * @param scene
     */
    private synchronized void setCss(Scene scene) {
        scene.getStylesheets().add(mClass.getResource("css/application.css").toExternalForm());
    }

    /**
     * @MethodName setRemoveTitlebar
     * @Description 창 크기 조절 막음
     *
     * @param stage
     */
    public synchronized void setResizeScreen(Stage stage, boolean flag) {
        stage.setResizable(flag);
    }

    /**
     * @MethodName setRemoveTitlebar
     * @Description 타이틀바 제거
     *
     * @param stage
     */
    private synchronized void setRemoveTitlebar(Stage stage) {
        stage.initStyle(StageStyle.UNDECORATED);
    }

    /**
     * @MethodName setMinScreenSize
     * @Description 최소 사이즈 설정
     *
     * @param stage
     */
    public synchronized void setScreenMinSize(Stage stage) {
        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());
    }

    /**
     * @MethodName setScreenPositionCenter
     * @Description 윈도우 화면 가운데 View 띄움
     *
     * @param stage
     */
    public synchronized void setScreenPositionCenter(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    /**
     * 
     * @MethodName setParentPositionCenter
     * @Description 부모뷰 화면 가운데 View 띄움
     * 
     * @param parentStage
     * @param stage
     */
    public synchronized void setParentPositionCenter(Stage parentStage, Stage stage) {
        double centerXPosition = parentStage.getX() + parentStage.getWidth() / 2d;
        double centerYPosition = parentStage.getY() + parentStage.getHeight() / 2d;
        stage.setX(centerXPosition - stage.getWidth() / 2d);
        stage.setY(centerYPosition - stage.getHeight() / 2d);
    }

    /**
     * 
     * @MethodName onApplicationFinish
     * @Description 프로그램 종료 처리
     *
     */
    public synchronized void applicationFinish() {
        System.exit(0);
    }

    public synchronized URL getFXMLResource(String fxmlName_) {
        return mClass.getResource("view/" + fxmlName_);
    }

    public synchronized ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle("com/glovis/controller/support/properties/UIResources", locale,
                new UTF8Control());
    }

    public synchronized void setIcon(Stage stage) {
        if (stage != null) {
            stage.getIcons().add(new Image(mClass.getResourceAsStream("resource/AuctionControllerApplication.png")));
        }
    }
}
