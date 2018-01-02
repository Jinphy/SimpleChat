package com.example.jinphy.simplechat.api;

import android.Manifest;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.text.TextUtils;

import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.custom_libs.RuntimePermission;

import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jinphy on 2017/12/4.
 */

abstract class BaseApi<T> implements ApiCallback<T>, ApiInterface<T> {

    //==================属性====================================================\\

    protected static ExecutorService threadPool = Executors.newCachedThreadPool();

    protected Context context;
    protected Map<String,String> headers;             // 请求头
    protected Params params;                          // 请求参数
    protected boolean showProgress;                   // 是否显示请求请求对话框
    protected boolean cancellable;                    // 是否可以取消
    protected String baseUrl;                         // 基础url
    protected String port;                            // 网络请求端口
    protected int connectTimeout;                     // 单位：毫秒
    protected int readtimeout;                        // 读取超时，单位：毫秒
    protected String path;                            // 网络请求接口
    protected CharSequence hint = "加载中...";         // 加载时对话框的提示信息
    protected boolean autoShowNo = true;              // 当返回码错误时，是否自动显示对话框，默认显示

    protected ApiCallback.OnStart onStart;            // 请求开始时回调，请求开始前回调，一定会回调
    protected ApiCallback.OnResponse<T> onResponse;      // 请求成功时回调
    protected ApiCallback.OnResponseYes<T> onResponseYes;// 请求成功并且状态码正确时回调
    protected ApiCallback.OnResponseNo<T> onResponseNo;  // 请求成功但是状态吗错误时回调
    protected ApiCallback.OnError onError;            // 请求错误时回调
    protected ApiCallback.OnCancel onCancel;          // 请求取消时回调
    protected ApiCallback.OnFinal onFinal;            // 请求结束时回调，该回调与OnStart对应，一定会回调

    //====================方法===============================================

    /*
     * DESC: 私有化构造函数
     * Created by jinphy, on 2017/12/4, at 22:17
     */
    protected BaseApi(Context context) {
        this.context = context;
        init();
    }

    /*
     * DESC: 初始化参数
     * Created by jinphy, on 2017/12/4, at 22:17
     */
    protected void init() {
        this.showProgress = false;
        this.params = Params.newInstance();
        this.headers = new HashMap<>();
        this.baseUrl = Api.BASE_URL;
        this.port = Api.COMMON_PORT;
        this.connectTimeout = 10_000;
        this.readtimeout = 10_000;
    }

    /**
     * DESC: 设置baseUrl
     * Created by jinphy, on 2017/12/4, at 22:08
     */
    @CallSuper
    public ApiInterface<T> baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * DESC: 设置请求端口
     * Created by jinphy, on 2017/12/4, at 22:08
     */
    public ApiInterface<T> port(String port) {
        this.port = port;
        return this;
    }

    /**
     * DESC: 设置接口路径
     * Created by jinphy, on 2017/12/4, at 23:38
     */
    @Override
    public ApiInterface<T> path(String path) {
        this.path = path;
        return this;
    }

    /**
     * DESC: 设置header
     * Created by jinphy, on 2017/12/4, at 22:28
     */
    public ApiInterface<T> header(String key, String value) {
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
    public ApiInterface<T> param(String key, Object value) {
        this.params.put(key, value.toString());
        return this;
    }

    /**
     * DESC: 设置连接超时
     * Created by jinphy, on 2017/12/4, at 22:34
     */
    @Override
    public ApiInterface<T> connectTimeout(int timeout) {
        this.connectTimeout = timeout;
        return this;
    }


    /**
     * DESC: 设置读取超时
     * Created by jinphy, on 2017/12/4, at 22:34
     */
    @Override
    public ApiInterface<T> readTimeout(int timeout) {
        this.readtimeout = timeout;
        return this;
    }

    /**
     * DESC: 设置网络请求是显示进度条
     * Created by jinphy, on 2017/12/4, at 21:47
     */
    @Override
    public ApiInterface<T> showProgress(boolean... show) {
        if (show.length > 0 && !show[0]) {
            this.showProgress = false;
        } else {
            this.showProgress = true;
        }
        return this;
    }

    /**
     * DESC: 当返回码错误时，是否自动显示对话框，默认显示
     * Created by jinphy, on 2018/1/2, at 20:13
     */
    @Override
    public ApiInterface<T> autoShowNo(boolean showNo) {
        this.autoShowNo = showNo;
        return this;
    }

    /**
     * DESC: 设置网络请求是否可以取消
     *
     * @param cancel 可变参数，不传则默认可以取消，多传则除了第一个参数其他的将被忽略
     *
     * Created by jinphy, on 2018/1/1, at 9:00
     */
    @Override
    public ApiInterface<T> cancellable(boolean... cancel) {
        if (cancel.length > 0 && !cancel[0]) {
            this.cancellable = false;
        } else {
            this.cancellable = true;
        }
        return this;
    }

    @Override
    public ApiInterface<T> hint(CharSequence hint) {
        this.hint = hint;
        return this;
    }

    //=================回调接口============================================================

    /**
     * DESC: 设置成功回调
     * Created by jinphy, on 2017/12/4, at 21:58
     */
    public ApiInterface<T> onResponse(ApiCallback.OnResponse<T> onResponse) {
        this.onResponse = onResponse;
        return this;
    }

    @Override
    public ApiInterface<T> onResponseYes(ApiCallback.OnResponseYes<T> onResponseYes) {
        this.onResponseYes = onResponseYes;
        return this;
    }

    @Override
    public ApiInterface<T> onResponseNo(ApiCallback.OnResponseNo<T> onResponseNo) {
        this.onResponseNo = onResponseNo;
        return this;
    }

    /**
     * DESC: 设置异常回调
     * Created by jinphy, on 2017/12/4, at 21:59
     */
    public ApiInterface<T> onError(ApiCallback.OnError onError) {
        this.onError = onError;
        return this;
    }

    /**
     * DESC: 设置取消回调
     * Created by jinphy, on 2018/1/1, at 8:52
     */
    @Override
    public ApiInterface<T> onCancel(OnCancel onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    /**
     * DESC: 设置开始回调
     * Created by jinphy, on 2017/12/4, at 23:24
     */
    public ApiInterface<T> onStart(ApiCallback.OnStart onStart ) {
        this.onStart = onStart;
        return this;
    }

    /**
     * DESC: 设置关闭回调
     * Created by jinphy, on 2017/12/4, at 22:01
     */
    public ApiInterface<T> onFinal(ApiCallback.OnFinal onFinal) {
        this.onFinal = onFinal;
        return this;
    }

    @Override
    public void doOnStart() {
        if (this.onStart != null) {
            this.onStart.call();
        }
    }

    @Override
    public void doOnResponse(T response) {
        if (this.onResponse != null) {
            this.onResponse.call(response);
        }
        this.doCheckResponse(response);
    }

    @Override
    public void doOnCancel() {
        if (this.onCancel != null) {
            this.onCancel.call();
        }
        this.cancel();            // 取消网络请求
    }

    @Override
    public void doOnError(Throwable e) {
        if (this.onError != null) {
            this.onError.call(e);
        }
    }

    @Override
    public void doOnFinal() {
        if (this.onFinal != null) {
            this.onFinal.call();
        }
    }


    /**
     * DESC: 执行网络请求
     * Created by jinphy, on 2017/12/4, at 22:19
     */
    @Override
    public void request() {
        RuntimePermission.getInstance(BaseApplication.activity())
                .permission(Manifest.permission.READ_PHONE_STATE)
                .onGranted(()->{
                    threadPool.execute(()->{
                        threadPool.execute(()->{
                            getObservable()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(ApiSubscriber.newInstance(this, context));
                        });
                    });
                })
                .onReject(()->{
                    BaseApplication.showToast("您拒绝了读取设备状态的相关权限！", false);
                })
                .execute();

    }

    abstract protected Observable<T> getObservable();

    abstract public void cancel();

    abstract public String url();
}