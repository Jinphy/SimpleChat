package com.example.jinphy.simplechat.models.message;

import com.example.jinphy.simplechat.application.App;
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
     * DESC: 保存接收的信息
     * <p>
     * Created by jinphy, on 2018/1/18, at 9:01
     */
    public Message[] saveReceive(Message... messages) {
        List<Message> list = new LinkedList<>();
        for (Message message : messages) {
            if (message.needSave()) {
                message.setId(0);
                list.add(message);
            }
            String owner;
            String account;
            // 如果是添加新好友的消息，则先预加载该好友的信息
            switch (message.getContentType()) {
                case Message.TYPE_SYSTEM_ADD_FRIEND:
                case Message.TYPE_SYSTEM_ADD_FRIEND_AGREE:
                case Message.TYPE_SYSTEM_RELOAD_FRIEND:
                    owner = message.getOwner();
                    account = message.getExtra();//好友的账号保存在该字段中
                    FriendRepository.getInstance().getOnline(
                            null,
                            owner,
                            account,
                            () -> EventBus.getDefault().post(new EBService()));

                    break;
                case Message.TYPE_SYSTEM_DELETE_FRIEND:
                    owner = message.getOwner();
                    account = message.getExtra();//好友的账号保存在该字段中
                    Friend friend = FriendRepository.getInstance().get(owner, account);
                    friend.setStatus(Friend.status_deleted);
                    FriendRepository.getInstance().update(friend);
                    break;
                case Message.TYPE_SYSTEM_NEW_GROUP:
                    String msgContent = message.getContent();
                    // split[0] :groupNo, split[1]:群成员的账号
                    String[] split = message.getExtra().split("@");
                    String groupNo = split[0];
                    String[] members = GsonUtils.toBean(split[1], String[].class);

                    // 从服务器获取群聊对象
                    GroupRepository.getInstance().getOnline(
                            null,
                            message.getOwner(),
                            groupNo,
                            ()->{
                                Group group = GroupRepository.getInstance().get(groupNo, message.getOwner());
                                // 群聊信息获取成功后，加载群聊中的成员

                                // 加载所有成员的好友信息
                                FriendRepository friendRepository = FriendRepository.getInstance();
                                for (String member : members) {
                                    friendRepository.getOnline(
                                            null,
                                            message.getOwner(),
                                            member,
                                            ()->{
                                                Member newMember = new Member();
                                                newMember.setStatus(Member.STATUS_OK);
                                                newMember.setPerson(friendRepository.get(message.getOwner(), member));
                                                newMember.setOwner(message.getOwner());
                                                newMember.setGroupNo(groupNo);
                                                newMember.setAllowChat(true);
                                                newMember.setId(0);
                                                MemberRepository mr = MemberRepository.getInstance();
                                                mr.save(newMember);
                                                group.addMembers(newMember);
                                                EventBus.getDefault().post(new EBService());
                                            });
                                }

                                // 创建新消息
                                Message newMsg = new Message();
                                newMsg.setCreateTime(System.currentTimeMillis()+"");
                                newMsg.setSourceType(Message.RECEIVE);
                                newMsg.setContent(msgContent);
                                newMsg.setContentType(Message.TYPE_CHAT_TEXT);
                                newMsg.setWith(groupNo);
                                newMsg.setOwner(message.getOwner());
                                newMsg.setExtra(group.getCreator());// 群消息时，该字段表示信息发送者

                                MessageRepository.getInstance().saveSend(newMsg);
                                MessageRecord record = new MessageRecord();
                                record.setNewMsgCount(1);
                                record.setLastMsg(newMsg);
                                record.setWith(group);
                                record.setOwner(message.getOwner());
                                MessageRecordRepository.getInstance().saveOrUpdate(record);
                                EventBus.getDefault().post(new EBService());
                            }
                    );
                    break;

                default:
                    break;
            }
        }
        if (messages.length > 0) {
            messageBox.put(list);
        }
        messages = new Message[list.size()];

        return list.toArray(messages);
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
            messageBox.put(messages);
        }
    }

    @Override
    public void update(List<Message> messages) {
        if (messages == null || messages.size() == 0) {
            return;
        }
        messageBox.put(messages);
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

    @Override
    public List<Message> loadSystemMsg(String owner, String contentType) {
        return messageBox.query()
                .equal(Message_.owner, owner)
                .equal(Message_.contentType, contentType)
                .build()
                .find();
    }
}
