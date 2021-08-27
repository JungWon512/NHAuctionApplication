package com.nh.controller.utils;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.nh.controller.setting.SettingApplication;
import javazoom.jl.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private String mDefinePrevMsg = "";
    private TTSNowRunnable mTTSNowRunnable;
    private TTSDefineRunnable mTTSDefineRunnable;

    public SoundUtil() {
        try {
            VoiceSelectionParams params = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("ko-KR")
                    .setSsmlGender(SsmlVoiceGender.FEMALE)
                    .build();

            AudioConfig config = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            TextToSpeechClient client = TextToSpeechClient.create(
                    TextToSpeechSettings.newBuilder()
                            .setCredentialsProvider(FixedCredentialsProvider
                                    .create(GoogleCredentials
                                            .fromStream(new FileInputStream("google_tts_service.json"))
                                    ))
                            .build());

            mTTSNowRunnable = new TTSNowRunnable(params, config, client);
            mTTSDefineRunnable = new TTSDefineRunnable(params, config, client);

            // 고정된 멘트들 Player 처리
            setDefineMessageList(SettingApplication.getInstance().getParsingSoundDataList());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void handleDiffChangeMessage(final String oldMsg, final String newMsg) {
        mTTSDefineRunnable.handleDiffChangeMessage(oldMsg, newMsg);
    }

    /**
     * 고정 안내 메시지 Set 처리 함수
     *
     * @param dataList 고정 안내 메시지 리스트
     */
    public void setDefineMessageList(List<String> dataList) {
        mTTSDefineRunnable.setDefinePlayerList(dataList);
    }

    /**
     * 고정 문구 메시지 재생 처리 함수
     *
     * @param msg 문구
     */
    public void playDefineSound(String msg) {
        if (CommonUtils.getInstance().isValidString(msg)) {
            stopSound();
            mDefinePrevMsg = msg;
            mTTSDefineRunnable.play(msg);
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
    public void playSound(String msg) {
        if (CommonUtils.getInstance().isValidString(msg)) {
            stopSound();
            mTTSNowRunnable.play(msg);
        }
    }

    /**
     * 음성 정지
     */
    public void stopSound() {
        mLogger.debug("stopSound");
        mTTSNowRunnable.stop();
        mTTSDefineRunnable.stop();
    }

    public String getDefinePrevMsg() {
        return mDefinePrevMsg;
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
        private final ArrayList<String> mTempMessageList = new ArrayList<>();
        private final HashMap<String, InputStream> mPlayerMap = new HashMap<>();
        private Player mCurrentPlayer = null;

        TTSDefineRunnable(VoiceSelectionParams params, AudioConfig config, TextToSpeechClient client) {
            mParams = params;
            mAudioConfig = config;
            mClient = client;
            mThreadService.execute(this);
        }

        public void handleDiffChangeMessage(final String oldMsg, final String newMsg) {
            try {
                mPlayerMap.remove(oldMsg);
                mThreadService.submit(() -> {
                    try {
                        mPlayerMap.put(newMsg, getTextToSpeechStream(newMsg));
                    } catch (Exception ex) {
                        mLogger.error(ex.getMessage());
                    }
                });
            } catch (Exception ex) {
                mLogger.error(ex.getMessage());
            }
        }

        public void setDefinePlayerList(List<String> msgList) {
            boolean isDataChanged = false;
            for (String value : msgList) {
                // 한개라도 다른게 있는 경우
                if (!mPlayerMap.containsKey(value)) {
                    isDataChanged = true;
                    break;
                }
            }

            // 데이터 초기화 처리
            if (isDataChanged) {
                mPlayerMap.clear();
                mTempMessageList.addAll(msgList);
                mThreadService.submit(this);
            }
        }

        /**
         * 저장된 Player 인덱스에 맞게 재생 처리 함수
         *
         * @param msg Target Message
         * @author hmju
         */
        public void play(String msg) {
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    long prevTime = System.currentTimeMillis();
                    mPlayerMap.get(msg).reset();
                    mCurrentPlayer = new Player(mPlayerMap.get(msg));
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
            mTempMessageList.forEach(msg -> {
                try {
                    mLogger.debug("Define Thread " + Thread.currentThread());
                    mPlayerMap.put(msg, getTextToSpeechStream(msg));
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
        private Player mPlayer = null;

        TTSNowRunnable(VoiceSelectionParams params, AudioConfig config, TextToSpeechClient client) {
            mParams = params;
            mAudioConfig = config;
            mClient = client;
            mThreadService.execute(this);
        }

        public void stop() {
            if (mPlayer != null) {
                mLogger.debug("Player Complete " + mPlayer.isComplete() + " HashCode " + mPlayer.hashCode());
                mPlayer.close();
                mPlayer = null;
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

        @Override
        public void run() {
            try {
                synchronized (this) {
                    if (mPlayer != null) {
                        mPlayer.close();
                        mPlayer = null;
                    }
                }
                long prevTime = System.currentTimeMillis();
                mPlayer = new Player(getTextToSpeechStream(mMessage));
                mLogger.debug("Diff Time " + (System.currentTimeMillis() - prevTime));
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
}
