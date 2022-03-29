package com.nh.controller.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.PemReader;
import com.google.api.client.util.SecurityUtils;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nh.controller.model.SettingSound;
import com.nh.controller.setting.SettingApplication;
import com.nh.share.utils.SentryUtil;

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

	private final String mGoogleServiceJson = """
			{
			  "type": "service_account",
			  "project_id": "smartauction-324007",
			  "private_key_id": "4963440ae917fd44bef4e822136709f479e841d3",
			  "private_key": "-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDbqnWpwmKM8Gjk\\ncIoXAgDLE37b3egJX/vGa66QNVtCIcNRkXoUn2Nn9kKZHdsmuHBmrMGdz82E3TMZ\\n7PjDSM+dMmdaHuk6Ezn/LqToLVGXeZ2XlfErsIXQRM4SA5o/f3HoMQ1/lnfufChS\\nTi+HoinihWWTjDXOawGn7WHNKce0bkOzXGQ5DcnH0qj/aK0vcDaXjo5Y0yd595wN\\nbVNByAGmUFGi8l0D8SnTgvhxjYI+3GScLVDJ44C/3NgSb1fyGIXiH9yAwmWEBG3Q\\nGJR+vLpnGXT86B/nZNs8UTjXvdTcemLp4sy1UUR0MlJWSGr7bbM0/41A82eh96pA\\nvTbMMWZfAgMBAAECggEAGitHBDfPpMm5PUm48b+/13GVodzgWUmRhZRrgeuRWSIL\\nkkqZ+B366jY4veQEKSs83MaE9Gd+rO7rORH1mtIwRaJtFJvtHgmPVbq5U52ehESt\\nRRNMXW+UqXig8h1ywVDOAaLiYEpNNGCfLxr4Z5imk8FflgHUoSg3VMmfZqEtFO37\\na0GIhRiZ5yYpU4uSApijvipDVXHIhtMDij8sD2y/9zDqBfY/DEdSAV3DKr1qgBr2\\ntVzkyaNp71p67SQgPkyEcxYc8fJrHql2DzoJ3FUvvykEA4BokX6e5ejQux1sItUK\\nVuaQC1sj32lF8ml7vCLaFa5c/Mf5KC8QBVNSmdMHPQKBgQDtTYu25akyhE9h98RJ\\naVrwiNhHCeqAJ2ElTrlH5g8gw/6fdjORRLNxIAm/KqIhhhg2C9uHpeRh4vsDz/vE\\nzQo9XmYU5Bd67q/fVKYI2gIgAxQ0ZoBIo46P6qzQ7bFqxoUfcanDG4bLefP9NXvK\\nfjvPJnL0vBgq8BkuC+pzULX+8wKBgQDs+Suf6StGmDiVv9qjJj+PchQvrU3wkdYc\\nmXbmNId4E3UVrVGGubQ+jlTLSUs+s9UwZ9aPTL8ll5oPr4W1/EpgmDuwTAtDu+My\\nolT7vKFrJZTpWhOSJiP1XJIBtpj4+MlWStzJmWyXF67m2kn1PqFyuHrVw5lysW+F\\nQoBeB0YN5QKBgDEdJ5mHFum5sKRaH2oCQCwgZoLtbndvrw+Fp5tV5jOl3QEr+ahL\\nS8hSFTJXpI7DrichdSIyF36a99DmLvmgZkolS4NvYdyzofrbDjIuzNnLSVc/D7X5\\nA/yNWY80Ys/ynoLPh482F0Ptza3Ob/yM+9v33TsB4w6f+tYo6TFMtx45AoGBAJSh\\nX8RHidYYSX1bPPWRWtJMue6BY14dCk8bziBrGACvK4OyFm1K8os92F88lE46mt9m\\ncYOlnkokwQNPkqznFXtqYB2eRH5yTPkIKgdOc2vxwWlvDtFezLTrH8SlU2LtH9LY\\n14w2h45o01GF3ldMthRvMtP6f7cZJRpO8JaJN14lAoGBAKbElmAYPs2hex0SSjEd\\n6NErxsiPtmPdHJYcTStrWV8/NtD/vE6jKSxldlkOajX93Dwoguu4sUi19Z5278+x\\nZrlGEWDiTlyJTqPiY8d2tKdsqqX528vsv8JnCaHquL2K1GBdmXyGgAt6bCeZKNg5\\nFfW4V515N/1di3/wwZhDDHPO\\n-----END PRIVATE KEY-----\\n",
			  "client_email": "smartauction@smartauction-324007.iam.gserviceaccount.com",
			  "client_id": "101192593998124018794",
			  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
			  "token_uri": "https://oauth2.googleapis.com/token",
			  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
			  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/smartauction%40smartauction-324007.iam.gserviceaccount.com"
			}
			""";

	private String mCurrentEntryMessage = "";
	private String mDefinePrevKey = "";
	private TTSNowRunnable mTTSNowRunnable;
	private TTSDefineRunnable mTTSDefineRunnable;

	public SoundUtil() {
		try {

			VoiceSelectionParams params = VoiceSelectionParams.newBuilder().setLanguageCode("ko-KR").setSsmlGender(SsmlVoiceGender.FEMALE).build();

//			AudioConfig config = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();

			mTTSNowRunnable = new TTSNowRunnable(params);
			mTTSDefineRunnable = new TTSDefineRunnable(params);
			initCertification(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_CONFIG, ""));
		} catch (Exception ex) {
			mLogger.error("Init Error " + ex.getMessage());
			ex.printStackTrace();
			SentryUtil.getInstance().sendExceptionLog(ex);
		}
	}

	/**
	 * 인증 초기화 처리 함수
	 *
	 * @param googleServiceJson GoogleCloud TTS Service 인증 파일
	 *                          <p>
	 *                          { "type": "service_account", "project_id": "...",
	 *                          "private_key_id": "...", "private_key": "...",
	 *                          "client_email": "...", "client_id": "...",
	 *                          "auth_uri": "...", "token_uri": "...",
	 *                          "auth_provider_x509_cert_url": "...",
	 *                          "client_x509_cert_url": "..." }
	 */
	public void initCertification(String googleServiceJson) {
		// 유효성 검사.
		if (googleServiceJson == null || googleServiceJson.isEmpty()) {
			mTTSNowRunnable.setClient(null);
			mTTSDefineRunnable.setClient(null);
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_CONFIG, "");
			SharedPreference.getInstance().setBoolean(SharedPreference.PREFERENCE_SETTING_USE_SOUND_AUCTION, false);
			return;
		}
		// 기존 인증값과 같고 TTS가 세팅 되어 있는 경우 패스.
		if (googleServiceJson.equals(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_CONFIG, "")) && mTTSNowRunnable.isClient() && mTTSDefineRunnable.isClient())
			return;

		try {
			JsonElement element = JsonParser.parseString(googleServiceJson);
			JsonObject json = element.getAsJsonObject();

			if (!json.has("client_id") || !json.has("client_email") || !json.has("private_key") || !json.has("private_key_id") || !json.has("token_uri") || !json.has("project_id")) {
				mTTSNowRunnable.setClient(null);
				mTTSDefineRunnable.setClient(null);
				SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_CONFIG, googleServiceJson);
				throw new NullPointerException("인증에 필요한 키값이 없습니다.");
			}

			TextToSpeechClient client = TextToSpeechClient.create(TextToSpeechSettings.newBuilder()
							.setCredentialsProvider(FixedCredentialsProvider.create(ServiceAccountCredentials.newBuilder()
							.setClientId(json.get("client_id").getAsString())
							.setClientEmail(json.get("client_email").getAsString())
							.setPrivateKey(privateKeyFromPkcs8(json.get("private_key").getAsString()))
							.setPrivateKeyId(json.get("private_key_id").getAsString())
							.setTokenServerUri(new URI(json.get("token_uri").getAsString()))
							.setProjectId(json.get("project_id").getAsString()).build())).build());

			mTTSNowRunnable.setClient(client);
			mTTSDefineRunnable.setClient(client);
			mTTSDefineRunnable.initSoundSetting();
			SharedPreference.getInstance().setString(SharedPreference.PREFERENCE_SETTING_SOUND_CONFIG, googleServiceJson);
		} catch (Exception ex) {
			mLogger.error("initCertification Error " + ex.getMessage());
			SentryUtil.getInstance().sendExceptionLog(ex);
		}
		
	}

	public PrivateKey privateKeyFromPkcs8(String privateKeyPkcs8) throws IOException {
		Reader reader = new StringReader(privateKeyPkcs8);
		PemReader.Section section = PemReader.readFirstSectionAndClose(reader, "PRIVATE KEY");
		if (section == null) {
			throw new IOException("Invalid PKCS#8 data.");
		} else {
			byte[] bytes = section.getBase64DecodedBytes();
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);

			try {
				KeyFactory keyFactory = SecurityUtils.getRsaKeyFactory();
				return keyFactory.generatePrivate(keySpec);
			} catch (InvalidKeySpecException | NoSuchAlgorithmException var7) {
				throw new IOException("Unexpected exception reading PKCS#8 data", var7);
			}
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
	 * 설정 -> 재생속도 변경시 처리 함수
	 *
	 * @author hmju
	 */
	public void soundSettingSpeedChanged() {
		mTTSDefineRunnable.notifySoundSpeedChanged();
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
	 * 출품 정보 메시지 재생 처리 함수
	 */
	public void playCurrentEntryMessage(PlaybackListener playbackListener) {
		if (CommonUtils.getInstance().isValidString(mCurrentEntryMessage)) {
			if (mTTSNowRunnable.isClient()) {
				stopSound();
				mTTSNowRunnable.play(mCurrentEntryMessage, playbackListener);

			} else {
				if (playbackListener != null) {
					playbackListener.playbackFinished(null);
				}
			}
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
			if (mTTSNowRunnable.isClient()) {
				stopSound();
				mTTSNowRunnable.play(msg, listener);
			} else {
				if (listener != null) {
					listener.playbackFinished(null);
				}
			}

		} else {
			if (listener != null) {
				listener.playbackFinished(null);
			}
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
	 * Google TTS 고정된 문구들 재생 관련 클래스
	 */
	private static class TTSDefineRunnable implements Runnable {

		private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
		private final ExecutorService mThreadService = Executors.newCachedThreadPool();
		private final VoiceSelectionParams mParams;
		private TextToSpeechClient mClient = null;
		private final HashMap<String, SettingSound> mSoundSettingMap = new HashMap<>();
		private Player mCurrentPlayer = null;

		TTSDefineRunnable(VoiceSelectionParams params) {
			mParams = params;
		}

		public void setClient(TextToSpeechClient client) {
			mClient = client;
			if (client == null) {
				mSoundSettingMap.clear();
			}
		}

		public Boolean isClient() {
			return mClient != null;
		}

		/**
		 * 사운드 초기화 처리 함수
		 */
		public void initSoundSetting() {

			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_INTRO, new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_INTRO, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_BUYER, new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_MSG_BUYER, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_GUIDE, new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_GUIDE, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_PRACTICE, new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_PRACTICE, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_GENDER, new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_GENDER, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_USE, new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_USE, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_1, new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_1, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_2, new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_2, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_3, new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_3, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_4, new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_4, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_5, new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_5, "")));
			mSoundSettingMap.put(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_6, new SettingSound(SharedPreference.getInstance().getString(SharedPreference.PREFERENCE_SETTING_SOUND_ETC_6, "")));

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
		 * 데이터 갱신 처리 함수
		 */
		public void notifySoundSpeedChanged() {
			mSoundSettingMap.forEach((key, data) -> {
				data.setChanged();
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
			mThreadService.submit(() -> {
				try {
					release();
					if (mSoundSettingMap.containsKey(msg)) {
						mCurrentPlayer = new Player(mSoundSettingMap.get(msg).getStream());
						mCurrentPlayer.play();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					mLogger.error("Play Error " + ex.getMessage());
					SentryUtil.getInstance().sendExceptionLog(ex);
				}
			});
		}

		/**
		 * 재생중인 Player Close 처리 함수
		 *
		 * @author hmju
		 */
		public void stop() {
			release();
		}

		@Override
		public void run() {
			mSoundSettingMap.forEach((key, data) -> {
				try {
					// 메시지가 변경이 된 경우에만 TTS 가져오기
					if (data.isChanged()) {
						if (mClient != null) {
							data.setStream(getTextToSpeechStream(data.getMessage()));
						}
					}
				} catch (Exception ex) {
					mLogger.error(ex.getMessage());
					SentryUtil.getInstance().sendExceptionLog(ex);
				}
			});
		}

		private synchronized void release() {
			try {
				if (mCurrentPlayer != null) {
					mCurrentPlayer.close();
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

		/**
		 * Network 통신을 통해서 Google TTS Stream 가져오기 함수
		 *
		 * @param msg 원하는 메시지
		 * @return TTS ByteArray
		 * @author hmju
		 */
		private InputStream getTextToSpeechStream(String msg) {
			SynthesisInput input = SynthesisInput.newBuilder().setText(msg).build();
			SynthesizeSpeechResponse response = mClient.synthesizeSpeech(input, mParams, getSoundRateConfig());
			return new ByteArrayInputStream(response.getAudioContent().toByteArray());
		}
		
		/**
		 * 음성 재생 속도
		 * 
		 * @return
		 */
		private AudioConfig getSoundRateConfig() {

			AudioConfig config = null;

			Double rate = GlobalDefine.AUCTION_INFO.PLAY_SOUND_SPEED_1_0;

			if (SettingApplication.getInstance().isSoundRate()) {
				rate = GlobalDefine.AUCTION_INFO.PLAY_SOUND_SPEED_1_1;
			}

			config = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).setSpeakingRate(rate).build();

			return config;
		}
		
	}

	/**
	 * Google TTS 바로 재생 하는 클래스
	 */
	private static class TTSNowRunnable implements Runnable {

		private final Logger mLogger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
		private final ExecutorService mThreadService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		private final VoiceSelectionParams mParams;
		private TextToSpeechClient mClient = null;
		private String mMessage = null;
		private AdvancedPlayer mPlayer = null;
		private PlaybackListener mPlayBackListener = null;

		TTSNowRunnable(VoiceSelectionParams params) {
			mParams = params;
		}

		public void setClient(TextToSpeechClient client) {
			mClient = client;
		}

		public Boolean isClient() {
			return mClient != null;
		}

		public void stop() {
			release();
		}

		public void play(String text) {
			try {
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
//                    release();
					if (mClient != null) {
						mPlayer = new AdvancedPlayer(getTextToSpeechStream(mMessage));
						mPlayer.setPlayBackListener(mPlayBackListener);
						mThreadService.submit(() -> {
							try {
								mPlayer.play();
							} catch (Exception ex) {
								mLogger.error("NowPlayer Error " + ex.getMessage());
								SentryUtil.getInstance().sendExceptionLog(ex);
							}

						});
					}
				}
			} catch (Exception ex) {
				mLogger.error("Exception Tts  " + ex);
				if (mPlayBackListener != null) {
					mPlayBackListener.playbackFinished(null);
				}
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

			if (mPlayer != null) {
				mPlayer.close();
				mPlayer = null;
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

		/**
		 * Network 통신을 통해서 Google TTS Stream 가져오기 함수
		 *
		 * @param msg 원하는 메시지
		 * @return TTS ByteArray
		 * @author hmju
		 */
		private InputStream getTextToSpeechStream(String msg) {
			SynthesisInput input = SynthesisInput.newBuilder().setText(msg).build();
			SynthesizeSpeechResponse response = mClient.synthesizeSpeech(input, mParams, getSoundRateConfig());
			return new ByteArrayInputStream(response.getAudioContent().toByteArray());
		}

		/**
		 * 음성 재생 속도
		 * 
		 * @return
		 */
		private AudioConfig getSoundRateConfig() {

			AudioConfig config = null;

			Double rate = GlobalDefine.AUCTION_INFO.PLAY_SOUND_SPEED_1_0;

			if (SettingApplication.getInstance().isSoundRate()) {
				rate = GlobalDefine.AUCTION_INFO.PLAY_SOUND_SPEED_1_1;
			}

			config = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).setSpeakingRate(rate).build();

			return config;
		}
	}

}
