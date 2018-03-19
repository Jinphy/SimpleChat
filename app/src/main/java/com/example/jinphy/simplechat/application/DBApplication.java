package com.example.jinphy.simplechat.application;

import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.models.MyObjectBox;

import io.objectbox.BoxStore;

/**
 * Created by jinphy on 2017/11/6.
 */

public class DBApplication extends BaseApplication {

    private static BoxStore boxStore;


    @Override
    public void onCreate() {
        super.onCreate();
        boxStore = MyObjectBox.builder().androidContext(this).build();
    }


    public static BoxStore boxStore(){
        return boxStore;
    }

}
