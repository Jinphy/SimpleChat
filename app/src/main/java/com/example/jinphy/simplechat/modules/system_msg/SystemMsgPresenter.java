package com.example.jinphy.simplechat.modules.system_msg;

import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.member.MemberRepository;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;

/**
 * DESC:
 * Created by jinphy on 2018/3/1.
 */

public class SystemMsgPresenter implements SystemMsgContract.Presenter {

    private final SystemMsgContract.View view;
    private MessageRepository messageRepository;
    private UserRepository userRepository;

    public SystemMsgPresenter(SystemMsgContract.View view) {
        this.view = view;
        messageRepository = MessageRepository.getInstance();
        userRepository = UserRepository.getInstance();
    }

    @Override
    public void start() {

    }

    @Override
    public int countFriends() {
        User user = userRepository.currentUser();
        return (int) messageRepository.count(
                Friend.system,
                user.getAccount(),
                Message.TYPE_SYSTEM_ADD_FRIEND);
    }

    @Override
    public int countNewFriends() {
        User user = userRepository.currentUser();
        return (int) messageRepository.countNew(
                Friend.system,
                user.getAccount(),
                Message.TYPE_SYSTEM_ADD_FRIEND);
    }

    @Override
    public int countMembers() {
        User user = userRepository.currentUser();
        return (int) messageRepository.count(
                Friend.system,
                user.getAccount(),
                Message.TYPE_SYSTEM_APPLY_JOIN_GROUP);
    }

    @Override
    public int countNewMembers() {
        User user = userRepository.currentUser();
        return (int) messageRepository.countNew(
                Friend.system,
                user.getAccount(),
                Message.TYPE_SYSTEM_APPLY_JOIN_GROUP);
    }

    @Override
    public int countNotices() {
        User user = userRepository.currentUser();
        return (int) messageRepository.count(
                Friend.system,
                user.getAccount(),
                Message.TYPE_SYSTEM_NOTICE);
    }

    @Override
    public int countNewNotices() {
        User user = userRepository.currentUser();
        return (int) messageRepository.countNew(
                Friend.system,
                user.getAccount(),
                Message.TYPE_SYSTEM_NOTICE);
    }
}
