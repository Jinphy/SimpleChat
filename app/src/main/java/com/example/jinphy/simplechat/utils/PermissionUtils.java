package com.example.jinphy.simplechat.utils;

import android.app.Activity;

import com.tbruyelle.rxpermissions2.RxPermissions;


/**
 * Created by jinphy on 2017/11/5.
 */


public class PermissionUtils {

    private PermissionUtils(){}


    public static boolean has(Activity activity, String... permissions) {
        final Result result = new Result();
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(permissions)
                .subscribe(granted-> {
                    result.result = granted;
                });
        return result.result;
    }

    private static class Result{
        boolean result;
    }
}
