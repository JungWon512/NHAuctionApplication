package com.nh.controller.utils;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 음성 실행 클래스
 * @author jhlee
 *
 */
public class SoundUtil {
	
	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private List<String> mSoundDataList = new ArrayList<String>();

	private int mCurrentIndex = -1;

	public void setSoundDataList(List<String> dataList) {
		mSoundDataList.clear();
		mSoundDataList.addAll(dataList);
	}
	
	/**
	 * 음성 시작
	 * 
	 * @param index
	 */
	public void playSound(int index) {
			
		if(index < 0) {
			mLogger.debug("SOUND INDEX IS NULL !!");
			return;
		}
		
		if(CommonUtils.getInstance().isListEmpty(mSoundDataList)) {
			mLogger.debug("SOUND IS NULL !!");
			return;
		}

		String strSound = mSoundDataList.get(index);

		if (strSound != null && !strSound.isEmpty() && !strSound.isBlank()) {
			mCurrentIndex = index;
			mLogger.debug("PLAY SOUND : " + strSound);
		} else {
			mLogger.debug("SOUND IS NULL !!");
		}
	}

	/**
	 * 음성 정지
	 * 
	 * @param index
	 */
	public void stopSound(int index) {
		mLogger.debug("stopSound index : " + index);
	}
	
	
	/**
	 * 음성 정지
	 * 
	 * @param index
	 */
	public void stopSound() {
		mLogger.debug("stopSound");
	}
	
	
	public int getCurrentIndex() {
		return mCurrentIndex;
	}
}
