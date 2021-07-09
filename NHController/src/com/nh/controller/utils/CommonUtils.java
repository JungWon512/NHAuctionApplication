package com.nh.controller.utils;

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
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import com.nh.controller.interfaces.BooleanListener;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
 *
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
	// 팝업창 타입 [END]

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
	 * 
	 * @MethodName getCurrentTime
	 * @Description 현재 시간을 반환 처리(yyyyMMddHHmmssSSS)
	 *
	 * @param format yyyyMMddHHmmssSSS / yyyyMMddHHmmss / yyyyMMdd
	 * @return String
	 */
	public String getCurrentTime(String format) {
		String result = LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));

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
	 * @MethodName getCurrentTime
	 * @Description 시간을 반환 처리(yyyy-MM-dd HH:mm)
	 *
	 * @param format yyyy-MM-dd HH:mm:ss
	 * @return String
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
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
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
		dialogPane.getStylesheets().add(getClass().getResource("/com/nh/controller/css/utils.css").toExternalForm());
		mLoadingDialog.initModality(Modality.NONE);
		mLoadingDialog.initStyle(StageStyle.UNDECORATED);
		mLoadingDialog.setDialogPane(dialogPane);
		
		dialogPane.setPrefWidth(340);
		
		VBox vbox = new VBox();
		vbox.setSpacing(6);
		vbox.setAlignment(Pos.CENTER);
		// alert popup message label
		Label contentText = new Label();
		
		if(message != null && !message.isEmpty()) {
			contentText.setText(message);
		}else {
			contentText.setText("Loading...");
		}
	
		contentText.setTextAlignment(TextAlignment.CENTER);
		contentText.setAlignment(Pos.CENTER);

		ImageView imageView = new ImageView();
		imageView.preserveRatioProperty().set(true);
		imageView.setPickOnBounds(true);
		imageView.setFitWidth(60);
		imageView.setFitHeight(60);
		Image image = new Image("/com/nh/controller/resource/ic_cow.png");
		imageView.setImage(image);

		ImageView progressImageView = new ImageView();
		progressImageView.preserveRatioProperty().set(true);
		progressImageView.setPickOnBounds(true);
		progressImageView.setFitWidth(60);
		progressImageView.setFitHeight(60);
		Image progressImage = new Image("/com/nh/controller/resource/ic_loading.gif");
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

		dialogPane.getStylesheets().add(getClass().getResource("/com/nh/controller/css/utils.css").toExternalForm());
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


	public void showToastMessage(Node node,BooleanListener listener) {
		
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
}
