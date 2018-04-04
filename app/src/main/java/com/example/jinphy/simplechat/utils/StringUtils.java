package com.example.jinphy.simplechat.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.support.annotation.IntRange;
import android.text.TextUtils;
import android.util.Base64;

import com.example.jinphy.simplechat.constants.LongConst;
import com.example.jinphy.simplechat.custom_libs.SChain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jinphy on 2017/11/4.
 */

public class StringUtils {

    private StringUtils() {
    }

    public static boolean isPhoneNumber(String number) {
        Pattern p = Pattern.compile("^((13[0-9])|(14[5|7])|(15[^4,\\D])|(18[0,3,5-9]))\\d{8}$");
        Matcher m = p.matcher(number);
        return m.matches();
    }

    public static boolean isNumber(String text) {
        Pattern p = Pattern.compile("[0-9]{1,}");
        Matcher m = p.matcher(text);
        return m.matches();
    }

    public static boolean isNumberOrLetter(String text) {
        Pattern p = Pattern.compile("[0-9a-zA-Z]{6,20}");
        Matcher m = p.matcher(text);
        return m.matches();
    }

    public static boolean equal(String one, String two) {
        if (one == null || two == null) {
            return false;
        }
        return one.equals(two);
    }

    /**
     * DESC: 判断两个字符串是否不相等，如果两个任意一个为空则不相等
     * Created by jinphy, on 2018/1/10, at 20:30
     */
    public static boolean notEqual(String one, String two) {
        return !equal(one, two);
    }

    /**
     * DESC: 判断两个字符串是否不相等，如果两个字符串都为空则认为相等
     * Created by jinphy, on 2018/1/10, at 20:29
     */
    public static boolean notEqualNullable(String one, String two) {
        if (TextUtils.isEmpty(one) && TextUtils.isEmpty(two)) {
            return false;
        }
        return notEqual(one, two);
    }

    public static String generateURI(String baseUrl, String port, String path, String params) {
        StringBuilder build = new StringBuilder();
        build.append(baseUrl)
                .append(":")
                .append(port)
                .append(path)
                .append(TextUtils.isEmpty(params) ? "" : "/?content=")
                .append(params);
        return build.toString();
    }


    /**
     * DESC: 格式化时间
     * Created by jinphy, on 2018/4/3, at 17:54
     */
    public static String formatTime(String milliSeconds) {
        if (!StringUtils.isNumber(milliSeconds)) {
            return "";
        }
        return formatTime(Long.valueOf(milliSeconds));
    }

    /**
     * DESC:格式化时间
     * @param milliSeconds 毫秒
     * Created by jinphy, on 2018/1/7, at 15:25
     */
    public static String formatTime(long milliSeconds) {
        long now = System.currentTimeMillis();
        long deltaTime = now - milliSeconds;

        if (deltaTime < LongConst.MINUTE_1) {
            return "刚刚";
        }
        if (deltaTime < LongConst.MINUTE_30) {
            return (deltaTime / 1000 / 60) + "分钟前";
        }
        Date date = new Date(milliSeconds);
        SimpleDateFormat format;
        if (deltaTime < LongConst.DATE_1) {
            format = new SimpleDateFormat("HH:mm:ss");
        } else {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return format.format(date);
    }

    /**
     * DESC: 将字节数组转换成16进制字符串
     * Created by jinphy, on 2017/12/31, at 20:15
     */
    public static String bytes2HexString(byte[] b) {
        StringBuffer result = new StringBuffer();
        String hex;
        for (int i = 0; i < b.length; i++) {
            hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            result.append(hex.toUpperCase());
        }
        return result.toString();
    }

    /**
     * DESC: 将16进制的字符串转换成字节数组
     * Created by jinphy, on 2017/12/31, at 20:21
     */
    public static byte[] hexString2Bytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }

    /**
     * DESC: 格式化角度字符串
     * Created by jinphy, on 2018/1/10, at 20:49
     */
    public static CharSequence formatDegree(Object value) {
        return SChain.with(value).append("o").superscript(0.5f).make();
    }

    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap, @IntRange(from = 0,to = 100) int... quality) {
        if (bitmap == null) {
            return "";
        }
        int q = 100;
        if (quality.length > 0) {
            q = quality[0];
        }
        String result = null;
        ByteArrayOutputStream out = null;
        try {
            if (bitmap != null) {
                out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, q, out);
                out.flush();

                byte[] bitmapBytes = out.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     * @param base64
     * @return
     */
    public static Bitmap base64ToBitmap(String base64) {
        if (TextUtils.isEmpty(base64)) {
            return null;
        }
        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    public static String bytesToStr(byte[] source) {
        if (source == null) {
            return "";
        }
        return org.java_websocket.util.Base64.encodeBytes(source);
    }

    public static String bytesToStr(byte[] source, int offset, int len) {
        if (source == null) {
            return "";
        }
        return org.java_websocket.util.Base64.encodeBytes(source, offset, len);
    }

    public static byte[] strToBytes(String source) {
        try {
            return org.java_websocket.util.Base64.decode(source);
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

}
