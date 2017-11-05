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
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(number);
        return m.matches();
    }

    public static boolean isNumber(String text) {
        Pattern p = Pattern.compile("[0-9]{1,}");
        Matcher m = p.matcher(text);
        return m.matches();
    }
}
