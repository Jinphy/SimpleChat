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

    /**
     * DESC: 获取设备的IMEI，需要运行时权限
     *
     *  权限：Manifest.permission.READ_PHONE_STATE
     *
     * Created by jinphy, on 2017/12/31, at 13:28
     */
    @SuppressLint("MissingPermission")
    public static String devceId(){
        String IMEI = null;
        Activity activity = BaseApplication.activity();
        if (activity != null) {
            TelephonyManager telephonyManager=(TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            IMEI=telephonyManager.getDeviceId();
        }
        return IMEI;
    }


    /**
     * DESC: 获取设备的IMEI并使用md5加密，需要运行时权限
     *
     *  权限：Manifest.permission.READ_PHONE_STATE
     *
     * Created by jinphy, on 2017/12/31, at 13:28
     */
    @SuppressLint("MissingPermission")
    public static String devceIdMD5(){
        return EncryptUtils.md5(devceId());
    }

    /**
     * DESC: 获取手机号码，需要运行时权限
     *
     *  权限：Manifest.permission.READ_PHONE_NUMBERS
     * Created by jinphy, on 2017/12/31, at 13:32
     */
    @SuppressLint("MissingPermission")
    public static String phone(){
        String phone = null;

        Activity activity = BaseApplication.activity();
        if (activity != null) {
            TelephonyManager telephonyManager=(TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            phone=telephonyManager.getLine1Number();
        }
        return phone;
    }


}
