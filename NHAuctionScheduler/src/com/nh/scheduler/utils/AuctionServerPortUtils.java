package com.nh.scheduler.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nh.scheduler.model.AuctionSchedulerPortModel;

/**
 * 
 * @ClassName AuctionServerPortUtils.java
 * @Description 서버에서 사용하는 기능 모음.
 * @anthor ishift
 * @since 2021.11.10
 */
public class AuctionServerPortUtils {

	private static final Logger logger = LogManager.getLogger(AuctionServerPortUtils.class);

	/**
	 * 사용 가능한 Port인지 확인. TCP/UDP Port 사용 가능 여부 확인.
	 *
	 * @param Port
	 * @return boolean true : 사용 가능 Port, false : 사용 불가 Port
	 */
	public boolean isAvailablePort(int Port) {
		boolean checkTCP = checkTCPPortAvailability(Port);
		if (checkTCP) {
			boolean checkUDP = checkUDPPortAvailability(Port);
			if (checkUDP) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 사용 가능한 TCP Port인지 확인.
	 *
	 * @param Port
	 * @return boolean true : 사용 가능 Port, false : 사용 불가 Port
	 */
	public boolean checkTCPPortAvailability(int port) {
		try {
			ServerSocket socket = new ServerSocket(port);
			socket.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 사용 가능한 UDP Port인지 확인.
	 *
	 * @param Port
	 * @return boolean true : 사용 가능 Port, false : 사용 불가 Port
	 */
	public boolean checkUDPPortAvailability(int port) {
		try {
			DatagramSocket socket = new DatagramSocket(port);
			socket.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Auction 서버 실행.
	 *
	 * @param port
	 * @return
	 */
	public void runAuctionServer(AuctionSchedulerPortModel auctionPort) {
		String port = auctionPort.getAuctionPort();
		String auctionBizPlaceCode = auctionPort.getAuctionBizPlaceCode();
		int auctionQcn = auctionPort.getAuctionQcn();
		String auctionDate = auctionPort.getAuctionDate();

		// 'sudo ./auction_server_start -start [port]'
		// [port] = AuctionServer Port
		String serverCommend = "/home/auctAdmin/auction_server/auction_server_start -start " + port;
		logger.info("== Run Server ==");
		logger.info("[" + serverCommend + "]");
		logger.info("================");
		try {
			Runtime.getRuntime().exec(new String[] { "sh", "-c", serverCommend });
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("Runtime IOException : " + e);
		}
	}

	/**
	 * PID Kill
	 *
	 * @param pid
	 * @return
	 */
	public void killPid(String pid) {
		try {
			Runtime.getRuntime().exec(new String[] { "sh", "-c", "kill -9 " + pid });
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
