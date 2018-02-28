package com.example.jinphy.simplechat.models.api.common;

import android.content.Context;

import com.example.jinphy.simplechat.utils.DialogUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * DESC:
 * Created by jinphy on 2018/1/1.
 */

class ZipApi<T> extends BaseApi<Response<T>[]> {

    private List<CommonApi<T>> apis;

    public static <U> ApiInterface<Response<U>[]> create(Context context) {
        return new ZipApi<>(context);
    }

    @Override
    public<U> ApiInterface<Response<T>[]> api(ApiInterface<U> api) {
        if (!(api instanceof CommonApi)) {
            ObjectHelper.throwRuntime("You can only pass in the instance of CommonApi as the parameter!");
        }
        this.apis.add((CommonApi<T>) api);
        return null;
    }

    protected ZipApi(Context context) {
        super(context);
        apis = new ArrayList<>(7);
    }

    /**
     * DESC: 检测网络请求结果，分流回调结果
     *
     * 1、如果返回码正确：200 则回调OnResponseYes
     *
     * 2、如果返回码错误，则回调OnResponseNo
     *
     * 3、由于这里是多条Api合并请求，所有只有在所有的Api请求的返回码都正确时才会回调OnResponseYes
     *      否则回调OnResponseNo
     * Created by jinphy, on 2018/1/1, at 13:36
     */
    @Override
    public void doCheckResponse(Response<T>[] response) {
        if (this.onResponseYes == null && this.onResponseNo == null) {
            return;
        }
        for (Response<T> item : response) {
            if (!Response.YES.equals(item.getCode())) {
                response[0] = item;
                if (this.onResponseNo != null) {
                    this.onResponseNo.call(response);
                }
                if (autoShowNo) {
                    DialogUtils.showResponseNo(response[0], context);
                }
                return;
            }
        }
        if (this.onResponseYes != null) {
            this.onResponseYes.call(response);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    protected Observable<Response<T>[]> getObservable() {
        if (this.apis.size() < 2) {
            ObjectHelper.throwRuntime("The ZipApi accept at least two api for zipping!");
        }

        checkCondition();

        CommonApi<T>[] apis = this.apis.toArray(new CommonApi[this.apis.size()]);
        Observable<Response<T>[]> observable = null;
        switch (apis.length) {
            case 2:
                observable = Observable.zip(
                        apis[0].getObservable(),
                        apis[1].getObservable(),
                        (response1, response2) -> new Response[]{response1,response2});
                break;
            case 3:
                observable = Observable.zip(
                        apis[0].getObservable(),
                        apis[1].getObservable(),
                        apis[2].getObservable(),
                        (r1, r2, r3) -> new Response[]{r1, r2, r3});
                break;
            case 4:
                observable = Observable.zip(
                        apis[0].getObservable(),
                        apis[1].getObservable(),
                        apis[2].getObservable(),
                        apis[3].getObservable(),
                        (r1, r2, r3, r4) -> new Response[]{r1, r2, r3, r4});
                break;
            case 5:
                observable = Observable.zip(
                        apis[0].getObservable(),
                        apis[1].getObservable(),
                        apis[2].getObservable(),
                        apis[3].getObservable(),
                        apis[4].getObservable(),
                        (r1, r2, r3, r4, r5) -> new Response[]{r1, r2, r3, r4, r5});
                break;
            case 6:
                observable = Observable.zip(
                        apis[0].getObservable(),
                        apis[1].getObservable(),
                        apis[2].getObservable(),
                        apis[3].getObservable(),
                        apis[4].getObservable(),
                        apis[5].getObservable(),
                        (r1, r2, r3, r4, r5, r6) -> new Response[]{r1, r2, r3, r4, r5, r6});
                break;
            default:
                ObjectHelper.throwRuntime("网络请求压缩太多了！");
                break;
        }

        // 返回压缩后的Observable
        return observable;
    }

    /**
     * DESC: 执行网络请求前的检测工作
     * Created by jinphy, on 2018/1/1, at 13:34
     */
    private void checkCondition() {
        // 添加共有参数到每条Api中
        if (!params.isEmpty()) {
            for (CommonApi<T> api : this.apis) {
                api.params.putAll(this.params);
            }
        }
    }

    @Override
    public void cancel() {
        for (CommonApi<T> api : this.apis) {
            api.cancel();
        }
    }

    @Override
    public String url() {
        return "";
    }
}
