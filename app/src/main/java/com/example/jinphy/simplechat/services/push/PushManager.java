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


    /**
     * DESC: 处理PushClient发送过来的消息
     * Created by jinphy, on 2018/3/16, at 14:56
     */
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
                    // 1
                    friendRepository.getOnline(
                            null,
                            message.getOwner(),
                            message.getExtra(),//好友的账号保存在该字段中
                            friend1 -> {
                                if (Message.TYPE_SYSTEM_ADD_FRIEND
                                        .equals(message.getContentType())) {
                                    if (Friend.status_deleted.equals(friend1.getStatus())) {
                                        friend1.setStatus(Friend.status_waiting);
                                        friendRepository.save(friend1);
                                    }
                                }
                                EventBus.getDefault().post(new EBService());
                            });
                    break;
                case Message.TYPE_SYSTEM_DELETE_FRIEND:
                    // 2
                    String account2 = message.getExtra();//好友的账号保存在该字段中
                    Friend friend2 = FriendRepository.getInstance().get(message.getOwner(), account2);
                    friend2.setStatus(Friend.status_deleted);
                    friendRepository.update(friend2);
                    Message msg2 = new Message();
                    msg2.setOwner(message.getOwner());
                    msg2.setWith(Friend.system);
                    msg2.setContentType(Message.TYPE_SYSTEM_NOTICE);
                    msg2.setExtra("删除好友");
                    msg2.setContent(friend2.getShowName() + "（" + account2 + ")" + message.getContent());
                    msg2.setCreateTime(System.currentTimeMillis()+"");
                    msg2.setSourceType(Message.RECEIVE);

                    MessageRepository.getInstance().saveReceive(msg2);
                    MessageRecordRepository.getInstance().update(msg2, false);

                    break;
                case Message.TYPE_SYSTEM_NEW_GROUP:
                    // 3
                    String msgContent = message.getContent();
                    // split[0] :groupNo, split[1]:群成员的账号
                    String[] split3 = message.getExtra().split("@");
                    String groupNo3 = split3[0];
                    String[] member3 = GsonUtils.toBean(split3[1], String[].class);

                    // 从服务器获取群聊对象
                    groupRepository.getOnline(
                            null,
                            message.getOwner(),
                            groupNo3,
                            ()->{
                                // 群聊信息获取成功后，加载群聊中的成员
                                Group tempGroup = groupRepository.get(groupNo3, message.getOwner());

                                // 加载所有成员的好友信息
                                for (String member : member3) {
                                    Friend oldFriend2 = friendRepository.get(message.getOwner(), member);
                                    if (oldFriend2 != null) {
                                        memberRepository.saveNew(oldFriend2, groupNo3);
                                        continue;
                                    }
                                    friendRepository.getOnline(
                                            null,
                                            message.getOwner(),
                                            member,
                                            friend3->{
                                                memberRepository.saveNew(friend3, groupNo3);
                                                EventBus.getDefault().post(new EBService());
                                            });
                                }
                                EventBus.getDefault().post(new EBService());
                                // 创建新消息
                                Message msg3 = new Message();
                                msg3.setCreateTime(System.currentTimeMillis()+"");
                                msg3.setSourceType(Message.RECEIVE);
                                msg3.setContent(msgContent);
                                msg3.setContentType(Message.TYPE_CHAT_TEXT);
                                msg3.setWith(groupNo3);
                                msg3.setOwner(message.getOwner());
                                msg3.setExtra(tempGroup.getCreator());// 群消息时，该字段表示信息发送者
                                messageRepository.saveSend(msg3);

                                messageRecordRepository.update(msg3, false);
                                EventBus.getDefault().post(new EBService());
                            }
                    );
                    break;
                case Message.TYPE_SYSTEM_APPLY_JOIN_GROUP:
                    // 4
                    String[] split4 = message.getExtra().split("@");
                    split4[3] = EncryptUtils.aesDecrypt(split4[3]);
                    message.setExtra(TextUtils.join("@", split4));
                    break;
                case Message.TYPE_SYSTEM_RELOAD_GROUP:
                    // 5
                    groupRepository.getOnline(
                            null,
                            message.getOwner(),
                            message.getExtra(),//需要重新加载的群聊的群号在extra字段中
                            ()-> EventBus.getDefault().post(new EBService()));
                    break;
                case Message.TYPE_SYSTEM_NEW_MEMBER:
                    // 6
                    String groupNo6 = message.getContent();
                    String account6 = message.getExtra();
                    Friend oldFriend6 = friendRepository.get(message.getOwner(), account6);
                    Group group6 = groupRepository.get(groupNo6, message.getOwner());

                    // 创建新消息
                    Message msg6 = new Message();
                    msg6.setCreateTime(System.currentTimeMillis()+"");
                    msg6.setSourceType(Message.RECEIVE);
                    msg6.setContent(account6 + "加入群聊：" + group6.getName() + "(" + groupNo6 + ")");
                    msg6.setContentType(Message.TYPE_SYSTEM_NOTICE);
                    msg6.setWith(Friend.system);
                    msg6.setOwner(message.getOwner());
                    msg6.setExtra("加入群聊");// 群消息时，该字段表示信息发送者
                    EventBus.getDefault().post(new EBService());
                    if (oldFriend6 != null) {
                        memberRepository.saveNew(oldFriend6, groupNo6);
                        messageRepository.saveSend(msg6);
                        messageRecordRepository.update(msg6, false);
                        EventBus.getDefault().post(new EBService());
                    } else {
                        friendRepository.getOnline(
                                null,
                                message.getOwner(),
                                account6,
                                friend6->{
                                    memberRepository.saveNew(friend6, groupNo6);
                                    messageRepository.saveSend(msg6);
                                    messageRecordRepository.update(msg6, false);
                                    EventBus.getDefault().post(new EBService());
                                    // TODO: 2018/3/16 在群聊中增加新成员入群的提示信息
                                });

                    }
                    break;
                case Message.TYPE_SYSTEM_BREAK_GROUP:
                    // 7
                    String groupNo7 = message.getExtra();
                    Group group7 = groupRepository.get(groupNo7, message.getOwner());
                    group7.setMyGroup(false);
                    group7.setBroke(true);
                    groupRepository.update(group7);
                    memberRepository.removeForGroup(group7);

                    // 创建新消息
                    Message msg7 = new Message();
                    msg7.setCreateTime(System.currentTimeMillis()+"");
                    msg7.setSourceType(Message.RECEIVE);
                    msg7.setContent("群聊："+group7.getName()+"（"+groupNo7+"）"+message.getContent());
                    msg7.setContentType(Message.TYPE_SYSTEM_NOTICE);
                    msg7.setWith(Friend.system);
                    msg7.setOwner(message.getOwner());
                    msg7.setExtra("解散群聊");// 群消息时，该字段表示信息发送者
                    messageRepository.saveReceive(msg7);

                    messageRecordRepository.update(msg7, false);
                    EventBus.getDefault().post(new EBService());

                    break;
                case Message.TYPE_SYSTEM_DELETE_MEMBER:
                    // 8
                    String[] split8 = message.getExtra().split("@");
                    String groupNo8 = split8[0];
                    String account8 = split8[1];
                    Group group8 = groupRepository.get(groupNo8, message.getOwner());
                    memberRepository.removeFromGroup(group8, account8);

                    // 创建新消息
                    Message msg8 = new Message();
                    msg8.setCreateTime(System.currentTimeMillis()+"");
                    msg8.setSourceType(Message.RECEIVE);
                    msg8.setContent(message.getContent()+"："+group8.getName()+"("+groupNo8+")");
                    msg8.setContentType(Message.TYPE_SYSTEM_NOTICE);
                    msg8.setWith(Friend.system);
                    msg8.setOwner(message.getOwner());
                    msg8.setExtra("退出群聊");// 群消息时，该字段表示信息发送者
                    messageRepository.saveReceive(msg8);

                    messageRecordRepository.update(msg8, false);
                    EventBus.getDefault().post(new EBService());
                    break;
                case Message.TYPE_SYSTEM_MEMBER_ALLOW_CHAT:
                    // 9
                    String[] split9 = message.getExtra().split("@");
                    String groupNo9 = split9[0];
                    String account9 = split9[1];
                    User user9 = userRepository.currentUser();
                    Friend friend9 = friendRepository.get(user9.getAccount(), account9);
                    Member member = memberRepository.get(groupNo9, account9, user9.getAccount());
                    if (member != null) {
                        member.setAllowChat(!member.isAllowChat());
                        memberRepository.update(member);
                    }
                    // TODO: 2018/3/16 在这里生成一条新的本地消息，类型是在聊天中提示
                    String content = message.getContent();//content: 已被群主禁言！


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
