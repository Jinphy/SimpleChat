package com.example.jinphy.simplechat.models.message_record;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;

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
        return messageRecordBox.query()
                .filter(record -> {
                    if (record.getWith() == null) {
                        messageRecordBox.remove(record);
                        return false;
                    }
                    return true;
                })
                .build().find();
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
}
