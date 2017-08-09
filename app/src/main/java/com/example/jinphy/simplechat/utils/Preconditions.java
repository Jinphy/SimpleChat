package com.example.jinphy.simplechat.utils;

/**
 * Created by jinphy on 2017/8/9.
 */

public class Preconditions {


    public static <T> T checkNotNull(T object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return object;
    }

    public static <T> T checkNotNull(T object, String msg) {
        if (object == null) {
            throw new NullPointerException(msg);
        }
        return object;
    }
}
