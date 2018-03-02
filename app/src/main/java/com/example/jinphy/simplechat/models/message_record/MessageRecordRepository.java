package com.example.jinphy.simplechat.models.message_record;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.models.friend.Friend;
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
                .equal(MessageRecord_.withId, with.getId())
                .build().findFirst();
    }

    @Override
    public List<MessageRecord> load(String owner) {
        return messageRecordBox.query()
                .filter(record-> StringUtils.equal(owner, record.getOwner())
                        && record.getFriend() != null)
                .build().find();
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
