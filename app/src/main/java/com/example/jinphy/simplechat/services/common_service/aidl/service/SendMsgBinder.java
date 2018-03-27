package com.example.jinphy.simplechat.services.common_service.aidl.service;

import android.content.Context;
import android.os.RemoteException;

import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;

import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.*;

/**
 * DESC:
 * Created by jinphy on 2018/3/21.
 */

public class SendMsgBinder extends ISendMsgBinder.Stub {


    Context context;
    private MessageRepository messageRepository;


    private static class InstanceHolder{
        static final SendMsgBinder DEFAULT = new SendMsgBinder();
    }

    public static SendMsgBinder getInstance(Context context) {
        if (context != null) {
            InstanceHolder.DEFAULT.context = context;
        }
        return InstanceHolder.DEFAULT;
    }


    public SendMsgBinder() {
        messageRepository = MessageRepository.getInstance();
    }

    public void setContext(Context context) {
        this.context = context;
    }



    @Override
    public void sendMessage(long id) throws RemoteException {
        Message message = messageRepository.get(id);
        if (message == null) {
            return;
        }
        messageRepository.<String>newTask()
                .param(Api.Key.fromAccount, message.getOwner())
                .param(Api.Key.toAccount, message.getWith())
                .param(Api.Key.createTime, message.getCreateTime())
                .param(Api.Key.content, message.getContent())
                .param(Api.Key.contentType, message.getContentType())
                .param(Api.Key.extra, message.getExtra())
                .doOnDataOk(okData -> {
                    message.setStatus(Message.STATUS_OK);
                    messageRepository.update(message);
                    send(context, TAG_SEND_MSG, okData.getCode() + ":" + id);
                })
                .doOnDataNo(noData -> {
                    message.setStatus(Message.STATUS_NO);
                    messageRepository.update(message);
                    send(context, TAG_SEND_MSG, Response.NO + ":" + id);
                })
                .submit(task -> messageRepository.sendMsg(task));
    }
}
