package com.example.jinphy.simplechat.services.common_service.aidl.service;

import android.content.Context;

import com.example.jinphy.simplechat.models.file_task.FileTask;
import com.example.jinphy.simplechat.models.file_task.FileTaskRepository;
import com.example.jinphy.simplechat.services.common_service.aidl.models.FileDownloader;
import com.example.jinphy.simplechat.services.common_service.aidl.models.OnError;

import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.TAG_DOWNLOAD_FILE;
import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.TAG_UPLOAD_FILE;
import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.send;

/**
 * DESC:
 * Created by jinphy on 2018/3/21.
 */

public class DownloadFileBinder extends IDownloadFileBinder.Stub {


    private final Context context;

    private FileTaskRepository fileTaskRepository;

    public DownloadFileBinder(Context context) {
        this.context = context;
        fileTaskRepository = FileTaskRepository.getInstance();
    }


    @Override
    public void download(long fileTaskId) {
        FileTask task = fileTaskRepository.get(fileTaskId);
        FileDownloader uploader = FileDownloader.with(task);
        if (uploader == null) {
            send(context, TAG_DOWNLOAD_FILE, "onError:" + fileTaskId);
            return;
        }
        uploader.doOnStart(id -> send(context, TAG_UPLOAD_FILE,"onStart:"+fileTaskId))
                .doOnUpdate((id, finishedLength, totalLength) -> {
                    send(context, TAG_DOWNLOAD_FILE, "onUpdate:"+id+":"+finishedLength+":"+totalLength);
                })
                .doOnError(id -> {
                    send(context, TAG_DOWNLOAD_FILE, "onError:" + id);
                })
                .doOnFinish(id -> {
                    send(context, TAG_DOWNLOAD_FILE, "onFinish:" + id);
                    fileTaskRepository.remove(fileTaskId);
                })
                .execute();
    }
}
