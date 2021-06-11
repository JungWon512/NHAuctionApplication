package com.nh.share.api.request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nh.share.api.ActionResultListener;
import com.nh.share.api.ActionRuler;
import com.nh.share.api.GlobalDefine;
import com.nh.share.api.StringUtil;
import com.nh.share.api.response.ResponseFileDownload;
import com.nh.share.interfaces.FileDownLoadListener;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * 파일 다운 로드를 수행한다 파일을 저장할 로컬 위치는 각 파일 시스템이 다르므로 따로 지정해 입력해야 한다. 파일의 확장자는 없다고
 * 가정한다. 파일 이름이 겹칠 경우나 디렉터리를 입력한 경우는 아래와 같이 임의로 수정한다.
 * 
 * ex) ./directory 입력하였으나 디렉토리인 경우 => ./directory0 (이 경우 directory0은 디렉토리가 아닌
 * 파일임) ./directory1/file 입력하였으나 이미 파일이 존재할 경우 => ./directory1/file0
 * ./directory1/file 입력하였으나 이미 파일이 존재하고 ./directory1/file0도 존재할 경우 =>
 * ./directory1/file1
 * 
 *
 */
public class ActionFileDownload extends Action {
    private static final Logger mLogger = LoggerFactory.getLogger(ActionFileDownload.class);

    private String mUrl; // 다운로드 파일 URL
    private String mFilePath; // 파일 저장 위치
    private String mFileName; // 저장 파일명
    private String mContentType; // 컨텐츠 타입
    private int mMaxRetry; // 최대 재시도 횟수
    private int mRetry = 0; // 재시도 횟수

    private FileDownLoadListener mFileDownLoadListener; // 테스트 모드 시 파일 다운로드 상태 전송
    private String mName; // 테스트 모드 시 파일 다운르도 구분명 TTS or 차량 이미지 or 전개도 이미지

    /**
     * @param url
     *            다운로드 URL
     * @param filePath
     *            저장 파일 경로
     * @param fileName
     *            저장 파일 이름
     * @param resultListener
     *            파일 다운로드 후 리스너
     */
    public ActionFileDownload(String url, String filePath, String fileName, int maxRetry, ActionResultListener resultListener, FileDownLoadListener fileDownLoadListener, String name) {
        mUrl = url;
        mFilePath = filePath;
        mFileName = fileName;
        mMaxRetry = maxRetry;
        mName = "DOWNLOAD " + name;

        if (mFileName.contains("jpeg") || mFileName.contains("jpg") || mFileName.contains("JPEG") || mFileName.contains("JPG")) {
            mContentType = "image/jpeg";
        } else if (mFileName.contains("png") || mFileName.contains("PNG")) {
            mContentType = "image/png";
        } else if (mFileName.contains("wav") || mFileName.contains("WAV")) {
            mContentType = "audio/x-wav";
        }

        mResultListenerBase = resultListener;
        if (fileDownLoadListener != null) {
            mFileDownLoadListener = fileDownLoadListener;
        }
    }

    public interface RetrofitAPIService {
        @GET
        Call<ResponseBody> downloadFileWithUrl(@Url String url);
    }

    private final Callback<ResponseBody> mCallBack = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            actionDone(resultType.ACTION_RESULT_RUNNEXT);

            if (response.isSuccessful()) {
                mLogger.debug("Content Type : " + response.body().contentType());

                if (response.body().contentType().toString().equals(mContentType)) {
                    String newFilePath = createFileWithResponseBody(response.body()); // 응답의 내용을 파일로 저장한다.
                    if (newFilePath != null) {
                        if (mFileDownLoadListener != null) {
                            mFileDownLoadListener.OnFileDownloadListener(mName + " 생성 완료 =>" + newFilePath);
                        }
                        ResponseFileDownload responseFileDownload = new ResponseFileDownload();
                        responseFileDownload.setFilePath(newFilePath);
                        responseFileDownload.setSuccess(true);
                        if (mResultListenerBase != null) {
                            mResultListenerBase.onResponseResult(responseFileDownload);
                        }
                    } else {
                        retryOrFailure(call);
                    }
                } else {
                    retryOrFailure(call);
                }
            } else {
                retryOrFailure(call);
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            if (mFileDownLoadListener != null) {
                mFileDownLoadListener.OnFileDownloadListener(mName + " 생성 실패=> " + t.toString());
            }
            actionDone(resultType.ACTION_RESULT_ERROR_RESPONSE);
        }
    };

    /**
     * 통신 응답의 데이터를 파일로 저장한다. 로컬 파일 위치는 전역변수를 사용한다.(좋지 않다고 판단되면 parameter로 전달하도록 수정하는
     * 것이..)
     * 
     * 입력된 로컬 파일 위치가 유효하지 않다면 파일명을 수정하여 저장한 후, 실제 저장된 로컬 파일 위치를 리턴한다.
     * 
     * @param body
     *            통신 응답
     * @return 성공: 저장된 로컬 파일 위치, 실패: null
     */
    private String createFileWithResponseBody(ResponseBody body) {
        File fileDirectory = new File(mFilePath);
        File file = new File(fileDirectory, mFileName);

        try {
            if (!fileDirectory.exists()) {
                fileDirectory.mkdirs();
            }

            if (file.exists()) {
                if (mFileDownLoadListener != null) {
                    mFileDownLoadListener.OnFileDownloadListener(mName + " 생성중 => 이미 생성 된 " + mFileName + "파일이 존재합니다.");
                }
                mLogger.info(" 이미 생성 된 " + mFileName + "파일이 존재합니다.");
            } else {
                if (file.createNewFile()) {
                    if (mFileDownLoadListener != null) {
                        mFileDownLoadListener.OnFileDownloadListener(mName + " 생성중 =>" + mFileName + " 파일이 정상적으로 생성되었습니다.");
                    }
                    mLogger.info(mFileName + "파일이 정상적으로 생성되었습니다.");
                }
            }

            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                byte[] fileReader = new byte[4096];
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();

            } catch (Exception e) {
                if(mFileDownLoadListener != null) {
                    mFileDownLoadListener.OnFileDownloadListener(mName + " 생성중 에러 => " + e.toString());
                }
                return null;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            return file.getAbsolutePath().toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    void actionDone(String message, resultType type, String errorCode) {
        String errStr = "";
        String errCodeDisp = "";

        if (StringUtil.getInstance().isValidString(errorCode)) {
            errCodeDisp = "[" + errorCode + "]";
        } else {
            errorCode = type.toString();
        }

        if (StringUtil.getInstance().isValidString(message)) {
            errStr = message;
        }

        switch (type) {
        case ACTION_RESULT_RUNNEXT:
            ActionRuler.getInstance().runNext();
            break;
        case ACTION_RESULT_ERROR_DISABLE_NETWORK:
            ActionRuler.getInstance().finish();
            mResultListenerBase.onResponseError(errorCode);
            break;
        case ACTION_RESULT_ERROR_NOT_RESPONSE:
            ActionRuler.getInstance().finish();
            mResultListenerBase.onResponseError(errorCode);
            break;
        case ACTION_RESULT_ERROR_RESPONSE:
            ActionRuler.getInstance().finish();
            mResultListenerBase.onResponseError(errorCode);
            break;
        case ACTION_RESULT_ERROR_INTRO:
            ActionRuler.getInstance().runNext();
            mResultListenerBase.onResponseError(errorCode);
            break;
        case ACTION_RESULT_ERROR_SKIP:
            ActionRuler.getInstance().runNext();
            mResultListenerBase.onResponseError(errorCode);
            break;
        }
    }

    @Override
    void actionDone(resultType type, String errorCode) {
        actionDone("", type, errorCode);
    }

    @Override
    void actionDone(resultType type) {
        actionDone("", type, "");
    }

    /**
     * 지정된 재시도 횟수만큼 재시도를 하거나, 응답 실패 처리한다.
     * 
     * @param call
     *            시도된 통신 객체, 재시도시 이 객체를 복사하여 이용한다.
     */
    private void retryOrFailure(Call<ResponseBody> call) {
        if (mRetry < mMaxRetry) {
            mRetry++;
            if(mFileDownLoadListener != null) {
                mFileDownLoadListener.OnFileDownloadListener(mName + " 재시도 횟수=> " + mRetry);
            }
            call.clone().enqueue(mCallBack);
        } else {
            actionDone(resultType.ACTION_RESULT_ERROR_RESPONSE);
        }
    }

    @Override
    public void run() {

        if (mFileDownLoadListener != null) {
            mFileDownLoadListener.OnFileDownloadListener(mName + " 다운로드 시작=> " + mUrl);
        }

        // 다운받을 URL의 임의 지정(http://로 시작)이므로 baseUrl은 없지만 입력하지 않으면 retrofit2 라이브러리가
        // 에러를내뱉는다. 아무거나 지정 가능. 실제 통신엔 무시된다.
        mRetrofit = new Retrofit.Builder().baseUrl(GlobalDefine.getInstance().getBaseDomain()).client(getDownloadHttpClient()).build();

        RetrofitAPIService mRetrofitAPIService = mRetrofit.create(RetrofitAPIService.class);
        mRetrofitAPIService.downloadFileWithUrl(mUrl).enqueue(mCallBack);

    }
}
