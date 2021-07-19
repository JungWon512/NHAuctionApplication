package com.nh.share.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
    private static final Logger mLogger = LoggerFactory.getLogger(FileUtils.class);

    private static FileUtils fileUtils = new FileUtils();

    public FileUtils() {

    }

    public static FileUtils getInstance() {
        return fileUtils;
    }

    /**
     * 
     * @MethodName writeFile
     * @Description TXT 파일 생성 및 쓰기 기능 처리
     *
     * @param filePath
     *            파일 생성 경로
     * @param content
     *            파일 쓰기 내용
     */
    public synchronized void writeFile(String directoryPath, String fileName, String content) {
        File fileDirectory = new File(directoryPath);
        File file = new File(fileDirectory, fileName);
        FileWriter fileWriter = null;

        try {

            if (!fileDirectory.exists()) {
                fileDirectory.mkdirs();
            }
            if (file.exists()) {
                mLogger.info(" 이미 생성 된 " + fileName + "파일이 존재합니다.");
            } else {
                if (file.createNewFile()) {
                    mLogger.info(fileName + "파일이 정상적으로 생성되었습니다.");
                }
            }

            fileWriter = new FileWriter(file, true);

            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * @MethodName deleteAuctionResourceDirectory
     * @Description 출품 데이터의 차량 이미지 및 TTS 파일 삭제 처리
     *
     * @return String 경매 리소스 디렉토리 경로
     */
    public synchronized void deleteAuctionResourceDirectory(String path) {

        File directory = new File(path);
        try {
            if (directory.exists()) {
                File[] fileList = directory.listFiles();

                for (int i = 0; i < fileList.length; i++) {
                    if (fileList[i].isFile()) {
                        fileList[i].delete();
                        mLogger.debug("파일이 삭제되었습니다.");
                    } else {
                        deleteAuctionResourceDirectory(fileList[i].getPath());
                        mLogger.debug("폴더가 삭제되었습니다.");
                    }
                    fileList[i].delete();
                }
                directory.delete();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
