package com.nh.share.api;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 문자열 처리 관련 Util Class
 */
public class StringUtil {
    private static StringUtil instance = new StringUtil();

    public static synchronized StringUtil getInstance() {
        return instance;
    }

    /**
     * 문자열 Null, Empty, Length 유효성 확인 함수
     *
     * @param str 확인 문자열
     * @return boolean true : 유효 문자, false : 무효 문자
     */
    public synchronized boolean isValidString(String str) {
        if (str == null || str.equals("") || str.isEmpty()) {
            return false;
        }

        if (str.trim().length() <= 0) {
            return false;
        }

        return true;
    }

    /**
     * 문자열 Trim 처리 함수
     *
     * @param str 문자열
     * @return result 변환 문자열 반환
     */
    public synchronized String getTrimString(String str) {
        return str.trim();
    }

    /**
     * 세자리수 콤마 찍기
     *
     * @param num
     * @param decimal 소수점 표현 유무
     * @return
     */
    public synchronized String setConvertComma(int num, boolean decimal) {
        if (decimal) {
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            return decimalFormat.format(num);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            return decimalFormat.format(num);
        }
    }

    /**
     * 세자리수 콤마 찍기
     *
     * @param numStr
     * @param decimal 소수점 표현 유무
     * @return
     */
    public synchronized String setConvertComma(String numStr, boolean decimal) {
        if (isIntegerFromStr(numStr)) {
            return setConvertComma(Integer.parseInt(numStr), decimal);
        }
        return numStr;
    }

    /**
     * integer 체크
     *
     * @param num
     * @return
     */
    public synchronized boolean isIntegerFromStr(String num) {
        try {
            Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    /**
     * 날짜 형식 변경 유틸
     *
     * @param stringDate  20180528170706
     * @param old_pattern "yyyyMMddHHmmss",
     * @param new_pattern "YY.MM.dd HH:mm"
     * @return
     */
    public synchronized String convertDatePattern(String stringDate, String old_pattern, String new_pattern) {
        SimpleDateFormat oldFormatter = new SimpleDateFormat(old_pattern, Locale.KOREA);
        try {
            Date time = oldFormatter.parse(stringDate);
            SimpleDateFormat newFormatter = new SimpleDateFormat(new_pattern, Locale.KOREA);
            return newFormatter.format(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stringDate;
    }

    public synchronized String convertUserIdView(String strUserId) {
        if (strUserId == null)
            return "";
        String convertUserId = strUserId;

        if (strUserId.length() > 3) {
            convertUserId = strUserId.substring(0, (strUserId.length() - 3)) + "***";
        }

        return convertUserId;
    }

}
