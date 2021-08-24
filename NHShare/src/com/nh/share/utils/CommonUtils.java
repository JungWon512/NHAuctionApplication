package com.nh.share.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * 
 * @ClassName CommonUtils.java
 * @Description 공통 유틸리티 관리 클래스
 * @author 박종식
 * @since 2019.10.22
 */
public class CommonUtils {
    private static final Logger mLogger = LoggerFactory.getLogger(CommonUtils.class);

    private static CommonUtils commonUtils = new CommonUtils();

    private Dialog mLoadingDialog; // 로딩 다이얼로그
    private Stage mLoadingDialogStage; // 로딩 다이얼로그 Parent

    private PopOver mPopOver; // 툴팁

    // 툴팁 타입 [START]
    public String TOOLTIP_POSITION_TOP_CENTER = "TOP_CENTER";
    public String TOOLTIP_POSITION_TOP = "TOP";
    public String TOOLTIP_POSITION_LEFT = "LEFT";
    public String TOOLTIP_POSITION_RIGHT = "RIGHT";
    public String TOOLTIP_POSITION_BOTTOM = "BOTTOM";
    public String TOOLTIP_POSITION_NOMAL = "NOMAL";
    // 툴팁 타입 [END]

    // 팝업창 타입 [START]
    public String ALERTPOPUP_ONE_BUTTON = "ONE_BUTTON";
    public String ALERTPOPUP_TWO_BUTTON = "TWO_BUTTON";
    // 팝업창 타입 [END]

    public CommonUtils() {

    }

    public static CommonUtils getInstance() {
        return commonUtils;
    }

    /**
     * 
     * @MethodName getAuctionTimeHoureInAMandPM
     * @Description 경매 시간을 AM/PM 형식으로 반환 처리.
     * 
     * @param date
     * @param Time
     * @return String (ex> 2019.10.10 AM 1:30)
     */
    public synchronized String getAuctionTimeHoureInAMandPM(String date, String time) {
        String dateTimeString = date + time;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
        String result = dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd a HH:mm"));
        return result;
    }

    /**
     * 
     * @MethodName getCurrentTime_yyyyMMdd
     * @Description 시간을 반환 처리(yyyyMMdd)
     *
     * @param format yyyyMM
     * @return String
     */
    public synchronized String getCurrentTime_yyyyMMdd(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String result = simpleDateFormat.format(date);
        return result;
    }

    /**
     * 
     * @MethodName getCurrentTime_yyyyMMdd
     * @Description 시간을 반환 처리(yyyyMMdd)
     *
     * @param format yyyyMM
     * @return String
     */
    public synchronized String getCurrentTime_yyyyMMdd(String curDate) {

        String result = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = simpleDateFormat.parse(curDate);
            result = simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @MethodName getCurrentTime_yyyyMMdd
     * @Description
     *
     * @param date
     * @param locale
     * @return
     */
    public synchronized String getCurrentTime_yyyyMMdd(Date date, Locale locale) {

        SimpleDateFormat simpleDateFormat = null;
        if (locale.getLanguage().equals(Locale.KOREAN.toString())) {
            simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 (EE)", locale);
        } else {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd (EE)", locale);
        }
        String result = simpleDateFormat.format(date);
        return result;
    }

    /**
     * @MethodName getCurrentTime_yyyyMMdd
     * @Description
     *
     * @param date
     * @param locale
     * @return
     */
    public synchronized String getCurrentTime_MMMMM(Date date, Locale locale) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMMM", locale);
        String result = simpleDateFormat.format(date);
        return result;
    }

    /**
     * 
     * @MethodName getCurrentTime
     * @Description 시간을 반환 처리(yyyyMM)
     *
     * @param format yyyyMM
     * @return String
     */
    public synchronized String getCurrentTime_yyyyMM(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
        String result = simpleDateFormat.format(date);
        return result;
    }

    /**
     * 
     * @MethodName getCurrentTime
     * @Description 시간을 반환 처리(yyyy-MM-dd HH:mm)
     *
     * @param format yyyy-MM-dd HH:mm
     * @return String
     */
    public synchronized String getCurrentTime_yyyyMMddHHmm(String time) {
        String dateTimeString = time;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateTimeString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String result = sFormat.format(date);
        return result;
    }
    
    /**
     * 
     * @MethodName getCurrentTime
     * @Description 시간을 반환 처리(yyyy-MM-dd HH:mm)
     *
     * @param format yyyy-MM-dd HH:mm:ss
     * @return String
     */
    public synchronized String getCurrentTime_yyyyMMddHHmmss(String time) {
        String dateTimeString = time;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateTimeString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String result = sFormat.format(date);
        return result;
    }

    /**
     * 
     * @MethodName getCurrentTime_yyyyMMdd_dot
     * @Description 시간을 반환 처리(yyyy.MM.dd)
     *
     * @param format yyyyMMdd
     * @return String
     */
    public synchronized String getCurrentTime_yyyyMMdd_dot(String time) {
        String dateTimeString = time;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateTimeString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy.MM.dd");
        String result = sFormat.format(date);
        return result;
    }

    /**
     * @MethodName showLoadingDialog
     * @Description 공통 프로그래스 다이알로그 Show 함수
     *
     * @param stage
     */
    public synchronized void showLoadingDialog(Stage stage, String message) {

        if (mLoadingDialog != null) {
            dismissLoadingDialog();
        }

        mLoadingDialog = new Dialog<>();
        DialogPane dialogPane = new DialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/com/glovis/share/css/share.css").toExternalForm());
        mLoadingDialog.initModality(Modality.NONE);
        mLoadingDialog.initStyle(StageStyle.UNDECORATED);
        mLoadingDialog.setDialogPane(dialogPane);
        dialogPane.setPadding(new Insets(0, 90, 0, 90));

        VBox vbox = new VBox();
        vbox.setSpacing(new Double(15));
        vbox.setAlignment(Pos.CENTER);
        // alert popup message label
        Label contentText = new Label();
        contentText.setText(message);
        contentText.setTextAlignment(TextAlignment.CENTER);
        contentText.setAlignment(Pos.CENTER);
        contentText.getStyleClass().add("regular-font");

        ImageView imageView = new ImageView();
        imageView.preserveRatioProperty().set(true);
        imageView.setPickOnBounds(true);
        imageView.setFitWidth(220);
        imageView.setFitHeight(110);
        Image image = new Image("/com/glovis/share/resources/images/logo.png");
        imageView.setImage(image);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxWidth(42);
        progressIndicator.setMaxHeight(42);

        vbox.getChildren().add(imageView);
        vbox.getChildren().add(contentText);
        vbox.getChildren().add(progressIndicator);

        dialogPane.setContent(vbox);

        if (stage != null) {
            mLoadingDialog.initOwner(stage);

            mLoadingDialogStage = stage;

            // 프로그래스 다이알로그 뒷 배경 블러처리 Add
            mLoadingDialogStage.getScene().getRoot().setEffect(getDialogBlurEffect());
            // 프로그래스 show 시 뒷 배경 터치 막음
            mLoadingDialogStage.getScene().getRoot().setDisable(true);
        }

        mLoadingDialog.show();

    }

    /**
     * @MethodName dismissLoadingDialog
     * @Description 공통 프로그래스 다이알로그 Dismiss 처리 함수
     *
     */
    public synchronized void dismissLoadingDialog() {

        if (mLoadingDialogStage != null) {
            // 프로그래스 다이알로그 뒷 배경 블러처리 remove
            mLoadingDialogStage.getScene().getRoot().setEffect(null);
        }

        if (mLoadingDialog != null) {

            if (mLoadingDialog.isShowing()) {

                try {
                    mLoadingDialogStage.getScene().getRoot().setDisable(false);
                    mLoadingDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                    mLoadingDialog.close();
                    mLoadingDialog = null;

                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                    mLoadingDialogStage.getScene().getRoot().setDisable(false);
                    mLoadingDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                    mLoadingDialog.close();
                    mLoadingDialog = null;
                }
            }
        }

    }

    /**
     * @MethodName showTooltip
     * @Description 툴팁 표시
     *
     * @param node     타겟 뷰
     * @param message  내용
     * @param position 위치 ( 상/상단 중앙/하/좌/우/)
     */
    public void showTooltip(final Node node, final String message, final String position) {

        Tooltip tooltip = new Tooltip();

        tooltipStartTiming(tooltip);

        Tooltip.install(node, tooltip);

        tooltip.setOnShowing(ev -> {

            tooltip.setFont(Font.font("", 14));
            tooltip.setText(message);

            Bounds boundsInScreen = node.localToScreen(node.getBoundsInLocal());

            if (TOOLTIP_POSITION_TOP_CENTER.equals(position)) {
                double x = (boundsInScreen.getMaxX() - boundsInScreen.getMinX()) / 2;
                double messageCenter = (int) (tooltip.getWidth() / 2);
                double tooltipHeight = (int) tooltip.getHeight();
                tooltip.show(node, (boundsInScreen.getMinX() + x) - messageCenter,
                        boundsInScreen.getMinY() - (tooltipHeight + 5));
            } else if (TOOLTIP_POSITION_TOP.equals(position)) {
                double tooltipHeight = (int) tooltip.getHeight();
                tooltip.show(node, boundsInScreen.getMinX(), boundsInScreen.getMinY() - (tooltipHeight + 5));
            } else if (TOOLTIP_POSITION_BOTTOM.equals(position)) {
                tooltip.show(node, boundsInScreen.getMinX(), boundsInScreen.getMaxY());
            } else if (TOOLTIP_POSITION_LEFT.equals(position)) {
                double tooltipWidth = (int) tooltip.getWidth();
                tooltip.show(node, boundsInScreen.getMinX() - tooltipWidth, boundsInScreen.getMinY());
            } else if (TOOLTIP_POSITION_RIGHT.equals(position)) {
                tooltip.show(node, boundsInScreen.getMaxX(), boundsInScreen.getMinY());
            } else if (TOOLTIP_POSITION_NOMAL.equals(position)) {
                // tooltip.show(node, boundsInScreen.getMaxX() , boundsInScreen.getMinY());
            }

        });
    }

    /**
     * @MethodName tooltipStartTiming
     * @Description 툴팁 표시 시간.
     *
     * @param tooltip
     */
    public synchronized void tooltipStartTiming(Tooltip tooltip) {
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(250)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @MethodName showPopupOver
     * @Description 툴팁
     *
     * @param node
     * @param message
     */
    public void showPopupOver(Node node, String message, String position) {

        try {

            if (mPopOver != null) {
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/glovis/share/view/PopupView.fxml"));
            AnchorPane parent = fxmlLoader.load();

            Label label = (Label) parent.getChildren().get(0);
            label.setText(message);

            mPopOver = new PopOver();
            mPopOver.setContentNode(parent);
            mPopOver.setDetachable(false);

            if (position.equals(TOOLTIP_POSITION_TOP)) {
                mPopOver.setArrowLocation(ArrowLocation.BOTTOM_CENTER); // 노드 위 선터
            } else if (position.equals(TOOLTIP_POSITION_BOTTOM)) {
                mPopOver.setArrowLocation(ArrowLocation.TOP_CENTER); // 노드 아래쪽 센터
            } else if (position.equals(TOOLTIP_POSITION_LEFT)) {
                mPopOver.setArrowLocation(ArrowLocation.RIGHT_CENTER); // 노드 왼쪽 센터
            } else if (position.equals(TOOLTIP_POSITION_RIGHT)) {
                mPopOver.setArrowLocation(ArrowLocation.LEFT_CENTER); // 노드 오른쪽 센터
            }

            mPopOver.show(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dismissPopupOver() {
        if (mPopOver != null) {
            mPopOver.hide();
            mPopOver = null;
        }
    }

    /**
     * @MethodName hideShowBehaviour
     * @Description View 보임/숨김 처리
     *
     * @param node
     * @param isVisible true : 보임 / false : 숨김
     */
    public synchronized void setHideShowBehaviour(Node node, boolean isVisible) {
        if (node != null && (node.isVisible() == isVisible)) {
            return;
        }
        node.setVisible(isVisible);
        node.managedProperty().bind(node.visibleProperty());
    }

    /**
     * @MethodName setNodeDisable
     * @Description View 활성화/비활성화 처리
     *
     * @param node
     * @param isDisable true 비활성화 /false 활성화
     */
    public synchronized void setNodeDisable(Node node, boolean isDisable) {
        if (node != null && (node.isDisable() == isDisable)) {
            return;
        }
        node.setDisable(isDisable);
    }

    /**
     * 
     * @MethodName getCurrentTime
     * @Description 현재 시간을 반환 처리(yyyyMMddHHmmssSSS)
     *
     * @return String
     */
    public synchronized String getCurrentTime() {
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date current = new Date();
        String result = date.format(current);

        return result;
    }

    /**
     * @MethodName setEngAndNumber
     * @Description 텍스트 필드 영문&숫자 입력
     *
     * @param textField
     */
    public synchronized void setTextStyle_Eng_Number(TextField textField) {
        textField.setTextFormatter(
                new TextFormatter<>(change -> (change.getControlNewText().matches("^[a-zA-Z0-9]*$")) ? change : null));
    }

    /**
     * @MethodName setEngAndNumber
     * @Description 텍스트 필드 영문만입력
     *
     * @param textField
     */
    public synchronized void setTextStyleOnly_Eng(TextField textField) {
        textField.setTextFormatter(
                new TextFormatter<>(change -> (change.getControlNewText().matches("^[a-zA-Z]*$")) ? change : null));
    }

    /**
     * @MethodName setOnlyNumber
     * @Description 텍스트 필드 숫자만 입력
     *
     * @param textField
     */
    public synchronized void setTextStyleOnly_Number(TextField textField) {
        textField.setTextFormatter(
                new TextFormatter<>(change -> (change.getControlNewText().matches("^[0-9]*$")) ? change : null));
    }

    /**
     * @MethodName setTextStyleKrOnly
     * @Description 텍스트 필드 국문만 입력
     *
     * @param textField
     */
    public synchronized void setTextStyleOnly_Kr(TextField textField) {
        textField.setTextFormatter(
                new TextFormatter<>(change -> (change.getControlNewText().matches("^[가-힣]*$")) ? change : null));
    }

    /**
     * @MethodName setTextStyle_Eng_Number_SpecialChar
     * @Description 텍스트 필드 영문,숫자,특수문자만 입력
     *
     * @param textField
     */
    public synchronized void setTextStyle_Eng_Number_SpecialChar(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(change -> (change.getControlNewText()
                .matches("^[a-zA-Z0-9\"'\\{\\}\\[\\]/?.,;:|\\)\\(*~`!^\\-_+&<>@#$%^\\\\=]*$")) ? change : null));
    }

    /**
     * @MethodName requestFocus
     * @Description 해당 뷰에 포커스
     *
     * @param node
     */
    public synchronized void requestFocus(Node node) {
        Platform.runLater(() -> {
            if (!node.isFocused()) {
                node.requestFocus();
            }
        });
    }

    /**
     * 
     * @MethodName showAlertPopupOneButton
     * @Description 버튼 1개 팝업창.
     * 
     * @param message
     * @param buttonTitle
     * @return Optional<ButtonType>
     * 
     */
    public Optional<ButtonType> showAlertPopupOneButton(Stage stage, String message, String buttonTitle) {
        Dialog<ButtonType> dialog = setAlertPopupStyle(stage, ALERTPOPUP_ONE_BUTTON, message, buttonTitle, "");
        Optional<ButtonType> result = dialog.showAndWait();
        return result;
    }

    /**
     * 
     * @MethodName showAlertPopupTwoButton
     * @Description 버튼 2개 팝업창.
     * 
     * @param message
     * @param leftButtonTitle
     * @param rightButtonTitle
     * @return Optional<ButtonType>
     * 
     */
    public Optional<ButtonType> showAlertPopupTwoButton(Stage stage, String message, String leftButtonTitle,
            String rightButtonTitle) {
        Dialog<ButtonType> dialog = setAlertPopupStyle(stage, ALERTPOPUP_TWO_BUTTON, message, leftButtonTitle,
                rightButtonTitle);
        Optional<ButtonType> result = dialog.showAndWait();
        return result;
    }

    /**
     * 
     * @MethodName setAlertPopupStyle
     * @Description 팝업창 Layout 셋팅
     * 
     * @param alertPopupType
     * @param message
     * @param leftButtonTitle
     * @param rightButtonTitle
     * @return
     */
    public Dialog<ButtonType> setAlertPopupStyle(Stage stage, String alertPopupType, String message,
            String leftButtonTitle, String rightButtonTitle) {
        Dialog<ButtonType> dialog = new Dialog<>();
        DialogPane dialogPane = new DialogPane() {
            @Override
            protected Node createButtonBar() {
                ButtonBar buttonBar = (ButtonBar) super.createButtonBar();
                buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_NONE);
                return buttonBar;
            }
        };

        dialogPane.getStylesheets().add(getClass().getResource("/com/glovis/share/css/share.css").toExternalForm());
        dialog.initOwner(stage);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setDialogPane(dialogPane);
        dialogPane.setPadding(new Insets(10));
        dialogPane.setMinHeight(100);
        dialogPane.setMinWidth(350);

        // one button alert popup
        if (alertPopupType.equals(ALERTPOPUP_ONE_BUTTON)) {
            ButtonType buttonTypeOne = new ButtonType(leftButtonTitle, ButtonData.LEFT);
            dialogPane.getButtonTypes().addAll(buttonTypeOne);
        }
        // two button alert popup
        else {
            ButtonType buttonTypeOne = new ButtonType(leftButtonTitle, ButtonData.LEFT);
            ButtonType buttonTypeTwo = new ButtonType(rightButtonTitle, ButtonData.RIGHT);
            dialogPane.getButtonTypes().addAll(buttonTypeOne, buttonTypeTwo);
        }

        // alert popup message label
        Label contentText = new Label();
        contentText.setText(message);
        contentText.setTextAlignment(TextAlignment.CENTER);
        contentText.setAlignment(Pos.CENTER);
        contentText.getStyleClass().add("regular-font");
        dialogPane.setContent(contentText);

        Region spacer = new Region();
        ButtonBar.setButtonData(spacer, ButtonBar.ButtonData.BIG_GAP);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        dialogPane.applyCss();
        dialogPane.layout();
        HBox hbox = (HBox) dialogPane.lookup(".container");
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().add(spacer);

        // center alert stage to parent stage
        /*
         * double centerXPosition = stage.getX() + stage.getWidth() / 2d; double
         * centerYPosition = stage.getY() + stage.getHeight() / 2d;
         * dialog.setX(centerXPosition - dialogPane.getWidth() / 2d);
         * dialog.setY(centerYPosition - dialogPane.getHeight() / 2d);
         */

        // 팝업창 뒷 배경 블러처리 Add
        stage.getScene().getRoot().setEffect(getDialogBlurEffect());

        // 팝업창 뒷 배경 블러처리 remove
        dialog.setOnCloseRequest(event -> {
            stage.getScene().getRoot().setEffect(null);
        });

        return dialog;
    }

    /**
     * 
     * @MethodName encodingToBase64
     * @Description 입력받은 text를 Base64로 인코딩한다.
     * 
     * @param text
     * @return
     */
    public synchronized String encodingToBase64(String text) {
        byte[] targetBytes = text.getBytes();
        Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(targetBytes);
        return new String(encodedBytes);
    }

    /**
     * 
     * @MethodName getDelegateCarImage
     * @Description 차량 대표 이미지 추출 처리
     *
     * @param imageList 차량 이미지 리스트
     */
    public synchronized String getDelegateCarImage(String imageList) {
        String carImagePath = null;

        if (imageList.length() > 0 && imageList.contains(",")) {
            imageList = imageList.replace("[", "");
            imageList = imageList.replace("]", "");
            String[] carImageList = imageList.split(",");
            if (carImageList.length > 0) {
                carImagePath = carImageList[0];
            }
        } else {
            carImagePath = imageList;
        }

        return carImagePath;
    }

    /**
     * 
     * @MethodName getDelegateCarImage
     * @Description 차량 대표 이미지 추출 처리
     *
     * @param imageList 차량 이미지 리스트
     */
    public synchronized int getDelegateCarImageSize(String imageList) {

        int carImagesSize = 1;

        if (imageList.length() > 0 && imageList.contains(",")) {
            imageList = imageList.replace("[", "");
            imageList = imageList.replace("]", "");
            String[] carImageList = imageList.split(",");
            carImagesSize = carImageList.length;
        }

        return carImagesSize;
    }

    /**
     * 시간을 미리세컨드로 변환
     *
     * @param dateString 201912241200
     * @return millis
     */
    public synchronized long getTimeToMillis(String dateString) {

        long resultMillis = 0;

        try {
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");

            Date d = df.parse(dateString);

            Calendar c = Calendar.getInstance();
            c.setTime(d);

            resultMillis = c.getTimeInMillis();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return resultMillis;
    }

    /**
     * 날짜 반환
     * 
     * @param plusDate_ : 값 만큼 더한 날짜 반환
     * @return
     */
    public String getDAYyyyyMMdd(int plusDate_) {

        String resultDate = "";

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = (calendar.get(Calendar.MONTH) + 1);
        int day = (calendar.get(Calendar.DAY_OF_MONTH) + plusDate_);

        resultDate = Integer.toString(year) + String.format("%02d", month) + String.format("%02d", day);

        return resultDate;
    }

    /**
     * 
     * @MethodName getDialogBlurEffect
     * @Description 팝업, 로딩 다이얼로그 뒷 배경 Effect
     * 
     * @return
     */
    public synchronized ColorAdjust getDialogBlurEffect() {
        // 로딩 다이얼로그&팝업창 뒷배경 색상
        ColorAdjust mColorAdjust = new ColorAdjust(0, 0, -0.4, 0);
        // 로딩 다이얼로그&팝업창 뒷배경 블러
        /*
         * BoxBlur mBlur = new BoxBlur(2,2,2); mColorAdjust.setInput(mBlur);
         */
        return mColorAdjust;
    }
    

    /**
     * 문자열 Null, Empty, Length 유효성 확인 함수
     *
     * @param str 확인 문자열
     * @return boolean true : 유효 문자, false : 무효 문자
     */
    public synchronized boolean isValidString(String str) {
        if (str == null || str.equals("") || str.isEmpty()) {
            return false;
        }

        if (str.trim().length() <= 0) {
            return false;
        }

        return true;
    }

}
