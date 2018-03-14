package com.example.jinphy.simplechat.services.push;

import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver;
import com.example.jinphy.simplechat.models.event_bus.EBService;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.group.GroupRepository;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.member.MemberRepository;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.models.message_record.MessageRecordRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * DESC:
 * Created by jinphy on 2018/1/18.
 */

public class PushManager {
    private UserRepository userRepository;

    private FriendRepository friendRepository;

    private MessageRepository messageRepository;

    private MessageRecordRepository messageRecordRepository;

    private GroupRepository groupRepository;

    private MemberRepository memberRepository;

    private WeakReference<PushService> pushService;

    private static class InstanceHolder{
        static final PushManager DEFAULT = new PushManager();
    }

    public static PushManager getInstance(PushService pushService) {
        InstanceHolder.DEFAULT.pushService = new WeakReference<>(pushService);
        return InstanceHolder.DEFAULT;
    }

    private PushManager() {
        userRepository = UserRepository.getInstance();
        friendRepository = FriendRepository.getInstance();
        messageRepository = MessageRepository.getInstance();
        messageRecordRepository = MessageRecordRepository.getInstance();
        groupRepository = GroupRepository.getInstance();
        memberRepository = MemberRepository.getInstance();
    }

    public User getUser() {
        if (userRepository.hasLogin()) {
            return userRepository.currentUser();
        }
        return null;
    }

    public void handleMessage(List<Map<String, String>> messagesList) {
        LogUtils.e(messagesList);
        // 解析消息
        Message[] messages = Message.parse(messagesList);

        // 保存消息
        messages = filterMessages(messages);

        // 更新消息记录
        updateMessageRecord(messages);

        EventBus.getDefault().post(new EBService());
    }


    private Message[] filterMessages(Message... messages) {
        List<Message> list = new LinkedList<>();
        for (Message message : messages) {
            if (message.needSave()) {
                message.setId(0);
                list.add(message);
            }
            switch (message.getContentType()) {
                case Message.TYPE_SYSTEM_ADD_FRIEND:
                case Message.TYPE_SYSTEM_ADD_FRIEND_AGREE:
                case Message.TYPE_SYSTEM_RELOAD_FRIEND:
                    friendRepository.getOnline(
                            null,
                            message.getOwner(),
                            message.getExtra(),//好友的账号保存在该字段中
                            friend -> {
                                if (Message.TYPE_SYSTEM_ADD_FRIEND
                                        .equals(message.getContentType())) {
                                    if (Friend.status_deleted.equals(friend.getStatus())) {
                                        friend.setStatus(Friend.status_waiting);
                                        friendRepository.save(friend);
                                    }
                                }
                                EventBus.getDefault().post(new EBService());
                            });
                    break;
                case Message.TYPE_SYSTEM_DELETE_FRIEND:
                    String account1 = message.getExtra();//好友的账号保存在该字段中
                    Friend friend = FriendRepository.getInstance().get(message.getOwner(), account1);
                    friend.setStatus(Friend.status_deleted);
                    friendRepository.update(friend);
                    Message msg1 = new Message();
                    msg1.setOwner(message.getOwner());
                    msg1.setWith(Friend.system);
                    msg1.setContentType(Message.TYPE_SYSTEM_NOTICE);
                    msg1.setExtra("删除好友");
                    msg1.setContent(friend.getShowName() + "（" + account1 + ")" + message.getContent());
                    msg1.setCreateTime(System.currentTimeMillis()+"");
                    msg1.setSourceType(Message.RECEIVE);

                    MessageRepository.getInstance().saveReceive(msg1);
                    MessageRecordRepository.getInstance().update(msg1, false);

                    break;
                case Message.TYPE_SYSTEM_NEW_GROUP:
                    String msgContent = message.getContent();
                    // split[0] :groupNo, split[1]:群成员的账号
                    String[] split2 = message.getExtra().split("@");
                    String groupNo2 = split2[0];
                    String[] members = GsonUtils.toBean(split2[1], String[].class);

                    // 从服务器获取群聊对象
                    groupRepository.getOnline(
                            null,
                            message.getOwner(),
                            groupNo2,
                            ()->{
                                // 群聊信息获取成功后，加载群聊中的成员
                                Group tempGroup = groupRepository.get(groupNo2, message.getOwner());

                                // 加载所有成员的好友信息
                                for (String member : members) {
                                    friendRepository.getOnline(
                                            null,
                                            message.getOwner(),
                                            member,
                                            friend1->{
                                                Member newMember = new Member();
                                                newMember.setStatus(Member.STATUS_OK);
                                                newMember.setPerson(friend1);
                                                newMember.setOwner(message.getOwner());
                                                newMember.setGroupNo(groupNo2);
                                                newMember.setAllowChat(true);
                                                newMember.setId(0);
                                                memberRepository.save(newMember);
                                                EventBus.getDefault().post(new EBService());
                                            });
                                }

                                // 创建新消息
                                Message newMsg = new Message();
                                newMsg.setCreateTime(System.currentTimeMillis()+"");
                                newMsg.setSourceType(Message.RECEIVE);
                                newMsg.setContent(msgContent);
                                newMsg.setContentType(Message.TYPE_CHAT_TEXT);
                                newMsg.setWith(groupNo2);
                                newMsg.setOwner(message.getOwner());
                                newMsg.setExtra(tempGroup.getCreator());// 群消息时，该字段表示信息发送者
                                messageRepository.saveSend(newMsg);

                                messageRecordRepository.update(newMsg, false);
                                EventBus.getDefault().post(new EBService());
                            }
                    );
                    break;
                case Message.TYPE_SYSTEM_APPLY_JOIN_GROUP:
                    String[] split6 = message.getExtra().split("@");
                    split6[3] = EncryptUtils.aesDecrypt(split6[3]);
                    message.setExtra(TextUtils.join("@", split6));
                    break;
                case Message.TYPE_SYSTEM_RELOAD_GROUP:
                    groupRepository.getOnline(
                            null,
                            message.getOwner(),
                            message.getExtra(),//需要重新加载的群聊的群号在extra字段中
                            ()-> EventBus.getDefault().post(new EBService()));
                    break;
                case Message.TYPE_SYSTEM_NEW_MEMBER:
                    String groupNo3 = message.getContent();
                    String account3 = message.getExtra();
                    friendRepository.getOnline(
                            null,
                            message.getOwner(),
                            account3,
                            friend1->{
                                Member newMember = new Member();
                                newMember.setStatus(Member.STATUS_OK);
                                newMember.setPerson(friend1);
                                newMember.setOwner(message.getOwner());
                                newMember.setGroupNo(groupNo3);
                                newMember.setAllowChat(true);
                                newMember.setId(0);
                                memberRepository.save(newMember);
                                EventBus.getDefault().post(new EBService());
                            });
                    break;
                case Message.TYPE_SYSTEM_BREAK_GROUP:
                    String groupNo4 = message.getExtra();
                    Group group4 = groupRepository.get(groupNo4, message.getOwner());
                    group4.setMyGroup(false);
                    group4.setBroke(true);
                    groupRepository.update(group4);
                    memberRepository.removeForGroup(group4);

                    // 创建新消息
                    Message msg4 = new Message();
                    msg4.setCreateTime(System.currentTimeMillis()+"");
                    msg4.setSourceType(Message.RECEIVE);
                    msg4.setContent("群聊："+group4.getName()+"（"+groupNo4+"）"+message.getContent());
                    msg4.setContentType(Message.TYPE_SYSTEM_NOTICE);
                    msg4.setWith(Friend.system);
                    msg4.setOwner(message.getOwner());
                    msg4.setExtra("解散群聊");// 群消息时，该字段表示信息发送者
                    messageRepository.saveReceive(msg4);

                    messageRecordRepository.update(msg4, false);
                    EventBus.getDefault().post(new EBService());

                    break;
                case Message.TYPE_SYSTEM_DELETE_MEMBER:
                    String[] split5 = message.getExtra().split("@");
                    String groupNo5 = split5[0];
                    String account5 = split5[1];
                    Group group5 = groupRepository.get(groupNo5, message.getOwner());
                    memberRepository.removeFromGroup(group5, account5);

                    // 创建新消息
                    Message msg5 = new Message();
                    msg5.setCreateTime(System.currentTimeMillis()+"");
                    msg5.setSourceType(Message.RECEIVE);
                    msg5.setContent(message.getContent());
                    msg5.setContentType(Message.TYPE_SYSTEM_NOTICE);
                    msg5.setWith(Friend.system);
                    msg5.setOwner(message.getOwner());
                    msg5.setExtra("退出群聊");// 群消息时，该字段表示信息发送者
                    messageRepository.saveReceive(msg5);

                    messageRecordRepository.update(msg5, false);
                    EventBus.getDefault().post(new EBService());
                    break;
                default:
                    break;
            }
        }
        if (list.size() > 0) {
            messages = new Message[list.size()];
            messages = list.toArray(messages);
            messageRepository.saveReceive(messages);
            return messages;
        }
        return null;
    }



    /**
     * DESC: 更新消息记录
     * Created by jinphy, on 2018/1/18, at 12:21
     */
    private void updateMessageRecord(Message[] messages) {
        if (messages == null || messages.length == 0) {
            return;
        }
        // 记录是否有需要退出当前账号的消息
        Message logoutMessage = null;
        for (Message message : messages) {
            if (Message.TYPE_SYSTEM_ACCOUNT_INVALIDATE.equals(message.getContentType())) {
                logoutMessage = message;
            }
            if (!message.needSave()) {
                continue;
            }
            messageRecordRepository.update(message, false);

        }
        if (logoutMessage != null) {
            // 账号非法，需要强制登出
            AppBroadcastReceiver.send(
                    pushService.get(),
                    AppBroadcastReceiver.LOGOUT,
                    logoutMessage.getContent());
        }
    }

}
