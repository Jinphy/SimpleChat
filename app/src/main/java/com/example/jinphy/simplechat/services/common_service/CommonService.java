package com.example.jinphy.simplechat.services.common_service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.jinphy.simplechat.services.common_service.aidl.service.BinderPool;
import com.example.jinphy.simplechat.services.common_service.aidl.service.IBinderPool;

public class CommonService extends Service {

    private IBinder binderPool;

    public CommonService() {
        binderPool = new BinderPool(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binderPool;
    }
}
