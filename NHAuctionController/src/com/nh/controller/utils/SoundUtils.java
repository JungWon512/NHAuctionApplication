package com.nh.controller.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.controller.model.SettingSounds;
import com.nh.controller.setting.SettingApplication;
import com.nh.share.utils.SentryUtil;

import javazoom.jl.player.advanced.PlaybackListener;
import voiceware.libttsapi;

public class SoundUtils {
	final static private String TTS_SERVER_HOST = "127.0.0.1";
	final static private int TTS_SERVER_MAIN_PORT = 7777;
	final static private int TTS_SERVER_API_PORT = 7000;
	final static private int TTS_DEFAULT_VOICE_SPEED = 100;
	final static private int TTS_SERVER_VOICE_TYPE = 14; // 14 : 헤련 / 15 : 현아 / 20 : 유라
	final static private int TTS_SERVER_PITCH_VALUE = 100;
	private static SoundUtils gInstance = null;
	private String mCurrentEntryMessage = "";
	private TTSNowRunnable mTTSNowRunnable;
	private TTSDefineRunnable mTTSDefineRunnable;
	private String mDefinePrevKey = "";
	private static TtsPlayer mCurrentPlayer;

	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static SoundUtils getInstance() {
		if (gInstance == null) {
			gInstance = new SoundUtils();
		}
		return gInstance;
	}

	public SoundUtils() {
		try {
			mTTSNowRunnable = new TTSNowRunnable();
			mTTSDefineRunnable = new TTSDefineRunnable();
		} catch (Exception ex) {
			mLogger.error("Init Error " + ex.getMessage());
			ex.printStackTrace();
			SentryUtil.getInstance().sendExceptionLog(ex);
		}
	}

	/**
	 * 고정된 안내 메시지 초기화 함수
	 */
	public void initSoundSetting() {
		mTTSDefineRunnable.initSoundSetting();
	}

	/**
	 * 고정된 안내 메시지 갱신 처리 함수
	 *
	 * @author hmju
	 */
	public void soundSettingDataChanged() {
		mTTSDefineRunnable.notifyDataChanged();
	}

	/**
	 * 고정 문구 메시지 재생 처리 함수
	 *
	 * @param key SharedPreference Key 값
	 */
	public void playDefineSound(String key) {
		if (CommonUtils.getInstance().isValidString(key)) {
			stopSound();
			mDefinePrevKey = key;
			mTTSDefineRunnable.play(key);
		} else {
			mLogger.debug("playDefineSound IS NULL !!");
		}
	}

	/**
	 * 음성 정지
	 */
	public void stopSound() {
		mTTSNowRunnable.stop();
		mTTSDefineRunnable.stop();
	}

	/**
	 * 음성 송출 쓰레드 종료
	 */
	public void stopSoundThread() {
		mTTSNowRunnable.closeThread();
		mTTSDefineRunnable.closeThread();
	}

	public String getDefinePrevKey() {
		return mDefinePrevKey;
	}

	/**
	 * 출품 정보 메시지 저장 함수
	 *
	 * @param msg CurrentEntryInfoMessage
	 */
	public void setCurrentEntryInfoMessage(String msg) {
		mCurrentEntryMessage = msg;
	}

	/**
	 * 출품 정보 메시지 재생 처리 함수
	 */
	public void playCurrentEntryMessage() {
		if (CommonUtils.getInstance().isValidString(mCurrentEntryMessage)) {
			stopSound();
			mTTSNowRunnable.play(mCurrentEntryMessage);
		}
	}

	/**
	 * 출품 정보 메시지 재생 처리 함수
	 */
	public void playCurrentEntryMessage(PlaybackListener playbackListener) {
		if (CommonUtils.getInstance().isValidString(mCurrentEntryMessage)) {
			stopSound();
			mTTSNowRunnable.play(mCurrentEntryMessage, playbackListener);
		} else {
			if (playbackListener != null) {
				playbackListener.playbackFinished(null);
			}
		}
	}

	/**
	 * 일반 메시지 재생 처리 함수
	 *
	 * @param msg Message
	 */
	public void playSound(String msg, PlaybackListener listener) {
		if (CommonUtils.getInstance().isValidString(msg)) {
			stopSound();
			mTTSNowRunnable.play(msg, listener);
		} else {
			if (listener != null) {
				listener.playbackFinished(null);
			}
		}
	}

	/**
	 * TTS 고정된 문구들 재생 관련 클래스
	 */
	private class TTSDefineRunnable implements Runnable {

		private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
		private final ExecutorService mThreadService = Executors.newCachedThreadPool();
		private final HashMap<String, SettingSounds> mSoundSettingMap = new HashMap<>();
		private LineListener mTtsPlayerListener = null;
		private String mMessage = null;

		TTSDefineRunnable() {
			mSoundSettingMap.clear();
			initSoundSetting();
		}

		/**
		 * 음성설정 정의 메시지 초기화 처리
		 */
		public void initSoundSetting() {

			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_INTRO, new SettingSounds(
					SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_INTRO, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_BUYER, new SettingSounds(
					SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_BUYER, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_GUIDE, new SettingSounds(
					SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_GUIDE, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_PRACTICE, new SettingSounds(
					SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_PRACTICE, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_GENDER, new SettingSounds(
					SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_GENDER, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_USE, new SettingSounds(
					SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_USE, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_1, new SettingSounds(
					SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_1, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_2, new SettingSounds(
					SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_2, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_3, new SettingSounds(
					SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_3, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_4, new SettingSounds(
					SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_4, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_5, new SettingSounds(
					SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_5, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_6, new SettingSounds(
					SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_6, "")));
		}

		/**
		 * 데이터 갱신 처리 함수
		 */
		public void notifyDataChanged() {
			mSoundSettingMap.forEach((key, data) -> {
				data.setMessage(SharedPreference.getInstance().getString(key, ""));
			});
		}

		/**
		 * 저장된 Player 인덱스에 맞게 재생 처리 함수
		 *
		 * @param msg Target Message
		 * @author jspark
		 */
		public void play(final String msg) {
			release();
			
			try {
				if (mSoundSettingMap.containsKey(msg) && !mSoundSettingMap.get(msg).getMessage().isEmpty()) {
					mMessage = mSoundSettingMap.get(msg).getMessage();
					mThreadService.submit(this);
					
					mLogger.debug("TTS Play Message " + mMessage + " Thread " + Thread.currentThread());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				SentryUtil.getInstance().sendExceptionLog(ex);
			}
		}

		/**
		 * 재생중인 Player Close 처리 함수
		 *
		 * @author jspark
		 */
		public void stop() {
			release();
		}

		@Override
		public void run() {
			try {
				synchronized (this) {
					mThreadService.submit(() -> {
						mTtsPlayerListener = new LineListener() {

							@Override
							public void update(LineEvent event) {
								if (event.getType() == LineEvent.Type.OPEN) {
									mLogger.debug("LineEvent.Type.OPEN");
								} else if (event.getType() == LineEvent.Type.START) {
									mLogger.debug("LineEvent.Type.START");
								} else if (event.getType() == LineEvent.Type.STOP) {
									mLogger.debug("LineEvent.Type.STOP");
									release();
								} else if (event.getType() == LineEvent.Type.CLOSE) {
									mLogger.debug("LineEvent.Type.CLOSE");
								}
							}
						};
						
						mCurrentPlayer = new TtsPlayer(mTtsPlayerListener);
						AudioInputStream audioInputStream = getTtsStreamData(mMessage);
						
						if (audioInputStream != null) {
							mCurrentPlayer.play(audioInputStream);
						}
					});
				}
			} catch (Exception ex) {
				mLogger.error("Exception Tts  " + ex);
				SentryUtil.getInstance().sendExceptionLog(ex);
			}
		}

		private synchronized void release() {
			try {
				if (mTtsPlayerListener != null) {
					mTtsPlayerListener = null;
				}

				if (mCurrentPlayer != null) {
					mCurrentPlayer.stop();
					mCurrentPlayer = null;
				}
			} catch (Exception ex) {
				mLogger.error("Stop Error " + ex.getMessage());
			}
		}

		/**
		 * Close Thread
		 */
		public void closeThread() {
			try {
				mThreadService.shutdownNow();
			} catch (Exception ex) {
				ex.printStackTrace();
				SentryUtil.getInstance().sendExceptionLog(ex);
			}
		}
	}

	private class TTSNowRunnable implements Runnable {

		private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
		private final ExecutorService mThreadService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		private String mMessage = null;
		private PlaybackListener mPlayBackListener = null;
		private LineListener mTtsPlayerListener = null;
		
		TTSNowRunnable() {

		}
		
		public void stop() {
			release();
		}

		public void play(String text) {
			try {
				release();
				mLogger.debug("TTS Play Message " + text + " Thread " + Thread.currentThread());
				mMessage = text;
				mThreadService.submit(this);
			} catch (Exception ex) {
				ex.printStackTrace();
				SentryUtil.getInstance().sendExceptionLog(ex);
			}
		}

		public void play(String text, final PlaybackListener listener) {
			try {
				release();
				mLogger.debug("TTS Play Message " + text + " Thread " + Thread.currentThread());
				mMessage = text;
				mPlayBackListener = listener;
				mThreadService.submit(this);
			} catch (Exception ex) {
				mLogger.debug("TTS Play Message Exception : " + ex);
				SentryUtil.getInstance().sendExceptionLog(ex);
			}
		}

		@Override
		public void run() {
			try {
				synchronized (this) {
					mThreadService.submit(() -> {
						mTtsPlayerListener = new LineListener() {

							@Override
							public void update(LineEvent event) {
								if (event.getType() == LineEvent.Type.OPEN) {
									mLogger.debug("LineEvent.Type.OPEN");
								} else if (event.getType() == LineEvent.Type.START) {
									mLogger.debug("LineEvent.Type.START");
									mPlayBackListener.playbackStarted(null);
								} else if (event.getType() == LineEvent.Type.STOP) {
									mLogger.debug("LineEvent.Type.STOP");
									mPlayBackListener.playbackFinished(null);
								} else if (event.getType() == LineEvent.Type.CLOSE) {
									mLogger.debug("LineEvent.Type.CLOSE");
								}
							}
						};
						
						mCurrentPlayer = new TtsPlayer(mTtsPlayerListener);
						AudioInputStream audioInputStream = getTtsStreamData(mMessage);
						
						if (audioInputStream != null) {
							mCurrentPlayer.play(audioInputStream);
						} else {
							if (mPlayBackListener != null) {
								mPlayBackListener.playbackFinished(null);
							}
						}
					});
				}
			} catch (Exception ex) {
				mLogger.error("Exception Tts  " + ex);
				SentryUtil.getInstance().sendExceptionLog(ex);
			}
		}

		/**
		 * 메모리 해제 처리 함수
		 */
		private synchronized void release() {
			if (mPlayBackListener != null) {
				mPlayBackListener = null;
			}
			
			if (mTtsPlayerListener != null) {
				mTtsPlayerListener = null;
			}

			if (mCurrentPlayer != null) {
				mCurrentPlayer.stop();
				mCurrentPlayer = null;
			}
		}

		/**
		 * Close Thread
		 */
		public void closeThread() {
			try {
				mThreadService.shutdownNow();
			} catch (Exception ex) {
				ex.printStackTrace();
				SentryUtil.getInstance().sendExceptionLog(ex);
			}
		}
	}

	public synchronized AudioInputStream getTtsStreamData(String msg) {
		int result = -1;
		libttsapi ttsapi = new libttsapi();
		AudioInputStream inputStreamResult = null;

		try {
			if (getTtsServerStatus(ttsapi) == ttsapi.TTS_SERVICE_ON) {
				result = ttsapi.ttsRequestBufferEx(TTS_SERVER_HOST, TTS_SERVER_API_PORT, msg, TTS_SERVER_VOICE_TYPE, libttsapi.FORMAT_WAV,
						libttsapi.TEXT_NORMAL, 100, getSoundSpeed(), TTS_SERVER_PITCH_VALUE, 0, libttsapi.TRUE, libttsapi.TRUE);
				
				mLogger.debug("TTS API Result :  " + result);
			}
		} catch (IOException e) {
			result = -9;
			mLogger.debug("TTS API Result :  " + result);
		}

		if (result == libttsapi.TTS_RESULT_SUCCESS) {
			mLogger.debug("RequestBuffer Success (length=" + ttsapi.nVoiceLength + ")!!!");

			try {
				ByteArrayInputStream stream = new ByteArrayInputStream(ttsapi.szVoiceData);
				inputStreamResult = AudioSystem.getAudioInputStream(stream);
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return inputStreamResult;
	}

	public InputStream convertInputStream(byte[] data) {
		return new ByteArrayInputStream(data);
	}

	/**
	 * TTS 서버 실행 상태 확인(1:실행중 / 2:일시정지 / 0:미실행
	 * 
	 * @param ttsapi
	 * @return
	 */
	public static int getTtsServerStatus(libttsapi ttsapi) {
		int result = -1;

		try {
			result = ttsapi.ttsRequestStatus(TTS_SERVER_HOST, TTS_SERVER_MAIN_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public static int getSoundSpeed() {
		int result = 0;
		String tmpValue = null;
		SharedPreference sharedPreference = SharedPreference.getInstance();
		
		tmpValue = sharedPreference.getString(SharedPreference.PREFERENCE_SETTING_SOUND_RATE, SettingApplication.getInstance().DEFAULT_SETTING_SOUND_RATE);
		
		result = (Integer.valueOf(tmpValue) * 10) + TTS_DEFAULT_VOICE_SPEED;
		
		return result;
	}
}
