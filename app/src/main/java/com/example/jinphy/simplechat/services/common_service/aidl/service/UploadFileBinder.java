package com.example.jinphy.simplechat.services.common_service.aidl.service;

import android.content.Context;
import android.os.RemoteException;

import com.example.jinphy.simplechat.models.api.common.Response;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.file_task.FileTask;
import com.example.jinphy.simplechat.models.file_task.FileTaskRepository;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.services.common_service.aidl.models.FileUploader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.TAG_SEND_MSG;
import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.TAG_UPLOAD_FILE;
import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.send;

/**
 * DESC:
 * Created by jinphy on 2018/3/21.
 */

public class UploadFileBinder extends IUploadFileBinder.Stub{

    Context context;
    private FileTaskRepository fileTaskRepository;
    private MessageRepository messageRepository;
    private SendMsgBinder msgSender;

    private List<MsgUploadTask> msgUploadTasks = new CopyOnWriteArrayList<>();

    private boolean isUploading;


    private static class InstanceHolder{
        static final UploadFileBinder DEFAULT = new UploadFileBinder();
    }

    public static UploadFileBinder getInstance(Context context) {
        InstanceHolder.DEFAULT.context = context;
        InstanceHolder.DEFAULT.msgSender.setContext(context);
        return InstanceHolder.DEFAULT;
    }


    public UploadFileBinder() {
        fileTaskRepository = FileTaskRepository.getInstance();
        messageRepository = MessageRepository.getInstance();
        msgSender = SendMsgBinder.getInstance(context);
    }


    /**
     * DESC: 上传文件
     * Created by jinphy, on 2018/3/21, at 21:37
     */
    public void uploadFileAndSendMsg(long fileTaskId, long msgId) {
        MsgUploadTask task = MsgUploadTask.make(fileTaskId, msgId);
        msgUploadTasks.add(task);
        executeUpload();
    }


    private void executeUpload() {
        if (isUploading) {
            return;
        }
        synchronized (this) {
            if (isUploading || msgUploadTasks.size() == 0) {
                return;
            }
            isUploading = true;
        }
        MsgUploadTask msgUploadTask = msgUploadTasks.remove(0);

        long fileTaskId = msgUploadTask.fileTaskId;
        long msgId = msgUploadTask.msgId;

        FileTask task = fileTaskRepository.get(fileTaskId);
        FileUploader uploader = FileUploader.with(task);
        if (uploader == null) {
            send(context, TAG_UPLOAD_FILE, "onStart:"+fileTaskId);
            isUploading = false;
            executeUpload();
            return;
        }
        uploader.doOnStart(id -> send(context, TAG_UPLOAD_FILE,"onStart:"+fileTaskId))
                .doOnUpdate((id, finishedLength, totalLength) -> {
                    send(context, TAG_UPLOAD_FILE, "onUpdate:"+id+":"+finishedLength+":"+totalLength);
                })
                .doOnError(id -> {
                    send(context, TAG_UPLOAD_FILE, "onError:" + id);
                    sendErrorMsg(msgId);
                    isUploading = false;
                    executeUpload();
                })
                .doOnFinish(id -> {
                    fileTaskRepository.remove(fileTaskId);
                    try {
                        msgSender.sendMessage(msgId);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        sendErrorMsg(msgId);
                    }
                    isUploading = false;
                    executeUpload();
                })
                .execute();
    }




    private void sendErrorMsg(long msgId) {
        Message message = messageRepository.get(msgId);
        message.setStatus(Message.STATUS_NO);
        messageRepository.update(message);
        send(context, TAG_SEND_MSG, Response.NO + ":" + msgId);
    }

    private static class MsgUploadTask{
        long fileTaskId;
        long msgId;


        static MsgUploadTask make(long fileTaskId, long msgId) {
            MsgUploadTask task = new MsgUploadTask();
            task.fileTaskId = fileTaskId;
            task.msgId = msgId;
            return task;
        }
    }

}
