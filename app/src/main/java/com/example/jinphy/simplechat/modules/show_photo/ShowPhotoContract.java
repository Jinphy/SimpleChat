package com.example.jinphy.simplechat.modules.show_photo;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.message.Message;

/**
 * DESC:
 * Created by jinphy on 2018/3/22.
 */

public interface ShowPhotoContract {

    interface View extends BaseView<Presenter>{

        void whenDownloadStart();

        void whenUpdateProgress(int percent);

        void whenDownloadFinish();

        void whenDownloadError();
    }


    interface Presenter extends BasePresenter{

        Message getMessage(long id);

        void downloadPhoto(Message msg);

        void removeThumbnail(Message msg);

        void registerDownloadListener();

        void unregisterDownloadListener();
    }

}
