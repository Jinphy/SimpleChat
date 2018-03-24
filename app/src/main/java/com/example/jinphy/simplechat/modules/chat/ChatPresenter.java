package com.example.jinphy.simplechat.modules.chat;

import android.os.IBinder;
import android.support.annotation.NonNull;

import com.example.jinphy.simplechat.broadcasts.AppBroadcastReceiver;
import com.example.jinphy.simplechat.models.api.send.Sender;
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
import com.example.jinphy.simplechat.services.common_service.aidl.service.IUploadFileBinder;
import com.example.jinphy.simplechat.services.common_service.aidl.service.UploadFileBinder;
import com.example.jinphy.simplechat.utils.Preconditions;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private MyUploadListener myUploadListener;

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
        while (BinderFactory.getBinder(BinderFactory.TYPE_UPLOAD_FILE) == null) {
            continue;
        }
        IBinder uploadFileIBinder = BinderFactory.getBinder(BinderFactory.TYPE_UPLOAD_FILE);
        myUploadListener = new MyUploadListener(this);
        uploadFileBinder =UploadFileBinder.asInterface(uploadFileIBinder);
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
    public void sendMsg(Message message) {
        Sender.newTask(message)
                .whenStart(() -> view.whenSendStart())
                .whenFinal(result -> view.whenSendFinal())
                .send();
    }

    @Override
    public void sendPhotoMsg(Message message) {
        myUploadListener.addMsg(message);
        messageRepository.saveSend(message);
        Observable.just(message.extra(Message.KEY_FILE_TASK_ID))
                .subscribeOn(Schedulers.io())
                .map(Long::valueOf)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(uploadFileBinder::upload)
                .subscribe();
    }

    @Override
    public void registerUploadFileListener() {
        AppBroadcastReceiver.registerUploadFileListener(myUploadListener);
    }

    @Override
    public void unregisterUploadFileListener() {
        AppBroadcastReceiver.unregisterUploadFileListener();
    }

    public static class MyUploadListener implements FileListener {
        private final ChatPresenter presenter;
        Map<Long, Message> msgMap = new ConcurrentHashMap<>();


        public MyUploadListener(ChatPresenter presenter) {
            this.presenter = presenter;
        }


        public void addMsg(Message message) {
            msgMap.put(Long.valueOf(message.extra(Message.KEY_FILE_TASK_ID)), message);
        }


        @Override
        public void onStart(long fileTaskId)  {
            presenter.view.whenSendStart();
        }

        @Override
        public void onUpdate(long fileTaskId, long finishedLength, long totalLength) {
        }

        @Override
        public void onError(long fileTaskId) {
            Message message = msgMap.remove(fileTaskId);
            message.setStatus(Message.STATUS_NO);
            presenter.messageRepository.update(message);
            presenter.view.whenSendFinal();
        }

        @Override
        public void onFinish(long fileTaskId) {
            presenter.sendMsg(msgMap.remove(fileTaskId));
        }
    }
}
