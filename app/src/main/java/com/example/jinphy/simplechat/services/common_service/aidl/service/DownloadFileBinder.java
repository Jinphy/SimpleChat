package com.example.jinphy.simplechat.services.common_service.aidl.service;

import android.content.Context;

import com.example.jinphy.simplechat.models.file_task.FileTask;
import com.example.jinphy.simplechat.models.file_task.FileTaskRepository;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.modules.system_msg.new_member.NewMemberAdapter;
import com.example.jinphy.simplechat.services.common_service.aidl.models.FileDownloader;
import com.example.jinphy.simplechat.services.common_service.aidl.models.OnError;

import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.TAG_DOWNLOAD_FILE;
import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.TAG_DOWNLOAD_VOICE;
import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.TAG_UPLOAD_FILE;
import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.send;

/**
 * DESC:
 * Created by jinphy on 2018/3/21.
 */

public class DownloadFileBinder extends IDownloadFileBinder.Stub {


    private Context context;

    private FileTaskRepository fileTaskRepository;
    private MessageRepository messageRepository;

    private static class InstanceHolder{
        static final DownloadFileBinder DEFUALT = new DownloadFileBinder();
    }

    public static DownloadFileBinder getInstance(Context context) {
        InstanceHolder.DEFUALT.context = context;
        return InstanceHolder.DEFUALT;
    }



    public DownloadFileBinder() {
        fileTaskRepository = FileTaskRepository.getInstance();
        messageRepository = MessageRepository.getInstance();
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

    @Override
    public void downloadVoice(long fileTaskId, long msgId) {
        FileTask task = fileTaskRepository.get(fileTaskId);
        FileDownloader uploader = FileDownloader.with(task);
        if (uploader == null) {
            send(context, TAG_DOWNLOAD_VOICE, Message.AUDIO_STATUS_NO+":"+msgId);
            return;
        }
        uploader.doOnError(id -> send(context, TAG_DOWNLOAD_VOICE, Message.AUDIO_STATUS_NO+":"+msgId))
                .doOnFinish(id -> {
                    send(context, TAG_DOWNLOAD_VOICE, Message.AUDIO_STATUS_NEW + ":" + msgId);
                    fileTaskRepository.remove(fileTaskId);
                })
                .execute();
    }
}
