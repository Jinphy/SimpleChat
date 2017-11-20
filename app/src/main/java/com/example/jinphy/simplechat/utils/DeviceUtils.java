package com.example.jinphy.simplechat.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.example.jinphy.simplechat.base.BaseApplication;

/**
 * Created by jinphy on 2017/11/19.
 */

public class DeviceUtils {

    @SuppressLint("MissingPermission")
    public static String devceId(){
        String IMEI = null;

        Activity activity = BaseApplication.currentActivity();
        if (activity != null) {
            if (PermissionUtils.has(activity,"android.permission.READ_PHONE_STATE")) {
                TelephonyManager telephonyManager=(TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
                IMEI=telephonyManager.getDeviceId();
            }
        }
        return IMEI;
    }

    @SuppressLint("MissingPermission")
    public static String phone(){
        String phone = null;

        Activity activity = BaseApplication.currentActivity();
        if (activity != null) {
            if (PermissionUtils.has(activity,"android.permission.READ_PHONE_STATE")) {
                TelephonyManager telephonyManager=(TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
                phone=telephonyManager.getLine1Number();
            }
        }
        return phone;
    }


}
