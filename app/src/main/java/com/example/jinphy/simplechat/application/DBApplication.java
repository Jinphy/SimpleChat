package com.example.jinphy.simplechat.application;

import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.model.MyObjectBox;

import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Created by jinphy on 2017/11/6.
 */

public class DBApplication extends BaseApplication {

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        boxStore = MyObjectBox.builder().androidContext(DBApplication.this).build();
    }

    public BoxStore getBoxStore(){
        return boxStore;
    }
}
