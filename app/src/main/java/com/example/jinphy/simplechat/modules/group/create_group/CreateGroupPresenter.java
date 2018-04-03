package com.example.jinphy.simplechat.modules.group.create_group;

import android.content.Context;

import com.apkfuns.logutils.LogUtils;
import com.example.jinphy.simplechat.models.friend.CheckedFriend;
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

import java.util.List;
import java.util.Map;

/**
 *
 *
 * Created by Jinphy on 2018/3/6.
 */

public class CreateGroupPresenter implements CreateGroupContract.Presenter {


    private final CreateGroupContract.View view;
    private GroupRepository groupRepository;
    private UserRepository userRepository;
    private FriendRepository friendRepository;
    private MemberRepository memberRepository;
    private MessageRepository messageRepository;
    private MessageRecordRepository recordRepository;
    public CreateGroupPresenter(CreateGroupContract.View view) {
        this.view = view;
        groupRepository = GroupRepository.getInstance();
        userRepository = UserRepository.getInstance();
        friendRepository = FriendRepository.getInstance();
        memberRepository = MemberRepository.getInstance();
        messageRepository = MessageRepository.getInstance();
        recordRepository = MessageRecordRepository.getInstance();
    }

    @Override
    public void start() {
    }

    @Override
    public String getAccessToken() {
        return userRepository.currentUser().getAccessToken();
    }

    @Override
    public String getCurrentAccount() {
        return userRepository.currentUser().getAccount();
    }

    @Override
    public void createGroup(Context context, Map<String, Object> params) {
        groupRepository.<Map<String, String>>newTask(params)
                .doOnDataOk(okData -> {
                    Group group = Group.parse(okData.getData());
                    groupRepository.saveMyGroup(group);
                    createMsg(group, okData.getData().get("msg"));
                    view.whenCreateGroupOk(group);
//                    LogUtils.e(group);
                })
                .submit(task -> groupRepository.createGroup(context, task));
    }

    private void createMsg(Group group, String msg) {
        // 生成消息
        Message message = new Message();
        message.setCreateTime(System.currentTimeMillis()+"");
        message.setSourceType(Message.SEND);
        message.setContent(msg);
        message.setContentType(Message.TYPE_CHAT_TEXT);
        message.setWith(group.getGroupNo());
        message.setOwner(group.getOwner());
        message.setExtra(group.getOwner());// 群消息时，该字段表示信息发送者

        // 生成消息记录
        messageRepository.saveSend(message);
        MessageRecord record = new MessageRecord();
        record.setNewMsgCount(0);
        record.setLastMsg(message);
        record.setWith(group);
        record.setOwner(group.getOwner());
        record.setId(0);
        recordRepository.saveOrUpdate(record);
    }



    @Override
    public List<CheckedFriend> loadFriends() {
        User user = userRepository.currentUser();
        List<Friend> friends = friendRepository.loadLocal(user.getAccount());
        return CheckedFriend.create(friends);
    }

    @Override
    public void saveMembers(Group group, List<String> members) {
        if (members == null || members.size() == 0) {
            return;
        }
        LogUtils.e("member size: " + members.size());
        User user = userRepository.currentUser();
        for (String m : members) {
            Member member = new Member();
            member.setStatus(Member.STATUS_OK);
            member.setAllowChat(true);
            member.setGroupNo(group.getGroupNo());
            member.setOwner(user.getAccount());
            member.setPerson(friendRepository.get(user.getAccount(), m));
            memberRepository.save(member);
        }
    }
}
