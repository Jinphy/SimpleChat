package com.example.jinphy.simplechat.utils;

/**
 * Created by jinphy on 2017/8/9.
 */

public class Preconditions {


    /**
     * DESC: 检测非空条件
     * Created by jinphy, on 2017/12/30, at 17:27
     */
    public static <T> T checkNotNull(T object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return object;
    }

    /**
     * DESC: 检测非空条件
     * Created by jinphy, on 2017/12/30, at 17:27
     */
    public static <T> T checkNotNull(T object, String msg) {
        if (object == null) {
            throw new NullPointerException(msg);
        }
        return object;
    }
}
