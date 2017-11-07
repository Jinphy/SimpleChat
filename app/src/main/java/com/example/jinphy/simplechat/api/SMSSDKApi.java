package com.example.jinphy.simplechat.api;

import android.content.Context;

import com.example.jinphy.simplechat.R;
import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by jinphy on 2017/11/5.
 */

public class SMSSDKApi {

    private static final String TAG = "SMSSDKApi";
    public static String countryChina = "86";

    public EventHandler eventHandler;
    private Consumer getVerificationCodeCallback;
    private Consumer submitVerificationCodeCallback;
    private static SMSSDKApi instance;

    public static synchronized SMSSDKApi getInstance(){
        if (instance == null) {
            instance = new SMSSDKApi();
        }
        return instance;
    }


    private SMSSDKApi() {
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int eventCode, int resultCode, Object data) {
                switch (eventCode) {
                    case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                        if (getVerificationCodeCallback != null) {
                            if (resultCode == SMSSDK.RESULT_ERROR) {
                                getVerificationCodeCallback.accept(new Response(Response.no));
                            } else {
                                getVerificationCodeCallback.accept(new Response(Response.yes));
                            }
                        }
                        break;
                    case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                        if (submitVerificationCodeCallback != null) {
                            if (resultCode == SMSSDK.RESULT_ERROR) {
                                submitVerificationCodeCallback.accept(new Response(Response.no));
                            } else {
                                submitVerificationCodeCallback.accept(new Response(Response.yes));
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public void getVerificationCode(String phone,Consumer callback) {
        getVerificationCodeCallback = callback;
        SMSSDK.getVerificationCode(countryChina, phone);
    }

    public void submitVerificationCode(String phone, String verificationCode,Consumer callback) {
        submitVerificationCodeCallback = callback;
        SMSSDK.submitVerificationCode(countryChina, phone, verificationCode);
    }


    public void register(Context context) {
        String appKey = context.getString(R.string.app_key);
        String appSecret = context.getString(R.string.app_secret);
        MobSDK.init(context, appKey, appSecret);
        SMSSDK.registerEventHandler(eventHandler);
    }

    public void unregister() {
        SMSSDK.unregisterEventHandler(eventHandler);
    }

}
