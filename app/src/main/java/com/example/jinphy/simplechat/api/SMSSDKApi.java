package com.example.jinphy.simplechat.api;

import android.content.Context;
import android.util.Log;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * 包可见
 * Created by jinphy on 2017/11/5.
 */

class SMSSDKApi implements ApiInterface<Void> {

    private static final String TAG = "SMSSDKApi";
    private static String countryChina = "86";

    private Context context;
    private EventHandler eventHandler;
    protected Api.OnNext onNext;
    protected String phone;
    protected String verificationCode;
    protected String path;
    protected boolean showProgress;

    /**
     * DESC: 创建对象
     * Created by Jinphy, on 2017/12/6, at 13:00
     */
    public static ApiInterface create(Context context) {
        return new SMSSDKApi(context);
    }

    protected SMSSDKApi(Context context) {
        this.context = context;
        init();
    }

    protected void init(){
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int eventCode, int resultCode, Object data) {
                unregister();
                context = null;
                Flowable.just(resultCode)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(SMSSDKApi.this::parseResponse)
                        .subscribe();
            }
        };
        register(context);
    }


    /*
     * DESC: 处理网络请求结果
     * Created by jinphy, on 2017/12/6, at 23:43
     */
    protected void parseResponse(int resultCode) {
        if (Api.Path.getVerificationCode.equals(path)) {
            if (onNext != null) {
                if (resultCode == SMSSDK.RESULT_ERROR) {
                    onNext.call(new Api.Response(Api.Response.NO, "获取验证码失败！", null));
                } else {
                    onNext.call(new Api.Response(Api.Response.YES, "验证码已发，请输入！", null));
                }
            }
        } else if (Api.Path.submitVerificationCode.equals(path)) {
            if (onNext != null) {
                if (resultCode == SMSSDK.RESULT_ERROR) {
                    onNext.call(new Api.Response(Api.Response.NO, "验证失败，请输入正确的验证码！", null));
                } else {
                    onNext.call(new Api.Response(Api.Response.YES, "验证成功，请继续！", null));
                }
            }
        }
    }

    /**
     * DESC: 设置手机号码
     * Created by jinphy, on 2017/12/6, at 0:03
     */
    protected void phone(String phone) {
        this.phone = phone;
    }

    /**
     * DESC: 设置验证码
     * Created by jinphy, on 2017/12/6, at 0:04
     */
    protected void verificationCode(String code) {
        this.verificationCode = code;
    }

    protected void register(Context context) {
        SMSSDK.registerEventHandler(eventHandler);
    }

    protected void unregister() {
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    /**
     * DESC: 设置请求参数
     * Created by jinphy, on 2017/12/6, at 0:22
     */
    @Override
    public ApiInterface param(String key, Object value) {
        if (Api.Key.phone.equals(key)) {
            phone(value.toString());
        } else if (Api.Key.verificationCode.equals(key)) {
            verificationCode(value.toString());
        }
        return this;
    }

    /**
     * DESC: 设置是否显示进度条
     * Created by Jinphy, on 2017/12/6, at 9:21
     */
    @Override
    public ApiInterface showProgress() {
        this.showProgress = true;
        return this;
    }


    /**
     * DESC: 设置连接超时
     * Created by jinphy, on 2017/12/4, at 22:34
     */
    @Override
    public ApiInterface connectTimeout(int timeout) {
        // no-op
        return this;
    }

    /**
     * DESC: 设置请求路径
     * Created by jinphy, on 2017/12/6, at 0:05
     */
    @Override
    public ApiInterface path(String path) {
        this.path = path;
        return this;
    }

    @Override
    public ApiInterface onNext(Api.OnNext onNext) {
        this.onNext = onNext;
        return this;
    }

    @Override
    public ApiInterface baseUrl(String baseUrl) {
        // no-op
        return this;
    }

    @Override
    public ApiInterface port(String port) {
        // no-op
        return this;
    }

    @Override
    public ApiInterface header(String key, String value) {
        // no-op
        return this;
    }

    @Override
    public ApiInterface onError(Api.OnError onError) {
        // no-op
        return this;
    }

    @Override
    public ApiInterface onStart(Api.OnStart onStart) {
        // no-op
        return this;
    }

    @Override
    public ApiInterface onOpen(Api.OnOpen onOpen) {
        // no-op
        return this;
    }

    @Override
    public ApiInterface onClose(Api.OnClose onClose) {
        // no-op
        return this;
    }

    /**
     * DESC: 请求网络
     * Created by jinphy, on 2017/12/6, at 0:06
     */
    public Void request() {
        ObjectHelper.requareNonNull(path, "请设置网络请求接口！");
        ObjectHelper.requareNonNull(phone, "请设置手机号码！");
        ObjectHelper.requareNonNull(onNext, "请设置回调接口！");
        if (Api.Path.getVerificationCode.equals(path)) {
            Log.e(TAG, "request: getVerificationCode");
            SMSSDK.getVerificationCode(countryChina, phone);
        } else if (Api.Path.submitVerificationCode.equals(path)) {
            Log.e(TAG, "request: submitVerificationCode");
            ObjectHelper.requareNonNull(verificationCode, "请设置验证码！");
            SMSSDK.submitVerificationCode(countryChina, phone, verificationCode);
        } else {
            throw new RuntimeException("请设置正确的请求路径！");
        }
        return null;
    }
}
