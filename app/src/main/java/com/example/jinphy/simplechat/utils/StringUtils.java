package com.example.jinphy.simplechat.utils;

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

    public static boolean notEqual(String one, String two) {
        return !equal(one, two);
    }

    public static String generateURI(String baseUrl, String port, String path, String params) {
        StringBuilder build = new StringBuilder();
        build.append(baseUrl)
                .append(":")
                .append(port)
                .append(path)
                .append("/?content=")
                .append(params);
        return build.toString();
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

}
