package com.example.jinphy.simplechat.base;

import android.app.Application;
import android.widget.Toast;


/**
 * Created by jinphy on 2017/8/18.
 */

public class BaseApplication extends Application {

    public static BaseApplication INSTANCE;
    private Toast toast;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        this.toast = getToast();
    }

    public Toast getToast() {
        return Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }
}
