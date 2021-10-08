package com.nh.controller.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.nh.controller.interfaces.MessageStringListener;
import com.nh.controller.setting.SettingApplication;
import com.nh.controller.utils.CommonUtils;
import com.nh.controller.utils.SharedPreference;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * 메세지 전송 팝업 Controller
 * 
 * @author jhlee
 *
 */
public class AuctionMessageController implements Initializable {

	@FXML
	private Button btnSend,btnAddMessage;

	@FXML
	private TextField textFieldSendMessage ,textFieldAddMessage;
	
	@FXML
	private ImageView btnClose;

	@FXML
	private ListView<String> msgListView;

	private MessageStringListener mStringListener;
	
	private ResourceBundle mResMsg = null; // 메세지 처리
	
	private ContextMenu mContextMenu = new ContextMenu();
	
	private int TEXT_MAX = 100;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// get ResMsg
		if (resources != null) {
			mResMsg = resources;
		}
		
		btnSend.setOnMouseClicked(event -> sendMessage(event));
		btnAddMessage.setOnMouseClicked(event -> addMessage());
		btnClose.setOnMouseClicked(event -> close(event));
		
		//저장된 메세지 목록
		initAddAllMessage(getMessageList());
	
		//메세지 선택
		msgListView.setOnMouseClicked(event -> selectMessage(event));
	
		//리스너 등록
		initTextFieldAddListener(textFieldSendMessage);
		initTextFieldAddListener(textFieldAddMessage);
		
		//우클릭 삭제
		initRightClick();	
	}
	
	private void initTextFieldAddListener(TextField textField) {
		
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {

				if (CommonUtils.getInstance().isValidString(newValue)) {

					String tmpStr = newValue;

					int max = TEXT_MAX;

					if(tmpStr.length() > max) {
						textField.setText(oldValue);
					}	
				}
			}
		});
		
	}
	
	/**
	 * 우클릭 삭제
	 */
	private void initRightClick() {
		
		MenuItem item1 = new MenuItem(mResMsg.getString("str.remove"));
		item1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(msgListView.getSelectionModel().getSelectedItem() != null) {
					msgListView.getItems().remove(msgListView.getSelectionModel().getSelectedItem());
					String msgJson = new Gson().toJson(msgListView.getItems());
					SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SEND_MESSAGE, msgJson);
					textFieldSendMessage.setText("");
				}
			}
		});
		mContextMenu.getItems().addAll(item1);

		msgListView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
			@Override
			public void handle(ContextMenuEvent event) {
				// 우클릭 삭제 show
				mContextMenu.show(msgListView, event.getScreenX(), event.getSceneY() +100);
			}
		});			
		
	}
	
	/**
	 * test Message List
	 * @return
	 */
	public List<String> getMessageList() {

		try {
			
			String getJson = SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SEND_MESSAGE, null);
			
			if(CommonUtils.getInstance().isValidString(getJson)) {
				
				ObjectMapper mapper = new ObjectMapper();
				
				List<String> msgDataList = mapper.readValue(getJson, List.class);

				return msgDataList;
			}else {
				return null;
			}
			
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return null;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * 메시지 항목 단일 선택
	 * @param event
	 */
	public void selectMessage(MouseEvent event) {
		
		if(mContextMenu.isShowing()) {
			mContextMenu.hide();
		}
		
		if(msgListView.getSelectionModel().getSelectedItem() != null) {
			String msg = msgListView.getSelectionModel().getSelectedItem();
			textFieldSendMessage.setText(msg);
		}
	
	}

	/**
	 * 메세지 리스너
	 * @param listener
	 */
	public void setCallBackListener(MessageStringListener listener) {
		this.mStringListener = listener;
	}
	
	
	/**
	 * 전송할 메세지 콜백
	 * @param event
	 */
	public void sendMessage(MouseEvent event) {
		if(mStringListener != null) {
			
			String msg = textFieldSendMessage.getText();
			
			if(msg != null && msg.length() > 0) {
				mStringListener.callBack(msg);
				textFieldSendMessage.setText("");
			}else {
				System.out.println("메세지 입력");
			}
			
		}
	}
	
	/**
	 * 메세지 단일 추가
	 * @param event
	 */
	public void addMessage() {
		
		String msg = textFieldAddMessage.getText();
		
		if(msg != null && msg.length() > 0) {
			msgListView.getItems().add(msg);
			msgListView.scrollTo(msgListView.getItems().size() -1);
			textFieldAddMessage.setText("");
			String msgJson = new Gson().toJson(msgListView.getItems());
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SEND_MESSAGE, msgJson);
		}
	}

	/**
	 * ADD 메세지 목록 추가
	 * 
	 * @param str
	 */
	private void initAddAllMessage(List<String> dataList) {
		if (!CommonUtils.getInstance().isListEmpty(dataList)) {
			msgListView.getItems().addAll(dataList);
		}
	}
	
	/**
	 * 창 닫기
	 * @param event
	 */
	public void close(MouseEvent event) {
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		stage.close();
	}
	
	

}
