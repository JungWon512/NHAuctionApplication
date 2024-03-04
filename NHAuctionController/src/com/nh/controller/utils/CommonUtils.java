package com.nh.controller.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.nh.controller.interfaces.BooleanListener;
import com.nh.controller.model.SpBidding;
import com.nh.controller.model.SpEntryInfo;
import com.nh.share.server.models.BidderConnectInfo;
import com.nh.share.setting.AuctionShareSetting;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
 * 공통 유틸리티 관리 클래스
 *
 * @author jhlee
 */
public class CommonUtils {

	private static CommonUtils commonUtils = null;

	public CommonUtils() {
	}

	public static CommonUtils getInstance() {
		if (commonUtils == null) {
			commonUtils = new CommonUtils();
		}
		return commonUtils;
	}

	private Dialog mLoadingDialog; // 로딩 다이얼로그
	private Stage mLoadingDialogStage; // 로딩 다이얼로그 Parent

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

	public double offSetX = 0;
	public double offSetY = 0;

	// 팝업창 타입 [END]

	/**
	 * @param date
	 * @param Time
	 * @return String (ex> 2019.10.10 AM 1:30)
	 * @MethodName getAuctionTimeHoureInAMandPM
	 * @Description 경매 시간을 AM/PM 형식으로 반환 처리.
	 */
	public synchronized String getAuctionTimeHoureInAMandPM(String date, String time) {
		String dateTimeString = date + time;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
		LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
		String result = dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd a HH:mm"));
		return result;
	}

	/**
	 * @param format yyyyMMddHHmmssSSS / yyyyMMddHHmmss / yyyyMMdd
	 * @return String
	 * @MethodName getCurrentTime
	 * @Description 현재 시간을 반환 처리(yyyyMMddHHmmssSSS)
	 */
	public String getCurrentTime(String format) {
		String result = LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));

		return result;
	}

	/**
	 * @param date
	 * @param locale
	 * @return
	 * @MethodName getCurrentTime_yyyyMMdd
	 * @Description
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
	 * @param date
	 * @param locale
	 * @return
	 * @MethodName getCurrentTime_yyyyMMdd
	 * @Description
	 */
	public synchronized String getCurrentTime_MMMMM(Date date, Locale locale) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMMM", locale);
		String result = simpleDateFormat.format(date);
		return result;
	}

	/**
	 * @param format yyyyMM
	 * @return String
	 * @MethodName getCurrentTime
	 * @Description 시간을 반환 처리(yyyyMM)
	 */
	public synchronized String getCurrentTime_yyyyMM(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
		String result = simpleDateFormat.format(date);
		return result;
	}

	/**
	 * @param format yyyy-MM-dd HH:mm
	 * @return String
	 * @MethodName getCurrentTime
	 * @Description 시간을 반환 처리(yyyy-MM-dd HH:mm)
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
	 * @param format yyyy-MM-dd HH:mm:ss
	 * @return String
	 * @MethodName getCurrentTime
	 * @Description 시간을 반환 처리(yyyy-MM-dd HH:mm)
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
	 * @param format yyyy-MM-dd HH:mm:ss
	 * @return String
	 * @MethodName getCurrentTime
	 * @Description 시간을 반환 처리(yyyy-MM-dd HH:mm)
	 */
	public synchronized String getCurrentTime_yyyyMMddHHmmssSSS(String time) {
		String dateTimeString = time;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date date = null;
		try {
			date = simpleDateFormat.parse(dateTimeString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String result = sFormat.format(date);
		return result;
	}

	public synchronized String getTodayYYYYMMDD() {

		String today = "";

		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		today = now.format(formatter);

		return today;
	}

	/**
	 * @param format yyyyMMdd
	 * @return String
	 * @MethodName getCurrentTime_yyyyMMdd_dot
	 * @Description 시간을 반환 처리(yyyy.MM.dd)
	 */
	public synchronized String getCurrentTime_yyyyMMdd(String time) {
		String dateTimeString = time;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = null;
		try {
			date = simpleDateFormat.parse(dateTimeString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
		String result = sFormat.format(date);
		return result;
	}

	/**
	 * 날짜 유효성 검사
	 * @param checkDate
	 * @return
	 */
	public boolean isValidationDate(String checkDate) {

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

			dateFormat.setLenient(false);
			dateFormat.parse(checkDate);
			return true;

		} catch (ParseException e) {
			return false;
		}
	}

	/**
	 * 두 날짜 사이에 개월 수 계산
	 * 
	 * @param fromDateStr 20200101
	 * @param toDateStr   20210917
	 * @return
	 */
	public synchronized String geDiffDateMonth(String fromDateStr, String toDateStr) {

		String baseVal = "4";
		
		String result = "";

		if (!isValidString(toDateStr) || !isValidString(toDateStr)) {
			return result;
		}

		int toDateVal = Integer.parseInt(toDateStr);
		int fromDateVal = Integer.parseInt(fromDateStr);

		if (fromDateVal > toDateVal) {
			return result;
		}

		try {

			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

			Date toDate = format.parse(toDateStr);
			Date fromDate = format.parse(fromDateStr);

			long baseDay = 24 * 60 * 60 * 1000; // 일

			// from 일자와 to 일자의 시간 차이를 계산한다.
			long calDate = toDate.getTime() - fromDate.getTime();

			// from 일자와 to 일자의 시간 차 값을 하루기준으로 나눠 준다.
			double diffDate = calDate / baseDay + 1;
//			long diffMonth = (calDate / baseMonth) + 1;
//			long diffYear = calDate / baseYear;

			double calDiffDate = diffDate / 30;
	
			long roundDate = Math.round(calDiffDate);
			
			if(roundDate >= 4) {
				result = Long.toString(roundDate);
			}else {
				result = baseVal;
			}
			
			
		} catch (Exception e) {
			System.out.println("[error] : " + e);
			return result;
		}

		return result;
	}

	/**
	 * @param stage
	 * @MethodName showLoadingDialog
	 * @Description 공통 프로그래스 다이알로그 Show 함수
	 */
	public synchronized void showLoadingDialog(Stage stage, String message) {

		if (mLoadingDialog != null) {
			dismissLoadingDialog();
		}

		mLoadingDialog = new Dialog<>();
		DialogPane dialogPane = new DialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("/com/nh/controller/css/utils.css").toExternalForm());
		mLoadingDialog.initModality(Modality.NONE);
		mLoadingDialog.initStyle(StageStyle.UNDECORATED);
		mLoadingDialog.setDialogPane(dialogPane);

		dialogPane.setPrefWidth(480);
		dialogPane.setPrefHeight(320);

		VBox vbox = new VBox();
		vbox.setSpacing(15);
		vbox.setAlignment(Pos.CENTER);
		// alert popup message label
		Label contentText = new Label();

		if (message != null && !message.isEmpty()) {
			contentText.setText(message);
		} else {
			contentText.setText("Loading...");
		}

		contentText.setTextAlignment(TextAlignment.CENTER);
		contentText.setAlignment(Pos.CENTER);

		ImageView imageView = new ImageView();
		imageView.preserveRatioProperty().set(true);
		imageView.setPickOnBounds(true);
		imageView.setFitWidth(120);
		imageView.setFitHeight(120);
		Image image = new Image("/com/nh/controller/resource/images/ic_logo.png");
		imageView.setImage(image);

		ImageView progressImageView = new ImageView();
		progressImageView.preserveRatioProperty().set(true);
		progressImageView.setPickOnBounds(true);
		progressImageView.setFitWidth(55);
		progressImageView.setFitHeight(55);
		Image progressImage = new Image("/com/nh/controller/resource/images/ic_loading.gif");
		progressImageView.setImage(progressImage);

		HBox.setHgrow(imageView, Priority.ALWAYS);
		HBox.setHgrow(contentText, Priority.ALWAYS);
		HBox.setHgrow(progressImageView, Priority.ALWAYS);

		vbox.getChildren().add(imageView);
		vbox.getChildren().add(contentText);
		vbox.getChildren().add(progressImageView);

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
	 * dialog show 상태 체크
	 * @return
	 */
	public boolean isLoadingDialogShowing() {
		
		if (mLoadingDialog != null) {
			return mLoadingDialog.isShowing();
		}else {
			return false;
		}
	}

	/**
	 * @param stage
	 * @MethodName showLoadingDialog
	 * @Description 공통 프로그래스 다이알로그 Show 함수
	 */
	public synchronized void showLoadingDialog(Dialog<Void> showingDialog, String message) {

		if (mLoadingDialog != null) {
			dismissLoadingDialog();
		}

		mLoadingDialog = new Dialog<>();
		DialogPane dialogPane = new DialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("/com/nh/controller/css/utils.css").toExternalForm());
		mLoadingDialog.initModality(Modality.NONE);
		mLoadingDialog.initStyle(StageStyle.UNDECORATED);
		mLoadingDialog.setDialogPane(dialogPane);

		mLoadingDialog.initOwner(showingDialog.getDialogPane().getScene().getWindow());
		
		dialogPane.setPrefWidth(480);
		dialogPane.setPrefHeight(320);

		VBox vbox = new VBox();
		vbox.setSpacing(15);
		vbox.setAlignment(Pos.CENTER);
		// alert popup message label
		Label contentText = new Label();

		if (message != null && !message.isEmpty()) {
			contentText.setText(message);
		} else {
			contentText.setText("Loading...");
		}

		contentText.setTextAlignment(TextAlignment.CENTER);
		contentText.setAlignment(Pos.CENTER);

		ImageView imageView = new ImageView();
		imageView.preserveRatioProperty().set(true);
		imageView.setPickOnBounds(true);
		imageView.setFitWidth(120);
		imageView.setFitHeight(120);
		Image image = new Image("/com/nh/controller/resource/images/ic_logo.png");
		imageView.setImage(image);

		ImageView progressImageView = new ImageView();
		progressImageView.preserveRatioProperty().set(true);
		progressImageView.setPickOnBounds(true);
		progressImageView.setFitWidth(55);
		progressImageView.setFitHeight(55);
		Image progressImage = new Image("/com/nh/controller/resource/images/ic_loading.gif");
		progressImageView.setImage(progressImage);

		HBox.setHgrow(imageView, Priority.ALWAYS);
		HBox.setHgrow(contentText, Priority.ALWAYS);
		HBox.setHgrow(progressImageView, Priority.ALWAYS);

		vbox.getChildren().add(imageView);
		vbox.getChildren().add(contentText);
		vbox.getChildren().add(progressImageView);

		dialogPane.setContent(vbox);

		mLoadingDialog.show();
	}
	
	/**
	 * @MethodName dismissLoadingDialog
	 * @Description 공통 프로그래스 다이알로그 Dismiss 처리 함수
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
	 * @param node     타겟 뷰
	 * @param message  내용
	 * @param position 위치 ( 상/상단 중앙/하/좌/우/)
	 * @MethodName showTooltip
	 * @Description 툴팁 표시
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
				tooltip.show(node, (boundsInScreen.getMinX() + x) - messageCenter, boundsInScreen.getMinY() - (tooltipHeight + 5));
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
	 * @param tooltip
	 * @MethodName tooltipStartTiming
	 * @Description 툴팁 표시 시간.
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
	 * @param node
	 * @param isVisible true : 보임 / false : 숨김
	 * @MethodName hideShowBehaviour
	 * @Description View 보임/숨김 처리
	 */
	public synchronized void setHideShowBehaviour(Node node, boolean isVisible) {
		if (node != null && (node.isVisible() == isVisible)) {
			return;
		}
		node.setVisible(isVisible);
		node.managedProperty().bind(node.visibleProperty());
	}

	/**
	 * @param node
	 * @param isDisable true 비활성화 /false 활성화
	 * @MethodName setNodeDisable
	 * @Description View 활성화/비활성화 처리
	 */
	public synchronized void setNodeDisable(Node node, boolean isDisable) {
		if (node != null && (node.isDisable() == isDisable)) {
			return;
		}
		node.setDisable(isDisable);
	}

	/**
	 * @return String
	 * @MethodName getCurrentTime
	 * @Description 현재 시간을 반환 처리(yyyyMMddHHmmssSSS)
	 */
	public synchronized String getCurrentTime() {
		SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date current = new Date();
		String result = date.format(current);

		return result;
	}

	/**
	 * @return String
	 * @MethodName getCurrentTime
	 * @Description 현재 시간을 반환 처리(yyyy-MM-dd HH:mm:ss.SSS)
	 */
	public synchronized String getCurrentTimeSc() {
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date current = new Date();
		String result = date.format(current);

		return result;
	}

	/**
	 * @param textField
	 * @MethodName setEngAndNumber
	 * @Description 텍스트 필드 영문&숫자 입력
	 */
	public synchronized void setTextStyle_Eng_Number(TextField textField) {
		textField.setTextFormatter(new TextFormatter<>(change -> (change.getControlNewText().matches("^[a-zA-Z0-9]*$")) ? change : null));
	}

	/**
	 * @param textField
	 * @MethodName setEngAndNumber
	 * @Description 텍스트 필드 영문만입력
	 */
	public synchronized void setTextStyleOnly_Eng(TextField textField) {
		textField.setTextFormatter(new TextFormatter<>(change -> (change.getControlNewText().matches("^[a-zA-Z]*$")) ? change : null));
	}

	/**
	 * @param textField
	 * @MethodName setOnlyNumber
	 * @Description 텍스트 필드 숫자만 입력
	 */
	public synchronized void setTextStyleOnly_Number(TextField textField) {
		textField.setTextFormatter(new TextFormatter<>(change -> (change.getControlNewText().matches("^[0-9]*$")) ? change : null));
	}

	/**
	 * @param textField
	 * @MethodName setTextStyleKrOnly
	 * @Description 텍스트 필드 국문만 입력
	 */
	public synchronized void setTextStyleOnly_Kr(TextField textField) {
		textField.setTextFormatter(new TextFormatter<>(change -> (change.getControlNewText().matches("^[가-힣]*$")) ? change : null));
	}

	/**
	 * @param textField
	 * @MethodName setTextStyle_Eng_Number_SpecialChar
	 * @Description 텍스트 필드 영문,숫자,특수문자만 입력
	 */
	public synchronized void setTextStyle_Eng_Number_SpecialChar(TextField textField) {
		textField.setTextFormatter(new TextFormatter<>(change -> (change.getControlNewText().matches("^[a-zA-Z0-9\"'\\{\\}\\[\\]/?.,;:|\\)\\(*~`!^\\-_+&<>@#$%^\\\\=]*$")) ? change : null));
	}

	/**
	 * @param node
	 * @MethodName requestFocus
	 * @Description 해당 뷰에 포커스
	 */
	public synchronized void requestFocus(Node node) {
		Platform.runLater(() -> {
			if (!node.isFocused()) {
				node.requestFocus();
			}
		});
	}

	/**
	 * @param message
	 * @param buttonTitle
	 * @return Optional<ButtonType>
	 * @MethodName showAlertPopupOneButton
	 * @Description 버튼 1개 팝업창.
	 */
	public Optional<ButtonType> showAlertPopupOneButton(Stage stage, String message, String buttonTitle) {
		Dialog<ButtonType> dialog = setAlertPopupStyle(stage, ALERTPOPUP_ONE_BUTTON, message, buttonTitle, "");
		Optional<ButtonType> result = dialog.showAndWait();
		return result;
	}

	/**
	 * @param message
	 * @param buttonTitle
	 * @return Optional<ButtonType>
	 * @MethodName showAlertPopupOneButton
	 * @Description 버튼 1개 팝업창. Dialog 위에 Dialog 상황일때 사용
	 */
	public Optional<ButtonType> showAlertPopupOneButton(Dialog<Void> showingDialog, String message, String buttonTitle) {
		Dialog<ButtonType> dialog = setAlertPopupStyle(showingDialog, ALERTPOPUP_ONE_BUTTON, message, buttonTitle, "");
		Optional<ButtonType> result = dialog.showAndWait();
		return result;
	}

	/**
	 * @param message
	 * @param leftButtonTitle
	 * @param rightButtonTitle
	 * @return Optional<ButtonType>
	 * @MethodName showAlertPopupTwoButton
	 * @Description 버튼 2개 팝업창.
	 */
	public Optional<ButtonType> showAlertPopupTwoButton(Stage stage, String message, String leftButtonTitle, String rightButtonTitle) {
		Dialog<ButtonType> dialog = setAlertPopupStyle(stage, ALERTPOPUP_TWO_BUTTON, message, leftButtonTitle, rightButtonTitle);
		Optional<ButtonType> result = dialog.showAndWait();
		return result;
	}
	
	/**
	 * @param message
	 * @param leftButtonTitle
	 * @param rightButtonTitle
	 * @return Optional<ButtonType>
	 * @MethodName showAlertPopupTwoButton
	 * @Description 버튼 2개 팝업창.
	 */
	public Optional<ButtonType> showAlertPopupTwoButton(Dialog<Void> showingDialog, String message, String leftButtonTitle, String rightButtonTitle) {
		Dialog<ButtonType> dialog = setAlertPopupStyle(showingDialog, ALERTPOPUP_TWO_BUTTON, message, leftButtonTitle, rightButtonTitle);
		Optional<ButtonType> result = dialog.showAndWait();
		return result;
	}

	/**
	 * @param alertPopupType
	 * @param message
	 * @param leftButtonTitle
	 * @param rightButtonTitle
	 * @return
	 * @MethodName setAlertPopupStyle
	 * @Description 팝업창 Layout 셋팅
	 */
	public Dialog<ButtonType> setAlertPopupStyle(Stage stage, String alertPopupType, String message, String leftButtonTitle, String rightButtonTitle) {
		
		Dialog<ButtonType> dialog = new Dialog<>();
		DialogPane dialogPane = new DialogPane() {
			@Override
			protected Node createButtonBar() {
				ButtonBar buttonBar = (ButtonBar) super.createButtonBar();
				buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_NONE);
				return buttonBar;
			}
		};

		dialogPane.getStylesheets().add(getClass().getResource("/com/nh/controller/css/utils.css").toExternalForm());

		if (stage != null) {
			dialog.initOwner(stage);
		} else {
			dialog.initOwner(dialogPane.getScene().getWindow());

		}

		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initStyle(StageStyle.UNDECORATED);
		dialog.setDialogPane(dialogPane);
		dialogPane.setPadding(new Insets(10));
		dialogPane.setMinHeight(200);
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

		if (stage != null) {
			double centerXPosition = stage.getX() + stage.getWidth() / 2d;
			double centerYPosition = stage.getY() + stage.getHeight() / 2d;
			dialog.setX(centerXPosition - dialogPane.getWidth() / 2d);
			dialog.setY(centerYPosition - dialogPane.getHeight() / 2d);

			// 팝업창 뒷 배경 블러처리 Add
			stage.getScene().getRoot().setEffect(getDialogBlurEffect());

			// 팝업창 뒷 배경 블러처리 remove
			dialog.setOnCloseRequest(event -> {
				stage.getScene().getRoot().setEffect(null);
			});

		}

		return dialog;
	}

	/**
	 * @param alertPopupType
	 * @param message
	 * @param leftButtonTitle
	 * @param rightButtonTitle
	 * @return
	 * @MethodName setAlertPopupStyle
	 * @Description 팝업창 Layout 셋팅
	 */
	public Dialog<ButtonType> setAlertPopupStyle(Dialog<Void> showingDialog, String alertPopupType, String message, String leftButtonTitle, String rightButtonTitle) {
		Dialog<ButtonType> dialog = new Dialog<>();
		DialogPane dialogPane = new DialogPane() {
			@Override
			protected Node createButtonBar() {
				ButtonBar buttonBar = (ButtonBar) super.createButtonBar();
				buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_NONE);
				return buttonBar;
			}
		};

		dialogPane.getStylesheets().add(getClass().getResource("/com/nh/controller/css/utils.css").toExternalForm());
		dialog.initOwner(showingDialog.getDialogPane().getScene().getWindow());

		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initStyle(StageStyle.UNDECORATED);
		dialog.setDialogPane(dialogPane);
		dialogPane.setPadding(new Insets(10));
		dialogPane.setMinHeight(200);
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

		if (showingDialog != null && showingDialog.isShowing()) {

			double centerXPosition = showingDialog.getX() + showingDialog.getWidth() / 2d;
			double centerYPosition = showingDialog.getY() + showingDialog.getHeight() / 2d;
			dialog.setX(centerXPosition - dialogPane.getWidth() / 2d);
			dialog.setY(centerYPosition - dialogPane.getHeight() / 2d);

			// 팝업창 뒷 배경 블러처리 Add
			showingDialog.getDialogPane().getScene().getRoot().setEffect(getDialogBlurEffect());

			// 팝업창 뒷 배경 블러처리 remove
			dialog.setOnCloseRequest(event -> {
				showingDialog.getDialogPane().getScene().getRoot().setEffect(null);
			});
		}

		return dialog;
	}

	/**
	 * @param text
	 * @return
	 * @MethodName encodingToBase64
	 * @Description 입력받은 text를 Base64로 인코딩한다.
	 */
	public synchronized String encodingToBase64(String text) {
		byte[] targetBytes = text.getBytes();
		Encoder encoder = Base64.getEncoder();
		byte[] encodedBytes = encoder.encode(targetBytes);
		return new String(encodedBytes);
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
	 * @return
	 * @MethodName getDialogBlurEffect
	 * @Description 팝업, 로딩 다이얼로그 뒷 배경 Effect
	 */
	public synchronized ColorAdjust getDialogBlurEffect() {
		// 로딩 다이얼로그&팝업창 뒷배경 색상
		ColorAdjust mColorAdjust = new ColorAdjust(0, 0, -0.4, 0);
		// 로딩 다이얼로그&팝업창 뒷배경 블러
		return mColorAdjust;
	}

	/**
	 * 리스트 Null 체크
	 *
	 * @author 이종환
	 */
	public boolean isListEmpty(List<?> list) {
		if ((list != null) && (list.size() != 0))
			return false;

		return true;
	}

	/**
	 * 현재 노드 스테이지 반환
	 *
	 * @param node
	 * @return
	 */
	public Stage getStage(Node node) {

		Stage stage = null;

		if (node != null) {
			stage = (Stage) node.getScene().getWindow();
		}
		return stage;
	}

	public void showToastMessage(Node node, BooleanListener listener) {

		FadeTransition mAnimationFadeIn = new FadeTransition(Duration.millis(500));
		mAnimationFadeIn.setNode(node);
		mAnimationFadeIn.setFromValue(0.0);
		mAnimationFadeIn.setToValue(1.0);
		mAnimationFadeIn.setCycleCount(1);
		mAnimationFadeIn.setAutoReverse(false);

		FadeTransition mAnimationFadeOut = new FadeTransition(Duration.millis(1000));
		mAnimationFadeOut.setNode(node);
		mAnimationFadeOut.setFromValue(1.0);
		mAnimationFadeOut.setToValue(0.0);
		mAnimationFadeOut.setCycleCount(1);
		mAnimationFadeOut.setAutoReverse(false);

		mAnimationFadeIn.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				Timer timer = new Timer(true);
				TimerTask timerTask = new TimerTask() {
					@Override
					public void run() {
						mAnimationFadeOut.playFromStart();
					}
				};
				timer.schedule(timerTask, 2000);
			}
		});

		mAnimationFadeOut.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				if (node.isVisible()) {
					node.setVisible(false);
					listener.callBack(true);
				}
			}
		});

		node.setVisible(true);
		mAnimationFadeIn.playFromStart();
	}

	/**
	 * 마우스 드래그 이동
	 *
	 * @param stage
	 * @param node
	 */
	public void canMoveStage(Stage stage, Node node) {

		Platform.runLater(() -> {

			if (node == null) {

				stage.getScene().setOnMousePressed((event) -> {

					if (stage.getScene().getRoot().isDisable()) {
						return;
					}

					offSetX = event.getSceneX();
					offSetY = event.getSceneY();
				});

				stage.getScene().setOnMouseDragged((event) -> {

					if (stage.getScene().getRoot().isDisable()) {
						return;
					}

					stage.setX(event.getScreenX() - offSetX);
					stage.setY(event.getScreenY() - offSetY);
				});
			} else {

				if (node.isDisable()) {
					return;
				}

				node.setOnMousePressed((event) -> {
					offSetX = event.getSceneX();
					offSetY = event.getSceneY();
				});

				node.setOnMouseDragged((event) -> {
					stage.setX(event.getScreenX() - offSetX);
					stage.setY(event.getScreenY() - offSetY);
				});
			}

		});
	}

	/**
	 * 더블클릭 풀스크린 모드
	 *
	 * @param stage
	 * @param node
	 */
	public void onDoubleClickfullScreenMode(Stage stage, Node node, String message) {

		Platform.runLater(() -> {

			if (node != null) {
				node.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent mouseEvent) {
						if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
							if (mouseEvent.getClickCount() == 2) {
								if (!stage.isFullScreen()) {

									if (message != null && !message.isEmpty()) {
										stage.setFullScreenExitHint(message);
									} else {
//										stage.setFullScreenExitHint("");
									}

									stage.setFullScreen(true);
								} else {
									stage.setFullScreen(false);
								}
							}
						}
					}
				});
			} else {
				stage.getScene().setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent mouseEvent) {
						if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
							if (mouseEvent.getClickCount() == 2) {
								if (!stage.isFullScreen()) {

									if (message != null && !message.isEmpty()) {
										stage.setFullScreenExitHint(message);
									} else {
//										stage.setFullScreenExitHint("");
									}

									stage.setFullScreen(true);
								} else {
									stage.setFullScreen(false);
								}
							}
						}
					}
				});
			}
		});
	}

	/**
	 * css 추가
	 *
	 * @param node
	 * @param name
	 */
	public void addStyleClass(Node node, String name) {
		node.getStyleClass().add(name);
	}

	/**
	 * css 삭제
	 *
	 * @param node
	 * @param name
	 */
	public void removeStyleClass(Node node, String name) {
		node.getStyleClass().removeAll(Collections.singleton(name));
	}

	/**
	 * 기준 금액 적용 만단위,천단위 param (str : 4,500,000 , baseUnit : 10,000 or 1000)
	 *
	 * @param str
	 * @return 4,500,000 / 10,000 = 450 4500000 / 1000 = 4500
	 */
	public int getBaseUnitDivision(String str, int baseUnit) {

		int price = Integer.parseInt(str);
		int resultPrice = price / baseUnit;

		return resultPrice;
	}

	/**
	 * 기준 금액 적용 param (str : 450 , baseUnit : 10,000)
	 *
	 * @param str
	 * @return 450 * 10000 = 4,500,000
	 */
	public int getBaseUnitMultipl(String str, int baseUnit) {

		int price = Integer.parseInt(str);
		int resultPrice = price * baseUnit;

		return resultPrice;
	}

	/**
	 * 콤마 표시
	 *
	 * @param str
	 * @return
	 */
	public String getNumberFormatComma(int value) {

		if (value <= 0) {
			return "0";
		}

		NumberFormat integerFormat = new DecimalFormat("#,###");

		return integerFormat.format(value);
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
	
	/**
	 * 문자열 Null, Empty, Length 유효성 확인 
	 * IP 유효성 확인
	 *
	 * @param str 확인 문자열
	 */
	public synchronized boolean isValidIp(String str) {
		if (str == null || str.equals("") || str.isEmpty()) {
			return false;
		}

		if (str.trim().length() <= 0) {
			return false;
		}
		
		
		/* by kih 2024.03.04 주석처리 
		String ipRegex = "(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])";
		
		if(!str.matches(ipRegex)) {
			return false;
		}
		*/
		
		return true;
	}
	

	/**
	 * 텍스트 필드 숫자만 입력
	 */
	public void setNumberTextField(TextField textField) {

		UnaryOperator<Change> filter = change -> {
			String text = change.getText();

			if (text.matches("[0-9]*")) {
				return change;
			}

			return null;
		};

		TextFormatter<String> textFormatter = new TextFormatter<>(filter);
		textField.setTextFormatter(textFormatter);
	}
	
	/**
	 * 문자열에 | 존재시 특수문자 ｜ 로 치환
	 * @param str
	 * @return
	 */
	public String replaceDelimiter(String str) {

		if(isValidString(str)) {			
			if(str.indexOf(AuctionShareSetting.DELIMITER) > 0) {
				str = str.replace(String.valueOf(AuctionShareSetting.DELIMITER), ",");
			}
		}
		return str;
	}

	/**
	 * 컬럼 센터 정렬
	 * 
	 * @param col
	 */
	public <T> void setAlignCenterCol(TableColumn<T, String> col) {
		col.getStyleClass().add("center-column");
	}

	/**
	 * 컬럼 왼쪽 정렬
	 * 
	 * @param col
	 */
	public void setAlignLeftCol(TableColumn<SpEntryInfo, String> col) {
		col.getStyleClass().add("left-column");
	}

	/**
	 * 컬럼 오른쪽 정렬
	 * 
	 * @param col
	 */
	public void setAlignRightCol(TableColumn<SpBidding, String> col) {
		col.getStyleClass().add("right-column");
	}
	
	/**
	 * 컬럼 데이터 콤마 표시
	 *
	 * @param column
	 */
	public synchronized <T> void setNumberColumnFactory(TableColumn<T, String> column, boolean isPrice) {

		column.setCellFactory(col -> new TableCell<T, String>() {
			@Override
			protected void updateItem(String value, boolean empty) {
				super.updateItem(value, empty);

				if (CommonUtils.getInstance().isValidString(value)) {
					setText(CommonUtils.getInstance().getNumberFormatComma(Integer.parseInt(value)));
				} else {
					setText("");
				}
			}
		});
	}
	
	

	/**
	 * DataList 5개씩 자르기
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection<List<BidderConnectInfo>> partDataList(List<BidderConnectInfo> list, int size) {

		Collection Rlist = Arrays.asList();

		Rlist = partition(list, size);

		return Rlist;
	}
	
	private <T> Collection<List<T>> partition(List<T> list, int size) {
		final AtomicInteger counter = new AtomicInteger(0);
		return list.stream().collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size)).values();
	}

	/**
	 * null : true , not null : false;
	 *
	 * @param strProperty
	 * @return
	 */
	public boolean isEmptyProperty(StringProperty strProperty) {

		if (strProperty != null && !strProperty.getValue().equals("")) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 어플리케이션 종료
	 */
	public void applicationExit() {
		Platform.exit();
		System.exit(0);
	}
	
	
	/**
	 * 기본 브라우저 open
	 */
	public void openBrowse(String url , boolean isAppExit) {
		
		if(!isValidString(url)) {
			return;
		}
		
		try {

			//브라우저
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI(url));
			}
			
			if(isAppExit) {
				applicationExit();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
