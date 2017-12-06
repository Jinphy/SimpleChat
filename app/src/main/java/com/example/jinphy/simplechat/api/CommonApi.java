package com.example.jinphy.simplechat.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by jinphy on 2017/12/4.
 */

class CommonApi implements ApiInterface<WebSocket> {
    // 宿舍WiFi
    //    public static String BASE_URL = "ws://192.168.0.3";
    //    成和WiFi
    public static String BASE_URL = "ws://192.168.3.21";
    //    我的手机WiFi
    //    public static String BASE_URL = "ws://192.168.43.224";
    //    公司WiFi
//    public static String BASE_URL = "ws://172.16.11.134";


    /**
     * DESC: 推送通道端口
     * Created by jinphy, on 2017/12/4, at 21:33
     */
    public static String PUSH_PORT = "4540";

    /**
     * DESC: 发送信息通道端口
     * Created by jinphy, on 2017/12/4, at 21:33
     */
    public static String SEND_PORT = "4541";

    /**
     * DESC: 普通网络请求端口
     * Created by jinphy, on 2017/12/4, at 21:34
     */
    public static String COMMON_PORT = "4542";


    //==================属性====================================================\\

    protected Context context;
    protected Map<String,String> headers;
    protected Map<String, String> params;
    protected boolean showProgress;
    protected String baseUrl;
    protected String port;
    protected int connectTimeout;
    protected String path;

    protected Api.OnStart onStart;
    protected Api.OnOpen onOpen;
    protected Api.OnNext onNext;
    protected Api.OnError onError;
    protected Api.OnClose onClose;

    //====================方法===============================================

    /**
     * DESC: 创建一个网络请求api
     * Created by jinphy, on 2017/12/4, at 22:17
     */
    public static ApiInterface create(Context context) {
        return new CommonApi(context);
    }

    /*
     * DESC: 私有化构造函数
     * Created by jinphy, on 2017/12/4, at 22:17
     */
    private CommonApi(Context context) {
        this.context = context;
        init();
    }

    /*
     * DESC: 初始化参数
     * Created by jinphy, on 2017/12/4, at 22:17
     */
    protected void init() {
        this.showProgress = false;
        this.params = new HashMap<>();
        this.headers = new HashMap<>();
        this.baseUrl = CommonApi.BASE_URL;
        this.port = CommonApi.COMMON_PORT;
        this.connectTimeout = 120;
    }

    /**
     * DESC: 设置baseUrl
     * Created by jinphy, on 2017/12/4, at 22:08
     */
    public ApiInterface baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * DESC: 设置请求端口
     * Created by jinphy, on 2017/12/4, at 22:08
     */
    public ApiInterface port(String port) {
        this.port = port;
        return this;
    }

    /**
     * DESC: 设置接口路径
     * Created by jinphy, on 2017/12/4, at 23:38
     */
    @Override
    public ApiInterface path(String path) {
        this.path = path;
        return this;
    }

    /**
     * DESC: 设置header
     * Created by jinphy, on 2017/12/4, at 22:28
     */
    public ApiInterface header(String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return this;
        }
        this.headers.put(key, value);
        return this;
    }

    /**
     * DESC: 添加请求参数
     * Created by jinphy, on 2017/12/4, at 21:43
     */
    @Override
    public ApiInterface param(String key, Object value) {
        if (TextUtils.isEmpty(key) || value == null || value.toString().trim().length() == 0) {
            return this;
        }
        this.params.put(key, value.toString());
        return this;
    }

    /**
     * DESC: 设置连接超时
     * Created by jinphy, on 2017/12/4, at 22:34
     */
    @Override
    public ApiInterface connectTimeout(int timeout) {
        this.connectTimeout = timeout;
        return this;
    }

    /**
     * DESC: 设置网络请求是显示进度条
     * Created by jinphy, on 2017/12/4, at 21:47
     */
    @Override
    public ApiInterface showProgress() {
        this.showProgress = true;
        return this;
    }

    /**
     * DESC: 执行网络请求
     * Created by jinphy, on 2017/12/4, at 22:19
     */
    public WebSocket request() {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("请求路劲接口不能为空，必须调用CommonApi.path(String path) 设置请求接口");
        }
        String url = StringUtils.generateURI(baseUrl, port, path, params);
        try {
            CustomWebSocketClient client = new CustomWebSocketClient(url, headers, connectTimeout);
            client.connect();
            return client;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            BaseApplication.showToast("uri不正确！", false);
            return null;
        }

    }


    //=================回调接口============================================================

    /**
     * DESC: 设置成功回调
     * Created by jinphy, on 2017/12/4, at 21:58
     */
    public ApiInterface onNext(Api.OnNext onNext ) {
        this.onNext = onNext;
        return this;
    }

    /**
     * DESC: 设置异常回调
     * Created by jinphy, on 2017/12/4, at 21:59
     */
    public ApiInterface onError(Api.OnError onError) {
        this.onError = onError;
        return this;
    }

    /**
     * DESC: 设置开始回调
     * Created by jinphy, on 2017/12/4, at 23:24
     */
    public ApiInterface onStart(Api.OnStart onStart ) {
        this.onStart = onStart;
        return this;
    }

    /**
     * DESC: 设置开始回调
     * Created by jinphy, on 2017/12/4, at 22:00
     */
    public ApiInterface onOpen(Api.OnOpen onOpen ) {
        this.onOpen = onOpen;
        return this;
    }


    /**
     * DESC: 设置关闭回调
     * Created by jinphy, on 2017/12/4, at 22:01
     */
    public ApiInterface onClose(Api.OnClose onClose ) {
        this.onClose = onClose;
        return this;
    }


    //===================网络请求客户端===========================================
    private class CustomWebSocketClient extends WebSocketClient{

        public CustomWebSocketClient(
                String uri,
                Map<String ,String>headers,
                int connectTimeout)throws URISyntaxException{
            super(new URI(uri),new Draft_6455(),headers,connectTimeout);
            if (onStart != null) {
                Flowable.just("")
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(value -> onStart.call())
                        .subscribe();
            }

        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.e(TAG, "onOpen: "+handshakedata.getHttpStatusMessage());
            if (onOpen == null) {
                return;
            }
            Flowable.just(this)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(onOpen::call)
                    .subscribe();
        }

        private static final String TAG = "CustomWebSocketClient";
        @Override
        public void onMessage(String message) {
            Log.e(TAG, "onMessage: "+message);
            if (onNext == null) {
                this.close();
                return;
            }
            Flowable.just(message)
                    .map(value -> GsonUtils.toBean(value, Api.Response.class))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(onNext::call)
                    .doOnComplete(this::close)
                    .subscribe();
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.e(TAG, "onClose: reason = " + reason);
            // TODO: 2017/12/4 在这里关闭进度条
            if (onClose == null) {
                return;
            }
            Flowable.just("")
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(value -> onClose.call())
                    .subscribe();
        }

        @Override
        public void onError(Exception ex) {
            Log.e(TAG, "onError: "+ex.getMessage());
            if (onError == null) {
                return;
            }
            Flowable.just(ex)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(onError::call)
                    .subscribe();
        }
    }

    //==================请求返回结果==============================================

    //==================请求接口 Path=============================================


}
