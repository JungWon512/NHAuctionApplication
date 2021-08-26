package com.nh.controller.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.nh.share.api.ActionRuler;

import javazoom.jl.player.Player;

/**
 * 음성 실행 클래스
 *
 * @author jhlee
 */
public class SoundUtil {

    private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private List<String> mSoundDataList = new ArrayList<String>();

    private int mCurrentIndex = -1;
    private final VoiceSelectionParams mVoice;
    private final AudioConfig mAudioConfig;
    private TextToSpeechClient mClient;

    public SoundUtil() {
        mVoice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("ko-KR")
                .setSsmlGender(SsmlVoiceGender.FEMALE)
                .build();

        mAudioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .build();
        try {
            /**
             * Google Cloud Service 사용하려면 Environment variables에 환경변수 처리 해야함
             * ${KEY}${VALUE}
             * GOOGLE_APPLICATION_CREDENTIALS=C:\workStudio\AuctionApplications\NHController\google_tts_service.json
             */
            mClient = TextToSpeechClient.create();
        } catch (Exception ex) {
            mLogger.error("TextToSpeechClient Error " + ex.getMessage());
        }
    }

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

        if (index < 0) {
            mLogger.debug("SOUND INDEX IS NULL !!");
            return;
        }

        if (CommonUtils.getInstance().isListEmpty(mSoundDataList)) {
            mLogger.debug("SOUND IS NULL !!");
            return;
        }

        String strSound = mSoundDataList.get(index);

        if (strSound != null && !strSound.isEmpty() && !strSound.isBlank()) {
            mCurrentIndex = index;
            ActionRuler.getInstance().addRunnable(new TextToSpeechRunnable(strSound));
            ActionRuler.getInstance().runNext();
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
     */
    public void stopSound() {
        mLogger.debug("stopSound");
    }


    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    /**
     * Google Cloud TTS Runnable Class
     */
    class TextToSpeechRunnable implements Runnable {

        private final String msg;

        TextToSpeechRunnable(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                SynthesisInput input = SynthesisInput.newBuilder().setText(msg).build();
                SynthesizeSpeechResponse response = mClient.synthesizeSpeech(input, mVoice, mAudioConfig);
                InputStream stream = new ByteArrayInputStream(response.getAudioContent().toByteArray());
                Player mp3Play = new Player(stream);
                mp3Play.play();
            } catch (Exception ex) {
                mLogger.error("textToSpeech Error " + ex.getMessage());
            }
        }
    }
}
