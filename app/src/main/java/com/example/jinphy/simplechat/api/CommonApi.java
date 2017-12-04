package com.example.jinphy.simplechat.api;

import android.content.Context;
import android.text.TextUtils;

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

public class CommonApi {
    // 宿舍WiFi
    //    public static String BASE_URL = "ws://192.168.0.3";
    //    成和WiFi
    public static String BASE_URL = "ws://192.168.3.21";
    //    我的手机WiFi
    //    public static String BASE_URL = "ws://192.168.43.224";


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

    protected OnStart onStart;
    protected OnOpen onOpen;
    protected OnNext onNext;
    protected OnError onError;
    protected OnClose onClose;

    //====================方法===============================================

    /**
     * DESC: 创建一个网络请求api
     * Created by jinphy, on 2017/12/4, at 22:17
     */
    public static CommonApi with(Context context) {
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
    public CommonApi baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * DESC: 设置请求端口
     * Created by jinphy, on 2017/12/4, at 22:08
     */
    public CommonApi port(String port) {
        this.port = port;
        return this;
    }

    /**
     * DESC: 设置接口路径
     * Created by jinphy, on 2017/12/4, at 23:38
     */
    public CommonApi path(String path) {
        this.path = path;
        return this;
    }

    /**
     * DESC: 设置header
     * Created by jinphy, on 2017/12/4, at 22:28
     */
    public CommonApi header(String key, String value) {
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
    public CommonApi param(String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return this;
        }
        this.params.put(key, value);
        return this;
    }

    /**
     * DESC: 设置连接超时
     * Created by jinphy, on 2017/12/4, at 22:34
     */
    public CommonApi connectTimeout(int timeout ) {
        this.connectTimeout = timeout;
        return this;
    }

    /**
     * DESC: 设置网络请求是显示进度条
     * Created by jinphy, on 2017/12/4, at 21:47
     */
    public CommonApi showProgress() {
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
            BaseApplication.showToast("uri语法不正确！", false);
            return null;
        }

    }


    //=================回调接口============================================================
    /**
     * DESC: 请求成功回调
     * Created by jinphy, on 2017/12/4, at 21:56
     */
    public interface OnNext {
        void call(Response result);
    }

    /**
     * DESC: 设置成功回调
     * Created by jinphy, on 2017/12/4, at 21:58
     */
    public CommonApi onNext(OnNext onNext ) {
        this.onNext = onNext;
        return this;
    }

    /**
     * DESC: 请求异常回调
     * Created by jinphy, on 2017/12/4, at 21:56
     */
    public interface OnError {
        void call(Exception e);
    }

    /**
     * DESC: 设置异常回调
     * Created by jinphy, on 2017/12/4, at 21:59
     */
    public CommonApi onError(OnError onError) {
        this.onError = onError;
        return this;
    }
    /**
     * DESC: 连接打开时回调
     * Created by jinphy, on 2017/12/4, at 21:56
     */
    public interface OnOpen {
        void call(WebSocket webSocket);
    }

    /**
     * DESC: 连接打开时回调
     * Created by jinphy, on 2017/12/4, at 23:24
     */
    public interface OnStart{
        void call();
    }

    /**
     * DESC: 设置开始回调
     * Created by jinphy, on 2017/12/4, at 23:24
     */
    public CommonApi onStart(OnStart onStart ) {
        this.onStart = onStart;
        return this;
    }

    /**
     * DESC: 设置开始回调
     * Created by jinphy, on 2017/12/4, at 22:00
     */
    public CommonApi onOpen(OnOpen onOpen ) {
        this.onOpen = onOpen;
        return this;
    }

    /**
     * DESC: 请求结束时回调
     * Created by jinphy, on 2017/12/4, at 21:57
     */
    public interface OnClose {
        void call();
    }

    /**
     * DESC: 设置关闭回调
     * Created by jinphy, on 2017/12/4, at 22:01
     */
    public CommonApi onClose(OnClose onClose ) {
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
            if (onOpen == null) {
                return;
            }
            Flowable.just(this)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(onOpen::call)
                    .subscribe();
        }

        @Override
        public void onMessage(String message) {
            if (onNext == null) {
                return;
            }
            Flowable.just(message)
                    .map(value -> GsonUtils.toBean(value, Response.class))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(onNext::call)
                    .subscribe();
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
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
    public static class Response{
        public static String YES = "1";
        public static String NO = "0";


        private String code;
        private String msg;
        private String data;

        public Response() {

        }

        public Response(String code, String msg, String data) {
            this.code = code;
            this.msg = msg;
            this.data = data;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    //==================请求接口 Path=============================================

    /**
     * DESC: 网络请求路径
     * Created by jinphy, on 2017/12/4, at 21:38
     */
    interface Path{
        String login = "/user/login";
        String findUser = "/user/findUser";
        String createNewUser = "/user/createNewUser";
    }

    //===================参数key==========================================================
    /**
     * DESC: 参数的key
     * Created by jinphy, on 2017/12/4, at 23:55
     */
    interface Key{

    }

}
