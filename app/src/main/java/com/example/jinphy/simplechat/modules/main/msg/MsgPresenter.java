package com.example.jinphy.simplechat.modules.main.msg;

import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.models.message_record.MessageRecordRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.utils.Preconditions;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by jinphy on 2017/8/10.
 */

public class MsgPresenter implements MsgContract.Presenter {
    MsgContract.View view;
    MessageRecordRepository recordRepository;
    UserRepository userRepository;
    MessageRepository messageRepository;
    FriendRepository friendRepository;

    private Map<String, String> nameMap = new HashMap<>();


    public MsgPresenter(@NonNull MsgContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        this.recordRepository = MessageRecordRepository.getInstance();
        this.userRepository = UserRepository.getInstance();
        this.messageRepository = MessageRepository.getInstance();
        this.friendRepository = FriendRepository.getInstance();
    }

    @Override
    public void start() {

    }


    @Override
    public List<MessageRecord> loadMsgRecords() {
        List<MessageRecord> messageRecords = recordRepository.load(userRepository.currentUser().getAccount());

        MessageRecord.sort(messageRecords);

        return messageRecords;
    }

    @Override
    public void deleteMsgRecord(MessageRecord record) {
        messageRepository.delete(record.getOwner(), record.getWith());
        recordRepository.delete(record);
    }

    @Override
    public void clearMsg(MessageRecord record) {
        messageRepository.delete(record.getOwner(), record.getWith());
        record.setNewMsgCount(0);
        recordRepository.update(record);
    }

    @Override
    public void updateRecord(MessageRecord record) {
        recordRepository.update(record);
    }

    @Override
    public String getName(String account) {
        if (account == null) {
            return null;
        }
        String name = nameMap.get(account);
        if (name != null) {
            return name;
        }

        User user = userRepository.currentUser();
        if (StringUtils.equal(account, user.getAccount())) {
            name = "自己";
            nameMap.put(account, name);
            return name;
        }
        Friend friend = friendRepository.get(user.getAccount(), account);
        if (friend == null) {
            name = "匿名";
            nameMap.put(account, name);
            return name;
        }
        name = friend.getShowName();
        nameMap.put(account, name);
        return name;
    }
}
