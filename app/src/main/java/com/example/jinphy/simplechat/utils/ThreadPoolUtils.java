package com.example.jinphy.simplechat.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * DESC:
 * Created by jinphy on 2018/1/19.
 */

public class ThreadPoolUtils {

    /**
     * DESC: 创建一个线程池
     * Created by jinphy, on 2018/1/19, at 10:04
     */
    public static final ExecutorService threadPool = Executors.newCachedThreadPool();

}
