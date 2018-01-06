package com.example.jinphy.simplechat.api;

import android.Manifest;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.text.TextUtils;

import com.example.jinphy.simplechat.api.Api.Data;
import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.custom_libs.RuntimePermission;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 *  泛型类，
 *  1、对于单条网络请求：CommonApi则 T：Response<U>
 *
 *  2、对于多条网络请求：ZipApi则 T：Response<U>[]
 *
 *
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
    // TODO: 2018/1/6 实现缓存
    protected boolean useCache;                       // 设置是否缓存
    protected String baseUrl;                         // 基础url
    protected String port;                            // 网络请求端口
    protected int connectTimeout;                     // 单位：毫秒
    protected int readTimeout;                        // 读取超时，单位：毫秒
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

    protected Type responseType;                      // Response 的具体类型
    protected Data dataType;                           // 枚举，表示response中data字段的类型
    //====================方法===============================================

    /*
     * DESC: 私有化构造函数
     * Created by jinphy, on 2017/12/4, at 22:17
     */
    protected BaseApi(Context context) {
        super();
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
        this.readTimeout = 10_000;

        /**
         * DESC: 这是返回数据类型的默认实现，使用该默认实现则不能访问{@link Response#getData()}方法
         *
         *      注：如果要访问{@code getData()} 方法，则需要在指定具体类型，
         *      @see Data
         *      @see BaseApi#dataType(Data, Class<U>)
         * Created by jinphy, on 2018/1/5, at 14:30
         */
        this.responseType = new TypeToken<Response<T>>(){}.getType();
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

    @Override
    public ApiInterface<T> params(Map<String, Object> params) {
        if (params == null) {
            return this;
        }
        for (Map.Entry<String, Object> param : params.entrySet()) {
            this.params.put(param.getKey(), param.getValue());
        }
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
        this.readTimeout = timeout;
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
    public ApiInterface<T> useCache(boolean... useCache) {
        if (useCache.length == 0 || useCache[0]) {
            this.useCache = true;
        } else {
            this.useCache = false;
        }
        return this;
    }

    @Override
    public ApiInterface<T> hint(CharSequence hint) {
        this.hint = hint;
        return this;
    }

    /**
     * DESC: 在回到时传入当前api设置对象进行额外的统一设置而不影响链式调用
     * Created by jinphy, on 2018/1/6, at 14:06
     */
    @Override
    public ApiInterface<T> setup(Setup<ApiInterface<T>> action) {
        if (action != null) {
            action.call(this);
        }
        return this;
    }

    /**
     * DESC: 根据指定的dataType 设置网络请求结果类Response的泛型类型
     * Created by jinphy, on 2018/1/5, at 15:12
     */
    @Override
    public ApiInterface<T> dataType(Data dataType, Class<?>... dataClass) {
        this.dataType = dataType;
        switch (dataType) {
            case MODEL:// 例如：返回类型为Response<GSUser>
                if (dataClass.length == 0) {
                    ObjectHelper.throwRuntime("You must pass in a class when specifying Data.MODEL");
                }

                responseType = GsonUtils.getType(Response.class, dataClass[0]);
                break;
            case MAP:// 例如：返回类型为Response<Map<String,String>>
                responseType = GsonUtils.getType(
                        Response.class,
                        GsonUtils.getType(Map.class, String.class, String.class)
                );
                break;
            case MODEL_ARRAY:// 例如：返回类型为Response<GSUser[]>
                if (dataClass.length == 0) {
                    ObjectHelper.throwRuntime("You must pass in a class when specifying Data.MODEL_ARRAY");
                }
                if (!dataClass[0].isArray()) {
                    ObjectHelper.throwRuntime("The class must be array then specifying Data.MODEL_ARRAY");
                }
                responseType = GsonUtils.getType(Response.class, dataClass[0]);
                break;
            case MAP_ARRAY:// 例如：返回类型为Response<Map<String,String>[]>
                /**
                 * 先把结果转换成List<Map<String,String>>，然后再在Request类的onMessage方法中拦截转换
                 * @see Request#onMessage(String)
                 * */
                responseType = GsonUtils.getType(
                        Response.class,
                        GsonUtils.getType(
                                List.class,
                                GsonUtils.getType(Map.class, String.class, String.class)
                        )
                );
                // 使用这个方法不能解析成Map数组
                //                responseType = GsonUtils.getType(
                //                        Response.class,
                //                        GsonUtils.getType(Map[].class, String.class, String.class)
                //                );

                break;
            case MODEL_LIST:// 例如：返回类型为Response<List<GSUser>>
                if (dataClass.length == 0) {
                    ObjectHelper.throwRuntime("You must pass in a class when specifying Data.MODEL_LIST");
                }
                responseType = GsonUtils.getType(
                        Response.class,
                        GsonUtils.getType(List.class, dataClass[0])
                );
                break;
            case MAP_LIST:// 例如：返回类型为Response<List<Map<String,String>>>
                responseType = GsonUtils.getType(
                        Response.class,
                        GsonUtils.getType(
                                List.class,
                                GsonUtils.getType(Map.class, String.class, String.class)
                        )
                );
                break;
            default:
                break;
        }
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
