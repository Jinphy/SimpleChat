package com.example.jinphy.simplechat.api;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.PopupWindow;


import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.custom_view.LoadingDialog;
import com.example.jinphy.simplechat.exceptions.ServerException;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.example.jinphy.simplechat.utils.PopupWindowUtils;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * description: 一个泛型的网络请求回调观察者
 *
 *  泛型T：
 *      1、当网络请求是单挑接口时，则T 为Response<U>
 *
 *      2、当网络请求是多条接口合并时，则T 为Response[] 数组
 *
 * Created by Jinphy on 2017/11/27.
 */

class ApiSubscriber<T> implements Observer<T> {

    protected Disposable disposable;
    /*请求API*/
    protected BaseApi api;
    /*回调接口*/
    protected ApiCallback<T> apiCallback;
    // 所引用防止内存泄漏
    protected WeakReference<Context> context;
    /*加载框可自己定义*/
    protected Dialog dialog;
    private static final String TAG = "ApiSubscriber";

    public static <U>ApiSubscriber<U> newInstance(BaseApi<U> api, Context context) {
        return new ApiSubscriber<>(api, context);
    }

    /**
     * 构造
     *
     * @param api
     */
    protected ApiSubscriber(BaseApi<T> api, Context context) {
        this.api = api;
        this.apiCallback = api;
        this.context = new WeakReference<>(context);
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
        if (ObjectHelper.reference(context)) {
            ((Activity) context.get()).runOnUiThread(() -> {
                showDialog();
                apiCallback.doOnStart();
            });
        }
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param response 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T response) {
        apiCallback.doOnResponse(response);
        LogUtils.e("网络请求成功：\n\n" +
                "[ Url      ]: "+api.url()+"\n\n" +
                "[ Response ]: "+ GsonUtils.toJson(response));
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        this.apiCallback.doOnError(e);
        this.apiCallback.doOnFinal();
        dismissDialog();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
        if (e instanceof SocketTimeoutException ||
                e instanceof ConnectException ||
                e instanceof UnknownHostException) {
            BaseApplication.showToast("网络连接异常，请检查网络是否连接！", false);
        } else if (e instanceof ServerException) {
            BaseApplication.showToast(e.getMessage(), false);
        } else {
            e.printStackTrace();
        }
        LogUtils.e("网络请求异常：\n\n" +
                "[ Url      ]: "+api.url()+"\n\n" +
                "[ Error    ]: "+e);

        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        apiCallback.doOnFinal();
        dismissDialog();
    }


    /**
     * 显示加载框
     */
    private void showDialog() {
        if (this.api.showProgress&&ObjectHelper.reference(context)) {
            if (dialog == null) {
                dialog = LoadingDialog.builder(context.get())
                        .cancellable(this.api.cancellable)
                        .title(api.hint)
                        .onCancel(dialog -> {
                            this.disposable.dispose();    // 取消RxJava订阅
                            this.apiCallback.doOnCancel();// 执行取消回调
                            this.apiCallback.doOnFinal(); // 执行网络请求结束回调
                        })
                        .build();

            }
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }
    }


    /**
     * 关闭对话框
     */
    private void dismissDialog() {
        if (api.showProgress&& dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
