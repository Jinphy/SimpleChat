package com.example.jinphy.simplechat.services.common_service.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

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

    // 该变量可以将异步执行的代码转成同步执行
    CountDownLatch countDownLatch;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binderPool = IBinderPool.Stub.asInterface(service);
        countDownLatch.countDown();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        context.unbindService(this);
        bindService();
    }

    @Override
    public void onBindingDied(ComponentName name) {
        context.unbindService(this);
        bindService();
    }


    private static class InstanceHolder{
        static final BinderFactory DEFAULT = new BinderFactory();
    }

    private BinderFactory() {
        ThreadPoolUtils.threadPool.execute(this::bindService);
    }

    public static void init(Context context) {
        InstanceHolder.DEFAULT.context = context.getApplicationContext();
    }


    /**
     * DESC: 绑定服务
     * Created by jinphy, on 2018/3/21, at 16:04
     */
    private synchronized void bindService() {
        countDownLatch = new CountDownLatch(1);
        Intent intent = new Intent(context, CommonService.class);
        context.bindService(intent, this, Context.BIND_AUTO_CREATE);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static IBinder getBinder(int type) {
        try {
            if (InstanceHolder.DEFAULT.binderPool == null) {
                return null;
            }
            return InstanceHolder.DEFAULT.binderPool.getBinder(type);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

}
