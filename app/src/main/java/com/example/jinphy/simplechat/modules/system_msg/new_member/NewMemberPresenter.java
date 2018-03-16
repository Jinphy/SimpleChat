package com.example.jinphy.simplechat.modules.system_msg.new_member;

import android.content.Context;
import android.text.TextUtils;

import com.example.jinphy.simplechat.base.BaseRepository;
import com.example.jinphy.simplechat.custom_libs.SChain;
import com.example.jinphy.simplechat.models.api.common.Api;
import com.example.jinphy.simplechat.models.api.common.Response;
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
import com.example.jinphy.simplechat.modules.system_msg.new_member.NewMemberAdapter.NewMember;
import com.example.jinphy.simplechat.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/12.
 */

public class NewMemberPresenter implements NewMemberContract.Presenter {

    private NewMemberContract.View view;

    private MessageRepository messageRepository;
    private GroupRepository groupRepository;
    private UserRepository userRepository;
    private MemberRepository memberRepository;
    private FriendRepository friendRepository;
    private MessageRecordRepository recordRepository;



    public NewMemberPresenter(NewMemberContract.View view) {
        this.view = view;
        messageRepository = MessageRepository.getInstance();
        groupRepository = GroupRepository.getInstance();
        userRepository = UserRepository.getInstance();
        memberRepository = MemberRepository.getInstance();
        friendRepository = FriendRepository.getInstance();
        recordRepository = MessageRecordRepository.getInstance();
    }



    @Override
    public void start() {

    }

    @Override
    public List<NewMember> loadNewMembers() {
        User user = userRepository.currentUser();
        List<Message> messages = messageRepository.loadSystemMsg
                (user.getAccount(), Message.TYPE_SYSTEM_APPLY_JOIN_GROUP);

        Message.sort(messages);

        List<NewMember> newMembers = new LinkedList<>();
        for (Message message : messages) {
            String[] split = message.getExtra().split("@");
            String groupNo = split[0];
            Group group = groupRepository.get(groupNo, message.getOwner());
            if (group == null) {
                messageRepository.delete(message);
            } else {
                if (StringUtils.equal(split[2], Group.STATUS_WAITING)) {
                    Member member = memberRepository.get(groupNo, split[1], user.getAccount());
                    if (member != null) {
                        split[2] = Group.STATUS_INVALIDATE;
                        message.setExtra(TextUtils.join("@", split));
                        messageRepository.update(message);
                    }
                }
                newMembers.add(NewMember.create(message, group, split[1], split[2], split[3]));
            }
        }
        return newMembers;
    }
    @Override
    public void updateMsg(List<Message> messages) {
        if (messages.size() > 0) {
            messageRepository.update(messages);
            Message message = messages.get(0);
            Friend friend = friendRepository.get(message.getOwner(), Friend.system);
            MessageRecord record = recordRepository.get(message.getOwner(), friend);
            record.setNewMsgCount(record.getNewMsgCount() - messages.size());
            recordRepository.saveOrUpdate(record);
        }
    }

    @Override
    public void updateMsg(Message message) {
        messageRepository.update(message);
    }

    @Override
    public void agreeJoinGroup(Context context, NewMember newMember) {
        groupRepository.<String>newTask()
                .param(Api.Key.groupNo, newMember.getGroupNo())
                .param(Api.Key.creator, newMember.getCreator())
                .param(Api.Key.account, newMember.getAccount())
                .doOnDataOk(okData -> {
                    view.whenAgreeOk(newMember);
                    User user = userRepository.currentUser();
                    Friend friend = friendRepository.get(user.getAccount(), newMember.getAccount());
                    if (friend != null) {
                        memberRepository.saveNew(friend, newMember.getGroupNo());
                    } else {
                        friendRepository.getOnline(
                                null,
                                user.getAccount(),
                                newMember.getAccount(),
                                friend1 -> {
                                    memberRepository.saveNew(friend1, newMember.getGroupNo());
                                }
                        );

                    }
                })
                .submit(task -> groupRepository.agreeJoinGroup(context, task));
    }

}
