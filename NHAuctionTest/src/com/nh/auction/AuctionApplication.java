package com.nh.auction;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.auction.controller.BaseAuction;
import com.nh.auction.utills.MoveStageUtil;

import javafx.application.Application;
import javafx.stage.Stage;

public class AuctionApplication extends Application {

    private Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void start(Stage primaryStage) {
        try {

            startMain(primaryStage);
        } catch (Exception e) {
            mLogger.debug("Main Exception : " + e.toString());
            e.printStackTrace();
        }
    }

    private void startMain(Stage primaryStage) throws Exception {
        Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(this));
        MoveStageUtil.getInstance().moveTestPage(primaryStage);
    }

    public static void main(String[] args) {
        
        long heapSize = Runtime.getRuntime().totalMemory();
        System.out.println("HeapSize : " + heapSize);
        System.out.println("HeapSize(M) : " + heapSize / (1024 * 1024) + "MB");
        launch(args);
    }

    private class ShutdownHookThread extends Thread {

        AuctionApplication parent;

        public ShutdownHookThread(AuctionApplication parent) {
            this.parent = parent;
        }

        public void run() {
            parent.quit();
        }
    }

    /**
     * 서비스 종료
     */
    private void quit() {
        try {
            mLogger.debug("[ShutdownHookThread AuctionApplication]");

            //네티 종료
            BaseAuction.getAuctionInstance().onAuctionAllClose(null);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
