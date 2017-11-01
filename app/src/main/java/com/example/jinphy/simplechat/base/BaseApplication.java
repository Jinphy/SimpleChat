package com.example.jinphy.simplechat.base;

import android.app.Application;


/**
 * Created by jinphy on 2017/8/18.
 */

public class BaseApplication extends Application {

    public static BaseApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
