package com.nh.controller.interfaces;

import com.nh.controller.utils.AudioFilePlay;

import javafx.scene.media.MediaPlayer;

/**
 * 
 * @ClassName AudioPlayListener.java
 * @Description TTS 송출 관련 리스너 클래스
 * @author 박종식
 * @since 2019.11.04
 */
public interface AudioPlayListener {
    public void onPlayReady(AudioFilePlay audioFilePlay, MediaPlayer mediaPlayer);
    public void onPlayCompleted();
}
