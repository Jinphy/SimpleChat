package com.example.jinphy.simplechat.services.push;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver;
import com.example.jinphy.simplechat.models.event_bus.EBService;
import com.example.jinphy.simplechat.models.friend.Friend;
import com.example.jinphy.simplechat.models.friend.FriendRepository;
import com.example.jinphy.simplechat.models.group.Group;
import com.example.jinphy.simplechat.models.group.GroupRepository;
import com.example.jinphy.simplechat.models.message.Message;
import com.example.jinphy.simplechat.models.message.MessageRepository;
import com.example.jinphy.simplechat.models.message_record.MessageRecord;
import com.example.jinphy.simplechat.models.message_record.MessageRecordRepository;
import com.example.jinphy.simplechat.models.user.User;
import com.example.jinphy.simplechat.models.user.UserRepository;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
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
        messages = messageRepository.saveReceive(messages);

        // 更新消息记录
        updateMessageRecord(messages);

        EventBus.getDefault().post(new EBService());
    }

    /**
     * DESC: 更新消息记录
     * Created by jinphy, on 2018/1/18, at 12:21
     */
    private void updateMessageRecord(Message[] messages) {
        MessageRecord record;
        Friend friend=null;
        Group group=null;

        // 记录是否有需要退出当前账号的消息
        Message logoutMessage = null;
        for (Message message : messages) {
            if (Message.TYPE_SYSTEM_ACCOUNT_INVALIDATE.equals(message.getContentType())) {
                logoutMessage = message;
            }
            if (!message.needSave()) {
                continue;
            }
            if (message.getWith().contains("G")) {
                group = groupRepository.get(message.getWith(), message.getOwner());
                if (group == null) {
                    continue;
                }
            } else {
                friend = friendRepository.get(message.getOwner(), message.getWith());
                if (friend == null) {
                    continue;
                }
            }
            record = messageRecordRepository.get(message.getOwner(),friend);
            if (record == null) {
                record = new MessageRecord();
                record.setOwner(message.getOwner());
                if (friend == null) {
                    record.setWith(group);
                } else {
                    record.setWith(friend);
                }
            }
            record.setNewMsgCount(record.getNewMsgCount() + 1);
            record.setLastMsg(message);
            messageRecordRepository.saveOrUpdate(record);
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
