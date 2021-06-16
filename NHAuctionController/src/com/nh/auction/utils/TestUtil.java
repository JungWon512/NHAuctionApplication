package com.nh.auction.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.nh.auction.interfaces.NettyControllable;
import com.nh.auction.interfaces.StringListener;
import com.nh.auction.models.AuctionCountDown;
import com.nh.auction.models.TestDataInfo;

import io.netty.channel.Channel;
import javafx.application.Platform;

public class TestUtil {

	private static TestUtil testData = null;

	private List<TestDataInfo> mDataList = getDataList();	// test data
	
	public TestUtil() {
	}

	public static TestUtil getInstance() {
		if (testData == null) {
			testData = new TestUtil();
		}
		return testData;
	}

	/**
	 * 테스트 데이터 전송
	 * 
	 * @return
	 */
	public void sendData(StringListener listener) {
		
		Timer timer = new Timer(true);
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					listener.callBack("출품 데이터 전송 완료");
				});
			}
		};
		timer.schedule(timerTask, 3000);
	}
	
	
	/**
	 * 테스트 상품 데이터 한건 가져옴
	 * 
	 * @return
	 */
	public TestDataInfo obtainProduct(int currentIndex) {

		TestDataInfo testDataInfo = null;

		if (!CommonUtils.getInstance().isListEmpty(mDataList)) {
			if (mDataList.size() > currentIndex) {
				testDataInfo = mDataList.get(currentIndex);
				currentIndex++;
			}
		}

		return testDataInfo;
	}
	
	/**
	 * 테스트 카운트 데이터 한건 가져옴
	 * 
	 * @return
	 */
	public AuctionCountDown obtainCountDownData() {
		AuctionCountDown auctionCountDown = new AuctionCountDown();
		auctionCountDown.setCountDownTime("3");
		return auctionCountDown;
	}
	

	/**
	 * 테스트 데이터 생성
	 * @return
	 */
	public List<TestDataInfo> getDataList() {

		List<TestDataInfo> dataList = new ArrayList<TestDataInfo>();

		for (int i = 1; i < 101; i++) {
			
			TestDataInfo testDataInfo = new TestDataInfo();

			testDataInfo.setProductCode("CW000"+i);
			testDataInfo.setName("NAME_000"+i);
			
			dataList.add(testDataInfo);
		}

		return dataList;
	}

	
	public List<String> getMessageList() {

		List<String> dataList = new ArrayList<String>();

		for (int i = 1; i < 50; i++) {
			dataList.add("Test Message_" + i);
		}

		return dataList;
	}
	
}
