package com.example.jinphy.simplechat.modules.show_file;

import android.os.IBinder;
import android.os.RemoteException;

import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.services.common_service.aidl.BinderFactory;
import com.example.jinphy.simplechat.services.common_service.aidl.service.DownloadFileBinder;
import com.example.jinphy.simplechat.services.common_service.aidl.service.IDownloadFileBinder;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * DESC:
 * Created by jinphy on 2018/3/28.
 */

public class ShowFilePresenter implements ShowFileContract.Presenter {

    private ShowFileContract.View view;
    private MessageRepository messageRepository;

    private IDownloadFileBinder downloadFileBinder;

    public ShowFilePresenter(ShowFileContract.View view) {
        this.view = view;
        messageRepository = MessageRepository.getInstance();
        initBinder();
    }

    private void initBinder() {
        IBinder binder = BinderFactory.getBinder(BinderFactory.TYPE_DOWNLOAD_FILE);
        downloadFileBinder = DownloadFileBinder.asInterface(binder);
    }


    @Override
    public void start() {

    }

    @Override
    public Message getMessage(long msgId) {
        return messageRepository.get(msgId);
    }

    @Override
    public void downloadFile(Message message) {
        long fileTaskId = Long.valueOf(message.extra(Message.KEY_FILE_TASK_ID));
        long msgId = message.getId();
        try {
            downloadFileBinder.downloadFile(fileTaskId, msgId);
        } catch (Exception e) {
            e.printStackTrace();
            initBinder();
            try {
                downloadFileBinder.downloadFile(fileTaskId, msgId);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
    }
}
