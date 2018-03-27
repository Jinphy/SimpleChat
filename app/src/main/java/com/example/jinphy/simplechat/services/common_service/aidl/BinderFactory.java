package com.example.jinphy.simplechat.services.common_service.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.services.common_service.CommonService;
import com.example.jinphy.simplechat.services.common_service.aidl.service.IBinderPool;
import com.example.jinphy.simplechat.utils.ThreadPoolUtils;

import java.util.concurrent.CountDownLatch;

/**
 * DESC:
 * Created by jinphy on 2018/3/21.
 */

public class BinderFactory implements ServiceConnection{

    public static final int TYPE_UPLOAD_FILE = 0;

    public static final int TYPE_DOWNLOAD_FILE = 1;

    public static final int TYPE_SEND_MSG = 2;


    private Context context;
    private IBinderPool binderPool;

    private static BinderFactory instance;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binderPool = IBinderPool.Stub.asInterface(service);
        LogUtils.e("connect");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        context.unbindService(this);
        bindService();
        LogUtils.e("disconnect");
    }

    @Override
    public void onBindingDied(ComponentName name) {
        context.unbindService(this);
        bindService();
        LogUtils.e("bindingDied");
    }


    private BinderFactory(Context context) {
        this.context = context;
        bindService();
    }

    public static synchronized void init(Context context) {
        if (instance != null) {
            return;
        }
        instance = new BinderFactory(context);
    }


    /**
     * DESC: 绑定服务
     * Created by jinphy, on 2018/3/21, at 16:04
     */
    private void bindService() {
        Intent intent = new Intent(context, CommonService.class);
        context.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }


    public static IBinder getBinder(int type) {
        try {
            if (instance.binderPool == null) {
                return null;
            }
            return instance.binderPool.getBinder(type);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

}
