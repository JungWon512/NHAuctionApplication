package com.nh.controller.utils;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.controller.interfaces.AudioPlayListener;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * 경매 효과음 재생 클래스
 * 
 * @author jhlee
 *
 */
public class AudioFilePlay {

	private static AudioFilePlay audioFilePlay = null;

	public AudioFilePlay() {
	}

	public static AudioFilePlay getInstance() {
		if (audioFilePlay == null) {
			audioFilePlay = new AudioFilePlay();
		}
		return audioFilePlay;
	}

	public enum AudioPlayTypes {
		START("../resource/sounds/" + "edasstart.wav"), 
		DING("../resource/sounds/" + "ding.wav"),
		FINISH("../resource/sounds/" + "edasend.wav");

		private final String code;

		AudioPlayTypes(String code_) {
			this.code = code_;
		}

		public static String which(AudioPlayTypes type_) {
			return Arrays.stream(AudioPlayTypes.values()).filter(type -> (type.equals(type_))).map(type -> type.code).findAny().orElse(null);
		}

	}

	private static final Logger mLogger = LoggerFactory.getLogger(AudioFilePlay.class);

	private String mFilePath;
	private MediaPlayer mMediaPlayer;

	private int mPlayDuration = 0;

	private boolean mIsPossiblePlay = true;

	private ScheduledExecutorService mService = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> mTtsPlayJob;

	private AudioPlayListener mAudioPlayListener;

	public AudioFilePlay(String filePath, AudioPlayListener audioPlayListener) {
		mFilePath = filePath;
		mAudioPlayListener = audioPlayListener;

		try {
			// File f = new File(mFilePath);
			// Media hit = new Media(f.toURI().toString());
			Media hit = new Media(mFilePath);
			mMediaPlayer = new MediaPlayer(hit);

			mMediaPlayer.setOnEndOfMedia(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mMediaPlayer.stop();
					mMediaPlayer.seek(mMediaPlayer.getStartTime());
				}
			});

			mMediaPlayer.setOnReady(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					if (mIsPossiblePlay) {
						mIsPossiblePlay = false;
						mPlayDuration = (int) mMediaPlayer.getMedia().getDuration().toMillis();
						mAudioPlayListener.onPlayReady(AudioFilePlay.this, mMediaPlayer);
					}
				}
			});

			mMediaPlayer.setOnPlaying(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
				}
			});

			mMediaPlayer.setOnPaused(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
				}
			});

			mMediaPlayer.setOnStopped(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
				}
			});

			mMediaPlayer.setOnError(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setTargetPlay(String filePath, AudioPlayListener audioPlayListener) {
		stopSound();
		mFilePath = filePath;
		mAudioPlayListener = audioPlayListener;

		try {
			// File f = new File(mFilePath);
			// Media hit = new Media(f.toURI().toString());
			Media hit = new Media(mFilePath);
			mMediaPlayer = new MediaPlayer(hit);

			mMediaPlayer.setOnEndOfMedia(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mMediaPlayer.stop();
					mMediaPlayer.seek(mMediaPlayer.getStartTime());
				}
			});

			mMediaPlayer.setOnReady(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					if (mIsPossiblePlay) {
						mIsPossiblePlay = false;
						mPlayDuration = (int) mMediaPlayer.getMedia().getDuration().toMillis();
						mAudioPlayListener.onPlayReady(AudioFilePlay.this, mMediaPlayer);
					}
				}
			});

			mMediaPlayer.setOnPlaying(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
				}
			});

			mMediaPlayer.setOnPaused(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
				}
			});

			mMediaPlayer.setOnStopped(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
				}
			});

			mMediaPlayer.setOnError(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void stopSound() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}

		if (mTtsPlayJob != null) {
			mTtsPlayJob.cancel(true);
		}

		mIsPossiblePlay = true;
	}

	public void startPlayTimer(int playDuration) {
		if (mTtsPlayJob != null) {
			mTtsPlayJob.cancel(true);
		}

		mTtsPlayJob = mService.schedule(new TtsPlayTimerJob(), playDuration, TimeUnit.MILLISECONDS);
	}

	public void playSound() {
		if (mMediaPlayer != null && mMediaPlayer.getStatus() == MediaPlayer.Status.READY) {
			startPlayTimer(mPlayDuration);
			mMediaPlayer.play();
		}
	}

	public void playSound(String filePath, AudioPlayListener listener) {

		try {
			// File f = new File(mFilePath);
			// Media hit = new Media(f.toURI().toString());
			Media hit = new Media(filePath);
			mMediaPlayer = new MediaPlayer(hit);

			mMediaPlayer.setOnEndOfMedia(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mMediaPlayer.stop();
					mMediaPlayer.seek(mMediaPlayer.getStartTime());
				}
			});

			mMediaPlayer.setOnReady(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					if (mIsPossiblePlay) {
						mIsPossiblePlay = false;
						mPlayDuration = (int) mMediaPlayer.getMedia().getDuration().toMillis();
						mAudioPlayListener.onPlayReady(AudioFilePlay.this, mMediaPlayer);
					}
				}
			});

			mMediaPlayer.setOnPlaying(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
				}
			});

			mMediaPlayer.setOnPaused(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
				}
			});

			mMediaPlayer.setOnStopped(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
				}
			});

			mMediaPlayer.setOnError(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
				}
			});

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public synchronized String getPlayStatus() {
		return mMediaPlayer.getStatus().toString();
	}

	public synchronized int getPlayDuration() {
		return mPlayDuration;
	}

	private class TtsPlayTimerJob implements Runnable {
		@Override
		public void run() {
			mIsPossiblePlay = true;

			mAudioPlayListener.onPlayCompleted();

			if (mTtsPlayJob != null) {
				mTtsPlayJob.cancel(true);
			}
		}
	}
}
