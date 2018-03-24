package com.example.jinphy.simplechat.services.common_service.aidl.service;

import android.content.Context;
import android.os.RemoteException;

/**
 * DESC:
 * Created by jinphy on 2018/3/21.
 */

public class SendMsgBinder extends ISendMsgBinder.Stub {


    private final Context context;

    public SendMsgBinder(Context context) {
        this.context = context;
    }




    @Override
    public void setMessage(long id) throws RemoteException {

    }
}
