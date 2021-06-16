package com.nh.auctionserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nh.auctionserver.netty.AuctionServer;
import com.nh.auctionserver.setting.AuctionServerSetting;
import com.nh.share.setting.AuctionShareSetting;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Hello Auction World!
 *
 */
public class AuctionServerApplication {
	private static AuctionServer mServer;
	private static final Logger mLogger = LogManager.getLogger(AuctionServerApplication.class);

	public static void main(String[] args) {
		if (args.length < 1) {
			runningError();
		} else if (args.length == 1) {
			if (args[0].toString().contains("version")) {
				versionInfo();
			} else {
				runningError();
			}
		} else {
			// 'java -jar AuctionServer.jar start [port]'
			if (args.length == 2) {
				if (args[0].toString().contains("start")) {
					runServer(args);
				} else {
					runningError();					
				}
			} else {
				runningError();
			}
		}
		FinishSignalHandler finishSignalHandler = new FinishSignalHandler();
		Signal.handle(new Signal("INT"), finishSignalHandler);
		// Signal.handle(new Signal("PIPE"), finishSignalHandler);
		Signal.handle(new Signal("ALRM"), finishSignalHandler);
		Signal.handle(new Signal("TERM"), finishSignalHandler);

		// 21.01.20 SIGPIPE 시그널 발생 시 처리 로직 변경
		PipeSignalHandler pipeSignalHandler = new PipeSignalHandler();
		Signal.handle(new Signal("PIPE"), pipeSignalHandler); // SignalHandler.SIG_IGN;
	}

	public static class FinishSignalHandler implements SignalHandler {
		@Override
		public void handle(Signal arg0) {
			mLogger.debug("FinishSignalHandler Signal : " + arg0);
			if (mServer != null) {
				mServer.stopServer();
			}
		}
	}

	public static class PipeSignalHandler implements SignalHandler {
		@Override
		public void handle(Signal arg0) {
			mLogger.debug("PipeSignalHandler Signal : " + arg0 + ">> SKIP");
		}
	}

	private static void versionInfo() {
		System.out.print("\r\n" + "[Glovis Auction Server Informations]" + "\r\n");
		System.out.print("Release Version : " + AuctionServerSetting.RELEASE_VERSION_NAME + "\r\n");
		System.out.print("Release Date : " + AuctionServerSetting.RELEASE_VERSION_DATE + "\r\n");
		System.out.print("Copyright NH Bank. All Right Reserved." + "\r\n");
		System.out.print("All rights reserved." + "\r\n");
	}

	private static void runningError() {
		System.out.print("\r\n" + "Invalid Arguments! Please Check Arguments" + "\r\n");
	}

	private static void runServer(String[] args) {
		mLogger.info(
				"************************************ Hello Auction World! **************************************");
		int port = AuctionShareSetting.SERVER_PORT;

		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			mLogger.info("Server Port Number Invalid!");
		}

		AuctionServer.Builder server = new AuctionServer.Builder(port);

		mLogger.info("Auction Server Start!!");
		mServer = server.buildAndRun();
	}
}
