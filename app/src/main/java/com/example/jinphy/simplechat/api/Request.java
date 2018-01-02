package com.example.jinphy.simplechat.api;

import android.util.Log;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.annotations.Get;
import com.example.jinphy.simplechat.annotations.Post;
import com.example.jinphy.simplechat.exceptions.ServerException;
import com.example.jinphy.simplechat.utils.DeviceUtils;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * DESC: 一个泛型网络请求类；泛型T：为Response类中data字段的类型
 * Created by jinphy on 2017/12/31.
 */

class Request<T> extends WebSocketClient implements ObservableOnSubscribe<Response<T>> {

    private ObservableEmitter<Response<T>> emitter;
    private Method method;
    private Body body;
    private String url;
    private int readTimeout; // 读取数据超时，单位毫秒

    private Disposable disposable;    // 读取数据超时的计时器，当网络超时时，取消网络请求

    public Request(
            String url,
            Map<String ,String> headers,
            int connectTimeout,
            int readTimeout)throws URISyntaxException {
        super(new URI(url),new Draft_6455(),headers,connectTimeout);
        this.url = url;
        this.readTimeout = readTimeout;
    }

    public String url(){
        return this.url;
    }

    /**
     * DESC: 网络连接成功是回调
     *  如果是post请求，则在这里发送请求体
     * Created by jinphy, on 2017/12/31, at 21:06
     */
    @Override
    public void onOpen(ServerHandshake handShakeData) {
        if (method == Method.POST) {
            send(GsonUtils.toJson(body));
        }
        // 网络连接打开时，倒计时阅读超时
        Observable.timer(readTimeout, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> this.disposable = disposable)
                .doOnNext(time->{
                    if (this.emitter != null && !this.emitter.isDisposed()) {
                        this.close(500);
                    }
                })
                .subscribe();
    }

    private static final String TAG = "CustomWebSocketClient";

    /**
     * DESC: 接收消息
     * Created by jinphy, on 2017/12/31, at 21:16
     */
    @Override
    public void onMessage(String message) {
        // 取消阅读超时任务
        if (this.disposable != null && !this.disposable.isDisposed()) {
            this.disposable.dispose();
        }
        // 如果没有超时则返回网络请求结果
        if (emitter!=null && !emitter.isDisposed()) {
            try {
                // 解密
                message = EncryptUtils.aesDecrypt(message);
                // 解码
                message = URLDecoder.decode(message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Type jsonType = new TypeToken<Response<T>>() {}.getType();
            try {
                Thread.sleep(500);//为了能够在网络条件好的情况下显示对话框，延迟0.3毫秒再返回数据
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            emitter.onNext(GsonUtils.toBean(message, jsonType));
        }
        this.close(200);
    }

    /**
     * DESC: 注意：网路连接超时时会回调该方法而不是onError方法，但是没有网络时会回调onError方法
     * Created by jinphy, on 2018/1/2, at 21:36
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        LogUtils.e("onClose");
        if (emitter != null && !emitter.isDisposed()) {
            switch (code) {
                case 200:
                    emitter.onComplete();
                    break;
                case 500:
                    emitter.onError(new ServerException("服务器无响应！"));
                    break;
                default:
                    emitter.onError(new ServerException("网络连接超时！"));
                    break;
            }
            emitter = null;
        }
    }

    @Override
    public void onError(Exception e) {
        LogUtils.e(e);
        if (emitter!=null && !emitter.isDisposed()) {
            emitter.onError(e);
            emitter = null;
        }
    }

    /**
     * DESC: 在订阅网络请求时获取时间发射器，用来发射网络请求事件
     * Created by jinphy, on 2017/12/31, at 15:53
     */
    @Override
    public void subscribe(ObservableEmitter<Response<T>> e) throws Exception {
        this.emitter = e;
        this.connect();
    }

    /**
     * DESC: 创建一个Builder 对象
     * Created by jinphy, on 2017/12/31, at 21:50
     */
    public static<U> Builder<U> newBuilder() {
        return new Builder<>();
    }


    public static class Builder<T> {
        private String baseUrl = Api.BASE_URL;
        private String port = Api.COMMON_PORT;
        private String path;
        private int connectTimeout = 10_000;// 单位是毫秒，默认为10秒
        private int readTimeout = 10_000;   // 单位是毫秒，默认为10秒
        private Map<String, String> headers = new HashMap<>();
        private Params params;
        private String requestId;

        private Builder() {
            requestId = path + DeviceUtils.devceId() + System.currentTimeMillis();
            requestId = EncryptUtils.md5(requestId);
        }

        /**
         * DESC: 设置baseUrl
         * Created by jinphy, on 2017/12/31, at 16:10
         */
        public Builder<T> baseUrl(@NonNull String baseUrl) {
            ObjectHelper.requireNonNull(baseUrl, "baseUrl cannot be null!");
            this.baseUrl = baseUrl;
            return this;
        }


        /**
         * DESC: 设置端口
         * Created by jinphy, on 2017/12/31, at 16:11
         */
        public Builder<T> port(@NonNull String port) {
            ObjectHelper.requireNonNull(port, "port cannot be null");
            this.port = port;
            return this;
        }

        /**
         * DESC: 设置网络请求接口
         * Created by jinphy, on 2017/12/31, at 17:03
         */
        public Builder<T> path(String path) {
            ObjectHelper.requireNonNull(path, "requestId cannot be null!");
            this.path = path;
            return this;
        }

        /**
         * DESC: 设置网络请求头
         * Created by jinphy, on 2017/12/31, at 16:14
         */
        public Builder<T> headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        /**
         * DESC: 设置网络请求参数
         * Created by jinphy, on 2017/12/31, at 16:15
         */
        public Builder<T> params(Params params) {
            this.params = params;
            return this;
        }

        /**
         * DESC: 设置连接超时
         * Created by jinphy, on 2018/1/2, at 16:42
         */
        public Builder<T> connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * DESC: 设置读取超时
         * Created by jinphy, on 2018/1/2, at 16:43
         */
        public Builder<T> readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        @SuppressWarnings("all")
        public Request<T> build(){
            try {
                Request<T> request = null;
                String url;
                Method method = Request.method(path);
                setupHeaders(method.get());
                switch (method) {
                    case GET:
                        url = StringUtils.generateURI(baseUrl, port, path, params.toString());
                        request = new Request<T>(url, headers, connectTimeout,readTimeout);
                        request.method = method;
                        break;
                    case POST:
                        url = StringUtils.generateURI(baseUrl, port, path, "");
                        request = new Request<T>(url, headers, connectTimeout,readTimeout);
                        request.method = method;
                        request.body = Body.create(requestId, params);
                        break;
                    default:
                        break;
                }
                return request;
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return null;
            }
        }

        private void setupHeaders(String method) {
            headers.put("method", method);
            headers.put("requestId", requestId);
        }


    }

    public enum Method{
        /**
         * DESC: 网络请求方法：POST
         * Created by jinphy, on 2017/12/31, at 19:07
         */
        POST("POST"),

        /**
         * DESC: 网络请求方法: GET
         * Created by jinphy, on 2017/12/31, at 19:07
         */
        GET("GET");

        private String value;
        Method(String value) {
            this.value = value;
        }

        public String get() {
            return value;
        }
    }

    /**
     * DESC: 根据网络请求接口获取请求方法
     * Created by jinphy, on 2017/12/31, at 19:30
     */
    public static Method method(@NonNull  String path) {
        ObjectHelper.requireNonNull(path, "requestId cannot be null!");
        try {
            for (Field field : Api.Path.class.getDeclaredFields()) {
                if (field.getType() == String.class) {
                    field.setAccessible(true);
                    String value = (String) field.get(null);
                    if (path.equals(value)) {
                        if (field.isAnnotationPresent(Post.class)) {
                            Log.e(TAG, "method: POST");
                            return Method.POST;
                        }
                        if (field.isAnnotationPresent(Get.class)) {
                            Log.e(TAG, "method: GET");
                            return Method.GET;
                        }
                        ObjectHelper.throwRuntime("The field:"+field+" Class "+ Api.Path.class.getName()+" must be annotated with Post or Get!");
                    }
                } else {
                    ObjectHelper.throwRuntime(Api.Path.class.getName()+" can only has the String field!");
                }
            }
            ObjectHelper.throwRuntime("The requestId = "+path+" isn't declared in "+ Api.Path.class.getName());
        } catch (IllegalAccessException e) {
            ObjectHelper.throwRuntime("illegalAccessException: " + e.getMessage());
        }
        return null;
    }

}
