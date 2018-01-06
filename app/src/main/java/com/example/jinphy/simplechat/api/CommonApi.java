package com.example.jinphy.simplechat.api;

import android.content.Context;
import android.text.TextUtils;

import com.example.jinphy.simplechat.utils.DialogUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;

import io.reactivex.Observable;

/**
 * DESC: 一个泛型的通用Api请求类。
 *
 *      泛型T:为Response类中data字段的类型
 * Created by jinphy on 2017/12/4.
 */

class CommonApi<T> extends BaseApi<Response<T>>{


    protected Request<T> request;                        // 网络请求对象

    //====================方法===============================================

    /**
     * DESC: 创建一个网络请求api
     * Created by jinphy, on 2017/12/4, at 22:17
     */
    public static<U> ApiInterface<Response<U>> create(Context context) {
        return new CommonApi<>(context);
    }


    @Override
    public<U> ApiInterface<Response<T>> api(ApiInterface<U> api) {
        ObjectHelper.throwRuntime("CommonApi cannot invoke this method!");
        return null;
    }

    /**
     * DESC: 私有化构造函数
     * Created by jinphy, on 2017/12/4, at 22:17
     */
    private CommonApi(Context context) {
        super(context);
    }

    @Override
    public void doCheckResponse(Response<T> response) {
        if (Response.YES.equals(response.getCode())) {
            if (this.onResponseYes != null) {
                this.onResponseYes.call(response);
            }
        } else {
            if (this.onResponseNo != null) {
                this.onResponseNo.call(response);
            }
            if (autoShowNo) {
                DialogUtils.showResponseNo(response, context);
            }
        }
    }

    /**
     * DESC: 创建一个网络请求
     * Created by jinphy, on 2017/12/31, at 21:52
     */
    private Request<T> getRequest(){
        request = Request.<T>newBuilder(responseType, dataType)
                .baseUrl(baseUrl)
                .port(port)
                .path(path)
                .connectTimeout(connectTimeout)
                .readTimeout(readTimeout)
                .headers(headers)
                .params(params)
                .build();
        return request;
    }

    @Override
    public Observable<Response<T>> getObservable() {
        if (TextUtils.isEmpty(path)) {
            throw new RuntimeException("请求路劲接口不能为空，必须调用CommonApi.requestId(String requestId) 设置请求接口");
        }

        return Observable.create(getRequest());
    }

    @Override
    public void cancel() {
        if (request != null && !request.isClosed() && !request.isClosing()) {
            request.close();
        }
    }

    @Override
    public String url() {
        return request.url();
    }
}
