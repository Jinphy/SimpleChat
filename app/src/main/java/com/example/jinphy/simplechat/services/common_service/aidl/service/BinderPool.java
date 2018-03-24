package com.example.jinphy.simplechat.services.common_service.aidl.service;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import static com.example.jinphy.simplechat.services.common_service.aidl.BinderFactory
        .TYPE_DOWNLOAD_FILE;
import static com.example.jinphy.simplechat.services.common_service.aidl.BinderFactory
        .TYPE_SEND_MSG;
import static com.example.jinphy.simplechat.services.common_service.aidl.BinderFactory
        .TYPE_UPLOAD_FILE ;

/**
 * DESC: binder池，用来产生各种Binder
 * Created by jinphy on 2018/3/21.
 */
public class BinderPool extends IBinderPool.Stub {

    private final Context context;

    public BinderPool(Context context) {
        this.context = context;
    }

    @Override
    public IBinder getBinder(int type) throws RemoteException {
        IBinder binder = null;
        switch (type) {
            case TYPE_UPLOAD_FILE:
                // 创建文件上传服务
                binder = new UploadFileBinder(context);
                break;
            case TYPE_DOWNLOAD_FILE:
                // 创建文件下载服务
                binder = new DownloadFileBinder(context);
                break;
            case TYPE_SEND_MSG:
                // 创建发送信息服务
                binder = new SendMsgBinder(context);
                break;
        }
        return binder;
    }
}
