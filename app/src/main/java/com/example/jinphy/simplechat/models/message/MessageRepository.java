package com.example.jinphy.simplechat.models.message;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.group.GroupRepository;
import com.example.jinphy.simplechat.models.member.MemberRepository;
import com.example.jinphy.simplechat.models.message_record.MessageRecordRepository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.objectbox.Box;

/**
 * DESC:
 * Created by jinphy on 2018/1/18.
 */

public class MessageRepository extends BaseRepository implements MessageDataSource {

    private Box<Message> messageBox;

    FriendRepository friendRepository;
    GroupRepository groupRepository;
    MessageRecordRepository mrRepository;
    MemberRepository memberRepository;


    private static class InstanceHolder{
        static final MessageRepository DEFAULT = new MessageRepository();
    }

    public static MessageRepository getInstance() {
        return InstanceHolder.DEFAULT;
    }

    private MessageRepository() {
        messageBox = App.boxStore().boxFor(Message.class);
        friendRepository = FriendRepository.getInstance();
        groupRepository = GroupRepository.getInstance();
        mrRepository = MessageRecordRepository.getInstance();
        memberRepository = MemberRepository.getInstance();
    }

    /**
     * DESC: 保存接收的信息
     * <p>
     * Created by jinphy, on 2018/1/18, at 9:01
     */
    public void saveReceive(Message... messages) {
        if (messages.length > 0) {
            messageBox.put(messages);
        }
    }

    @Override
    public void saveReceive(List<Message> messages) {
        if (messages != null && messages.size() > 0) {
            messageBox.put(messages);
        }
    }

    /**
     * DESC: 保存发送的信息
     * Created by jinphy, on 2018/3/4, at 15:18
     */
    public void saveSend(Message message) {
        if (message == null) {
            return;
        }
        messageBox.put(message);
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

    public synchronized void update(Message message) {
        if (message != null) {
            messageBox.put(message);
        }
    }

    @Override
    public void update(List<Message> messages) {
        if (messages != null && messages.size() > 0) {
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

    @Override
    public Message get(long id) {
        Message message = messageBox.get(id);
        message.setExtra(message.getExtra());
        return message;
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
        List<Message> messages = messageBox.query()
                .equal(Message_.owner, owner)
                .equal(Message_.with, with)
                .build()
                .find();
        for (Message message : messages) {
            if (message.isNew()) {
                message.setNew(false);
                messageBox.put(message);
            }
        }
        return messages;
    }

    @Override
    public List<Message> loadNew(String owner, String with) {
        List<Message> messages = messageBox.query()
                .equal(Message_.owner, owner)
                .equal(Message_.with, with)
                .equal(Message_.isNew, true)
                .build()
                .find();
        if (messages != null) {
            for (Message message : messages) {
                message.setNew(false);
            }
            messageBox.put(messages);

            return messages;
        } else {
            return new LinkedList<>();
        }
    }

    public long count(String with, String owner) {
        return messageBox.query()
                .equal(Message_.with, with)
                .equal(Message_.owner,owner)
                .build().count();
    }

    public long countNew(String with,String owner) {
        return messageBox.query()
                .equal(Message_.with, with)
                .equal(Message_.owner, owner)
                .equal(Message_.isNew, true)
                .build().count();
    }

    public long count(String with, String owner, String contentType) {
        return messageBox.query()
                .equal(Message_.with, with)
                .equal(Message_.owner, owner)
                .equal(Message_.contentType, contentType)
                .build().count();
    }

    public long countNew(String with,String owner, String contentType) {
        return messageBox.query()
                .equal(Message_.with, with)
                .equal(Message_.owner, owner)
                .equal(Message_.contentType, contentType)
                .equal(Message_.isNew, true)
                .build().count();
    }


    @Override
    public List<Message> loadSystemMsg(String owner, String contentType) {
        return messageBox.query()
                .equal(Message_.owner, owner)
                .equal(Message_.contentType, contentType)
                .build()
                .find();
    }


    @Override
    public void sendMsg(BaseRepository.Task<String> task) {
        Api.<String>common(null)
                .setup(api -> handleBuilder(api, task))
                .autoShowNo(false)
                .dataType(Api.Data.MODEL,String.class)
                .showProgress(false)
                .path(Api.Path.sendMsg)
                .request();
    }
}
