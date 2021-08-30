package com.nh.controller.utils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.nh.controller.model.SettingSound;

import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackListener;

/**
 * 음성 실행 클래스
 *
 * @author jhlee
 */
public class SoundUtil {

    private static SoundUtil gInstance = null;

    public static SoundUtil getInstance() {
        if (gInstance == null) {
            gInstance = new SoundUtil();
        }
        return gInstance;
    }

    private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private String mCurrentEntryMessage = "";
    private String mDefinePrevKey = "";
    private TTSNowRunnable mTTSNowRunnable;
    private TTSDefineRunnable mTTSDefineRunnable;
    public LocalSoundDefineRunnable mLocalSoundDefineRunnable;

    public SoundUtil() {
        try {
            VoiceSelectionParams params = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("ko-KR")
                    .setSsmlGender(SsmlVoiceGender.FEMALE)
                    .build();

            AudioConfig config = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            // GOOGLE_APPLICATION_CREDENTIALS=C:\workStudio\AuctionApplications\NHController\google_tts_service.json
            TextToSpeechClient client = TextToSpeechClient.create(
                    TextToSpeechSettings.newBuilder()
                            .setCredentialsProvider(FixedCredentialsProvider
                                    .create(GoogleCredentials
                                            .fromStream(new FileInputStream("google_tts_service.json"))
                                    ))
                            .build());

           
            mTTSNowRunnable = new TTSNowRunnable(params, config, client);
            mTTSDefineRunnable = new TTSDefineRunnable(params, config, client);
            mLocalSoundDefineRunnable = new LocalSoundDefineRunnable();
   
        } catch (Exception ex) {
            mLogger.error("Init Error " + ex.getMessage());
            ex.printStackTrace();
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
     * 일반 메시지 재생 처리 함수
     *
     * @param msg Message
     */
    public void playSound(String msg,PlaybackListener listener) {
        if (CommonUtils.getInstance().isValidString(msg)) {
            stopSound();
            mTTSNowRunnable.play(msg,listener);
        }
    }
    
    
    /**
     * 경매 시작,카운트다운,정지 재생 처리 함수
     * @param type
     * @param lineListener
     */
    public void playLocalSound(LocalSoundDefineRunnable.LocalSoundType type , LineListener lineListener) {
    	stopSound();
    	mLocalSoundDefineRunnable.play(type, lineListener);
    }

    /**
     * 음성 정지
     */
    public void stopSound() {
        mTTSNowRunnable.stop();
        mTTSDefineRunnable.stop();
    }

    public String getDefinePrevKey() {
        return mDefinePrevKey;
    }

    /**
     * Google TTS 고정된 문구들 재생 관련 클래스
     */
    private static class TTSDefineRunnable implements Runnable {

        private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
        private final ExecutorService mThreadService = Executors.newCachedThreadPool();
        private final VoiceSelectionParams mParams;
        private final AudioConfig mAudioConfig;
        private final TextToSpeechClient mClient;
        private final HashMap<String, SettingSound> mSoundSettingMap = new HashMap<>();
        private Player mCurrentPlayer = null;

        TTSDefineRunnable(VoiceSelectionParams params, AudioConfig config, TextToSpeechClient client) {
            mParams = params;
            mAudioConfig = config;
            mClient = client;
            mThreadService.execute(this);
        }

        /**
         * 사운드 초기화 처리 함수
         */
        public void initSoundSetting() {

            mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_INTRO,
                    new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_INTRO, ""))
            );
            mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_BUYER,
                    new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_BUYER, ""))
            );
            mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_GUIDE,
                    new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_GUIDE, ""))
            );
            mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_PRACTICE,
                    new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_PRACTICE, ""))
            );
            mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_GENDER,
                    new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_GENDER, ""))
            );
            mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_USE,
                    new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_USE, ""))
            );
            mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_1,
                    new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_1, ""))
            );
            mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_2,
                    new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_2, ""))
            );
            mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_3,
                    new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_3, ""))
            );
            mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_4,
                    new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_4, ""))
            );
            mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_5,
                    new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_5, ""))
            );
            mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_6,
                    new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_6, ""))
            );

            // Api 호출해서 가져오기
            mThreadService.submit(this);
        }

        /**
         * 데이터 갱신 처리 함수
         */
        public void notifyDataChanged() {
            mSoundSettingMap.forEach((key, data) -> {
                data.setMessage(SharedPreference.getInstance().getString(key, ""));
            });
            mThreadService.submit(this);
        }

        /**
         * 저장된 Player 인덱스에 맞게 재생 처리 함수
         *
         * @param msg Target Message
         * @author hmju
         */
        public void play(final String msg) {
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    long prevTime = System.currentTimeMillis();
                    mCurrentPlayer = new Player(mSoundSettingMap.get(msg).getStream());
                    mLogger.debug("Diff Time " + (System.currentTimeMillis() - prevTime));
                    mCurrentPlayer.play();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mLogger.error("Play Error " + ex.getMessage());
                }
            });
        }

        /**
         * 재생중인 Player Close 처리 함수
         *
         * @author hmju
         */
        public void stop() {
            try {
                if (mCurrentPlayer != null) {
                    mCurrentPlayer.close();
                    mCurrentPlayer = null;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                mLogger.error("Stop Error " + ex.getMessage());
            }
        }

        @Override
        public void run() {
            mSoundSettingMap.forEach((key, data) -> {
                try {
                    // 메시지가 변경이 된 경우에만 TTS 가져오기
                    if (data.isChanged()) {
                        data.setStream(getTextToSpeechStream(data.getMessage()));
                    }
                } catch (Exception ex) {
                    mLogger.error(ex.getMessage());
                }
            });
        }

        /**
         * Network 통신을 통해서 Google TTS Stream 가져오기 함수
         *
         * @param msg 원하는 메시지
         * @return TTS ByteArray
         * @author hmju
         */
        private InputStream getTextToSpeechStream(String msg) {
            SynthesisInput input = SynthesisInput.newBuilder().setText(msg).build();
            SynthesizeSpeechResponse response = mClient.synthesizeSpeech(input, mParams, mAudioConfig);
            return new ByteArrayInputStream(response.getAudioContent().toByteArray());
        }
    }

    /**
     * Google TTS 바로 재생 하는 클래스
     */
    private static class TTSNowRunnable implements Runnable {

        private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
        private final ExecutorService mThreadService = Executors.newCachedThreadPool();
        private final VoiceSelectionParams mParams;
        private final AudioConfig mAudioConfig;
        private final TextToSpeechClient mClient;
        private String mMessage = null;
        private AdvancedPlayer mPlayer = null;
        private PlaybackListener mPlayBackListener = null;
  
        TTSNowRunnable(VoiceSelectionParams params, AudioConfig config, TextToSpeechClient client) {
            mParams = params;
            mAudioConfig = config;
            mClient = client;
            mThreadService.execute(this);
        }

        public void stop() {
            if (mPlayer != null) {
                mLogger.debug("Player Complete " +  " HashCode " + mPlayer.hashCode());
            	mPlayer.setPlayBackListener(null);
                mPlayer.close();
                mPlayer = null;
                mPlayBackListener = null;
            }
        }

        public void play(String text) {
            try {
                mLogger.debug("TTS Play Message " + text + " Thread " + Thread.currentThread());
                mMessage = text;
                mThreadService.submit(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void play(String text,PlaybackListener listener) {
            try {
                mLogger.debug("TTS Play Message " + text + " Thread " + Thread.currentThread());
                mMessage = text;
                mPlayBackListener = listener;
                mThreadService.submit(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        @Override
        public void run() {
            try {
                synchronized (this) {
                    if (mPlayer != null) {
                        mPlayer.close();
                        mPlayer = null;
                        mLogger.debug(" ");
                        mPlayBackListener = null;
                    }
                }
                long prevTime = System.currentTimeMillis();
                mPlayer = new AdvancedPlayer(getTextToSpeechStream(mMessage));
                mLogger.debug("Diff Time " + (System.currentTimeMillis() - prevTime));
                mPlayer.setPlayBackListener(mPlayBackListener);
                mPlayer.play();
            } catch (Exception ex) {
                mLogger.error("Run " + ex);
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
            }
        }

        /**
         * Network 통신을 통해서 Google TTS Stream 가져오기 함수
         *
         * @param msg 원하는 메시지
         * @return TTS ByteArray
         * @author hmju
         */
        private InputStream getTextToSpeechStream(String msg) {
            SynthesisInput input = SynthesisInput.newBuilder().setText(msg).build();
            SynthesizeSpeechResponse response = mClient.synthesizeSpeech(input, mParams, mAudioConfig);
            return new ByteArrayInputStream(response.getAudioContent().toByteArray());
        }
    }
    
    
    /**
     * wav 파일 재생.
     * @author jhlee
     *
     */
    public class LocalSoundDefineRunnable {
    	
    	private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
        private Clip mClip;
 
        public enum LocalSoundType{
        	START,
        	END,
        	DING
        }

	    public void play(LocalSoundType type, LineListener listener) {
	    	   try {
	    		   
	    		    mClip = AudioSystem.getClip();
	                
	                AudioInputStream mEdasstart = AudioSystem.getAudioInputStream(this.getClass().getResource(GlobalDefine.FILE_INFO.LOCAL_SOUND_START));
	                AudioInputStream mEdasend = AudioSystem.getAudioInputStream(this.getClass().getResource(GlobalDefine.FILE_INFO.LOCAL_SOUND_END));
	                AudioInputStream mDing = AudioSystem.getAudioInputStream(this.getClass().getResource(GlobalDefine.FILE_INFO.LOCAL_SOUND_DING));
		            
	                if(type.equals(LocalSoundType.START)) {
	   		        	mClip.open(mEdasstart);
	   		        } else if(type.equals(LocalSoundType.END)) {
	   		        	mClip.open(mEdasend);
	   		        } else if(type.equals(LocalSoundType.DING)) {
	   		        	mClip.open(mDing);
	   		        }

	   		        if(listener != null) {
	   		        	mClip.addLineListener(listener);
	   		        }
	   		        
	   		        mClip.start(); 
	                
	            } catch (Exception ex) {
	                mLogger.error("Run " + ex);
	            }
	    	   
	    }
	    
	    /**
         * 재생중인 Audio Clip Close 처리 함수
         *
         * @author jhlee
         */
        public void stop() {
            try {
                if (mClip != null) {
                	mClip.close();
                	mClip = null;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                mLogger.error("Stop Error " + ex.getMessage());
            }
        }
        
    }
}
