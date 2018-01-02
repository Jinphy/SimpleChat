package com.example.jinphy.simplechat.api;

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

    void request();
}
