package com.example.jinphy.simplechat.utils;

/**
 * DESC:
 * Created by jinphy on 2018/1/3.
 */

public class ThreadUtils {

    /**
     * DESC: sleep
     * Created by jinphy, on 2018/1/3, at 11:38
     */
    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
