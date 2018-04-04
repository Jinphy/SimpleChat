package com.example.jinphy.simplechat.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.R;
import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseActivity;
import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.group.GroupRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;

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
            return App.app()
                    .getPackageManager()
                    .getPackageInfo(App.app().getPackageName(), 0)
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
