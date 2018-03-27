package com.example.jinphy.simplechat.modules.show_photo;

import android.os.IBinder;

import com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver;
import com.example.jinphy.simplechat.models.file_task.FileTaskRepository;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.modules.chat.FileListener;
import com.example.jinphy.simplechat.services.common_service.aidl.BinderFactory;
import com.example.jinphy.simplechat.services.common_service.aidl.service.DownloadFileBinder;
import com.example.jinphy.simplechat.services.common_service.aidl.service.IDownloadFileBinder;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * DESC:
 * Created by jinphy on 2018/3/22.
 */

public class ShowPhotoPresenter implements ShowPhotoContract.Presenter {

    private ShowPhotoContract.View view;
    private MessageRepository messageRepository;
    private FileTaskRepository fileTaskRepository;

    private IDownloadFileBinder downloadFileBinder;
    private MyDownloadListener downloadListener;

    public ShowPhotoPresenter(ShowPhotoContract.View view) {
        this.view = view;
        messageRepository = MessageRepository.getInstance();
        fileTaskRepository = FileTaskRepository.getInstance();

        IBinder downloadFileIBinder = BinderFactory.getBinder(BinderFactory.TYPE_DOWNLOAD_FILE);
        downloadFileBinder = DownloadFileBinder.asInterface(downloadFileIBinder);
        downloadListener = new MyDownloadListener(this);
    }


    @Override
    public void start() {

    }

    @Override
    public Message getMessage(long id) {
        return messageRepository.get(id);
    }


    @Override
    public void removeThumbnail(Message msg) {
        if (msg == null) {
            return;
        }
        msg.removeExtra(Message.KEY_THUMBNAIL);
        msg.removeExtra(Message.KEY_FILE_TASK_ID);
        msg.setExtra(null);
        messageRepository.update(msg);
    }

    @Override
    public void downloadPhoto(Message msg) {
        Observable.just(msg.extra(Message.KEY_FILE_TASK_ID))
                .map(Long::valueOf)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(downloadFileBinder::download)
                .subscribe();
    }

    @Override
    public void registerDownloadListener() {
        AppBroadcastReceiver.registerDownloadFileListener(downloadListener);
    }

    @Override
    public void unregisterDownloadListener() {
        AppBroadcastReceiver.unregisterDownloadFileListener();
    }

    public static class MyDownloadListener implements FileListener{

        private final ShowPhotoPresenter presenter;

        public MyDownloadListener(ShowPhotoPresenter presenter) {
            this.presenter = presenter;
        }

        @Override
        public void onStart(long fileTaskId) {
            presenter.view.whenDownloadStart();
        }

        @Override
        public void onUpdate(long fileTaskId, long finishedLength, long totalLength) {
            int percent = (int) ((finishedLength * 1.0 / totalLength) * 100);
            presenter.view.whenUpdateProgress(percent);
        }

        @Override
        public void onError(long fileTaskId) {
            presenter.view.whenDownloadError();
        }

        @Override
        public void onFinish(long fileTaskId) {
            presenter.view.whenDownloadFinish();
        }
    }
}
