package com.example.jinphy.simplechat.modules.system_msg.new_member;

import android.content.Context;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.message.Message;

import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/12.
 */

public interface NewMemberContract {


    interface View extends BaseView<Presenter> {

        void whenAgreeOk(NewMember newMember);
    }


    interface Presenter extends BasePresenter{

        List<NewMember> loadNewMembers();

        void updateMsg(List<Message> messages);

        void updateMsg(Message message);

        void deleteMsg(Message message);

        void agreeJoinGroup(Context context, NewMember newMember);
    }
}
