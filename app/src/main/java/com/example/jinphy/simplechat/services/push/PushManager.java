package com.example.jinphy.simplechat.services.push;

import android.os.IBinder;
import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver;
import com.example.jinphy.simplechat.models.event_bus.EBService;
import com.example.jinphy.simplechat.models.file_task.FileTask;
import com.example.jinphy.simplechat.models.file_task.FileTaskRepository;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.group.GroupRepository;
import com.example.jinphy.simplechat.models.member.Member;
import com.example.jinphy.simplechat.models.member.MemberRepository;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.models.message_record.MessageRecordRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;
import com.example.jinphy.simplechat.services.common_service.aidl.BinderFactory;
import com.example.jinphy.simplechat.services.common_service.aidl.service.DownloadFileBinder;
import com.example.jinphy.simplechat.services.common_service.aidl.service.IDownloadFileBinder;
import com.example.jinphy.simplechat.utils.EncryptUtils;
import com.example.jinphy.simplechat.utils.FileUtils;
import com.example.jinphy.simplechat.utils.GsonUtils;
import com.example.jinphy.simplechat.utils.ImageUtil;
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

    private FileTaskRepository fileTaskRepository;

    private WeakReference<PushService> pushService;

    private IDownloadFileBinder downloader;

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
        fileTaskRepository = FileTaskRepository.getInstance();
        initBinder();
    }


    private void initBinder() {
        IBinder binder = BinderFactory.getBinder(BinderFactory.TYPE_DOWNLOAD_FILE);
        downloader = DownloadFileBinder.asInterface(binder);
    }


    /**
     * DESC: 处理PushClient发送过来的消息
     * Created by jinphy, on 2018/3/16, at 14:56
     */
    public synchronized void handleMessage(List<Map<String, String>> messagesList) {
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
                    getFriend(message);
                    break;
                case Message.TYPE_SYSTEM_DELETE_FRIEND:
                    // 2
                    deleteFriend(message);
                    break;
                case Message.TYPE_SYSTEM_NEW_GROUP:
                    // 3
                    getNewGroup(message);
                    break;
                case Message.TYPE_SYSTEM_APPLY_JOIN_GROUP:
                    // 4
                    joinGroup(message);
                    break;
                case Message.TYPE_SYSTEM_RELOAD_GROUP:
                    // 5
                    updateGroup(message);
                    break;
                case Message.TYPE_SYSTEM_NEW_MEMBER:
                    // 6
                    getNewMember(message);
                    break;
                case Message.TYPE_SYSTEM_BREAK_GROUP:
                    // 7
                    deleteGroup(message);
                    break;
                case Message.TYPE_SYSTEM_DELETE_MEMBER:
                    // 8
                    deleteMember(message);
                    break;
                case Message.TYPE_SYSTEM_MEMBER_ALLOW_CHAT:
                    // 9
                    updateMember(message);
                    break;
                case Message.TYPE_CHAT_TEXT:
                    handleChatTextMsg(message);
                    break;
                case Message.TYPE_CHAT_IMAGE:
                    handleChatImageMsg(message);
                    break;
                case Message.TYPE_CHAT_VOICE:
                    handleChatVoiceMsg(message);
                    break;
                case Message.TYPE_CHAT_FILE:
                    handleChatFileMsg(message);
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


    /**
     * DESC: 获取好友
     * Created by jinphy, on 2018/3/17, at 13:09
     */
    private void getFriend(Message message) {
        friendRepository.getOnline(
                null,
                message.getOwner(),
                message.getExtra(),//好友的账号保存在该字段中
                friend1 -> {
                    if (Message.TYPE_SYSTEM_ADD_FRIEND.equals(message.getContentType())) {
                        if (Friend.status_deleted.equals(friend1.getStatus())) {
                            friend1.setStatus(Friend.status_waiting);
                            friendRepository.save(friend1);
                        }
                    }
                    EventBus.getDefault().post(new EBService());
                });

    }


    /**
     * DESC: 删除好友
     * Created by jinphy, on 2018/3/17, at 13:11
     */
    private void deleteFriend(Message message) {
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

    }

    /**
     * DESC: 获取群聊
     * Created by jinphy, on 2018/3/17, at 13:12
     */
    private void getNewGroup(Message message) {
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
                    Message msg = new Message();
                    msg.setCreateTime(System.currentTimeMillis()+"");
                    msg.setSourceType(Message.RECEIVE);
                    msg.setContent(msgContent);
                    msg.setContentType(Message.TYPE_CHAT_TEXT);
                    msg.setWith(groupNo3);
                    msg.setOwner(message.getOwner());
                    msg.extra(Message.KEY_SENDER, tempGroup.getOwner());
                    messageRepository.saveSend(msg);

                    messageRecordRepository.update(msg, false);
                    EventBus.getDefault().post(new EBService());
                }
        );
    }

    /**
     * DESC: 处理加入群聊的消息
     * Created by jinphy, on 2018/3/17, at 13:13
     */
    private void joinGroup(Message message) {
        String[] split4 = message.getExtra().split("@");
        split4[3] = EncryptUtils.aesDecrypt(split4[3]);
        message.setExtra(TextUtils.join("@", split4));
    }

    /**
     * DESC: 更新群聊
     * Created by jinphy, on 2018/3/17, at 13:16
     */
    private void updateGroup(Message message) {
        groupRepository.getOnline(
                null,
                message.getOwner(),
                message.getExtra(),//需要重新加载的群聊的群号在extra字段中
                ()-> EventBus.getDefault().post(new EBService()));
    }

    /**
     * DESC: 有新成员加入，获取新成员
     * Created by jinphy, on 2018/3/17, at 13:16
     */
    private void getNewMember(Message message) {
        String groupNo = message.getContent();
        String[] accounts = GsonUtils.toBean(message.getExtra(), String[].class);
        Group group = groupRepository.get(groupNo, message.getOwner());

        // 创建新消息
        Message msg = new Message();
        msg.setCreateTime(message.getCreateTime());
        msg.setSourceType(Message.RECEIVE);
        msg.setContentType(Message.TYPE_SYSTEM_NOTICE);
        msg.setWith(Friend.system);
        msg.setOwner(message.getOwner());
        msg.setExtra("加入群聊");// 群消息时，该字段表示信息发送者
        Friend oldFriend;
        for (String account : accounts) {
            msg.setContent(account + "加入群聊：" + group.getName() + "(" + groupNo + ")");
            oldFriend = friendRepository.get(message.getOwner(), account);
            if (oldFriend != null) {
                memberRepository.saveNew(oldFriend, groupNo);
                messageRepository.saveSend(msg);
                messageRecordRepository.update(msg, false);
                EventBus.getDefault().post(new EBService());
            } else {
                friendRepository.getOnline(
                        null,
                        message.getOwner(),
                        account,
                        friend->{
                            memberRepository.saveNew(friend, groupNo);
                            messageRepository.saveSend(msg);
                            messageRecordRepository.update(msg, false);
                            EventBus.getDefault().post(new EBService());
                            // TODO: 2018/3/16 在群聊中增加新成员入群的提示信息
                        });

            }

        }
    }

    /**
     * DESC: 群聊被解散，删除群聊
     * Created by jinphy, on 2018/3/17, at 13:17
     */
    private void deleteGroup(Message message) {
        String groupNo = message.getExtra();
        Group group = groupRepository.get(groupNo, message.getOwner());
        group.setMyGroup(false);
        group.setBroke(true);
        groupRepository.update(group);
        memberRepository.removeForGroup(group);

        // 创建新消息
        Message msg = new Message();
        msg.setCreateTime(message.getCreateTime());
        msg.setSourceType(Message.RECEIVE);
        msg.setContent("群聊："+group.getName()+"（"+groupNo+"）"+message.getContent());
        msg.setContentType(Message.TYPE_SYSTEM_NOTICE);
        msg.setWith(Friend.system);
        msg.setOwner(message.getOwner());
        msg.setExtra("解散群聊");// 群消息时，该字段表示信息发送者
        messageRepository.saveReceive(msg);

        messageRecordRepository.update(msg, false);
        EventBus.getDefault().post(new EBService());

    }

    /**
     * DESC: 有成员退出群聊，删除该成员
     *
     * Created by jinphy, on 2018/3/17, at 13:19
     */
    private void deleteMember(Message message) {
        String[] split = message.getExtra().split("@");
        String groupNo = split[0];
        String account = split[1];
        Group group = groupRepository.get(groupNo, message.getOwner());

        // 创建新消息
        Message msg = new Message();
        msg.setCreateTime(message.getCreateTime());
        msg.setSourceType(Message.RECEIVE);
        msg.setContentType(Message.TYPE_SYSTEM_NOTICE);
        msg.setWith(Friend.system);
        msg.setOwner(message.getOwner());

        if (StringUtils.equal(account, message.getOwner())) {
            // 要删除的成员是自己，所以是被群主移出了群聊
            group.setMyGroup(false);
            groupRepository.update(group);
            memberRepository.removeForGroup(group);
            msg.setContent("您已被群主移出群聊："+group.getName()+"("+groupNo+")");
            msg.setExtra("移出群聊");
        } else {
            memberRepository.removeFromGroup(group, account);
            msg.setContent(message.getContent()+"："+group.getName()+"("+groupNo+")");
            msg.setExtra("退出群聊");// 群消息时，该字段表示信息发送者
        }
        messageRepository.saveReceive(msg);
        messageRecordRepository.update(msg, false);
        EventBus.getDefault().post(new EBService());
    }

    /**
     * DESC: 更新成员
     * Created by jinphy, on 2018/3/17, at 13:20
     */
    private void updateMember(Message message) {

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

    }

    /**
     * DESC: 处理聊天的文本信息
     * Created by jinphy, on 2018/3/18, at 21:40
     */
    private void handleChatTextMsg(Message message) {
        String chatWithGroup = message.removeExtra(Message.KEY_CHAT_GROUP);
        if (chatWithGroup != null && "true".equals(chatWithGroup)) {
            // 群聊
            String groupNo = message.removeExtra(Message.KEY_GROUP_NO);
            message.setWith(groupNo);

            // 设置成null时，在保存到数据库时，通过调用getExtra更新extra
            message.setExtra(null);
        }
    }

    private void handleChatImageMsg(Message message) {
        handleChatTextMsg(message);
        FileTask task = FileTask.parse(message, ImageUtil.PHOTO_PATH);
        fileTaskRepository.save(task);


        message.extra(Message.KEY_FILE_PATH, task.getFilePath());
        message.extra(Message.KEY_FILE_TASK_ID, task.getId());


        // 必须调用该方法，否则extra字段不更新
        message.setExtra(null);

    }

    private void handleChatVoiceMsg(Message message) {
        handleChatTextMsg(message);
        FileTask task = FileTask.parse(message, ImageUtil.AUDIO_PATH);
        fileTaskRepository.save(task);

        message.extra(Message.KEY_FILE_PATH, task.getFilePath());
        message.extra(Message.KEY_FILE_TASK_ID, task.getId());
        message.extra(Message.KEY_AUDIO_STATUS, Message.AUDIO_STATUS_NO);

        // 必须调用该方法，否则extra字段不更新
        message.setExtra(null);
    }

    private void handleChatFileMsg(Message message) {
        handleChatTextMsg(message);
        FileTask task = FileTask.parse(message, ImageUtil.FILE_PATH);

        String filePath = ImageUtil.FILE_PATH + "/" + message.extra(Message.KEY_ORIGINAL_FILE_NAME);
        filePath = FileUtils.createUniqueFileName(filePath);

        task.setFilePath(filePath);
        fileTaskRepository.save(task);
        message.extra(Message.KEY_FILE_PATH, task.getFilePath());
        message.extra(Message.KEY_FILE_TASK_ID, task.getId());
        message.extra(Message.KEY_FILE_STATUS, Message.FILE_STATUS_NO_DOWNLOAD);

        // 必须调用该方法，否则extra字段不更新
        message.setExtra(null);
    }
}
