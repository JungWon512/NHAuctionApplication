package com.nh.controller.utils;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

public class TtsPlayer implements LineListener {
	LineListener mTtsPlayerListener = null;
	Clip mClip;

	public TtsPlayer(LineListener ttsListener) {
		mTtsPlayerListener = ttsListener;
	}

	@Override
	public void update(LineEvent event) {
		mTtsPlayerListener.update(event);
	}
	
	public void play(AudioInputStream audioInputStream) {
		if (audioInputStream != null) {
			AudioFormat format = audioInputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);

	        try {
	        	mClip = (Clip) AudioSystem.getLine(info);
	        	mClip.open(audioInputStream);
	        	mClip.start();
	        	mClip.addLineListener(mTtsPlayerListener);
			} catch (LineUnavailableException | IOException e) {
				e.printStackTrace();
			}
		} else {
			stop();
		}
	}
	
	public void stop() {
		if (mClip != null) {
			mClip.flush();
			mClip.stop();
			mClip.close();
			mClip = null;
		}
	}
}
