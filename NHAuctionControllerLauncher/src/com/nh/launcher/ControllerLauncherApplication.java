package com.nh.launcher;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class ControllerLauncherApplication extends Application {
	private Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private ResourceBundle mResMsg = null;
	
	@Override
	public void start(Stage arg0) throws Exception {
		moveIntroStage(arg0);
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * 로그인
	 * 
	 * @param stage
	 */
	public synchronized void moveIntroStage(Stage stage) {

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getFXMLResource("IntroView.fxml"), getResourceBundle());
			Parent parent = fxmlLoader.load();
			IntroController controller = fxmlLoader.getController();
			controller.setStage(stage);
			stage.initStyle(StageStyle.UNDECORATED);
			Scene scene = new Scene(parent);
			stage.setScene(scene);
			stage.show();
			
			Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
			stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
			stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
		
			setWindowClose(stage);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setWindowClose(Stage stage) {
		
		if (stage != null) {
			
			stage.setOnCloseRequest(e -> {
				Platform.exit();
				System.exit(0);
			});
		}
	}
	
	public synchronized URL getFXMLResource(String fxmlName_) {
		return getApplicationClass().getResource("view/" + fxmlName_);
	}
	
	public ResourceBundle getResourceBundle() {
		return ResourceBundle.getBundle("com/nh/controller/utils/properties/UIResources");
	}
	
	private Class<?> getApplicationClass() {
		return ControllerLauncherApplication.class;
	}
}
