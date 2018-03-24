package com.example.jinphy.simplechat.services.common_service.aidl.service;

import android.content.Context;

import com.example.jinphy.simplechat.models.api.send.SendResult;
import com.example.jinphy.simplechat.models.api.send.Sender;
import com.example.jinphy.simplechat.models.file_task.FileTask;
import com.example.jinphy.simplechat.models.file_task.FileTaskRepository;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.services.common_service.aidl.models.FileUploader;

import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.TAG_UPLOAD_FILE;
import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.send;

/**
 * DESC:
 * Created by jinphy on 2018/3/21.
 */

public class UploadFileBinder extends IUploadFileBinder.Stub {


    private final Context context;
    private FileTaskRepository fileTaskRepository;

    public UploadFileBinder(Context context) {
        this.context = context;
        fileTaskRepository = FileTaskRepository.getInstance();
    }


    /**
     * DESC: 上传文件
     * Created by jinphy, on 2018/3/21, at 21:37
     */
    @Override
    public void upload(long fileTaskId) {
        FileTask task = fileTaskRepository.get(fileTaskId);
        FileUploader uploader = FileUploader.with(task);
        if (uploader == null) {
            send(context, TAG_UPLOAD_FILE, "onStart:"+fileTaskId);
            return;
        }
        uploader.doOnStart(id -> send(context, TAG_UPLOAD_FILE,"onStart:"+fileTaskId))
                .doOnUpdate((id, finishedLength, totalLength) -> {
                    send(context, TAG_UPLOAD_FILE, "onUpdate:"+id+":"+finishedLength+":"+totalLength);
                })
                .doOnError(id -> {
                    send(context, TAG_UPLOAD_FILE, "onError:" + id);
                })
                .doOnFinish(id -> {
                    send(context, TAG_UPLOAD_FILE, "onFinish:" + id);
                    fileTaskRepository.remove(fileTaskId);
                })
                .execute();
    }

}
