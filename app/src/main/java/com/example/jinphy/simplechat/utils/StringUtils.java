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
}
