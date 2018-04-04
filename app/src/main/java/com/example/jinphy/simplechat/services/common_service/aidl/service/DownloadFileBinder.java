package com.example.jinphy.simplechat.services.common_service.aidl.service;

import android.content.Context;

import com.example.jinphy.simplechat.models.file_task.FileTask;
import com.example.jinphy.simplechat.models.file_task.FileTaskRepository;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.services.common_service.aidl.models.FileDownloader;

import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.TAG_DOWNLOAD_FILE;
import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.TAG_DOWNLOAD_PHOTO;
import static com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver.TAG_DOWNLOAD_VOICE;
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
        static final DownloadFileBinder DEFAULT = new DownloadFileBinder();
    }

    public static DownloadFileBinder getInstance(Context context) {
        InstanceHolder.DEFAULT.context = context;
        return InstanceHolder.DEFAULT;
    }



    public DownloadFileBinder() {
        fileTaskRepository = FileTaskRepository.getInstance();
        messageRepository = MessageRepository.getInstance();
    }


    @Override
    public void downloadPhoto(long fileTaskId) {
        FileTask task = fileTaskRepository.get(fileTaskId);
        FileDownloader downloader = FileDownloader.with(task);
        if (downloader == null) {
            send(context, TAG_DOWNLOAD_PHOTO, "onError:" + fileTaskId);
            return;
        }
        downloader.doOnStart(id -> send(context, TAG_DOWNLOAD_PHOTO,"onStart:"+fileTaskId))
                .doOnUpdate((id, finishedLength, totalLength) -> {
                    send(context, TAG_DOWNLOAD_PHOTO, "onUpdate:"+id+":"+finishedLength+":"+totalLength);
                })
                .doOnError(id -> {
                    send(context, TAG_DOWNLOAD_PHOTO, "onError:" + id);
                })
                .doOnFinish(id -> {
                    send(context, TAG_DOWNLOAD_PHOTO, "onFinish:" + id);
                    fileTaskRepository.remove(fileTaskId);
                })
                .execute();
    }

    @Override
    public void downloadVoice(long fileTaskId, long msgId) {
        FileTask task = fileTaskRepository.get(fileTaskId);
        FileDownloader downloader = FileDownloader.with(task);
        if (downloader == null) {
            send(context, TAG_DOWNLOAD_VOICE, Message.AUDIO_STATUS_NO+":"+msgId);
            return;
        }
        downloader.doOnError(id -> send(context, TAG_DOWNLOAD_VOICE, Message.AUDIO_STATUS_NO+":"+msgId))
                .doOnFinish(id -> {
                    send(context, TAG_DOWNLOAD_VOICE, Message.AUDIO_STATUS_NEW + ":" + msgId);
                    fileTaskRepository.remove(fileTaskId);
                })
                .execute();
    }

    @Override
    public void downloadFile(long fileTaskId, long msgId){
        FileTask task = fileTaskRepository.get(fileTaskId);
        FileDownloader downloader = FileDownloader.with(task);
        if (downloader == null) {
            send(context, TAG_DOWNLOAD_FILE, "onError:"+msgId);
            Message message = messageRepository.get(msgId);
            message.extra(Message.KEY_FILE_STATUS, Message.FILE_STATUS_DOWNLOADED);
            message.setExtra(null);
            messageRepository.update(message);
            return;
        }
        downloader.doOnStart(id -> {
            send(context, TAG_DOWNLOAD_FILE, "onStart:" + msgId);
            Message message = messageRepository.get(msgId);
            message.extra(Message.KEY_FILE_STATUS, Message.FILE_STATUS_DOWNLOADING);
            message.setExtra(null);
            messageRepository.update(message);
        })
                .doOnUpdate((id, finishedLength, totalLength) -> {
                    send(context, TAG_DOWNLOAD_FILE, "onUpdate:"+msgId+":"+finishedLength+":"+totalLength);
                })
                .doOnError(id -> {
                    send(context, TAG_DOWNLOAD_FILE, "onError:" + msgId);
                    Message message = messageRepository.get(msgId);
                    message.extra(Message.KEY_FILE_STATUS, Message.FILE_STATUS_NO_DOWNLOAD);
                    message.setExtra(null);
                    messageRepository.update(message);
                })
                .doOnFinish(id -> {
                    send(context, TAG_DOWNLOAD_FILE, "onFinish:" + msgId);
                    fileTaskRepository.remove(fileTaskId);
                    Message message = messageRepository.get(msgId);
                    message.extra(Message.KEY_FILE_STATUS, Message.FILE_STATUS_DOWNLOADED);
                    message.setExtra(null);
                    messageRepository.update(message);
                })
                .execute();
    }
}
