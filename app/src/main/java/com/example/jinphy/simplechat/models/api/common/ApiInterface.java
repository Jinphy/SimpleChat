package com.example.jinphy.simplechat.models.api.common;

import java.util.Map;

/**
 * Created by Jinphy on 2017/12/6.
 */

public interface ApiInterface<T> {

    /**
     * DESC: 设置baseUrl
     * Created by jinphy, on 2017/12/4, at 22:08
     */
    ApiInterface<T> baseUrl(String baseUrl);

    /**
     * DESC: 设置请求端口
     * Created by jinphy, on 2017/12/4, at 22:08
     */
    ApiInterface<T> port(String port);

    /**
     * DESC: 设置接口路径
     * Created by jinphy, on 2017/12/4, at 23:38
     */
    ApiInterface<T> path(String path);

    /**
     * DESC: 设置header
     * Created by jinphy, on 2017/12/4, at 22:28
     */
    ApiInterface<T> header(String key, String value);

    /**
     * DESC: 添加请求参数
     * Created by jinphy, on 2017/12/4, at 21:43
     */
    ApiInterface<T> param(String key, Object value);

    ApiInterface<T> params(Map<String, Object> params);

    /**
     * DESC: 添加网络请求
     * Created by jinphy, on 2018/1/1, at 9:56
     */
    <U>ApiInterface<T> api(ApiInterface<U> api);

    /**
     * DESC: 设置连接超时
     * Created by jinphy, on 2017/12/4, at 22:34
     */
    ApiInterface<T> connectTimeout(int timeout);



    /**
     * DESC: 设置读取超时
     * Created by jinphy, on 2017/12/4, at 22:34
     */
    ApiInterface<T> readTimeout(int timeout);

    /**
     * DESC: 设置网络请求是显示进度条
     * Created by jinphy, on 2017/12/4, at 21:47
     */
    ApiInterface<T> showProgress(boolean...show);




    /**
     * DESC: 设置当返回码错误时是否自动显示对话框，默认显示
     * Created by jinphy, on 2017/12/4, at 22:34
     */
    ApiInterface<T> autoShowNo(boolean showNo);


    /**
     * DESC: 设置网络请求是否可以取消
     * Created by jinphy, on 2018/1/1, at 8:58
     */
    ApiInterface<T> cancellable(boolean... cancel);


    /**
     * DESC: 设置是否缓存，默认不缓存
     * Created by jinphy, on 2018/1/6, at 13:14
     */
    ApiInterface<T> useCache(boolean... useCache);

    /**
     * DESC: 设置加载时对话框的提示信息
     * Created by jinphy, on 2018/1/1, at 8:58
     */
    ApiInterface<T> hint(CharSequence hint);

    /**
     * DESC: 设置成功回调
     * Created by jinphy, on 2017/12/4, at 21:58
     */
    ApiInterface<T> onResponse(ApiCallback.OnResponse<T> onResponse);


    /**
     * DESC: 设置网络请求返回结果中{@code data }字段的类型
     *      注意，该方法在网络请求不需要使用{@code data} 字段时，可以不用设置
     *      但是，如果要用到该字段时，则必须调用该方法显示设置{@code data} 的返回类型，
     *      否则将会出错
     *
     *  @see Response#data
     *  @see Api.Data ，该函数的详细用法请参考{@code Data} 枚举
     * Created by jinphy, on 2018/1/5, at 13:52
     */
    ApiInterface<T> dataType(Api.Data dataType, Class<?>...dataClass);


    /**
     * DESC: 设置请求成功并且返回码正确时回调
     * <p>
     * Created by jinphy, on 2017/12/31, at 15:09
     */
    ApiInterface<T> onResponseYes(ApiCallback.OnResponseYes<T> onResponseYes);

    /**
     * DESC: 设置请求成功但是返回码错误时回调
     * Created by jinphy, on 2017/12/31, at 15:11
     */
    ApiInterface<T> onResponseNo(ApiCallback.OnResponseNo<T> onResponseNo);

    /**
     * DESC: 设置异常回调
     * Created by jinphy, on 2017/12/4, at 21:59
     */
    ApiInterface<T> onError(ApiCallback.OnError onError);

    /**
     * DESC: 设置开始回调
     * Created by jinphy, on 2017/12/4, at 22:00
     */
    ApiInterface<T> onStart(ApiCallback.OnStart onStart);

    /**
     * DESC: 设置关闭回调
     * Created by jinphy, on 2017/12/4, at 22:01
     */
    ApiInterface<T> onFinal(ApiCallback.OnFinal onFinal);

    /**
     * DESC: 设置取消时回调
     * Created by jinphy, on 2018/1/1, at 8:50
     */
    ApiInterface<T> onCancel(ApiCallback.OnCancel onCancel);

    ApiInterface<T> setup(ApiCallback.Setup<ApiInterface<T>> action);

    void request();
}
