package com.example.jinphy.simplechat.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.listener_adapters.ActivityLiftcycle;
import com.example.jinphy.simplechat.secret.Secret;
import com.example.jinphy.simplechat.services.common_service.aidl.BinderFactory;
import com.example.jinphy.simplechat.utils.AppUtils;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.ObjectHelper;
import com.mob.MobSDK;

import java.lang.ref.WeakReference;

import io.reactivex.annotations.NonNull;


/**
 * Created by jinphy on 2017/8/18.
 */

@SuppressLint("Registered")
public class BaseApplication extends Application implements ActivityLiftcycle {

    private static final String TAG = "BaseApplication";

    private static BaseApplication INSTANCE;
    private static Toast toast;
    private static boolean DEBUG;
    private static WeakReference<Activity> currentActivity=null;

    public int count = 0;

    private static boolean isForeground;


    public static BaseApplication app(){
        return INSTANCE;
    }


    /**
     * 获取当前正在前台的Activity
     * */
    public static BaseActivity activity(){
        if (ObjectHelper.reference(currentActivity)) {
            Activity activity = currentActivity.get();
            if (activity instanceof BaseActivity) {
                return (BaseActivity) activity;
            }
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        DEBUG = AppUtils.debug();
        initToast();
        registerActivityLifecycleCallbacks(this);
//        EventBus.getDefault().register(this);
        String appKey = EncryptUtils.aesDecrypt(Secret.APP_KEY);
        String appSecret = EncryptUtils.aesDecrypt(Secret.APP_SECRET);
        MobSDK.init(this, appKey, appSecret);
        LogUtils.getLogConfig()
                .configAllowLog(AppUtils.debug())
                .configTagPrefix("Jinphy");

        BinderFactory.init(this);
//        EventBus.getDefault().register(this);

    }

    @Override
    public void onTerminate() {
//        EventBus.getDefault().unregister(this);
        super.onTerminate();
    }

    public static void showToast(@NonNull  Object msg, boolean isLong){
        if (toast == null) {
            initToast();
        }

        if (isLong) {
            toast.setDuration(Toast.LENGTH_LONG);
        } else {
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        if (msg instanceof CharSequence) {
            toast.setText((CharSequence) msg);
        } else {
            toast.setText(msg.toString());
        }
        toast.show();
    }

    //------------------log-----------------------------


    /**
     * log日志，等级为V
     */
    public  static void v(String tag, @NonNull Object msg) {
        if (DEBUG) {
            Log.v(tag, msg.toString());
        }
    }

    /**
     * log日志，等级为D
     * */
    public static void d(String tag, @NonNull Object msg) {
        if (DEBUG) {
            Log.d(tag, msg.toString());
        }
    }

    /**
     * log日志，等级为I
     * */
    public static void i(String tag, @NonNull Object msg) {
        if (DEBUG) {
            Log.i(tag, msg.toString());
        }
    }

    /**
     * log日志，等级为W
     * */
    public static   void w(String tag, @NonNull Object msg) {
        if (DEBUG) {
            Log.w(tag, msg.toString());
        }
    }

    /**
     * log日志，等级为E
     * */
    public static void e(String tag, @NonNull Object msg) {
        if (DEBUG) {
            Log.e(tag, msg.toString());
        }
    }

    //------------private--------------------------------

    @SuppressLint("ShowToast")
    private static void initToast() {
        toast = Toast.makeText(INSTANCE, "", Toast.LENGTH_SHORT);
    }


    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof BaseActivity) {
            currentActivity = new WeakReference<>(activity);
        }
        isForeground = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        isForeground = false;
    }

    public static boolean atForeground() {
        return isForeground;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
