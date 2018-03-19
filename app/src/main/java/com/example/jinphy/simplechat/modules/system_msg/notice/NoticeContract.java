package com.example.jinphy.simplechat.modules.system_msg.notice;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.message.Message;

import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/2.
 */

public interface NoticeContract {

    interface View extends BaseView<Presenter> {

    }


    interface Presenter extends BasePresenter {

        List<Message> loadNoticeMsg();

        void updateMsg(List<Message> messages);
    }

}

