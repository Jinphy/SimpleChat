package com.example.jinphy.simplechat.modules.chat;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.event_bus.EBSendMsg;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.message.Message;

import java.util.List;

/**
 * Created by jinphy on 2017/8/13.
 */

public interface ChatContract {


    interface View extends BaseView<Presenter> {

        void showBar(android.view.View view);

        void hideBar(android.view.View view);

        void animateBar(android.view.View view, float fromValue, float toValue, boolean showBar);

        void setStatusBarColor(float factor);

        void moveHorizontal(float factor);

        void animateHorizontal(float fromFactor, float toFactor, boolean exit);

        void updateView();

        void whenSendStart(Message message);

        void whenSendFinal(EBSendMsg msg);

        void cacheMsg(Message message);

        void onSelectFilesResult(List<String> filePaths);
    }

    interface Presenter extends BasePresenter {

        List<Message> loadMessages(String friendAccount);

        List<Member> loadMembers(String groupNo);

        Friend getFriend(String friendAccount);

        Group getGroup(String groupNo);

        String getOwner();

        void sendTextMsg(Message message);

        void sendFileMsg(Message message);

        void updateRecord(Message message);

        String getUserAvatar();

        void updateMsg(List<Message> messages);

        void updateMsg(Message... messages);

        Member getSelfMember(String groupNo);

        void downloadVoice(Message message);

        Message getMessage(long msgId);
    }
}

