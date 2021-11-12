package com.nh.auctionserver;

import java.nio.charset.Charset;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.nh.auctionserver.netty.AuctionServer;
import com.nh.auctionserver.setting.AuctionServerSetting;
import com.nh.auctionserver.socketio.SocketIOServer;
import com.nh.share.code.GlobalDefineCode;
import com.nh.share.setting.AuctionShareSetting;

import io.netty.handler.ssl.SslContext;
import lombok.RequiredArgsConstructor;

@SpringBootApplication
@Component
@RequiredArgsConstructor
public class AuctionServerApplication implements CommandLineRunner, ApplicationListener<ApplicationStartedEvent> {
	private static AuctionServer mServer;
	private static final Logger mLogger = LoggerFactory.getLogger(AuctionServerApplication.class);

	private String[] args = null;

	private final SocketIOServer mSocketIOServer;
	private final SslContext mSSLContext;

	// private final SocketIOHandler mSocketIOHandler;

	public static void main(String[] args) {
		SpringApplication.run(AuctionServerApplication.class, args);
//		if (args.length < 1) {
//			runningError();
//		} else if (args.length == 1) {
//			if (args[0].toString().contains("version")) {
//				versionInfo();
//			} else {
//				runningError();
//			}
//		} else {
//			// 'java -jar AuctionServer.jar start [port]'
//			if (args.length == 2) {
//				if (args[0].toString().contains("start")) {
//					runServer(args);
//				} else {
//					runningError();					
//				}
//			} else {
//				runningError();
//			}
//		}
//		FinishSignalHandler finishSignalHandler = new FinishSignalHandler();
//		Signal.handle(new Signal("INT"), finishSignalHandler);
//		// Signal.handle(new Signal("PIPE"), finishSignalHandler);
//		Signal.handle(new Signal("ALRM"), finishSignalHandler);
//		Signal.handle(new Signal("TERM"), finishSignalHandler);
//
//		// 21.01.20 SIGPIPE 시그널 발생 시 처리 로직 변경
//		PipeSignalHandler pipeSignalHandler = new PipeSignalHandler();
//		Signal.handle(new Signal("PIPE"), pipeSignalHandler); // SignalHandler.SIG_IGN;
	}
	
	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		runServer(new String[] { "start", AuctionServerSetting.DEFAULT_CONNECT_PORT });
		//mSocketIOServer.getSocketIOHandler().setSocketIOPort(9000);
		mSocketIOServer.start();

		mServer.setSocketIOHandler(mSocketIOServer.getSocketIOHandler(), mSSLContext);
	}

	@Override
	public void run(String... args) throws Exception {
		// CommmandLine에서 실행한 args 받아오기
		this.args = args;
	}

//
//	public static class FinishSignalHandler implements SignalHandler {
//		@Override
//		public void handle(Signal arg0) {
//			mLogger.debug("FinishSignalHandler Signal : " + arg0);
//			if (mServer != null) {
//				mServer.stopServer();
//			}
//		}
//	}
//
//	public static class PipeSignalHandler implements SignalHandler {
//		@Override
//		public void handle(Signal arg0) {
//			mLogger.debug("PipeSignalHandler Signal : " + arg0 + ">> SKIP");
//		}
//	}
//
//	private static void versionInfo() {
//		System.out.print("\r\n" + "[Glovis Auction Server Informations]" + "\r\n");
//		System.out.print("Release Version : " + AuctionServerSetting.RELEASE_VERSION_NAME + "\r\n");
//		System.out.print("Release Date : " + AuctionServerSetting.RELEASE_VERSION_DATE + "\r\n");
//		System.out.print("Copyright NH Bank. All Right Reserved." + "\r\n");
//		System.out.print("All rights reserved." + "\r\n");
//	}

	private static void runningError() {
		System.out.print("\r\n" + "Invalid Arguments! Please Check Arguments" + "\r\n");
	}

	private static void runServer(String[] args) {
		mLogger.info(
				"************************************ Hello Auction World! **************************************");
		int port = Integer.valueOf(AuctionServerSetting.DEFAULT_CONNECT_PORT);

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

	@PreDestroy
	private void autoStop() {
		mLogger.debug(">> called autoStop ");
		if (mServer != null) {
			mServer.stopServer();
		}

		mSocketIOServer.stop();

		mLogger.info(">> socket closed... ");
	}
}
