package com.example.jinphy.simplechat.modules.chat;

import android.os.IBinder;
import android.support.annotation.NonNull;

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
import com.example.jinphy.simplechat.services.common_service.aidl.BinderFactory;
import com.example.jinphy.simplechat.services.common_service.aidl.service.DownloadFileBinder;
import com.example.jinphy.simplechat.services.common_service.aidl.service.IDownloadFileBinder;
import com.example.jinphy.simplechat.services.common_service.aidl.service.ISendMsgBinder;
import com.example.jinphy.simplechat.services.common_service.aidl.service.IUploadFileBinder;
import com.example.jinphy.simplechat.services.common_service.aidl.service.SendMsgBinder;
import com.example.jinphy.simplechat.services.common_service.aidl.service.UploadFileBinder;
import com.example.jinphy.simplechat.utils.Preconditions;
import com.example.jinphy.simplechat.utils.ScreenUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jinphy on 2017/8/13.
 */

public class ChatPresenter implements ChatContract.Presenter {

    private ChatContract.View view;
    private UserRepository userRepository;
    private FriendRepository friendRepository;
    private MessageRepository messageRepository;
    private MessageRecordRepository recordRepository;
    private GroupRepository groupRepository;
    private MemberRepository memberRepository;

    private IUploadFileBinder uploadFileBinder;
    private ISendMsgBinder sendMsgBinder;
    private IDownloadFileBinder downloadBinder;

    public ChatPresenter(@NonNull ChatContract.View view) {
        this.view = Preconditions.checkNotNull(view);
        userRepository = UserRepository.getInstance();
        friendRepository = FriendRepository.getInstance();
        messageRepository = MessageRepository.getInstance();
        recordRepository = MessageRecordRepository.getInstance();
        groupRepository = GroupRepository.getInstance();
        memberRepository = MemberRepository.getInstance();
        initBind();
    }

    private void initBind() {
        IBinder uploadFileIBinder = BinderFactory.getBinder(BinderFactory.TYPE_UPLOAD_FILE);
        uploadFileBinder =UploadFileBinder.asInterface(uploadFileIBinder);

        IBinder sendMsgIBinder = BinderFactory.getBinder(BinderFactory.TYPE_SEND_MSG);
        sendMsgBinder = SendMsgBinder.asInterface(sendMsgIBinder);

        IBinder downloadFileIBinder = BinderFactory.getBinder(BinderFactory.TYPE_DOWNLOAD_FILE);
        downloadBinder = DownloadFileBinder.asInterface(downloadFileIBinder);
    }


    @Override
    public void start() {

    }

    @Override
    public List<Message> loadMessages(String withAccount) {
        User user = userRepository.currentUser();
        return messageRepository.load(user.getAccount(), withAccount);
    }

    public List<Message> loadNewMessages(String withAccount) {
        User user = userRepository.currentUser();
        return messageRepository.loadNew(user.getAccount(), withAccount);
    }
    @Override
    public List<Member> loadMembers(String groupNo) {
        User user = userRepository.currentUser();
        return memberRepository.get(groupNo, user.getAccount());
    }

    @Override
    public Friend getFriend(String friendAccount) {
        User user = userRepository.currentUser();
        return friendRepository.get(user.getAccount(), friendAccount);
    }

    @Override
    public Group getGroup(String groupNo) {
        User user = userRepository.currentUser();
        return groupRepository.get(groupNo, user.getAccount());
    }

    @Override
    public String getOwner() {
        return userRepository.currentUser().getAccount();
    }


    @Override
    public int getMemberCount(String groupNo) {
        User user = userRepository.currentUser();
        return (int) memberRepository.count(groupNo, user.getAccount());
    }

    @Override
    public Message getMessage(long msgId) {
        return messageRepository.get(msgId);
    }

    @Override
    public void removeMsg(Message message) {
        messageRepository.delete(message);
    }

    @Override
    public void updateRecord(Message message) {
        if (message == null) {
            return;
        }
        String with = message.getWith();
        MessageRecord record ;
        if (with.contains("G")) {
            // 群聊
            Group group = groupRepository.get(with, message.getOwner());
            record = recordRepository.get(message.getOwner(), group);
            if (record == null) {
                record = new MessageRecord();
                record.setWith(group);
                record.setOwner(message.getOwner());
            }
        } else {
            // 与好友聊天
            Friend friend = friendRepository.get(message.getOwner(), message.getWith());
            record = recordRepository.get(message.getOwner(), friend);
            if (record == null) {
                record = new MessageRecord();
                record.setWith(friend);
                record.setOwner(message.getOwner());
            }
        }
        record.setNewMsgCount(0);
        record.setLastMsg(message);
        recordRepository.saveOrUpdate(record);
    }

    @Override
    public String getUserAvatar() {
        return userRepository.currentUser().getAvatar();
    }

    @Override
    public Member getSelfMember(String groupNo) {
        String owner = userRepository.currentUser().getAccount();
        return memberRepository.get(groupNo, owner, owner);
    }

    @Override
    public void updateMsg(List<Message> messages) {
        messageRepository.update(messages);
    }

    @Override
    public void updateMsg(Message... messages) {
        messageRepository.update(messages);
    }

    @Override
    public void sendTextMsg(Message message) {
        Observable.just(message)
                .subscribeOn(Schedulers.io())
                .map(msg -> {
                    messageRepository.saveSend(message);
                    return message.getId();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(id -> {
                    try {
                        sendMsgBinder.sendMessage(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                        initBind();
                        sendTextMsg(message);
                    }
                    view.whenSendStart(message);
                })
                .subscribe();
    }

    @Override
    public void sendFileMsg(Message message) {
        Observable.just(message)
                .subscribeOn(Schedulers.io())
                .map(msg->{
                    messageRepository.saveSend(msg);
                    return Long.valueOf(msg.extra(Message.KEY_FILE_TASK_ID));
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(fileTaskId -> {
                    try {
                        uploadFileBinder.uploadFileAndSendMsg(fileTaskId, message.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendFileMsg(message);
                    }
                    view.whenSendStart(message);
                })
                .subscribe();
    }

    @Override
    public void downloadVoice(Message message) {
        Observable.just(message)
                .subscribeOn(Schedulers.io())
                .map(message1 -> {
                    message1.extra(Message.KEY_AUDIO_STATUS, Message.AUDIO_STATUS_DOWNLOADING);
                    message1.setExtra(null);
                    messageRepository.update(message);
                    view.cacheMsg(message);
                    return message1.extra(Message.KEY_FILE_TASK_ID);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(fileTaskId -> {
                    try {
                        downloadBinder.downloadVoice(Long.valueOf(fileTaskId), message.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendFileMsg(message);
                    }
                })
                .subscribe();
    }
}
