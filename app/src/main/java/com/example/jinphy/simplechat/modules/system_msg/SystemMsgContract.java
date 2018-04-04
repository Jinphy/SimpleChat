package com.example.jinphy.simplechat.modules.system_msg;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;

/**
 * DESC:
 * Created by jinphy on 2018/3/1.
 */

public interface SystemMsgContract {

    interface View extends BaseView<Presenter>{

    }



    interface Presenter extends BasePresenter{

        int countFriends();

        int countNewFriends();

        int countMembers();

        int countNewMembers();

        int countNotices();

        int countNewNotices();

        void updateSystemMsgRecord();

        void deleteMsg(String type);
    }
}
