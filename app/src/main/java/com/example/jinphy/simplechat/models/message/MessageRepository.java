package com.example.jinphy.simplechat.models.message;

import com.example.jinphy.simplechat.application.App;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.objectbox.Box;

/**
 * DESC:
 * Created by jinphy on 2018/1/18.
 */

public class MessageRepository implements MessageDataSource {

    private Box<Message> messageBox;


    private static class InstanceHolder{
        static final MessageRepository DEFAULT = new MessageRepository();
    }

    public static MessageRepository getInstance() {
        return InstanceHolder.DEFAULT;
    }

    private MessageRepository() {
        messageBox = App.boxStore().boxFor(Message.class);
    }

    /**
     * DESC: 保存新消息
     * <p>
     * Created by jinphy, on 2018/1/18, at 9:01
     */
    public void save(Message... messages) {
        List<Message> list = new LinkedList<>();
        for (Message message : messages) {
            if (message.needSave()) {
                message.setId(0);
                list.add(message);
            }
        }
        if (messages.length > 0) {
            messageBox.put(list);
        }
    }

    /**
     * DESC: 更新消息
     * Created by jinphy, on 2018/1/18, at 9:02
     */
    public void update(Message... messages) {
        if (messages.length > 0) {
            messageBox.put(messages);
        }
    }

    /**
     * DESC: 删除消息
     * Created by jinphy, on 2018/1/18, at 9:05
     */
    public void delete(Message...messages) {
        if (messages.length > 0) {
            messageBox.remove(messages);
        }
    }

    /**
     * DESC: 删除消息
     * Created by jinphy, on 2018/1/18, at 9:06
     */
    public void delete(long...ids) {
        if (ids.length > 0) {
            messageBox.remove(ids);
        }
    }

    /**
     * DESC: 删除消息
     * Created by jinphy, on 2018/1/18, at 9:08
     */
    public void delete(Collection<Message> messages) {
        if (messages != null && messages.size() > 0) {
            messageBox.remove(messages);
        }
    }

    /**
     * DESC: 删除消息
     * Created by jinphy, on 2018/1/18, at 9:27
     */
    public void delete(String owner) {
        messageBox.remove(messageBox.query().equal(Message_.owner, owner).build().find());
    }

    /**
     * DESC: 删除消息
     * Created by jinphy, on 2018/1/18, at 9:31
     */
    public void delete(String owner, String with) {
        messageBox.remove(messageBox.query()
                .equal(Message_.owner,owner)
                .equal(Message_.with,with)
                .build()
                .find()
        );
    }

    /**
     * DESC: 加载消息
     *
     *
     * @param owner 消息的拥有者，是一个账号，即该消息属于哪个账户的
     * @param with  消息的聊天对象，是一个账号
     *
     * Created by jinphy, on 2018/1/18, at 9:11
     */
    public List<Message> load(String owner, String with) {
        return messageBox.query()
                .equal(Message_.owner, owner)
                .equal(Message_.with, with)
                .build()
                .find();
    }

}
