package com.nh.auction.utills;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoadingDialog extends Dialog {
    
    public LoadingDialog(String str) {
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/LoadingDialog.fxml"));
            Parent root = loader.load();
            getDialogPane().setContent(root);
            initStyle(StageStyle.UNDECORATED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
