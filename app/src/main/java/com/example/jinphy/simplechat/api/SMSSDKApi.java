package com.example.jinphy.simplechat.api;

import android.content.Context;
import android.text.TextUtils;

import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 包可见
 * Created by jinphy on 2017/11/5.
 */

class SMSSDKApi {

    private static final String TAG = "SMSSDKApi";
    private static String countryChina = "86";


    private Context context;
    private EventHandler eventHandler;
    protected Consumer onNext;
    protected String phone;
    protected String verificationCode;
    protected String path;

    public SMSSDKApi(Context context) {
        this.context = context;
        init();
    }

    private void init(){
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int eventCode, int resultCode, Object data) {
                if (CommonApi.Path.getVerificationCode.equals(path)) {
                    if (onNext != null) {
                        if (resultCode == SMSSDK.RESULT_ERROR) {
                            onNext.accept(new Response(Response.NO, "获取验证码失败！", null));
                        } else {
                            onNext.accept(new Response(Response.YES, "验证码已发，请输入！", null));
                        }
                    }
                } else if (CommonApi.Path.submitVerificationCode.equals(path)) {
                    if (onNext != null) {
                        if (resultCode == SMSSDK.RESULT_ERROR) {
                            onNext.accept(new Response(Response.NO, "验证失败，请输入正确的验证码！", null));
                        } else {
                            onNext.accept(new Response(Response.YES, "验证成功！", null));
                        }
                    }
                }
                unregister();
            }
        };
        register(context);
    }

    /**
     * DESC: 设置请求参数
     * Created by jinphy, on 2017/12/6, at 0:22
     */
    public SMSSDKApi param(String key, String value) {
        if (CommonApi.Key.phone.equals(key)) {
            return phone(value);
        } else if (CommonApi.Key.verificationCode.equals(key)) {
            return verificationCode(value);
        }
        return this;
    }

    /**
     * DESC: 设置手机号码
     * Created by jinphy, on 2017/12/6, at 0:03
     */
    protected SMSSDKApi phone(String phone) {
        this.phone = phone;
        return this;
    }

    /**
     * DESC: 设置验证码
     * Created by jinphy, on 2017/12/6, at 0:04
     */
    protected SMSSDKApi verificationCode(String code) {
        this.verificationCode = code;
        return this;
    }

    /**
     * DESC: 设置请求路径
     * Created by jinphy, on 2017/12/6, at 0:05
     */
    public SMSSDKApi path(String path) {
        this.path = path;
        return this;
    }

    /**
     * DESC: 请求网络
     * Created by jinphy, on 2017/12/6, at 0:06
     */
    public void request() {
        ObjectHelper.requareNonNull(path, "请设置网络请求接口！");
        ObjectHelper.requareNonNull(phone, "请设置手机号码！");
        ObjectHelper.requareNonNull(onNext, "请设置回调接口！");
        if (CommonApi.Path.getVerificationCode.equals(path)) {
            SMSSDK.getVerificationCode(countryChina, phone);
        } else if (CommonApi.Path.submitVerificationCode.equals(path)) {
            ObjectHelper.requareNonNull(verificationCode, "请设置验证码！");
            SMSSDK.submitVerificationCode(countryChina, phone, verificationCode);
        } else {
            throw new RuntimeException("请设置正确的请求路径！");
        }
    }

    protected void register(Context context) {
        String appKey = context.getString(R.string.app_key);
        String appSecret = context.getString(R.string.app_secret);
        MobSDK.init(context, appKey, appSecret);
        SMSSDK.registerEventHandler(eventHandler);
    }

    protected void unregister() {
        SMSSDK.unregisterEventHandler(eventHandler);
    }

}
