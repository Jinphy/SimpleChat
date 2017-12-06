package com.example.jinphy.simplechat.api;

import android.text.TextUtils;

import org.java_websocket.WebSocket;

/**
 * Created by Jinphy on 2017/12/6.
 */

public interface ApiInterface<T> {

    /**
     * DESC: 设置baseUrl
     * Created by jinphy, on 2017/12/4, at 22:08
     */
    ApiInterface baseUrl(String baseUrl);

    /**
     * DESC: 设置请求端口
     * Created by jinphy, on 2017/12/4, at 22:08
     */
    ApiInterface port(String port);

    /**
     * DESC: 设置接口路径
     * Created by jinphy, on 2017/12/4, at 23:38
     */
    ApiInterface path(String path);

    /**
     * DESC: 设置header
     * Created by jinphy, on 2017/12/4, at 22:28
     */
    ApiInterface header(String key, String value);

    /**
     * DESC: 添加请求参数
     * Created by jinphy, on 2017/12/4, at 21:43
     */
    ApiInterface param(String key, Object value);

    /**
     * DESC: 设置连接超时
     * Created by jinphy, on 2017/12/4, at 22:34
     */
    ApiInterface connectTimeout(int timeout);

    /**
     * DESC: 设置网络请求是显示进度条
     * Created by jinphy, on 2017/12/4, at 21:47
     */
    ApiInterface showProgress();


    /**
     * DESC: 设置成功回调
     * Created by jinphy, on 2017/12/4, at 21:58
     */
    ApiInterface onNext(Api.OnNext onNext);

    /**
     * DESC: 设置异常回调
     * Created by jinphy, on 2017/12/4, at 21:59
     */
    ApiInterface onError(Api.OnError onError);

    /**
     * DESC: 设置开始回调
     * Created by jinphy, on 2017/12/4, at 23:24
     */
    ApiInterface onStart(Api.OnStart onStart);

    /**
     * DESC: 设置开始回调
     * Created by jinphy, on 2017/12/4, at 22:00
     */
    ApiInterface onOpen(Api.OnOpen onOpen);

    /**
     * DESC: 设置关闭回调
     * Created by jinphy, on 2017/12/4, at 22:01
     */
    ApiInterface onClose(Api.OnClose onClose);

    T request();
}
