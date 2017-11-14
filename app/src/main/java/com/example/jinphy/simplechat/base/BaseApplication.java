package com.example.jinphy.simplechat.base;

import android.app.Application;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import io.reactivex.annotations.NonNull;


/**
 * Created by jinphy on 2017/8/18.
 */

public class BaseApplication extends Application {

    private static BaseApplication INSTANCE;
    private static Toast toast;
    private static boolean DEBUG = true;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        initToast();
    }

    private static void initToast() {
        toast = Toast.makeText(INSTANCE, "", Toast.LENGTH_SHORT);
    }
    public static BaseApplication instance(){
        return INSTANCE;
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


    /**
     * log日志，等级为V
     */
    public  void v(String tag, @NonNull Object msg) {
        if (DEBUG) {
            Log.v(tag, msg.toString());
        }
    }

    /**
     * log日志，等级为D
     * */
    public void d(String tag, @NonNull Object msg) {
        if (DEBUG) {
            Log.d(tag, msg.toString());
        }
    }


    /**
     * log日志，等级为I
     * */
    public  void i(String tag, @NonNull Object msg) {
        if (DEBUG) {
            Log.i(tag, msg.toString());
        }
    }


    /**
     * log日志，等级为W
     * */
    public  void w(String tag, @NonNull Object msg) {
        if (DEBUG) {
            Log.w(tag, msg.toString());
        }
    }


    /**
     * log日志，等级为E
     * */
    public  void e(String tag, @NonNull Object msg) {
        if (DEBUG) {
            Log.e(tag, msg.toString());
        }
    }

}
