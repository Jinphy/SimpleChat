package com.example.jinphy.simplechat.modules.chat;

import android.support.annotation.NonNull;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.models.api.send.SendCallback;
import com.example.jinphy.simplechat.models.api.send.SendResult;
import com.example.jinphy.simplechat.models.api.send.Sender;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.group.GroupRepository;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.models.message_record.MessageRecordRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.List;

/**
 * Created by jinphy on 2017/8/13.
 */

public class ChatPresenter implements ChatContract.Presenter {

    private ChatContract.View view;
    private UserRepository userRepository;
    private FriendRepository friendRepository;
    private MessageRepository messageRepository;
    private MessageRecordRepository recordRepository;
    private GroupRepository groupRepository;

    public ChatPresenter(@NonNull ChatContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        userRepository = UserRepository.getInstance();
        friendRepository = FriendRepository.getInstance();
        messageRepository = MessageRepository.getInstance();
        recordRepository = MessageRecordRepository.getInstance();
        groupRepository = GroupRepository.getInstance();
    }



    @Override
    public void start() {

    }

    @Override
    public List<Message> loadMessages(String withAccount) {
        User user = userRepository.currentUser();
        return messageRepository.load(user.getAccount(), withAccount);
    }



    @Override
    public Friend getFriend(String friendAccount) {
        User user = userRepository.currentUser();
        return friendRepository.get(user.getAccount(), friendAccount);
    }

    @Override
    public Group getGroup(String groupNo) {
        User user = userRepository.currentUser();
        return groupRepository.get(groupNo, user.getAccount());
    }

    @Override
    public void sendTextMsg(String friendAccount, String content,int position) {
        User user = userRepository.currentUser();
        Message message = Message.make(user.getAccount(), friendAccount, content);
        Sender.newTask(message)
                .whenStart(() -> {
                    view.whenSendStart(message);
                    messageRepository.saveSend(message);
                })
                .whenFinal(result -> {
                    if (SendResult.OK.equals(result.code)) {
                        message.setStatus(Message.STATUS_OK);
                        view.whenSendFinal();
                    } else {
                        message.setStatus(Message.STATUS_NO);
                        view.whenSendFinal();
                    }
                    messageRepository.update(message);
                })
                .send();
    }

    @Override
    public void updateRecord(Message message) {
        if (message == null) {
            return;
        }
        String with = message.getWith();
        MessageRecord record ;
        if (with.contains("G")) {
            // 群聊
            Group group = groupRepository.get(with, message.getOwner());
            record = recordRepository.get(message.getOwner(), group);
            if (record == null) {
                record = new MessageRecord();
                record.setWith(group);
                record.setOwner(message.getOwner());
            }
        } else {
            // 与好友聊天
            Friend friend = friendRepository.get(message.getOwner(), message.getWith());
            record = recordRepository.get(message.getOwner(), friend);
            if (record == null) {
                record = new MessageRecord();
                record.setWith(friend);
                record.setOwner(message.getOwner());
            }
        }
        record.setNewMsgCount(0);
        record.setLastMsg(message);
        recordRepository.saveOrUpdate(record);
    }

    @Override
    public String getUserAvatar() {
        return userRepository.currentUser().getAvatar();
    }

    @Override
    public void updateMsg(List<Message> messages) {
        messageRepository.update(messages);

    }
}
