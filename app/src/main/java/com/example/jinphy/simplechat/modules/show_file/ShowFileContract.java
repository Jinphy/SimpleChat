package com.example.jinphy.simplechat.modules.show_file;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.message.Message;

/**
 * DESC:
 * Created by jinphy on 2018/3/28.
 */

public interface ShowFileContract {


    interface View extends BaseView<Presenter> {
    }



    interface Presenter extends BasePresenter{

        Message getMessage(long msgId);

        void downloadFile(Message message);
    }
}
