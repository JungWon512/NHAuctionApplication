package com.nh.controller.controller;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nh.controller.ControllerApplication;
import com.nh.controller.utils.MoveStageUtil;
import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.model.AuctionGenerateInformationResult;
import com.nh.share.api.request.ActionRequestAuctionGenerateInformation;
import com.nh.share.api.request.ActionRequestCreateTts;
import com.nh.share.api.response.ResponseAuctionGenerateInformation;
import com.nh.share.api.response.ResponseCreateTtsResult;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.utils.CommonUtils;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * 
 * @ClassName AuctionListViewController.java
 * @Description 현재 제어 및 모니터링 가능한 경매 목록을 화면에 표시
 * @anthor ishift
 * @since 2019.10.28
 */
public class AuctionListController implements Initializable {

    private final Logger mLogger = LogManager.getLogger(MethodHandles.lookup().lookupClass());
    private Class<?> mClass = ControllerApplication.class;

    private ResourceBundle mCurrentResources;

    private Stage mStage;

    @FXML
    private AnchorPane mAnchorPaneRoot;

    @FXML
    private Button mBtnAuctionListRefresh; // 경매 목록 새로고침 버튼

    @FXML
    private Label mLabelCurrentDateTime; // 현재 날짜/시간 (우측 상단)

    @FXML
    private VBox mEmptyDataVBox;

    @FXML
    private ListView<AuctionGenerateInformationResult> mListViewAuction; // 경매 목록 리스트 View

    private ObservableList<AuctionGenerateInformationResult> mObservableListAuctionItems; // 경매 목록 리스트 Data

    // 이미 열려있는 제어프로그램은 경매 목록에서 선택하지 못하도록 열려있는 경매 제어프로그램 정보 Map으로 관리.
    private HashMap<String, String> mMapControlOpen = new HashMap<String, String>();
    private HashMap<String, String> mMapResultMonitorOpen = new HashMap<String, String>();
    private HashMap<String, String> mMapConnectMonitorOpen = new HashMap<String, String>();

    private int mRetryTtsIndex = 0;
    private ArrayList<String> mRetryTtsInfoList;
    private String mRetryAuctionCode;
    private String mRetryAuctionRound;
    private String mRetryAuctionLaneCode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mLogger.debug("" + getClass().getName());
        mCurrentResources = resources;
    }

    public void setStage(Stage stag) {
        mStage = stag;
        mStage.setTitle(mCurrentResources.getString("application.name"));

        // 현재 날짜/시간
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            mLabelCurrentDateTime
                    .setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("a HH:mm\nyyyy-MM-dd")));
        }), new KeyFrame(Duration.seconds(1)) // 1초마다 갱신
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        // ListView 마우스 클릭 막기. (Cell 안에 Button만 선택되어야 한다.)
        mListViewAuction.setMouseTransparent(false);
        mListViewAuction.setFocusTraversable(false);

        // ListView Cell 아이템 Array 초기화.
        mObservableListAuctionItems = FXCollections.observableArrayList();

        // 경매목록 test 데이터 셋팅
        // settingTestData();

        // 경매목록 API
        getAuctionGenerateInformations();
    }

    /**
     * 
     * @MethodName onAuctionListRefresh
     * @Description 경매 목록 새로고침 버튼
     *
     */
    @FXML
    private void onAuctionListRefresh() {
        mLogger.debug("[" + getClass().getName() + "] 새로고침 버튼 클릭");

        // API 호출
        getAuctionGenerateInformations();
    }

    /**
     * 
     * @MethodName getAuctionGenerateInformations
     * @Description 경매 목록 데이터 api 호출
     *
     */
    private void getAuctionGenerateInformations() {

        Date from = new Date();
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMdd");
        String auctionDate = transFormat.format(from);

        mLogger.debug(
                "[" + getClass().getName() + "] AuctionGenerateInformations API (auctionDateTime:" + auctionDate + ")");

        // show progress
        Platform.runLater(() -> CommonUtils.getInstance().showLoadingDialog(
                (Stage) mAnchorPaneRoot.getScene().getWindow(), mCurrentResources.getString("str.check.auction.list")));

        // request api
        ActionRuler.getInstance().addAction(new ActionRequestAuctionGenerateInformation(auctionDate, "all",
                new ActionResultListener<ResponseAuctionGenerateInformation>() {
                    @Override
                    public void onResponseResult(ResponseAuctionGenerateInformation result) {
                        mLogger.debug("[" + getClass().getName() + "] onResponseResult (" + result.getStatus() + ")");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                mObservableListAuctionItems.clear();
                                if (result.getResult() != null && result.getResult().size() > 0) {
                                    sortAuctionList(result.getResult()); // 경매 목록 정렬
                                    mObservableListAuctionItems.addAll(result.getResult());
                                    mEmptyDataVBox.setVisible(false);
                                } else {
                                    mEmptyDataVBox.setVisible(true);
                                }

                                CommonUtils.getInstance().dismissLoadingDialog();
                                loadAuctionListView(); // 경매 목록 로드
                            }
                        });
                    }

                    @Override
                    public void onResponseError(String message) {
                        mLogger.debug("[" + getClass().getName() + "] onResponseError (" + message + ")");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                CommonUtils.getInstance().dismissLoadingDialog();
                            }
                        });
                    }

                }));
        ActionRuler.getInstance().runNext();
    }

    /**
     * 
     * @MethodName sortAuctionList
     * @Description 경매목록 정렬 (1순위 : 시작시간 / 2순위 : 경매회차) 오름차순
     * 
     * @param auctionList
     */
    private void sortAuctionList(List<AuctionGenerateInformationResult> auctionList) {
        // 경매목록 정렬 (1순위 : 시작시간 / 2순위 : 경매회차) 오름차순
        Collections.sort(auctionList, new Comparator<AuctionGenerateInformationResult>() {

            @Override
            public int compare(AuctionGenerateInformationResult o1, AuctionGenerateInformationResult o2) {
                int result = 0;
                if (Integer.valueOf(o1.getAuctionTime()) < Integer.valueOf(o2.getAuctionTime())) {
                    result = -1;
                } else if (Integer.valueOf(o1.getAuctionTime()) > Integer.valueOf(o2.getAuctionTime())) {
                    result = 1;
                } else {
                    if (Integer.valueOf(o1.getAuctionRound()) < Integer.valueOf(o2.getAuctionRound())) {
                        result = -1;
                    } else if (Integer.valueOf(o1.getAuctionRound()) > Integer.valueOf(o2.getAuctionRound())) {
                        result = 1;
                    }
                }
                return result;
            }

        });
    }

    /**
     * 
     * @MethodName loadAuctionListView
     * @Description 경매 목록 ListView Cell Load.
     *
     */
    private void loadAuctionListView() {
        mLogger.debug("[" + getClass().getName() + "] 경매 목록 로드");
        // ListView에 경매 목록 데이터 Add
        mListViewAuction.getItems().clear();
        mListViewAuction.getItems().addAll(mObservableListAuctionItems);
        mListViewAuction.setCellFactory(list -> new ListCell<AuctionGenerateInformationResult>() {
            @Override
            protected void updateItem(AuctionGenerateInformationResult item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    String auctioUniqueCode = item.getAuctionCode() + item.getAuctionLaneCode()
                            + item.getAuctionRound();
                    AuctionListCellController cell = new AuctionListCellController();

                    if (Integer.parseInt(item.getTtsFailCnt()) > 0) {
                        cell.setTtsInfoVBoxVisible(true);
                        cell.setTtsFailInfoLabel(mCurrentResources.getString("str.tts.fail.count")
                                + item.getTtsFailCnt() + mCurrentResources.getString("str.count.unit"));
                    } else {
                        cell.setTtsInfoVBoxVisible(false);
                    }

                    // 종료된 경매 항목 Disable 처리
                    if (item.getAuctionStatus() == null
                            || item.getAuctionStatus().equals(GlobalDefineCode.AUCTION_API_STATUS_COMPLETED)) {
                        cell.setDisable(true);
                    } else {
                        cell.setDisable(false);
                    }
                    if (item.getAuctionCode().equals(GlobalDefineCode.AUCTION_TYPE_REALTIME)) {
                        cell.setBtnAuctionTypeText(mCurrentResources.getString("str.auction.realtime"));
                    } else if (item.getAuctionCode().equals(GlobalDefineCode.AUCTION_TYPE_SPOT)) {
                        cell.setBtnAuctionTypeText(mCurrentResources.getString("str.auction.spot"));
                    } else {
                        cell.setBtnAuctionTypeText("");
                    }

                    cell.setBtnAuctionLaneNameText(item.getAuctionLaneName());
                    cell.setLabelAuctionNameText(item.getAuctionName() + " - " + item.getAuctionRound()
                            + mCurrentResources.getString("str.auction.round") + " ("
                            + mCurrentResources.getString("str.total.entry") + item.getAuctionLaneEntryCount()
                            + mCurrentResources.getString("str.entry.count.unit") + ")");

                    String auctionDate = CommonUtils.getInstance().getAuctionTimeHoureInAMandPM(item.getAuctionDate(),
                            item.getAuctionTime());
                    cell.setLabelAuctionDateText(mCurrentResources.getString("str.auction.time") + auctionDate);

                    // 제어프로그램 버튼 disable 처리. (열려있는 제어프로그램은 버튼 선택 못하도록 막음)
                    if (mMapControlOpen.containsKey(auctioUniqueCode)) {
                        cell.mBtnActionControlProgram.setDisable(true);
                    } else {
                        cell.mBtnActionControlProgram.setDisable(false);
                    }

                    if (mMapResultMonitorOpen.containsKey(auctioUniqueCode)) {
                        cell.mBtnAuctionResultMonitoringBtn.setDisable(true);
                    } else {
                        cell.mBtnAuctionResultMonitoringBtn.setDisable(false);
                    }

                    if (mMapConnectMonitorOpen.containsKey(auctioUniqueCode)) {
                        cell.mBtnActionConnectMonitor.setDisable(true);
                    } else {
                        cell.mBtnActionConnectMonitor.setDisable(false);
                    }

                    cell.mBtnRetryCreateTts.setOnAction(e -> {
                        mLogger.debug("[TTS 재생성 버튼 선택]");
                        // TTS 재생성 요청 처리
                        String ttsFailList = item.getTtsFailExhino();
                        String[] ttsFailArray;

                        if (ttsFailList.length() > 0) {
                            ttsFailArray = ttsFailList.split(",", -1);
                            mRetryTtsInfoList = new ArrayList<>(Arrays.asList(ttsFailArray));
                        }

                        mRetryTtsIndex = 0;
                        mRetryAuctionCode = item.getAuctionCode();
                        mRetryAuctionRound = item.getAuctionRound();
                        mRetryAuctionLaneCode = item.getAuctionLaneCode();

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                CommonUtils.getInstance().showLoadingDialog(
                                        (Stage) mAnchorPaneRoot.getScene().getWindow(),
                                        mCurrentResources.getString("str.tts.retry.create"));
                            }
                        });

                        retryCreateTts(mRetryTtsInfoList.get(mRetryTtsIndex));
                    });

                    cell.mBtnActionControlProgram.setOnAction(e -> {
                        mLogger.debug("[제어프로그램 열기 버튼 선택]");
                        CommonUtils.getInstance().showLoadingDialog(mStage,
                                mCurrentResources.getString("str.start.program"));
                        // 경매 제어 프로그램 열기
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(
                                    MoveStageUtil.getInstance().getFXMLResource("AuctionControlView.fxml"),
                                    MoveStageUtil.getInstance().getResourceBundle(new Locale("ko")));
                            Parent rootLayout = (Parent) fxmlLoader.load();
                            Stage stage = new Stage();
                            stage.initModality(Modality.WINDOW_MODAL);
                            stage.initStyle(StageStyle.DECORATED);
                            stage.setScene(new Scene(rootLayout));
                            stage.setTitle(mCurrentResources.getString("str.auction.controller.name"));
                            MoveStageUtil.getInstance().setIcon(stage);
                            AuctionControlController controller = fxmlLoader.getController();
                            controller.setStage((Stage) mAnchorPaneRoot.getScene().getWindow(), stage, item);

                            stage.setOnShowing(event -> {
                                CommonUtils.getInstance().dismissLoadingDialog();
                            });
                            stage.setOnShown(event -> {
                                // stage.show() 이후 호출되는 이벤트
                                mLogger.debug("[stage.setOnShown]");
                                mMapControlOpen.put(auctioUniqueCode, "controller");
                                cell.mBtnActionControlProgram.setDisable(true);
                                MoveStageUtil.getInstance().setParentPositionCenter(mStage, stage);
                                stage.sizeToScene();
                            });
                            stage.setOnHidden(event -> {
                                // stage.close() 이후 호출되는 이벤트
                                mLogger.debug("[stage.setOnHidden]");
                                mMapControlOpen.remove(auctioUniqueCode);
                                cell.mBtnActionControlProgram.setDisable(false);
                                getAuctionGenerateInformations(); // 경매목록 새로고침
                            });
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    });

                    // 낙/유찰 모니터링 버튼
                    cell.mBtnAuctionResultMonitoringBtn.setOnAction(e -> {
                        CommonUtils.getInstance().showLoadingDialog(mStage,
                                mCurrentResources.getString("str.start.program"));
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(
                                    MoveStageUtil.getInstance().getFXMLResource("AuctionResultView.fxml"),
                                    MoveStageUtil.getInstance().getResourceBundle(new Locale("ko")));
                            Parent rootLayout = (Parent) fxmlLoader.load();
                            Stage stage = new Stage();
                            stage.initModality(Modality.WINDOW_MODAL);
                            stage.initStyle(StageStyle.DECORATED);
                            stage.setScene(new Scene(rootLayout));
                            stage.setTitle(mCurrentResources.getString("str.auction.result.monitoring"));
                            MoveStageUtil.getInstance().setIcon(stage);
                            AuctionResultController controller = fxmlLoader.getController();
                            controller.setStage(stage, item);
                            stage.setOnShowing(event -> {
                                CommonUtils.getInstance().dismissLoadingDialog();
                            });
                            stage.setOnShown(event -> {
                                mMapResultMonitorOpen.put(auctioUniqueCode, "resultMonitor");
                                cell.mBtnAuctionResultMonitoringBtn.setDisable(true);
                            });
                            stage.setOnHidden(event -> {
                                mMapResultMonitorOpen.remove(auctioUniqueCode);
                                cell.mBtnAuctionResultMonitoringBtn.setDisable(false);
                            });
                            stage.show();
                            MoveStageUtil.getInstance().setScreenMinSize(stage);
                            MoveStageUtil.getInstance()
                                    .setParentPositionCenter((Stage) mAnchorPaneRoot.getScene().getWindow(), stage);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    });
                    cell.mBtnActionConnectMonitor.setOnAction(event -> {
                        CommonUtils.getInstance().showLoadingDialog(mStage,
                                mCurrentResources.getString("str.start.program"));
                        // 접속자 모니터링 프로그램
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(
                                    MoveStageUtil.getInstance().getFXMLResource("AuctionConnectView.fxml"),
                                    MoveStageUtil.getInstance().getResourceBundle(new Locale("ko")));
                            Parent rootLayout = (Parent) fxmlLoader.load();
                            Stage stage = new Stage();
                            stage.initModality(Modality.WINDOW_MODAL);
                            stage.initStyle(StageStyle.DECORATED);
                            stage.setTitle(mCurrentResources.getString("str.auction.connector.monitoring"));
                            MoveStageUtil.getInstance().setIcon(stage);
                            stage.setScene(new Scene(rootLayout));
                            AuctionConnectController controller = fxmlLoader.getController();
                            controller.setStage(stage, item);

                            stage.setOnShowing(evt -> {
                                CommonUtils.getInstance().dismissLoadingDialog();
                            });

                            stage.setOnShown(evt -> {
                                // stage.show() 이후 호출되는 이벤트
                                mLogger.debug("[stage.setOnShown]");
                                mMapConnectMonitorOpen.put(auctioUniqueCode, "connectMonitor");
                                cell.mBtnActionConnectMonitor.setDisable(true);
                            });
                            stage.setOnHidden(evt -> {
                                // stage.close() 이후 호출되는 이벤트
                                mLogger.debug("[stage.setOnHidden]");
                                mMapConnectMonitorOpen.remove(auctioUniqueCode);
                                cell.mBtnActionConnectMonitor.setDisable(false);
                            });
                            stage.show();
                            MoveStageUtil.getInstance().setScreenMinSize(stage);
                            MoveStageUtil.getInstance()
                                    .setParentPositionCenter((Stage) mAnchorPaneRoot.getScene().getWindow(), stage);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    });
                    setGraphic(cell.getAnchorPaneRoot());
                } else {
                    setGraphic(null);
                    setText(null);
                }
            }
        });
    }

    private void retryCreateTts(String retryTtsInfo) {
        mLogger.debug("TTS 재생성 요청 : " + mRetryAuctionCode + " / " + mRetryAuctionRound + " / " + mRetryAuctionLaneCode
                + " / " + retryTtsInfo);

        if (mRetryTtsInfoList.size() > mRetryTtsIndex) {
            ActionRuler.getInstance().addAction(new ActionRequestCreateTts(mRetryAuctionCode, mRetryAuctionRound,
                    mRetryAuctionLaneCode, retryTtsInfo, new ActionResultListener<ResponseCreateTtsResult>() {
                        @Override
                        public void onResponseResult(ResponseCreateTtsResult result) {
                            mLogger.debug(
                                    "[" + getClass().getName() + "] onResponseResult (" + result.getStatus() + ")");
                            mRetryTtsIndex++;

                            if (mRetryTtsInfoList.size() > mRetryTtsIndex) {
                                retryCreateTts(mRetryTtsInfoList.get(mRetryTtsIndex));
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        CommonUtils.getInstance().dismissLoadingDialog();
                                        getAuctionGenerateInformations(); // 경매목록 새로고침
                                    }
                                });
                            }
                        }

                        @Override
                        public void onResponseError(String message) {
                            mRetryTtsIndex++;

                            mLogger.debug("[" + getClass().getName() + "] onResponseError (" + message + ")");

                            if (mRetryTtsInfoList.size() > mRetryTtsIndex) {
                                retryCreateTts(mRetryTtsInfoList.get(mRetryTtsIndex));
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        CommonUtils.getInstance().dismissLoadingDialog();
                                    }
                                });
                            }
                        }

                    }));

            ActionRuler.getInstance().runNext();
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    CommonUtils.getInstance().dismissLoadingDialog();
                }
            });
        }
    }
}
