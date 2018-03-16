package com.example.jinphy.simplechat.models.message;

import com.example.jinphy.simplechat.application.App;
import com.example.jinphy.simplechat.models.event_bus.EBInteger;
import com.example.jinphy.simplechat.models.event_bus.EBService;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.group.GroupRepository;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.member.MemberRepository;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.models.message_record.MessageRecordRepository;
import com.example.jinphy.simplechat.utils.GsonUtils;

import org.greenrobot.eventbus.EventBus;

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

    /**
     * DESC: 保存发送的信息
     * Created by jinphy, on 2018/3/4, at 15:18
     */
    public void saveSend(Message message) {
        if (message == null) {
            return;
        }
        message.setId(0);
        messageBox.put(message);
    }

    /**
     * DESC: 更新消息
     * Created by jinphy, on 2018/1/18, at 9:02
     */
    public void update(Message... messages) {
        if (messages.length > 0) {
            for (Message message : messages) {
                update(message);
            }
        }
    }

    public synchronized void update(Message message) {
        if (message == null) {
            return;
        }
        Message old = messageBox.get(message.getId());
        if (old != null) {
            old.update(message);
            messageBox.put(old);
        } else {
            message.setId(0);
            messageBox.put(message);
        }
    }

    @Override
    public void update(List<Message> messages) {
        if (messages == null || messages.size() == 0) {
            return;
        }
        for (Message message : messages) {
            update(message);
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

    public long count(String with,String owner) {
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
}
