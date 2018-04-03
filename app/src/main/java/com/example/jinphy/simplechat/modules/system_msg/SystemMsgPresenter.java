package com.example.jinphy.simplechat.modules.system_msg;

import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.models.message_record.MessageRecordRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;

import java.security.acl.Owner;

/**
 * DESC:
 * Created by jinphy on 2018/3/1.
 */

public class SystemMsgPresenter implements SystemMsgContract.Presenter {

    private final SystemMsgContract.View view;
    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private MessageRecordRepository recordRepository;
    private FriendRepository friendRepository;

    public SystemMsgPresenter(SystemMsgContract.View view) {
        this.view = view;
        messageRepository = MessageRepository.getInstance();
        userRepository = UserRepository.getInstance();
        recordRepository = MessageRecordRepository.getInstance();
        friendRepository = FriendRepository.getInstance();
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

    @Override
    public void updateSystemMsgRecord() {
        User user = userRepository.currentUser();
        recordRepository.update(user.getAccount(), Friend.system);
    }

    @Override
    public void deleteMsg(String type) {
        User user = userRepository.currentUser();
        messageRepository.delete(user.getAccount(), Friend.system, type);
        recordRepository.update(user.getAccount(), Friend.system);
    }
}
