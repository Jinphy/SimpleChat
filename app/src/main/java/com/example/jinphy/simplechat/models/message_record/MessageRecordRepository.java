package com.example.jinphy.simplechat.models.message_record;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.group.GroupRepository;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.List;

import io.objectbox.Box;

/**
 * DESC:
 * Created by jinphy on 2018/1/18.
 */

public class MessageRecordRepository implements MessageRecordDataSource {

    private Box<MessageRecord> messageRecordBox;

    private static class InstanceHolder{
        static final MessageRecordRepository DEFAULT = new MessageRecordRepository();
    }

    public static MessageRecordRepository getInstance() {
        return InstanceHolder.DEFAULT;
    }

    private MessageRecordRepository() {
        messageRecordBox = App.boxStore().boxFor(MessageRecord.class);
    }

    /**
     * DESC: 保存或更新
     * Created by jinphy, on 2018/1/18, at 10:58
     */
    @Override
    public void saveOrUpdate(MessageRecord record) {
        messageRecordBox.put(record);
    }

    @Override
    public MessageRecord get(String owner, Friend with) {
        return messageRecordBox.query()
                .equal(MessageRecord_.owner, owner)
                .equal(MessageRecord_.withFriendId, with.getId())
                .build().findFirst();
    }

    @Override
    public MessageRecord get(String owner, Group group) {
        return messageRecordBox.query()
                .equal(MessageRecord_.owner, owner)
                .equal(MessageRecord_.withGroupId, group.getId())
                .build().findFirst();
    }

    @Override
    public List<MessageRecord> load(String owner) {
        List<MessageRecord> messageRecords = messageRecordBox.query()
                .filter(record -> {
                    if (record.getWith() == null || record.getMessage() == null) {
                        return false;
                    }
                    return StringUtils.equal(record.getOwner(), owner);
                })
                .build().find();

        return messageRecords;
    }

    @Override
    public void delete(String owner, Friend with) {
        List<MessageRecord> messageRecords = messageRecordBox.query()
                .equal(MessageRecord_.owner, owner)
                .equal(MessageRecord_.withFriendId, with.getId())
                .build().find();
        if (messageRecords != null && messageRecords.size() > 0) {
            messageRecordBox.remove(messageRecords);
        }
    }

    @Override
    public void delete(String owner, Group with) {
        List<MessageRecord> messageRecords = messageRecordBox.query()
                .equal(MessageRecord_.owner, owner)
                .equal(MessageRecord_.withFriendId, with.getId())
                .build().find();
        if (messageRecords != null && messageRecords.size() > 0) {
            messageRecordBox.remove(messageRecords);
        }
    }



    @Override
    public void delete(long... ids) {
        if (ids.length > 0) {
            messageRecordBox.remove(ids);
        }
    }

    @Override
    public void delete(MessageRecord... records) {
        if (records.length > 0) {
            messageRecordBox.remove(records);
        }
    }

    /**
     * DESC: 更新消息记录
     *
     * @param resetNewMsgCount 标志是否需要清空新消息条数
     * Created by jinphy, on 2018/3/14, at 9:17
     */
    @Override
    public void update(Message msg,boolean resetNewMsgCount) {
        MessageRecord record;
        String with = msg.getWith();
        Friend friend=null;
        Group group = null;
        if ((friend = FriendRepository.getInstance().get(msg.getOwner(), with)) != null) {
            record = get(msg.getOwner(), friend);
        } else if ((group = GroupRepository.getInstance().get(with, msg.getOwner())) != null) {
            record = get(msg.getOwner(), group);
        } else {
            return;
        }
        if (record != null) {
            record.setLastMsg(msg);
            if (resetNewMsgCount) {
                record.setNewMsgCount(0);
            } else {
                record.addNewMsgCount();
            }
        } else {
            record = new MessageRecord();
            record.setLastMsg(msg);
            record.setOwner(msg.getOwner());
            record.setWith(friend);
            record.setWith(group);
            if (!resetNewMsgCount) {
                record.addNewMsgCount();
            }
        }
        messageRecordBox.put(record);
    }
}
