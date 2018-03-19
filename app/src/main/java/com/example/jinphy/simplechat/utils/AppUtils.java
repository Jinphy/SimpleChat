package com.example.jinphy.simplechat.utils;

import android.content.pm.ApplicationInfo;

import com.example.jinphy.simplechat.base.BaseApplication;

/**
 * DESC:
 * Created by jinphy on 2017/12/31.
 */

public class AppUtils {



    /**
     * DESC: 获取当前APP的版本， 例如：3.0.3
     * Created by Jinphy, on 2017/12/25, at 17:26
     */
    private static String getAppVersion() {
        if (BaseApplication.app() == null) {
            return "";
        }
        try {
            return BaseApplication.app()
                    .getPackageManager()
                    .getPackageInfo(BaseApplication.app().getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * DESC: 判断当前运行版本是否是debug版本
     * Created by Jinphy, on 2017/12/28, at 16:32
     */
    public static boolean debug() {
        try {
            ApplicationInfo info = BaseApplication.app().getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
