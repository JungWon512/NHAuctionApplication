package com.nh.controller.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AuctionMessageController implements Initializable {

    private final Logger mLogger = LogManager.getLogger(AuctionMessageController.class);

    private final Preferences prefs = Preferences.userNodeForPackage(AuctionMessageController.class);
    private final String PREFERENCE_MESSAGE = "PREFERENCE_MESSAGE"; // 메시지 내용 목록 Key
    private final String PREFERENCE_MESSAGE_CNT = "PREFERENCE_MESSAGE_CNT"; // 메시지 내용 수 Key

    @FXML
    private TextField mTextFieldSendMessage; // 전송 메시지 내용

    @FXML
    public Button mBtnSend; // 전송 버튼

    @FXML
    private ListView<String> mListViewMessage; // 전송 메시지 내용 ListView

    @FXML
    private TextField mTextFieldAddMessage; // 추가할 메시지 내용

    @FXML
    private Button mBtnMessageAdd; // 추가할 메시지 내용 저장

    private Stage mStage;

    private ResourceBundle mCurrentResources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        mCurrentResources = resources;
    }

    public void setStage(Stage stage) {
        mStage = stage;
        mStage.setTitle(mCurrentResources.getString("str.auction.controller.message"));

        // 현재 Preferences에 저장되어 있는 메시지 수가 0이면 고정 문구 셋팅.
        if (prefs.getInt(PREFERENCE_MESSAGE_CNT, 0) == 0) {
            ArrayList<String> messageList = new ArrayList<String>();
            messageList.add(mCurrentResources.getString("str.message1"));
            messageList.add(mCurrentResources.getString("str.message2"));
            messageList.add(mCurrentResources.getString("str.message3"));
            messageList.add(mCurrentResources.getString("str.message4"));
            messageList.add(mCurrentResources.getString("str.message5"));
            setMessageList(messageList);
        }
        loadListView();

        // ListView Item Click Event Add
        mListViewMessage.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                String selectMessage = mListViewMessage.getSelectionModel().getSelectedItem();
                mTextFieldSendMessage.setText(selectMessage);
            }

        });
    }

    /**
     * 
     * @MethodName loadListView
     * @Description 메시지 목록 load
     *
     */
    private void loadListView() {
        ObservableList<String> items = FXCollections.observableArrayList(getMessageList());
        mListViewMessage.setItems(items);
        mListViewMessage.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {

            @Override
            public ListCell<String> call(ListView<String> param) {
                MessageCell cell = new MessageCell();
                cell.mBtnDelete.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        int selectIndex = cell.getIndex();
                        mListViewMessage.getItems().remove(selectIndex);
                        removeMessageList(selectIndex);
                    }

                });
                return cell;
            }

        });
    }

    /**
     * 
     * @MethodName setMessageList
     * @Description 메시지 목록을 Preferences에 저장
     * 
     * @param messageList
     */
    private void setMessageList(ArrayList<String> messageList) {
        for (int i = 0; i < messageList.size(); i++) {
            String message = messageList.get(i);
            prefs.put(PREFERENCE_MESSAGE + i, message);
        }
        prefs.putInt(PREFERENCE_MESSAGE_CNT, messageList.size());
    }

    /**
     * 
     * @MethodName getMessageList
     * @Description Preferences에 저장되어 있는 메시지 목록을 조회
     * 
     * @return
     */
    private ArrayList<String> getMessageList() {
        int count = prefs.getInt(PREFERENCE_MESSAGE_CNT, 0);
        ArrayList<String> messageList = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            messageList.add(prefs.get(PREFERENCE_MESSAGE + i, ""));
        }
        return messageList;
    }

    /**
     * 
     * @MethodName addMessageList
     * @Description 추가할 메시지 문구를 Preferences에 추가
     * 
     * @param message
     */
    private void addMessageList(String message) {
        int count = prefs.getInt(PREFERENCE_MESSAGE_CNT, 0);
        prefs.put(PREFERENCE_MESSAGE + count, message);
        prefs.putInt(PREFERENCE_MESSAGE_CNT, count + 1);
    }

    /**
     * 
     * @MethodName removeMessageList
     * @Description 메시지 Cell Item 삭제
     * 
     * @param index
     */
    private void removeMessageList(int index) {
        ArrayList<String> messageList = getMessageList();
        messageList.remove(index);
        try {
            prefs.clear(); // 전체 삭제
            setMessageList(messageList); // 다시 셋팅
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @MethodName sendMessageTextNullCheck
     * @Description 전송 메시지 Text Null Check
     * 
     * @return
     */
    public boolean sendMessageTextNullCheck() {
        String sendMessage = mTextFieldSendMessage.getText();
        sendMessage = sendMessage.trim();
        if (sendMessage != null && sendMessage.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 
     * @MethodName getSendMessage
     * @Description 전송 메시지 문구 return
     * 
     * @return
     */
    public String getSendMessage() {
        return mTextFieldSendMessage.getText();
    }

    @FXML
    private void onActionMessageAdd() {
        // 메시지 추가
        String addMessageText = mTextFieldAddMessage.getText();
        addMessageText = addMessageText.trim();
        if (addMessageText != null && addMessageText.length() > 0) {
            addMessageList(addMessageText);
            loadListView();
        } else {
            mLogger.info("추가하실 메시지 내용을 입력하세요.");
        }
        mTextFieldAddMessage.setText("");
    }

    @FXML
    private void onActionClose() {
        // 화면 닫기
        mStage.close();
    }

    /**
     * 
     * @ClassName AuctionMessageController.java
     * @Description 메시지 ListView Cell.
     * @anthor ishift
     * @since
     */
    private class MessageCell extends ListCell<String> {
        HBox mHBox = new HBox();
        Label mLabelMessage = new Label();
        Pane mPane = new Pane();
        Button mBtnDelete = new Button("-");

        public MessageCell() {
            super();
            mBtnDelete.getStyleClass().add("control-popup-button");
            mBtnDelete.getStyleClass().add("control-popup-button:hover");
            mHBox.getChildren().addAll(mLabelMessage, mPane, mBtnDelete);
            mHBox.setAlignment(Pos.CENTER_LEFT); // Label, Button 정렬
            HBox.setHgrow(mPane, Priority.ALWAYS);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty) {
                setGraphic(null);
            } else {
                mLabelMessage.setText(item);
                setGraphic(mHBox);
            }
        }
    }
}
