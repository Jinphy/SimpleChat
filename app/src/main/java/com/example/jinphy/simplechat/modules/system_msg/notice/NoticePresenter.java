package com.example.jinphy.simplechat.modules.system_msg.notice;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.models.message_record.MessageRecordRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;

import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/2.
 */

public class NoticePresenter implements NoticeContract.Presenter{

    private final NoticeContract.View view;
    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private MessageRecordRepository recordRepository;
    private FriendRepository friendRepository;


    public NoticePresenter(NoticeContract.View view) {
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
    public List<Message> loadNoticeMsg() {
        User user = userRepository.currentUser();
        List<Message> messages = messageRepository.loadSystemMsg(
                user.getAccount(), Message.TYPE_SYSTEM_NOTICE);
        Message.sort(messages);
        return messages;
    }

    @Override
    public void updateMsg(List<Message> messages) {
        if (messages.size() > 0) {
            LogUtils.e("1");
            messageRepository.update(messages);
            LogUtils.e("2");
            Message message = messages.get(0);
            Friend friend = friendRepository.get(message.getOwner(), Friend.system);
            MessageRecord record = recordRepository.get(message.getOwner(), friend);
            record.setNewMsgCount(record.getNewMsgCount() - messages.size());
            LogUtils.e("3");
            recordRepository.saveOrUpdate(record);
            LogUtils.e("4");
        }
    }
}
