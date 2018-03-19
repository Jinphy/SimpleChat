package com.example.jinphy.simplechat.modules.chat;

import com.example.jinphy.simplechat.base.BasePresenter;
import com.example.jinphy.simplechat.base.BaseView;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;

import java.util.List;

/**
 * Created by jinphy on 2017/8/13.
 */

public interface ChatContract {


    interface View extends BaseView<Presenter> {

        void showBar(android.view.View view);

        void hideBar(android.view.View view);

        void animateBar(android.view.View view, float fromValue, float toValue, boolean showBar);

        void fabAction(android.view.View view);

        void showSendBtn();

        void showMoreBtn();

        void showVoiceBtn();

        void showKeyboardBtn();

        void showTextInput();

        void showVoiceInput();

        void showEmotionLayout();

        void hideEmotionLayout();

        void showMoreLayout();

        void hideMoreLayout();

        void showFabEmotion();

        void hideFabEmotion();

        void setStatusBarColor(float factor);

        void hideExtraBottomLayout();

        void moveHorizontal(float factor);

        void animateHorizontal(float fromFactor, float toFactor, boolean exit);

        void updateView();

        void whenSendStart();

        void whenSendFinal();

        boolean isFriend();

    }

    interface Presenter extends BasePresenter {

        List<Message> loadMessages(String friendAccount);

        List<Member> loadMembers(String groupNo);

        Friend getFriend(String friendAccount);

        Group getGroup(String groupNo);

        String getOwner();

        void sendTextMsg(Message message);

        void updateRecord(Message message);

        String getUserAvatar();

        void updateMsg(List<Message> messages);

        Member getSelfMember(String groupNo);

    }
}

