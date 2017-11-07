package com.example.jinphy.simplechat.application;

import com.example.jinphy.simplechat.api.NetworkManager;
import com.example.jinphy.simplechat.base.BaseApplication;
import com.example.jinphy.simplechat.model.friend.MyObjectBox;


import io.objectbox.BoxStore;

/**
 * Created by jinphy on 2017/11/6.
 */

public class DBApplication extends BaseApplication {

    private BoxStore boxStore;

    private NetworkManager networkManager;

    @Override
    public void onCreate() {
        super.onCreate();
        boxStore = MyObjectBox.builder().androidContext(DBApplication.this).build();
        networkManager = NetworkManager.getInstance();
    }

    public BoxStore getBoxStore(){
        return boxStore;
    }

    public NetworkManager getNetworkManager(){
        return networkManager;
    }
}
