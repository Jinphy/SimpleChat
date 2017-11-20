package com.example.jinphy.simplechat.base;

import android.app.Activity;
import android.app.Application;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.example.jinphy.simplechat.model.event_bus.EBActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.SoftReference;

import io.reactivex.annotations.NonNull;


/**
 * Created by jinphy on 2017/8/18.
 */

public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    private static BaseApplication INSTANCE;
    private static Toast toast;
    private static boolean DEBUG = true;
    private static SoftReference<Activity> currentActivity;


    public static BaseApplication instance(){
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        initToast();
        EventBus.getDefault().register(this);
    }

    @Subscribe(priority = 100, threadMode = ThreadMode.BACKGROUND)
    public synchronized void onActivityResumed(EBActivity event) {
        if (!event.resume) {
            return;
        }
        if (currentActivity == null || currentActivity.get() != event.activity) {
            currentActivity = new SoftReference<>(event.activity);
        }
        BaseApplication.e(TAG, "onActivityResumed: resumed activity = "+
                event.activity.getClass().getSimpleName());
    }

    @Subscribe(priority = 100, threadMode = ThreadMode.BACKGROUND)
    public synchronized void onActivityPaused(EBActivity event) {
        if (event.resume) {
            return;
        }
        if (currentActivity != null && currentActivity.get() == event.activity) {
            currentActivity = null;
        }
        BaseApplication.e(TAG, "onActivityResumed: paused activity = "+
                event.activity.getClass().getSimpleName());
    }

    /**
     * 获取当前正在前台的Activity
     * */
    public static Activity currentActivity() {
        if (currentActivity != null) {
            return currentActivity.get();
        }
        return null;
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
        toast.setText(msg.toString());
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
    public static   void i(String tag, @NonNull Object msg) {
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

    private static void initToast() {

        toast = Toast.makeText(INSTANCE, "", Toast.LENGTH_SHORT);
    }

}
