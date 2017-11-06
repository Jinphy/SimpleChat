package com.example.jinphy.simplechat.repositories.smssdk;

import android.content.Context;
import android.util.Log;

import com.example.jinphy.simplechat.R;
import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by jinphy on 2017/11/5.
 */

public class SMSSDKRepository {

    private static final String TAG = "SMSSDKRepository";
    public static String countryChina = "86";

    public EventHandler eventHandler;

    public SMSSDKRepository(Callback callback) {
        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int eventCode, int resultCode, Object data) {

                callback.afterEvent(eventCode, resultCode, data);
            }
        };
    }

    public void getVerificationCode(String phone) {
        SMSSDK.getVerificationCode(countryChina, phone);
    }

    public void submitVerificationCode(String phone, String verificationCode) {
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

    public interface Callback {
        void afterEvent(int eventCode, int resultCode, Object data);
    }
}
